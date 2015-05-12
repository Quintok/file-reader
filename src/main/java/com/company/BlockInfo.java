package com.company;

import java.nio.ByteBuffer;

public class BlockInfo extends ClassInfo {
    private final AllocateFlag flag;
    private final DataBlock dataBlock;
    public BlockInfo(ByteBuffer input) {
        super(input);
        flag = AllocateFlag.values()[DataConverterByteStream.getInt(input)];
        dataBlock = DataConverterByteStream.get(input);
        if (dataBlock.getOffset() == -1)
            dataBlock.setOffset(0);
    }

    public DataBlock getDataBlock() {
        return dataBlock;
    }

    public AllocateFlag getFlag() {
        return flag;
    }

    public long getSize() {
        return dataBlock.getSize();
    }

    public enum AllocateFlag {
        ALLOCATED,
        UNUSED
    }

    @Override
    public String toString() {
        return "BlockInfo{" +
                "flag=" + flag +
                ", dataBlock=" + dataBlock +
                '}';
    }
}
