package jiguang.chat.activity.historyfile.controller;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import jiguang.chat.activity.historyfile.activity.HistoryFileActivity;
import jiguang.chat.activity.historyfile.fragment.AudioFileFragment;
import jiguang.chat.activity.historyfile.fragment.DocumentFileFragment;
import jiguang.chat.activity.historyfile.fragment.ImageFileFragment;
import jiguang.chat.activity.historyfile.fragment.OtherFileFragment;
import jiguang.chat.activity.historyfile.fragment.VideoFileFragment;
import jiguang.chat.activity.historyfile.view.HistoryFileView;
import jiguang.chat.adapter.ViewPagerAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.entity.SelectedHistoryFileListener;

/**
 * Created by ${chenyn} on 2017/8/24.
 */

public class HistoryFileController implements View.OnClickListener, ViewPager.OnPageChangeListener,
        SelectedHistoryFileListener {
    private HashMap<Integer, Integer> idMap = new HashMap<>();
    private DocumentFileFragment mDocumentFragment;
    private VideoFileFragment mVideoFragment;
    private ImageFileFragment mImgFragment;
    private AudioFileFragment mAudioFragment;
    private OtherFileFragment mOtherFragment;
    private HistoryFileActivity mContext;
    private Conversation conversation;
    private HistoryFileView mSFView;
    private String userName;
    private boolean isGroup;
    private long gid;


    public HistoryFileController(HistoryFileActivity context, HistoryFileView view, String userName, long groupId, boolean isGroup) {
        this.mContext = context;
        this.mSFView = view;
        this.userName = userName;
        this.gid = groupId;
        this.isGroup = isGroup;
        List<Fragment> fragments = new ArrayList<>();
        // init Fragment
        mDocumentFragment = new DocumentFileFragment();
        mVideoFragment = new VideoFileFragment();
        mImgFragment = new ImageFileFragment();
        mAudioFragment = new AudioFileFragment();
        mOtherFragment = new OtherFileFragment();
        fragments.add(mImgFragment);
        fragments.add(mDocumentFragment);
        fragments.add(mVideoFragment);
        fragments.add(mAudioFragment);
        fragments.add(mOtherFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mContext.getSupportFragmentManger(),
                fragments);
        mSFView.setViewPagerAdapter(viewPagerAdapter);
        mImgFragment.setController(this, userName, groupId, isGroup);
        mDocumentFragment.setController(this, userName, groupId, isGroup, mContext);
        mVideoFragment.setController(this, userName, groupId, isGroup, mContext);
        mAudioFragment.setController(this, userName, groupId, isGroup, mContext);
        mOtherFragment.setController(this, userName, groupId, isGroup, mContext);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        mSFView.setCurrentItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_album_btn:
                mSFView.setCurrentItem(0);
                break;
            case R.id.actionbar_file_btn:
                mSFView.setCurrentItem(1);
                break;
            case R.id.actionbar_video_btn:
                mSFView.setCurrentItem(2);
                break;
            case R.id.actionbar_audio_btn:
                mSFView.setCurrentItem(3);
                break;
            case R.id.actionbar_other_btn:
                mSFView.setCurrentItem(4);
                break;
            case R.id.return_btn:
                mContext.finish();
                break;
            case R.id.delete_file_btn:
                if (idMap.size() == 0) {
                    break;
                }
                if (isGroup) {
                    conversation = JMessageClient.getGroupConversation(gid);
                } else {
                    conversation = JMessageClient.getSingleConversation(userName);
                }
                JGApplication.ids.clear();
                for (Integer id : idMap.keySet()) {
                    //把即将被删除的消息添加进集合中,注意获取message的id是从map中拿出来的.
                    JGApplication.ids.add(conversation.getMessage(idMap.get(id)));
                    //删除图片信息之后要刷新聊天ui并刷新gridView
                    conversation.deleteMessage(idMap.get(id));
                }
                mImgFragment.notifyGridView();
                mDocumentFragment.notifyListDocument();
                mVideoFragment.notifyListVideo();
                mAudioFragment.notifyListAudio();
                mOtherFragment.notifyListOther();
                break;
            case R.id.tv_choose:
                mSFView.setDeleteRl();
                mImgFragment.notifyImage();
                mDocumentFragment.notifyDocument();
                mVideoFragment.notifyVideo();
                mAudioFragment.notifyAudio();
                mOtherFragment.notifyOther();
                break;
        }
    }

    @Override
    public void onSelected(int msgId, int position) {
        idMap.put(position, msgId);
        mSFView.updateSelectedState(idMap.size());
    }

    @Override
    public void onUnselected(int msgId, int position) {
        idMap.remove(position);
        mSFView.updateSelectedState(idMap.size());
    }
}
