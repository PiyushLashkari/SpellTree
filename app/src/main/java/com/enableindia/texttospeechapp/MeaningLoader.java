package com.enableindia.texttospeechapp;

/**
 * Created by archds on 7/18/2015.
 */
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MeaningLoader extends AsyncTaskLoader<SharedPreferences>
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences prefs = null;
    StringBuilder meaning=new StringBuilder(" ");

    public static void persist(final SharedPreferences.Editor editor) {
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




        HttpURLConnection connection = null;
        String response = new String();
        Document document = null;

        //We are using Jackson JSON parser to deserialize the JSON. See http://wiki.fasterxml.com/JacksonHome
        //Feel free to use which ever library you prefer.
        ObjectMapper mapper = new ObjectMapper();

     //   Log.d("MeaningLoader", "Inside loadInBackground()");

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            InputSource is = new InputSource(new StringReader(response));
            document = db.parse("http://www.dictionaryapi.com/api/v1/references/sd3/xml/" + "apple" + "?key=c6a748cd-fc58-4511-bc9e-a4852b267e93");
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            NodeList nodeLst = document.getElementsByTagName("entry");
            Log.d("MeaningLoader", "Nodelist length is " + nodeLst.getLength());
            int ctr = 0;
            System.out.println("List 1 is " + nodeLst.getLength());
            int len = nodeLst.getLength();
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
                                meaning = meaning.append((ctr) + ". " + e.getTextContent().substring(1, 2).toUpperCase().concat(e.getTextContent().substring(2)) + "\n");
                               // Log.d("GetMeaningOfWord", "Inside onPostExecute(), meaning is "+meaning);

                                break;
                            }
                    }

                }
            }
        } catch (Exception e) {
            meaning= new StringBuilder("ParseError");
        }

        //   connection.disconnect();
        Log.d("MeaningLoader" ,"Meaning is "+meaning);

     //   return meaning;


       return (prefs);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Log.d("MeaningLoader","onSharedPreferencesChanged()");
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
        Log.d("MeaningLoader","onStartLoading()");
        if (prefs != null) {
            deliverResult(prefs);
        }

        if (takeContentChanged() || prefs == null) {
            forceLoad();
        }
    }
}