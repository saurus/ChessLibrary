package org.saurus.net.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpGet {
	public static Reader get(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setUseCaches(true);
			urlConnection.addRequestProperty("Cache-Control", "max-stale=" + 120);
			urlConnection.getLastModified();
			
			//urlConnection.setIfModifiedSince(ifmodifiedsince);
			return new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	}
}
