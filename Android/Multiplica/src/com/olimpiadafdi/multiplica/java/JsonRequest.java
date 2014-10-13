package com.olimpiadafdi.multiplica.java;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class JsonRequest {
	private String uri;
	private DBHandler db;
	private Runnable updateDataSuccess;
	private Runnable updateDataError;

	/**
	 * Constructor
	 * 
	 * @param uri
	 *            The uri to be requested
	 */
	public JsonRequest(String uri, Context context, Runnable updateDataSuccess,
			Runnable updateDataError) {
		this.uri = uri;
		this.db = new DBHandler(context);
		this.updateDataSuccess = updateDataSuccess;
		this.updateDataError = updateDataError;
	}

	public void request() {
		new DownloadDataTask().execute(this.uri);
	}

	private class DownloadDataTask extends AsyncTask<String, Integer, Boolean> {

		// JSON-Parser
		@Override
		protected Boolean doInBackground(String... uri) {

			String html = loadData(uri[0]);
			if (html == null) {
				return false;
			}

			ArrayList<Item> items = parseHtml(html);
			if (items == null) {
				return false;
			}

			for (int i = 0; i < items.size(); i++) {
				Log.d("test", i + ": " + items.get(i).getUrl());
			}

			db.clearItems();
			db.writeItems(items);
			return true;
		}

		private ArrayList<Item> parseHtml(String html) {
			ArrayList<Item> postings = new ArrayList<Item>();
			try {
				JSONObject twitterObject = new JSONObject(html);
				JSONArray twitterPostingsArray = twitterObject
						.getJSONArray("RelatedTopics");

				String url;
				String text;

				for (int i = 0; i < twitterPostingsArray.length(); i++) {
					Item item = new Item();
					url = twitterPostingsArray.getJSONObject(i)
							.getString("FirstURL").toString();
					text = twitterPostingsArray.getJSONObject(i)
							.getString("Text").toString();

					item.setUrl(url);
					item.setText(text);

					postings.add(item);
				}
				return postings;
			} catch (JSONException e) {
				Log.e("test", "There was an error parsing the JSON", e);
			}
			return null;
		}

		private String loadData(String url) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;

			try {
				response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();

				if (entity != null) {

					InputStream instream = entity.getContent();

					BufferedInputStream bis = new BufferedInputStream(instream);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);

					int current = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
					}
					// publishProgress(1);

					// Convert the Bytes read to a String.
					String html = new String(baf.toByteArray());
					return html;
				}
			} catch (ClientProtocolException e) {
				Log.e("test", "There was a protocol based error", e);
			} catch (IOException e) {
				Log.e("test", "There was an IO Stream related error", e);
			}
			publishProgress();
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			// progress[0]
		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				updateDataSuccess.run();
			} else {
				updateDataError.run();
			}
		}

	}
	
}
