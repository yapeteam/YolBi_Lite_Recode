package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.util.Objects;

public class ClientSpoof extends Module {
    private final ModeValue<String> mode = new ModeValue<String>("Spoof Client", "Vanilla",
            "Lunar",
            "Vanilla",
            "OptiFine",
            "Fabric",
            "LabyMod",
            "Feather",
            "CheatBreaker"
    );

    public ClientSpoof() {
        super("ClientSpoof", ModuleCategory.MISC);
        addValues(mode);
    }

    @Listener
    private void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof CPacketCustomPayload) {
            e.setCancelled(true);

            if (mode.is("LabyMod")) {
                Packet<INetHandlerPlayServer> packet1 = new CPacketCustomPayload("labymod3:main", getInfo());
                Packet<INetHandlerPlayServer> packet2 = new CPacketCustomPayload("LMC", getInfo());
                PacketUtil.skip(packet1);
                PacketUtil.skip(packet2);
                Objects.requireNonNull(mc.getConnection()).sendPacket(packet1);
                Objects.requireNonNull(mc.getConnection()).sendPacket(packet2);
                return;
            }

            PacketBuffer buffer;
            switch (mode.getValue()) {
                case "Lunar":
                    buffer = generatePacketBuffer("lunarclient:v2.14.5-2411");
                    break;
                case "OptiFine":
                    buffer = generatePacketBuffer("optifine");
                    break;
                case "Fabric":
                    buffer = generatePacketBuffer("fabric");
                    break;
                case "Feather":
                    buffer = generatePacketBuffer("Feather Forge");
                    break;
                case "CheatBreaker":
                    buffer = generatePacketBuffer("CB");
                    break;
                case "Vanilla":
                default:
                    buffer = generatePacketBuffer("vanilla");
                    break;
            }
            Packet<INetHandlerPlayServer> packet = new CPacketCustomPayload("MC|Brand", buffer);
            PacketUtil.skip(packet);
            Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
        }
    }

    private PacketBuffer getInfo() {
        return new PacketBuffer(Unpooled.buffer())
                .writeString("INFO")
                .writeString(
                        "{  \n" +
                                "   \"version\": \"3.9.25\",\n" +
                                "   \"ccp\": {  \n" +
                                "      \"enabled\": true,\n" +
                                "      \"version\": 2\n" +
                                "   },\n" +
                                "   \"shadow\":{  \n" +
                                "      \"enabled\": true,\n" +
                                "      \"version\": 1\n" +
                                "   },\n" +
                                "   \"addons\": [  \n" +
                                "      {  \n" +
                                "         \"uuid\": \"null\",\n" +
                                "         \"name\": \"null\"\n" +
                                "      }\n" +
                                "   ],\n" +
                                "   \"mods\": [\n" +
                                "      {  \n" +
                                "         \"hash\":\"sha256:null\",\n" +
                                "         \"name\":\"null.jar\"\n" +
                                "      }\n" +
                                "   ]\n" +
                                "}"
                );
    }

    private PacketBuffer generatePacketBuffer(String string) {
        return new PacketBuffer(Unpooled.buffer()).writeString(string);
    }
}
