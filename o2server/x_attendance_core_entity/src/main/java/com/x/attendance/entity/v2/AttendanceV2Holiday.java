package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Schema(name = "AttendanceV2Holiday", description = "中国节假日数据.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2Holiday.table, indexes = {
        @Index(name = PersistenceProperties.AttendanceV2Holiday.table + JpaObject.IndexNameMiddle
                + AttendanceV2Holiday.year_FIELDNAME + "_IDX", columnList = JpaObject.ColumnNamePrefix
                + AttendanceV2Holiday.year_FIELDNAME),
        @Index(name = PersistenceProperties.AttendanceV2Holiday.table + JpaObject.IndexNameMiddle
                + AttendanceV2Holiday.dateString_FIELDNAME + "_IDX", columnList = JpaObject.ColumnNamePrefix
                + AttendanceV2Holiday.dateString_FIELDNAME)
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Holiday extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2Holiday.table;
    private static final long serialVersionUID = 4601040815595094715L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public void onPersist() throws Exception {
    }

    public static final String year_FIELDNAME = "year";
    @FieldDescribe("年份")
    @Column(name = ColumnNamePrefix + year_FIELDNAME)
    private Integer year;

    public static final String dateString_FIELDNAME = "dateString";
    @FieldDescribe("日期，格式 yyyy-MM-dd")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + dateString_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String dateString;

    public static final String name_FIELDNAME = "name";
    @FieldDescribe("节假日名称")
    @Column(length = JpaObject.length_64B, name = ColumnNamePrefix + name_FIELDNAME)
    private String name;

    public static final String offDay_FIELDNAME = "offDay";
    @FieldDescribe("是否放假日，true 放假，false 调休工作日")
    @Column(name = ColumnNamePrefix + offDay_FIELDNAME)
    private Boolean offDay;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOffDay() {
        return offDay;
    }

    public void setOffDay(Boolean offDay) {
        this.offDay = offDay;
    }
}
