package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import java.util.Collection;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrConfigSecretary;

/**
 * @param copier 对象转换类
 * @param id     上一页最后一条的ID
 * @param count  每页条目数:pagesize
 * @param sequenceField  作分页序列的属性名
 * @param equals    等于的条件集合
 * @param notEquals  不等于（例外）的条件集合
 * @param likes    模糊查询条件集合
 * @param ins      IN查询条件集合
 * @param notIns   NOT IN查询条件集合
 * @param members  隶属于
 * @param notMembers  非隶属于
 * @param andJoin  条件的连接方式
 * @param order  排序方式ASC|DESC
 * 
 * @author LIYI
 *
 */
@Wrap( OkrConfigSecretary.class )
public class WrapInAdminFilter extends GsonPropertyObject {
	BeanCopyTools<OkrConfigSecretary, WrapOutOkrConfigSecretary> copier;
	String id;
	Integer count;
	String sequenceField = "sequence";
	ListOrderedMap<String, Object> equals;
	ListOrderedMap<String, Object> notEquals;
	ListOrderedMap<String, Object> likes;
	ListOrderedMap<String, Collection<?>> ins;
	ListOrderedMap<String, Collection<?>> notIns;
	ListOrderedMap<String, Object> members;
	ListOrderedMap<String, Object> notMembers;
	boolean andJoin;
	String order = "DESC";
	
	public BeanCopyTools<OkrConfigSecretary, WrapOutOkrConfigSecretary> getCopier() {
		return copier;
	}
	public void setCopier(BeanCopyTools<OkrConfigSecretary, WrapOutOkrConfigSecretary> copier) {
		this.copier = copier;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getSequenceField() {
		return sequenceField;
	}
	public void setSequenceField(String sequenceField) {
		this.sequenceField = sequenceField;
	}
	public ListOrderedMap<String, Object> getEquals() {
		return equals;
	}
	public void setEquals(ListOrderedMap<String, Object> equals) {
		this.equals = equals;
	}
	public ListOrderedMap<String, Object> getNotEquals() {
		return notEquals;
	}
	public void setNotEquals(ListOrderedMap<String, Object> notEquals) {
		this.notEquals = notEquals;
	}
	public ListOrderedMap<String, Object> getLikes() {
		return likes;
	}
	public void setLikes(ListOrderedMap<String, Object> likes) {
		this.likes = likes;
	}
	public ListOrderedMap<String, Collection<?>> getIns() {
		return ins;
	}
	public void setIns(ListOrderedMap<String, Collection<?>> ins) {
		this.ins = ins;
	}
	public ListOrderedMap<String, Collection<?>> getNotIns() {
		return notIns;
	}
	public void setNotIns(ListOrderedMap<String, Collection<?>> notIns) {
		this.notIns = notIns;
	}
	public ListOrderedMap<String, Object> getMembers() {
		return members;
	}
	public void setMembers(ListOrderedMap<String, Object> members) {
		this.members = members;
	}
	public ListOrderedMap<String, Object> getNotMembers() {
		return notMembers;
	}
	public void setNotMembers(ListOrderedMap<String, Object> notMembers) {
		this.notMembers = notMembers;
	}
	public boolean isAndJoin() {
		return andJoin;
	}
	public void setAndJoin(boolean andJoin) {
		this.andJoin = andJoin;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
}
