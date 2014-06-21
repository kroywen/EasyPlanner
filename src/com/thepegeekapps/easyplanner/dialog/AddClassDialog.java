package com.thepegeekapps.easyplanner.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thepegeekapps.easyplanner.R;

public class AddClassDialog extends DialogFragment {
	
	public interface OnAddClassListener {
		void onAddClass(String className);
	}
	
	private EditText classNameView;
	private OnAddClassListener listener;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnAddClassListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.add_class_dialog, null);
	    initializeViews(view);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setInverseBackgroundForced(true);
	    AlertDialog dialog = builder.create();
	    dialog.setView(view, 0, 0, 0, 0);
	    
	    return dialog;
	}
	
	private void initializeViews(View view) {
		classNameView = (EditText) view.findViewById(R.id.classNameView);
		
		Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		Button addBtn = (Button) view.findViewById(R.id.addBtn);
		addBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					String className = classNameView.getText().toString().trim();
					listener.onAddClass(className);
				}
				dismiss();
			}
		});
	}

}
