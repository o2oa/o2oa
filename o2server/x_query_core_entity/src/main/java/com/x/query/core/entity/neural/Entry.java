package com.x.query.core.entity.neural;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
import com.x.query.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "Entry", description = "数据中心神经网络条目.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Neural.Entry.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Neural.Entry.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Entry extends SliceJpaObject {

	private static final long serialVersionUID = -5610293696763235753L;

	private static final String TABLE = PersistenceProperties.Neural.Entry.table;

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

	public static final String TYPE_VALIDATION = "validation";
	public static final String TYPE_LEARN = "learn";

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("类型,test或者learn")
	@Column(length = length_32B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	private String type;

	public static final String bundle_FIELDNAME = "bundle";
	@FieldDescribe("通用数据标识,cms为doucment.getId(),processPlatform为workCompletedId")
	@Column(length = length_255B, name = ColumnNamePrefix + bundle_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + bundle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String bundle;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("标题")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String model_FIELDNAME = "model";
	@FieldDescribe("模型")
	@Column(length = length_id, name = ColumnNamePrefix + model_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + model_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String model;

	public static final String inValueLabelList_FIELDNAME = "inValueLabelList";
	@FieldDescribe("输入标签")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ inValueLabelList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + inValueLabelList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(name = ColumnNamePrefix + inValueLabelList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + inValueLabelList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<Integer> inValueLabelList;

	public static final String outValueLabelList_FIELDNAME = "outValueLabelList";
	@FieldDescribe("结果标签")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ outValueLabelList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ outValueLabelList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(name = ColumnNamePrefix + outValueLabelList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + outValueLabelList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<Integer> outValueLabelList;

	public static final String inValueCount_FIELDNAME = "inValueCount";
	@FieldDescribe("输入值数量")
	@Column(name = ColumnNamePrefix + inValueCount_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + inValueCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer inValueCount;

	public static final String outValueCount_FIELDNAME = "outValueCount";
	@FieldDescribe("输出值数量")
	@Column(name = ColumnNamePrefix + outValueCount_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + outValueCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer outValueCount;

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Integer> getInValueLabelList() {
		return inValueLabelList;
	}

	public void setInValueLabelList(List<Integer> inValueLabelList) {
		this.inValueLabelList = inValueLabelList;
	}

	public List<Integer> getOutValueLabelList() {
		return outValueLabelList;
	}

	public void setOutValueLabelList(List<Integer> outValueLabelList) {
		this.outValueLabelList = outValueLabelList;
	}

	public Integer getInValueCount() {
		return inValueCount;
	}

	public void setInValueCount(Integer inValueCount) {
		this.inValueCount = inValueCount;
	}

	public Integer getOutValueCount() {
		return outValueCount;
	}

	public void setOutValueCount(Integer outValueCount) {
		this.outValueCount = outValueCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}