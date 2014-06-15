package com.wrensystems.listminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wrensystems.listminder.model.Checklist;
import com.wrensystems.listminder.model.ChecklistItem;
import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.ChecklistItemCursor;

public class ChecklistFragment2 extends Fragment {
	public static final String EXTRA_LIST_ID = "listId";
	private static final String DIALOG_NEW_ITEM = "new_item";
	private static final int REQUEST_NEW_ITEM = 0;
	private static final int REQUEST_ADD_FROM_TEMPLATE = 1;
	private ChecklistItemCursorAdapter mAdapter;
	private ChecklistItemCursor mChecklistItemCursor;
	private long mListId;
	private ListView mListView;
	private TextView mEmptyMsg;
	private EditText mNewItemText;
	private Button mAddItem;
	private String mNewItemName;
	

	public static ChecklistFragment2 newInstance(long listId) {
		Bundle bundle = new Bundle();
		bundle.putLong(EXTRA_LIST_ID, listId);
		
		ChecklistFragment2 fragment = new ChecklistFragment2();
		fragment.setArguments(bundle);
		
		return fragment;
	}	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		mListId = getArguments().getLong(EXTRA_LIST_ID);
		
		Checklist list = ChecklistSvc.get(getActivity()).getList(mListId);
		if (list != null) {
			getActivity().setTitle(getString(R.string.checklist_title, list.getName()));
		}
		
		mChecklistItemCursor = ChecklistSvc.get(getActivity()).getListItems(mListId);
		
		mAdapter = new ChecklistItemCursorAdapter(getActivity(), mChecklistItemCursor);		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		View view = inflater.inflate(R.layout.fragment_checklist2, container, false);
		
		mListView = (ListView)view.findViewById(R.id.checklist_items);
		mListView.setAdapter(mAdapter);
				
		mEmptyMsg = (TextView)view.findViewById(android.R.id.empty);
		if (mAdapter.getCount() > 0)
			mEmptyMsg.setVisibility(View.INVISIBLE);
		
		mNewItemText = (EditText)view.findViewById(R.id.new_item_name);
		mNewItemText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mNewItemName = s.toString();
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		mAddItem = (Button)view.findViewById(R.id.new_item_add_button);
		mAddItem.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChecklistItemCursorAdapter adapter = (ChecklistItemCursorAdapter)mListView.getAdapter();
				ChecklistItem item = new ChecklistItem();
				item.setName(mNewItemName);
				ChecklistSvc.get(getActivity()).addListItem(mListId, item);
				
				mNewItemText.setText("");
				
				mChecklistItemCursor.requery();
				adapter.notifyDataSetChanged();				
			}
		});
		
		return view;
	}

	@Override
	public void onResume() {
		mChecklistItemCursor.requery();
		((ChecklistItemCursorAdapter)mListView.getAdapter()).notifyDataSetChanged();
		
		super.onResume();
	}

	@Override
	public void onDestroy() {
		mChecklistItemCursor.close();
		super.onDestroy();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_checklist, menu);		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			case R.id.menu_item_new_item:
				showAddDialog(mListId);
				return true;
			case R.id.menu_item_remove_completed_items:
				ChecklistItemCursorAdapter adapter = (ChecklistItemCursorAdapter)mListView.getAdapter();
				// Make sure we have fresh data before processing
				mChecklistItemCursor.requery();
				adapter.notifyDataSetChanged();
				
				ChecklistSvc listSvc = ChecklistSvc.get(getActivity());
				for (int i = adapter.getCount() - 1; i >= 0; i--) {
					ChecklistItem listItem = ((ChecklistItemCursor)adapter.getItem(i)).getItem();
					if (listItem.isCompleted()) {
						listSvc.deleteListItem(listItem.getId());
					}
				}
				mChecklistItemCursor.requery();
				adapter.notifyDataSetChanged();
				return true;
			case R.id.menu_item_add_from_template:
				Intent intent = new Intent(getActivity(), SelectTemplateActivity.class);
				intent.putExtra(SelectTemplateFragment.EXTRA_LIST_ID, mListId);
				getActivity().startActivityForResult(intent, REQUEST_ADD_FROM_TEMPLATE);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private class ChecklistItemCursorAdapter extends CursorAdapter {
		
		private ChecklistItemCursor mChecklistItemCursor;
		
		public ChecklistItemCursorAdapter(Context context, ChecklistItemCursor cursor) {
			super(context, cursor, 0);
			mChecklistItemCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = 
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.list_item_checklistitem, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ChecklistItem item = mChecklistItemCursor.getItem();
			
			TextView nameView = (TextView)view.findViewById(R.id.checklistItem_list_item_nameTextView);
			nameView.setText(item.getName());
			
			CheckBox completeCheckBox = (CheckBox)view.findViewById(R.id.checklistItem_list_item_completedCheckBox);
			completeCheckBox.setChecked(item.isCompleted());
			completeCheckBox.setTag(item);
			
			completeCheckBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ChecklistItem item = (ChecklistItem)v.getTag();
					CheckBox checkBox = (CheckBox)v;
					item.setCompleted(checkBox.isChecked());
					ChecklistSvc.get(getActivity()).updateListItem(item);
					mChecklistItemCursor.requery();
				}
			});
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == REQUEST_NEW_ITEM || requestCode == REQUEST_ADD_FROM_TEMPLATE) {
			mChecklistItemCursor.requery();
			((ChecklistItemCursorAdapter)mListView.getAdapter()).notifyDataSetChanged();			
		}
	}
	
	private void showAddDialog(long listId) {
		FragmentManager mgr = getActivity().getSupportFragmentManager();
		NewChecklistItemFragment dialog = NewChecklistItemFragment.newInstance(listId);
		dialog.setTargetFragment(ChecklistFragment2.this, REQUEST_NEW_ITEM);
		dialog.show(mgr, DIALOG_NEW_ITEM);
		
	}

}
