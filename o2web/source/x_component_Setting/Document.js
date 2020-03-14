MWF.xApplication.Setting.Document = new Class({
    Implements: [Options, Events],
    initialize: function(explorer, contentAreaNode, options){
        this.setOptions(options);
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.contentAreaNode = contentAreaNode;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);

        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.base_nameSetting);

        this.baseTitleInput = new MWF.xApplication.Setting.Document.Input(this.explorer, this.node, {
            "lp": {"title": this.lp.base_title, "infor": this.lp.base_title_infor, "action": this.lp.base_title_action},
            "data": {"key": "collectData", "valueKey": "title", "notEmpty": true, "infor": this.lp.base_title_empty },
            "value": this.explorer.collectData.title
        });
        this.baseFooterInput = new MWF.xApplication.Setting.Document.Input(this.explorer, this.node, {
            "lp": {"title": this.lp.base_footer, "infor": this.lp.base_footer_infor, "action": this.lp.base_footer_action},
            "data": {"key": "collectData", "valueKey": "footer", "notEmpty": false},
            "value": this.explorer.collectData.footer
        });
    },
    "destroy": function(){
        this.node.destroy();
        this.contentAreaNode.empty();
        MWF.release(this);
    }
});

MWF.xApplication.Setting.Document.Input = new Class({
    Implements: [Options, Events],
    initialize: function(explorer, contentAreaNode, data, options){
        this.setOptions(options);
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.contentAreaNode = contentAreaNode;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.data = data;
        this.load();
    },
    load: function(){
        this.loadInput(this.data.lp.title, this.data.lp.infor, this.data.value, true, this.data.lp.action, function(){
            this.inputValueArea.empty();
            if (!this.input) this.input = new Element("input", {"disabled": true, "styles": this.css.explorerContentInputNode}).inject(this.inputValueArea);
            this.input.set("value", this.data.value);
            this.input.set({"disabled": false, "styles": this.css.explorerContentInputNode_edit});

            this.button.setStyle("display", "none");
            this.input.focus();

            if (!this.okButton) this.okButton = this.createButton(this.lp.ok, function(e){
                if (this.data.lp.confirm){
                    var _self = this;
                    this.app.confirm("warn", e, "", {"html": this.data.lp.confirm}, 400, 200, function(){
                        if (_self.submitData()) _self.editCancel();
                        this.close();
                    }, function(){this.close();})
                }else{
                    if (this.submitData()) this.editCancel();
                }

            }.bind(this)).inject(this.button, "after");

            if (!this.cancelButton) this.cancelButton = this.createButton(this.lp.cancel, function(){
                this.fireEvent("cancel");
                this.editCancel();
            }.bind(this)).inject(this.okButton, "after");

            this.okButton.setStyle("display", "block");
            this.cancelButton.setStyle("display", "block");
        }.bind(this));
    },
    editCancel: function(){
        this.input.set({"disabled": true, "styles": this.css.explorerContentInputNode, "value": this.data.value});
        this.inputValueArea.empty();
        this.input = null;
        this.inputValueArea.set("text", this.data.show || this.data.value);
        this.okButton.setStyle("display", "none");
        this.cancelButton.setStyle("display", "none");
        this.button.setStyle("display", "block");
    },
    submitData: function(){
        var value = this.input.get("value");
        if (this.data.data.notEmpty){
            if (!value){
                this.app.notice(this.data.data.infor, "error");
                return false;
            }
        }
        this.explorer[this.data.data.key][this.data.data.valueKey] = value;
        this.data.value = value;

        var method = "";
        if (this.data.data.key=="collectData") method = "setCollect";
        if (this.data.data.key=="personData") method = "setPerson";
        if (this.data.data.key=="portalData") method = "setPortal";
        if (this.data.data.key=="tokenData") method = "setToken";
        if (this.data.data.key=="proxyData") method = "setProxy";
        if (this.data.data.key=="nativeData"){
            method = "setAppStyle";
            if (!this.explorer.nativeData.indexPortal){
                this.explorer.nativeData.indexType = "default";
            }else{
                this.explorer.nativeData.indexType = "portal";
            }
        }

        this.actions[method](this.explorer[this.data.data.key], function(){
            this.fireEvent("editSuccess");
            this.app.notice(this.lp.setSaved, "success");
        }.bind(this));
        return true;
    },
    createButton: function(text, action){
        var button = new Element("div", {"type": "button", "styles": this.css.explorerContentButtonNode, "text": text});
        button.addEvents({
            "click": function(){
                if (action) action(button);
            }.bind(this),
            "mouseover": function(){this.setStyle("border", "1px solid #999999");},
            "mouseout": function(){this.setStyle("border", "1px solid #eeeeee");}
        });
        return button
    },
    loadInput: function(title, infor, value, isEdit, actionTitle, action){
        var titleNode = new Element("div", {"styles": this.css.explorerContentItemTitleNode, "text": title}).inject(this.contentAreaNode);
        // var dataNode = new Element("div", {"styles": this.css.explorerContentItemDataNode, "text": title}).inject(this.contentAreaNode);
        // dataNode.set("text", value);

        var titleInforArea = new Element("div", {"styles": this.css.explorerContentInputInforNode, "text": infor}).inject(this.contentAreaNode);
        var inputArea = new Element("div", {"styles": this.css.explorerContentInputAreaNode}).inject(this.contentAreaNode);
        this.inputValueArea = new Element("div", {"styles": this.css.explorerContentInputValueAreaNode}).inject(inputArea);
        this.inputValueArea.set("text", this.data.show || value);

        // this.input = new Element("input", {"disabled": true, "styles": this.css.explorerContentInputNode}).inject(inputValueArea);
        // this.input.set("value", value);
        if (isEdit){
            this.button = this.createButton(actionTitle, action).inject(inputArea);
        }
        // return this.input;
    }

});


