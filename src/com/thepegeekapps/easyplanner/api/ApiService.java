package com.thepegeekapps.easyplanner.api;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.thepegeekapps.easyplanner.parser.ApiParser;
import com.thepegeekapps.easyplanner.parser.ParserFactory;
import com.thepegeekapps.easyplanner.util.Utilities;

public class ApiService extends IntentService {
	
	public static final String TAG = ApiService.class.getSimpleName();
	
	public static final String ACTION_API_RESULT = "action_api_result";
	public static final String EXTRA_API_STATUS = "extra_api_status";
	public static final String EXTRA_API_RESPONSE = "extra_api_response";
	
	public static final int API_STATUS_NONE = -1;
	public static final int API_STATUS_SUCCESS = 0;
	public static final int API_STATUS_ERROR = 1;
	
	public ApiService() {
		this(ApiService.class.getSimpleName());
	}

	public ApiService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String command = intent.getData().toString();
		String method = intent.getAction();
		
		AndroidHttpClient client = AndroidHttpClient.newInstance(
			System.getProperty("http.agent"), this);
		HttpRequestBase request = getHttpRequest(method);
		
		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/json");
		
		if (ApiData.POST.equalsIgnoreCase(method) || ApiData.PUT.equalsIgnoreCase(method)) {
			HttpEntityEnclosingRequestBase r = (HttpEntityEnclosingRequestBase) request;
			r.setURI(URI.create(ApiData.BASE_URL + command));
			try {
				List<NameValuePair> params = buildParams(extras);
				if (!Utilities.isEmpty(params)) {
					r.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			String url = createURL(command, extras);
			request.setURI(URI.create(url));
		}
		
		Log.d(TAG, request.getMethod() + ": " + request.getURI().toString());
		
		HttpResponse response = null;
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				ApiParser parser = ParserFactory.getParser(command, method);
				if (parser != null) {
					parser.parse(is);
					ApiResponse apiResponse = parser.getApiResponse();
					apiResponse.setMethod(method);
					apiResponse.setRequestName(command);
					sendResult(API_STATUS_SUCCESS, apiResponse);
				}
				
				is.close();
			} else {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setMethod(method);
				apiResponse.setRequestName(command);
				sendResult(API_STATUS_SUCCESS, apiResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ApiResponse apiResponse = new ApiResponse();
			apiResponse.setError(e.getLocalizedMessage());
			sendResult(API_STATUS_ERROR, apiResponse);
		} finally {
			client.close();
		}
	}
	
	private HttpRequestBase getHttpRequest(String method) {
		if (method.equalsIgnoreCase(ApiData.GET)) {
			return new HttpGet();
		} else if (method.equalsIgnoreCase(ApiData.POST)) {
			return new HttpPost();
		} else if (method.equalsIgnoreCase(ApiData.PUT)) {
			return new HttpPut();
		} else if (method.equalsIgnoreCase(ApiData.DELETE)) {
			return new HttpDelete();
		} else {
			return null;
		}
	}
	
	private void sendResult(int apiStatus, ApiResponse apiResponse) {
		Intent resultIntent = new Intent(ACTION_API_RESULT);
		resultIntent.putExtra(EXTRA_API_STATUS, apiStatus);
		resultIntent.putExtra(EXTRA_API_RESPONSE, apiResponse);
		LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
	}
	
	private String createURL(String command, Bundle params) {
		Uri.Builder uriBuilder = Uri.parse(ApiData.BASE_URL + command).buildUpon();
		if (params != null) {
			Set<String> keys = params.keySet();
			if (keys != null && !keys.isEmpty()) {
				Iterator<String> i = keys.iterator();
				while (i.hasNext()) {
					String key = i.next();
					String value = String.valueOf(params.get(key));
					uriBuilder.appendQueryParameter(key, value);
				}
			}
		}
		String result = uriBuilder.build().toString();
		return result;
	}
	
	private List<NameValuePair> buildParams(Bundle extras) {
		if (extras == null || extras.isEmpty()) {
			return null;
		}
		
		Set<String> keys = extras.keySet();
		if (Utilities.isEmpty(keys)) {
			return null;
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String key : keys) {
			params.add(new BasicNameValuePair(key, String.valueOf(extras.get(key))));
		}
		return params;
	}

}
