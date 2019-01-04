package jiguang.chat.utils.keyboard.interfaces;

import android.view.ViewGroup;

import jiguang.chat.utils.keyboard.adpater.EmoticonsAdapter;

public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}
