package com.ttoview.nakayosi.ttoview.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.manager.CardManager;
import com.ttoview.nakayosi.ttoview.manager.GetCardImageManager;
import com.ttoview.nakayosi.ttoview.manager.GpsInfoManager;
import com.ttoview.nakayosi.ttoview.manager.UrlShotnerManager;
import com.ttoview.nakayosi.ttoview.model.HttpRequestResult;
import java.util.UUID;


@SuppressLint({"ShowToast", "NewApi"})
public class WriteActivity2 extends BaseActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    public static final int SHOW_PUSH_CONFIRM = 2001;
    String uid;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;
    ProgressDialog dialog;
    boolean alertFalg = true;
    boolean hasExtraInfoFlag = false;
    int step = 1;
    int gpsFindCount = 0;
    ClipboardManager clipboardManager;
    CheckBox readOnlyChkBox;
    boolean readOnlyFlag = false;

    private InputMethodManager ipm;

    Button url_ok_button;
    ImageView goHome;
    EditText urlWrite;
    TextView urlView;
    ImageView progressImage, progressExplain, card_img;
    LinearLayout urlLayout, writeLyaoutMain;
    AlertDialog alertDialog;
    String finalUrl;
    private String Uuid;
    private String serial_num;



    GpsInfoManager gps;
    double latitude, longitude;
    private final String packname = "com.nhn.android.blog";
    Context mContext;

    EditText writeText;

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write2);
        progressImage = (ImageView) findViewById(R.id.progressImage);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        card_img = (ImageView)findViewById(R.id.card_img);
        card_img.setImageResource(R.drawable.logo02);
        mContext = this;
        Uuid = GetDevicesUUID(this);
        Log.d("test2", "UUID : " + GetDevicesUUID(this));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        writeText = (EditText) findViewById(R.id.urlWrite);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        readOnlyChkBox = (CheckBox) findViewById(R.id.checkBox);

        if (getIntent().hasExtra("url") || getIntent().hasExtra("android.intent.extra.TEXT")) {
            String writedUrl = null;
            if (getIntent().hasExtra("url"))
                writedUrl = getIntent().getStringExtra("url").toString();
            if (getIntent().hasExtra("android.intent.extra.TEXT"))
                writedUrl = getIntent().getStringExtra("android.intent.extra.TEXT").toString();

            writeText.setText(writedUrl);
            hasExtraInfoFlag = true;

            String[] mimeType = new String[1];
            mimeType[0] = ClipDescription.MIMETYPE_TEXT_URILIST;
            clipboardManager.setPrimaryClip(new ClipData(new ClipDescription("text_data", mimeType), new ClipData.Item(writedUrl)));
            Toast.makeText(getApplicationContext(), "작성하신 SNS글의 URL이 클립보드에 복사되었습니다.", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
        }

        url_ok_button = (Button) findViewById(R.id.url_ok_button);
        goHome = (ImageView) findViewById(R.id.goHome);

        urlWrite = (EditText) findViewById(R.id.urlWrite);
        urlView = (TextView) findViewById(R.id.urlView);
        urlLayout = (LinearLayout) findViewById(R.id.urlLayout);
        writeLyaoutMain = (LinearLayout) findViewById(R.id.writeLyaoutMain);
        progressExplain = (ImageView) findViewById(R.id.progressExplain);


        urlWrite.setVisibility(View.INVISIBLE);
        url_ok_button.setVisibility(View.INVISIBLE);
        urlLayout.setVisibility(View.INVISIBLE);

        ipm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        writeLyaoutMain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ipm.hideSoftInputFromWindow(writeLyaoutMain.getWindowToken(), 0);
                urlWrite.clearFocus();
            }
        });


        url_ok_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urlWrite.getText().length() > 5) {
                    step = 3;
                    progressImage.setImageResource(R.drawable.step3);
                    urlWrite.setVisibility(View.INVISIBLE);
                    url_ok_button.setVisibility(View.INVISIBLE);
                    String writedUrl = urlWrite.getText().toString().trim();

                    if (!writedUrl.startsWith("http")) {
                        writedUrl = "http://" + writedUrl;
                    }

                    //url 단축
                    String shortUrl = "";
                    try {
                        shortUrl = new UrlShotnerManager(mContext).execute(writedUrl).get();
                        Log.d("test2", "줄인 url=" + shortUrl);
                    } catch (Exception e) {
                        Log.d("test2", "에러코드:" + e.toString());
                    }

                    //플랫폼 정보 획득
                    String putUrl;
                    String article_num;
                    String platformCode = getUrlInfo(writedUrl);
                    if(platformCode.equals("4")){
                        //구글포토스의경우 /photos 가 붙기떄문에
                        article_num = shortUrl.split("\\/")[4];
                    }else {
                        article_num = shortUrl.split("\\/")[3];
                    }
                    String is_writeable;
                    is_writeable = readOnlyFlag ? "0" : "1";

                    //카드정보 수정 시도.
                    putUrl = "http://ttoview.com/card/" + serial_num + "/" + Uuid + "/" + article_num + "/" + platformCode + "/" + is_writeable;
                    Log.d("test2", putUrl);
                    CardManager cardManager = new CardManager(mContext);
                    HttpRequestResult result;

                    try {
                        result = cardManager.execute("PUT",putUrl).get();
                        Log.d("test2",result.getResultCode()+"");
                        putDoneAlert(mContext);
                    }catch (Exception e){
                        Log.d("test2",e.toString());
                        Toast.makeText(getApplicationContext(), "기록에 실패 하였습니다.\n다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                        putFailAlert(mContext);
                    }

                    progressExplain.setImageResource(R.drawable.explain_step4);
                    progressImage.setImageResource(R.drawable.step4);
                    //applyNewLineCharacter(urlView);
                    urlLayout.setVisibility(View.INVISIBLE);
                    readOnlyChkBox.setVisibility(View.INVISIBLE);
                    url_ok_button.setVisibility(View.INVISIBLE);
                    ipm.hideSoftInputFromWindow(writeLyaoutMain.getWindowToken(), 0);

                } else
                    Toast.makeText(getApplicationContext(), "정확한 URL 주소를 입력해주세요.", Toast.LENGTH_LONG).show();
            }
        });

        goHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        });

        urlWrite.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (clipboardManager.getPrimaryClip() == null || clipboardManager.getPrimaryClip().getItemAt(0) == null || clipboardManager.getPrimaryClip().getItemAt(0).getText() == null) {
                    Toast.makeText(getApplicationContext(), "붙여넣을 내용이 없습니다.", Toast.LENGTH_LONG).show();
                } else {
                    urlWrite.setText(clipboardManager.getPrimaryClip().getItemAt(0).getText());
                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                        @Override
                        public void run() {
                            ipm.hideSoftInputFromWindow(writeLyaoutMain.getWindowToken(), 0);
                            urlWrite.clearFocus();
                        }
                    }, 400);
                }
                return false;
            }
        });

        readOnlyChkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readOnlyChkBox.isChecked()) {
                    readOnlyFlag = true;
                } else {
                    readOnlyFlag = false;
                }

                Log.d("test2", "리드온리 플래그: " + readOnlyFlag);
            }
        });
        readOnlyChkBox.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // do nothing
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    /************************************
     * ���⼭���� NFC ���� �޼ҵ�
     ************************************/
    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }


        super.onPause();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    // NFC �±� ��ĵ�� ȣ��Ǵ� �޼ҵ�
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        HttpRequestResult result = null;
        if (step == 1) {
            if (tag != null) {
                byte[] tagid = tag.getId();
                uid = toHexString(tagid);
                Log.d("test", uid);

                String connectUrl;
                // RestManager restManager = new RestManager(WriteActivity2.this);
                CardManager cardManager = new CardManager(this);
                connectUrl = "http://ttoview.com/card/uid/" + uid;

                try {
                    //정품인증하기
                    result = cardManager.execute("GET", connectUrl).get();

                    //정품인지 판단.
                    if (result.getResultCode() == 200 && result.getResultJson().getString("is_writeable").equals("1"))//정품 일경우 스텝2로 변경
                    {

                        String cardType = result.getResultJson().getString("place_code")+"-"+result.getResultJson().getString("card_code");
                        serial_num = result.getResultJson().getString("serial_num");
                        //카드이미지 변경
                        card_img.setImageBitmap(new GetCardImageManager(this).execute(String.valueOf(cardType)).get());


                        step = 2;
                        urlWrite.setVisibility(View.VISIBLE);
                        url_ok_button.setVisibility(View.VISIBLE);
                        progressImage.setImageResource(R.drawable.step2);
                        urlLayout.setVisibility(View.VISIBLE);
                        progressExplain.setImageResource(R.drawable.explain_step2);
                        readOnlyChkBox.setVisibility(View.VISIBLE);

                    } else if (result.getResultCode() == 200 && result.getResultJson().getString("is_writeable").equals("0")) {
                        Toast.makeText(getApplicationContext(), "이 카드는 재기록이 불가능합니다.", Toast.LENGTH_LONG).show();
                    } else//비 정품 일경우
                        Toast.makeText(getApplicationContext(), "정품 카드가 아닙니다.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "인터넷에 연결되어 있지 않거나 연결이 불안정 합니다.", Toast.LENGTH_LONG).show();
                }
            }
        }//end step1
    }

    public static final String CHARS = "0123456789ABCDEF";

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0f)).append(
                    CHARS.charAt(data[i] & 0x0f));
        }
        return sb.toString();
    }


    public static class MainDialog extends DialogFragment {
        @SuppressLint("NewApi")
        public Dialog onCreateDialog(Bundle saveInsBundle) {
            AlertDialog.Builder mBulider = new AlertDialog.Builder(
                    getActivity(), AlertDialog.THEME_HOLO_LIGHT);
            LayoutInflater mLIF = getActivity().getLayoutInflater();
            mBulider.setView(mLIF.inflate(R.layout.customdialog, null));
            mBulider.setTitle("쓰기 완료");

            mBulider.setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            return mBulider.create();
        }
    }

    public void putDoneAlert(Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        alertDialog.setTitle("기록완료");
        alertDialog.setMessage("카드에 기록이 완료되었습니다.");
        //getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("홈으로",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
        alertDialog.show();
    }

    public void putFailAlert(Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        alertDialog.setTitle("기록실패");
        alertDialog.setMessage("카드기록에 실패하였습니다.");
        //getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setPositiveButton("재시도",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getBaseContext(), WriteActivity2.class));
                    }
                });
        alertDialog.setNegativeButton("홈으로",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    }
                });
        alertDialog.show();
    }


    //문자단위 개행함수.
    private void applyNewLineCharacter(TextView textView) {
        Paint paint = textView.getPaint();
        String text = textView.getText().toString();
        int frameWidth = 600;
        int startIndex = 0;
        int endIndex = paint.breakText(text, true, frameWidth, null);
        String save = text.substring(startIndex, endIndex);
        // Count line of TextView
        int lines = 1;

        while (true) {
            // Set new start index
            startIndex = endIndex;
            // Get substring the remaining of text
            text = text.substring(startIndex);

            if (text.length() == 0) break;
            else lines++;

            if (lines == 4) // 3줄이 넘으면 줄임표(...)를 붙인다.
            {
                save = save.substring(0, save.length() - 2) + "...";
                break;
            }

            // Calculate end of index that fits
            endIndex = paint.breakText(text, true, frameWidth, null);
            // Append substring that fits into the frame
            save += "\n" + text.substring(0, endIndex);
        }
        // Set text to TextView
        textView.setText(save);
    }

    //디바이스의 UUID 정보 획득
    private String GetDevicesUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return "A-" + deviceId.toUpperCase();
    }

    String getUrlInfo(String url) {
        int i = 0;
        String[] platformList = {"test.do", "story.kakao.com", "m.facebook.com", "www.facebook.com", "goo.gl"};
        String result;
        String[] splitedUrl = url.split("\\/");
        String flatform = splitedUrl[2];
        for (String getList : platformList) {
            if (getList.equals(flatform)) break;
            i++;
        }
        if (i == platformList.length) {
            i = 99;
        }
        result = String.valueOf(i);
        return result;
    }



}
