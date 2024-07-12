package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.render.GradientBlur;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import lombok.val;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HeadUpDisplay extends Module {
    private ClientTheme theme = null;
    private final BooleanValue waterMark = new BooleanValue("Water Mark", true);
    private final BooleanValue moduleList = new BooleanValue("Module List", true);
    private final ModeValue<String> font = new ModeValue<>("Font", "PingFang", "Jello", "PingFang", "default");

    private final Map<Module, ModuleNode> moduleNodes = new HashMap<>();

    public HeadUpDisplay() {
        super("HUD", ModuleCategory.VISUAL);
        addValues(waterMark, moduleList, font);
    }

    static class ModuleNode {
        private final GradientBlur gradientBlur = new GradientBlur();

        public void render(AbstractFontRenderer font, String text, ClientTheme theme, float x, float y, float width, float height, int index) {
            gradientBlur.set(x, y, (int) width, (int) height, 50);
            RenderUtil.drawBloomShadow(x, y, width, height, 12, 15, theme.getColor(index * 200), true, true, true, false, false);
            RenderUtil.drawGradientRectTB(x, y, x + width, y + height, gradientBlur.getBColor().getRGB(), gradientBlur.getTColor().getRGB());
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 66).getRGB());
            font.drawString(text, x + 2.5, y + (height - font.getHeight()) / 2f + 0.5f, new Color(0, 0, 0).getRGB());
            font.drawString(text, x + 2, y + (height - font.getHeight()) / 2f, theme.getColor(index * 200), false);
        }

        public void update() {
            gradientBlur.getPixels();
            gradientBlur.update(GradientBlur.ColorMode.TOP_RIGHT);
        }
    }

    @Listener
    private void onRender(EventRender2D e) {
        if (theme == null)
            theme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);
        val font = getFontRenderer();
        if (waterMark.getValue())
            font.drawString(YolBi.name + " " + YolBi.version, 2, 2, -1);
        if (moduleList.getValue()) {
            List<Module> activeModules = YolBi.instance.getModuleManager().getModules().stream()
                    .filter(Module::isEnabled)
                    .sorted(Comparator.comparingInt(m -> (int) -font.getStringWidth(m.getName() + (m.getSuffix() != null ? " " + m.getSuffix() : ""))))
                    .collect(Collectors.toList());
            moduleNodes.values().forEach(ModuleNode::update);
            for (int i = 0; i < activeModules.size(); i++) {
                Module module = activeModules.get(i);
                String text = module.getName() + (module.getSuffix() != null ? " " + module.getSuffix() : "");
                double width = font.getStringWidth(text) + 4;
                float height = 12;
                double x = e.getScaledresolution().getScaledWidth() - width;
                double y = i * height;
                ModuleNode node = moduleNodes.get(module);
                if (node == null) {
                    node = new ModuleNode();
                    moduleNodes.put(module, node);
                }
                node.render(font, text, theme, (float) x, (float) y, (float) width, height, i);
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