MWF.xApplication.Setting.Document.Check = new Class({
    Extends: MWF.xApplication.Setting.Document.Input,

    loadInput: function(title, infor, value, isEdit, actionTitle, action){
        var titleNode = new Element("div", {"styles": this.css.explorerContentItemTitleNode, "text": title}).inject(this.contentAreaNode);

        if (infor) var titleInforArea = new Element("div", {"styles": this.css.explorerContentInputInforNode, "text": infor}).inject(this.contentAreaNode);
        var inputArea = new Element("div", {"styles": this.css.explorerContentInputAreaNode}).inject(this.contentAreaNode);

        this.inputValueArea = new Element("div").inject(inputArea);
        this.inputPoint = new Element("div").inject(this.inputValueArea);
        this.inputInfor = new Element("div", {"styles": this.css.explorerContentCheckInforValueAreaNode}).inject(inputArea);

        if (value) {
            this.inputPoint.setStyles(this.css.explorerContentCheckPointValueAreaNode_on);
            this.inputValueArea.setStyles(this.css.explorerContentCheckValueAreaNode_on);
            this.inputInfor.set("text", this.lp.on);
        }else{
            this.inputPoint.setStyles(this.css.explorerContentCheckPointValueAreaNode_off);
            this.inputValueArea.setStyles(this.css.explorerContentCheckValueAreaNode_off);
            this.inputInfor.set("text", this.lp.off);
        }
        if (isEdit){
            this.inputValueArea.addEvent("click", function(){
                if (this.data.value) {
                    this.data.value = false;
                    if (this.data.data.valueKey.indexOf(".")!=-1){
                        var o = this.explorer[this.data.data.key];
                        var keys = this.data.data.valueKey.split(".");
                        keys.each(function(k, i){
                            if (i==(keys.length-1)){
                                o[k] = false;
                            }else{
                                o = o[k];
                            }
                        }.bind(this));
                    }else{
                        this.explorer[this.data.data.key][this.data.data.valueKey] = false;
                    }

                    this.inputPoint.setStyles(this.css.explorerContentCheckPointValueAreaNode_off);
                    this.inputValueArea.setStyles(this.css.explorerContentCheckValueAreaNode_off);
                    this.inputInfor.set("text", this.lp.off);
                }else{
                    this.data.value = true;
                    if (this.data.data.valueKey.indexOf(".")!=-1){
                        var o = this.explorer[this.data.data.key];
                        var keys = this.data.data.valueKey.split(".");
                        keys.each(function(k, i){
                            if (i==(keys.length-1)){
                                o[k] = true;
                            }else{
                                o = o[k];
                            }
                        }.bind(this));
                    }else{
                        this.explorer[this.data.data.key][this.data.data.valueKey] = true;
                    }
                    this.inputPoint.setStyles(this.css.explorerContentCheckPointValueAreaNode_on);
                    this.inputValueArea.setStyles(this.css.explorerContentCheckValueAreaNode_on);
                    this.inputInfor.set("text", this.lp.on);
                }
                var method = "";
                if (this.data.data.key=="collectData") method = "setCollect";
                if (this.data.data.key=="personData") method = "setPerson";
                if (this.data.data.key=="portalData") method = "setPortal";
                if (this.data.data.key=="tokenData") method = "setToken";
                if (this.data.data.key=="proxyData") method = "setProxy";
                if (this.data.data.key=="mobileStyleData") method = "setProxy";
                if (this.data.data.key=="nativeData"){
                    method = "setAppStyle";
                    if (!this.explorer.nativeData.indexPortal){
                        this.explorer.nativeData.indexType = "default";
                    }else{
                        this.explorer.nativeData.indexType = "portal";
                    }
                }

                this.actions[method](this.explorer[this.data.data.key], function(){
                    this.fireEvent("editSuccess");
                    //this.app.notice(this.lp.setSaved, "success");
                }.bind(this));
            }.bind(this));
        }
    }
});

