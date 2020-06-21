MWF.xDesktop.requireApp("Setting", "SettingLoginUI", null, false);

MWF.xApplication.Setting.UIIndexDocument = new Class({
    Extends: MWF.xApplication.Setting.UILoginDocument,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.ui_indexSetting);


        MWF.getJSON(o2.session.path+"/xDesktop/$Layout/styles.json", function(json){
            MWF.UD.getPublicData("indexThemes", function(themesJson){
                var init = false;
                if (themesJson){
                    this.enabledThemes = themesJson;
                }else{
                    this.enabledThemes = [];
                    init = true;
                }

                json.map(function(item, index){
                    item.id = item.style;
                    item.name = "default";
                    //item.url = "../x_desktop/res/mwf4/package/xDesktop/$Layout/"+item.style;
                    item.url = item.style;
                    item.enabled = (!this.enabledThemes.length || this.enabledThemes.indexOf(item.style)!=-1);
                    if (init) this.enabledThemes.push(item.style);
                    return item;
                }.bind(this));

                this.styleList = new MWF.xApplication.Setting.UIIndexDocument.StyleList(this, this.node, json, {
                    "title": this.lp.ui_index_systemStyle,
                    "infor": this.lp.ui_index_systemStyle_infor,
                    "type": "loginStyle",
                    "onCreateStyle": function(name, css){
                        this.createStyle(name, css);
                    }.bind(this)
                });

                MWF.UD.getPublicData("indexStyleList", function(json){

                    this.indexStyleList = json;
                    if (!this.indexStyleList) this.indexStyleList = {"styleList": []};

                    this.styleList = new MWF.xApplication.Setting.UIIndexDocument.StyleList(this, this.node, this.indexStyleList.styleList, {
                        "title": this.lp.ui_index_customStyle,
                        "infor": this.lp.ui_index_customStyle_infor,
                        "actionTitle": this.lp.ui_index_customStyle_Action,
                        "type": "loginStyle",
                        "onCreateStyle": function(name, css){
                            this.createStyle(name, css);
                        }.bind(this)
                    });
                }.bind(this));


            }.bind(this));
        }.bind(this));
    },

    createStyle: function(name, css){

        var id = (new MWF.widget.UUID()).id;
        var listItem = {
            "title": name,
            "name": "indexStyle_"+id,
            "id": "indexStyle_"+id,
            "preview": css.desktop.desktop.background,
            "enabled": true
        };
        var styleData = {
            "title": name,
            "name": "indexStyle_"+id,
            "id": "indexStyle_"+id,
            "data": css
        };
        this.indexStyleList.styleList.push(listItem);

        MWF.UD.putPublicData("indexStyle_"+id, styleData, function(){
            MWF.UD.putPublicData("indexStyleList", this.indexStyleList, {
                "success": function(){
                    this.styleList.addItem(listItem);
                }.bind(this),
                "failure": function(){
                    MWF.UD.deletePublicData("indexStyle_"+id);
                }.bind(this)
            });
        }.bind(this));
    }
});


MWF.xApplication.Setting.UIIndexDocument.StyleList = new Class({
    Extends: MWF.xApplication.Setting.UILoginDocument.StyleList,

    addItem: function(item){
        this.items.push(new MWF.xApplication.Setting.UIIndexDocument.Item(this, item));
    }
});

