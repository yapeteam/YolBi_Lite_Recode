package cn.yapeteam.yolbi.managers;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventLoadWorld;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.utils.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Objects;

public class BotManager implements IMinecraft {
    public static ArrayList<Entity> bots = new ArrayList<>();

    @Listener
    public void onWorldChange(EventLoadWorld event) {
        bots.clear();
    }

    public static boolean addBot(Entity entity) {
        if (!bots.contains(entity)) {
            bots.add(entity);
            return true;
        }
        return false;
    }

    @Listener
    public void TickEvent(EventTick e) {
        if (mc.world == null) return;
        mc.world.playerEntities.forEach(entity -> {
            if (entity != mc.player && (!bots.contains(entity) && isbot(entity))) {
                if (!bots.contains(entity)) addBot(entity);
            }
        });
    }

    public boolean isbot(EntityPlayer entity) {
        return Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entity.getUniqueID()) == null;
    }
}
