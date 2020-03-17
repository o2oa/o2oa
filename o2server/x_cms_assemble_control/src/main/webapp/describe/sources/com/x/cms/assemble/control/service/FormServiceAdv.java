package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.core.entity.element.Form;

/**
 * 对表单信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class FormServiceAdv {
	
	private FormService formService = new FormService();

	public List<Form> list( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( Form.class, ids );
//			return formService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listIdsByAppId( String appId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return formService.listIdsWithAppId( emc, appId );
		} catch ( Exception e ) {
			throw e;
		}
	}

    public List<Form> listByAppId( String appId ) throws Exception {
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> formIds = formService.listIdsWithAppId( emc, appId);
            return emc.list( Form.class, formIds );
//            return formService.list( emc, formIds );
        } catch ( Exception e ) {
            throw e;
        }
    }

	public Form get( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return formService.id( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Form save( Form form, EffectivePerson effectivePerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			form = formService.save( emc, form );
			return form;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( Form form, EffectivePerson effectivePerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			formService.delete( emc, form.getId() );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public String getNameWithId(String formId) throws Exception {
		if( StringUtils.isEmpty( formId )) {
			return  "未命名表单";
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return formService.getNameWithId( emc, formId );
		} catch ( Exception e ) {
			throw e;
		}
	}

}
