package com.ttoview.nakayosi.ttoview.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.json.JSONObject;

/**
 * Created by sungs on 2016-10-25.
 */
public class UrlShotnerManager extends AsyncTask<String, Void, String> {
    public static final String SHORTENER_URL = "https://www.googleapis.com/urlshortener/v1/url?key=";
    public static final String API_KEY = "AIzaSyA4cue8D26H0LsM1EMzZGIYsoE9uk2nvVY";


    private Context mContext;
    ProgressDialog dialog;

    public UrlShotnerManager(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(mContext, "", "URL 단축중 입니다..", true);
    }

    @Override
    protected String doInBackground(String... params) {

        String shortUrl = getShortenUrl(params[0]);
        return shortUrl;
    }

    @Override
    protected void onPostExecute(String url) {
        super.onPostExecute(url);
        dialog.dismiss();
        Log.d("test", "URL 단축 완료");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }

    public static String getShortenUrl(String originalUrl) {

        System.out.println("[DEBUG] INPUT_URL : " + originalUrl);
        Log.d("test2","[DEBUG] INPUT_URL : " + originalUrl);
        String resultUrl = originalUrl;
        String originalUrlJsonStr = "{\"longUrl\":\"" + originalUrl + "\"}";
        System.out.println("[DEBUG] INPUT_JSON : " + originalUrlJsonStr);
        Log.d("test2","[DEBUG] INPUT_JSON : " + originalUrlJsonStr);
        URL url = null;
        HttpURLConnection connection = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        StringBuffer sb = null; // Google의 단축URL서비스 결과 JSON String Data
        JSONObject jsonObj = null; // 결과 JSON String Data로 생성할 JSON Object
        try {
            url = new URL(SHORTENER_URL + API_KEY);
            System.out.println("[DEBUG] DESTINATION_URL : " + url.toString());
            Log.d("test2","[DEBUG] DESTINATION_URL : " + url.toString());

        } catch (Exception e) {
            System.out.println("[ERROR] URL set Failed");
            Log.d("test2","[ERROR] URL set Failed"+e.toString());
            return resultUrl;
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "toolbar");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
        } catch (Exception e) {
            System.out.println("[ERROR] Connection open Failed");
            Log.d("test2","[ERROR] Connection open Failed"+e.toString());
            e.printStackTrace();
            return resultUrl;
        }
        try {
            osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(originalUrlJsonStr);
            osw.flush();
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            sb = new StringBuffer();
            String buf = "";
            while ((buf = br.readLine()) != null) {
                sb.append(buf);
            }
            System.out.println("[DEBUG] RESULT_JSON_DATA : " + sb.toString());
            Log.d("test2","[DEBUG] RESULT_JSON_DATA : " + sb.toString());
            jsonObj = new JSONObject(sb.toString());
            resultUrl = jsonObj.getString("id");

        } catch (Exception e) {
            System.out.println("[ERROR] Result JSON Data(From Google) set JSONObject Failed");
            Log.d("test2","[ERROR] Result JSON Data(From Google) set JSONObject Failed" + e.toString());
            e.printStackTrace();
            return resultUrl;
        } finally {
            if (osw != null) try {
                osw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("[DEBUG] RESULT_URL : " + resultUrl);
        Log.d("test2","[DEBUG] RESULT_URL : " + resultUrl);

        return resultUrl;
    }
}
