package org.cef.misc;

public class StringRef {
    private String value_;

    public StringRef() {
    }

    public StringRef(String value) {
        this.value_ = value;
    }

    public void set(String value) {
        this.value_ = value;
    }

    public String get() {
        return this.value_;
    }
}
