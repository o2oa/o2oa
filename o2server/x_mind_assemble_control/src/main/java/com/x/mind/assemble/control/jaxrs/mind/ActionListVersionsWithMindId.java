package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindVersionQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindWrapOutConvert;
import com.x.mind.entity.MindVersionInfo;

/**
 * 查询脑图信息（下一步）
 * @author O2LEE
 *
 */
public class ActionListVersionsWithMindId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListVersionsWithMindId.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String mindId ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<String>ids = null;
		List<MindVersionInfo> mindVersionInfos = null;
		Boolean check = true;

		if( check ){
			try {
				ids = mindInfoService.listVersionsWithMindId(mindId);
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindVersionQuery( e, "系统在根据ID查询指定的脑图信息时发生异常。",  mindId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				if( ListTools.isNotEmpty( ids ) ) {
					mindVersionInfos = mindInfoService.listVersionWithIds(ids);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "系统在根据ID查询指定的脑图信息时发生异常。" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty(mindVersionInfos)) {
				try {
					wraps = Wo.copier.copy(mindVersionInfos);
					SortTools.desc( wraps, "updateTime" );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionMindWrapOutConvert( e, "将数据库实体对象转换为输出对象时发生异常！" );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
		}
		result.setData(wraps);
		return result;
	}
	
	public static class Wo extends MindVersionInfo  {		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<MindVersionInfo, Wo> copier = WrapCopierFactory.wo( MindVersionInfo.class, Wo.class, null,Wo.Excludes);
	}
}