package com.x.jpush.assemble.control.jaxrs.sample;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.core.entity.SampleEntityClassName;

public class ActionGet extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		logger.info("execute action 'ActionGet'......");
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		
		//与数据库交互示例, 根据ID查询单条记录
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			SampleEntityClassName sampleEntityClassName = emc.find( id, SampleEntityClassName.class );			
			if( sampleEntityClassName != null ){
				wrap = Wo.copier.copy( sampleEntityClassName );
				result.setCount(1L);
				result.setData( wrap );
			}else {
				Exception exception = new ExceptionSampleEntityClassNameNotExists( id );
				result.error( exception );
			}
		} catch (Exception e) {
			Exception exception = new ExceptionSampleEntityClassFind( e, "系统在根据ID查询指定示例数据记录时发生异常！ID=" + id );
			result.error( exception );
			logger.error(e);
		}
		
		logger.info("action 'ActionGet' execute completed!");
		return result;
	}

	public static class Wo extends SampleEntityClassName  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<SampleEntityClassName, Wo> copier = WrapCopierFactory.wo( SampleEntityClassName.class, Wo.class, null,Wo.Excludes);
	}

}