package o2.collect.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.OrderColumn;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

@Entity
@Table(name = PersistenceProperties.Unit.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Unit.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Unit extends SliceJpaObject {

	private static final long serialVersionUID = -4792895013245264264L;

	private static final String TABLE = PersistenceProperties.Unit.table;

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
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
		if (StringUtils.isEmpty(this.centerContext)) {
			this.centerContext = "/x_program_center";
		}
	}

	/* flag标志位 */
	/* Entity 默认字段结束 */

	/* 更新运行方法 */
	public static final String pinyin_FIELDNAME = "pinyin";
	@FieldDescribe("name拼音.")
	@Index(name = TABLE + IndexNameMiddle + pinyin_FIELDNAME)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + pinyin_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyin;

	public static final String pinyinInitial_FIELDNAME = "pinyinInitial";
	@FieldDescribe("name拼音首字母.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + pinyinInitial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyinInitial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyinInitial;

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("名称,不可重名.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name" }, type = Unit.class))
	private String name;

	public static final String password_FIELDNAME = "password";
	@FieldDescribe("password.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + password_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String password;

	public static final String controllerMobileList_FIELDNAME = "controllerMobileList";
	@FieldDescribe("管理手机号.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ controllerMobileList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ controllerMobileList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_32B, name = ColumnNamePrefix + controllerMobileList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerMobileList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true, mobileString = true)
	private List<String> controllerMobileList;

	public static final String centerHost_FIELDNAME = "centerHost";
	@FieldDescribe("中心节点地址.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerHost_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerHost_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerHost;

	public static final String centerContext_FIELDNAME = "centerContext";
	@FieldDescribe("中心节点应用根.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerContext_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerHost_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerContext;

	public static final String centerPort_FIELDNAME = "centerPort";
	@FieldDescribe("中心节点应用端口.")
	@Column(name = ColumnNamePrefix + centerPort_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerPort_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer centerPort;

	public static final String httpProtocol_FIELDNAME = "httpProtocol";
	@FieldDescribe("协议http或者https.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + httpProtocol_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + httpProtocol_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String httpProtocol;

	public String getHttpProtocol() {
		return StringUtils.equals("https", this.httpProtocol) ? "https" : "http";
	}

	// public static String[] FLAGS = new String[] { JpaObject.id_FIELDNAME,
	// name_FIELDNAME };

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPinyinInitial() {
		return pinyinInitial;
	}

	public void setPinyinInitial(String pinyinInitial) {
		this.pinyinInitial = pinyinInitial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCenterHost() {
		return centerHost;
	}

	public void setCenterHost(String centerHost) {
		this.centerHost = centerHost;
	}

	public String getCenterContext() {
		return centerContext;
	}

	public void setCenterContext(String centerContext) {
		this.centerContext = centerContext;
	}

	public Integer getCenterPort() {
		return centerPort;
	}

	public void setCenterPort(Integer centerPort) {
		this.centerPort = centerPort;
	}

	public List<String> getControllerMobileList() {
		return controllerMobileList;
	}

	public void setControllerMobileList(List<String> controllerMobileList) {
		this.controllerMobileList = controllerMobileList;
	}

	public void setHttpProtocol(String httpProtocol) {
		this.httpProtocol = httpProtocol;
	}

}