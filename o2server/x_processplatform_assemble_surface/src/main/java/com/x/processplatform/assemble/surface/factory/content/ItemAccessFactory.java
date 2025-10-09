package com.x.processplatform.assemble.surface.factory.content;

import com.google.gson.JsonElement;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.ItemAccess;
import com.x.query.core.entity.ItemAccess_;
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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ItemAccessFactory extends AbstractFactory {
    private static final Logger logger = LoggerFactory.getLogger(ItemAccessFactory.class);

    private final CacheCategory cacheCategory = new CacheCategory(ItemAccess.class);

    public ItemAccessFactory(Business abstractBusiness) throws Exception {
        super(abstractBusiness);
    }


    @SuppressWarnings("unchecked")
    public List<ItemAccess> listWithProcessOrApp(String processId, String appId) throws Exception {
        if(StringUtils.isEmpty(processId)){
            return Collections.emptyList();
        }
        CacheKey cacheKey = new CacheKey(ItemAccess.class, processId);
        Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
        if (optional.isPresent()) {
            return (List<ItemAccess>) optional.get();
        }
        Process process = this.business().process().pick(processId);
        if (process != null) {
            processId = process.getEdition();
        }
        List<ItemAccess> list = listObjectWithCategoryId(processId);
        if(ListTools.isEmpty(list)){
            list = listObjectWithCategoryId(appId);
        }
        CacheManager.put(cacheCategory, cacheKey, list);
        return list;
    }

    public List<ItemAccess> listObjectWithCategoryId(String categoryId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(ItemAccess.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ItemAccess> cq = cb.createQuery(ItemAccess.class);
        Root<ItemAccess> root = cq.from(ItemAccess.class);
        Predicate p = cb.equal(root.get(ItemAccess_.itemCategoryId), categoryId);
        p = cb.and(p, cb.equal(root.get(ItemAccess_.itemCategory), ItemCategory.pp));
        cq.select(root).where(p).orderBy(cb.asc(root.get(ItemAccess_.path)));
        List<ItemAccess> list = em.createQuery(cq).getResultList();
        list.forEach(em::detach);
        return list;
    }

    public void convert(JsonElement data, String processId, String appId, String activity,
            EffectivePerson effectivePerson) throws Exception {
        if(StringUtils.isEmpty(processId) && StringUtils.isEmpty(appId)){
            return;
        }
        List<ItemAccess> itemAccessList = this.listWithProcessOrApp(processId, appId);
        if (ListTools.isEmpty(itemAccessList) || this.business()
                .ifPersonCanManageApplicationOrProcess(effectivePerson, appId, processId)) {
            return;
        }
        List<String> authList = getPersonAuth(effectivePerson.getDistinguishedName());
        for (ItemAccess itemAccess : itemAccessList) {
            List<String> readerList = itemAccess.getProperties().getReaderAndEditorList();
            List<String> excludeReaderList = itemAccess.getProperties().getExcludeReaderListExcludeEditor();
            List<String> readActivityIdList = itemAccess.getProperties().getReadActivityIdList();
            List<String> excludeReadActivityIdList = itemAccess.getProperties().getExcludeReadActivityIdList();
            logger.debug("processId:{},path:{},readerList:{},readActivityIdList:{}",processId, itemAccess.getPath(), readerList, readActivityIdList);
            boolean remove = excludeReadActivityIdList.contains(activity) || ListTools.containsAny(excludeReaderList, authList);
            if(!remove){
                remove = (ListTools.isNotEmpty(readerList) || ListTools.isNotEmpty(readActivityIdList))
                                && (!readActivityIdList.contains(activity) && !ListTools.containsAny(
                                readerList, authList));
            }
            if (remove) {
                XGsonBuilder.removeWithPath(data, itemAccess.getPath());
            }
        }
    }

    /**
     * 保存数据的时候对不可编辑的字段用源数据覆盖
     * @param data
     * @param job
     * @param processId
     * @param appId
     * @param activity
     * @param effectivePerson
     * @throws Exception
     */
    public void editConvert(JsonElement data, String job, String processId, String appId, String activity,
            EffectivePerson effectivePerson) throws Exception {
        if(StringUtils.isEmpty(processId) && StringUtils.isEmpty(appId)){
            return;
        }
        List<ItemAccess> itemAccessList = this.listWithProcessOrApp(processId, appId);
        if (ListTools.isEmpty(itemAccessList) || this.business()
                .ifPersonCanManageApplicationOrProcess(effectivePerson, appId, processId)) {
            return;
        }
        List<Item> list = this.business().item().listWithJobWithPath(job);
        DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
        JsonElement oldData = converter.assemble(list);

        List<String> authList = getPersonAuth(effectivePerson.getDistinguishedName());
        for (ItemAccess itemAccess : itemAccessList) {
            List<String> editorList = itemAccess.getProperties().getEditorList();
            List<String> excludeEditorList = itemAccess.getProperties().getExcludeEditorList();
            List<String> editActivityIdList = itemAccess.getProperties().getEditActivityIdList();
            List<String> excludeEditActivityIdList = itemAccess.getProperties().getExcludeEditActivityIdList();
            boolean remove = excludeEditActivityIdList.contains(activity) || ListTools.containsAny(excludeEditorList, authList);
            if(!remove){
                if(ListTools.isNotEmpty(editorList) || ListTools.isNotEmpty(editActivityIdList)){
                    remove = !editActivityIdList.contains(activity) && !ListTools.containsAny(editorList, authList);
                }else {
                    List<String> readerList = itemAccess.getProperties().getReaderAndEditorList();
                    List<String> excludeReaderList = itemAccess.getProperties()
                            .getExcludeReaderListExcludeEditor();
                    List<String> readActivityIdList = itemAccess.getProperties()
                            .getReadActivityIdList();
                    List<String> excludeReadActivityIdList = itemAccess.getProperties()
                            .getExcludeReadActivityIdList();
                    logger.debug("processId:{},path:{},readerList:{},readActivityIdList:{}",
                            processId, itemAccess.getPath(), readerList, readActivityIdList);
                    remove = excludeReadActivityIdList.contains(activity) || ListTools.containsAny(
                            excludeReaderList, authList);
                    if (!remove) {
                        remove = (ListTools.isNotEmpty(readerList) || ListTools.isNotEmpty(
                                readActivityIdList))
                                && (!readActivityIdList.contains(activity)
                                && !ListTools.containsAny(
                                readerList, authList));
                    }
                }
            }
            if (remove) {
                XGsonBuilder.replaceWithPath(data, oldData, itemAccess.getPath());
            }
        }
    }

    private List<String> getPersonAuth(String person) throws Exception {
        List<String> authList = new ArrayList<>(
                this.business().organization().person().getAuthInfo(person));
        List<String> unitList = authList.stream()
                .filter(OrganizationDefinition::isUnitDistinguishedName)
                .collect(Collectors.toList());
        authList.addAll(this.business().organization().unit().listWithUnitSupNested(unitList));
        return authList.stream().distinct().collect(Collectors.toList());
    }

}
