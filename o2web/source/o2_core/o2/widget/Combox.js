o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.Combox = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "list": [],
        "optionsMethod": null,
        "splitStr": /,\s*|;\s*|，\s*|；\s*/g,
        "splitShow": ", ",
        "focusList": false,
        "noDataColor": true,
        "onlySelect": false,
        "count": 0
    },
    initialize: function(options){
        this.setOptions(options);
        this.splitRegExp = new RegExp(this.options.splitStr);
        this.path = o2.session.path+"/widget/$Combox/";
        this.cssPath = o2.session.path+"/widget/$Combox/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.values = [];
        //this.lastValue();
        this.load();
    },
    getSelectList: function(value, callback){
        var list = [];

        if (this.options.list.length){
            if (this.options.onlySelect){
                list = this.options.list;
            }else{
                var listValues = this.options.list.filter(function(v, i){
                    var key = (v.keyword || "")+v.text;
                    return (key.indexOf(value)!==-1);
                });
                list = listValues;
            }
        }
        if (this.options.optionsMethod){
            this.options.optionsMethod(value, function(methodValues){
                if (methodValues.length){
                    if (list.length){
                        list = list.concat(methodValues);
                    }else{
                        list = methodValues;
                    }
                }

                if (callback) callback(list);
            }.bind(this));
            //var methodValues = this.options.optionsMethod(value);
        }else{
            if (callback) callback(list);
        }
        //return list;
    },
    load: function(){
        if (this.fireEvent("queryLoad")){
            this.node = new Element("div", {"styles": this.css.comboxNode});
            this.copyPrototype();

            this.setEvent();

            this.fireEvent("postLoad");
        }
    },
    clear: function(){
        if (this.input) o2.release(this.input);
        this.input = null;
        this.node.empty();
        this.values = [];
    },
    getData: function(){
        var node = this.node.getFirst();
        var data = [];
        while (node){
            var item = node.retrieve("item");
            if (item){
                if (item.data || item.value){
                    data.push(item.data || item.value);
                }
            }
            node = node.getNext();
        }
        return data;
    },
    copyPrototype: function(){
        this.inject = this.node.inject.bind(this.node);
        this.setStyle = this.node.setStyle.bind(this.node);
        this.setStyles = this.node.setStyles.bind(this.node);
        this.set = this.node.set.bind(this.node);
        // for (k in this.node.constructor.prototype){
        //     if (typeOf(this.node[k])==="function"){
        //         this[k] = this.node[k].bind(this.node);
        //     }else{
        //         this[k] = this.node[k];
        //     }
        // }
    },
    setEvent:function(){
        this.node.addEvents({
            "focus": function(e){
                if (!this.selectStatus){
                    if (!this.editItem) this.intoEdit(e);
                }
            }.bind(this),
            "mousedown": function(e){
                if (!this.selectStatus){
                    if (!this.editItem) this.intoEdit(e);
                    e.stopPropagation();
                    e.preventDefault();
                }
            }.bind(this)
            // "selectstart": function(e){
            //     if (e,event.buttons = "")
            //     if (e.event.buttons!=0){
            //         this.selectStatus = true;
            //         this.stopEdit();
            //     }
            // }.bind(this)
            // // "mouseup": function(e){
            // //     this.selectStatus = false;
            // // }.bind(this),
            // "dblclick": function(e){
            //     var range = document.createRange();
            //     range.selectNodeContents(this.node);
            //     alert(range.toString());
            // }.bind(this)
        });
    },

    stopEdit: function(){
        if (this.editItem){
            this.editItem.input.confirmValue();
        }else{
            if (this.input) this.input.confirmValue();
        }
        if (this.input) this.input.node.hide();
    },
    intoEdit: function(e){
        if (this.options.count){
            if (this.values.length>=this.options.count){
                // if (this.input) this.input.noBlur = true;
                if (this.input) this.input.node.hide();
                //this.getLast().edit();
                return false;
            }
        }
        if (!this.input){
            this.input = new o2.widget.Combox.Input(this, this, "");
            this.input.node.inject(this.node);
            this.input.node.setStyle("width", "1px");
        }
        if (e){
            if (e.type==="item"){
                var node = e.node;
                if (e.input){
                    node = e.input.optionListNode || e.input.node;
                }
                this.input.node.inject(node, "after");
            }else{
                var value = this.input.node.get("value");
                if (value) this.commitInput();
                var node = this.node.getFirst();
                while (node){
                    if (node.retrieve("item")){
                        var p = node.getPosition();
                        var s = node.getSize();
                        if (p.x>e.page.x && (p.y-3)<e.page.y && (p.y+s.y+3)>e.page.y) break;
                    }
                    node = node.getNext();
                }
                if (node){
                    this.input.node.inject(node, "before");
                }else{
                    this.input.node.inject(this.node);
                }
            }

        }
        this.input.node.show();
        this.input.setInputNodeStyles();
        //this.input.node.set("value", "111");
        this.input.node.focus();
        this.input.setInputPosition();
        if (this.options.focusList) this.input.searchItems();
    },
    commitInput: function(data){
        var valueStr = this.input.node.get("value");
        this.input.node.set("value", "");
        if (valueStr){
            var values = valueStr.split(this.splitRegExp);
            if (this.options.count) values = values.slice(0, this.options.count);
            this.createItem(values, 0, data, function(){
                this.input.node.set("value", "");
                //this.input.hideOptionList();
                this.input.searchItems();
                this.input.setInputWidth();

                window.setTimeout(function(){
                    // if (this.input){
                    //     this.input.node.blur();
                    //     this.input.node.focus();
                    // }else{
                    this.intoEdit();
                    // }
                }.bind(this), 10);
            }.bind(this));
            //
            // values.each(function(value){
            //     this.values.push(new o2.widget.Combox.Value(this, value, data));
            //
            // }.bind(this));
            //

        }
    },
    createItem: function(values, i, data, callback){
        if (values[i]){
            var value = values[i];

            var itemData = data;
            var itemText = value;
            if (!itemData){
                if (typeOf(value)==="object"){
                    itemData = value.value;
                    itemText = value.text;
                }
            }

            var v = new o2.widget.Combox.Value(this, itemText, itemData, {
                "onLoad": function(){
                    i++;
                    if (values[i]){
                        this.createItem(values, i, data, callback);
                    }else{
                        if (callback) callback();
                    }
                }.bind(this)
            }, true);
            this.values.push(v);
            v.load();
        }
    },
    addNewValues: function(values, callback){
        this.createItem(values, 0, null, callback);
    },
    addNewValue: function(value, data){
        if (value){
            this.values.push(new o2.widget.Combox.Value(this, value, data));
            if (this.input){
                this.input.node.set("value", "");
                this.input.hideOptionList();
                this.input.searchItems();
                this.input.setInputWidth();
            }
        }
    },
    getFirst: function(){
        var node = this.node.getFirst();
        if (node){
            while (node && !node.retrieve("item")){  node = node.getNext(); }
            if (node) return node.retrieve("item");
        }
        return null;
    },
    getLast: function(){
        var node = this.node.getLast();
        if (node){
            while (node && !node.retrieve("item")){  node = node.getPrevious(); }
            if (node) return node.retrieve("item");
        }
        return null;
    },
    getNextItem: function(){
        var node = this.input.node.getNext();
        if (node) return node.retrieve("item");
        return null;
    },
    getPreviousItem: function(){
        var node = this.input.node.getPrevious();
        while (node && !node.retrieve("item")){  node = node.getPrevious(); }
        if (node) return node.retrieve("item");
        return null;
    },
    deleteItem: function(item){
        this.values.erase(item);
        item.node.destroy();
        if (item.input) item.input.destroy();
        o2.release(item);
    }
});

