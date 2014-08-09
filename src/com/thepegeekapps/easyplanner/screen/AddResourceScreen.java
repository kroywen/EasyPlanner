package com.thepegeekapps.easyplanner.screen;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.util.Utilities;

public class AddResourceScreen extends BaseScreen implements OnCheckedChangeListener, OnClickListener {
	
	public static final int CAPTURE_PHOTO_REQUEST_CODE = 0;
	public static final int SELECT_PHOTO_REQUEST_CODE = 1;
	public static final int GET_LINK_REQUEST_CODE = 2;
	public static final int BROWSE_DROPBOX_REQUEST_CODE = 3;
	public static final int BROWSE_GOOGLE_DRIVE_REQUEST_CODE = 4;
	public static final int BROWSE_ONE_DRIVE_REQUEST_CODE = 5;
	
	private View layoutDropbox;
	private View layoutGoogleDrive;
	private View layoutOneDrive;
	private View layoutLink;
	private View layoutGallery;
	private View layoutCamera;
	private RadioGroup mediaSourceGroup;
	private Button cancelBtn;
	private Button addBtn;
	private ImageView cameraImage;
	private TextView cameraText;
	private Button cameraBtn;
	private ImageView galleryImage;
	private TextView galleryText;
	private Button galleryBtn;
	private TextView linkText;
	private Button linkBtn;
	private TextView dropboxText;
	private Button dropboxBtn;
	private TextView googleDriveText;
	private Button googleDriveBtn;
	private TextView oneDriveText;
	private Button oneDriveBtn;
	
