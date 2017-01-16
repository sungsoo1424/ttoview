package com.ttoview.nakayosi.ttoview.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.NFC.NdefMessageParser;
import com.ttoview.nakayosi.ttoview.NFC.ParsedRecord;
import com.ttoview.nakayosi.ttoview.NFC.TextRecord;
import com.ttoview.nakayosi.ttoview.NFC.UriRecord;
import com.ttoview.nakayosi.ttoview.R;

public class ReadActivity extends BaseActivity {

    TextView readResult;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_read);

        Log.d("dd", "onCreate");
/*		if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/

        readResult = (TextView) findViewById(R.id.readResult);

        // NFC ���� ��ü ����
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent targetIntent = new Intent(this, ReadActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, targetIntent, 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        Log.d("dd", "discovered");
        try {
            ndef.addDataType("*/*");
            Log.d("dd", "try");
        } catch (MalformedMimeTypeException e) {
            Log.d("dd", "fail");
            throw new RuntimeException("fail", e);
        }

        mFilters = new IntentFilter[]{ndef,};

        mTechLists = new String[][]{new String[]{NfcF.class.getName()}};

        Intent passedIntent = getIntent();
        Log.d("dd", "getintent");
        if (passedIntent != null) {
            Log.d("dd", "passedintetn !=null");
            String action = passedIntent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                Log.d("dd", "ProcessTag");
                processTag(passedIntent);
            }
        }
    }

    @SuppressLint("NewApi")
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
        Log.d("dd", "onResume");

        if (mAdapter != null) {
            Log.d("", "mAdapter find");
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                    mTechLists);
        }
    }

    public void onPause() {
        super.onPause();
        Log.d("dd", "onPause");
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    // NFC �±� ��ĵ�� ȣ��Ǵ� �޼ҵ�
    public void onNewIntent(Intent passedIntent) {
        // NFC �±�
        Tag tag = passedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d("", "NEw");
        if (tag != null) {
            byte[] tagId = tag.getId();
            readResult.append("�±� ID : " + toHexString(tagId) + "\n"); // TextView�� �±� ID ������
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
        Parcelable[] rawMsgs = passedIntent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        Log.d("dd", "dddd");
        if (rawMsgs == null) {
            return;
        }

        // ����! rawMsgs.length : ��ĵ�� �±� ����
//        Toast.makeText(getApplicationContext(), "��ĵ ����!", Toast.LENGTH_SHORT).show();

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
                Log.d("", "TEXT" + recordStr);
            } else if (recordType == ParsedRecord.TYPE_URI) {

                recordStr = "URI : " + ((UriRecord) record).getUri().toString();
                Log.d("", "URI" + recordStr);
            }

            readResult.append(recordStr + "\n"); // �о���� �ؽ�Ʈ ���� TextView�� ������
        }

        return size;
    }
}
