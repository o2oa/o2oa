MWF.xApplication.designermanager.options.multitask = false;
var o2DM = MWF.xApplication.designermanager;
MWF.xDesktop.requireApp("designermanager", "config", null, false);
o2DM.apps = [];
o2DM.appMap = {};
o2DM.currentApp = null;
MWF.xApplication.designermanager.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "designermanager",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"title": MWF.xApplication.designermanager.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.designermanager.LP;
	},
	loadApplication: function(callback){
		o2DM._openApp = this.openApp.bind(this);
		layout.openApplication = (e, appNames, options, statusObj)=>{
			o2DM._openApp(appNames, options, statusObj);
		};

		var url = this.path+this.options.style+"/main.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
			this.loadNav();
		}.bind(this));
	},
	loadNav: function(){
		this.nav = new o2DM.Nav(this);
		this.nav.load();
	},
	// loadNav: function(){
	// 	o2.Actions.load("x_processplatform_assemble_surface").TaskAction.listMyPaging(1,5, function(json){
	// 		this.nav.loadHtml(this.path+this.options.style+"/taskView.html", {"bind": {"lp": this.lp, "data": json.data}, "module": this}, function(){
	// 			this.doSomething();
	// 		}.bind(this));
	// 	}.bind(this));
	// },
	openApp: function(path, options, status, callback){
		this._openApp(path, options, status, callback);
	},
	_openApp: function(path, options, status, callback){
		debugger;
		var node = new Element('div.app-content').inject(this.applicationNode);
		var clazz = MWF.xApplication;
		if( o2.typeOf(path) !== "string" )return;
		path.split(".").each(function (a) {
			clazz[a] = clazz[a] || {};
			clazz = clazz[a];
		});
		clazz.options = clazz.options || {};

		var _load = function () {
			if( clazz.Main ){
				var opt = options || {};
				opt.embededParent = node;
				var component = new clazz.Main(this.desktop, opt);

				component.appId = opt.appId ?
					opt.appId :
					(clazz.options.multitask ? path + "-" + new o2.widget.UUID() : path);
				component.options.appId = component.appId;
				// o2DM.appMap[component.appId] = {
				// 	tag: this.createTag(component),
				// 	app: component
				// };

				component.status = status;

				var tag = this.createTag(component, node);

				//this.fireEvent("queryLoadApplication", component);
				component.load();
				component.setEventTarget(this);
				var _self = this;
				// this.component.refresh = function () {
				//     if( layout.inBrowser ){
				//         window.location.reload();
				//     }else{
				//         _self.form.app.refresh();
				//     }
				// };

				component._tag = tag;
				component._container = node;

				component.setTitle = function (str) {
					// tag.setAttribute('text', str);
					// tag._elements.text.textContent = str;
					tag.innerHTML = str;
					tag.setAttribute('title', str + ((component.appId) ? "-" + component.appId : ""));
					component.options.title = str;
				};

				component.dm_close = ()=>{
					if(o2DM.currentApp === component){
						o2DM.currentApp = null;
						var index = o2DM.apps.indexOf(component);
						var lastApp = index > 0 ? o2DM.apps[index-1] : o2DM.apps[0];
						if(lastApp){
							lastApp._tag.setAttribute('type', 'current');
							lastApp._container.show();
							o2DM.currentApp = lastApp;
						}else{
							o2DM.currentApp = null;
						}
					}
					o2DM.apps.erase(component);
					delete o2DM.appMap[component.appId];
					tag.remove();
					component.close();
					node.destroy();
				};

				component.dm_hide = ()=>{
					component._tag.setAttribute('type', 'default');
					component._container.hide();
				};

				component.dm_active = ()=>{
					if(o2DM.currentApp === component) return;
					o2DM.currentApp?.dm_hide();
					tag.setAttribute('type', 'current');
					node.show();
					o2DM.currentApp = component;
					this.addToHistory( component );
				};

				o2DM.appMap[component.appId] = component;
				o2DM.apps.push(component);

				debugger;

				component.dm_active();
			}else{
				console.log('应用未找到：'+path, 'error');
			}
			this.loaded = true;
			if(callback)callback();
		}.bind(this);

		MWF.xDesktop.requireApp(path, "lp."+o2.language, null, false);
		MWF.xDesktop.requireApp(path, "Main", null, false);
		if (clazz.loading && clazz.loading.then){
			clazz.loading.then(function(){
				_load();
			});
		}else{
			_load();
		}
	},
	createTag: function (component){
		var tag = new Element('oo-tag', {
			text: component.options.title,
			close: 'on',
			type: 'current'
		}).inject(this.taskBar);
		tag.component = component;
		tag.addEventListener("close", function (e) {
			component.dm_close();
			e.stopPropagation();
		});
		tag.addEventListener("click", function (e) {
			component.dm_active();
		});
		return tag;
	},
	addToHistory: function(component){
		const status = component.recordStatus && component.recordStatus();
		if( status && status.id ){
			o2.UD.getDataJson(o2DM._HISTORY_DESIGNER_NAME, (items)=>{
				(items) => {
					var list = o2DM._sort(items || [], 'time', true).filter((item) => {
						return item.id !== path.id;
					});
					const config = o2DM._findConfig(component.options.name);
					list.unshift({
						isHistory: true,
						_appType: config?._appType,
						_type: config?._type || 'designer',
						componentName: component.options.name,
						id: status.id,
						status: status,
						time: new Date().getTime(),
						timeString: new Date().format('db')
					});
					(list.length > o2DM._HISTORY_DESIGNER_MAX_COUNT) && (list.length = o2DM._HISTORY_DESIGNER_MAX_COUNT);
					o2.UD.putData(o2DM._HISTORY_DESIGNER_NAME, list);
				};
			});
		}
	}
});

