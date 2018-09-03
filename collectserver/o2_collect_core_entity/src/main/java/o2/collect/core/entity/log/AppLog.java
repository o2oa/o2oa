package o2.collect.core.entity.log;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.StringTools;

import o2.collect.core.entity.PersistenceProperties;

@Entity
@Table(name = PersistenceProperties.Log.AppLog.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Log.AppLog.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AppLog extends SliceJpaObject {

	private static final long serialVersionUID = -2976141463716490951L;

	private static final String TABLE = PersistenceProperties.Log.AppLog.table;

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

		this.unit = StringTools.utf8SubString(this.unit, JpaObject.length_id);
		this.unitName = StringTools.utf8SubString(this.unitName, JpaObject.length_255B);
		this.centerHost = StringTools.utf8SubString(this.centerHost, JpaObject.length_255B);
		this.centerContext = StringTools.utf8SubString(this.centerContext, JpaObject.length_255B);
		this.deviceToken = StringTools.utf8SubString(this.deviceToken, JpaObject.length_255B);
		this.distinguishedName = StringTools.utf8SubString(this.distinguishedName, JpaObject.length_255B);
		this.name = StringTools.utf8SubString(this.name, JpaObject.length_255B);
		this.mobile = StringTools.utf8SubString(this.mobile, JpaObject.length_32B);
		this.o2Version = StringTools.utf8SubString(this.o2Version, JpaObject.length_32B);
		this.osType = StringTools.utf8SubString(this.osType, JpaObject.length_32B);
		this.osVersion = StringTools.utf8SubString(this.osVersion, JpaObject.length_64B);
		this.osCpu = StringTools.utf8SubString(this.osCpu, JpaObject.length_64B);
		this.osMemory = StringTools.utf8SubString(this.osMemory, JpaObject.length_64B);
		this.osDpi = StringTools.utf8SubString(this.osDpi, JpaObject.length_64B);
		this.androidManufacturer = StringTools.utf8SubString(this.androidManufacturer, JpaObject.length_255B);
		this.manufacturerOsVersion = StringTools.utf8SubString(this.manufacturerOsVersion, JpaObject.length_255B);
		this.logContent = StringTools.utf8SubString(this.logContent, JpaObject.length_10M);

	}

	/* 更新运行方法 */

	public static final String unit_FIELDNAME = "unit";
	@FieldDescribe("组织")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + unit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unit;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("组织名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + unitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitName;

	public static final String centerHost_FIELDNAME = "centerHost";
	@FieldDescribe("中心服务器host")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerHost_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerHost_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerHost;

	public static final String centerPort_FIELDNAME = "centerPort";
	@FieldDescribe("中心服务器port")
	@Column(name = ColumnNamePrefix + centerPort_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerPort_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer centerPort;

	public static final String centerContext_FIELDNAME = "centerContext";
	@FieldDescribe("中心服务器上下文")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerContext_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerContext_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerContext;

	public static final String deviceToken_FIELDNAME = "deviceToken";
	@FieldDescribe("手机设备号")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + deviceToken_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deviceToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deviceToken;

	public static final String distinguishedName_FIELDNAME = "distinguishedName";
	@FieldDescribe("用户信息")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + distinguishedName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + distinguishedName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String distinguishedName;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("用户的姓名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String name;

	public static final String mobile_FIELDNAME = "mobile";
	@FieldDescribe("用户的手机号码")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + mobile_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mobile_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mobile;

	public static final String o2Version_FIELDNAME = "o2Version";
	@FieldDescribe("当前O2应用版本号")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + o2Version_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + o2Version_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String o2Version;

	public static final String osType_FIELDNAME = "osType";
	@FieldDescribe("手机系统")
	@Column(length = length_32B, name = ColumnNamePrefix + osType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + osType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String osType;

	public static final String osVersion_FIELDNAME = "osVersion";
	@FieldDescribe("当前手机系统版本")
	@Column(length = length_64B, name = ColumnNamePrefix + osVersion_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + osVersion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String osVersion;

	public static final String osCpu_FIELDNAME = "osCpu";
	@FieldDescribe("cpu 信息")
	@Column(length = length_64B, name = ColumnNamePrefix + osCpu_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + osCpu_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String osCpu;

	public static final String osMemory_FIELDNAME = "osMemory";
	@FieldDescribe("内存信息")
	@Column(length = length_64B, name = ColumnNamePrefix + osMemory_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + osMemory_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String osMemory;

	public static final String osDpi_FIELDNAME = "osDpi";
	@FieldDescribe("手机分辨率")
	@Column(length = length_64B, name = ColumnNamePrefix + osDpi_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + osDpi_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String osDpi;

	public static final String androidManufacturer_FIELDNAME = "androidManufacturer";
	@FieldDescribe("android手机制造商")
	@Column(length = length_255B, name = ColumnNamePrefix + androidManufacturer_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + androidManufacturer_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String androidManufacturer;

	public static final String manufacturerOsVersion_FIELDNAME = "manufacturerOsVersion";
	@FieldDescribe("android 定制系统版本")
	@Column(length = length_255B, name = ColumnNamePrefix + manufacturerOsVersion_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + manufacturerOsVersion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String manufacturerOsVersion;

	public static final String logDate_FIELDNAME = "logDate";
	@FieldDescribe("日志记录日期.")
	@Column(name = ColumnNamePrefix + logDate_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + logDate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date logDate;

	public static final String logContent_FIELDNAME = "logContent";
	@FieldDescribe("这个是日志信息 内容比较大 有可能是几M的.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + logContent_FIELDNAME)
	private String logContent;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getCenterHost() {
		return centerHost;
	}

	public void setCenterHost(String centerHost) {
		this.centerHost = centerHost;
	}

	public Integer getCenterPort() {
		return centerPort;
	}

	public void setCenterPort(Integer centerPort) {
		this.centerPort = centerPort;
	}

	public String getCenterContext() {
		return centerContext;
	}

	public void setCenterContext(String centerContext) {
		this.centerContext = centerContext;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getO2Version() {
		return o2Version;
	}

	public void setO2Version(String o2Version) {
		this.o2Version = o2Version;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getOsCpu() {
		return osCpu;
	}

	public void setOsCpu(String osCpu) {
		this.osCpu = osCpu;
	}

	public String getOsMemory() {
		return osMemory;
	}

	public void setOsMemory(String osMemory) {
		this.osMemory = osMemory;
	}

	public String getOsDpi() {
		return osDpi;
	}

	public void setOsDpi(String osDpi) {
		this.osDpi = osDpi;
	}

	public String getAndroidManufacturer() {
		return androidManufacturer;
	}

	public void setAndroidManufacturer(String androidManufacturer) {
		this.androidManufacturer = androidManufacturer;
	}

	public String getManufacturerOsVersion() {
		return manufacturerOsVersion;
	}

	public void setManufacturerOsVersion(String manufacturerOsVersion) {
		this.manufacturerOsVersion = manufacturerOsVersion;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}

}