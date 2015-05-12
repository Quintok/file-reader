package com.company;

import com.company.Database.Database;
import com.company.Database.SObjectInfo;
import com.company.polydata.*;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Optional;

public abstract class ClassInfo implements Serializable {

    public ClassInfo(ByteBuffer input) {

    }

    public enum Type {
        BlockData(BlockData.class, 1000),
        BlockInfo(BlockInfo.class, 1001),
        DataBlock(DataBlock.class, 1003),
        FreeBlockManager(FreeBlockManager.class, 1004),
        PolyValue(PolyValue.class, 1007),
        CSTRBinary(BinaryDataType.class, 1017),
        PropertySet(PropertySet.class, 2190),
        Database(Database.class, 2000),
        SObjectInfo(SObjectInfo.class, 2002);

        private final Class<? extends ClassInfo> type;
        private final int classId;

        private Type(Class<? extends ClassInfo> type, final int classId) {
            this.type = type;
            this.classId = classId;
        }

        public static Optional<Type> getTypeForId(final int id) {
            for (Type type : values()) {
                if (type.getClassId() == id)
                    return Optional.of(type);
            }
            return Optional.empty();
        }

        public int getClassId() {
            return classId;
        }

        public Class<? extends ClassInfo> getType() {
            return type;
        }
    }
}