MWF.xApplication.Setting.UIIndexDocument.Item = new Class({
    Extends: MWF.xApplication.Setting.UILoginDocument.Style.Item,
    getCss: function(){
        var css = null;
        if (this.data.url){
            var cssDesktopPath = o2.session.path+"/xDesktop/$Layout/"+this.data.url+"/css.wcss";
            cssDesktopPath = (cssDesktopPath.indexOf("?")!=-1) ? cssDesktopPath+"&v="+COMMON.version : cssDesktopPath+"?v="+COMMON.version;
            var cssWindowPath = o2.session.path+"/xDesktop/$Window/desktop_"+this.data.url+"/css.wcss";
            cssWindowPath = (cssWindowPath.indexOf("?")!=-1) ? cssWindowPath+"&v="+COMMON.version : cssWindowPath+"?v="+COMMON.version;

            css = {};
            MWF.getJSON(cssDesktopPath, function(json){
                css.desktop = json;
            }.bind(this), false);
            MWF.getJSON(cssWindowPath, function(json){
                css.window = json;
            }.bind(this), false);
        }else{
            MWF.UD.getPublicData(this.data.name, function(json){
                css = json.data;
            }, false);
        }
        return css;
    },
    showPreview: function(){
        this.styleCss = this.getCss();
        this.previewNode = new Element("div.previewNode", {"styles": {
            "margin-top": "10px",
            "position": "relative"
        }}).inject(this.itemArea);

        MWF.xDesktop.requireApp("Setting", "preview.Layout", function(){
            var layout = new MWF.xApplication.Setting.preview.Layout(this.previewNode, {
                styles: {"position": "absolute", "height": "720px", "width": "960px", "box-shadow": "0px 0px 30px #666666"}
            });
            layout.app = this.app;
            layout.css = this.styleCss.desktop;
            layout.windowCss = this.styleCss.window;
            layout.load();

            this.previewMaskNode = new Element("div", {"styles": {"position": "absolute", "top": "0px", "left": "0px"}}).inject(this.previewNode);
            var size = layout.node.getSize();
            var zidx = layout.node.getStyle("z-index") || 0;
            if (MWF.xDesktop.zIndexPool) zidx = MWF.xDesktop.zIndexPool.applyZindex();

            this.previewMaskNode.setStyles({"width": ""+size.x+"px", "height": ""+size.y+"px", "z-index": zidx});
            this.previewMaskNode.addEvents({
                "click": function(e){
                    window.open(o2.filterUrl("../x_desktop/index.html?style="+this.data.id+"&styletype="+((this.data.name=="default") ? "default" : "custom")));
                    e.stopPropagation();
                }.bind(this),
                "mousedown": function(e){e.stopPropagation();},
                "mouseup": function(e){e.stopPropagation();}
            });

            this.previewNode.setStyles({
                "transform-origin": "0px 0px",
                "transform": "scale(0.4)"
            })
        }.bind(this));
    },
    hidePreview: function(){
        this.previewMaskNode.destroy();
        this.previewNode.getFirst().destroy();
        this.previewNode.empty();
        this.previewNode.destroy();
        this.previewMaskNode = null;
        this.previewNode = null;
    },

    setCurrent: function(e){
        if (this.data.enabled){
            this.list.document.enabledThemes.erase(this.data.id);
            this.setUncurrentStyle();
        }else{
            this.list.document.enabledThemes.push(this.data.id);
            this.setCurrentStyle()
        }
        MWF.UD.putPublicData("indexThemes", this.list.document.enabledThemes);
        MWF.UD.putPublicData("indexStyleList", this.list.document.indexStyleList);
        e.stopPropagation();
    },
    setUncurrentStyle: function(){
        this.data.enabled = false;
        this.checkIcon.setStyles(this.css.explorerContentListNotCheckIconAreaNode);
        this.checkIcon.set("title", this.lp.ui_index_disabled);
    },
    setCurrentStyle: function(nosave){
        this.data.enabled = true;
        this.checkIcon.setStyles(this.css.explorerContentListCheckIconAreaNode);
        this.checkIcon.set("title", this.lp.ui_index_enabled);
    },

    createNewCustomStyleData: function(dlg){
        var name = dlg.content.getElement("input").get("value");
        if (!name){
            this.app.notice(ui_login_customStyle_newName_empty, "error");
            return false;
        }else{
            var css = this.getCss();
            var newCss = Object.clone(css);
            this.list.fireEvent("createStyle", [name, newCss]);
            dlg.close();
        }
    },
    editStyle: function(){
        this.editor = new MWF.xApplication.Setting.UIIndexDocument.Editor(this);
    },
    deleteStyleData: function(){
        this.list.document.indexStyleList.styleList.erase(this.data);
        MWF.UD.deletePublicData(this.data.id, function(){
            MWF.UD.putPublicData("indexStyleList", this.list.document.indexStyleList, function(){
                this.list.items.erase(this);
                this.destroy();
            }.bind(this));
        }.bind(this))
    }
});


