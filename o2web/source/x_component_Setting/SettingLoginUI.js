MWF.xDesktop.requireApp("Setting", "Document", null, false);

MWF.xApplication.Setting.UILoginDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.ui_loginSetting);

        var defaultStyleData = {
            "title": this.lp.ui_login_default,
            "name": "default",
            "url": o2.session.path+"/xDesktop/$Authentication/default",
            "enabled": true
        };

        MWF.UD.getPublicData("loginStyleList", function(json){
            this.loginStyleList = json;
            if (!this.loginStyleList) this.loginStyleList = {"enabledId": "", "styleList": []};

            defaultStyleData.enabled = !this.loginStyleList.enabledId;
            this.defaultStyle = new MWF.xApplication.Setting.UILoginDocument.Style(this, this.node, defaultStyleData, {
                "title": this.lp.ui_login_defaultStyle,
                "infor": this.lp.ui_login_defaultStyle_infor,
                "onCreateStyle": function(name, css){
                    this.createStyle(name, css);
                }.bind(this)
            });

            this.styleList = new MWF.xApplication.Setting.UILoginDocument.StyleList(this, this.node, this.loginStyleList.styleList, {
                "title": this.lp.ui_login_customStyle,
                "infor": this.lp.ui_login_customStyle_infor,
                "actionTitle": this.lp.ui_login_customStyle_Action,
                "defaultStyleData": defaultStyleData,
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
            "name": "loginStyle_"+id,
            "enabled": false
        };
        var styleData = {
            "title": name,
            "name": "loginStyle_"+id,
            "data": css
        };
        this.loginStyleList.styleList.push(listItem);

        MWF.UD.putPublicData("loginStyle_"+id, styleData, function(){
            MWF.UD.putPublicData("loginStyleList", this.loginStyleList, {
                "success": function(){
                    this.styleList.addItem(listItem);
                }.bind(this),
                "failure": function(){
                    MWF.UD.deletePublicData("loginStyle_"+id);
                }.bind(this)
            });
        }.bind(this));
    }
});


