package com.ttoview.nakayosi.ttoview.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.MarketVersionChecker;
import com.ttoview.nakayosi.ttoview.R;

import java.util.ArrayList;

/**
 * Created by jeongchanghyeon on 2015. 10. 5..
 */
public class SplashActivity extends BaseActivity {
    private final int MY_PERMISSION_REQUEST = 100;
    static final String packName = "com.ttoview.nakayosi.ttoview";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo isEnableNetWork = manager.getActiveNetworkInfo();
        if (isEnableNetWork == null || !isEnableNetWork.isConnected()) {
            //네트워크가 사용 불가능 하면.
            showNetworkAlert();
        }else {

            if (versionCheck()) {
                if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
                    checkPermission();
                } else {
                    //메인 시작
                    startMain();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        boolean startFlag = true;
        ArrayList<String> permissionList = new ArrayList<String>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            startFlag = false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
            startFlag = false;
        }

        if (startFlag == true) startMain();

        Log.d("test3", "어레이 리스트 사이즈 :" + permissionList.size());
        if (permissionList.size() > 0) {
            String permissionArray[] = permissionList.toArray(new String[permissionList.size()]);
            requestPermissions(permissionArray, MY_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionFlag = true;
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) permissionFlag = false;
                }
                if (permissionFlag) {
                    startMain();
                } else {
                    Toast.makeText(SplashActivity.this, "권한을 부여하지 않으면 실행 할 수 없습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    public boolean versionCheck() {
        MarketVersionChecker marketVersionChecker = new MarketVersionChecker();
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String serverVersion = marketVersionChecker.execute(packName).get();
            String currentVersion = packageInfo.versionName;

            //부등호 반대로 바꿔야함
            if (serverVersion.compareTo(currentVersion) < 0) {
                Log.d("test2", "현재 버전은 구버전입니다 업데이트 내역이 존재합니다.");
                showVersionAlert();
                return false;
            }
        } catch (Exception e) {
            Log.d("test2", e.toString());

            return false;
        }
        return true;
    }

    private void startMain() {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            //Do Something
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    public void showVersionAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("새로운 버전이 존재합니다.");
        alertDialog.setMessage("업데이트를 진행해야 실행이 가능합니다. \n 업데이트 하시겠습니까?");
        alertDialog.setCancelable(false);

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("업데이트",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + packName));
                        startActivity(intent);
                        finish();
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void showNetworkAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("네트워크에 연결되어 있지 않습니다..");
        alertDialog.setMessage("WIFI 또는 통신사네트워크에 연결후 재실행 해주세요.");
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton("종료",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog.setPositiveButton("네트워크 설정",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                });
        alertDialog.setNeutralButton("WIFI 설정",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                });
        alertDialog.show();
    }
}
