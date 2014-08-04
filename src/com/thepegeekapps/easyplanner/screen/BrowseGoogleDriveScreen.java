package com.thepegeekapps.easyplanner.screen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResource.MetadataResult;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.util.Utilities;

public class BrowseGoogleDriveScreen extends BaseScreen implements ConnectionCallbacks, OnConnectionFailedListener {
	
	public static final int RESOLVE_CONNECTION_REQUEST_CODE = 0;
	public static final int OPEN_FILE_REQUEST_CODE = 1;
	
	private View progressView;
	
	private GoogleApiClient googleApiClient;
	private boolean resolutionStarted;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_google_drive_screen);
		initializeViews();
		
		googleApiClient = new GoogleApiClient.Builder(this)
	        .addApi(Drive.API)
	        .addScope(Drive.SCOPE_FILE)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .build();
	}
	
	private void initializeViews() {
		progressView = findViewById(R.id.progressView);
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
    	googleApiClient.connect();
    	progressView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		progressView.setVisibility(View.INVISIBLE);
		if (!resolutionStarted) {
		    if (connectionResult.hasResolution()) {
		        try {
		            connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
		            resolutionStarted = true;
		        } catch (IntentSender.SendIntentException e) {
		            e.printStackTrace();
		            showToast(e.getLocalizedMessage());
		        }
		    } else {
		        GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
		    }
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		progressView.setVisibility(View.INVISIBLE);
		
		IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder().build(googleApiClient);
		try {
			startIntentSenderForResult(intentSender, OPEN_FILE_REQUEST_CODE, null, 0, 0, 0);
		} catch (SendIntentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {}
	
	@Override
	protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
	    switch (requestCode) {
        case RESOLVE_CONNECTION_REQUEST_CODE:
            if (resultCode == RESULT_OK) {
                googleApiClient.connect();
                progressView.setVisibility(View.VISIBLE);
            }
            break;
        case OPEN_FILE_REQUEST_CODE:
        	if (resultCode == RESULT_OK) {
        		DriveId driveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
        		downloadFile(driveId);
        	} else {
        		finish();
        	}
        	break;
	    }
	}
	
	private void downloadFile(DriveId driveId) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getString(R.string.downloading));
		dialog.setIndeterminate(true);
		dialog.show();
		
		final DriveFile driveFile = Drive.DriveApi.getFile(googleApiClient, driveId);
		driveFile.getMetadata(googleApiClient).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
			@Override
			public void onResult(MetadataResult result) {
				final String filename = result.getMetadata().getTitle();
				driveFile.openContents(googleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(new ResultCallback<DriveApi.ContentsResult>() {
					@Override
					public void onResult(DriveApi.ContentsResult contentsResult) {
						Contents contents = contentsResult.getContents();
						
						if (contents == null) {
							onDownloadError(getString(R.string.download_error));
						} else {
					        InputStream is = contents.getInputStream();
					        
					        File file = null;
					        try {
					        	File docDir = new File(Environment.getExternalStorageDirectory(), "/easyplanner/");
								docDir.mkdirs();
								
								file = new File(docDir, filename);
								FileOutputStream fos = new FileOutputStream(file);
								Utilities.copyStream(is, fos);
					        } catch (IOException e) {
					        	e.printStackTrace();
					        }
					        
					        driveFile.discardContents(googleApiClient, contents);
					        dialog.hide();
					        
					        if (file != null && file.exists()) {
						        onDownloadSuccess(file.getAbsolutePath());
					        } else {
					        	onDownloadError(getString(R.string.download_error));
					        }
						}
					}
				});
			}
		});		
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
