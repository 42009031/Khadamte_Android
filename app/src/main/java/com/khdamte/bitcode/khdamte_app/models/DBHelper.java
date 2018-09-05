package com.khdamte.bitcode.khdamte_app.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Amado on 7/28/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String OFFICE_TABLE_NAME = "offices";
    public static final String OFFICE_COLUMN_ID = "id";
    public static final String OFFICE_COLUMN_NAME = "name";
    public static final String OFFICE_COLUMN_IMAGE = "image";
    public static final String OFFICE_COLUMN_DESCRIPTION = "description";
    public DBHelper(Context paramContext) {
        super(paramContext, "FavouriteOffices.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+OFFICE_TABLE_NAME+" (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " name varchar(255)," +
                " image varchar(255)," +
                " description varchar(255)) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS offices");
    }


    public boolean checkOfficeExist(String office_id) {
        Cursor localCursor = getReadableDatabase().rawQuery("select * from "+OFFICE_TABLE_NAME+" where "+OFFICE_COLUMN_ID+ " = " + office_id  , null);
        localCursor.moveToFirst();
        boolean bool1 = localCursor.isAfterLast();
        boolean bool2 = false;
        if (!bool1) {
            bool2 = true;
            localCursor.moveToNext();
        }
        return bool2;
    }

    public ArrayList<Office_Model> getAllOffices() {
        ArrayList<Office_Model> localArrayList = new ArrayList<Office_Model>();
        Cursor localCursor = getReadableDatabase().rawQuery("select * from offices", null);
        localCursor.moveToFirst();
        while (!localCursor.isAfterLast()) {
            String id = localCursor.getString(localCursor.getColumnIndex("id"));
            String name = localCursor.getString(localCursor.getColumnIndex("name"));
            String image = localCursor.getString(localCursor.getColumnIndex("image"));
            String desc = localCursor.getString(localCursor.getColumnIndex("description"));
            localArrayList.add(new Office_Model(id, name, image, desc));
            localCursor.moveToNext();
        }
        return localArrayList;
    }

    public boolean insertFavOffice(String id, String name, String image, String desc) {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("id", id);
        localContentValues.put("name", name);
        localContentValues.put("image", image);
        localContentValues.put("description", desc);
        localSQLiteDatabase.insert(OFFICE_TABLE_NAME, null, localContentValues);
        return true;
    }

    public boolean deleteOffice(String office_id) {
        getWritableDatabase().delete(OFFICE_TABLE_NAME, "id = " + office_id, null);
        return true;
    }

    public boolean deleteAllOffices() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + OFFICE_TABLE_NAME);
        db.close();
        return true;
    }
}
