package jiguang.chat.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import jiguang.chat.application.JGApplication;
import jiguang.chat.controller.ContactsController;
import jiguang.chat.database.FriendEntry;
import jiguang.chat.database.FriendRecommendEntry;
import jiguang.chat.database.UserEntry;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.entity.FriendInvitation;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.ThreadUtil;
import jiguang.chat.utils.pinyin.HanziToPinyin;
import jiguang.chat.view.ContactsView;

/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class ContactsFragment extends BaseFragment {
    private View mRootView;
    private ContactsView mContactsView;
    private ContactsController mContactsController;
    private Activity mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        mRootView = layoutInflater.inflate(R.layout.fragment_contacts,
                (ViewGroup) getActivity().findViewById(R.id.main_view), false);

        mContactsView = (ContactsView) mRootView.findViewById(R.id.contacts_view);

        mContactsView.initModule(mRatio, mDensity);
        mContactsController = new ContactsController(mContactsView, this.getActivity());

        mContactsView.setOnClickListener(mContactsController);
        mContactsView.setListener(mContactsController);
        mContactsView.setSideBarTouchListener(mContactsController);
        mContactsController.initContacts();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mContactsView.showContact();
        mContactsController.refreshContact();
        //如果放到数据库做.能提高效率和网络状态不好的情况,但是不能实时获取在其他终端修改后的搜索匹配.
        //为搜索群组做准备
        JGApplication.mGroupInfoList.clear();
        ThreadUtil.runInThread(new Runnable() {
            @Override
            public void run() {
                JMessageClient.getGroupIDList(new GetGroupIDListCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<Long> groupIDList) {
                        if (responseCode == 0) {
                            for (final Long groupID : groupIDList) {
                                JMessageClient.getGroupInfo(groupID, new GetGroupInfoCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                                        if (responseCode == 0) {
                                            JGApplication.mGroupInfoList.add(groupInfo);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        //为搜索好友做准备
        if (JGApplication.mFriendInfoList != null)
            JGApplication.mFriendInfoList.clear();
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                if (i == 0) {
                    JGApplication.mFriendInfoList = list;
                }
            }
        });
    }

    //接收到好友事件
    public void onEvent(ContactNotifyEvent event) {
        final UserEntry user = JGApplication.getUserEntry();
        final String reason = event.getReason();
        final String username = event.getFromUsername();
        final String appKey = event.getfromUserAppKey();
        //对方接收了你的好友请求
        if (event.getType() == ContactNotifyEvent.Type.invite_accepted) {
            JMessageClient.getUserInfo(username, appKey, new GetUserInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                    if (responseCode == 0) {
                        String name = info.getNickname();
                        if (TextUtils.isEmpty(name)) {
                            name = info.getUserName();
                        }
                        FriendEntry friendEntry = FriendEntry.getFriend(user, username, appKey);
                        if (friendEntry == null) {
                            final FriendEntry newFriend = new FriendEntry(info.getUserID(), username, info.getNotename(), info.getNickname(), appKey, info.getAvatar(), name, getLetter(name), user);
                            newFriend.save();
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContactsController.refresh(newFriend);
                                }
                            });
                        }
                    }
                }
            });
            FriendRecommendEntry entry = FriendRecommendEntry.getEntry(user, username, appKey);
            entry.state = FriendInvitation.ACCEPTED.getValue();
            entry.save();

            Conversation conversation = JMessageClient.getSingleConversation(username);
            if (conversation == null) {
                conversation = Conversation.createSingleConversation(username);
                EventBus.getDefault().post(new Event.Builder()
                        .setType(EventType.createConversation)
                        .setConversation(conversation)
                        .build());
            }

            //拒绝好友请求
        } else if (event.getType() == ContactNotifyEvent.Type.invite_declined) {
            JGApplication.forAddFriend.remove(username);
            FriendRecommendEntry entry = FriendRecommendEntry.getEntry(user, username, appKey);
            entry.state = FriendInvitation.BE_REFUSED.getValue();
            entry.reason = reason;
            entry.save();
            //收到好友邀请
        } else if (event.getType() == ContactNotifyEvent.Type.invite_received) {
            //如果同一个人申请多次,则只会出现一次;当点击进验证消息界面后,同一个人再次申请则可以收到
            if (JGApplication.forAddFriend.size() > 0) {
                for (String forAdd : JGApplication.forAddFriend) {
                    if (forAdd.equals(username)) {
                        return;
                    } else {
                        JGApplication.forAddFriend.add(username);
                    }
                }
            } else {
                JGApplication.forAddFriend.add(username);
            }
            JMessageClient.getUserInfo(username, appKey, new GetUserInfoCallback() {
                @Override
                public void gotResult(int status, String desc, UserInfo userInfo) {
                    if (status == 0) {
                        String name = userInfo.getNickname();
                        if (TextUtils.isEmpty(name)) {
                            name = userInfo.getUserName();
                        }
                        FriendRecommendEntry entry = FriendRecommendEntry.getEntry(user, username, appKey);
                        if (null == entry) {
                            if (null != userInfo.getAvatar()) {
                                String path = userInfo.getAvatarFile().getPath();
                                entry = new FriendRecommendEntry(userInfo.getUserID(), username, userInfo.getNotename(), userInfo.getNickname(), appKey, path,
                                        name, reason, FriendInvitation.INVITED.getValue(), user, 0);
                            } else {
                                entry = new FriendRecommendEntry(userInfo.getUserID(), username, userInfo.getNotename(), userInfo.getNickname(), appKey, null,
                                        username, reason, FriendInvitation.INVITED.getValue(), user, 0);
                            }
                        } else {
                            entry.state = FriendInvitation.INVITED.getValue();
                            entry.reason = reason;
                        }
                        entry.save();
                        //收到好友请求数字 +1
                        int showNum = SharePreferenceManager.getCachedNewFriendNum() + 1;
                        mContactsView.showNewFriends(showNum);
                        SharePreferenceManager.setCachedNewFriendNum(showNum);
                    }
                }
            });
        } else if (event.getType() == ContactNotifyEvent.Type.contact_deleted) {
            JGApplication.forAddFriend.remove(username);
            FriendEntry friendEntry = FriendEntry.getFriend(user, username, appKey);
            friendEntry.delete();
            mContactsController.refreshContact();
        }
    }

    public void onEventMainThread(Event event) {
        if (event.getType() == EventType.addFriend) {
            FriendRecommendEntry recommendEntry = FriendRecommendEntry.getEntry(event.getFriendId());
            if (null != recommendEntry) {
                FriendEntry friendEntry = FriendEntry.getFriend(recommendEntry.user,
                        recommendEntry.username, recommendEntry.appKey);
                if (null == friendEntry) {
                    friendEntry = new FriendEntry(recommendEntry.uid, recommendEntry.username, recommendEntry.noteName, recommendEntry.nickName, recommendEntry.appKey,
                            recommendEntry.avatar, recommendEntry.displayName,
                            getLetter(recommendEntry.displayName), recommendEntry.user);
                    friendEntry.save();
                    mContactsController.refresh(friendEntry);
                }
            }
        }
    }

    private String getLetter(String name) {
        String letter;
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance()
                .get(name);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (token.type == HanziToPinyin.Token.PINYIN) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        String sortString = sb.toString().substring(0, 1).toUpperCase();
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase();
        } else {
            letter = "#";
        }
        return letter;
    }
}
