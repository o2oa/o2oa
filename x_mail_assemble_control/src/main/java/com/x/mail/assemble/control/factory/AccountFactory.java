package com.x.mail.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.mail.assemble.control.AbstractFactory;
import com.x.mail.assemble.control.Business;
import com.x.mail.assemble.control.LogUtil;
import com.x.mail.core.entity.Account;
import com.x.mail.core.entity.Account_;

/**
 * 应用信息表基础功能服务类
 * @author liyi
 */
public class AccountFactory extends AbstractFactory {

	private LogUtil logger = new LogUtil(AccountFactory.class);
	
	public AccountFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的Appinfo应用信息对象")
	public Account get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Account.class, ExceptionWhen.none);
	}
	
	@MethodDescribe("列示全部的Appinfo应用信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Account> root = cq.from(Account.class);
		cq.select(root.get(Account_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的Appinfo应用信息列表")
	public List<Account> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Account> cq = cb.createQuery(Account.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = root.get(Account_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	@MethodDescribe("对应用信息进行模糊查询，并且返回信息列表.")
	public List<String> listLike(String keyStr) throws Exception {
		String str = keyStr.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = cb.like(root.get(Account_.account), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Account_.mailAddress), str + "%", '\\'));
		cq.select(root.get(Account_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}
	
	/**
	 * 根据用户名称查询邮件账号
	  * @param userName
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAccountsByUser(String userName ) throws Exception{
		EntityManager em = null;
		em = this.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = cb.equal(root.get(Account_.ownerName), userName);
		cq.select(root.get(Account_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	/**
	 * 根据用户查询用户可以访问到的所有栏目ID
	 * 
	 *  SELECT * FROM X.CMS_APPINFO APP WHERE ID IN(
			SELECT ADMINCONFIG.OBJECTID FROM X.CMS_APPCATAGORY_ADMIN ADMINCONFIG 
			WHERE ADMINCONFIG.OBJECTTYPE='APPINFO' AND ADMINCONFIG.ADMINNAME=''
		) OR ID IN (
		    SELECT PERMISSIONCONFIG.OBJECTID FROM X.CMS_APPCATAGORY_PERMISSION PERMISSIONCONFIG 
		    WHERE PERMISSIONCONFIG.OBJECTTYPE = 'APPINFO' AND 
			( PERMISSIONCONFIG.USEDOBJECTNAME = '' AND PERMISSIONCONFIG.USEDOBJECTTYPE='USER' OR
			  PERMISSIONCONFIG.USEDOBJECTNAME = '' AND PERMISSIONCONFIG.USEDOBJECTTYPE='DEPARTMENT' OR
			  PERMISSIONCONFIG.USEDOBJECTNAME = '' AND PERMISSIONCONFIG.USEDOBJECTTYPE='COMPANY' 
		    )
		)
	 * @param person
	 * @return
	 * @throws Exception 
	 */
//	public List<String> listAccountByUserPermission( String person, String department, String company ) throws Exception {
//		//1、用户管理的所有栏目可以被访问到
//		//2、用户有权限访问的所有栏目可以被访问到
//		Query query = null;
//		List<String> admin_ids = null;
//		List<String> permission_ids = null;
//		EntityManager em = null;
//		StringBuffer sql_stringBuffer_1 = null;
//		em = this.entityManagerContainer().get( AppCatagoryAdmin.class );		
//		sql_stringBuffer_1 = new StringBuffer("select o.objectId from "+AppCatagoryAdmin.class.getCanonicalName()+" o ");
//		sql_stringBuffer_1.append("where o.objectType='APPINFO' and o.adminName = ?1 ");
//		
//		logger.debug("listAccountByUserPermission:["+sql_stringBuffer_1.toString()+"]");
//		logger.debug( "person:'" + person + "'" );
//		query = em.createQuery( sql_stringBuffer_1.toString(), String.class );
//		query.setParameter(1, person);//为查询设置所有的参数值
//		admin_ids = query.setMaxResults(100).getResultList();
//		if(admin_ids != null ){
//			logger.debug( "admin_ids.size()=:" + admin_ids.size() );
//		}else{
//			logger.debug( "admin_ids is null!"  );
//		}
//		if( admin_ids == null || admin_ids.size() == 0 ){
//			admin_ids = new ArrayList<String>();
//			admin_ids.add("");
//		}
//		
//		StringBuffer sql_stringBuffer_2 = null;
//		em = this.entityManagerContainer().get( AppCatagoryPermission.class );		
//		sql_stringBuffer_2 = new StringBuffer("select o.objectId from "+AppCatagoryPermission.class.getCanonicalName()+" o ");
//		sql_stringBuffer_2.append("where o.objectType = 'APPINFO' and ( ");
//		sql_stringBuffer_2.append("( o.usedObjectName = ?1 and o.usedObjectType='USER' ) or ");
//		sql_stringBuffer_2.append("( o.usedObjectName = ?2 and o.usedObjectType='DEPARTMENT' ) or ");
//		sql_stringBuffer_2.append("( o.usedObjectName = ?3 and o.usedObjectType='COMPANY' ) ");
//		sql_stringBuffer_2.append(")");
//		logger.debug("listAccountByUserPermission:["+sql_stringBuffer_2.toString()+"]");
//		logger.debug( "person:'" + person + "', company='" + company +  "', department='" + department + "'" );
//		query = em.createQuery( sql_stringBuffer_2.toString(), String.class );
//		query.setParameter(1, person);
//		query.setParameter(2, department);
//		query.setParameter(3, company);
//		permission_ids = query.setMaxResults(100).getResultList();
//		if(permission_ids != null ){
//			logger.debug( "permission_ids.size()=:" + permission_ids.size() );
//		}else{
//			logger.debug( "permission_ids is null!"  );
//		}
//		if( permission_ids == null || permission_ids.size() == 0 ){
//			permission_ids = new ArrayList<String>();
//			permission_ids.add("");
//		}
//		StringBuffer sql_stringBuffer = null;
//		em = this.entityManagerContainer().get( Account.class );		
//		sql_stringBuffer = new StringBuffer("select o.id from "+Account.class.getCanonicalName()+" o ");
//		sql_stringBuffer.append("where o.id in ?1 or o.id in ?2");
//		query = em.createQuery( sql_stringBuffer.toString(), String.class );
//		query.setParameter(1, admin_ids);
//		query.setParameter(2, permission_ids);
//		permission_ids = query.setMaxResults(100).getResultList();		
//		logger.debug("listAccountByUserPermission:["+sql_stringBuffer.toString()+"]");
//		logger.debug( admin_ids );
//		logger.debug( permission_ids );
//		
//		return query.setMaxResults(100).getResultList();
//	}
}