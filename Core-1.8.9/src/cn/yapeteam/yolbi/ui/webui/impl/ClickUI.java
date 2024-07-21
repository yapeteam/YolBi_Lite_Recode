package cn.yapeteam.yolbi.ui.webui.impl;

import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.ui.webui.WebScreen;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.IJSQueryCallback;

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
        String[] parts = query.split("/");
        if (parts.length == 2 && parts[0].equals("clickui")) {
            switch (parts[1]) {
                case "cats":
                    JsonArray array = new JsonArray();
                    for (ModuleCategory value : ModuleCategory.values())
                        array.add(new JsonPrimitive(value.name()));
                    cb.success(array.toString());
                    break;
                default:
                    cb.failure(404, "invalid request");
            }
            return true;
        }
        return false;
    }

    @Override
    public void cancelQuery(IBrowser b, long queryId) {

    }
}
