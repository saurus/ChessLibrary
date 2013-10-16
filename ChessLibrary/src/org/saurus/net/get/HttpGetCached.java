package org.saurus.net.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class HttpGetCached<T> {
	private URL url;
	private String lastModified;
	private T data;
	private boolean hasChanged;

	public HttpGetCached(String urlString) throws MalformedURLException {
		this.url = new URL(urlString);
	}

	public HttpGetCached(URL url, String lastModified, T data) {
		this.url = url;
		this.lastModified = lastModified;
		this.data = data;
		this.hasChanged = true;
	}

	public HttpGetCached(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

	public String getLastModified() {
		return lastModified;
	}

	public T getData() {
		return data;
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	public HttpGetCached<T> get() {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setUseCaches(true);
			if (lastModified != null) {
				// urlConnection.addRequestProperty("Cache-Control",
				// "max-stale=" + 120);
				urlConnection.addRequestProperty("If-Modified-Since", lastModified);
			}

			urlConnection.connect();

			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				Reader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String newLastModified = urlConnection.getHeaderField("Last-Modified");
				HttpGetCached<T> newcachedData = convert(reader, newLastModified);

				log("HTTP_OK: previous LastModified: " + lastModified + ", current: " + newLastModified + ", new data "
						+ ((newcachedData == null) ? "NOT FOUND" : "found"));
				return newcachedData;
			} else if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
				log("HTTP_NOT_MODIFIED: previous LastModified: " + lastModified);
				this.hasChanged = false;
				return this;
			}
			return null;
		} catch (IOException e) {
			log("got IOException...");
			return null;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	}

	protected abstract void log(String string);

	protected abstract HttpGetCached<T> convert(Reader reader, String lastModified);
}
