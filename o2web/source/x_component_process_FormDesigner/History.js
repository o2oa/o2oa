// MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xApplication.process.FormDesigner.History = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        style: "default",
        maxLength: 20 //最大的item数，0表示不限制
    },
    initialize: function(form, container, options){
        this.setOptions(options);
        this.form = form;
        this.designer = form.designer;
        this.container = container;
        this.root = this.form.node;
        this.path = "../x_component_process_FormDesigner/$History/";
        this.iconPath = this.path+this.options.style+"/icon/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();
    },

    load: function(data) {
        //存储当前表面状态数组-上一步
        this.preArray = [];
        //存储当前表面状态数组-下一步
        this.nextArray = [];

        this.node = new Element("div", {"style":"font-size:12px;"}).inject(this.container);
        this.add({
            "operation": "open", //操作 create, copy, move, delete
            "type": "form", //property
            "moduleId": (this.form.json.name || "form" )+ " ["+this.form.json.mode+"]"
        });

        // var _self = this;
        // this.tooltips = new MWF.FCWHistory.Tooltips(
        //     this.form.designer.formNode,
        //     this.actionNode,
        //     this.form.designer,
        //     null,
        //     {
        //         onPostCreate: function () {
        //             _self.node.inject( this.contentNode );
        //             _self.add({
        //                 "operation": "open", //操作 create, copy, move, delete
        //                 "type": "form", //property
        //                 "moduleId": "form"
        //             });
        //
        //         }
        //     }
        // );
        // this.tooltips.load();
    },
    //获取domPath
    getPath: function (node) {
        var root = this.root;
        var path = [];
        var parent, childrens, nodeIndex;
        while (node && node !== root) {
            parent = node.parentElement;
            childrens = Array.from(parent.children);
            nodeIndex = childrens.indexOf(node);
            path.push(nodeIndex);
            node = parent;
        }
        return path.reverse();
    },
    add: function(log, module) {
        // var log = { //也有可能是对象数组
        //     "operation": "create", //操作 create, copy, move, delete, cut, paste
        //     "type": "module", //property 组件变化或属性变化
        //     "moduleType": "", //模块类型
        //     "moduleId": "", //模块id
        //      "fromList": [{  //原始数据
        //          "json": {},  //原始json
        //         "jsonObject": {}, //本module所包含的子json
        //         "html": "", //原始html
        //         "path": [], //原始dom path
        //      }],
        //      "toList": [{  //结束数据
        //          "json": {},  //最终json
        //          "jsonObject": {}, //本module所包含的子json
        //          "html": "", //最终html
        //          "path": [], //最终dom path
        //      }]
        // };
        var item;
        if( log.type === "module" ){
            switch (log.moduleType) {
                case "Table$Td":
                    item = new MWF.FCWHistory.ModuleTableTdItem(this, log);
                    break;
                case "Datatable$Title":
                case "Datatable$Data":
                    item = new MWF.FCWHistory.ModuleDatatableTdItem(this, log);
                    break;
                case "Tab$Page":
                    item = new MWF.FCWHistory.ModuleTabpageItem(this, log);
                    break;
                default:
                    item = new MWF.FCWHistory.ModuleItem(this, log);
            }
        }else{
            item = new MWF.FCWHistory.Item(this, log);
        }
        item.load();

        this.addItem(item);
    },
    checkProperty: function(log, module){
        // var log = {
        //     "type": "property",
        //     "force": false,
        //     "title": "",
        //     "moduleId": this.json.id,
        //     "moduleType": "",
        //     "notSetEditStyle": false
        //     "changeList": [
        //          {
        //              "name": name,
        //              "compareName": compareName, //对比名称，value对应的是name, 但是对比名称和name不一样时可以传这个
        //              "fromValue": oldValue,
        //              "toValue": this.json[name]
        //          }
        //      ]
        // };

        var isModified = false;
        for( var i=0; i<log.changeList.length; i++ ){
            var c = log.changeList[i];
            if( !c.fromValue && !c.toValue )continue;
            if( this.compareObjects( c.fromValue, c.toValue ) )continue;
            isModified = true;
            break;
        }
        if( !isModified )return;

        var flag = false;
        if( this.preArray.length ){
            var lastItem = this.preArray.getLast();
            var lastSubItem;
            if( lastItem.data.type === "property" ) {
                var change = log.changeList[0];
                if (lastItem.moduleIdList.contains(log.moduleId) || (change.name === "id" && lastItem.moduleIdList.contains(change.fromValue))) {
                    if (change.name === "id") lastItem.moduleIdList.push(change.toValue);
                    lastSubItem = lastItem.getLastSubItem();

                    var it;
                    while( this.nextArray.length ){
                        it = this.nextArray.pop();
                        it.destroy();
                    }

                    while( lastItem.nextArray && lastItem.nextArray.length ){
                        it = lastItem.nextArray.pop();
                        it.destroy();
                    }

                    var lastChangeList = lastSubItem.data.changeList;
                    if ( log.force ) {
                        lastItem.addSubItem(log);
                    }else if( change.compareName ){
                        if (lastChangeList.length === 1 && log.changeList.length === 1  && lastChangeList[0].compareName === change.compareName) {
                            lastChangeList[0].toValue = change.toValue;
                        }else{
                            lastItem.addSubItem(log);
                        }
                    }else{
                        if (lastChangeList.length === 1 && log.changeList.length === 1  && lastChangeList[0].name === change.name) {
                            // if( lastSubItem.data.fromValue === change.toValue ){ //回到最初的值了
                            //     if( lastItem.preArray.length === 1 ){
                            //         this.destroyItem( lastItem );
                            //     }else{
                            //         lastItem.destroySubItem(lastSubItem);
                            //     }
                            // }else{
                            lastChangeList[0].toValue = change.toValue;
                            // }
                        } else {
                            lastItem.addSubItem(log);
                        }
                    }

                    flag = true;
                }
            }
        }
        if( !flag ){
            var item = new MWF.FCWHistory.PropertySingleItem(this, log);
            item.load( module );
            this.addItem(item);
        }
    },
    checkMultiProperty: function(log, modules){
        var flag = false;
        if( this.preArray.length ){
            var lastItem = this.preArray.getLast();
            if( lastItem.data.type === "multiProperty" ) {
                var change = log.changeList[0];
                var lastChangeList = lastItem.data.changeList;
                if (lastChangeList[0].name === change.name) {
                    var moduleIdList = log.changeList.map(function (c) {
                        return c.module.json.id;
                    });
                    if( this.compareObjects( lastItem.moduleIdList, moduleIdList ) ){
                        var it;
                        while( this.nextArray.length ){
                            it = this.nextArray.pop();
                            it.destroy();
                        }

                        lastItem.data.changeList.each(function ( c, i ) {
                            c.toValue = log.changeList[i].toValue;
                        });

                        flag = true;
                    }
                }
            }
        }
        if( !flag ) {
            //console.log(log);
            var item = new MWF.FCWHistory.PropertyMultiItem(this, log);
            item.load(modules);
            this.addItem(item);
        }
    },
    addItem: function(item){
        var it;
        while( this.nextArray.length ){
            it = this.nextArray.pop();
            it.destroy();
        }

        //删除上一个property的已经undo的subItem
        if( this.preArray.length ){
            it = this.preArray.getLast();
            if( it.data.type === "property" ){
                while( it.nextArray.length ){
                    var subit = it.nextArray.pop();
                    subit.destroy();
                }
            }
        }

        this.preArray.push(item);

        //大于最大条目数
        if( this.options.maxLength > 0 && this.preArray.length > this.options.maxLength ){
            this.destroyItem(this.preArray[0]);
        }
    },
    destroy: function(){
        var it;
        while( this.nextArray.length ){
            it = this.nextArray.pop();
            it.destroy();
        }
        while( this.preArray.length ){
            it = this.preArray.pop();
            it.destroy();
        }
        this.node.destroy();
        MWF.release(this);
    },
    destroyItem: function(item){
        this.preArray.erase(item);
        item.destroy();
    },
    goto: function(item, notRedoItem){
        var it;
        if( item.status === "pre" ){
            it = this.preArray.getLast();
            while (it && item !== it){
                it.undo();
                this.nextArray.unshift(it); //插入到灰显数组前面
                this.preArray.pop(); //删除preArray最后一个
                it = this.preArray.getLast();
            }
            item.selectModule("undo");
        }else if( item.status === "next" ){
            if( this.preArray.length ){  //上一个property的subItem要redo一下
                it = this.preArray.getLast();
                if( it.data.type === "property" )it.redo();
            }
            it = this.nextArray[0];
            while (it && item !== it){
                it.redo();
                this.preArray.push(it); //插入到preArray数组最后
                this.nextArray.shift();
                it = this.nextArray[0];
            }
            item.redo( notRedoItem );
            this.preArray.push(item); //插入到preArray数组最后
            this.nextArray.shift();
            item.selectModule("redo");
        }
    },
    compareObjects: function(o, p, deep){
        if( o === p )return true;
        return JSON.stringify(o) === JSON.stringify(p);
    }
});

