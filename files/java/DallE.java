package com.rickystudios.oneai;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

public class DallE {
	
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final String apiKey = "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";
	private static final String baseUrl = "https://api.openai.com/v1/images/generations";
	
	private OnImageGeneratedListener onImageGeneratedListener;
	private Context context;
	
	public DallE(Context cxt){
		this.context = cxt;
	}
	
	public void setOnImageGeneratedListener(OnImageGeneratedListener onImageGeneratedListener) {
		this.onImageGeneratedListener = onImageGeneratedListener;
	}
	
	public interface OnImageGeneratedListener {
		void onImageGenerated(List<String> imageUrls);
		void onError();
	}
	
	public void generateImage(String textPrompt) {
		new AsyncTask<String, Void, List<String>>() {
			@Override
			protected List<String> doInBackground(String... strings) {
				JSONObject requestJson = new JSONObject();
				try {
					requestJson.put("prompt", strings[0]);
					requestJson.put("size", "512x512");
					requestJson.put("num_images", 4);
					requestJson.put("model", "image-alpha-001");
					
					
					OkHttpClient client = new OkHttpClient();
					
					Request request = new Request.Builder()
					.url(baseUrl)
					.addHeader("Content-Type", "application/json")
					.addHeader("Authorization", "Bearer " + apiKey)
					.post(RequestBody.create(JSON, requestJson.toString()))
					.build();
					
					Response response = client.newCall(request).execute();
					JSONObject jsonObject = new JSONObject(response.body().string());
					JSONArray jsonArray = jsonObject.getJSONArray("data");
					List<String> imageUrls = new ArrayList<>();
					for (int i = 0; i < jsonArray.length(); i++) {
						imageUrls.add(jsonArray.getJSONObject(i).getString("url"));
					}
					return imageUrls;
					
				}catch(Exception e){
					return null;
				}
				
			}
			
			@Override
			protected void onPostExecute(List<String> imageUrls) {
				if (imageUrls != null) {
					if (onImageGeneratedListener != null) {
						
						onImageGeneratedListener.onImageGenerated(imageUrls);
					}
				} else {
					if (onImageGeneratedListener != null) {
						onImageGeneratedListener.onError();
					}
				}
			}
		}.execute(textPrompt);
	}
	
	
	
	
}
