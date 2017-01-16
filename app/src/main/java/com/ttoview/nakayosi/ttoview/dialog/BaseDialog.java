package com.ttoview.nakayosi.ttoview.dialog;

import android.app.Dialog;
import android.content.Context;

import com.ttoview.nakayosi.ttoview.R;

public abstract class BaseDialog extends Dialog {

    public BaseDialog(Context context) {
        super(context, R.style.DimDialog);
        setCanceledOnTouchOutside(false);
    }

}
