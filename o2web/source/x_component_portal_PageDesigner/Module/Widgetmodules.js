MWF.xApplication.portal.PageDesigner.Module.Widgetmodules = MWF.PCWidgetmodules = new Class({
    Extends: MWF.FC$Module,
    Implements: [Options, Events],
    options: {
        "style": "default"
        //"propertyPath": "../x_component_portal_PageDesigner/Module/Radio/radio.html"
    },

    initialize: function(form, options){
        this.setOptions(options);

        this.path = "../x_component_portal_PageDesigner/Module/Widgetmodules/";
        this.cssPath = "../x_component_portal_PageDesigner/Module/Widgetmodules/"+this.options.style+"/css.wcss";

        this._loadCss();
        //this.moduleType = "element";
        //this.moduleName = "radio";

        this.form = form;
        this.page = form;
        this.container = null;
        this.containerNode = null;
    },

    _createMoveNode: function(){
        this.moveNode = new Element("div", {
            "styles": this.css.moduleNodeMove,
            "events": {
                "selectstart": function(){
                    return false;
                }
            }
        }).inject(this.form.container);
    },
    _createNode: function(relativeNode, position){
        var module = this;
        var url = this.path+"widgetSelect.html";
        MWF.require("MWF.widget.Dialog", function(){
            var size = $(document.body).getSize();
            var x = size.x/2-180;
            var y = size.y/2-100;

            var dlg = new MWF.DL({
                "title": "Insert",
                "style": "property",
                "top": y,
                "left": x-40,
                "fromTop":size.y/2-65,
                "fromLeft": size.x/2,
                "width": 360,
                "height": 200,
                "url": url,
                "lp": MWF.xApplication.process.FormDesigner.LP.propertyTemplate,
                "buttonList": [
                    {
                        "text": MWF.APPFD.LP.button.ok,
                        "action": function(){
                            var widgetid = module.widgetSelect.options[module.widgetSelect.selectedIndex].value;
                            if( !widgetid || widgetid==="none" ){
                                module.page.designer.notice(module.page.designer.lp.notice.selectWidget, "error");
                                return;
                            }

                            var wrapDiv = "no";
                            dlg.node.getElements(".wrapDiv").each( function (el) {
                                if( el.get("checked") )wrapDiv = el.get("value");
                            });

                            module.appendWidgetModules( widgetid, relativeNode, position, wrapDiv );
                            this.close();
                        }
                    },
                    {
                        "text": MWF.APPFD.LP.button.cancel,
                        "action": function(){
                            module._clearDragComplete();
                            this.close();
                        }
                    }
                ],
                "onPostShow": function(){
                    o2.Actions.load("x_portal_assemble_designer").PortalAction.list(function (json) {
                        var td = dlg.node.getElementById("MWFPortalSelectTd");
                        var select = module.appSelect = new Element("select").inject(td);
                        var option = new Element("option", {"text": ""}).inject(select);
                        json.data.each(function(app){
                            var option = new Element("option", {
                                "text": app.name,
                                "value": app.id
                            }).inject(select);
                        }.bind(this));
                        select.addEvent("change", function(){
                            var appid = module.appSelect.options[module.appSelect.selectedIndex].value;
                            module.setWidgetSelectOptions( appid );
                        })
                    });

                    var td = dlg.node.getElementById("MWFWidgetSelectTd");
                    module.widgetSelect = new Element("select").inject(td);
                    module.setWidgetSelectOptions();
                }.bind(this)
            });

            dlg.show();
        }.bind(this));
    },
    setWidgetSelectOptions: function( aplication ){
        var select = this.widgetSelect;
        select.empty();
        this.form.designer.actions.listWidget( aplication || this.form.designer.application.id, function(json){
            var option = new Element("option", {"text": "none"}).inject(select);
            json.data.each(function(widget){
                var option = new Element("option", {
                    "text": widget.name,
                    "value": widget.id
                }).inject(select);
            }.bind(this));
        }.bind(this));
    },
    _dragComplete: function(relativeNode, position){
        if (!this.node){
            this._createNode(relativeNode, position );
        }else{
            this._clearDragComplete();
        }
    },
    //_dragMoveComplete: function( relativeNode, position ){
    //    //this.setStyleTemplate();
    //
    //    if( this.injectNoticeNode )this.injectNoticeNode.destroy();
    //    //var overflow = this.moveNode.retrieve("overflow");
    //    //if( overflow ){
    //    //    this.moveNode.setStyle("overflow",overflow);
    //    //    this.moveNode.eliminate("overflow");
    //    //}
    //
    //    //if (!this.node){
    //    //    this._createNode();
    //    //}
    //    //this._resetTreeNode();
    //
    //    //if( relativeNode && position ){
    //    //    this.node.inject( relativeNode, position );
    //    //}else{
    //    //    this.node.inject(this.copyNode, "before");
    //    //}
    //
    //    //this._initModule();
    //
    //    //var thisDisplay = this.node.retrieve("thisDisplay");
    //    //if (thisDisplay){
    //    //    this.node.setStyle("display", thisDisplay);
    //    //}
    //
    //    this._clearDragComplete();
    //
    //    //this.form.json.moduleList[this.json.id] = this.json;
    //    //if (this.form.scriptDesigner) this.form.scriptDesigner.createModuleScript(this.json);
    //
    //    //this.selected();
    //},
    _clearDragComplete : function(){
        if( this.injectNoticeNode )this.injectNoticeNode.destroy();
        if (this.copyNode) this.copyNode.destroy();
        if (this.moveNode) this.moveNode.destroy();
        this.moveNode = null;
        this.copyNode = null;
        this.nextModule = null;
        this.form.moveModule = null;
        delete this;
    },
    appendWidgetModules: function( widgetid, relativeNode, position, wrapDiv ){
        this.getWidgetData( widgetid ).then(function(data) {

            if( wrapDiv === "yes" ){
                this.appendWithWrap(data, relativeNode, position);
            }else{
                this.appendWithoutWrap(data, relativeNode, position);
            }

            this.page.selected();
            this._clearDragComplete();
        }.bind(this));
    },
    appendWithWrap(data, relativeNode, position){
        var parentModule = this.parentContainer || this.inContainer || this.onDragModule;
        var wrapModule = this.page.createModuleImmediately("Div", parentModule,
            relativeNode || this.copyNode, position || "before", true, false
        );
        wrapModule._setEditStyle_custom("id");
        // wrapModule.selected();

        var node = wrapModule.node;
        node.set('html', data.html);

        this.checkId(data, node);

        var moduleList = [];
        var copyModuleNode = node.getFirst();
        while (copyModuleNode) {
            var copyModuleJson = this.page.getDomjson(copyModuleNode);
            var module = this.page.loadModule(copyModuleJson, copyModuleNode, wrapModule);
            module._setEditStyle_custom("id");
            // module.selected();

            moduleList.push( module );

            copyModuleNode = copyModuleNode.getNext();
        }

        if( this.page.history ){
            wrapModule.addHistoryLog("create", [wrapModule].concat(moduleList));
        }
    },
    appendWithoutWrap(data, relativeNode, position){
        var parentModule = this.parentContainer || this.inContainer || this.onDragModule;
        var node = new Element("div", {
            "styles": {"display": "none"},
            "html": data.html
        }).inject(this.page.designer.content);

        this.checkId(data, node);

        // var injectNode = this.page.node;
        // var where = "bottom";
        // var parent = this.page;
        // if (this.page.currentSelectedModule) {
        //     var toModule = this.page.currentSelectedModule;
        //     injectNode = toModule.node;
        //     parent = toModule;
        //
        //     if (toModule.moduleType != "container" && toModule.moduleType != "page") {
        //         where = "after";
        //         parent = toModule.parentContainer;
        //     }
        // }

        var moduleList = [];
        var copyModuleNode = node.getFirst();
        while (copyModuleNode) {
            copyModuleNode.inject( relativeNode || this.copyNode, position || "before" );
            var copyModuleJson = this.page.getDomjson(copyModuleNode);
            var module = this.page.loadModule(copyModuleJson, copyModuleNode, parentModule);
            module._setEditStyle_custom("id");
            // module.selected();

            moduleList.push( module );

            copyModuleNode = node.getFirst();
        }
        node.destroy();
        node = null;

        if( this.page.history && moduleList.length){
            moduleList[0].addHistoryLog("create", moduleList);
        }
    },
    checkId(data, node){
        var datatemplateJsons = [];
        var idMap = {};
        var originalIds = {};
        Object.each(data.json.moduleList, function (moduleJson) {
            originalIds[moduleJson.id] = true;
        });

        Object.each(data.json.moduleList, function (moduleJson) {
            var oid = moduleJson.id;
            var id = moduleJson.id;
            var idx = 1;
            while (this.page.json.moduleList[id] || (originalIds[id] && idx > 1)) {
                id = oid + "_" + idx;
                idx++;
            }
            if (oid !== id) {
                idMap[oid] = id;
                moduleJson.id = id;
                var moduleNode = node.getElementById(oid);
                if (moduleNode) moduleNode.set("id", id);
            }
            if (moduleJson.type === "Datatemplate") datatemplateJsons.push(moduleJson);
            this.page.json.moduleList[moduleJson.id] = moduleJson;
        }.bind(this));

        datatemplateJsons.each(function (json) {
            this.page.designer.checkDatatemplateRelativeId(json, idMap);
        }.bind(this));
    },
    getWidgetData: function(widgetid){
        return MWF.Actions.get("x_portal_assemble_designer").getWidget(widgetid, function(json){
            var data = json.data;
            var widgetDataStr = null;
            if (this.page.options.mode !== "Mobile"){
                widgetDataStr = data.data;
            }else{
                widgetDataStr = data.mobileData;
            }
            var d = null;
            if (widgetDataStr){
                d = JSON.decode(MWF.decodeJsonString(widgetDataStr));
                d.updateTime = data.updateTime;
            }

            var tmpNode = new Element("div").inject( this.page.container );
            tmpNode.set("html", d.html);
            d.html = tmpNode.getFirst().get("html");
            tmpNode.destroy();

            return d;
        }.bind(this));
    }

});
