package com.skyblue.skybluea.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.skyblue.skybluea.download.DownloadModel;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "skybluedb";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "downloads";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our course name column
    private static final String FILE_NAME_COL = "file_name";

    // below variable is for our file name size column
    private static final String FILE_SIZE_COL = "file_size";

    // below variable is for our file name size column
    private static final String FILE_DOWNLOADED_DATE_COL = "download_date";

    // below variable is for our file download status column
    private static final String FILE_DOWNLOADED_STATUS_COL = "download_status";

    private static final int FILE_READY_TO_DOWNLOAD = 0;
    private static final int FILE_DOWNLOAD_COMPLETED = 1;
    private static final int FILE_DOWNLOAD_PAUSED = 2;
    private static final int FILE_DOWNLOAD_ERROR = 3;

    private static final String FILE_DOWNLOAD_URL_COL = "file_url";

    private static final String FILE_DOWNLOAD_PATH_COL = "file_path";

    private static final String FILE_VIDEO_THUMBNAIL_COL = "thumbnail";

    Context context;

    // creating a constructor for our database handler.
    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FILE_NAME_COL + " TEXT,"
                + FILE_SIZE_COL + " TEXT,"
                + FILE_DOWNLOADED_DATE_COL + " TEXT,"
                + FILE_DOWNLOADED_STATUS_COL + " TEXT,"
                + FILE_DOWNLOAD_URL_COL + " TEXT,"
                + FILE_VIDEO_THUMBNAIL_COL + " TEXT,"
                + FILE_DOWNLOAD_PATH_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addNewDownload(String fileName, int fileSize, String timeDate, int downloadStatus, String fileUrl , String filePath) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(FILE_NAME_COL, fileName);
        values.put(FILE_SIZE_COL, fileSize);
        values.put(FILE_DOWNLOADED_DATE_COL, timeDate);
        values.put(FILE_DOWNLOADED_STATUS_COL, downloadStatus);
        values.put(FILE_DOWNLOAD_URL_COL, fileUrl);
        values.put(FILE_DOWNLOAD_PATH_COL, filePath);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);


        // at last we are closing our
        // database after adding database.
        db.close();
    }

    //get the all notes
    public ArrayList<DownloadModel> getDownloads() {
        ArrayList<DownloadModel> arrayList = new ArrayList<>();

        // select all query
        String select_query= "SELECT *FROM " + TABLE_NAME;

        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DownloadModel downloadModel = new DownloadModel();
                downloadModel.setId(cursor.getString(0));
                downloadModel.setFileName(cursor.getString(1));
                downloadModel.setFileSize(cursor.getString(2));
                downloadModel.setFileDownloadDate(cursor.getString(3));
                downloadModel.setFilePath(cursor.getString(7));
                arrayList.add(downloadModel);
            }while (cursor.moveToNext());
        }
        return arrayList;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


}
