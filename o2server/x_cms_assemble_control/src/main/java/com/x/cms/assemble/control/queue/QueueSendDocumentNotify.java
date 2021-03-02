package com.x.cms.assemble.control.queue;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.MessageFactory;
import com.x.cms.assemble.control.service.ReviewService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Document正式发布后，向所有的阅读者推送消息通知
 */
public class QueueSendDocumentNotify extends AbstractQueue<String> {

	private static  Logger logger = LoggerFactory.getLogger( QueueSendDocumentNotify.class );
	private UserManagerService userManagerService = new UserManagerService();

	public void execute( String documentId ) throws Exception {
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>start QueueSendDocumentNotify:" + documentId );
		if( StringUtils.isEmpty(documentId) ) {
			logger.debug("can not send publish notify , document is NULL!" );
			return;
		}
		List<String> persons = null;
		Document document = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			document = emc.find(documentId, Document.class);
			if(document !=null && StringUtils.equals( "信息" , document.getDocumentType())) {
				logger.debug("send publish notify for new document:" + document.getTitle() );
				AppInfo appInfo = emc.find(document.getAppId(), AppInfo.class);
				CategoryInfo category = emc.find(document.getCategoryId(), CategoryInfo.class);

				if (appInfo != null && category != null) {
					ReviewService reviewService = new ReviewService();
					persons = reviewService.listPermissionPersons(appInfo, category, document);
					if (ListTools.isNotEmpty(persons)) {
						//有可能是*， 一般是所有的人员标识列表
						if (persons.contains("*")) {
							String topUnitName = document.getCreatorTopUnitName();
							logger.debug(">>>>>document.getCreatorTopUnitName()=" + topUnitName);
							if (StringUtils.equalsAnyIgnoreCase("cipher", topUnitName) || StringUtils.equalsAnyIgnoreCase("xadmin", topUnitName)) {
								//取发起人所有顶层组织
								if (!StringUtils.equalsAnyIgnoreCase("cipher", document.getCreatorIdentity()) &&
										!StringUtils.equalsAnyIgnoreCase("xadmin", document.getCreatorIdentity())) {
									topUnitName = userManagerService.getTopUnitNameByIdentity(document.getCreatorIdentity());
								} else if (!StringUtils.equalsAnyIgnoreCase("cipher", document.getCreatorPerson()) &&
										!StringUtils.equalsAnyIgnoreCase("xadmin", document.getCreatorPerson())) {
									topUnitName = userManagerService.getTopUnitNameWithPerson(document.getCreatorPerson());
								}
							}
							if (StringUtils.isNotEmpty(topUnitName)) {
								//取顶层组织的所有人
								persons = listPersonWithUnit(topUnitName);
							} else {
								persons = new ArrayList<>();
							}
						}
					}
				} else {
					logger.debug("can not send publish notify for document, category or  appinfo not exists! ID： " + document.getId());
				}
			}
		}
		if( ListTools.isNotEmpty( persons )) {
			//去一下重复
			HashSet<String> set = new HashSet<String>( persons );
			persons.clear();
			persons.addAll(set);

			MessageWo wo = MessageWo.copier.copy(document);
			for( String person : persons ) {
				if( !StringUtils.equals( "*", person  )) {
					MessageFactory.cms_publish(person, wo);
				}
			}
			logger.debug(documentId +" cms send total count:" + persons.size()  );
		}
		logger.debug(documentId + " QueueSendDocumentNotify cms send publish notify for new document completed! " );
	}

	/**
	 * 根据组织名称，获取该组织下所有的人员标识
	 * @param unitName
	 * @return
	 */
	private List<String> listPersonWithUnit(String unitName) {
		UserManagerService  userManagerService = new UserManagerService();
		List<String> persons = null;
		try {
			persons = userManagerService.listPersonWithUnit(unitName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persons;
	}

	public static class MessageWo{

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Document, MessageWo> copier = WrapCopierFactory.wo(Document.class, MessageWo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("数据库主键,自动生成.")
		private String id;

		@FieldDescribe("文档摘要")
		private String summary;

		@FieldDescribe("文档标题")
		private String title;

		@FieldDescribe("文档类型，跟随分类类型，信息 | 数据")
		private String documentType = "信息";

		@FieldDescribe("栏目ID")
		private String appId;

		@FieldDescribe("栏目名称")
		private String appName;

		@FieldDescribe("分类ID")
		private String categoryId;

		@FieldDescribe("分类名称")
		private String categoryName;

		@FieldDescribe("创建人，可能为空，如果由系统创建。")
		private String creatorPerson;

		@FieldDescribe("文档发布时间")
		private Date publishTime;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public String getCreatorPerson() {
			return creatorPerson;
		}

		public void setCreatorPerson(String creatorPerson) {
			this.creatorPerson = creatorPerson;
		}

		public Date getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(Date publishTime) {
			this.publishTime = publishTime;
		}
	}
}
