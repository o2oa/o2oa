package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.adapter.ForwardMsgAdapter;
import jiguang.chat.controller.ActivityController;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.HandleResponseCode;


/**
 * Created by ${chenyn} on 2017/7/16.
 */

public class ForwardMsgActivity extends BaseActivity {

    private LinearLayout mLl_groupAll;
    private LinearLayout mSearch_title;
    private ListView mListView;
    private ForwardMsgAdapter mAdapter;
    private Dialog mDialog;
    private List<Conversation> forwardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_msg);
        ActivityController.addActivity(this);

        initView();
        initData();
    }

    private void initView() {
        if (getIntent().getFlags() == 1) {
            initTitle(true, true, "发送名片", "", false, "");
        } else {
            initTitle(true, true, "转发", "", false, "");
        }

        mListView = (ListView) findViewById(R.id.forward_business_list);
        mLl_groupAll = (LinearLayout) findViewById(R.id.ll_groupAll);
        mSearch_title = (LinearLayout) findViewById(R.id.search_title);
    }

    private void initData() {
        List<Conversation> conversationList = JMessageClient.getConversationList();
        for (Conversation conv : conversationList) {
            if (!conv.getTargetId().equals("feedback_Android")) {
                forwardList.add(conv);
            }
        }
        mAdapter = new ForwardMsgAdapter(this, forwardList);
        mListView.setAdapter(mAdapter);

        //搜索栏
        mSearch_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForwardMsgActivity.this, SearchContactsActivity.class);
                setExtraIntent(intent);
            }
        });

        //群组
        mLl_groupAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForwardMsgActivity.this, GroupActivity.class);
                setExtraIntent(intent);
            }
        });

        //最近联系人
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Object itemAtPosition = parent.getItemAtPosition(position);
                //发送名片
                final Intent intent = getIntent();
                final Conversation conversation = (Conversation) itemAtPosition;
                if (intent.getFlags() == 1) {
                    String toName = conversation.getTitle();
                    setBusinessCard(toName, intent, conversation);
                    //转发消息
                } else {
                    DialogCreator.createForwardMsg(ForwardMsgActivity.this, mWidth, true, conversation, null, null, null);
                }
            }
        });
    }

    private void setExtraIntent(Intent intent) {
        //发送名片,ForwardMsgActivity跳转过来
        if (getIntent().getFlags() == 1) {
            intent.setFlags(2);
            intent.putExtra("userName", getIntent().getStringExtra("userName"));
            intent.putExtra("appKey", getIntent().getStringExtra("appKey"));
            intent.putExtra("avatar", getIntent().getStringExtra("avatar"));
        } else {
            //转发消息,启动群组界面,设置flag标识为1
            intent.setFlags(1);
        }
        startActivity(intent);
    }

    private void setBusinessCard(String name, final Intent intent, final Conversation conversation) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancel:
                        mDialog.dismiss();
                        break;
                    case R.id.btn_sure:
                        mDialog.dismiss();
                        //把名片的userName和appKey通过extra发送给对方
                        TextContent content = new TextContent("推荐了一张名片");
                        content.setStringExtra("userName", intent.getStringExtra("userName"));
                        content.setStringExtra("appKey", intent.getStringExtra("appKey"));
                        content.setStringExtra("businessCard", "businessCard");

                        Message textMessage = conversation.createSendMessage(content);
                        MessageSendingOptions options = new MessageSendingOptions();
                        options.setNeedReadReceipt(false);
                        JMessageClient.sendMessage(textMessage, options);
                        textMessage.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    Toast.makeText(ForwardMsgActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    HandleResponseCode.onHandle(ForwardMsgActivity.this, i, false);
                                }
                            }
                        });
                        break;
                }
            }
        };
        mDialog = DialogCreator.createBusinessCardDialog(ForwardMsgActivity.this, listener, name,
                intent.getStringExtra("userName"), intent.getStringExtra("avatar"));
        mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
    }
}