MWF.FCWHistory = MWF.xApplication.process.FormDesigner.History;

MWF.FCWHistory.Item = new Class({
    Implements: [Options, Events],
    options: {},
    initialize: function (history, log) {
        this.history = history;
        this.data = log;
        this.status = "pre";
        this.form = this.history.form;
        this.root = this.history.root;
    },
    load: function (module) {
        this.node = new Element("div", {
            styles : this._getItemStyle(),
            text: this._getText(),
            events: {
                click: this.comeHere.bind(this)
            }
        }).inject( this.history.node );
        this.node.setStyle("background-image", "url("+this.history.iconPath+ this.data.operation +".png)");
        this._afterLoad(module);
    },
    _afterLoad: function(){

    },
    _getItemStyle: function(){
        return this.history.css.itemNode;
    },
    _getText: function () {
        if( this.data.title )return this.data.title;
        var lp = MWF.xApplication.process.FormDesigner.LP.formAction;
        var type = this.getType();
        type = type ?  (" <" + type + "> ") : " ";
        return  ( lp[this.data.operation] || this.data.operation ) + type + this.data.moduleId;
    },
    getType: function(){
        var type = (this.data.type || "").toLowerCase();
        switch ( type ) {
            case "module": case "property":
                return (this.data.moduleType || "").capitalize();
            default:
                return type.capitalize();
        }
    },
    getTypeText: function(){
        var type = (this.data.type || "").toLowerCase();
        switch ( type ) {
            case "form": return this.history.designer.lp.propertyTemplate.form;
            case "page": return this.history.designer.lp.pageform;
            case "module":
                var moduleType = (this.data.moduleType || "").toLowerCase();
                var tool = this.history.designer.toolsData[moduleType] || this.history.designer.toolsData[moduleType.capitalize()];
                return (tool) ? tool.text : moduleType;
            default:
                return "";
        }
    },
    comeHere: function ( e, notRedoThis ) {
        this.history.goto(this, notRedoThis);
    },
    undo: function () { //回退
        this.status = "next";
        this.node.setStyles( this.history.css.itemNode_undo );
        this._undo();
    },
    redo: function(){ //重做
        this.status = "pre";
        this.node.setStyles(this.history.css.itemNode_redo);
        this._redo();
    },
    _undo: function(){
    },
    _redo: function(){
    },
    setBrushStyle: function( type ){
        var to = this.data.toList[0];
        var from = this.data.fromList[0];
        var dom = this.getDomByPath( to.path );
        if(dom){
            var module = dom.retrieve("module");
            if(module){
                var json = type === "undo" ? (from.json || {}) : (to.json || {});
                module.setBrushStyle( json );
            }
        }
    },
    destroy: function () {
        this.node.destroy();
        MWF.release(this);
    },
    unselectModule: function () {
        if(this.form.currentSelectedModule && this.form.currentSelectedModule.unSelected){
            this.form.currentSelectedModule.unSelected();
        }
        this.form.currentSelectedModule = null;

        if( this.form.selectedModules && this.form.selectedModules.length ){
            this.form.selectedModules = [];
        }
    },
    _selectModule: function (path) {
        var dom = this.getDomByPath(path);
        if(dom){
            var module = dom.retrieve("module");
            if(module)module.selected();
        }
    },
    deleteModuleList: function(){
        for( var i=this.data.toList.length-1; i>-1; i-- ){
            var to = this.data.toList[i];
            this._deleteModule( to.path );
        }
    },
    _deleteModule: function( path ){
        var module, dom = this.getDomByPath( path );
        if(dom)module = dom.retrieve("module");
        if(module)module.destroy();
    },
    loadModuleList: function(){
        for( var i=0; i<this.data.toList.length; i++ ) {
            var to = this.data.toList[i];
            this._loadModule(to.path, to.html, to.json, to.jsonObject);
        }
    },
    _loadModule: function( path, html, json, jsonObject ){
        var dom = this.injectHtmlByPath( path, html );
        this.addModulesJson(jsonObject);
        var parentModule = this.getParentModule(dom);
        var module = this.form.loadModule(json, dom, parentModule || this.form);
        module._setEditStyle_custom("id");
    },
    //根据路径顺序排序
    sortByPath: function( arr ){
        arr.sort(function (a, b) {
            var max = Math.max(a.path.length, b.path.length);
            for( var i=0; i< max; i++ ){
                if( a.path[i] && !b.path[i] && b.path[i]!==0 )return -1;
                if( b.path[i] && !a.path[i] && a.path[i]!==0 )return 1;
                if( a.path[i] !== b.path[i] )return a.path[i] - b.path[i];
            }
            return -1;
        });
    },
    //根据路径获取dom
    getDomByPath: function(path){
        var i, nodeIndex;
        var node = this.root;
        for( i=0; i<path.length; i++ ){
            nodeIndex = path[i];
            node = node.children[nodeIndex];
        }
        return node;
    },
    //插入到对应位置
    injectToByPath: function(path, dom){
        var i, nodeIndex;
        var node = this.root;
        for( i=0; i<path.length - 1; i++ ){
            nodeIndex = path[i];
            node = node.children[nodeIndex];
        }
        var last = path.getLast();
        if( last === 0 ){
            dom.inject( node, "top" );
        }else{
            var contains = false;
            for( i=0; i<last; i++ ){
                if( node.children[i] === dom ){ //如果位置包含当前dom
                    contains = true;
                    break;
                }
            }
            node = node.children[contains ? last : (last-1)];
            dom.inject(node, "after");
        }
        this.resetTreeNode( dom );
    },
    //插入HTML到对应位置
    injectHtmlByPath: function(path, html){
        var i, nodeIndex;
        var node = this.root;
        for( i=0; i<path.length - 1; i++ ){
            nodeIndex = path[i];
            node = node.children[nodeIndex];
        }
        var dom = new Element("div");
        var last = path.getLast();
        var parentNode = node;
        if( last === 0 ){
            dom.inject( node, "top" );
        }else{
            node = node.children[last-1];
            dom.inject(node, "after");
        }
        dom.outerHTML = html; //dom没了
        dom = parentNode.children[last];
        return dom;
    },
    resetTreeNode: function(node){
        var module = node.retrieve("module");
        if(module){
            module.parentContainer = this.getParentModule( node );
            module._resetTreeNode();
        }
    },
    addModulesJson: function( jsonObject ){
        if(jsonObject){
            for( var id in jsonObject ){
                this.form.json.moduleList[id] = jsonObject[id];
            }
        }
    },
    getParentModule: function (node) {
        var parent, parentNode = node.getParent();
        while( parentNode && !parent ){
            var mwftype = parentNode.get("mwftype");
            if( mwftype === "form") {
                parent = this.form;
            }else if( mwftype ){
                parent = parentNode.retrieve("module");
            }else{
                parentNode = parentNode.getParent();
            }
        }
        return parent;
    },
    getParentModuleByType: function( node, moduleType ){
        var parentNode = node;
        var module;
        while( parentNode && !module ){
            if( parentNode.get("mwftype") === moduleType )module = parentNode.retrieve("module");
            parentNode = parentNode.getParent();
        }
        return module;
    },
    selectModule: function(type){
        this.form.selected();
    },
    changeJsonDate: function(json, name, value){
        var key = name.split(".");
        var len = key.length-1;
        key.each(function(n, i){
            if (i<len) {
                if (!json.hasOwnProperty(n)) json[n] = {};
                json = json[n];
            }
        }.bind(this));
        if( typeOf(value) === "null" ){
            delete json[key[len]];
        }else{
            json[key[len]] = value;
        }
    }
});

