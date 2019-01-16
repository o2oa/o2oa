package jiguang.chat.utils.imagepicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.utils.imagepicker.bean.ImageItem;
import jiguang.chat.utils.imagepicker.util.BitmapUtil;
import jiguang.chat.utils.imagepicker.view.CropImageView;


public class ImageCropActivity extends ImageBaseActivity implements View.OnClickListener, CropImageView.OnBitmapSaveCompleteListener {

    private CropImageView mCropImageView;
    private Bitmap mBitmap;
    private boolean mIsSaveRectangle;
    private int mOutputX;
    private int mOutputY;
    private ArrayList<ImageItem> mImageItems;
    private ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        imagePicker = ImagePicker.getInstance();

        //初始化View
        findViewById(R.id.btn_back).setOnClickListener(this);
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setText("完成");
        btn_ok.setOnClickListener(this);
        TextView tv_des = (TextView) findViewById(R.id.tv_des);
        tv_des.setText("图片裁剪");
        mCropImageView = (CropImageView) findViewById(R.id.cv_crop_image);
        mCropImageView.setOnBitmapSaveCompleteListener(this);

        //获取需要的参数
        mOutputX = imagePicker.getOutPutX();
        mOutputY = imagePicker.getOutPutY();
        mIsSaveRectangle = imagePicker.isSaveRectangle();
        mImageItems = imagePicker.getSelectedImages();
        String imagePath = mImageItems.get(0).path;

        mCropImageView.setFocusStyle(imagePicker.getStyle());
        mCropImageView.setFocusWidth(imagePicker.getFocusWidth());
        mCropImageView.setFocusHeight(imagePicker.getFocusHeight());

        //缩放图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeFile(imagePath, options);
//        mCropImageView.setImageBitmap(mBitmap);
        //设置默认旋转角度
        mCropImageView.setImageBitmap(mCropImageView.rotate(mBitmap, BitmapUtil.getBitmapDegree(imagePath)));

//        mCropImageView.setImageURI(Uri.fromFile(new File(imagePath)));
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.btn_ok) {
            mCropImageView.saveBitmapToFile(imagePicker.getCropCacheFolder(this), mOutputX, mOutputY, mIsSaveRectangle);
        }
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
//        Toast.makeText(ImageCropActivity.this, "裁剪成功:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        //裁剪后替换掉返回数据的内容，但是不要改变全局中的选中数据
        mImageItems.remove(0);
        ImageItem imageItem = new ImageItem();
        imageItem.path = file.getAbsolutePath();
        mImageItems.add(imageItem);

        Intent intent = new Intent();
        intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mImageItems);
        setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
        finish();
    }

    @Override
    public void onBitmapSaveError(File file) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBitmap && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
