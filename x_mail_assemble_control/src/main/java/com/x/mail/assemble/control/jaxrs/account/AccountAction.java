package com.x.mail.assemble.control.jaxrs.account;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.mail.assemble.control.Business;
import com.x.mail.assemble.control.LogUtil;
import com.x.mail.assemble.control.factory.AccountFactory;
import com.x.mail.core.entity.Account;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;


@Path("account")
public class AccountAction extends StandardJaxrsAction{
	
	private LogUtil logger = new LogUtil( AccountAction.class );
	private BeanCopyTools<WrapInAccount, Account> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAccount.class, Account.class, null, WrapInAccount.Excludes );
	private BeanCopyTools<Account, WrapOutAccount> wrapout_copier = BeanCopyToolsBuilder.create( Account.class, WrapOutAccount.class, null, WrapOutAccount.Excludes);
	
	private Ehcache cache = ApplicationCache.instance().getCache( Account.class );
	
	@HttpMethodDescribe(value = "获取当前用户的邮件账号", response = WrapOutAccount.class)
	@GET
	@Path("list/user")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyAccount( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAccount>> result = new ActionResult<>();
		List<WrapOutAccount> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		logger.debug("[listMyAccount]user[" + currentPerson.getName() + "] try to get account in his/her permission......" );
		
		String cacheKey = "account.listMyAccount." + currentPerson.getName();
		Element element = null;
		element = cache.get(cacheKey);
		if( element != null ){
			logger.debug("[listMyAccount]get accountlist from cache. cacheKey="+cacheKey );
			wraps = (List<WrapOutAccount>) element.getObjectValue();
			result.setData( wraps );
		}else{
			logger.debug("[listMyAccount]no accountlist cache exists for person{'name':'"+currentPerson.getName()+"'}. cacheKey=" + cacheKey );
			Business business = null;
			AccountFactory AccountFactory = null;
			
			List<Account> accountList = null;
			List<String> account_ids = null;
			
			
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {			
					business = new Business(emc);			
					AccountFactory accountFactory = business.getAccountFactory();
					
					//获取用户有权限访问的所有应用列表
					//account_ids = accountFactory.listAll();
					logger.debug("[listMyAccount]listAccountByUserPermission{"
							+ "'person':'"+ currentPerson.getName() +"'}......" );
					account_ids = accountFactory.listAccountsByUser(currentPerson.getName());
					
					//查询ID IN ids 的所有应用信息列表
					accountList = AccountFactory.list( account_ids );
					//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
					wraps = wrapout_copier.copy( accountList );
			
					//对查询的列表进行排序
					Collections.sort( wraps );
					result.setData( wraps );
					
					//将查询结果放进缓存里
					logger.debug("[listMyAccount]push accountlist to cache. cacheKey="+cacheKey );
					cache.put( new Element( cacheKey, wraps ) );
								
				} catch ( Throwable th ) {
					th.printStackTrace();
					result.error(th);
				}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	
	@HttpMethodDescribe(value = "根据ID获取account对象.", response = WrapOutAccount.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAccount> result = new ActionResult<>();
		WrapOutAccount wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[get]user[" + currentPerson.getName() + "] try to get account['id':'"+id+"']......" );		
		String cacheKey = "account.get." + id;
		Element element = null;
		element = cache.get(cacheKey);	
		if( element != null ){
			logger.debug("[get]get account from cache. cacheKey="+cacheKey );
			wrap = (WrapOutAccount) element.getObjectValue();
			result.setData( wrap );
		}else{
			logger.debug("[get]no account cache exists for account{'id':'"+id+"'}. cacheKey=" + cacheKey );
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Account account = business.getAccountFactory().get(id);
				if ( null == account ) {
					result.warn("[get]account{id:" + id + "} not existed.");
				}else{
					//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
					BeanCopyTools<Account, WrapOutAccount> copier = BeanCopyToolsBuilder.create(Account.class, WrapOutAccount.class, null, WrapOutAccount.Excludes);
					wrap = new WrapOutAccount();
					copier.copy(account, wrap);
					result.setData(wrap);
					
					//将查询结果放进缓存里
					logger.debug("[listAllAccount]push accountlist to cache. cacheKey="+cacheKey );
					cache.put( new Element( cacheKey, wrap ) );
				}
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	

	
	@HttpMethodDescribe(value = "创建Account信息对象.", request = WrapInAccount.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInAccount wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		
		//获取到当前用户信息
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		logger.debug("get person uid from request. person=" + currentPerson.getName() );
		
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
				Business business = new Business(emc);
				
				Account account = new Account();
				wrapin_copier.copy( wrapIn, account );
				//如果JSON给过来的ID不为空，那么使用用户传入的ID
				if( wrapIn.getId().length() == account.getId().length() ){
					logger.debug("Used id in data, id=" + wrapIn.getId() );
					account.setId( wrapIn.getId() );
				}
				logger.debug("System trying to beginTransaction to save account......" );
				emc.beginTransaction( Account.class );
				
				emc.persist( account, CheckPersistType.all );
				emc.commit();
				logger.debug("System save account success......" );
				
				logger.debug("System try to remove all account cache......" );
				//清除所有的Account缓存
				ApplicationCache.notify( Account.class );
				
				//成功新增一个应用信息
				//logger.debug("System trying to save log......" );
				//emc.beginTransaction( Log.class);
				//business.log( currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功创建一个应用信息", account.getId(), "", "", "", "APP", "新增" );
				emc.commit();
				wrap = new WrapOutId( account.getId() );
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
			
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	
	@HttpMethodDescribe(value = "根据ID删除Account对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		logger.debug("method delete has been called, try to delete account{'id':'"+id+"'}......" );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson currentPerson = this.effectivePerson(request);
			Business business = new Business(emc);
			
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			Account account = business.getAccountFactory().get(id);
			if (null == account) {
				throw new Exception("account{id:" + id + "} not existed.");
			}
			
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if ( !business.isXAdmin( request, currentPerson ) && !account.getOwnerName().equals(currentPerson.getName()) ) {
				throw new Exception("person{name:" + currentPerson.getName() + "} has sufficient permissions");
			}
			
			//判断account是否允许被删除
			if ( business.accountDeleteAvailable( id ) ) { 
				logger.debug("System trying to beginTransaction to delete account......" );
				
				//在删除应用信息之前，先删除相关的管理和权限设置
				
				//进行数据库持久化操作
				emc.beginTransaction( Account.class );
				emc.remove( account, CheckRemoveType.all );
				emc.commit();
				logger.debug("System delete account success......" );
				
				logger.debug("System try to remove all account cache......" );
				//清除所有的Account缓存
				ApplicationCache.notify( Account.class );
				
				//成功删除一个账号
//				logger.debug("System trying to save log......" );
//				emc.beginTransaction( Log.class);
//				business.log( currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功删除一个应用信息", account.getId(), "", "", "", "APP", "删除" );
//				emc.commit();
				
				wrap = new WrapOutId( account.getId() );
				result.setData(wrap);
			}else{
				logger.debug("account can't be deleted, this account is referenced by other object ......" );
				throw new Exception("account{id:" + id + "} is referenced by other object!can't delete it.");
			}
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新Account对象.", request = WrapInAccount.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInAccount wrapIn) {
		logger.debug("method put has been called, try to update account{'id':'"+id+"'}......" );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson currentPerson = this.effectivePerson(request);
			
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			Account account = business.getAccountFactory().get(id);
			if ( null == account ) {
				throw new Exception("account{id:" + id + "} not existed.");
			}
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if ( !business.isXAdmin( request, currentPerson ) && !account.getOwnerName().equals(currentPerson.getName()) ) {
				throw new Exception("person{name:" + currentPerson.getName() + "} has sufficient permissions");
			}
			//进行数据库持久化操作
			BeanCopyTools<WrapInAccount, Account> copier = BeanCopyToolsBuilder.create(WrapInAccount.class, Account.class, null, WrapInAccount.Excludes);
			logger.debug("System trying to beginTransaction to update account......" );
			emc.beginTransaction( Account.class);
			copier.copy( wrapIn, account );
			emc.check( account, CheckPersistType.all);			
			emc.commit();
			logger.debug("System update account success......" );
			
			logger.debug("System try to remove all account cache......" );
			//清除所有的Account缓存
			ApplicationCache.notify( Account.class );
			
			//成功更新一个应用信息
//			logger.debug("System trying to save log......" );
//			emc.beginTransaction( Log.class);
//			business.log( currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功更新一个应用信息", account.getId(), "", "", "", "APP", "更新" );
//			emc.commit();
			
			wrap = new WrapOutId( account.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}