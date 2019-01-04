package jiguang.chat.view;

import android.view.View;
import android.widget.LinearLayout;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;


public class MenuItemView {

    private View mView;
    private LinearLayout mCreateGroupLl;
    private LinearLayout mAddFriendLl;
    private LinearLayout mSendMsgLl;
    private LinearLayout mLl_saoYiSao;

    public MenuItemView(View view) {
        this.mView = view;
    }

    public void initModule() {
        mCreateGroupLl = (LinearLayout) mView.findViewById(R.id.create_group_ll);
        mAddFriendLl = (LinearLayout) mView.findViewById(R.id.add_friend_with_confirm_ll);
        mSendMsgLl = (LinearLayout) mView.findViewById(R.id.send_message_ll);
        mLl_saoYiSao = (LinearLayout) mView.findViewById(R.id.ll_saoYiSao);
    }

    public void setListeners(View.OnClickListener listener) {
        mCreateGroupLl.setOnClickListener(listener);
        mAddFriendLl.setOnClickListener(listener);
        mSendMsgLl.setOnClickListener(listener);
        mLl_saoYiSao.setOnClickListener(listener);
    }

    public void showAddFriendDirect() {
        mAddFriendLl.setVisibility(View.GONE);
    }

    public void showAddFriend() {
        mAddFriendLl.setVisibility(View.VISIBLE);
    }

    public void setColor() {

    }
}
