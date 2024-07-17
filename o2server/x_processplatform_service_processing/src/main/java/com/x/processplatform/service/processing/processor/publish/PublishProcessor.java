package com.x.processplatform.service.processing.processor.publish;

import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.EmbedCreatorType;
import com.x.processplatform.core.entity.element.PublishCmsCreatorType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.PublishTable;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WrapScriptObject;
import com.x.processplatform.service.processing.processor.AeiObjects;

/**
 * 数据发布节点处理器
 *
 * @author sword
 */
public class PublishProcessor extends AbstractPublishProcessor {

    public static final Logger LOGGER = LoggerFactory.getLogger(PublishProcessor.class);
    private static final String CMS_PUBLISH_URI = "document/cipher/publish/content";

    public PublishProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
        super(entityManagerContainer);
    }

    @Override
    protected Work arriving(AeiObjects aeiObjects, Publish publish) throws Exception {
        // 发送ProcessingSignal
        aeiObjects.getProcessingAttributes()
                .push(Signal.publishArrive(aeiObjects.getWork().getActivityToken(), publish));
        return aeiObjects.getWork();
    }

    @Override
    protected void arrivingCommitted(AeiObjects aeiObjects, Publish publish) throws Exception {
        // Do nothing
    }

    @Override
    protected List<Work> executing(AeiObjects aeiObjects, Publish publish) throws Exception {
        // 发送ProcessingSignal
        aeiObjects.getProcessingAttributes()
                .push(Signal.publishExecute(aeiObjects.getWork().getActivityToken(), publish));
        List<Work> results = new ArrayList<>();
        boolean passThrough = false;
        switch (publish.getPublishTarget()) {
            case Publish.PUBLISH_TARGET_CMS:
                // 发布到内容管理
                passThrough = this.publishToCms(aeiObjects, publish);
                break;
            case Publish.PUBLISH_TARGET_TABLE:
                // 发布到数据中心自建表
                passThrough = this.publishToTable(aeiObjects, publish);
                break;
            default:
                break;
        }
        if (passThrough) {
            results.add(aeiObjects.getWork());
        } else {
            LOGGER.info("work title:{}, id:{} public return false, stay in the current activity.",
                    () -> aeiObjects.getWork().getTitle(), () -> aeiObjects.getWork().getId());
        }
        return results;
    }

    private boolean publishToTable(AeiObjects aeiObjects, Publish publish) throws Exception {
        List<AssignPublish> list = this.evalTableBody(aeiObjects, publish);
        boolean flag = true;
        for (AssignPublish assignPublish : list) {
            WrapBoolean resp = ThisApplication.context().applications()
                    .postQuery(
                            x_query_service_processing.class, Applications.joinQueryUri("table",
                                    assignPublish.getTableName(), "update",
                                    aeiObjects.getWork().getJob()),
                            assignPublish.getData())
                    .getData(WrapBoolean.class);
            LOGGER.debug("publish to table：{}, result：{}", assignPublish.getTableName(),
                    resp.getValue());
            if (BooleanUtils.isFalse(resp.getValue())) {
                flag = false;
            }
        }
        return flag;
    }

    private List<AssignPublish> evalTableBody(AeiObjects aeiObjects, Publish publish)
            throws Exception {
        List<AssignPublish> list = new ArrayList<>();
        if (ListTools.isNotEmpty(publish.getPublishTableList())) {
            for (PublishTable publishTable : publish.getPublishTableList()) {
                AssignPublish assignPublish = new AssignPublish();
                assignPublish.setTableName(publishTable.getTableName());
                if (PublishTable.TABLE_DATA_BY_PATH.equals(publishTable.getQueryTableDataBy())) {
                    this.evalTableBodyFromData(aeiObjects, publishTable, assignPublish);
                } else {
                    this.evalTableBodyFromScript(aeiObjects, publishTable, assignPublish);
                }
                if (assignPublish.getData() == null) {
                    assignPublish.setData(gson.toJsonTree(aeiObjects.getData()));
                }
                list.add(assignPublish);
            }
        }
        return list;
    }

    private void evalTableBodyFromData(AeiObjects aeiObjects, PublishTable publishTable,
            AssignPublish assignPublish)
            throws Exception {
        if (StringUtils.isNotBlank(publishTable.getQueryTableDataPath())) {
            Object o = aeiObjects.getData().find(publishTable.getQueryTableDataPath());
            if (o != null) {
                assignPublish.setData(gson.toJsonTree(o));
            }
        }
    }

    private void evalTableBodyFromScript(AeiObjects aeiObjects, PublishTable publishTable,
            final AssignPublish assignPublish) throws Exception {
        WrapScriptObject assignBody = new WrapScriptObject();
        if (hasTableAssignDataScript(publishTable)) {
            Source source = aeiObjects.business().element()
                    .getCompiledScript(aeiObjects.getApplication().getId(),
                            publishTable.getTargetAssignDataScript(),
                            publishTable.getTargetAssignDataScriptText());
            GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
                    .putMember(GraalvmScriptingFactory.BINDING_NAME_JAXRSBODY, assignBody);
            GraalvmScriptingFactory.eval(source, bindings, jsonElement -> {
                if (!jsonElement.isJsonNull()) {
                    assignPublish.setData(jsonElement);
                }
            });
        }
    }

    private boolean publishToCms(AeiObjects aeiObjects, Publish publish) throws Exception {
        CmsDocument cmsDocument = this.evalCmsBody(aeiObjects, publish);
        Data data = aeiObjects.getData();
        cmsDocument.setWf_workId(aeiObjects.getWork().getId());
        cmsDocument.setWf_jobId(aeiObjects.getWork().getJob());
        cmsDocument.setIdentity(getCmsCreator(aeiObjects, publish));
        String categoryId = this.getCmsCategoryId(data, publish);
        if (StringUtils.isBlank(categoryId)) {
            LOGGER.warn("{}工作数据发布到内容管理失败：分类ID为空！",
                    () -> aeiObjects.getWork().getId());
            return false;
        }
        cmsDocument.setCategoryId(categoryId);
        if (BooleanUtils.isTrue(publish.getUseProcessForm())) {
            cmsDocument.setWf_formId(aeiObjects.getWork().getForm());
        }
        if (BooleanUtils.isTrue(publish.getInheritAttachment())) {
            List<Attachment> attachments = aeiObjects.getAttachments();
            if (ListTools.isNotEmpty(attachments)) {
                cmsDocument.setWf_attachmentIds(
                        ListTools.extractField(attachments, JpaObject.id_FIELDNAME, String.class,
                                true, true));
            }
        }
        String title = (String) data.find(publish.getTitleDataPath());
        if (StringUtils.isBlank(title)) {
            title = aeiObjects.getWork().getTitle();
        }
        cmsDocument.setTitle(StringTools.utf8SubString(title, 255));
        List<CmsPermission> readerList = new ArrayList<>();

        List<String> list = this.findPathData(data, publish.getReaderDataPathList(), true);
        if (ListTools.isNotEmpty(list)) {
            List<Review> reviewList = aeiObjects.getReviews();
            list.addAll(
                    ListTools.extractField(reviewList, Review.person_FIELDNAME, String.class, true,
                            true));
        }
        list.stream().forEach(s -> {
            CmsPermission cmsPermission = new CmsPermission();
            cmsPermission.setPermissionObjectName(s);
            readerList.add(cmsPermission);
        });
        cmsDocument.setReaderList(readerList);
        List<CmsPermission> authorList = new ArrayList<>();
        list = this.findPathData(data, publish.getAuthorDataPathList(), true);
        list.add(aeiObjects.getWork().getCreatorPerson());
        list.stream().forEach(s -> {
            CmsPermission cmsPermission = new CmsPermission();
            cmsPermission.setPermissionObjectName(s);
            authorList.add(cmsPermission);
        });
        cmsDocument.setAuthorList(authorList);
        cmsDocument.setPictureList(
                this.findPathData(data, publish.getPictureDataPathList(), false));
        WoId woId = ThisApplication.context().applications()
                .putQuery(x_cms_assemble_control.class, CMS_PUBLISH_URI, cmsDocument)
                .getData(WoId.class);
        LOGGER.info("流程数据发布-发布文档【{}】到内容管理返回：{}", aeiObjects.getWork().getTitle(),
                woId.getId());
        // 发送消息通知
        this.cmsDocumentNotify(aeiObjects, publish, woId);
        return true;
    }

    private String getCmsCreator(AeiObjects aeiObjects, Publish publish) throws Exception {
        PublishCmsCreatorType type = publish.getCmsCreatorType();
        if (null == type) {
            type = PublishCmsCreatorType.creator;
        }
        String value = "";
        switch (type) {
            case identity:
                value = publish.getCmsCreatorIdentity();
                break;
            case lastIdentity:
                value = this.findLastIdentity(aeiObjects.getWork());
                break;
            default:
                value = aeiObjects.getWork().getCreatorIdentity();
                break;
        }
        if (PublishCmsCreatorType.script.equals(type) && (
                StringUtils.isNotBlank(publish.getCmsCreatorScript()) || StringUtils.isNotBlank(
                        publish.getCmsCreatorScriptText()))) {
            GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings();
            Source source = aeiObjects.business().element()
                    .getCompiledScript(aeiObjects.getWork().getApplication(),
                            publish, Business.EVENT_PUBLISHCMSCREATOR);
            List<String> os = GraalvmScriptingFactory.evalAsDistinguishedNames(source, bindings);
            os = ListTools.trim(os, true, false);
            if (ListTools.isNotEmpty(os)) {
                value = os.get(0);
            }
        }
        return StringUtils.isBlank(value) ? aeiObjects.getWork().getCreatorIdentity() : value;
    }

    private String findLastIdentity(Work work) throws Exception {
        EntityManagerContainer emc = this.entityManagerContainer();
        String id = this.business().taskCompleted().getLastWithWork(work.getId());
        if (StringUtils.isEmpty(id)) {
            return work.getCreatorIdentity();
        } else {
            TaskCompleted o = emc.find(id, TaskCompleted.class);
            return o.getIdentity();
        }
    }

    private String getCmsCategoryId(Data data, Publish publish) throws Exception {
        if (Publish.CMS_CATEGORY_FROM_DATA.equals(publish.getCategorySelectType())) {
            return StringUtils.isNotBlank(publish.getCategoryIdDataPath())
                    ? (String) data.find(publish.getCategoryIdDataPath())
                    : "";
        } else {
            return publish.getCategoryId();
        }
    }

    private void cmsDocumentNotify(AeiObjects aeiObjects, Publish publish, WoId woId) {
        try {
            if (StringUtils.isNotBlank(publish.getNotifyDataPathList())) {
                List<String> list = this.findPathData(aeiObjects.getData(),
                        publish.getNotifyDataPathList(), true);
                if (ListTools.isNotEmpty(list)) {
                    CmsNotify cmsNotify = new CmsNotify(list);
                    ThisApplication.context().applications()
                            .postQuery(x_cms_assemble_control.class, Applications
                                    .joinQueryUri("document", woId.getId(), "notify"), cmsNotify);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private CmsDocument evalCmsBody(AeiObjects aeiObjects, Publish publish) throws Exception {
        CmsDocument cmsDocument = new CmsDocument();
        WrapScriptObject assignBody = new WrapScriptObject();
        if (hasCmsAssignDataScript(publish)) {
            Source source = aeiObjects.business().element()
                    .getCompiledScript(aeiObjects.getApplication().getId(),
                            aeiObjects.getActivity(), Business.EVENT_PUBLISHCMSBODY);
            GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
                    .putMember(GraalvmScriptingFactory.BINDING_NAME_JAXRSBODY, assignBody);
            GraalvmScriptingFactory.eval(source, bindings, jsonElement -> {
                if (!jsonElement.isJsonNull()) {
                    cmsDocument.setDocData(jsonElement);
                }
            });
        }
        if (cmsDocument.getDocData() == null) {
            cmsDocument.setDocData(gson.toJsonTree(aeiObjects.getData()));
        }
        return cmsDocument;
    }

    /**
     * 取得通过路径指定的人员组织
     */
    private List<String> findPathData(Data data, String path, boolean isUser) throws Exception {
        List<String> list = new ArrayList<>();
        String[] paths = StringUtils.isNotBlank(path) ? path.split(",") : new String[0];
        for (String str : paths) {
            if (isUser) {
                list.addAll(data.extractDistinguishedName(str.trim()));
            } else {
                Object o = data.find(str.trim());
                if (o != null) {
                    list.addAll(GraalvmScriptingFactory.Helper.stringOrDistinguishedNameAsList(
                            gson.toJsonTree(o)));
                }
            }
        }
        return list;
    }

    public class AssignPublish {

        private String tableName;

        private JsonElement data;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public JsonElement getData() {
            return data;
        }

        public void setData(JsonElement data) {
            this.data = data;
        }
    }

    public class CmsDocument extends GsonPropertyObject {

        private static final long serialVersionUID = -1644861126932404754L;

        private String identity;
        private String title;
        private String wf_workId;
        private String wf_jobId;
        private String wf_formId;
        private String summary;
        private String categoryId;
        private List<String> pictureList;
        private List<String> wf_attachmentIds;
        private JsonElement docData;
        private List<CmsPermission> readerList;
        private List<CmsPermission> authorList;

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getWf_workId() {
            return wf_workId;
        }

        public void setWf_workId(String wf_workId) {
            this.wf_workId = wf_workId;
        }

        public String getWf_jobId() {
            return wf_jobId;
        }

        public void setWf_jobId(String wf_jobId) {
            this.wf_jobId = wf_jobId;
        }

        public String getWf_formId() {
            return wf_formId;
        }

        public void setWf_formId(String wf_formId) {
            this.wf_formId = wf_formId;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<String> getPictureList() {
            return pictureList;
        }

        public void setPictureList(List<String> pictureList) {
            this.pictureList = pictureList;
        }

        public List<String> getWf_attachmentIds() {
            return wf_attachmentIds;
        }

        public void setWf_attachmentIds(List<String> wf_attachmentIds) {
            this.wf_attachmentIds = wf_attachmentIds;
        }

        public JsonElement getDocData() {
            return docData;
        }

        public void setDocData(JsonElement docData) {
            this.docData = docData;
        }

        public List<CmsPermission> getReaderList() {
            return readerList;
        }

        public void setReaderList(List<CmsPermission> readerList) {
            this.readerList = readerList;
        }

        public List<CmsPermission> getAuthorList() {
            return authorList;
        }

        public void setAuthorList(List<CmsPermission> authorList) {
            this.authorList = authorList;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }
    }

    public class CmsPermission extends GsonPropertyObject {

        private static final long serialVersionUID = -5739174280288142754L;
        private String permissionObjectName;

        public String getPermissionObjectName() {
            return permissionObjectName;
        }

        public void setPermissionObjectName(String permissionObjectName) {
            this.permissionObjectName = permissionObjectName;
        }
    }

    public class CmsNotify extends GsonPropertyObject {

        private static final long serialVersionUID = 5831963157463553841L;

        public CmsNotify(List<String> notifyPersonList) {
            this.notifyPersonList = notifyPersonList;
        }

        private List<String> notifyPersonList;
        private Boolean notifyByDocumentReadPerson = false;
        private Boolean notifyCreatePerson = false;

        public List<String> getNotifyPersonList() {
            return notifyPersonList;
        }

        public void setNotifyPersonList(List<String> notifyPersonList) {
            this.notifyPersonList = notifyPersonList;
        }

        public Boolean getNotifyByDocumentReadPerson() {
            return notifyByDocumentReadPerson;
        }

        public void setNotifyByDocumentReadPerson(Boolean notifyByDocumentReadPerson) {
            this.notifyByDocumentReadPerson = notifyByDocumentReadPerson;
        }

        public Boolean getNotifyCreatePerson() {
            return notifyCreatePerson;
        }

        public void setNotifyCreatePerson(Boolean notifyCreatePerson) {
            this.notifyCreatePerson = notifyCreatePerson;
        }
    }

    @Override
    protected void executingCommitted(AeiObjects aeiObjects, Publish publish, List<Work> works)
            throws Exception {
        // Do nothing
    }

    @Override
    protected Optional<Route> inquiring(AeiObjects aeiObjects, Publish publish) throws Exception {
        // 发送ProcessingSignal
        aeiObjects.getProcessingAttributes()
                .push(Signal.publishInquire(aeiObjects.getWork().getActivityToken(), publish));
        return aeiObjects.getRoutes().stream().findFirst();
    }

    @Override
    protected void inquiringCommitted(AeiObjects aeiObjects, Publish publish) throws Exception {
        // Do nothing
    }

    private boolean hasCmsAssignDataScript(Publish publish) {
        return StringUtils.isNotEmpty(publish.getTargetAssignDataScript())
                || StringUtils.isNotEmpty(publish.getTargetAssignDataScriptText());
    }

    private boolean hasTableAssignDataScript(PublishTable publishTable) {
        return StringUtils.isNotEmpty(publishTable.getTargetAssignDataScript())
                || StringUtils.isNotEmpty(publishTable.getTargetAssignDataScriptText());
    }
}
