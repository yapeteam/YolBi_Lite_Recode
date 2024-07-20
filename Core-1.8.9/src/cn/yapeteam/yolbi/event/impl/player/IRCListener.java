//package cn.yapeteam.yolbi.event.impl.player;
//
//import cn.yapeteam.yolbi.event.Listener;
//import cn.yapeteam.yolbi.event.impl.EventPacket;
//import com.mojang.authlib.GameProfile;
//import net.minecraft.network.play.server.S38PacketPlayerListItem;
//import net.minecraft.network.play.server.S38PacketPlayerListItem.Action;
//import net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData;
//import net.minecraft.util.IChatComponent;
//import net.minecraft.util.ChatComponentText;
//
//public class IRCListener extends Listener<EventPacket> {
//
//    @Override
//    public void onEvent(EventPacket event) {
//        if (event.getPacket() instanceof S38PacketPlayerListItem) {
//            S38PacketPlayerListItem packet = (S38PacketPlayerListItem) event.getPacket();
//            if (packet.getAction() == Action.ADD_PLAYER) {
//                for (AddPlayerData data : packet.getEntries()) {
//                    if (isIRCUser(data.getProfile().getName())) {
//                        // 修改玩家名称前缀为[IRC]
//                        GameProfile newProfile = new GameProfile(data.getProfile().getId(), "[IRC] " + data.getProfile().getName());
//                        data.setProfile(newProfile);
//                        // 设置名称为绿色
//                        data.setDisplayName(new ChatComponentText("§a[IRC] " + data.getProfile().getName()));
//                    }
//                }
//            }
//        }
//    }
//
//    private boolean isIRCUser(String name) {
//        // 检查玩家名称是否以"[IRC]"开头
//        return name.startsWith("[IRC]");
//    }
//}
