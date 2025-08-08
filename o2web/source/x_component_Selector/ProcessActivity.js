MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.ProcessActivity = new Class({
	Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "values": [],
        "names": [],
        "expand": false,
        "forceSearchInItem" : true,
        "application": "",
        "process": ""
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectProcess});
    },
    _init : function(){
        this.selectType = "ProcessActivity";
        this.className = "ProcessActivity";
    },
    loadSelectItems: function(addToNext){
        this.designerAction.listProcess(this.options.application, function(json){
            var processes = json.data;
            if (this.options.process){
                processes = processes.filter(function(app){
                    return app.name === this.options.process;
                }.bind(this));
            }
            if (processes.length){
                processes.each(function(data){
                    var category = this._newItemCategory(data, this, this.itemAreaNode);
                    // this.designerAction.getProcess(data.id, function(data){
                    //     var processData = data.data;
                    //     var activityList = [].concat(
                    //         processData.manualList || [],
                    //         processData.conditionList || [],
                    //         processData.choiceList || [],
                    //         processData.parallelList || [],
                    //         processData.splitList || [],
                    //         processData.mergeList || [],
                    //         processData.embedList || [],
                    //         processData.publishList || [],
                    //         processData.invokeList || [],
                    //         processData.cancelList || [],
                    //         processData.delayList || [],
                    //         processData.messageList || [],
                    //         processData.serviceList || [],
                    //         processData.endList || []
                    //     );
                    //     activityList.forEach(function(data){
                    //         var d = {
                    //             id: data.id,
                    //             name: data.name,
                    //             distinguishedName: data.name+'@'+data.id+'@A'
                    //         }
                    //         var item = this._newItem(d, this, category.children);
                    //         this.items.push(item);
                    //     }.bind(this));

                    // }.bind(this));

                    // data.processList.each(function(d){
                    //     d.applicationName = data.name;
                    //     var item = this._newItem(d, this, category.children);
                    //     this.items.push(item);
                    // }.bind(this));
                }.bind(this));
            }
        }.bind(this));
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(data){
        return data.processList || [];
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.ProcessActivity.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        if( !id )return;
        this.processAction.getProcess(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : (typeOf(id)=="string") ? id : id.id), async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.ProcessActivity.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.ProcessActivity.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.ProcessActivity.Item = new Class({
	Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.alias ? this.data.name +"("+this.data.alias+")"  : this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/processicon.png)");
    },
    _getTtiteText: function(){
        return this.data.alias ? this.data.name +"("+this.data.alias+")"  : this.data.name;
    },
    loadSubItem: function(){
        return false;
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object"){
                // return (this.data.id === item.id);
                if( this.data.id && item.id ){
                    return this.data.id === item.id;
                }else{
                    return this.data.name === item.name;
                }
            }
            if (typeOf(item)==="string") return (this.data.id === item) || (this.data.name === item);
            return false;
        }.bind(this));
        if (selectedItem.length){
            this.selectedSingle();
        }
    },
    checkSelected: function(){

        var selectedItem = this.selector.selectedItems.filter(function(item, index){
            //return (item.data.id === this.data.id);
            if( item.data.id && this.data.id){
                return item.data.id === this.data.id;
            }else{
                return item.data.name === this.data.name;
            }
        }.bind(this));
        if (selectedItem.length){
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    }
});

MWF.xApplication.Selector.ProcessActivity.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        var name =  this.data.alias ? this.data.name +"("+this.data.alias+")"  : this.data.name;
        return this.data.processName + ' - '+name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/processicon.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                if( item.data.id && this.data.id){
                    return item.data.id === this.data.id;
                }else{
                    return item.data.name === this.data.name;
                }
            }.bind(this));
            this.items = items;
            if (items.length){
                items.each(function(item){
                    item.selectedItem = this;
                    item.setSelected();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.Selector.ProcessActivity.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,
    _getShowName: function(){
        return this.data.name;
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/applicationicon.png)");
    },
    loadSub: function(callback){
        if (!this.loaded){
            this.selector.designerAction.getProcess(this.data.id, function(data){
                var processData = data.data;
                var activityList = [].concat(
                    processData.manualList || [],
                    processData.conditionList || [],
                    processData.choiceList || [],
                    processData.parallelList || [],
                    processData.splitList || [],
                    processData.mergeList || [],
                    processData.embedList || [],
                    processData.publishList || [],
                    processData.invokeList || [],
                    processData.cancelList || [],
                    processData.delayList || [],
                    processData.messageList || [],
                    processData.serviceList || [],
                    processData.endList || []
                );
                activityList.unshift({
                    name: "草稿",
                    id: 'draft-'+this.data.id
                });
                activityList.forEach(function(data){
                    var d = {
                        processName: this.data.name,
                        id: data.id,
                        name: data.name,
                        alias: data.alias,
                        distinguishedName: data.name+'@'+data.id+'@A'
                    }
                    var item = this.selector._newItem(d, this.selector, this.children);
                    this.selector.items.push(item);
                }.bind(this));

                if (callback) callback();
            }.bind(this));
            this.loaded = true;

            // this.selector.action.listProcess(function(subJson){
            //     subJson.data.each(function(subData){
            //         subData.applicationName = this.data.name;
            //         subData.application = this.data.id;
            //         var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
            //         this.selector.items.push( category );
            //     }.bind(this));

            //     this.loaded = true;
            //     if (callback) callback();
            // }.bind(this), null, this.data.id);
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        return true;
    },
    check: function(){}
});
