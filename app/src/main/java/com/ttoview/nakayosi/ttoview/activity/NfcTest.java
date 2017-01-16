package com.ttoview.nakayosi.ttoview.activity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.MifareUltralight;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;
import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.R;

@SuppressLint({"ShowToast", "NewApi"})
public class NfcTest extends BaseActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    public static final int SHOW_PUSH_CONFIRM = 2001;
    String a;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;

    private final String packname = "com.nhn.android.blog";

    EditText writeText;

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.nfc_test);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        writeText = (EditText) findViewById(R.id.nfc_test);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        if (getIntent().hasExtra("url")) {
            writeText.setText(getIntent().getStringExtra("url"));
        }

        Intent intent = new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


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
            nfcAdapter
                    .enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    // NFC �±� ��ĵ�� ȣ��Ǵ� �޼ҵ�
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            byte[] tagid = tag.getId();

            a = toHexString(tagid);
            Log.d("1", a);
        }
        Log.d("", "new");
        if (intent != null) {
            Log.d("", "new2");
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

    private void processTag(Intent intent) {
        String s = writeText.getText().toString();


        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (s.equals("")) {
        }

        else {
            Log.d("", s);

            NdefMessage message = createTagMessage(s, TYPE_URI);
            writeTag(message, detectedTag);
        }
    }


    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        Log.d("dd", "size : " + size);
        try {
            Ndef ndef = Ndef.get(tag);
            MifareUltralight mifare = MifareUltralight.get(tag);

            Log.d("test","미페어 타입 :"+ mifare.getType());

            if (ndef != null) {
                Log.v("test","ndef가 널이아니다.");
                mifare.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(), "이 카드는 읽기전용입니다.",Toast.LENGTH_LONG).show();
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(getApplicationContext(), "메세지의 크기가 너무 큽니다..",Toast.LENGTH_LONG).show();
                    return false;
                }
                byte[] pwd = new byte[] { (byte)0x70, (byte)0x61, (byte)0x73, (byte)0x73 };
                byte[] pack = new byte[] { (byte)0x98, (byte)0x76 };

                // write PACK:
                byte[] result = mifare.transceive(new byte[] {
                        (byte)0xA2,  /* CMD = WRITE */
                        (byte)0x2C,  /* PAGE = 44 */
                        pack[0], pack[1], 0, 0
                });
                Log.d("test","팩 기록 결과 :"+result);

                // write PWD:
                result = mifare.transceive(new byte[] {
                        (byte)0xA2,  /* CMD = WRITE */
                        (byte)0x2B,  /* PAGE = 43 */
                        pwd[0], pwd[1], pwd[2], pwd[3]
                });
                Log.d("test","패스워드 기록 결과 :"+result);


                //ndef.writeNdefMessage(message);

                Log.d("test", "size : " + message);


                final MainDialog mMainDialog = new MainDialog();

                mMainDialog.show(getFragmentManager(), "AAA");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    // Do Something
                    public void run() {
                        mMainDialog.dismiss();
                    }
                }, 1500);

            }

            else {
                Log.v("test","ndef가 널이다.");
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
        // byte[] uriField =
        // "example.com".getBytes(Charset.forName("US-ASCII"));
        byte[] payload = new byte[uriField.length + 1]; // URI Prefix�� 1�� �߰���
        // ����.
        payload[0] = 0x00;
        // ���λ�
        // 0x03 : http://
        // 0x01 http://www.
        System.arraycopy(uriField, 0, payload, 1, uriField.length); // payload��
        // URI ����
        // ���� uriField 0 ��°���� ����
        // ���纻 payload 1��°���� uriField ���̸�ŭ uriField�� ����
        // payload[0]�� ��������� �������

        NdefRecord rtdUriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_URI, new byte[0], payload);
        return rtdUriRecord;
        /*
         * return new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI,
		 * NdefRecord.RTD_URI, new byte[0], data);
		 */

    }
}
