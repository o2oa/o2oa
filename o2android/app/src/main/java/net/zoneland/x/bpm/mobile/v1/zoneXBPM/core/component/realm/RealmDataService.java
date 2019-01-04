package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.portal.PortalData;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.BBSCollectionRealmObject;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.LoginHistoryRealmObject;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.MyAppListObject;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.NativeAppDataRealmObject;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.PortalDataRealmObject;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.UsuallyPerson;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.UsuallyPersonRealmObject;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AppItemOnlineVo;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.BBSCollectionSectionVO;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.LoginHistoryVO;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.realm.RealmObservable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by FancyLou on 2016/10/27.
 */

public class RealmDataService {




    /**
     * 保存常用联系人
     *
     * @param owner
     * @param person
     * @return
     */
    public Observable<UsuallyPerson> saveUsuallyPerson(final String owner, final String person,
                                                       final String  ownerDisplay, final String personDisplay , final String gender,
                                                       final String mobile) {
        return RealmObservable.object(new Func1<Realm, UsuallyPerson>() {
            @Override
            public UsuallyPerson call(Realm realm) {
                String id = UUID.randomUUID().toString();
                UsuallyPersonRealmObject realmObject = realm
                        .createObject(UsuallyPersonRealmObject.class, id);
                realmObject.setOwner(owner);
                realmObject.setPerson(person);
                realmObject.setOwnerDisplay(ownerDisplay);
                realmObject.setPersonDisplay(personDisplay);
                realmObject.setGender(gender);
                realmObject.setMobile(mobile);
                realmObject.setUnitId(O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), ""));
                UsuallyPerson usuallyPerson = new UsuallyPerson();
                usuallyPerson.setId(realmObject.getId());
                usuallyPerson.setOwner(owner);
                usuallyPerson.setOwnerDisplay(ownerDisplay);
                usuallyPerson.setPerson(person);
                usuallyPerson.setPersonDisplay(personDisplay);
                usuallyPerson.setGender(gender);
                usuallyPerson.setMobile(mobile);
                return usuallyPerson;
            }
        });
    }

    /**
     * 删除常用联系人
     *
     * @param owner
     * @param person
     * @return
     */
    public Observable<Boolean> deleteUsuallyPerson(final String owner, final String person) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                RealmResults<UsuallyPersonRealmObject> results = realm
                        .where(UsuallyPersonRealmObject.class).equalTo("owner", owner)
                        .equalTo("person", person).equalTo("unitId", unitId).findAll();
                return results.deleteAllFromRealm();
            }
        });
    }

    /**
     * 是否常用联系人
     *
     * @param owner
     * @param person
     * @return
     */
    public Observable<Boolean> isUsuallyPerson(final String owner, final String person) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                RealmResults<UsuallyPersonRealmObject> results = realm
                        .where(UsuallyPersonRealmObject.class).equalTo("owner", owner)
                        .equalTo("person", person).equalTo("unitId", unitId).findAll();
                if (results != null && results.size() > 0) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 获取常用联系人列表
     *
     * @param owner
     * @return
     */
    public Observable<List<UsuallyPerson>> loadUsuallyPersonByOwner(final String owner) {
        return RealmObservable.object(new Func1<Realm, List<UsuallyPerson>>() {
            @Override
            public List<UsuallyPerson> call(Realm realm) {
                String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                RealmResults<UsuallyPersonRealmObject> results = realm
                        .where(UsuallyPersonRealmObject.class).equalTo("owner", owner)
                        .equalTo("unitId", unitId)
                        .findAll();
                List<UsuallyPerson> list = new ArrayList<UsuallyPerson>();
                if (results != null && !results.isEmpty()) {
                    Iterator<UsuallyPersonRealmObject> it = results.iterator();
                    while (it.hasNext()) {
                        UsuallyPersonRealmObject u = it.next();
                        UsuallyPerson person = new UsuallyPerson();
                        person.setPerson(u.getPerson());
                        person.setPersonDisplay(u.getPersonDisplay());
                        person.setOwner(u.getOwner());
                        person.setOwnerDisplay(u.getOwnerDisplay());
                        person.setId(u.getId());
                        person.setGender(u.getGender());
                        person.setMobile(u.getMobile());
                        list.add(person);
                    }
                }
                return list;
            }
        });
    }

    /*******************************************************bbs collection ************************************/


    /**
     * 当前版块是否已经收藏
     *
     * @param sectionId
     * @return
     */
    public Observable<Boolean> hasTheSectionCollected(final String sectionId) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                try {
                    return !(realm.where(BBSCollectionRealmObject.class).equalTo("id", sectionId).equalTo("unitId", unitId).findAll().isEmpty());
                } catch (Exception e) {
                    XLog.error("查询收藏版块", e);
                    return false;
                }
            }
        });
    }


    /**
     * 收藏版块
     *
     * @param vo
     * @return
     */
    public Observable<Boolean> saveBBSCollection(final BBSCollectionSectionVO vo) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                try {
                    String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                    XLog.debug(vo.toString());
                    BBSCollectionRealmObject object = new BBSCollectionRealmObject();
                    object.setId(vo.getId());
                    object.setSectionName(vo.getSectionName());
                    object.setSectionIcon(vo.getSectionIcon());
                    object.setCreateTime(vo.getCreateTime());
                    object.setUnitId(unitId);
                    realm.copyToRealmOrUpdate(object);
                    return true;
                } catch (Exception e) {
                    XLog.error("收藏版块", e);
                    return false;
                }
            }
        });
    }

    /**
     * 删除收藏版块
     *
     * @param ids
     * @return
     */
    public Observable<Boolean> deleteBBSCollections(final List<String> ids) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                try {
                    if (ids != null && !ids.isEmpty()) {
                        for (int i = 0; i < ids.size(); i++) {
                            realm.where(BBSCollectionRealmObject.class).equalTo("id", ids.get(i))
                                    .findFirst().deleteFromRealm();
                        }
                    }
                    return true;
                } catch (Exception e) {
                    XLog.error("删除收藏版块", e);
                    return false;
                }
            }
        });
    }


    /**
     * 收藏版块列表
     *
     * @return
     */
    public Observable<List<BBSCollectionSectionVO>> findBBSCollectionAll() {
        return RealmObservable.object(new Func1<Realm, List<BBSCollectionSectionVO>>() {
            @Override
            public List<BBSCollectionSectionVO> call(Realm realm) {
                List<BBSCollectionSectionVO> list = new ArrayList<BBSCollectionSectionVO>();
                try {
                    String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                    RealmResults<BBSCollectionRealmObject> result = realm.where(BBSCollectionRealmObject.class).equalTo("unitId", unitId).findAll().sort("createTime", Sort.ASCENDING);
                    if (result != null && !result.isEmpty()) {
                        Iterator<BBSCollectionRealmObject> it = result.iterator();
                        while (it.hasNext()) {
                            BBSCollectionRealmObject o = it.next();
                            BBSCollectionSectionVO vo = new BBSCollectionSectionVO();
                            vo.setId(o.getId());
                            vo.setSectionName(o.getSectionName());
                            vo.setSectionIcon(o.getSectionIcon());
                            vo.setCreateTime(o.getCreateTime());
                            XLog.debug("vo:" + vo.toString());
                            list.add(vo);
                        }
                    }
                } catch (Exception e) {
                    XLog.error("查询收藏版块列表", e);
                }
                return list;
            }
        });
    }

    /**
     * 是否有收藏数据
     *
     * @return
     */
    public Observable<Boolean> hasAnyBBSCollection() {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                try {
                    String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                    return !(realm.where(BBSCollectionRealmObject.class).equalTo("unitId", unitId).findAll().isEmpty());
                } catch (Exception e) {
                    XLog.error("查询收藏版块列表", e);
                    return false;
                }
            }
        });
    }


    /************************************LoginHistoryRealmObject 登录历史*******************************************************/

    /**
     * 新增或者修改登录时间
     * @param userName
     * @param phone
     * @return
     */
    public Observable<Boolean> saveOrUpdateLogin(final String userName, final String phone) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                String unitId =O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                RealmResults<LoginHistoryRealmObject> results  = realm.where(LoginHistoryRealmObject.class).equalTo("unitId", unitId).equalTo("loginPhone", phone).equalTo("loginName", userName)
                        .findAll();
                if (results.isEmpty()){//add
                    LoginHistoryRealmObject realmObject = new LoginHistoryRealmObject();
                    realmObject.setId(UUID.randomUUID().toString());
                    realmObject.setLoginName(userName);
                    realmObject.setLoginPhone(phone);
                    realmObject.setLastLoginTime(DateHelper.now());
                    realmObject.setUnitId(unitId);
                    realm.copyToRealmOrUpdate(realmObject);
                    return true;
                }else {//update
                    LoginHistoryRealmObject object = results.first();
                    object.setLastLoginTime(DateHelper.now());
                    return true;
                }
            }
        });
    }


    /**
     * 获取登录列表
     * @return
     */
    public Observable<List<LoginHistoryVO>> findLoginHistory() {
        return RealmObservable.object(new Func1<Realm, List<LoginHistoryVO>>() {
            @Override
            public List<LoginHistoryVO> call(Realm realm) {
                List<LoginHistoryVO> list = new ArrayList<LoginHistoryVO>();
                String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                RealmResults<LoginHistoryRealmObject> results = realm.where(LoginHistoryRealmObject.class)
                        .equalTo("unitId", unitId).findAll().sort("lastLoginTime", Sort.DESCENDING);
                if (results != null && !results.isEmpty()) {
                    Iterator<LoginHistoryRealmObject> iterator = results.iterator();
                    while (iterator.hasNext()) {
                        LoginHistoryRealmObject o = iterator.next();
                        list.add(new LoginHistoryVO(o.getId(), o.getLoginName(),o.getLoginPhone(), o.getLastLoginTime(), o.getUnitId()));
                    }
                }
                return list;
            }
        });
    }

    /************************************ MyApp 自己配置的应用 *******************************************************/

    /**
     * 添加我的应用
     *
     * @param list
     * @return
     */
    public Observable<Boolean> saveMyApp(final List<MyAppListObject> list) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                try {
                    String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                    String userId = O2SDKManager.Companion.instance().getDistinguishedName();
                    for (int i = 0;i<list.size();i++) {
                        MyAppListObject vo = list.get(i);
                        MyAppListObject object = new MyAppListObject();
                        object.setAppId(vo.getAppId()+unitId+userId);
                        object.setAppTitle(vo.getAppTitle());
                        object.setUnitId(unitId);
                        object.setUserId(userId);
                        object.setSortId(i+1);
                        realm.copyToRealm(object);
                    }
                    return true;
                } catch (Exception e) {
                    XLog.error("添加我的应用", e);
                    return false;
                }
            }
        });
    }

    /**
     * 删除我的应用
     *
     * @param ids
     * @return
     */
    public Observable<Boolean> deleteMyApp(final List<MyAppListObject> ids) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                try {
                    String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                    String userId = O2SDKManager.Companion.instance().getDistinguishedName();
                    if (ids != null && !ids.isEmpty()) {
                        for (int i = 0; i < ids.size(); i++) {
                            MyAppListObject myapp = ids.get(i);
                            if (myapp==null) {
                                continue;
                            }
                            try {
                                realm.where(MyAppListObject.class).equalTo("appId", myapp.getAppId()+unitId+userId)
                                        .findFirst().deleteFromRealm();
                            } catch (Exception e) {
                                XLog.error("没有查询到这个应用:"+myapp.getAppTitle(), e);
                            }
                        }
                    }
                    return true;
                } catch (Exception e) {
                    XLog.error("删除我的应用", e);
                    return false;
                }
            }
        });
    }


    /**
     * 我的应用列表
     *
     * @rrn
     */
    public Observable<List<MyAppListObject>> findMyAppList() {
        return RealmObservable.object(new Func1<Realm, List<MyAppListObject>>() {
            @Override
            public List<MyAppListObject> call(Realm realm) {
                List<MyAppListObject> list = new ArrayList<>();
                try {
                    String unitId = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_BIND_UNIT_ID_KEY(), "");
                    String userId = O2SDKManager.Companion.instance().getDistinguishedName();
                    RealmResults<MyAppListObject> result = realm.where(MyAppListObject.class).equalTo("unitId", unitId)
                            .equalTo("userId", userId)
                            .findAllSorted("sortId");
                    if (result != null && !result.isEmpty()) {
                        Iterator<MyAppListObject> it = result.iterator();
                        while (it.hasNext()) {
                            MyAppListObject o = it.next();
                            try {
                                MyAppListObject vo = new MyAppListObject();
                                int appIdL = o.getAppId().length();
                                int unitL = unitId.length();
                                int userL = userId.length();
                                String appId = o.getAppId().substring(0, appIdL - unitL - userL);
                                vo.setAppId(appId);
                                vo.setAppTitle(o.getAppTitle());
                                list.add(vo);
                            } catch (Exception e) {
                                XLog.error("查询。。。。");
                                try {
                                    o.deleteFromRealm();
                                } catch (Exception e1) {
                                    XLog.error("删除。。。。");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    XLog.error("我的应用列表", e);
                }
                return list;
            }
        });
    }

    /************************************Portal 数据缓存 *******************************************************/

    public Observable<Boolean> savePortalList(final List<PortalData> portalList) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                try {
                    if (portalList!=null) {
                        for (int i = 0; i < portalList.size(); i++) {
                            PortalData data = portalList.get(i);
                            PortalDataRealmObject realmObject = new PortalDataRealmObject();
                            realmObject.setId(data.getId());
                            realmObject.setName(data.getName());
                            realmObject.setAlias(data.getAlias());
                            realmObject.setCreateTime(data.getCreateTime());
                            realmObject.setUpdateTime(data.getUpdateTime());
                            realmObject.setPortalCategory(data.getPortalCategory());
                            realmObject.setDescription(data.getDescription());
                            realmObject.setFirstPage(data.getFirstPage());
                            realmObject.setLastUpdatePerson(data.getLastUpdatePerson());
                            realmObject.setLastUpdateTime(data.getLastUpdateTime());
                            realmObject.setEnable(data.getEnable());
                            realm.copyToRealm(realmObject);
                        }
                    }
                }catch (Exception e) {
                    XLog.error("", e);
                    return false;
                }
                return true;
            }
        });
    }

    public Observable<Boolean> deleteAllPortal() {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                return realm.where(PortalDataRealmObject.class).findAll().deleteAllFromRealm();
            }
        });
    }

    public Observable<List<PortalData>> findAllPortalList() {
        return RealmObservable.object(new Func1<Realm, List<PortalData>>() {
            @Override
            public List<PortalData> call(Realm realm) {
                RealmResults<PortalDataRealmObject> result = realm.where(PortalDataRealmObject.class).findAll();
                Iterator<PortalDataRealmObject> it = result.iterator();
                List<PortalData> list = new ArrayList<>();
                while (it.hasNext()) {
                    PortalDataRealmObject object = it.next();
                    PortalData data = new PortalData();
                    data.setId( object.getId() == null ?"" : object.getId());
                    data.setName( object.getName() == null ?"" : object.getName());
                    data.setAlias( object.getAlias() == null ?"" : object.getAlias());
                    data.setCreateTime( object.getCreateTime() == null ?"" : object.getCreateTime());
                    data.setCreatorPerson( object.getCreatorPerson() == null ?"" : object.getCreatorPerson());
                    data.setPortalCategory( object.getPortalCategory() == null ?"" : object.getPortalCategory());
                    data.setUpdateTime( object.getUpdateTime() == null ?"" : object.getUpdateTime());
                    data.setLastUpdatePerson( object.getLastUpdatePerson() == null ?"" : object.getLastUpdatePerson());
                    data.setLastUpdateTime( object.getLastUpdateTime() == null ?"" : object.getLastUpdateTime());
                    data.setFirstPage( object.getFirstPage() == null ?"" : object.getFirstPage());
                    data.setDescription( object.getDescription() == null ?"" : object.getDescription());
                    data.setEnable( object.getEnable() == null ? false : object.getEnable());
                    list.add(data);
                }
                return list;
            }
        });
    }


    /************************************native app 数据缓存 *******************************************************/


    public Observable<Boolean> saveNativeList(final List<AppItemOnlineVo> list) {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                try{
                    if (list!=null) {
                        for (int i = 0; i < list.size(); i++) {
                            AppItemOnlineVo vo = list.get(i);
                            NativeAppDataRealmObject realmObject = new NativeAppDataRealmObject();
                            realmObject.setId(vo.getId());
                            realmObject.setName(vo.getName());
                            realmObject.setKey(vo.getKey());
                            realmObject.setEnable(vo.getEnable());
                            realm.copyToRealm(realmObject);
                        }
                    }
                }catch (Exception e) {
                    XLog.error("", e);
                    return false;
                }
                return true;
            }
        });
    }

    public Observable<Boolean> deleteALlNativeApp() {
        return RealmObservable.object(new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                return realm.where(NativeAppDataRealmObject.class).findAll().deleteAllFromRealm();
            }
        });
    }

    public Observable<List<AppItemOnlineVo>> findAllNativeApp() {
        return RealmObservable.object(new Func1<Realm, List<AppItemOnlineVo>>() {
            @Override
            public List<AppItemOnlineVo> call(Realm realm) {
                RealmResults<NativeAppDataRealmObject> result = realm.where(NativeAppDataRealmObject.class).findAll();
                Iterator<NativeAppDataRealmObject> it = result.iterator();
                List<AppItemOnlineVo> list = new ArrayList<>();
                while (it.hasNext()) {
                    NativeAppDataRealmObject object = it.next();
                    AppItemOnlineVo vo = new AppItemOnlineVo(
                            object.getId() == null ? -1 : object.getId(),
                            object.getKey() == null ? "" : object.getKey(),
                            object.getName() == null ? "" : object.getName(),
                            object.getEnable() == null ? false : object.getEnable());
                    list.add(vo);
                }
                return list;
            }
        });
    }


}
