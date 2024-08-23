package com.x.query.assemble.surface;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.surface.factory.AppInfoFactory;
import com.x.query.assemble.surface.factory.ApplicationFactory;
import com.x.query.assemble.surface.factory.CategoryInfoFactory;
import com.x.query.assemble.surface.factory.ImportModelFactory;
import com.x.query.assemble.surface.factory.IndexFactory;
import com.x.query.assemble.surface.factory.ProcessFactory;
import com.x.query.assemble.surface.factory.QueryFactory;
import com.x.query.assemble.surface.factory.StatFactory;
import com.x.query.assemble.surface.factory.ViewFactory;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.PersistenceProperties.Reveal;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

public class Business {

    private static final Logger LOGGER = LoggerFactory.getLogger(Business.class);

    private static CacheCategory cache = new CacheCategory(Query.class, View.class, Stat.class, Reveal.class,
            Table.class, Statement.class, ImportModel.class);

    private static URLClassLoader dynamicEntityClassLoader = null;

    public static ClassLoader getDynamicEntityClassLoader() throws Exception {
        if (null == dynamicEntityClassLoader) {
            refreshDynamicEntityClassLoader();
        }
        return dynamicEntityClassLoader;
    }

    public static synchronized void refreshDynamicEntityClassLoader() throws Exception {
        List<URL> urlList = new ArrayList<>();
        IOFileFilter filter = new WildcardFileFilter(DynamicEntity.JAR_PREFIX + "*.jar");
        for (File o : FileUtils.listFiles(Config.dir_dynamic_jars(true), filter, null)) {
            urlList.add(o.toURI().toURL());
        }
        URL[] urls = new URL[urlList.size()];
		if (null != dynamicEntityClassLoader) {
			dynamicEntityClassLoader.close();
		}
        dynamicEntityClassLoader = URLClassLoader.newInstance(urlList.toArray(urls),
                null != ThisApplication.context() ? ThisApplication.context().servletContext().getClassLoader()
                        : Thread.currentThread().getContextClassLoader());
    }

