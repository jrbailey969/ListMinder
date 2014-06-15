package com.wrensystems.listminder;

import java.util.Date;
import java.util.UUID;

import com.wrensystems.listminder.model.Checklist;
import com.wrensystems.listminder.model.ChecklistSvc;

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

public class NewChecklistFragment extends DialogFragment {
	public static final String EXTRA_NEW_LIST_ID = "com.wrensystems.listminder.new_checklist_id";
	
	private EditText mNameText;
	private Button mSaveButton;
	private String mName;

	public static NewChecklistFragment newInstance() {
		NewChecklistFragment fragment = new NewChecklistFragment();
		
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_new_checklist, null);
		
		final Dialog dialog = new AlertDialog.Builder(getActivity())
			.setView(view)
			.setNegativeButton(android.R.string.cancel, null)
			//.setTitle(R.string.time_picker_title)
			.create();
		
		mSaveButton = (Button)view.findViewById(R.id.dialog_new_checklist_save_button);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Checklist list = new Checklist();
				list.setName(mName);
				list.setLastUpdated(new Date());
				ChecklistSvc.get(getActivity()).addList(list);
				sendResult(Activity.RESULT_OK, list.getId());
				dialog.dismiss();
			}
		});
		

		mNameText = (EditText)view.findViewById(R.id.dialog_new_checklist_name);
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
		intent.putExtra(EXTRA_NEW_LIST_ID, newId);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
	}

}
