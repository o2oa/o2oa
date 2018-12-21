package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindVersionNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindWrapOutConvert;
import com.x.mind.entity.MindVersionInfo;

/**
 * 查询历史版本脑图信息
 * @author O2LEE
 *
 */
public class ActionMindVersionViewWithId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMindVersionViewWithId.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		MindVersionInfo mindVersionInfo = null;
		Boolean check = true;

		if( check ){
			try {
				mindVersionInfo = mindInfoService.getMindVersionInfo(id);
				if( mindVersionInfo == null ) {
					check = false;
					Exception exception = new ExceptionMindVersionNotExists( id );
					result.error(exception);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "系统在根据ID查询指定的脑图信息时发生异常。" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				wo = Wo.copier.copy(mindVersionInfo);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionMindWrapOutConvert( e, "将数据库实体对象转换为输出对象时发生异常！" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( check ){
			String content;
			try {
				content = mindInfoService.getMindVersionContent(id);
				if( StringUtils.isNotEmpty(content)) {
					wo.setContent(content);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "系统在根据ID查询指定的历史版本脑图内容信息时发生异常。" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wo);
		return result;
	}
	
	public static class Wo extends MindVersionInfo  {		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<MindVersionInfo, Wo> copier = WrapCopierFactory.wo( MindVersionInfo.class, Wo.class, null,Wo.Excludes);
		private String content = null;
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}
}