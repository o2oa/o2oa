package com.x.strategydeploy.core.entity;

import java.util.Date;
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

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 重点工作信息
 * 
 * @author WUSHUTAO
 **/

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.KeyWorkInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.KeyWorkInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class KeyworkInfo extends SliceJpaObject {
	private static final long serialVersionUID = -790189711734940666L;
	private static final String TABLE = PersistenceProperties.KeyWorkInfo.table;

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

	/*
	 * =============================以上为 JpaObject
	 * 默认字段============================================
	 */

	@FieldDescribe("重点工作标题")
	@Column(name = "xkeyworktitle", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xkeyworktitle")
	@CheckPersist(allowEmpty = false)
	private String keyworktitle;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("重点工作描述")
	@Column(name = "xkeyworkdescribe", length = JpaObject.length_1M)
	@CheckPersist(allowEmpty = true)
	private String keyworkdescribe;

	@FieldDescribe("重点工作创建者")
	@Column(name = "xkeyworkcreator", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xkeyworkcreator")
	@CheckPersist(allowEmpty = true)
	private String keyworkcreator;

	@FieldDescribe("重点工作所属组织")
	@Column(name = "xkeyworkunit", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xkeyworkunit")
	@CheckPersist(allowEmpty = true)
	private String keyworkunit;

	@FieldDescribe("重点工作所使用范围")
	@Column(name = "xkeyworkuserscope", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xkeyworkuserscope")
	@CheckPersist(allowEmpty = true)
	private String keyworkuserscope;

	@FieldDescribe("重点工作所属年份")
	@Column(name = "xkeyworkyear", length = JpaObject.length_64B)
	@Index(name = TABLE + "_xkeyworkyear")
	@CheckPersist(allowEmpty = true)
	private String keyworkyear;

	@FieldDescribe("重点工作有效期，开始时间，修改为月份")
	@Column(name = "xkeyworkbegindate")
	@Index(name = TABLE + "_xkeyworkbegindate")
	@CheckPersist(allowEmpty = true)
	// private Date keyworkbegindate;
	private Integer keyworkbegindate;

	@FieldDescribe("重点工作有效期，结束时间，修改为月份")
	@Column(name = "xkeyworkenddate")
	@Index(name = TABLE + "_xkeyworkenddate")
	@CheckPersist(allowEmpty = true)
	// private Date keyworkenddate;
	private Integer keyworkenddate;

	@FieldDescribe("是否为战略性质的工作")
	@Column(name = "xisstrategywork", length = JpaObject.length_255B)
	@Index(name = TABLE + "_isstrategywork")
	@CheckPersist(allowEmpty = true)
	private String isstrategywork;

	@FieldDescribe("举措列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + "_measuresList", joinIndex = @Index(name = TABLE + "_measuresList_join"))
	@ElementColumn(length = JpaObject.length_255B, name = "xmeasuresList")
	@ElementIndex(name = TABLE + "_measuresList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> measureslist;

	public static final String attachmentList_FIELDNAME = "attachmentList";
	@FieldDescribe("附件列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join"))
	@ElementColumn(length = JpaObject.length_255B, name = "xattachmentList")
	@ElementIndex(name = TABLE + "_attachmentList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;

	@FieldDescribe("状态")
	@Column(name = "xstatus", length = JpaObject.length_255B)
	@Index(name = TABLE + "_status")
	@CheckPersist(allowEmpty = true)
	private String status;

	@FieldDescribe("序号")
	@Column(name = "xsequencenumber", length = JpaObject.length_255B)
	@Index(name = TABLE + "_sequencenumber")
	@CheckPersist(allowEmpty = true)
	private Integer sequencenumber;

	public String getKeyworktitle() {
		return keyworktitle;
	}

	public void setKeyworktitle(String keyworktitle) {
		this.keyworktitle = keyworktitle;
	}

	public String getKeyworkdescribe() {
		return keyworkdescribe;
	}

	public void setKeyworkdescribe(String keyworkdescribe) {
		this.keyworkdescribe = keyworkdescribe;
	}

	public String getKeyworkcreator() {
		return keyworkcreator;
	}

	public void setKeyworkcreator(String keyworkcreator) {
		this.keyworkcreator = keyworkcreator;
	}

	public String getKeyworkunit() {
		return keyworkunit;
	}

	public void setKeyworkunit(String keyworkunit) {
		this.keyworkunit = keyworkunit;
	}

	public String getKeyworkuserscope() {
		return keyworkuserscope;
	}

	public void setKeyworkuserscope(String keyworkuserscope) {
		this.keyworkuserscope = keyworkuserscope;
	}

	public List<String> getMeasureslist() {
		return measureslist;
	}

	public void setMeasureslist(List<String> measureslist) {
		this.measureslist = measureslist;
	}

	public String getKeyworkyear() {
		return keyworkyear;
	}

	public void setKeyworkyear(String keyworkyear) {
		this.keyworkyear = keyworkyear;
	}

	public Integer getKeyworkbegindate() {
		return keyworkbegindate;
	}

	public void setKeyworkbegindate(Integer keyworkbegindate) {
		this.keyworkbegindate = keyworkbegindate;
	}

	public Integer getKeyworkenddate() {
		return keyworkenddate;
	}

	public void setKeyworkenddate(Integer keyworkenddate) {
		this.keyworkenddate = keyworkenddate;
	}

	public String getIsstrategywork() {
		return isstrategywork;
	}

	public void setIsstrategywork(String isstrategywork) {
		this.isstrategywork = isstrategywork;
	}

	public List<String> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSequencenumber() {
		return sequencenumber;
	}

	public void setSequencenumber(Integer sequencenumber) {
		this.sequencenumber = sequencenumber;
	}

}
