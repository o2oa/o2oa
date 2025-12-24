package com.x.ai.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

/**
 * 会话
 * @author sword
 */
@Schema(name = "Completion", description = "会话.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Completion.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Completion.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Completion extends SliceJpaObject {

	private static final long serialVersionUID = 5424940251448214931L;

	private static final String TABLE = PersistenceProperties.Completion.table;

	public Completion() {
	}

	public Completion(String person, String clueId, String input, String content) {
		this.person = person;
		this.clueId = clueId;
		this.input = input;
		this.content = content;
	}

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

	@Override
	public void onPersist() {
		if(this.generateType == null){
			this.generateType = "chat";
		}
	}

	@PostLoad
	public void postLoad() {
		this.createDateTime = this.getCreateTime();
		this.updateDateTime = this.getUpdateTime();
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("所属用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String clueId_FIELDNAME = "clueId";
	@FieldDescribe("线索ID")
	@Column(length = length_255B, name = ColumnNamePrefix + clueId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String clueId;

	public static final String generateType_FIELDNAME = "generateType";
	@FieldDescribe("生成类型")
	@Column(length = length_255B, name = ColumnNamePrefix + generateType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String generateType;

	public static final String input_FIELDNAME = "input";
	@FieldDescribe("输入内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + input_FIELDNAME)
	private String input;

	public static final String content_FIELDNAME = "content";
	@FieldDescribe("输出内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + content_FIELDNAME)
	private String content;

	public static final String referenceIdList_FIELDNAME = "referenceIdList";
	@FieldDescribe("关联素材ID列表.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + referenceIdList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + referenceIdList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + referenceIdList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + referenceIdList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> referenceIdList;

	@FieldDescribe("创建时间.")
	@Transient
	private Date createDateTime;

	@FieldDescribe("修改时间.")
	@Transient
	private Date updateDateTime;

	public static final String extra_FIELDNAME = "extra";
	@FieldDescribe("扩展信息.")
	@Transient
	private Map<String, Object> extra;

	@FieldDescribe("工具调用信息列表.")
	@Transient
	private List<ToolCall> toolCallList;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getClueId() {
		return clueId;
	}

	public void setClueId(String clueId) {
		this.clueId = clueId;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public List<String> getReferenceIdList() {
		return referenceIdList;
	}

	public void setReferenceIdList(List<String> referenceIdList) {
		this.referenceIdList = referenceIdList;
	}

	public String getGenerateType() {
		return generateType;
	}

	public void setGenerateType(String generateType) {
		this.generateType = generateType;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, Object> extra) {
		this.extra = extra;
	}

	public List<ToolCall> getToolCallList() {
		return toolCallList;
	}

	public void setToolCallList(List<ToolCall> toolCallList) {
		this.toolCallList = toolCallList;
	}
}
