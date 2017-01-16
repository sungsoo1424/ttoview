package com.ttoview.nakayosi.ttoview.manager;


import com.ttoview.nakayosi.ttoview.model.SnsModel;

/**
 * Created by Taeun on 15. 7. 30..
 */
public class BaseSnsManager {

    public static final String SNS_LOGIN_TYPE_FACEBOOK = "01";
    public static final String SNS_LOGIN_TYPE_KAKAO = "02";
    public static final String SNS_LOGIN_TYPE_NAVER = "03";
    public static final String SNS_LOGIN_TYPE_GOOGLE = "04";

    protected LoginInterface mListener;

    public void setOnLoginListener(LoginInterface listener) {
        this.mListener = listener;
    }

    public interface LoginInterface {
        void onCompleted(SnsModel model, String type);

        void onError(Exception e);
    }
}
