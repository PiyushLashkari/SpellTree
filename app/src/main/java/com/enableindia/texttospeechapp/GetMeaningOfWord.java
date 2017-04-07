package com.enableindia.texttospeechapp;

/**
 * Created by archds on 6/26/2015.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.example.texttospeechapp.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetMeaningOfWord extends Activity implements
        LoaderManager.LoaderCallbacks<SharedPreferences> {
    private TTSManager ttsManager = null;
    private static String meaning = new String("");
    private static String word;
    private static final String KEY = "prefs";
    private static boolean isSuccess=false;
    private String PREFS_NAME = "TTS_PREFS";

    private TextView textView;
    private String contentID;
    private GestureDetectorCompat gestureDetectorCompat;
    private boolean bUpdateMeaningDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_meaning);
        getActionBar().setTitle("Word Meaning");
        textView = (TextView) findViewById(R.id.textView);
        word = getIntent().getStringExtra("word");
        contentID = getIntent().getStringExtra("content_id");
        gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());
        Log.d("GetMeaningOfWord", "Inside onCreate()");

        //Initiating loader for performing get meaning task
        getLoaderManager().initLoader(0, null, this);
        bUpdateMeaningDB = false;
    }



    @Override
    public Loader<SharedPreferences> onCreateLoader(int id, Bundle args) {
        Log.d("GetMeaningOfWord","onCreateLoader()");
        return (new MeaningLoader(this));
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onLoadFinished(Loader<SharedPreferences> loader,
                               SharedPreferences prefs) {
        Log.d("GetMeaningOfWord", "onLoadFinished()");
        int value = prefs.getInt(KEY, 0);
        value += 1;
        textView.setText(meaning);
        // update value
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY, value);
       // MeaningLoader.persist(editor);
    }

    @Override
    public void onLoaderReset(Loader<SharedPreferences> loader) {
        Log.d("GetMeaningOfWord", "onLoaderReset()");
        // NOT used
    }

    void replay()
    {
        Log.d("GetMeaningOfWord", "Inside replay()");
        if (isSuccess)
        {
            ttsManager.initQueue("Meaning of the word " + word + " is ");
            ttsManager.addQueue(meaning.toString());

            if (bUpdateMeaningDB == false && !meaning.equals("") && !contentID.equals("")) {
                // Updating the DB
                EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(GetMeaningOfWord.this.getBaseContext());
                mDbHelper.open();

                mDbHelper.updateMeaning(contentID, meaning.toString());
                mDbHelper.close();

                bUpdateMeaningDB = true;
            }
        } else {
            ttsManager.initQueue("Unable to get the meaning of the word " + word);
            //ttsManager.addQueue(meaning);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //LoadMeaningTask task = new LoadMeaningTask();
        //task.execute();

        SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        ttsManager = new TTSManager();
        ttsManager.init(this);
        String pitch=sharedpreferences.getString("pitch", "0.7");
        String rate=sharedpreferences.getString("rate", "0.7");
        Log.d("LearnMode", "Pitch is " + pitch);
        Log.d("LearnMode", "Rate is " + rate);
        ttsManager.setTtsPitch(Float.parseFloat(pitch));
        ttsManager.setTtsRate(Float.parseFloat(rate));
        Log.d("Learn Mode", "TTS Manager initialised");


        Button playButton=(Button)findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                replay();
            }
        });

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Log.d("Event", "done ");

        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent ev) {
            Log.d("DoubleTap", "done ");
            ttsManager.initQueue(meaning.toString());
            replay();
            return true;
        }



    }

    @Override
    public void onPause()
    {
        super.onPause();

        if ( ttsManager != null ) {
            ttsManager.shutDown();
        }
    }

    /**
     * Releases the resources used by the TextToSpeech engine.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if ( ttsManager != null ) {
            ttsManager.shutDown();
        }
    }



    public static class MeaningLoader extends AsyncTaskLoader<SharedPreferences>
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SharedPreferences prefs = null;
       StringBuilder meaningOfWord=new StringBuilder("");
        public void persist(final SharedPreferences.Editor editor) {
            editor.apply();
        }

        public MeaningLoader(Context context) {
            super(context);
        }

        // Load the data asynchronously
        @Override
        public SharedPreferences loadInBackground() {
            Log.d("MeaningLoader","Inside loadInBackground()");
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            prefs.registerOnSharedPreferenceChangeListener(this);
            StringBuffer response=new StringBuffer();
            int responseCode=1;
            String responseMessage = new String(" ");

            try {
                String url = "http://www.dictionaryapi.com/api/v1/references/sd3/xml/" + word + "?key=" + java.net.URLEncoder.encode("c6a748cd-fc58-4511-bc9e-a4852b267e93", "UTF-8");
                url = url.replaceAll(" ", "%20");
                URL obj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("content-type", "text/xml");
                responseCode = connection.getResponseCode();
                responseMessage = connection.getResponseMessage();

                Log.d("GetMeaningOfWord", "Sending 'GET' request to URL : " + url);
                Log.d("GetMeaningOfWord", "Response Code is " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                connection.disconnect();
                System.out.println("Response is " + response);
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                 e.printStackTrace();
                meaningOfWord= new StringBuilder("IOException occurred while sending request");
            }


            //We are using Jackson JSON parser to deserialize the JSON. See http://wiki.fasterxml.com/JacksonHome
            //Feel free to use which ever library you prefer.
            if(responseCode==200)

            {
                try {
                    isSuccess=true;
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(response.toString()));
                    Document document = db.parse(is);

                    NodeList nodeLst = document.getElementsByTagName("entry");
                    Log.d("MeaningLoader", "Nodelist length is " + nodeLst.getLength());
                    int ctr = 0;
                    System.out.println("List 1 is " + nodeLst.getLength());
                    int len = nodeLst.getLength();
                    if(len==0) {
                        isSuccess = false;
                        meaningOfWord = meaningOfWord.append("Meaning of the word is not present in the dictionary");
                    }
                    for (int i = 0; i < len; i++) {
                        NodeList nodeLst2 = nodeLst.item(i).getChildNodes();
                        //System.out.println("List2 is "+nodeLst2.getLength());
                        for (int ii = 0; ii < nodeLst2.getLength(); ii++) {
                            if (nodeLst2.item(ii).getNodeName().equals("def")) {
                                NodeList nodeLst3 = nodeLst2.item(ii).getChildNodes();
                                for (int iii = 0; iii < nodeLst3.getLength(); iii++)
                                    if (nodeLst3.item(iii).getNodeName().equals("dt")) {
                                        Element e = (Element) nodeLst3.item(iii);
                                        ctr++;
                                        //System.out.println(e.getTextContent());
                                        meaningOfWord = meaningOfWord.append((ctr) + ". " + e.getTextContent().substring(1, 2).toUpperCase().concat(e.getTextContent().substring(2)) + "\n");
                                        // Log.d("GetMeaningOfWord", "Inside onPostExecute(), meaning is "+meaning);

                                        break;
                                    }
                            }

                        }
                    }
                } catch (SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    isSuccess=false;
                    meaningOfWord= new StringBuilder("SAXException Occured");
                } catch (ParserConfigurationException e) {
                    // TODO Auto-generated catch block
                    isSuccess=false;
                    e.printStackTrace();
                    meaningOfWord= new StringBuilder("ParserConfigurationException Occured");
                } catch (IOException e) {
                    isSuccess=false;
                    e.printStackTrace();
                    meaningOfWord= new StringBuilder("IOException Occured");
                }
            }
         else
            {
                meaningOfWord=new StringBuilder("ERROR \n".concat(responseMessage.toString()).toString());
                meaningOfWord.append("Contact admin");

            }

           Log.d("MeaningLoader" ,"Meaning is "+meaning);
           //   return meaning;
            meaning=meaningOfWord.toString();
            return (prefs);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            Log.d("MeaningLoader", "onSharedPreferencesChanged()");
            // notify loader that content has changed
            onContentChanged();
        }


        /**
         * starts the loading of the data
         * once result is ready the onLoadFinished method is called
         * in the main thread. It loader was started earlier the result
         * is return directly

         * method must be called from main thread.
         */

        @Override
        protected void onStartLoading() {
            Log.d("MeaningLoader", "onStartLoading()");
            if (prefs != null) {
                deliverResult(prefs);
            }

            if (takeContentChanged() || prefs == null) {
                forceLoad();
            }
        }
    }}