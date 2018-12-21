package com.x.okr.assemble.control.jaxrs.queue;

import java.util.Date;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.entity.OkrWorkDynamics;

public class QueueWorkDynamicRecord extends AbstractQueue<WrapInWorkDynamic> {
	
	private static  Logger logger = LoggerFactory.getLogger( QueueWorkDynamicRecord.class );
	
	DateOperation dateOperation = new DateOperation();
	
	public void execute( WrapInWorkDynamic wrapIn ) throws Exception {
		OkrWorkDynamics okrWorkDynamics = new OkrWorkDynamics();
		wrapIn.copyTo( okrWorkDynamics );
		okrWorkDynamics.setDateTime( new Date() );
		okrWorkDynamics.setDateTimeStr( dateOperation.getNowDateTime() );
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( OkrWorkDynamics.class );
			emc.persist( okrWorkDynamics, CheckPersistType.all );
			emc.commit();
		}catch( Exception e ){
			logger.error( e );
		}
		
	}
}
