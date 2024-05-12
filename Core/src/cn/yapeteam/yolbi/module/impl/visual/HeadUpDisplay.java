package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.ResourceManager;
import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.loader.api.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventExternalRender;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.font.FontUtil;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import lombok.val;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ModuleInfo(name = "HUD", category = ModuleCategory.VISUAL)
public class HeadUpDisplay extends Module {
    private ClientTheme theme = null;
    private final BooleanValue waterMark = new BooleanValue("Water Mark", true);
    private final BooleanValue moduleList = new BooleanValue("Module List", true);
    private final ModeValue<String> font = new ModeValue<>("Font", "Jello", "Jello", "PingFang", "default");

    private final Font font18;

    public HeadUpDisplay() {
        addValues(waterMark, moduleList, font);
        font18 = FontUtil.getFontFromTTF(new ByteArrayInputStream(Objects.requireNonNull(ResourceManager.resources.get("fonts/PingFang_Normal.ttf"))), 18, Font.PLAIN);
    }

    @Listener
    private void onRender(EventRender2D e) {
        if (theme == null)
            theme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);
        val font = getFontRenderer();
        if (waterMark.getValue()) {
            font.drawString(YolBi.name + " " + YolBi.version, 2, 2, -1);
        }
        if (moduleList.getValue()) {
            List<Module> activeModules = YolBi.instance.getModuleManager().getModules().stream().filter(Module::isEnabled).sorted(Comparator.comparingInt(m -> -(m.getName() + (m.getSuffix() != null ? " " + m.getSuffix() : "")).length())).collect(Collectors.toList());
            for (int i = 0; i < activeModules.size(); i++) {
                Module module = activeModules.get(i);
                String text = module.getName() + (module.getSuffix() != null ? " " + module.getSuffix() : "");
                double width = font.getStringWidth(text) + 4;
                float height = 12;
                double x = e.getScaledresolution().getScaledWidth() - width;
                double y = i * height;
                {
                    RenderUtil.drawRect2(x, y, width, height, 0x80000000);
                    font.drawString(text, x + 2, y + (height - font.getHeight()) / 2f, theme.getColor(i * 100));
                }
            }
        }
    }

    @Listener
    private void onExternal(EventExternalRender e) {
        Graphics g = e.getGraphics();
        if (theme == null)
            theme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);

        if (moduleList.getValue()) {
            List<Module> activeModules = YolBi.instance.getModuleManager().getModules().stream().filter(Module::isEnabled).sorted(Comparator.comparingInt(m -> -(m.getName() + (m.getSuffix() != null ? " " + m.getSuffix() : "")).length())).collect(Collectors.toList());
            for (int i = 0; i < activeModules.size(); i++) {
                Module module = activeModules.get(i);
                String text = module.getName() + (module.getSuffix() != null ? " " + module.getSuffix() : "");
                float height = 18;
                double y = 100 + i * height;
                {
                    g.setFont(font18);
                    float width = g.getFontMetrics().stringWidth(text) + 4 * 1.5f;
                    g.setColor(new Color(0x80000000, true));
                    g.fillRect(100, (int) y, (int) width, (int) height);
                    g.setColor(new Color(theme.getColor(i * 100)));
                    g.drawString(text, 102, (int) (y + (height - g.getFontMetrics().getHeight()) / 2f + height));
                }
            }
        }
    }

    private AbstractFontRenderer getFontRenderer() {
        switch (font.getValue()) {
            case "Jello":
                return YolBi.instance.getFontManager().getJelloRegular18();
            case "PingFang":
                return YolBi.instance.getFontManager().getPingFang18();
            case "default":
                return YolBi.instance.getFontManager().getDefault18();
        }
        return YolBi.instance.getFontManager().getDefault18();
    }
}
