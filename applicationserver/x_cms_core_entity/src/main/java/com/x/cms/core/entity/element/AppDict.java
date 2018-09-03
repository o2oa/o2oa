package com.x.cms.core.entity.element;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.PersistenceProperties;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Element.AppDict.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.AppDict.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AppDict extends SliceJpaObject {

	private static final long serialVersionUID = -6564254194819206271L;
	private static final String TABLE = PersistenceProperties.Element.AppDict.table;

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

	public void onPersist() throws Exception {

	}

	/* 以上为 JpaObject 默认字段 */

	/* 更新运行方法 */

	@FieldDescribe("应用ID.")
	@Column(name = "xappId", length = JpaObject.length_id)
	@Index(name = TABLE + "_appId")
	@CheckPersist(citationExists = {
			/* 检查关联的Application需存在 */
			@CitationExist(type = AppInfo.class) }, allowEmpty = true)
	private String appId;

	@FieldDescribe("文件名称.")
	@Column(name = "xname", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_name")
	@CheckPersist(citationNotExists =
	/* 同一个应用下不能有重名 */
	@CitationNotExist(fields = { "id", "alias", "name" }, type = AppDict.class), allowEmpty = true)
	private String name;

	@FieldDescribe("别名.")
	@Column(name = "xalias", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_alias")
	@CheckPersist(citationNotExists =
	/* 同一个应用下不能有重名 */
	@CitationNotExist(fields = { "id", "alias", "name" }, type = AppDict.class), allowEmpty = true)
	private String alias;

	@FieldDescribe("说明.")
	@Column(name = "xdescription", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_description")
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("信息创建人UID")
	@Column(name = "xcreatorUid", length = JpaObject.length_64B)
	@CheckPersist(allowEmpty = true)
	private String creatorUid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCreatorUid() {
		return creatorUid;
	}

	public void setCreatorUid(String creatorUid) {
		this.creatorUid = creatorUid;
	}

}