MWF.FCWHistory.ModuleItem = new Class({
    Extends: MWF.FCWHistory.Item,
    _afterLoad: function () {
        if( this.data.toList && this.data.toList.length > 1 ){
            this.sortByPath(this.data.toList);
        }
        if( this.data.fromList && this.data.fromList.length > 1 ){
            this.sortByPath(this.data.fromList);
        }
    },
    _getText: function () {
        if( this.data.title )return this.data.title;
        var lp = MWF.xApplication.process.FormDesigner.LP.formAction;
        var type = this.getType();
        type = type ?  (" <" + type + "> ") : " ";
        return  ( lp[this.data.operation] || this.data.operation ) + type + this.data.moduleId;
    },
    _undo: function(){
        switch (this.data.operation) {
            case "create":
                this.deleteModuleList();
                break;
            case "copy":
                this.deleteModuleList();
                break;
            case "move":
                var to = this.data.toList[0];
                var from = this.data.fromList[0];
                var dom = this.getDomByPath( to.path );
                this.injectToByPath( from.path, dom );
                break;
            case "delete":
                this.loadModuleList();
                break;
            case "cut":
                this.loadModuleList();
                break;
            case "paste":
                this.deleteModuleList();
                break;
            case "styleBrush":
                this.setBrushStyle( "undo" );
                break;
        }
        this.unselectModule();
    },
    _redo: function(){
        switch (this.data.operation) {
            case "create":
                this.loadModuleList();
                break;
            case "copy":
                this.loadModuleList();
                break;
            case "move":
                var to = this.data.toList[0];
                var from = this.data.fromList[0];
                var dom = this.getDomByPath( from.path );
                this.injectToByPath( to.path, dom );
                break;
            case "delete":
                this.deleteModuleList();
                break;
            case "cut":
                this.deleteModuleList();
                break;
            case "paste":
                this.loadModuleList();
                break;
            case "styleBrush":
                this.setBrushStyle( "redo" );
                break;
        }
        this.unselectModule();
    },
    selectModule: function(type){
        if( ["delete","cut"].contains(this.data.operation) || this.data.toList.length > 1){
            this.form.selected();
        }else{
            this._selectModule(this.data.toList[0].path);
        }
    }
});

