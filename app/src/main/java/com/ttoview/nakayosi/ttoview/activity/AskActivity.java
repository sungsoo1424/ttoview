package com.ttoview.nakayosi.ttoview.activity;

import android.annotation.SuppressLint;

import android.app.PendingIntent;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.NFC.NdefMessageParser;
import com.ttoview.nakayosi.ttoview.NFC.ParsedRecord;
import com.ttoview.nakayosi.ttoview.NFC.TextRecord;
import com.ttoview.nakayosi.ttoview.NFC.UriRecord;
import com.ttoview.nakayosi.ttoview.R;
import java.util.List;
import java.util.UUID;

@SuppressLint({"ShowToast", "NewApi"})
public class AskActivity extends BaseActivity {

    TextView readResult;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private EditText edit_Uuid,edit_Sn;
    private Button coppy_uuid;
    private TextView email;

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        edit_Uuid = (EditText)findViewById(R.id.uuid);
        edit_Sn = (EditText)findViewById(R.id.Serial_Num);
        coppy_uuid = (Button)findViewById(R.id.coppy_uuid);
        email = (TextView)findViewById(R.id.email);
        edit_Uuid.setText(GetDevicesUUID(this));

        //readResult = (TextView) findViewById(R.id.readResult);

        // NFC ���� ��ü ����
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter mifare = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        mFilters = new IntentFilter[] { mifare };


        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            String action = passedIntent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                processTag(passedIntent);
            }
        }

        coppy_uuid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyClipBord();
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyClipBord();
                sendMail();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
            View rootView = inflater.inflate(R.layout.fragment_read, container,
                    false);
            return rootView;
        }
    }

    /************************************
     * ���⼭���� NFC ���� �޼ҵ�
     ************************************/
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                    mTechLists);
        }
    }

    public void onPause() {
        super.onPause();

        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    // NFC �±� ��ĵ�� ȣ��Ǵ� �޼ҵ�
    public void onNewIntent(Intent passedIntent) {
        // NFC �±�
        Tag tag = passedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagId = tag.getId();
            String uID = toHexString(tagId);
            Log.d("test2",uID);
        }
        if (passedIntent != null) {
           processTag(passedIntent); // processTag �޼ҵ� ȣ��
        }
    }

    // NFC �±� ID�� �����ϴ� �޼ҵ�
    public static final String CHARS = "0123456789ABCDEF";
    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; ++i) {
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(
                    CHARS.charAt(data[i] & 0x0F));
        }
        return sb.toString();
    }

    // onNewIntent �޼ҵ� ���� �� ȣ��Ǵ� �޼ҵ�
    private void processTag(Intent passedIntent) {
        Parcelable[] rawMsgs = passedIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs == null) {
            return;
        }

        NdefMessage[] msgs;
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
                showTag(msgs[i]); // showTag �޼ҵ� ȣ��
            }
        }
    }

    // NFC �±� ������ �о���̴� �޼ҵ�
    private int showTag(NdefMessage mMessage) {
        List<ParsedRecord> records = NdefMessageParser.parse(mMessage);
        final int size = records.size();
        for (int i = 0; i < size; i++) {
            ParsedRecord record = records.get(i);
            int recordType = record.getType();
            String recordStr = ""; // NFC �±׷κ��� �о���� �ؽ�Ʈ ��
            if (recordType == ParsedRecord.TYPE_TEXT) {
                recordStr = "TEXT : " + ((TextRecord) record).getText();
            } else if (recordType == ParsedRecord.TYPE_URI) {
                recordStr = "URI : " + ((UriRecord) record).getUri().toString();
            }
            Log.d("test2",recordStr);
            edit_Sn.setText(recordStr.split("/")[3]);
        }

        return size;
    }

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

    private void copyClipBord(){
        ClipboardManager clipboardManager =  (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String txt = "UUID="+edit_Uuid.getText().toString()+" / Card_Num="+edit_Sn.getText().toString();
        ClipData clipData = ClipData.newPlainText("text",txt);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this,"UUID, Card_Num이 클립보드에 복사되었습니다.",Toast.LENGTH_LONG).show();
    }
    private void sendMail(){
        Uri uri=Uri.parse("mailto:ttoview@ttoview.com");
        Intent i=new Intent(Intent.ACTION_SENDTO,uri);
        startActivity(i);
    }
}
