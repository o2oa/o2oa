package com.x.ai.assemble.control.queue;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.bean.DocIndex;
import com.x.ai.assemble.control.util.HttpUtil;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.connection.FilePart;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.query.core.entity.Item;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Document变更标题时也需要更新一下热点图片里的数据
 *
 */
public class QueueDocumentIndex extends AbstractQueue<String> {

	private static final Logger logger = LoggerFactory.getLogger(QueueDocumentIndex.class);

	public void execute(String docId) throws Exception {
		AiConfig aiConfig = Business.getConfig();
		if (BooleanUtils.isNotTrue(aiConfig.getO2AiEnable())
				|| StringUtils.isBlank(aiConfig.getO2AiBaseUrl())
				|| StringUtils.isBlank(aiConfig.getO2AiToken())) {
			logger.warn("o2 ai not enable, ignore.");
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = emc.find(docId, Document.class);
			if(document != null){
				List<Item> dataItems = business.cmsItem().listWithDocWithPath(document.getId());
				DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
				JsonElement jsonElement = converter.assemble(dataItems);
				String content = XGsonBuilder.extractString(jsonElement, "htmleditor");
				DocIndex docIndex = new DocIndex();
				docIndex.setTitle(document.getTitle());
				docIndex.setContent(content);
				docIndex.setReferenceId(document.getId());
				docIndex.setCatalogId(document.getAppId());
				docIndex.setCatalogName(document.getAppName());
				docIndex.setReferenceCreateDateTime(document.getCreateTime());
				docIndex.setReferenceCreatorPerson(document.getCreatorPerson());
				docIndex.setReferenceCreatorUnit(document.getCreatorUnitName());
				if(StringUtils.join(aiConfig.getQuestionsIndexAppList()).contains(document.getAppId())){
					docIndex.setQuestionEnable(true);
				}
				List<String> permissionList = new ArrayList<>();
				if(BooleanUtils.isNotTrue(document.getIsAllRead())) {
					Set<String> set = new HashSet<>(document.getReadPersonList());
					set.addAll(document.getReadUnitList());
					set.addAll(document.getReadGroupList());
					set.addAll(document.getAuthorPersonList());
					set.addAll(document.getAuthorUnitList());
					set.addAll(document.getAuthorGroupList());
					set.addAll(document.getManagerList());
					set.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.Manager));
					set.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.CMSManager));
					permissionList.addAll(set);
				}
				docIndex.setPermissionList(permissionList);
				String url = aiConfig.getO2AiBaseUrl() + "/index-gateway-doc/update";
				List<NameValuePair> heads = List.of(new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
				ActionResponse response = ConnectionAction.post(url, heads, docIndex);
				uploadFile(emc, document, aiConfig);
				logger.info("ai documentIndex:{}-{} , resp: {}", docIndex.getReferenceId(), docIndex.getTitle(), XGsonBuilder.toJson(response));
			}
		}catch (Exception e){
			logger.error(e);
		}
	}

	private void uploadFile(EntityManagerContainer emc, Document document, AiConfig aiConfig) throws Exception{
		List<FileInfo> fileInfoList = emc.listEqual(FileInfo.class, FileInfo.documentId_FIELDNAME, document.getId());
		List<FilePart> filePartList = new ArrayList<>();
		fileInfoList.stream().filter(f -> aiConfig.getO2AiFileList().contains(f.getExtension())).forEach(f -> {
			try {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(FileInfo.class, f.getStorage());
				FilePart filePart = new FilePart(f.getFileName(), f.readContent(mapping), Config.mimeTypes(f.getExtension()), "file");
				filePartList.add(filePart);
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}
		});
		if(!filePartList.isEmpty()){
			String url = aiConfig.getO2AiBaseUrl() + "/gateway-doc/upload/reference-id/"+document.getId()+"/mode/replace";
			List<NameValuePair> heads = List.of(new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
			ActionResponse response = HttpUtil.postMultiPartBinary(url, heads, null, filePartList);
			logger.debug("ai document {} file index resp: {}", document.getId(), XGsonBuilder.toJson(response));
		}
	}


}
