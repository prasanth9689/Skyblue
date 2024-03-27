package com.skyblue.skybluea.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "skyblue_channel";
    static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "channel";
    public static final String ID_COL = "id";
    public static final String USER_ID_COL = "user_id";
    public static final String CHANNEL_ID_COL = "chnnel_id";
    public static final String CHANNEL_NAME_COL = "channel_name";
    public static final String CHANNEL_CREATED_DATE = "created_date";


        // Creating table query
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID_COL + " TEXT,"
                + CHANNEL_ID_COL + " TEXT,"
                + CHANNEL_NAME_COL + " TEXT,"
                + CHANNEL_CREATED_DATE + " TEXT)";

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
