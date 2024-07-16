package cn.yapeteam.loader.oauth.profile;

import com.google.gson.JsonObject;
import com.mojang.authlib.UserType;

import java.util.UUID;

public interface IProfile {
    String getName();

    void setName(String name);

    UUID getUUID();

    boolean login(CallBack loginCallback) throws Exception;

    JsonObject serialize();

    UserType getUserType();
}
