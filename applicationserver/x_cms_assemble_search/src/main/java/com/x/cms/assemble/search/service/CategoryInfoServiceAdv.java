package com.x.cms.assemble.search.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.CategoryInfo;

/**
 * 对栏目分类信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class CategoryInfoServiceAdv {
	
	private CategoryInfoService categoryInfoService = new CategoryInfoService();

	public List<String> listIdsByAppId( String appId ) throws Exception {
		if (StringUtils.isEmpty( appId )) {
			throw new Exception("appId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listByAppId(emc, appId);
		} catch (Exception e) {
			throw e;
		}
	}

    public List<CategoryInfo> listByAppId( String appId ) throws Exception {
        if (appId == null || appId.isEmpty()) {
            throw new Exception("appId is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> ids = categoryInfoService.listByAppId(emc, appId);
            if(ListTools.isNotEmpty(ids) ){
                return categoryInfoService.list( emc, ids );
            }
            return null;
        } catch (Exception e) {
            throw e;
        }
    }
   
	public List<CategoryInfo> list(List<String> ids) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.list(emc, ids);
		} catch (Exception e) {
			throw e;
		}
	}

	public CategoryInfo get(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<CategoryInfo> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listAll(emc);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> listAllIds() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listAllIds(emc);
		} catch (Exception e) {
			throw e;
		}
	}
}
