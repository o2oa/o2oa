package com.x.organization.core.entity;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.*;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.List;

/**
 * @author sy
 * @date 2022/08/02 15:15
 * @description
 */
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.PersonSuperior.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.PersonSuperior.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersonSuperior extends SliceJpaObject {

    private static final long serialVersionUID = -6904458304707436920L;
    private static final String TABLE = PersistenceProperties.PersonSuperior.table;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("数据库主键,自动生成.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    /* 以上为 JpaObject 默认字段 */

    public void onPersist() throws Exception {
        this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
        this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
        this.unique = StringUtils.isBlank(this.unique) ? this.id : this.unique;
        this.distinguishedName = this.name + PersistenceProperties.distinguishNameSplit + this.unique
                + PersistenceProperties.distinguishNameSplit
                + PersistenceProperties.PersonSuperior.distinguishNameCharacter;
        /** 生成默认排序号 */
        if (null == this.orderNumber) {
            this.orderNumber = DateTools.timeOrderNumber();
        }
    }

    /** 更新运行方法 */

    /**
     * Entity 默认字段结束
     */

    public static final String pinyin_FIELDNAME = "pinyin";
    @FieldDescribe("name拼音,自动生成")
    @Index(name = TABLE + IndexNameMiddle + pinyin_FIELDNAME)
    @Column(length = length_255B, name = ColumnNamePrefix + pinyin_FIELDNAME)
    private String pinyin;

    public static final String pinyinInitial_FIELDNAME = "pinyinInitial";
    @FieldDescribe("name拼音首字母,自动生成")
    @Column(length = length_255B, name = ColumnNamePrefix + pinyinInitial_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + pinyinInitial_FIELDNAME)
    private String pinyinInitial;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("描述.")
    @Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String description;

    public static final String name_FIELDNAME = "name";
    @FieldDescribe("名称,同一个组织中不可重名.")
    @Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
    @CheckPersist(allowEmpty = false, simplyString = true, citationNotExists = @CitationNotExist(fields = {
            name_FIELDNAME}, type = PersonSuperior.class, equals = @Equal(property = "person", field = "person")))
    private String name;

    public static final String unique_FIELDNAME = "unique";
    @Flag
    @FieldDescribe("唯一标识,不可重复,为空则使用自动填充值")
    @Column(length = length_255B, name = ColumnNamePrefix + unique_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + unique_FIELDNAME)
    @CheckPersist(allowEmpty = true, simplyString = true, citationNotExists = @CitationNotExist(fields = unique_FIELDNAME, type = PersonSuperior.class))
    private String unique;

    public static final String distinguishedName_FIELDNAME = "distinguishedName";
    @Flag
    @FieldDescribe("识别名,自动填充,@PA结尾.")
    @Column(length = length_255B, name = ColumnNamePrefix + distinguishedName_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + distinguishedName_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String distinguishedName;

    public static final String person_FIELDNAME = "person";
    @FieldDescribe("个人属性所属个人,不可为空.")
    @Column(length = length_id, name = ColumnNamePrefix + person_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
    @CheckPersist(allowEmpty = false, citationExists = {@CitationExist(type = Person.class)})
    private String person;

    public static final String orderNumber_FIELDNAME = "orderNumber";
    @FieldDescribe("排序号,升序排列,为空在最后")
    @Index(name = TABLE + "_orderNumber")
    @Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
    private Integer orderNumber;

    public static final String superiorList_FIELDNAME = "superiorList";
    @FieldDescribe("属性值,多值.")
    @ContainerTable(name = TABLE + ContainerTableNameMiddle + superiorList_FIELDNAME, joinIndex = @Index(name = TABLE
            + IndexNameMiddle + superiorList_FIELDNAME + JoinIndexNameSuffix))
    @ElementIndex(name = TABLE + IndexNameMiddle + superiorList_FIELDNAME + ElementIndexNameSuffix)
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ElementColumn(length = JpaObject.length_255B, name = ColumnNamePrefix + superiorList_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private List<String> superiorList;

    /**
     * flag标志位
     */

    // public static String[] FLA GS = new String[] { JpaObject.id_FIELDNAME,
    // unique_FIELDNAME };
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public List<String> getSuperiorList() {
        return superiorList;
    }

    public void setSuperiorList(List<String> superiorList) {
        this.superiorList = superiorList;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinInitial() {
        return pinyinInitial;
    }

    public void setPinyinInitial(String pinyinInitial) {
        this.pinyinInitial = pinyinInitial;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
