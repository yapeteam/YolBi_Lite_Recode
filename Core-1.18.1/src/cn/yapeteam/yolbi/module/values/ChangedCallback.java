package cn.yapeteam.yolbi.module.values;

import cn.yapeteam.ymixin.annotations.DontMap;

@DontMap
public interface ChangedCallback<T> {
    T run(T oldV, T newV);
}
