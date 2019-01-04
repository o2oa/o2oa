/**
 * Created by hq_19 on 2017/5/31.
 */
MWF.xApplication.Org.BaseInfor = new Class({
    initialize: function(content){
        this.content = content;
        this.item = content.item;
        this.data = this.item.data;
        this.explorer = this.item.explorer;
        this.contentNode = this.content.baseContentNode;
        this.style = this.item.style.person;
        this.attributes = [];
        this.mode = "read";
        this.load();
    },
    load: function(){
        this.baseBgNode = new Element("div", {"styles": this.style.baseBgNode}).inject(this.contentNode);
        this.baseNode = new Element("div", {"styles": this.style.baseNode}).inject(this.baseBgNode);
        this.baseInforNode = new Element("div", {"styles": this.style.baseInforNode}).inject(this.baseNode);
        this.baseInforLeftNode = new Element("div", {"styles": this.style.baseInforLeftNode}).inject(this.baseInforNode);
        this.baseInforRightNode = new Element("div", {"styles": this.style.baseInforRightNode}).inject(this.baseInforNode);
        // this.actionEditAreaNode = new Element("div", {"styles": this.style.baseInforRightActionAreaNode}).inject(this.baseInforNode);
        // this.actionEditContentNode = new Element("div", {"styles": this.style.baseInforRightActionContentNode}).inject(this.actionEditAreaNode);

        this.actionAreaNode = new Element("div", {"styles": this.style.actionAreaNode}).inject(this.baseBgNode);

        this.loadLeftInfor();
        this.loadRightInfor();

        this.loadAction();
    },
    loadAction: function(){
        //this.explorer.app.lp.edit
        if (MWF.AC.isPersonEditor({"list": this.data.controllerList})){
            this.editNode = new Element("div", {"styles": this.style.actionNode, "text": this.explorer.app.lp.edit}).inject(this.actionAreaNode);
            var actionAreas = this.baseInforRightNode.getElements("td");
            var actionArea = actionAreas[actionAreas.length-1];
            this.baseInforEditActionAreaNode = new Element("div", {"styles": this.style.baseInforEditActionAreaNode}).inject(actionArea);

            this.saveNode = new Element("div", {"styles": this.style.actionSaveNode, "text": this.explorer.app.lp.save}).inject(this.baseInforEditActionAreaNode);
            this.cancelNode = new Element("div", {"styles": this.style.actionCancelNode, "text": this.explorer.app.lp.cancel}).inject(this.baseInforEditActionAreaNode);

            this.editNode.setStyle("display", "block");
            this.editNode.addEvent("click", this.edit.bind(this));
            this.saveNode.addEvent("click", this.save.bind(this));
            this.cancelNode.addEvent("click", this.cancel.bind(this));

            this.iconNode.setStyle("cursor", "pointer");
            this.iconNode.addEvent("click", function(){this.changePersonIcon();}.bind(this));
        }
    },
    edit: function(){
        this.nameNode.empty();
        this.nameInputNode = new Element("input", {"styles": this.style.nameInputNode}).inject(this.nameNode);
        this.nameInputNode.set("value", this.data.name);

        this.signatureNode.empty();
        this.signatureTextNode = new Element("textarea", {"styles": this.style.signatureTextNode}).inject(this.signatureNode);
        this.signatureTextNode.set("value", (this.data.signature));

        var tdContents = this.baseInforRightNode.getElements("td.inforContent");
        tdContents[0].empty();
        this.uniqueInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[0]);
        this.uniqueInputNode.set("value", (this.data.unique));

        tdContents[1].empty();
        this.mobileInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[1]);
        this.mobileInputNode.set("value", (this.data.mobile));

        tdContents[2].empty();
        var html = "<input name=\"personGenderRadioNode\" value=\"m\" type=\"radio\" "+((this.data.genderType==="m") ? "checked" : "")+"/>"+this.explorer.app.lp.man;
        html += "<input name=\"personGenderRadioNode\" value=\"f\" type=\"radio\" "+((this.data.genderType==="f") ? "checked" : "")+"/>"+this.explorer.app.lp.female;
        html += "<input name=\"personGenderRadioNode\" value=\"o\" type=\"radio\" "+((this.data.genderType==="d") ? "checked" : "")+"/>"+this.explorer.app.lp.other;
        tdContents[2].set("html", html);

        // this.mobileInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[2]);
        // this.mobileInputNode.set("value", (this.data.mobile));

        tdContents[3].empty();
        this.mailInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[3]);
        this.mailInputNode.set("value", (this.data.mail));

        tdContents[4].empty();
        this.employeeInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[4]);
        this.employeeInputNode.set("value", (this.data.employee));

        tdContents[5].empty();
        this.qqInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[5]);
        this.qqInputNode.set("value", (this.data.qq));

        tdContents[6].empty();
        this.displayInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[6]);
        this.displayInputNode.set("value", (this.data.display));

        tdContents[7].empty();
        this.weiboInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[7]);
        this.weiboInputNode.set("value", (this.data.weibo));

        var _self = this;
        this.baseInforNode.getElements("input").addEvents({
            "focus": function(){if (this.get("type").toLowerCase()==="text"){this.setStyles(_self.style.inputNode_focus);}},
            "blur": function(){if (this.get("type").toLowerCase()==="text"){this.setStyles(_self.style.inputNode_blur);}}
        });
        this.baseInforNode.getElements("textarea").addEvents({
            "focus": function(){this.setStyles(_self.style.inputNode_focus);},
            "blur": function(){this.setStyles(_self.style.inputNode_blur);}
        });

        this.mode = "edit";

        this.editNode.setStyle("display", "none");
        this.saveNode.setStyle("display", "block");
        this.cancelNode.setStyle("display", "block");
    },
    changePersonIcon: function(){
        var options = {};
        var width = "668";
        var height = "510";
        width = width.toInt();
        height = height.toInt();

        var size = this.explorer.app.content.getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;
        if (layout.mobile){
            x = 20;
            y = 0;
        }

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function() {
            MWF.require("MWF.widget.ImageClipper", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.explorer.app.lp.changePersonIcon,
                    "style": "image",
                    "top": y,
                    "left": x - 20,
                    "fromTop": y,
                    "fromLeft": x - 20,
                    "width": width,
                    "height": height,
                    "html": "<div></div>",
                    "maskNode": this.explorer.app.content,
                    "container": this.explorer.app.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function () {
                                _self.uploadPersonIcon();
                                this.close();
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function () {
                                _self.image = null;
                                this.close();
                            }
                        }
                    ]
                });
                dlg.show();

                this.image = new MWF.widget.ImageClipper(dlg.content.getFirst(), {
                    "aspectRatio": 1,
                    "description" : "",
                    "imageUrl" : "",
                    "resetEnable" : false
                });
                this.image.load(this.data.icon);
            }.bind(this));
        }.bind(this))
    },
    uploadPersonIcon: function(){
        if (this.image){
            if( this.image.getResizedImage() ){
                this.explorer.actions.changePersonIcon(this.data.id ,function(){
                    this.explorer.actions.getPerson(function(json){
                        if (json.data){
                            this.data.icon = json.data.icon;
                            if (this.data.icon){
                                this.iconNode.set("src", this._getIcon());
                                this.item.iconNode.getElement("img").set("src", this.item._getIcon());
                            }
                        }
                    }.bind(this), null, this.data.id, false)
                }.bind(this), null, this.image.getFormData(), this.image.resizedImage);
            }
        }
    },
    save: function(){
        var tdContents = this.baseInforRightNode.getElements("td.inforContent");
        var gender = "";
        var radios = tdContents[2].getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                gender = radios[i].value;
                break;
            }
        }

        if (!this.nameInputNode.get("value") || !this.uniqueInputNode.get("value") || !this.employeeInputNode.get("value") || !gender){
            this.explorer.app.notice(this.explorer.app.lp.inputPersonInfor, "error", this.explorer.propertyContentNode);
            return false;
        }
        if (!this.displayInputNode.get("value")) this.data.display = this.nameInputNode.get("value");
        this.baseBgNode.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });

        this.savePerson(function(){
            this.cancel();
            this.baseBgNode.unmask();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.explorer.app.notice("request json error: "+errorText, "error");
            this.baseBgNode.unmask();
        }.bind(this));
    },
    savePerson: function(callback, cancel){
        this.data.name = this.nameInputNode.get("value");
        this.data.employee = this.employeeInputNode.get("value");
        this.data.unique = this.uniqueInputNode.get("value");
        this.data.display = this.displayInputNode.get("value");
        this.data.mobile = this.mobileInputNode.get("value");
        this.data.mail = this.mailInputNode.get("value");
        this.data.qq = this.qqInputNode.get("value");
        //this.data.weixin = this.personWeixinInput.input.get("value");
        this.data.weibo = this.weiboInputNode.get("value")

        var tdContents = this.baseInforRightNode.getElements("td.inforContent");
        var radios = tdContents[2].getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                this.data.genderType = radios[i].value;
                break;
            }
        }
        this.explorer.actions.savePerson(this.data, function(json){
            this.data.id = json.data.id;
            this.iconNode.set("src", this._getIcon());
            if (callback) callback();
        }.bind(this), function(xhr, text, error){
            if (cancel) cancel(xhr, text, error);
        }.bind(this));
    },
    cancel: function(){
        this.nameNode.set("html", this.data.name);
        this.signatureNode.set("html", this.data.signature || this.explorer.options.lp.noSignature);

        var tdContents = this.baseInforRightNode.getElements("td.inforContent");
        tdContents[0].set("html", this.data.unique || "");
        tdContents[1].set("html", this.data.mobile || "");
        tdContents[2].set("html", this.getGenderType());
        tdContents[3].set("html", this.data.mail || "");
        tdContents[4].set("html", this.data.employee || "");
        tdContents[5].set("html", this.data.qq || "");
        tdContents[6].set("html", this.data.display || "");
        tdContents[7].set("html", this.data.weibo || "");

        this.mode = "read";

        this.editNode.setStyle("display", "block");
        this.saveNode.setStyle("display", "none");
        this.cancelNode.setStyle("display", "none");
    },

    getGenderType: function(){
        var text = "";
        if (this.data.genderType){
            switch (this.data.genderType) {
                case "m":
                    text = this.explorer.app.lp.man;
                    break;
                case "f":
                    text = this.explorer.app.lp.female;
                    break;
                default:
                    text = this.explorer.app.lp.other;
            }
        }
        return text;
    },

    loadLeftInfor: function(){
        this.iconAreaNode = new Element("div", {"styles": this.style.baseInforIconAreaNode}).inject(this.baseInforLeftNode);
        this.iconNode = new Element("img", {"styles": this.style.baseInforIconNode}).inject(this.iconAreaNode);
        this.iconNode.set("src", this._getIcon());

        this.nameNode = new Element("div", {"styles": this.style.baseInforNameNode, "text": this.data.name}).inject(this.baseInforLeftNode);
        this.signatureNode = new Element("div", {"styles": this.style.baseInforSignatureNode}).inject(this.baseInforLeftNode);
        this.signatureNode.set("text", (this.data.signature || this.explorer.options.lp.noSignature ));
    },
    loadRightInfor: function(){
        // var text = "";
        // if (this.data.genderType){
        //     switch (this.data.genderType) {
        //         case "m":
        //             text = this.explorer.app.lp.man;
        //             break;
        //         case "f":
        //             text = this.explorer.app.lp.female;
        //             break;
        //         default:
        //             text = this.explorer.app.lp.other;
        //     }
        // }
        var html = "<table cellpadding='3px' cellspacing='3px'>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personUnique+"</td><td class='inforContent'>"+(this.data.unique || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personMobile+"</td><td class='inforContent'>"+(this.data.mobile || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personGender+"</td><td class='inforContent'>"+this.getGenderType()+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personMail+"</td><td class='inforContent'>"+(this.data.mail || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personEmployee+"</td><td class='inforContent'>"+(this.data.employee || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personQQ+"</td><td class='inforContent'>"+(this.data.qq || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personDisplay+"</td><td class='inforContent'>"+(this.data.display || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personWeixin+"</td><td class='inforContent'>"+(this.data.weibo || "")+"</td></tr>";

        html += "<tr><td colspan='4' class='inforAction'></td></tr>";
        this.baseInforRightNode.set("html", html);

        this.baseInforRightNode.getElements("td.inforTitle").setStyles(this.style.baseInforRightTitleNode);
        this.baseInforRightNode.getElements("td.inforContent").setStyles(this.style.baseInforRightContentNode);
        this.baseInforRightNode.getElements("td.inforAction").setStyles(this.style.baseInforRightActionNode);
    },
    destroy: function(){
        this.baseBgNode.empty();
        this.baseBgNode.destroy();
        MWF.release(this);
    },
    _getIcon: function(){
        var src = "data:image/png;base64,"+this.data.icon;
        if (!this.data.icon){
            if (this.data.genderType==="f"){
                src = "/x_component_Org/$Explorer/default/icon/female.png"
            }else{
                src = "/x_component_Org/$Explorer/default/icon/man.png"
            }
        }
        return src;
    }
});

