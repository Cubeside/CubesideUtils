package de.iani.cubesideutils.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArgsParser implements Iterable<String>, Iterator<String> {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    static @interface ArgMatcher {

    }

    private String[] args;

    private int current;

    public ArgsParser(String[] args) {
        this.args = args;
        this.current = -1;
    }

    public ArgsParser(String[] args, int skipParts) {
        if (skipParts < 0) {
            throw new IllegalArgumentException("skipParts must be >= 0");
        }
        this.args = args;
        this.current = -1 + skipParts;
    }

    @Override
    public boolean hasNext() {
        return current < args.length - 1;
    }

    public String[] toArray() {
        return args.length <= current + 1 ? new String[0] : Arrays.copyOfRange(args, current + 1, args.length);
    }

    public int remaining() {
        return Math.max(args.length - 1 - current, 0);
    }

    public String seeAll(String def) {
        int i = current + 1;
        if (args.length <= i) {
            return def;
        }
        StringBuilder sb = new StringBuilder();
        while (args.length > i) {
            sb.append(args[i]);
            sb.append(' ');
            ++i;
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getAll(String def) {
        ++current;
        if (args.length <= current) {
            return def;
        }
        StringBuilder sb = new StringBuilder();
        while (args.length > current) {
            sb.append(args[current]);
            sb.append(' ');
            ++current;
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String seeNext(String def) {
        if (args.length <= current + 1) {
            return def;
        }
        return args[current + 1];
    }

    public String getNext(String def) {
        ++current;
        if (args.length <= current) {
            return def;
        }
        return args[current];
    }

    @Override
    public String next() {
        return getNext();
    }

    public String getNext() {
        String res = getNext(null);
        if (res == null) {
            throw new NoSuchElementException();
        }
        return res;
    }

    public int getNext(int def) {
        String next = getNext(null);
        if (next == null) {
            return def;
        }
        try {
            return Integer.parseInt(next);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public double getNext(double def) {
        String next = getNext(null);
        if (next == null) {
            return def;
        }
        try {
            return Double.parseDouble(next);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public Boolean getNext(boolean ignored) {
        String next = getNext(null);
        if (next == null) {
            return null;
        }
        if (next.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        } else if (next.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T getNextEnum(Class<T> enumClass, T def) {
        if (!hasNext()) {
            return def;
        }

        Method matcher = null;
        boolean priority = false;
        for (Method method : enumClass.getMethods()) {
            if (method.isAnnotationPresent(ArgMatcher.class) && isLegalMatcherMethod(enumClass, method)) {
                matcher = method;
                break;
            }
            if (!priority && (method.getName().equals("match") || method.getName().equals("parse")) && isLegalMatcherMethod(enumClass, method)) {
                matcher = method;
                priority = method.getName().equals("parse");
            }
        }

        try {
            if (matcher != null) {
                try {
                    return (T) matcher.invoke(null, next());
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof RuntimeException) {
                        throw (RuntimeException) e.getCause();
                    }
                    if (e.getCause() instanceof Error) {
                        throw (Error) e.getCause();
                    }
                    throw new AssertionError(e.getCause());
                }
            }

            String arg = next();
            try {
                return (T) enumClass.getMethod("valueOf", String.class).invoke(null, arg.toUpperCase());
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof IllegalArgumentException) {
                    // ignore, continue method
                } else {
                    if (e.getCause() instanceof Error) {
                        throw (Error) e.getCause();
                    } else if (e.getCause() instanceof RuntimeException) {
                        throw (RuntimeException) e.getCause();
                    } else {
                        throw new AssertionError(e.getCause());
                    }
                }
            }

            try {
                T[] types = (T[]) enumClass.getMethod("values").invoke(null);
                for (T t : types) {
                    if (t.name().equalsIgnoreCase(arg)) {
                        return t;
                    }
                }
                for (T t : types) {
                    if (t.toString().equalsIgnoreCase(arg)) {
                        return t;
                    }
                }
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof Error) {
                    throw (Error) e.getCause();
                } else if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                } else {
                    throw new AssertionError(e.getCause());
                }
            }
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new AssertionError(e);
        }

        return null;
    }

    private <T extends Enum<T>> boolean isLegalMatcherMethod(Class<T> enumClass, Method method) {
        if ((method.getModifiers() & Modifier.STATIC) == 0) {
            return false;
        }
        if (!enumClass.isAssignableFrom(method.getReturnType())) {
            return false;
        }
        if (!Arrays.equals(method.getParameterTypes(), new Class<?>[] { String.class })) {
            return false;
        }
        if (Arrays.stream(method.getExceptionTypes()).anyMatch(type -> !RuntimeException.class.isAssignableFrom(type) && !Error.class.isAssignableFrom(type))) {
            return false;
        }
        return true;
    }

    public long getNextTimespan() throws NumberFormatException, ParseException {
        String string = getNext();
        string = string.toLowerCase();
        long res = 0;
        if (string.endsWith("s")) {
            res += Integer.parseInt(string.substring(0, string.length() - 1)) * 1000;
        } else if (string.endsWith("m")) {
            res += Integer.parseInt(string.substring(0, string.length() - 1)) * 1000 * 60;
        } else if (string.endsWith("h")) {
            res += Integer.parseInt(string.substring(0, string.length() - 1)) * 1000 * 60 * 60;
        } else if (string.endsWith("d")) {
            res += Integer.parseInt(string.substring(0, string.length() - 1)) * 1000 * 60 * 60 * 24;
        } else {
            throw new ParseException("String doesn't end with s, m h or d", string.length() - 1);
        }
        return res;
    }

    public long getAllTimespan() throws NumberFormatException, ParseException {
        long res = 0;
        while (hasNext()) {
            res += getNextTimespan();
        }
        return res;
    }

    @Override
    public Iterator<String> iterator() {
        return clone();
    }

    @Override
    public ArgsParser clone() {
        ArgsParser result = new ArgsParser(args);
        result.current = this.current;
        return result;
    }

}
