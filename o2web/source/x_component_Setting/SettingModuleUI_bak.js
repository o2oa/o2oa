MWF.xDesktop.requireApp("Setting", "SettingLoginUI", null, false);

MWF.xApplication.Setting.UIModuleDocument = new Class({
    Extends: MWF.xApplication.Setting.UILoginDocument,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.ui_moduleSetting);

        MWF.Actions.get("x_component_assemble_control").listComponent(function(json){
            this.moduleList = new MWF.xApplication.Setting.UIModuleDocument.ModuleList(this, this.node, json.data, {
                "title": this.lp.ui_module_modules,
                "infor": this.lp.ui_module_modules_infor,
                "actionTitle": this.lp.ui_module_modules_Action,
                "type": "loginStyle",
                "onCreateStyle": function(name, css){
                    this.createStyle(name, css);
                }.bind(this)
            });
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


MWF.xApplication.Setting.UIModuleDocument.ModuleList = new Class({
    Extends: MWF.xApplication.Setting.UILoginDocument.StyleList,

    addItem: function(item){
        this.items.push(new MWF.xApplication.Setting.UIModuleDocument.Item(this, item));
    },
    createNewCustomStyle: function(e){
        layout.desktop.openApplication(e, "Deployment");
    }
});

MWF.xApplication.Setting.UIModuleDocument.Item = new Class({
    Extends: MWF.xApplication.Setting.UILoginDocument.Style.Item,

    load: function(){
        this.itemArea = new Element("div", {"styles": this.css.explorerContentListModuleActionAreaNode}).inject(this.content);
        this.itemIconArea = new Element("div", {"styles": this.css.explorerContentListItemModuleIconAreaNode}).inject(this.itemArea);
        this.itemIcon = new Element("div", {"styles": this.css.explorerContentListItemModuleIconNode}).inject(this.itemIconArea);

        // if (this.data.enabled){
        //     this.checkIcon = new Element("div", {"styles": this.css.explorerContentListCheckIconAreaNode}).inject(this.itemArea);
        //     this.checkIcon.set("title", this.lp.ui_login_current);
        // }else{
        //     this.checkIcon = new Element("div", {"styles": this.css.explorerContentListNotCheckIconAreaNode}).inject(this.itemArea);
        //     this.checkIcon.set("title", this.lp.ui_login_setCurrent);
        // }

        this.itemTextArea = new Element("div", {"styles": this.css.explorerContentListModuleActionTextAreaNode}).inject(this.itemArea);

        var icon = "/x_component_"+this.data.path.replace(/\./g, "_")+"/$Main/"+this.data.iconPath;
        this.itemIcon.setStyle("background", "url("+icon+") no-repeat center center");
        this.itemTextArea.set("text", this.data.title);

        //if (this.data.name!="default"){
        //    this.editAction = new Element("div", {"styles": this.css.explorerContentStyleActionNode, "text": this.lp.edit}).inject(this.content);
            //this.copyAction = new Element("div", {"styles": this.css.explorerContentStyleActionNode, "text": this.lp.copy}).inject(this.content);
        //    this.deleteAction = new Element("div", {"styles": this.css.explorerContentStyleActionNode, "text": this.lp.delete}).inject(this.content);
        // }else{
        //     this.copyAction = new Element("div", {"styles": this.css.explorerContentStyleActionNode, "text": this.lp.copy}).inject(this.content);
        // }

        //this.setEvents();
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
                    window.open("/x_desktop/index.html?style="+this.item.data.id+"&styletype="+((this.item.data.name=="default") ? "default" : "custom"));
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