MWF.xApplication.Setting.UILoginDocument.StyleList = new Class({
    Implements: [Options, Events],
    initialize: function(doc, contentAreaNode, data, options){
        this.setOptions(options);
        this.document = doc;
        this.explorer = this.document.explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.contentAreaNode = contentAreaNode;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.data = data;
        this.items = [];
        this.load();
    },
    load: function(){
        var titleNode = new Element("div", {"styles": this.css.explorerContentItemTitleNode, "text": this.options.title}).inject(this.contentAreaNode);
        if (this.options.infor) var titleInforArea = new Element("div", {"styles": this.css.explorerContentInputInforNode, "text": this.options.infor}).inject(this.contentAreaNode);

        var inputArea = new Element("div", {"styles": this.css.explorerContentListAreaNode}).inject(this.contentAreaNode);

        if (this.options.actionTitle) this.createCustomStyle(inputArea);

        this.itemArea = new Element("div", {"styles": {"overflow": "hidden", "clear": "both"}}).inject(inputArea);
        if (this.data && this.data.length){
            this.data.each(function(styleData){
                this.addItem(styleData);
                //new MWF.xApplication.Setting.UILoginDocument.Style.Item(this, styleData);
            }.bind(this));
        }
    },
    addItem: function(item){
        this.items.push(new MWF.xApplication.Setting.UILoginDocument.Style.Item(this, item));
    },

    createCustomStyle: function(inputArea){
        this.actionArea = new Element("div", {"styles": this.css.explorerContentListStyleActionAreaNode}).inject(inputArea);
        var actionIconArea = new Element("div", {"styles": this.css.explorerContentListActionIconAreaNode}).inject(this.actionArea);
        var actionIcon = new Element("div", {"styles": this.css.explorerContentListActionIconNode}).inject(actionIconArea);
        if (this.type=="object") actionIcon.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/edit.png) no-repeat center center");

        var actionTextArea = new Element("div", {"styles": this.css.explorerContentListActionTextAreaNode, "text": this.options.actionTitle}).inject(this.actionArea);

        this.actionArea.addEvents({
            "mouseover": function(){this.actionArea.setStyles(this.css.explorerContentListStyleActionAreaNode_over);}.bind(this),
            "mouseout": function(){this.actionArea.setStyles(this.css.explorerContentListStyleActionAreaNode);}.bind(this),
            "mousedown": function(){this.actionArea.setStyles(this.css.explorerContentListStyleActionAreaNode_down);}.bind(this),
            "mouseup": function(){this.actionArea.setStyles(this.css.explorerContentListStyleActionAreaNode_over);}.bind(this),
            "click": function(e){
                this.createNewCustomStyle(e);
            }.bind(this)
        });
    },
    getDefaultCss: function(){
        var css = null;
        var cssPath = this.options.defaultStyleData.url+"/css.wcss";
        cssPath = (cssPath.indexOf("?")!=-1) ? cssPath+"&v="+COMMON.version : cssPath+"?v="+COMMON.version;
        MWF.getJSON(cssPath, function(json){
            css = json;
        }.bind(this), false);
        return css;
    },
    createNewCustomStyle: function(e){
        var p = this.actionArea.getPosition(this.actionArea.getOffsetParent());
        var width = 470;
        var height = 110;

        var size = this.app.content.getSize();
        if (p.y+height>size.y) p.y = size.y-height-10;
        if (p.y<0) p.y = 10;
        if (p.x+width>size.x) p.x = size.x-width-10;
        if (p.x<0) p.x = 10;

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function(){
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.lp.ui_login_customStyle_newName,
                "style": "settingStyle",
                "top": p.y,
                "left": p.x,
                "fromTop":p.y,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "html": "",
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": this.lp.ok,
                        "action": function(){
                            _self.createNewCustomStyleData(this);
                        }
                    },
                    {
                        "text": this.lp.cancel,
                        "action": function(){this.close();}
                    }
                ]
            });
            dlg.show();
            var content = new Element("div", {"styles": this.css.explorerContentListStyleEditAreaNode}).inject(dlg.content);
            new Element("input", {"styles": this.css.explorerContentListEditInputNode}).inject(content);
        }.bind(this));

    },

    createNewCustomStyleData: function(dlg){
        var name = dlg.content.getElement("input").get("value");
        if (!name){
            this.app.notice(ui_login_customStyle_newName_empty, "error");
            return false;
        }else{
            var css = this.getDefaultCss();
            var newCss = Object.clone(css);
            this.fireEvent("createStyle", [name, newCss]);
            dlg.close();
        }
    }

});

MWF.xApplication.Setting.UILoginDocument.Style = new Class({
    Implements: [Options, Events],
    initialize: function(doc, contentAreaNode, data, options){
        this.setOptions(options);
        this.document = doc;
        this.explorer = this.document.explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.contentAreaNode = contentAreaNode;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.data = data;
        this.load();
    },
    load: function(){
        var titleNode = new Element("div", {"styles": this.css.explorerContentItemTitleNode, "text": this.options.title}).inject(this.contentAreaNode);
        if (this.options.infor) var titleInforArea = new Element("div", {"styles": this.css.explorerContentInputInforNode, "text": this.options.infor}).inject(this.contentAreaNode);

        var inputArea = new Element("div", {"styles": this.css.explorerContentListAreaNode}).inject(this.contentAreaNode);
        this.itemArea = new Element("div", {"styles": {"overflow": "hidden", "clear": "both"}}).inject(inputArea);

        this.item = new MWF.xApplication.Setting.UILoginDocument.Style.Item(this, this.data);

    }
});


