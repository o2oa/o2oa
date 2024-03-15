package com.x.portal.core.entity;

import com.google.gson.JsonElement;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;

/**
 * 门户页面历史版本
 * @author sword
 */
@Schema(name = "PageVersion", description = "门户页面历史版本.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.PageVersion.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.PageVersion.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PageVersion extends SliceJpaObject {

	private static final long serialVersionUID = -3009126987868308142L;
	private static final String TABLE = PersistenceProperties.PageVersion.table;

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

	/* 更新运行方法 */

	public PageVersion() {

	}

	public PageVersion(String page, JsonElement jsonElement, String person) {
		this.page = page;
		this.data = XGsonBuilder.toJson(jsonElement);
		this.person = person;
	}

	public static final String page_FIELDNAME = "page";
	@FieldDescribe("所属页面.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + page_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + page_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String page;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("操作人.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ person_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String person;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}
}
