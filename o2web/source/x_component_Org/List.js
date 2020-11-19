MWF.xApplication.Org.List = new Class({
    Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "action": false,
        "canEdit": true,
        "data": {
            "name": "",
            "attributeList": [""]
        },
        "attr": [],
        "saveAction": "savePersonAttribute",
        "deleteAction": "removePersonAttribute",
        "deleteItemTitle": MWF.xApplication.Org.LP.deleteAttributeTitle,
        "deleteItemText": MWF.xApplication.Org.LP.deleteAttribute
	},
    _loadPath: function(){
        this.path = "../x_component_Org/$List/";
        this.cssPath = "../x_component_Org/$List/"+this.options.style+"/css.wcss";
    },
    initialize: function(node, content, options){
        this.setOptions(options);

        this._loadPath();
        this._loadCss();

        this.content = content;
        this.contentNode = $(node);

        this.items = [];
        this.selectedItems = [];
    },
    load: function(headers){
        this.headers = headers;
        //this.node = new Element("div", {"styles": this.css.node}).inject(this.contentNode);

        var html = "<table cellspacing='0' cellpadding='5' border='0' width='80%' align='center' style='line-height:normal; clear: both;'>";
        html += "<tr><th style='width:20px'></th>";
        headers.each(function(title){
            html += "<th style='"+title.style+"'>"+title.text+"</th>";
        }.bind(this));
        html += "</table>";
        this.contentNode.set("html", html);

        this.loadAction();

        this.table = new HtmlTable(this.contentNode.getFirst("table"));
        this.contentNode.getElements("th").setStyles(this.css.listTitle);
    },
    // reLoad: function(){
    //     var html = "<table cellspacing='0' cellpadding='5' border='0' width='80%' align='center' style='line-height:normal; clear: both;'>";
    //     html += "<tr><th style='width:20px'></th>";
    //     this.headers.each(function(title){
    //         html += "<th style='"+title.style+"'>"+title.text+"</th>";
    //     }.bind(this));
    //     html += "</table>";
    //     this.contentNode.set("html", html);
    //
    //     this.table = new HtmlTable(this.contentNode.getFirst("table"));
    //     this.contentNode.getElements("th").setStyles(this.css.listTitle);
    // },
    loadAction: function(){
        this.actionAreaNode = new Element("div", {"styles": this.css.actionAreaNode}).inject(this.contentNode, "top");
        if (this.options.action){
            this.actionNode = new Element("div", {"styles": this.css.actionNode}).inject(this.actionAreaNode);
            this.deleteAction = new Element("div", {"styles": this.css.deleteActionNode_disabled, "text": this.content.explorer.app.lp.delete}).inject(this.actionNode);
            this.addAction = new Element("div", {"styles": this.css.addActionNode, "text": this.content.explorer.app.lp.add}).inject(this.actionNode);

            this.addAction.addEvent("click", function(){
                this.addItem();
            }.bind(this));
            this.deleteAction.addEvent("click", function(e){
                this.deleteItem(e);
            }.bind(this));
            this.fireEvent("postLoadAction")
        }
    },
    addItem: function(){
        var data = Object.clone(this.options.data);
        var tr = new MWF.xApplication.Org.List.Item(data, this.options.attr, this);
        this.items.push(tr);
        tr.edit(tr.tr.tds[1]);
    },
    deleteItem: function(e){
        if (this.selectedItems.length){
            this.fireEvent("queryDelete");
            var _self = this;

            this.content.explorer.app.confirm("infor", e, this.options.deleteItemTitle, this.options.deleteItemText, 350, 120, function(){
                this.close();

                var delCount = 0;
                var deleteCompleted = function(){
                    if (delCount === _self.selectedItems.length){
                        var continueDelete = true;
                        _self.fireEvent("delete", continueDelete);
                        if (continueDelete){
                            while (_self.selectedItems.length){
                                var tr = _self.selectedItems[0];
                                tr.destroy();
                            }
                        }
                        _self.fireEvent("postDelete", delCount);
                    }
                };
                _self.selectedItems.each(function(item){
                    item["delete"](function(){
                        delCount++;
                        deleteCompleted();
                    });
                }.bind(this));
            }, function(){this.close();});
        }
    },

    push: function(data){
        // var rows = [""];
        // attr.each(function(n){
        //     if (typeOf(n)==="function"){
        //         rows.push(n.apply(data));
        //     }else if(typeOf(n)==="string"){
        //         rows.push(data[n]);
        //     }else{
        //         rows.push("");
        //     }
        // }.bind(this));
        // var tr = this.table.push(rows, {"styles": this.css.contentTrNode});
        // tr.tds.each(function(td){td.setStyles(this.css.contentTdNode);}.bind(this));
        // tr.tds[0].setStyles(this.css.selectTdNode);

        var i = this.items.push(new MWF.xApplication.Org.List.Item(data, this.options.attr, this));
        return this.items[i-1];
    },
    setAction: function(){
        if (this.selectedItems.length){
            this.deleteAction.setStyles(this.css.deleteActionNode);
        }else{
            this.deleteAction.setStyles(this.css.deleteActionNode_disabled);
        }
    },
    clear: function(){
        this.items = [];
        this.selectedItems = [];
        var table = this.contentNode.getFirst("table");
        while (table.rows.length>1){
            table.rows[table.rows.length-1].destroy();
        }
        //this.reLoad();
    }

});
MWF.xApplication.Org.List.Item = new Class({
    initialize: function(data, attr, list){
        this.data = data;
        this.attr = attr;
        this.list = list;
        this.css = this.list.css;
        this.load();
    },
    reload: function(data){
        this.data = data;
        this.tr.tds.each(function(td, i){
            if (i===0) this.selectTd = td;
            if (i>0){
                var at = this.attr[i-1];
                if (typeOf(at)==="object"){
                    if (at.get){
                        td.set("text", at.get.apply(this.data));
                    }else{
                        td.set("text", "");
                    }
                }else if(typeOf(at)==="string"){
                    if (at==="icon"){
                        td.set("html", "<div></div>");
                    }else{
                        var v = this.data[at];
                        if (typeOf(v)==="array") v = v.join(",");
                        td.set("text", v);
                    }
                }else{
                    td.set("text", "");
                }
                if (at.events){
                    if (at.events["init"]) at.events["init"].apply({"item": this, "data": this.data, "td": td, "item": this});
                }
            }
            td.setStyles(this.css.contentTdNode);
            td.set("title", td.get("text"));
        }.bind(this));
    },
    load: function(){
        var rows = [""];
        this.attr.each(function(n){
            if (typeOf(n)==="object"){
                if (n.get){
                    var v = n.get.apply(this.data) || "";

                    v = v.replace(/\</g, "&lt;");
                    v = v.replace(/\</g, "&gt;");
                    rows.push(v);
                }else if(n.getHtml){
                    var v = n.getHtml.apply(this.data);
                    rows.push(v);
                }else{
                    rows.push("");
                }
            }else if(typeOf(n)==="string"){
                if (n==="icon"){
                    rows.push("<div>cc</div>");
                }else{
                    rows.push(this.data[n]);
                }
            }else{
                rows.push("");
            }
        }.bind(this));
        this.tr = this.list.table.push(rows, {"styles": this.css.contentTrNode});
        this.tr.tr.store("data", this.data);
        var _self = this;
        this.tr.tds.each(function(td, i){
            td.setStyles(this.css.contentTdNode);
            td.set("title", td.get("text"));
            if (i===0) this.selectTd = td;
            if (i>0){
                if (this.list.options.action || this.list.options.canEdit){
                    td.store("attr", this.attr[i-1]);
                    if (this.list.options.canEdit){
                        td.addEvent("click", function(){
                            _self.edit(this);
                        });
                    }
                }
                var at = this.attr[i-1];
                if (at.events){
                    td.removeEvents("click");
                    Object.each(at.events, function(v, k){
                        if (k.toLowerCase!=="init") td.addEvent(k, v.bind({"data": this.data, "td": td, "item": this}));
                    }.bind(this));
                    if (at.events["init"]) at.events["init"].apply({"item": this, "data": this.data, "td": td, "item": this});
                }
            }
        }.bind(this));

        if (this.list.options.action){
            this.selectTd.setStyles(this.css.selectTdNode);
            this.selectTd.addEvent("click", function(){
                if (!this.isSelected){
                    this.selected();
                }else{
                    this.unSelected();
                }
            }.bind(this));
        }else{
            this.selectTd.setStyles(this.css.blankTdNode);
        }
    },
    edit: function(td){
        var attr = td.retrieve("attr");
        var text = td.get("text");
        td.empty();
        var input = new Element("input", {"styles": this.css.inputNode, "value": text}).inject(td);
        td.removeEvents("click");
        var _self = this;
        input.focus();
        input.addEvents({
            "blur": function(){
                var value = this.get("value");
                if (value){
                    if (typeOf(attr)==="object"){
                        if (attr.set){
                            attr.set.apply(_self.data, [value]);
                        }
                    }else if(typeOf(attr)==="string") {
                        _self.data[attr] = value
                    }
                }
                _self.editCompleted(td, value, text);
            }
        });
    },
    editCompleted: function(td, value, text){
        td.empty();
        if (!value && !text){
            if (td.cellIndex===1){
                this.destroy();
            }
        }else if (!value){
            if (td.cellIndex===1){
                td.set("text", text);
            }else{
                td.set("text", value);
                if (value!==text) this.save(td);
            }
        }else{
            td.set("text", value);
            if (value!==text) this.save(td);
        }
        var _self = this;
        if (this.list.options.canEdit){
            td.addEvent("click", function(){
                _self.edit(this);
            });
        }
    },
    "delete": function(callback){
        this.list.content.explorer.actions[this.list.options.deleteAction](this.data.id, function(){
            if (callback) callback();
            //this.destroy();
        }.bind(this));

    },
    destroy: function(){

        this.list.items.erase(this);
        if (this.isSelected) this.unSelected();
        this.list.setAction();
        this.tr.tr.destroy();
        MWF.release(this);
    },

    save: function(td){
        this.list.content.explorer.actions[this.list.options.saveAction](this.data, function(json){
            this.list.fireEvent("postSave", [this, json.data.id]);
            this.data.id = json.data.id;
        }.bind(this), function(xhr, text, error){
            td.set("text", "");
            this.edit(td);
            this.list.content.explorer.app.notice((JSON.decode(xhr.responseText).message.trim() || "request json error"), "error");
        }.bind(this));
    },

    selected: function(){
        this.selectTd.setStyles(this.css.selectTdNode_selected);
        this.tr.tr.setStyles(this.css.contentTrNode_selected);
        this.list.selectedItems.push(this);
        this.isSelected = true;
        this.list.setAction();
    },
    unSelected: function(){
        this.selectTd.setStyles(this.css.selectTdNode);
        this.tr.tr.setStyles(this.css.contentTrNode);
        this.list.selectedItems.erase(this);
        this.isSelected = false;
        this.list.setAction();
    }


});