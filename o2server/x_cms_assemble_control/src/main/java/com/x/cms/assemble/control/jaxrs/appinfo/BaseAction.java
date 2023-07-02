package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.service.AppDictServiceAdv;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.FormServiceAdv;
import com.x.cms.assemble.control.service.PermissionQueryService;
import com.x.cms.assemble.control.service.ScriptServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class BaseAction extends StandardJaxrsAction {

    protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(AppInfo.class, CategoryInfo.class,
            AppDict.class, AppDictItem.class, View.class,
            ViewCategory.class, ViewFieldConfig.class, Review.class);

    protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
    protected FormServiceAdv formServiceAdv = new FormServiceAdv();
    protected ScriptServiceAdv scriptServiceAdv = new ScriptServiceAdv();
    protected AppDictServiceAdv appDictServiceAdv = new AppDictServiceAdv();
    protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
    protected DocumentQueryService documentServiceAdv = new DocumentQueryService();
    protected UserManagerService userManagerService = new UserManagerService();
    protected PermissionQueryService permissionQueryService = new PermissionQueryService();

    /**
     * 当前登录者访问栏目分类列表查询
     * 1、根据人员的访问权限获取可以访问的栏目信息ID列表
     * 2、根据人员的访问权限获取可以访问的分类信息ID列表
     * 3、将栏目信息和分类信息查询出来组织在一起，如果只有分类，那么也要把栏目信息加上
     * 4、如果栏目信息下没有分类，则删除栏目信息的输出
     *
     * @param personName
     * @param isAnonymous
     * @param inAppInfoIds
     * @param appType
     * @param documentType
     * @param manager
     * @param maxCount
     * @return
     * @throws Exception
     */
    protected List<Wo> listViewAbleAppInfoByPermission(String personName, Boolean isAnonymous,
            List<String> inAppInfoIds, String appType, String documentType,
            Boolean manager, Integer maxCount) throws Exception {
        List<String> unitNames = null;
        List<String> groupNames = null;
        List<String> viewableAppInfoIds = new ArrayList<>();
        List<String> viewableCategoryIds = new ArrayList<>();
        if (manager) {
            if (ListTools.isNotEmpty(inAppInfoIds)) {
                viewableAppInfoIds = inAppInfoIds; // 可发布栏目就限制为inAppInfoIds
            } else {
                // 管理员应该能看所有的栏目
                viewableAppInfoIds = appInfoServiceAdv.listAllIds(documentType);
//				if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
//					viewableAppInfoIds = appInfoServiceAdv.listAllIds(documentType)
//				}
                if (ListTools.isEmpty(viewableAppInfoIds)) {
                    if (viewableAppInfoIds == null) {
                        viewableAppInfoIds = new ArrayList<>();
                    }
                    viewableAppInfoIds.add("NO_APPINFO");
                }
            }
            viewableCategoryIds = categoryInfoServiceAdv.listCategoryIdsWithAppIds(viewableAppInfoIds, documentType,
                    maxCount);
        } else {
            if (!isAnonymous) {
                unitNames = userManagerService.listUnitNamesWithPerson(personName);
                groupNames = userManagerService.listGroupNamesByPerson(personName);
            }
            // 查询用户可以访问到的栏目
            viewableAppInfoIds = permissionQueryService.listViewableAppIdByPerson(personName, isAnonymous, unitNames,
                    groupNames, inAppInfoIds, null, documentType, appType, maxCount);
            if (ListTools.isEmpty(viewableAppInfoIds)) {
                viewableAppInfoIds.add("NO_APPINFO");
            }

            // 根据人员的发布权限获取可以发布文档的分类信息ID列表
            viewableCategoryIds = permissionQueryService.listViewableCategoryIdByPerson(personName, isAnonymous,
                    unitNames, groupNames, viewableAppInfoIds,
                    null, null, documentType, appType, maxCount, false);
        }
        return composeCategoriesIntoAppInfo(viewableAppInfoIds, viewableCategoryIds, appType);
    }

    /**
     * 当前登录者文档发布栏目分类列表查询<br/>
     * 1、根据人员的发布权限获取可以发布文档的栏目信息ID列表<br/>
     * 2、根据人员的发布权限获取可以发布文档的分类信息ID列表<br/>
     * 3、将栏目信息和分类信息查询出来组织在一起，如果只有分类，那么也要把栏目信息加上
     * 4、如果栏目信息下没有分类，则删除栏目信息的输出
     *
     * @param personName
     * @param isAnonymous
     * @param inAppInfoIds
     * @param documentType
     * @param appType
     * @param manager
     * @param maxCount
     * @return
     * @throws Exception
     */
    protected List<Wo> listPublishAbleAppInfoByPermission(String personName, Boolean isAnonymous,
            List<String> inAppInfoIds, String documentType, String appType, Boolean manager, Integer maxCount)
            throws Exception {
        List<String> unitNames = null;
        List<String> groupNames = null;
        List<String> publishableCategoryIds = new ArrayList<>();
        if (manager) {
            // 管理员，可以在所有的栏目和分类中进行发布，只需要过滤指定的栏目ID和信息类别即可
            publishableCategoryIds = categoryInfoServiceAdv.listCategoryIdsWithAppIds(inAppInfoIds, documentType,
                    maxCount);
        } else {
            // 如果不是管理员，则需要根据该员工的权限来进一步分析可见栏目和分类
            if (!isAnonymous) {
                unitNames = userManagerService.listUnitNamesWithPerson(personName);
                groupNames = userManagerService.listGroupNamesByPerson(personName);
            }
            // 2、根据人员的发布权限获取可以发布文档的分类信息ID列表
            publishableCategoryIds = permissionQueryService.listPublishableCategoryIdByPerson(
                    personName, isAnonymous, unitNames, groupNames, inAppInfoIds, null, null, documentType, appType,
                    maxCount, false);
        }
        return composeCategoriesIntoAppInfo(inAppInfoIds, publishableCategoryIds, appType);
    }

    /**
     * 根据指定的栏目和分类ID，将分类组织到栏目信息中
     *
     * @param appInfoIds
     * @param categoryInfoIds
     * @param appType
     * @return
     * @throws Exception
     */
    private List<Wo> composeCategoriesIntoAppInfo(List<String> appInfoIds, List<String> categoryInfoIds, String appType)
            throws Exception {
        List<Wo> wraps = null;
        List<WoCategory> wrapCategories = null;
        Map<String, Wo> app_map = new HashMap<>();
        Map<String, WoCategory> category_map = new HashMap<>();
        List<AppInfo> appInfoList = appInfoServiceAdv.list(appInfoIds);
        List<CategoryInfo> categoryInfoList = categoryInfoServiceAdv.list(categoryInfoIds);
        if (ListTools.isNotEmpty(appInfoList)) {
            wraps = Wo.copier.copy(appInfoList);
            if (ListTools.isNotEmpty(wraps)) {
                for (Wo wo : wraps) {
                    app_map.put(wo.getId(), wo);
                }
            }
        }
        if (ListTools.isNotEmpty(categoryInfoList)) {
            wrapCategories = WoCategory.copier.copy(categoryInfoList);
            if (ListTools.isNotEmpty(wrapCategories)) {
                for (WoCategory woCategory : wrapCategories) {
                    category_map.put(woCategory.getId(), woCategory);
                }
            }
        }

        // 循环category_map，将category装配到app_map里相应的app里
        Set<String> category_set = category_map.keySet();
        Iterator<String> category_iterator = category_set.iterator();
        String category_key = null;
        AppInfo appInfo = null;
        WoCategory woCategory = null;
        Wo wo_app = null;
        while (category_iterator.hasNext()) {
            category_key = category_iterator.next().toString();
            woCategory = category_map.get(category_key);
            wo_app = app_map.get(woCategory.getAppId());
            if (wo_app == null) {
                // 栏目信息在map里不存在，将栏目信息查询出来，放到map里
                appInfo = appInfoServiceAdv.get(woCategory.getAppId());
                if (appInfo != null) {
                    wo_app = Wo.copier.copy(appInfo);
                }
            }
            if (null != wo_app) {
                if (wo_app.getWrapOutCategoryList() == null) {
                    wo_app.setWrapOutCategoryList(new ArrayList<>());
                }
                wo_app.getWrapOutCategoryList().add(woCategory);
                app_map.put(wo_app.getId(), wo_app);
            }
        }
        // 将app_map转移到List里返回
        if (wraps == null) {
            wraps = new ArrayList<>();
        }
        wraps.clear();
        Set<String> app_set = app_map.keySet();
        Iterator<String> app_iterator = app_set.iterator();
        String app_key = null;
        while (app_iterator.hasNext()) {
            app_key = app_iterator.next().toString();
            wo_app = app_map.get(app_key);
            if (StringUtils.isEmpty(wo_app.getAppType())) {
                wo_app.setAppType("未分类");
            }
            if ("全部".equalsIgnoreCase(appType) || "all".equalsIgnoreCase(appType)
                    || (StringUtils.isNotEmpty(wo_app.getAppType()) && wo_app.getAppType().equalsIgnoreCase(appType))) {
                wraps.add(wo_app);
            }
        }
        return wraps;
    }

    public static class Wo extends AppInfo {

        private Long rank;

        public Long getRank() {
            return rank;
        }

        public void setRank(Long rank) {
            this.rank = rank;
        }

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> excludes = new ArrayList<String>();

        @FieldDescribe("栏目下的分类信息列表")
        private List<WoCategory> wrapOutCategoryList = null;

        @FieldDescribe("配置支持信息JSON内容")
        private String config = null;

        public List<WoCategory> getWrapOutCategoryList() {
            return wrapOutCategoryList;
        }

        public void setWrapOutCategoryList(List<WoCategory> wrapOutCategoryList) {
            this.wrapOutCategoryList = wrapOutCategoryList;
        }

        public String getConfig() {
            return this.config;
        }

        public void setConfig(final String config) {
            this.config = config;
        }

        static WrapCopier<AppInfo, Wo> copier = WrapCopierFactory.wo(AppInfo.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible));

        static WrapCopier<AppInfo, Wo> copier2 = WrapCopierFactory.wo(AppInfo.class, Wo.class,
                JpaObject.singularAttributeField(AppInfo.class, true, false), null);

        static WrapCopier<AppInfo, Wo> copier3 = WrapCopierFactory.wo(AppInfo.class, Wo.class,
                JpaObject.singularAttributeField(AppInfo.class, true, true), null);

    }

    public static class WoCategory extends CategoryInfo {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<String>();

        static WrapCopier<CategoryInfo, WoCategory> copier = WrapCopierFactory.wo(CategoryInfo.class, WoCategory.class,
                null, ListTools.toList(JpaObject.FieldsInvisible));

        static WrapCopier<CategoryInfo, WoCategory> copier2 = WrapCopierFactory.wo(CategoryInfo.class, WoCategory.class,
                JpaObject.singularAttributeField(CategoryInfo.class, true, true), null);

    }

    /**
     * 是否是栏目管理员
     *
     * @param person
     * @param appInfo
     * @return
     * @throws Exception
     */
    public boolean isAppInfoManager(String person, List<String> unitNames, List<String> groupNames, AppInfo appInfo) throws Exception {
        if (appInfo != null) {
            if (ListTools.isNotEmpty(appInfo.getManageablePersonList())) {
                if (appInfo.getManageablePersonList().contains(person)) {
                    return true;
                }
            }
            if (ListTools.isNotEmpty(appInfo.getManageableUnitList())) {
                if (ListTools.isNotEmpty(unitNames) &&
                        ListTools.containsAny(unitNames, appInfo.getManageableUnitList())) {
                    return true;
                }
            }
            if (ListTools.isNotEmpty(appInfo.getManageableGroupList())) {
                if (ListTools.isNotEmpty(groupNames) &&
                        ListTools.containsAny(groupNames, appInfo.getManageableGroupList())) {
                    return true;
                }
            }
        }
        return false;
    }

}
