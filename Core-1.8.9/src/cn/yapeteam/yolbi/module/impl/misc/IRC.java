//package cn.yapeteam.yolbi.module.impl.misc;
//
//import cn.yapeteam.yolbi.event.Listener;
//import cn.yapeteam.yolbi.event.impl.game.EventTick;
//import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
//import cn.yapeteam.yolbi.module.Module;
//import cn.yapeteam.yolbi.module.ModuleCategory;
//import io.netty.buffer.Unpooled;
//import net.minecraft.client.network.NetworkPlayerInfo;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.network.play.client.C17PacketCustomPayload;
//import net.minecraft.network.play.server.S38PacketPlayerListItem;
//import net.minecraft.util.ChatComponentText;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class IRC extends Module {
//
//    private Set<String> ircUsers = new HashSet<>();  // 存储 IRC 用户的昵称集合
//
//    public IRC() {
//        super("IRC(To be fixed)", ModuleCategory.MISC);
//    }
//
//    @Override
//    public void onEnable() {
//        super.onEnable();
//        sendIRCIdentificationPacket();
//    }
//
//    @Listener
//    private void onTick(EventTick event) {
//        sendIRCIdentificationPacket();
//    }
//
//    private void sendIRCIdentificationPacket() {
//        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
//        buffer.writeString("IRC");
//        buffer.writeString(mc.thePlayer.getName());  // 自动获取当前玩家的昵称
//        C17PacketCustomPayload packet = new C17PacketCustomPayload("IRC|Ident", buffer);
//        mc.getNetHandler().addToSendQueue(packet);
//    }
//
//    @Listener
//    private void onPacketReceive(EventPacketReceive event) {
//        if (event.getPacket() instanceof S38PacketPlayerListItem) {
//            S38PacketPlayerListItem packet = (S38PacketPlayerListItem) event.getPacket();
//            List<S38PacketPlayerListItem.AddPlayerData> players = packet.getEntries();
//            for (S38PacketPlayerListItem.AddPlayerData playerData : players) {
//                NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(playerData.getProfile().getId());
//                if (playerInfo != null) {
//                    String displayName = playerInfo.getDisplayName() != null ? playerInfo.getDisplayName().getUnformattedText() : playerInfo.getGameProfile().getName();
//                    if (isIRCUser(displayName) && !displayName.contains("[IRC]")) {
//                        playerInfo.setDisplayName(new ChatComponentText(displayName + " [IRC]"));
//                    }
//                }
//            }
//        }
//    }
//
//    private boolean isIRCUser(String playerName) {
//        // 检查玩家昵称是否在 IRC 用户列表中
//        return ircUsers.contains(playerName);
//    }
//
//    public void addIRCUser(String playerName) {
//        ircUsers.add(playerName);  // 添加 IRC 用户到列表
//    }
//
//    public void removeIRCUser(String playerName) {
//        ircUsers.remove(playerName);  // 从列表中移除 IRC 用户
//    }
//}



package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import io.netty.buffer.Unpooled;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class IRC extends Module {

    private String server = "https://27.25.142.18";
    private int port = 8889;
    private String channel = "#chat";
    private String nickname = "[Yolbi]";

    public IRC() {
        super("IRC", ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        sendIRCIdentificationPacket();
    }

    @Listener
    private void onTick(EventTick event) {
        // Periodically send the IRC identification packet
        sendIRCIdentificationPacket();
    }

    private void sendIRCIdentificationPacket() {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeString("IRC");
        buffer.writeString(nickname);
        C17PacketCustomPayload packet = new C17PacketCustomPayload("IRC|Ident", buffer);
        mc.getNetHandler().addToSendQueue(packet);
    }

    @Listener
    private void onPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem packet = (S38PacketPlayerListItem) event.getPacket();
            List<S38PacketPlayerListItem.AddPlayerData> players = packet.getEntries();
            for (S38PacketPlayerListItem.AddPlayerData playerData : players) {
                NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(playerData.getProfile().getId());
                if (playerInfo != null) {
                    String displayName = playerInfo.getDisplayName().getUnformattedText();
                    if (!displayName.contains("[IRC]")) {
                        playerInfo.setDisplayName(new ChatComponentText(displayName + " [IRC]"));
                    }
                }
            }
        }
    }
}



//package cn.yapeteam.yolbi.module.impl.misc;
//
//import cn.yapeteam.yolbi.module.Module;
//import cn.yapeteam.yolbi.module.ModuleCategory;
//import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
//import net.minecraft.network.play.client.C01PacketChatMessage;
//
//public class IRC extends Module {
//
//    private final BooleanValue sendIRCIdentity = new BooleanValue("Send IRC Identity", true);
//
//    public IRC() {
//        super("IRC", ModuleCategory.MISC);
//        addValues(sendIRCIdentity);
//    }
//
//    @Override
//    protected void onEnable() {
//        if (sendIRCIdentity.getValue()) {
//            sendIRCIdentityPacket();
//        }
//    }
//
//    private void sendIRCIdentityPacket() {
//        String message = "/IRCIDENTITY1";
//        mc.getNetHandler().getNetworkManager().sendPacket(new C01PacketChatMessage(message));
//    }
//
//    @Override
//    protected void onDisable() {
//
//    }
//}