MWF.xApplication.Setting.UIIndexDocument.Editor = new Class({
    Extends: MWF.xApplication.Setting.UILoginDocument.Style.Editor,

    createCssEditor: function(){
        var _self = this;
        MWF.require("MWF.widget.Maplist", function(){
            Object.each(this.styleCss.desktop, function(v, k){
                var mapListNode = new Element("div", {"styles": this.css.explorerContentStyleEditMapNode}).inject(this.editorArea);
                var mList = new MWF.widget.Maplist.Style(mapListNode, {"title": "desktop."+k, "style": "styleEditor",
                    "onChange": function(){
                        _self.styleCss.desktop[k] = this.toJson();
                        if (k=="desktop"){
                            _self.item.data.preview = _self.styleCss.desktop[k].background;
                            MWF.UD.putPublicData("indexStyleList", _self.item.list.document.indexStyleList);
                        }
                        _self.showPreview();

                        var o = {
                            "name": _self.item.data.name,
                            "title": _self.item.data.title,
                            "id": _self.item.data.id,
                            "data": _self.styleCss
                        };
                        MWF.UD.putPublicData(_self.item.data.id, o);
                    }
                });
                mList.app = this.app;
                mList.load(v);
            }.bind(this));

            Object.each(this.styleCss.window, function(v, k){
                var mapListNode = new Element("div", {"styles": this.css.explorerContentStyleEditMapNode}).inject(this.editorArea);
                var mList = new MWF.widget.Maplist.Style(mapListNode, {"title": "window."+k, "style": "styleEditor",
                    "onChange": function(){
                        _self.styleCss.window[k] = this.toJson();
                        _self.showPreview();

                        var o = {
                            "name": _self.item.data.name,
                            "title": _self.item.data.title,
                            "id": _self.item.data.id,
                            "data": _self.styleCss
                        };
                        MWF.UD.putPublicData(_self.item.data.id, o);
                    }
                });
                mList.app = this.app;
                mList.load(v);
            }.bind(this));
        }.bind(this));
    },
    showPreview: function(){
        this.previewArea.empty();
        this.previewNode = new Element("div", {"styles": {"position": "relative", "height": "216px"}}).inject(this.previewArea);

        //this.styleCss = this.getCss();
        // this.previewNode = new Element("div.previewNode", {"styles": {
        //     "margin-top": "10px",
        //     "position": "relative"
        // }}).inject(this.itemArea);

        MWF.xDesktop.requireApp("Setting", "preview.Layout", function(){
            var layout = new MWF.xApplication.Setting.preview.Layout(this.previewNode, {
                styles: {"position": "absolute", "height": "720px", "width": "960px", "box-shadow": "0px 0px 30px #666666"}
            });
            layout.app = this.app;
            layout.css = this.styleCss.desktop;
            layout.windowCss = this.styleCss.window;
            layout.load();

            this.previewMaskNode = new Element("div", {"styles": {"position": "absolute", "top": "0px", "left": "0px"}}).inject(this.previewNode);
            var size = layout.node.getSize();
            var zidx = layout.node.getStyle("z-index") || 0;
            if (MWF.xDesktop.zIndexPool) zidx = MWF.xDesktop.zIndexPool.applyZindex();

            this.previewMaskNode.setStyles({"width": ""+size.x+"px", "height": ""+size.y+"px", "z-index": zidx});
            this.previewMaskNode.addEvents({
                "click": function(e){
                    window.open(o2.filterUrl("../x_desktop/index.html?style="+this.item.data.id+"&styletype="+((this.item.data.name=="default") ? "default" : "custom")));
                    e.stopPropagation();
                }.bind(this),
                "mousedown": function(e){e.stopPropagation();},
                "mouseup": function(e){e.stopPropagation();}
            });

            this.previewNode.setStyles({
                "transform-origin": "0px 0px",
                "transform": "scale(0.6)"
            })
        }.bind(this));

    }
});
