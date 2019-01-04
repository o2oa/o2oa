package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by fancy on 2017/4/12.
 */

public class O2RealmInstance {

    private O2RealmInstance() {

    }

    private static O2RealmInstance instance;

    public static O2RealmInstance getInstance() {
        if (instance==null) {
            instance = new O2RealmInstance();
        }
        return instance;
    }

    private RealmConfiguration realmConfig;

    public Realm getRealm() {
        if (realmConfig==null){
            realmConfig = new RealmConfiguration.Builder()
                    .name("o2oa.realm")
                    .schemaVersion(20)
//                    .deleteRealmIfMigrationNeeded()
//                    .migration(new O2RealmMigration())
                    .build();
        }
        return Realm.getInstance(realmConfig);
    }
}