MWF.xApplication.Org.attribute = new Class({
    initialize: function(container, data, item, style){
        this.container = $(container);
        this.data = data;
        this.style = style;
        this.item = item;
        this.selected = false;
        this.load();
    },
    load: function(){
        this.node = new Element("tr", {
            "styles": this.style.contentTrNode
        }).inject(this.container);

        this.selectNode = new Element("td", {
            "styles": this.style.selectNode
        }).inject(this.node);

        this.nameNode = new Element("td", {
            "styles": this.style.nameNode,
            "html": (this.data.name) ? this.data.name : "<input type='text'/>"
        }).inject(this.node);
        this.input = this.nameNode.getFirst("input");
        if (this.input) this.setEditNameInput();

        this.valueNode = new Element("td", {
            "styles": this.style.valueNode
        }).inject(this.node);

        // this.createActionNode();
        // this.setEvent();
        this.loadValue();
    },
    loadValue: function(){
        if (this.data.attributeList) this.valueNode.set("text", this.data.attributeList.join(","));
    },

    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    },








    createActionNode: function(){
        this.actionNode = new Element("td", {"styles": this.style.actionAttributeNode}).inject(this.node);
    },
    selectNodeClick: function(){
        if (!this.selected){
            this.selected = true;
            this.selectNode.setStyles(this.style.selectNode_selected);
            this.node.setStyles(this.style.contentNode_selected);
            this.item.selectedAttributes.push(this);
            this.item.checkDeleteAttributeAction();
        }else{
            this.selected = false;
            this.selectNode.setStyles(this.style.selectNode);
            this.node.setStyles(this.style.contentNode);
            this.item.selectedAttributes.erase(this);
            this.item.checkDeleteAttributeAction();
        }
    },
    valueNodeClick: function(){
        this.valueNode.addEvent("click", function(){
            if (!this.valueInput){
                this.valueNode.empty();
                this.valueInput = new Element("input", {"type": "text", "value": (this.data.attributeList) ? this.data.attributeList.join(",") : ""}).inject(this.valueNode);
                this.setEditValueInput();
            }
        }.bind(this));
    },
    setEditValueInput: function(){
        this.valueInput.setStyles(this.style.nameInputNode);
        this.valueInput.focus();
        this.valueInput.addEvents({
            "blur": function(){
                var value = this.valueInput.get("value");
                if (value){
                    if (value != this.data.attributeList.join(",")){
                        this.saveValue(value);
                    }else{
                        this.valueNode.empty();
                        this.valueInput = null;
                        this.valueNode.set("text", this.data.attributeList.join(","));
                    }
                }else{
                    if (!this.data.id){
                        this.node.destroy();
                        delete this;
                    }else{
                        this.valueNode.empty();
                        this.valueInput = null;
                        this.valueNode.set("text", this.data.attributeList.join(","));
                    }
                }
            }.bind(this)
        });
    },
    saveValue: function(value){
        var oldValue = this.data.attributeList;
        this.data.attributeList = value.split("/,\s*/");
        this.item.explorer.actions.saveCompanyAttribute(this.data, function(json){
            this.data.id = json.data.id;
            this.valueNode.empty();
            this.valueInput = null;
            this.valueNode.set("text", this.data.attributeList.join(","));
        }.bind(this), function(xhr, text, error){
            this.data.attributeList = oldValue;
            this.valueInput.focus();
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.item.explorer.app.notice("request json error: "+errorText, "error");
        }.bind(this));
    },

    save: function(name){
        var oldName = this.data.name;
        this.data.name = name;
        this.item.explorer.actions.saveCompanyAttribute(this.data, function(json){
            this.data.id = json.data.id;
            this.nameNode.empty();
            this.input = null;
            this.nameNode.set("text", this.data.name);
        }.bind(this), function(xhr, text, error){
            this.data.name = oldName;
            this.input.focus();
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.item.explorer.app.notice("request json error: "+errorText, "error");
        }.bind(this));
    },
    remove: function(){
        this.item.explorer.actions.deleteCompanyAttribute(this.data.id, function(){
            this.node.destroy();
            delete this;
        }.bind(this));
    }
});
MWF.xApplication.Org.PersonExplorer.PersonAttribute = new Class({
    Extends: MWF.xApplication.Org.attribute
});
MWF.xApplication.Org.PersonExplorer.PersonIdentity = new Class({
    Extends: MWF.xApplication.Org.attribute,

    load: function(){
        this.node = new Element("tr", {
            "styles": this.style.contentTrNode
        }).inject(this.container);

        this.selectNode = new Element("td", {
            "styles": this.style.selectNode
        }).inject(this.node);

        this.nameNode = new Element("td", {
            "styles": this.style.nameNode,
            "html": (this.data.name) ? this.data.name : "<input type='text'/>"
        }).inject(this.node);
        this.input = this.nameNode.getFirst("input");
        if (this.input) this.setEditNameInput();

        this.departmentNode = new Element("td", {
            "styles": this.style.valueNode,
            "text": this.data.departmentName
        }).inject(this.node);

        this.companyNode = new Element("td", {
            "styles": this.style.valueNode,
            "text": this.data.companyName
        }).inject(this.node);


        this.valueNode = new Element("td", {
            "styles": this.style.valueNode
        }).inject(this.node);

        // this.createActionNode();
        // this.setEvent();
        //this.loadValue();
    }
});