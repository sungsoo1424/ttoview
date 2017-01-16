package com.ttoview.nakayosi.ttoview.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.kakao.auth.Session;
import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;
import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.manager.BaseSnsManager;
import com.ttoview.nakayosi.ttoview.manager.FacebookLoginManager;
import com.ttoview.nakayosi.ttoview.manager.KakaoLoginManager;
import com.ttoview.nakayosi.ttoview.model.EditImageModel;
import com.ttoview.nakayosi.ttoview.model.SnsModel;
import com.ttoview.nakayosi.ttoview.util.ImageUtil;
import com.ttoview.nakayosi.ttoview.util.picker.activity.BasePickerActivity;
import com.ttoview.nakayosi.ttoview.util.picker.activity.PhotoPickerActivity;
import com.ttoview.nakayosi.ttoview.util.picker.media.BaseMediaData;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaItem;
import com.ttoview.nakayosi.ttoview.view.WriteThumbView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jeongchanghyeon on 2015. 9. 27..
 */
public class SNSWriteActivity extends BaseActivity {

    public static final int ACTIVITY_RESULT_GALLERY = 0xf1;
    private Map<String, String> ExecParam = new HashMap<>();
    private Map<String, String> marketParam = new HashMap<>();


    /**
     * 미디어 아이템 리스트
     */
    private ArrayList<BaseMediaData> mSelectedMediaDatas;

    /**
     * 선택된 이미지 폴더 리스트
     */

    private ArrayList<String> mSelectedMediaGroupIds;

    private List<WriteThumbView> mImageViewList;

    private ArrayList<EditImageModel> mEditImageModels;

    private CallbackManager mCallbackManager;


    /**
     * 이미지의 정상 사이즈 저장
     */
    private int mOriginWidth = 0;
    private int mOriginHeight = 0;