MWF.FCWHistory.ModuleTableTdItem = new Class({
    Extends: MWF.FCWHistory.ModuleItem,
    _getItemStyle: function(){
        return this.history.css.itemNode_table;
    },
    getTrPathList: function(){
        var trPathStrList = [];
        this.data.toList.each(function (log) {
            var path = Array.clone(log.path);
            path.pop();
            var str = path.join(",");
            if( trPathStrList.indexOf( str ) === -1 )trPathStrList.push(str);
        });
        return trPathStrList.map(function (str) {
            return str.split(",").map(function (path) {
                return path.toInt()
            });
        }.bind(this));
    },
    restoreRow: function(){
        this.getTrPathList().each(function (path) {
            this.injectHtmlByPath( path, "<tr></tr>" ); //创建tr
        }.bind(this));
        this.restoreTds();
    },
    restoreTds: function(){
        var log;
        for( var i=0; i<this.data.toList.length; i++ ){
            log = this.data.toList[i];
            this.restoreTd( log.path, log.html, log.json, log.jsonObject, i );
        }
    },
    restoreTd: function( path, html, json, jsonObject, i ){
        var tdNode = this.injectHtmlByPath( path, html );
        this.addModulesJson( jsonObject );

        var tableModule = this.getParentModuleByType(tdNode, "table");
        tableModule.loadExistedNodeTd(tdNode, json);
    },
    deleteRow: function(){
        this.deleteTds();
        this.getTrPathList().reverse().each(function (path) {
            var tr = this.getDomByPath( path );
            if(tr)tr.destroy();
        }.bind(this));
    },
    deleteTds: function(){
        var log, dom;
        for( var i=this.data.toList.length-1; i>-1; i-- ){
            log = this.data.toList[i];
            dom = this.getDomByPath( log.path );
            this.deleteTd( dom, i );
        }
    },
    deleteTd: function( dom, i ){
        var tableModule = this.getParentModuleByType(dom, "table");
        tableModule.deleteTdWithNode(dom);
    },
    _undo: function(){
        switch (this.data.operation) {
            case "insertRow": //td的操作，插入行
                this.deleteRow();
                break;
            case "insertCol": //td的操作，插入列
                this.deleteTds();
                break;
            case "deleteRow": //td的操作，删除行
                this.restoreRow();
                break;
            case "deleteCol": //td的操作，删除列
                this.restoreTds();
                break;
            case "splitCell": //拆分单元格
                this.deleteTds(); //先删除新建的单元格
                var fromLog = this.data.fromList[0]; //恢复原有的单元格
                this.restoreTd( fromLog.path, fromLog.html, fromLog.json, fromLog.jsonObject );
                break;
            case "mergeCell": //合并单元格
                //先删除新建的单元格
                var dom = this.getDomByPath( this.data.toList[0].path );
                this.deleteTd(dom);
                //恢复原来的单元格
                for( var i=0; i<this.data.fromList.length; i++ ){
                    var log = this.data.fromList[i];
                    this.restoreTd( log.path, log.html, log.json, log.jsonObject );
                }
                break;
            case "styleBrush":
                this.setBrushStyle( "undo" );
                break;
        }
        this.unselectModule();
    },
    _redo: function(){
        var dom, module, log;
        switch (this.data.operation) {
            case "insertRow": //td的操作，插入行
                this.restoreRow();
                break;
            case "insertCol": //td的操作，插入列
                this.restoreTds();
                break;
            case "deleteRow": //td的操作，插入列
                this.deleteRow();
                break;
            case "deleteCol": //td的操作，删除列
                this.deleteTds();
                break;
            case "splitCell": //拆分单元格
                dom = this.getDomByPath( this.data.fromList[0].path );
                this.deleteTd( dom );
                this.restoreTds();
                break;
            case "mergeCell": //合并单元格
                //删除原单元格
                for( var i=this.data.fromList.length-1; i>-1; i--){
                    dom = this.getDomByPath( this.data.fromList[i].path );
                    this.deleteTd(dom);
                }
                //恢复新的单元格
                var to = this.data.toList[0];
                this.restoreTd( to.path, to.html, to.json, to.jsonObject );
                break;
            case "styleBrush":
                this.setBrushStyle( "redo" );
                break;
        }
        this.unselectModule();
    },
    selectModule: function(type){
        if( ["mergeCell"].contains(this.data.operation) ){
            this._selectModule(this.data.toList[0].path);
        }else{
            this.selectTableModule();
        }
    },
    selectTableModule: function () {
        var log = this.data.toList[0] || this.data.fromList[0];
        var path = Array.clone(log.path);
        path.pop();
        path.pop();
        var dom = this.getDomByPath(path);
        var tableModule = this.getParentModuleByType(dom, "table");
        tableModule.selected();
    }
});

