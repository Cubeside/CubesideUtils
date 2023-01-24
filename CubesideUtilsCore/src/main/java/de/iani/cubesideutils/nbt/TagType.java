package de.iani.cubesideutils.nbt;

public enum TagType {
    END {
        @Override
        public BaseTag create() {
            throw new UnsupportedOperationException("END");
        }
    },
    BYTE {
        @Override
        public ByteTag create() {
            return new ByteTag();
        }
    },
    SHORT {
        @Override
        public ShortTag create() {
            return new ShortTag();
        }
    },
    INT {
        @Override
        public IntTag create() {
            return new IntTag();
        }
    },
    LONG {
        @Override
        public LongTag create() {
            return new LongTag();
        }
    },
    FLOAT {
        @Override
        public FloatTag create() {
            return new FloatTag();
        }
    },
    DOUBLE {
        @Override
        public DoubleTag create() {
            return new DoubleTag();
        }
    },
    BYTE_ARRAY {
        @Override
        public ByteArrayTag create() {
            return new ByteArrayTag();
        }
    },
    STRING {
        @Override
        public StringTag create() {
            return new StringTag();
        }
    },
    LIST {
        @Override
        public ListTag<? extends BaseTag<?>> create() {
            return new ListTag<>();
        }
    },
    COMPOUND {
        @Override
        public CompoundTag create() {
            return new CompoundTag();
        }
    },
    INT_ARRAY {
        @Override
        public IntArrayTag create() {
            return new IntArrayTag();
        }
    },
    LONG_ARRAY {
        @Override
        public LongArrayTag create() {
            return new LongArrayTag();
        }
    };

    public abstract BaseTag create();

    private static final TagType[] values = values();

    public static TagType valueOf(int type) {
        if (type < 0 || type >= values.length) {
            throw new IllegalArgumentException("invalid tag type: " + type);
        }
        return values[type];
    }
}
