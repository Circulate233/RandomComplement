package com.circulation.random_complement.common.util;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record XYPair(int x, int y) {

    public static XYPair of(int x, int y) {
        return new XYPair(x, y);
    }

    public boolean equals(int x, int y) {
        return abs(this.x - x) < 3 && abs(this.y - y) < 3;
    }

    private int abs(int i) {
        return i < 0 ? -i : i;
    }
}