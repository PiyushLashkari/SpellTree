package com.enableindia.texttospeechapp;

/**
 * Created by srhemach on 2015-07-08.
 */
public class Exercise {
    public int SubCategory_Id;
    public String SubCategory_Name;
    public int Category_Id;

    public Exercise() {
    }

    @Override
    public String toString() {
        return "SubCategory [ID=" + SubCategory_Id
                + ",Name=" + SubCategory_Name
                + ",CategoryID=" + Category_Id + "]";
    }
}
