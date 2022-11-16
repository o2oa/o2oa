package com.x.query.service.processing.index;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.query.core.express.index.Indexs;

public class Doc extends GsonPropertyObject {

    private static final long serialVersionUID = 7520881746955503078L;

    private String id;

    private String title;

    private String summary;

    private String body;

    private String attachment;

    private String category;

    private String type;

    private String key;

    private Date updateTime;

    private Date createTime;

    private String createTimeMonth;

    private String updateTimeMonth;

    private String creatorPerson;

    private String creatorUnit;

    private Boolean completed;

    private List<String> readers;

    private Map<String, String> stringRepo = new HashMap<>();
    private Map<String, Date> dateRepo = new HashMap<>();
    private Map<String, Boolean> booleanRepo = new HashMap<>();
    private Map<String, Number> numberRepo = new HashMap<>();

    private Map<String, List<String>> stringListRepo = new HashMap<>();
    private Map<String, List<Date>> dateListRepo = new HashMap<>();
    private Map<String, List<Boolean>> booleanListRepo = new HashMap<>();
    private Map<String, List<Number>> numberListRepo = new HashMap<>();

    private Map<String, String> dataStringRepo = new HashMap<>();
    private Map<String, Date> dataDateRepo = new HashMap<>();
    private Map<String, Boolean> dataBooleanRepo = new HashMap<>();
    private Map<String, Number> dataNumberRepo = new HashMap<>();

    private Map<String, List<String>> dataStringListRepo = new HashMap<>();
    private Map<String, List<Date>> dataDateListRepo = new HashMap<>();
    private Map<String, List<Boolean>> dataBooleanListRepo = new HashMap<>();
    private Map<String, List<Number>> dataNumberListRepo = new HashMap<>();

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Map<String, String> getStringRepo() {
        return stringRepo;
    }

    public Map<String, Date> getDateRepo() {
        return dateRepo;
    }

    public Map<String, Boolean> getBooleanRepo() {
        return booleanRepo;
    }

    public Map<String, Number> getNumberRepo() {
        return numberRepo;
    }

    public Map<String, List<String>> getStringListRepo() {
        return stringListRepo;
    }

    public Map<String, List<Date>> getDateListRepo() {
        return dateListRepo;
    }

    public Map<String, List<Boolean>> getBooleanListRepo() {
        return booleanListRepo;
    }

    public Map<String, List<Number>> getNumberListRepo() {
        return numberListRepo;
    }

    public Map<String, String> getDataStringRepo() {
        return dataStringRepo;
    }

    public Map<String, Date> getDataDateRepo() {
        return dataDateRepo;
    }

    public Map<String, Boolean> getDataBooleanRepo() {
        return dataBooleanRepo;
    }

    public Map<String, Number> getDataNumberRepo() {
        return dataNumberRepo;
    }

    public Map<String, List<String>> getDataStringListRepo() {
        return dataStringListRepo;
    }

    public Map<String, List<Date>> getDataDateListRepo() {
        return dataDateListRepo;
    }

    public Map<String, List<Boolean>> getDataBooleanListRepo() {
        return dataBooleanListRepo;
    }

