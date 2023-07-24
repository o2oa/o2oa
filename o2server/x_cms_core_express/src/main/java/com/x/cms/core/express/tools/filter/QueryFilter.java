package com.x.cms.core.express.tools.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.express.tools.filter.term.DateBetweenTerm;
import com.x.cms.core.express.tools.filter.term.EqualsTerm;
import com.x.cms.core.express.tools.filter.term.InTerm;
import com.x.cms.core.express.tools.filter.term.IsFalseTerm;
import com.x.cms.core.express.tools.filter.term.IsTrueTerm;
import com.x.cms.core.express.tools.filter.term.LikeTerm;
import com.x.cms.core.express.tools.filter.term.MemberTerm;
import com.x.cms.core.express.tools.filter.term.NotEqualsTerm;
import com.x.cms.core.express.tools.filter.term.NotInTerm;
import com.x.cms.core.express.tools.filter.term.NotMemberTerm;


public class QueryFilter {

	private String joinType = "and";

	private List<EqualsTerm> equalsTerms = null;

	private List<InTerm> inTerms = null;

	private List<LikeTerm> likeTerms = null;

	private List<IsTrueTerm> isTrueTerms = null;

	private List<IsFalseTerm> isFalseTerms = null;

	private List<MemberTerm> memberTerms = null;

	private List<NotEqualsTerm> notEqualsTerms = null;

	private List<NotInTerm> notInTerms = null;

	private List<NotMemberTerm> notMemberTerms = null;
	
	private List<DateBetweenTerm> dateBetweenTerms = null;

	private QueryFilter and = null;

	private QueryFilter or = null;

	public List<DateBetweenTerm> getDateBetweenTerms() {
		return dateBetweenTerms;
	}

	public void setDateBetweenTerms(List<DateBetweenTerm> dateBetweenTerms) {
		this.dateBetweenTerms = dateBetweenTerms;
	}

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

	public List<IsTrueTerm> getIsTrueTerms() {
		return isTrueTerms;
	}

	public void setIsTrueTerms(List<IsTrueTerm> isTrueTerms) {
		this.isTrueTerms = isTrueTerms;
	}

	public List<IsFalseTerm> getIsFalseTerms() {
		return isFalseTerms;
	}

	public void setIsFalseTerms(List<IsFalseTerm> isFalseTerms) {
		this.isFalseTerms = isFalseTerms;
	}

	public void addIsTrueTerm( IsTrueTerm term ) {
		if( this.isTrueTerms == null ){ this.isTrueTerms = new ArrayList<>(); }
		boolean exists = false;
		for( IsTrueTerm _term : this.isTrueTerms ) {
			if ( _term.getName().equals( term.getName() )) {
				exists = true;
			}
		}
		if( !exists ) {
			this.isTrueTerms.add( term );
		}
	}
	
	public void addIsFalseTerm( IsFalseTerm term ) {
		if( this.isFalseTerms == null ){ this.isFalseTerms = new ArrayList<>(); }
		boolean exists = false;
		for( IsFalseTerm _term : this.isFalseTerms ) {
			if ( _term.getName().equals( term.getName() )) {
				exists = true;
			}
		}
		if( !exists ) {
			this.isFalseTerms.add( term );
		}
	}
	
	public void addEqualsTerm(EqualsTerm term) {
		if (this.equalsTerms == null) {
			this.equalsTerms = new ArrayList<>();
		}
		boolean exists = false;
		for (EqualsTerm _term : this.equalsTerms) {
			if (_term.getName().equals(term.getName())) {
				exists = true;
				// 替换新的值
				_term.setValue(term.getValue());
			}
		}
		if (!exists) {
			this.equalsTerms.add(term);
		}
	}

	public void setEqualsTerms(List<EqualsTerm> equalsTerms) {
		this.equalsTerms = equalsTerms;
	}

	public void addInTerm(InTerm term) {
		if (this.inTerms == null) {
			this.inTerms = new ArrayList<>();
		}
		boolean exists = false;
		for (InTerm _term : this.inTerms) {
			if (_term.getName().equals(term.getName())) {
				exists = true;
				// 替换新的值
				_term.setValue(term.getValue());
			}
		}
		if (!exists) {
			this.inTerms.add(term);
		}
	}

