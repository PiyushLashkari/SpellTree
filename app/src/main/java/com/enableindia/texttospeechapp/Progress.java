package com.enableindia.texttospeechapp;

/**
 * Created by srhemach on 2015-07-08.
 */
public class Progress {
    public int Test_Id;
    public String Student_Id;
    public String Timestamp;
    public String Result;
    public String Exercise_Id_List;
    public String Progress_Type;

    public Progress() {
    }

    @Override
    public String toString() {
        return "Progress [ID=" + Test_Id
                + ",String=" + Student_Id
                + ",Example=" + Progress_Type
                + ",SubCategoryId=" + Result
                + ",Meaning=" + Exercise_Id_List + "]";
    }

}