MWF.FCWHistory.ModuleDatatableTdItem = new Class({
    Extends: MWF.FCWHistory.ModuleTableTdItem,
    restoreTd: function (path, html, json, jsonObject, i) {
        var tdNode = this.injectHtmlByPath(path, html);
        this.addModulesJson(jsonObject);

        var tableModule = this.getParentModuleByType(tdNode, "datatable");
        if( tdNode.tagName === "TD"){
            tableModule.loadExistedNodeTd(tdNode, json);
        }else if( tdNode.tagName === "TH"){
            tableModule.loadExistedNodeTh(tdNode, json);
        }
    },
    deleteTd: function( dom, i ){
        var tableModule = this.getParentModuleByType(dom, "datatable");
        if( dom.tagName === "TD"){
            tableModule.deleteTdWithNode(dom);
        }else if( dom.tagName === "TH"){
            tableModule.deleteThWithNode(dom);
        }
    },
    _undo: function(){
        switch (this.data.operation) {
            case "insertCol": //td的操作，插入列
                this.deleteTds();
                break;
            case "deleteCol": //td的操作，删除列
                this.restoreTds();
                break;
            case "styleBrush":
                this.setBrushStyle( "undo" );
                break;
        }
        this.unselectModule();
    },
    _redo: function(){
        var dom, module, log;
        switch (this.data.operation) {
            case "insertCol": //td的操作，插入列
                this.restoreTds();
                break;
            case "deleteCol": //td的操作，删除列
                this.deleteTds();
                break;
            case "styleBrush":
                this.setBrushStyle( "redo" );
                break;
        }
        this.unselectModule();
    },
    selectModule: function(type){
        this.selectTableModule();
    },
    selectTableModule: function () {
        var log = this.data.toList[0] || this.data.fromList[0];
        var path = Array.clone(log.path);
        path.pop();
        path.pop();
        var dom = this.getDomByPath(path);
        var tableModule = this.getParentModuleByType(dom, "datatable");
        tableModule.selected();
    }
});

