package com.ttoview.nakayosi.ttoview;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.ttoview.nakayosi.ttoview.activity.WriteActivity;

@SuppressLint("ShowToast")
public class BlogViewActivity extends BaseActivity {
    static int back_push;
    static long touchTime;
    WebView Viewer;
    Button Bu_copy;
    Button backButton;
    TextView tv;
    final String url = "http://blog.naver.com";
    final String filiterurl = "http://m.blog.naver.com/HotTopicList.nhn";
    private final String packname = "com.nhn.android.blog";

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_blog);

        Viewer = (WebView) findViewById(R.id.web);
        Viewer.setWebViewClient(new MyWebClient());
        Viewer.getSettings().setJavaScriptEnabled(true);
        Viewer.loadUrl(url);
/*		Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://blog.naver.com"));
        startActivity(intent);*/
/*		Intent intent = getPackageManager().getLaunchIntentForPackage(packname);
        startActivity(inten1t);*/

        Bu_copy = (Button) findViewById(R.id.URLcopy);
        backButton = (Button) findViewById(R.id.back);

        Bu_copy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                ClipboardManager c = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String a = Viewer.getUrl();
                c.setText(a);
//                Toast.makeText(getApplicationContext(), "�� �� �� ��", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BlogViewActivity.this, WriteActivity.class);
                intent.putExtra("url",a);
                startActivity(intent);
                finish();
            }
        });

        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BlogViewActivity.this, WriteActivity.class);
                startActivity(intent);
                finish();
            }
        });


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.write, menu);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // do nothing
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (Viewer.getUrl().equals(filiterurl)) {
                finish();
                Log.d("1", "filiter");
            } else if (Viewer.canGoBack()) {
                Viewer.goBack();
                Log.d("1", Viewer.getUrl());
                Log.d("1", "goback");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_write,
                    container, false);
            return rootView;
        }
    }

    class MyWebClient extends WebViewClient { //���信�� ó���ϴ� ���
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //SCWV.loadUrl(url);
            Viewer.loadUrl(url);

            return true;
        }
    }


}
