package cn.yapeteam.yolbi.ui.webui.impl;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.module.values.Value;
import cn.yapeteam.yolbi.module.values.impl.*;
import cn.yapeteam.yolbi.ui.webui.WebScreen;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.IJSQueryCallback;

import java.awt.*;

public class ClickUI extends WebScreen {
    public static ClickUI instance = new ClickUI();

    public ClickUI() {
        super("clickui");
    }

    @Override
    public void onAddressChange(IBrowser browser, String url) {

    }

    @Override
    public void onTitleChange(IBrowser browser, String title) {
        System.out.println("Title: " + title);
    }

    @Override
    public void onTooltip(IBrowser browser, String text) {

    }

    @Override
    public void onStatusMessage(IBrowser browser, String value) {

    }

    @Override
    public boolean handleQuery(IBrowser b, long queryId, String query, boolean persistent, IJSQueryCallback cb) {
        //"clickui/mods:COMBAT"
        String[] parts = query.split(":");
        if (parts.length != 2 && parts.length != 1) return false;
        String[] path = parts[0].split("/");
        if (path.length == 2 && path[0].equals("clickui")) {
            String[] args = parts[1].split(",");
            JsonArray array;
            JsonObject object;
            ModuleManager manager = YolBi.instance.getModuleManager();
            switch (path[1]) {
                case "cats":
                    array = new JsonArray();
                    for (ModuleCategory value : ModuleCategory.values())
                        if (!manager.getModulesByCategory(value).isEmpty())
                            array.add(new JsonPrimitive(value.name()));
                    cb.success(array.toString());
                    break;
                case "mods":
                    if (args.length == 1) {
                        array = new JsonArray();
                        for (Module module : manager.getModulesByCategory(ModuleCategory.valueOf(args[0])))
                            array.add(new JsonPrimitive(module.getName()));
                        cb.success(array.toString());
                    } else cb.failure(404, "invalid args length");
                    break;
                case "values":
                    if (args.length == 1) {
                        array = new JsonArray();
                        JsonObject base = new JsonObject();
                        Module module = manager.getModuleByName(args[0]);
                        base.addProperty("bool:enabled", module.isEnabled());
                        base.addProperty("number:key", module.getKey());
                        array.add(base);
                        for (Value<?> value : module.getValues()) {
                            object = new JsonObject();
                            object.addProperty(value.getType() + ":" + value.getName(), value.toString());
                            switch (value.getType()) {
                                case "number":
                                    NumberValue<?> numberValue = (NumberValue<?>) value;
                                    object.addProperty("min", numberValue.getMin());
                                    object.addProperty("max", numberValue.getMax());
                                    object.addProperty("inc", numberValue.getInc());
                                    break;
                                case "mode":
                                    JsonArray mode_array = new JsonArray();
                                    ModeValue<?> modeValue = (ModeValue<?>) value;
                                    for (String mode : modeValue.getModesAsString())
                                        mode_array.add(new JsonPrimitive(mode));
                                    object.add("modes", mode_array);
                                    break;
                                default:
                                    break;
                            }
                            array.add(object);
                        }
                        cb.success(array.toString());
                    } else cb.failure(0, "invalid args length");
                    break;
                case "set"://modname valuename value
                    if (args.length == 3) {
                        Module module = manager.getModuleByName(args[0]);
                        String[] type_name = args[1].split(":");
                        String type = type_name[0];
                        String name = type_name[1];
                        String val = args[2];
                        switch (name) {
                            case "enabled":
                                module.setEnabled(Boolean.parseBoolean(val));
                                break;
                            case "key":
                                module.setKey(Integer.parseInt(val));
                                break;
                            default: {
                                Value<?> value = module.getValueByName(name);

                                switch (type) {
                                    case "bool":
                                        ((BooleanValue) value).setValue(Boolean.parseBoolean(val));
                                        break;
                                    case "color":
                                        ((ColorValue) value).setValue(new Color(Integer.parseInt(val)));
                                        break;
                                    case "mode":
                                        ((ModeValue<?>) value).setMode(val);
                                        break;
                                    case "number":
                                        ((NumberValue<?>) value).setValue(val);
                                        break;
                                    case "text":
                                        ((TextValue) value).setValue(val);
                                        break;
                                }
                            }
                        }
                        cb.success("");
                    } else cb.failure(0, "invalid args length");
                    break;
                default:
                    cb.failure(0, "invalid request");
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void cancelQuery(IBrowser b, long queryId) {

    }
}
