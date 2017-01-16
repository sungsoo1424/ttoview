package com.ttoview.nakayosi.ttoview;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * Created by jacob on 2015. 9. 8..
 */
public abstract class ActionBarActivity extends BaseActivity {

    protected static final int BAR_TYPE_NONE = 0x01;
    protected static final int BAR_TYPE_ONLY_BACK = 0x02;
    protected static final int BAR_TYPE_ONLY_MENU = 0x03;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_actionbar);
        setActionBarStyle();
        getLayoutInflater().inflate(layoutResID, ((FrameLayout) findViewById(R.id.contentLayout)));


/**
 * Android Softkey가 아닌 Device에서 엑션바 드랍다운 안되는 문제 해결
 */
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected abstract void setActionBarStyle();
}
