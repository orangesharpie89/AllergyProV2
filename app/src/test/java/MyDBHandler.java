package com.example.seanwilliams.AllergyProV2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper
{
    private static MyDBHandler sInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "products.db";
    public static final String TABLE_PRODUCTS = "menuitems";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MENUITEMNAME = "_menuitemname";
    public static final String COLUMN_MENUITEMINGREDIENTS = "_menuitemingredients";

    private MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_MENUITEMNAME + " TEXT," +
                COLUMN_MENUITEMINGREDIENTS + " TEXT" +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public static synchronized MyDBHandler getInstance(Context context)
    {
        if(sInstance == null)
        {
            sInstance = new MyDBHandler(context.getApplicationContext(), null, null, 1);
        }
        return sInstance;
    }

    //Add new row to the database
    public void addProduct(MenuItem item)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MENUITEMNAME, item.get_menuitemname());
        values.put(COLUMN_MENUITEMINGREDIENTS, item.get_menuitemingredients());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);
        //db.close();
    }

    public void deleteProduct(String productName)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(" DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_MENUITEMNAME + "=\"" + productName + "\";");

    }

    //Print out database as string
    public String databaseToString()
    {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_MENUITEMNAME + " FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_MENUITEMINGREDIENTS + " LIKE '%Flour%'";

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            if(c.getString(c.getColumnIndex("_menuitemname"))!= null)
            {
                dbString+=c.getString(c.getColumnIndex("_menuitemname"));
                dbString+="\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }




    public List<MenuItem> getMenuItems()
    {
        List<MenuItem> items = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS + " ORDER BY " +
                COLUMN_ID + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem();
                item.set_id(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                item.set_menuitemname(cursor.getString(cursor.getColumnIndex(COLUMN_MENUITEMNAME)));
                item.set_menuitemingredients(cursor.getString(cursor.getColumnIndex(COLUMN_MENUITEMINGREDIENTS)));

                items.add(item);
            } while (cursor.moveToNext());
        }

        // close db connection
        //db.close();

        // return notes list
        return items;
    }

    public void clear()
    {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, 1, 2);
    }

    public String[] findSomething(String search)
    {
        String[] results = new String[1];
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_MENUITEMNAME + " FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_MENUITEMINGREDIENTS + " LIKE '%Flour%'";

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
         int i = 0;
        while(!c.isAfterLast())
        {
            if(c.getString(c.getColumnIndex("_menuitemname"))!= null)
            {
                results[i] = c.getString(c.getColumnIndex("_menuitemname"));
            }
            i++;
            c.moveToNext();
        }
        db.close();
        String[] foo = new String[1];
        foo[0] = databaseToString();
        if(results[0] == null)
        {
            return foo;
        }
        return foo;
    }
}
