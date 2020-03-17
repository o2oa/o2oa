package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;

public class ActionAppInfoExportMenu extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionAppInfoExportMenu.class );

    /**
     * 实现栏目设计导入内容列表查询
     * 1、所有分类信息列表
     * 2、所有的表单信息列表
     * 3、所有的视图（列表和数据视图）信息列表
     * 4、所有的数据字典列表
     * 5、所有的脚本信息列表
     * 6、所有的分类视图关联信息
     *
     * @param request
     * @param effectivePerson
     * @param appInfoId
     * @return
     * @throws Exception
     */
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appInfoId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
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

        //开始查询所有的分类列表
        if( check ){
            List<CategoryInfo> categoryList = null;
            try{
                categoryList = categoryInfoServiceAdv.listByAppId( appInfoId );
                if(ListTools.isNotEmpty(categoryList) ){
                    List<WoEntry> categories = new ArrayList<>();
                    for( CategoryInfo category : categoryList ){
                        categories.add( new WoEntry( category.getId(), category.getCategoryName()) );
                    }
                    wo.setCategories( categories );
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
            try{
                formList = formServiceAdv.listByAppId( appInfoId );
                if(ListTools.isNotEmpty(formList) ){
                    List<WoEntry> forms = new ArrayList<>();
                    for( Form form : formList ){
                        forms.add( new WoEntry( form.getId(), form.getName() ) );
                    }
                    wo.setForms( forms );
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
            try{
                scriptList = scriptServiceAdv.listWithAppId(appInfoId);
                if(ListTools.isNotEmpty(scriptList) ){
                    List<WoEntry> scripts = new ArrayList<>();
                    for( Script script : scriptList ){
                        scripts.add( new WoEntry( script.getId(), script.getName() ) );
                    }
                    wo.setScripts( scripts );
                }
            }catch(Exception e){
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "系统根据栏目ID查询所有的脚本信息列表时发生异常。ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        //开始查询所有的数据字典列表
        if( check ){
            List<AppDict> dictList = null;
            try{
                dictList = appDictServiceAdv.listWithAppId( appInfoId );
                if(ListTools.isNotEmpty(dictList) ){
                    List<WoEntry> dictEntry = new ArrayList<>();
                    for( AppDict dict : dictList ){
                        dictEntry.add( new WoEntry( dict.getId(), dict.getName() ) );
                    }
                    wo.setScripts( dictEntry );
                }
            }catch(Exception e){
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "系统根据栏目ID查询所有的数据字典信息列表时发生异常。ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        result.setData( wo );
		return result;
	}

    /**
     * 1、所有分类信息列表
     * 2、所有的表单信息列表
     * 3、所有的视图（列表和数据视图）信息列表
     * 4、所有的数据字典列表
     * 5、所有的脚本信息列表
     * 6、所有的分类视图关联信息
     */
    public static class Wo  {

        @FieldDescribe("栏目内所有的表单信息列表")
        private List<WoEntry> forms = null;

        @FieldDescribe("栏目内所有的分类信息列表")
        private List<WoEntry> categories = null;

        @FieldDescribe("栏目内所有的脚本信息列表")
        private List<WoEntry> scripts = null;

        @FieldDescribe("栏目内所有的数据字典信息列表")
        private List<WoEntry> dicts = null;

        @FieldDescribe("栏目内所有的列表视图信息列表")
        private List<WoEntry> views = null;

        @FieldDescribe("栏目内所有的数据视力信息列表")
        private List<WoEntry> queryViews = null;

        public List<WoEntry> getForms() {
            return forms;
        }

        public void setForms(List<WoEntry> forms) {
            this.forms = forms;
        }

        public List<WoEntry> getCategories() {
            return categories;
        }

        public void setCategories(List<WoEntry> categories) {
            this.categories = categories;
        }

        public List<WoEntry> getScripts() {
            return scripts;
        }

        public void setScripts(List<WoEntry> scripts) {
            this.scripts = scripts;
        }

        public List<WoEntry> getDicts() {
            return dicts;
        }

        public void setDicts(List<WoEntry> dicts) {
            this.dicts = dicts;
        }

        public List<WoEntry> getViews() {
            return views;
        }

        public void setViews(List<WoEntry> views) {
            this.views = views;
        }

        public List<WoEntry> getQueryViews() {
            return queryViews;
        }

        public void setQueryViews(List<WoEntry> queryViews) {
            this.queryViews = queryViews;
        }
    }

    public static class WoEntry  {

        @FieldDescribe("信息的名称")
        private String e_name = null;

        @FieldDescribe("信息的ID")
        private String e_id = null;

        public WoEntry( String e_id, String e_name){
            this.e_id = e_id;
            this.e_name = e_name;
        }

        public String getE_name() {
            return e_name;
        }

        public void setE_name(String e_name) {
            this.e_name = e_name;
        }

        public String getE_id() {
            return e_id;
        }

        public void setE_id(String e_id) {
            this.e_id = e_id;
        }
    }
}