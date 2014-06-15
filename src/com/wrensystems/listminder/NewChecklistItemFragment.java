package com.wrensystems.listminder;

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

import com.wrensystems.listminder.model.Checklist;
import com.wrensystems.listminder.model.ChecklistItem;
import com.wrensystems.listminder.model.ChecklistSvc;

public class NewChecklistItemFragment extends DialogFragment {
	private long mListId;
	private EditText mNameText;
	private Button mSaveButton;
	private String mName;

	public static NewChecklistItemFragment newInstance(long listId) {
		Bundle bundle = new Bundle();
		bundle.putLong(ChecklistFragment.EXTRA_LIST_ID, listId);
	
		NewChecklistItemFragment fragment = new NewChecklistItemFragment();
		fragment.setArguments(bundle);
		
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		mListId = getArguments().getLong(ChecklistFragment.EXTRA_LIST_ID);

		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_new_checklistitem, null);
		
		final Dialog dialog = new AlertDialog.Builder(getActivity())
			.setView(view)
			.setNegativeButton(android.R.string.cancel, null)
			//.setTitle(R.string.time_picker_title)
			.create();
		
		mSaveButton = (Button)view.findViewById(R.id.dialog_new_checklistitem_save_button);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChecklistItem item = new ChecklistItem();
				item.setName(mName);
				ChecklistSvc.get(getActivity()).addListItem(mListId, item);
				sendResult(Activity.RESULT_OK);
				dialog.dismiss();
			}
		});
		

		mNameText = (EditText)view.findViewById(R.id.dialog_new_checklistitem_name);
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
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) {
			return;
		}
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, new Intent());
	}

}
