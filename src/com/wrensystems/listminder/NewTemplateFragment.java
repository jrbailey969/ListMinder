package com.wrensystems.listminder;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.model.Template;

public class NewTemplateFragment extends DialogFragment {
	public static final String EXTRA_NEW_TEMPLATE_ID = "com.wrensystems.listminder.new_template_id";
	
	private EditText mNameText;
	private Button mSaveButton;
	private String mName;

	public static NewTemplateFragment newInstance() {
		NewTemplateFragment fragment = new NewTemplateFragment();
		
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_new_template, null);
		
		final Dialog dialog = new AlertDialog.Builder(getActivity())
			.setView(view)
			.setNegativeButton(android.R.string.cancel, null)
			//.setTitle(R.string.time_picker_title)
			.create();
		
		mSaveButton = (Button)view.findViewById(R.id.dialog_new_template_save_button);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Template template = new Template();
				template.setName(mName);
				template.setLastUpdated(new Date());
				ChecklistSvc.get(getActivity()).addTemplate(template);
				sendResult(Activity.RESULT_OK, template.getId());
				dialog.dismiss();
			}
		});
		

		mNameText = (EditText)view.findViewById(R.id.dialog_new_template_name);
		mNameText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mName = s.toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// nothing for now
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// nothing for now
				
			}
		});
				
		return dialog;
	}
	
	private void sendResult(int resultCode, long newId) {
		if (getTargetFragment() == null) {
			return;
		}
		
		Intent intent = new Intent();
		intent.putExtra(EXTRA_NEW_TEMPLATE_ID, newId);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
	}

}
