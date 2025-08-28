package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;


class ActionDeleteWithId extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			File file = emc.find(id, File.class);
			if (null == file) {
				throw new ExceptionFileNotExisted(id);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(file.getPerson())) {
				throw new ExceptionFileAccessDenied(effectivePerson.getDistinguishedName(), file.getName(),
						file.getId());
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(File.class,
					file.getStorage());
			if (null == mapping) {
				throw new ExceptionFileNotExisted(file.getStorage());
			}

			file.deleteContent(mapping);

			//启动事务
			emc.beginTransaction( File.class );
			//删除对象
			emc.remove( file, CheckRemoveType.all );
			//提交事务
			emc.commit();
			result.setData( new Wo( id ));

			return result;
		}
	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
