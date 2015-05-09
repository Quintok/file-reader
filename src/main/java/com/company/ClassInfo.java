package com.company;

import java.nio.ByteBuffer;
import com.company.Database.Database;

public abstract class ClassInfo {
    protected final ByteBuffer buffer;

    public ClassInfo(ByteBuffer input) {
        this.buffer = input;
    }

    enum Type {
        BlockData(BlockData.class, 1000),
        BlockInfo(BlockInfo.class, 1001),
        DataBlock(DataBlock.class, 1003),
        FreeBlockManager(FreeBlockManager.class, 1004),
        Database(Database.class, 2000);

        private final Class<? extends ClassInfo> type;
        private final int classId;

        private Type(Class<? extends ClassInfo> type, final int classId) {
            this.type = type;
            this.classId = classId;
        }

        public int getClassId() {
            return classId;
        }

        public Class<? extends ClassInfo> getType() {
            return type;
        }
    }
}
