package com.wrensystems.listminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wrensystems.listminder.model.Checklist;
import com.wrensystems.listminder.model.ChecklistItem;
import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.ChecklistCursor;

public class ListIndexFragment2 extends Fragment {
	private static final String TAG = "ListIndexFragment";
	private static final String DIALOG_NEW_LIST = "new_list";
	private static final int REQUEST_NEW_LIST = 0;
	private ChecklistCursor mChecklistCursor;
	private Button mAddCrimeButton;
	private ListView mListView;
	private CursorAdapter mAdapter;
	private TextView mEmptyMsg;
	private EditText mNewListText;
	private Button mAddList;
	private String mNewListName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		getActivity().setTitle(R.string.checklists_title);
		mChecklistCursor = ChecklistSvc.get(getActivity()).getLists();
		
		mAdapter = new ChecklistCursorAdapter(getActivity(), mChecklistCursor);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//View view = super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_list_index2, container, false);
		
		mListView = (ListView)view.findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), ChecklistActivity.class);
				intent.putExtra(ChecklistFragment2.EXTRA_LIST_ID, id);
				startActivity(intent);				
			}
		});

		// Context menu options		
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.fragment_list_index_context, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_checklist:
						ChecklistCursorAdapter adapter = (ChecklistCursorAdapter)mListView.getAdapter();
						ChecklistSvc svc = ChecklistSvc.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (mListView.isItemChecked(i)) {
								Checklist list = ((ChecklistCursor)adapter.getItem(i)).getList();
								svc.deleteList(list.getId());
							}
						}
						mode.finish();
						mChecklistCursor.requery();
						adapter.notifyDataSetChanged();
						return true;
					default:
						return false;
				}
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
			}
		});
		
		mEmptyMsg = (TextView)view.findViewById(android.R.id.empty);
		if (mAdapter.getCount() > 0)
			mEmptyMsg.setVisibility(View.INVISIBLE);
		
		
		mNewListText = (EditText)view.findViewById(R.id.new_list_name);
		mNewListText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mNewListName = s.toString();
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		mAddList = (Button)view.findViewById(R.id.new_list_add_button);
		mAddList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Checklist list = new Checklist();
				list.setName(mNewListName);
				ChecklistSvc.get(getActivity()).addList(list);
				
				mNewListText.setText("");
				mEmptyMsg.setVisibility(View.INVISIBLE);
				
				mChecklistCursor.requery();
				((ChecklistCursorAdapter)mListView.getAdapter()).notifyDataSetChanged();			
			}
		});

		return view;
	}

	@Override
	public void onDestroy() {
		mChecklistCursor.close();
		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_list_index, menu);		
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_list:
				//showAddDialog();
				return true;
			case R.id.menu_item_manage_templates:
				Intent intent = new Intent(getActivity(), TemplateIndexActivity.class);
				startActivity(intent);				
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private class ChecklistCursorAdapter extends CursorAdapter {
		
		private ChecklistCursor mChecklistCursor;
		
		public ChecklistCursorAdapter(Context context, ChecklistCursor cursor) {
			super(context, cursor, 0);
			mChecklistCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = 
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.list_item_checklist, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Checklist list = mChecklistCursor.getList();
			
			TextView nameView = (TextView)view.findViewById(R.id.checklist_list_item_nameTextView);
			nameView.setText(list.getName());
			
			TextView dateView = (TextView)view.findViewById(R.id.checklist_list_item_lastUpdatedTextView);
			dateView.setText(DateFormat.format("MM/dd/yyyy kk:mm", list.getLastUpdated()));
		}

		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == REQUEST_NEW_LIST) {
			long listId = data.getLongExtra(NewChecklistFragment.EXTRA_NEW_LIST_ID, 0);
			mChecklistCursor.requery();
			((ChecklistCursorAdapter)mListView.getAdapter()).notifyDataSetChanged();			
		}
	}
}