MWF.xApplication.Setting.Document.Select = new Class({
    Extends: MWF.xApplication.Setting.Document.Input,

    loadInput: function(title, infor, value, isEdit, actionTitle, action){
        var titleNode = new Element("div", {"styles": this.css.explorerContentItemTitleNode, "text": title}).inject(this.contentAreaNode);

        if (infor) var titleInforArea = new Element("div", {"styles": this.css.explorerContentInputInforNode, "text": infor}).inject(this.contentAreaNode);
        var inputArea = new Element("div", {"styles": this.css.explorerContentInputAreaNode}).inject(this.contentAreaNode);

        this.inputValueArea = new Element("div", {"styles": this.css.explorerContentInputValueAreaNode}).inject(inputArea);
        this.input = new Element("select", {"styles": this.css.explorerContentSelectValueAreaNode}).inject(this.inputValueArea);

        var options = [];
        if (typeOf(this.data.options)==="function"){
            options = this.data.options();
        }else{
            options = this.data.options
        }

        options.each(function(option){
            new Element("option", {"value": option.value, "text": option.text, "selected": (value==option.value)}).inject(this.input);
        }.bind(this));
        if (isEdit){
            this.input.addEvent("change", function(){
                var v = this.input.options[this.input.selectedIndex].get("value");
                this.data.value = v;

                if (this.data.data.valueKey.indexOf(".")!=-1){
                    var o = this.explorer[this.data.data.key];
                    var keys = this.data.data.valueKey.split(".");
                    keys.each(function(k, i){
                        if (i==(keys.length-1)){
                            o[k] = v;
                        }else{
                            o = o[k];
                        }
                    }.bind(this));
                }else{
                    this.explorer[this.data.data.key][this.data.data.valueKey] = v;
                }

                //this.explorer[this.data.data.key][this.data.data.valueKey] = v;

                var method = "";
                if (this.data.data.key=="collectData") method = "setCollect";
                if (this.data.data.key=="personData") method = "setPerson";
                if (this.data.data.key=="portalData") method = "setPortal";
                if (this.data.data.key=="tokenData") method = "setToken";
                if (this.data.data.key=="proxyData") method = "setProxy";
                if (this.data.data.key=="nativeData"){
                    method = "setAppStyle";
                    if (!this.explorer.nativeData.indexPortal){
                        this.explorer.nativeData.indexType = "default";
                    }else{
                        this.explorer.nativeData.indexType = "portal";
                    }
                }

                this.actions[method](this.explorer[this.data.data.key], function(){
                    this.fireEvent("editSuccess");
                    //this.app.notice(this.lp.setSaved, "success");
                }.bind(this));
            }.bind(this));
        }
    }
});
MWF.xApplication.Setting.Document.Button = new Class({
    Extends: MWF.xApplication.Setting.Document.Input,

    loadInput: function(title, infor, value, isEdit, actionTitle, action){
        var titleNode = new Element("div", {"styles": this.css.explorerContentItemTitleNode, "text": title}).inject(this.contentAreaNode);
        if (infor) var titleInforArea = new Element("div", {"styles": this.css.explorerContentInputInforNode, "text": infor}).inject(this.contentAreaNode);
        var inputArea = new Element("div", {"styles": this.css.explorerContentInputAreaNode}).inject(this.contentAreaNode);

        this.itemArea = new Element("div", {"styles": this.css.explorerContentListActionAreaNode}).inject(inputArea);
        this.itemIconArea = new Element("div", {"styles": this.css.explorerContentListItemIconAreaNode}).inject(this.itemArea);
        this.itemIcon = new Element("div", {"styles": this.css.explorerContentListItemIconNode}).inject(this.itemIconArea);
        this.itemTextArea = new Element("div", {"styles": this.css.explorerContentListActionTextAreaNode}).inject(this.itemArea);
        this.itemIcon.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/"+this.data.icon+") no-repeat center center");

        var t = this.data.itemTitle;
        var regexp = /\{.+?\}/g;
        var r = t.match(regexp);
        if(r){
            if (r.length){
                for (var i=0; i<r.length; i++){
                    var text = r[i].substr(0,r[i].lastIndexOf("}"));
                    text = text.substr(text.indexOf("{")+1,text.length);
                    var value = this.data[text];
                    var reg = new RegExp("\\{"+text+"\\}", "g");
                    t = t.replace(reg,value);
                }
            }
        }
        this.itemTextArea.set("text", t);
        var _self = this;
        this.itemArea.addEvents({
            "mouseover": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListActionAreaNode_over);},
            "mouseout": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListActionAreaNode);},
            "mousedown": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListActionAreaNode_down);},
            "mouseup": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListActionAreaNode_over);},
            "click": function(e){_self.data.action(e);}
        });
    }
});

