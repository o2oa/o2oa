package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ActionAppInfoExport extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionAppInfoExport.class );

    /**
     * 实现栏目设计导入功能
     * 1、栏目信息
     * 2、所有分类信息
     * 3、所有的表单设计
     * 4、视图（列表和数据视图）设计
     * 5、数据字典设计
     * 6、脚本信息
     *
     * @param request
     * @param effectivePerson
     * @param appInfoId
     * @return
     * @throws Exception
     */
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appInfoId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		AppInfo appInfo = null;
		Boolean check = true;
		
		if( StringUtils.isEmpty(appInfoId) ){
			check = false;
			Exception exception = new ExceptionAppInfoIdEmpty();
			result.error( exception );
		}
        //尝试查询栏目信息，判断栏目信息是否存在
        if( check ){
            try {
                appInfo = appInfoServiceAdv.get( appInfoId );
                if( appInfo == null ){
                    check = false;
                    Exception exception = new ExceptionAppInfoNotExists( appInfoId );
                    result.error( exception );
                }
            } catch (Exception e) {
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "根据指定ID查询应用栏目信息对象时发生异常。ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        if( check ){
            try {
                wo = Wo.copier.copy( appInfo );
            } catch (Exception e) {
                Exception exception = new ExceptionAppInfoProcess( e, "将查询出来的应用栏目信息对象转换为可输出的数据信息时发生异常。" );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        //开始查询所有的分类列表
        if( check ){
            List<CategoryInfo> categoryList = null;
//            List<String> categoryIds = null;
            try{
                categoryList = categoryInfoServiceAdv.listByAppId( appInfoId );
                if(ListTools.isNotEmpty(categoryList) ){
                    wo.setCategories( WoCategory.copier.copy( categoryList ) );
                }
            }catch(Exception e){
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "系统根据栏目ID查询所有的分类信息ID列表时发生异常。ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        //开始查询所有的表单列表
        if( check ){
            List<Form> formList = null;
//            List<String> formIds = null;
            try{
                formList = formServiceAdv.listByAppId( appInfoId );
                if(ListTools.isNotEmpty(formList) ){
                    wo.setForms( WoForm.copier.copy(formList) );
                }
            }catch(Exception e){
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "系统根据栏目ID查询所有的表单信息ID列表时发生异常。ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        //开始查询所有的脚本列表
        if( check ){
            List<Script> scriptList = null;
//            List<String> scriptIds = null;
            try{
                scriptList = scriptServiceAdv.listWithAppId(appInfoId);
                if(ListTools.isNotEmpty(scriptList) ){
                    wo.setScripts( WoScript.copier.copy(scriptList) );
                }
            }catch(Exception e){
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "系统根据栏目ID查询所有的脚本信息列表时发生异常。ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        result.setData( wo );
		return result;
	}

    /**
     * 1、栏目信息
     * 2、所有分类信息
     * 3、所有的表单设计
     * 4、视图（列表和数据视图）设计
     * 5、数据字典设计
     * 6、脚本信息
     */
    public static class Wo extends AppInfo  {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<>();

        public static WrapCopier<AppInfo, Wo> copier = WrapCopierFactory.wo( AppInfo.class, Wo.class, null, Wo.Excludes );

        @FieldDescribe("栏目内所有的分类信息列表")
        private List<WoCategory> categories = null;

        @FieldDescribe("栏目内所有的表单信息列表")
        private List<WoForm> forms = null;

        @FieldDescribe("栏目内所有的列表视图信息列表")
        private List<WoView> views = null;

        @FieldDescribe("栏目内所有的数据视图信息列表")
        private List<WoQueryView> queryViews = null;

        @FieldDescribe("栏目内所有的脚本信息列表")
        private List<WoScript> scripts = null;

        @FieldDescribe("栏目内所有的数据字典信息列表")
        private List<WoAppDict> dicts = null;

        public List<WoCategory> getCategories() {
            return categories;
        }

        public void setCategories(List<WoCategory> categories) {
            this.categories = categories;
        }

        public List<WoForm> getForms() {
            return forms;
        }

        public void setForms(List<WoForm> forms) {
            this.forms = forms;
        }

        public List<WoView> getViews() {
            return views;
        }

        public void setViews(List<WoView> views) {
            this.views = views;
        }

        public List<WoQueryView> getQueryViews() {
            return queryViews;
        }

        public void setQueryViews(List<WoQueryView> queryViews) {
            this.queryViews = queryViews;
        }

        public List<WoScript> getScripts() {
            return scripts;
        }

        public void setScripts(List<WoScript> scripts) {
            this.scripts = scripts;
        }

        public List<WoAppDict> getDicts() {
            return dicts;
        }

        public void setDicts(List<WoAppDict> dicts) {
            this.dicts = dicts;
        }
    }

    /**
     * 用于输出的分类信息对象
     */
    public static class WoCategory extends CategoryInfo  {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<>();

        public static WrapCopier<CategoryInfo, WoCategory> copier = WrapCopierFactory.wo( CategoryInfo.class, WoCategory.class, null, WoCategory.Excludes );

    }

    /**
     * 用于输出的表单信息对象
     */
    public static class WoForm extends Form {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<>();

        public static WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo( Form.class, WoForm.class, null, WoForm.Excludes );

    }

    /**
     * 用于输出的列表视图信息对象
     */
    public static class WoView extends View {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<>();

        public static WrapCopier< View, WoView > copier = WrapCopierFactory.wo( View.class, WoView.class, null, WoView.Excludes );

        @FieldDescribe("列表视图内所有的列表列信息")
        private List<ViewFieldConfig> fields = null;

        @FieldDescribe("列表视图所有的分类关联关系")
        private List<ViewCategory> viewCatagories = null;

        public List<ViewFieldConfig> getFields() {
            return fields;
        }

        public void setFields(List<ViewFieldConfig> fields) {
            this.fields = fields;
        }

        public List<ViewCategory> getViewCatagories() {
            return viewCatagories;
        }

        public void setViewCatagories(List<ViewCategory> viewCatagories) {
            this.viewCatagories = viewCatagories;
        }
    }

    /**
     * 用于输出的数据视图信息对象
     */
    public static class WoQueryView extends View {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<>();

        public static WrapCopier<View, WoQueryView> copier = WrapCopierFactory.wo( View.class, WoQueryView.class, null, WoQueryView.Excludes );

    }

    /**
     * 用于输出的数据字典信息对象
     */
    public static class WoScript extends Script {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<>();

        public static WrapCopier<Script, WoScript> copier = WrapCopierFactory.wo( Script.class, WoScript.class, null, WoScript.Excludes );

    }

    /**
     * 用于输出的数据字典信息对象
     */
    public static class WoAppDict extends AppDict {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<>();

        public static WrapCopier<Script, WoScript> copier = WrapCopierFactory.wo( Script.class, WoScript.class, null, WoScript.Excludes );

        @FieldDescribe("数据字典中所有的对象列表")
        private List<WoAppDictItem> items = null;
    }

    /**
     * 用于输出的数据字典信息对象
     */
    public static class WoAppDictItem extends AppDictItem {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<>();

        public static WrapCopier<AppDictItem, WoAppDictItem> copier = WrapCopierFactory.wo( AppDictItem.class, WoAppDictItem.class, null, WoAppDictItem.Excludes );
        
    }
}