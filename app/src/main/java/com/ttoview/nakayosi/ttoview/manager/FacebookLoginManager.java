package com.ttoview.nakayosi.ttoview.manager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.ttoview.nakayosi.ttoview.model.SnsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by jeongchanghyeon on 2015. 10. 13..
 */
public class FacebookLoginManager extends BaseSnsManager {

    private Activity mActivity;

    private CallbackManager mManager;

    public FacebookLoginManager(Activity activity, CallbackManager manager) {
        this.mActivity = activity;
        this.mManager = manager;


//        LoginManager.getInstance().logOut();
    }

    /**
     * 페이스북 로그인 api
     */
    private void requestFacebookLogin() {
        Log.v("test","로그인을 시도합니다.");
        Collection<String> permissions = Arrays.asList("public_profile", "email");
        LoginManager.getInstance().logInWithReadPermissions(mActivity, permissions);
        LoginManager.getInstance().registerCallback(mManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResults) {
                Log.v("test","콜벡 성공.");
                Log.v("test","\"HTU FacebookLoginManager :: onSuccess");
                System.out.println("HTU FacebookLoginManager :: onSuccess");
                GraphRequest request = GraphRequest.newMeRequest(loginResults.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (null == object || !object.has("email") || object.getString("email").isEmpty()) {
                                Log.v("test","이메일이 등록된 페이스북 아이디만 가입이 가능합니다.");
                                mListener.onError(new Exception("이메일이 등록된 페이스북 아이디만 가입이 가능합니다."));
                                return;
                            }

                            Log.v("test","모델을 만듭니다");
                            SnsModel model = new SnsModel();
                            model.setAccessToken(response.getRequest().getAccessToken().getToken());
                            model.setSNSID(object.getString("id"));
                            model.setUserEmail(object.getString("email"));
                            model.setUserName(object.getString("name"));
                            model.setAvatarImageUrl("http://graph.facebook.com/" + model.getSNSID() + "/picture?type=large");
                            Log.v("test",model.toString());

                            mListener.onCompleted(model, SNS_LOGIN_TYPE_FACEBOOK);
                        } catch (JSONException e) {
                            Log.v("test","로그인 실패"+e.toString());
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.v("test","로그인 실패3");
                mListener.onError(new Exception("페이스북 로그인에 실패했습니다. 다시 로그인해주세요."));
            }


            @Override
            public void onError(FacebookException e) {
                Log.v("test","페이스북 로그인 실패2"+ e.toString());
                e.printStackTrace();
                mListener.onError(new Exception("페이스북 로그인에 실패했습니다. 다시 로그인해주세요."));
            }
        });
    }

    /**
     * 유저정보 요청 API
     */
    public void requestFacebookGraphApi() {
        Log.v("test","사용자의 정보를 요청합니다.");
        Log.v("test","AccessToken.getCurrentAccessToken()이 널인가? "+(null != AccessToken.getCurrentAccessToken() ? true : false));
        Log.v("test","AccessToken.getCurrentAccessToken().getToken()이 널인가? "+(null != AccessToken.getCurrentAccessToken().getToken() ? true : false));
        Log.v("test","AccessToken.getCurrentAccessToken().getToken().isEmpty()이 널인가? "+(AccessToken.getCurrentAccessToken().getToken().isEmpty() ? true : false));
        Log.v("test","사용자의 정보를 요청합니다.");

        if (null != AccessToken.getCurrentAccessToken() && null != AccessToken.getCurrentAccessToken().getToken() && !AccessToken.getCurrentAccessToken().getToken().isEmpty()) {

            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        if (null == object) {
                            LoginManager.getInstance().logOut();
                            requestFacebookLogin();
                            Log.v("test","오브젝트가 널이다.");
                            return;
                        }

                        if (!object.has("email") || object.getString("email").isEmpty()) {
                            mListener.onError(new Exception("이메일이 등록된 페이스북 아이디만 가입이 가능합니다."));
                            Log.v("test","이메일이 없다.");
                            return;
                        }

                        SnsModel model = new SnsModel();

                        model.setAccessToken(response.getRequest().getAccessToken().getToken());
                        model.setSNSID(object.getString("id"));
                        model.setUserEmail(object.getString("email"));
                        model.setUserName(object.getString("name"));
                        model.setAvatarImageUrl("http://graph.facebook.com/" + model.getSNSID() + "/picture?type=large");
                        Log.v("test",model.toString());
                        mListener.onCompleted(model, SNS_LOGIN_TYPE_FACEBOOK);
                    } catch (JSONException e) {
                       Log.v("test","정보조회 실패"+e.toString());
                        e.printStackTrace();
                        LoginManager.getInstance().logOut();
                        requestFacebookLogin();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
            return;
        }
        else
        {
            Log.v("test","여기걸림");
        }

        requestFacebookLogin();
    }
}
