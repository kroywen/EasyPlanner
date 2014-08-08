package com.thepegeekapps.easyplanner.screen;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.DropboxEntryAdapter;
import com.thepegeekapps.easyplanner.util.Utilities;

public class BrowseDropboxScreen extends BaseScreen implements OnItemClickListener {
	
	public static final String APP_KEY = "6gvxz6b3fzsvpqt";
	public static final String APP_SECRET = "195fqlmzssn3ceu";
	
	final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
	
	private ListView list;
	private View progressView;
	
	private DropboxAPI<AndroidAuthSession> dbApi;
	private String currentPath = "/";
	private boolean afterLogin;
	private DropboxEntryAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_dropbox_screen);
		initializeViews();
		
		AndroidAuthSession session = buildSession();
		dbApi = new DropboxAPI<AndroidAuthSession>(session);
        
        if (!dbApi.getSession().isLinked()) {
        	dbApi.getSession().startOAuth2Authentication(this);
        	afterLogin = true;
        } else {
        	new ListCurrentDirTask().execute();
        }
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = dbApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                storeAuth(session);
                if (afterLogin) {
                	new ListCurrentDirTask().execute();
                }
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox: " + e.getLocalizedMessage());
            }
        }
    }
	
	private void initializeViews() {
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);
		progressView = findViewById(R.id.progressView);
	}

    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;
        session.setOAuth2AccessToken(secret);
    }

    private void storeAuth(AndroidAuthSession session) {
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }
    
    public void updateList(List<Entry> contents) {
    	adapter = new DropboxEntryAdapter(this, contents);
    	list.setAdapter(adapter);
    }
    
    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Entry entry = adapter.getItem(position);
		if (entry.isDir) {
			currentPath = currentPath + '/' + entry.fileName();
			new ListCurrentDirTask().execute();
		} else {
			new DownloadEntryTask(entry).execute();
		}
	}
    
    private void onDownloadSuccess(String filepath) {
    	Intent result = new Intent();
    	result.putExtra("filepath", filepath);
    	setResult(RESULT_OK, result);
    	finish();
    }
    
    private void onDownloadError() {
    	showToast(getString(R.string.download_error));
    	setResult(RESULT_CANCELED);
    	finish();
    }
    
    @Override
	public void onBackPressed() {
		if (!currentPath.equals("/")) {
			int end = currentPath.lastIndexOf('/');
			currentPath = currentPath.substring(0, end);
			new ListCurrentDirTask().execute();
		} else {
			super.onBackPressed();
		}
	}
    
    class ListCurrentDirTask extends AsyncTask<Void, Void, Void> {
    	
    	private Entry entry;
    	
    	@Override
    	protected void onPreExecute() {
    		progressView.setVisibility(View.VISIBLE);
    	}
    	
    	@Override
    	protected Void doInBackground(Void... params) {
    		try {
    			entry = dbApi.metadata(currentPath, 0, null, true, null);
    		} catch (DropboxException e) {
    			e.printStackTrace();
    			entry = null;
    		}
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		progressView.setVisibility(View.INVISIBLE);
    		if (entry != null) {
    			updateList(entry.contents);
    		}
    	}
    	
    }
    
    class DownloadEntryTask extends AsyncTask<Void, Long, Boolean> implements OnCancelListener {
    	
    	private ProgressDialog dialog;
    	private Entry entry;
    	private String filepath;
    	
    	public DownloadEntryTask(Entry entry) {
    		this.entry = entry;
    		dialog = new ProgressDialog(BrowseDropboxScreen.this);
    		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		dialog.setMessage(getString(R.string.downloading));
    		dialog.setIndeterminate(true);
    		dialog.setOnCancelListener(this);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		dialog.show();
    	}
    	
    	@Override
    	protected Boolean doInBackground(Void... params) {
    		try {
				File docDir = new File(Environment.getExternalStorageDirectory(), "/easyplanner/");
				docDir.mkdirs();
				
				File file = new File(docDir, entry.fileName());
				FileOutputStream fos = new FileOutputStream(file);
				dbApi.getFile(entry.path, null, fos, new ProgressListener() {
					@Override
					public void onProgress(long bytes, long total) {
						publishProgress(bytes, total);
					}
				});
				fos.close();
				
				filepath = file.getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
    	}
    	
    	@Override
		protected void onProgressUpdate(Long... values) {
			String message = String.format(getString(R.string.download_pattern), 
				Utilities.getReadableFilesize(values[0]), Utilities.getReadableFilesize(values[1]));
			dialog.setMessage(message);
		}
		
		@Override
		public void onPostExecute(Boolean result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			if (!result.booleanValue()) {
				onDownloadError();
			} else {
				onDownloadSuccess(filepath);
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			cancel(true);
		}
    	
    }

}