MWF.xApplication.Setting.Document.Image = new Class({
    Extends: MWF.xApplication.Setting.Document.Input,

    loadInput: function(title, infor, value, isEdit, actionTitle, action){
        var titleNode = new Element("div", {"styles": this.css.explorerContentItemTitleNode, "text": title}).inject(this.contentAreaNode);
        if (infor) var titleInforArea = new Element("div", {"styles": this.css.explorerContentInputInforNode, "text": infor}).inject(this.contentAreaNode);
        var inputArea = new Element("div", {"styles": this.css.explorerContentImgInputAreaNode}).inject(this.contentAreaNode);
        this.itemArea = new Element("div", {"styles": this.css.explorerContentImgActionAreaNode}).inject(inputArea);
        this.itemIconArea = new Element("div", {"styles": this.css.explorerContentImgItemIconAreaNode}).inject(this.itemArea);
        this.itemIcon = new Element("div", {"styles": this.css.explorerContentImgItemIconNode}).inject(this.itemIconArea);

        if (this.data.iconData){
            this.img = new Element("img", {"src": "data:image/png;base64,"+this.data.iconData}).inject(this.itemIcon);
        }else{
            this.itemIcon.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/"+this.list.data.icon+") no-repeat center center");
        }
        this.itemTextArea = new Element("div", {"styles": this.css.explorerContentImgActionTextAreaNode}).inject(this.itemArea);
        this.itemTextDefaultArea = new Element("div", {"styles": this.css.explorerContentImgActionTextAreaNode}).inject(this.itemArea);

        if (this.img){
            var size = this.img.getSize();
            var x = Math.round(size.x);
            var y = Math.round(size.y);
            titleNode.set("text", title+this.lp.imgSize+x+" * "+y);
        }

        var t = this.data.itemTitle;
        var regexp = /\{.+?\}/g;
        var r = t.match(regexp);
        if(r){
            if (r.length){
                for (var i=0; i<r.length; i++){
                    var text = r[i].substr(0,r[i].lastIndexOf("}"));
                    text = text.substr(text.indexOf("{")+1,text.length);
                    var value = this.data[text];
                    var reg = new RegExp("\\{"+text+"\\}", "g");
                    t = t.replace(reg,value);
                }
            }
        }
        this.itemTextArea.set("text", t);
        this.itemTextDefaultArea.set("text", this.lp.defaultImg);

        var _self = this;
        // this.itemArea.addEvents({
        //     "mouseover": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentImgActionAreaNode_over);},
        //     "mouseout": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentImgActionAreaNode);}
        // });
        this.itemTextArea.addEvents({
            "mouseover": function(){this.itemTextArea.setStyles(this.css.explorerContentImgActionTextAreaNode_over);}.bind(this),
            "mouseout": function(){this.itemTextArea.setStyles(this.css.explorerContentImgActionTextAreaNode);}.bind(this),
            "click": function(e){_self.changeImage(e);}
        });
        this.itemTextDefaultArea.addEvents({
            "mouseover": function(){this.itemTextDefaultArea.setStyles(this.css.explorerContentImgActionTextAreaNode_over);}.bind(this),
            "mouseout": function(){this.itemTextDefaultArea.setStyles(this.css.explorerContentImgActionTextAreaNode);}.bind(this),
            "click": function(e){_self.defaultImage(e);}
        });

    },
    changeImage: function(e){
        var method = "";
        switch (this.data.value.name){
            case "launch_logo":
                method = "imageLaunchLogo";
                break;
            case "login_avatar":
                method = "imageLoginAvatar";
                break;
            case "index_bottom_menu_logo_blur":
                method = "imageMenuLogoBlur";
                break;
            case "index_bottom_menu_logo_focus":
                method = "imageMenuLogoFocus";
                break;
            case "people_avatar_default":
                method = "imagePeopleAvatarDefault";
                break;
            case "process_default":
                method = "imageProcessDefault";
                break;
            case "setup_about_logo":
                method = "imageSetupAboutLogo";
                break;
        }

        MWF.require("MWF.widget.Upload", function(){
            var upload = new MWF.widget.Upload(this.app.content, {
                "data": null,
                "action": this.actions.action,
                "method": method,
                "onCompleted": function(json){

                    this.actions.mobile_currentStyle(function(json){
                        var imgs = json.data.images.filter(function(img){
                            return img.name==this.data.value.name;
                        }.bind(this));
                        var imgData = imgs[0].value;
                        this.img.set("src", "data:image/png;base64,"+imgData);
                    }.bind(this));
                }.bind(this)
            });
            upload.load();
        }.bind(this));
    },
    defaultImage: function(e){
        var _self = this;
        var imgName = this.lp.mobile_style_imgs[this.data.value.name];
        var imgInfor = this.lp.mobile_style_imgs_defaultInfor.replace("{name}", imgName);
        this.app.confirm("infor", e, this.lp.mobile_style_imgs_defaultTitle, imgInfor, 360, 150, function(){
            _self.setDefaultImage();
            this.close();
        }, function(){
            this.close();
        });
    },
    setDefaultImage: function(){
        var method = "";
        switch (this.data.value.name){
            case "launch_logo":
                method = "imageLaunchLogoErase";
                break;
            case "login_avatar":
                method = "imageLoginAvatarErase";
                break;
            case "index_bottom_menu_logo_blur":
                method = "imageMenuLogoBlurErase";
                break;
            case "index_bottom_menu_logo_focus":
                method = "imageMenuLogoFocusErase";
                break;
            case "people_avatar_default":
                method = "imagePeopleAvatarDefaultErase";
                break;
            case "process_default":
                method = "imageProcessDefaultErase";
                break;
            case "setup_about_logo":
                method = "imageSetupAboutLogoErase";
                break;
        }
        this.actions[method](function(){
            this.actions.mobile_currentStyle(function(json){
                var imgs = json.data.images.filter(function(img){
                    return img.name==this.data.value.name;
                }.bind(this));
                var imgData = imgs[0].data;
                this.img.set("src", "data:image/png;base64,"+this.data.iconData);
            }.bind(this));
        }.bind(this));
    }

});


