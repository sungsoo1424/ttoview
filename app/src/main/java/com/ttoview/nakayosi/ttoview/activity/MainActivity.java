package com.ttoview.nakayosi.ttoview.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.dialog.SelectorDialog;
import com.ttoview.nakayosi.ttoview.dialog.SelectorDialog2;
import com.ttoview.nakayosi.ttoview.dialog.SelectorDialog3;

import java.security.MessageDigest;

import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends BaseActivity {
    Context context = this;

    Button w;
    Button off;
    Button img;
    Button write;
    Button test;
    ViewFlipper flipper;
    private NfcAdapter nfcAdapter;
    public static final int SHOW_PUSH_CONFIRM = 2001;
    Animation showIn;
    Animation showOut;


    public void onLinkButton(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.welchon.com/index.do"));
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_main_layout);
        getAppKeyHash();
/*		if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/

        flipper = (ViewFlipper) findViewById(R.id.flipper);

        /*
        //플립 이미지 추가시
        for(int i=0;i<2;i++){
            ImageView img= new ImageView(this);
            img.setImageResource(R.drawable.logo00 +i);
            flipper.addView(img);
        }
        */

        showIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        showOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        showIn.setDuration(2000);
        showOut.setDuration(2000);

        flipper.setInAnimation(showIn);
        flipper.setOutAnimation(showOut);

        flipper.setFlipInterval(7000);//플리핑 간격(1000ms)
        flipper.startFlipping();//자동 Flipping 시작

        flipper.setOnTouchListener(new View.OnTouchListener() {
            float pastX = 0;
            float currentX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:

                        // check if we want to handle touch events, return true
                        // else don't handle further touch events, return false
                        Log.d("test", "다운발생");
                        flipper.stopFlipping();
                        pastX = event.getX();
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // finish handling touch events
                        Log.d("test", "업 발생");
                        currentX = event.getX();

                        if (currentX - pastX > 10) {
                            Log.d("test", " 오른쪽 드래그");
                            flipper.stopFlipping();
                            showIn.setDuration(500);
                            showOut.setDuration(500);
                            flipper.setInAnimation(showIn);
                            flipper.setOutAnimation(showOut);
                            flipper.showNext();

                            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                                @Override
                                public void run() {
                                    // 실행할 동작 코딩
                                    showIn.setDuration(2000);
                                    showOut.setDuration(2000);
                                    flipper.startFlipping();
                                }
                            }, 5000);
                        }


                        /*
                        else if(currentX - pastX <-10){
                            Log.d("test"," 왼쪽 드래그");
                            showIn.setDuration(500);
                            showOut.setDuration(500);
                            flipper.setInAnimation(showOut);
                            flipper.setOutAnimation(showIn);
                            flipper.showPrevious();
                            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                                @Override
                                public void run() {
                                    showIn.setDuration(2000);
                                    showOut.setDuration(2000);
                                    flipper.startFlipping();
                                }
                            }, 5000);
                        }
                        */


                        else {
                            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                                @Override
                                public void run() {
                                    showIn.setDuration(2000);
                                    showOut.setDuration(2000);
                                    flipper.startFlipping();
                                }
                            }, 5000);
                        }

                        break;
                }

                return false;
            }
        });

        final ProgressDialog dialog;

        img = (Button) findViewById(R.id.imgButton);
        off = (Button) findViewById(R.id.offNFC);
        w = (Button) findViewById(R.id.writeActivityButton);
/*		help = (Button)findViewById(R.id.helpButton);*/

        write = (Button) findViewById(R.id.writeBlog);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        if (nfcAdapter == null || nfcAdapter.isNdefPushEnabled() == false) {
            dialog = ProgressDialog.show(MainActivity.this, "", "NFC를 설정하기 위해 설정창으로 이동합니다.", true);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                //Do Something
                public void run() {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS), 0);
                    } else {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
                    }
                    dialog.dismiss();
                    //finish();
                }
            }, 1500);
        }


        off.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SelectorDialog3 selectorDialog3 = new SelectorDialog3(MainActivity.this);
                selectorDialog3.setOnDialogClickListener(new SelectorDialog3.DialogClickListener() {
                    @Override
                    public void onDoneClick(int index) {
                        Intent intent = null;
                        switch (index) {
                            case 0:
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS), 0);
                                break;
                            case 1:
                                intent = new Intent(getBaseContext(), AskActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                    }
                });
                selectorDialog3.show();
            }
        });
        w.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (nfcAdapter.isNdefPushEnabled() == false) {
                    showDialog(SHOW_PUSH_CONFIRM);
                } else {
                    Intent intent = new Intent(getBaseContext(), WriteActivity2.class);
                    startActivity(intent);
                }
            }
        });

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectorDialog selectorDialog = new SelectorDialog(MainActivity.this);
                selectorDialog.setOnDialogClickListener(new SelectorDialog.DialogClickListener() {
                    @Override
                    public void onDoneClick(int index) {
                        Intent intent = null;
                        switch (index) {
                            case 0:
                                Toast.makeText(getApplicationContext(), "준비중 입니다.", Toast.LENGTH_LONG).show();
                                intent = new Intent(getBaseContext(), MainActivity.class);

                                break;
                            case 1:
                                intent = new Intent(getBaseContext(), SNSWriteActivity.class);
                                break;
                            case 2:
                                intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.photos");
                                if (intent == null) {
                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.photos"));
                                }
                                break;
                            default:
                                break;
                        }
                        startActivity(intent);
                    }
                });
                selectorDialog.show();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectorDialog2 selectorDialog = new SelectorDialog2(MainActivity.this);
                selectorDialog.setOnDialogClickListener(new SelectorDialog2.DialogClickListener() {
                    @Override
                    public void onDoneClick(int index) {
                        Intent intent = null;
                        switch (index) {
                            case 0:
                                intent = new Intent(context, WebViewActivity.class);
                                intent.putExtra("platform", "naver");
                                break;
                            case 1:
                                intent = new Intent(context, WebViewActivity.class);
                                intent.putExtra("platform", "kakao");
                                break;
                            case 2:
                                intent = new Intent(context, WebViewActivity.class);
                                intent.putExtra("platform", "facebook");
                                break;
                            default:
                                break;
                        }
                        startActivity(intent);
                    }
                });
                selectorDialog.show();

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String foramt = intent.getStringExtra("SCAN_RESULT_FORMAT");

                Intent Fintent = new Intent(Intent.ACTION_VIEW, Uri.parse(contents));
                startActivity(Fintent);
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder;
        switch (id) {
            case SHOW_PUSH_CONFIRM:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("NFC 설정");
                builder.setMessage("NFC가 꺼져있어 실행할 수 없습니다. 설정화면으로 이동 하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @SuppressLint("InlinedApi")
                    public void onClick(DialogInterface dialog, int which) {
                   /*Intent newIntent = new Intent(
                   getApplicationContext(),
	               WriteActivity.class);
	               startActivityForResult(newIntent, 0);*/
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS), 0);
                    }
                });
                builder.setNegativeButton("아니요",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                return builder.create();
        }
        return null;
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("test", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
*/
}
