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

import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.model.Template;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.TemplateCursor;

public class TemplateIndexFragment extends ListFragment {
	private static final String DIALOG_NEW_TEMPLATE = "new_template";
	private static final int REQUEST_NEW_TEMPLATE = 0;
	private TemplateCursor mTemplateCursor;
	private Button mAddCrimeButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		getActivity().setTitle(R.string.templates_title);
		mTemplateCursor = ChecklistSvc.get(getActivity()).getTemplates();
		
		TemplateCursorAdapter adapter = new TemplateCursorAdapter(getActivity(), mTemplateCursor);
		setListAdapter(adapter);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		View view = inflater.inflate(R.layout.fragment_template_index, container, false);
		
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
				mode.getMenuInflater().inflate(R.menu.fragment_template_index_context, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_template:
						TemplateCursorAdapter adapter = (TemplateCursorAdapter)getListAdapter();
						ChecklistSvc svc = ChecklistSvc.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								Template template = ((TemplateCursor)adapter.getItem(i)).getTemplate();
								svc.deleteTemplate(template.getId());
							}
						}
						mode.finish();
						mTemplateCursor.requery();
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
		
		mAddCrimeButton = (Button)view.findViewById(R.id.add_template);
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
		mTemplateCursor.close();
		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_template_index, menu);		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			case R.id.menu_item_new_template:
				showAddDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), TemplateActivity.class);
		intent.putExtra(TemplateFragment.EXTRA_TEMPLATE_ID, id);
		startActivity(intent);
	}
	
	private class TemplateCursorAdapter extends CursorAdapter {
		
		private TemplateCursor mTemplateCursor;
		
		public TemplateCursorAdapter(Context context, TemplateCursor cursor) {
			super(context, cursor, 0);
			mTemplateCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = 
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.list_item_template, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Template template = mTemplateCursor.getTemplate();
			
			TextView nameView = (TextView)view.findViewById(R.id.template_list_item_nameTextView);
			nameView.setText(template.getName());
			
			TextView dateView = (TextView)view.findViewById(R.id.template_list_item_lastUpdatedTextView);
			dateView.setText(DateFormat.format("MM/dd/yyyy kk:mm", template.getLastUpdated()));
		}

		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == REQUEST_NEW_TEMPLATE) {
			mTemplateCursor.requery();
			((TemplateCursorAdapter)getListAdapter()).notifyDataSetChanged();			
		}
	}
	
	
	private void showAddDialog() {
		FragmentManager mgr = getActivity().getSupportFragmentManager();
		NewTemplateFragment dialog = NewTemplateFragment.newInstance();
		dialog.setTargetFragment(TemplateIndexFragment.this, REQUEST_NEW_TEMPLATE);
		dialog.show(mgr, DIALOG_NEW_TEMPLATE);
		
	}

}
