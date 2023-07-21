package com.x.query.service.processing.index;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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

    private List<String> readers;

    private Map<String, String> stringRepo = new HashMap<>();
    private Map<String, Date> dateRepo = new HashMap<>();
    private Map<String, Boolean> booleanRepo = new HashMap<>();
    private Map<String, Number> numberRepo = new HashMap<>();

    private Map<String, Collection<String>> stringsRepo = new HashMap<>();
    private Map<String, Collection<Date>> datesRepo = new HashMap<>();
    private Map<String, Collection<Boolean>> booleansRepo = new HashMap<>();
    private Map<String, Collection<Number>> numbersRepo = new HashMap<>();

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

    public Doc addString(String prefix, String field, String value) {
        if (StringUtils.isNotBlank(value)) {
            this.stringRepo.put(prefix + field, value);
        }
        return this;
    }

    public Doc addDate(String prefix, String field, Date value) {
        if (null != value) {
            this.dateRepo.put(prefix + field, value);
        }
        return this;
    }

    public Doc addBoolean(String prefix, String field, Boolean value) {
        if (null != value) {
            this.booleanRepo.put(prefix + field, value);
        }
        return this;
    }

    public Doc addNumber(String prefix, String field, Number value) {
        if (null != value) {
            this.numberRepo.put(prefix + field, value);
        }
        return this;
    }

    public Doc addStrings(String prefix, String field, Collection<String> values) {
        if ((null != values) && (!values.isEmpty())) {
            this.stringsRepo.put(prefix + field, values);
        }
        return this;
    }

    public Doc addDates(String prefix, String field, Collection<Date> values) {
        if ((null != values) && (!values.isEmpty())) {
            this.datesRepo.put(prefix + field, values);
        }
        return this;
    }

    public Doc addBooleans(String prefix, String field, Collection<Boolean> values) {
        if ((null != values) && (!values.isEmpty())) {
            this.booleansRepo.put(prefix + field, values);
        }
        return this;
    }

    public Doc addNumbers(String prefix, String field, Collection<Number> values) {
        if ((null != values) && (!values.isEmpty())) {
            this.numbersRepo.put(prefix + field, values);
        }
        return this;
    }

    public org.apache.lucene.document.Document toDocument(boolean convertData) {
        org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
        document.add(new StringField(Indexs.FIELD_ID, this.getId(), Field.Store.YES));
        addTitle(document, this.getTitle());
        document.add(new TextField(Indexs.FIELD_SUMMARY, Objects.toString(this.getSummary(), ""), Field.Store.YES));
        document.add(new TextField(Indexs.FIELD_BODY, Objects.toString(this.getBody(), ""), Field.Store.YES));
        document.add(
                new TextField(Indexs.FIELD_ATTACHMENT, Objects.toString(this.getAttachment(), ""), Field.Store.YES));
        this.getReaders().stream()
                .forEach(o -> document.add(new StringField(Indexs.FIELD_READERS, o, Field.Store.YES)));
        addString(document, Indexs.FIELD_CATEGORY, this.getCategory());
        addString(document, Indexs.FIELD_TYPE, this.getType());
        addString(document, Indexs.FIELD_CREATETIMEMONTH, this.getCreateTimeMonth());
        addString(document, Indexs.FIELD_UPDATETIMEMONTH, this.getUpdateTimeMonth());
        addString(document, Indexs.FIELD_CREATORPERSON, Objects.toString(this.getCreatorPerson(), ""));
        addString(document, Indexs.FIELD_CREATORUNIT, Objects.toString(this.getCreatorUnit(), ""));
        addDate(document, Indexs.FIELD_INDEXTIME, new Date());
        addDate(document, Indexs.FIELD_CREATETIME, this.getCreateTime());
        addDate(document, Indexs.FIELD_UPDATETIME, this.getUpdateTime());
        if (convertData) {
            this.stringRepo.entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addString(document, o.getKey(), o.getValue()));
            this.dateRepo.entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addDate(document, o.getKey(), o.getValue()));
            this.numberRepo.entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addNumber(document, o.getKey(), o.getValue()));
            this.booleanRepo.entrySet().stream().filter(o -> null != o.getValue()).forEach(
                    o -> addBoolean(document, o.getKey(), o.getValue()));
            this.stringsRepo.entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addStrings(document, o.getKey(), o.getValue()));
            this.datesRepo.entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addDates(document, o.getKey(), o.getValue()));
            this.numbersRepo.entrySet().stream().filter(o -> null != o.getValue())
                    .forEach(o -> addNumbers(document, o.getKey(), o.getValue()));
            this.booleansRepo.entrySet().stream().filter(o -> null != o.getValue()).forEach(
                    o -> addBooleans(document, o.getKey(), o.getValue()));
        }
        return document;
    }

    private void addTitle(org.apache.lucene.document.Document document, String value) {
        String str = Objects.toString(this.getTitle(), "");
        document.add(new TextField(Indexs.FIELD_TITLE, str, Field.Store.YES));
        document.add(new SortedDocValuesField(Indexs.FIELD_TITLE, new BytesRef(str)));
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

    private void addStrings(org.apache.lucene.document.Document document, String field, Collection<String> values) {
        values.stream().filter(o -> !Objects.isNull(o))
                .forEach(o -> document.add(new StringField(field, o, Field.Store.YES)));
    }

    private void addNumbers(org.apache.lucene.document.Document document, String field, Collection<Number> values) {
        values.stream().filter(o -> !Objects.isNull(o)).forEach(o -> {
            long store = org.apache.lucene.util.NumericUtils.doubleToSortableLong(o.doubleValue());
            document.add(new LongPoint(field, store));
            document.add(new StoredField(field, store));
        });
    }

    private void addDates(org.apache.lucene.document.Document document, String field, Collection<Date> values) {
        values.stream().filter(o -> !Objects.isNull(o)).forEach(o -> {
            long store = o.getTime();
            document.add(new LongPoint(field, store));
            document.add(new StoredField(field, store));
        });
    }

    private void addBooleans(org.apache.lucene.document.Document document, String field,
            Collection<Boolean> values) {
        values.stream().filter(o -> !Objects.isNull(o)).forEach(o -> {
            String store = BooleanUtils.isTrue(o) ? Indexs.BOOLEAN_TRUE_STRING_VALUE
                    : Indexs.BOOLEAN_FALSE_STRING_VALUE;
            document.add(new StringField(field, store, Field.Store.YES));
        });
    }

}