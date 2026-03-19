package com.x.pan.assemble.control.factory;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.file.core.entity.open.FileStatus;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;
import com.x.pan.assemble.control.AbstractFactory;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.Folder3_;
import com.x.pan.core.entity.ZonePermission;
import com.x.pan.core.entity.ZonePermission_;
import com.x.pan.core.entity.ZoneRoleEnum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 */
public class Folder3Factory extends AbstractFactory {

    private static final Logger logger = LoggerFactory.getLogger(Folder3Factory.class);

    private final Cache.CacheCategory zoneCacheCategory = new Cache.CacheCategory(Identity.class,
            Unit.class, ZonePermission.class, Group.class);

    public Folder3Factory(Business business) throws Exception {
        super(business);
    }

    /**
     * 获取共享区列表
     *
     * @param person
     * @param justCreator
     * @return
     * @throws Exception
     */
    public List<Folder3> listZoneObject(String person, Boolean justCreator) throws Exception {
        List<String> list = business().getUserInfo(person);
        EntityManager em = this.entityManagerContainer().get(Folder3.class);
        EntityManager subEm = this.entityManagerContainer().get(ZonePermission.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaBuilder subCb = subEm.getCriteriaBuilder();
        CriteriaQuery<Folder3> cq = cb.createQuery(Folder3.class);
        Root<Folder3> root = cq.from(Folder3.class);
        Predicate p = cb.equal(root.get(Folder3_.superior), Business.TOP_FOLD);
        p = cb.and(p, cb.equal(root.get(Folder3_.status), FileStatus.VALID.getName()));
        if (BooleanUtils.isTrue(justCreator)) {
            p = cb.and(p, cb.equal(root.get(Folder3_.person), person));
        }

        Subquery<ZonePermission> subQuery = cq.subquery(ZonePermission.class);
        Root<ZonePermission> subRoot = subQuery.from(
                subEm.getMetamodel().entity(ZonePermission.class));
        subQuery.select(subRoot);
        Predicate p_permission = subCb.equal(subRoot.get(ZonePermission_.zoneId),
                root.get(Folder3_.id));
        p_permission = subCb.and(p_permission, subRoot.get(ZonePermission_.name).in(list));
        subQuery.where(p_permission);
        p = cb.and(p, cb.exists(subQuery));

        cq.where(p);
        return em.createQuery(cq).getResultList();
    }

    /**
     * 是否是共享区管理员
     *
     * @param zoneId
     * @param person
     * @return
     * @throws Exception
     */
    public boolean isZoneAdmin(String zoneId, String person) throws Exception {
        Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), zoneId, person,
                "isZoneAdmin");
        Optional<?> optional = CacheManager.get(zoneCacheCategory, cacheKey);
        if (optional.isPresent()) {
            return ((Boolean) optional.get());
        }
        List<String> list = business().getUserInfo(person);
        EntityManager em = this.entityManagerContainer().get(ZonePermission.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ZonePermission> root = cq.from(ZonePermission.class);
        Predicate p = cb.equal(root.get(ZonePermission_.zoneId), zoneId);
        p = cb.and(p, root.get(ZonePermission_.name).in(list));
        p = cb.and(p, cb.equal(root.get(ZonePermission_.role), ZoneRoleEnum.ADMIN.getValue()));
        cq.select(cb.count(root)).where(p);
        boolean flag = em.createQuery(cq).getSingleResult() > 0;
        CacheManager.put(zoneCacheCategory, cacheKey, flag);
        return flag;
    }

