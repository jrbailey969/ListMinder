package com.wrensystems.listminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wrensystems.listminder.model.Checklist;
import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.ChecklistCursor;

public class ListIndexFragment extends ListFragment {
	private static final String TAG = "ListIndexFragment";
	private static final String DIALOG_NEW_LIST = "new_list";
	private static final int REQUEST_NEW_LIST = 0;
	private ChecklistCursor mChecklistCursor;
	private Button mAddCrimeButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		getActivity().setTitle(R.string.checklists_title);
		mChecklistCursor = ChecklistSvc.get(getActivity()).getLists();
		
		ChecklistCursorAdapter adapter = new ChecklistCursorAdapter(getActivity(), mChecklistCursor);
		setListAdapter(adapter);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//View view = super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_list_index, container, false);
		
		ListView listView = (ListView)view.findViewById(android.R.id.list);

		// Context menu options		
 		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.fragment_checklist_context, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_checklist:
						ChecklistCursorAdapter adapter = (ChecklistCursorAdapter)getListAdapter();
						ChecklistSvc svc = ChecklistSvc.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
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
		
		mAddCrimeButton = (Button)view.findViewById(R.id.add_checklist);
		mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAddDialog();
			}
		});
		
		return view;
	}

	@Override
	public void onDestroy() {
		mChecklistCursor.close();
		super.onDestroy();
	}

/*	@Override
	public void onResume() {
		super.onResume();
		((ChecklistAdapter)getListAdapter()).notifyDataSetChanged();
	}*/

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_list_index, menu);		
	}

	
/*	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
	}
*/
/*	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		ChecklistAdapter adapter = (ChecklistAdapter)getListAdapter();
		Crime crime = adapter.getItem(position);
		
		switch (item.getItemId()) {
			case R.id.menu_item_delete_crime:
				CrimeLab.get(getActivity()).deleteCrime(crime);
				adapter.notifyDataSetChanged();
				return true;
		}
		return super.onContextItemSelected(item);
	}
*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_list:
				showAddDialog();
				return true;
			case R.id.menu_item_manage_templates:
				Intent intent = new Intent(getActivity(), TemplateIndexActivity.class);
				startActivity(intent);				
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//Checklist list = ((ChecklistCursorAdapter)getListAdapter())..getItem(position);
		
		Intent intent = new Intent(getActivity(), ChecklistActivity2.class);
		intent.putExtra(ChecklistFragment2.EXTRA_LIST_ID, id); //list.getId());
		startActivity(intent);
	}
	/*
	private class ChecklistAdapter extends ArrayAdapter<Checklist> {
		public ChecklistAdapter(ArrayList<Checklist> crimes) {
			super(getActivity(), 0, crimes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_checklist, null);
			}
			
			Checklist checklist = getItem(position);
			
			TextView nameView = (TextView)convertView.findViewById(R.id.checklist_list_item_nameTextView);
			nameView.setText(checklist.getName());
			
			TextView dateView = (TextView)convertView.findViewById(R.id.checklist_list_item_lastUpdatedTextView);
			dateView.setText(checklist.getLastUpdated().toString());
			
			return convertView;
		}
	}
	*/
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
			((ChecklistCursorAdapter)getListAdapter()).notifyDataSetChanged();			
		}
	}
	
	
	private void showAddDialog() {
		FragmentManager mgr = getActivity().getSupportFragmentManager();
		NewChecklistFragment dialog = NewChecklistFragment.newInstance();
		dialog.setTargetFragment(ListIndexFragment.this, REQUEST_NEW_LIST);
		dialog.show(mgr, DIALOG_NEW_LIST);
		
	}

}
