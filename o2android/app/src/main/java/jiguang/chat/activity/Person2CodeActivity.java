package jiguang.chat.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import jiguang.chat.model.User;
import jiguang.chat.model.UserModel;
import jiguang.chat.pickerimage.utils.AttachmentStore;
import jiguang.chat.pickerimage.utils.StorageUtil;

/**
 * Created by ${chenyn} on 2017/8/16.
 */

public class Person2CodeActivity extends BaseActivity {

    private static final String SD_PATH = "/sdcard/dskqxt/pic/";
    private static final String IN_PATH = "/dskqxt/pic/";

    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_userName)
    TextView tvUserName;
    @BindView(R.id.iv_erWeiMa)
    ImageView ivErWeiMa;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.ll_copy)
    LinearLayout llCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person2code);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        Intent intent = getIntent();

        if (intent.getStringExtra("avatar") != null) {
            ivAvatar.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra("avatar")));
        }
        //根据用户名和appkey生成二维码bitmap返回
        String appKey = intent.getStringExtra("appkey");
        String userName = intent.getStringExtra("username");
        String platform = "a";
        tvUserName.setText("用户名: " + userName);
        Gson gson = new Gson();
        User user = new User(appKey, userName, platform);

        //生成json然后根据json生成二维码
        //json格式 {"type":"user","user":{"appkey":"4f7aef34fb361292c566a1cd","platform":"a","username":"nnnn"}}
        UserModel<User> userModel = new UserModel<>("user", user);
        String toErWeiMa = gson.toJson(userModel);

        Bitmap bitmap = null;
        try {
//            bitmap = EncodingHandler.create2Code(toErWeiMa, 600);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ivErWeiMa.setImageBitmap(bitmap);
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存二维码的底部弹窗
                final Dialog photoDialog = new Dialog(Person2CodeActivity.this, R.style.jmui_default_dialog_style);
                LayoutInflater inflater = LayoutInflater.from(Person2CodeActivity.this);
                View view = inflater.inflate(R.layout.save_erweima, null);
                photoDialog.setContentView(view);
                Window window = photoDialog.getWindow();
                window.setWindowAnimations(R.style.mystyle); // 添加动画
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                photoDialog.show();
                photoDialog.setCanceledOnTouchOutside(true);
                Button savePhoto = (Button) view.findViewById(R.id.btn_save);
                Button cancel = (Button) view.findViewById(R.id.btn_cancel);

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.btn_save) {
                            //截屏dialog并保存到手机
                            String path = screenShotView(llCopy);
                            savePicture(path);
                            photoDialog.dismiss();

                        } else {
                            photoDialog.cancel();
                        }
                    }
                };
                savePhoto.setOnClickListener(listener);
                cancel.setOnClickListener(listener);
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String screenShotView(View view) {
        Bitmap temBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temBitmap);
        view.draw(canvas);

        return saveBitmap(Person2CodeActivity.this, temBitmap);
    }


    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public void savePicture(String path) {
        if (path == null) {
            return;
        }

        String picPath = StorageUtil.getSystemImagePath();
        String dstPath = picPath + path;
        if (AttachmentStore.copy(path, dstPath) != -1) {
            try {
                ContentValues values = new ContentValues(2);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.DATA, dstPath);
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Toast.makeText(Person2CodeActivity.this, getString(R.string.picture_save_to), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(Person2CodeActivity.this, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(Person2CodeActivity.this, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show();
        }
    }


}
