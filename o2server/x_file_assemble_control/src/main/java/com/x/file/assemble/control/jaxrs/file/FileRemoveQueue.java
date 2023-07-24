package com.x.file.assemble.control.jaxrs.file;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

public class FileRemoveQueue extends AbstractQueue<Map<String, String>> {

	private static Logger logger = LoggerFactory.getLogger(FileRemoveQueue.class);

	static final String REFERENCE = "reference";
	static final String REFERENCETYPE = "referenceType";

	@Override
	protected void execute(Map<String, String> map) throws Exception {
		String reference = map.get(REFERENCE);
		String referenceTypeString = map.get(REFERENCETYPE);
		ReferenceType referenceType = EnumUtils.getEnum(ReferenceType.class, referenceTypeString);
		if (StringUtils.isEmpty(reference) || (null == referenceType)) {
			logger.warn("接收到无效的删除文件请求, referenceType: {}, reference: {}.", referenceTypeString, reference);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> ids = business.file().listWithReferenceTypeWithReference(referenceType, reference);
				for (File o : emc.list(File.class, ids)) {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(File.class,
							o.getStorage());
					if (null == mapping) {
						throw new ExceptionStorageMappingNotExisted(o.getStorage());
					} else {
						o.deleteContent(mapping);
						emc.beginTransaction(File.class);
						emc.remove(o);
						emc.commit();
					}
				}
			}
		}
	}
}
