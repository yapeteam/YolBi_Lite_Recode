package cn.yapeteam.yolbi.module.values.impl;

import cn.yapeteam.ymixin.annotations.DontMap;
import cn.yapeteam.yolbi.module.values.Value;
import cn.yapeteam.yolbi.module.values.Visibility;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DontMap
@Setter
@Getter
public class ModeValue<T> extends Value<T> {
    private T[] modes;

    @SafeVarargs
    public ModeValue(String name, T current, T... modes) {
        super("mode", name);
        this.value = current;
        this.modes = modes;
    }

    @SafeVarargs
    public ModeValue(String name, Visibility visibility, T current, T... modes) {
        super("mode", name);
        this.value = current;
        this.modes = modes;
        setVisibility(visibility);
    }

    public boolean is(T mode) {
        return getValue().equals(mode);
    }

    public void setMode(String str) {
        value = getCallback() != null ? getCallback().run(value, getMode(str)) : getMode(str);
    }

    public T getMode(String name) {
        return Arrays.stream(modes).filter(m -> m.toString().equals(name)).findFirst().orElse(null);
    }

    public void increment() {
        int index = Arrays.asList(modes).indexOf(getValue());
        setValue(modes[index < modes.length - 1 ? index + 1 : 0]);
    }

    public void decrement() {
        int index = Arrays.asList(modes).indexOf(getValue());
        setValue(modes[index > 0 ? index - 1 : modes.length - 1]);
    }

    public String[] getModesAsString() {
        List<String> modeList = new ArrayList<>();
        for (T mode : modes)
            modeList.add(mode.toString());
        return modeList.toArray(new String[0]);
    }

    @Override
    public String toString() {
        if (value == null) return "null";
        return value.toString();
    }
}
