package com.company;

public enum WorkingMode {
    PRIMARY(27),
    SECONDARY(75);

    private final int offset;

    private WorkingMode(final int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
