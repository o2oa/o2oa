package com.x.portal.core.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(name = "Widget", description = "门户组件.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Widget.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.Widget.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Widget extends SliceJpaObject {

    private static final long serialVersionUID = -7592453160980697792L;

    private static final String TABLE = PersistenceProperties.Widget.table;

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
    }

    /* flag标志位 */
    /* Entity 默认字段结束 */

    public String getDataOrMobileData() {
        if (StringUtils.isNotEmpty(this.getData())) {
            return this.getData();
        } else if (StringUtils.isNotEmpty(this.getMobileData())) {
            return this.getMobileData();
        }
        return null;
    }

    public String getMobileDataOrData() {
        if (StringUtils.isNotEmpty(this.getMobileData())) {
            return this.getMobileData();
        } else if (StringUtils.isNotEmpty(this.getData())) {
            return this.getData();
        }
        return null;
    }

    public static final String name_FIELDNAME = "name";
    @RestrictFlag
    @FieldDescribe("名称.")
    @Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
    @CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
    /* 检查在同一Portal下不能重名 */
    @CitationNotExist(fields = { "name", "id",
            "alias" }, type = Widget.class, equals = @Equal(property = "portal", field = "portal")))
    private String name;

    public static final String alias_FIELDNAME = "alias";
    @RestrictFlag
    @FieldDescribe("别名.")
    @Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + alias_FIELDNAME)
    @CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
    /* 检查在同一应用下不能重名 */
    @CitationNotExist(fields = { "name", "id",
            "alias" }, type = Widget.class, equals = @Equal(property = "portal", field = "portal")))
    private String alias;

    public static final String category_FIELDNAME = "category";
    @FieldDescribe("分类")
    @Column(length = length_255B, name = ColumnNamePrefix + category_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + category_FIELDNAME)
    @CheckPersist(allowEmpty = true, simplyString = false)
    private String category;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("描述.")
    @Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String description;

    public static final String portal_FIELDNAME = "portal";
    @FieldDescribe("所属的Portal.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + portal_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + portal_FIELDNAME)
    @CheckPersist(allowEmpty = false, citationExists = @CitationExist(type = Portal.class, fields = JpaObject.id_FIELDNAME))
    @IdReference(Portal.class)
    private String portal;

    public static final String data_FIELDNAME = "data";
    @FieldDescribe("文本内容.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String data;

    public static final String mobileData_FIELDNAME = "mobileData";
    @FieldDescribe("移动端文本内容.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_10M, name = ColumnNamePrefix + mobileData_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String mobileData;

    public static final String hasMobile_FIELDNAME = "hasMobile";
    @FieldDescribe("是否有移动端内容.")
    @Column(name = ColumnNamePrefix + hasMobile_FIELDNAME)
    private Boolean hasMobile;

    // public static String[] FLA GS = new String[] { JpaObject.id_FIELDNAME };

    // public static String[] RESTRICTFLA GS = new String[] { name_FIELDNAME,
    // alias_FIELDNAME };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMobileData() {
        return mobileData;
    }

    public void setMobileData(String mobileData) {
        this.mobileData = mobileData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getHasMobile() {
        return hasMobile;
    }

    public void setHasMobile(Boolean hasMobile) {
        this.hasMobile = hasMobile;
    }

    public String getCategory() {
        return Objects.toString(this.category, "");
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
