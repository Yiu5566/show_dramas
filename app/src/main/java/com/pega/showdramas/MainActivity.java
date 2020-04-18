package com.pega.showdramas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new retrievedata().execute();
    }
}

class retrievedata extends AsyncTask<Void, Void, String> {
    final static String TAG = "retrievedata";

    @Override
    protected String doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        URL url;
        try {
            url = new URL("https://static.linetv.tw/interview/dramas-sample.json");
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET"); //Your method here
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line + "\n");

            if (buffer.length() == 0)
                return null;

            return buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "IO Exception", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if(response != null) {
            JSONObject json = null;
            try {
                json = new JSONObject(response);
                JSONArray dramas = json.getJSONArray("data");
                String tmp = dramas.getJSONObject(0).getString("name");
                Log.i(TAG, "yiu test："+ tmp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}