	public void setInTerms(List<InTerm> inTerms) {
		this.inTerms = inTerms;
	}

	public void addLikeTerm(LikeTerm term) {
		if (this.likeTerms == null) {
			this.likeTerms = new ArrayList<>();
		}
		boolean exists = false;
		for (LikeTerm _term : this.likeTerms) {
			if (_term.getName().equals(term.getName())) {
				exists = true;
				// 替换新的值
				_term.setValue(term.getValue());
			}
		}
		if (!exists) {
			this.likeTerms.add(term);
		}
	}

	public void setLikeTerms(List<LikeTerm> likeTerms) {
		this.likeTerms = likeTerms;
	}

	public void addMemberTerm(MemberTerm term) {
		if (this.memberTerms == null) {
			this.memberTerms = new ArrayList<>();
		}
		boolean exists = false;
		for (MemberTerm _term : this.memberTerms) {
			if (_term.getName().equals(term.getName())) {
				exists = true;
				// 替换新的值
				_term.setValue(term.getValue());
			}
		}
		if (!exists) {
			this.memberTerms.add(term);
		}
	}

	public void setMemberTerms(List<MemberTerm> memberTerms) {
		this.memberTerms = memberTerms;
	}

	public void addNotEqualsTerm(NotEqualsTerm term) {
		if (this.notEqualsTerms == null) {
			this.notEqualsTerms = new ArrayList<>();
		}
		boolean exists = false;
		for (NotEqualsTerm _term : this.notEqualsTerms) {
			if (_term.getName().equals(term.getName())) {
				exists = true;
				// 替换新的值
				_term.setValue(term.getValue());
			}
		}
		if (!exists) {
			this.notEqualsTerms.add(term);
		}
	}

	public void setNotEqualsTerms(List<NotEqualsTerm> notEqualsTerms) {
		this.notEqualsTerms = notEqualsTerms;
	}

	public void addNotInTerm(NotInTerm term) {
		if (this.notInTerms == null) {
			this.notInTerms = new ArrayList<>();
		}
		boolean exists = false;
		for (NotInTerm _term : this.notInTerms) {
			if (_term.getName().equals(term.getName())) {
				exists = true;
				// 替换新的值
				_term.setValue(term.getValue());
			}
		}
		if (!exists) {
			this.notInTerms.add(term);
		}
	}

	public void setNotInTerms(List<NotInTerm> notInTerms) {
		this.notInTerms = notInTerms;
	}

	public void addNotMemberTerm(NotMemberTerm term) {
		if (this.notMemberTerms == null) {
			this.notMemberTerms = new ArrayList<>();
		}
		boolean exists = false;
		for (NotMemberTerm _term : this.notMemberTerms) {
			if (_term.getName().equals(term.getName())) {
				exists = true;
				// 替换新的值
				_term.setValue(term.getValue());
			}
		}
		if (!exists) {
			this.notMemberTerms.add(term);
		}
	}
	
	public void addDateBetweenTerm( DateBetweenTerm term) {
		if( term == null ) {
			return;
		}
		if( term.getValue() == null ) {
			return;
		}
		if( term.getValue().get(0) == null ) {
			return;
		}
		if( term.getValue().get(1) == null ) {
			return;
		}
		if (this.dateBetweenTerms == null) {
			this.dateBetweenTerms = new ArrayList<>();
		}
		boolean exists = false;
		for ( DateBetweenTerm _term : this.dateBetweenTerms) {
			if (_term.getName().equals(term.getName())) {
				exists = true;
				// 替换新的值
				_term.setValue( term.getValue() );
			}
		}
		if (!exists) {
			this.dateBetweenTerms.add(term);
		}
	}