    /**
     * 是否是共享区编辑者
     *
     * @param zoneId
     * @param person
     * @return
     * @throws Exception
     */
    public boolean isZoneEditor(String zoneId, String person) throws Exception {
        Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), zoneId, person,
                "isZoneEditor");
        Optional<?> optional = CacheManager.get(zoneCacheCategory, cacheKey);
        if (optional.isPresent()) {
            return ((Boolean) optional.get());
        }
        List<String> list = business().getUserInfo(person);
        EntityManager em = this.entityManagerContainer().get(ZonePermission.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ZonePermission> root = cq.from(ZonePermission.class);
        Predicate p = cb.equal(root.get(ZonePermission_.zoneId), zoneId);
        p = cb.and(p, root.get(ZonePermission_.name).in(list));
        p = cb.and(p, root.get(ZonePermission_.role)
                .in(ZoneRoleEnum.ADMIN.getValue(), ZoneRoleEnum.EDITOR.getValue()));
        cq.select(cb.count(root)).where(p);
        boolean flag = em.createQuery(cq).getSingleResult() > 0;
        CacheManager.put(zoneCacheCategory, cacheKey, flag);
        return flag;
    }

    /**
     * 是否是共享区读者
     *
     * @param zoneId
     * @param person
     * @return
     * @throws Exception
     */
    public boolean isZoneReader(String zoneId, String person) throws Exception {
        Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), zoneId, person,
                "isZoneReader");
        Optional<?> optional = CacheManager.get(zoneCacheCategory, cacheKey);
        if (optional.isPresent()) {
            return (Boolean) optional.get();
        }
        List<String> list = business().getUserInfo(person);
        EntityManager em = this.entityManagerContainer().get(ZonePermission.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ZonePermission> root = cq.from(ZonePermission.class);
        Predicate p = cb.equal(root.get(ZonePermission_.zoneId), zoneId);
        p = cb.and(p, root.get(ZonePermission_.name).in(list));
        p = cb.and(p, root.get(ZonePermission_.role)
                .in(ZoneRoleEnum.ADMIN.getValue(), ZoneRoleEnum.EDITOR.getValue(),
                        ZoneRoleEnum.READER.getValue()));
        cq.select(cb.count(root)).where(p);
        boolean flag = em.createQuery(cq).getSingleResult() > 0;
        CacheManager.put(zoneCacheCategory, cacheKey, flag);
        return flag;
    }

    /**
     * 是否是否可查看
     *
     * @param zoneId
     * @param person
     * @return
     * @throws Exception
     */
    public boolean isZoneViewer(String zoneId, String person) throws Exception {
        Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), zoneId, person,
                "isZoneViewer");
        Optional<?> optional = CacheManager.get(zoneCacheCategory, cacheKey);
        if (optional.isPresent()) {
            return (Boolean) optional.get();
        }
        List<String> list = business().getUserInfo(person);
        EntityManager em = this.entityManagerContainer().get(ZonePermission.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ZonePermission> root = cq.from(ZonePermission.class);
        Predicate p = cb.equal(root.get(ZonePermission_.zoneId), zoneId);
        p = cb.and(p, root.get(ZonePermission_.name).in(list));
        cq.select(cb.count(root)).where(p);
        boolean flag = em.createQuery(cq).getSingleResult() > 0;
        CacheManager.put(zoneCacheCategory, cacheKey, flag);
        return flag;
    }

    /**
     * 获取指定对象（人员、组织或群组）在目录上的权限
     *
     * @param name
     * @param zoneId
     * @return
     * @throws Exception
     */
    public ZonePermission getZonePermission(String name, String zoneId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(ZonePermission.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ZonePermission> cq = cb.createQuery(ZonePermission.class);
        Root<ZonePermission> root = cq.from(ZonePermission.class);
        Predicate p = cb.equal(root.get(ZonePermission_.zoneId), zoneId);
        p = cb.and(p, cb.equal(root.get(ZonePermission_.name), name));
        List<ZonePermission> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
        return ListTools.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 获取指定目录和指定角色(可选)的所有权限
     *
     * @param zoneId
     * @param role
     * @return
     * @throws Exception
     */
    public List<ZonePermission> listZonePermission(String zoneId, String role) throws Exception {
        EntityManager em = this.entityManagerContainer().get(ZonePermission.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ZonePermission> cq = cb.createQuery(ZonePermission.class);
        Root<ZonePermission> root = cq.from(ZonePermission.class);
        Predicate p = cb.equal(root.get(ZonePermission_.zoneId), zoneId);
        if (StringUtils.isNotBlank(role)) {
            p = cb.and(p, cb.equal(root.get(ZonePermission_.role), role));
        }
        return em.createQuery(cq.where(p)).getResultList();
    }

    public List<String> listWithSuperior(String superior, String status) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Folder3.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Folder3> root = cq.from(Folder3.class);
        Predicate p = cb.equal(root.get(Folder3_.superior), superior);
        if (StringUtils.isNotEmpty(status)) {
            p = cb.and(p, cb.equal(root.get(Folder3_.status), status));
        }
        cq.select(root.get(Folder3_.id)).where(p);
        return em.createQuery(cq).getResultList();
    }

    public List<String> listSubNested(String id, String status) throws Exception {
        ListOrderedSet<String> list = new ListOrderedSet<>();
        List<String> subs = this.listSubDirect(id, status);
        for (String str : subs) {
            if (!list.contains(str)) {
                list.add(str);
                list.addAll(this.listSubNested(str, status));
            }
        }
        return list.asList();
    }

    public List<String> listSubDirect(String id, String status) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Folder3.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Folder3> root = cq.from(Folder3.class);
        Predicate p = cb.equal(root.get(Folder3_.superior), id);
        if (StringUtils.isNotEmpty(status)) {
            p = cb.and(p, cb.equal(root.get(Folder3_.status), status));
        }
        cq.select(root.get(Folder3_.id)).where(p);
        return em.createQuery(cq).getResultList();
    }

    public List<Folder3> listSubNested1(String id, String status) throws Exception {
        List<Folder3> list = new ArrayList<>();
        List<Folder3> subs = this.listSubDirect1(id, status);
        for (Folder3 folder : subs) {
            list.add(folder);
            list.addAll(this.listSubNested1(folder.getId(), status));
        }
        return list;
    }

    public List<Folder3> listSubDirect1(String id, String status) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Folder3.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Folder3> cq = cb.createQuery(Folder3.class);
        Root<Folder3> root = cq.from(Folder3.class);
        Predicate p = cb.equal(root.get(Folder3_.superior), id);
        if (StringUtils.isNotEmpty(status)) {
            p = cb.and(p, cb.equal(root.get(Folder3_.status), status));
        }
        return em.createQuery(cq.where(p)).getResultList();
    }

    public List<Folder3> listSubDirectObjectPermission(String id, String status,
            EffectivePerson effectivePerson) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Folder3.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Folder3> cq = cb.createQuery(Folder3.class);
        Root<Folder3> root = cq.from(Folder3.class);
        Predicate p = cb.equal(root.get(Folder3_.superior), id);
        if (StringUtils.isNotEmpty(status)) {
            p = cb.and(p, cb.equal(root.get(Folder3_.status), status));
        }
        if (business().getSystemConfig().getReadPermissionDown() && !business().controlAble(
                effectivePerson)) {
            List<String> list = business().getUserInfo(effectivePerson.getDistinguishedName());
            EntityManager subEm = this.entityManagerContainer().get(ZonePermission.class);
            CriteriaBuilder subCb = subEm.getCriteriaBuilder();
            Subquery<ZonePermission> subQuery = cq.subquery(ZonePermission.class);
            Root<ZonePermission> subRoot = subQuery.from(
                    subEm.getMetamodel().entity(ZonePermission.class));
            subQuery.select(subRoot);
            Predicate p_permission = subCb.equal(subRoot.get(ZonePermission_.zoneId),
                    root.get(Folder3_.id));
            p_permission = subCb.and(p_permission, subRoot.get(ZonePermission_.name).in(list));
            subQuery.where(p_permission);
            p = cb.and(p, cb.exists(subQuery));
        }
        return em.createQuery(cq.where(p)).getResultList();
    }

    public List<Folder3> listNotSetPermissionSubDirect(String id) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Folder3.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Folder3> cq = cb.createQuery(Folder3.class);
        Root<Folder3> root = cq.from(Folder3.class);
        Predicate p = cb.equal(root.get(Folder3_.superior), id);
        p = cb.and(p, cb.isFalse(root.get(Folder3_.hasSetPermission)));
        return em.createQuery(cq.where(p)).getResultList();
    }

    public Long countSubDirect(String id) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Folder3.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Folder3> root = cq.from(Folder3.class);
        Predicate p = cb.equal(root.get(Folder3_.superior), id);
        p = cb.and(p, cb.equal(root.get(Folder3_.status), FileStatus.VALID.getName()));
        cq.select(cb.count(root)).where(p);
        return em.createQuery(cq).getSingleResult();
    }

    public boolean exist(String name, String superior, String zoneId, String status,
            String excludeId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Folder3.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Folder3> root = cq.from(Folder3.class);
        Predicate p = cb.equal(root.get(Folder3_.superior), superior);
        p = cb.and(p, cb.equal(root.get(Folder3_.name), name));
        if (StringUtils.isNotBlank(status)) {
            p = cb.and(p, cb.equal(root.get(Folder3_.status), status));
        }
        if (StringUtils.isNotBlank(zoneId)) {
            p = cb.and(p, cb.equal(root.get(Folder3_.zoneId), zoneId));
        }
        if (StringUtils.isNotEmpty(excludeId)) {
            p = cb.and(p, cb.notEqual(root.get(Folder3_.id), excludeId));
        }
        cq.select(cb.count(root)).where(p);
        long count = em.createQuery(cq).getSingleResult();
        return count > 0;
    }

    private void listSuPNested(String id, List<Folder3> list) throws Exception {
        if (!Business.TOP_FOLD.equals(id)) {
            Folder3 folder = this.entityManagerContainer().find(id, Folder3.class);
            if (folder != null && !list.contains(folder)) {
                list.add(folder);
                if (!Business.TOP_FOLD.equals(folder.getSuperior())) {
                    listSuPNested(folder.getSuperior(), list);
                }
            }
        }
    }

    public List<String> listSuPNested(String id) throws Exception {
        List<Folder3> list = new ArrayList<>();
        listSuPNested(id, list);
        Collections.reverse(list);
        return list.stream().map(Folder3::getId).collect(Collectors.toList());
    }

    public List<Folder3> listSuPNestedObj(String id) throws Exception {
        List<Folder3> list = new ArrayList<>();
        listSuPNested(id, list);
        Collections.reverse(list);
        return list;
    }

    public String getSupPath(String id) throws Exception {
        String path = "";
        List<Folder3> list = listSuPNestedObj(id);
        if (!list.isEmpty()) {
            for (Folder3 folder : list) {
                path = path + "/" + folder.getName();
            }
        } else {
            path = "/";
        }
        return path;
    }

    public String adjustFileName(String superior, String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        list.add(fileName);
        for (int i = 1; i < 10; i++) {
            list.add(fileName + i);
        }
        list.add(StringTools.uniqueToken());
        EntityManager em = this.entityManagerContainer().get(Folder3.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Folder3> root = cq.from(Folder3.class);
        Predicate p = cb.equal(root.get(Folder3_.superior), superior);
        p = cb.and(p, root.get(Folder3_.name).in(list));
        p = cb.and(p, cb.equal(root.get(Folder3_.status), FileStatusEnum.VALID.getName()));
        cq.select(root.get(Folder3_.name)).where(p);
        List<String> os = em.createQuery(cq).getResultList();
        list = ListUtils.subtract(list, os);
        return list.get(0);
    }
}
