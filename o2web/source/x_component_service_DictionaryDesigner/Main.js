MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.DictionaryDesigner = MWF.xApplication.process.DictionaryDesigner || {};
MWF.xDesktop.requireApp("process.DictionaryDesigner", "Main", null, false);
MWF.xApplication.service.DictionaryDesigner.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("service.DictionaryDesigner", "Dictionary", null, false);
MWF.xApplication.service.DictionaryDesigner.Main = new Class({
	Extends: MWF.xApplication.process.DictionaryDesigner.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "service.DictionaryDesigner",
		"icon": "icon.png",
		"title": MWF.APPDD.LP.title,
		"appTitle": MWF.APPDD.LP.title,
		"id": "",
        "width": "1200",
        "height": "600",
		"actions": null,
		"category": null
	},
	onQueryLoad: function(){
        this.shortcut = true;
        if (this.status){
            this.options.id = this.status.id;
            this.setOptions(this.status.options);
        }
		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.APPDD.LP.newDictionary;
		}
		if (!this.actions){
		    this.actions = MWF.Actions.get("x_program_center");
        }
		
		this.lp = MWF.xApplication.process.DictionaryDesigner.LP;

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
                                var item = new MWF.xApplication.service.DictionaryDesigner.Dictionary.item(key, value, parent, level, this.dictionary, true, nextSibling);
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
        if (callback) callback();
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
        this.actions.listDictionary(function (json) {
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
                var dictionary = new MWF.xApplication.service.DictionaryDesigner.Dictionary(this, data);
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
                this.dictionary = new MWF.xApplication.service.DictionaryDesigner.DictionaryReader(this, ddata);
            }else{
                this.dictionary = new MWF.xApplication.service.DictionaryDesigner.Dictionary(this, ddata);
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
                                var dictionary = new MWF.xApplication.service.DictionaryDesigner.DictionaryReader(this, data, {"showTab": showTab});
                            }else{
                                var dictionary = new MWF.xApplication.service.DictionaryDesigner.Dictionary(this, data, {"showTab": showTab});
                            }

                            dictionary.load();
                        }.bind(this), true);
                    }.bind(this));
                    this.status.openDictionarys = [];
                }
            }
		}.bind(this));
	},
	loadNewDictionaryData: function(callback){
        var data = {
            "name": "",
            "id": "",
            "alias": "",
            "description": "",
            "data": {}
        };
        this.createListDictionaryItem(data, true);
        if (callback) callback(data);
	},
	loadDictionaryData: function(id, callback){

		this.actions.getDictionary(id, function(json){
			if (json){
				var data = json.data;

                if (callback) callback(data);
			}
		}.bind(this));
	},


    recordStatus: function(){

        if (this.tab){
            var openDictionarys = [];
            this.tab.pages.each(function(page){
                if (page.dictionary.data.id!=this.options.id) openDictionarys.push(page.dictionary.data.id);
            }.bind(this));
            var currentId = this.tab.showPage.dictionary.data.id;
            var status = {
                "id": this.options.id,
                "openDictionarys": openDictionarys,
                "currentId": currentId,
                "options": {
                    "action": this.options.action,
                    "noCreate": this.options.noCreate,
                    "noDelete": this.options.noDelete,
                    "noModifyName": this.options.noModifyName,
                    "readMode": this.options.readMode
                }
            };
            return status;
        }
        return {"id": this.options.id, "application": application};
    }
});
