package com.wrensystems.listminder.persistence;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wrensystems.listminder.model.Checklist;
import com.wrensystems.listminder.model.ChecklistItem;
import com.wrensystems.listminder.model.Template;
import com.wrensystems.listminder.model.TemplateItem;

public class ListMinderDatabaseHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "listminder.sqlite";
	private static final int VERSION = 2;
	
	private static final String COLUMN_ID = "_id";
	
	private static final String TABLE_CHECKLIST = "checklist";
	private static final String COLUMN_CHECKLIST_NAME = "name";
	private static final String COLUMN_CHECKLIST_LAST_UPDATED = "last_updated";
	
	private static final String TABLE_CHECKLIST_ITEM = "checklist_item";
	private static final String COLUMN_CHECKLIST_ITEM_NAME = "name";
	private static final String COLUMN_CHECKLIST_ITEM_IS_COMPLETED = "is_completed";
	private static final String COLUMN_CHECKLIST_ITEM_CHECKLIST_ID = "checklist_id";
	
	private static final String TABLE_TEMPLATE = "template";
	private static final String COLUMN_TEMPLATE_NAME = "name";
	private static final String COLUMN_TEMPLATE_LAST_UPDATED = "last_updated";
	
	private static final String TABLE_TEMPLATE_ITEM = "template_item";
	private static final String COLUMN_TEMPLATE_ITEM_NAME = "name";
	private static final String COLUMN_TEMPLATE_ITEM_TEMPLATE_ID = "template_id";
	
	public ListMinderDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table checklist (" +
				"_id integer primary key autoincrement, name varchar(100), last_updated integer)");
		
		db.execSQL("create table checklist_item (" +
				"_id integer primary key autoincrement, name varchar(100), is_completed integer, " +
				"checklist_id integer references checklist(_id))");
		
		db.execSQL("create table template (" +
				"_id integer primary key autoincrement, name varchar(100), last_updated integer)");

		db.execSQL("create table template_item (" +
				"_id integer primary key autoincrement, name varchar(100), " +
				"template_id integer references template(_id))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			db.execSQL("create table template (" +
					"_id integer primary key autoincrement, name varchar(100), last_updated integer)");
	
			db.execSQL("create table template_item (" +
					"_id integer primary key autoincrement, name varchar(100), " +
					"template_id integer references template(_id))");
		}		
	}
	
	public long insertChecklist(Checklist list) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_CHECKLIST_NAME, list.getName());
		values.put(COLUMN_CHECKLIST_LAST_UPDATED, list.getLastUpdated().getTime());
		return getWritableDatabase().insert(TABLE_CHECKLIST, null, values);
	}
	
	public void deleteChecklist(long listId) {
		getWritableDatabase().delete(TABLE_CHECKLIST, COLUMN_ID + " = ?", new String[] { String.valueOf(listId) });
	}
	
	public long insertChecklistItem(long listId, ChecklistItem item) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_CHECKLIST_ITEM_NAME, item.getName());
		values.put(COLUMN_CHECKLIST_ITEM_IS_COMPLETED, item.isCompleted() ? 1 : 0);
		values.put(COLUMN_CHECKLIST_ITEM_CHECKLIST_ID, listId);
		return getWritableDatabase().insert(TABLE_CHECKLIST_ITEM, null, values);
	}
	
	public void updateChecklistItem(ChecklistItem item) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_CHECKLIST_ITEM_NAME, item.getName());
		values.put(COLUMN_CHECKLIST_ITEM_IS_COMPLETED, item.isCompleted() ? 1 : 0);
		getWritableDatabase().update(TABLE_CHECKLIST_ITEM, values, COLUMN_ID + " = ?", new String[] { String.valueOf(item.getId()) });
	}

	public void deleteChecklistItem(long itemId) {
		getWritableDatabase().delete(TABLE_CHECKLIST_ITEM, COLUMN_ID + " = ?", new String[] { String.valueOf(itemId) });
	}
	
	public long insertTemplate(Template template) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_TEMPLATE_NAME, template.getName());
		values.put(COLUMN_TEMPLATE_LAST_UPDATED, template.getLastUpdated().getTime());
		return getWritableDatabase().insert(TABLE_TEMPLATE, null, values);
	}
	
	public void deleteTemplate(long templateId) {
		getWritableDatabase().delete(TABLE_TEMPLATE, COLUMN_ID + " = ?", new String[] { String.valueOf(templateId) });
	}
		
	public long insertTemplateItem(long templateId, TemplateItem item) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_TEMPLATE_ITEM_NAME, item.getName());
		values.put(COLUMN_TEMPLATE_ITEM_TEMPLATE_ID, templateId);
		return getWritableDatabase().insert(TABLE_TEMPLATE_ITEM, null, values);
	}
	
	public void deleteTemplateItem(long itemId) {
		getWritableDatabase().delete(TABLE_TEMPLATE_ITEM, COLUMN_ID + " = ?", new String[] { String.valueOf(itemId) });
	}
	
	public ChecklistCursor queryLists() {
		Cursor cursor = getReadableDatabase().query(TABLE_CHECKLIST, 
				null, null, null, null, null, COLUMN_CHECKLIST_LAST_UPDATED + " desc");
		return new ChecklistCursor(cursor);
	}
	
	public ChecklistCursor queryLists(long id) {
		Cursor cursor = getReadableDatabase().query(TABLE_CHECKLIST, 
				null, // all columns 
				COLUMN_ID + " = ?", // filter
				new String[]{ String.valueOf(id) }, // filter args 
				null, // group by 
				null, // having 
				null, // order by
				"1");
		return new ChecklistCursor(cursor);
	}
	
	public ChecklistItemCursor queryListItems(long listId) {
		Cursor cursor = getReadableDatabase().query(TABLE_CHECKLIST_ITEM, 
				null, // all columns 
				COLUMN_CHECKLIST_ITEM_CHECKLIST_ID + " = ?", // filter
				new String[]{ String.valueOf(listId) }, // filter args 
				null, // group by 
				null, // having 
				COLUMN_CHECKLIST_ITEM_NAME + " asc");
		return new ChecklistItemCursor(cursor);
	}
	
	public ChecklistItemCursor queryListItems(long listId, long itemId) {
		Cursor cursor = getReadableDatabase().query(TABLE_CHECKLIST_ITEM, 
				null, // all columns 
				COLUMN_CHECKLIST_ITEM_CHECKLIST_ID + " = ? AND " + COLUMN_ID + " = ?", // filter
				new String[]{ String.valueOf(listId), String.valueOf(itemId) }, // filter args 
				null, // group by 
				null, // having 
				null, // order by
				"1");
		return new ChecklistItemCursor(cursor);
	}
	
	public TemplateCursor queryTemplates() {
		Cursor cursor = getReadableDatabase().query(TABLE_TEMPLATE, 
				null, null, null, null, null, COLUMN_TEMPLATE_LAST_UPDATED + " desc");
		return new TemplateCursor(cursor);
	}
	
	public TemplateCursor queryTemplates(long id) {
		Cursor cursor = getReadableDatabase().query(TABLE_TEMPLATE, 
				null, // all columns 
				COLUMN_ID + " = ?", // filter
				new String[]{ String.valueOf(id) }, // filter args 
				null, // group by 
				null, // having 
				null, // order by
				"1");
		return new TemplateCursor(cursor);
	}
	
	public TemplateItemCursor queryTemplateItems(long templateId) {
		Cursor cursor = getReadableDatabase().query(TABLE_TEMPLATE_ITEM, 
				null, // all columns 
				COLUMN_TEMPLATE_ITEM_TEMPLATE_ID + " = ?", // filter
				new String[]{ String.valueOf(templateId) }, // filter args 
				null, // group by 
				null, // having 
				COLUMN_TEMPLATE_ITEM_NAME + " asc");
		return new TemplateItemCursor(cursor);
	}
	
	public TemplateItemCursor queryTemplateItems(long templateId, long itemId) {
		Cursor cursor = getReadableDatabase().query(TABLE_TEMPLATE_ITEM, 
				null, // all columns 
				COLUMN_TEMPLATE_ITEM_TEMPLATE_ID + " = ? AND " + COLUMN_ID + " = ?", // filter
				new String[]{ String.valueOf(templateId), String.valueOf(itemId) }, // filter args 
				null, // group by 
				null, // having 
				null, // order by
				"1");
		return new TemplateItemCursor(cursor);
	}
	
	public static class ChecklistCursor extends CursorWrapper {
		
		public ChecklistCursor(Cursor cursor) {
			super(cursor);
		}
		
		public Checklist getList() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			
			Checklist list = new Checklist();
			list.setId(getLong(getColumnIndex(COLUMN_ID)));
			list.setName(getString(getColumnIndex(COLUMN_CHECKLIST_NAME)));
			list.setLastUpdated(new Date(getLong(getColumnIndex(COLUMN_CHECKLIST_LAST_UPDATED))));
			return list;
		}
	}

	public static class ChecklistItemCursor extends CursorWrapper {
		
		public ChecklistItemCursor(Cursor cursor) {
			super(cursor);
		}
		
		public ChecklistItem getItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			
			ChecklistItem item = new ChecklistItem();
			item.setId(getLong(getColumnIndex(COLUMN_ID)));
			item.setName(getString(getColumnIndex(COLUMN_CHECKLIST_ITEM_NAME)));
			item.setCompleted(getInt(getColumnIndex(COLUMN_CHECKLIST_ITEM_IS_COMPLETED)) == 1);
			return item;
		}
	}
	
	public static class TemplateCursor extends CursorWrapper {
		
		public TemplateCursor(Cursor cursor) {
			super(cursor);
		}
		
		public Template getTemplate() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			
			Template template = new Template();
			template.setId(getLong(getColumnIndex(COLUMN_ID)));
			template.setName(getString(getColumnIndex(COLUMN_TEMPLATE_NAME)));
			template.setLastUpdated(new Date(getLong(getColumnIndex(COLUMN_TEMPLATE_LAST_UPDATED))));
			return template;
		}
	}

	public static class TemplateItemCursor extends CursorWrapper {
		
		public TemplateItemCursor(Cursor cursor) {
			super(cursor);
		}
		
		public TemplateItem getItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			
			TemplateItem item = new TemplateItem();
			item.setId(getLong(getColumnIndex(COLUMN_ID)));
			item.setName(getString(getColumnIndex(COLUMN_TEMPLATE_ITEM_NAME)));
			return item;
		}
	}
	
}
