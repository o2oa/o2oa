package com.x.teamwork.assemble.control.jaxrs.taskview;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.TaskView;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.InTerm;
import com.x.teamwork.core.entity.tools.filter.term.IsFalseTerm;
import com.x.teamwork.core.entity.tools.filter.term.IsTrueTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

public class WrapInQueryTask {
	
	@FieldDescribe("用于搜索的标题，单值，非必填.")
	private String key = null;

	private Long rank = 0L;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
	
	/**
	 * 根据传入的查询参数，组织一个完整的QueryFilter对象
	 * @param taskView  
	 * @return
	 */
	public QueryFilter getQueryFilter( TaskView taskView ) {
		QueryFilter queryFilter = getQueryFilterWithViewConfig( taskView );
		//组织查询条件对象
		if( StringUtils.isNotEmpty( this.getKey() )) {
			QueryFilter key_queryFilter = new QueryFilter();
			key_queryFilter.setJoinType( "or" );
			key_queryFilter.addLikeTerm( new LikeTerm( "name", "%" + this.getKey() + "%" ) );
			key_queryFilter.addLikeTerm( new LikeTerm( "summay", "%" + this.getKey() + "%" ) );
//			key_queryFilter.addLikeTerm( new LikeTerm( "executorUnit", "%" + this.getKey() + "%" ) );
//			key_queryFilter.addLikeTerm( new LikeTerm( "creatorPerson", "%" + this.getKey() + "%" ) );
//			key_queryFilter.addLikeTerm( new LikeTerm( "executorIdentity", "%" + this.getKey() + "%" ) );
			key_queryFilter.addLikeTerm( new LikeTerm( "executor", "%" + this.getKey() + "%" ) );
			if( queryFilter != null ) {
				queryFilter.setAnd( key_queryFilter );
			}else {
				queryFilter = key_queryFilter;
			}
		}
		if( queryFilter == null ) {
			queryFilter = new QueryFilter();
		}
		return queryFilter;
	}
	
	private QueryFilter getQueryFilterWithViewConfig( TaskView taskView ) {
		QueryFilter queryFilter = new QueryFilter();
		//组织查询条件对象		
		if( StringUtils.isNotEmpty( taskView.getProject() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "project",  taskView.getProject() ) );
		}
		
		//筛选条件_是否已完成：-1-全部， 1-是，0-否.
		if( taskView.getWorkCompleted() == 1 ) {
			queryFilter.addIsTrueTerm( new IsTrueTerm("completed"));
		}else if( taskView.getWorkCompleted() == 0 ) {
			queryFilter.addIsFalseTerm( new IsFalseTerm("completed"));
		}
		
		//筛选条件_是否已超时：-1-全部， 1-是，0-否.
		if( taskView.getWorkOverTime() == 1 ) {
			queryFilter.addIsTrueTerm( new IsTrueTerm("overtime"));
		}else if( taskView.getWorkOverTime() == 0 ) {
			queryFilter.addIsFalseTerm( new IsFalseTerm("overtime"));
		}
		//只查询我作为负责人的任务
		if( taskView.getIsExcutor() ) {
			queryFilter.addEqualsTerm( new EqualsTerm( "executor", taskView.getOwner() ) );
		}

		if( ListTools.isNotEmpty( taskView.getChoosePriority() )) {
			queryFilter.addInTerm( new InTerm("priority", new ArrayList<>(taskView.getChoosePriority())) );
		}
		
		
		if( ListTools.isNotEmpty( taskView.getChooseWorkTag() )) {
			//需要换成In符合条件的任务ID
		}
		return queryFilter;
	}
}
