package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMMsgFile;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by fancyLou on 2020-06-15. Copyright © 2020 O2. All rights reserved.
 */
public class ActionFileDownload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionFileDownload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			IMMsgFile file = emc.find(id, IMMsgFile.class);
			if (null == file) {
				throw new ExceptionFileNotExist(id);
			}

			StorageMapping mapping = ThisApplication.context().storageMappings().get(IMMsgFile.class,
					file.getStorage());
			if (null == mapping) {
				throw new ExceptionStorageNotExist(file.getStorage());
			}
			StreamingOutput streamingOutput = output -> {
				try {
					file.readContent(mapping, output);
					output.flush();
				} catch (Exception e) {
					LOGGER.warn("{}附件下载异常：{}", file.getName(), e.getMessage());
				}
			};
			String fastETag = file.getId()+file.getUpdateTime().getTime();
			Wo wo = new Wo(streamingOutput, this.contentType(false, file.getName()),
					this.contentDisposition(false, file.getName()),
					file.getLength(), fastETag);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		private static final long serialVersionUID = 4287911201461304784L;

		public Wo(StreamingOutput streamingOutput, String contentType, String contentDisposition, Long contentLength, String fastETag) {
			super(streamingOutput, contentType, contentDisposition, contentLength, fastETag);
		}

	}
}
