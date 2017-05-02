package com.programmer2.mybarcodescanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PROGRAMMER2 on 5/2/2017.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbbarcode.db";
    private static final String TABLE_ITEM = "item";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BARCODE = "barcode";
    private static final String COLUMN_DESCRIPTION = "name";

    private static final String TABLE_ITEM_CREATE = "create table if not exists "+TABLE_ITEM+" ("
            +COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            +COLUMN_BARCODE+" TEXT, "
            +COLUMN_DESCRIPTION+" TEXT)";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //CREATE TABLE
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_ITEM_CREATE);
    }

    public void queryData(String sql){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    //ADD ITEM
    public void insertItem(Item item){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_BARCODE, item.getBarcode());
        values.put(COLUMN_DESCRIPTION, item.getDescription());

        database.insert(TABLE_ITEM , null , values);
        database.close();
    }
}
