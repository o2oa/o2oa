package jiguang.chat.activity.historyfile.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;
import java.util.ArrayList;

import jiguang.chat.activity.BaseActivity;
import jiguang.chat.utils.BitmapLoader;
import jiguang.chat.view.ImgBrowserViewPager;
import jiguang.chat.view.PhotoView;

/**
 * Created by ${chenyn} on 2017/8/24.
 */

public class BrowserFileImageActivity extends BaseActivity {
    private ArrayList<String> mPathList;
    private PhotoView photoView;
    private ImgBrowserViewPager mViewPager;
    private int mPosition;
    private ImageButton mReturnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_file_image);

        mPathList = getIntent().getStringArrayListExtra("historyImagePath");
        mPosition = getIntent().getIntExtra("position", 0);
        mViewPager = (ImgBrowserViewPager) findViewById(R.id.img_browser_viewpager);
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mViewPager.setAdapter(mPagerAdapter);
        //设置显示点击的那张图片
        mViewPager.setCurrentItem(mPosition);

        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            photoView = new PhotoView(true, container.getContext());
            photoView.setTag(position);
            String path = mPathList.get(position);
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    Bitmap bitmap = BitmapLoader.getBitmapFromFile(path, mWidth, mHeight);
                    if (bitmap != null) {
                        photoView.setImageBitmap(bitmap);
                    } else {
                        photoView.setImageResource(R.drawable.jmui_picture_not_found);
                    }
                } else {
                    photoView.setImageResource(R.drawable.jmui_picture_not_found);
                }
            }
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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
        public int getCount() {
            return mPathList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    };
}
