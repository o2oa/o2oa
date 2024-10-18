MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ScriptDesigner = MWF.xApplication.process.ScriptDesigner || {};
MWF.xDesktop.requireApp("process.ScriptDesigner", "Main", null, false);
MWF.xApplication.service.ScriptDesigner.options = {
	"multitask": true,
	"executable": false
};
MWF.xDesktop.requireApp("service.ScriptDesigner", "Script", null, false);
MWF.xApplication.service.ScriptDesigner.Main = new Class({
	Extends: MWF.xApplication.process.ScriptDesigner.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "service.ScriptDesigner",
		"icon": "icon.png",
		"title": MWF.APPSD.LP.title,
		"appTitle": MWF.APPSD.LP.title,
		"id": "",
		"actions": null,
		"category": null,

        "sortKeys": ['name', 'alias', 'createTime', 'updateTime'],
        "sortKey": '',
        "listToolbarExpanded": false
	},
	onQueryLoad: function(){
		if (this.status){
			this.options.id = this.status.id;
        }
		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.APPSD.LP.newScript;
		}

        this.actions = MWF.Actions.get("x_program_center");

		this.lp = MWF.xApplication.process.ScriptDesigner.LP;

        this.addEvent("queryClose", function(e){
            if (this.explorer && this.explorer.app && this.explorer.app.window){
                this.explorer.reload();
            }
        }.bind(this));
	},

    getUd: function ( callback ){
        MWF.UD.getDataJson(this.options.name, function (data){
            if( data ){
                this.options.sortKey = data.sortKey;
                this.options.listToolbarExpanded = data.listToolbarExpanded || false;
            }
            callback();
        }.bind(this));
    },
    setUd: function (){
        var data = {
            sortKey: this.options.sortKey,
            listToolbarExpanded: this.options.listToolbarExpanded
        };
        MWF.UD.putData(this.options.name, data);
    },
    openApp: function (){
        layout.openApplication(null, 'service.ServiceManager', {
            appId: 'service.ServiceManager'
        }, {
            "navi":2
        });
    },

    getApplication:function(callback){
        if (callback) callback();
    },


    loadScriptList: function() {
        if( this.currentListScriptItem ){
            var d = this.currentListScriptItem.retrieve('script');
            this.options.id = d.id;
        }
        if( this.itemArray && this.itemArray.length  ){
            this.itemArray = this.itemArray.filter(function(i){
                if(!i.data.isNewScript)i.node.destroy();
                return i.data.isNewScript;
            });
        }else{
            this.itemArray = [];
        }
        this.actions.listScript(function (json) {
            this.checkSort(json.data);
            json.data.each(function(script){
                this.createListScriptItem(script);
            }.bind(this));
        }.bind(this), null, false);
    },


    addIncludeToList: function(name){
        this.actions.getScriptByName(name, function(json){
            var script = json.data;
            var includeScriptItem = new Element("div", {"styles": this.css.includeScriptItem}).inject(this.propertyIncludeListArea);
            var includeScriptItemAction = new Element("div", {"styles": this.css.includeScriptItemAction}).inject(includeScriptItem);
            var includeScriptItemText = new Element("div", {"styles": this.css.includeScriptItemText}).inject(includeScriptItem);
            includeScriptItemText.set("text", script.name+" ("+script.alias+")");
            includeScriptItem.store("script", script);

            var _self = this;
            includeScriptItemAction.addEvent("click", function(){
                var node = this.getParent();
                var script = node.retrieve("script");
                if (script){
                    _self.scriptTab.showPage.script.data.dependScriptList.erase(script.name);
                }
                node.destroy();
            });
        }.bind(this), function(){
            this.scriptTab.showPage.script.data.dependScriptList.erase(name);
        }.bind(this));
    },
    loadScriptByData: function(node, e){
        var script = node.retrieve("script");

        var openNew = true;
        for (var i = 0; i<this.scriptTab.pages.length; i++){
            if (script.id==this.scriptTab.pages[i].script.data.id){
                this.scriptTab.pages[i].showTabIm();
                openNew = false;
                break;
            }
        }
        if (openNew){
            this.loadScriptData(script.id, function(data){
                var script = new MWF.xApplication.service.ScriptDesigner.Script(this, data);
                script.load();
            }.bind(this), true);
        }
        //var _self = this;
        //var options = {
        //    "onQueryLoad": function(){
        //        this.actions = _self.actions;
        //        this.options.id = script.id;
        //        this.application = _self.application;
        //    }
        //};
        //this.desktop.openApplication(e, "process.ScriptDesigner", options);
    },
	//loadForm------------------------------------------
    loadScript: function(){
        //this.scriptTab.addTab(node, title);
		this.getScriptData(this.options.id, function(data){
			this.script = new MWF.xApplication.service.ScriptDesigner.Script(this, data);
			this.script.load();

            if (this.status){
                if (this.status.openScripts){
                    this.status.openScripts.each(function(id){
                        this.loadScriptData(id, function(data){
                            var showTab = true;
                            if (this.status.currentId){
                                if (this.status.currentId!=data.id) showTab = false;
                            }
                            var script = new MWF.xApplication.service.ScriptDesigner.Script(this, data, {"showTab": showTab});
                            script.load();
                        }.bind(this), true);
                    }.bind(this));

                    this.status.openScripts = [];
                }
            }
		}.bind(this));
	},

    loadNewScriptData: function(callback){
        // this.actions.getUUID(function(id){
            var data = {
                "name": "",
               // "id": id,
                "alias": "",
                "description": "",
                "language": "javascript",
                "dependScriptList": [],
                "isNewScript": true,
                "text": ""
            };
            this.createListScriptItem(data, true);
            if (callback) callback(data);
        // }.bind(this))
	},
    loadScriptData: function(id, callback, notSetTile){
		this.actions.getScript(id, function(json){
			if (json){
				var data = json.data;

                if (!notSetTile){
                    this.setTitle(this.options.appTitle + "-"+data.name);
                    if(this.taskitem)this.taskitem.setText(this.options.appTitle + "-"+data.name);
                    this.options.appTitle = this.options.appTitle + "-"+data.name;
                }

                if (callback) callback(data);
			}
		}.bind(this));
	},

	recordStatus: function(){
        if (this.scriptTab){
            var openScripts = [];
            this.scriptTab.pages.each(function(page){
                if (page.script.data.id!=this.options.id) openScripts.push(page.script.data.id);
            }.bind(this));
            var currentId = this.scriptTab.showPage.script.data.id;
            var status = {
                "id": this.options.id,
                "openScripts": openScripts,
                "currentId": currentId
            };
            return status;
        }
		return {"id": this.options.id};
	},
    getFormToolbarHTML: function(callback){
        var toolbarUrl = "../x_component_service_ScriptDesigner/$Main/default/toolbars.html";
        MWF.getRequestText(toolbarUrl, function(responseText, responseXML){
            var htmlString = responseText;
            htmlString = o2.bindJson(htmlString, {"lp": MWF.APPSD.LP.formToolbar});
            var temp = new Element('div').set('html', htmlString);
            if (callback) callback( temp.childNodes[0] );
        }.bind(this));
        // var r = new Request.HTML({
        // 	url: toolbarUrl,
        // 	method: "get",
        // 	onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
        // 		var toolbarNode = responseTree[0];
        // 		if (callback) callback(toolbarNode);
        // 	}.bind(this),
        // 	onFailure: function(xhr){
        // 		this.notice("request cmsToolbars error: "+xhr.responseText, "error");
        // 	}.bind(this)
        // });
        // r.send();
    }
});
