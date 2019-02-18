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
 * 战略部署信息
 * 
 * @author WUSHUTAO
 **/

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.StrategyDeployInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.StrategyDeployInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StrategyDeploy extends SliceJpaObject {
	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.StrategyDeployInfo.table;

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

	@FieldDescribe("战略标题")
	@Column(name = "xstrategydeploytitle", length = JpaObject.length_255B)
	@Index(name = TABLE + "_strategydeploytitle")
	@CheckPersist(allowEmpty = false)
	private String strategydeploytitle;

	@FieldDescribe("年份")
	@Column(name = "xstrategydeployyear", length = JpaObject.length_64B)
	@Index(name = TABLE + "_strategydeployyear")
	@CheckPersist(allowEmpty = true)
	private String strategydeployyear;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("描述")
	@Column(name = "xstrategydeploydescribe", length = JpaObject.length_1M)
	@CheckPersist(allowEmpty = true)
	private String strategydeploydescribe;

	@FieldDescribe("所属组织")
	@Column(name = "xstrategydeployunit", length = JpaObject.length_255B)
	@Index(name = TABLE + "_strategydeployunit")
	@CheckPersist(allowEmpty = true)
	private String strategydeployunit;

	@FieldDescribe("创建者")
	@Column(name = "xstrategydeploycreator", length = JpaObject.length_255B)
	@Index(name = TABLE + "_strategydeploycreator")
	@CheckPersist(allowEmpty = true)
	private String strategydeploycreator;

	@FieldDescribe("使用者范围（部门列表）")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + "_deptList", joinIndex = @Index(name = TABLE + "_deptList_join"))
	@ElementColumn(length = JpaObject.length_255B, name = "xdeptList")
	@ElementIndex(name = TABLE + "_deptList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> deptlist;

	@FieldDescribe("层级标志")
	@Column(name = "xstrategydeployconfiglevel", length = JpaObject.length_255B)
	@Index(name = TABLE + "_strategydeployconfiglevel")
	@CheckPersist(allowEmpty = true)
	private String strategydeployconfiglevel;

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

	public String getStrategydeploytitle() {
		return strategydeploytitle;
	}

	public void setStrategydeploytitle(String strategydeploytitle) {
		this.strategydeploytitle = strategydeploytitle;
	}

	public String getStrategydeployyear() {
		return strategydeployyear;
	}

	public void setStrategydeployyear(String strategydeployyear) {
		this.strategydeployyear = strategydeployyear;
	}

	public String getStrategydeploydescribe() {
		return strategydeploydescribe;
	}

	public void setStrategydeploydescribe(String strategydeploydescribe) {
		this.strategydeploydescribe = strategydeploydescribe;
	}

	public String getStrategydeployunit() {
		return strategydeployunit;
	}

	public void setStrategydeployunit(String strategydeployunit) {
		this.strategydeployunit = strategydeployunit;
	}

	public String getStrategydeploycreator() {
		return strategydeploycreator;
	}

	public void setStrategydeploycreator(String strategydeploycreator) {
		this.strategydeploycreator = strategydeploycreator;
	}

	public List<String> getDeptlist() {
		return deptlist;
	}

	public void setDeptlist(List<String> deptlist) {
		this.deptlist = deptlist;
	}

	public String getStrategydeployconfiglevel() {
		return strategydeployconfiglevel;
	}

	public void setStrategydeployconfiglevel(String strategydeployconfiglevel) {
		this.strategydeployconfiglevel = strategydeployconfiglevel;
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
