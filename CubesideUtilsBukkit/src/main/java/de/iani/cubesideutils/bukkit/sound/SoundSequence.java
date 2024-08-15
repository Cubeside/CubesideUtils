package de.iani.cubesideutils.bukkit.sound;

import com.google.common.base.Preconditions;
import de.iani.cubesideutils.conditions.Condition;
import de.iani.cubesideutils.serialization.StringSerializable;
import java.util.ArrayList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SoundSequence implements StringSerializable {
    public static final String SERIALIZATION_TYPE = "SoundSequence";
    private static final SoundSequence EMPTY = new SoundSequence();

    private final SoundEvent[] soundEvents;
    private boolean hasDelay;

    private SoundSequence(SoundEvent... soundEvents) {
        this.soundEvents = soundEvents;
        boolean hasDelay = false;
        for (int i = 0; !hasDelay && i < soundEvents.length; i++) {
            if (soundEvents[i] instanceof SoundWaitEvent) {
                hasDelay = true;
            }
        }
        this.hasDelay = hasDelay;
    }

    public void play(Player player, Plugin plugin) {
        if (!hasDelay) {
            for (int i = 0; i < soundEvents.length; i++) {
                if (soundEvents[i] instanceof SoundPlayEvent playEvent) {
                    playEvent.play(player);
                }
            }
            return;
        }
        new SoundPlayer(player, plugin).play();
    }

    public void playToAll(Condition<? super Player> hearSoundCondition, Plugin plugin) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (hearSoundCondition == null || hearSoundCondition.test(player)) {
                play(player, plugin);
            }
        }
    }

    public void playToAll(Plugin plugin) {
        playToAll(null, plugin);
    }

    public static SoundSequence empty() {
        return EMPTY;
    }

    public static SoundSequence ofSingleSound(Sound.Type sound, Sound.Source source, float volume, float pitch) {
        Preconditions.checkNotNull(sound, "sound");
        Preconditions.checkNotNull(source, "source");
        return ofSingleSound(Sound.sound().type(sound).volume(volume).pitch(pitch).source(source).build());
    }

    public static SoundSequence ofSingleSound(Key sound, Sound.Source source, float volume, float pitch) {
        Preconditions.checkNotNull(sound, "sound");
        Preconditions.checkNotNull(source, "source");
        return ofSingleSound(Sound.sound().type(sound).volume(volume).pitch(pitch).source(source).build());
    }

    public static SoundSequence ofSingleSound(Sound sound) {
        Preconditions.checkNotNull(sound, "sound");
        return new SoundSequence(new SoundPlayEvent(sound));
    }

    public static class Builder {
        private ArrayList<SoundEvent> soundEvents = new ArrayList<>();

        public Builder play(Sound.Type sound, Sound.Source source, float volume, float pitch) {
            Preconditions.checkNotNull(sound, "sound");
            Preconditions.checkNotNull(source, "source");
            return play(Sound.sound().type(sound).volume(volume).pitch(pitch).source(source).build());
        }

        public Builder play(Key sound, Sound.Source source, float volume, float pitch) {
            Preconditions.checkNotNull(sound, "sound");
            Preconditions.checkNotNull(source, "source");
            return play(Sound.sound().type(sound).volume(volume).pitch(pitch).source(source).build());
        }

        public Builder play(Sound sound) {
            Preconditions.checkNotNull(sound, "sound");
            soundEvents.add(new SoundPlayEvent(sound));
            return this;
        }

        public Builder delay(int ticks) {
            Preconditions.checkArgument(ticks >= 0, "ticks may not me negative");
            if (ticks > 0) {
                if (!soundEvents.isEmpty()) {
                    if (soundEvents.get(soundEvents.size() - 1) instanceof SoundWaitEvent waitEvent) {
                        int newTicks = ticks + waitEvent.ticks;
                        if (newTicks > 0) { // otherwise integer overflow, fall back to two waits (who wants to wait that long?)
                            soundEvents.set(soundEvents.size() - 1, new SoundWaitEvent(newTicks));
                            return this;
                        }
                    }
                }
                soundEvents.add(new SoundWaitEvent(ticks));
            }
            return this;
        }

        public SoundSequence build() {
            if (soundEvents.isEmpty()) {
                return SoundSequence.EMPTY;
            }
            return new SoundSequence(soundEvents.toArray(new SoundEvent[soundEvents.size()]));
        }
    }

    private static interface SoundEvent {
    }

    private static class SoundPlayEvent implements SoundEvent {
        private final Sound sound;

        public SoundPlayEvent(Sound sound) {
            this.sound = sound;
        }

        public void play(Player player) {
            player.playSound(sound);
        }
    }

    private static class SoundWaitEvent implements SoundEvent {
        private final int ticks;

        public SoundWaitEvent(int ticks) {
            this.ticks = ticks;
        }
    }

    private class SoundPlayer {
        private final Player player;
        private final Plugin plugin;
        private int nextSound;

        public SoundPlayer(Player player, Plugin plugin) {
            this.player = player;
            this.plugin = plugin;
        }

        public void play() {
            while (true) {
                SoundEvent current = soundEvents[nextSound];
                if (current instanceof SoundPlayEvent event) {
                    event.play(player);
                    nextSound++;
                } else if (current instanceof SoundWaitEvent event) {
                    nextSound++;
                    if (nextSound >= soundEvents.length) {
                        return;
                    }
                    plugin.getServer().getScheduler().runTaskLater(plugin, this::play, event.ticks);
                    return;
                } else {
                    throw new IllegalStateException();
                }
                if (nextSound >= soundEvents.length) {
                    return;
                }
            }
        }
    }

    @Override
    public String getSerializationType() {
        return SERIALIZATION_TYPE;
    }

    @Override
    public String serializeToString() {
        StringBuilder sb = new StringBuilder();
        for (SoundEvent e : soundEvents) {
            if (!sb.isEmpty()) {
                sb.append(";");
            }
            if (e instanceof SoundPlayEvent event) {
                Sound sound = event.sound;
                sb.append("p;").append(sound.name().asMinimalString());
                sb.append(";").append(sound.volume());
                sb.append(";").append(sound.pitch());
                if (sound.seed().isPresent()) {
                    sb.append(";").append(sound.seed().getAsLong());
                } else {
                    sb.append(";null");
                }
                sb.append(";").append(sound.source().name());
            } else if (e instanceof SoundWaitEvent event) {
                sb.append("w;").append(event.ticks);
            }
        }
        return sb.toString();
    }

    public static SoundSequence deserialize(String serialized) {
        SoundSequence.Builder soundSequence = new SoundSequence.Builder();
        String[] parts = serialized.split("\\;");
        int pos = 0;
        while (pos < parts.length) {
            String type = parts[pos++];
            if (type.equals("p")) {
                // play sound
                Sound.Builder builder = Sound.sound();
                builder.type(Key.key(parts[pos++]));
                builder.volume(Float.parseFloat(parts[pos++]));
                builder.pitch(Float.parseFloat(parts[pos++]));
                String seed = parts[pos++];
                if (!seed.equals("null")) {
                    builder.seed(Long.parseLong(seed));
                }
                builder.source(Sound.Source.valueOf(parts[pos++]));
                soundSequence.play(builder.build());
            } else if (type.equals("w")) {
                // wait
                int ticks = Integer.parseInt(parts[pos++]);
                soundSequence.delay(ticks);
            } else {
                throw new IllegalArgumentException("type=" + type);
            }
        }
        return soundSequence.build();
    }
}