	public void addDateBetweenTerm( String name, Date startDate, Date endDate) {
		if( startDate == null ) {
			startDate = new Date();
		}
		if( endDate == null ) {
			endDate = new Date();
		}
		DateBetweenTerm term = new DateBetweenTerm();
		term.setName( name );
		term.setValue( new ArrayList<Date>());
		term.getValue().add( startDate );
		term.getValue().add( endDate );
		addDateBetweenTerm(term);
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

	public String getQueryContent() {
		StringBuffer content = new StringBuffer("query:");
		content.append("{");
		if (this.joinType != null) {
			content.append("'joinType':'" + this.joinType.toString() + "'");
		}
		if (this.and != null) {
			content.append("'and':" + this.and.toString());
		}
		if (this.or != null) {
			content.append("'or':" + this.or.toString());
		}
		content.append("}");

		content.append(",equalsTerms:[");
		if (ListTools.isNotEmpty(equalsTerms)) {
			for (EqualsTerm term : equalsTerms) {
				content.append("{");
				content.append("'" + term.getName().toString() + "':");
				content.append("'" + term.getValue().toString() + "'");
				content.append("},");
			}
		}
		content.append("]");

		content.append(",notEqualsTerms:[");
		if (ListTools.isNotEmpty(notEqualsTerms)) {
			for (NotEqualsTerm term : notEqualsTerms) {
				content.append("{");
				content.append("'" + term.getName().toString() + "':");
				content.append("'" + term.getValue().toString() + "'");
				content.append("},");
			}
		}
		content.append("]");

		content.append(",inTerms:[");
		if (ListTools.isNotEmpty(inTerms)) {
			for (InTerm term : inTerms) {
				content.append("{");
				content.append("'" + term.getName().toString() + "':");
				if (ListTools.isNotEmpty(term.getValue())) {
					content.append("[");
					for (Object object : term.getValue()) {
						content.append("'" + object.toString() + "', ");
					}
					content.append("]");
				}
				content.append("},");
			}
		}
		content.append("]");

		content.append(",notInTerms:[");
		if (ListTools.isNotEmpty(notInTerms)) {
			for (NotInTerm term : notInTerms) {
				content.append("{");
				content.append("'" + term.getName().toString() + "':");
				if (ListTools.isNotEmpty(term.getValue())) {
					content.append("[");
					for (Object object : term.getValue()) {
						content.append("'" + object.toString() + "', ");
					}
					content.append("]");
				}
				content.append("},");
			}
		}
		content.append("]");

		content.append(",likeTerms:[");
		if (ListTools.isNotEmpty(likeTerms)) {
			for (LikeTerm term : likeTerms) {
				content.append("{");
				content.append("'" + term.getName().toString() + "':");
				content.append("'" + term.getValue().toString() + "'");
				content.append("},");
			}
		}
		content.append("]");

		content.append(",memberTerms:[");
		if (ListTools.isNotEmpty(memberTerms)) {
			for (MemberTerm term : memberTerms) {
				content.append("{");
				content.append("'" + term.getName().toString() + "':");
				content.append("'" + term.getValue().toString() + "'");
				content.append("},");
			}
		}
		content.append("]");

		content.append(",notMemberTerms:[");
		if (ListTools.isNotEmpty(notMemberTerms)) {
			for (NotMemberTerm term : notMemberTerms) {
				content.append("{");
				content.append("'" + term.getName().toString() + "':");
				content.append("'" + term.getValue().toString() + "'");
				content.append("},");
			}
		}
		
		content.append(",betweenTerms:[");
		if (ListTools.isNotEmpty(dateBetweenTerms)) {
			for ( DateBetweenTerm term : dateBetweenTerms) {
				content.append("{");
				content.append("'" + term.getName().toString() + "':");
				if (ListTools.isNotEmpty( term.getValue())) {
					content.append("[");
					for ( Date object : term.getValue()) {
						content.append("'" + object.toString() + "', ");
					}
					content.append("]");
				}
				content.append("},");
			}
		}
		
		content.append("]");
		return content.toString();
	}

	/**
	 * 将查询的所有内容组织成String，然后计算一个SHA1 DigestUtils.sha1Hex(content)
	 * 
	 * @return
	 */
	public String getContentSHA1() {
		String content = getQueryContent();
		if (StringUtils.isEmpty(content)) {
			return DigestUtils.sha1Hex("null");
		} else {
			return DigestUtils.sha1Hex(content);
		}
	}

	
}
