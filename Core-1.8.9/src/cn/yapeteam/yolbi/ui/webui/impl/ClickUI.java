package cn.yapeteam.yolbi.ui.webui.impl;

import cn.yapeteam.yolbi.ui.webui.WebScreen;
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
        System.out.println(query);
        if (query.equals("run")) {
            //b.runJS("alert('Hello, World!');", "");
            cb.success("666");
            return true;
        }
        return false;
    }

    @Override
    public void cancelQuery(IBrowser b, long queryId) {

    }
}