MWF.FCWHistory.ModuleTabpageItem = new Class({
    Extends: MWF.FCWHistory.ModuleItem,
    restoreTabage: function(){
        var to = this.data.toList[0];

        var contentNode = this.injectHtmlByPath( to.content.path, to.content.html );
        this.addModulesJson( to.content.jsonObject );

        var tabNode = this.injectHtmlByPath( to.path, to.html );
        this.addModulesJson( to.jsonObject );

        var tabModule = this.getParentModuleByType(tabNode, "tab");

        tabModule.loadExistedNodePage(tabNode, contentNode, to.json, to.content.json);
    },
    _undo: function(){
        var dom, module, to = this.data.toList[0];
        switch (this.data.operation) {
            case "add":
                dom = this.getDomByPath( to.path );
                if(dom)module = dom.retrieve("module");
                if(module)module._delete();
                break;
            case "copy":
                break;
            case "move":
                dom = this.getDomByPath( to.path );
                this.injectToByPath( this.data.fromList[0].path, dom );

                dom = this.getDomByPath( to.content.path );
                this.injectToByPath( this.data.fromList[0].content.path, dom );
                break;
            case "delete":
                this.restoreTabage();
                break;
            case "styleBrush":
                this.setBrushStyle( "undo" );
                break;
        }
        this.unselectModule();
    },
    _redo: function(){
        var dom, module, to = this.data.toList[0];
        switch (this.data.operation) {
            case "add":
                this.restoreTabage();
                break;
            case "copy":
                break;
            case "move":
                dom = this.getDomByPath( this.data.fromList[0].path );
                this.injectToByPath( to.path, dom );

                dom = this.getDomByPath( this.data.fromList[0].content.path );
                this.injectToByPath( to.content.path, dom );
                break;
            case "delete":
                dom = this.getDomByPath( to.path );
                if(dom)module = dom.retrieve("module");
                if(module)module._delete();
                break;
            case "styleBrush":
                this.setBrushStyle( "redo" );
                break;
        }
        this.unselectModule();
    },
    selectModule: function(type){
        if( ["delete"].contains(this.data.operation) || this.data.toList.length > 1){
            this.form.selected();
        }else{
            this._selectModule(this.data.toList[0].path);
        }
    }
});

