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
		this.addEvents();
		if(!this.desktop.appCurrentList)this.desktop.appCurrentList = [];
		o2DM.openApp = this.openApp.bind(this);
		layout.openApplication = (e, appNames, options, statusObj)=>{
			o2DM.openApp(appNames, options, statusObj);
		};

		var url = this.path+this.options.style+"/main.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
			this.loadNav();
			this.restoreApps();
		}.bind(this));
	},
	loadNav: function(){
		this.designerNav = new o2DM.DesingerNav(this);
		this.designerNav.load();
		this.nav = this.designerNav;

		this.collectionNav = new o2DM.CollectionNav(this);
		this.collectionNav.load();
	},
	_getAppId: function(path, options, status, clazz){
		var opt = options || {};
		var sta = status || {};
		let appId, id;
		if(clazz.options.multitask){
			id = opt.id || sta.id || sta.application || sta.column;
			if(opt.appId || sta.appId){
				appId = opt.appId || sta.appId;
			}else if( id && path ){
				appId = `${path}${id}`;
			}else{
				appId = path + "-" + new o2.widget.UUID();
			}
		}else{
			appId = path;
		}
		return appId;
	},
	_loadClazz: function (path, callback){
		var clazz = MWF.xApplication;
		if( o2.typeOf(path) !== "string" )return;
		path.split(".").each(function (a) {
			clazz[a] = clazz[a] || {};
			clazz = clazz[a];
		});
		clazz.options = clazz.options || {};

		MWF.xDesktop.requireApp(path, "lp."+o2.language, null, false);
		MWF.xDesktop.requireApp(path, "Main", null, false);
		if (clazz.loading && clazz.loading.then){
			clazz.loading.then(function(){
				if(callback)callback(clazz);
			});
		}else{
			if(callback)callback(clazz);
		}
	},
	openApp: function(path, options, status, callback, param){
		this._loadClazz(path, (clazz)=>{
			var appId = this._getAppId(path, options, status, clazz);
			if(appId && o2DM.appMap[appId]){
				var taskItem = o2DM.appMap[appId].taskItem;
				taskItem.active();
				if(callback)callback();
			}else{
				this._openApp(path, options, status, callback, param);
			}
		})
	},
	_openApp: function(path, options, status, callback, param){
		this._loadClazz(path, (clazz)=>{
			if( clazz.Main ){
				var appId = this._getAppId(path, options, status, clazz);

				var node = new Element('div.app-content');
				node.inject(this.applicationNode);

				var opt = options || {};
				opt.taskItem = null;
				opt.embededParent = node;
				var app = new clazz.Main(this.desktop, opt);

				app.appId = appId;

				console.log(app.appId, opt, status, app);

				app.options.appId = app.appId;

				app.status = status;

				app.desktop = this.desktop;

				var taskItem;
				if( param?.taskItem ){
					taskItem = param.taskItem;
					taskItem.node = node;
					taskItem.clazz = clazz;
				}else{
					taskItem = new o2DM.TaskItem(this, app, node, param?.tag, clazz)
				}

				app.load();
				app.setEventTarget(this);

				o2DM.appMap[app.appId] = app;
				param?.index > -1 ?
					(o2DM.apps[param.index] = app) :
					o2DM.apps.push(app);

				app.loaded = true;

				if(callback)callback(app, node);

				taskItem.active();
			}else{
				console.log('应用未找到：'+path, 'error');
			}
		});
	},
	searchDesigner: function (){
		o2DM.openApp('FindDesigner');
	},
	addToHistory: function(app, title){
		let status;
		try{
			status = app.recordStatus && app.recordStatus();
		}catch(e){
			setTimeout( ()=>{
				this.addToHistory(app, title);
			},500);
			return;
		}
		if( status && status.id ){
			o2.UD.getDataJson(o2DM._HISTORY_DESIGNER_NAME, (items)=>{
				var list = o2DM._sort(items || [], 'time', true).filter((item) => {
					return item.id !== status.id;
				});
				const config = o2DM._findConfig(app.options.name);
				const obj = {
					isHistory: true,
					_appType: config?._appType,
					_type: config?._type || 'designer',
					componentName: app.options.name,
					title: title || app.lp.title,
					id: status.id,
					status: status,
					time: new Date().getTime(),
					timeString: new Date().format('db')
				};
				console.log(obj);
				list.unshift(obj);
				(list.length > o2DM._HISTORY_DESIGNER_MAX_COUNT) && (list.length = o2DM._HISTORY_DESIGNER_MAX_COUNT);
				o2.UD.putData(o2DM._HISTORY_DESIGNER_NAME, list);
			});
		}
	},
	openHistory: function (e){
		if(this.historyMenu){
			this.historyMenu.destroy();
		}
		o2.UD.getDataJson(o2DM._HISTORY_DESIGNER_NAME, (items) => {
			const menu = new $OOUI.Menu(e.target, {
				area: this.content,
				styles: { width: '8.75rem' },
				items: o2DM._sort(items || [], 'time', true).map((item) => {
					return {
						...item,
						_pinyin: o2DM._toPY(item),
						icon: o2DM._ooiconMap[item.componentName] || '',
						name: o2DM._appNameMap[item.componentName] + ' ' + item.componentName,
						label: item.title || o2DM._appNameMap[item.componentName] + ' ' + item.componentName
					};
				})
			});
			menu.show();
			menu.menu.addEventListener('command', (e)=>{
				const d = e.detail;
				o2DM.openApp(d.componentName, null, d.status || { id: d.id, application: {id: d.appId} });
			});
			this.historyMenu = menu;
		});
	},
	restoreApps: function (){
		o2.UD.getDataJson("designers", (json)=>{
			Object.keys(json.appMap).forEach(appId=>{
				const obj = json.appMap[appId];
				const app = {
					options: obj,
					status: obj.status,
					appId: appId
				};
				new o2DM.TaskItem(this, app);
				o2DM.appMap[appId] = app;
				o2DM.apps.push(app);
			});
			debugger;
			if(json.currentApp && o2DM.appMap[json.currentApp]){
				o2DM.appMap[json.currentApp].taskItem.active();
			}
		});
	},
	addEvents: function (){
		window.onbeforeunload = (e)=>{
			if (!this.isLogout){
				this.recordDesktopStatus();

				e = e || window.event;
				e.returnValue = '如果关闭或刷新当前页面，未保存的内容会丢失，请确定您的操作';
				return '如果关闭或刷新当前页面，未保存的内容会丢失，请确定您的操作';
			}
		};
	},
	recordDesktopStatus: function(callback){
		Object.each(o2DM.appMap, (app, id)=>{
			if (!app.options.desktopReload){
				app.taskItem?.close();
			}
		});
		var status = this.getLayoutStatusData();
		console.log(status);

		try{
			o2.UD.putData("designers", status, function(){
				if (callback) callback();
			});
		}catch(e){}

	},
	getLayoutStatusData: function(){
		var status = {
			"currentApp": (o2DM.currentApp) ? o2DM.currentApp.appId : "",
			"appMap": {}
		};
		Object.each(o2DM.appMap, (app, id)=>{
			var appStatus = this.getAppStatusData(app, id);
			if (appStatus) status.appMap[id] = appStatus;
		});
		return status;
	},
	getAppStatusData: function(app){
		debugger;
		var appStatus;
		if (app.window){
			if (app.options.desktopReload){
				appStatus = {
					"isStatus": true,
					"desktopReload": true,
					"appId": app.appId,
					"componentName": app.options.name,
					"name": app.options.name,
					"style": app.options.style,
					"title": app.options.title,
					"window": {}
				};
				if (app.recordStatus){
					appStatus.status = app.recordStatus();
				}
			}
		}else{
			if (app.options){
				app.options.isStatus = true;
				appStatus = app.options;
			}
		}
		return appStatus;
	},
	locateToCurrent: function (e){
		this.nav.locateToCurrent(e);
	},
	collapseNav: function (e){
		this.nav.collapseLevel1(e);
	},
	createRefreshNode: function(){
		this.refreshNode = new Element("div.o2-designer-refresh-node.icon-refresh").inject(this.content);
		this.refreshNode.set("morph", {
			"duration": 100,
			"transition": Fx.Transitions.Quart.easeOut
		});
		this.refreshNode.addEvent("click", function(){
			if (o2DM.currentApp) o2DM.currentApp.refresh();
			this.hideRefresh();
		}.bind(this));
	},
	showRefresh: function(){
		if( !o2DM.currentApp ){
			return;
		}
		if (!this.refreshNodeShow){
			if (!this.refreshNode) this.createRefreshNode();
			var size = this.taskbar.getSize();
			var nodeSize = this.refreshNode.getSize();
			var top = size.y;
			var left = size.x/2-nodeSize.x/2;
			this.refreshNode.setStyles({
				"left": left,
				"top": 0-nodeSize.y,
				"opacity": 0
			});

			this.refreshNode.morph({
				"top": top,
				"left": left,
				"opacity": 0.9
			});
			this.refreshNodeShow = true;

			this.refreshTimeoutId = window.setTimeout(function(){
				this.hideRefresh();
			}.bind(this), 2000);
		}
	},
	hideRefresh: function(){
		if (this.refreshNodeShow){
			if (this.refreshNode){
				var size = this.taskbar.getSize();
				var nodeSize = this.refreshNode.getSize();
				var top = size.y;
				var left = size.x/2-nodeSize.x/2;
				this.refreshNode.morph({
					"top": 0-nodeSize.y,
					"left": left,
					"opacity": 0
				});
				window.setTimeout(function(){
					this.refreshNodeShow = false;
				}.bind(this), 100);
			}
		}
		if (this.refreshTimeoutId){
			window.clearTimeout(this.refreshTimeoutId);
			this.refreshTimeoutId = "";
		}
	},
	toDesignerNav: function (e){
		this.nav = this.designerNav;
		this.tabDesigner.addClass('current');
		this.tabCollection.removeClass('current');
		this.oonavDesigner.removeClass('hide');
		this.oonavCollection.addClass('hide');
	},
	toCollectionNav: function (e){
		this.nav = this.collectionNav;
		this.tabDesigner.removeClass('current');
		this.tabCollection.addClass('current');
		this.oonavDesigner.addClass('hide');
		this.oonavCollection.removeClass('hide');
	}
});

