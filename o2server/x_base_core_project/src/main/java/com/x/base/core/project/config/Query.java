package com.x.base.core.project.config;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.utils.SystemUtils;
import org.quartz.CronExpression;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class Query extends ConfigObject {

	public static Query defaultInstance() {
		return new Query();
	}

	public Query() {
		this.crawlWorkCompleted = new CrawlWorkCompleted();
		this.crawlWork = new CrawlWork();
		this.crawlCms = new CrawlCms();
		this.extractOffice = default_extractOffice;
		this.extractPdf = default_extractPdf;
		this.extractText = default_extractText;
		this.extractImage = default_extractImage;
		this.tessLanguage = default_tessLanguage;
	}

	@FieldDescribe("已完成工作收集器设置.")
	private CrawlWorkCompleted crawlWorkCompleted;

	@FieldDescribe("工作收集器设置.")
	private CrawlWork crawlWork;

	@FieldDescribe("内容管理收集器设置.")
	private CrawlCms crawlCms;

	@FieldDescribe("抽取office中的文本.")
	private Boolean extractOffice = true;

	@FieldDescribe("抽取pdf中的文本.")
	private Boolean extractPdf = true;

	@FieldDescribe("抽取文本中的文本.")
	private Boolean extractText = true;

	@FieldDescribe("抽取图像中的文本.")
	private Boolean extractImage = false;

	@FieldDescribe("tess使用语言.")
	private String tessLanguage = "chi_sim";

//	@FieldDescribe("可使用搜索人员.")
//	private List<String> searchPeople = new ArrayList<>();
//
//	@FieldDescribe("可使用搜索群组.")
//	private List<String> searchGroups = new ArrayList<>();
//
//	@FieldDescribe("可使用角色.")
//	private List<String> searchRoles = new ArrayList<>();

	public static final Boolean default_extractOffice = true;
	public static final Boolean default_extractPdf = true;
	public static final Boolean default_extractText = true;
	public static final Boolean default_extractImage = false;
	public static final String default_tessLanguage = "chi_sim";

	public Boolean getExtractOffice() {
		return BooleanUtils.isTrue(extractOffice);
	}

	public Boolean getExtractPdf() {
		return BooleanUtils.isTrue(extractPdf);
	}

	public Boolean getExtractText() {
		return BooleanUtils.isTrue(extractText);
	}

	public Boolean getExtractImage() {
		return SystemUtils.IS_OS_WINDOWS && BooleanUtils.isTrue(extractImage);
	}

	public String getTessLanguage() {
		return StringUtils.isNotEmpty(this.tessLanguage) ? this.tessLanguage : default_tessLanguage;
	}

	public CrawlCms getCrawlCms() {
		return this.crawlCms == null ? new CrawlCms() : this.crawlCms;
	}

	public CrawlWork getCrawlWork() {
		return this.crawlWork == null ? new CrawlWork() : this.crawlWork;
	}

	public CrawlWorkCompleted getCrawlWorkCompleted() {
		return this.crawlWorkCompleted == null ? new CrawlWorkCompleted() : this.crawlWorkCompleted;
	}

//	public List<String> getSearchPeople() {
//		return searchPeople == null ? new ArrayList<String>() : this.searchPeople;
//	}
//
//	public List<String> getSearchGroups() {
//		return searchGroups == null ? new ArrayList<String>() : this.searchGroups;
//	}
//
//	public List<String> getSearchRoles() {
//		return searchRoles == null ? new ArrayList<String>() : this.searchRoles;
//	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_QUERY);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public static class CrawlWork extends ConfigObject {

		public static CrawlWork defaultInstance() {
			CrawlWork o = new CrawlWork();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "45 45 * * * ?";

		public final static Integer DEFAULT_CONUT = 1000;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("每次处理的数量,默认为1000,同时每次将重爬最旧的25%以提高数据质量.")
		private Integer count = DEFAULT_CONUT;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public Integer getCount() {
			return ((null == this.count) || (count < 0)) ? DEFAULT_CONUT : this.count;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public void setCount(Integer count) {
			this.count = count;
		}
	}

	public static class CrawlWorkCompleted extends ConfigObject {

		public static CrawlWorkCompleted defaultInstance() {
			CrawlWorkCompleted o = new CrawlWorkCompleted();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "50 50 21 * * ?";

		public final static Integer DEFAULT_CONUT = 5000;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("每次处理的数量,默认为5000,同时每次将重爬最旧的10%以提高数据质量.")
		private Integer count = DEFAULT_CONUT;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public Integer getCount() {
			return ((null == this.count) || (count < 0)) ? DEFAULT_CONUT : this.count;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public void setCount(Integer count) {
			this.count = count;
		}
	}

	public static class CrawlCms extends ConfigObject {

		public static CrawlCms defaultInstance() {
			CrawlCms o = new CrawlCms();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "55 55 8/2 * * ?";

		public final static Integer DEFAULT_CONUT = 1000;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("每次处理的数量,默认为1000,同时每次将重爬最旧的10%以提高数据质量.")
		private Integer count = DEFAULT_CONUT;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public Integer getCount() {
			return ((null == this.count) || (count < 0)) ? DEFAULT_CONUT : this.count;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public void setCount(Integer count) {
			this.count = count;
		}
	}

	public void setCrawlWorkCompleted(CrawlWorkCompleted crawlWorkCompleted) {
		this.crawlWorkCompleted = crawlWorkCompleted;
	}

	public void setCrawlWork(CrawlWork crawlWork) {
		this.crawlWork = crawlWork;
	}

	public void setCrawlCms(CrawlCms crawlCms) {
		this.crawlCms = crawlCms;
	}

	public void setExtractOffice(Boolean extractOffice) {
		this.extractOffice = extractOffice;
	}

	public void setExtractPdf(Boolean extractPdf) {
		this.extractPdf = extractPdf;
	}

	public void setExtractText(Boolean extractText) {
		this.extractText = extractText;
	}

	public void setExtractImage(Boolean extractImage) {
		this.extractImage = extractImage;
	}

	public void setTessLanguage(String tessLanguage) {
		this.tessLanguage = tessLanguage;
	}

//	public void setSearchPeople(List<String> searchPeople) {
//		this.searchPeople = searchPeople;
//	}
//
//	public void setSearchGroups(List<String> searchGroups) {
//		this.searchGroups = searchGroups;
//	}
//
//	public void setSearchRoles(List<String> searchRoles) {
//		this.searchRoles = searchRoles;
//	}

}