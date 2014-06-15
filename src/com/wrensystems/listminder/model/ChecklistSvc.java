package com.wrensystems.listminder.model;

import android.content.Context;

import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.ChecklistCursor;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.ChecklistItemCursor;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.TemplateCursor;
import com.wrensystems.listminder.persistence.ListMinderDatabaseHelper.TemplateItemCursor;

public class ChecklistSvc {
	private static ChecklistSvc sListSvc;
	private ListMinderDatabaseHelper mDbHelper;
	
	private ChecklistSvc(Context appContext) {
		mDbHelper = new ListMinderDatabaseHelper(appContext);		
	}
	
	public static ChecklistSvc get(Context appContext) {
		if (sListSvc == null) {
			sListSvc = new ChecklistSvc(appContext.getApplicationContext());
		}
		
		return sListSvc;
	}
	
	public ChecklistCursor getLists() {
		return mDbHelper.queryLists();
	}
	
	public Checklist getList(long id) {
        Checklist list = null;
        ChecklistCursor cursor = mDbHelper.queryLists(id);
        cursor.moveToFirst();
        // if we got a row, get a run
        if (!cursor.isAfterLast())
            list = cursor.getList();
        cursor.close();
        return list;
	}
	
	public long addList(Checklist list) {
		return mDbHelper.insertChecklist(list);
	}
	
	public void deleteList(long listId) {
		mDbHelper.deleteChecklist(listId);
	}
	
	public ChecklistItemCursor getListItems(long listId) {
		return mDbHelper.queryListItems(listId);
	}

	public ChecklistItem getListItem(long listId, long itemId) {
        ChecklistItem item = null;
        ChecklistItemCursor cursor = mDbHelper.queryListItems(listId, itemId);
        cursor.moveToFirst();
        // if we got a row, get a run
        if (!cursor.isAfterLast())
            item = cursor.getItem();
        cursor.close();
        return item;
	}

	public long addListItem(long listId, ChecklistItem item) {
		return mDbHelper.insertChecklistItem(listId, item);
	}
	
	public void updateListItem(ChecklistItem item) {
		mDbHelper.updateChecklistItem(item);
	}
	
	public void deleteListItem(long itemId) {
		mDbHelper.deleteChecklistItem(itemId);
	}
	
	public TemplateCursor getTemplates() {
		return mDbHelper.queryTemplates();
	}
	
	public Template getTemplate(long id) {
        Template template = null;
        TemplateCursor cursor = mDbHelper.queryTemplates(id);
        cursor.moveToFirst();
        // if we got a row, get a run
        if (!cursor.isAfterLast())
            template = cursor.getTemplate();
        cursor.close();
        return template;
	}
	
	public long addTemplate(Template template) {
		return mDbHelper.insertTemplate(template);
	}
	
	public void deleteTemplate(long id) {
		mDbHelper.deleteTemplate(id);
	}
	
	public TemplateItemCursor getTemplateItems(long templateId) {
		return mDbHelper.queryTemplateItems(templateId);
	}

	public TemplateItem getTemplateItem(long templateId, long itemId) {
        TemplateItem item = null;
        TemplateItemCursor cursor = mDbHelper.queryTemplateItems(templateId, itemId);
        cursor.moveToFirst();
        // if we got a row, get a run
        if (!cursor.isAfterLast())
            item = cursor.getItem();
        cursor.close();
        return item;
	}

	public long addTemplateItem(long templateId, TemplateItem item) {
		return mDbHelper.insertTemplateItem(templateId, item);
	}
	
/*	public void updateTemplateItem(TemplateItem item) {
		mDbHelper.updateTemplateItem(item);
	}
*/	
	public void deleteTemplateItem(long itemId) {
		mDbHelper.deleteTemplateItem(itemId);
	}
	
	
}
