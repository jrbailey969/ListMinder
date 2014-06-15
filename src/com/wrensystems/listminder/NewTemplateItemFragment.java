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

import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.model.TemplateItem;

public class NewTemplateItemFragment extends DialogFragment {
	private long mListId;
	private EditText mNameText;
	private Button mSaveButton;
	private String mName;

	public static NewTemplateItemFragment newInstance(long listId) {
		Bundle bundle = new Bundle();
		bundle.putLong(TemplateFragment.EXTRA_TEMPLATE_ID, listId);
	
		NewTemplateItemFragment fragment = new NewTemplateItemFragment();
		fragment.setArguments(bundle);
		
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		mListId = getArguments().getLong(TemplateFragment.EXTRA_TEMPLATE_ID);

		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_new_templateitem, null);
		
		final Dialog dialog = new AlertDialog.Builder(getActivity())
			.setView(view)
			.setNegativeButton(android.R.string.cancel, null)
			.create();
		
		mSaveButton = (Button)view.findViewById(R.id.dialog_new_templateitem_save_button);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TemplateItem item = new TemplateItem();
				item.setName(mName);
				ChecklistSvc.get(getActivity()).addTemplateItem(mListId, item);
				sendResult(Activity.RESULT_OK);
				dialog.dismiss();
			}
		});
		

		mNameText = (EditText)view.findViewById(R.id.dialog_new_templateitem_name);
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
