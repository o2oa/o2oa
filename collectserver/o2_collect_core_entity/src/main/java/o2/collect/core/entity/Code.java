package o2.collect.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.project.annotation.FieldDescribe;

@Entity
@Table(name = PersistenceProperties.Code.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Code.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Code extends SliceJpaObject {

	private static final long serialVersionUID = -4792895013245264264L;

	private static final String TABLE = PersistenceProperties.Code.table;

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

	public static final String meta_FIELDNAME = "meta";
	@FieldDescribe("用于关联的字段.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + meta_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + meta_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String meta;

	public static final String answer_FIELDNAME = "answer";
	@FieldDescribe("验证码.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + answer_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	@Index(name = TABLE + IndexNameMiddle + answer_FIELDNAME)
	private String answer;

	public static final String mobile_FIELDNAME = "mobile";
	@FieldDescribe("手机号.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + mobile_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	@Index(name = TABLE + IndexNameMiddle + mobile_FIELDNAME)
	private String mobile;

	public static final String expiredTime_FIELDNAME = "expiredTime";
	@FieldDescribe("验证码过期时间.")
	@Column(name = ColumnNamePrefix + expiredTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + expiredTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date expiredTime;

	/* flag标志位 */

	// public static String[] FLAGS = new String[] { JpaObject.id_FIELDNAME };

	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}