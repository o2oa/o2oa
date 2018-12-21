package com.x.mind.assemble.control.factory;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.mind.assemble.control.AbstractFactory;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindIconInfo;


/**
 * 类   名：MindIconInfoFactory<br/>
 * 实体类：MindIconInfo<br/>
 * 作   者：O2LEE<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-11-15 17:17:26 
**/
public class MindIconInfoFactory extends AbstractFactory {

	public MindIconInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的脑图信息的缩略图
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindIconInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindIconInfo.class, ExceptionWhen.none );
	}
	
}
