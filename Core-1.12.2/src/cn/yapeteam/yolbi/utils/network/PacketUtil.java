package cn.yapeteam.yolbi.utils.network;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.network.EventFinalPacketSend;
import cn.yapeteam.yolbi.utils.IMinecraft;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unused")
public class PacketUtil implements IMinecraft {
    public static void sendPacket(Packet<INetHandlerPlayServer> packet) {
        Objects.requireNonNull(mc.getConnection()).getNetworkManager().sendPacket(packet);
    }

    public static Method flushOutboundQueue = null, dispatchPacket = null;

    static {
        try {
            flushOutboundQueue = NetworkManager.class.getDeclaredMethod(Mapper.map("net.minecraft.network.NetworkManager", "flushOutboundQueue", "()V", Mapper.Type.Method));
            dispatchPacket = NetworkManager.class.getDeclaredMethod(Mapper.map("net.minecraft.network.NetworkManager", "dispatchPacket", "(Lnet/minecraft/network/Packet;[Lio/netty/util/concurrent/GenericFutureListener;)V", Mapper.Type.Method), Packet.class, GenericFutureListener[].class);

            flushOutboundQueue.setAccessible(true);
            dispatchPacket.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Logger.exception(e);
        }
    }

    public static void sendPacketFinal(Packet<INetHandlerPlayServer> packet) {
        if (flushOutboundQueue != null && dispatchPacket != null) {
            EventFinalPacketSend event = new EventFinalPacketSend(packet);
            if (!PacketUtil.shouldIgnorePacket(packet))
                YolBi.instance.getEventManager().post(event);
            if (event.isCancelled()) return;
            try {
                flushOutboundQueue.invoke(Objects.requireNonNull(mc.getConnection()).getNetworkManager());
                dispatchPacket.invoke(mc.getConnection().getNetworkManager(), event.getPacket(), null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Logger.exception(e);
            }
        }
    }

    public static void sendPacketNoEvent(Packet<INetHandlerPlayServer> packet) {
        PacketUtil.skip(packet);
        mc.getConnection().getNetworkManager().sendPacket(packet);
    }

    public static ArrayList<Packet<? extends INetHandler>> skip_list = new ArrayList<>();

    public static void skip(Packet<? extends INetHandler> packet) {
        if (!skip_list.contains(packet)) {
            skip_list.add(packet);
        }
    }

    public static void remove(Packet<? extends INetHandler> packet) {
        skip_list.remove(packet);
    }

    public static boolean shouldSkip(Packet<? extends INetHandler> packet) {
        return skip_list.contains(packet);
    }

    public static boolean shouldIgnorePacket(Packet<? extends INetHandler> packet) {
        return packet instanceof CPacketLoginStart || packet instanceof CPacketEncryptionResponse || packet instanceof C00Handshake || packet instanceof CPacketServerQuery || packet instanceof CPacketPing;
    }
}
