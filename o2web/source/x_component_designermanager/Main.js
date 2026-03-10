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
				var app = new clazz.Main(this.desktop, opt);

				let appId, id;
				if(clazz.options.multitask){
					appId = opt.appId || status?.appId;
					id = opt.id || status?.id || status?.application || status?.column;
					if( !appId && id && app.options.name ){
						appId = `${app.options.name}${id}`;
					}else{
						appId = path + "-" + new o2.widget.UUID();
					}
				}else{
					appId = app.options.name || path;
				}
				app.appId = appId;

				console.log(app.appId, opt, status, app);

				if(o2DM.appMap[app.appId]){
					o2DM.appMap[app.appId].dm_active();
					return;
				}

				app.options.appId = app.appId;

				app.status = status;

				var tag = this.createTag(app, node);

				//this.fireEvent("queryLoadApplication", app);
				app.load();
				app.setEventTarget(this);
				var _self = this;
				// this.app.refresh = function () {
				//     if( layout.inBrowser ){
				//         window.location.reload();
				//     }else{
				//         _self.form.app.refresh();
				//     }
				// };

				app._tag = tag;
				app._container = node;

				app.setTitle =  (str)=> {
					// tag.setAttribute('text', str);
					// tag._elements.text.textContent = str;
					tag.innerHTML = str;
					tag.setAttribute('title', str + ((app.appId) ? "-" + app.appId : ""));
					app.options.title = str;
					if(clazz.options.multitask){
						this.addToHistory(app, str);
					}
				};

				app.dm_close = (ignore)=>{
					if(!ignore && o2DM.currentApp === app){
						o2DM.currentApp = null;
						var index = o2DM.apps.indexOf(app);
						var lastApp = index > 0 ? o2DM.apps[index-1] : o2DM.apps[0];
						if(lastApp){
							lastApp.dm_active();
							// lastApp._tag.setAttribute('type', 'current');
							// lastApp._container.show();
							// o2DM.currentApp = lastApp;
						}else{
							o2DM.currentApp = null;
						}
					}
					o2DM.apps.erase(app);
					delete o2DM.appMap[app.appId];
					tag.remove();
					node.remove();
					app.close();
				};

				app.dm_hide = ()=>{
					app._tag.setAttribute('type', 'default');
					app._tag.oomenu?.hide();
					app._container.hide();
				};

				app.dm_active = ()=>{
					if(o2DM.currentApp === app) return;
					o2DM.currentApp?.dm_hide();
					tag.setAttribute('type', 'current');
					tag.oomenu?.hide();
					node.show();
					o2DM.currentApp = app;
					if(!clazz.options.multitask){
						this.addToHistory( app );
					}
				};

				o2DM.appMap[app.appId] = app;
				o2DM.apps.push(app);

				debugger;

				app.dm_active();
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
	createTag: function (app){
		var tag = new Element('oo-tag', {
			text: app.options.title,
			close: 'on',
			menu: 'on',
			type: 'current'
		}).inject(this.taskBar);
		tag.app = app;
		tag.addEventListener("close", function (e) {
			app.dm_close();
			e.stopPropagation();
		});
		tag.addEventListener("click", function (e) {
			app.dm_active();
		});
		tag.addEventListener("menu", function (e){
			if(!tag.oomenu){
				tag.oomenu = new $OOUI.Menu(tag._elements.menu, {
					area: this.content,
					styles: { width: '8.75rem' },
					items: [{
						label: '刷新', icon: 'reset',
						command: ()=>{
							app.refresh();
						}
					},{
						label: '全部关闭', icon: 'switch',
						command: ()=>{
							while (o2DM.apps.length){
								o2DM.apps[0].dm_close(true);
							}
							o2DM.currentApp = null;
						}
					},{
						label: '关闭其他', icon: 'process-monitor',
						command: ()=>{
							while (o2DM.apps.length > 1){
								var cmpt = o2DM.apps[0] === app ? o2DM.apps[1] : o2DM.apps[0];
								cmpt.dm_close(true);
							}
							app.dm_active();
						}
					}]
				});
				tag.oomenu.show();
			}
		});
		return tag;
	},
	searchDesigner: function (){
		o2DM._openApp('FindDesigner');
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
				o2DM._openApp(d.componentName, null, d.status || { id: d.id, application: {id: d.appid} });
			});
			this.historyMenu = menu;
		});
	},
	locateToCurrent: function (e){
		this.nav.locateToCurrent(e);
	},
	expandSelected: function (e){
		this.nav.expandSelected(e);
	},
	collapseSelected: function (e){
		this.nav.collapseSelected(e);
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
							child.id = appid === 'service.ServiceManager' ?
								`${child.componentName}` :
								`${child.componentName}.${appid}`;
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
			if(this.afterExpand){
				this.afterExpand();
			}
			return;
		}
		data.loaded = true;
		if( data.children && data.children.length > 0 ){
			var template = Array.clone(data.children);
			Promise.resolve(this.getList( template, data.appid || data.id )).then((children)=>{
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
						area: this.app.content,
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
	},
	locateToCurrent: function (e){
		const app = o2DM.currentApp;
		if( app ){
			this._expandToApp(app, ()=>{
				let item = this.oonav.getItem(app.options.id || app.options.name);
				if(item){
					item.itemEl.scrollIntoView({
						behavior: 'smooth', block: 'center', inline: 'center'
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
			const appid = app.application ? app.application.id : '';

			const getKey = (config)=>{
				switch (config._type){
					case 'app-category':
						return config.componentName;
					case 'designer-category':
						return config.componentName.startsWith('service.') ?
							`${config.componentName}` :
							`${config.componentName}.${appid}`;
					case 'app':
						return appid || id;
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
	collapseSelected: function (e){

	}
});