MWF.FCWHistory.PropertySingleItem = new Class({
    Extends: MWF.FCWHistory.Item,
    load: function (module) {
        this.module = module;
        this.node = new Element("div", {
            styles : this._getItemStyle(),
            text: this._getText()
            // events: {
            //     click: this.comeHere.bind(this)
            // }
        }).inject( this.history.node );
        this.node.setStyle("background-image", "url("+this.history.iconPath+ "property.png)");
        this._afterLoad(module);
    },
    _getItemStyle: function(){
        return this.history.css.itemNode_property;
    },
    _afterLoad: function ( module ) {
        this.moduleIdList = [ this.data.moduleId ];
        this.nextArray = [];
        this.preArray = [];
        this.path = this.data.path || this.history.getPath( module.node );
        this.addSubItem( this.data );
    },
    getModule: function(){
        var module, dom = this.getDomByPath( this.path );
        if(dom)module = dom.retrieve("module");
        if( !module && this.module && this.form.moduleList.contains(this.module) ){
            module = this.module;
        }
        return module;
    },
    _getText: function () {
        if( this.data.title )return this.data.title;
        var lp = MWF.xApplication.process.FormDesigner.LP.formAction;
        var type = this.getType();
        type = type ?  (" <" + type + "> ") : " ";
        return lp.property + " " + type + this.data.moduleId;
    },
    destroy: function () {
        var si = this.preArray.pop();
        while (si){
            si.destroy();
            si = this.preArray.pop(); //删除preArray最后一个
        }

        si = this.nextArray.pop();
        while (si){
            si.destroy();
            si = this.nextArray.pop(); //删除nextArray最后一个
        }

        this.node.destroy();
        MWF.release(this);
    },
    undo: function () { //回退
        this.status = "next";
        this.node.setStyles( this.history.css.itemNode_property_undo );
        this._undo();
    },
    redo: function( notRedoItem ){ //重做
        this.status = "pre";
        this.node.setStyles(this.history.css.itemNode_property_redo);
        if( !notRedoItem )this._redo();
    },
    _undo: function () {
        // for( var i=this.subItemList.length-1; i > -1; i-- ){
        //     var subItem = this.subItemList.length[i];
        //     subItem.undo();
        // }
        var si = this.preArray.getLast();
        var flag = false;
        while (si){
            si.undo();
            flag = true;
            this.nextArray.unshift(si); //插入到灰显数组前面
            this.preArray.pop(); //删除preArray最后一个
            si = this.preArray.getLast();
        }
        if(flag){
            var module = this.getModule();
            if( module && module.property ){
                module.property.reset();
            }
        }
    },
    _redo: function () {
        // for( var i=0; i < this.subItemList.length; i++ ){
        //     var subItem = this.subItemList.length[i];
        //     subItem.redo();
        // }
        var si = this.nextArray[0];
        var flag = false;
        while (si){
            si.redo();
            flag = true;
            this.preArray.push(si); //插入到preArray数组最后
            this.nextArray.shift();
            si = this.nextArray[0];
        }
        if(flag){
            var module = this.getModule();
            if( module && module.property ){
                module.property.reset();
            }
        }
    },
    getLastSubItem: function(){
        return this.preArray.getLast();
    },
    destroySubItem: function(subItem){
        this.preArray.erase( subItem );
        subItem.destroy();
    },
    addSubItem: function ( data ) {
        var subItem = new MWF.FCWHistory.PropertySingleItem.SubItem(this, data);
        subItem.load();
        this._addSubItem(subItem);
    },
    _addSubItem: function(subItem){
        var si;
        while( this.nextArray.length ){
            si = this.nextArray.pop();
            si.destroy();
        }

        this.preArray.push(subItem);
    },
    getNext: function(subItem){
        var index = this.nextArray.indexOf(subItem);
        if( index < 0 )return null;
        if( index === this.nextArray.length - 1 )return null;
        return this.nextArray[ index + 1 ];
    },
    goto: function(subItem){
        var si, si_next, notSetEditStyle = false;
        if( subItem.status === "pre" ){
            si = this.preArray.getLast();
            while (si && subItem !== si){
                //如果下一个subitem的名称和现在一样，不设置SetEditStyle
                si_next = this.preArray.length>1 ? this.preArray[this.preArray.length-2] : null;
                notSetEditStyle = si_next && (subItem !== si_next) && ( si_next.data.changeList[0].name === si.data.changeList[0].name );

                si.undo( notSetEditStyle );
                this.nextArray.unshift(si); //插入到灰显数组前面
                this.preArray.pop(); //删除preArray最后一个
                si = this.preArray.getLast();
            }
        }else if( subItem.status === "next" ){
            var subItemNext = this.getNext(subItem);
            si = this.nextArray[0];
            while (si && subItem !== si){
                //如果下一个subitem的名称和现在一样，不设置SetEditStyle
                si_next = this.nextArray.length>1 ? this.nextArray[1] : null;
                notSetEditStyle = si_next && (subItemNext !== si_next) && ( si_next.data.changeList[0].name === si.data.changeList[0].name );

                si.redo( notSetEditStyle );
                this.preArray.push(si); //插入到preArray数组最后
                this.nextArray.shift();
                si = this.nextArray[0];
            }
            subItem.redo();
            this.preArray.push(subItem); //插入到preArray数组最后
            this.nextArray.shift();
        }
        var module = this.getModule();
        if( module && module.property ){
            module.property.reset();
        }
    },
    selectModule: function(){
        this._selectModule(this.path);
    }
});

