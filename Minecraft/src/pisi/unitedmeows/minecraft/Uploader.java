package pisi.unitedmeows.minecraft;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;

public class Uploader {
	public static String TOKEN = "Your token/api_key here";

	public static String uploadFile(String url, File binaryFile) {
		String charset = "UTF-8";
		String boundary = "------------------------" + Long.toHexString(System.currentTimeMillis());
		String CRLF = "\r\n";
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			connection.addRequestProperty("User-Agent", "CheckpaySrv/1.0.0");
			connection.addRequestProperty("Accept", "*/*");
			OutputStream output = connection.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
			writer.append("--").append(boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(binaryFile.getName()).append("\"").append(CRLF);
			writer.append("Content-Type: application/octet-stream").append(CRLF);
			writer.append(CRLF).flush();
			Files.copy(binaryFile.toPath(), output);
			output.flush();
			writer.append(CRLF).append("--").append(boundary).append("--").flush();
			int responseCode = ((HttpURLConnection) connection).getResponseCode();
			if (responseCode != 200) {
				return "Error";
			}
			InputStream Instream = connection.getInputStream();
			String finished = new String(IOUtils.toByteArray((InputStream) Instream));
			JsonObject jsonObject = new JsonParser().parse(finished).getAsJsonObject();
			String URL = jsonObject.get("url").getAsString();
			ChatComponentText ichatcomponent = new ChatComponentText("Uploaded to: " + URL);
			ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL));
			Minecraft.getMinecraft().thePlayer.addChatMessage(ichatcomponent);
			return URL;
		}
		catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}
}