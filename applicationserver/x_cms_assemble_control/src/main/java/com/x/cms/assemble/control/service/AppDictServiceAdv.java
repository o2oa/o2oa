package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.element.AppDict;

/**
 * 对栏目信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class AppDictServiceAdv {

	private AppDictService appDictService = new AppDictService();

	/**
	 * 根据栏目 ID获取所有的数据字典信息ID列表
	 * 
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdsWithAppId( String appId ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appDictService.listIdsWithAppId( emc, appId );
		} catch (Exception e) {
			throw e;
		}
	}

    /**
     * 根据栏目 ID获取所有的数据字典信息列表
     *
     * @param appId
     * @return
     * @throws Exception
     */
    public List<AppDict> listWithAppId(String appId ) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> ids = appDictService.listIdsWithAppId( emc, appId );
            if(ListTools.isNotEmpty(ids) ){
                return appDictService.list( emc, ids );
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

}
