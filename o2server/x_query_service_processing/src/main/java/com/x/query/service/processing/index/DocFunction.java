package com.x.query.service.processing.index;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.hankcs.hanlp.HanLP;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.Item;
import com.x.query.core.express.index.Indexs;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;

public class DocFunction {

    private DocFunction() {
        // nothing
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DocFunction.class);

    private static final List<String> PROCESSPLATFORM_REVIEW_FIELDS = new UnmodifiableList<>(
            Arrays.asList(Review.person_FIELDNAME));

    private static final List<String> CMS_REVIEW_FIELDS = new UnmodifiableList<>(
            Arrays.asList(com.x.cms.core.entity.Review.permissionObj_FIELDNAME));

    private static final DataItemConverter<Item> CONVERTER = new DataItemConverter<>(Item.class);

    private static final Gson gson = XGsonBuilder.instance();

    public static final Function<Pair<Business, String>, Pair<String, Optional<Doc>>> wrapWork = param -> {
        try {
            Work work = param.first().entityManagerContainer().find(param.second(),
                    Work.class);
            if (null != work) {
                LOGGER.debug("DocFunction wrapWork:{}.", param.second());
                Doc doc = new Doc();
                doc.setReaders(readers(param.first(), work));
                doc.setId(work.getJob());
                doc.setCategory(Indexs.CATEGORY_PROCESSPLATFORM);
                doc.setType(Indexs.TYPE_WORK);
                doc.setKey(work.getApplication());
                doc.setTitle(work.getTitle());
                doc.setCreateTime(work.getCreateTime());
                doc.setUpdateTime(work.getUpdateTime());
                doc.setCreateTimeMonth(DateTools.format(work.getCreateTime(), DateTools.format_yyyyMM));
                doc.setUpdateTimeMonth(DateTools.format(work.getUpdateTime(), DateTools.format_yyyyMM));
                doc.setCreatorPerson(OrganizationDefinition.name(work.getCreatorPerson()));
                if (StringUtils.isEmpty(doc.getCreatorPerson())) {
                    doc.setCreatorPerson(Config.query().index().getCreatorPersonUnknown());
                }
                if (StringUtils.equals(EffectivePerson.CIPHER, doc.getCreatorPerson())) {
                    doc.setCreatorPerson(Config.query().index().getCreatorPersonCipher());
                }
                doc.setCreatorUnit(OrganizationDefinition.name(work.getCreatorUnit()));
                if (StringUtils.isEmpty(doc.getCreatorUnit())) {
                    doc.setCreatorUnit(Config.query().index().getCreatorUnitUnknown());
                }
                doc.addBoolean(Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN, TaskCompleted.completed_FIELDNAME, false);
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.creatorUnitLevelName_FIELDNAME,
                        work.getCreatorUnitLevelName());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.application_FIELDNAME,
                        work.getApplication());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.applicationName_FIELDNAME,
                        work.getApplicationName());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.applicationAlias_FIELDNAME,
                        work.getApplicationAlias());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.process_FIELDNAME, work.getProcess());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.processName_FIELDNAME,
                        work.getProcessName());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.processAlias_FIELDNAME,
                        work.getProcessAlias());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.job_FIELDNAME, work.getJob());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.serial_FIELDNAME, work.getSerial());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, Work.activityName_FIELDNAME,
                        work.getActivityName());
                workAddTaskPersonNames(doc, param.first(), work);
                update(param.first(), work, doc);
                return Pair.of(work.getApplication(), Optional.of(doc));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Pair.of("", Optional.empty());
    };

    public static final Function<Pair<Business, String>, Pair<String, Optional<Doc>>> wrapWorkCompleted = param -> {
        try {
            WorkCompleted workCompleted = param.first().entityManagerContainer().find(param.second(),
                    WorkCompleted.class);
            if (null != workCompleted) {
                LOGGER.debug("DocFunction wrapWorkCompleted:{}.", param.second());
                Doc doc = new Doc();
                doc.setReaders(readers(param.first(), workCompleted));
                doc.setId(workCompleted.getJob());
                doc.setCategory(Indexs.CATEGORY_PROCESSPLATFORM);
                doc.setType(Indexs.TYPE_WORKCOMPLETED);
                doc.setKey(workCompleted.getApplication());
                doc.setTitle(workCompleted.getTitle());
                doc.setCreateTime(workCompleted.getCreateTime());
                doc.setUpdateTime(workCompleted.getUpdateTime());
                doc.setCreateTimeMonth(DateTools.format(workCompleted.getCreateTime(), DateTools.format_yyyyMM));
                doc.setUpdateTimeMonth(DateTools.format(workCompleted.getUpdateTime(), DateTools.format_yyyyMM));
                doc.setCreatorPerson(OrganizationDefinition.name(workCompleted.getCreatorPerson()));
                doc.setCreatorUnit(OrganizationDefinition.name(workCompleted.getCreatorUnit()));
                doc.addBoolean(Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN, TaskCompleted.completed_FIELDNAME, true);
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, WorkCompleted.creatorUnitLevelName_FIELDNAME,
                        workCompleted.getCreatorUnitLevelName());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, WorkCompleted.application_FIELDNAME,
                        workCompleted.getApplication());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, WorkCompleted.applicationName_FIELDNAME,
                        workCompleted.getApplicationName());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, WorkCompleted.applicationAlias_FIELDNAME,
                        workCompleted.getApplicationAlias());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, WorkCompleted.process_FIELDNAME,
                        workCompleted.getProcess());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, WorkCompleted.processName_FIELDNAME,
                        workCompleted.getProcessName());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, WorkCompleted.processAlias_FIELDNAME,
                        workCompleted.getProcessAlias());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, WorkCompleted.job_FIELDNAME,
                        workCompleted.getJob());
                doc.addString(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING, WorkCompleted.serial_FIELDNAME,
                        workCompleted.getSerial());
                doc.addBoolean(Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN, WorkCompleted.expired_FIELDNAME,
                        workCompleted.getExpired());
                doc.addDate(Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE, WorkCompleted.expireTime_FIELDNAME,
                        workCompleted.getExpireTime());
                update(param.first(), workCompleted, doc);
                return Pair.of(workCompleted.getApplication(), Optional.of(doc));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Pair.of("", Optional.empty());
    };

    public static final Function<Pair<Business, String>, Pair<String, Optional<Doc>>> wrapDocument = param -> {
        try {
            Document document = param.first().entityManagerContainer().find(param.second(), Document.class);
            if ((null != document) && (!StringUtils.equals(document.getDocumentType(), Document.DOCUMENT_TYPE_DATA))) {
                LOGGER.debug("DocFunction wrapDocument:{}.", param.second());
                Doc doc = new Doc();
                doc.setReaders(readers(param.first(), document));
                doc.setId(document.getId());
                doc.setCategory(Indexs.CATEGORY_CMS);
                doc.setType(Indexs.TYPE_DOCUMENT);
                doc.setKey(document.getAppId());
                doc.setTitle(document.getTitle());
                doc.setCreateTime(document.getCreateTime());
                doc.setUpdateTime(document.getUpdateTime());
                doc.setCreateTimeMonth(DateTools.format(document.getCreateTime(), DateTools.format_yyyyMM));
                doc.setUpdateTimeMonth(DateTools.format(document.getUpdateTime(), DateTools.format_yyyyMM));
                doc.setCreatorPerson(OrganizationDefinition.name(document.getCreatorPerson()));
                doc.setCreatorUnit(OrganizationDefinition.name(document.getCreatorUnitName()));
                doc.addString(Indexs.PREFIX_FIELD_CMS_STRING, Document.appId_FIELDNAME,
                        document.getAppId());
                doc.addString(Indexs.PREFIX_FIELD_CMS_STRING, Document.appName_FIELDNAME,
                        document.getAppName());
                doc.addString(Indexs.PREFIX_FIELD_CMS_STRING, Document.appAlias_FIELDNAME, document.getAppAlias());
                doc.addString(Indexs.PREFIX_FIELD_CMS_STRING, Document.categoryId_FIELDNAME, document.getCategoryId());
                doc.addString(Indexs.PREFIX_FIELD_CMS_STRING, Document.categoryName_FIELDNAME,
                        document.getCategoryName());
                doc.addString(Indexs.PREFIX_FIELD_CMS_STRING, Document.categoryAlias_FIELDNAME,
                        document.getCategoryAlias());
                doc.addString(Indexs.PREFIX_FIELD_CMS_STRING, Indexs.FIELD_DESCRIPTION, document.getDescription());
                doc.addDate(Indexs.PREFIX_FIELD_CMS_STRING, Indexs.FIELD_PUBLISHTIME, document.getPublishTime());
                doc.addDate(Indexs.PREFIX_FIELD_CMS_STRING, Indexs.FIELD_MODIFYTIME, document.getModifyTime());
                update(param.first(), document, doc, Config.query().index().getDataStringThreshold());
                return Pair.of(document.getAppId(), Optional.of(doc));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Pair.of("", Optional.empty());
    };

    private static List<String> readers(Business business, Work work) throws Exception {
        List<String> list = business.entityManagerContainer()
                .fetchEqualAndEqual(Review.class, PROCESSPLATFORM_REVIEW_FIELDS, Review.job_FIELDNAME,
                        work.getJob(), Review.application_FIELDNAME, work.getApplication())
                .stream().map(Review::getPerson).filter(StringUtils::isNotBlank).distinct()
                .collect(Collectors.toList());
        list.add(work.getApplication());
        list.add(work.getProcess());
        Optional<Process> optional = business.process().get(work.getProcess());
        if (optional.isPresent()) {
            list.add(optional.get().getId());
            String edition = optional.get().getEdition();
            if (StringUtils.isNotEmpty(edition)) {
                list.add(edition);
            }
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    private static void workAddTaskPersonNames(Doc doc, Business business, Work work) throws Exception {
        List<Task> list = business.entityManagerContainer().listEqualAndEqual(Task.class, Task.job_FIELDNAME,
                work.getJob(), Task.application_FIELDNAME, work.getApplication());
        doc.addStrings(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS, Indexs.FIELD_ROCESSPLATFORM_TASKPERSONNAMES,
                list.stream().map(Task::getPerson).filter(StringUtils::isNotBlank)
                        .map(OrganizationDefinition::name).distinct()
                        .collect(Collectors.toList()));
        doc.addStrings(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS, Indexs.FIELD_ROCESSPLATFORM_PREVTASKPERSONNAMES,
                list.stream().map(Task::getPerson).filter(StringUtils::isNotBlank)
                        .map(OrganizationDefinition::name).distinct()
                        .collect(Collectors.toList()));
    }

    private static List<String> readers(Business business, WorkCompleted workCompleted) throws Exception {
        List<String> list = business.entityManagerContainer()
                .fetchEqualAndEqual(Review.class, PROCESSPLATFORM_REVIEW_FIELDS, Review.job_FIELDNAME,
                        workCompleted.getJob(), Review.application_FIELDNAME, workCompleted.getApplication())
                .stream().map(Review::getPerson).filter(StringUtils::isNotBlank).distinct()
                .collect(Collectors.toList());
        list.add(workCompleted.getApplication());
        list.add(workCompleted.getProcess());
        Optional<Process> optional = business.process().get(workCompleted.getProcess());
        if (optional.isPresent()) {
            list.add(optional.get().getId());
            String edition = optional.get().getEdition();
            if (StringUtils.isNotEmpty(edition)) {
                list.add(edition);
            }
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    private static List<String> readers(Business business, com.x.cms.core.entity.Document document) throws Exception {
        List<String> list = business.entityManagerContainer()
                .fetchEqualAndEqual(com.x.cms.core.entity.Review.class, CMS_REVIEW_FIELDS,
                        com.x.cms.core.entity.Review.docId_FIELDNAME, document.getId(),
                        com.x.cms.core.entity.Review.appId_FIELDNAME, document.getAppId())
                .stream().map(com.x.cms.core.entity.Review::getPermissionObj).filter(StringUtils::isNotBlank).distinct()
                .collect(Collectors.toList());
        list.add(document.getAppId());
        list.add(document.getCategoryId());
        return list;
    }

    private static void update(Business business, Work work, Doc wrap) {
        List<Item> items = null;
        try {
            items = business.entityManagerContainer().listEqualAndEqual(Item.class,
                    DataItem.bundle_FIELDNAME,
                    work.getJob(), DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
            if (!ListTools.isEmpty(items)) {
                wrap.setBody(DataItemConverter.ItemText.text(items, true, true, true, true, true, ","));
                wrap.setSummary(HanLP.getSummary(wrap.getBody(), Config.query().index().getSummaryLength()));
                if (BooleanUtils.isTrue((Config.query().index().getWorkIndexAttachment()))) {
                    wrap.setAttachment(attachment(business, work.getJob()));
                } else {
                    wrap.setAttachment("");
                }
                update(wrap, CONVERTER.assemble(items), "", Config.query().index().getDataStringThreshold());
            } else {
                LOGGER.warn("class:DocFunction, function:update work:{}, items is empty.", work.getId());
            }
        } catch (Exception e) {
            LOGGER.error(new ExceptionWorkUpdate(e, work.getId(), items));
        }
    }

    private static void update(Business business, WorkCompleted workCompleted, Doc wrap) {
        List<Item> items = null;
        try {
            if (BooleanUtils.isTrue(workCompleted.getMerged())) {
                Data data = workCompleted.getData();
                items = CONVERTER.disassemble(gson.toJsonTree(data));
            } else {
                items = business.entityManagerContainer().listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME,
                        workCompleted.getJob(), DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
            }
            if (!ListTools.isEmpty(items)) {
                wrap.setBody(DataItemConverter.ItemText.text(items, true, true, true, true, true, ","));
                wrap.setSummary(HanLP.getSummary(wrap.getBody(), Config.query().index().getSummaryLength()));
                if (BooleanUtils.isTrue((Config.query().index().getWorkCompletedIndexAttachment()))) {
                    wrap.setAttachment(attachment(business, workCompleted.getJob()));
                } else {
                    wrap.setAttachment("");
                }
                update(wrap, CONVERTER.assemble(items), "", Config.query().index().getDataStringThreshold());
            } else {
                LOGGER.warn("class:DocFunction, function:update workCompleted:{}, items is empty.",
                        workCompleted.getId());
            }
        } catch (Exception e) {
            LOGGER.error(new ExceptionWorkCompletedUpdate(e, workCompleted.getId(), items));
        }
    }

    private static void update(Business business, com.x.cms.core.entity.Document document, Doc wrap,
            Integer dataStringThreshold) {
        List<Item> items = null;
        try {
            items = business.entityManagerContainer().listEqualAndEqual(Item.class,
                    DataItem.bundle_FIELDNAME, document.getId(), DataItem.itemCategory_FIELDNAME, ItemCategory.cms);
            if (!ListTools.isEmpty(items)) {
                wrap.setBody(DataItemConverter.ItemText.text(items, true, true, true, true, true, ","));
                wrap.setSummary(HanLP.getSummary(wrap.getBody(), Config.query().index().getSummaryLength()));
                if (BooleanUtils.isTrue((Config.query().index().getWorkCompletedIndexAttachment()))) {
                    wrap.setAttachment(attachment(business, document));
                } else {
                    wrap.setAttachment("");
                }
                update(wrap, CONVERTER.assemble(items), "", dataStringThreshold);
            } else {
                LOGGER.warn("class:DocFunction, function:update document:{}, items is empty.", document.getId());
            }
        } catch (Exception e) {
            LOGGER.error(new ExceptionDocumentUpdate(e, document.getId(), items));
        }
    }

    private static void update(Doc wrap, JsonElement jsonElement, String name, Integer dataStringThreshold) {
        if (null != jsonElement && (!jsonElement.isJsonNull()) && (!StringUtils.startsWith(name, "$"))) {
            if (jsonElement.isJsonPrimitive()) {
                updatePrimitive(wrap, jsonElement.getAsJsonPrimitive(), name, dataStringThreshold);
            } else if (jsonElement.isJsonArray()) {
                updateArray(wrap, jsonElement.getAsJsonArray(), name, dataStringThreshold);
            } else if (jsonElement.isJsonObject()) {
                jsonElement.getAsJsonObject().entrySet().stream().forEach(o -> update(wrap, o.getValue(),
                        StringUtils.isEmpty(name) ? o.getKey() : (name + "." + o.getKey()), dataStringThreshold));
            }
        }
    }

    public static void updatePrimitive(Doc doc, JsonPrimitive jsonPrimitive, String name,
            Integer dataStringThreshold) {
        if (jsonPrimitive.isString()) {
            String value = jsonPrimitive.getAsString();
            if (StringUtils.length(value) <= dataStringThreshold) {
                if (BooleanUtils.isTrue(DateTools.isDateTimeOrDateOrTime(value))) {
                    try {
                        doc.addDate(Indexs.PREFIX_FIELD_DATA_DATE, name, DateTools.parse(value));
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                } else {
                    doc.addString(Indexs.PREFIX_FIELD_DATA_STRING, name, value);
                }
            }
        } else if (jsonPrimitive.isBoolean()) {
            doc.addBoolean(Indexs.PREFIX_FIELD_DATA_BOOLEAN, name, jsonPrimitive.getAsBoolean());
        } else if (jsonPrimitive.isNumber()) {
            doc.addNumber(Indexs.PREFIX_FIELD_DATA_NUMBER, name, jsonPrimitive.getAsNumber());
        }
    }

    private static void updateArray(Doc doc, JsonArray jsonArray, String name, Integer dataStringThreshold) {
        List<JsonPrimitive> list = new ArrayList<>();
        jsonArray.forEach(o -> {
            if (o.isJsonObject()) {
                update(doc, o, name, dataStringThreshold);
            } else if (o.isJsonPrimitive()) {
                list.add(o.getAsJsonPrimitive());
            }
        });
        if (BooleanUtils.isTrue(list.stream().map(JsonPrimitive::isString).reduce(true, (a, b) -> a && b))) {
            updateArrayString(doc, name, list);
        } else if (BooleanUtils.isTrue(list.stream().map(JsonPrimitive::isNumber).reduce(true, (a, b) -> a && b))) {
            doc.addNumbers(Indexs.PREFIX_FIELD_DATA_NUMBERS, name,
                    list.stream().map(JsonPrimitive::getAsNumber).collect(Collectors.toList()));
        } else if (BooleanUtils.isTrue(list.stream().map(JsonPrimitive::isBoolean).reduce(true, (a, b) -> a && b))) {
            doc.addBooleans(Indexs.PREFIX_FIELD_DATA_BOOLEANS, name,
                    list.stream().map(JsonPrimitive::getAsBoolean).collect(Collectors.toList()));
        }
    }

    private static void updateArrayString(Doc wrap, String name, List<JsonPrimitive> list) {
        List<String> values = list.stream().map(JsonPrimitive::getAsString).collect(Collectors.toList());
        if (BooleanUtils
                .isTrue(values.stream().map(DateTools::isDateTimeOrDateOrTime).reduce(true, (a, b) -> a && b))) {
            wrap.addDates(Indexs.PREFIX_FIELD_DATA_DATES, name, values.stream().map(s -> {
                try {
                    return DateTools.parse(s);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                return null;
            }).collect(Collectors.toList()));
        } else {
            wrap.addStrings(Indexs.PREFIX_FIELD_DATA_STRINGS, name, values);
        }
    }

    private static String attachment(Business business, String job) throws Exception {
        List<String> list = new ArrayList<>();
        Tika tika = new Tika();
        for (Attachment o : business.entityManagerContainer().listEqual(Attachment.class, Attachment.job_FIELDNAME,
                job)) {
            list.add(o.getName());
            if (StringUtils.isNotEmpty(o.getText())) {
                list.add(o.getText());
            }
            if ((null != o.getLength()) && (o.getLength() > 0)
                    && (o.getLength() < Config.query().index().getAttachmentMaxSize() * 1024 * 1024)) {
                list.add(storageObjectToText(tika, o));
            } else {
                LOGGER.warn("忽略文件长度为0或者过大的附件:{}, size:{}, id:{}.", o.getName(), o.getLength(), o.getId());
            }
        }
        return StringUtils.join(list, ",");
    }

    private static String attachment(Business business, com.x.cms.core.entity.Document document) throws Exception {
        List<String> list = new ArrayList<>();
        Tika tika = new Tika();
        for (FileInfo o : business.entityManagerContainer().listEqual(FileInfo.class, FileInfo.documentId_FIELDNAME,
                document.getId())) {
            list.add(o.getName());
            if (StringUtils.isNotEmpty(o.getText())) {
                list.add(o.getText());
            }
            if ((null != o.getLength()) && (o.getLength() > 0)
                    && (o.getLength() < Config.query().index().getAttachmentMaxSize() * 1024 * 1024)) {
                list.add(storageObjectToText(tika, o));
            } else {
                LOGGER.warn("忽略文件长度为0或者过大的附件:{}, size:{}, id:{}.", o.getName(), o.getLength(), o.getId());
            }
        }
        return StringUtils.join(list, ",");
    }

    private static String storageObjectToText(Tika tika, StorageObject storageObject) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(storageObject.getName());
        try {
            StorageMapping mapping = ThisApplication.context().storageMappings().get(storageObject.getClass(),
                    storageObject.getStorage());
            if (null != mapping) {
                try (InputStream input = new ByteArrayInputStream(storageObject.readContent(mapping))) {
                    builder.append(",").append(tika.parseToString(input));
                }
            } else {
                LOGGER.warn("storageMapping is null can not extract storageObject text, storageObject:{}, name:{}.",
                        storageObject.getId(), storageObject.getName());
            }
        } catch (Throwable th) {
            // 需要Throwable,tika可能抛出Error
            LOGGER.warn("error extract attachment text, storageObject:{}, name:{}, message:{}.", storageObject.getId(),
                    storageObject.getName(), th.getMessage());
        }
        return builder.toString();
    }

}
