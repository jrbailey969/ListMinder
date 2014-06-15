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
import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.model.Template;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.TemplateCursor;

public class TemplateIndexFragment extends Fragment {
	private static final String DIALOG_NEW_TEMPLATE = "new_template";
	private static final int REQUEST_NEW_TEMPLATE = 0;
	private TemplateCursor mTemplateCursor;
	private ListView mListView;
	private CursorAdapter mAdapter;
	private TextView mEmptyMsg;
	private EditText mNewTemplateText;
	private Button mAddTemplate;
	private String mNewTemplateName;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		getActivity().setTitle(R.string.templates_title);
		mTemplateCursor = ChecklistSvc.get(getActivity()).getTemplates();
		
		mAdapter = new TemplateCursorAdapter(getActivity(), mTemplateCursor);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		View view = inflater.inflate(R.layout.fragment_template_index, container, false);
		
		mListView = (ListView)view.findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), TemplateActivity.class);
				intent.putExtra(TemplateFragment.EXTRA_TEMPLATE_ID, id);
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
				mode.getMenuInflater().inflate(R.menu.fragment_template_index_context, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_template:
						TemplateCursorAdapter adapter = (TemplateCursorAdapter)mListView.getAdapter();
						ChecklistSvc svc = ChecklistSvc.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (mListView.isItemChecked(i)) {
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
		
		mEmptyMsg = (TextView)view.findViewById(android.R.id.empty);
		if (mAdapter.getCount() > 0)
			mEmptyMsg.setVisibility(View.INVISIBLE);
		
		
		mNewTemplateText = (EditText)view.findViewById(R.id.new_template_name);
		mNewTemplateText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mNewTemplateName = s.toString();
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		mAddTemplate = (Button)view.findViewById(R.id.new_template_add_button);
		mAddTemplate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Template template = new Template();
				template.setName(mNewTemplateName);
				ChecklistSvc.get(getActivity()).addTemplate(template);
				
				mNewTemplateText.setText("");
				mEmptyMsg.setVisibility(View.INVISIBLE);
				
				mTemplateCursor.requery();
				((TemplateCursorAdapter)mListView.getAdapter()).notifyDataSetChanged();			
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
			((TemplateCursorAdapter)mListView.getAdapter()).notifyDataSetChanged();			
		}
	}
	
	
	private void showAddDialog() {
		FragmentManager mgr = getActivity().getSupportFragmentManager();
		NewTemplateFragment dialog = NewTemplateFragment.newInstance();
		dialog.setTargetFragment(TemplateIndexFragment.this, REQUEST_NEW_TEMPLATE);
		dialog.show(mgr, DIALOG_NEW_TEMPLATE);
		
	}

}
