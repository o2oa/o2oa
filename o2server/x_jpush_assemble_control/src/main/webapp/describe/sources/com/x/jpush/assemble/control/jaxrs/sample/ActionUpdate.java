package com.x.jpush.assemble.control.jaxrs.sample;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.core.entity.SampleEntityClassName;

/**
 * 示例数据信息更新服务
 */
public class ActionUpdate extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionUpdate.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		SampleEntityClassName sampleEntityClassName = null;
		Boolean check = true;

		//与数据库交互示例, 根据ID查询单条记录
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			sampleEntityClassName = emc.find( id, SampleEntityClassName.class );			
			if( sampleEntityClassName == null ){
				Exception exception = new ExceptionSampleEntityClassNameNotExists( id );
				result.error( exception );
			}
		} catch (Exception e) {
			Exception exception = new ExceptionSampleEntityClassFind( e, "系统在根据ID查询指定示例数据记录时发生异常！ID=" + id );
			result.error( exception );
			logger.error(e);
		}
				
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionSampleEntityClassProcess(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if( StringUtils.isNotEmpty( wi.getName() )) {
				check = false;
				Exception exception = new ExceptionSampleEntityClassNameEmpty();
				result.error(exception);
			}
		}

		if (check) {
			Wi.copier.copy( wi ).copyTo( sampleEntityClassName, JpaObject.FieldsUnmodify );
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				//启动事务
				emc.beginTransaction( SampleEntityClassName.class );
				//保存对象
				emc.check( sampleEntityClassName, CheckPersistType.all );
				//提交事务
				emc.commit();
			} catch (Exception e) {
				Exception exception = new ExceptionSampleEntityClassProcess( e, "系统在保存数据记录时发生异常!" );
				result.error( exception );
				logger.error(e);
			}		
		}
		return result;
	}
	
	/**
	 * 用于接受前端传入的对象型参数的帮助类
	 *
	 */
	public static class Wi {
		
		public static WrapCopier<Wi, SampleEntityClassName> copier = WrapCopierFactory.wi( Wi.class, SampleEntityClassName.class, null, JpaObject.FieldsUnmodifyExcludeId );
		
		@FieldDescribe("数据库主键,自动生成.")
		private String id;

		public void onPersist() throws Exception {
		}
		
		@FieldDescribe("示例字符串field")
		private String name;

		@FieldDescribe("示例时间field")
		private Date date;

		@FieldDescribe("示例整型数字field")
		private Integer orderNumber;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public Integer getOrderNumber() {
			return orderNumber;
		}

		public void setOrderNumber(Integer orderNumber) {
			this.orderNumber = orderNumber;
		}
	}
	
	/**
	 * 用于输出响应内容的帮助类
	 *
	 */
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}