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
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Review;
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

    public static final Function<Pair<Business, String>, Optional<Doc>> wrapWork = param -> {
        try {
            Work work = param.first().entityManagerContainer().find(param.second(),
                    Work.class);
            if (null == work) {
                throw new ExceptionEntityNotExist(param.second(), Work.class);
            }
            Doc wrap = new Doc();
            wrap.setReaders(readers(param.first(), work));
            wrap.setCompleted(false);
            wrap.setId(work.getJob());
            wrap.setCategory(Indexs.CATEGORY_PROCESSPLATFORM);
            wrap.setType(Indexs.TYPE_WORKCOMPLETED);
            wrap.setKey(work.getApplication());
            wrap.setTitle(work.getTitle());
            wrap.setCreateTime(work.getCreateTime());
            wrap.setUpdateTime(work.getUpdateTime());
            wrap.setCreateTimeMonth(DateTools.format(work.getCreateTime(), DateTools.format_yyyyMM));
            wrap.setUpdateTimeMonth(DateTools.format(work.getUpdateTime(), DateTools.format_yyyyMM));
            wrap.setCreatorPerson(OrganizationDefinition.name(work.getCreatorPerson()));
            wrap.setCreatorUnit(OrganizationDefinition.name(work.getCreatorUnit()));
            wrap.addString(Indexs.FIELD_CREATORUNITLEVELNAME, work.getCreatorUnitLevelName());
            wrap.addString(Indexs.FIELD_APPLICATION, work.getApplication());
            wrap.addString(Indexs.FIELD_APPLICATIONNAME, work.getApplicationName());
            wrap.addString(Indexs.FIELD_APPLICATIONALIAS, work.getApplicationAlias());
            wrap.addString(Indexs.FIELD_PROCESS, work.getProcess());
            wrap.addString(Indexs.FIELD_PROCESSNAME, work.getProcessName());
            wrap.addString(Indexs.FIELD_PROCESSALIAS, work.getProcessAlias());
            wrap.addString(Indexs.FIELD_JOB, work.getJob());
            wrap.addString(Indexs.FIELD_SERIAL, work.getSerial());
            update(param.first(), work, wrap);
            return Optional.of(wrap);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Optional.empty();
    };

    public static final Function<Pair<Business, String>, Optional<Doc>> wrapWorkCompleted = param -> {
        try {
            WorkCompleted workCompleted = param.first().entityManagerContainer().find(param.second(),
                    WorkCompleted.class);
            if (null == workCompleted) {
                throw new ExceptionEntityNotExist(param.second(), WorkCompleted.class);
            }
            Doc wrap = new Doc();
            wrap.setReaders(readers(param.first(), workCompleted));
            wrap.setCompleted(true);
            wrap.setId(workCompleted.getJob());
            wrap.setCategory(Indexs.CATEGORY_PROCESSPLATFORM);
            wrap.setType(Indexs.TYPE_WORKCOMPLETED);
            wrap.setKey(workCompleted.getApplication());
            wrap.setTitle(workCompleted.getTitle());
            wrap.setCreateTime(workCompleted.getCreateTime());
            wrap.setUpdateTime(workCompleted.getUpdateTime());
            wrap.setCreateTimeMonth(DateTools.format(workCompleted.getCreateTime(), DateTools.format_yyyyMM));
            wrap.setUpdateTimeMonth(DateTools.format(workCompleted.getUpdateTime(), DateTools.format_yyyyMM));
            wrap.setCreatorPerson(OrganizationDefinition.name(workCompleted.getCreatorPerson()));
            wrap.setCreatorUnit(OrganizationDefinition.name(workCompleted.getCreatorUnit()));
            wrap.addString(Indexs.FIELD_CREATORUNITLEVELNAME, workCompleted.getCreatorUnitLevelName());
            wrap.addString(Indexs.FIELD_APPLICATION, workCompleted.getApplication());
            wrap.addString(Indexs.FIELD_APPLICATIONNAME, workCompleted.getApplicationName());
            wrap.addString(Indexs.FIELD_APPLICATIONALIAS, workCompleted.getApplicationAlias());
            wrap.addString(Indexs.FIELD_PROCESS, workCompleted.getProcess());
            wrap.addString(Indexs.FIELD_PROCESSNAME, workCompleted.getProcessName());
            wrap.addString(Indexs.FIELD_PROCESSALIAS, workCompleted.getProcessAlias());
            wrap.addString(Indexs.FIELD_JOB, workCompleted.getJob());
            wrap.addString(Indexs.FIELD_SERIAL, workCompleted.getSerial());
            wrap.addBoolean(Indexs.FIELD_EXPIRED, workCompleted.getExpired());
            wrap.addDate(Indexs.FIELD_EXPIRETIME, workCompleted.getExpireTime());
            update(param.first(), workCompleted, wrap);
            return Optional.of(wrap);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Optional.empty();
    };

    public static final Function<Pair<Business, String>, Optional<Doc>> wrapDocument = param -> {
        try {
            Document document = param.first().entityManagerContainer().find(param.second(), Document.class);
            if (null == document) {
                throw new ExceptionEntityNotExist(param.second(), Document.class);
            }
            Doc wrap = new Doc();
            wrap.setReaders(readers(param.first(), document));
            wrap.setCompleted(true);
            wrap.setId(document.getId());
            wrap.setCategory(Indexs.CATEGORY_CMS);
            wrap.setType(Indexs.TYPE_DOCUMENT);
            wrap.setKey(document.getAppId());
            wrap.setTitle(document.getTitle());
            wrap.setCreateTime(document.getCreateTime());
            wrap.setUpdateTime(document.getUpdateTime());
            wrap.setCreateTimeMonth(DateTools.format(document.getCreateTime(), DateTools.format_yyyyMM));
            wrap.setUpdateTimeMonth(DateTools.format(document.getUpdateTime(), DateTools.format_yyyyMM));
            wrap.setCreatorPerson(OrganizationDefinition.name(document.getCreatorPerson()));
            wrap.setCreatorUnit(OrganizationDefinition.name(document.getCreatorUnitName()));
            wrap.addString(Indexs.FIELD_APPID, document.getAppId());
            wrap.addString(Indexs.FIELD_APPNAME, document.getAppName());
            wrap.addString(Indexs.FIELD_APPALIAS, document.getAppAlias());
            wrap.addString(Indexs.FIELD_CATEGORYID, document.getCategoryId());
            wrap.addString(Indexs.FIELD_CATEGORYNAME, document.getCategoryName());
            wrap.addString(Indexs.FIELD_CATEGORYALIAS, document.getCategoryAlias());
            wrap.addString(Indexs.FIELD_DESCRIPTION, document.getDescription());
            wrap.addDate(Indexs.FIELD_PUBLISHTIME, document.getPublishTime());
            wrap.addDate(Indexs.FIELD_MODIFYTIME, document.getModifyTime());
            update(param.first(), document, wrap, Config.query().index().getDataStringThreshold());
            return Optional.of(wrap);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Optional.empty();
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
        try {
            List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class,
                    DataItem.bundle_FIELDNAME,
                    work.getJob(), DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
            wrap.setBody(DataItemConverter.ItemText.text(items, true, true, true, true, true, ","));
            wrap.setSummary(HanLP.getSummary(wrap.getBody(), Config.query().index().getSummaryLength()));
            if (BooleanUtils.isTrue((Config.query().index().getWorkIndexAttachment()))) {
                wrap.setAttachment(attachment(business, work.getJob()));
            } else {
                wrap.setAttachment("");
            }
            update(wrap, CONVERTER.assemble(items), "", Config.query().index().getDataStringThreshold());
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private static void update(Business business, WorkCompleted workCompleted, Doc wrap) {
        try {
            List<Item> items = null;
            if (BooleanUtils.isTrue(workCompleted.getMerged())) {
                Data data = workCompleted.getProperties().getData();
                items = CONVERTER.disassemble(gson.toJsonTree(data));
            } else {
                items = business.entityManagerContainer().listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME,
                        workCompleted.getJob(), DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
            }
            wrap.setBody(DataItemConverter.ItemText.text(items, true, true, true, true, true, ","));
            wrap.setSummary(HanLP.getSummary(wrap.getBody(), Config.query().index().getSummaryLength()));
            if (BooleanUtils.isTrue((Config.query().index().getWorkCompletedIndexAttachment()))) {
                wrap.setAttachment(attachment(business, workCompleted.getJob()));
            } else {
                wrap.setAttachment("");
            }
            update(wrap, CONVERTER.assemble(items), "", Config.query().index().getDataStringThreshold());
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private static void update(Business business, com.x.cms.core.entity.Document document, Doc wrap,
            Integer dataStringThreshold) {
        try {
            List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class,
                    DataItem.bundle_FIELDNAME, document.getId(), DataItem.itemCategory_FIELDNAME, ItemCategory.cms);
            wrap.setBody(DataItemConverter.ItemText.text(items, true, true, true, true, true, ","));
            wrap.setSummary(HanLP.getSummary(wrap.getBody(), Config.query().index().getSummaryLength()));
            if (BooleanUtils.isTrue((Config.query().index().getWorkCompletedIndexAttachment()))) {
                wrap.setAttachment(attachment(business, document));
            } else {
                wrap.setAttachment("");
            }
            update(wrap, CONVERTER.assemble(items), "", dataStringThreshold);
        } catch (Exception e) {
            LOGGER.error(e);
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
                        doc.addDate(Indexs.PREFIX_FIELD_DATA_DATE + name, DateTools.parse(value));
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                } else {
                    doc.addString(Indexs.PREFIX_FIELD_DATA_STRING + name, value);
                }
            }
        } else if (jsonPrimitive.isBoolean()) {
            doc.addBoolean(Indexs.PREFIX_FIELD_DATA_BOOLEAN + name, jsonPrimitive.getAsBoolean());
        } else if (jsonPrimitive.isNumber()) {
            doc.addNumber(Indexs.PREFIX_FIELD_DATA_NUMBER + name, jsonPrimitive.getAsNumber());
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
            doc.addNumberList(Indexs.PREFIX_FIELD_DATA_NUMBERS + name,
                    list.stream().map(JsonPrimitive::getAsNumber).collect(Collectors.toList()));
        } else if (BooleanUtils.isTrue(list.stream().map(JsonPrimitive::isBoolean).reduce(true, (a, b) -> a && b))) {
            doc.addBooleanList(Indexs.PREFIX_FIELD_DATA_BOOLEANS + name,
                    list.stream().map(JsonPrimitive::getAsBoolean).collect(Collectors.toList()));
        }
    }

    private static void updateArrayString(Doc wrap, String name, List<JsonPrimitive> list) {
        List<String> values = list.stream().map(JsonPrimitive::getAsString).collect(Collectors.toList());
        if (BooleanUtils
                .isTrue(values.stream().map(DateTools::isDateTimeOrDateOrTime).reduce(true, (a, b) -> a && b))) {
            wrap.addDateList(Indexs.PREFIX_FIELD_DATA_DATES + name, values.stream().map(s -> {
                try {
                    return DateTools.parse(s);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                return null;
            }).collect(Collectors.toList()));
        } else {
            wrap.addStringList(Indexs.PREFIX_FIELD_DATA_STRINGS + name, values);
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
            StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
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
