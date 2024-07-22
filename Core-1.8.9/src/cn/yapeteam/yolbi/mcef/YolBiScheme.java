package cn.yapeteam.yolbi.mcef;

import cn.yapeteam.loader.ResourceManager;
import cn.yapeteam.loader.logger.Logger;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.api.IScheme;
import net.montoyo.mcef.api.ISchemeResponseData;
import net.montoyo.mcef.api.ISchemeResponseHeaders;
import net.montoyo.mcef.api.SchemePreResponse;
import net.montoyo.mcef.utilities.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class YolBiScheme implements IScheme {
    private String contentType = null;
    private InputStream is = null;

    @Override
    public SchemePreResponse processRequest(String url) {
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Logger.exception(e);
            return SchemePreResponse.NOT_HANDLED;
        }
        // yolbi://clickui/#
        String head = "yolbi://";
        if (url.isEmpty())
            return SchemePreResponse.NOT_HANDLED;
        if (url.length() < head.length())
            return SchemePreResponse.NOT_HANDLED;
        if (!Character.isLetter(url.charAt(url.length() - 1))) {
            int pos = url.lastIndexOf('/');
            if (pos != -1)
                url = url.substring(0, pos);
        }
        url = url.substring(head.length());
        String loc = "web/" + url;
        if (!ResourceManager.resources.isFile(loc))
            loc += "/index.html";
        System.out.println(loc);
        is = ResourceManager.resources.getStream(loc);
        if (is == null) {
            Log.warning("Resource " + url + " NOT found!");
            return SchemePreResponse.NOT_HANDLED;
        }
        contentType = null;
        int pos = url.lastIndexOf('.');
        if (pos != -1)
            contentType = MCEF.PROXY.mimeTypeFromExtension(url.substring(pos + 1));
        return SchemePreResponse.HANDLED_CONTINUE;
    }

    @Override
    public void getResponseHeaders(ISchemeResponseHeaders rep) {
        if (contentType != null)
            rep.setMimeType(contentType);

        rep.setStatus(200);
        rep.setStatusText("OK");
        rep.setResponseLength(-1);
    }

    @Override
    public boolean readResponse(ISchemeResponseData data) {
        try {
            int ret = is.read(data.getDataArray(), 0, data.getBytesToRead());
            if (ret <= 0)
                is.close();

            data.setAmountRead(Math.max(ret, 0));
            return ret > 0;
        } catch (IOException e) {
            Logger.exception(e);
            return false;
        }
    }
}
