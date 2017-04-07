/**
 * Created by srhemach on 2015-05-01.
 */

package com.enableindia.texttospeechapp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.enableindia.texttospeechapp.EnableIndiaDB;

public class EnableIndiaDataAdapter {
    private static final String TAG = "EnableIndiaDataAdapter";

    private SQLiteDatabase mDb;
    private EnableIndiaDB mDbHelper;

    private static int nTestId = 20000;

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
            mDb = mDbHelper.getWritableDatabase();
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
                        category.Category_Id = Integer.parseInt(mCur.getString(0));
                        category.Category_Name = mCur.getString(1);
                        category.Weight = Integer.parseInt(mCur.getString(2));

                        categories.add(category);

                    } while (mCur.moveToNext());
                }
            }

        } catch (Exception e) {
            // sql error
        }

        return categories;
    }

    // Get Exercises corresponding to the Category
    public List<Exercise> getExercises(String sCategory) {

        List<Exercise> exercises = null;

        try {

            String query = "SELECT * FROM " + TABLE_LEARNING_MAP + " WHERE Category_Id = " +
                           sCategory;
            Cursor mCur = mDb.rawQuery(query, null);

            // go over each row, build elements and add it to list
            exercises = new LinkedList<Exercise>();
            if (mCur != null) {
                if (mCur.moveToFirst()) {
                    do {
                        Exercise exercise = new Exercise();
                        exercise.SubCategory_Id = Integer.parseInt(mCur.getString(0));
                        exercise.SubCategory_Name = mCur.getString(1);
                        exercise.Category_Id = Integer.parseInt(mCur.getString(2));

                        exercises.add(exercise);

                    } while (mCur.moveToNext());
                }
            }

        } catch (Exception e) {
            // sql error
        }

        return exercises;
    }

    // Get Exercises corresponding to the SubCategory/Module
    public List<Content> getTestContentForCategory(String sCategory) {

        List<Content> contents = null;
        // go over each row, build elements and add it to list
        contents = new LinkedList<Content>();

        try {

            List<Exercise> lstExercises = getExercises(sCategory);

            for (int i = 0; i < lstExercises.size(); i++) {

                String query = "SELECT * FROM " + TABLE_CONTENTS + " WHERE Subcategory_Id = " +
                        lstExercises.get(i).SubCategory_Id;
                Cursor mCur = mDb.rawQuery(query, null);

                if (mCur != null) {
                    if (mCur.moveToFirst()) {
                        do {
                            Content content = new Content();
                            content.Content_Id = Integer.parseInt(mCur.getString(0));
                            content.Content_String = mCur.getString(1);
                            content.Content_Example = mCur.getString(2);
                            content.Subcategory_Id = Integer.parseInt(mCur.getString(3));
                            content.Content_Meaning = mCur.getString(4);

                            contents.add(content);

                        } while (mCur.moveToNext());
                    }
                }
            }
        } catch (Exception e) {
            // sql error
            e.printStackTrace();
        }

        if ( contents.size() > 0 ) {
            Collections.shuffle(contents);
        }

        if ( contents.size() > 10 ) {
            return contents.subList(0,10);
        } else {
            return contents;
        }
    }


    // Get Exercises corresponding to the SubCategory/Module
    public List<Content> getContent(String sSubCategory) {

        List<Content> contents = null;

        try {

            String query = "SELECT * FROM " + TABLE_CONTENTS + " WHERE Subcategory_Id = " +
                    sSubCategory;
            Cursor mCur = mDb.rawQuery(query, null);

            // go over each row, build elements and add it to list
            contents = new LinkedList<Content>();
            if (mCur != null) {
                if (mCur.moveToFirst()) {
                    do {
                        Content content = new Content();
                        // Init
                        content.Content_Id = 0;
                        content.Content_String = "";
                        content.Content_Example = "";
                        content.Subcategory_Id = 0;
                        content.Content_Meaning = "";

                        content.Content_Id = Integer.parseInt(mCur.getString(0));
                        content.Content_String = mCur.getString(1);
                        // TODO: Handle 'null' content.
                        content.Content_Example = mCur.getString(2);
                        content.Subcategory_Id = Integer.parseInt(mCur.getString(3));
                        content.Content_Meaning = mCur.getString(4);

                        contents.add(content);

                    } while (mCur.moveToNext());
                }
            }

        } catch (Exception e) {
            // sql error
            e.printStackTrace();
        }

        return contents;
    }


    // Update Meaning of content.
    public void updateMeaning(String contentID, String sMeaning) {
        String sQuery = "";

        try {
            ContentValues row = new ContentValues();
            row.put("Content_Meaning", sMeaning);

            sQuery = "Content_Id=" + contentID;

            long id = mDb.update(TABLE_CONTENTS, row, sQuery, null);
        } catch (Exception e) {
            // sql error
            e.printStackTrace();
        }
    }

    // Write Test/Learn Result.
    public void writeTestScore(String sCategory, String sTestResult, String sProgressType) {

        try {
// set the format to sql date time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            String sTime = dateFormat.format(date);

            ContentValues row = new ContentValues();
            row.put("Test_Id", nTestId++);
            row.put("Student_Id", "student.test@enable-india.com");
            row.put("Timestamp", sTime);
            row.put("Result", sTestResult);
            row.put("Exercise_Id_List", sCategory);
            row.put("Progress_Type", sProgressType);

            long id = mDb.insert(TABLE_PROGRESS, null, row);
        } catch (Exception e) {
            // sql error
            e.printStackTrace();
        }

    }
    // Get Exercises corresponding to the SubCategory/Module
    public List<Progress> getProgress() {

        List<Progress> progress = null;

        try {

            String query = "SELECT * FROM " + TABLE_PROGRESS;
            Cursor mCur = mDb.rawQuery(query, null);

            // go over each row, build elements and add it to list
            progress = new LinkedList<Progress>();
            if (mCur != null) {
                if (mCur.moveToFirst()) {
                    do {
                        Progress progress1 = new Progress();
                        progress1.Test_Id = Integer.parseInt(mCur.getString(0));
                        //TODO: This is very Crude. Change the schema so that the TestID is string
                        // and ensure that it is unique.
                        nTestId = progress1.Test_Id;
                        nTestId++;
                        progress1.Student_Id = mCur.getString(1);
                        progress1.Timestamp = mCur.getString(2);
                        progress1.Result = mCur.getString(3);
                        progress1.Exercise_Id_List = mCur.getString(4);
                        progress1.Progress_Type = mCur.getString(5);

                        progress.add(progress1);

                    } while (mCur.moveToNext());
                }
            }

        } catch (Exception e) {
            // sql error
            e.printStackTrace();
        }

        return progress;
    }


}