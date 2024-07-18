package org.cef.misc;

public class IntRef {
    private int value_;

    public IntRef() {
    }

    public IntRef(int value) {
        this.value_ = value;
    }

    public void set(int value) {
        this.value_ = value;
    }

    public int get() {
        return this.value_;
    }
}
