MWF.xApplication.portal.ScriptDesigner.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("portal.PortalManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("portal.ScriptDesigner", "Script", null, false);
MWF.require("MWF.xDesktop.UserData", null, false);
MWF.xApplication.portal.ScriptDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "portal.ScriptDesigner",
		"icon": "icon.png",
		"title": MWF.xApplication.portal.ScriptDesigner.LP.title,
		"appTitle": MWF.xApplication.portal.ScriptDesigner.LP.title,
		"id": "",
		"actions": null,
		"category": null,
		"processData": null,

        "sortKeys": ['name', 'alias', 'createTime', 'updateTime'],
        "sortKey": '',
        "listToolbarExpanded": false
	},
	onQueryLoad: function(){
		if (this.status){
            this.options.application = this.status.applicationId;
            this.application = this.status.application;
			this.options.id = this.status.id;
		}
		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.xApplication.portal.ScriptDesigner.LP.newScript;
		}

        if( this.options.application ){
            if( !this.application  )this.application = this.options.application;
        }

        this.actions = MWF.Actions.get("x_portal_assemble_designer");
		//this.actions = new MWF.xApplication.portal.PortalManager.Actions.RestActions();
		
		this.lp = MWF.xApplication.portal.ScriptDesigner.LP;

        this.addEvent("queryClose", function(e){
            if (this.explorer){
                this.explorer.reload();
            }
        }.bind(this));
	},
	
	loadApplication: function(callback){
		this.createNode();
		if (!this.options.isRefresh){
			this.maxSize(function(){
				this.openScript();
			}.bind(this));
		}else{
			this.openScript();
		}
		if (callback) callback();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
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
	openScript: function(){
        this.getApplication(function(){
            this.getUd(function (){
                this.loadNodes();
                this.loadScriptListNodes();
                this.loadContentNode(function(){

                    this.loadProperty();
                    //	this.loadTools();
                    this.resizeNode();
                    this.addEvent("resize", this.resizeNode.bind(this));
                    this.loadScript();

                    if (this.toolbarContentNode){
                        this.setScrollBar(this.toolbarContentNode, null, {
                            "V": {"x": 0, "y": 0},
                            "H": {"x": 0, "y": 0}
                        });
                        this.setScrollBar(this.propertyDomArea, null, {
                            "V": {"x": 0, "y": 0},
                            "H": {"x": 0, "y": 0}
                        });
                    }

                }.bind(this));
            }.bind(this))

        }.bind(this));
	},
	loadNodes: function(){
        this.scriptListNode = new Element("div", {
            "styles": this.css.scriptListNode
        }).inject(this.node);

		this.propertyNode = new Element("div", {
			"styles": this.css.propertyNode
		}).inject(this.node);

		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.node);
	},
    //loadScriptList-------------------------------
    loadScriptListNodes: function(){
        this.scriptListTitleNode = new Element("div", {
            "styles": this.css.scriptListTitleNode,
            "text": MWF.xApplication.portal.ScriptDesigner.LP.scriptLibrary
        }).inject(this.scriptListNode);

        this.scriptListResizeNode = new Element("div", {"styles": this.css.scriptListResizeNode}).inject(this.scriptListNode);

        this.createListTitleNodes();

        this.scriptListAreaSccrollNode = new Element("div", {"styles": this.css.scriptListAreaSccrollNode}).inject(this.scriptListNode);
        this.scriptListAreaNode = new Element("div", {"styles": this.css.scriptListAreaNode}).inject(this.scriptListAreaSccrollNode);

        this.loadScriptListResize();

        this.loadScriptList();
    },

    createListTitleNodes: function (){
        this.scriptListTitleNode.setStyle("display", 'flex');

        this.titleActionArea = new Element("div", {
            styles: this.css.titleActionArea
        }).inject(this.scriptListTitleNode);

        this.moreAction = new Element("div", {
            styles: this.css.moreAction,
            title: this.lp.searchAndSort
        }).inject(this.titleActionArea);
        this.moreAction.addEvent("click", function(){
            var isHidden = this.toolbarNode.getStyle("display") === "none";
            this.toolbarNode.setStyle("display", isHidden ? "" : "none" );
            this.resizeNode();
            this.options.listToolbarExpanded = isHidden;
            this.setUd();
        }.bind(this));

        this.toolbarNode =  new Element("div", {
            styles: this.css.toolbarNode
        }).inject(this.scriptListNode);
        if( this.options.listToolbarExpanded )this.toolbarNode.show();

        this.createSortNode();
        this.createSearchNode();
    },
    getUd: function ( callback ){
        MWF.UD.getDataJson(this.options.name + "_" + this.application.id, function (data){
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
        MWF.UD.putData(this.options.name + "_" + this.application.id, data);
    },
    openApp: function (){
        layout.openApplication(null, 'portal.PortalManager', {
            application: this.application,
            appId: 'portal.PortalManager'+this.application.id
        }, {
            "navi":3
        });
    },
    createElement: function(){
        var flag = true;
        this.itemArray.each(function(i){
            if( i.data.isNewScript ){
                flag = false;
                return;
            }
        });
        if( !flag ){
            this.notice(this.lp.duplicateNewNote, 'info');
            return;
        }
        if( this.currentListScriptItem ){
            this.currentListScriptItem.setStyles(this.css.listScriptItem);
        }
        this.options.id = "";
        this.loadScript();
    },
    createSortNode: function(){
        this.itemSortArea = new Element("div.itemSortArea", {
            styles: this.css.itemSortArea
        }).inject(this.toolbarNode);
        this.itemSortSelect = new Element('select.itemSortSelect', {
            styles: this.css.itemSortSelect,
            events: {
                change: function(){
                    this.options.sortKey = this.itemSortSelect[ this.itemSortSelect.selectedIndex ].value;
                    this.setUd();
                    this.loadScriptList();
                }.bind(this)
            }
        }).inject(this.itemSortArea);
        new Element('option',{ 'text': this.lp.sorkKeyNote, 'value': "" }).inject(this.itemSortSelect);
        this.options.sortKeys.each(function (key){
            var opt = new Element('option',{ 'text': this.lp[key] + " " + this.lp.asc, 'value': key+"-asc" }).inject(this.itemSortSelect);
            if( this.options.sortKey === opt.get('value') )opt.set('selected', true);
            opt = new Element('option',{ 'text': this.lp[key] + " " + this.lp.desc, 'value': key+"-desc" }).inject(this.itemSortSelect);
            if( this.options.sortKey === opt.get('value') )opt.set('selected', true);
        }.bind(this));
    },
    createSearchNode: function (){
        this.searchNode = new Element("div.searchNode", {
            "styles": this.css.searchArea
        }).inject(this.toolbarNode);

        this.searchInput = new Element("input.searchInput", {
            "styles": this.css.searchInput,
            "placeholder": this.lp.searchPlacholder,
            "value": this.options.searchKey || ""
        }).inject(this.searchNode);

        this.searchButton = new Element("i", {
            "styles": this.css.searchButton
        }).inject(this.searchNode);

        this.searchCancelButton = new Element("i", {
            "styles": this.css.searchCancelButton
        }).inject(this.searchNode);

        this.searchInput.addEvents({
            focus: function(){
                this.searchNode.addClass("mainColor_border");
                this.searchButton.addClass("mainColor_color");
            }.bind(this),
            blur: function () {
                this.searchNode.removeClass("mainColor_border");
                this.searchButton.removeClass("mainColor_color");
            }.bind(this),
            keydown: function (e) {
                if( (e.keyCode || e.code) === 13 ){
                    this.search();
                }
            }.bind(this),
            keyup: function (e){
                this.searchCancelButton.setStyle('display', this.searchInput.get('value') ? '' : 'none');
            }.bind(this)
        });

        this.searchCancelButton.addEvent("click", function (e) {
            this.searchInput.set("value", "");
            this.searchCancelButton.hide();
            this.search();
        }.bind(this));

        this.searchButton.addEvent("click", function (e) {
            this.search();
        }.bind(this));
    },
    checkSort: function (data){
        if( !!this.options.sortKey ){
            var sortKey = this.options.sortKey.split("-");
            var key = sortKey[0], isDesc = sortKey[1] === 'desc';
            data.sort(function (a, b){
                var av = a[key];
                var bv = b[key];
                if( typeOf(av) === 'string' && typeOf(bv) === 'string' ){
                    var isLetterA = /^[a-zA-Z0-9]/.test(av);
                    var isLetterB = /^[a-zA-Z0-9]/.test(bv);

                    if (isLetterA && !isLetterB) return isDesc ? 1 : -1; // a是字母，b不是，a排在前面
                    if (!isLetterA && isLetterB) return isDesc ? -1 : 1;  // a不是字母，b是，b排在前面

                    return isDesc ?  bv.localeCompare(av) : av.localeCompare(bv);
                }
                return isDesc ? (bv - av) : (av - bv);
            }.bind(this));
        }
    },
    checkShow: function (i){
        if( this.options.searchKey ){
            var v = this.options.searchKey;
            if( i.data.name.contains(v) || (i.data.alias || "").contains(v) || i.data.id.contains(v) ){
                //i.node.setStyle("display", "");
            }else{
                i.node.setStyle("display", "none");
            }
        }
    },
    search: function (){
        var v = this.searchInput.get("value");
        this.options.searchKey = v;
        this.itemArray.each(function (i){
            if( !v ){
                i.node.setStyle("display", "");
            }else if( i.data.name.contains(v) || (i.data.alias || "").contains(v) || i.data.id.contains(v) ){
                i.node.setStyle("display", "");
            }else{
                i.node.setStyle("display", "none");
            }
        }.bind(this));
    },

    loadScriptListResize: function(){
//		var size = this.propertyNode.getSize();
//		var position = this.propertyResizeBar.getPosition();
        this.scriptListResize = new Drag(this.scriptListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.scriptListAreaSccrollNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "onDrag": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
                var bodySize = this.content.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = x.toFloat() - position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<40) width = 40;
                this.contentNode.setStyle("margin-left", width+1);
                this.scriptListNode.setStyle("width", width);
            }.bind(this)
        });
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
        this.actions.listScript(this.application.id, function (json) {
            this.checkSort(json.data);
            json.data.each(function(script){
                this.createListScriptItem(script);
            }.bind(this));
        }.bind(this), null, false);
    },
    createListScriptItem: function(script, isNew){
        var _self = this;
        var listScriptItem = new Element("div", {"styles": this.css.listScriptItem}).inject(this.scriptListAreaNode, (isNew) ? "top": "bottom");
        var listScriptItemIcon = new Element("div", {"styles": this.css.listScriptItemIcon}).inject(listScriptItem);
        var listScriptItemText = new Element("div", {"styles": this.css.listScriptItemText, "text": (script.name) ? script.name+" ("+script.alias+")" : this.lp.newScript}).inject(listScriptItem);

        listScriptItem.store("script", script);
        listScriptItem.addEvents({
            "click": function(e){_self.loadScriptByData(this, e);},
            "mouseover": function(){if (_self.currentListScriptItem!=this) this.setStyles(_self.css.listScriptItem_over);},
            "mouseout": function(){if (_self.currentListScriptItem!=this) this.setStyles(_self.css.listScriptItem);}
        });

         if( script.id === this.options.id ){
            listScriptItem.setStyles(this.css.listScriptItem_current);
            this.currentListScriptItem = listScriptItem;
        }

        var itemObj = {
            node: listScriptItem,
            data: script
        };
        this.itemArray.push(itemObj);
        this.checkShow(itemObj);

        this.listScriptItemMove(listScriptItem);

    },
    createScriptListCopy: function(node){
        var copyNode = node.clone().inject(this.node);
        copyNode.position({
            "relativeTo": node,
            "position": "upperLeft",
            "edge": "upperLeft"
        });
        var size = copyNode.getSize();
        copyNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px",
            "z-index": 50001,
        });
        return copyNode;
    },
    listDragEnter: function(dragging, inObj){
        var markNode = inObj.retrieve("markNode");
        if (!markNode){
            var size = inObj.getSize();
            markNode = new Element("div", {"styles": this.css.dragListItemMark}).inject(this.node);
            markNode.setStyles({
                "width": ""+size.x+"px",
                "height": ""+size.y+"px",
                "position": "absolute",
                "background-color": "#666",
                "z-index": 50000,
                "opacity": 0.3
             //   "border": "2px solid #ffba00"
            });
            markNode.position({
                "relativeTo": inObj,
                "position": "upperLeft",
                "edge": "upperLeft"
            });
            var y = markNode.getStyle("top").toFloat()-1;
            var x = markNode.getStyle("left").toFloat()-2;
            markNode.setStyles({
                "left": ""+x+"px",
                "top": ""+y+"px",
            });
            inObj.store("markNode", markNode);
        }
    },
    listDragLeave: function(dragging, inObj){
        var markNode = inObj.retrieve("markNode");
        if (markNode) markNode.destroy();
        inObj.eliminate("markNode");
    },
    listScriptItemMove: function(node){
        var iconNode = node.getFirst();
        iconNode.addEvent("mousedown", function(e){
            var script = node.retrieve("script");
            if (script.id!=this.scriptTab.showPage.script.data.id){
                var copyNode = this.createScriptListCopy(node);

                var droppables = [this.designNode, this.propertyDomArea];
                var listItemDrag = new Drag.Move(copyNode, {
                    "droppables": droppables,
                    "onEnter": function(dragging, inObj){
                        this.listDragEnter(dragging, inObj);
                    }.bind(this),
                    "onLeave": function(dragging, inObj){
                        this.listDragLeave(dragging, inObj);
                    }.bind(this),
                    "onDrag": function(e){
                        //nothing
                    }.bind(this),
                    "onDrop": function(dragging, inObj){
                        if (inObj){
                            this.addIncludeScript(script);
                            this.listDragLeave(dragging, inObj);
                            copyNode.destroy();
                        }else{
                            copyNode.destroy();
                        }
                    }.bind(this),
                    "onCancel": function(dragging){
                        copyNode.destroy();
                    }.bind(this)
                });
                listItemDrag.start(e);
            }
        }.bind(this));
    },
    addIncludeScript: function(script){
        var currentScript = this.scriptTab.showPage.script;
        if (currentScript.data.dependScriptList.indexOf(script.name)==-1){
            currentScript.data.dependScriptList.push(script.name);
            this.addIncludeToList(script.name);
        }
    },
    addIncludeToList: function(name){
        this.actions.getScriptByName(name, this.application.id, function(json){
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
                var script = new MWF.xApplication.portal.ScriptDesigner.Script(this, data);
                script.load();
            }.bind(this), true);
        }
    },
	
	//loadContentNode------------------------------
    loadContentNode: function(toolbarCallback, contentCallback){
		this.contentToolbarNode = new Element("div#contentToolbarNode", {
			"styles": this.css.contentToolbarNode
		}).inject(this.contentNode);
		this.loadContentToolbar(toolbarCallback);
		
		this.editContentNode = new Element("div", {
			"styles": this.css.editContentNode
		}).inject(this.contentNode);

		this.loadEditContent(function(){
		//	if (this.designDcoument) this.designDcoument.body.setStyles(this.css.designBody);
			if (this.designNode) this.designNode.setStyles(this.css.designNode);
            if (contentCallback) contentCallback();
		}.bind(this));
	},
    loadContentToolbar: function(callback){
		this.getFormToolbarHTML(function(toolbarNode){
			var spans = toolbarNode.getElements("span");
			spans.each(function(item, idx){
				var img = item.get("MWFButtonImage");
				if (img){
					item.set("MWFButtonImage", this.path+""+this.options.style+"/toolbar/"+img);
				}
			}.bind(this));

			$(toolbarNode).inject(this.contentToolbarNode);
			MWF.require("MWF.widget.Toolbar", function(){
				this.toolbar = new MWF.widget.Toolbar(toolbarNode, {"style": "ProcessCategory"}, this);
				this.toolbar.load();
                var _self = this;
                //this.styleSelectNode = toolbarNode.getElement("select");
                //this.styleSelectNode.addEvent("change", function(){
                //    _self.changeEditorStyle(this);
                //});
                this.styleSelectNode = toolbarNode.getElement("select[MWFnodetype='theme']");
                this.styleSelectNode.addEvent("change", function(){
                    _self.changeEditorStyle(this);
                });

                this.fontsizeSelectNode = toolbarNode.getElement("select[MWFnodetype='fontSize']");
                this.fontsizeSelectNode.addEvent("change", function(){
                    _self.changeFontSize(this);
                });

                this.editorSelectNode = toolbarNode.getElement("select[MWFnodetype='editor']");
                this.editorSelectNode.addEvent("change", function(){
                    _self.changeEditor(this);
                });

                this.monacoStyleSelectNode = toolbarNode.getElement("select[MWFnodetype='monaco-theme']");
                this.monacoStyleSelectNode.addEvent("change", function(){
                    _self.changeEditorStyle(this);
                });

				if (callback) callback();
			}.bind(this));
		}.bind(this));
	},
    changeEditor: function(node){
        var idx = node.selectedIndex;
        var value = node.options[idx].value;

        if (!MWF.editorData){
            MWF.editorData = {
                "javascriptEditor": {
                    "monaco_theme": "vs",
                    "theme": "tomorrow",
                    "fontSize" : "12px"
                }
            };
        }
        MWF.editorData.javascriptEditor["editor"] = value;
        MWF.UD.putData("editor", MWF.editorData);

        this.scriptTab.pages.each(function(page){
            var editor = page.script.editor;
            if (editor) editor.changeEditor(value);
        }.bind(this));

        if (value=="ace"){
            this.monacoStyleSelectNode.hide();
            this.styleSelectNode.show();
        }else{
            this.monacoStyleSelectNode.show();
            this.styleSelectNode.hide();
        }

    },
    changeFontSize: function(node){
        var idx = node.selectedIndex;
        var value = node.options[idx].value;
        //var editorData = null;
        this.scriptTab.pages.each(function(page){
            //if (!editorData) editorData = page.invoke.editor.editorData;
            var editor = page.script.editor;
            if (editor) editor.setFontSize(value);
        }.bind(this));
        //if (!editorData) editorData = MWF.editorData;
        //editorData.javainvokeEditor.theme = value;
        if (!MWF.editorData){
            MWF.editorData = {
                "javascriptEditor": {
                    "monaco_theme": "vs",
                    "theme": "tomorrow",
                    "fontSize" : "12px"
                }
            };
        }
        MWF.editorData.javascriptEditor["fontSize"] = value;

        MWF.UD.putData("editor", MWF.editorData);

    },
    changeEditorStyle: function(node){
        var idx = node.selectedIndex;
        var value = node.options[idx].value;
        //var editorData = null;
        this.scriptTab.pages.each(function(page){
            //if (!editorData) editorData = page.script.editor.editorData;
            var editor = page.script.editor;
            if (editor) editor.setTheme(value);
        }.bind(this));
        //if (!editorData) editorData = MWF.editorData;
        //editorData.javascriptEditor.theme = value;
        if (!MWF.editorData){
            MWF.editorData = {
                "javascriptEditor": {
                    "monaco_theme": "vs",
                    "theme": "tomorrow",
                    "fontSize" : "12px"
                }
            };
        }

        if (MWF.editorData.javascriptEditor.editor === "monaco"){
            MWF.editorData.javascriptEditor.monaco_theme = value;
        }else{
            MWF.editorData.javascriptEditor.theme = value;
        }

        MWF.UD.putData("editor", MWF.editorData);
    },
	getFormToolbarHTML: function(callback){
		var toolbarUrl = this.path+this.options.style+"/toolbars.html";
        MWF.getRequestText(toolbarUrl, function(responseText, responseXML){
            var htmlString = responseText;
            htmlString = o2.bindJson(htmlString, {"lp": MWF.xApplication.portal.ScriptDesigner.LP.formToolbar});
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
		// 		this.notice("request portalToolbars error: "+xhr.responseText, "error");
		// 	}.bind(this)
		// });
		// r.send();
	},
    maxOrReturnEditor: function(){
        if (!this.isMax){
            this.designNode.inject(this.node);
            this.designNode.setStyles({
                "position": "absolute",
                "width": "100%",
                "height": "100%",
                "top": "0px",
                "margin": "0px",
                "left": "0px"
            });
            this.scriptTab.pages.each(function(page){
                page.script.setAreaNodeSize();
            });
            this.isMax = true;
        }else{
            this.isMax = false;
            this.designNode.inject(this.editContentNode);
            this.designNode.setStyles(this.css.designNode);
            this.designNode.setStyles({
                "position": "static"
            });
            this.resizeNode();
            this.scriptTab.pages.each(function(page){
                page.script.setAreaNodeSize();
            });
        }

    },
    loadEditContent: function(callback){
        this.designNode = new Element("div", {
            "styles": this.css.designNode
        }).inject(this.editContentNode);

        MWF.require("MWF.widget.Tab", function(){
            this.scriptTab = new MWF.widget.Tab(this.designNode, {"style": "script"});
            this.scriptTab.load();
        }.bind(this), false);


        //MWF.require("MWF.widget.ScrollBar", function(){
            //    new MWF.widget.ScrollBar(this.designNode, {"distance": 100});
            //}.bind(this));
	},
	
	//loadProperty------------------------
	loadProperty: function(){
		this.propertyTitleNode = new Element("div", {
			"styles": this.css.propertyTitleNode,
			"text": MWF.xApplication.portal.ScriptDesigner.LP.property
		}).inject(this.propertyNode);
		
		this.propertyResizeBar = new Element("div", {
			"styles": this.css.propertyResizeBar
		}).inject(this.propertyNode);
		this.loadPropertyResize();
		
		this.propertyContentNode = new Element("div", {
			"styles": this.css.propertyContentNode
		}).inject(this.propertyNode);
		
		this.propertyDomArea = new Element("div", {
			"styles": this.css.propertyDomArea
		}).inject(this.propertyContentNode);
		
		this.propertyDomPercent = 0.3;
		this.propertyContentResizeNode = new Element("div", {
			"styles": this.css.propertyContentResizeNode
		}).inject(this.propertyContentNode);
		
		this.propertyContentArea = new Element("div", {
			"styles": this.css.propertyContentArea
		}).inject(this.propertyContentNode);
		
		this.loadPropertyContentResize();

        this.setPropertyContent();
        this.setIncludeNode();
	},
    setIncludeNode: function(){
        this.includeTitleNode = new Element("div", {"styles": this.css.includeTitleNode}).inject(this.propertyDomArea);
        this.includeTitleActionNode = new Element("div", {"styles": this.css.includeTitleActionNode}).inject(this.includeTitleNode);
        this.includeTitleTextNode = new Element("div", {"styles": this.css.includeTitleTextNode, "text": this.lp.include}).inject(this.includeTitleNode);
        this.includeTitleActionNode.addEvent("click", function(){
            this.addInclude();
        }.bind(this));

        this.propertyIncludeListArea = new Element("div", {
            "styles": {"overflow": "hidden"}
        }).inject(this.propertyDomArea);
    },
    addInclude: function(){



    },
    setPropertyContent: function(){
        var node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.id+":"}).inject(this.propertyContentArea);
        this.propertyIdNode = new Element("div", {"styles": this.css.propertyTextNode, "text": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.name+":"}).inject(this.propertyContentArea);
        this.propertyNameNode = new Element("input", {"styles": this.css.propertyInputNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.alias+":"}).inject(this.propertyContentArea);
        this.propertyAliasNode = new Element("input", {"styles": this.css.propertyInputNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.description+":"}).inject(this.propertyContentArea);
        this.propertyDescriptionNode = new Element("textarea", {"styles": this.css.propertyInputAreaNode, "value": ""}).inject(this.propertyContentArea);
    },
	loadPropertyResize: function(){
//		var size = this.propertyNode.getSize();
//		var position = this.propertyResizeBar.getPosition();
		this.propertyResize = new Drag(this.propertyResizeBar,{
			"snap": 1,
			"onStart": function(el, e){
				var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
				var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
				el.store("position", {"x": x, "y": y});
				
				var size = this.propertyNode.getSize();
				el.store("initialWidth", size.x);
			}.bind(this),
			"onDrag": function(el, e){
				var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
				var bodySize = this.content.getSize();
				var position = el.retrieve("position");
				var initialWidth = el.retrieve("initialWidth").toFloat();
				var dx = position.x.toFloat()-x.toFloat();
				
				var width = initialWidth+dx;
				if (width> bodySize.x/2) width =  bodySize.x/2;
				if (width<40) width = 40;
				this.contentNode.setStyle("margin-right", width+1);
				this.propertyNode.setStyle("width", width);
			}.bind(this)
		});
	},
	loadPropertyContentResize: function(){
		this.propertyContentResize = new Drag(this.propertyContentResizeNode, {
			"snap": 1,
			"onStart": function(el, e){
				var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
				var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
				el.store("position", {"x": x, "y": y});

				var size = this.propertyDomArea.getSize();
				el.store("initialHeight", size.y);
			}.bind(this),
			"onDrag": function(el, e){
				var size = this.propertyContentNode.getSize();

	//			var x = e.event.x;
				var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
				var position = el.retrieve("position");
				var dy = y.toFloat()-position.y.toFloat();

				var initialHeight = el.retrieve("initialHeight").toFloat();
				var height = initialHeight+dy;
				if (height<40) height = 40;
				if (height> size.y-40) height = size.y-40;

				this.propertyDomPercent = height/size.y;

				this.setPropertyContentResize();

			}.bind(this)
		});
	},
	setPropertyContentResize: function(){
		var size = this.propertyContentNode.getSize();
		var resizeNodeSize = this.propertyContentResizeNode.getSize();
		var height = size.y-resizeNodeSize.y;
		
		var domHeight = this.propertyDomPercent*height;
		var contentHeight = height-domHeight;
		
		this.propertyDomArea.setStyle("height", ""+domHeight+"px");
		this.propertyContentArea.setStyle("height", ""+contentHeight+"px");
	},
	

	
	//resizeNode------------------------------------------------
	resizeNode: function(){
        if (!this.isMax){
            var nodeSize = this.node.getSize();
            this.contentNode.setStyle("height", ""+nodeSize.y+"px");
            this.propertyNode.setStyle("height", ""+nodeSize.y+"px");

            var contentToolbarMarginTop = this.contentToolbarNode.getStyle("margin-top").toFloat();
            var contentToolbarMarginBottom = this.contentToolbarNode.getStyle("margin-bottom").toFloat();
            var allContentToolberSize = this.contentToolbarNode.getComputedSize();
            var y = nodeSize.y - allContentToolberSize.totalHeight - contentToolbarMarginTop - contentToolbarMarginBottom;
            this.editContentNode.setStyle("height", ""+y+"px");

            if (this.designNode){
                var designMarginTop = this.designNode.getStyle("margin-top").toFloat();
                var designMarginBottom = this.designNode.getStyle("margin-bottom").toFloat();
                y = nodeSize.y - allContentToolberSize.totalHeight - contentToolbarMarginTop - contentToolbarMarginBottom - designMarginTop - designMarginBottom;
                this.designNode.setStyle("height", ""+y+"px");
            }


            titleSize = this.propertyTitleNode.getSize();
            titleMarginTop = this.propertyTitleNode.getStyle("margin-top").toFloat();
            titleMarginBottom = this.propertyTitleNode.getStyle("margin-bottom").toFloat();
            titlePaddingTop = this.propertyTitleNode.getStyle("padding-top").toFloat();
            titlePaddingBottom = this.propertyTitleNode.getStyle("padding-bottom").toFloat();

            y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom;
            y = nodeSize.y-y;
            this.propertyContentNode.setStyle("height", ""+y+"px");
            this.propertyResizeBar.setStyle("height", ""+y+"px");

            this.setPropertyContentResize();

            titleSize = this.scriptListTitleNode.getSize();
            titleMarginTop = this.scriptListTitleNode.getStyle("margin-top").toFloat();
            titleMarginBottom = this.scriptListTitleNode.getStyle("margin-bottom").toFloat();
            titlePaddingTop = this.scriptListTitleNode.getStyle("padding-top").toFloat();
            titlePaddingBottom = this.scriptListTitleNode.getStyle("padding-bottom").toFloat();
            nodeMarginTop = this.scriptListAreaSccrollNode.getStyle("margin-top").toFloat();
            nodeMarginBottom = this.scriptListAreaSccrollNode.getStyle("margin-bottom").toFloat();

            y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom+nodeMarginTop+nodeMarginBottom;
            y = nodeSize.y-y;

            var leftToolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : {x:0,y:0};

            this.scriptListAreaSccrollNode.setStyle("height", ""+(y-leftToolbarSize.y)+"px");
            this.scriptListResizeNode.setStyle("height", ""+y+"px");
        }
	},
	
	//loadForm------------------------------------------
    loadScript: function(){
        //this.scriptTab.addTab(node, title);
		this.getScriptData(this.options.id, function(data){
			this.script = new MWF.xApplication.portal.ScriptDesigner.Script(this, data);
			this.script.load();

            if (this.status){
                if (this.status.openScripts){
                    this.status.openScripts.each(function(id){
                        this.loadScriptData(id, function(data){
                            var showTab = true;
                            if (this.status.currentId){
                                if (this.status.currentId!=data.id) showTab = false;
                            }
                            var script = new MWF.xApplication.portal.ScriptDesigner.Script(this, data, {"showTab": showTab});
                            script.load();
                        }.bind(this), true);
                    }.bind(this));

                    this.status.openScripts = [];
                }
            };
            // if (!this.scriptHelpMenu){
            //     MWF.require("MWF.widget.ScriptHelp", function(){
            //         this.scriptHelpMenu = new MWF.widget.ScriptHelp($("MWFScriptAutoCode"), this.script.editor);
            //         this.scriptHelpMenu.getEditor = function(){
            //             if (this.scriptTab.showPage) return this.scriptTab.showPage.script.editor.editor;
            //             return null;
            //         }.bind(this)
            //     }.bind(this));
            // }
		}.bind(this));
	},

    getScriptData: function(id, callback){
		if (!id){
			this.loadNewScriptData(callback);
		}else{
			this.loadScriptData(id, callback);
		}
	},

    loadNewScriptData: function(callback){
        this.actions.getUUID(function(id){
            var data = {
                "name": "",
                "id": id,
                "application": this.application.id,
                "alias": "",
                "description": "",
                "language": "javascript",
                "dependScriptList": [],
                "isNewScript": true,
                "text": ""
            }
            this.createListScriptItem(data, true);
            if (callback) callback(data);
        }.bind(this))
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

                if (!this.application){
                    this.actions.getApplication(this.data.application, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        if (callback) callback(data);
                    }.bind(this));
                }else{
                    if (callback) callback(data);
                }
			}
		}.bind(this));
	},

    saveScript: function(){
        if (this.scriptTab.showPage){
            var script = this.scriptTab.showPage.script;
            script.save(function(){
                if (script==this.script){
                    var name = script.data.name;
                    this.setTitle(MWF.xApplication.portal.ScriptDesigner.LP.title + "-"+name);
                    this.options.desktopReload = true;
                    this.options.id = script.data.id;
                }
            }.bind(this));
        }
	},
    saveDictionaryAs: function(){
        this.dictionary.saveAs();
	},
    dictionaryExplode: function(){
        this.dictionary.explode();
    },
    dictionaryImplode: function(){
        this.dictionary.implode();
    },
	recordStatus: function(){
        var application = o2.typeOf(this.application) === "object" ? {
            name: this.application.name,
            id: this.application.id
        } : this.application;
        if (this.scriptTab){
            var openScripts = [];
            this.scriptTab.pages.each(function(page){
                if (page.script.data.id!=this.options.id) openScripts.push(page.script.data.id);
            }.bind(this));
            var currentId = this.scriptTab.showPage.script.data.id;
            var status = {
                "id": this.options.id,
                "application": application,
                "openScripts": openScripts,
                "currentId": currentId
            };
            return status;
        }
		return {"id": this.options.id, "application": application};
	},
    showScriptVersion: function(){

        this.versionNode = new Element("div");
        this.dlg = o2.DL.open({
            "title": MWF.xApplication.portal.ScriptDesigner.LP.version["title"],
            "content": this.versionNode,
            "offset": {"y": -100},
            "isMax": false,
            "width": 500,
            "height": 300,
            "buttonList": [
                {
                    "type": "cancel",
                    "text": MWF.xApplication.portal.ScriptDesigner.LP.version["close"],
                    "action": function(){ this.close(); }
                }
            ],
            "onPostShow": function(){
                this.loadVersionList();
            }.bind(this),
            "onPostClose": function(){
                this.dlg = null;
            }.bind(this)
        });
    },
    loadVersionList : function(){
        var tableHtml = "<table width='100%' cellspacing='0' cellpadding='3' style='margin-top: 1px'><tr>" +
            "<th>"+MWF.xApplication.portal.ScriptDesigner.LP.version["no"]+"</th>" +
            "<th>"+MWF.xApplication.portal.ScriptDesigner.LP.version["updateTime"]+"</th>" +
            "<th>"+MWF.xApplication.portal.ScriptDesigner.LP.version["op"]+"</th>" +
            "</tr></table>";
        this.versionNode.set("html", tableHtml);
        this.versionTable = this.versionNode.getElement("table");
        o2.Actions.load("x_portal_assemble_designer").ScriptVersionAction.listWithScript(this.options.id, function(json){
            this.versionList = json.data;
            this.versionList.sort(function (a, b) {
                return new Date(b.updateTime) - new Date(a.updateTime)
            });
            this.versionList.each(function (version,index) {
                var node = new Element("tr").inject(this.versionTable);
                var html = "<td>"+(index+1)+"</td>" +
                    "<td>"+version.updateTime+"</td>" +
                    "<td></td>";
                node.set("html", html);
                var actionNode = new Element("div",{"styles":{
                        "width": "60px",
                        "padding": "0px 3px",
                        "border-radius": "20px",
                        "cursor" : "pointer",
                        "color": "#ffffff",
                        "background-color": "#4A90E2",
                        "float": "left",
                        "margin-right": "2px",
                        "text-align": "center",
                        "font-weight": "100"
                    }}).inject(node.getLast("td"));
                actionNode.set("text", MWF.xApplication.portal.ScriptDesigner.LP.version["resume"]);
                actionNode.addEvent("click",function (e) {

                    console.log(this);
                    var _self = this;
                    this.confirm("warn", e,  MWF.xApplication.portal.ScriptDesigner.LP.version["resumeConfirm"], MWF.xApplication.portal.ScriptDesigner.LP.version["resumeInfo"], 460, 120, function(){
                        _self.resumeScript(version);
                        this.close();
                    }, function(){
                        this.close();
                    });
                }.bind(this));
            }.bind(this))
        }.bind(this));
    },
    resumeScript : function(version){
        o2.Actions.load("x_portal_assemble_designer").ScriptVersionAction.get(version.id, function( json ){
            var scriptData = JSON.parse(json.data.data);
            this.script.editor.setValue(scriptData.text);

            this.dlg.close();
            this.notice(MWF.xApplication.portal.ScriptDesigner.LP.version["resumeSuccess"]);
        }.bind(this), null, false);
    },
});
