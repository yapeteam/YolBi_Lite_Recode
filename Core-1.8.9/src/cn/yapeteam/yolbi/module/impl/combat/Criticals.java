package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Packet", "Packet", "Single Packet", "Low Jump", "Jump");

    public Criticals() {
        super("Criticals", ModuleCategory.COMBAT);
        addValues(mode);
    }

    private boolean canCritical() {
        return mc.thePlayer != null && mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava() && !mc.thePlayer.isRiding();
    }

    @Listener
    private void onAttack(EventAttack event) {
        // critical check
        if (!canCritical()) return;

        String curMode = mode.getValue(); // get mode
        double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ; // i'm lazy

        switch (curMode) {
            case "Packet":
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0625, z, false));
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + MathUtils.getRandom(0.001, 0.01), z, false));
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                break;
            case "Single Packet":
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11, z, false));
                break;
            case "Low Jump":
                // just for fun :)
                mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 3 == 0 ? 0.16 : 0.14;
                break;
            case "Jump":
                mc.thePlayer.jump(); // may be legit?
                break;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}
