package jiguang.chat.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import jiguang.chat.application.JGApplication;
import jiguang.chat.pickerimage.utils.AttachmentStore;
import jiguang.chat.pickerimage.utils.StorageUtil;
import jiguang.chat.utils.BitmapLoader;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.NativeImageLoader;
import jiguang.chat.view.ImgBrowserViewPager;
import jiguang.chat.view.PhotoView;


//用于浏览图片
public class BrowserViewPagerActivity extends BaseActivity {

    private static String TAG = BrowserViewPagerActivity.class.getSimpleName();
    private PhotoView photoView;
    private ImgBrowserViewPager mViewPager;
    private ProgressDialog mProgressDialog;
    //存放所有图片的路径
    private List<String> mPathList = new ArrayList<String>();
    //存放图片消息的ID
    private List<Integer> mMsgIdList = new ArrayList<Integer>();
    private TextView mNumberTv;
    private Button mSendBtn;
    private CheckBox mOriginPictureCb;
    private TextView mTotalSizeTv;
    private CheckBox mPictureSelectedCb;
    private Button mLoadBtn;
    private int mPosition;
    private Conversation mConv;
    private Message mMsg;
    private boolean mFromChatActivity = true;
    //当前消息数
    private int mStart;
    private int mOffset = 18;
    private Context mContext;
    private boolean mDownloading = false;
    private int mMessageId;
    private int[] mMsgIds;
    private int mIndex = 0;
    private final UIHandler mUIHandler = new UIHandler(this);
    private BackgroundHandler mBackgroundHandler;
    private final static int DOWNLOAD_ORIGIN_IMAGE_SUCCEED = 1;
    private final static int DOWNLOAD_PROGRESS = 2;
    private final static int DOWNLOAD_COMPLETED = 3;
    private final static int SEND_PICTURE = 5;
    private final static int DOWNLOAD_ORIGIN_PROGRESS = 6;
    private final static int DOWNLOAD_ORIGIN_COMPLETED = 7;
    private final static int INITIAL_PICTURE_LIST = 0x2000;
    private final static int INIT_ADAPTER = 0x2001;
    private final static int GET_NEXT_PAGE_OF_PICTURE = 0x2002;
    private final static int SET_CURRENT_POSITION = 0x2003;
    private Dialog mDialog;


    /**
     * 用来存储图片的选中情况
     */
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageButton returnBtn;
        RelativeLayout titleBarRl, checkBoxRl;

        mContext = this;
        setContentView(R.layout.activity_image_browser);
        mViewPager = (ImgBrowserViewPager) findViewById(R.id.img_browser_viewpager);
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        mNumberTv = (TextView) findViewById(R.id.number_tv);
        mSendBtn = (Button) findViewById(R.id.pick_picture_send_btn);
        titleBarRl = (RelativeLayout) findViewById(R.id.title_bar_rl);
        checkBoxRl = (RelativeLayout) findViewById(R.id.check_box_rl);
        mOriginPictureCb = (CheckBox) findViewById(R.id.origin_picture_cb);
        mTotalSizeTv = (TextView) findViewById(R.id.total_size_tv);
        mPictureSelectedCb = (CheckBox) findViewById(R.id.picture_selected_cb);
        mLoadBtn = (Button) findViewById(R.id.load_image_btn);

        HandlerThread backgroundThread = new HandlerThread("Work on BrowserActivity");
        backgroundThread.start();
        mBackgroundHandler = new BackgroundHandler(backgroundThread.getLooper());
        final Intent intent = this.getIntent();
        long groupId = intent.getLongExtra(JGApplication.GROUP_ID, 0);
        String targetAppKey = intent.getStringExtra(JGApplication.TARGET_APP_KEY);
        mMessageId = intent.getIntExtra("msgId", 0);
        if (groupId != 0) {
            mConv = JMessageClient.getGroupConversation(groupId);
        } else {
            String targetId = intent.getStringExtra(JGApplication.TARGET_ID);
            if (targetId != null) {
                mConv = JMessageClient.getSingleConversation(targetId, targetAppKey);
            }
        }
        mStart = intent.getIntExtra("msgCount", 0);
        mPosition = intent.getIntExtra(JGApplication.POSITION, 0);
        mFromChatActivity = intent.getBooleanExtra("fromChatActivity", true);
        boolean browserAvatar = intent.getBooleanExtra("browserAvatar", false);

