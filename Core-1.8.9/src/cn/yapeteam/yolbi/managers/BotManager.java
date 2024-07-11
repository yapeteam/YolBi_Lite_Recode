package cn.yapeteam.yolbi.managers;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventLoadWorld;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.module.impl.combat.AntiBot;
import cn.yapeteam.yolbi.utils.IMinecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class BotManager implements IMinecraft {
    public static ArrayList<Entity> bots = new ArrayList<>();

    @Listener
    public void onWorldChange(EventLoadWorld event) {
        bots.clear();
    }

    public static void addBot(Entity entity) {
        if (!bots.contains(entity))
            bots.add(entity);
    }

    @Listener
    public void TickEvent(EventTick e) {
        if (mc.theWorld == null) return;
        bots.forEach(entity -> {
            if (entity != mc.thePlayer && (!bots.contains(entity) && AntiBot.isBot((EntityPlayer) entity))) {
                if (!bots.contains(entity)) addBot(entity);
            }
        });
    }
}
