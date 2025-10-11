MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.ScriptArea", null, false);
MWF.xApplication.process.FormDesigner.widget.FieldPermission = new Class({
	Implements: [Options, Events],
	options: {
		isField: false
	},
	initialize: function(node, property, options){
		this.setOptions(options);
		this.node = $(node);
        this.property = property;
		
		this.path = "../x_component_process_FormDesigner/widget/$FieldPermission/";

		// const { 
		// 	readByActivity, readByActivityValue, readByOrg, readByOrgValue, readByScript, readByScriptValue, hideCannotRead, 
		// 	editByActivity, editByActivityValue, editByOrg, editByOrgValue, editByScript, editByScriptValue } = this.property.data;
		// this.formDefaultConfig = {
		// 	readByActivity: true, readByActivityValue,
		// 	readByOrg, readByOrgValue,
		// 	readByScript, readByScriptValue,
		// 	hideCannotRead: hideCannotRead || 'inherit',
		// 	editByActivity, editByActivityValue,
		// 	editByOrg, editByOrgValue,
		// 	editByScript, editByScriptValue
		// }
		this.formDefaultConfig = this.property.data;

		this.processConfig = {};

		this.load();
	},
	load: function(){
		this.isField = this.options.isField;
		this.isProcess = this.property.designer.options.name==='process.FormDesigner';
		this.isCms = this.property.designer.options.name==='cms.FormDesigner';
		this.node.loadAll({
            html: [this.path+"view.html"],
            css: [this.path+"style.css"]
        }, {bind: {lp: this.property.designer.lp, isProcess: this.isProcess, isField: this.isField}, module: this}, () => {
            this.loadConfig();
            // this.loadPermission();
        });
	},
	loadConfig: function(){
		//加载表单默认配值
		this.loadDefaultConfig();
		
		if (this.isProcess && this.isField){
			//获取当前字段的流程数据权限配置
			const path = this._getModulePath();

			o2.Actions.load('x_processplatform_assemble_designer').ItemAccessAction.listWithPath(path).then((json)=>{
				if (json.data && json.data.length){
					//如果已有配置，列出流程数据权限配置
					this.loadProcessConfig(json.data);
				}
			});
		}
		
	},


	loadDefaultConfig: function(){
		o2.xhr_get(this.path+"item.html", ({responseText})=>{
			this.itemHTML = responseText;
			this.bodyNode.loadHtmlText(this.itemHTML, {bind: {data: this.formDefaultConfig, lp: this.property.designer.lp, isCms: this.isCms, isProcess: this.isProcess},  module: this});
        });
	},

	loadProcessConfig: function(list){
		const application = this.property.form.data.json.application;
		o2.Actions.load('x_processplatform_assemble_designer').ProcessAction.listWithApplication(application).then((json)=>{
			this.processList = json.data;
			o2.xhr_get(this.path+"process.html", ({responseText})=>{
				this.processHTML = responseText;
				list.forEach((data)=>{
					const process = this.processList.find((p)=>{
						return p.id === data.itemCategoryId;
					});
					if (process) data.processName = process.name;
					if (data.readerList && data.readerList.length){
						data.readByOrg = true;
					}
					if (data.readActivityList && data.readActivityList.length){
						data.readByActivity = true;
					}

					if (data.editorList && data.editorList.length){
						data.editByOrg = true;
					}
					if (data.editActivityList && data.editActivityList.length){
						data.editByActivity = true;
					}
					this.bodyNode.loadHtmlText(this.processHTML, {bind: {data: data, lp: this.property.designer.lp},  module: this});
				});
			});
		});
	},

	loadScriptEditor: function(name, e, d, item){
		MWF.require("MWF.widget.ScriptArea", () => {
			const _self = this;
			this.editor = new MWF.widget.ScriptArea(e.target, {
				"isbind": false,
				"mode": "javascript",
				"maxObj": this.property.designer.content,
				"maxPosition": "absolute",
				"style": "v10",
				"onChange": function () {
					item.data[name] = this.getData();
				},
			});
			this.editor.load({ code: item.data[name] });
		});
	},

	changeCheck: function(name, e, d, item){
		const show = e.target.checked ? 'block' : 'none';
		e.target.parentElement.nextElementSibling.style.display = show;
		item.data[name] = e.target.checked ? ['true'] : null;
	},
	checkHideCannotRead: function(name, e, d, item){
		item.data[name] = e.target.value;
	},
	selectActivitys: function(name, type, e, d, item){
		const process = (type==='process') ? item.data.itemCategoryId : ''
        new o2.O2Selector(this.property.designer.content, {
            "title": this.property.designer.lp.selectPermissions,
            "values": e.target.value,
            "type": 'activity',
            "application": this.property.form.data.json.application,
			"process": process,
            "style": "v10",
            "count": 0,
            "onComplete": function (items) {
                if (items.length) {
                    const v = items.map(item=>item.data.uniqueName || item.data.distinguishedName);
					item.data[name] =  items.map(item=>{
						const {unique, id, name, alias, type, process} = item.data
						return {unique, id, name, alias, type, process};
					});
                    e.target.value = v;
                }else{
                    e.target.value = ''
                    item.data[name] = [];
                }
				if ((type==='process')){
					//需要直接保存
					this.saveProcessConfig(item.data)
				}
            }.bind(this)
        });
	},
	changeActivitys: function(name, e, d, item){
		item.data[name] = e.currentTarget.value;
	},

	saveProcessConfig: function(data){
        return o2.Actions.load("x_processplatform_assemble_designer").ItemAccessAction.save(data);
    },

	selectOrgs: function(name, type, e, d, item){
		new o2.O2Selector(this.property.designer.content, {
            "title": this.property.designer.lp.selectPermissions,
            "values": e.target.value,
            "types": ['unit', 'group', 'identity', 'role'],
            "style": "v10",
            "count": 0,
            "onComplete": function (items) {
                if (items.length) {
                    const v = items.map(item=>item.data.uniqueName || item.data.distinguishedName);
					item.data[name] =  v;
                    e.target.value = v;
                    
                }else{
                    e.target.value = '';
                    item.data[name] = [];
                }
				if ((type==='process')){
					//需要直接保存
					this.saveProcessConfig(item.data)
				}
            }.bind(this)
        });
	},

	getActivityDn: function(data){
		return data.map((d)=>{
			debugger;
			const {name, id, unique, process, type} = d;
			return name+'@'+id+'@'+unique+'@'+process+'@'+type+'@A';
		});
	},

	getProcessList: function(cb){
		if (this.processList){
			cb?.(this.processList);
		}else{
			const application = this.property.form.data.json.application;
			o2.Actions.load('x_processplatform_assemble_designer').ProcessAction.listWithApplication(application).then((json)=>{
				this.processList = json.data;
				cb?.(this.processList);
			});
		}
	},
	_getNewPathData: function(process, name){
        return {
            itemCategoryId: process,
			processName: name,
            path: this._getModulePath(),
            readerList: null,
            readActivityList: null,
            editActivityList: null,
            editorList: null
        }
    },
	_getModulePath: function(){
		const module = this.property.module;
		let id = module.json.id;
		let m = module.parentContainer;
		while(m){
			if (m.moduleName === "datatable"){
				id = `${m.json.id}.data.*.${id}`;
			}
			if (m.moduleName === "datatemplate"){
				id = `${m.json.id}.*.${id}`;
			}
			m = m.parentContainer;
		}
		return id;
	},

	addPermissionsProcess: function(e){
		this.getProcessList((processList)=>{
			const content = new Element('div');
			const html = `
			<oo-select>
				${processList.map(p=>'<oo-option value="'+p.id+'" text="'+p.name+'"></oo-option>').join()}
			</oo-select>`
        	content.set("html", html); 

			$OOUI.dialog(this.property.designer.lp.addFormPermissionsProcess, content, this.property.designer.content, {
				positionNode: this.property.propertyNode,
				events: {
					ok: (e)=>{
						const select = content.querySelector('oo-select');
						const data = this._getNewPathData(select.value, select.text);
						this.saveProcessConfig(data).then(()=>{
							o2.xhr_get(this.path+"process.html", ({responseText})=>{
								this.processHTML = responseText;
								this.bodyNode.loadHtmlText(this.processHTML, {bind: {data: data, lp: this.property.designer.lp},  module: this});
								this.bodyNode.lastElementChild.scrollIntoView({behavior: "smooth", block: "end", inline: "nearest"});
							});
						});
						e.target.close();
					}
				}
			});
		});
	},

	deleteItem: function(e, d, item){
		debugger;
		const info = this.property.designer.lp.deletePathProcessInfo.replace('{process}', item.data.processName);
        const line = e.target.closest('.fieldPermissions-item');
        line.addClass('deleting');
        $OOUI.confirm.warn(this.property.designer.lp.deletePathTitle, info, this.property.propertyNode, e.target).then(({ dlg, status }) => {
            line.removeClass('deleting');
            if (status === 'ok') {
                if (item.data.id){
                    o2.Actions.load("x_processplatform_assemble_designer").ItemAccessAction.deleteWithProcessWithPath(item.data.itemCategoryId, item.data.path).then(()=>{
                        line.remove();
                    });
                }else{
                    line.remove();
                }
            }
            dlg.close();
        });
	}
	
});

