package com.thepegeekapps.easyplanner.screen;

import java.io.File;
import java.util.Arrays;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveDownloadOperationListener;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;
import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.OneDriveEntryAdapter;
import com.thepegeekapps.easyplanner.util.Utilities;

public class BrowseOneDriveScreen extends BaseScreen implements LiveAuthListener, OnItemClickListener {
	
	public static final String CLIENT_ID = "0000000040127E18";
	private static Iterable<String> scopes = Arrays.asList("wl.signin", "wl.basic", "wl.photos", "wl.skydrive");
	
	private static final String ROOT_FOLDER = "me/skydrive";
	
	private ListView list;
	private View progressView;
	
	private LiveAuthClient auth;
    private LiveConnectClient client;
    private String currentFolderId;
    private Stack<String> path;
    private OneDriveEntryAdapter adapter;
    private boolean cancelled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_one_drive_screen);
		initializeViews();
		
		path = new Stack<String>();
		auth = new LiveAuthClient(this, CLIENT_ID);
	}
	
	private void initializeViews() {
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);
		progressView = findViewById(R.id.progressView);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		auth.initialize(scopes, new LiveAuthListener() {
			@Override
			public void onAuthError(LiveAuthException exception, Object userState) {
				showToast(exception.getLocalizedMessage());
				finish();
			}
			@Override
			public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
				if (status == LiveStatus.UNKNOWN) {
					auth.login(BrowseOneDriveScreen.this, scopes, BrowseOneDriveScreen.this);
				} else if (status == LiveStatus.CONNECTED) {
					authSuccess(session);
				} else {
					authError(status);
				}
			}
		});
	}

	@Override
	public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
		if (status == LiveStatus.CONNECTED) {
            authSuccess(session);
        } else {
            authError(status);
        }  
	}
	
	private void authSuccess(LiveConnectSession session) {
		client = new LiveConnectClient(session);
		loadFolder(ROOT_FOLDER);
	}
	
	private void authError(LiveStatus status) {
		client = null;
        showToast("Login did not connect. Status is " + status + ".");
        finish();
	}

	@Override
	public void onAuthError(LiveAuthException exception, Object userState) {
		showToast(exception.getLocalizedMessage());        
        client = null;
        finish();
	}
	
	private void loadFolder(String folderId) {
		currentFolderId = folderId;
		client.getAsync(folderId + "/files", new LiveOperationListener() {
			@Override
			public void onError(LiveOperationException exception, LiveOperation operation) {
				progressView.setVisibility(View.INVISIBLE);
				showToast(exception.getLocalizedMessage());
			}
			@Override
			public void onComplete(LiveOperation operation) {
				progressView.setVisibility(View.INVISIBLE);
				
				JSONObject result = operation.getResult();
                if (result.has("error")) {
                    JSONObject error = result.optJSONObject("error");
                    String code = error.optString("code");
                    String message = error.optString("message");
                    showToast("Error " + code + ": " + message);
                    return;
                }
                
                JSONArray data = result.optJSONArray("data");
                updateList(data);
			}
		});
		progressView.setVisibility(View.VISIBLE);
	}
	
	public void updateList(JSONArray array) {
    	adapter = new OneDriveEntryAdapter(this, array);
    	list.setAdapter(adapter);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		JSONObject obj = adapter.getItem(position);
		String type = obj.optString("type");
		if (type.equals("folder") || type.equals("album")) {
			path.push(currentFolderId);
			loadFolder(obj.optString("id"));
		} else {
			downloadFile(obj);
		}
	}
	
	private void downloadFile(JSONObject obj) {
		final ProgressDialog dialog = new ProgressDialog(BrowseOneDriveScreen.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getString(R.string.downloading));
		dialog.setIndeterminate(true);
		
		String id = obj.optString("id");
        String filename = obj.optString("name");
        
        try {
        	File docDir = new File(Environment.getExternalStorageDirectory(), "/easyplanner/");
			docDir.mkdirs();
			
			final File file = new File(docDir, filename);
			
			final LiveDownloadOperation operation = client.downloadAsync(id + "/content", new LiveDownloadOperationListener() {
				@Override
				public void onDownloadProgress(int totalBytes, int bytesRemaining, LiveDownloadOperation operation) {
					long bytesDownloaded = totalBytes - bytesRemaining;
					String message = String.format(getString(R.string.download_pattern), 
						Utilities.getReadableFilesize(bytesDownloaded), Utilities.getReadableFilesize(totalBytes));
					dialog.setMessage(message);
				}
				@Override
				public void onDownloadFailed(LiveOperationException exception, LiveDownloadOperation operation) {
					dialog.dismiss();
					if (!cancelled) {
						onDownloadError(exception.getLocalizedMessage());
					}
				}
				@Override
				public void onDownloadCompleted(LiveDownloadOperation operation) {
					dialog.dismiss();
					onDownloadSuccess(file.getAbsolutePath());
				}
			});
			
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancelled = true;
					operation.cancel();
				}
			});
			dialog.show();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        
	}
	
	@Override
	public void onBackPressed() {
		if (!path.isEmpty()) {
			loadFolder(path.pop());
		} else {
            super.onBackPressed();
        }        
	}
	
	private void onDownloadSuccess(String filepath) {
		Intent result = new Intent();
        result.putExtra("filepath", filepath);
        setResult(RESULT_OK, result);
        finish();
	}
	
	private void onDownloadError(String error) {
		showToast(error);
		setResult(RESULT_CANCELED);
		finish();
	}

}
