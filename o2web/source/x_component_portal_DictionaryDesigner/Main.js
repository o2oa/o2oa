MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.DictionaryDesigner = MWF.xApplication.process.DictionaryDesigner || {};
MWF.xDesktop.requireApp("process.DictionaryDesigner", "Main", null, false);
MWF.xApplication.portal.DictionaryDesigner.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("portal.DictionaryDesigner", "Dictionary", null, false);
MWF.xApplication.portal.DictionaryDesigner.Main = new Class({
	Extends: MWF.xApplication.process.DictionaryDesigner.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "portal.DictionaryDesigner",
		"icon": "icon.png",
		"title": MWF.APPDD.LP.title,
		"appTitle": MWF.APPDD.LP.title,
		"id": "",
        "width": "1200",
        "height": "600",
		"actions": null,
		"category": null,
		"portalData": null
	},
	onQueryLoad: function(){
        this.shortcut = true;
        if (this.status){
            this.options.application = this.status.applicationId;
            this.application = this.status.application || this.status.applicationId;
            this.options.id = this.status.id;
            this.setOptions(this.status.options);
        }
        if( !this.application && this.options.application ){
            this.application = this.options.application;
        }
		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.APPDD.LP.newDictionary;
		}
		if (!this.actions){
		    this.actions = MWF.Actions.get("x_portal_assemble_designer");
            this.actions.application = this.application;
        }
		
		this.lp = MWF.xApplication.portal.DictionaryDesigner.LP;

        this.addEvent("queryClose", function(e){
            if (this.explorer && this.explorer.reload){
                this.explorer.reload();
            }
        }.bind(this));
	},
    pasteModule: function(){
        if (this.shortcut) {
            if (MWF.clipboard.data) {
                if (MWF.clipboard.data.type == "dictionary") {
                    if (this.tab.showPage) {
                        var dictionary = this.tab.showPage.dictionary;
                        if (dictionary) {
                            if (dictionary.currentSelectedItem) {
                                var item = dictionary.currentSelectedItem;
                                var key = MWF.clipboard.data.data.key;

                                var value = (typeOf(MWF.clipboard.data.data.value)=="object") ? Object.clone(MWF.clipboard.data.data.value) : MWF.clipboard.data.data.value;

                                var level = item.level;
                                var parent = item;
                                var nextSibling = null;
                                if (!item.parent){//top level
                                    level = 1;
                                }else{
                                    if (item.type!="array" && item.type!="object"){
                                        parent = item.parent;
                                        nextSibling = item;
                                    }else{
                                        if (item.exp){
                                            level = item.level+1;
                                        }else{
                                            parent = item.parent;
                                            nextSibling = item;
                                        }
                                    }
                                }
                                var idx = parent.children.length;
                                if (item.type=="array"){
                                    if (nextSibling){
                                        key = nextSibling.key;
                                        parent.value.splice(nextSibling.key, 0, value);
                                        for (var i=nextSibling.key; i<parent.children.length; i++){
                                            subItem = parent.children[i];
                                            subItem.key = subItem.key+1;
                                            subItem.setNodeText();
                                        }
                                    }else{
                                        var key = parent.value.length;
                                        parent.value.push(value);
                                    }
                                    idx = key;
                                }else{
                                    var oldKey = key;
                                    var i = 0;
                                    while (parent.value[key] != undefined) {
                                        i++;
                                        key = oldKey + i;
                                    }
                                    parent.value[key] = value;
                                    if (nextSibling) var idx = parent.children.indexOf(nextSibling);
                                }
                                var item = new MWF.xApplication.portal.DictionaryDesigner.Dictionary.item(key, value, parent, level, this.dictionary, true, nextSibling);
                                if (idx) parent.children[idx-1].nextSibling = item;
                                parent.children.splice(idx, 0, item);

                            }
                        }
                    }
                }
            }
        }
    },

    getApplication:function(callback){
        if (!this.application){
            this.actions.getApplication(this.options.application, function(json){
                this.application = {"name": json.data.name, "id": json.data.id};
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    openApp: function (){
        layout.openApplication(null, 'portal.PortalManager', {
            application: this.application,
            appId: 'portal.PortalManager'+this.application.id
        }, {
            "navi":2
        });
    },

    loadDictionaryList: function(){
        if( this.currentListDictionaryItem ){
            var d = this.currentListDictionaryItem.retrieve('dictionary');
            this.options.id = d.id;
        }
        if( this.itemArray && this.itemArray.length  ){
            this.itemArray = this.itemArray.filter(function(i){
                if(i.data.id)i.node.destroy();
                return !i.data.id;
            });
        }else{
            this.itemArray = [];
        }
        this.actions.listDictionary(this.application.id || this.application, function (json) {
            this.checkSort(json.data);
            json.data.each(function(dictionary){
                this.createListDictionaryItem(dictionary);
            }.bind(this));
        }.bind(this), null, false);
    },

    loadDictionaryByData: function(node, e){
        var dictionary = node.retrieve("dictionary");

        var openNew = true;
        for (var i = 0; i<this.tab.pages.length; i++){
            if (dictionary.id==this.tab.pages[i].dictionary.data.id){
                this.tab.pages[i].showTabIm();
                openNew = false;
                break;
            }
        }
        if (openNew){
            this.loadDictionaryData(dictionary.id, function(data){
                var dictionary = new MWF.xApplication.portal.DictionaryDesigner.Dictionary(this, data);
                dictionary.load();
            }.bind(this), true);
        }
    },


	//loadForm------------------------------------------
    loadDictionary: function(){

		this.getDictionaryData(this.options.id, function(ddata){
            this.setTitle(this.options.appTitle + "-"+ddata.name);
            if (this.taskitem) this.taskitem.setText(this.options.appTitle + "-"+ddata.name);
            this.options.appTitle = this.options.appTitle + "-"+ddata.name;

            if (this.options.readMode){
                this.dictionary = new MWF.xApplication.portal.DictionaryDesigner.DictionaryReader(this, ddata);
            }else{
                this.dictionary = new MWF.xApplication.portal.DictionaryDesigner.Dictionary(this, ddata);
            }

			this.dictionary.load();

            if (this.status){
                if (this.status.openDictionarys){
                    this.status.openDictionarys.each(function(id){
                        this.loadDictionaryData(id, function(data){
                            var showTab = true;
                            if (this.status.currentId){
                                if (this.status.currentId!=data.id) showTab = false;
                            }
                            if (this.options.readMode){
                                var dictionary = new MWF.xApplication.portal.DictionaryDesigner.DictionaryReader(this, data, {"showTab": showTab});
                            }else{
                                var dictionary = new MWF.xApplication.portal.DictionaryDesigner.Dictionary(this, data, {"showTab": showTab});
                            }

                            dictionary.load();
                        }.bind(this), true);
                    }.bind(this));
                    this.status.openDictionarys = [];
                }
            }
		}.bind(this));
	},

	loadDictionaryData: function(id, callback){

		this.actions.getDictionary(id, function(json){
			if (json){
				var data = json.data;

                if (!this.application){
                    this.actions.getApplication(data.application, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        if (callback) callback(data);
                    }.bind(this));
                }else{
                    if (callback) callback(data);
                }
			}
		}.bind(this));
	}
});
