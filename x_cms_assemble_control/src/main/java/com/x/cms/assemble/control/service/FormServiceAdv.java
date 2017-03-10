package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.core.entity.element.Form;

public class FormServiceAdv {
	
	private FormService formService = new FormService();

	public List<Form> list( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return formService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByAppId( String appId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return formService.listByAppId( emc, appId );
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

	public Form save( Form form, EffectivePerson currentPerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			form = formService.save( emc, form );
			return form;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( Form form, EffectivePerson currentPerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			formService.delete( emc, form.getId() );
		} catch ( Exception e ) {
			throw e;
		}
	}

}
