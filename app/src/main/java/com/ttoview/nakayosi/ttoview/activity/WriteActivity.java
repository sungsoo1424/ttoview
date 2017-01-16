package com.ttoview.nakayosi.ttoview.activity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;
import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.manager.GpsInfoManager;
import com.ttoview.nakayosi.ttoview.manager.RestManager;
import com.ttoview.nakayosi.ttoview.model.TagInfoModel;

@SuppressLint({"ShowToast", "NewApi"})
public class WriteActivity extends BaseActivity {

    Button blog;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    public static final int SHOW_PUSH_CONFIRM = 2001;
    String uid;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;
    ProgressDialog dialog;
    boolean alertFalg = true;

    GpsInfoManager gps;
    double latitude,longitude;

    private final String packname = "com.nhn.android.blog";

    EditText writeText;
    int gpsFindCount=0;

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        blog = (Button) findViewById(R.id.blogButton);
        writeText = (EditText) findViewById(R.id.writeText);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (getIntent().hasExtra("url")) {
            writeText.setText(getIntent().getStringExtra("url"));
        }

        Intent intent = new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        blog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WriteActivity.this, com.ttoview.nakayosi.ttoview.BlogViewActivity.class);
                startActivity(intent);
            }
        });


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
        }

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
        gps = new GpsInfoManager(this);

        if (gps.isGetLocation()) {
            dialog = ProgressDialog.show(this, "","GPS탐색중 입니다..", true);
            final Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.d("test","타이머가 돌고 있습니다2"+latitude);
                    Log.d("test","카운트 :"+gpsFindCount);
                    gpsFindCount ++;

                    if(latitude!=0.0) {
                        //GPS정보 획득시 타이머 종료
                        Log.d("test","타이머가 종료되었습니다: "+latitude+","+longitude);
                        dialog.dismiss();
                        timer.cancel();
                    }

                    if(gpsFindCount>15){
                        Log.d("test","위치를 찾지 못하였습니다."+latitude+","+longitude);
                        dialog.dismiss();
                        timer.cancel();
                    }

                }
                @Override
                public boolean cancel() {
                    Log.v("test","타이머 종료");
                    return super.cancel();
                }
            };
            timer.schedule(timerTask, 0, 1000);


        } else {
            // GPS 를 사용할수 없으므로
            if(alertFalg) gps.showSettingsAlert();
            alertFalg = false;
        }
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    // NFC �±� ��ĵ�� ȣ��Ǵ� �޼ҵ�
    @Override
    protected void onNewIntent(Intent intent) {
        TagInfoModel tagInfoModel=null;
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagid = tag.getId();

            uid = toHexString(tagid);
            Log.d("1", uid);

            //uid,gps1,gps2
            RestManager restManager = new RestManager(WriteActivity.this);
            try {
                tagInfoModel = restManager.execute(uid,String.valueOf(latitude),String.valueOf(longitude)).get();
                Log.d("test",tagInfoModel.toString());
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "인터넷에 연결되어 있지 않거나 연결이 불안정 합니다.",Toast.LENGTH_LONG).show();
            }

            if(tagInfoModel.getCertification().equals("true")){  Toast.makeText(getApplicationContext(), "정품 카드입니다.",Toast.LENGTH_LONG).show();  processTag(intent);}
            else Toast.makeText(getApplicationContext(), "정품 카드가 아닙니다.",Toast.LENGTH_LONG).show();
        }

        if (intent != null) {
            processTag(intent); // processTag �޼ҵ� ȣ��
        }
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

    // onNewIntent �޼ҵ� ���� �� ȣ��Ǵ� �޼ҵ�
    private void processTag(Intent intent) {
        // EditText�� �Էµ� ���� ������
        String s = writeText.getText().toString();

        // ������ �±׸� ����Ű�� ��ü
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // �ƹ��͵� �Է¹��� ������ �±׿� ���� ����
        if (s.equals("")) {
//            Toast.makeText(getApplicationContext(), "������ �Է����ּ���.", 1000).show();
        }
        else {
            Log.d("", s);
            //NdefMessage message = createTagMessage(s, TYPE_TEXT);
            NdefMessage message = createTagMessage(s, TYPE_URI);
            writeTag(message, detectedTag);
        }
    }

    // ������ �±׿� NdefMessage�� ���� �޼ҵ�
    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        Log.d("dd", "size : " + size);
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    return false;
                }

                ndef.writeNdefMessage(message);
                Log.d("dd", "size : " + message);
                final MainDialog mMainDialog = new MainDialog();
                mMainDialog.show(getFragmentManager(), "AAA");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    // Do Something
                    public void run() {
                        mMainDialog.dismiss();
                    }
                }, 1500);

            } else {
                NdefFormatable formatable = NdefFormatable.get(tag);
                if (formatable != null) {
                    try {
                        formatable.connect();
                        formatable.format(message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            return false;
        }

        return true;
    }

    public static class MainDialog extends DialogFragment {
        @SuppressLint("NewApi")
        public Dialog onCreateDialog(Bundle saveInsBundle) {
            AlertDialog.Builder mBulider = new AlertDialog.Builder(
                    getActivity());
            LayoutInflater mLIF = getActivity().getLayoutInflater();
            mBulider.setView(mLIF.inflate(R.layout.customdialog, null));
            mBulider.setTitle("쓰기 완료");
            return mBulider.create();

        }
    }

    private NdefMessage createTagMessage(String msg, int type) {
        NdefRecord[] records = new NdefRecord[1];

        if (type == TYPE_TEXT) {
            Log.d("ddd", "TEXT");
            records[0] = createTextRecord(msg, Locale.KOREAN, true);
        } else if (type == TYPE_URI) {

            records[0] = createUriRecord(msg);
            Log.d("ddd", "URI" + records[0]);
        }
        NdefMessage mMessage = new NdefMessage(records);

        return mMessage;
    }

    private NdefRecord createTextRecord(String text, Locale locale,
                                        boolean encodeInUtf8) {
        final byte[] langBytes = locale.getLanguage().getBytes(
                Charsets.US_ASCII);
        final Charset utfEncoding = encodeInUtf8 ? Charsets.UTF_8 : Charset
                .forName("UTF-16");
        final byte[] textBytes = text.getBytes(utfEncoding);
        final int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        final char status = (char) (utfBit + langBytes.length);
        final byte[] data = Bytes.concat(new byte[]{(byte) status},
                langBytes, textBytes);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                new byte[0], data);
    }

    private NdefRecord createUriRecord(String msg) {
        Log.d("ddd", "URI2");
        byte[] uriField = msg.getBytes(Charset.forName("US-ASCII"));
        byte[] payload = new byte[uriField.length + 1]; // URI Prefix�� 1�� �߰���
        payload[0] = 0x00;
        System.arraycopy(uriField, 0, payload, 1, uriField.length); // payload��
        NdefRecord rtdUriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_URI, new byte[0], payload);
        return rtdUriRecord;
    }

}
