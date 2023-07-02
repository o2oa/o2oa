package com.x.message.core.entity;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Mass", description = "消息群发.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Mass.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Mass.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Mass extends SliceJpaObject {

	private static final long serialVersionUID = 7070331767500621787L;

	private static final String TABLE = PersistenceProperties.Mass.table;

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

	/* 更新运行方法 */

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public static final String TYPE_QIYEWEIXIN = "qiyeweixin";
	public static final String TYPE_DINGDING = "dingding";
	public static final String TYPE_ZHENGWUDINGDING = "zhengwuDingding";
	public static final String TYPE_WELINK = "welink";

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建人，可能为空，如果由系统创建。")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String body_FIELDNAME = "body";
	@FieldDescribe("内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = length_10M, name = ColumnNamePrefix + body_FIELDNAME)
	private String body;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("消息类型.")
	@Column(length = length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String type;

	public static final String personList_FIELDNAME = "personList";
	@FieldDescribe("个人对象.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ personList_FIELDNAME, joinIndex = @Index(name = TABLE + personList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + personList_FIELDNAME)
	@ElementIndex(name = TABLE + personList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> personList;

	public static final String identityList_FIELDNAME = "identityList";
	@FieldDescribe("身份对象.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ identityList_FIELDNAME, joinIndex = @Index(name = TABLE + identityList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + identityList_FIELDNAME)
	@ElementIndex(name = TABLE + identityList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> identityList;

	public static final String groupList_FIELDNAME = "groupList";
	@FieldDescribe("群组对象.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ groupList_FIELDNAME, joinIndex = @Index(name = TABLE + groupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + groupList_FIELDNAME)
	@ElementIndex(name = TABLE + groupList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> groupList;

	public static final String unitList_FIELDNAME = "unitList";
	@FieldDescribe("组织对象.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ unitList_FIELDNAME, joinIndex = @Index(name = TABLE + unitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + unitList_FIELDNAME)
	@ElementIndex(name = TABLE + unitList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> unitList;

	public static final String sendPersonList_FIELDNAME = "sendPersonList";
	@FieldDescribe("发送人员对象.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + sendPersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ sendPersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + sendPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + sendPersonList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> sendPersonList;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getPersonList() {
		return personList;
	}

	public void setPersonList(List<String> personList) {
		this.personList = personList;
	}

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

	public List<String> getUnitList() {
		return unitList;
	}

	public void setUnitList(List<String> unitList) {
		this.unitList = unitList;
	}

	public List<String> getSendPersonList() {
		return sendPersonList;
	}

	public void setSendPersonList(List<String> sendPersonList) {
		this.sendPersonList = sendPersonList;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

}