	private long classId;
	private long date;
	private String cameraFile;
	private String galleryFile;
	private String link;
	private String linkTitle;
	private String dropboxFile;
	private String googleDriveFile;
	private String oneDriveFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_resource_screen);
		getIntentData();
		initializeViews();
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			classId = intent.getLongExtra(ApiData.PARAM_CLASS_ID, 0);
			date = intent.getLongExtra(ApiData.PARAM_DATE, 0);
		}
	}
	
	private void initializeViews() {
		mediaSourceGroup = (RadioGroup) findViewById(R.id.mediaSourceGroup);
		mediaSourceGroup.setOnCheckedChangeListener(this);
		
		layoutDropbox = findViewById(R.id.layoutDropbox);
		layoutGoogleDrive = findViewById(R.id.layoutGoogleDrive);
		layoutOneDrive = findViewById(R.id.layoutOneDrive);
		layoutLink = findViewById(R.id.layoutLink);
		layoutGallery = findViewById(R.id.layoutGallery);
		layoutCamera = findViewById(R.id.layoutCamera);
		
		cameraImage = (ImageView) findViewById(R.id.cameraImage);
		cameraText = (TextView) findViewById(R.id.cameraText);
		cameraBtn = (Button) findViewById(R.id.cameraBtn);
		cameraBtn.setOnClickListener(this);
		
		galleryImage = (ImageView) findViewById(R.id.galleryImage);
		galleryText = (TextView) findViewById(R.id.galleryText);
		galleryBtn = (Button) findViewById(R.id.galleryBtn);
		galleryBtn.setOnClickListener(this);
		
		linkText = (TextView) findViewById(R.id.linkText);
		linkBtn = (Button) findViewById(R.id.linkBtn);
		linkBtn.setOnClickListener(this);
		
		dropboxText = (TextView) findViewById(R.id.dropboxText);
		dropboxBtn = (Button) findViewById(R.id.dropboxBtn);
		dropboxBtn.setOnClickListener(this);
		
		googleDriveText = (TextView) findViewById(R.id.googleDriveText);
		googleDriveBtn = (Button) findViewById(R.id.googleDriveBtn);
		googleDriveBtn.setOnClickListener(this);
		
		oneDriveText = (TextView) findViewById(R.id.oneDriveText);
		oneDriveBtn = (Button) findViewById(R.id.oneDriveBtn);
		oneDriveBtn.setOnClickListener(this);
		
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(this);
		
		addBtn = (Button) findViewById(R.id.addBtn);
		addBtn.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.sourceDropbox:
			setMediaLayout(layoutDropbox);
			break;
		case R.id.sourceGoogleDrive:
			setMediaLayout(layoutGoogleDrive);
			break;
		case R.id.sourceLink:
			setMediaLayout(layoutLink);
			break;
		case R.id.sourceGallery:
			setMediaLayout(layoutGallery);
			break;
		case R.id.sourceCamera:
			setMediaLayout(layoutCamera);
			break;
		case R.id.sourceOneDrive:
			setMediaLayout(layoutOneDrive);
		}
	}
	
	private void setMediaLayout(View layout) {
		layoutDropbox.setVisibility(layout == layoutDropbox ? View.VISIBLE : View.INVISIBLE);
		layoutGoogleDrive.setVisibility(layout == layoutGoogleDrive ? View.VISIBLE : View.INVISIBLE);
		layoutOneDrive.setVisibility(layout == layoutOneDrive ? View.VISIBLE : View.INVISIBLE);
		layoutLink.setVisibility(layout == layoutLink ? View.VISIBLE : View.INVISIBLE);
		layoutGallery.setVisibility(layout == layoutGallery ? View.VISIBLE : View.INVISIBLE);
		layoutCamera.setVisibility(layout == layoutCamera ? View.VISIBLE : View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancelBtn:
			cancel();
			break;
		case R.id.addBtn:
			addAll();
			break;
		case R.id.cameraBtn:
			dispatchTakePictureIntent();
			break;
		case R.id.galleryBtn:
			Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, SELECT_PHOTO_REQUEST_CODE);
			break;
		case R.id.linkBtn:
			intent = new Intent(this, GetLinkScreen.class);
			startActivityForResult(intent, GET_LINK_REQUEST_CODE);
			break;
		case R.id.dropboxBtn:
			intent = new Intent(this, BrowseDropboxScreen.class);
			startActivityForResult(intent, BROWSE_DROPBOX_REQUEST_CODE);
			break;
		case R.id.googleDriveBtn:
			intent = new Intent(this, BrowseGoogleDriveScreen.class);
			startActivityForResult(intent, BROWSE_GOOGLE_DRIVE_REQUEST_CODE);
			break;
		case R.id.oneDriveBtn:
			intent = new Intent(this, BrowseOneDriveScreen.class);
			startActivityForResult(intent, BROWSE_ONE_DRIVE_REQUEST_CODE);
			break;
		}
	}
	
	private void dispatchTakePictureIntent() {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
		    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if (intent.resolveActivity(getPackageManager()) != null) {
		    	File photoFile = null;
		        try {
		            photoFile = createImageFile();
		        } catch (IOException e) {
		        	e.printStackTrace();
		        }
		        if (photoFile != null) {
		        	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		            startActivityForResult(intent, CAPTURE_PHOTO_REQUEST_CODE);
		        }
		    }
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    File image = new File(storageDir, imageFileName + ".jpg");
	    cameraFile = image.getAbsolutePath();
	    return image;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CAPTURE_PHOTO_REQUEST_CODE:
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.outWidth = options.outHeight = cameraImage.getWidth();
				Bitmap bitmap = BitmapFactory.decodeFile(cameraFile, options);
				cameraImage.setImageBitmap(bitmap);
				cameraText.setText(R.string.photo);
				cameraText.setTextColor(0xff000000);
				break;
			case SELECT_PHOTO_REQUEST_CODE:
				galleryFile = getPhotoPath(data.getData());
				options = new BitmapFactory.Options();
				options.outWidth = options.outHeight = galleryImage.getWidth();
				bitmap = BitmapFactory.decodeFile(galleryFile, options);
				galleryImage.setImageBitmap(bitmap);
				galleryText.setText(R.string.photo);
				galleryText.setTextColor(0xff000000);
				break;
			case GET_LINK_REQUEST_CODE:
				link = data.getDataString();
				linkTitle = data.getStringExtra("title");
				linkText.setText(linkTitle);
				linkText.setTextColor(0xff000000);
				break;
			case BROWSE_DROPBOX_REQUEST_CODE:
				dropboxFile = data.getStringExtra("filepath");
				dropboxText.setText(Utilities.extractFilename(dropboxFile));
				dropboxText.setTextColor(0xff000000);
				break;
			case BROWSE_GOOGLE_DRIVE_REQUEST_CODE:
				googleDriveFile = data.getStringExtra("filepath");
				googleDriveText.setText(Utilities.extractFilename(googleDriveFile));
				googleDriveText.setTextColor(0xff000000);
				break;
			case BROWSE_ONE_DRIVE_REQUEST_CODE:
				oneDriveFile = data.getStringExtra("filepath");
				oneDriveText.setText(Utilities.extractFilename(oneDriveFile));
				oneDriveText.setTextColor(0xff000000);
				break;
			}
		}
	}
	
	private String getPhotoPath(Uri uri) {
		String[] projection = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, projection, null, null, null);
        c.moveToFirst();
        String path = c.getString(c.getColumnIndex(projection[0]));
        c.close();
        return path;
	}
	
	private void cancel() {
		deleteDownloadedFile(dropboxFile);
		deleteDownloadedFile(googleDriveFile);
		deleteDownloadedFile(oneDriveFile);
		deleteDownloadedFile(galleryFile);
		deleteDownloadedFile(cameraFile);
		
		setResult(RESULT_CANCELED);
		finish();
	}
	
	private void deleteDownloadedFile(String path) {
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}
	
	private void addAll() {
		showProgressDialog(R.string.adding);
		
		if (!TextUtils.isEmpty(cameraFile)) {
			new UploadResourceTask(cameraFile).execute();
			cameraFile = null;
			return;
		}
		
		if (!TextUtils.isEmpty(galleryFile)) {
			new UploadResourceTask(galleryFile).execute();
			galleryFile = null;
			return;
		}
		
		if (!TextUtils.isEmpty(dropboxFile)) {
			new UploadResourceTask(dropboxFile).execute();
			dropboxFile = null;
			return;
		}
			
		if (!TextUtils.isEmpty(googleDriveFile)) {
			new UploadResourceTask(googleDriveFile).execute();
			googleDriveFile = null;
			return;
		}
		
		if (!TextUtils.isEmpty(oneDriveFile)) {
			new UploadResourceTask(oneDriveFile).execute();
			oneDriveFile = null;
			return;
		}
		
		if (!TextUtils.isEmpty(link)) {
			requestCreateLink();
			return;
		}
		
		hideProgressDialog();
		setResult(RESULT_OK);
		finish();
	}
	
	private void requestCreateLink() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.RESOURCE));
		intent.setAction(ApiData.POST);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_TITLE, linkTitle);
		intent.putExtra(ApiData.PARAM_URL, link);
		intent.putExtra(ApiData.PARAM_DATE, Utilities.parseTime(date, Utilities.dd_MM_yyyy));
		startService(intent);
		
		link = null;
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			int status = apiResponse.getStatus();
			if (status == ApiResponse.STATUS_SUCCESS) {
				if (ApiData.RESOURCE.equalsIgnoreCase(apiResponse.getRequestName())) {
					addAll();
				}
			} else {
				hideProgressDialog();
				showInfoDialog(getString(R.string.error), apiResponse.getError());
			}
		} else {
			hideProgressDialog();
			showInfoDialog(getString(R.string.error), apiResponse.getError());
		}
	}
	
	class UploadResourceTask extends AsyncTask<Void, Void, Void> {
		
		private String filename;
		
		public UploadResourceTask(String filename) {
			this.filename= filename; 
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(ApiData.BASE_URL + ApiData.UPLOAD);
			
			ContentBody tokenBody = new StringBody(settings.getString(ApiData.TOKEN), ContentType.MULTIPART_FORM_DATA);
			ContentBody classIdBody = new StringBody(String.valueOf(classId), ContentType.MULTIPART_FORM_DATA);
			ContentBody dateBody = new StringBody(Utilities.parseTime(date, Utilities.dd_MM_yyyy), ContentType.MULTIPART_FORM_DATA);
			File file = new File(filename);
			ContentBody fileBody = new FileBody(file);
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
				.addPart(ApiData.TOKEN, tokenBody)
				.addPart(ApiData.PARAM_CLASS_ID, classIdBody)
				.addPart(ApiData.PARAM_DATE, dateBody)
				.addPart(ApiData.PARAM_FILE, fileBody);
			
			HttpEntity entity = builder.build();
			post.setEntity(entity);
			
			try {
				client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
			}
			client.getConnectionManager().shutdown();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			addAll();
		}
		
	}

}
