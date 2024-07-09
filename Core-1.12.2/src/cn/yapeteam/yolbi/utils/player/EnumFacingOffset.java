package cn.yapeteam.yolbi.utils.player;

import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

@Getter
public class EnumFacingOffset {
    public EnumFacing enumFacing;
    private final Vec3d offset;

    public EnumFacingOffset(final EnumFacing enumFacing, final Vec3d offset) {
        this.enumFacing = enumFacing;
        this.offset = offset;
    }
}