    public void onRightButton(View view) {
        try {
            EditText contentFiled = (EditText) findViewById(R.id.contentFiled);
            if (!contentFiled.getText().toString().equals("")) {
                if (mEditImageModels.size() > 0) {
                    final ProgressDialog dialog = ProgressDialog.show(SNSWriteActivity.this, "","게시중 입니다..", true);
                    List<File> fileList = new ArrayList<File>();
                    for (int i = 0; i < mEditImageModels.size(); i++) {
                        File uploadFile = new File(writeStoryImage(this, ((BitmapDrawable) mImageViewList.get(i).getThumbDrawable()).getBitmap()));
                        fileList.add(uploadFile);
                    }
                    PostRequest.StoryPermission permission = PostRequest.StoryPermission.PUBLIC;
                    KakaoStoryService.requestPostPhoto(new StoryResponseCallback<MyStoryInfo>() {

                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.d("test", "로그인 안함1");
                            onKakaoLogin();
                            dialog.dismiss();
                        }

                        @Override
                        public void onNotSignedUp() {
                            Log.d("test", "로그인 실패");
                            dialog.dismiss();
                        }

                        @Override
                        public void onNotKakaoStoryUser() {
                            Log.d("test", "카카오스토리 유저 아님");
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Log.d("test", "실패");
                            dialog.dismiss();
                        }

                        @Override
                        public void onSuccess(MyStoryInfo result) {
                            Logger.d(result.toString());
                            Log.d("test", "test : " + result.getUrl());
                            try {
                                KakaoStoryService.requestGetMyStory(new StoryResponseCallback<MyStoryInfo>() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onSuccess(MyStoryInfo result) {
                                        dialog.dismiss();
                                        if (result.getUrl() != null && !result.getUrl().equals("")) {
                                            Intent intent = new Intent(SNSWriteActivity.this, WriteActivity2.class);
                                            intent.putExtra("url", result.getUrl());
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNotKakaoStoryUser() {
                                        dialog.dismiss();
                                    }
                                }, result.getId());

                            } catch (KakaoParameterException e) {
                                e.printStackTrace();
                                dialog.dismiss();
                            }
                        }
                    }, fileList, contentFiled.getText().toString(), permission, true, ExecParam, ExecParam, marketParam, marketParam);
                } else {
                    Toast.makeText(SNSWriteActivity.this, "이미지를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SNSWriteActivity.this, "글을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }
            changeButton();
           // dialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("test",e.toString());
        } catch (KakaoParameterException e) {
            e.printStackTrace();
            Log.v("test","카카오 파라메터: "+e.toString());
        }
    }

    /**
     * 이미지 추가 버튼 핸들러
     *
     * @param view
     */
    public void onImageAddButton(View view) {
        Intent intent = new Intent(this, PhotoPickerActivity.class);
        intent.putExtra(BasePickerActivity.PICKER_OPTION_MULTI_SELECT, "");
        if (null != mSelectedMediaDatas && 0 < mSelectedMediaDatas.size()) {
            intent.putExtra("editImages", mEditImageModels);
            intent.putExtra("mediaList", mSelectedMediaDatas);
            intent.putExtra("groupIds", mSelectedMediaGroupIds);
        }
        startActivityForResult(intent, ACTIVITY_RESULT_GALLERY);
    }

    public void onCameraButton(View view) {
        Intent intent = new Intent(this, PhotoPickerActivity.class);
        intent.putExtra(BasePickerActivity.PICKER_OPTION_MULTI_SELECT, "");
        if (null != mSelectedMediaDatas && 0 < mSelectedMediaDatas.size()) {
            intent.putExtra("editImages", mEditImageModels);
            intent.putExtra("mediaList", mSelectedMediaDatas);
            intent.putExtra("groupIds", mSelectedMediaGroupIds);
        }
        startActivityForResult(intent, ACTIVITY_RESULT_GALLERY);
    }

    public void onBlogButton(View view) {

    }

    public void onKakaoButton(View view) {

        KakaoLoginManager manager = new KakaoLoginManager(this, (LoginButton) findViewById(R.id.com_kakao_login));
        manager.setOnLoginListener(new BaseSnsManager.LoginInterface() {
            @Override
            public void onCompleted(SnsModel model, String type) {
            }
            @Override
            public void onError(Exception e) {
            }
        });
        manager.requestKakaoLoginApi();
        changeButton();


    }

    public void changeButton()
    {
        ImageButton kakaoButton = (ImageButton) findViewById(R.id.kakaoButton);
        Session session = Session.getCurrentSession();
        if (null != session && session.isOpened()) {
            kakaoButton.setSelected(true);
        } else {
            kakaoButton.setSelected(false);
        }
    }

    public void onKakaoLogin() {
        KakaoLoginManager manager = new KakaoLoginManager(this, (LoginButton) findViewById(R.id.com_kakao_login));
        manager.setOnLoginListener(new BaseSnsManager.LoginInterface() {
            @Override
            public void onCompleted(SnsModel model, String type) {
            }
            @Override
            public void onError(Exception e) {
            }
        });
        manager.requestKakaoLoginApi();
    }



    public void onFacebookButton(View view) {
        FacebookLoginManager manager = new FacebookLoginManager(this, mCallbackManager);
        manager.setOnLoginListener(new BaseSnsManager.LoginInterface() {
            @Override
            public void onCompleted(SnsModel model, String type) {
                ImageButton kakaoButton = (ImageButton) findViewById(R.id.kakaoButton);
                if (Session.getCurrentSession() != null && Session.getCurrentSession().getAccessToken() != null) {
                    kakaoButton.setSelected(true);
                } else {
                    kakaoButton.setSelected(false);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        manager.requestFacebookGraphApi();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ImageButton kakaoButton = (ImageButton) findViewById(R.id.kakaoButton);
        Session session = Session.getCurrentSession();
        if (null != session && session.isOpened()) {
            kakaoButton.setSelected(true);
        } else {
            kakaoButton.setSelected(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sns_write);
        mCallbackManager = CallbackManager.Factory.create();

        mImageViewList = new ArrayList<>();
        mEditImageModels = new ArrayList<EditImageModel>();

       changeButton();

        mImageViewList = new ArrayList<WriteThumbView>();
        /**
         * ImageView Setting
         */
        mImageViewList.add((WriteThumbView) findViewById(R.id.addImage01));
        mImageViewList.add((WriteThumbView) findViewById(R.id.addImage02));
        mImageViewList.add((WriteThumbView) findViewById(R.id.addImage03));
        mImageViewList.add((WriteThumbView) findViewById(R.id.addImage04));
        mImageViewList.add((WriteThumbView) findViewById(R.id.addImage05));

        mImageViewList.get(0).post(new Runnable() {

            @Override
            public void run() {
                mOriginWidth = mImageViewList.get(0).getMeasuredWidth();
                mOriginHeight = mOriginWidth;

                setImageViewLayoutParams();
            }
        });

        EditText contentFiled = (EditText) findViewById(R.id.contentFiled);
        contentFiled.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeButton();
            }
        });
        contentFiled.requestFocus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Session.ACCESS_TOKEN_REQUEST:
                case Session.AUTHORIZATION_CODE_REQUEST:
                    try {
                        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                            return;
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    return;
            }

            setImageViewLayoutParams();

            if (Activity.RESULT_OK == resultCode && ACTIVITY_RESULT_GALLERY == requestCode) {

                try {
                    if (data.hasExtra("editImagePathList")) {
                        mSelectedMediaDatas = data.getParcelableArrayListExtra("mediaList");
                        mSelectedMediaGroupIds = data.getStringArrayListExtra("groupIds");
                        ArrayList<EditImageModel> models = data.getParcelableArrayListExtra("editImagePathList");
                        setImageList(models);
                    }
                    toggleImageAddButton();
                } catch (Exception exception) {
                }
            }
        }
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 카카오스토리에 사진파일을 전송하기 위해서 사이즈를 변경하는 메소드
     *
     * @param context
     * @param bitmap
     * @return
     * @throws IOException
     */
    private static String writeStoryImage(final Context context, final Bitmap bitmap) throws IOException {
        final File diskCacheDir = new File(context.getCacheDir(), "story");

        if (!diskCacheDir.exists())
            diskCacheDir.mkdirs();

        final String file = diskCacheDir.getAbsolutePath() + File.separator + "temp_" + System.currentTimeMillis() + ".jpg";

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file), 8 * 1024);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        return file;
    }

    private void setImageList(ArrayList<EditImageModel> editImagePathList) {

        findViewById(R.id.imageLayout).setVisibility(View.VISIBLE);

        if (0 < mEditImageModels.size()) {
            for (int i = 0; i < mImageViewList.size(); i++) {
                mImageViewList.get(i).setThumbImageBitmap(null);
                mImageViewList.get(i).setVisibility(View.GONE);
            }
        }

        mEditImageModels.clear();

        for (int i = 0; i < editImagePathList.size(); i++) {
            final int position = i;
            Bitmap selectedPhoto = ImageUtil.getBitmapLimitScale(editImagePathList.get(i).getEditMediaPath());
            mEditImageModels.add(editImagePathList.get(i));
            mImageViewList.get(i).setThumbImageBitmap(selectedPhoto);
            mImageViewList.get(i).setVisibility(View.VISIBLE);
            mImageViewList.get(i).setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageViewList.get(i).showDeleteButton(true);
            mImageViewList.get(i).setOnDeleteButtonListener(new WriteThumbView.DeleteButtonListner() {

                @Override
                public void onDeleteClick() {

                    int currentImageSize = mEditImageModels.size();
                    for (int i = position; i < currentImageSize; i++) {
                        if ((i + 1) == currentImageSize) {
                            mImageViewList.get(i).setThumbImageBitmap(null);
                            mImageViewList.get(i).setVisibility(View.GONE);
                        } else {
                            Bitmap selectedPhoto = ImageUtil.getBitmapLimitScale(mEditImageModels.get(i + 1).getEditMediaPath());
                            mImageViewList.get(i).setThumbImageBitmap(selectedPhoto);
                        }
                    }

                    if (0 != mSelectedMediaGroupIds.size()) {
                        for (int i = 0; i < mSelectedMediaGroupIds.size(); i++) {
                            if (((MediaItem) mSelectedMediaDatas.get(position)).getGroupId() == Long.parseLong(mSelectedMediaGroupIds.get(i))) {
                                mSelectedMediaGroupIds.remove(i);
                                break;
                            }
                        }
                    }

                    if (0 != mSelectedMediaDatas.size()) {
                        mSelectedMediaDatas.remove(position);
                    }

                    if (0 != mEditImageModels.size()) {
                        mEditImageModels.remove(position);
                    }

                    toggleImageAddButton();
                }
            });
        }
    }

    /**
     * 썸네일 이미지뷰의 크기 조정
     */
    private void setImageViewLayoutParams() {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mImageViewList.get(0).getLayoutParams();
        final int imageViewMargin = (int) getResources().getDimension(R.dimen.WRITE_IMAGEVIEW_MARGIN);
        params.width = mOriginWidth;
        params.height = mOriginHeight;
        params.leftMargin = imageViewMargin;
        params.rightMargin = imageViewMargin;
        for (int i = 0; i < mImageViewList.size(); i++) {
            mImageViewList.get(i).setLayoutParams(params);
        }
    }

    /**
     * 이미지 추가 버튼 활성 / 비활성 설정
     */
    private void toggleImageAddButton() {

        if (mEditImageModels.size() == 5) {
            ((ImageButton) findViewById(R.id.cameraButton)).setImageResource(R.drawable.btn_camera_on);
            findViewById(R.id.addImageDimLayout).setVisibility(View.GONE);
            findViewById(R.id.cameraButton).setClickable(false);
        } else if (mEditImageModels.size() == 0) {
            findViewById(R.id.imageLayout).setVisibility(View.GONE);
            ((ImageButton) findViewById(R.id.cameraButton)).setImageResource(R.drawable.selector_btn_camera);
            findViewById(R.id.addImageDimLayout).setVisibility(View.GONE);
            findViewById(R.id.cameraButton).setClickable(true);
        } else {
            ((ImageButton) findViewById(R.id.cameraButton)).setImageResource(R.drawable.btn_camera_on);
            findViewById(R.id.addImageDimLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.cameraButton).setClickable(true);
        }
    }
}
