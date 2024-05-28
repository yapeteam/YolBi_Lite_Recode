package cn.yapeteam.loader.utils.vec;

import cn.yapeteam.loader.mixin.annotations.DontMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DontMap
@Getter
@Setter
@AllArgsConstructor
public class Vector4d {
    public double x;
    public double y;
    public double z;
    public double w;
}
