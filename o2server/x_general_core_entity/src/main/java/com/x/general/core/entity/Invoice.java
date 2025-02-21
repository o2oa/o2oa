package com.x.general.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

@Schema(name = "Invoice", description = "电子发票.")
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Invoice.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Invoice.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.general)
public class Invoice extends StorageObject {

	private static final long serialVersionUID = -8883987079043800355L;

	private static final String TABLE = PersistenceProperties.Invoice.table;
	private static final String BASE_DIR = "invoice";
	public static final String EXT_PDF = "pdf";

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
		this.lastUpdateTime = new Date();
		if (this.properties == null) {
			this.properties = new InvoiceProperties();
		}
	}

	public Invoice() {
		this.properties = new InvoiceProperties();
	}

	public Invoice(String storage, String name, String person, String extension){
		Date now = new Date();
		this.setCreateTime(now);
		this.lastUpdateTime = now;
		this.storage = storage;
		this.name = name;
		this.person = person;
		this.extension = extension;
	}

	@Override
	public String path() throws Exception {
		String str = BASE_DIR;
		str += DateTools.compactDate(this.getCreateTime());
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}

	@Override
	public String getStorage() {
		return storage;
	}

	@Override
	public void setStorage(String storage) {
		this.storage = storage;
	}

	@Override
	public Long getLength() {
		return length;
	}

	@Override
	public void setLength(Long length) {
		this.length = length;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getExtension() {
		return extension;
	}

	@Override
	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	@Override
	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("上传用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String person;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("发票文件名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名,必须要有扩展名的文件才允许上传.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String extension;

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("存储器的名称,也就是多个存放节点的名字.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String storage;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	private Boolean deepPath;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("发票抬头.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String code_FIELDNAME = "code";
	@FieldDescribe("发票代码.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + code_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String code;

	public static final String number_FIELDNAME = "number";
	@FieldDescribe("发票号码.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + number_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String number;

	public static final String date_FIELDNAME = "date";
	@FieldDescribe("开票日期，发票上的日期.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + date_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String date;

	public static final String invoiceDate_FIELDNAME = "invoiceDate";
	@FieldDescribe("开票日期，时间格式")
	@Column(name = ColumnNamePrefix + invoiceDate_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date invoiceDate;

	public static final String amount_FIELDNAME = "amount";
	@FieldDescribe("未税金额.")
	@Column(name = ColumnNamePrefix + amount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double amount;

	public static final String taxAmount_FIELDNAME = "taxAmount";
	@FieldDescribe("税费.")
	@Column(name = ColumnNamePrefix + taxAmount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double taxAmount;

	public static final String totalAmountString_FIELDNAME = "totalAmountString";
	@FieldDescribe("总金额(大写).")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + totalAmountString_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String totalAmountString;

	public static final String totalAmount_FIELDNAME = "totalAmount";
	@FieldDescribe("总金额.")
	@Column(name = ColumnNamePrefix + totalAmount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double totalAmount;

	public static final String buyerName_FIELDNAME = "buyerName";
	@FieldDescribe("购买方名称.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + buyerName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String buyerName;

	public static final String buyerCode_FIELDNAME = "buyerCode";
	@FieldDescribe("购买方纳税人识别号.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + buyerCode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String buyerCode;

	public static final String buyerAddress_FIELDNAME = "buyerAddress";
	@FieldDescribe("购买方地址.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + buyerAddress_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String buyerAddress;

	public static final String buyerAccount_FIELDNAME = "buyerAccount";
	@FieldDescribe("购买方开户行及账号.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + buyerAccount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String buyerAccount;

	public static final String sellerName_FIELDNAME = "sellerName";
	@FieldDescribe("销售方名称.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sellerName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sellerName;

	public static final String sellerCode_FIELDNAME = "sellerCode";
	@FieldDescribe("销售方纳税人识别号.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sellerCode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sellerCode;

	public static final String sellerAddress_FIELDNAME = "sellerAddress";
	@FieldDescribe("销售方地址.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sellerAddress_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sellerAddress;

	public static final String sellerAccount_FIELDNAME = "sellerAccount";
	@FieldDescribe("销售方开户行及账号.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sellerAccount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sellerAccount;

	public static final String drawer_FIELDNAME = "drawer";
	@FieldDescribe("开票人.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + drawer_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String drawer;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("发票类型.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String type;

	public static final String detail_FIELDNAME = "detail";
	@FieldDescribe("开票项目.")
	@Column(length = 500, name = ColumnNamePrefix + detail_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String detail;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private InvoiceProperties properties;

	public InvoiceProperties getProperties() {
		if (null == this.properties) {
			this.properties = new InvoiceProperties();
		}
		return this.properties;
	}

	public void setProperties(InvoiceProperties properties) {
		this.properties = properties;
	}

	public void setContent(String content) {
		this.getProperties().setContent(content);
	}

	public void setDetailList(List<InvoiceDetail> detailList) {
		this.getProperties().setDetailList(detailList);
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getTotalAmountString() {
		return totalAmountString;
	}

	public void setTotalAmountString(String totalAmountString) {
		this.totalAmountString = totalAmountString;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public String getBuyerAddress() {
		return buyerAddress;
	}

	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}

	public String getBuyerAccount() {
		return buyerAccount;
	}

	public void setBuyerAccount(String buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getSellerAddress() {
		return sellerAddress;
	}

	public void setSellerAddress(String sellerAddress) {
		this.sellerAddress = sellerAddress;
	}

	public String getSellerAccount() {
		return sellerAccount;
	}

	public void setSellerAccount(String sellerAccount) {
		this.sellerAccount = sellerAccount;
	}

	public String getDrawer() {
		return drawer;
	}

	public void setDrawer(String drawer) {
		this.drawer = drawer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