    public static void reloadClassLoader() {
        try {
            EntityManagerContainerFactory.close();
            Business.refreshDynamicEntityClassLoader();
            ThisApplication.context().initDatas(true, Business.getDynamicEntityClassLoader());
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public CacheCategory cache() {
        return cache;
    }

    private EntityManagerContainer emc;

    public Business(EntityManagerContainer emc) throws Exception {
        this.emc = emc;
    }

    public EntityManagerContainer entityManagerContainer() {
        return this.emc;
    }

    private Organization organization;

    public Organization organization() throws Exception {
        if (null == this.organization) {
            this.organization = new Organization(ThisApplication.context());
        }
        return organization;
    }

    private QueryFactory query;

    public QueryFactory query() throws Exception {
        if (null == this.query) {
            this.query = new QueryFactory(this);
        }
        return query;
    }

    private ViewFactory view;

    public ViewFactory view() throws Exception {
        if (null == this.view) {
            this.view = new ViewFactory(this);
        }
        return view;
    }

    private StatFactory stat;

    public StatFactory stat() throws Exception {
        if (null == this.stat) {
            this.stat = new StatFactory(this);
        }
        return stat;
    }

    private ImportModelFactory importModel;

    public ImportModelFactory importModel() throws Exception {
        if (null == this.importModel) {
            this.importModel = new ImportModelFactory(this);
        }
        return importModel;
    }

    private ProcessFactory process;

    public ProcessFactory process() throws Exception {
        if (null == this.process) {
            this.process = new ProcessFactory(this);
        }
        return process;
    }

    private ApplicationFactory application;

    public ApplicationFactory application() throws Exception {
        if (null == this.application) {
            this.application = new ApplicationFactory(this);
        }
        return application;
    }

    private AppInfoFactory appInfo;

    public AppInfoFactory appInfo() throws Exception {
        if (null == this.appInfo) {
            this.appInfo = new AppInfoFactory(this);
        }
        return appInfo;
    }

    private CategoryInfoFactory categoryInfo;

    public CategoryInfoFactory categoryInfo() throws Exception {
        if (null == this.categoryInfo) {
            this.categoryInfo = new CategoryInfoFactory(this);
        }
        return categoryInfo;
    }

    private IndexFactory index;

    public IndexFactory index() throws Exception {
        if (null == this.index) {
            this.index = new IndexFactory(this);
        }
        return index;
    }

    @SuppressWarnings("unchecked")
    public <T extends JpaObject> T pick(String flag, Class<T> cls) throws Exception {
        CacheKey cacheKey = new CacheKey(cls, flag);
        Optional<?> optional = CacheManager.get(cache, cacheKey);
        if (optional.isPresent()) {
            return (T) optional.get();
        } else {
            T t = this.entityManagerContainer().flag(flag, cls);
            if (null != t) {
                entityManagerContainer().get(cls).detach(t);
                CacheManager.put(cache, cacheKey, t);
                return t;
            }
            return null;
        }
    }

    public boolean readable(EffectivePerson effectivePerson, Query query) throws Exception {
        if (null == query) {
            return false;
        }
        if (effectivePerson.isManager()) {
            return true;
        }
        if (StringUtils.equals(effectivePerson.getDistinguishedName(), query.getCreatorPerson())
                || query.getControllerList().contains(effectivePerson.getDistinguishedName())) {
            return true;
        }
        if (query.getAvailableIdentityList().isEmpty() && query.getAvailableUnitList().isEmpty()) {
            return true;
        }
        if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
                OrganizationDefinition.QueryManager))) {
            return true;
        }
        if (CollectionUtils.containsAny(query.getAvailableIdentityList(),
                organization().identity().listWithPerson(effectivePerson))) {
            return true;
        }
        return CollectionUtils.containsAny(query.getAvailableUnitList(),
                organization().unit().listWithPersonSupNested(effectivePerson));
    }

    public boolean readable(EffectivePerson effectivePerson, View view) throws Exception {
        if (null == view) {
            return false;
        }
        if (effectivePerson.isManager()) {
            return true;
        }
        if (view.getAvailableIdentityList().isEmpty() && view.getAvailableUnitList().isEmpty()) {
            return true;
        }
        if (CollectionUtils.containsAny(view.getAvailableIdentityList(),
                organization().identity().listWithPerson(effectivePerson))) {
            return true;
        }
        if (CollectionUtils.containsAny(view.getAvailableUnitList(),
                organization().unit().listWithPersonSupNested(effectivePerson))) {
            return true;
        }
        Query q = this.entityManagerContainer().find(view.getQuery(), Query.class);
        // 在所属query的管理人员中
        if (null != q && ListTools.contains(q.getControllerList(), effectivePerson.getDistinguishedName())) {
            return true;
        }
        return BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
                OrganizationDefinition.QueryManager));
    }

    public boolean readable(EffectivePerson effectivePerson, Stat stat) throws Exception {
        if (null == stat) {
            return false;
        }
        if (effectivePerson.isManager()) {
            return true;
        }
        if (stat.getAvailableIdentityList().isEmpty() && stat.getAvailableUnitList().isEmpty()) {
            return true;
        }
        if (CollectionUtils.containsAny(stat.getAvailableIdentityList(),
                organization().identity().listWithPerson(effectivePerson))) {
            return true;
        }
        if (CollectionUtils.containsAny(stat.getAvailableUnitList(),
                organization().unit().listWithPersonSupNested(effectivePerson))) {
            return true;
        }
        Query q = this.entityManagerContainer().find(stat.getQuery(), Query.class);
        // 在所属query的管理人员中
        if (null != q && ListTools.contains(q.getControllerList(), effectivePerson.getDistinguishedName())) {
            return true;
        }
        return BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
                OrganizationDefinition.QueryManager));
    }

    public boolean readable(EffectivePerson effectivePerson, Statement statement) throws Exception {
        if (null == statement) {
            return false;
        }
        if (BooleanUtils.isTrue(statement.getAnonymousAccessible())) {
            return true;
        }
        if (effectivePerson.isManager()) {
            return true;
        }
        if (statement.getExecutePersonList().isEmpty() && statement.getExecuteUnitList().isEmpty()) {
            return true;
        }
        if (CollectionUtils.containsAny(statement.getExecutePersonList(),
                organization().identity().listWithPerson(effectivePerson))) {
            return true;
        }
        if (CollectionUtils.containsAny(statement.getExecutePersonList(), effectivePerson.getDistinguishedName())) {
            return true;
        }
        if (CollectionUtils.containsAny(statement.getExecuteUnitList(),
                organization().unit().listWithPersonSupNested(effectivePerson))) {
            return true;
        }
        Query q = this.entityManagerContainer().find(statement.getQuery(), Query.class);
        // 在所属query的管理人员中
        if (null != q && ListTools.contains(q.getControllerList(), effectivePerson.getDistinguishedName())) {
            return true;
        }
        return organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
                OrganizationDefinition.QueryManager);
    }

    public boolean readable(EffectivePerson effectivePerson, Table o) throws Exception {
        if (null == o) {
            return false;
        }
        if (ListTools.isEmpty(o.getReadPersonList()) && ListTools.isEmpty(o.getReadUnitList())) {
            return true;
        }
        if (BooleanUtils.isTrue(effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
                OrganizationDefinition.Manager, OrganizationDefinition.QueryManager)))) {
            return true;
        }
        if (effectivePerson.isPerson(o.getEditPersonList()) || effectivePerson.isPerson(o.getReadPersonList())) {
            return true;
        }
        if (ListTools.isNotEmpty(o.getEditUnitList()) || ListTools.isNotEmpty(o.getReadUnitList())) {
            List<String> units = this.organization().unit().listWithPersonSupNested(effectivePerson.getDistinguishedName());
            if (ListTools.containsAny(units, o.getEditUnitList())
                    || ListTools.containsAny(units, o.getReadUnitList())) {
                return true;
            }
        }
        return false;
    }

    public boolean readable(EffectivePerson effectivePerson, ImportModel model) throws Exception {
        if (null == model) {
            return false;
        }
        if (effectivePerson.isManager()) {
            return true;
        }
        if (model.getAvailableIdentityList().isEmpty() && model.getAvailableUnitList().isEmpty()) {
            return true;
        }
        if (CollectionUtils.containsAny(model.getAvailableIdentityList(),
                organization().identity().listWithPerson(effectivePerson))) {
            return true;
        }
        if (CollectionUtils.containsAny(model.getAvailableUnitList(),
                organization().unit().listWithPersonSupNested(effectivePerson))) {
            return true;
        }
        Query q = this.entityManagerContainer().find(model.getQuery(), Query.class);
        // 在所属query的管理人员中
        if (null != q && ListTools.contains(q.getControllerList(), effectivePerson.getDistinguishedName())) {
            return true;
        }
        return organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
                OrganizationDefinition.QueryManager);
    }

    public boolean editable(EffectivePerson effectivePerson, Table o) throws Exception {
        boolean result = false;
        if (BooleanUtils.isTrue(effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
                OrganizationDefinition.Manager, OrganizationDefinition.QueryManager)))) {
            result = true;
        }
        if (!result) {
            if (ListTools.isEmpty(o.getEditPersonList()) && ListTools.isEmpty(o.getEditUnitList())) {
                result = true;
            } else if (ListTools.isNotEmpty(o.getEditPersonList()) && effectivePerson.isPerson(o.getEditPersonList())) {
                result = true;
            } else if (ListTools.isNotEmpty(o.getEditUnitList())) {
                List<String> units = this.organization().unit().listWithPersonSupNested(effectivePerson.getDistinguishedName());
                if (ListTools.containsAny(units, o.getEditUnitList())) {
                    result = true;
                }
            }
        }
        return result;
    }

    public boolean executable(EffectivePerson effectivePerson, Statement o) throws Exception {
        if (null == o) {
            return false;
        }
        if (BooleanUtils.isTrue(o.getAnonymousAccessible())) {
            return true;
        }
        if ((!effectivePerson.isAnonymous()) && ListTools.isEmpty(o.getExecutePersonList())
                && ListTools.isEmpty(o.getExecuteUnitList())) {
            return true;
        }
        if (BooleanUtils
                .isTrue(effectivePerson.isManager()
                        || (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
                                OrganizationDefinition.QueryManager))
                        || effectivePerson.isPerson(o.getExecutePersonList()))) {
            return true;
        }
        if (ListTools.isNotEmpty(o.getExecuteUnitList())) {
            List<String> units = this.organization().unit().listWithPersonSupNested(effectivePerson.getDistinguishedName());
            if (ListTools.containsAny(units, o.getExecuteUnitList())) {
                return true;
            }
        }
        return false;
    }

    public boolean controllable(EffectivePerson effectivePerson) throws Exception {
        boolean result = false;
        if (effectivePerson.isManager() || BooleanUtils.isTrue((this.organization().person().hasRole(effectivePerson,
                OrganizationDefinition.Manager, OrganizationDefinition.QueryManager)))) {
            result = true;
        }
        return result;
    }

}
