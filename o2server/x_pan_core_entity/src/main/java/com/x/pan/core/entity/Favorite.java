package com.x.pan.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.Date;

/**
 * 收藏夹
 * @author sword
 */
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Favorite.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Favorite.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN })})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Favorite extends SliceJpaObject {

	private static final long serialVersionUID = -7048900810010120001L;

	private static final String TABLE = PersistenceProperties.Favorite.table;

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
	public void onPersist() {
	}

	public Favorite() {
	}

	public Favorite(String name, String person, String folder, String zoneId, Integer orderNumber) {
		this.name = name;
		this.folder = folder;
		this.zoneId = zoneId;
		this.person = person;
		if(orderNumber == null) {
			this.orderNumber = 999;
		}
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("创建用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String name;

	public static final String folder_FIELDNAME = "folder";
	@FieldDescribe("关联目录.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + folder_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + folder_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String folder;

	public static final String zoneId_FIELDNAME = "zoneId";
	@FieldDescribe("关联目录共享区ID。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + zoneId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + zoneId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String zoneId;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
