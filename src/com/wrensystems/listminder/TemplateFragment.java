package com.wrensystems.listminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
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

import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.model.Template;
import com.wrensystems.listminder.model.TemplateItem;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.TemplateItemCursor;

public class TemplateFragment extends ListFragment {
	public static final String EXTRA_TEMPLATE_ID = "listId";
	private static final String DIALOG_NEW_ITEM = "new_item";
	private static final int REQUEST_NEW_ITEM = 0;
	private TemplateItemCursor mTemplateItemCursor;
	private long mTemplateId;
	private Button mAddItemButton;
	

	public static TemplateFragment newInstance(long listId) {
		Bundle bundle = new Bundle();
		bundle.putLong(EXTRA_TEMPLATE_ID, listId);
		
		TemplateFragment fragment = new TemplateFragment();
		fragment.setArguments(bundle);
		
		return fragment;
	}	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		mTemplateId = getArguments().getLong(EXTRA_TEMPLATE_ID);
		
		Template template = ChecklistSvc.get(getActivity()).getTemplate(mTemplateId);
		getActivity().setTitle(getString(R.string.template_title, template.getName()));
				
		mTemplateItemCursor = ChecklistSvc.get(getActivity()).getTemplateItems(mTemplateId);
		
		TemplateItemCursorAdapter adapter = new TemplateItemCursorAdapter(getActivity(), mTemplateItemCursor);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		View view = inflater.inflate(R.layout.fragment_template, container, false);
		
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
				mode.getMenuInflater().inflate(R.menu.fragment_template_context, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_templateitem:
						TemplateItemCursorAdapter adapter = (TemplateItemCursorAdapter)getListAdapter();
						ChecklistSvc listSvc = ChecklistSvc.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								TemplateItem templateItem = ((TemplateItemCursor)adapter.getItem(i)).getItem();
								listSvc.deleteTemplateItem(templateItem.getId());
							}
						}
						mode.finish();
						mTemplateItemCursor.requery();
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
		
		mAddItemButton = (Button)view.findViewById(R.id.add_templateitem);
		mAddItemButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAddDialog(mTemplateId);
			}
		});
		
		return view;
	}

	@Override
	public void onDestroy() {
		mTemplateItemCursor.close();
		super.onDestroy();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_template, menu);		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			case R.id.menu_item_new_templateitem:
				showAddDialog(mTemplateId);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private class TemplateItemCursorAdapter extends CursorAdapter {
		
		private TemplateItemCursor mTemplateItemCursor;
		
		public TemplateItemCursorAdapter(Context context, TemplateItemCursor cursor) {
			super(context, cursor, 0);
			mTemplateItemCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = 
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.list_item_templateitem, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TemplateItem item = mTemplateItemCursor.getItem();
			
			TextView nameView = (TextView)view.findViewById(R.id.templateItem_list_item_nameTextView);
			nameView.setText(item.getName());
		}

		
	}
	

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == REQUEST_NEW_ITEM) {
			mTemplateItemCursor.requery();
			((TemplateItemCursorAdapter)getListAdapter()).notifyDataSetChanged();			
		}
	}
	
	
	private void showAddDialog(long templateId) {
		FragmentManager mgr = getActivity().getSupportFragmentManager();
		NewTemplateItemFragment dialog = NewTemplateItemFragment.newInstance(templateId);
		dialog.setTargetFragment(TemplateFragment.this, REQUEST_NEW_ITEM);
		dialog.show(mgr, DIALOG_NEW_ITEM);
		
	}

}
