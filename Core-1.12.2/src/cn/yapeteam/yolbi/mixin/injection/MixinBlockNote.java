package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Inject;
import cn.yapeteam.ymixin.annotations.Local;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Target;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.block.EventNote;
import net.minecraft.block.BlockNote;
import net.minecraft.util.math.BlockPos;

@Mixin(BlockNote.class)
public class MixinBlockNote {
    @Inject(
            method = "eventReceived",
            desc = "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z",
            target = @Target("HEAD")
    )
    public void onNote(@Local(source = "pos", index = 3) BlockPos pos, @Local(source = "eventParam", index = 5) int eventParam, @Local(source = "eventID", index = 4) int eventID) {
        YolBi.instance.getEventManager().post(new EventNote(pos, eventParam, eventID));
    }
}
