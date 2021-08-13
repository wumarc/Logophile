package com.example.logophile.Class;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class OxfordDictionaryRequest extends AsyncTask<String, Void, String> {

    final String app_id = "4e70043f";
    final String app_key = "33b360fc6c382e1de7b73cfb52d18061";

    public OxfordDictionaryRequest(AsyncResponse delegate) {
        this.delegate = delegate; //don't need context
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("app_id",app_id);
            urlConnection.setRequestProperty("app_key",app_key);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public interface AsyncResponse {
        void processFinished(String output);
    }

    public AsyncResponse delegate;

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONArray results = jsonObject.getJSONArray("results");
            JSONObject lEntries = results.getJSONObject(0);

            JSONArray laArray = lEntries.getJSONArray("lexicalEntries");
            JSONObject entries = laArray.getJSONObject(0);

            JSONArray e = entries.getJSONArray("entries");
            JSONObject jsonObject1 = e.getJSONObject(0);

            JSONArray sensesArray = jsonObject1.getJSONArray("senses");
            JSONObject d = sensesArray.getJSONObject(0);

            JSONArray de = d.getJSONArray("definitions");
            delegate.processFinished(de.getString(0));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}