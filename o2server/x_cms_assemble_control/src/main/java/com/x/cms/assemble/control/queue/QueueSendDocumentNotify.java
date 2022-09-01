package com.x.cms.assemble.control.queue;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.MessageFactory;
import com.x.cms.assemble.control.service.ReviewService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.query.DocumentNotify;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Document正式发布后，向通知对象或者所有的阅读者推送消息通知
 * @author sword
 */
public class QueueSendDocumentNotify extends AbstractQueue<DocumentNotify> {

	private static  Logger logger = LoggerFactory.getLogger( QueueSendDocumentNotify.class );
	private UserManagerService userManagerService = new UserManagerService();

	@Override
	public void execute(DocumentNotify documentNotify) throws Exception {
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>start QueueSendDocumentNotify:{}", documentNotify);
		if( StringUtils.isEmpty(documentNotify.getDocumentId()) ) {
			return;
		}
		Document document = null;
		AppInfo appInfo = null;
		CategoryInfo category = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			document = emc.find(documentNotify.getDocumentId(), Document.class);
			if(document!=null){
				appInfo = emc.find(document.getAppId(), AppInfo.class);
				category = emc.find(document.getCategoryId(), CategoryInfo.class);
				if(appInfo==null || category==null){
					return;
				}
			}else{
				return;
			}
		}
		if(BooleanUtils.isFalse(category.getSendNotify())){
			return;
		}
		final List<String> persons = new ArrayList<>();
		if(ListTools.isNotEmpty(Config.messages().getConsumers(MessageConnector.TYPE_CMS_PUBLISH))) {
			if(BooleanUtils.isTrue(documentNotify.getNotifyByDocumentReadPerson())){
				if(!CategoryInfo.DOCUMENT_TYPE_DATA.equals(document.getDocumentType())) {
					ReviewService reviewService = new ReviewService();
					persons.addAll(reviewService.listPermissionPersons(appInfo, category, document));
					if (persons.contains("*")) {
						String topUnitName = document.getCreatorTopUnitName();
						if (EffectivePerson.CIPHER.equalsIgnoreCase(topUnitName)
								|| Token.defaultInitialManager.equalsIgnoreCase(topUnitName)) {
							//取发起人所有顶层组织
							if (!EffectivePerson.CIPHER.equalsIgnoreCase(document.getCreatorIdentity()) &&
									!Token.defaultInitialManager.equalsIgnoreCase(document.getCreatorIdentity())) {
								topUnitName = userManagerService.getTopUnitNameByIdentity(document.getCreatorIdentity());
							} else if (!EffectivePerson.CIPHER.equalsIgnoreCase(document.getCreatorPerson()) &&
									!Token.defaultInitialManager.equalsIgnoreCase(document.getCreatorPerson())) {
								topUnitName = userManagerService.getTopUnitNameWithPerson(document.getCreatorPerson());
							}
						}
						if (StringUtils.isNotEmpty(topUnitName)) {
							//取顶层组织的所有人
							persons.clear();
							persons.addAll(listPersonWithUnit(topUnitName));
						}
					}
				}
			}else if (ListTools.isNotEmpty(documentNotify.getNotifyPersonList())) {
				documentNotify.getNotifyPersonList().stream().forEach(name -> {
					persons.addAll(userManagerService.listPersonWithName(name));
				});
			}
			if (ListTools.isNotEmpty(persons)) {
				List<String> personList = persons.stream().distinct().collect(Collectors.toList());
				persons.clear();
				logger.info("{}-文档消息发送人数：{}", document.getTitle(),personList.size());
				MessageWo wo = MessageWo.copier.copy(document);
				for (String person : personList) {
					if (!StringUtils.equals("*", person)) {
						MessageFactory.cms_publish(person, wo);
					}
				}
				personList.clear();
				logger.debug(documentNotify.getDocumentId() + " cms send total count:" + persons.size());
			}
		}
		boolean flag = document!=null && StringUtils.isNotBlank(document.getCreatorPerson()) &&
				!BooleanUtils.isFalse(documentNotify.getNotifyCreatePerson());
		if(flag){
			MessageWo wo = MessageWo.copier.copy(document);
			MessageFactory.cms_publish_creator(wo);
		}
		logger.debug(documentNotify.getDocumentId() + " QueueSendDocumentNotify cms send publish notify for new document completed! " );
	}

	/**
	 * 根据组织名称，获取该组织下所有的人员标识
	 * @param unitName
	 * @return
	 */
	private List<String> listPersonWithUnit(String unitName) {
		List<String> persons = null;
		try {
			persons = userManagerService.listPersonWithUnit(unitName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persons;
	}

	public static class MessageWo extends Document{

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<Document, MessageWo> copier = WrapCopierFactory.wo(Document.class, MessageWo.class,
				JpaObject.singularAttributeField(Document.class, true, true), null);

	}
}
