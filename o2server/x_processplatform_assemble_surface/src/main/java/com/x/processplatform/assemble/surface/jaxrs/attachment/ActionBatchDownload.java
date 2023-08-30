package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.tuple.Pair;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Attachment;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionBatchDownload extends BaseBatchDownloadWithWorkOrWorkCompleted {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBatchDownload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String site, String fileName, String flag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			final Business business = new Business(emc);

			List<Pair<String, String>> pairs = getTitleAndJob(effectivePerson, business, workId);
			String title = pairs.size() == 1 ? pairs.get(0).getLeft() : "";
			final Map<String, List<Attachment>> readableMap = new HashMap<>();
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPerson(effectivePerson);
			for (Pair<String, String> pair : pairs) {
				List<Attachment> attachmentList = listAttachment(business, site, pair.getRight());
				List<Attachment> readableAttachmentList = new ArrayList<>();
				for (Attachment attachment : attachmentList) {
					if (this.read(attachment, effectivePerson, identities, units, business)) {
						readableAttachmentList.add(attachment);
					}
				}
				if(readableAttachmentList.size() > 0) {
					String key = pair.getLeft();
					if (readableMap.containsKey(key)) {
						key = key + StringTools.randomNumber4();
					}
					readableMap.put(key, readableAttachmentList);
				}
			}
			fileName = adjustFileName(fileName, title);
			Map<String, byte[]> map = new HashMap<>();
			this.assembleFile(business, map, flag);
			StreamingOutput streamingOutput = output -> {
				try {
					downToZip(readableMap, output, map);
					output.flush();
				} catch (Exception e) {
					LOGGER.warn("batchDown error：{}", e.getMessage());
				}
			};
			LOGGER.info("{} do batchDown to:{}, from work:{}.", effectivePerson.getDistinguishedName(), fileName, workId);
			String fastETag = StringTools.uniqueToken();
			Wo wo = new Wo(streamingOutput, this.contentType(true, fileName),
					this.contentDisposition(true, fileName),
					null, fastETag);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionBatchDownloadWithWorkOrWorkCompleted$Wo")
	public static class Wo extends WoFile {

		public Wo(StreamingOutput streamingOutput, String contentType, String contentDisposition, Long contentLength, String fastETag) {
			super(streamingOutput, contentType, contentDisposition, contentLength, fastETag);
		}

	}

}
