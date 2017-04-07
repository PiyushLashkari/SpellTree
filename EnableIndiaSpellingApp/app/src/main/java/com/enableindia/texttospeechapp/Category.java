/**
 * Created by srhemach on 2015-05-01.
 */

package com.cisco.texttospeechapp;

class Category {
    public int id;
    public String name;
    public int weight;

    public Category() {
    }

    @Override
    public String toString() {
        return "Category [id=" + id
                + ",name=" + name
                + ",weight=" + weight + "]";
    }
}