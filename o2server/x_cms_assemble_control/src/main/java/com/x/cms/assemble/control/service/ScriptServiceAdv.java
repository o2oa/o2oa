package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.element.Script;

/**
 * 对栏目信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class ScriptServiceAdv {

	private ScriptService scriptService = new ScriptService();

	/**
	 * 根据栏目 ID获取所有的脚本信息ID列表
	 * 
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdsWithAppId( String appId ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return scriptService.listIdsWithAppId( emc, appId);
		} catch (Exception e) {
			throw e;
		}
	}

    /**
     * 根据栏目 ID获取所有的脚本信息列表
     *
     * @param appId
     * @return
     * @throws Exception
     */
    public List<Script> listWithAppId(String appId ) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> ids = scriptService.listIdsWithAppId( emc, appId);
            if(ListTools.isNotEmpty(ids) ){
                return emc.list( Script.class, ids );
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

}
