package com.x.crm.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.CrmRegion.CrmRegionConfig.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CrmRegion extends SliceJpaObject {

	private static final long serialVersionUID = 6838366882497763424L;

	private static final String TABLE = PersistenceProperties.CrmRegion.CrmRegionConfig.table;

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(name = "xid", length = JpaObject.length_id)

	//private String id = createId();
	private String id = createId();

	@EntityFieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@EntityFieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe("列表序号, 由创建时间以及ID组成.在保存时自动生成.")
	@Column(name = "xsequence", length = AbstractPersistenceProperties.length_sequence)
	@Index(name = TABLE + "_sequence")
	private String sequence;

	/**
	 * 获取记录ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置记录ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置信息创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取信息更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 设置信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 获取信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * 设置信息记录排序号
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() throws Exception {
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		// 序列号信息的组成，与排序有关
		if (null == this.sequence) {
			this.sequence = StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId());
		}
		this.onPersist();
	}

	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() throws Exception {
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() throws Exception {
	}
	/*=============================以上为 JpaObject 默认字段============================================*/

	/*============================以下为具体不同的业务及数据表字段要求====================================*/

	@EntityFieldDescribe("城市自定义ID")
	@Column(name = "xcityid", length = JpaObject.length_128B)
	@Index(name = TABLE + "_cityid")
	@CheckPersist(allowEmpty = false)
	private String cityid;

	@EntityFieldDescribe("城市名称")
	@Column(name = "xcityname", length = JpaObject.length_255B)
	@Index(name = TABLE + "_cityname")
	@CheckPersist(allowEmpty = false)
	private String cityname;

	@EntityFieldDescribe("父节点ID，与城市自定义ID对应")
	@Column(name = "xparentid", length = JpaObject.length_128B)
	@Index(name = TABLE + "_parentid")
	@CheckPersist(allowEmpty = false)
	private String parentid;

	@EntityFieldDescribe("简称")
	@Column(name = "xshortname", length = JpaObject.length_128B)
	@Index(name = TABLE + "_shortname")
	@CheckPersist(allowEmpty = false)
	private String shortname;

	@EntityFieldDescribe("层级")
	@Column(name = "xleveltype", length = JpaObject.length_128B)
	@Index(name = TABLE + "_leveltype")
	@CheckPersist(allowEmpty = false)
	private String leveltype;

	@EntityFieldDescribe("城市编码")
	@Column(name = "xcitycode", length = JpaObject.length_128B)
	@Index(name = TABLE + "_citycode")
	@CheckPersist(allowEmpty = false)
	private String citycode;

	@EntityFieldDescribe("邮政编码")
	@Column(name = "xzipcode", length = JpaObject.length_128B)
	@Index(name = TABLE + "_zipcode")
	@CheckPersist(allowEmpty = false)
	private String zipcode;

	@EntityFieldDescribe("合并名称")
	@Column(name = "xmergername", length = JpaObject.length_255B)
	@Index(name = TABLE + "_mergername")
	@CheckPersist(allowEmpty = false)
	private String mergername;

	@EntityFieldDescribe("经度")
	@Column(name = "xlng", length = JpaObject.length_128B)
	@Index(name = TABLE + "_lng")
	@CheckPersist(allowEmpty = true)
	private String lng;

	@EntityFieldDescribe("纬度")
	@Column(name = "xlat", length = JpaObject.length_128B)
	@Index(name = TABLE + "_lat")
	@CheckPersist(allowEmpty = true)
	private String lat;

	@EntityFieldDescribe("城市拼音")
	@Column(name = "xcitypinyin", length = JpaObject.length_128B)
	@Index(name = TABLE + "_citypinyin")
	@CheckPersist(allowEmpty = false)
	private String citypinyin;

	public String getCityid() {
		return cityid;
	}

	public void setCityid(String cityid) {
		this.cityid = cityid;
	}

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getLeveltype() {
		return leveltype;
	}

	public void setLeveltype(String leveltype) {
		this.leveltype = leveltype;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getMergername() {
		return mergername;
	}

	public void setMergername(String mergername) {
		this.mergername = mergername;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getCitypinyin() {
		return citypinyin;
	}

	public void setCitypinyin(String citypinyin) {
		this.citypinyin = citypinyin;
	}

}