MWF.FCWHistory.PropertySingleItem.SubItem = new Class({
    Extends: MWF.FCWHistory.Item,
    initialize: function (item, log) {
        this.parentItem = item;
        this.history = item.history;
        this.data = log;
        this.status = "pre";
        this.form = this.history.form;
        this.root = this.history.root;
    },
    load: function () {
        this.node = new Element("div", {
            styles: this.history.css.subItemNode,
            text: this.getText(),
            events: {
                click: this.comeHere.bind(this)
            }
        }).inject(this.history.node);
    },
    getText: function () {
        return this.data.changeList[0].compareName || this.data.changeList[0].name;
    },
    comeHere: function (e) {
        this.parentItem.comeHere( null, true );
        this.parentItem.goto( this );
    },
    undo: function ( notSetEditStyle ) { //回退
        this.status = "next";
        this.node.setStyles( this.history.css.subItemNode_undo );
        this._undo( notSetEditStyle );
    },
    redo: function( notSetEditStyle ){ //重做
        this.status = "pre";
        this.node.setStyles( this.history.css.subItemNode_redo );
        this._redo( notSetEditStyle );
    },
    _undo: function ( notSetEditStyle ) {
        //console.log( "_undo", this.data);
        var module = this.parentItem.getModule();
        if (module) {
            var json = module.json;
            for( var i=this.data.changeList.length-1; i>-1; i-- ){
                var change = this.data.changeList[i];
                if (change.name === "id") {
                    json.id = change.fromValue;
                    this.form.json.moduleList[change.fromValue] = json;
                    delete this.form.json.moduleList[change.toValue];
                }else{
                    // json[change.name] = change.fromValue;
                    this.changeJsonDate(json, change.name, change.fromValue);
                    module.setPropertiesOrStyles(change.name, change.toValue);
                    this.setScriptJsEditor(module, change.name, change.fromValue);
                }
                if(!notSetEditStyle && !this.data.notSetEditStyle){
                    //console.log("change.name")
                    module._setEditStyle(change.name, null, change.toValue);
                }
            }
        }
    },
    _redo: function (notSetEditStyle) {
        //console.log( "_redo", this.data);
        var module = this.parentItem.getModule();
        if (module) {
            var json = module.json;
            for( var i=0; i<this.data.changeList.length; i++ ){
                var change = this.data.changeList[i];
                if( change.name === "id" ){
                    json.id = change.toValue;
                    this.form.json.moduleList[ change.toValue ] = json;
                    delete this.form.json.moduleList[ change.fromValue ];
                }else{
                    // json[change.name] = change.toValue;
                    this.changeJsonDate(json, change.name, change.toValue);
                    module.setPropertiesOrStyles(change.name, change.fromValue);
                    this.setScriptJsEditor(module, change.name, change.toValue);
                }
                if(!notSetEditStyle && !this.data.notSetEditStyle){
                    module._setEditStyle(change.name, null, change.fromValue);
                }
            }
        }
    },
    setScriptJsEditor: function (module, name, value) {
        if( module )return;
        var jsEditor = module.getScriptJsEditor(name);
        if(jsEditor){
            var v = "";
            switch ( o2.typeOf(value)) {
                case "object":
                    if( o2.typeOf(value.code) === "string" ){
                        v = value.code;
                    }else if( o2.typeOf(value.actionScript) === "string" ){
                        v = value.actionScript;
                    }
                    break;
                case "string":
                    v = value;
                    break;
            }
            jsEditor.setValue( v, true );
        }
    }
});

MWF.FCWHistory.PropertyMultiItem = new Class({
    Extends: MWF.FCWHistory.Item,
    load: function () {
        this.node = new Element("div", {
            styles: this._getItemStyle(),
            text: this._getText(),
            events: {
                click: this.comeHere.bind(this)
            }
        }).inject(this.history.node);
        this.node.setStyle("background-image", "url(" + this.history.iconPath + "property.png)");

        this.modules = [];
        this.moduleIdList = [];
        this.data.changeList.each(function (log) {
            log.path = this.history.getPath(log.module.node);
            this.modules.push( log.module );
            this.moduleIdList.push( log.module.json.id );
        }.bind(this))
    },
    _getItemStyle: function () {
        return this.history.css.itemNode;
    },
    getModule: function ( log ) {
        var module, dom = this.getDomByPath(log.path);
        if (dom) module = dom.retrieve("module");
        if( !module && log.module ){
            module = log.module;
        }
        return module;
    },
    _getText: function () {
        if (this.data.title) return this.data.title;
        var lp = MWF.xApplication.process.FormDesigner.LP.formAction;
        return lp.batchModify + this.data.changeList[0].name + lp.property;
    },
    _undo: function () {
        //console.log( "_undo", this.data);
        for( var i=this.data.changeList.length-1; i>-1; i-- ){
            var change = this.data.changeList[i];
            var module = this.getModule( change );
            if( module ){
                var json = module.json;
                this.changeJsonDate(json, change.name, change.fromValue);
                module.setPropertiesOrStyles(change.name, change.toValue);
                module._setEditStyle(change.name, null, change.toValue);
                if( module.property )module.property.reset();
            }
        }
    },
    _redo: function () {
        //console.log( "_redo", this.data);
        for( var i=0; i<this.data.changeList.length; i++ ){
            var change = this.data.changeList[i];
            var module = this.getModule( change );
            if( module ) {
                var json = module.json;
                this.changeJsonDate(json, change.name, change.toValue);
                module.setPropertiesOrStyles(change.name, change.fromValue);
                module._setEditStyle(change.name, null, change.fromValue);
                if( module.property )module.property.reset();
            }
        }
    }
});

// MWF.FCWHistory.Tooltips = new Class({
//     Extends: MTooltips,
//     options : {
//         style: "design",
//         axis: "y",      //箭头在x轴还是y轴上展现
//         position : { //node 固定的位置
//             x : "right", //x轴上left center right,  auto 系统自动计算
//             y : "bottom" //y 轴上top middle bottom, auto 系统自动计算
//         },
//         event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
//         isAutoHide: false,
//         hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
//         displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
//         hasArrow : false,
//         hasCloseAction: true,
//         hasMask: false,
//         isParentOffset: true,
//         nodeStyles: {
//             padding: "0px",
//             "min-height": "100px",
//             "border-radius" : "0px"
//         }
//     },
//     _customNode : function( node, contentNode ){
//         new Element("div", {
//             "style": "padding-left: 10px; background-color: rgb(242, 242, 242); color: #333333; height: 30px; line-height: 30px; ",
//             "text": "历史记录"
//         }).inject(contentNode, "before");
//     },
// })
