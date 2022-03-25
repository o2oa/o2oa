package com.x.mind.assemble.control.jaxrs.folder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindFolderQuery;
import com.x.mind.entity.MindFolderInfo;

/**
 * 查询用户的个人文件夹信息列表
 * @author O2LEE
 *
 */
public class ActionListMyFolder extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListMyFolder.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<Wo> woFolderInfos = null;
		List<MindFolderInfo> folderInfos = null;
		List<String> ids = null;
		Boolean check = true;
		
		if( check ){
			try {
				ids = mindFolderInfoService.listAllIdsWithPerson(effectivePerson.getDistinguishedName());
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindFolderQuery( e, "系统在根据个人名称查询所有的脑图文件夹信息ID列表时发生异常。" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ListTools.isNotEmpty( ids ) ) {
				try {
					folderInfos = mindFolderInfoService.listWithIds(ids);
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionMindFolderQuery(e, "系统在根据脑图文件夹信息ID列表查询文件夹信息列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( ListTools.isNotEmpty( folderInfos ) ){
				woFolderInfos = Wo.copier.copy(folderInfos);
				for( Wo wo : woFolderInfos ) {
					// 如果上级目录ID为空或者为0，说明是一级目录
					if( StringUtils.isEmpty(wo.getParentId()) || "root".equals(wo.getParentId()) ) {
						composeChildren(wo, woFolderInfos);
						wraps.add( wo );
					}
				}
			}
		}
		if( check ){
			if( ListTools.isNotEmpty( wraps ) ){
				SortTools.asc( wraps,  "name");
				result.setData(wraps);
				result.setCount( Long.parseLong( wraps.size() + "" ) );
			}
		}
		return result;
	}

	/**
	 * 递归组织下级目录信息
	 * @param woFolder
	 * @param woFolderInfos
	 */
	private Wo composeChildren(Wo folder, List<Wo> allFolderInfos) {
		if( folder == null ) {
			return folder;
		}
		if( ListTools.isNotEmpty( allFolderInfos ) ) {
			for( Wo _folder : allFolderInfos ) {
				if( folder.getId().equals( _folder.getParentId() ) ) {
					composeChildren( _folder, allFolderInfos );
					folder.addChild( _folder );
				}
			}
		}
		return folder;
	}

	public static class Wo extends MindFolderInfo  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<MindFolderInfo, Wo> copier = WrapCopierFactory.wo( MindFolderInfo.class, Wo.class, null,Wo.Excludes);
		
		@FieldDescribe( "下级文件夹列表" )
		private List<Wo> children = null;

		public List<Wo> getChildren() {
			return children;
		}

		public void setChildren(List<Wo> children) {
			this.children = children;
		}
		
		public void addChild(Wo child) {
			if( child == null ) {
				return;
			}
			if( this.children == null ) {
				this.children = new ArrayList<>();
			}
			this.children.add( child );
			try {
				SortTools.asc( this.children,  "updateTime");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}