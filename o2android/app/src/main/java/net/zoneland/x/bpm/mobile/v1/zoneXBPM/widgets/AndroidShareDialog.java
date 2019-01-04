package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FancyLou on 2016/1/8.
 */
public class AndroidShareDialog extends Dialog implements AdapterView.OnItemClickListener  {
    private GridView mGridView;
    private float mDensity;
    private String msgText = "分享内容";
    private String mImgPath;
    private int mScreenOrientation;
    private List<ShareItem> mListData;
    private Handler mHandler = new Handler();

//    private Runnable work = new Runnable() {
//        public void run() {
//            int orient = getScreenOrientation();
//            if (orient != mScreenOrientation) {
//                if (orient == 0)
//                    mGridView.setNumColumns(4);
//                else {
//                    mGridView.setNumColumns(6);
//                }
//                mScreenOrientation = orient;
//                ((AndroidShareDialog.MyAdapter) mGridView.getAdapter()).notifyDataSetChanged();
//            }
//            mHandler.postDelayed(this, 1000L);
//        }
//    };

    public AndroidShareDialog(Context context) {
        super(context, R.style.shareDialogTheme);
    }

    public AndroidShareDialog(Context context, int theme, String msgText, final String imgUri) {
        super(context, theme);
        this.msgText = msgText;

        if(TextUtils.isEmpty(imgUri)){
            this.mImgPath = null;// getAppLauncherPath();
        }else{
            if (Patterns.WEB_URL.matcher(imgUri).matches()) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            mImgPath = getImagePath(imgUri, getFileCache());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                this.mImgPath = imgUri;
            }
        }
    }

    public AndroidShareDialog(Context context, String msgText, final String imgUri) {
        super(context, R.style.shareDialogTheme);
        this.msgText = msgText;

        if(TextUtils.isEmpty(imgUri)){
            this.mImgPath = null; //getAppLauncherPath();
        }else{
            if (Patterns.WEB_URL.matcher(imgUri).matches()) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            mImgPath = getImagePath(imgUri, getFileCache());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                this.mImgPath = imgUri;
            }
        }

    }

    void init(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.mDensity = dm.density;
        this.mListData = new ArrayList<ShareItem>();
        this.mListData.add(new ShareItem(context.getString(R.string.share_wx), R.mipmap.share_wx,
                "com.tencent.mm.ui.tools.ShareImgUI", "com.tencent.mm"));

//        this.mListData.add(new ShareItem(context.getString(R.string.share_friends), R.mipmap.share_friends,
//                "com.tencent.mm.ui.tools.ShareToTimeLineUI", "com.tencent.mm"));

        this.mListData.add(new ShareItem(context.getString(R.string.share_qq), R.mipmap.share_qq,
                "com.tencent.mobileqq.activity.JumpActivity","com.tencent.mobileqq"));

//        this.mListData.add(new ShareItem(context.getString(R.string.share_douban), R.mipmap.share_douban,
//                "com.douban.frodo.activity.StatusEditActivity","com.douban.frodo"));
//
//        this.mListData.add(new ShareItem(context.getString(R.string.share_weibo), R.mipmap.share_weibo,
//                "com.sina.weibo.EditActivity", "com.sina.weibo"));

        this.mListData.add(new ShareItem(context.getString(R.string.share_copy), R.mipmap.share_copy,
                "", ""));

        this.mListData.add(new ShareItem(context.getString(R.string.share_more), R.mipmap.share_more,
                "",""));

    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        init(context);
        setContentView(R.layout.dialog_share);
        //ui
        this.mGridView = (GridView) findViewById(R.id.grid_view_share_dialog);
        this.mGridView.setHorizontalSpacing((int) (10.0F * this.mDensity));
        this.mGridView.setVerticalSpacing((int) (10.0F * this.mDensity));
        this.mGridView.setStretchMode(GridView.STRETCH_SPACING);
        this.mGridView.setColumnWidth((int) (90.0F * this.mDensity));
        this.mGridView.setHorizontalScrollBarEnabled(false);
        this.mGridView.setVerticalScrollBarEnabled(false);

        getWindow().setGravity(80);

        this.mScreenOrientation = 0;
        this.mGridView.setNumColumns(4);
//        if (getScreenOrientation() == 0) {
//            this.mScreenOrientation = 0;
//            this.mGridView.setNumColumns(4);
//        } else {
//            this.mGridView.setNumColumns(6);
//            this.mScreenOrientation = 1;
//        }
        this.mGridView.setAdapter(new MyAdapter(context));
        this.mGridView.setOnItemClickListener(this);

//        this.mHandler.postDelayed(this.work, 1000L);

        setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
//                mHandler.removeCallbacks(work);
            }
        });
    }

    public void show() {
        super.show();
    }

    public int getScreenOrientation() {
        int landscape = 0;
        int portrait = 1;
        Point pt = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getSize(pt);
        int width = pt.x;
        int height = pt.y;
        return width > height ? portrait : landscape;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ShareItem share = this.mListData.get(position);
        shareMsg(getContext(), getContext().getString(R.string.share_to), this.msgText, this.mImgPath, share);
    }

    private void shareMsg(Context context, String msgTitle, String msgText,
                          String imgPath, ShareItem share) {
        if (!share.packageName.isEmpty() && !isAvailable(getContext(), share.packageName)) {
            Toast.makeText(getContext(), context.getString(R.string.share_uninstall_app) + share.title, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        if ((imgPath == null) || (imgPath.equals(""))) {
            intent.setType("text/plain");
        } else {
            File f = new File(imgPath);
            if ((f != null) && (f.exists()) && (f.isFile())) {
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(!share.packageName.isEmpty()) {
            intent.setComponent(new ComponentName(share.packageName,share.activityName));
            context.startActivity(intent);
        }else {
            if(share.title.equals(context.getString(R.string.share_more))){
                //自带的分享控件
                context.startActivity(Intent.createChooser(intent, msgTitle));
            }else{
                //复制链接
                copyTextToClipboard(msgText, context);
            }
        }
    }

    public boolean isAvailable(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    private File getFileCache() {
        File cache = null;

        if (Environment.getExternalStorageState().equals("mounted"))
            cache = new File(Environment.getExternalStorageDirectory() + "/." + getContext().getPackageName());
        else {
            cache = new File(getContext().getCacheDir().getAbsolutePath() + "/." + getContext().getPackageName());
        }
        if ((cache != null) && (!cache.exists())) {
            cache.mkdirs();
        }
        return cache;
    }



    public String getImagePath(String imageUrl, File cache) throws Exception {
        String name = imageUrl.hashCode() + imageUrl.substring(imageUrl.lastIndexOf("."));
        File file = new File(cache, name);

        if (file.exists()) {
            return file.getAbsolutePath();
        }

        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            is.close();
            fos.close();

            return file.getAbsolutePath();
        }

        return null;
    }

    private final class MyAdapter extends BaseAdapter {

        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return mListData.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0L;
        }

        private View getItemView(ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.item_dialog_share_list, parent, false);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getItemView(parent);
            }
            ImageView iv = (ImageView) convertView.findViewById(R.id.image_view_share_logo);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_share_name);
            AndroidShareDialog.ShareItem item = (AndroidShareDialog.ShareItem) mListData.get(position);
            iv.setImageResource(item.logo);
            tv.setText(item.title);
            return convertView;
        }
    }


    private class ShareItem {
        String title;
        int logo;
        String activityName;
        String packageName;

        public ShareItem(String title, int logo, String activityName, String packageName) {
            this.title = title;
            this.logo = logo;
            this.activityName = activityName;
            this.packageName = packageName;
        }
    }

    /**
     * 实现文本复制功能
     * @param content
     */
    private void copyTextToClipboard(String content, Context context)
    {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Text", content.trim());
        cmb.setPrimaryClip(clip);
        XToast.INSTANCE.toastShort(context, context.getResources().getString(R.string.share_copy_link));
    }

}
