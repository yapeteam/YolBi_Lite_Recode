package org.cef.network;

import java.util.Date;

public final class CefCookie {
    public final String name;
    public final String value;
    public final String domain;
    public final String path;
    public final boolean secure;
    public final boolean httponly;
    public final Date creation;
    public final Date lastAccess;
    public final boolean hasExpires;
    public final Date expires;

    public CefCookie(String name, String value, String domain, String path, boolean secure, boolean httponly, Date creation, Date lastAccess, boolean hasExpires, Date expires) {
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httponly = httponly;
        this.creation = creation;
        this.lastAccess = lastAccess;
        this.hasExpires = hasExpires;
        this.expires = expires;
    }
}
