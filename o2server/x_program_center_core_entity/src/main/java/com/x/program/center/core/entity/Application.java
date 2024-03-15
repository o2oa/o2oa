package com.x.program.center.core.entity;

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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "Application", description = "服务管理应用.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Application.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Application.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Application extends SliceJpaObject {

	private static final long serialVersionUID = 874852784032487858L;

	private static final String TABLE = PersistenceProperties.Application.TABLE;

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

	public static final String name_FIELDNAME = "name";

	@FieldDescribe("名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String category_FIELDNAME = "category";
	@FieldDescribe("分类")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + category_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + category_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String category;

	public static final String subCategory_FIELDNAME = "subCategory";
	@FieldDescribe("子分类.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + subCategory_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String subCategory;

	public static final String version_FIELDNAME = "version";
	@FieldDescribe("版本")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + version_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String version;

	public static final String price_FIELDNAME = "price";
	@FieldDescribe("价格.")
	@Column(name = ColumnNamePrefix + price_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double price;

	public static final String describe_FIELDNAME = "describe";
	@FieldDescribe("描述.必填")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1K, name = ColumnNamePrefix + describe_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String describe;

	public static final String abort_FIELDNAME = "abort";
	@FieldDescribe("应用简介")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_4K, name = ColumnNamePrefix + abort_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String abort;

	public static final String installSteps_FIELDNAME = "installSteps";
	@FieldDescribe("应用安装步骤")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + installSteps_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String installSteps;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("首页图片 Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + icon_FIELDNAME)
	private String icon;

	public static final String publisher_FIELDNAME = "publisher";
	@FieldDescribe("发布者.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + publisher_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publisher_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String publisher;

	public static final String publishTime_FIELDNAME = "publishTime";
	@FieldDescribe("发布时间")
	@Column(name = ColumnNamePrefix + publishTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publishTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date publishTime;

	public static final String grade_FIELDNAME = "grade";
	@FieldDescribe("评分.")
	@Column(name = ColumnNamePrefix + grade_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + grade_FIELDNAME)
	private Double grade;

	public static final String commentCount_FIELDNAME = "commentCount";
	@FieldDescribe("评论数")
	@Column(name = ColumnNamePrefix + commentCount_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + commentCount_FIELDNAME)
	private Integer commentCount;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public static final String recommend_FIELDNAME = "recommend";
	@FieldDescribe("推荐指数")
	@Column(name = ColumnNamePrefix + recommend_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + recommend_FIELDNAME)
	private Integer recommend;

	public static final String downloadCount_FIELDNAME = "downloadCount";
	@FieldDescribe("下载次数")
	@Column(name = ColumnNamePrefix + downloadCount_FIELDNAME)
	private Integer downloadCount;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(Integer downloadCount) {
		this.downloadCount = downloadCount;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getAbort() {
		return abort;
	}

	public void setAbort(String abort) {
		this.abort = abort;
	}

	public String getInstallSteps() {
		return installSteps;
	}

	public void setInstallSteps(String installSteps) {
		this.installSteps = installSteps;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Integer getRecommend() {
		return recommend;
	}

	public void setRecommend(Integer recommend) {
		this.recommend = recommend;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
}