MWF.xApplication.Setting.Document.List = new Class({
    Extends: MWF.xApplication.Setting.Document.Input,

    loadInput: function(title, infor, value, isEdit, actionTitle, action){
        this.items = [];
        var titleNode = new Element("div", {"styles": this.css.explorerContentItemTitleNode, "text": title}).inject(this.contentAreaNode);
        if (infor) var titleInforArea = new Element("div", {"styles": this.css.explorerContentInputInforNode, "text": infor}).inject(this.contentAreaNode);
        var inputArea = new Element("div", {"styles": this.css.explorerContentListAreaNode}).inject(this.contentAreaNode);

        this.type = (typeOf(value)=="object") ? "object" : "list";

        if (actionTitle){
            this.actionArea = new Element("div", {"styles": this.css.explorerContentListActionAreaNode}).inject(inputArea);
            var actionIconArea = new Element("div", {"styles": this.css.explorerContentListActionIconAreaNode}).inject(this.actionArea);
            var actionIcon = new Element("div", {"styles": this.css.explorerContentListActionIconNode}).inject(actionIconArea);
            if (this.type=="object") actionIcon.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/edit.png) no-repeat center center");

            var actionTextArea = new Element("div", {"styles": this.css.explorerContentListActionTextAreaNode, "text": actionTitle}).inject(this.actionArea);

            this.actionArea.addEvents({
                "mouseover": function(){this.actionArea.setStyles(this.css.explorerContentListActionAreaNode_over);}.bind(this),
                "mouseout": function(){this.actionArea.setStyles(this.css.explorerContentListActionAreaNode);}.bind(this),
                "mousedown": function(){this.actionArea.setStyles(this.css.explorerContentListActionAreaNode_down);}.bind(this),
                "mouseup": function(){this.actionArea.setStyles(this.css.explorerContentListActionAreaNode_over);}.bind(this),
                "click": function(){
                    if (this.type=="list") this.addItem();
                    if (this.type=="object") this.items[0].edit();
                }.bind(this)
            });
        }

        this.itemArea = new Element("div", {"styles": {"overflow": "hidden", "clear": "both"}}).inject(inputArea);

        if (this.type=="list"){
            if (value.length && value.length){
                value.each(function(v){
                    this.createItem(v);
                }.bind(this));
            }
        }
        if (this.type=="object"){
            this.createItem(value);
        }
    },
    createItem: function(v){
        this.items.push(new MWF.xApplication.Setting.Document.List.Item(this, v));
    },
    addItem: function(){
        new MWF.xApplication.Setting.Document.List.ItemEditor(this, Object.clone(this.data.addItem), function(data){
            this.data.value.push(data);
            this.save(data);
        }.bind(this));
    },
    save: function(data){
        if (this.data.data.key=="publicData"){
            o2.UD.putPublicData("faceKeys", this.explorer[this.data.data.key], function(){
                this.fireEvent("editSuccess");
                this.reloadItems();
            }.bind(this));
        }else{
            var method = "";
            if (this.data.data.key=="collectData") method = "setCollect";
            if (this.data.data.key=="personData") method = "setPerson";
            if (this.data.data.key=="portalData") method = "setPortal";
            if (this.data.data.key=="tokenData") method = "setToken";
            if (this.data.data.key=="proxyData") method = "setProxy";
            if (this.data.data.key=="nativeData"){
                method = "setAppStyle";
                if (!this.explorer.nativeData.indexPortal){
                    this.explorer.nativeData.indexType = "default";
                }else{
                    this.explorer.nativeData.indexType = "portal";
                }
            }

            this.actions[method](this.explorer[this.data.data.key], function(){
                this.fireEvent("editSuccess");
                this.reloadItems();
            }.bind(this));
        }
    },
    reloadItems: function(){
        this.itemArea.empty();
        this.items = [];

        if (this.type=="list"){
            if (this.data.value.length && this.data.value.length){
                this.data.value.each(function(v){
                    this.createItem(v);
                }.bind(this));
            }
        }
        if (this.type=="object"){
            this.createItem(this.data.value);
        }
    }

});
MWF.xApplication.Setting.Document.List.ItemEditor = new Class({
    initialize: function(list, data, saveAction, title){
        this.list = list;
        this.title= title;
        this.explorer = this.list.explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.data = data;
        this.isSelected = false;
        this.saveAction = saveAction;
        this.load();
    },
    load: function(){
        var position = (this.list.actionArea) ? this.list.actionArea.getPosition(this.app.content) : this.list.itemArea.getPosition(this.app.content);
        var size = this.app.content.getSize();
        var width = size.x*0.9;
        if (width>600) width = 600;

        var h=0;
        Object.each(this.data, function(v, k){
            if ((this.list.data.readonly && this.list.data.readonly.indexOf(k)==-1) || !this.list.data.readonly) {
                var t = typeOf(v);
                switch (t) {
                    case "string":
                        h += 80;
                        break;
                    case "object":
                        h += 180;
                        break;
                    default:
                        h += 80;
                }
            }
        }.bind(this));
        //var i = Object.keys(this.data).length;
        var height = h+80;
        var size = this.app.content.getSize();
        if (height>size.y){
            height = size.y;
        }


        //var height = size.y*0.9;
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function(){
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.title || this.list.data.lp.action || this.list.data.lp.editAction,
                "style": "setting",
                "top": y,
                "left": x,
                "fromTop":position.y,
                "fromLeft": position.x,
                "width": width,
                "height": height,
                "html": "",
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": this.lp.ok,
                        "action": function(){
                            _self.save(this);
                        }
                    },
                    {
                        "text": this.lp.cancel,
                        "action": function(){this.close();}
                    }
                ]
            });
            dlg.show();

            this.content = new Element("div", {"styles": this.css.explorerContentListEditAreaNode}).inject(dlg.content);
            this.inputs = {};
            Object.each(this.data, function(v, k){
                if ((this.list.data.readonly && this.list.data.readonly.indexOf(k)==-1) || !this.list.data.readonly){
                    new Element("div", {"styles": this.css.explorerContentListEditTitleNode, "text": this.lp.list[k] || k}).inject(this.content);
                    if (typeOf(v)=="string"){
                        this.inputs[k] = new Element("input", {"styles": this.css.explorerContentListEditInputNode, "value": v}).inject(this.content);
                    }
                    if (typeOf(v)=="number"){
                        this.inputs[k] = new Element("input", {"styles": this.css.explorerContentListEditInputNode, "value": v}).inject(this.content);
                    }
                    if (typeOf(v)=="object"){
                        var mapListNode = new Element("div", {"styles": this.css.explorerContentListEditMapNode}).inject(this.content);
                        MWF.require("MWF.widget.Maplist", function(){
                            var mList = new MWF.widget.Maplist(mapListNode, {"title": this.lp.list[k], "style": "setting"});
                            mList.load(v);
                            this.inputs[k] = mList;
                        }.bind(this));
                    }
                    if (typeOf(v)=="boolean"){
                        this.inputs[k] = new Element("select", {
                            "html": "<option value='true' "+((v) ? "selected": "")+">yes</option><option value='false' "+((v) ? "": "selected")+">no</option>"
                        }).inject(this.content);
                        this.inputs[k].getValue = function(){
                            return this.options[this.selectedIndex].value;
                        }
                    }
                }
            }.bind(this));
            //setupModule = new MWF.xApplication.AppMarket.Module.Setup(this, dlg);
        }.bind(this));
    },
    save: function(dlg){
        debugger;
        var keys = Object.keys(this.inputs);
        var values = {};
        var flag = true;

        Object.each(this.inputs, function(input, k){
            if ((this.list.data.readonly && this.list.data.readonly.indexOf(k)==-1) || !this.list.data.readonly){
                var value = (typeOf(input)=="element" && input.tagName.toString().toLowerCase()!="select") ? input.get("value") : input.getValue();
                if (this.list.data.data.notEmpty && !value && value!==false){
                    flag = false;
                    this.app.notice(this.lp.pleaseInput+(this.lp.list[k] || k), "error");
                    return false;
                }else{
                    values[k] = value;
                }
            }
        }.bind(this));

        if (flag){
            Object.each(this.data, function(v, k){
                if (typeOf(this.data[k])=="number"){
                    this.data[k] = values[k].toFloat();
                }if (typeOf(this.data[k])=="boolean"){
                    this.data[k] = (values[k]==="true");
                }else{
                    if (this.list.data.data.notEmpty){
                        this.data[k] = values[k] || v;
                    }else{
                        this.data[k] = values[k] || "";
                    }
                }
            }.bind(this));
            if (this.saveAction) this.saveAction(this.data);

            dlg.close();
        }
    }
});
MWF.xApplication.Setting.Document.List.Item = new Class({
    initialize: function(list, data){
        this.list = list;
        this.explorer = this.list.explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.content = this.list.itemArea;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.data = data;
        this.isSelected = false;
        this.load();
    },
    load: function(){
        this.itemArea = new Element("div", {"styles": this.css.explorerContentListActionAreaNode}).inject(this.content);
        this.itemIconArea = new Element("div", {"styles": this.css.explorerContentListItemIconAreaNode}).inject(this.itemArea);
        this.itemIcon = new Element("div", {"styles": this.css.explorerContentListItemIconNode}).inject(this.itemIconArea);
        this.itemTextArea = new Element("div", {"styles": this.css.explorerContentListActionTextAreaNode}).inject(this.itemArea);

        if (this.list.data.iconData){
            this.itemArea.setStyles(this.css.explorerContentListActionAreaNode_img);
            this.itemIconArea.setStyles(this.css.explorerContentListItemIconAreaNode_img);
            this.img = new Element("img", {"src": "data:image/png;base64,"+this.list.data.iconData}).inject(this.itemIcon);
        }else{
            this.itemIcon.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/"+this.list.data.icon+") no-repeat center center");
        }


        var t = this.list.data.itemTitle;
        var regexp = /\{.+?\}/g;
        var r = t.match(regexp);
        if(r){
            if (r.length){
                for (var i=0; i<r.length; i++){
                    var text = r[i].substr(0,r[i].lastIndexOf("}"));
                    text = text.substr(text.indexOf("{")+1,text.length);
                    var value = this.data[text];
                    var reg = new RegExp("\\{"+text+"\\}", "g");
                    t = t.replace(reg,value);
                }
            }
        }
        this.itemTextArea.set("text", t);
        var _self = this;
        this.itemArea.addEvents({
            "mouseover": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListActionAreaNode_over);},
            "mouseout": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListActionAreaNode);},
            "mousedown": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListActionAreaNode_down);},
            "mouseup": function(){if (!_self.isSelected) this.setStyles(_self.css.explorerContentListActionAreaNode_over);},
            "click": function(){if (!_self.isSelected) _self.clickItem(this);}
        });
    },
    clickItem: function(){

        this.list.items.each(function(item){
            if (item.isSelected) item.unSelected();
        }.bind(this));

        if ((this.list.type != "object") && this.list.data.lp.action){
            this.itemArea.setStyles(this.css.explorerContentListActionAreaNode_selected);
            this.isSelected = true;
            this.createAction();
        }else{
            this.edit();
        }

    },
    unSelected: function(){
        this.itemArea.setStyles(this.css.explorerContentListActionAreaNode);
        if (this.actionArea) this.actionArea.setStyle("display", "none");
        this.isSelected = false;
    },

    createAction: function(){
        if (!this.actionArea){
            this.actionArea = new Element("div", {"styles": this.css.explorerContentListItemActionAreaNode}).inject(this.itemArea);
            this.createActionButton(this.lp.delete, function(button, e){
                var _self = this;
                this.app.confirm("infor", e, this.lp.deleteItem, this.lp.deleteItemInfor, 400, 150, function(){
                    _self.deleteItem();
                    this.close();
                }, function(){this.close()});
            }.bind(this));

            this.createActionButton(this.lp.edit, function(button, e){
                this.edit();
            }.bind(this));
        }else{
            this.actionArea.setStyle("display", "block");
        }
    },
    edit: function(){
        new MWF.xApplication.Setting.Document.List.ItemEditor(this.list, this.data, function(data){
            this.list.save(data);
        }.bind(this), this.list.data.lp.editAction);
    },
    createActionButton: function(text, action){
        var button = new Element("div", {"styles": this.css.explorerContentListItemActionNode, "text": text}).inject(this.actionArea);
        button.addEvents({
            "click": function(e){
                if (action) action(button, e);
            }.bind(this),
            "mouseover": function(){this.setStyle("border", "1px solid #999999");},
            "mouseout": function(){this.setStyle("border", "1px solid #eeeeee");}
        });
    },

    deleteItem: function(){
        this.list.data.value.erase(this.data);
        this.list.save();
        this.destroy();
    },
    destroy: function(){
        this.itemArea.destroy();
        MWF.release(this);
    }

});
