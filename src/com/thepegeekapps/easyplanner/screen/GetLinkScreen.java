package com.thepegeekapps.easyplanner.screen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.thepegeekapps.easyplanner.R;

public class GetLinkScreen extends BaseScreen implements OnClickListener {
	
	private Button closeBtn;
	private EditText urlView;
	private Button saveBtn;
	private WebView webView;
	private View progressView;
	
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_link_screen);
		initializeViews();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initializeViews() {
		closeBtn = (Button) findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(this);
		
		urlView = (EditText) findViewById(R.id.urlView);
		urlView.setSelection(urlView.getText().toString().length());
		urlView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE ||
					event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) 
				{
					hideSoftKeyborad();
		            url = v.getText().toString();
		            if (!TextUtils.isEmpty(url)) {
		            	if (!(url.startsWith("http://") || url.startsWith("https://"))) {
		            		url = "http://" + url;
		            	}
		            	webView.loadUrl(url);
		            }
		            return true;
		        }
				return false;
			}
		});
		
		saveBtn = (Button) findViewById(R.id.saveBtn);
		saveBtn.setOnClickListener(this);
		
		webView = (WebView) findViewById(R.id.webView);
		WebSettings settings = webView.getSettings();
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		settings.setJavaScriptEnabled(true);
		settings.setPluginState(PluginState.ON);
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				saveBtn.setEnabled(false);
				progressView.setVisibility(View.VISIBLE);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				saveBtn.setEnabled(true);
				progressView.setVisibility(View.INVISIBLE);
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		
		progressView = findViewById(R.id.progressView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.closeBtn:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.saveBtn:
			Intent result = new Intent();
			result.setData(Uri.parse(url));
			result.putExtra("title", webView.getTitle());
			setResult(RESULT_OK, result);
			finish();
			break;
		}
	}

}
