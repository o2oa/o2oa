package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.teamwork.assemble.control.Business;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;


/**
 * 对动态信息查询的服务
 *
 * @author O2LEE
 */
public class DynamicQueryService {

	private DynamicService dynamicService = new DynamicService();

	/**
	 * 根据动态的标识查询动态信息
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public Dynamic get( String flag ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return dynamicService.get(emc, flag );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据ID列表查询动态信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Dynamic> list(List<String> ids) throws Exception {
		if (ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( Dynamic.class,  ids );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 对项目信息进行分页查询(包含权限)
	 * @param personName
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @param adjustPage
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public List<Dynamic> listPagingWithCondition( String personName, String orderField, String orderType, QueryFilter queryFilter, Integer adjustPage,
												  Integer pageSize) throws Exception {
		if( pageSize == null || pageSize <= 0 ) { pageSize = 20; }
		if(adjustPage == null || adjustPage <=0) { adjustPage = 1; }
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc);
			return business.dynamicFactory().listPagingWithCondition(personName, orderField, orderType, queryFilter, adjustPage, pageSize);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据条件统计项目信息(包含权限)
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithCondition( String personName, QueryFilter queryFilter) throws Exception {
		//按正常逻辑根据序列进行分页查询
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc);
			return business.dynamicFactory().countWithCondition(personName, queryFilter);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据过滤条件查询符合要求的动态信息数量
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( QueryFilter queryFilter  ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return dynamicService.countWithFilter(emc, queryFilter );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据过滤条件查询符合要求的动态信息列表
	 * @param currentPerson
	 * @param pageSize
	 * @param pageNum
	 * @param projectIds
	 * @param taskIds
	 * @return
	 * @throws Exception
	 */
	public List<Dynamic> listWithFilter( EffectivePerson currentPerson, Integer pageSize, Integer pageNum, List<String> projectIds, List<String> taskIds ) throws Exception {
		List<Dynamic> dynamicList = null;
		List<Dynamic> result = new ArrayList<>();
		Integer maxCount = 20;
		Integer startNumber = 0;
		String orderField = "createTime";
		String orderType = "desc";

		if( pageNum == 0 ) { pageNum = 1; }
		if( pageSize == 0 ) { pageSize = 20; }
		maxCount = pageSize * pageNum;
		startNumber = pageSize * ( pageNum -1 );

		if( StringUtils.isEmpty( orderField ) ) {
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType ) ) {
			orderType = "desc";
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			dynamicList = dynamicService.listWithFilter(emc, maxCount, orderField, orderType, projectIds, taskIds );
			if( ListTools.isNotEmpty( dynamicList )) {
				for( int i = 0; i<dynamicList.size(); i++ ) {
					if( i >= startNumber ) {
						result.add( dynamicList.get( i ));
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 根据过滤条件查询符合要求的动态信息列表
	 * @param effectivePerson
	 * @param pageSize
	 * @param lastId
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Dynamic> listWithFilter(EffectivePerson effectivePerson, Integer pageSize, String lastId, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		List<Dynamic> dynamicList = null;
		List<Dynamic> resultList = new ArrayList<>();
		Dynamic lastDynamic = null;

		Integer maxCount = 2000;
		if( pageSize == 0 ) { pageSize = 20; }

		if( StringUtils.isEmpty( orderField )) {
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType )) {
			orderType = "desc";
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( StringUtils.isNotEmpty(lastId) && !"(0)".equals( lastId ) && !"null".equals( lastId )) {
				lastDynamic = emc.find( lastId, Dynamic.class );
			}
			dynamicList = dynamicService.listWithFilterNext(emc, maxCount, null, orderField, orderType, queryFilter );
		} catch (Exception e) {
			throw e;
		}
		if( ListTools.isNotEmpty( dynamicList )) {
			int count = 0;
			if( lastDynamic != null ) {
				boolean add = false;
				//获取自lastDynamic之后的一页内容
				for( Dynamic dynamic : dynamicList ) {
					if( add ) {
						count ++;
						if( count <= pageSize ) {
							resultList.add( dynamic );
						}
					}
					if( dynamic.getId().equals( lastDynamic.getId() )) {
						add = true;
					}
				}
			}else {
				//只获取第一页内容
				for( Dynamic dynamic : dynamicList ) {
					count ++;
					if( count <= pageSize ) {
						resultList.add(dynamic);
					}
				}
			}
		}
		return resultList;
	}
}
