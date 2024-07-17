package cn.yapeteam.yolbi.font.renderer;

import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.Closeable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class FontRenderer implements Closeable, AbstractFontRenderer {
    public static Minecraft mc = Minecraft.getInstance();
    private static final Char2IntArrayMap colorCodes = new Char2IntArrayMap() {{
        put('0', 0x000000);
        put('1', 0x0000AA);
        put('2', 0x00AA00);
        put('3', 0x00AAAA);
        put('4', 0xAA0000);
        put('5', 0xAA00AA);
        put('6', 0xFFAA00);
        put('7', 0xAAAAAA);
        put('8', 0x555555);
        put('9', 0x5555FF);
        put('A', 0x55FF55);
        put('B', 0x55FFFF);
        put('C', 0xFF5555);
        put('D', 0xFF55FF);
        put('E', 0xFFFF55);
        put('F', 0xFFFFFF);
    }};

    private static final ExecutorService ASYNC_WORKER = Executors.newCachedThreadPool();
    private final Object2ObjectMap<ResourceLocation, ObjectList<DrawEntry>> GLYPH_PAGE_CACHE = new Object2ObjectOpenHashMap<>();
    private final float originalSize;
    private final ObjectList<GlyphMap> maps = new ObjectArrayList<>();
    private final Char2ObjectArrayMap<Glyph> allGlyphs = new Char2ObjectArrayMap<>();
    private final int charsPerPage;
    private final int padding;
    private final String prebakeGlyphs;
    private int scaleMul = 0;
    private Font font;
    private int previousGameScale = -1;
    private Future<Void> prebakeGlyphsFuture;
    private boolean initialized;

    public FontRenderer(Font font, float sizePx, int charactersPerPage, int paddingBetweenCharacters, @Nullable String prebakeCharacters) {
        this.originalSize = sizePx;
        this.charsPerPage = charactersPerPage;
        this.padding = paddingBetweenCharacters;
        this.prebakeGlyphs = prebakeCharacters;

        init(font, sizePx);
    }

    public FontRenderer(Font font, float sizePx) {
        this(font, sizePx, 256, 5, null);
    }

    private static int floorNearestMulN(int x, int n) {
        return n * (int) Math.floor((double) x / (double) n);
    }

    public static String stripControlCodes(String text) {
        char[] chars = text.toCharArray();
        StringBuilder f = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '§') {
                i++;
                continue;
            }
            f.append(c);
        }
        return f.toString();
    }

    private void sizeCheck() {
        int gs = (int) mc.getWindow().getGuiScale();
        if (gs != this.previousGameScale) {
            close();
            init(this.font, this.originalSize);
        }
    }

    private void init(Font font, float sizePx) {
        if (initialized) throw new IllegalStateException("Double call to init()");
        initialized = true;
        this.previousGameScale = (int) mc.getWindow().getGuiScale();
        this.scaleMul = this.previousGameScale;
        this.font = font.deriveFont(sizePx * this.scaleMul);
        if (prebakeGlyphs != null && !prebakeGlyphs.isEmpty()) {
            prebakeGlyphsFuture = this.prebake();
        }
    }

    private Future<Void> prebake() {
        return ASYNC_WORKER.submit(() -> {
            for (char c : prebakeGlyphs.toCharArray()) {
                if (Thread.interrupted()) break;
                locateGlyph1(c);
            }
            return null;
        });
    }

    private GlyphMap generateMap(char from, char to) {
        GlyphMap gm = new GlyphMap(from, to, this.font, randomResourceLocation(), padding);
        maps.add(gm);
        return gm;
    }

    private Glyph locateGlyph0(char glyph) {
        for (GlyphMap map : maps) {
            if (map.contains(glyph)) {
                return map.getGlyph(glyph);
            }
        }
        int base = floorNearestMulN(glyph, charsPerPage);
        GlyphMap glyphMap = generateMap((char) base, (char) (base + charsPerPage));
        return glyphMap.getGlyph(glyph);
    }

    @Nullable
    private Glyph locateGlyph1(char glyph) {
        return allGlyphs.computeIfAbsent(glyph, this::locateGlyph0);
    }

    @Override
    public void drawString(PoseStack stack, String s, double x, double y, int color) {
        float r = ((color >> 16) & 0xff) / 255f;
        float g = ((color >> 8) & 0xff) / 255f;
        float b = ((color) & 0xff) / 255f;

        float a = ((color >> 24) & 0xff) / 255f;
        drawString(stack, s, (float) x, (float) y, r, g, b, a);
    }

    @Override
    public void drawString(PoseStack stack, String s, double x, double y, Color color) {
        drawString(stack, s, (float) x, (float) y, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha());
    }

    @Override
    public double getHeight() {
        return getStringHeight("A");
    }

    @Override
    public Font getFont() {
        return font;
    }

    public void drawString(PoseStack stack, String s, float x, float y, float r, float g, float b, float a) {
        drawString(stack, s, x, y, r, g, b, a, false, 0);
    }

    public static double roundToDecimal(double n, int point) {
        if (point == 0) {
            return Math.floor(n);
        }
        double factor = Math.pow(10, point);
        return Math.round(n * factor) / factor;
    }

    public void drawString(PoseStack stack, String s, float x, float y, float r, float g, float b, float a, boolean gradient, int offset) {
        if (prebakeGlyphsFuture != null && !prebakeGlyphsFuture.isDone()) {
            try {
                prebakeGlyphsFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        sizeCheck();
        float r2 = r, g2 = g, b2 = b;
        stack.pushPose();
        y -= 3f;
        stack.translate(roundToDecimal(x, 1), roundToDecimal(y, 1), 0);
        stack.scale(1f / this.scaleMul, 1f / this.scaleMul, 1f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);//getPositionTexColorProgram

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        Matrix4f mat = stack.last().pose();

        char[] chars = s.toCharArray();
        float xOffset = 0;
        float yOffset = 0;
        boolean inSel = false;
        int lineStart = 0;

        synchronized (GLYPH_PAGE_CACHE) {
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (inSel) {
                    inSel = false;
                    char c1 = Character.toUpperCase(c);
                    if (colorCodes.containsKey(c1)) {
                        int ii = colorCodes.get(c1);
                        int[] col = RGBIntToRGB(ii);
                        r2 = col[0] / 255f;
                        g2 = col[1] / 255f;
                        b2 = col[2] / 255f;
                    } else if (c1 == 'R') {
                        r2 = r;
                        g2 = g;
                        b2 = b;
                    }
                    continue;
                }

                if (gradient) {
                    Color color = Color.green;//HudEditor.getColor(i * offset);
                    r2 = color.getRed() / 255f;
                    g2 = color.getGreen() / 255f;
                    b2 = color.getBlue() / 255f;
                    a = color.getAlpha() / 255f;
                }

                if (c == '§') {
                    inSel = true;
                    continue;
                } else if (c == '\n') {
                    yOffset += (float) (getStringHeight(s.substring(lineStart, i)) * scaleMul);
                    xOffset = 0;
                    lineStart = i + 1;
                    continue;
                }
                Glyph glyph = locateGlyph1(c);
                if (glyph != null) {
                    if (glyph.value() != ' ') {
                        ResourceLocation i1 = glyph.owner().bindToTexture;
                        DrawEntry entry = new DrawEntry(xOffset, yOffset, r2, g2, b2, glyph);
                        GLYPH_PAGE_CACHE.computeIfAbsent(i1, integer -> new ObjectArrayList<>()).add(entry);
                    }
                    xOffset += glyph.width();
                }
            }
            for (ResourceLocation ResourceLocation : GLYPH_PAGE_CACHE.keySet()) {
                RenderSystem.setShaderTexture(0, ResourceLocation);
                List<DrawEntry> objects = GLYPH_PAGE_CACHE.get(ResourceLocation);
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);//POSITION_TEXTURE_COLOR
                for (DrawEntry object : objects) {
                    float xo = object.atX;
                    float yo = object.atY;
                    float cr = object.r;
                    float cg = object.g;
                    float cb = object.b;
                    Glyph glyph = object.toDraw;
                    GlyphMap owner = glyph.owner();
                    float w = glyph.width();
                    float h = glyph.height();
                    float u1 = (float) glyph.u() / owner.width;
                    float v1 = (float) glyph.v() / owner.height;
                    float u2 = (float) (glyph.u() + glyph.width()) / owner.width;
                    float v2 = (float) (glyph.v() + glyph.height()) / owner.height;

                    bufferBuilder.vertex(mat, xo + 0, yo + h, 0).uv(u1, v2).color(cr, cg, cb, a).endVertex();
                    bufferBuilder.vertex(mat, xo + w, yo + h, 0).uv(u2, v2).color(cr, cg, cb, a).endVertex();
                    bufferBuilder.vertex(mat, xo + w, yo + 0, 0).uv(u2, v1).color(cr, cg, cb, a).endVertex();
                    bufferBuilder.vertex(mat, xo + 0, yo + 0, 0).uv(u1, v1).color(cr, cg, cb, a).endVertex();
                }
                //bb.end();
                tesselator.end();//bufferBuilder.end();
                //BufferedReader.(bb.end());//drawWithGlobalProgram
            }
            GLYPH_PAGE_CACHE.clear();
        }

        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.0F);
        stack.popPose();
        RenderSystem.bindTexture(0);
    }

    @Override
    public void drawCenteredString(PoseStack stack, String s, double x, double y, int color) {
        float r = ((color >> 16) & 0xff) / 255f;
        float g = ((color >> 8) & 0xff) / 255f;
        float b = ((color) & 0xff) / 255f;
        float a = ((color >> 24) & 0xff) / 255f;
        drawString(stack, s, (float) (x - getStringWidth(s) / 2f), (float) y, r, g, b, a);
    }

    @Override
    public void drawCenteredString(PoseStack stack, String s, double x, double y, Color color) {
        drawString(stack, s, (float) (x - getStringWidth(s) / 2f), (float) y, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public void drawCenteredString(PoseStack stack, String s, float x, float y, float r, float g, float b, float a) {
        drawString(stack, s, (float) (x - getStringWidth(s) / 2f), y, r, g, b, a);
    }

    @Override
    public double getStringWidth(String text) {
        char[] c = stripControlCodes(text).toCharArray();
        float currentLine = 0;
        float maxPreviousLines = 0;
        for (char c1 : c) {
            if (c1 == '\n') {
                maxPreviousLines = Math.max(currentLine, maxPreviousLines);
                currentLine = 0;
                continue;
            }
            Glyph glyph = locateGlyph1(c1);
            currentLine += glyph == null ? 0 : (glyph.width() / (float) this.scaleMul);
        }
        return Math.max(currentLine, maxPreviousLines);
    }

    @Override
    public double getStringHeight(String text) {
        char[] c = stripControlCodes(text).toCharArray();
        if (c.length == 0) {
            c = new char[]{' '};
        }
        float currentLine = 0;
        float previous = 0;
        for (char c1 : c) {
            if (c1 == '\n') {
                if (currentLine == 0) {
                    currentLine = (locateGlyph1(' ') == null ? 0 : (Objects.requireNonNull(locateGlyph1(' ')).height() / (float) this.scaleMul));
                }
                previous += currentLine;
                currentLine = 0;
                continue;
            }
            Glyph glyph = locateGlyph1(c1);
            currentLine = Math.max(

                    glyph == null ? 0 : (glyph.height() / (float) this.scaleMul)

                    , currentLine);
        }
        return currentLine + previous;
    }

    @Override
    public void close() {
        try {
            if (prebakeGlyphsFuture != null && !prebakeGlyphsFuture.isDone() && !prebakeGlyphsFuture.isCancelled()) {
                prebakeGlyphsFuture.cancel(true);
                prebakeGlyphsFuture.get();
                prebakeGlyphsFuture = null;
            }
            for (GlyphMap map : maps) {
                map.destroy();
            }
            maps.clear();
            allGlyphs.clear();
            initialized = false;
        } catch (Exception ignored) {
        }
    }

    @Contract(value = "-> new", pure = true)
    public static @NotNull ResourceLocation randomResourceLocation() {
        return new ResourceLocation("minecraft", randomString());
    }

    public static int nextInt(int origin, int bound) {
        Random rng = new Random();
        int r = new Random().nextInt();
        if (origin < bound) {
            // It's not case (1).
            final int n = bound - origin;
            final int m = n - 1;
            if ((n & m) == 0) {
                // It is case (2): length of range is a power of 2.
                r = (r & m) + origin;
            } else if (n > 0) {
                // It is case (3): need to reject over-represented candidates.
                for (int u = r >>> 1;
                     u + m - (r = u % n) < 0;
                     u = rng.nextInt() >>> 1)
                    ;
                r += origin;
            } else {
                // It is case (4): length of range not representable as long.
                while (r < origin || r >= bound) {
                    r = rng.nextInt();
                }
            }
        }
        return r;
    }

    private static String randomString() {
        return IntStream.range(0, 32)
                .mapToObj(operand -> String.valueOf((char) nextInt('a', 'z' + 1)))
                .collect(Collectors.joining());
    }

    @Contract(value = "_ -> new", pure = true)
    public static int @NotNull [] RGBIntToRGB(int in) {
        int red = in >> 8 * 2 & 0xFF;
        int green = in >> 8 & 0xFF;
        int blue = in & 0xFF;
        return new int[]{red, green, blue};
    }

    public double getFontHeight(String str) {
        return getStringHeight(str);
    }

    public void drawGradientString(PoseStack stack, String s, float x, float y, int offset) {
        drawString(stack, s, x, y, 255, 255, 255, 255, true, offset);
    }

    public void drawGradientCenteredString(PoseStack matrices, String s, float x, float y, int i) {
        drawGradientString(matrices, s, (float) (x - getStringWidth(s) / 2f), y, i);
    }

    @AllArgsConstructor
    public static class DrawEntry {
        float atX;
        float atY;
        float r;
        float g;
        float b;
        Glyph toDraw;
    }
}
