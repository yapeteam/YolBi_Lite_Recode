package cn.yapeteam.yolbi.managers;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventLoadWorld;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.utils.IMinecraft;
import cn.yapeteam.yolbi.utils.web.URLUtil;
import com.google.gson.JsonObject;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

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
    public void TickEvent(EventTick e){
        if(mc.theWorld == null) return;
        mc.theWorld.playerEntities.forEach(entity -> {
            if (entity != mc.thePlayer && (!bots.contains(entity) && isbot(entity))) {
                if(!bots.contains(entity)) addBot(entity);
            }
        });
    }

    public boolean isbot(EntityPlayer entity){
        // must have a player info
        final NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(entity.getUniqueID());
        if (info == null) {
            return true;
        }

        return false;
    }

}
