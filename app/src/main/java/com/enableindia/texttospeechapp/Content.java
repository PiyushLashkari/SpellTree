package com.enableindia.texttospeechapp;

/**
 * Created by srhemach on 2015-07-08.
 */
public class Content {
    public int Content_Id;
    public String Content_String;
    public String Content_Example;
    public int Subcategory_Id;
    public String Content_Meaning;

    public Content() {
    }

    @Override
    public String toString() {
        return "Content [ID=" + Content_Id
                + ",String=" + Content_String
                + ",Example=" + Content_Example
                + ",SubCategoryId=" + Subcategory_Id
                + ",Meaning=" + Content_Meaning + "]";
    }
}
