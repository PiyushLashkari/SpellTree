/**
 * Created by srhemach on 2015-05-01.
 */

package com.cisco.texttospeechapp;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cisco.texttospeechapp.EnableIndiaDB;

public class EnableIndiaDataAdapter {
    private static final String TAG = "EnableIndiaDataAdapter";

    private SQLiteDatabase mDb;
    private EnableIndiaDB mDbHelper;

    private static String TABLE_CATEGORY = "Category";
    public static String TABLE_CONTENTS = "Contents";
    public static String TABLE_LEARNING_MAP = "Learning_Mapping";
    public static String TABLE_PRACTICE_TEST_MAP = "Practice_Test_Mapping";
    public static String TABLE_PROGRESS = "Progress";
    public static String TABLE_STUDENTS = "Students";

    public EnableIndiaDataAdapter(Context context) {
        Context mContext = context;
        mDbHelper = new EnableIndiaDB(mContext);
    }

    public EnableIndiaDataAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public EnableIndiaDataAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    //
    // PUBLIC METHODS TO ACCESS DB CONTENT
    //

    public Cursor getTestData() {
        try {
            String sql = "SELECT * FROM myTable";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    // Get Categories
    public List<Category> getCategories() {

        List<Category> categories = null;

        try {

            String query = "SELECT * FROM " + TABLE_CATEGORY;
            Cursor mCur = mDb.rawQuery(query, null);

            // go over each row, build elements and add it to list
            categories = new LinkedList<Category>();
            if (mCur != null) {
                if (mCur.moveToFirst()) {
                    do {

                        Category category = new Category();
                        category.id = Integer.parseInt(mCur.getString(0));
                        category.name = mCur.getString(1);
                        category.weight = Integer.parseInt(mCur.getString(2));

                        categories.add(category);

                    } while (mCur.moveToNext());
                }
            }

        } catch (Exception e) {
            // sql error
        }

        return categories;
    }
}