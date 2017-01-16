package com.ttoview.nakayosi.ttoview.activity;

import android.os.Bundle;
import android.view.Window;

import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.R;

public class HelpActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.helpactivity);
    }
}
