package cn.yapeteam.yolbi.event.impl.player;

import cn.yapeteam.yolbi.event.Event;

public class EventUpdate extends Event {
    public Object forward;
    private boolean isPre;
    private float yaw;
    private float pitch;
    private double x, y, z;
    private boolean onground;
    private boolean alwaysSend;
    private boolean sneaking;
    public static float YAW, PITCH, PREVYAW, PREVPITCH;
    public static boolean SNEAKING;

    public boolean isPre() {
        return isPre;
    }

    public boolean isPost() {
        return !isPre;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }
    public double getZ() {
        return this.z;
    }
    public boolean isSneaking(){
        return this.sneaking;
    }
    public boolean isOnground() {
        return this.onground;
    }
    public void setSneaking(boolean sneaking){
        this.sneaking = sneaking;
    }
    public boolean shouldAlwaysSend() {
        return this.alwaysSend;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setZ(double z) {
        this.z = z;
    }

    public void setGround(boolean ground) {
        this.onground = ground;
    }

    public void setAlwaysSend(boolean alwaysSend) {
        this.alwaysSend = alwaysSend;
    }

    public void setOnGround(boolean onground) {
        this.onground = onground;
    }

}