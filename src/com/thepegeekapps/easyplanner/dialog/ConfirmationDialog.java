package com.thepegeekapps.easyplanner.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;

public class ConfirmationDialog extends DialogFragment {
	
	private TextView titleView;
	private TextView textView;
	private Button okBtn;
	private Button cancelBtn;
	
	private String title;
	private String text;
	private OnClickListener okListener;
	private String okText;
	private OnClickListener cancelListener;
	private String cancelText;
	
	private void initializeViews(View view) {
		titleView = (TextView) view.findViewById(R.id.titleView);
		titleView.setText(title);
		textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(text);
		okBtn = (Button) view.findViewById(R.id.okBtn);
		okBtn.setText(okText);
		okBtn.setOnClickListener(okListener);
		cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
		cancelBtn.setText(cancelText);
		cancelBtn.setOnClickListener(cancelListener);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setOkListener(String okText, OnClickListener okListener) {
		this.okText = okText;
		this.okListener = okListener;
	}
	
	public void setCancelListener(String cancelText, OnClickListener cancelListener) {
		this.cancelText = cancelText;
		this.cancelListener = cancelListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.confirmation_dialog, null);
	    initializeViews(view);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setInverseBackgroundForced(true);
	    AlertDialog dialog = builder.create();
	    dialog.setView(view, 0, 0, 0, 0);
	    
//	    Dialog dialog2 = new Dialog(getActivity(), R.style.CustomDialog);
//	    dialog2.setContentView(view);
	    
	    return dialog;
	}

}
