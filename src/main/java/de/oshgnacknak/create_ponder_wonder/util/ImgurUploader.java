package de.oshgnacknak.create_ponder_wonder.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.oshgnacknak.create_ponder_wonder.BuildConfig;
import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class ImgurUploader {
	public static final String UPLOAD_API_URL = "https://api.imgur.com/3/upload";
	private static final ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

	// prevent instantiation
	private ImgurUploader() {
	}

	public static String upload(File file) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost uploadPost = new HttpPost(UPLOAD_API_URL);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("type", "file");
			builder.addTextBody("disable_audio", "1");

			uploadPost.setHeader("Authorization", "Client " + BuildConfig.IMGUR_CLIENTID);

			builder.addBinaryBody("video", new FileInputStream(file), ContentType.APPLICATION_OCTET_STREAM, file.getName());

			HttpEntity multipart = builder.build();
			uploadPost.setEntity(multipart);
			CloseableHttpResponse response = httpClient.execute(uploadPost);
			HttpEntity responseEntity = response.getEntity();
			String responseString = EntityUtils.toString(responseEntity);
			JsonObject jsonResp = new Gson().fromJson(responseString, JsonObject.class); // String to JSONObject
			return jsonResp.getAsJsonObject("data").get("id").getAsString();
		} catch (FileNotFoundException e) {
			CreatePonderWonder.LOGGER.error("File not found: {}", file.getAbsolutePath());
			return null;
		} catch (IOException e) {
			CreatePonderWonder.LOGGER.error("Error uploading to Imgur: {}", e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unused")
	public static void tryUpload(String filepath) {
		if (BuildConfig.IMGUR_CLIENTID == null) {
			return;
		}

		executor.execute(() -> {
			File file = new File(filepath);
			String imgurId = upload(file);
			if (imgurId != null) {
				CreatePonderWonder.LOGGER.info("Uploaded to Imgur: https://imgur.com/{}", imgurId);
				try (FileWriter fw = new FileWriter(file.getParentFile() + "/uploads.txt", true)) {
					fw.write(imgurId + "\n");
				} catch (IOException e) {
					CreatePonderWonder.LOGGER.error("Error writing to uploads.txt: {}", e.getMessage());
				}
			}
		});
	}
}
