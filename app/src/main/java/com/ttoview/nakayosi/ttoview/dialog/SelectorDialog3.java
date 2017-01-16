package com.ttoview.nakayosi.ttoview.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ttoview.nakayosi.ttoview.R;


/**
 * 기본 2버튼 다이얼로그
 */
public class SelectorDialog3 extends BaseDialog implements View.OnClickListener {

    private LinearLayout mWriteButton;
    private LinearLayout mKakaoStoryButton;

    private DialogClickListener mListener;
    private Runnable mRunnableDone;
    private Runnable mRunnableCancel;

    public void setOnDialogClickListener(DialogClickListener listener) {
        mListener = listener;
    }

    public static interface DialogClickListener {

        public void onDoneClick(int index);
    }

    public SelectorDialog3(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_selection3, null);

        mWriteButton = (LinearLayout) view.findViewById(R.id.writeButton);
        mKakaoStoryButton = (LinearLayout) view.findViewById(R.id.kakaoStoryButton);
        mWriteButton.setOnClickListener(this);
        mKakaoStoryButton.setOnClickListener(this);
        setContentView(view);
    }

    public void setRunnableDone(Runnable runnable) {
        mRunnableDone = runnable;
    }

    public void setRunnableCancel(Runnable runnable) {
        mRunnableCancel = runnable;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.writeButton:
                if (null != mListener) {
                    mListener.onDoneClick(0);
                }
                if (null != mRunnableDone) {
                    mRunnableDone.run();
                }
                dismiss();
                break;
            case R.id.kakaoStoryButton:
                if (null != mListener) {
                    mListener.onDoneClick(1);
                }
                if (null != mRunnableCancel) {
                    mRunnableCancel.run();
                }
                dismiss();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
