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

                            var wrapDiv = "yes";
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

                    var td = dlg.node.getElementById("MWFWidgetSelectTd");
                    this.form.designer.actions.listWidget(this.form.designer.application.id, function(json){
                        var select = this.widgetSelect = new Element("select").inject(td);
                        var option = new Element("option", {"text": "none"}).inject(select);
                        json.data.each(function(widget){
                            var option = new Element("option", {
                                "text": widget.name,
                                "value": widget.id
                            }).inject(select);
                        }.bind(this));
                    }.bind(this));
                }.bind(this)
            });

            dlg.show();
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
        MWF.Actions.get("x_portal_assemble_designer").getWidget(widgetid, function(json){

            var parentModule = this.parentContainer || this.inContainer || this.onDragModule;
            this.containerModule = this.page.createModuleImmediately("Div", parentModule, relativeNode || this.copyNode, position || "before", false, false);

            this.node = this.containerModule.node;

            var data = this.getWidgetData( json.data );

            var moduleList = this.page.json.moduleList;


            var tmpNode = new Element("div").inject( this.page.container );
            tmpNode.set("html", data.html);
            var html = tmpNode.getFirst().get("html");
            tmpNode.destroy();

            this.node.set("html", html );

            Object.each(data.json.moduleList, function (moduleJson) {
                var oid = moduleJson.id;
                var id = moduleJson.id;
                var idx = 1;
                while (this.page.json.moduleList[id]) {
                    id = oid + "_" + idx;
                    idx++;
                }

                if (oid != id) {
                    moduleJson.id = id;
                    var moduleNode = this.node.getElementById(oid);
                    if (moduleNode) moduleNode.set("id", id);
                }
                this.page.json.moduleList[moduleJson.id] = moduleJson;
            }.bind(this));

            if( wrapDiv === "no" ){
                this.node.getChildren().each( function (el) {
                    if( el.get("MWFType") && el.get("id")){
                        var id = el.get("id");
                        el.inject( relativeNode || this.copyNode, position || "before" );
                    }
                }.bind(this));
                this.page.parseModules( parentModule, parentModule.node);
                //this.containerModule.delete();
                this.page.selected();
                this.containerModule.destroy();
            }else{
                this.page.parseModules(this.containerModule, this.node);
            }

            //var copyModuleNode = this.node.getFirst();
            //while (copyModuleNode) {
            //    //copyModuleNode.inject(injectNode, where);
            //    var copyModuleJson = this.page.getDomjson(copyModuleNode);
            //    var module = this.page.loadModule(copyModuleJson, copyModuleNode, this.containerModule);
            //    module._setEditStyle_custom("id");
            //    module.selected();
            //    //loadModule: function(json, dom, parent)
            //
            //    copyModuleNode = copyModuleNode.getNext();
            //}

            this._clearDragComplete();

            //this.setCustomStyles();
            //this.node.setProperties(this.json.properties);
            //this.setNodeEvents();

            //if (this.options.mode==="Mobile"){
            //    if (oldStyleValue) this._setEditStyle("pageStyleType", null, oldStyleValue);
            //}
        }.bind(this), null, false);
    },
    getWidgetData: function(data){
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
        return d;
    }

});
