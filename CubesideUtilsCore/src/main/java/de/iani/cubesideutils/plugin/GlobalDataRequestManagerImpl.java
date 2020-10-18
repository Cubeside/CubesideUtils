package de.iani.cubesideutils.plugin;

import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.plugin.api.GlobalDataRequestManager;
import de.iani.cubesideutils.serialization.StringSerializable;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public abstract class GlobalDataRequestManagerImpl<T extends Enum<T>> implements GlobalDataRequestManager<T> {

    private static class Request<V> implements Future<V> {
        private Object lock;
        private boolean running;
        private boolean cancelled;
        private boolean done;
        private V result;

        private Request() {
            this.lock = new Object();
            this.done = false;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (!mayInterruptIfRunning) {
                return false;
            }
            synchronized (this.lock) {
                if (this.running) {
                    return false;
                }
                this.cancelled = true;
                this.done = true;
                return true;
            }
        }

        @Override
        public boolean isCancelled() {
            synchronized (this.lock) {
                return this.cancelled;
            }
        }

        @Override
        public boolean isDone() {
            synchronized (this.lock) {
                return this.done;
            }
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            synchronized (this.lock) {
                while (!this.done) {
                    this.lock.wait();
                }

                if (this.cancelled) {
                    throw new CancellationException();
                }

                return this.result;
            }
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            long start = System.currentTimeMillis();
            synchronized (this.lock) {
                while (timeout > 0 && !this.done) {
                    this.lock.wait(unit.toMillis(timeout));
                    long now = System.currentTimeMillis();
                    timeout = (now - start);
                    start = now;
                }

                if (this.cancelled) {
                    throw new CancellationException();
                }
                if (!this.done) {
                    throw new TimeoutException();
                }

                return this.result;
            }
        }

        public void set(V result) {
            synchronized (this.lock) {
                if (this.done || !this.running) {
                    throw new IllegalStateException();
                }
                this.result = result;
                this.done = true;
                this.lock.notifyAll();
            }
        }

        public boolean setRunning() {
            synchronized (this.lock) {
                if (this.cancelled) {
                    return false;
                }
                this.running = true;
                return true;
            }
        }
    }

    protected static class Delegator<T extends Enum<T>> {
        private GlobalDataRequestManagerImpl<T> requestManager;

        public Delegator() {

        }

        public void handleMessage(T messageType, GlobalServer source, DataInputStream data) {
            if (this.requestManager == null) {
                throw new IllegalStateException();
            }
            this.requestManager.handleMessage(messageType, source, data);
        }

        private void setRequestManager(GlobalDataRequestManagerImpl<T> requestManager) {
            if (this.requestManager != null) {
                throw new IllegalStateException();
            }
            this.requestManager = Objects.requireNonNull(requestManager);
        }
    }

    private GlobalDataHelperImpl<T> helper;
    private Map<UUID, Request<?>> activeRequests;

    public GlobalDataRequestManagerImpl(Pair<GlobalDataHelperImpl<T>, Delegator<T>> helperAndDelegator) {
        this.helper = helperAndDelegator.first;
        this.activeRequests = Collections.synchronizedMap(new HashMap<>());
        helperAndDelegator.second.setRequestManager(this);
    }

    protected GlobalDataHelperImpl<T> getHelper() {
        return this.helper;
    }

    @Override
    public <V> Future<V> makeRequest(T requestType, GlobalServer server, Object... data) {
        Request<V> request = new Request<>();
        UUID requestId = UUID.randomUUID();
        this.activeRequests.put(requestId, request);

        Object[] requestData = new Object[data.length + 2];
        requestData[0] = false;
        requestData[1] = requestId;
        System.arraycopy(data, 0, requestData, 2, data.length);

        getHelper().sendData(server, requestType, requestData);
        return request;
    }

    @SuppressWarnings("unchecked")
    protected void handleMessage(T messageType, GlobalServer source, DataInputStream data) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        DataOutputStream responseOut = new DataOutputStream(responseBytes);
        try {
            boolean isResponse = data.readBoolean();
            UUID requestId = readUUID(data);

            if (isResponse) {
                Request<Object> request = (Request<Object>) this.activeRequests.remove(requestId);
                if (request == null) {
                    throw new NoSuchElementException("unknown request id");
                }
                if (!request.setRunning()) {
                    return;
                }
                Object result = handleResponse(messageType, source, data);
                request.set(result);
            } else {
                responseOut.writeInt(messageType.ordinal());
                responseOut.writeBoolean(true);
                sendMsgPart(responseOut, requestId);

                respondToRequest(messageType, source, data, responseOut);

                byte[] msgarry = responseBytes.toByteArray();
                source.sendData(getHelper().getChannel(), msgarry);
            }
        } catch (IOException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "IOException trying to send GlobalDataMessage!", e);
            return;
        }
    }

    protected void sendMsgPart(DataOutputStream msgout, Object msg) throws IOException {
        getHelper().sendMsgPart(msgout, msg);
    }

    protected UUID readUUID(DataInputStream msgin) throws IOException {
        return getHelper().readUUID(msgin);
    }

    protected <S extends StringSerializable> S readStringSerializable(DataInputStream msgin) throws IOException {
        return getHelper().readStringSerializable(msgin);
    }

    protected abstract void respondToRequest(T requestType, GlobalServer source, DataInputStream requestData, DataOutputStream responseData) throws IOException;

    protected abstract Object handleResponse(T requestType, GlobalServer source, DataInputStream responseData) throws IOException;

}
