/**
 * Created by srhemach on 2015-05-01.
 */

package com.enableindia.texttospeechapp;

public class Category {
    public int Category_Id;
    public String Category_Name;
    public int Weight;

    public Category() {
    }

    @Override
    public String toString() {
        return "Category [ID=" + Category_Id
                + ",Name=" + Category_Name
                + ",Weight=" + Weight + "]";
    }
}