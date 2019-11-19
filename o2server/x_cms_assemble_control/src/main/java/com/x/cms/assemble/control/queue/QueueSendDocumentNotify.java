package com.x.cms.assemble.control.queue;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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

/**
 * Document正式发布后，向所有的阅读者推送消息通知
 */
public class QueueSendDocumentNotify extends AbstractQueue<Document> {
	
	private static  Logger logger = LoggerFactory.getLogger( QueueSendDocumentNotify.class );

	public void execute( Document document ) throws Exception {
		if( document == null ) {
			logger.info("can not send publish notify , document is NULL!" );
			return;
		}
		if( !StringUtils.equalsIgnoreCase( "信息" , document.getDocumentType()) ) {
			logger.info("can not send publish notify , document is not '信息'!" );
			return;
		}
		logger.info("send publish notify for new document:" + document.getTitle() );	
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( document.getAppId(), AppInfo.class );
			CategoryInfo category = emc.find( document.getCategoryId(), CategoryInfo.class );
//			Boolean sendNotify = false;
			if( appInfo != null && category != null ) {
//				//根据栏目和分类配置判断是否需要提醒
//				if(StringUtils.equals( "信息", document.getDocumentType() )) {
//					if( category.getSendNotify() == null ) {
//						if( appInfo.getSendNotify() == null ) {
//							//都为空，默认发送通知
//							sendNotify = true;
//						}else {
//							sendNotify = appInfo.getSendNotify();
//						}
//					}else {
//						sendNotify = category.getSendNotify();
//					}
//				}else {
//					//数据类型，只有分类设置了需要通知，才会有通知，为空和为false都不通知
//					if( category.getSendNotify() ) {
//						sendNotify = true;
//					}
//				}
			//	if( sendNotify ) {
					//计算该文档有多少阅读者
				ReviewService reviewService = new ReviewService();
				List<String> persons = reviewService.listPermissionPersons( appInfo, category, document );
				if( ListTools.isNotEmpty( persons )) {
					//有可能是*， 一般是所有的人员标识列表
					if( persons.contains( "*" )) {
						List<String> allPersons = listPersonWithUnit( document.getCreatorTopUnitName() );
						if( ListTools.isNotEmpty( allPersons )) {
							for( String person : persons ) {
								if( StringUtils.equals( "*" , person ) && allPersons.contains( person )) {
									allPersons.add( person );
								}
							}
						}
						persons = allPersons;
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
					logger.debug("cms send total count:" + persons.size()  );
				}
				logger.info("cms send publish notify for new document completed! " );
				//}
			}
			logger.info("can not send publish notify for document, category or  appinfo not exists! ID： " + document.getId() );
		}
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
