package com.x.cms.assemble.search.jaxrs.search;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.search.bean.WrapDocument;
import com.x.cms.assemble.search.es.Criteria;
import com.x.cms.assemble.search.es.ElasticSearchJestClient;
import com.x.cms.assemble.search.service.DocumentSearchService;
import com.x.cms.core.entity.tools.LogUtil;

import io.searchbox.client.JestClient;

public class ActionSearchDocuments extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSearchDocuments.class );
	
	protected ActionResult<List<WrapDocument>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<WrapDocument>> result = new ActionResult<>();
		Wi wi = null;
		String personName = effectivePerson.getDistinguishedName();
		List<WrapDocument> docList = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		Boolean manager = false;
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionSearchProcess( e, "系统在将JSON信息转换为对象时发生异常。");
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ) {
			//说明是需要按输入的人员来查询人员可以看到的信息
			if(StringUtils.isNotEmpty( wi.getPersonName() )) {
				personName = wi.getPersonName();
			}
		}
		
		if( check ) {
			try {
				manager = userManagerService.isManager( request, effectivePerson );
				if( !manager ) {
					unitNames = userManagerService.listUnitNamesWithPerson(personName);
					groupNames = userManagerService.listGroupNamesByPerson(personName);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionSearchProcess( e, "系统在查询用户组织、群组和管理员权限信息时发生异常。");
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			try {
				LogUtil.INFO( "personName",  personName );
				LogUtil.INFO( "manager",  manager );
				LogUtil.INFO( "unitNames",  unitNames );
				LogUtil.INFO( "groupNames",  groupNames );

				//先查询所有的栏目		
		      	JestClient client = ElasticSearchJestClient.getClient();  
		      	DocumentSearchService documentDao = new DocumentSearchService(client);  
		      	docList = documentDao.search(personName, unitNames, groupNames, wi.getKey(), wi.getTerms(), wi.getMatchs() );
		      	result.setData( docList );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionSearchProcess( e, "系统进行信息检索时发生异常。");
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi{
		
		@FieldDescribe("人员标识")
		private String personName = null;
		
		@FieldDescribe("搜索标识")
		private String key = null;
		
		@FieldDescribe("用于普通过滤的条件")
		private List<Criteria> terms = null;
		
		@FieldDescribe("用于进行全文分词检索的条件")
		private List<Criteria> matchs = null;		

		public List<Criteria> getTerms() {
			return terms;
		}

		public List<Criteria> getMatchs() {
			return matchs;
		}

		public void setTerms(List<Criteria> terms) {
			this.terms = terms;
		}

		public void setMatchs(List<Criteria> matchs) {
			this.matchs = matchs;
		}

		public String getPersonName() {
			return personName;
		}

		public void setPersonName(String personName) {
			this.personName = personName;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
		
	}
}