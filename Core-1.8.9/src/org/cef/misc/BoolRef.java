package org.cef.misc;

public class BoolRef {
    private boolean value_;

    public BoolRef() {
    }

    public BoolRef(boolean value) {
        this.value_ = value;
    }

    public void set(boolean value) {
        this.value_ = value;
    }

    public boolean get() {
        return this.value_;
    }
}
