package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.realm;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by FancyLou on 2016/9/8.
 */
public final class RealmObservable {

    private RealmObservable() {}


    /**
     * 获取数据
     * @param function
     * @param <T>
     * @return
     */
    public static <T> Observable<T>  object(final Func1<Realm, T> function) {

        return Observable.create(new OnSubscribeRealmObject<T>() {
            @Override
            public T get(Realm realm) {
                return function.call(realm);
            }
        });
    }
}
