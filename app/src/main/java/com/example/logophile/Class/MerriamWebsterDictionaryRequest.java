package com.example.logophile.Class;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

public class MerriamWebsterDictionaryRequest extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate;

    public MerriamWebsterDictionaryRequest(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... word) {
        try {
            URL url = new URL((word[0]));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // Store the output into a buffer from the server get request
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

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONArray rootArray = new JSONArray(result);
            JSONArray definition = rootArray.getJSONObject(0).getJSONArray("shortdef");
            delegate.processFinished(definition.getString(0).substring(0,1).toUpperCase() + definition.getString(0).substring(1));
        } catch (JSONException e) {
            delegate.processFinished("Definition not found");
            e.printStackTrace();
        }
    }

    public interface AsyncResponse {
        void processFinished(String output);
    }

}
