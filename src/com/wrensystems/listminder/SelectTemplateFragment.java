package com.wrensystems.listminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wrensystems.listminder.model.ChecklistSvc;
import com.wrensystems.listminder.model.Template;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.TemplateCursor;

public class SelectTemplateFragment extends ListFragment {
	public static final String EXTRA_LIST_ID = "checklist_id";
	private TemplateCursor mTemplateCursor;
	private Button mAddButton;
	private long mListId;

	public static SelectTemplateFragment newInstance(long listId) {
		Bundle bundle = new Bundle();
		bundle.putLong(EXTRA_LIST_ID, listId);
		
		SelectTemplateFragment fragment = new SelectTemplateFragment();
		fragment.setArguments(bundle);
		
		return fragment;
	}	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		mListId = getArguments().getLong(EXTRA_LIST_ID);		
		
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
		
		mAddButton = (Button)view.findViewById(R.id.add_template);
		mAddButton.setVisibility(View.GONE);
		
		return view;
	}

	@Override
	public void onDestroy() {
		mTemplateCursor.close();
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), SelectTemplateItemsActivity.class);
		intent.putExtra(SelectTemplateItemsFragment.EXTRA_TEMPLATE_ID, id);
		intent.putExtra(SelectTemplateItemsFragment.EXTRA_LIST_ID, mListId);
		startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
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
	

	
}
