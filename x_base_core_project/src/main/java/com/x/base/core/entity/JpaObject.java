package com.x.base.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.commons.beanutils.PropertyUtils;

import com.x.base.core.gson.GsonPropertyObject;

@MappedSuperclass
public abstract class JpaObject extends GsonPropertyObject implements Serializable {

	private static final long serialVersionUID = 2809501197843500002L;

	public static String createId() {
		return UUID.randomUUID().toString();
	}

	public static List<String> FieldsUnmodifies = new ArrayList<>();

	public static List<String> FieldsInvisible = new ArrayList<>();

	abstract public String getId();

	abstract public void setId(String id);

	abstract public Date getCreateTime();

	abstract public void setCreateTime(Date createTime);

	abstract public Date getUpdateTime();

	abstract public void setUpdateTime(Date updateTime);

	abstract public String getSequence();

	abstract public void setSequence(String sequence);

	/* 暂存String */
	@Column(length = JpaObject.length_255B, name = "xscratchString")
	private String scratchString;

	/* 暂存Boolean */
	@Column(name = "xscratchBoolean")
	private Boolean scratchBoolean;

	/* 暂存Date */
	@Column(name = "xscratchDate")
	private Date scratchDate;

	/* 暂存Integer */
	@Column(name = "xscratchInteger")
	private Date scratchInteger;

	public String getScratchString() {
		return scratchString;
	}

	public void setScratchString(String scratchString) {
		this.scratchString = scratchString;
	}

	public Boolean getScratchBoolean() {
		return scratchBoolean;
	}

	public void setScratchBoolean(Boolean scratchBoolean) {
		this.scratchBoolean = scratchBoolean;
	}

	public Date getScratchDate() {
		return scratchDate;
	}

	public void setScratchDate(Date scratchDate) {
		this.scratchDate = scratchDate;
	}

	public Date getScratchInteger() {
		return scratchInteger;
	}

	public void setScratchInteger(Date scratchInteger) {
		this.scratchInteger = scratchInteger;
	}

	public static final int length_1B = 1;

	public static final int length_2B = 2;

	public static final int length_4B = 4;

	public static final int length_8B = 8;

	public static final int length_16B = 16;

	public static final int length_32B = 32;

	public static final int length_64B = 64;

	public static final int length_96B = 96;

	public static final int length_128B = 128;

	public static final int length_255B = 255;

	// public static final int length_384B = 384;

	// public static final int length_512B = 512;

	// public static final int length_1K = 1024;

	public static final int length_2K = 1024 * 2;

	public static final int length_4K = 1024 * 4;

	public static final int length_8K = 1024 * 8;

	public static final int length_16K = 1024 * 16;

	public static final int length_32K = 1024 * 32;

	public static final int length_64K = 1024 * 64;

	public static final int length_128K = 1024 * 128;

	public static final int length_1M = 1048576;

	public static final int length_10M = 10485760;

	public static final int length_1G = 1073741824;

	public static final int length_id = length_64B;

	public static final String ID = "id";

	public static final String IDCOLUMN = "xid";

	public static final String DISTRIBUTEFACTOR = "distributeFactor";

	public static final String[] ID_DISTRIBUTEFACTOR = new String[] { "id", "distributeFactor" };

	public Object get(String name) throws Exception {
		return PropertyUtils.getProperty(this, name);
	}

	static {
		FieldsUnmodifies.add(DISTRIBUTEFACTOR);
		FieldsUnmodifies.add(ID);
		FieldsUnmodifies.add("updateTime");
		FieldsUnmodifies.add("createTime");
		FieldsUnmodifies.add("sequence");
		FieldsInvisible.add(DISTRIBUTEFACTOR);
		FieldsInvisible.add("sequence");
		FieldsInvisible.add("password");
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name, Class<T> clazz) throws Exception {
		Object o = get(name);
		if (null == o) {
			return null;
		}
		return (T) o;
	}

	public int hashCode() {
		return 31 + ((this.getId() == null) ? 0 : this.getId().hashCode());
	}

	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof JpaObject)) {
			return false;
		} else {
			return this.getId().equalsIgnoreCase(((JpaObject) o).getId());
		}
	}

}