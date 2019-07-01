package com.mobile.smsforwarder;

import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //information of database
    public  static final int DATABASE_VERSION = 1;
    public  static final String DATABASE_NAME = "smsforwarder_db.db";

    //table relation
    public  static final String TABLE_RELATION = "relation";
    public  static final String RELATION_ID = "id";
    public  static final String RELATION_NAME = "name";
    public  static final String RELATION_GENDATE = "gendate";

    //table number
    public  static final String TABLE_NUMBER = "number";
    public  static final String NUMBER_ID = "id";
    public  static final String NUMBER_NAME = "name";
    public  static final String NUMBER_DIGITS = "digits";
    public  static final String NUMBER_TYPE = "type";
    public  static final String NUMBER_GENDATE = "gendate";
    public  static final String NUMBER_RELATION_ID = "relation_id";

    //table mail
    public  static final String TABLE_MAIL = "mail";
    public  static final String MAIL_ID = "id";
    public  static final String MAIL_NAME = "name";
    public  static final String MAIL_ADRESS = "adress";
    public  static final String MAIL_GENDATE = "gendate";
    public  static final String MAIL_RELATION_ID = "relation_id";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRelationTable =
                "CREATE TABLE " + TABLE_RELATION + "(" +
                        RELATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        RELATION_NAME + " TEXT UNIQUE," +
                        RELATION_GENDATE + " TEXT)";

        String createNumberTable =
                "CREATE TABLE " + TABLE_NUMBER + "(" +
                        NUMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        NUMBER_NAME + " TEXT NOT NULL," +
                        NUMBER_DIGITS + " TEXT NOT NULL," +
                        NUMBER_TYPE + " TEXT NOT NULL," +
                        NUMBER_GENDATE + " TEXT," +
                        NUMBER_RELATION_ID + " INTEGER," +
                        "FOREIGN KEY(\"relation_id\") REFERENCES \"relation\"(\"id\") )";

        String createMailTable =
                "CREATE TABLE " + TABLE_MAIL + "(" +
                        MAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MAIL_NAME + " TEXT NOT NULL," +
                        MAIL_ADRESS + " TEXT NOT NULL," +
                        MAIL_GENDATE + " TEXT," +
                        MAIL_RELATION_ID + " INTEGER," +
                        "FOREIGN KEY(\"relation_id\") REFERENCES \"relation\"(\"id\") )";



        db.execSQL(createRelationTable);
        db.execSQL(createNumberTable);
        db.execSQL(createMailTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RELATION);
        onCreate(db);
    }

    public long insertStudent(){
        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_NAME,name);
//        contentValues.put(COLUMN_SURNAME,surname);
//        contentValues.put(COLUMN_AGE,age);
//
//        return db.insert(TABLE_NAME,null,contentValues);
        return 0;

    }

}
