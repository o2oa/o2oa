/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jiguang.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import jiguang.chat.application.JGApplication;
import jiguang.chat.model.Constant;
import jiguang.chat.model.InfoModel;
import jiguang.chat.model.User;
import jiguang.chat.model.UserModel;
import jiguang.chat.utils.HandleResponseCode;
import jiguang.chat.utils.dialog.LoadDialog;


/**
 * 二维码扫描使用
 */
public final class CommonScanActivity extends Activity implements  View.OnClickListener {
    SurfaceView scanPreview = null;
    View scanContainer;
    View scanCropView;
    ImageView scanLine;
//    ScanManager scanManager;
    TextView iv_light;
    TextView qrcode_g_gallery;
    final int PHOTOREQUESTCODE = 1111;

    @BindView(R.id.scan_image)
    ImageView scan_image;
    @BindView(R.id.authorize_return)
    ImageView authorize_return;
    private int scanMode;//扫描模型（二维码）

    @BindView(R.id.common_title_TV_center)
    TextView title;
    @BindView(R.id.scan_hint)
    TextView scan_hint;
    @BindView(R.id.tv_scan_result)
    TextView tv_scan_result;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan_code);
        ButterKnife.bind(this);
        scanMode = getIntent().getIntExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
        initView();
    }

    void initView() {
//        switch (scanMode) {
//            case DecodeThread.QRCODE_MODE:
//                title.setText(R.string.scan_qrcode_title);
//                scan_hint.setText(R.string.scan_qrcode_hint);
//                break;
//        }
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_container);
        scanCropView = findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        qrcode_g_gallery = (TextView) findViewById(R.id.qrcode_g_gallery);
        qrcode_g_gallery.setOnClickListener(this);
        iv_light = (TextView) findViewById(R.id.iv_light);
        iv_light.setOnClickListener(this);
        authorize_return.setOnClickListener(this);
        //构造出扫描管理器
//        scanManager = new ScanManager(this, scanPreview, scanContainer, scanCropView, scanLine, scanMode, this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        scanManager.onResume();
        scan_image.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
//        scanManager.onPause();
    }

    /**
     * 根据扫描二维码的结果查询user
     */
    public void scanResult(Result rawResult, Bundle bundle) {
        //扫描成功后，扫描器不会再连续扫描，如需连续扫描，调用reScan()方法。
        String jsonUser = rawResult.getText();

        //如果扫的二维码是网址,那么就访问这个网址
        if (jsonUser.startsWith("http")) {
            String substring = jsonUser.substring(0, jsonUser.indexOf("/",
                    jsonUser.indexOf("/", jsonUser.indexOf("/", 0) + 1) + 1) + 1);
            Uri uri = Uri.parse(substring);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(CommonScanActivity.this, "扫描的二维码不能解析", Toast.LENGTH_SHORT).show();
            }
        } else if (jsonUser.startsWith("{\"type")) {
            //解析根据二维码扫描出的json
            Gson gson = new Gson();
            Type userType = new TypeToken<UserModel<User>>() {
            }.getType();

            try {
                final UserModel<User> userResult = gson.fromJson(jsonUser, userType);

                final LoadDialog dialog = new LoadDialog(CommonScanActivity.this, false, "正在加载...");
                dialog.show();
                JMessageClient.getUserInfo(userResult.user.username, userResult.user.appkey, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        dialog.dismiss();
                        if (i == 0) {
                            //扫出来的是自己
                            Intent intent = new Intent();
                            if (userInfo.getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                                intent.setClass(CommonScanActivity.this, PersonalActivity.class);
                                //扫出来的是好友
                            } else if (userInfo.isFriend()) {
                                intent.setClass(CommonScanActivity.this, FriendInfoActivity.class);
                                intent.putExtra(JGApplication.TARGET_ID, userInfo.getUserName());
                                intent.putExtra(JGApplication.TARGET_APP_KEY, userInfo.getAppKey());
                                intent.putExtra("fromContact", true);
                                //扫出来的非好友
                            } else {
                                InfoModel.getInstance().friendInfo = userInfo;
                                intent.setClass(CommonScanActivity.this, SearchFriendInfoActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            HandleResponseCode.onHandle(CommonScanActivity.this, i, false);
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(CommonScanActivity.this, "扫描的二维码不能解析", Toast.LENGTH_SHORT).show();
            }
            //如果不是jchat的二维码也不是网址就把扫描出的文本显示出来
        } else {
            Intent intent = new Intent(CommonScanActivity.this, ScanResultActivity.class);
            intent.putExtra("result", jsonUser);
            startActivity(intent);
        }
    }

//
//    @Override
//    public void scanError(Exception e) {
//        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//        //相机扫描出错时
//        if (e.getMessage() != null && e.getMessage().startsWith("相机")) {
//            scanPreview.setVisibility(View.INVISIBLE);
//        }
//    }

    public void showPictures(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photo_path;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTOREQUESTCODE:
                    String[] proj = {MediaStore.Images.Media.DATA};

                    Cursor cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor == null) {
//                        scanManager.scanningImage(data.getData().getPath());
                    } else if (cursor.moveToFirst()) {
                        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(colum_index);
                        if (photo_path == null) {
//                            photo_path = Utils.getPath(getApplicationContext(), data.getData());
                        }
//                        scanManager.scanningImage(photo_path);
                    }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_light:
//                scanManager.switchLight();
                break;
            case R.id.qrcode_g_gallery:
                showPictures(PHOTOREQUESTCODE);
                break;
            case R.id.authorize_return:
                finish();
                break;
            default:
                break;
        }
    }

}