o2.widget.Combox.Value = new Class({
    Implements: [Options, Events],
    initialize: function(combox, value, data, options, delayLoad){
        this.setOptions(options);
        this.combox = combox;
        this.css = this.combox.css;
        this.value = value;
        this.data = data || null;
        this.type = "item";
        this.index = this.combox.values.length;
        if(!delayLoad){
            this.load();
        }
    },
    getItemPosition: function(){
        var i=0;
        var item = this.getPreviousItem();
        while (item){
            i++;
            item = item.getPreviousItem();
        }
        return i;
    },
    checkData: function(callback){
        if (!this.data){
            this.combox.getSelectList(this.value, function(list){
                if (list.length==1){
                    this.data = list[0].value;
                    this.value = list[0].text;
                }
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    load: function(){

        this.node = new Element("div", {"styles": this.css.valueItemNode});
        if (this.combox.input){
            this.node.inject(this.combox.input.node, "before");
        }else{
            this.node.inject(this.combox.node);
        }

        if (this.getNextItem()){
            this.node.set("text", this.value+this.combox.options.splitShow);
        }else{
            this.node.set("text", this.value);
        }
        var prev = this.getPreviousItem();
        if (prev){
            prev.node.set("text", prev.value+this.combox.options.splitShow);
        }

        this.node.store("item", this);
        this.node.addEvents({
            "click": function(e){this.edit();e.stopPropagation();}.bind(this),
            "mouseover": function(e){this.node.setStyles(this.css.valueItemNode_over)}.bind(this),
            "mouseout": function(e){this.node.setStyles(this.css.valueItemNode)}.bind(this),
            "mousedown": function(e){e.stopPropagation();}.bind(this),
            //"mouseup": function(e){document.all.testCombox.innerHTML +="<br>"+this.value+"mouseup"; this.edit(); e.stopPropagation();}.bind(this),
            "focus": function(e){e.stopPropagation();}
        });
        this.checkData(function(){
            if (this.options.noDataColor) if (!this.data) this.node.setStyle("color", "#bd0000");
            this.combox.fireEvent("commitInput", [this]);
            this.combox.fireEvent("change", [this]);
            this.fireEvent("load");
        }.bind(this));
    },

    edit: function(where){
        //this.combox.commitInput();
        if (!this.input){
            this.input = new o2.widget.Combox.Input(this.combox, this, this.value);
            this.input.node.inject(this.node, "after");
            this.input.hide();
        }
        this.input.node.show();
        this.input.setInputNodeStyles();

        this.node.hide();
        this.input.setInputPosition(where);
        this.combox.editItem = this;
        if(this.combox.input)this.combox.input.hide();
        this.input.searchItems();
    },
    commitInput: function(data){
        var oldValues = this.combox.values.map(function(v){ return v.data || v.value});
        var valueStr = this.input.node.get("value");
        if (valueStr){
            var values = valueStr.split(this.combox.splitRegExp);
            if (values.length>1){
                this.input.node.set("value", "");
                this.combox.editItem = null;
                var combox = this.combox;
                this.combox.deleteItem(this);
                //combox.intoEdit(this);
                combox.input.node.set("value", valueStr);
                combox.commitInput();
            }else{
                if (this.value==valueStr){
                    this.data = data || this.data;
                }else{
                    this.value = valueStr;
                    this.data = data || null;
                }

                if (!this.data){
                    if (this.options.noDataColor) this.node.setStyle("color", "#bd0000");
                }else{
                    this.node.setStyle("color", "");
                }

                if (this.value){
                    if (this.getNextItem()){
                        this.node.set("text", this.value+this.combox.options.splitShow);
                    }else{
                        this.node.set("text", this.value);
                    }

                    this.node.show();
                    this.input.hide();
                    this.combox.editItem = null;
                    this.combox.fireEvent("commitInput", [this]);
                    this.combox.fireEvent("change", [this, oldValues]);
                }else{
                    this.combox.editItem = null;
                    var combox = this.combox;
                    this.input.hideOptionList();
                    this.combox.deleteItem(this);
                    combox.fireEvent("change", [this, oldValues]);
                }
            }

        }else{
            this.combox.editItem = null;
            var combox = this.combox;
            this.input.hideOptionList();
            this.combox.deleteItem(this);
            combox.fireEvent("change", [this]);
        }
        window.setTimeout(function(){
            // if (this.combox.input){
            //     this.combox.input.show();
            //     this.combox.input.node.blur();
            //     this.combox.input.node.focus();
            // }else{
            this.combox.intoEdit(this);
            // }
        }.bind(this), 10);
    },
    getNextItem: function(){
        var node = (this.input) ? this.input.node.getNext() : this.node.getNext();
        while (node && !node.retrieve("item")){  node = node.getNext(); }
        if (node) return node.retrieve("item");
        return null;
    },
    getPreviousItem: function(){
        var node = this.node.getPrevious();
        while (node && !node.retrieve("item")){  node = node.getPrevious(); }
        if (node) return node.retrieve("item");
        return null;
    }
});

o2.widget.Combox.Input = new Class({
    initialize: function(combox, bind, value){
        this.combox = combox;
        this.bind = bind;
        this.css = this.combox.css;
        this.node = new Element("input", {"styles": this.css.inputNode, "type":"text", "value": value});
        if (this.combox.options.onlySelect) this.node.set("readonly", true);
        this.setInputNodeStyles();
        this.setInputWidth();
        this.setEvent();
        this.hideOption = false;
    },
    hide: function(){
        this.node.hide();
        this.hideOptionList();
    },
    setEvent: function(){
        this.node.addEvents({
            "mousedown": function(e){e.stopPropagation();},
            "input": function(e){
                this.setInputWidth();
                this.searchItems();
                var v = this.node.get("value");
                var s = v.substr(v.length-1, 1);
                if (s=="，" || s=="；"){
                    this.node.set("value", v.substr(0, v.length-1));
                    if (this.optionListNode && this.optionListNode.getChildren().length===1){
                        this.confirmValue();
                    }else{
                        this.bind.commitInput();
                    }
                }
            }.bind(this),
            "keydown": function(e){
                //if (e.code===186 || e.code===188 || e.code===13 || e.event.key==="," || e.event.key===";" || e.event.code==="Semicolon" || e.event.code==="Comma"){
                if (e.code===186 || e.code===188 || e.code===13){
                    if (e.code===13){
                        this.confirmValue();
                    }else{
                        if (this.optionListNode && this.optionListNode.getChildren().length===1){
                            this.confirmValue();
                        }else{
                            this.bind.commitInput();
                        }
                    }
                    e.preventDefault();
                    e.stopPropagation();
                }
                if (e.code===37){ //left
                    if (this.node.selectionStart==0 && this.node.selectionEnd==0){
                        var item = this.bind.getPreviousItem();
                        if (item){
                            this.bind.commitInput();
                            item.edit();
                        }
                        e.preventDefault();
                        e.stopPropagation();
                    }
                }
                if (e.code===39){ //right
                    var idx = this.node.get("value").length;
                    if (this.node.selectionStart==idx && this.node.selectionEnd==idx){
                        var item = this.bind.getNextItem();
                        if (item){
                            this.bind.commitInput();
                            item.edit("start");
                        }
                        e.preventDefault();
                        e.stopPropagation();
                    }
                }
                if (e.code===8){ //backspace
                    if (this.node.selectionStart==0 && this.node.selectionEnd==0){
                        var item = this.bind.getPreviousItem();
                        if (item){
                            this.bind.commitInput();
                            item.edit();
                            item.input.setInputPosition();
                        }
                        e.preventDefault();
                        e.stopPropagation();
                    }
                }
                if (e.code===46){ //del
                    var idx = this.node.get("value").length;
                    if (this.node.selectionStart==idx && this.node.selectionEnd==idx){
                        var item = this.bind.getNextItem();
                        if (item){
                            this.bind.commitInput();
                            item.edit();
                            item.input.setInputPosition("start");
                        }
                        e.preventDefault();
                        e.stopPropagation();
                    }
                }
                if (e.code===38){   //up
                    this.selectPrevOption();
                }
                if (e.code===40){   //down
                    this.selectNextOption();
                }
            }.bind(this),
            "blur": function(e){
                //if (!this.noBlur){
                if ((this.combox.editItem == this.bind) || (!this.combox.editItem)){
                    this.bind.commitInput();
                }
                this.hideOptionList();
                e.stopPropagation();
                //}
            }.bind(this)
        });
    },
    setSelectedOption: function(node){
        var styles = node.getStyles("background-color", "background", "color");
        node.store("originalStyle", styles);
        node.setStyles(this.css.optionNode_selected);
        this.selectedOption = node;
    },
    selectPrevOption: function(){
        if (this.optionListNode){
            if (this.selectedOption) this.selectedOption.setStyles(this.selectedOption.retrieve("originalStyle"));
            node = (this.selectedOption)? (this.selectedOption.getPrevious() || this.optionListNode.getLast()) : this.optionListNode.getLast();
            if (node)this.setSelectedOption(node);
            this.optionListNode.scrollToNode(node, "top");
        }
    },
    selectNextOption: function(){
        if (this.optionListNode){
            if (this.selectedOption) this.selectedOption.setStyles(this.selectedOption.retrieve("originalStyle"));
            node = (this.selectedOption)? (this.selectedOption.getNext() || this.optionListNode.getFirst()) : this.optionListNode.getFirst();
            if (node)this.setSelectedOption(node);
            this.optionListNode.scrollToNode(node, "bottom");
        }
    },
    selectOption: function(node){
        if (this.optionListNode){
            if (this.selectedOption) this.selectedOption.setStyles(this.selectedOption.retrieve("originalStyle"));
            if (node)this.setSelectedOption(node);
        }
    },
    confirmValue: function(){
        if (this.optionListNode){
            if (this.selectedOption){
                var data = this.selectedOption.retrieve("data");
                var text = this.selectedOption.get("text");
                this.node.set("value", text);
                this.setInputWidth();
                this.bind.commitInput(data);

                this.selectedOption = null;
            }else{
                this.bind.commitInput();
            }
        }else{
            this.bind.commitInput();
        }
    },
    setInputWidth: function(){
        if (this.node){
            var value = this.node.get("value");
            if (!this.tmpDivNode) this.tmpDivNode = new Element("div").set("style", this.node.get("style")).setStyles({"display": "none", "float": "left", "width": "auto"}).inject(document.body);
            this.tmpDivNode.empty();
            value = value.replace(/\s/g, "&nbsp");
            this.tmpDivNode.set("html", value);
            var size = this.tmpDivNode.getComputedSize();
            var x = size.width-size.computedLeft-size.computedRight+5;
            var nodeSize = this.combox.node.getComputedSize();
            if (x<1) x=1;
            if (x>nodeSize.width) x = nodeSize.width;
            this.node.setStyle("width", ""+x+"px");
            this.node.focus();
        }
    },
    setInputPosition: function(where){
        this.node.focus();
        if (where==="start"){
            this.node.setSelectionRange(0,0);
        }else{
            var idx = this.node.get("value").length;
            this.node.setSelectionRange(idx,idx);
        }
    },
    setInputNodeStyles: function(){
        if (this.node){
            var styles = this.combox.node.getStyles("font-family", "min-height", "font-size", "font-weight", "font-variant", "font-style", "line-height", "color", "text-align");
            if (!this.inputHeight){
                var size = this.combox.node.getComputedSize();
                this.inputHeight = ""+size.height+"px";
                styles.height = ""+size.height+"px";
            }
            this.node.setStyles(styles);
        }
    },
    destroy: function(){
        this.node.destroy();
        o2.release(this);
    },
    searchItems: function(){
        this.hideOption = true;
        var value = this.node.get("value");
        if (value || this.combox.options.focusList){
            this.combox.getSelectList(value, function(list){
                if (this.hideOption){
                    if (list.length){
                        this.showOptionList(list);
                    }else{
                        this.hideOptionList();
                    }
                }
            }.bind(this));
            //var list = this.combox.getSelectList(value);
        }else{
            this.hideOptionList();
        }
    },
    createOptionListNode: function(){
        // this.relativeOptionListLocation = new Element("div", {"styles": this.css.relativeOptionListLocation});
        // this.relativeOptionListLocation.inject(this.node, "after");
        this.optionListNode = new Element("div", {"styles": this.css.optionListNode});
        this.optionListNode.inject(this.node, "after");;
        this.optionListNode.addEvents({
            "mousedown": function(e){
                this.noBlur = true;
                e.preventDefault();
                e.stopPropagation();
            }.bind(this),
            "mouseup": function(e){
                this.node.focus();
                this.noBlur = false;
            }.bind(this)
        });
    },
    showOptionList: function(list){
        if (!this.optionListNode) this.createOptionListNode();
        this.optionListNode.setStyle("dispaly", "block");
        this.optionListNode.empty();

        var _self = this;
        var styles = this.combox.node.getStyles("font-family", "font-size", "font-weight", "font-variant", "font-style", "line-height", "color", "text-align");
        list.each(function(option){
            var optionNode = new Element("div", {"styles": this.css.optionNode, "text": option.text}).inject(this.optionListNode);
            optionNode.store("data", option.value);
            optionNode.setStyles(styles);
            optionNode.addEvents({
                "mousedown": function(){
                    // if (_self.bind.edit){
                    //     _self.bind.edit();
                    // }else{
                    //     _self.bind.values[_self.bind.values.length-1].edit();
                    // }
                    _self.confirmValue();
                },
                "mouseover": function(){_self.selectOption(this);}
            });
        }.bind(this));
        var node = this.optionListNode.getFirst();
        if (node){
            var styles = node.getStyles("background-color", "background", "color");
            node.store("originalStyle", styles);
            node.setStyles(this.css.optionNode_selected);
            this.selectedOption = node;
        }

        this.optionListNode.position({
            "relativeTo": this.node,
            "position": "leftBottom",
            "edge": "leftTop",
            "offset": {"y": 3}
        });
        var pNode = this.optionListNode.getOffsetParent();
        var p = this.optionListNode.getPosition(pNode);
        var s = this.optionListNode.getSize();
        var ps = pNode.getSize();
        //var ss = pNode.getScroll();
        if (p.y+s.y>ps.y){
            this.optionListNode.position({
                "relativeTo": this.node,
                "position": "leftTop",
                "edge": "leftBottom",
                "offset": {"y": 3}
            });
        }

        var p = this.optionListNode.getPosition(pNode);
        if (p.y<10){
            var top = this.optionListNode.getStyle("top").toInt();
            top = top-p.y+10;
            this.optionListNode.setStyle("top", ""+top+"px");
        }

        if (layout.desktop.offices){
            Object.each(layout.desktop.offices, function(office){
                if (this.optionListNode.isOverlap(office.officeNode)){
                    office.hide();
                }
            }.bind(this));
        }
    },
    hideOptionList: function(){
        if (this.optionListNode){
            this.optionListNode.destroy();
            this.optionListNode = null;
        }
        this.hideOption = false;
        if (layout.desktop.offices){
            Object.each(layout.desktop.offices, function(office){
                if (layout.desktop.currentApp && layout.desktop.currentApp.appId===office.form.app.appId){
                    var display = office.officeNode.retrieve("officeDisplay");
                    if (display) office.officeNode.setStyle("display", display);
                }
            });
        }
    }
});