        returnBtn.setOnClickListener(listener);
        mSendBtn.setOnClickListener(listener);
        mLoadBtn.setOnClickListener(listener);

        // 在聊天界面中点击图片
        if (mFromChatActivity) {
            titleBarRl.setVisibility(View.GONE);
            checkBoxRl.setVisibility(View.GONE);
            //预览头像
            if (browserAvatar) {
                String path = intent.getStringExtra("avatarPath");
                photoView = new PhotoView(mFromChatActivity, mContext);
                mLoadBtn.setVisibility(View.GONE);
                try {
                    File file = new File(path);
                    mPathList.add(path);
                    mViewPager.setAdapter(pagerAdapter);
                    mViewPager.addOnPageChangeListener(onPageChangeListener);
                    if (file.exists()) {
//                        Picasso.with(mContext).load(file).into(photoView);
                        Glide.with(mContext).load(file).dontAnimate().into(photoView);
                    } else {
                        photoView.setImageBitmap(NativeImageLoader.getInstance().getBitmapFromMemCache(path));
                    }
                } catch (Exception e) {
                    photoView.setImageResource(R.drawable.jmui_picture_not_found);
                }
                //预览聊天界面中的图片
            } else {
                mBackgroundHandler.sendEmptyMessage(INITIAL_PICTURE_LIST);
            }
            // 在选择图片时点击预览图片
        } else {
            mPathList = intent.getStringArrayListExtra("pathList");
            mViewPager.setAdapter(pagerAdapter);
            mViewPager.addOnPageChangeListener(onPageChangeListener);
            int[] pathArray = intent.getIntArrayExtra("pathArray");
            //初始化选中了多少张图片
            for (int i = 0; i < pathArray.length; i++) {
                if (pathArray[i] == 1) {
                    mSelectMap.put(i, true);
                }
            }
            showSelectedNum();
            mLoadBtn.setVisibility(View.GONE);
            mViewPager.setCurrentItem(mPosition);
            String numberText = mPosition + 1 + "/" + mPathList.size();
            mNumberTv.setText(numberText);
            int currentItem = mViewPager.getCurrentItem();
            checkPictureSelected(currentItem);
            checkOriginPictureSelected();
            //第一张特殊处理
            mPictureSelectedCb.setChecked(mSelectMap.get(currentItem));
            showTotalSize();
        }
    }

    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            return mPathList.size();
        }

        /**
         * 点击某张图片预览时，系统自动调用此方法加载这张图片左右视图（如果有的话）
         */
        @Override
        public View instantiateItem(ViewGroup container, int position) {
            photoView = new PhotoView(mFromChatActivity, container.getContext());
            photoView.setTag(position);
            String path = mPathList.get(position);
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    Bitmap bitmap = BitmapLoader.getBitmapFromFile(path, mWidth, mHeight);
                    if (bitmap != null) {
                        photoView.setMaxScale(9);
                        photoView.setImageBitmap(bitmap);
                    } else {
                        photoView.setImageResource(R.drawable.jmui_picture_not_found);
                    }
                } else {
                    Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(path);
                    if (bitmap != null) {
                        photoView.setMaxScale(9);
                        photoView.setImageBitmap(bitmap);
                    } else {
                        photoView.setImageResource(R.drawable.jmui_picture_not_found);
                    }
                }
            } else {
                photoView.setImageResource(R.drawable.jmui_picture_not_found);
            }
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            //图片长按保存到手机
            onImageViewFound(photoView, path);
            return photoView;
        }

        @Override
        public int getItemPosition(Object object) {
            View view = (View) object;
            int currentPage = mViewPager.getCurrentItem();
            if (currentPage == (Integer) view.getTag()) {
                return POSITION_NONE;
            } else {
                return POSITION_UNCHANGED;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    };

    private void onImageViewFound(PhotoView photoView, final String path) {
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                View.OnClickListener listener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = v.getId();
                        if (i == R.id.jmui_delete_conv_ll) {
                            savePicture(path, mDialog);

                        } else if (i == R.id.jmui_top_conv_ll) {
                            Intent intent = new Intent(BrowserViewPagerActivity.this, ForwardMsgActivity.class);
                            JGApplication.forwardMsg.clear();
                            JGApplication.forwardMsg.add(mMsg);
                            startActivity(intent);

                        } else {
                        }
                        mDialog.dismiss();
                    }
                };
                mDialog = DialogCreator.createSavePictureDialog(mContext, listener);
                mDialog.show();
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                return false;
            }
        });
    }

    // 保存图片
    public void savePicture(String path, Dialog dialog) {
        if (TextUtils.isEmpty(path)) {
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
                Toast.makeText(mContext, getString(R.string.picture_save_to), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } catch (Exception e) {
                dialog.dismiss();
                Toast.makeText(mContext, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show();
            }
        } else {
            dialog.dismiss();
            Toast.makeText(mContext, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show();
        }
    }

    private void setLoadBtnText(ImageContent ic) {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        //保留小数点后两位
        ddf1.setMaximumFractionDigits(2);
        double size = ic.getFileSize() / 1048576.0;
        String loadText = mContext.getString(R.string.load_origin_image) + "(" + ddf1.format(size) + "M" + ")";
        mLoadBtn.setText(loadText);
    }

    /**
     * 在图片预览中发送图片，点击选择CheckBox时，触发事件
     *
     * @param currentItem 当前图片索引
     */
    private void checkPictureSelected(final int currentItem) {
        mPictureSelectedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (mSelectMap.size() + 1 <= 9) {
                    if (isChecked) {
                        mSelectMap.put(currentItem, true);
                    } else {
                        mSelectMap.delete(currentItem);
                    }
                } else if (isChecked) {
                    Toast.makeText(mContext, mContext.getString(R.string.picture_num_limit_toast), Toast.LENGTH_SHORT).show();
                    mPictureSelectedCb.setChecked(mSelectMap.get(currentItem));
                } else {
                    mSelectMap.delete(currentItem);
                }

                showSelectedNum();
                showTotalSize();
            }
        });

    }

    /**
     * 点击发送原图CheckBox，触发事件
     */
    private void checkOriginPictureSelected() {
        mOriginPictureCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (mSelectMap.size() < 1) {
                        mPictureSelectedCb.setChecked(true);
                    }
                }
            }
        });
    }

    //显示选中的图片总的大小
    private void showTotalSize() {
        if (mSelectMap.size() > 0) {
            List<String> pathList = new ArrayList<String>();
            for (int i = 0; i < mSelectMap.size(); i++) {
                pathList.add(mPathList.get(mSelectMap.keyAt(i)));
            }
            String totalSize = BitmapLoader.getPictureSize(pathList);
            String totalText = mContext.getString(R.string.origin_picture)
                    + String.format(mContext.getString(R.string.combine_title), totalSize);
            mTotalSizeTv.setText(totalText);
        } else {
            mTotalSizeTv.setText(mContext.getString(R.string.origin_picture));
        }
    }

    //显示选中了多少张图片
    private void showSelectedNum() {
        if (mSelectMap.size() > 0) {
            String sendText = mContext.getString(R.string.jmui_send) + "(" + mSelectMap.size() + "/" + "9)";
            mSendBtn.setText(sendText);
        } else {
            mSendBtn.setText(mContext.getString(R.string.jmui_send));
        }
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        //在滑动的时候更新CheckBox的状态
        @Override
        public void onPageScrolled(final int i, float v, int i2) {
            checkPictureSelected(i);
            checkOriginPictureSelected();
            mPictureSelectedCb.setChecked(mSelectMap.get(i));
        }

        @Override
        public void onPageSelected(final int i) {
            if (mFromChatActivity) {
                mMsg = mConv.getMessage(mMsgIdList.get(i));
                ImageContent ic = (ImageContent) mMsg.getContent();
                //每次选择或滑动图片，如果不存在本地图片则下载，显示大图
                if (ic.getLocalPath() == null && i != mPosition) {
//                    mLoadBtn.setVisibility(View.VISIBLE);
                    downloadImage();
                } else if (ic.getBooleanExtra("hasDownloaded") != null && !ic.getBooleanExtra("hasDownloaded")) {
                    setLoadBtnText(ic);
                    mLoadBtn.setVisibility(View.GONE);
                } else {
                    mLoadBtn.setVisibility(View.GONE);
                }
                if (i == 0) {
                    getImgMsg();
                }
            } else {
                String numText = i + 1 + "/" + mPathList.size();
                mNumberTv.setText(numText);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    /**
     * 滑动到当前页图片的第一张时，加载上一页消息中的图片
     */
    private void getImgMsg() {
//        ImageContent ic;
//        final int msgSize = mMsgIdList.size();
//        List<Message> msgList = mConv.getMessagesFromNewest(mStart, mOffset);
//        mOffset = msgList.size();
//        if (mOffset > 0) {
//            for (Message msg : msgList) {
//                if (msg.getContentType().equals(ContentType.image)) {
//                    mMsgIdList.add(0, msg.getId());
//                    ic = (ImageContent) msg.getContent();
//                    if (!TextUtils.isEmpty(ic.getLocalPath())) {
//                        mPathList.add(0, ic.getLocalPath());
//                    } else {
//                        mPathList.add(0, ic.getLocalThumbnailPath());
//                    }
//                }
//            }
//            mStart += mOffset;
//            if (msgSize == mMsgIdList.size()) {
//                getImgMsg();
//            } else {
//                //加载完上一页图片后，设置当前图片仍为加载前的那一张图片
//                mPosition = mMsgIdList.size() - msgSize;
//                mUIHandler.sendMessage(mUIHandler.obtainMessage(SET_CURRENT_POSITION, mPosition));
//            }
//        }
    }

    /**
     * 初始化会话中的所有图片路径
     */
    private void initImgPathList() {
        mMsgIdList = this.getIntent().getIntegerArrayListExtra(JGApplication.MsgIDs);
        Message msg;
        ImageContent ic;
        for (int msgID : mMsgIdList) {
            msg = mConv.getMessage(msgID);
            if (msg.getContentType().equals(ContentType.image)) {
                ic = (ImageContent) msg.getContent();
                if (!TextUtils.isEmpty(ic.getLocalPath())) {
                    mPathList.add(ic.getLocalPath());
                } else {
                    mPathList.add(ic.getLocalThumbnailPath());
                }
            }
        }
    }

    private void initCurrentItem() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, this.getString(R.string.jmui_local_picture_not_found_toast), Toast.LENGTH_SHORT).show();
        }
        mMsg = mConv.getMessage(mMessageId);
        photoView = new PhotoView(mFromChatActivity, this);
        int currentItem = mMsgIdList.indexOf(mMsg.getId());
        try {
            ImageContent ic = (ImageContent) mMsg.getContent();
            //如果点击的是第一张图片并且图片未下载过，则显示大图
            if (ic.getLocalPath() == null && mMsgIdList.indexOf(mMsg.getId()) == 0) {
                downloadImage();
            }
            String path = mPathList.get(mMsgIdList.indexOf(mMsg.getId()));
            //如果发送方上传了原图
            if (ic.getBooleanExtra("originalPicture") != null && ic.getBooleanExtra("originalPicture")) {
                mLoadBtn.setVisibility(View.GONE);
                setLoadBtnText(ic);
                photoView.setImageBitmap(BitmapLoader.getBitmapFromFile(path, mWidth, mHeight));
            } else {
//                Picasso.with(mContext).load(new File(path)).into(photoView);
                Glide.with(mContext).load(new File(path)).dontAnimate().into(photoView);
            }

            mViewPager.setCurrentItem(currentItem);
        } catch (NullPointerException e) {
            photoView.setImageResource(R.drawable.jmui_picture_not_found);
            mViewPager.setCurrentItem(currentItem);
        } finally {
            if (currentItem == 0) {
                mBackgroundHandler.sendEmptyMessage(GET_NEXT_PAGE_OF_PICTURE);
            }
        }
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.return_btn) {
                int pathArray[] = new int[mPathList.size()];
                for (int i = 0; i < pathArray.length; i++) {
                    pathArray[i] = 0;
                }
                for (int j = 0; j < mSelectMap.size(); j++) {
                    pathArray[mSelectMap.keyAt(j)] = 1;
                }
                Intent intent = new Intent();
                intent.putExtra("pathArray", pathArray);
                setResult(JGApplication.RESULT_CODE_SELECT_PICTURE, intent);
                finish();
            } else if (viewId == R.id.pick_picture_send_btn) {
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setMessage(mContext.getString(R.string.sending_hint));
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                mPosition = mViewPager.getCurrentItem();

                if (mOriginPictureCb.isChecked()) {
                    Log.i(TAG, "发送原图");
                    getOriginPictures(mPosition);
                } else {
                    Log.i(TAG, "发送缩略图");
                    getThumbnailPictures(mPosition);
                }
            } else if (viewId == R.id.load_image_btn) {
                downloadOriginalPicture();
            }

        }
    };

    private void downloadOriginalPicture() {
        final ImageContent imgContent = (ImageContent) mMsg.getContent();
        //如果不存在下载进度
        if (!mMsg.isContentDownloadProgressCallbackExists()) {
            mMsg.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double progress) {
                    android.os.Message msg = mUIHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    if (progress < 1.0) {
                        msg.what = DOWNLOAD_ORIGIN_PROGRESS;
                        bundle.putInt("progress", (int) (progress * 100));
                        msg.setData(bundle);
                        msg.sendToTarget();
                    } else {
                        msg.what = DOWNLOAD_ORIGIN_COMPLETED;
                        msg.sendToTarget();
                    }
                }
            });
            imgContent.downloadOriginImage(mMsg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int status, String desc, File file) {
                    if (status == 0) {
                        imgContent.setBooleanExtra("hasDownloaded", true);
                    } else {
                        imgContent.setBooleanExtra("hasDownloaded", false);
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                    }
                }
            });
        }
    }


    /**
     * 获得选中图片的原图路径
     *
     * @param position 选中的图片位置
     */
    private void getOriginPictures(int position) {
        if (mSelectMap.size() < 1) {
            mSelectMap.put(position, true);
        }
        mMsgIds = new int[mSelectMap.size()];
        //根据选择的图片路径生成队列
        for (int i = 0; i < mSelectMap.size(); i++) {
            createImageContent(mPathList.get(mSelectMap.keyAt(i)), true);
        }
    }

    /**
     * 获得选中图片的缩略图路径
     *
     * @param position 选中的图片位置
     */
    private void getThumbnailPictures(int position) {
        if (mSelectMap.size() < 1) {
            mSelectMap.put(position, true);
        }
        mMsgIds = new int[mSelectMap.size()];
        for (int i = 0; i < mSelectMap.size(); i++) {
            createImageContent(mPathList.get(mSelectMap.keyAt(i)), false);
        }
    }

    /**
     * 根据图片路径生成ImageContent
     *
     * @param path       图片路径
     * @param isOriginal 是否发送原图
     */
    private void createImageContent(String path, final boolean isOriginal) {
        Bitmap bitmap;
        if (isOriginal || BitmapLoader.verifyPictureSize(path)) {
            File file = new File(path);
            ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
                @Override
                public void gotResult(int status, String desc, ImageContent imageContent) {
                    if (status == 0) {
                        if (isOriginal) {
                            imageContent.setBooleanExtra("originalPicture", true);
                        }
                        Message msg = mConv.createSendMessage(imageContent);
                        mMsgIds[mIndex] = msg.getId();
                    } else {
                        mMsgIds[mIndex] = -1;
                    }
                    mIndex++;
                    if (mIndex >= mSelectMap.size()) {
                        mUIHandler.sendEmptyMessage(SEND_PICTURE);
                    }
                }
            });
        } else {
            bitmap = BitmapLoader.getBitmapFromFile(path, 720, 1280);
            ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                @Override
                public void gotResult(int status, String desc, ImageContent imageContent) {
                    if (status == 0) {
                        Message msg = mConv.createSendMessage(imageContent);
                        mMsgIds[mIndex] = msg.getId();
                    } else {
                        mMsgIds[mIndex] = -1;
                    }
                    mIndex++;
                    if (mIndex >= mSelectMap.size()) {
                        mUIHandler.sendEmptyMessage(SEND_PICTURE);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDownloading) {
            mProgressDialog.dismiss();
            //TODO cancel download image
        }
        int pathArray[] = new int[mPathList.size()];
        for (int i = 0; i < pathArray.length; i++) {
            pathArray[i] = 0;
        }
        for (int i = 0; i < mSelectMap.size(); i++) {
            pathArray[mSelectMap.keyAt(i)] = 1;
        }
        Intent intent = new Intent();
        intent.putExtra("pathArray", pathArray);
        setResult(JGApplication.RESULT_CODE_SELECT_PICTURE, intent);
        super.onBackPressed();
    }

    //每次在聊天界面点击图片或者滑动图片自动下载大图
    private void downloadImage() {
        Log.d(TAG, "Downloading image!");
        ImageContent imgContent = (ImageContent) mMsg.getContent();
        if (imgContent.getLocalPath() == null) {
            //如果不存在进度条Callback，重新注册
            if (!mMsg.isContentDownloadProgressCallbackExists()) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMessage(mContext.getString(R.string.downloading_hint));
                mDownloading = true;
                mProgressDialog.show();
                // 显示下载进度条
                mMsg.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {

                    @Override
                    public void onProgressUpdate(double progress) {
                        android.os.Message msg = mUIHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        if (progress < 1.0) {
                            msg.what = DOWNLOAD_PROGRESS;
                            bundle.putInt("progress", (int) (progress * 100));
                            msg.setData(bundle);
                            msg.sendToTarget();
                        } else {
                            msg.what = DOWNLOAD_COMPLETED;
                            msg.sendToTarget();
                        }
                    }
                });
                // msg.setContent(imgContent);
                imgContent.downloadOriginImage(mMsg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int status, String desc, File file) {
                        mDownloading = false;
                        if (status == 0) {
                            android.os.Message msg = mUIHandler.obtainMessage();
                            msg.what = DOWNLOAD_ORIGIN_IMAGE_SUCCEED;
                            Bundle bundle = new Bundle();
                            bundle.putString("path", file.getAbsolutePath());
                            bundle.putInt(JGApplication.POSITION, mViewPager.getCurrentItem());
                            msg.setData(bundle);
                            msg.sendToTarget();
                        } else {
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                        }
                    }
                });
            }
        }
    }

    private static class UIHandler extends Handler {
        private final WeakReference<BrowserViewPagerActivity> mActivity;

        public UIHandler(BrowserViewPagerActivity activity) {
            mActivity = new WeakReference<BrowserViewPagerActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            BrowserViewPagerActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case DOWNLOAD_ORIGIN_IMAGE_SUCCEED:
                        //更新图片并显示
                        Bundle bundle = msg.getData();
                        activity.mPathList.set(bundle.getInt(JGApplication.POSITION), bundle.getString("path"));
                        activity.mViewPager.getAdapter().notifyDataSetChanged();
                        activity.mLoadBtn.setVisibility(View.GONE);
                        break;
                    case DOWNLOAD_PROGRESS:
                        activity.mProgressDialog.setProgress(msg.getData().getInt("progress"));
                        break;
                    case DOWNLOAD_COMPLETED:
                        activity.mProgressDialog.dismiss();
                        break;
                    case SEND_PICTURE:
                        Intent intent = new Intent();
                        intent.putExtra(JGApplication.MsgIDs, activity.mMsgIds);
                        activity.setResult(JGApplication.RESULT_CODE_BROWSER_PICTURE, intent);
                        activity.finish();
                        break;
                    //显示下载原图进度
                    case DOWNLOAD_ORIGIN_PROGRESS:
                        String progress = msg.getData().getInt("progress") + "%";
                        activity.mLoadBtn.setText(progress);
                        break;
                    case DOWNLOAD_ORIGIN_COMPLETED:
                        activity.mLoadBtn.setText(activity.getString(R.string.download_completed_toast));
                        activity.mLoadBtn.setVisibility(View.GONE);
                        break;
                    case INIT_ADAPTER:
                        activity.mViewPager.setAdapter(activity.pagerAdapter);
                        activity.mViewPager.addOnPageChangeListener(activity.onPageChangeListener);
                        activity.initCurrentItem();
                        break;
                    case SET_CURRENT_POSITION:
                        if (activity.mViewPager != null && activity.mViewPager.getAdapter() != null) {
                            activity.mViewPager.getAdapter().notifyDataSetChanged();
                            int position = (int) msg.obj;
                            activity.mViewPager.setCurrentItem(position);
                        }
                        break;
                }
            }
        }
    }

    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INITIAL_PICTURE_LIST:
                    initImgPathList();
                    mUIHandler.sendEmptyMessage(INIT_ADAPTER);
                    break;
                case GET_NEXT_PAGE_OF_PICTURE:
                    getImgMsg();
                    break;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.trans_finish_in);
    }
}
