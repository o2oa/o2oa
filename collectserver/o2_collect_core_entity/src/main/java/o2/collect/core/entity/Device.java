package o2.collect.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

@Entity
@Table(name = PersistenceProperties.Device.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Device.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Device extends SliceJpaObject {

	private static final long serialVersionUID = -4792895013245264264L;

	private static final String TABLE = PersistenceProperties.Device.table;

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

	/* 默认内容结束 */

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("名称,不可重名.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	/* 一个设备可能同时注册在两个公司,名称可能重复 */
	private String name;

	public static final String deviceType_FIELDNAME = "deviceType";
	@FieldDescribe("系统类别.")
	@Enumerated(EnumType.STRING)
	@Column(length = DeviceType.length, name = ColumnNamePrefix + deviceType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deviceType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private DeviceType deviceType;

	public static final String unit_FIELDNAME = "unit";
	@FieldDescribe("所属组织.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + unit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unit_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Unit.class) })
	private String unit;

	public static final String account_FIELDNAME = "account";
	@FieldDescribe("所属account")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + account_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + account_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Account.class) })
	private String account;

	public static final String connectTime_FIELDNAME = "connectTime";
	@FieldDescribe("最后连接时间.")
	@Column(name = ColumnNamePrefix + connectTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + connectTime_FIELDNAME)
	private Date connectTime;

	/* flag标志位 */

	// public static String[] FLAGS = new String[] { JpaObject.id_FIELDNAME };

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Date getConnectTime() {
		return connectTime;
	}

	public void setConnectTime(Date connectTime) {
		this.connectTime = connectTime;
	}

}