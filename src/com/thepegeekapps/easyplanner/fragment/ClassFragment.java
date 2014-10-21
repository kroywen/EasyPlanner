package com.thepegeekapps.easyplanner.fragment;

import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiResponseReceiver;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.api.OnApiResponseListener;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.parser.ApiParser;
import com.thepegeekapps.easyplanner.parser.ParserFactory;
import com.thepegeekapps.easyplanner.screen.BaseScreen;
import com.thepegeekapps.easyplanner.screen.ClassScreen;
import com.thepegeekapps.easyplanner.storage.Settings;

public class ClassFragment extends Fragment implements OnApiResponseListener {
	
	protected ApiResponseReceiver responseReceiver;
	protected long classId;
	protected Settings settings;
	protected RequestDataTask dataTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		classId = (getArguments() != null) ? getArguments().getLong(Clas.CLASS_ID) : 0;
		settings = new Settings(getActivity());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter(ApiService.ACTION_API_RESULT);
		responseReceiver = new ApiResponseReceiver(this);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
			responseReceiver, intentFilter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
	}

	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		// TODO Auto-generated method stub
		
	}
	
	protected void showProgressDialog(int messageResId) {
		((BaseScreen) getActivity()).showProgressDialog(messageResId);
	}
	
	protected void hideProgressDialog() {
		BaseScreen screen = (BaseScreen) getActivity();
		if (screen != null) {
			screen.hideProgressDialog();
		}
	}
	
	protected long getTime() {
		ClassScreen screen = (ClassScreen) getActivity();
		return screen != null ? screen.getTime() : 0; 
	}
	
	protected void setTime(long time) {
		ClassScreen screen = (ClassScreen) getActivity();
		if (screen != null) {
			screen.setTime(time);
		}
	}
	
	
	
	class RequestDataTask extends AsyncTask<Void, Void, Void> {
		
		private String command;
		private String method;
		private Bundle extras;
		
		public RequestDataTask(Intent intent) {
			command = intent.getData().toString();
			method = intent.getAction();
			extras = intent.getExtras();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			AndroidHttpClient client = AndroidHttpClient.newInstance(
				System.getProperty("http.agent"), ClassFragment.this.getActivity());
			HttpGet request = new HttpGet();
				
			request.addHeader("Accept", "application/json");
			request.addHeader("Content-Type", "application/json");
				
			String url = createURL(command, extras);
			request.setURI(URI.create(url));
				
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
						sendResult(ApiService.API_STATUS_SUCCESS, apiResponse);
					}
					
					is.close();
				} else {
					ApiResponse apiResponse = new ApiResponse();
					apiResponse.setMethod(method);
					apiResponse.setRequestName(command);
					sendResult(ApiService.API_STATUS_SUCCESS, apiResponse);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setError(e.getLocalizedMessage());
				sendResult(ApiService.API_STATUS_ERROR, apiResponse);
			} finally {
				client.close();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dataTask = null;
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
		
	}
	
	protected void sendResult(int apiStatus, ApiResponse apiResponse) {
		Intent resultIntent = new Intent(ApiService.ACTION_API_RESULT);
		resultIntent.putExtra(ApiService.EXTRA_API_STATUS, apiStatus);
		resultIntent.putExtra(ApiService.EXTRA_API_RESPONSE, apiResponse);
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(resultIntent);
	}

}
