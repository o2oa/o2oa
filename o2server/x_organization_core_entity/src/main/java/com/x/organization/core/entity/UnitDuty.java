package com.x.organization.core.entity;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UnitDuty", description = "组织组织职务.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.UnitDuty.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.UnitDuty.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class UnitDuty extends SliceJpaObject {

	private static final long serialVersionUID = -7350923715735957417L;

	private static final String TABLE = PersistenceProperties.UnitDuty.table;

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
		this.pinyin = PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE);
		this.pinyinInitial = PinyinHelper.getShortPinyin(name);
		this.unique = StringUtils.isBlank(this.unique) ? this.id : this.unique;
		this.distinguishedName = this.name + PersistenceProperties.distinguishNameSplit + this.unique
				+ PersistenceProperties.distinguishNameSplit + PersistenceProperties.UnitDuty.distinguishNameCharacter;
		/** 生成默认排序号 */
		if (null == this.orderNumber) {
			this.orderNumber = DateTools.timeOrderNumber();
		}
	}

	/* 更新运行方法 */

	/** 默认内容结束 */

	public static final String pinyin_FIELDNAME = "pinyin";
	@FieldDescribe("name拼音,自动生成")
	@Index(name = TABLE + "_pinyin")
	@Column(length = length_255B, name = ColumnNamePrefix + pinyin_FIELDNAME)
	private String pinyin;

	public static final String pinyinInitial_FIELDNAME = "pinyinInitial";
	@FieldDescribe("name拼音首字母,自动生成")
	@Column(length = length_255B, name = ColumnNamePrefix + pinyinInitial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyinInitial_FIELDNAME)
	private String pinyinInitial;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称,同一组织下不可重名.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationNotExists = @CitationNotExist(fields = name_FIELDNAME, type = UnitDuty.class, equals = @Equal(property = "unit", field = "unit")))
	private String name;

	public static final String unique_FIELDNAME = "unique";
	@Flag
	@FieldDescribe("唯一标识,不可重复,为空则使用自动填充值")
	@Column(length = length_255B, name = ColumnNamePrefix + unique_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unique_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists = @CitationNotExist(fields = unique_FIELDNAME, type = UnitDuty.class))
	private String unique;

	public static final String distinguishedName_FIELDNAME = "distinguishedName";
	@Flag
	@FieldDescribe("识别名,自动填充,@UD结尾.")
	@Column(length = length_255B, name = ColumnNamePrefix + distinguishedName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + distinguishedName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String distinguishedName;

	public static final String unit_FIELDNAME = "unit";
	@FieldDescribe("组织职务所属组织,不可为空.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + unit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unit_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Unit.class) })
	private String unit;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public static final String identityList_FIELDNAME = "identityList";
	@FieldDescribe("组织职务身份成员,多值.")
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + identityList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + identityList_FIELDNAME + JoinIndexNameSuffix))
	@ElementIndex(name = TABLE + IndexNameMiddle + identityList_FIELDNAME + ElementIndexNameSuffix)
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + identityList_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Identity.class) })
	private List<String> identityList;

	/** flag标志位 */

	// public static String[] FLA GS = new String[] { JpaObject.id_FIELDNAME,
	// unique_FIELDNAME,
	// distinguishedName_FIELDNAME };

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public void setPinyinInitial(String pinyinInitial) {
		this.pinyinInitial = pinyinInitial;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPinyinInitial() {
		return pinyinInitial;
	}

}