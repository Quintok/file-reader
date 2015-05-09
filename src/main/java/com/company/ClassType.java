package com.company;

import java.io.InputStream;

public abstract class ClassType {
    protected final InputStream input;

    public ClassType(InputStream input) {
        this.input = input;
    }

    enum Type {
        BlockMetadata(BlockMetadata.class, 1003),
        DataBlock(DataBlock.class, 1000),
        FreeBlockManager(com.company.FreeBlockManager.class, 1004);

        private final Class<? extends ClassType> type;
        private final int classId;

        private Type(Class<? extends ClassType> type, final int classId) {
            this.type = type;
            this.classId = classId;
        }

        public int getClassId() {
            return classId;
        }

        public Class<? extends ClassType> getType() {
            return type;
        }
    }
}