    public Map<String, List<Number>> getDataNumberListRepo() {
        return dataNumberListRepo;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getReaders() {
        return readers;
    }

    public void setReaders(List<String> readers) {
        this.readers = readers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreatorPerson() {
        return creatorPerson;
    }

    public void setCreatorPerson(String creatorPerson) {
        this.creatorPerson = creatorPerson;
    }

    public String getCreatorUnit() {
        return creatorUnit;
    }

    public void setCreatorUnit(String creatorUnit) {
        this.creatorUnit = creatorUnit;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCreateTimeMonth() {
        return createTimeMonth;
    }

    public void setCreateTimeMonth(String createTimeMonth) {
        this.createTimeMonth = createTimeMonth;
    }

    public String getUpdateTimeMonth() {
        return updateTimeMonth;
    }

    public void setUpdateTimeMonth(String updateTimeMonth) {
        this.updateTimeMonth = updateTimeMonth;
    }

    public void addString(String name, String value) {
        stringRepo.put(name, value);
    }

    public void addDate(String name, Date value) {
        dateRepo.put(name, value);
    }

    public void addBoolean(String name, Boolean value) {
        booleanRepo.put(name, value);
    }

    public void addNumber(String name, Number value) {
        numberRepo.put(name, value);
    }

    public void addStringList(String name, List<String> values) {
        stringListRepo.put(name, values);
    }

    public void addDateList(String name, List<Date> values) {
        dateListRepo.put(name, values);
    }

    public void addBooleanList(String name, List<Boolean> values) {
        booleanListRepo.put(name, values);
    }

    public void addNumberList(String name, List<Number> values) {
        numberListRepo.put(name, values);
    }

    public void dataAddString(String name, String value) {
        dataStringRepo.put(name, value);
    }

    public void dataAddDate(String name, Date value) {
        dataDateRepo.put(name, value);
    }

    public void dataAddBoolean(String name, Boolean value) {
        dataBooleanRepo.put(name, value);
    }

    public void dataAddNumber(String name, Number value) {
        dataNumberRepo.put(name, value);
    }

    public void dataAddStringList(String name, List<String> values) {
        dataStringListRepo.put(name, values);
    }

    public void dataAddDateList(String name, List<Date> values) {
        dataDateListRepo.put(name, values);
    }

    public void dataAddBooleanList(String name, List<Boolean> values) {
        dataBooleanListRepo.put(name, values);
    }

    public void dataAddNumberList(String name, List<Number> values) {
        dataNumberListRepo.put(name, values);
    }

    public org.apache.lucene.document.Document toDocument(boolean convertData) {
        org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
        document.add(new StringField(Indexs.FIELD_ID, this.getId(), Field.Store.YES));
        document.add(new TextField(Indexs.FIELD_TITLE, Objects.toString(this.getTitle(), ""), Field.Store.YES));
        document.add(new TextField(Indexs.FIELD_SUMMARY, Objects.toString(this.getSummary(), ""), Field.Store.YES));
        document.add(new TextField(Indexs.FIELD_BODY, Objects.toString(this.getBody(), ""), Field.Store.YES));
        document.add(
                new TextField(Indexs.FIELD_ATTACHMENT, Objects.toString(this.getAttachment(), ""), Field.Store.YES));
        this.getReaders().stream()
                .forEach(o -> document.add(new StringField(Indexs.FIELD_READERS, o, Field.Store.YES)));
        addString(document, Indexs.FIELD_CATEGORY, this.getCategory());
        addString(document, Indexs.FIELD_CREATETIMEMONTH, this.getCreateTimeMonth());
        addString(document, Indexs.FIELD_UPDATETIMEMONTH, this.getUpdateTimeMonth());
        addString(document, Indexs.FIELD_CREATORPERSON, Objects.toString(this.getCreatorPerson(), ""));
        addString(document, Indexs.FIELD_CREATORUNIT, Objects.toString(this.getCreatorUnit(), ""));
        addDate(document, Indexs.FIELD_INDEXTIME, new Date());
        addDate(document, Indexs.FIELD_CREATETIME, this.getCreateTime());
        addDate(document, Indexs.FIELD_UPDATETIME, this.getUpdateTime());
        addBoolean(document, Indexs.FIELD_COMPLETED, this.getCompleted());
        if (convertData) {
            this.getStringRepo().entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addString(document, o.getKey(), o.getValue()));
            this.getDateRepo().entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addDate(document, o.getKey(), o.getValue()));
            this.getNumberRepo().entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addNumber(document, o.getKey(), o.getValue()));
            this.getBooleanRepo().entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addBoolean(document, o.getKey(), o.getValue()));
            this.getDataStringRepo().entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addString(document, Indexs.PREFIX_FIELD_DATA_STRING + o.getKey(), o.getValue()));
            this.getDataDateRepo().entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addDate(document, Indexs.PREFIX_FIELD_DATA_DATE + o.getKey(), o.getValue()));
            this.getDataNumberRepo().entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addNumber(document, Indexs.PREFIX_FIELD_DATA_NUMBER + o.getKey(), o.getValue()));
            this.getDataBooleanRepo().entrySet().stream().filter(o -> null != o.getValue()).forEach(
                    o -> addBoolean(document, Indexs.PREFIX_FIELD_DATA_BOOLEAN + o.getKey(), o.getValue()));
        }
        return document;
    }

    private void addString(org.apache.lucene.document.Document document, String field, String value) {
        document.add(new SortedDocValuesField(field, new BytesRef(value)));
        document.add(new StringField(field, value, Field.Store.YES));
    }

    private void addNumber(org.apache.lucene.document.Document document, String field, Number value) {
        long store = org.apache.lucene.util.NumericUtils.doubleToSortableLong(value.doubleValue());
        document.add(new NumericDocValuesField(field, store));
        document.add(new LongPoint(field, store));
        document.add(new StoredField(field, store));
    }

    private void addDate(org.apache.lucene.document.Document document, String field, Date value) {
        long store = value.getTime();
        document.add(new NumericDocValuesField(field, store));
        document.add(new LongPoint(field, store));
        document.add(new StoredField(field, store));
    }

    private static void addBoolean(org.apache.lucene.document.Document document, String field, Boolean value) {
        String store = BooleanUtils.isTrue(value) ? Indexs.BOOLEAN_TRUE_STRING_VALUE
                : Indexs.BOOLEAN_FALSE_STRING_VALUE;
        document.add(new SortedDocValuesField(field, new BytesRef(store)));
        document.add(new StringField(field, store, Field.Store.YES));
    }

}