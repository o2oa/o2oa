package com.x.query.assemble.surface.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.assemble.surface.AbstractFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.express.index.Indexs;

public class IndexFactory extends AbstractFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexFactory.class);

    public IndexFactory(Business business) throws Exception {
        super(business);
    }

    /**
     * 判断当前用户,如果是管理员或者服务器那么强制返回指定用户
     * 
     * @param effectivePerson
     * @param person
     * @return
     * @throws Exception
     */
    public String who(EffectivePerson effectivePerson, String person) throws Exception {
        if (effectivePerson.isCipher() || effectivePerson.isManager()) {
            if (StringUtils.isNotBlank(person)) {
                String p = business().organization().person().get(person);
                if (StringUtils.isNotBlank(p)) {
                    return p;
                } else {
                    throw new ExceptionEntityExist(person);
                }
            } else {
                return null;
            }
        }
        return effectivePerson.getDistinguishedName();
    }

    /**
     * 根据将访问的目录填充用户所有权限,通过是否在可管理人员列表中进行判断,如果是系统管理员直接返回null,在后续的搜索中表示没有任何限制.
     * 
     * @param person
     * @param category
     * @param key
     * @return
     * @throws Exception
     */
    public List<String> determineReaders(String person, String category, String key) throws Exception {
        if (StringUtils.isBlank(person)) {
            return new ArrayList<>();
        }
        if (StringUtils.equalsIgnoreCase(Indexs.CATEGORY_PROCESSPLATFORM, category)) {
            return determineReadersApplication(person, key);
        } else if (StringUtils.equalsIgnoreCase(Indexs.CATEGORY_CMS, category)) {
            return determineReadersAppInfo(person, key);
        } else if (StringUtils.equalsIgnoreCase(Indexs.CATEGORY_SEARCH, category)) {
            return determineReadersSearch(person);
        }
        return Arrays.asList(person);
    }

    private List<String> determineReadersApplication(String person, String key) throws Exception {
        List<String> list = new ArrayList<>();
        if (BooleanUtils.isTrue(
                business().organization().person().hasRole(person, OrganizationDefinition.ProcessPlatformManager))
                && StringUtils.isBlank(person)) {
            return list;
        }
        list.add(person);
        Optional<Application> optionalApplication = business().application().get(key);
        if (optionalApplication.isPresent()) {
            if (optionalApplication.get().getControllerList().contains(person)) {
                list.add(optionalApplication.get().getId());
            }
            entityManagerContainer()
                    .idsEqual(Process.class, Process.application_FIELDNAME, optionalApplication.get().getId())
                    .forEach(o -> {
                        Optional<Process> optionalProcess;
                        try {
                            optionalProcess = business().process().get(o);
                            if (optionalProcess.get().getControllerList().contains(person)) {
                                list.add(optionalProcess.get().getId());
                            }
                        } catch (Exception e) {
                            LOGGER.error(e);
                        }
                    });
        }
        return list;
    }

    private List<String> determineReadersAppInfo(String person, String key) throws Exception {
        List<String> list = new ArrayList<>();
        if (BooleanUtils
                .isTrue(business().organization().person().hasRole(person, OrganizationDefinition.CMSManager))) {
            return list;
        }
        list.add(person);
        list.add(Indexs.READERS_SYMBOL_ALL);
        Optional<AppInfo> optionalAppInfo = business().appInfo().get(key);
        if (optionalAppInfo.isPresent()) {
            List<String> groups = null;
            List<String> units = null;
            if (ListTools.isNotEmpty(optionalAppInfo.get().getManageablePersonList())
                    && optionalAppInfo.get().getManageablePersonList().contains(person)) {
                list.add(optionalAppInfo.get().getId());
            }
            if (ListTools.isNotEmpty(optionalAppInfo.get().getManageableGroupList())) {
                groups = business().organization().group().listWithPerson(person);
                if (ListTools.containsAny(groups, optionalAppInfo.get().getManageableGroupList())) {
                    list.add(optionalAppInfo.get().getId());
                }
            }
            if (ListTools.isNotEmpty(optionalAppInfo.get().getManageableUnitList())) {
                units = business().organization().unit().listWithPerson(person);
                if (ListTools.containsAny(units, optionalAppInfo.get().getManageableUnitList())) {
                    list.add(optionalAppInfo.get().getId());
                }
            }
            determineReadersCategoryInfo(person, optionalAppInfo.get(), groups, units, list);
        }
        return list;
    }

    private void determineReadersCategoryInfo(String person, AppInfo appInfo, List<String> groups, List<String> units,
            List<String> list) throws Exception {
        for (String id : entityManagerContainer().idsEqual(CategoryInfo.class, CategoryInfo.appId_FIELDNAME,
                appInfo.getId())) {
            Optional<CategoryInfo> optionalCategoryInfo;
            try {
                optionalCategoryInfo = business().categoryInfo().get(id);
                if (optionalCategoryInfo.isPresent()) {
                    determineReadersCategoryInfoManageablePerson(person, optionalCategoryInfo.get(), list);
                    groups = determineReadersCategoryInfoManageableGroup(person, groups, optionalCategoryInfo.get(),
                            list);
                    units = determineReadersCategoryInfoManageableUnit(person, units, optionalCategoryInfo.get(), list);
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    private List<String> determineReadersCategoryInfoManageableUnit(String person, List<String> units,
            CategoryInfo categoryInfo, List<String> list) throws Exception {
        if (ListTools.isNotEmpty(categoryInfo.getManageableUnitList())) {
            if (units == null) {
                units = business().organization().unit().listWithPerson(person);
            }
            if (ListTools.containsAny(units, categoryInfo.getManageableUnitList())) {
                list.add(categoryInfo.getId());
            }
        }
        return units;
    }

    private List<String> determineReadersCategoryInfoManageableGroup(String person, List<String> groups,
            CategoryInfo categoryInfo, List<String> list) throws Exception {
        if (ListTools.isNotEmpty(categoryInfo.getManageableGroupList())) {
            if (groups == null) {
                groups = business().organization().group().listWithPerson(person);
            }
            if (ListTools.containsAny(groups, categoryInfo.getManageableGroupList())) {
                list.add(categoryInfo.getId());
            }
        }
        return groups;
    }

    private void determineReadersCategoryInfoManageablePerson(String person, CategoryInfo categoryInfo,
            List<String> list) {
        if (ListTools.isNotEmpty(categoryInfo.getManageablePersonList())
                && categoryInfo.getManageablePersonList().contains(person)) {
            list.add(categoryInfo.getId());
        }
    }

    private List<String> determineReadersSearch(String person) throws Exception {
        List<String> list = new ArrayList<>();
        list.add(person);
        list.add(Indexs.READERS_SYMBOL_ALL);
        List<String> groups = business().organization().group().listWithPerson(person);
        List<String> units = business().organization().unit().listWithPerson(person);
        if (BooleanUtils
                .isTrue(business().organization().person().hasRole(person, OrganizationDefinition.CMSManager))) {
            list.addAll(this.entityManagerContainer().ids(AppInfo.class));
            list.addAll(this.entityManagerContainer().ids(CategoryInfo.class));
        } else {
            list.addAll(this.entityManagerContainer().idsInOrInOrIsMember(AppInfo.class,
                    AppInfo.manageableUnitList_FIELDNAME, units, AppInfo.manageableGroupList_FIELDNAME, groups,
                    AppInfo.manageablePersonList_FIELDNAME, person));
            list.addAll(this.entityManagerContainer().idsInOrInOrIsMember(CategoryInfo.class,
                    CategoryInfo.manageableUnitList_FIELDNAME, units, CategoryInfo.manageableGroupList_FIELDNAME,
                    groups, CategoryInfo.manageablePersonList_FIELDNAME, person));
        }
        if (BooleanUtils
                .isTrue(business().organization().person().hasRole(person,
                        OrganizationDefinition.ProcessPlatformManager))) {
            list.addAll(this.entityManagerContainer().ids(Application.class));
            list.addAll(this.entityManagerContainer().ids(Process.class));
        } else {
            list.addAll(this.entityManagerContainer().idsIsMember(Application.class,
                    Application.controllerList_FIELDNAME, person));
        }
        return list;
    }

}