o2DM.DesingerNav = new Class({
	initialize: function (main){
		this.main = main;
		this.oonav = main.oonavDesigner;
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
	getList: function ( template, appId ) {
		var list = template;
		this.categories = [];
		//var appId = this.getAppId();
		if( list.length === 1 && list[0].listAction){
			return list[0].listAction( appId ).then((data)=>{
				return data.map(d=>{
					const tmplt = Object.clone(list[0]);
					switch ( tmplt._type ){
						case 'app':
							d.children = tmplt.children.map((child)=>{
								child.appId = d.id;
								//child._parent = d;
								return child;
							});
							d.img = d.icon ? `data:image/png;base64,${d.icon}` : d.defaultIcon;
							d.icon = null;
							// d.children.push({_type: 'separator'});
							// d.children.push(...o2DM._appTools);
							break;
						case 'designer':
							d.appId = appId;
							// d.children = [...o2DM._designerTools];
							break;
					}
					d.text = d.name;
					if(d.ooicon) d.icon = d.ooicon;
					d.handleClick = ()=>{
						tmplt.handleClick(d, appId);
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
							child.id = appId === 'service.ServiceManager' ?
								`${child.componentName}` :
								`${child.componentName}.${appId}`;
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
	checkToolCondition: function (e, tool, data){
		debugger;
		if( !tool.condition ){
			return true;
		}
		var flag = true;
		switch(tool._type){
			case 'app-tool':
				flag = tool.condition(data, data);
				break;
			case 'designer-tool':
				flag = tool.condition(data, this.getAppdata(data));
				break;
		}
		!flag && e?.currentTarget?.addClass('hide');
		return flag;
	},
	getDesignerData : function(data){
		return this.oonav.getItem(data.id)?.data;
	},
	getAppdata: function(data){
		return this.oonav.getItem(data.appId)?.data;
	},
	getAppId: function(data){
		return this.getAppdata(data)?.id;
	},
	getAppname: function(data){
		return this.getAppdata(data)?.name;
	},
	handleExpand: function (e, data){
		if(!data._type || data.loaded){
			if(this.afterExpand){
				this.afterExpand();
			}
			return;
		}
		data.loaded = true;
		if( data.children && data.children.length > 0 ){
			var template = Array.clone(data.children);
			Promise.resolve(this.getList( template, data.appId || data.id )).then((children)=>{
				this.parseCategory();

				data.children = children || [];

				this._setToolEvents(data);

				if(this.afterExpand){
					this.afterExpand();
				}
			});
		}
	},
	_setToolEvents: function (data){
		data.children.map(child=>{
			const itemEl = this.oonav.getItem(child.id).itemEl;
			itemEl.addEventListener('mouseenter',(e)=>{
				this.oonav.querySelectorAll(`div[slot="${child.id}-inner"]`).forEach(
					slot=>slot.removeClass('hide')
				);
			});
			itemEl.addEventListener('mouseleave',(e)=>{
				this.oonav.querySelectorAll(`div[slot="${child.id}-inner"]`).forEach(
					slot=>slot.addClass('hide')
				);
			});
		});
	},
	_checkTools: function (data){
		if(data._type === 'app' || data._type === 'designer'){
			const node = new Element('div.slot.hide.ooicon-point3',{
				slot: `${data.id}-inner`
			}).inject(this.oonav);
			node.addEventListener('click', (e)=>{
				e.stopPropagation();
				if(!node.menu){
					node.menu = new $OOUI.Menu(node, {
						area: this.main.content,
						styles: { width: '8.75rem' },
						items: (data._type === 'app' ? o2DM._appTools : o2DM._designerTools).filter(tool=>{
							return this.checkToolCondition(null, tool, data);
						}).map((tool)=>{
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
			new Element('div.slot.hide.ooicon-create',{
				slot: `${data.id}-inner`,
				events: {
					click: (e)=>{
						data.handleCreate(data.appId || this.getAppId(data), this.getAppname(data));
						e.stopPropagation();
					}
				}
			}).inject(this.oonav);
		}
	},
	// handleCreate: function (e, data){
	// 	data.handleCreate(data.appId || this.getAppId(data), this.getAppname(data));
	// 	e.stopPropagation();
	// },
	handleClick: function (e, data){
		if( data._type === 'designer-tool' ){
			var d = this.getDesignerData(data);
			data.handleClick(d, d.isHistory ? d.appData : this.getAppdata(data));
		}else{
			data.handleClick(data, data.appId || this.getAppId(data), this.getAppdata(data));
		}
	},
	locateToCurrent: function (e){
		const app = o2DM.currentApp;
		if( app ){
			this._expandToApp(app, ()=>{
				let item = this.oonav.getItem(app.options.id || app.options.name);
				if(item){
					item.itemEl.scrollIntoView({
						behavior: 'smooth', block: 'end', inline: 'end'
					});
					item.select();
				}
				this.afterExpand = null;
			});
		}
	},
	_expandToApp: function (app, callback){
		const key = app.options.id || app.options.name;
		let item = this.oonav.getItem(key);
		if(!item){
			debugger;
			const configs = o2DM._findAllParentConfigs(app.options.name) || [];
			const id = app.options.id;
			const appId = app.application ? app.application.id : '';

			const getKey = (config)=>{
				switch (config._type){
					case 'app-category':
						return config.componentName;
					case 'designer-category':
						return config.componentName.startsWith('service.') ?
							`${config.componentName}` :
							`${config.componentName}.${appId}`;
					case 'app':
						return appId || id;
					case 'designer':
						return id;
				}
			};

			const doExpand = ()=>{
				if( configs.length > 0 ){
					const config = configs.shift();
					const item = this.oonav.getItem(getKey(config));
					if(item){
						this.afterExpand = ()=>{
							configs.length === 0 ? callback() : doExpand();
						};
						item.expand(false);
					}
				}
			};

			doExpand();
		}else{
			let parentItem = item.parentItem;
			const parents = [];
			while(parentItem){
				parents.push(parentItem);
				parentItem = parentItem.parentItem;
			}
			parents.reverse().forEach(item=>item.expand(false));
			callback();
		}
	},
	expandSelected: function (e){

	},
	collapseLevel1: function (e){
		this.oonav.data.children.forEach(child=>{
			child.expanded = false;
		});
	}
});

o2DM.CollectionNav = new Class({
	Extends: o2DM.DesingerNav,
	initialize: function (main){
		this.main = main;
		this.oonav = main.oonavCollection;
	},
});

o2DM.TaskItem = new Class({
	initialize: function (main, app, node, tag, clazz){
		this.app = app;
		this.main = main;
		this.node = node;
		this.desktop = main.desktop;
		this.taskbar = main.taskbar;
		this.clazz = clazz;

		//??
		//this.node = new Element('div.app-content').inject(this.main.applicationNode);

		this.node?.inject(this.main.applicationNode);

		this.bindThisToApp();
		this.tag = tag || this.createTag('default');
		this.bindAppToTag();
	},
	isApploaded: function (){
		const app = this.app;
		return !app.options.isStatus;
	},
	bindThisToApp: function(){
		var app = this.app;
		app.taskItem = this;
		app.refresh = ()=> {
			this.refresh();
		};

		app.setTitle =  (str)=> {
			this.setTitle(str);
		};
	},
	setTitle: function (str){
		const {app, tag, clazz} = this;
		tag.innerHTML = str;
		tag.setAttribute('title', str + ((app.appId) ? "-" + app.appId : ""));
		app.options.title = str;
		if(clazz?.options.multitask){
			this.main.addToHistory(app, str);
		}
	},
	refresh: function (){
		const app = this.app;
		if(o2DM.currentApp === app){
			o2DM.currentApp = null;
		}
		if(this.desktop.currentApp === app){
			this.desktop.currentApp = null;
		}
		var appStatus ={
			"id": app.options.id,
			"componentName": app.options.name,
			"name": app.options.name,
			"style": app.options.style,
			"appId": app.appId
		};
		var status = (app.recordStatus) ? app.recordStatus() : null;
		var index = o2DM.apps.indexOf(app);
		var tag = this.createTag('default').inject(this.tag, 'before');
		this.close(true);
		o2DM.openApp(appStatus.componentName, appStatus, status, null, {
			isRefresh: true,
			index: index,
			tag: tag
		});
	},
	close: function (ignore){
		const app = this.app;
		if(this.desktop.currentApp === app){
			this.desktop.currentApp = null;
		}
		if(o2DM.currentApp === app){
			o2DM.currentApp = null;
			if(!ignore ){
				var index = o2DM.apps.indexOf(app);
				let lastApp;
				if( index === 0 ){
					lastApp = o2DM.apps[index+1];
				}else if(index > 0){
					lastApp = o2DM.apps[index-1];
				}else if(index === -1){
					if(o2DM.apps.length > 0){
						lastApp = o2DM.apps[0];
					}
				}
				if(lastApp){
					lastApp.taskItem?.active();
				}
			}
		}
		o2DM.apps.erase(app);
		delete o2DM.appMap[app.appId];
		this.tag.remove();
		this.node?.destroy();
		app.close && app.close();
	},
	hide: function (){
		var {app, tag, node} = this;
		tag.setAttribute('type', 'default');
		tag.oomenu?.hide();
		node?.hide();
		if(this.isApploaded()){
			app.setUncurrent();
		}
	},
	active: function (){
		var app = this.app;
		if( app.options.isStatus ){
			//const appId = app.options.appId;
			const index = o2DM.apps.indexOf(app);
			this.main._openApp(app.options.componentName, app.options, app.status, (newApp, node)=>{
				newApp.options.isStatus = false;
				this.node = node;
				this.app = newApp;
				// o2DM.apps[index] = newApp;
				// o2DM.appMap[appId] = newApp;
				this.bindThisToApp();
			}, {
				taskItem: this,
				index: index
			});
		}else{
			var {tag, node, clazz} = this;
			if(o2DM.currentApp === app) return;
			o2DM.currentApp?.taskItem.hide();
			tag.setAttribute('type', 'current');
			tag.oomenu?.hide();
			node?.show();
			app.setCurrent();
			o2DM.currentApp = app;
			if(!clazz.options.multitask){
				this.main.addToHistory( app );
			}
		}
	},
	createTag: function (type){
		return new Element('oo-tag', {
			text: this.app?.options?.title || '',
			close: 'on',
			menu: 'on',
			type: type || 'current'
		}).inject(this.taskbar);
	},
	bindAppToTag: function (){
		const {tag, app} = this;
		tag.taskItem = this;
		tag.app = app;
		tag.addEventListener("close",  (e)=>{
			this.close();
			e.stopPropagation();
		});
		tag.addEventListener("click", (e)=>{
			this.active();
		});
		tag.addEventListener("menu", (e)=>{
			if(!tag.oomenu){
				tag.oomenu = new $OOUI.Menu(tag._elements.menu, {
					area: this.content,
					styles: { width: '8.75rem' },
					items: [{
						label: '刷新', icon: 'reset',
						command: ()=>{
							this.refresh();
						}
					},{
						label: '全部关闭', icon: 'switch',
						command: ()=>{
							while (o2DM.apps.length){
								o2DM.apps[0].taskItem?.close(true);
							}
							//o2DM.currentApp = null;
						}
					},{
						label: '关闭其他', icon: 'process-monitor',
						command: ()=>{
							while (o2DM.apps.length > 1){
								var cmpt = o2DM.apps[0] === app ? o2DM.apps[1] : o2DM.apps[0];
								cmpt.taskItem?.close(true);
							}
							//o2DM.currentApp = null;
							this.active();
						}
					}]
				});
				tag.oomenu.show();
			}
		});
	}

});
