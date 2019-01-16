package jiguang.chat.activity.historyfile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import jiguang.chat.activity.historyfile.controller.HistoryFileController;
import jiguang.chat.activity.historyfile.view.HistoryFileView;

/**
 * Created by ${chenyn} on 2017/8/23.
 */

public class HistoryFileActivity extends FragmentActivity {
    private HistoryFileView mView;
    private HistoryFileController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_file);
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        long groupId = intent.getLongExtra("groupId", 0);
        boolean isGroup = intent.getBooleanExtra("isGroup", false);

        mView = (HistoryFileView) findViewById(R.id.send_file_view);
        mView.initModule();
        mController = new HistoryFileController(this, mView, userName, groupId, isGroup);
        mView.setOnClickListener(mController);
        mView.setOnPageChangeListener(mController);
        mView.setScroll(true);
    }

    public FragmentManager getSupportFragmentManger() {
        return getSupportFragmentManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.trans_finish_in);
    }
}
