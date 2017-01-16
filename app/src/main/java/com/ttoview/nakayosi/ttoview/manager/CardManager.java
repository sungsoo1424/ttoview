package com.ttoview.nakayosi.ttoview.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ttoview.nakayosi.ttoview.model.HttpRequestResult;

/**
 * Created by sungs on 2016-11-23.
 */

public class CardManager extends AsyncTask<String, Void, HttpRequestResult> {

    Context mContext;
    ProgressDialog dialog;
    String method;

    public CardManager(Context context) {
        this.mContext = context;
    }


    @Override
    protected HttpRequestResult doInBackground(String... strings) {

        HttpRequestResult result = null;
        method = strings[0];
        result = registCard(strings[1]);
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(mContext, "", "버전 확인중 입니다..", true);
    }

    @Override
    protected void onPostExecute(HttpRequestResult s) {
        super.onPostExecute(s);
        dialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    private HttpRequestResult registCard(String url) {
        BufferedReader bufferedReader = null;
        HttpURLConnection urlConnection = null;
        StringBuilder sb = new StringBuilder();
        HttpRequestResult result = new HttpRequestResult();
        String json;

        try {
            URL urlToRequest = new URL(url);
            Log.d("test2","URL : "+url);
            Log.d("test2","Method : "+method);

            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestProperty("Accept", "application/json");
            switch (method){
                case "POST" :  urlConnection.setDoOutput(true); urlConnection.setDoInput(true); break;
                case "GET" :  urlConnection.setDoInput(true); break;
                case "PUT" :  urlConnection.setDoInput(true); break;
            }
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestMethod(method);
            urlConnection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");
            }

            result.setResultCode(urlConnection.getResponseCode());
            result.setResultJson(new JSONObject(sb.toString()));

        } catch (Exception e) {
            Log.d("test3", e.toString());
        }
        urlConnection.disconnect();
        return result;
    }
}
