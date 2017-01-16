package com.ttoview.nakayosi.ttoview.manager;

import android.app.Activity;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.ttoview.nakayosi.ttoview.model.SnsModel;

/**
 * Created by jeongchanghyeon on 2015. 10. 13..
 */
public class KakaoLoginManager extends BaseSnsManager {

    private Activity mActivity;

    private LoginButton mLoginButton;
    public KakaoLoginManager(Activity activity, LoginButton loginButton) {
        this.mActivity = activity;
        this.mLoginButton = loginButton;
    }

    public SnsModel getLoginInfo()
    {
        SnsModel model = new SnsModel();

        return model;
    }

    /**
     * 카카오 로그인 api
     */
    public void requestKakaoLoginApi() {
        Log.v("test","카카오 로그인 api함수 호출.");
        Session session = Session.getCurrentSession();
        if (null != session && session.isOpened()) {
            // 카카오톡 유저 정보 요청
            Log.v("test","카카오 유저정보를 조회한다.");
            requestKakaoUserApi();
        } else {
            // 카카오톡 세션이 없다면 카카오톡 버튼 클릭 처리
            Log.v("test","카카톡 세션이 없다.");
            session.addCallback(mKakaoSessionCallback);
            mLoginButton.performClick();
        }
    }

    /**
     * 유저정보 요청 API
     */
    private void requestKakaoUserApi() {
        Log.v("test","유저정보 가져오는 함수 호출");
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.v("test","세션닫힘");

            }

            @Override
            public void onNotSignedUp() {
                Log.v("test","로그인안함");

            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.v("test","실패");

            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.v("test","카카오 유저정보 조회 성공.");
                String avatarImage = userProfile.getProfileImagePath();
                String snsId = userProfile.getId() + "";
                String userName = userProfile.getNickname();
                String oAuthToken = Session.getCurrentSession().getAccessToken();

                SnsModel model = new SnsModel();
                model.setUserEmail("");
                model.setAccessToken(oAuthToken);
                model.setUserName(userName);
                model.setSNSID(snsId);
                model.setAvatarImageUrl(avatarImage);

                mListener.onCompleted(model, SNS_LOGIN_TYPE_KAKAO);
            }
        });

    }

    /**
     * 카카오톡 세션 콜백
     */
    private ISessionCallback mKakaoSessionCallback = new ISessionCallback() {

        @Override
        public void onSessionOpened() {
            requestKakaoUserApi();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.v("test","세션 오픈 실패");
            if(exception != null) {
                Log.v("test",exception.toString());
            }

        }
    };

}