o2DM.Nav = new Class({
	initialize: function (app){
		this.app = app;
		this.oonav = app.oonav;
	},
	load: function (){
		this.oonav.addEventListener('select', (e)=>{
			debugger;
			this.handleClick(e, e.detail.data)
		});
		this.oonav.addEventListener('expand', (e)=>{
			this.handleExpand(e, e.detail.data)
		});
		var template = Array.clone(o2DM._config.children);
		Promise.resolve(this.getList( template )).then((data)=>{
			this.parseCategory();
			this.oonav.setMenu(data);
		});
	},
	getList: function ( template, appid ) {
		var list = template;
		this.categories = [];
		//var appid = this.getAppid();
		if( list.length === 1 && list[0].listAction){
			return list[0].listAction( appid ).then((data)=>{
				return data.map(d=>{
					const tmplt = Object.clone(list[0]);
					switch ( tmplt._type ){
						case 'app':
							d.children = tmplt.children.map((child)=>{
								child.appid = d.id;
								//child._parent = d;
								return child;
							});
							d.img = d.icon ? `data:image/png;base64,${d.icon}` : d.defaultIcon;
							d.icon = null;
							// d.children.push({_type: 'separator'});
							// d.children.push(...o2DM._appTools);
							break;
						case 'designer':
							d.appid = appid;
							// d.children = [...o2DM._designerTools];
							break;
					}
					d.text = d.name;
					if(d.ooicon) d.icon = d.ooicon;
					d.handleClick = ()=>{
						tmplt.handleClick(d, appid);
					};
					if( tmplt.categorized ){
						(!d.category || d.category==='未分类') && (d.category = o2DM._UNCATEGORIZED);
						!this.categories.includes( d.category ) && this.categories.push( d.category );
					}
					d._type = tmplt._type;
					d._config = tmplt;
					//d._parent = data;
					this._checkCreate(d);
					this._checkTools(d);
					return d;
				});
			});
		}else{
			return list.map(child=>{
				if(child.ooicon) child.icon = child.ooicon;
				if(!child.id){
					switch (child._type){
						case 'app-category':
							child.selectable = 'yes';
							child.id = `${child.componentName}`; break;
						case 'designer-category':
							child.id = `${child.componentName}.${appid}`;
							break;
					}
				}
				child.text = child.name;
				//child._parent = list;
				this._checkCreate(child);
				this._checkTools(child);
				return child;
			});
		}
	},
	parseCategory: function (e){
		var hasUncategorized = this.categories.includes(o2DM._UNCATEGORIZED);
		hasUncategorized && (this.categories = this.categories.erase( o2DM._UNCATEGORIZED ));
		if( this.categories.length > 0 ){
			this.categories = o2DM._sort(this.categories,'').map(c=>{ return {value: c, label: c}; });
			this.categories.unshift({value: o2DM._ALL, label: '全部分类'});
			hasUncategorized && this.categories.push({value: o2DM._UNCATEGORIZED, label: '未分类'});
		}
	},
	checkCondition: function (e, data){
		debugger;
		if( !data.condition ){
			return true;
		}
		var flag = true;
		switch(data._type){
			case 'app-tool':
				flag = data.condition(data, this.getAppdata(data));
				break;
			case 'designer-tool':
				flag = data.condition(this.getDesignerData(data), this.getAppdata(data));
				break;
		}
		!flag && e.currentTarget.addClass('hide');
		return flag;
	},
	getDesignerData : function(data){
		return this.oonav.getItem(data.id)?.data;
	},
	getAppdata: function(data){
		return this.oonav.getItem(data.appid)?.data;
	},
	getAppid: function(data){
		return this.getAppdata(data)?.id;
	},
	getAppname: function(data){
		return this.getAppdata(data)?.name;
	},
	handleExpand: function (e, data){
		if(!data._type || data.loaded){
			return;
		}
		data.loaded = true;
		if( !this.checkCondition(e, data) ){
			return;
		}
		if( data.children && data.children.length > 0 ){
			var template = Array.clone(data.children);
			Promise.resolve(this.getList( template, data.appid || data.id )).then((children)=>{
				this.parseCategory();
				data.children = children || [];
			});
		}
	},
	_checkTools: function (data){
		if(data._type === 'app' || data._type === 'designer'){
			const node = new Element('div.slot.ooicon-point3',{
				slot: `${data.id}-inner`
			}).inject(this.oonav);
			node.addEventListener('click', (e)=>{
				e.stopPropagation();
				if(!node.menu){
					node.menu = new $OOUI.Menu(node, {
						area: this.app.content,
						styles: { width: '8.75rem' },
						items: (data._type === 'app' ? o2DM._appTools : o2DM._designerTools).map((tool)=>{
							tool.label = tool.name;
							tool.icon = tool.ooicon;
							return tool;
						})
					});
					node.menu.show();
					node.menu.menu.addEventListener('command', (e)=>{
						const d = e.detail;
						if(d._type === 'designer-tool'){
							d.handleClick(data, this.getAppdata(data));
						}else{
							d.handleClick(data, data.id, data);
						}
					});
				}
			});
		}
	},
	_checkCreate: function (data){
		if(data.handleCreate){
			new Element('div.slot.ooicon-create',{
				slot: `${data.id}-inner`,
				events: {
					click: (e)=>{
						data.handleCreate(data.appid || this.getAppid(data), this.getAppname(data));
						e.stopPropagation();
					}
				}
			}).inject(this.oonav);
		}
	},
	// handleCreate: function (e, data){
	// 	data.handleCreate(data.appid || this.getAppid(data), this.getAppname(data));
	// 	e.stopPropagation();
	// },
	handleClick: function (e, data){
		if( data._type === 'designer-tool' ){
			var d = this.getDesignerData(data);
			data.handleClick(d, d.isHistory ? d.appData : this.getAppdata(data));
		}else{
			data.handleClick(data, data.appid || this.getAppid(data), this.getAppdata(data));
		}
	}
});
