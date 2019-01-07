package jiguang.chat.utils.photovideo.takevideo.camera;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;


/**
 * 防抖点击事件绑定  可以参照rxbinding
 *
 *
 ViewClickOnSubscribe click = new ViewClickOnSubscribe();
 click.addOnClickListener(tv_test);
 click.addOnClickListener(tv_test1);
 click.addOnClickListener(tv_test2);

 subscription = Observable.create(click).throttleFirst(500 , TimeUnit.MILLISECONDS ).subscribe(new Action1<View>() {
    @Override
    public void call(View view) {
        switch (view.getId()) {
            case R.id.tv_test:
                Log.i("you", "test");
                break;
            case R.id.tv_test1:
                Log.i("you", "test1");
                break;
            }
        }
    });

 Subscription subscription;

 @Override
 protected void onDestroy() {
    super.onDestroy();
    subscription.unsubscribe();
 }

 */

public class ViewClickOnSubscribe implements Observable.OnSubscribe<View> {

    /**
     * 注册防抖点击的控件
     */
    private List<View> clickViews = new ArrayList<View>();

    /**
     * 添加控件点击事件
     * @param v
     */
    public void addOnClickListener(@NonNull View v) {
        clickViews.add(v);
    }

    @Override
    public void call(final Subscriber<? super View> subscriber) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(v);
                }
            }
        };
        for (View v : clickViews) {
            v.setOnClickListener(listener);
        }
        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                Iterator<View> iterator = clickViews.iterator();
                while (iterator.hasNext()) {
                    iterator.next().setOnClickListener(null);
                    iterator.remove();
                }
            }
        });
    }
}
