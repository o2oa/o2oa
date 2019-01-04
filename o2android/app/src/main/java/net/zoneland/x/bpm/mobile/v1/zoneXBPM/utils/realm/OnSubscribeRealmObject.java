package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.realm;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.O2RealmInstance;

import io.realm.Realm;
import io.realm.exceptions.RealmException;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by FancyLou on 2016/9/8.
 */
public abstract class OnSubscribeRealmObject<T> implements Observable.OnSubscribe<T> {


    @Override
    public void call(final Subscriber<? super T> subscriber) {
        Realm realm = O2RealmInstance.getInstance().getRealm();
        T object = null;
        realm.beginTransaction();
        try {
            object = get(realm);
            realm.commitTransaction();
        }catch (RuntimeException  e) {
            realm.cancelTransaction();
            subscriber.onError(new RealmException("Error during transaction.", e));
        }catch (Error error) {
            realm.cancelTransaction();
            subscriber.onError(error);
        }finally {
            try {
                realm.close();
            }catch (RealmException e) {
                subscriber.onError(e);
            }
        }
        subscriber.onNext(object);
        subscriber.onCompleted();

    }

    public abstract T get(Realm realm);
}
