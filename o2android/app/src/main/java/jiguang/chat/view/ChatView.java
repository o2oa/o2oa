package jiguang.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import cn.jpush.im.android.api.model.Conversation;
import jiguang.chat.activity.ChatActivity;
import jiguang.chat.adapter.ChattingListAdapter;
import jiguang.chat.view.listview.DropDownListView;

/**
 * Created by ${chenyn} on 2017/3/28.
 */

public class ChatView extends RelativeLayout {
    Context mContext;
    private ImageButton mReturnButton;
    private TextView mGroupNumTv;
    private ImageButton mRightBtn;
    private DropDownListView mChatListView;
    private Conversation mConv;
    private Button mAtMeBtn;

    private TextView mChatTitle;

    public ChatView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initModule(float density, int densityDpi) {
        mReturnButton = (ImageButton) findViewById(R.id.jmui_return_btn);
        mGroupNumTv = (TextView) findViewById(R.id.jmui_group_num_tv);
        mRightBtn = (ImageButton) findViewById(R.id.jmui_right_btn);
        mChatTitle = (TextView) findViewById(R.id.jmui_title);
        mAtMeBtn = (Button) findViewById(R.id.jmui_at_me_btn);
        if (densityDpi <= 160) {
            mChatTitle.setMaxWidth((int)(180 * density + 0.5f));
        }else if (densityDpi <= 240) {
            mChatTitle.setMaxWidth((int)(190 * density + 0.5f));
        }else {
            mChatTitle.setMaxWidth((int)(200 * density + 0.5f));
        }
        mChatListView = (DropDownListView) findViewById(R.id.lv_chat);

    }

    public void setToPosition(int position) {
        mChatListView.smoothScrollToPosition(position);
        mAtMeBtn.setVisibility(GONE);
    }

    public void setChatListAdapter(ChattingListAdapter chatAdapter) {
        mChatListView.setAdapter(chatAdapter);
    }

    public DropDownListView getListView() {
        return mChatListView;
    }
    public void setToBottom() {
        mChatListView.clearFocus();
        mChatListView.post(new Runnable() {
            @Override
            public void run() {
                mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
            }
        });
    }
    public void setConversation(Conversation conv) {
        this.mConv = conv;
    }

    public void setGroupIcon() {
        mRightBtn.setImageResource(R.drawable.jmui_group_chat_detail);
    }

    public void setListeners(ChatActivity listeners) {
        mReturnButton.setOnClickListener(listeners);
        mRightBtn.setOnClickListener(listeners);
        mAtMeBtn.setOnClickListener(listeners);
    }

    public void dismissRightBtn() {
        mRightBtn.setVisibility(View.GONE);
    }

    public void showRightBtn() {
        mRightBtn.setVisibility(View.VISIBLE);
    }

    public void setChatTitle(int id, int count) {
        mChatTitle.setText(id);
        mGroupNumTv.setText("(" + count + ")");
        mGroupNumTv.setVisibility(View.VISIBLE);
    }

    public void setChatTitle(int id) {
        mChatTitle.setText(id);
    }
    public void showAtMeButton() {
        mAtMeBtn.setVisibility(VISIBLE);
    }


    //设置群聊名字
    public void setChatTitle(String name, int count) {
        mChatTitle.setText(name);
        mGroupNumTv.setText("(" + count + ")");
        mGroupNumTv.setVisibility(View.VISIBLE);
    }

    public void setChatTitle(String title) {
        mChatTitle.setText(title);
    }
    public void dismissGroupNum() {
        mGroupNumTv.setVisibility(View.GONE);
    }
}
