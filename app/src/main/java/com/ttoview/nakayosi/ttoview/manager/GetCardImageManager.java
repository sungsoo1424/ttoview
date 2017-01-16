package com.ttoview.nakayosi.ttoview.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sungs on 2016-09-19.
 */
public class GetCardImageManager extends AsyncTask<String, Void, Bitmap>  {
    private Context mContext;
    ProgressDialog dialog;
    String imgUrl = "http://ttoview.com/images/card/";
    Bitmap bmImg;

    public GetCardImageManager(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(mContext, "","카드 로딩중 입니다..", true);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try{
            imgUrl+=params[0]+".png";
            Log.d("test",imgUrl);
            URL myFileUrl = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bmImg = BitmapFactory.decodeStream(is);
            conn.disconnect();
        }catch(Exception e){

            Log.d("test",e.toString());

        }
        return bmImg;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        dialog.dismiss();
        Log.d("test","사진 가져오기 완료");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }
}