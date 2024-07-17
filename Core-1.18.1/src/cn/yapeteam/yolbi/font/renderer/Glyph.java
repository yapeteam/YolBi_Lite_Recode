package cn.yapeteam.yolbi.font.renderer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Glyph {
    int u;
    int v;
    int width;
    int height;
    char value;
    GlyphMap owner;

    int u() {
        return u;
    }

    int v() {
        return v;
    }

    int width() {
        return width;
    }

    int height() {
        return height;
    }

    char value() {
        return value;
    }

    GlyphMap owner() {
        return owner;
    }
}
