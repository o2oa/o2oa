package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by fancy on 2017/4/11.
 */

public class O2RealmMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        XLog.info("migrate oldVersion:" + oldVersion + ", newVersion:" + newVersion);
        RealmSchema schema = realm.getSchema();
        while (oldVersion< newVersion) {
            if (oldVersion == 9) {
                schema.create("PortalDataRealmObject")
                        .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                        .addField("name", String.class)
                        .addField("alias", String.class)
                        .addField("description", String.class)
                        .addField("portalCategory", String.class)
                        .addField("firstPage", String.class)
                        .addField("creatorPerson", String.class)
                        .addField("lastUpdateTime", String.class)
                        .addField("lastUpdatePerson", String.class)
                        .addField("createTime", String.class)
                        .addField("updateTime", String.class)
                        .addField("enable", Boolean.class);
                schema.create("NativeAppDataRealmObject")
                        .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                        .addField("key", String.class)
                        .addField("name", String.class)
                        .addField("enable", Boolean.class);
                oldVersion++;
            }
            if (oldVersion == 10) {
                oldVersion++;
            }
            if (oldVersion == 11) {
//                schema.create("IMUserEntryRealmObject")
//                        .addField("_id", String.class, FieldAttribute.PRIMARY_KEY)
//                        .addField("username", String.class)
//                        .addField("appKey", String.class);
//                schema.create("ImFriendEntryRealmObject")
//                        .addField("_id", String.class, FieldAttribute.PRIMARY_KEY)
//                        .addField("uid", Long.class)
//                        .addField("username", String.class)
//                        .addField("appKey", String.class)
//                        .addField("avatar", String.class)
//                        .addField("displayName", String.class)
//                        .addField("letter", String.class)
//                        .addField("nickName", String.class)
//                        .addField("noteName", String.class)
//                        .addField("user", IMUserEntryRealmObject.class);
//                oldVersion++;
            }
        }

    }
}
