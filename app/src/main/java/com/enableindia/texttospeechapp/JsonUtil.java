package com.enableindia.texttospeechapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by srhemach on 2015-07-14.
 */
public class JsonUtil {

    public static String toJSon(Result result) {
        try {
            // Here we convert Java Object to JSON
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(Result.TAG_NUM_QUESTIONS, Integer.toString(result.nNumQuestions));
            jsonObj.put(Result.TAG_NUM_QUESTIONS_ANSWERED, Integer.toString(result.nNumQuestionsAttended));
            jsonObj.put(Result.TAG_NUM_CORRECT_ANSWERS, Integer.toString(result.nNumCorrectAns));
            jsonObj.put(Result.TAG_SCORE, Integer.toString(result.nScore));

            // we need a json array to hold the list of wrong answers
            if (result.lstWrongAnswers.size() > 0 ) {
                // In this case we need a json array to hold the java list
                JSONArray jsonArr = new JSONArray();

                for (int i = 0; i < result.lstWrongAnswers.size(); i++) {
                    JSONObject wrObj = new JSONObject();
                    wrObj.put(Result.TAG_CONTENT_ID, Integer.toString(result.lstWrongAnswers.get(i).nContentId));
                    wrObj.put(Result.TAG_WRONG_ANSWER, result.lstWrongAnswers.get(i).sWrongAnswer);
                    jsonArr.put(wrObj);
                }

                jsonObj.put(Result.TAG_WRONG_ANSWERS, jsonArr);
            }

            return jsonObj.toString();

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return null;

    }
}