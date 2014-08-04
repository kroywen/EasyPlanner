package com.thepegeekapps.easyplanner.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;

public class InputDialog extends DialogFragment implements OnClickListener {
	
	public interface OnInputClickListener {
		void onInputOkClick(String inputText);
		void onInputCancelClick();
	}
	
	private TextView titleView;
	private TextView textView;
	private EditText inputView;
	private Button okBtn;
	private Button cancelBtn;
	
	private String title;
	private String text;
	private String hint;
	private String okText;
	private String cancelText;
	private OnInputClickListener listener;
	
	private View rootView;
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setHint(String hint) {
		this.hint = hint;
	}
	
	public void setButtons(String okText, String cancelText, OnInputClickListener listener) {
		this.okText = okText;
		this.cancelText = cancelText;
		this.listener = listener;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		rootView = inflater.inflate(R.layout.input_dialog, null);
	    initializeViews(rootView);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setInverseBackgroundForced(true);
	    AlertDialog dialog = builder.create();
	    dialog.setView(rootView, 0, 0, 0, 0);
	    
	    return dialog;
	}
	
	private void initializeViews(View view) {
		titleView = (TextView) view.findViewById(R.id.titleView);
		titleView.setText(title);
		textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(text);
		inputView = (EditText) view.findViewById(R.id.inputView);
		inputView.setHint(hint);
		okBtn = (Button) view.findViewById(R.id.okBtn);
		okBtn.setText(okText);
		okBtn.setOnClickListener(this);
		cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
		cancelBtn.setText(cancelText);
		cancelBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okBtn:
			if (listener != null) {
				String inputText = inputView.getText().toString().trim();
				listener.onInputOkClick(inputText);
			}
			break;
		case R.id.cancelBtn:
			if (listener != null) {
				listener.onInputCancelClick();
			}
			break;
		}
		dismiss();
	}
	
	public void hideSoftKeyborad() {
		View view = rootView.findFocus();
		if (view == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}