MWF.xApplication.Setting.UILoginDocument.Style.Item = new Class({
    Extends: MWF.xApplication.Setting.Document.List.Item,

    load: function(){
        this.itemArea = new Element("div", {"styles": this.css.explorerContentListStyleActionAreaNode}).inject(this.content);
        this.itemIconArea = new Element("div", {"styles": this.css.explorerContentListItemIconAreaNode}).inject(this.itemArea);
        this.itemIcon = new Element("div", {"styles": this.css.explorerContentListItemIconNode}).inject(this.itemIconArea);

        if (this.data.enabled){
            this.checkIcon = new Element("div", {"styles": this.css.explorerContentListCheckIconAreaNode}).inject(this.itemArea);
            this.checkIcon.set("title", this.lp.ui_login_current);
        }else{
            this.checkIcon = new Element("div", {"styles": this.css.explorerContentListNotCheckIconAreaNode}).inject(this.itemArea);
            this.checkIcon.set("title", this.lp.ui_login_setCurrent);
        }

        this.itemTextArea = new Element("div", {"styles": this.css.explorerContentListActionTextAreaNode}).inject(this.itemArea);
        this.itemIcon.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/style.png) no-repeat center center");
        this.itemTextArea.set("text", this.data.title);

        if (this.data.name!="default"){
            this.editAction = new Element("div", {"styles": this.css.explorerContentStyleActionNode, "text": this.lp.edit}).inject(this.content);
            this.copyAction = new Element("div", {"styles": this.css.explorerContentStyleActionNode, "text": this.lp.copy}).inject(this.content);
            this.deleteAction = new Element("div", {"styles": this.css.explorerContentStyleActionNode, "text": this.lp.delete}).inject(this.content);
        }else{
            this.copyAction = new Element("div", {"styles": this.css.explorerContentStyleActionNode, "text": this.lp.copy}).inject(this.content);
        }

        this.setEvents();
    },
    setEvents: function(){
        var _self = this;
        this.itemArea.addEvents({
            "mouseover": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListStyleActionAreaNode_over);},
            "mouseout": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListStyleActionAreaNode);},
            "mousedown": function(){this.setStyles(_self.css.explorerContentListStyleActionAreaNode_down);},
            "mouseup": function(){this.setStyles(_self.css.explorerContentListStyleActionAreaNode_over);},
            "click": function(){_self.preview(this);}
        });
        this.checkIcon.addEvents({
            "mousedown": function(e){e.stopPropagation();},
            "mouseup": function(e){e.stopPropagation();},
            "click": function(e){_self.setCurrent(e);}
        });

        if (this.editAction){
            this.editAction.addEvents({
                "mouseover": function(){this.setStyles(_self.css.explorerContentStyleActionNode_over);},
                "mouseout": function(){this.setStyles(_self.css.explorerContentStyleActionNode);},
                "click": function(){_self.editStyle(this);}
            });
        }
        if (this.copyAction){
            this.copyAction.addEvents({
                "mouseover": function(){this.setStyles(_self.css.explorerContentStyleActionNode_over);},
                "mouseout": function(){this.setStyles(_self.css.explorerContentStyleActionNode);},
                "click": function(){_self.copyStyle(this);}
            });
        }
        if (this.deleteAction){
            this.deleteAction.addEvents({
                "mouseover": function(){this.setStyles(_self.css.explorerContentStyleActionNode_over);},
                "mouseout": function(){this.setStyles(_self.css.explorerContentStyleActionNode);},
                "click": function(e){_self.deleteStyle(e);}
            });
        }
    },
    copyStyle: function(){
        var p = this.itemArea.getPosition(this.itemArea.getOffsetParent());

        var width = 600;
        var height = 110;

        var size = this.app.content.getSize();
        if (p.y+height>size.y) p.y = size.y-height-10;
        if (p.y<0) p.y = 10;
        if (p.x+width>size.x) p.x = size.x-width-10;
        if (p.x<0) p.x = 10;

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function(){
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.lp.ui_login_customStyle_newName,
                "style": "settingStyle",
                "top": p.y,
                "left": p.x,
                "fromTop":p.y,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "html": "",
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": this.lp.ok,
                        "action": function(){
                            _self.createNewCustomStyleData(this);
                        }
                    },
                    {
                        "text": this.lp.cancel,
                        "action": function(){this.close();}
                    }
                ]
            });
            dlg.show();
            var content = new Element("div", {"styles": this.css.explorerContentListStyleEditAreaNode}).inject(dlg.content);
            new Element("input", {"styles": this.css.explorerContentListEditInputNode, "value": this.data.title+this.lp.copyName}).inject(content);
        }.bind(this));
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
        this.editor = new MWF.xApplication.Setting.UILoginDocument.Style.Editor(this);
    },

    deleteStyle: function(e){
        var _self = this;
        var t = this.lp.ui_login_delete_confirm.replace("{title}", this.data.title);
        this.app.confirm("wram", e, this.lp.ui_login_delete_confirmTitle, t, 400, 150, function(){
            _self.deleteStyleData();
            this.close();
        }, function(){this.close()});
    },
    deleteStyleData: function(){
        if (this.list.document.loginStyleList.enabledId==this.data.name){
            this.list.document.defaultStyle.item.setCurrentStyle(true);
        }
        this.list.document.loginStyleList.styleList.erase(this.data);
        MWF.UD.deletePublicData(this.data.name, function(){
            MWF.UD.putPublicData("loginStyleList", this.list.document.loginStyleList, function(){
                this.list.items.erase(this);
                this.destroy();
            }.bind(this));
        }.bind(this))
    },
    destroy: function(){
        this.itemArea.destroy();
        if (this.editAction) this.editAction.destroy();
        if (this.deleteAction) this.deleteAction.destroy();
        if (this.copyAction) this.copyAction.destroy();
        MWF.release(this);
    },
    setCurrent: function(e){
        var _self = this;
        var t = this.lp.ui_login_setCurrent_confirm.replace("{title}", this.data.title);
        this.app.confirm("infor", e, this.lp.ui_login_setCurrent_confirmTitle, t, 400, 150, function(){
            _self.setCurrentStyle();
            this.close();
        }, function(){this.close()});
        e.stopPropagation();
    },
    setUncurrent: function(){
        this.data.enabled = false;
        this.checkIcon.setStyles(this.css.explorerContentListNotCheckIconAreaNode);
        this.checkIcon.set("title", this.lp.ui_login_setCurrent);
    },
    setCurrentStyle: function(nosave){
        this.list.document.defaultStyle.item.setUncurrent();
        this.list.document.styleList.items.each(function(item){
            item.setUncurrent();
        }.bind(this));

        this.list.document.loginStyleList.enabledId = (this.data.name=="default") ? "" : this.data.name;
        this.data.enabled = true;
        this.checkIcon.setStyles(this.css.explorerContentListCheckIconAreaNode);
        this.checkIcon.set("title", this.lp.ui_login_current);
        if (!nosave) MWF.UD.putPublicData("loginStyleList", this.list.document.loginStyleList);
    },
    preview: function(){
        if (!this.isSelected){
            this.itemArea.setStyle("height", "340px");
            this.isSelected = true;
            this.showPreview();
        }else{
            this.itemArea.setStyle("height", "40px");
            this.isSelected = false;
            this.hidePreview();
        }
    },
    getCss: function(){
        var css = null;
        if (this.data.url){
            var cssPath = this.data.url+"/css.wcss";
            cssPath = (cssPath.indexOf("?")!=-1) ? cssPath+"&v="+COMMON.version : cssPath+"?v="+COMMON.version;
            MWF.getJSON(cssPath, function(json){
                css = json;
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
        this.previewNode = new Element("div", {"styles": {"margin-top": "10px", "margin-bottom": "20px", "position": "relative"}}).inject(this.itemArea);
        MWF.require("MWF.xDesktop.Authentication", function(){
            //var action = new MWF.xApplication.Authentication.Actions.RestActions();
            var form = new MWF.xDesktop.Authentication.LoginForm(this, {}, {
                "draggable": false,
                "closeAction": false,
                "hasMask" : false,
                "relativeToApp" : false,
                "isLimitSize": false,
                "hasScroll": false,
                "ifFade": false,
                "top": "0",
                "left": "0"
            }, {
                "css": this.styleCss,
                "container" : this.previewNode,
                "lp": MWF.LP.authentication
                //"actions": action
            });
            form.create();

            this.previewMaskNode = new Element("div", {"styles": {"position": "absolute", "top": "0px", "left": "0px"}}).inject(this.previewNode);

            var size = form.formAreaNode.getSize();
            var zidx = form.formAreaNode.getStyle("z-index");
            this.previewMaskNode.setStyles({"width": ""+size.x+"px", "height": ""+size.y+"px", "z-index": zidx+1});
            this.previewMaskNode.addEvents({
                "click": function(e){e.stopPropagation();},
                "mousedown": function(e){e.stopPropagation();},
                "mouseup": function(e){e.stopPropagation();}
            });

            form.formAreaNode.setStyles({
                "transform-origin": "0px 0px",
                "transform": "scale(0.58)"
            })


        }.bind(this));

    },
    hidePreview: function(){
        this.previewMaskNode.destroy();
        this.previewNode.empty();
        this.previewNode.destroy();
        this.previewMaskNode = null;
        this.previewNode = null;
    }
});

MWF.xApplication.Setting.UILoginDocument.Style.Editor = new Class({
    initialize: function(item){
        this.item = item;
        this.document = this.item.list.document;
        this.explorer = this.document.explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.contentAreaNode = this.document.contentAreaNode;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.load();
    },
    load: function(){
        this.editAreaNode = new Element("div", {"styles": this.css.explorerContentStyleEditNode}).inject(this.contentAreaNode);
        this.editAreaNode.position({
            "relativeTo": this.item.itemArea,
            "position": 'upperLeft',
            "edge": 'upperLeft'
        });
        this.document.node.setStyle("display", "none");

        var myFx = new Fx.Morph(this.editAreaNode, {
            "duration": '100',
            "transition": Fx.Transitions.Sine.easeOut
        });

        var p = this.contentAreaNode.getPosition(this.contentAreaNode.getOffsetParent());
        var size = this.contentAreaNode.getSize();
        var toCss = {
            "left": ""+p.x+"px",
            "top": ""+p.y+"px",
            "width": ""+size.x+"px",
            "height": ""+size.y+"px",
            "background-color": "#ffffff"
        };
        myFx.start(toCss).chain(function(){
            this.editAreaNode.setStyles({
                "width": "100%",
                "height": "100%",
                "position": "static"
            });
            new Element("div", {"styles": {
                "height": "100px",
                "line-height": "100px",
                "text-align": "center",
                "font-size": "18px",
                "color": "#999999"
            }, "text": this.lp.loading}).inject(this.editAreaNode);
            window.setTimeout(this.createEditorContent.bind(this), 100);
        }.bind(this));


    },
    createEditorContent: function(){
        this.area = new Element("div", {"styles": this.css.explorerContentStyleEditorAreaNode}).inject(this.editAreaNode);
        leftArea = new Element("div", {"styles": this.css.explorerContentStyleEditorLeftAreaNode}).inject(this.area);
        rightArea = new Element("div", {"styles": this.css.explorerContentStyleEditorRightAreaNode}).inject(this.area);

        this.editorArea = new Element("div", {"styles": this.css.explorerContentStyleEditEditorNode}).inject(leftArea);
        this.previewArea = new Element("div", {"styles": this.css.explorerContentStyleEditPreviewNode}).inject(rightArea);


        this.styleCss = this.item.getCss();
        this.createCssEditor();
        this.showPreview();

        this.area.getPrevious().destroy();

        this.returnAction = new Element("div", {"styles": this.css.explorerContentStyleEditorReturnNode, "text": this.lp.returnBack}).inject(this.editAreaNode);
        this.returnAction.addEvent("click", function(e){
            this.destroy();
        }.bind(this));
    },
    destroy: function(){
        this.editAreaNode.destroy();
        this.document.node.setStyle("display", "block");
        MWF.release(this);
    },
    createCssEditor: function(){
        var _self = this;
        MWF.require("MWF.widget.Maplist", function(){
            Object.each(this.styleCss, function(v, k){
                var mapListNode = new Element("div", {"styles": this.css.explorerContentStyleEditMapNode}).inject(this.editorArea);
                var mList = new MWF.widget.Maplist.Style(mapListNode, {"title": k, "style": "styleEditor",
                    "onChange": function(){
                        _self.styleCss[k] = this.toJson();
                        _self.showPreview();

                        var o = {
                            "name": _self.item.data.name,
                            "title": _self.item.data.title,
                            "data": _self.styleCss
                        };
                        MWF.UD.putPublicData(_self.item.data.name, o);
                    }
                });
                mList.app = this.app;
                mList.load(v);
            }.bind(this));
        }.bind(this));
    },
    showPreview: function(){
        this.previewArea.empty();
        MWF.require("MWF.xDesktop.Authentication", function(){
            this.previewNode = new Element("div", {"styles": {"position": "relative", "height": "216px"}}).inject(this.previewArea);
            //var action = new MWF.xApplication.Authentication.Actions.RestActions();
            var loginForm = new MWF.xDesktop.Authentication.LoginForm(this, {}, {
                "draggable": false,
                "closeAction": false,
                "hasMask" : false,
                "relativeToApp" : false,
                "isLimitSize": false,
                "hasScroll": false,
                "ifFade": false,
                "top": "0",
                "left": "0"
            }, {
                "css": this.styleCss,
                "container" : this.previewNode,
                "lp": MWF.LP.authentication
                //"actions": action
            });
            loginForm.create();
            loginForm.formAreaNode.setStyles({
                "transform-origin": "0px 0px",
                "transform": "scale(0.4)"
            });

            var previewSignUpNode = new Element("div", {"styles": {"position": "relative", "height": "268px"}}).inject(this.previewArea);
            var signUpForm = new MWF.xDesktop.Authentication.SignUpForm(this, {}, {
                "draggable": false,
                "closeAction": false,
                "hasMask" : false,
                "relativeToApp" : false,
                "isLimitSize": false,
                "hasScroll": false,
                "ifFade": false,
                "top": "0",
                "left": "0"
            }, {
                "css": this.styleCss,
                "container" : previewSignUpNode,
                "lp": MWF.LP.authentication
                //"actions": action
            });
            signUpForm.create();

            signUpForm.formAreaNode.setStyles({
                "transform-origin": "0px 0px",
                "transform": "scale(0.4)"
            });

            var previewResetPasswordNode = new Element("div", {"styles": {"position": "relative", "height": "248px"}}).inject(this.previewArea);
            var resetPasswordForm = new MWF.xDesktop.Authentication.ResetPasswordForm(this, {}, {
                "draggable": false,
                "closeAction": false,
                "hasMask" : false,
                "relativeToApp" : false,
                "isLimitSize": false,
                "hasScroll": false,
                "ifFade": false,
                "top": "0",
                "left": "0"
            }, {
                "css": this.styleCss,
                "container" : previewResetPasswordNode,
                "lp": MWF.LP.authentication
                //"actions": action
            });
            resetPasswordForm.create();

            resetPasswordForm.formAreaNode.setStyles({
                "transform-origin": "0px 0px",
                "transform": "scale(0.4)"
            });

        }.bind(this));

    }
});