package com.enableindia.texttospeechapp;

import java.util.List;

/**
 * Created by srhemach on 2015-07-14.
 */
public class Result {

    // JSON Node names
    public static final String TAG_NUM_QUESTIONS = "numQuestions";
    public static final String TAG_NUM_QUESTIONS_ANSWERED = "numQuestionsAnswered";
    public static final String TAG_NUM_CORRECT_ANSWERS = "numCorrectAns";
    public static final String TAG_SCORE = "score";
    public static final String TAG_WRONG_ANSWERS = "wrongAnswers";
    public static final String TAG_CONTENT_ID = "contentId";
    public static final String TAG_WRONG_ANSWER = "wrongAnswer";

    // TODO: Make the members private and implement get/set methods.
    public Integer nNumQuestions;
    public Integer nNumQuestionsAttended;
    public Integer nNumCorrectAns;
    public Integer nScore;
    public List<WrongAnswer> lstWrongAnswers;

    public class WrongAnswer {
        public Integer nContentId;
        public String  sWrongAnswer;
    }
}