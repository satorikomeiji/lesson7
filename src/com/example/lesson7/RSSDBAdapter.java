package com.example.lesson7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
* Created with IntelliJ IDEA.
* User: satori
* Date: 10/31/13
* Time: 8:17 PM
* To change this template use File | Settings | File Templates.
*/

public class RSSDBAdapter {
    public static final String KEY_TITLE = "title";
    public static final String KEY_LINK = "link";
    public static final String KEY_FEED_ID = "feed_id";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DESCRIPTION = "description";


    private static final String TAG = "RSSDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_CHANNELS_TABLE = "channels";
    private static final String DATABASE_ENTRIES_TABLE = "entries";
    private static final int DATABASE_VERSION = 2;

    /**
    * Database creation sql statement
    */
    private static final String DATABASE_CREATE_CHANNELS =
            "create table channels (_id integer primary key autoincrement, "
                    + "title text not null, link text not null);";
    private static final String DATABASE_CREATE_ENTRIES =
            "create table entries (_id integer primary key autoincrement, "
                    + "title text not null, description text not null, feed_id integer, "
                    + " FOREIGN KEY (feed_id) REFERENCES " + DATABASE_CHANNELS_TABLE + " (_id));";


    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_CHANNELS);
            db.execSQL(DATABASE_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CHANNELS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_ENTRIES_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public RSSDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public RSSDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @param link the body of the note
     * @return rowId or -1 if failed
     */
    public long addRSS(String title, String link) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_LINK, link);

        return mDb.insert(DATABASE_CHANNELS_TABLE, null, initialValues);
    }


    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteRSS(long rowId) {

        return mDb.delete(DATABASE_CHANNELS_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllChannels() {

        return mDb.query(DATABASE_CHANNELS_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_LINK}, null, null, null, null, null);
    }
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchChannel(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_CHANNELS_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_LINK}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param link value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateChannel(long rowId, String title, String link) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_LINK, link);
        return mDb.update(DATABASE_CHANNELS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateArticles(ArrayList<Article> articles) {
        mDb.delete(DATABASE_ENTRIES_TABLE, null, null);
        ContentValues args = new ContentValues();
        boolean answer = true;
        for (Article article: articles)   {
            args.put(KEY_TITLE, article.title);
            args.put(KEY_DESCRIPTION, article.description);
            args.put(KEY_FEED_ID, article.id);
            answer = answer && (mDb.insert(DATABASE_ENTRIES_TABLE, null, args) > 0);
            Log.w(TAG, "updating " + article.id + "  " + article.description);
        }
        return answer;
    }

    public Cursor fetchEntriesForId(long id) {
        Log.w(TAG, "fetching Entries For Id");
        return mDb.query(DATABASE_ENTRIES_TABLE, new String[] {KEY_ROWID,
                KEY_TITLE, KEY_DESCRIPTION, KEY_FEED_ID}, KEY_FEED_ID + "=" + id, null,
                null, null, null, null);
    }
}


