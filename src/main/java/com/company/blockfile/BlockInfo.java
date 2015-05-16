package com.company.blockfile;

public class BlockInfo extends ClassInfo {
    private final AllocateFlag flag;
    private final DataBlock dataBlock;
    public BlockInfo(DataConverterByteStream converter) {
        super(converter);
        flag = AllocateFlag.values()[converter.getInt()];
        dataBlock = converter.get();
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
