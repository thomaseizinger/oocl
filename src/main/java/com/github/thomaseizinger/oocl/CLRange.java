package com.github.thomaseizinger.oocl;

public final class CLRange {

    private final long[] dimensionSizes;

    private CLRange(long... dimensionSizes) {
        this.dimensionSizes = dimensionSizes;
    }

    public static CLRange of(long... dimensionSizes) {
        return new CLRange(dimensionSizes);
    }

    public long[] toArray() {
        return dimensionSizes;
    }
}
