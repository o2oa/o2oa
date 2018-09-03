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
@Table(name = PersistenceProperties.Log.UnexpectedErrorLog.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Log.UnexpectedErrorLog.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class UnexpectedErrorLog extends SliceJpaObject {

	private static final long serialVersionUID = -2976141463716490951L;

	private static final String TABLE = PersistenceProperties.Log.UnexpectedErrorLog.table;

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

		this.unitName = StringTools.utf8SubString(this.unitName, JpaObject.length_255B);
		this.unit = StringTools.utf8SubString(this.unit, JpaObject.length_id);
		this.address = StringTools.utf8SubString(this.address, JpaObject.length_64B);
		this.version = StringTools.utf8SubString(this.version, JpaObject.length_32B);
		if (null == this.occurTime) {
			this.occurTime = new Date();
		}
		this.exceptionClass = StringTools.utf8SubString(this.exceptionClass, JpaObject.length_255B);
		this.loggerName = StringTools.utf8SubString(this.loggerName, JpaObject.length_255B);
		this.person = StringTools.utf8SubString(this.person, JpaObject.length_255B);
		this.message = StringTools.utf8SubString(this.message, JpaObject.length_2K);
		this.stackTrace = StringTools.utf8SubString(this.stackTrace, JpaObject.length_1M);
		this.requestUrl = StringTools.utf8SubString(this.requestUrl, JpaObject.length_2K);
		this.requestHead = StringTools.utf8SubString(this.requestHead, JpaObject.length_2K);
		this.requestMethod = StringTools.utf8SubString(this.requestMethod, JpaObject.length_8B);
		this.requestRemoteAddr = StringTools.utf8SubString(this.requestRemoteAddr, JpaObject.length_255B);
		this.requestRemoteHost = StringTools.utf8SubString(this.requestRemoteHost, JpaObject.length_255B);
		this.requestBody = StringTools.utf8SubString(this.requestBody, JpaObject.length_10M);
		if (null == requestBodyLength) {
			this.requestBodyLength = 0L;
		}
	}

	/* 更新运行方法 */

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("unitName.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + unitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unitName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String unitName;

	public static final String unit_FIELDNAME = "unit";
	@FieldDescribe("unit.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + unit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String unit;

	public static final String address_FIELDNAME = "address";
	@FieldDescribe("address.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + address_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + address_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String address;

	public static final String version_FIELDNAME = "version";
	@FieldDescribe("服务器版本.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + version_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String version;

	public static final String occurTime_FIELDNAME = "occurTime";
	@FieldDescribe("错误发生时间.")
	@Column(name = ColumnNamePrefix + occurTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + occurTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date occurTime;

	public static final String exceptionClass_FIELDNAME = "exceptionClass";
	@FieldDescribe("错误的类名.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + exceptionClass_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + exceptionClass_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String exceptionClass;

	public static final String loggerName_FIELDNAME = "loggerName";
	@FieldDescribe("loggerName.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + loggerName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + loggerName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String loggerName;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("person.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String person;

	public static final String message_FIELDNAME = "message";
	@FieldDescribe("错误消息.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + message_FIELDNAME)
	private String message;

	public static final String stackTrace_FIELDNAME = "stackTrace";
	@FieldDescribe("错误堆栈.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + stackTrace_FIELDNAME)
	private String stackTrace;

	public static final String requestUrl_FIELDNAME = "requestUrl";
	@FieldDescribe("请求的url.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + requestUrl_FIELDNAME)
	private String requestUrl;

	public static final String requestHead_FIELDNAME = "requestHead";
	@FieldDescribe("请求的Head,用;进行分割.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + requestHead_FIELDNAME)
	private String requestHead;

	public static final String requestMethod_FIELDNAME = "requestMethod";
	@FieldDescribe("请求方式.")
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + requestMethod_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String requestMethod;

	public static final String requestRemoteAddr_FIELDNAME = "requestRemoteAddr";
	@FieldDescribe("远程访问地址.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + requestRemoteAddr_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + requestRemoteAddr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String requestRemoteAddr;

	public static final String requestRemoteHost_FIELDNAME = "requestRemoteHost";
	@FieldDescribe("远程访问主机.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + requestRemoteHost_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + requestRemoteHost_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String requestRemoteHost;

	public static final String requestBody_FIELDNAME = "requestBody";
	@FieldDescribe("请求体.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + requestBody_FIELDNAME)
	private String requestBody;

	public static final String requestBodyLength_FIELDNAME = "requestBodyLength";
	@FieldDescribe("请求长度.")
	@Column(name = ColumnNamePrefix + requestBodyLength_FIELDNAME)
	private Long requestBodyLength;

	public static String[] FLAGS = new String[] { JpaObject.id_FIELDNAME };

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getOccurTime() {
		return occurTime;
	}

	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestRemoteAddr() {
		return requestRemoteAddr;
	}

	public void setRequestRemoteAddr(String requestRemoteAddr) {
		this.requestRemoteAddr = requestRemoteAddr;
	}

	public String getRequestRemoteHost() {
		return requestRemoteHost;
	}

	public void setRequestRemoteHost(String requestRemoteHost) {
		this.requestRemoteHost = requestRemoteHost;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public Long getRequestBodyLength() {
		return requestBodyLength;
	}

	public void setRequestBodyLength(Long requestBodyLength) {
		this.requestBodyLength = requestBodyLength;
	}

	public String getExceptionClass() {
		return exceptionClass;
	}

	public void setExceptionClass(String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	public String getRequestHead() {
		return requestHead;
	}

	public void setRequestHead(String requestHead) {
		this.requestHead = requestHead;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}