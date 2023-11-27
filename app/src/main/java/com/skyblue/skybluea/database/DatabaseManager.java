package com.skyblue.skybluea.database;

import static com.skyblue.skybluea.database.DatabaseHelper.TABLE_NAME;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.skyblue.skybluea.model.ChannelsModel;

import java.util.ArrayList;

public class DatabaseManager {

    private Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context c){
        context = c;
    }

    public DatabaseManager open() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        return this;
    }

    public void saveChannelListFromServer(ArrayList<String> channelNameArray,
                                          ArrayList<String> channelIdArray,
                                          ArrayList<String> channelCreatedDateArray){
    //    SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int channalNameSize = channelNameArray.size();
     //   database.beginTransaction();

        String INSERT = "insert into "
                + TABLE_NAME+ " (" + DatabaseHelper.CHANNEL_NAME_COL + "," +
                                     DatabaseHelper.CHANNEL_ID_COL +  "," +
                                     DatabaseHelper.CHANNEL_CREATED_DATE + ") values (?,?,?)";


        SQLiteStatement insert = database.compileStatement(INSERT);

        for (int i = 0; i < channalNameSize; i++) {

            insert.bindString(1, channelNameArray.get(i));
            insert.bindString(2, channelIdArray.get(i));
            insert.bindString(3, channelCreatedDateArray.get(i));
            insert.executeInsert();
        }

   //     database.setTransactionSuccessful();

//        SQLiteDatabase db = databaseHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(DatabaseHelper.CHANNEL_NAME_COL, channelName);
//
//        db.insert(TABLE_NAME, null, values);
    }

    public ArrayList<ChannelsModel> loadCahnnels(){
        ArrayList<ChannelsModel> arrayList = new ArrayList<>();

        String select_query= "SELECT *FROM " + TABLE_NAME;

        Cursor cursor = database.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChannelsModel channels = new ChannelsModel();
                channels.setId(cursor.getString(0));
                channels.setUserId(cursor.getString(1));
                channels.setChannelId(cursor.getString(2));
                channels.setChannelName(cursor.getString(3));
                channels.setChannelCreateDate(cursor.getString(4));
                arrayList.add(channels);
            }while (cursor.moveToNext());
        }
        return arrayList;
    }

    public void deleteById(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID_COL + "=" + _id, null);
    }

    public void deleteAllChannel() {
        database.delete(DatabaseHelper.TABLE_NAME, null, null);
    }
}
