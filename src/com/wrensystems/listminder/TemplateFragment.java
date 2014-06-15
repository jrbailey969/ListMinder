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
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.model.Template;
import com.wrensystems.listminder.model.TemplateItem;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.TemplateItemCursor;

public class TemplateFragment extends Fragment {
	public static final String EXTRA_TEMPLATE_ID = "listId";
	private static final String DIALOG_NEW_ITEM = "new_item";
	private static final int REQUEST_NEW_ITEM = 0;
	private TemplateItemCursor mTemplateItemCursor;
	private long mTemplateId;
	private Button mAddItemButton;
	private ListView mListView;
	private CursorAdapter mAdapter;
	private TextView mEmptyMsg;
	private EditText mNewTemplateItemText;
	private Button mAddTemplateItem;
	private String mNewTemplateItemName;	

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
		
		mAdapter = new TemplateItemCursorAdapter(getActivity(), mTemplateItemCursor);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		View view = inflater.inflate(R.layout.fragment_template, container, false);
		
		mListView = (ListView)view.findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter);

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
				mode.getMenuInflater().inflate(R.menu.fragment_template_context, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_templateitem:
						TemplateItemCursorAdapter adapter = (TemplateItemCursorAdapter)mListView.getAdapter();
						ChecklistSvc listSvc = ChecklistSvc.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (mListView.isItemChecked(i)) {
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
		
		mEmptyMsg = (TextView)view.findViewById(android.R.id.empty);
		if (mAdapter.getCount() > 0)
			mEmptyMsg.setVisibility(View.INVISIBLE);
		
		
		mNewTemplateItemText = (EditText)view.findViewById(R.id.new_template_item_name);
		mNewTemplateItemText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mNewTemplateItemName = s.toString();
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		mAddTemplateItem = (Button)view.findViewById(R.id.new_template_item_add_button);
		mAddTemplateItem.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TemplateItem item = new TemplateItem();
				item.setName(mNewTemplateItemName);
				ChecklistSvc.get(getActivity()).addTemplateItem(mTemplateId, item);
				
				mNewTemplateItemText.setText("");
				mEmptyMsg.setVisibility(View.INVISIBLE);
				
				mTemplateItemCursor.requery();
				((TemplateItemCursorAdapter)mListView.getAdapter()).notifyDataSetChanged();			
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
			((TemplateItemCursorAdapter)mListView.getAdapter()).notifyDataSetChanged();			
		}
	}
	
	
	private void showAddDialog(long templateId) {
		FragmentManager mgr = getActivity().getSupportFragmentManager();
		NewTemplateItemFragment dialog = NewTemplateItemFragment.newInstance(templateId);
		dialog.setTargetFragment(TemplateFragment.this, REQUEST_NEW_ITEM);
		dialog.show(mgr, DIALOG_NEW_ITEM);
		
	}

}
