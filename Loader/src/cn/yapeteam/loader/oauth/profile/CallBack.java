package cn.yapeteam.loader.oauth.profile;

import java.util.UUID;

public interface CallBack {
    void login(String name, UUID uuid, String access_token, String refresh_token);
}
