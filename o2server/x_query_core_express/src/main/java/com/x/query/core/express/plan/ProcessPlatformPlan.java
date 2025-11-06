package com.x.query.core.express.plan;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.ApplicationBaseEntity;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.core.entity.Document;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.ItemAccess;
import com.x.query.core.entity.Item_;
import com.x.query.core.express.plan.ProcessPlatformPlan.WhereEntry.ApplicationEntry;
import com.x.query.core.express.plan.ProcessPlatformPlan.WhereEntry.ProcessEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ProcessPlatformPlan extends Plan {

    private static final long serialVersionUID = 8346759115447768182L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPlatformPlan.class);

    private static final CacheCategory processCache = new CacheCategory(Process.class);

    public ProcessPlatformPlan() {
        this.selectList = new SelectEntries();
        this.where = new WhereEntry();
        this.filterList = new TreeList<FilterEntry>();
    }

    public WhereEntry where = new WhereEntry();

    @Override
    void adjust() throws Exception {
        this.adjustRuntime();
        this.adjustWhere();
        // 先调整slectEntry 顺序不能改
        this.adjustSelectList();
    }

    private void adjustRuntime() {
        if (null == this.runtime) {
            this.runtime = new Runtime();
        }
        this.runtime.person = StringUtils.trimToEmpty(this.runtime.person);
        if (null == this.runtime.parameter) {
            this.runtime.parameter = new HashMap<String, String>();
        }
    }

    private void adjustWhere() throws Exception {
        if (null == this.where) {
            this.where = new WhereEntry();
        }
        this.where.dateRange.adjust();
    }

    private void adjustSelectList() {
        SelectEntries list = new SelectEntries();
        for (SelectEntry o : ListTools.trim(this.selectList, true, true)) {
            if (BooleanUtils.isTrue(o.available())) {
                list.add(o);
            }
        }
        this.selectList = list;
    }

    /**
     * 获取文档的路径访问权限
     *
     * @param bundles
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Pair<List<ItemAccess>, String>> listBundleItemAccess(List<String> bundles)
            throws Exception {
        if (this.runtime.isManager || BooleanUtils.isFalse(this.where.accessible)
                || StringUtils.isEmpty(this.runtime.person)) {
            return new HashMap<>();
        }
        Map<String, Pair<List<ItemAccess>, String>> map = new HashMap<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> fieldList = List.of(Review.process_FIELDNAME, Review.job_FIELDNAME,
                    Review.activityUnique_FIELDNAME);
            List<Review> reviewList = emc.fetchEqualAndIn(Review.class, fieldList,
                    Review.person_FIELDNAME, this.runtime.person, Review.job_FIELDNAME, bundles);
            List<String> processList = reviewList.stream().map(Review::getProcess).distinct()
                    .collect(Collectors.toList());
            Map<String, List<ItemAccess>> processMap = new HashMap<>();
            for (String processId : processList) {
                processMap.put(processId,
                        this.listItemAccess(emc, this.getProcessEdition(emc, processId)));
            }
            reviewList.forEach(o -> map.put(o.getJob(),
                    Pair.of(processMap.get(o.getProcess()), o.getActivityUnique())));
        }
        return map;
    }

    private String getProcessEdition(EntityManagerContainer emc, String processId)
            throws Exception {
        CacheKey cacheKey = new CacheKey("getProcessEdition", processId);
        Optional<?> optional = CacheManager.get(processCache, cacheKey);
        if (optional.isPresent()) {
            return (String) optional.get();
        } else {
            if (ListTools.isNotEmpty(this.where.processList)) {
                Optional<String> editionOpt = this.where.processList.stream()
                        .filter(o -> StringUtils.equals(o.id, processId)).map(o -> o.edition)
                        .findFirst();
                if (editionOpt.isPresent()) {
                    CacheManager.put(processCache, cacheKey, editionOpt.get());
                    return editionOpt.get();
                }
            }
            Process process = emc.find(processId, Process.class);
            if (process != null) {
                CacheManager.put(processCache, cacheKey, process.getEdition());
                return process.getEdition();
            }
        }
        return processId;
    }

    @Override
    public Pair<List<String>, Long> listBundlePaging() throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(ApplicationBaseEntity.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<Review> root = cq.from(Review.class);
            cq.select(root.get(Review_.job))
                    .where(this.reviewPredicate(emc, cb, root, cq));
            List<Order> orderList = new TreeList<>();
            this.joinPagingOrder(orderList, cb, root, cq, Review.job_FIELDNAME);
            if(orderList.isEmpty()) {
                Order order = cb.desc(root.get(Review_.startTime));
                orderList.add(order);
            }
            cq.orderBy(orderList.toArray(new Order[0]));
            int startPosition = (this.runtime.page == null || this.runtime.page < 1) ? 0 : (this.runtime.page - 1) * this.runtime.count;
            long start = System.currentTimeMillis();
            List<String> docIdList = em.createQuery(cq).setFirstResult(startPosition).setMaxResults(this.runtime.count).getResultList();
            LOGGER.info("listBundlePaging cost:{}", System.currentTimeMillis() - start);

            CriteriaQuery<Long> cq2 = cb.createQuery(Long.class);
            Root<Review> root2 = cq2.from(Review.class);
            cq2.select(cb.count(root2.get(Review_.id)))
                    .where(this.reviewPredicate(emc, cb, root2, cq2));
            start = System.currentTimeMillis();
            Long count = em.createQuery(cq2).getSingleResult();
            LOGGER.info("listBundlePaging count cost:{}", System.currentTimeMillis() - start);
            return Pair.of(docIdList, count);
        }
    }

    private Predicate reviewPredicate(EntityManagerContainer emc, CriteriaBuilder cb, Root<Review> root,
            CriteriaQuery<?> cq) throws Exception {
        Predicate p = this.where.reviewPredicateV2(cb, root, cq, this.runtime, this.filterList);
        if (BooleanUtils.isTrue(this.where.accessible) && (StringUtils.isNotEmpty(
                this.runtime.person) && !OrganizationDefinition.isSystemUser(this.runtime.person))) {
            p = cb.and(p, cb.equal(root.get(Review_.person), this.runtime.person));
            List<FilterEntry> pathList = new ArrayList<>();
            Stream.concat(this.filterList.stream(), this.runtime.filterList.stream()).filter(
                    Objects::nonNull).filter(o -> BooleanUtils.isTrue(o.available())).forEach(
                    pathList::add);
            Predicate accessPredicate = this.accessPredicate(emc, cb, root, pathList);
            if (accessPredicate != null) {
                p = cb.and(p, accessPredicate);
            }
        }else{
            p = cb.and(p, cb.equal(root.get(Review_.person), EffectivePerson.CIPHER));
        }
        if (this.where.scope.equals(SCOPE_WORK)) {
            p = cb.and(p, cb.isFalse(root.get(Review_.completed)));
        } else if (this.where.scope.equals(SCOPE_WORKCOMPLETED)) {
            p = cb.and(p, cb.isTrue(root.get(Review_.completed)));
        }
        return p;
    }

    @Override
    List<String> listBundle() throws Exception {
        List<FilterEntry> pathList = new ArrayList<>();
        List<FilterEntry> filterEntries = new TreeList<>();
        for (FilterEntry o : ListTools.trim(this.filterList, true, true)) {
            if (BooleanUtils.isTrue(o.available())) {
                filterEntries.add(o);
                pathList.add(o);
            }
        }
        List<FilterEntry> runtimeFilterEntries = new TreeList<>();
        for (FilterEntry o : ListTools.trim(this.runtime.filterList, true, true)) {
            if (BooleanUtils.isTrue(o.available())) {
                runtimeFilterEntries.add(o);
                pathList.add(o);
            }
        }
        List<String> jobs = listBundleByReview(pathList);
        if (!filterEntries.isEmpty()) {
            jobs = listBundleFilterEntry(jobs, filterEntries, threadPool);
        }
        if (!runtimeFilterEntries.isEmpty()) {
            jobs = listBundleFilterEntry(jobs, runtimeFilterEntries, threadPool);
        }
        return jobs;
    }

    private List<String> listBundleByReview(List<FilterEntry> pathList) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(Review.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<Review> root = cq.from(Review.class);
            Predicate p = this.where.reviewPredicate(cb, root);
            if (BooleanUtils.isTrue(this.where.accessible) && (StringUtils.isNotEmpty(
                    this.runtime.person) && !OrganizationDefinition.isSystemUser(this.runtime.person))) {
                p = cb.and(p, cb.equal(root.get(Review_.person), this.runtime.person));
                Predicate accessPredicate = this.accessPredicate(emc, cb, root, pathList);
                if (accessPredicate != null) {
                    p = cb.and(p, accessPredicate);
                }
            }
            if (this.where.scope.equals(SCOPE_WORK)) {
                p = cb.and(p, cb.isFalse(root.get(Review_.completed)));
            } else if (this.where.scope.equals(SCOPE_WORKCOMPLETED)) {
                p = cb.and(p, cb.isTrue(root.get(Review_.completed)));
            }
            cq.select(root.get(Review_.job)).where(p);
            cq.orderBy(cb.desc(root.get(Review_.startTime)));
            LOGGER.debug("listBundleByReview filter:{}", cq.toString());
            return em.createQuery(cq).getResultList().stream().distinct()
                    .collect(Collectors.toList());
        }
    }

    /**
     * 过滤字段权限列表，返回有权限的活动列表
     * 一个流程中存在一个字段不可查，则限制这个流程查询
     * 一个流程中多个限制字段的活动列表取交集，取交集后值为空则限制这个字段查询
     * 多个流程之间字段权限互不干扰
     * @param emc
     * @param root
     * @param pathList
     * @return
     * @throws Exception
     */
    private Predicate accessPredicate(EntityManagerContainer emc, CriteriaBuilder cb,
            Root<Review> root,
            List<FilterEntry> pathList) throws Exception {
        if (ListTools.isEmpty(this.where.processList) || ListTools.isEmpty(pathList)
                || ListTools.isEmpty(this.runtime.authList) || this.runtime.isManager) {
            return null;
        }
        boolean flag = false;
        Predicate p = cb.disjunction();
        List<String> processIdList = new ArrayList<>();
        for (ProcessEntry processEntry : this.where.processList) {
            processIdList.add(processEntry.id);
            Predicate predicate = cb.equal(root.get(Review_.process), processEntry.id);
            List<ItemAccess> accessList = new ArrayList<>(
                    this.listItemAccess(emc, processEntry.edition));
            if (ListTools.isNotEmpty(accessList)) {
                flag = true;
                SortTools.desc(accessList, ItemAccess.path_FIELDNAME);
                List<Triple<FilterEntry, Boolean, List<String>>> activityFilterList = this.getPathAccess(accessList, pathList);

                predicate = this.dearPathAccess(predicate, activityFilterList, processEntry, cb,
                        root);
            }
            LOGGER.debug("person {} access to process{} filter:{}", this.runtime.person,
                    processEntry.id, predicate.toString());
            p = cb.or(p, predicate);
        }
        if(flag && CollectionUtils.isNotEmpty(this.where.applicationList)){
            List<String> appIds = ListTools.extractField(this.where.applicationList,
                    JpaObject.id_FIELDNAME, String.class, true, true);
            Predicate predicate = root.get(Review_.application).in(appIds);
            predicate = cb.and(predicate, cb.not(root.get(Review_.process).in(processIdList)));
            p = cb.or(p, predicate);
        }
        return flag ? p : null;
    }

    private Predicate dearPathAccess(Predicate predicate,
            List<Triple<FilterEntry, Boolean, List<String>>> activityFilterList,
            ProcessEntry processEntry, CriteriaBuilder cb, Root<Review> root) {
        if (ListTools.isEmpty(activityFilterList)) {
            return predicate;
        }
        Optional<FilterEntry> optional = activityFilterList.stream()
                .filter(o -> BooleanUtils.isFalse(o.second())).map((Triple::first))
                .findFirst();
        if (optional.isPresent()) {
            LOGGER.info("person {} no access to process{} path:{}", this.runtime.person,
                    processEntry.id, optional.get().path);
            predicate = cb.and(predicate,
                    cb.equal(root.get(Review_.activityUnique), "nothing"));
        } else {
            List<List<String>> list = activityFilterList.stream().map((Triple::third))
                    .filter(ListTools::isNotEmpty).collect(
                            Collectors.toList());
            if (!list.isEmpty()) {
                Set<String> activitySet = new HashSet<>(
                        list.stream().filter(ListTools::isNotEmpty)
                                .reduce((list1, list2) -> {
                                    List<String> result = new ArrayList<>(list1);
                                    result.retainAll(list2);
                                    return result;
                                })
                                .orElse(new ArrayList<>()));
                if (activitySet.isEmpty()) {
                    predicate = cb.and(predicate,
                            cb.equal(root.get(Review_.activityUnique), "nothing"));
                } else {
                    predicate = cb.and(predicate,
                            root.get(Review_.activityUnique).in(activitySet));
                }
            }
        }
        return predicate;
    }

    private List<Triple<FilterEntry, Boolean, List<String>>> getPathAccess(
            List<ItemAccess> accessList, List<FilterEntry> pathList) throws Exception {
        List<Triple<FilterEntry, Boolean, List<String>>> activityList = new ArrayList<>();
        for (FilterEntry filter : pathList) {
            String path = filter.path;
            List<String> list = new ArrayList<>();
            List<String> excludeList = new ArrayList<>();
            boolean flag = true;
            int i = 0;
            for (ItemAccess itemAccess : accessList) {
                if (path.equalsIgnoreCase(itemAccess.getPath()) || path.startsWith(
                        itemAccess.getPath() + ".")) {
                    List<String> excludeReaderList = itemAccess.getProperties().getExcludeReaderListExcludeEditor(this.runtime.authList);
                    if(ListTools.containsAny(excludeReaderList, this.runtime.authList)){
                        flag = false;
                    }else {
                        List<String> readerList = itemAccess.getProperties()
                                .getReaderAndEditorList();
                        List<String> readActivityIdList = itemAccess.getProperties()
                                .getReadActivityIdList();
                        excludeList.addAll(itemAccess.getProperties().getExcludeReadActivityIdList());
                        if ((ListTools.isNotEmpty(readerList) || ListTools.isNotEmpty(
                                readActivityIdList)) && !ListTools.containsAny(readerList,
                                this.runtime.authList)) {
                            if (ListTools.isEmpty(readerList)) {
                                if (i == 0) {
                                    list.addAll(readActivityIdList);
                                } else {
                                    list.retainAll(readActivityIdList);
                                }
                            } else if (ListTools.isEmpty(readActivityIdList)) {
                                flag = false;
                            } else {
                                if (i == 0) {
                                    list.addAll(readActivityIdList);
                                } else {
                                    list.retainAll(readActivityIdList);
                                }
                            }
                            i++;
                        }
                    }
                    if(!flag){
                        break;
                    }
                }
            }
            if (i > 0 && list.isEmpty()) {
                flag = false;
            }
            list.removeAll(excludeList);
            activityList.add(Triple.of(filter, flag, list));
        }
        return activityList;
    }

    private List<String> listBundleFilterEntry(List<String> jobs, List<FilterEntry> filterEntries,
            ExecutorService threadPool) throws Exception {
        List<String> partJobs = new TreeList<>();
        List<List<String>> batchJobs = ListTools.batch(jobs, 500);
        for (int i = 0; i < filterEntries.size(); i++) {
            FilterEntry f = filterEntries.get(i);
            LOGGER.debug("listBundle_filterEntry:{}.", () -> f);
            List<String> os = new TreeList<>();
            List<CompletableFuture<List<String>>> futures = new TreeList<>();
            for (List<String> _batch : batchJobs) {
                CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
                    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance()
                            .create()) {
                        EntityManager em = emc.get(Item.class);
                        CriteriaBuilder cb = em.getCriteriaBuilder();
                        CriteriaQuery<String> cq = cb.createQuery(String.class);
                        Root<Item> root = cq.from(Item.class);
                        Predicate p = root.get(Item_.bundle).in(_batch);
                        p = f.toPredicate(cb, root, this.runtime, p);
                        cq.select(root.get(Item_.bundle)).where(p);
                        List<String> parts = em.createQuery(cq).getResultList();
                        List<String> list = new ArrayList<>(_batch);
                        list.retainAll(parts);
                        return list;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new TreeList<>();
                }, threadPool);
                futures.add(future);
            }
            for (CompletableFuture<List<String>> future : futures) {
                os.addAll(future.get(300, TimeUnit.SECONDS));
                LOGGER.debug("批次数据填充完成.");
            }
            // 不等于在这里单独通过等于处理
            if (Comparison.isNotEquals(f.comparison)) {
                os = ListUtils.subtract(jobs, os);
            }
            if (i == 0) {
                partJobs.addAll(os);
            } else {
                if (StringUtils.equals("and", f.logic)) {
                    partJobs = ListUtils.intersection(partJobs, os);
                } else {
                    partJobs = ListUtils.union(partJobs, os);
                }
            }
        }
        jobs = ListUtils.intersection(jobs, partJobs);
        return jobs;
    }

    public static class WhereEntry extends GsonPropertyObject {

        private static final long serialVersionUID = 8233208785074889649L;

        public Boolean accessible = false;

        public String scope = SCOPE_WORK;

        public List<ApplicationEntry> applicationList = new TreeList<>();
        public List<ProcessEntry> processList = new TreeList<>();
        public DateRangeEntry dateRange;
        public List<CreatorPersonEntry> creatorPersonList;
        public List<CreatorUnitEntry> creatorUnitList;
        public List<CreatorIdentityEntry> creatorIdentityList;

        Boolean available() {
            return StringUtils.equals(this.scope, SCOPE_WORK) || StringUtils.equals(this.scope,
                    SCOPE_WORKCOMPLETED)
                    || StringUtils.equals(this.scope, SCOPE_ALL);
        }

        public static class ApplicationEntry {

            public String name;

            public String alias;

            public String id;

        }

        public static class ProcessEntry {

            public String name;

            public String alias;

            public String id;

            public String edition;

            public String application;

        }

        public static class CreatorUnitEntry {

            public String name;

            public String id;

        }

        public static class CreatorIdentityEntry {

            public String name;

            public String id;

        }

        public static class CreatorPersonEntry {

            public String name;

            public String id;

        }

        private Predicate reviewPredicateV2(CriteriaBuilder cb, Root<Review> root,
                CriteriaQuery<?> cq, Runtime runtime, List<FilterEntry> filterList) throws Exception {
            List<Predicate> ps = new TreeList<>();
            ps.add(this.reviewPredicateProcess(cb, root));
            ps.add(this.reviewPredicateCreator(cb, root));
            ps.add(this.reviewPredicateDate(cb, root));
            ps = ListTools.trim(ps, true, false);
            if (ps.isEmpty()) {
                throw new IllegalAccessException("where is empty.");
            }
            //业务字段过滤
            ps.add(this.assembleFilterPredicate(cb, root, cq, runtime, filterList));
            ps.add(this.assembleFilterPredicate(cb, root, cq, runtime, runtime.filterList));
            ps = ListTools.trim(ps, true, false);
            return cb.and(ps.toArray(new Predicate[]{}));
        }

        private Predicate assembleFilterPredicate(CriteriaBuilder cb, Root<? extends JpaObject> root,
                CriteriaQuery<?> cq, Runtime runtime, List<FilterEntry> filterList) throws Exception{
            if(ListTools.isEmpty(filterList)){
                return null;
            }
            Predicate p = null;
            for (FilterEntry f : filterList) {
                if(StringUtils.isEmpty(f.path)){
                    continue;
                }
                Subquery<Long> subquery = cq.subquery(Long.class);
                Root<Item> itemRoot = subquery.from(Item.class);
                Predicate subPredicate = cb.equal(itemRoot.get(Item_.bundle), root.get(Review.job_FIELDNAME));
                subPredicate = f.toPredicate(cb, itemRoot, runtime, subPredicate);
                subquery.select(cb.literal(1L)).where(subPredicate);
                Predicate existsP = cb.exists(subquery);
                if (Comparison.isNotEquals(f.comparison)) {
                    existsP = cb.not(existsP);
                }
                if(p == null){
                    p = existsP;
                }else if(StringUtils.equalsIgnoreCase("or", f.logic)){
                    p = cb.or(p, existsP);
                }else{
                    p = cb.and(p, existsP);
                }
            }
            return p;
        }

        private Predicate reviewPredicate(CriteriaBuilder cb, Root<Review> root) throws Exception {
            List<Predicate> ps = new TreeList<>();
            ps.add(this.reviewPredicateProcess(cb, root));
            ps.add(this.reviewPredicateCreator(cb, root));
            ps.add(this.reviewPredicateDate(cb, root));
            ps = ListTools.trim(ps, true, false);
            if (ps.isEmpty()) {
                throw new IllegalAccessException("where is empty.");
            }
            return cb.and(ps.toArray(new Predicate[]{}));
        }

        private Predicate reviewPredicateProcess(CriteriaBuilder cb, Root<Review> root) throws Exception {
            List<String> applicationIds = ListTools.extractField(this.applicationList, JpaObject.id_FIELDNAME,
                    String.class, true, true);
            List<String> processIds = ListTools.extractField(this.processList,
                    JpaObject.id_FIELDNAME, String.class,
                    true, true);
            processIds = processIds.stream().filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            if (applicationIds.isEmpty() && processIds.isEmpty()) {
                return null;
            }
            Predicate p = cb.disjunction();
            if (!applicationIds.isEmpty()) {
                p = cb.or(p, root.get(Review_.application).in(applicationIds));
            }
            if (!processIds.isEmpty()) {
                p = cb.or(p, root.get(Review_.process).in(processIds));
            }
            return p;
        }

        private Predicate reviewPredicateCreator(CriteriaBuilder cb, Root<Review> root)
                throws Exception {
            List<String> creatorUnits = ListTools.extractField(this.creatorUnitList, "name",
                    String.class, true, true);
            List<String> creatorPersons = ListTools.extractField(this.creatorPersonList, "name",
                    String.class, true,
                    true);
            List<String> creatorIdentitys = ListTools.extractField(this.creatorIdentityList, "name",
                    String.class, true,
                    true);
            if (creatorUnits.isEmpty() && creatorPersons.isEmpty() && creatorIdentitys.isEmpty()) {
                return null;
            }
            Predicate p = cb.disjunction();
            if (!creatorUnits.isEmpty()) {
                p = cb.or(p, root.get(Review_.creatorUnit).in(creatorUnits));
            }
            if (!creatorPersons.isEmpty()) {
                p = cb.or(p, root.get(Review_.creatorPerson).in(creatorPersons));
            }
            if (!creatorIdentitys.isEmpty()) {
                p = cb.or(p, root.get(Review_.creatorIdentity).in(creatorIdentitys));
            }
            return p;
        }

        private Predicate reviewPredicateDate(CriteriaBuilder cb, Root<Review> root) {
            if (null == this.dateRange || (!this.dateRange.available())) {
                return null;
            }
            if (null == this.dateRange.start) {
                return cb.lessThanOrEqualTo(root.get(Review_.startTime), this.dateRange.completed);
            } else if (null == this.dateRange.completed) {
                return cb.greaterThanOrEqualTo(root.get(Review_.startTime), this.dateRange.start);
            } else {
                return cb.between(root.get(Review_.startTime), this.dateRange.start,
                        this.dateRange.completed);
            }
        }
    }
}
