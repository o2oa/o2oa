package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.wrapin.WiSiteFileInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.processplatform.core.entity.content.Attachment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;

class ActionReplaceToDoc extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionReplaceToDoc.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId, JsonElement jsonElement)
            throws Exception {

        LOGGER.debug("execute:{}, docId:{}.", effectivePerson::getDistinguishedName, () -> docId);

        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        Wo wo = new Wo();
        result.setData(wo);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Document document = documentQueryService.get(docId);
            if (null == document) {
                throw new ExceptionDocumentNotExists(docId);
            }
            if (!business.isDocumentEditor(effectivePerson, null, null, document)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            String person = effectivePerson.getDistinguishedName();
            if (StringUtils.isNotEmpty(wi.getPerson()) && effectivePerson.isManager()) {
                person = wi.getPerson();
            }
            if (ListTools.isEmpty(wi.getSiteFileInfoList())) {
                wo.setValue(false);
                return result;
            }
            Map<String, List<Attachment>> attMap = new HashMap<>();
            Map<String, List<FileInfo>> fileMap = new HashMap<>();
            for (WiSiteFileInfo siteFileInfo : wi.getSiteFileInfoList()) {
                if (StringUtils.isBlank(siteFileInfo.getSite())) {
                    continue;
                }
                if (WiSiteFileInfo.TYPE_PROCESS_PLATFORM.equals(siteFileInfo.getType())) {
                    List<Attachment> attList = checkAttachment(siteFileInfo.getFileInfoList(),
                            person, business);
                    attList.forEach(att -> att.setSite(siteFileInfo.getSite()));
                    attMap.put(siteFileInfo.getSite(), attList);
                } else if (WiSiteFileInfo.TYPE_CMS.equals(siteFileInfo.getType())) {
                    List<FileInfo> fileList = checkFileInfo(siteFileInfo.getFileInfoList(), person,
                            business);
                    fileList.forEach(att -> att.setSite(siteFileInfo.getSite()));
                    fileMap.put(siteFileInfo.getSite(), fileList);
                }
            }
            this.replaceSiteFile(attMap, fileMap, document, person, business);
        }
        CacheManager.notify(FileInfo.class);
        CacheManager.notify(Document.class);
        return result;
    }

    private void replaceSiteFile(Map<String, List<Attachment>> attMap,
            Map<String, List<FileInfo>> fileMap, Document document, String person, Business business) throws Exception {
        EntityManagerContainer emc = business.entityManagerContainer();
        StorageMapping mapping = ThisApplication.context().storageMappings()
                .random(FileInfo.class);
        for (Entry<String, List<Attachment>> entry : attMap.entrySet()){
            this.removeSiteFile(entry.getKey(), document.getId(), business);
            for (Attachment o : entry.getValue()) {
                StorageMapping attMapping = ThisApplication.context().storageMappings()
                        .get(Attachment.class, o.getStorage());
                byte[] bytes = o.readContent(attMapping);
                FileInfo fileInfo = creteFileInfo(person, document,
                        mapping, o.getName(), o.getSite());
                fileInfo.saveContent(mapping, bytes, o.getName(),
                        Config.general().getStorageEncrypt());
                fileInfo.setSeqNumber(o.getOrderNumber());
                emc.beginTransaction(FileInfo.class);
                emc.persist(fileInfo, CheckPersistType.all);
                emc.commit();
            }
        }
        for (Entry<String, List<FileInfo>> entry : fileMap.entrySet()){
            this.removeSiteFile(entry.getKey(), document.getId(), business);
            for (FileInfo o : entry.getValue()) {
                StorageMapping attMapping = ThisApplication.context().storageMappings()
                        .get(FileInfo.class, o.getStorage());
                byte[] bytes = o.readContent(attMapping);
                FileInfo fileInfo = creteFileInfo(person, document,
                        mapping, o.getName(), o.getSite());
                fileInfo.saveContent(mapping, bytes, o.getName(),
                        Config.general().getStorageEncrypt());
                fileInfo.setSeqNumber(o.getSeqNumber());
                emc.beginTransaction(FileInfo.class);
                emc.persist(fileInfo, CheckPersistType.all);
                emc.commit();
            }
        }
    }

    private void removeSiteFile(String site, String docId, Business business) throws Exception {
        EntityManagerContainer emc = business.entityManagerContainer();
        List<FileInfo> oldFileList = emc.listEqualAndEqual(FileInfo.class,
                FileInfo.documentId_FIELDNAME, docId, FileInfo.site_FIELDNAME, site);
        for (FileInfo o : oldFileList) {
            StorageMapping mapping = ThisApplication.context().storageMappings()
                    .get(FileInfo.class, o.getStorage());
            o.deleteContent(mapping);
            emc.beginTransaction(FileInfo.class);
            emc.remove(o);
            emc.commit();
        }
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 4382689061793305054L;

        @FieldDescribe("替换文档指定site的附件列表，先删再增，为空则不操作.")
        @FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiFileReplaceInfo", fieldValue = "[{'site':'附件框分类','fileInfoList':[{'id':'附件id','name':'附件名称'}]},'type':'附件来源(cms或processPlatform)']}]")
        private List<WiSiteFileInfo> siteFileInfoList;

        private String person;

        public List<WiSiteFileInfo> getSiteFileInfoList() {
            return siteFileInfoList;
        }

        public void setSiteFileInfoList(
                List<WiSiteFileInfo> siteFileInfoList) {
            this.siteFileInfoList = siteFileInfoList;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -5986602289699981815L;

        public Wo() {
            this.setValue(true);
        }
    }

}
