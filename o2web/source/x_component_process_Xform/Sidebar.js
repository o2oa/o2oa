MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.require("MWF.widget.Tree", null, false);
/** @class Sidebar 侧边操作条。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var sidebar = this.form.get("fieldId"); //获取侧边操作条
 * //方法2
 * var sidebar = this.target; //在侧边操作条和操作本身的事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Sidebar = MWF.APPSidebar =  new Class(
    /** @lends MWF.xApplication.process.Xform.Sidebar# */
{
	Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        this.node.setStyles(this.form.css.sidebar);
        this.toolbarNode = this.node.getFirst("div");
        this.toolbarNode.empty();

        if (this.form.businessData.task){
            MWF.require("MWF.widget.Toolbar", function(){
                var toolbars = [];
                //this.form.businessData.task.routeNameList.each(function(route, i){
                //    if (!this.json.defaultTools) this.json.defaultTools = [];
                //    var o= {
                //        "type": "MWFToolBarButton",
                //        "img": "submit.png",
                //        "title": route,
                //        "action": "processWork:"+route,
                //        "text": route,
                //        "id": "action_processWork",
                //        "control": "allowProcessing",
                //        "condition": "",
                //        "read": false
                //    };
                //    toolbars.push(o);
                //}.bind(this));
                ( this.getRouteNameList() || [] ).each(function(route, i){
                    if (!this.json.defaultTools) this.json.defaultTools = [];
                    var o= {
                        "type": "MWFToolBarButton",
                        "img": "submit.png",
                        "title": route.displayName,
                        "action": "processWork:"+route.routeName,
                        "text": route.displayName,
                        "id": "action_processWork",
                        "control": "allowProcessing",
                        "condition": "",
                        "read": false
                    };
                    toolbars.push(o);
                }.bind(this));
                this.json.defaultTools = toolbars.concat(this.json.defaultTools);
                //this.json.defaultTools.unshift(o);

                /**
                 * @summary Toolbar组件，平台使用该组件生成操作条。
                 * @member {o2.widget.Toolbar}
                 * @example
                 *  //可以在脚本中获取该组件
                 * var toolbarWidget = this.form.get("fieldId").toolbarWidget; //获取组件对象
                 */
                this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);
                //alert(this.readonly)

                if (this.json.hideSystemTools){
                    if (this.json.tools.length){
                        this.setCustomToolbars(this.json.tools, this.toolbarNode);
                        this.toolbarWidget.load();
                    }else{
                        this.toolbarNode.setStyle("display", "none");
                    }
                }else{
                    if (this.json.defaultTools.length || this.json.tools.length){
                        if (this.json.defaultTools){
                            this.setToolbars(this.json.defaultTools, this.toolbarNode, this.readonly);
                            this.setCustomToolbars(this.json.tools, this.toolbarNode);
                            this.toolbarWidget.load();
                        }else{
                            MWF.getJSON(this.form.path+"toolbars.json", function(json){
                                this.setToolbars(json, this.toolbarNode, this.readonly, true);
                                this.setCustomToolbars(this.json.tools, this.toolbarNode);
                                this.toolbarWidget.load();
                            }.bind(this), false);
                        }
                    }else{
                        this.toolbarNode.setStyle("display", "none");
                    }
                }
                if (this.toolbarWidget.children.length){
                    //this.form.app.addEvent("resize", this.loadPosition.bind(this));
                    this.node.setStyle("display", "none");
                    window.setTimeout(this.loadPosition.bind(this), 500);
                    var _self = this;
                    this.form.app.content.getFirst().addEvent("scroll", function(e){
                        _self.loadPosition(this);
                    });
                    this.form.app.addEvent("resize", function(e){
                        _self.loadPosition(this);
                    });
                }else{
                    this.toolbarNode.setStyle("display", "none");
                }
            }.bind(this));
        }
    },
    loadPosition: function(){
        // this.node.setStyle("display", "block");
        // var parent = this.node.getParent();
        // while(parent && (!parent.get("MWFtype"))) parent = parent.getParent();
        //
        // var top = this.json.styles.top;
        // this.sideNode = parent || this.form.node;
        //
        // var size = this.form.app.content.getSize();
        // var scroll = this.form.app.content.getScroll();
        // var sideSize = this.sideNode.getSize();
        //
        // var y = (scroll.y+size.y/2)-sideSize.y/2;
        // var x = 5;
        //
        // var position = "centerRight";
        // var edge = "centerLeft";
        // if (!parent){
        //     edge = "centerRight";
        //     x = 5;
        // }
        //
        // if (this.json.barPosition=="left"){
        //     position = "centerLeft";
        //     edge = "centerRight";
        //     x = -5;
        //     if (!parent){
        //         edge = "centerLeft";
        //         x = 5;
        //     }
        // }
        //
        // this.node.position({
        //     "relativeTo": this.sideNode,
        //     "position": position,
        //     "edge": edge,
        //     "offset" : {"y": y, "x": x}
        // });
        // this.json.styles.top = top;
        // if (top) this.node.setStyle("top", top);

        this.node.setStyle("display", "block");
        var parent = this.node.getParent();
        while(parent && (!parent.get("MWFtype"))) parent = parent.getParent();
        this.sideNode = parent || this.form.node;
        var size = this.form.app.content.getSize();
        //var scroll = this.form.designer.designNode.getScroll();
        var sideSize = this.sideNode.getSize();
        var sidePosition = this.sideNode.getPosition(this.sideNode.getOffsetParent());
        var nodeSize = this.node.getSize();

        if (sideSize.y>size.y){
            var center = (size.y/2-nodeSize.y/2);
            if (center<sidePosition.y){
                this.node.setStyle("top", ""+sidePosition.y+"px");
            }else if (center>(sidePosition.y+sideSize.y)){
                var tmp = (sidePosition.y+sideSize.y)-nodeSize.y;
                this.node.setStyle("top", ""+tmp+"px");
            }else{
                this.node.setStyle("top", ""+center+"px");
            }
        }else{
            var top = sidePosition.y+sideSize.y/2-nodeSize.y/2;
            if (top>size.y){
                if (sidePosition.y+nodeSize.y>size.y){
                    this.node.setStyle("top", ""+sidePosition.y+"px");
                }else{
                    var tmp = size.y-nodeSize.y;
                    this.node.setStyle("top", ""+tmp+"px");
                }
            }else if(top<=0){
                if(sidePosition.y+sideSize.y<nodeSize.y){
                    var tmp = sidePosition.y+sideSize.y-nodeSize.y;
                    this.node.setStyle("top", ""+tmp+"px");
                }else{
                    this.node.setStyle("top", "45px");
                }
            }else{
                this.node.setStyle("top", ""+top+"px");
            }
        }


        var left = sideSize.x+sidePosition.x+5;
        this.node.setStyle("left", ""+left+"px");
        this.node.setStyle("position", "absolute");


        this.node.setStyles({"right": "auto", "bottom": "auto"});

        // this.json.styles = this.node.getStyles(["top", "left", "bottom", "right", "position"]);
        //
        // var p = this.sideNode.getPosition();
        // var s = this.sideNode.getSize();
        // this.sidePosition = {"p": p, "s": s};

    },
    setCustomToolbars: function(tools, node){
        var path = "../x_component_process_FormDesigner/Module/Actionbar/";
        tools.each(function(tool){
            var flag = true;
            if (this.readonly){
                flag = tool.readShow;
            }else{
                flag = tool.editShow;
            }
            if (flag){
                flag = true;
                if (tool.control){
                    flag = this.form.businessData.control[tool.control]
                }
                if (tool.condition){
                    var hideFlag = this.form.Macro.exec(tool.condition, this);
                    flag = !hideFlag;
                }
                if (flag){
                    var actionNode = new Element("div", {
                        "id": tool.id,
                        "MWFnodetype": tool.type,
                        //"MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                        "MWFButtonImage": path+(this.form.options.style||"default") +"/custom/"+tool.img,
                        "title": tool.title,
                        "MWFButtonAction": "runCustomAction",
                        "MWFButtonText": tool.text
                    }).inject(node);
                    if (tool.actionScript){
                        actionNode.store("script", tool.actionScript);
                    }
                    if (tool.sub){
                        var subNode = node.getLast();
                        this.setCustomToolbars(tool.sub, subNode);
                    }
                }
            }
        }.bind(this));
    },
    setToolbars: function(tools, node, readonly, noCondition){
        var path = "../x_component_process_FormDesigner/Module/Actionbar/";
        tools.each(function(tool){
            var flag = true;
            if (tool.control){
                flag = this.form.businessData.control[tool.control]
            }
            if (!noCondition) if (tool.condition){
                var hideFlag = this.form.Macro.exec(tool.condition, this);
                flag = !hideFlag;
            }
            if (tool.id == "action_processWork"){
                if (!this.form.businessData.task){
                    flag = false;
                }
            }
            if (readonly) if (!tool.read) flag = false;
            if (flag){
                var actionNode = new Element("div", {
                    "id": tool.id,
                    "MWFnodetype": tool.type,
                    //"MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                    "MWFButtonImage": path+(this.form.options.style||"default") +"/tools/default/"+tool.img,
                    "title": tool.title,
                    "MWFButtonAction": tool.action,
                    "MWFButtonText": tool.text
                }).inject(node);
                if (tool.sub){
                    var subNode = node.getLast();
                    this.setToolbars(tool.sub, subNode, readonly, noCondition);
                }
            }
        }.bind(this));
    },
    getRouteNameList: function( routeList ){
        var _self = this;

        var list = [];
        if( !routeList )routeList = this.getRouteDataList();
        routeList.each(function(route, i){
            if( route.hiddenScriptText && this.form && this.form.Macro ){
                if( this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true" )return;
            }
            var routeName = route.name;
            if( route.displayNameScriptText && this.form && this.form.Macro ){
                routeName = this.form.Macro.exec(route.displayNameScriptText, this);
            }

            list.push({
                "routeId" : route.id,
                "displayName" : routeName,
                "routeName" : route.name
            })
        }.bind(this));
        return list;
    },
    getRouteDataList : function(){
        if( !this.routeDataList ){
            o2.Actions.get("x_processplatform_assemble_surface").listRoute( {"valueList":this.form.businessData.task.routeList} , function( json ){
                json.data.each( function(d){
                    d.selectConfigList = JSON.parse( d.selectConfig || "[]" );
                }.bind(this));
                this.routeDataList = json.data;
            }.bind(this), null, false );
        }
        return this.routeDataList;
    },
    getRouteData : function( routeId ){
        var routeList = this.getRouteDataList();
        for( var i=0; i<routeList.length; i++ ){
            if( routeList[i].id === routeId ){
                return routeList[i];
            }
        }
    },
    runCustomAction: function(bt){
        var script = bt.node.retrieve("script");
        this.form.Macro.exec(script, this);
    },
    saveWork: function(){
        this.form.saveWork();
    },
    closeWork: function(){
        this.form.closeWork();
    },
    processWork: function(route){
        opinion = this.form.getOpinion();
        if (!this.form.formCustomValidation()){
            this.form.app.content.unmask();
            //    if (callback) callback();
            return false;
        }
        this.form.submitWork(route, opinion.opinion, opinion.medias)

        // var opinionField = this.json.opinion || "opinion";
        // var data = this.form.getData();
        // var opinion = data[opinionField];
        // data[opinionField] = "";
        // this.form.submitWork(route, opinion, null, null, data);
    },
    resetWork: function(){
        this.form.resetWork();
    },
    retractWork: function(e, ev){
        this.form.retractWork(e, ev);
    },
    rerouteWork: function(e, ev){
        this.form.rerouteWork(e, ev);
    },
    deleteWork: function(){
        this.form.deleteWork();
    },
    printWork: function(){
        this.form.printWork();
    }
}); 
