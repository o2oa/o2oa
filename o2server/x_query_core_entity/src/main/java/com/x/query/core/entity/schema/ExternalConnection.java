package com.x.query.core.entity.schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.query.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Statement", description = "数据中心查询语句.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@javax.persistence.Table(name = PersistenceProperties.Schema.ExternalConnection.TABLE, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.Schema.ExternalConnection.TABLE + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ExternalConnection extends SliceJpaObject {

    private static final long serialVersionUID = -5610293696763235753L;

    private static final String TABLE = PersistenceProperties.Schema.ExternalConnection.TABLE;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("数据库主键,自动生成.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    /* 以上为 JpaObject 默认字段 */

    @Override
    public void onPersist() throws Exception {
        // nothing
    }

    public static final String NAME_FIELDNAME = "name";
    @Flag
    @FieldDescribe("名称.")
    @Column(length = length_255B, name = ColumnNamePrefix + NAME_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + NAME_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String name;

    public static final String DESCRIPTION_FIELDNAME = "description";
    @FieldDescribe("描述.")
    @Column(length = length_255B, name = ColumnNamePrefix + DESCRIPTION_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String description;

    public static final String ENABLE_FIELDNAME = "enable";
    @FieldDescribe("是否启用.")
    @CheckPersist(allowEmpty = false)
    @Column(name = ColumnNamePrefix + ENABLE_FIELDNAME)
    private Boolean enable;

    public static final String URL_FIELDNAME = "url";
    @FieldDescribe("描述.")
    @Column(length = length_255B, name = ColumnNamePrefix + URL_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String url;

    public static final String USER_FIELDNAME = "user";
    @FieldDescribe("数据库jdbc连接用户名.")
    @Column(length = length_255B, name = ColumnNamePrefix + USER_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String user;

    public static final String PASS_FIELDNAME = "pass";
    @FieldDescribe("数据库jdbc连接密码.")
    @Column(length = length_255B, name = ColumnNamePrefix + PASS_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String pass;

    public static final String DRIVERCLASSNAME_FIELDNAME = "driverClassName";
    @FieldDescribe("数据库驱动类名.")
    @Column(length = length_255B, name = ColumnNamePrefix + DRIVERCLASSNAME_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String driverClassName;

    public static final String SCHEMA_FIELDNAME = "schema";
    @FieldDescribe("模式.")
    @Column(length = length_255B, name = ColumnNamePrefix + SCHEMA_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String schema;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    

}
