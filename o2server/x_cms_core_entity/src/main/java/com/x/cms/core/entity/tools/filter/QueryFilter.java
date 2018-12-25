package com.x.cms.core.entity.tools.filter;

import java.util.List;

import com.x.cms.core.entity.tools.filter.term.EqualsTerm;
import com.x.cms.core.entity.tools.filter.term.InTerm;
import com.x.cms.core.entity.tools.filter.term.LikeTerm;
import com.x.cms.core.entity.tools.filter.term.MemberTerm;
import com.x.cms.core.entity.tools.filter.term.NotEqualsTerm;
import com.x.cms.core.entity.tools.filter.term.NotInTerm;
import com.x.cms.core.entity.tools.filter.term.NotMemberTerm;

public class QueryFilter{

	private String joinType = "and";
	
	private List<EqualsTerm> equalsTerms = null;
	
	private List<InTerm> inTerms = null;
	
	private List<LikeTerm> likeTerms = null;
	
	private List<MemberTerm> memberTerms = null;
	
	private List<NotEqualsTerm> notEqualsTerms = null;
	
	private List<NotInTerm> notInTerms = null;
	
	private List<NotMemberTerm> notMemberTerms = null;
	
	private QueryFilter and = null;
	
	private QueryFilter or = null;

	public List<EqualsTerm> getEqualsTerms() {
		return equalsTerms;
	}

	public List<InTerm> getInTerms() {
		return inTerms;
	}

	public List<LikeTerm> getLikeTerms() {
		return likeTerms;
	}

	public List<MemberTerm> getMemberTerms() {
		return memberTerms;
	}

	public List<NotEqualsTerm> getNotEqualsTerms() {
		return notEqualsTerms;
	}

	public List<NotInTerm> getNotInTerms() {
		return notInTerms;
	}

	public List<NotMemberTerm> getNotMemberTerms() {
		return notMemberTerms;
	}

	public QueryFilter getAnd() {
		return and;
	}

	public QueryFilter getOr() {
		return or;
	}

	public void setEqualsTerms(List<EqualsTerm> equalsTerms) {
		this.equalsTerms = equalsTerms;
	}

	public void setInTerms(List<InTerm> inTerms) {
		this.inTerms = inTerms;
	}

	public void setLikeTerms(List<LikeTerm> likeTerms) {
		this.likeTerms = likeTerms;
	}

	public void setMemberTerms(List<MemberTerm> memberTerms) {
		this.memberTerms = memberTerms;
	}

	public void setNotEqualsTerms(List<NotEqualsTerm> notEqualsTerms) {
		this.notEqualsTerms = notEqualsTerms;
	}

	public void setNotInTerms(List<NotInTerm> notInTerms) {
		this.notInTerms = notInTerms;
	}

	public void setNotMemberTerms(List<NotMemberTerm> notMemberTerms) {
		this.notMemberTerms = notMemberTerms;
	}

	public void setAnd(QueryFilter and) {
		this.and = and;
	}

	public void setOr(QueryFilter or) {
		this.or = or;
	}

	public String getJoinType() {
		return joinType;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}
}
