package com.wrensystems.listminder;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wrensystems.listminder.model.ChecklistItem;
import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.model.Template;
import com.wrensystems.listminder.model.TemplateItem;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.TemplateItemCursor;

public class SelectTemplateItemsFragment extends ListFragment {
	public static final String EXTRA_TEMPLATE_ID = "templateId";
	public static final String EXTRA_LIST_ID = "listId";
	private static final String DIALOG_NEW_ITEM = "new_item";
	private static final int REQUEST_NEW_ITEM = 0;	
	private TemplateItemCursor mTemplateItemCursor;
	private long mTemplateId;
	private long mListId;
	private ArrayList<String> mSelectedItems;
	private MenuItem mSelectAll;
	private MenuItem mDeselectAll;
	private boolean mSelectAllOption = true;
	

	public static SelectTemplateItemsFragment newInstance(long templateId, long listId) {
		Bundle bundle = new Bundle();
		bundle.putLong(EXTRA_TEMPLATE_ID, templateId);
		bundle.putLong(EXTRA_LIST_ID, listId);
		
		SelectTemplateItemsFragment fragment = new SelectTemplateItemsFragment();
		fragment.setArguments(bundle);
		
		return fragment;
	}	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		mTemplateId = getArguments().getLong(EXTRA_TEMPLATE_ID);
		mListId = getArguments().getLong(EXTRA_LIST_ID);

		Template template = ChecklistSvc.get(getActivity()).getTemplate(mTemplateId);
		getActivity().setTitle(getString(R.string.template_title, template.getName()));
						
		mTemplateItemCursor = ChecklistSvc.get(getActivity()).getTemplateItems(mTemplateId);
		
		mSelectedItems = new ArrayList<String>();
		
		TemplateItemCursorAdapter adapter = new TemplateItemCursorAdapter(getActivity(), mTemplateItemCursor);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		View view = inflater.inflate(R.layout.fragment_select_templateitems, container, false);
		
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
		inflater.inflate(R.menu.fragment_select_templateitems, menu);	
		mSelectAll = menu.findItem(R.id.menu_item_select_all_templateitems);
		mDeselectAll = menu.findItem(R.id.menu_item_deselect_all_templateitems);
		displaySelectAllMenuItem(mSelectAllOption);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			case R.id.menu_item_select_all_templateitems:
				TemplateItemCursorAdapter adapter = (TemplateItemCursorAdapter)getListAdapter();
				mSelectedItems = new ArrayList<String>();
				for (int i = adapter.getCount() - 1; i >= 0; i--) {
					TemplateItem templateItem = ((TemplateItemCursor)adapter.getItem(i)).getItem();
					mSelectedItems.add(templateItem.getName());
				}
				displaySelectAllMenuItem(false);
				mTemplateItemCursor.requery();
				adapter.notifyDataSetChanged();
				return true;
			case R.id.menu_item_deselect_all_templateitems:
				mSelectedItems = new ArrayList<String>();
				displaySelectAllMenuItem(true);
				mTemplateItemCursor.requery();
				((TemplateItemCursorAdapter)getListAdapter()).notifyDataSetChanged();
				return true;
			case R.id.menu_item_add_templateitems:
				addSelectedItems();
				getActivity().finish();
				return true;
			case R.id.menu_item_new_templateitem:
				showAddDialog(mTemplateId);
				return true;
			default:
				return super.onOptionsItemSelected(item);
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
            return inflater.inflate(R.layout.list_item_select_templateitem, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TemplateItem item = mTemplateItemCursor.getItem();
			
			TextView nameView = (TextView)view.findViewById(R.id.selectTemplateItem_list_item_nameTextView);
			nameView.setText(item.getName());
			
			CheckBox checkBox = (CheckBox)view.findViewById(R.id.selectTemplateItem_list_item_selectedCheckBox);
			checkBox.setChecked(mSelectedItems.contains(item.getName()));
			checkBox.setTag(item.getName());
			checkBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String itemName = (String)v.getTag();
					if (((CheckBox)v).isChecked()) {
						if (!mSelectedItems.contains(itemName)) {
							mSelectedItems.add(itemName);
						}
						if (((TemplateItemCursorAdapter)getListAdapter()).getCount() == mSelectedItems.size()) {
							displaySelectAllMenuItem(false);
							getActivity().invalidateOptionsMenu();
						}
					} else {
						if (mSelectedItems.contains(itemName)) {
							mSelectedItems.remove(itemName);
						}
						displaySelectAllMenuItem(true);
						getActivity().invalidateOptionsMenu();
					}
						
					
				}
			});
		}

		
	}
	
	private void addSelectedItems() {
		ChecklistSvc listSvc = ChecklistSvc.get(getActivity());
		for (String itemName : mSelectedItems) {
			ChecklistItem newItem = new ChecklistItem();
			newItem.setName(itemName);
			listSvc.addListItem(mListId, newItem);			
		}
	}
	
	private void showAddDialog(long templateId) {
		FragmentManager mgr = getActivity().getSupportFragmentManager();
		NewTemplateItemFragment dialog = NewTemplateItemFragment.newInstance(templateId);
		dialog.setTargetFragment(SelectTemplateItemsFragment.this, REQUEST_NEW_ITEM);
		dialog.show(mgr, DIALOG_NEW_ITEM);
		
	}
	
	private void displaySelectAllMenuItem(boolean isVisible) {
		mSelectAllOption = isVisible;
		mSelectAll.setVisible(isVisible);
		mDeselectAll.setVisible(!isVisible);				
	}
	
}
