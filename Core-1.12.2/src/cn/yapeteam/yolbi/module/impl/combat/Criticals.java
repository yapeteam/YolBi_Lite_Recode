package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import net.minecraft.network.play.client.CPacketPlayer;

public class Criticals extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Packet", "Packet", "Single Packet", "Low Jump", "Jump");

    public Criticals() {
        super("Criticals", ModuleCategory.COMBAT);
        addValues(mode);
    }

    private boolean canCritical() {
        return mc.player != null && mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isRiding();
    }

    @Listener
    private void onAttack(EventAttack event) {
        // critical check
        if (!canCritical()) return;

        String curMode = mode.getValue(); // get mode
        double x = mc.player.posX, y = mc.player.posY, z = mc.player.posZ; // i'm lazy

        switch (curMode) {
            case "Packet":
                PacketUtil.sendPacket(new CPacketPlayer.Position(x, y + 0.0625, z, false));
                PacketUtil.sendPacket(new CPacketPlayer.Position(x, y, z, false));
                PacketUtil.sendPacket(new CPacketPlayer.Position(x, y + MathUtils.getRandom(0.001, 0.01), z, false));
                PacketUtil.sendPacket(new CPacketPlayer.Position(x, y, z, false));
                break;
            case "Single Packet":
                PacketUtil.sendPacket(new CPacketPlayer.Position(x, y + 0.11, z, false));
                break;
            case "Low Jump":
                // just for fun :)
                mc.player.motionY = mc.player.ticksExisted % 3 == 0 ? 0.16 : 0.14;
                break;
            case "Jump":
                mc.player.jump(); // may be legit?
                break;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}
