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
import com.x.base.core.project.tools.ListTools;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.core.entity.SampleEntityClassName;

public class ActionListAll extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListAll.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		logger.info("execute action 'ActionListAll'......");
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		
		//与数据库交互示例, 根据ID查询单条记录
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			
			//查询所有的数据，最大返回1000条，并且按数据更新时间倒序
			List<SampleEntityClassName> allEntity = business.sampleEntityClassNameFactory().listAll( 1000 );
			
			if( ListTools.isNotEmpty( allEntity ) ){
				wraps = Wo.copier.copy( allEntity );
				result.setCount(Long.parseLong( wraps.size() + "") );
				result.setData( wraps );
			}			
		} catch (Exception e) {
			Exception exception = new ExceptionSampleEntityClassFind( e, "系统在查询所有示例数据记录时发生异常!" );
			result.error( exception );
			logger.error(e);
		}		
		
		logger.info("action 'ActionListAll' execute completed!");
		return result;
	}

	/**
	 * 
	 * 向外输出的结果对象包装类
	 *
	 */
	public static class Wo extends SampleEntityClassName  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<SampleEntityClassName, Wo> copier = WrapCopierFactory.wo( SampleEntityClassName.class, Wo.class, null,Wo.Excludes);
	}

}