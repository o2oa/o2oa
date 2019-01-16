package jiguang.chat.activity.receiptmessage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.List;

import jiguang.chat.adapter.ViewPagerAdapter;

/**
 * Created by ${chenyn} on 2017/9/5.
 */

public class ReceiptMessageListActivity extends FragmentActivity implements ViewPager.OnPageChangeListener,
        View.OnClickListener{

    private MessageNotReadFragment mNoRead;
    private MessageAlreadyReadFragment mAlreadyRead;
    private ViewPager mReceipt_viewPager;
    private ImageButton mReturnBtn;
    private int[] mBtnIdArray;
    private int[] mIVIdArray;
    private TextView[] mTextViewArray;
    private View[] mViewArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_message_list);

        initView();
        initFragment();
    }

    private void initView() {
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mReceipt_viewPager = (ViewPager) findViewById(R.id.receipt_message);
        //未读.已读
        mBtnIdArray = new int[] {R.id.tv_noRead, R.id.tv_alreadyRead};
        //下划线
        mIVIdArray = new int[] {R.id.line_noRead, R.id.line_alreadyRead};

        mTextViewArray = new TextView[mBtnIdArray.length];
        mViewArray = new View[mBtnIdArray.length];

        //拿到上面数组中的四个组件
        for (int i = 0; i < mBtnIdArray.length; i++) {
            mTextViewArray[i] = (TextView) findViewById(mBtnIdArray[i]);
            mViewArray[i] = findViewById(mIVIdArray[i]);
        }

        //先设置显示未读的文字和下划线
        mViewArray[0].setVisibility(View.VISIBLE);
        mTextViewArray[0].setTextColor(getResources().getColor(R.color.send_file_action_bar_selected));

        mTextViewArray[0].setText("未读(" + getIntent().getIntExtra("noReadCount", 0) + ")");
        mTextViewArray[1].setText("已读(" + getIntent().getIntExtra("alreadyReadCount", 0) + ")");

        mReturnBtn.setOnClickListener(this);
        mReceipt_viewPager.addOnPageChangeListener(this);
        //设置未读和已读响应点击事件
        for (int i = 0; i < mBtnIdArray.length; i++) {
            mTextViewArray[i].setOnClickListener(this);
        }

    }

    private void initFragment() {
        long groupIdForReceipt = getIntent().getLongExtra("groupIdForReceipt", 0);
        List<Fragment> fragments = new ArrayList<>();
        mNoRead = new MessageNotReadFragment(groupIdForReceipt);
        mAlreadyRead = new MessageAlreadyReadFragment(groupIdForReceipt);

        fragments.add(mNoRead);
        fragments.add(mAlreadyRead);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                fragments);

        mReceipt_viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mReceipt_viewPager.setCurrentItem(position);
        for (int i = 0; i < mBtnIdArray.length; i++) {
            if (i == position) {
                mViewArray[i].setVisibility(View.VISIBLE);
                mTextViewArray[i].setTextColor(getResources().getColor(R.color.send_file_action_bar_selected));
            } else {
                mViewArray[i].setVisibility(View.INVISIBLE);
                mTextViewArray[i].setTextColor(getResources().getColor(R.color.send_file_action_bar));
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.return_btn) {
            finish();

        } else if (i == R.id.tv_noRead) {
            mReceipt_viewPager.setCurrentItem(0);

        } else if (i == R.id.tv_alreadyRead) {
            mReceipt_viewPager.setCurrentItem(1);

        } else {
        }
    }
}
