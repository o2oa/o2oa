MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xDesktop.requireApp("process.Work", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("process.Xform", "Org", null, false);
MWF.xApplication.process.Work.Processor = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "mediaNode": null,
        "opinion": "",
        "tabletWidth" : 0,
        "tabletHeight" : 0,
        "orgHeight" : 276,
        "maxOrgCountPerline" : 2
	},
	
	initialize: function(node, task, options, form){
		this.setOptions(options);
		
		this.path = "/x_component_process_Work/$Processor/";
		this.cssPath = "/x_component_process_Work/$Processor/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.task = task;
		this.node = $(node);
        this.selectedRoute = null;

        this.form = form;

        this.load();
	},
    load: function(){
        this.routeSelectorTile = new Element("div", {"styles": this.css.routeSelectorTile, "text": MWF.xApplication.process.Work.LP.selectRoute}).inject(this.node);
        this.routeSelectorArea = new Element("div", {"styles": this.css.routeSelectorArea}).inject(this.node);

        this.routeOpinionTile = new Element("div", {"styles": this.css.routeOpinionTile, "text": MWF.xApplication.process.Work.LP.inputOpinion}).inject(this.node);
        this.routeOpinionArea = new Element("div", {"styles": this.css.routeOpinionArea}).inject(this.node);
        this.setOpinion();

        this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.node);
        this.orgsTile = new Element("div", {"styles": this.css.orgsTitle, "text": MWF.xApplication.process.Work.LP.selectPerson}).inject(this.orgsArea);

        this.buttonsArea = new Element("div", {"styles": this.css.buttonsArea}).inject(this.node);
        this.setButtons();

        this.setRouteList();

        this.fireEvent("postLoad");
    },

    setRouteList: function(){
        var _self = this;
        //this.task.routeNameList = ["送审核", "送办理", "送公司领导阅"];
        var routeList = this.getRouteDataList();
        //this.task.routeNameList.each(function(route, i){
        routeList.each(function(route, i){
            if( route.hiddenScriptText ){
                if( this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true" )return;
            }
            var routeName = route.name;
            if( route.displayNameScriptText ){
                routeName = this.form.Macro.exec(route.displayNameScriptText, this);
            }
            var routeNode = new Element("div", {"styles": this.css.routeNode}).inject(this.routeSelectorArea);
            var routeIconNode = new Element("div", {"styles": this.css.routeIconNode}).inject(routeNode);
            var routeTextNode = new Element("div", {"styles": this.css.routeTextNode, "text": routeName}).inject(routeNode);
            routeNode.store( "route", this.task.routeList[i] );
            routeNode.store( "routeName", route.name );

            routeNode.addEvents({
                "mouseover": function(e){_self.overRoute(this);},
                "mouseout": function(e){_self.outRoute(this);},
                "click": function(e){_self.selectRoute(this);}
            });

            if (routeList.length==1){
                this.selectRoute(routeNode);
            }else{
                this.setSize(0);
            }

        }.bind(this));
    },
    overRoute: function(node){
        if (this.selectedRoute){
            if (this.selectedRoute.get("text") != node.get("text")){
                node.setStyle("background-color", "#f7e1d0");
            }
        }else{
            node.setStyle("background-color", "#f7e1d0");
        }
    },
    outRoute: function(node){
        if (this.selectedRoute){
            if (this.selectedRoute.get("text") != node.get("text")){
                node.setStyles(this.css.routeNode);
            }
        }else{
            node.setStyles(this.css.routeNode);
        }
    },
    selectRoute: function(node){
        if (this.selectedRoute){
            if (this.selectedRoute.get("text") != node.get("text")){
                this.selectedRoute.setStyles(this.css.routeNode);
                this.selectedRoute.getFirst().setStyles(this.css.routeIconNode);
                this.selectedRoute.getLast().setStyles(this.css.routeTextNode);

                this.selectedRoute = node;
                node.setStyle("background-color", "#da7429");
                node.getFirst().setStyle("background-image", "url("+"/x_component_process_Work/$Processor/default/checked.png)");
                node.getLast().setStyle("color", "#FFF");

            }else{
                this.selectedRoute.setStyles(this.css.routeNode);
                this.selectedRoute.getFirst().setStyles(this.css.routeIconNode);
                this.selectedRoute.getLast().setStyles(this.css.routeTextNode);

                this.selectedRoute = null;
            }
        }else{
            this.selectedRoute = node;
            node.setStyle("background-color", "#da7429");
            node.getFirst().setStyle("background-image", "url("+"/x_component_process_Work/$Processor/default/checked.png)");
            node.getLast().setStyle("color", "#FFF");
        }
        this.routeSelectorArea.setStyle("background-color", "#FFF");
        this.loadOrgs( this.selectedRoute ? this.selectedRoute.retrieve("route") : "" );
    },

    setOpinion: function(){
        this.selectIdeaNode = new Element("div", {"styles": this.css.selectIdeaNode}).inject(this.routeOpinionArea);
        this.selectIdeaScrollNode = new Element("div", {"styles": this.css.selectIdeaScrollNode}).inject(this.selectIdeaNode);
        this.selectIdeaAreaNode = new Element("div", {"styles": {
            "overflow": "hidden"
        }}).inject(this.selectIdeaScrollNode);

        this.inputOpinionNode = new Element("div", {"styles": this.css.inputOpinionNode}).inject(this.routeOpinionArea);
        this.inputTextarea = new Element("textarea", {"styles": this.css.inputTextarea, "value": this.options.opinion || MWF.xApplication.process.Work.LP.inputText}).inject(this.inputOpinionNode);
        this.inputTextarea.addEvents({
            "focus": function(){if (this.get("value")==MWF.xApplication.process.Work.LP.inputText) this.set("value", "");},
            "blur": function(){if (!this.get("value")) this.set("value", MWF.xApplication.process.Work.LP.inputText);},
            "keydown": function(){this.inputTextarea.setStyles( this.inputTextareaStyle || this.css.inputTextarea);}.bind(this)
        });

        this.mediaActionArea = new Element("div", {"styles": this.css.inputOpinionMediaActionArea}).inject(this.inputOpinionNode);
        this.handwritingAction = new Element("div", {"styles": this.css.inputOpinionHandwritingAction, "text": MWF.xApplication.process.Work.LP.handwriting}).inject(this.mediaActionArea);
        this.handwritingAction.addEvent("click", function(){
            this.handwriting();
        }.bind(this));

        // if (navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia){
        //     this.audioRecordAction = new Element("div", {"styles": this.css.inputOpinionAudioRecordAction, "text": MWF.xApplication.process.Work.LP.audioRecord}).inject(this.mediaActionArea);
        //     this.audioRecordAction.addEvent("click", function(){
        //         this.audioRecord();
        //     }.bind(this));
        // }

        if (layout.mobile){
            this.selectIdeaNode.inject(this.routeOpinionArea, "after");
        }

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.selectIdeaScrollNode, {
                "style":"small", "where": "before", "distance": 30, "friction": 4, "indent": false,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        MWF.require("MWF.widget.UUID", function(){
            MWF.UD.getDataJson("idea", function(json){
                if (json){
                    if (json.ideas){
                        this.setIdeaList(json.ideas);
                    }
                }else{
                    MWF.UD.getPublicData("idea", function(pjson){
                        if (pjson){
                            if (pjson.ideas){
                                this.setIdeaList(pjson.ideas);
                            }
                        }
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },
    audioRecord: function(){
        if (!this.audioRecordNode) this.createAudioRecord();
        this.audioRecordNode.show();
        this.audioRecordNode.position({
            "relativeTo": this.options.mediaNode || this.node,
            "position": "center",
            "edge": "center"
        });

        MWF.require("MWF.widget.AudioRecorder", function () {
            this.audioRecorder = new MWF.widget.AudioRecorder(this.audioRecordNode, {
                "onSave" : function( blobFile ){
                    this.soundFile = blobFile;
                    this.audioRecordNode.hide();
                    // this.page.get("div_image").node.set("src",base64Image);
                }.bind(this),
                "onCancel": function(){
                    this.soundFile = null;
                    this.audioRecordNode.hide();
                }.bind(this)
            }, null );
        }.bind(this));
    },
    createAudioRecord: function(){
        this.audioRecordNode = new Element("div", {"styles": this.css.handwritingNode}).inject(this.node, "after");
        var size = (this.options.mediaNode || this.node).getSize();
        // var y = Math.max(size.y, 320);
        // var x = Math.max(size.x, 400);

        // for (k in this.node.style){
        //     if (this.node.style[k]) this.audioRecordNode.style[k] = this.node.style[k];
        // }
        var zidx = this.node.getStyle("z-index");
        this.audioRecordNode.setStyles({
            "height": ""+size.y+"px",
            "width": ""+size.x+"px",
            "z-index": zidx+1
        });
    },

    handwriting: function(){
        if (!this.handwritingNode) this.createHandwriting();
        if(this.handwritingNodeMask)this.handwritingNodeMask.show();
        this.handwritingNode.show();
        this.handwritingNode.position({
            "relativeTo": this.options.mediaNode || this.node,
            "position": "center",
            "edge": "center"
        });
    },
    createHandwriting: function(){
        this.handwritingNodeMask = new Element("div", {"styles": this.css.handwritingMask}).inject(this.node);

        this.handwritingNode = new Element("div", {"styles": this.css.handwritingNode}).inject(this.node, "after");
        //var size = (this.options.mediaNode || this.node).getSize();
        //var y = size.y;
        //var x = size.x;
        //兼容以前的默认高宽
        var x = 600;
        var y = 320;
        if (!layout.mobile){

            x = Math.max( this.options.tabletWidth || x , 500);
            y = Math.max(this.options.tabletHeight ? (parseInt(this.options.tabletHeight) + 110) : y, 320);

            //y = Math.max(size.y, 320);
            //x = Math.max(size.x, 480);
        }
        // for (k in this.node.style){
        //     if (this.node.style[k]) this.handwritingNode.style[k] = this.node.style[k];
        // }
        var zidx = this.node.getStyle("z-index");
        this.handwritingNode.setStyles({
            "height": ""+y+"px",
            "width": ""+x+"px",
            "z-index": zidx+1
        });
        if( layout.mobile ){
            this.handwritingNode.addEvent('touchmove' , function(e){
                e.preventDefault();
            })
        }
        this.handwritingNode.position({
            "relativeTo": this.options.mediaNode || this.node,
            "position": "center",
            "edge": "center"
        });
        this.handwritingAreaNode = new Element("div", {"styles": this.css.handwritingAreaNode}).inject(this.handwritingNode);
        this.handwritingActionNode = new Element("div", {"styles": this.css.handwritingActionNode, "text": MWF.xApplication.process.Work.LP.saveWrite}).inject(this.handwritingNode);
        var h = this.handwritingActionNode.getSize().y+this.handwritingActionNode.getStyle("margin-top").toInt()+this.handwritingActionNode.getStyle("margin-bottom").toInt();
        h = y - h;
        this.handwritingAreaNode.setStyle("height", ""+h+"px");

        MWF.require("MWF.widget.Tablet", function () {
            this.tablet = new MWF.widget.Tablet(this.handwritingAreaNode, {
                "style": "default",
                "contentWidth" : this.options.tabletWidth || 0,
                "contentHeight" : this.options.tabletHeight || 0,
                "onSave" : function( base64code, base64Image, imageFile ){
                    this.handwritingFile = imageFile;
                    this.handwritingNode.hide();
                    this.handwritingNodeMask.hide();
                    // this.page.get("div_image").node.set("src",base64Image);
                }.bind(this),
                "onCancel": function(){
                    this.handwritingFile = null;
                    this.handwritingNode.hide();
                    this.handwritingNodeMask.hide();
                }.bind(this)
            }, null );
            this.tablet.load();
        }.bind(this));

        this.handwritingActionNode.addEvent("click", function(){
            //this.handwritingNode.hide();
            if (this.tablet) this.tablet.save();
        }.bind(this));
    },

    setIdeaList: function(ideas){
        var _self = this;
        ideas.each(function(idea){
            new Element("div", {
                "styles": this.css.selectIdeaItemNode,
                "text": idea,
                "events": {
                    "click": function(){
                        if (_self.inputTextarea.get("value")==MWF.xApplication.process.Work.LP.inputText){
                            _self.inputTextarea.set("value", this.get("text"));
                        }else{
                            _self.inputTextarea.set("value", _self.inputTextarea.get("value")+", "+this.get("text"));
                        }
                    },
                    "dblclick": function(){
                        if (_self.inputTextarea.get("value")==MWF.xApplication.process.Work.LP.inputText){
                            _self.inputTextarea.set("value", this.get("text"));
                        }else{
                            _self.inputTextarea.set("value", _self.inputTextarea.get("value")+", "+this.get("text"));
                        }
                    },
                    "mouseover": function(){this.setStyles(_self.css.selectIdeaItemNode_over);},
                    "mouseout": function(){this.setStyles(_self.css.selectIdeaItemNode);}
                }
            }).inject(this.selectIdeaAreaNode);
        }.bind(this));
    },
    setButtons: function(){
        this.cancelButton = new Element("div", {"styles": this.css.cancelButton}).inject(this.buttonsArea);
        var iconNode = new Element("div", {"styles": this.css.cancelIconNode}).inject(this.cancelButton);
        var textNode = new Element("div", {"styles": this.css.cancelTextNode, "text": MWF.xApplication.process.Work.LP.cancel}).inject(this.cancelButton);

        this.okButton = new Element("div", {"styles": this.css.okButton}).inject(this.buttonsArea);
        var iconNode = new Element("div", {"styles": this.css.okIconNode}).inject(this.okButton);
        var textNode = new Element("div", {"styles": this.css.okTextNode, "text": MWF.xApplication.process.Work.LP.ok}).inject(this.okButton);

        this.cancelButton.addEvent("click", function(){
            this.destroy();
            this.fireEvent("cancel");
        }.bind(this));

        this.okButton.addEvent("click", function( ev ){
            if (!this.selectedRoute) {
                this.routeSelectorArea.setStyle("background-color", "#ffe9e9");
                //new mBox.Notice({
                //    type: "error",
                //    position: {"x": "center", "y": "top"},
                //    move: false,
                //    target: this.routeSelectorArea,
                //    delayClose: 6000,
                //    content: MWF.xApplication.process.Work.LP.mustSelectRoute
                //});
                MWF.xDesktop.notice(
                    "error",
                    {"x": "center", "y": "top"},
                    MWF.xApplication.process.Work.LP.mustSelectRoute,
                    this.routeSelectorArea,
                    null,  //{"x": 0, "y": 30}
                    { "closeOnBoxClick" : true, "closeOnBodyClick" : true, "fixed" : true, "delayClose" : 6000 }
                );
                return false;
            }
debugger;
            var routeName = this.selectedRoute.retrieve("routeName") || this.selectedRoute.get("text");
            var opinion = this.inputTextarea.get("value");
            if (opinion === MWF.xApplication.process.Work.LP.inputText) opinion = "";
            var medias = [];
            if (this.handwritingFile) medias.push(this.handwritingFile);
            if (this.soundFile) medias.push(this.soundFile);
            if (this.videoFile) medias.push(this.videoFile);

            var currentRouteId = this.selectedRoute.retrieve("route");
            var routeData = this.getRouteData( currentRouteId );
            if( !opinion && medias.length === 0  ){
                if( routeData.opinionRequired == true ){
                    this.inputTextarea.setStyle("background-color", "#ffe9e9");
                    //new mBox.Notice({
                    //    type: "error",
                    //    position: {"x": "center", "y": "top"},
                    //    move: false,
                    //    target: this.inputTextarea,
                    //    delayClose: 6000,
                    //    content: MWF.xApplication.process.Work.LP.opinionRequired
                    //});
                    MWF.xDesktop.notice(
                        "error",
                        {"x": "center", "y": "top"},
                        MWF.xApplication.process.Work.LP.opinionRequired,
                        this.inputTextarea,
                        null,  //{"x": 0, "y": 30}
                        { "closeOnBoxClick" : true, "closeOnBodyClick" : true, "fixed" : true, "delayClose" : 6000 }
                    );
                    return false;
                }
            }

            var appendTaskOrgItem = "";
            if( routeData.type === "appendTask" && routeData.appendTaskIdentityType === "select" ){
                if( !this.orgItems || this.orgItems.length === 0 ){
                    //new mBox.Notice({
                    //    type: "error",
                    //    position: {"x": "center", "y": "top"},
                    //    move: false,
                    //    target: this.orgsArea,
                    //    delayClose: 6000,
                    //    content: MWF.xApplication.process.Work.LP.noAppendTaskIdentityConfig //"没有配置转交人，请联系管理员"
                    //});
                    MWF.xDesktop.notice(
                        "error",
                        {"x": "center", "y": "center"},
                        MWF.xApplication.process.Work.LP.noAppendTaskIdentityConfig,
                        this.node,
                        null,  //{"x": 0, "y": 30}
                        { "closeOnBoxClick" : true, "closeOnBodyClick" : true, "fixed" : true, "delayClose" : 6000 }
                    );
                    return false;
                }else{
                    appendTaskOrgItem = this.orgItems[0]
                }
            }


            this.saveOrgsWithCheckEmpower( function(){
                var array = [routeName, opinion, medias];

                if( appendTaskOrgItem ){
                    var appandTaskIdentityList = appendTaskOrgItem.getData();
                    if( !appandTaskIdentityList || appandTaskIdentityList.length === 0){
                        //new mBox.Notice({
                        //    type: "error",
                        //    position: {"x": "center", "y": "top"},
                        //    move: false,
                        //    target: this.orgsArea,
                        //    delayClose: 6000,
                        //    content:  MWF.xApplication.process.Work.LP.selectAppendTaskIdentityNotice //"请选择转交人"
                        //});
                        MWF.xDesktop.notice(
                            "error",
                            {"x": "center", "y": "center"},
                            MWF.xApplication.process.Work.LP.selectAppendTaskIdentityNotice,
                            this.node,
                            {"x": 0, "y": 30},
                            { "closeOnBoxClick" : true, "closeOnBodyClick" : true, "fixed" : true, "delayClose" : 6000 }
                        );
                        return;
                    }else{
                        array.push( appendTaskOrgItem.getData() );
                        appendTaskOrgItem.setData([]);
                    }
                }

                debugger;
                if( routeData.validationScriptText ){
                    var validation = this.form.Macro.exec(routeData.validationScriptText, this);
                    if( !validation || validation.toString() !== "true" ){
                        if( typeOf(validation) === "string" ){
                            MWF.xDesktop.notice(
                                "error",
                                {"x": "center", "y": "center"},
                                validation,
                                this.node,
                                {"x": 0, "y": 30},
                                { "closeOnBoxClick" : true, "closeOnBodyClick" : true, "fixed" : true, "delayClose" : 6000 }
                            );
                            return false;
                        }else{
                            //"路由校验失败"
                            MWF.xDesktop.notice(
                                "error",
                                {"x": "center", "y": "center"},
                                MWF.xApplication.process.Work.LP.routeValidFailure,
                                this.node,
                                {"x": 0, "y": 30},
                                { "closeOnBoxClick" : true, "closeOnBodyClick" : true, "fixed" : true, "delayClose" : 6000 }
                            );
                            return false;
                        }
                    }
                }

                this.node.mask({
                    "inject": {"where": "bottom", "target": this.node},
                    "destroyOnHide": true,
                    "style": {
                        "background-color": "#999",
                        "opacity": 0.3,
                        "z-index":600
                    }
                });
                this.fireEvent("submit", array );
            }.bind(this))
        }.bind(this));
    },


    destroy: function(){
        this.node.empty();
        delete this.task;
        delete this.node;
        delete this.routeSelectorTile;
        delete this.routeSelectorArea;
        delete this.routeOpinionTile;
        delete this.routeOpinionArea;
        delete this.buttonsArea;
        delete this.inputOpinionNode;
        delete this.inputTextarea;
        delete this.cancelButton;
        delete this.okButton;
    },
    getRouteDataList : function(){
        if( !this.routeDataList ){
            o2.Actions.get("x_processplatform_assemble_surface").listRoute( {"valueList":this.task.routeList} , function( json ){
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
    getMaxOrgLength : function(){
        var routeList = this.getRouteDataList();
        var length = 0;
        routeList.each( function(route){
            length = Math.max( length, route.selectConfigList.length);
        });
        return length;
    },
    getOrgData : function( routeId ){
        var routeList = this.getRouteDataList();
        for( var i=0; i<routeList.length; i++ ){
            if( routeList[i].id === routeId ){
                return routeList[i].selectConfigList;
            }
        }
    },
    loadOrgs : function( route ){
        if( !this.form || !route ){
            this.orgsArea.hide();
            this.setSize( 0 );
            return;
        }else{
            this.orgsArea.show();
        }
        if( !this.orgTableObject )this.orgTableObject = {};
        if( !this.orgItemsObject )this.orgItemsObject = {};
        var isLoaded = false;
        for( var key in this.orgTableObject ){
            if( route === key ){
                this.orgTableObject[key].show();
                this.orgItems = this.orgItemsObject[key] || [];
                var data = this.getOrgData( route );
                this.setSize( data.length );
                isLoaded = true;
            }else{
                this.orgTableObject[key].hide();
            }
        }
        if( isLoaded )return;

        this.orgItems = [];
        this.orgItemsObject[route] = this.orgItems;

        var data = this.getOrgData( route );
        this.setSize( data.length );
        if( data.length  ){
            this.orgsArea.show();

            var len = data.length;

            var routeOrgTable = new Element("table",{
                "cellspacing" : 0, "cellpadding" : 0, "border" : 0, "width" : "100%",
                "styles" : this.css.routeOrgTable
            }).inject( this.orgsArea );
            this.orgTableObject[route] = routeOrgTable;

            //if( len <= this.options.maxOrgCountPerline ){
            //    var width = 1 / len * 100;
            //    var tr = new Element("tr").inject( routeOrgTable );
            //    for (var n=0; n<len; n++){
            //        new Element("td", { "width" : width+"%", "styles" : this.css.routeOrgOddTd }).inject( tr );
            //    }
            //}else{
            //    var lines = ((len+1)/this.options.maxOrgCountPerline).toInt();
            //    var width = 1 / this.options.maxOrgCountPerline * 100;
            //    for( var n=0; n<lines; n++ ){
            //        var tr = new Element("tr").inject( routeOrgTable );
            //        for( var i=0; i<this.options.maxOrgCountPerline; i++ ){
            //            new Element("td", { "width" : width+"%", "styles" : this.css.routeOrgOddTd }).inject( tr );
            //        }
            //    }
            //}
            //
            //var tds = routeOrgTable.getElements("td");
            //data.each( function( config, i ){
            //    this.loadOrg( tds[i], config )
            //}.bind(this))

            var lines = ((len+1)/2).toInt();
            for (var n=0; n<lines; n++){
                var tr = new Element("tr").inject( routeOrgTable );
                new Element("td", { "width" : "50%", "styles" : this.css.routeOrgOddTd }).inject( tr );
                new Element("td", { "width" : "50%", "styles" : this.css.routeOrgEvenTd }).inject( tr );
            }

            var trs = routeOrgTable.getElements("tr");
            data.each( function( config, i ){
                var sNode;
                var width;
                if (i+1==len && (len % 2===1)){
                    sNode = trs[trs.length-1].getFirst("td");
                    sNode.set("colspan", 2);
                    trs[trs.length-1].getLast("td").destroy();
                    sNode.setStyle("border","0px");
                    sNode.set("width","100%");
                    this.loadOrg( sNode, config, "all")
                }else{
                    var row = ((i+2)/2).toInt();
                    var tr = trs[row-1];
                    sNode = (i % 2===0) ? tr.getFirst("td") : tr.getLast("td");
                    this.loadOrg( sNode, config, (i % 2===0) ? "left" : "right" )
                }
            }.bind(this))
        }else{
            this.orgsArea.hide();
        }

    },
    loadOrg : function( container, json, position ){
        var titleNode = new Element("div.selectorTitle", {
            "styles" : this.css.selectorTitle
        }).inject(container);
        var titleTextNode = new Element("div.selectorTitleText", {
            "text": json.title,
            "styles" : this.css.selectorTitleText
        }).inject(titleNode);

        var errorNode = new Element("div.selectorErrorNode", {
            "styles" : this.css.selectorErrorNode
        }).inject(titleNode);

        var contentNode = new Element("div.selectorContent", {
            "styles" : this.css.selectorContent
        }).inject(container);
        var org = new MWF.xApplication.process.Work.Processor.Org( contentNode, this.form, json, this );
        org.errContainer = errorNode;
        org.summitDlalog = this;
        org.load();
        this.orgItems.push( org )

    },
    getCurrentRouteSelectorList : function(){
        var selectorList = [];
        var currentRoute = this.selectedRoute ? this.selectedRoute.retrieve("route") : "";
        var orgList = this.orgItemsObject[currentRoute];
        if( !orgList )return [];
        orgList.each( function( org ){
            if( org.selector && org.selector.selector ){
                selectorList.push( org.selector.selector );
            }
        }.bind(this))
        return selectorList;
    },
    getOffsetY : function(node){
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 ) +
            (node.getStyle("padding-top").toInt() || 0 ) +
            (node.getStyle("padding-bottom").toInt() || 0 )+
            (node.getStyle("border-top-width").toInt() || 0 ) +
            (node.getStyle("border-bottom-width").toInt() || 0 );
    },
    setSize : function( currentOrgLength ){
        if( layout.mobile )return;

        var lines = ((currentOrgLength+1)/2).toInt();
        var flag = false;

        var height = 0;
        if( this.routeSelectorTile )height = height + this.getOffsetY(this.routeSelectorTile) +  this.routeSelectorTile.getStyle("height").toInt();
        if( this.routeSelectorArea )height = height + this.getOffsetY(this.routeSelectorArea) +  this.routeSelectorArea.getStyle("height").toInt();
        if( this.routeOpinionTile )height = height + this.getOffsetY(this.routeOpinionTile) +  this.routeOpinionTile.getStyle("height").toInt();
        if( this.routeOpinionArea )height = height + this.getOffsetY(this.routeOpinionArea) +  this.routeOpinionArea.getStyle("height").toInt();
        //if( this.buttonsArea )height = height + this.getOffsetY(this.buttonsArea) +  this.buttonsArea.getStyle("height").toInt();

        if( lines > 0 ){
            if( this.orgsTile )height = height + this.getOffsetY(this.orgsTile) +  this.orgsTile.getStyle("height").toInt();
            height = height + lines*this.options.orgHeight + this.getOffsetY(this.orgsArea);
            this.node.setStyle( "height", height );

            //flag = (lines*this.options.orgHeight + 431) > Math.floor( this.form.app.content.getSize().y * 0.9);
            //this.node.store("height", Math.min( Math.floor( this.form.app.content.getSize().y * 0.9) , lines*this.options.orgHeight + 431 ));
        }else{
            this.node.setStyle( "height", height );
            //this.node.store("height", 401 );
        }
        if( this.getMaxOrgLength() > 1 ){
            this.node.setStyles( this.css.node_wide );
            this.inputOpinionNode.setStyles( this.css.inputOpinionNode_wide );
            this.inputTextarea.setStyles( this.css.inputTextarea_wide );
            this.inputTextareaStyle = this.css.inputTextarea_wide;
            this.selectIdeaNode.setStyles( this.css.selectIdeaNode_wide );

        }else{
            this.node.setStyles( this.css.node );
            this.inputOpinionNode.setStyles( this.css.inputOpinionNode );
            this.inputTextarea.setStyles( this.css.inputTextarea );
            this.inputTextareaStyle = this.css.inputTextarea;
            this.selectIdeaNode.setStyles( this.css.selectIdeaNode );
        }
        //this.node.store("width", this.node.getStyle("width").toInt() + ( flag ? 20 : 0 ));
        this.fireEvent("resize");
    },
    validationOrgs : function(){
        if( !this.orgItems || !this.orgItems.length )return true;
        var flag = true;
        this.orgItems.each( function(item){
            if( !item.validation() )flag = false;
        }.bind(this));
        return flag;
    },
    isOrgsHasEmpower: function(){
        if( !this.orgItems || !this.orgItems.length )return true;
        var flag = false;
        this.needCheckEmpowerOrg = [];
        this.orgItems.each( function(item){
            if( item.hasEmpowerIdentity() ){
                this.needCheckEmpowerOrg.push(item);
                flag = true;
            }
        }.bind(this));
        return flag;
    },
    saveOrgs : function( keepSilent ){
        if( !this.orgItems || !this.orgItems.length )return true;
        var flag = true;
        this.orgItems.each( function(item){
            if( !item.save( !keepSilent ) )flag = false;
        }.bind(this));
        return flag;
    },
    saveOrgsWithCheckEmpower : function( callback ){
        if( !this.orgItems || !this.orgItems.length ){
            if( callback )callback();
            return true;
        }
        if( !this.validationOrgs() )return false;
        if( !this.isOrgsHasEmpower() ){
            if( callback )callback();
            return true;
        }
        //this.checkEmpowerMode = true;
        this.showEmpowerDlg( callback );
    },
    showEmpowerDlg : function( callback ){
        //this.empowerMask = new Element("div", {"styles": this.css.handwritingMask}).inject(this.node);

        //this.needCheckEmpowerOrg.each( function(org){
        //    org.saveCheckedEmpowerData();
        //}.bind(this));

        var empowerNode = new Element("div.empowerNode", {"styles": this.css.empowerNode});
        var empowerTitleNode = new Element("div",{
            text : MWF.xApplication.process.Xform.LP.empowerDlgText,
            styles : this.css.empowerTitleNode
        }).inject(empowerNode);

        var orgs = this.needCheckEmpowerOrg;
        var len = orgs.length;
        var lines = ((len+1)/2).toInt();

        var empowerTable = new Element("table",{
            "cellspacing" : 0, "cellpadding" : 0, "border" : 0, "width" : "100%",
            "styles" : this.css.empowerTable
        }).inject( empowerNode );

        for (var n=0; n<lines; n++){
            var tr = new Element("tr").inject( empowerTable );
            new Element("td", { "width" : "50%", "styles" : this.css.empowerOddTd }).inject( tr );
            new Element("td", { "width" : "50%", "styles" : this.css.empowerEvenTd }).inject( tr );
        }

        var trs = empowerTable.getElements("tr");
        orgs.each( function( org, i ){
            var sNode;
            var width;
            if (i+1==len && (len % 2===1)){
                sNode = trs[trs.length-1].getFirst("td");
                sNode.set("colspan", 2);
                trs[trs.length-1].getLast("td").destroy();
                width = "50%";
            }else{
                var row = ((i+2)/2).toInt();
                var tr = trs[row-1];
                sNode = (i % 2===0) ? tr.getFirst("td") : tr.getLast("td");
            }

            var titleNode = new Element("div.empowerAreaTitle", {
                "styles" : this.css.empowerAreaTitle
            }).inject(sNode);

            var titleTextNode = new Element("div.empowerAreaTitleText", {
                "text": org.json.title,
                "styles" : this.css.empowerAreaTitleText
            }).inject(titleNode);

            var selectAllNode = new Element("div",{
                styles : {
                    float : "right"
                }
            }).inject(titleNode);

            var contentNode = new Element("div.empowerAreaContent", {
                "styles" : this.css.empowerAreaContent
            }).inject(sNode);

            org.loadCheckEmpower( null, contentNode, selectAllNode );

        }.bind(this));

        empowerNode.setStyle( "height", lines*this.options.orgHeight + 20 );
        //var dlgHeight = Math.min( Math.floor( this.form.app.content.getSize().y * 0.9) , lines*this.options.orgHeight + 151 );

        //var width = this.node.retrieve("width");
        //empowerNode.setStyle( "width", width );
        var width = "880";
        //if( len > 1 ){
        //    width = "840"
        //}else{
        //    width = "420"
        //}
        empowerNode.setStyle( "width", width+"px" );

        this.node.getParent().mask( {
            "style": this.css.mask
        });
        this.empowerDlg = o2.DL.open({
            "title": MWF.xApplication.process.Xform.LP.selectEmpower,
            "style": this.form.json.dialogStyle || "user",
            "isResize": false,
            "content": empowerNode,
            //"container" : this.node,
            "width": width, //600,
            "height": "auto", //dlgHeight,
            "mark" : false,
            "buttonList": [
                {
                    "type" : "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function(d, e){
                        //if (this.empowerDlg) this.empowerDlg.okButton.click();

                        orgs.each( function( org, i ){
                            org.saveCheckedEmpowerData( function(){
                                if( i === orgs.length-1 ){
                                    if( callback )callback();
                                    this.node.getParent().unmask();
                                    this.empowerDlg.close();
                                }
                            }.bind(this))
                        }.bind(this))
                    }.bind(this)
                },
                {
                    "type" : "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function(){
                        this.node.getParent().unmask();
                        this.empowerDlg.close();
                    }.bind(this)
                }
            ]
        });
    }

});

MWF.xApplication.process.Work.Processor.Org = new Class({
    Implements: [Options, Events],
    options: {
        moduleEvents : ["queryLoadSelector","postLoadSelector","queryLoadCategory","postLoadCategory","selectCategory", "unselectCategory","queryLoadItem","postLoadItem","selectItem", "unselectItem","change"]
    },
    initialize: function (container, form, json, processor, options) {
        this.form = form;
        this.json = json;
        this.processor = processor;
        this.container = $(container);
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        this.setOptions(options);
    },
    load : function(){
        var options = this.getOptions();

        if(options){
            this.selector = new MWF.O2Selector(this.container, options);
        }
    },
    _getOrgOptions: function(){
        this.selectTypeList = typeOf( this.json.selectType ) == "array" ? this.json.selectType : [this.json.selectType];
        if( this.selectTypeList.contains( "identity" ) ) {
            this.identityOptions = new MWF.xApplication.process.Work.Processor.IdentityOptions(this.form, this.json);
        }
        if( this.selectTypeList.contains( "unit" ) ) {
            this.unitOptions = new MWF.xApplication.process.Work.Processor.UnitOptions(this.form, this.json);
        }
        //if( this.selectTypeList.contains( "group" ) ){
        //    this.groupOptions = new MWF.APPOrg.GroupOptions( this.form, this.json );
        //}
    },
    getOptions: function(){
        var _self = this;
        this._getOrgOptions();
        if( this.selectTypeList.length === 0 )return false;
        var exclude = [];
        if( this.json.exclude ){
            var v = this.form.Macro.exec(this.json.exclude.code, this);
            exclude = typeOf(v)==="array" ? v : [v];
        }

        var identityOpt;
        if( this.identityOptions ){
            identityOpt = this.identityOptions.getOptions();
            if (this.json.identityRange!=="all"){
                if ( !identityOpt.noUnit && (!identityOpt.units || !identityOpt.units.length) ){
                    this.form.notice(MWF.xApplication.process.Xform.LP.noIdentitySelectRange, "error", this.node);
                    return false;
                }
            }
            if ( !identityOpt.noUnit && this.json.dutyRange && this.json.dutyRange!=="all"){
                if (!identityOpt.dutys || !identityOpt.dutys.length){
                    this.form.notice(MWF.xApplication.process.Xform.LP.noIdentityDutySelectRange, "error", this.node);
                    return false;
                }
            }
            identityOpt.values = this.getValue();
            identityOpt.exclude = exclude;
        }

        var unitOpt;
        if( this.unitOptions ){
            unitOpt = this.unitOptions.getOptions();
            if (this.json.unitRange!=="all"){
                if ( !unitOpt.units || !unitOpt.units.length){
                    this.form.notice(MWF.xApplication.process.Xform.LP.noUnitSelectRange, "error", this.node);
                    return false;
                }
            }
            unitOpt.values = this.getValue();
            unitOpt.exclude = exclude;
        }

        //var groupOpt;
        //if( this.groupOptions ){
        //    groupOpt = this.groupOptions.getOptions();
        //    groupOpt.values = (this.json.isInput) ? [] : values;
        //    groupOpt.exclude = exclude;
        //}

        var defaultOpt = {
            "style" : "process",
            "width" : "auto",
            "height" : "240",
            "embedded" : true,
            "hasLetter" : false, //字母
            "hasTop" : true, //可选、已选的标题
            "hasShuttle" : true //穿梭按钮
        };

        debugger;

        if( this.json.events && typeOf(this.json.events) === "object" ){
            Object.each(this.json.events, function(e, key){
                if (e.code){
                    if (this.options.moduleEvents.indexOf(key)!==-1){
                        //this.addEvent(key, function(event){
                        //    return this.form.Macro.fire(e.code, this, event);
                        //}.bind(this));
                        if( key === "postLoadSelector" ) {
                            this.addEvent("loadSelector", function (selector) {
                                return this.form.Macro.fire(e.code, selector);
                            }.bind(this))
                        }else if( key === "queryLoadSelector"){
                            defaultOpt["onQueryLoad"] = function(target){
                                return this.form.Macro.fire(e.code, target);
                            }.bind(this)
                        }else{
                            defaultOpt["on"+key.capitalize()] = function(target){
                                return this.form.Macro.fire(e.code, target);
                            }.bind(this)
                        }
                    }
                }
            }.bind(this));
        }

        if( this.form.json.selectorStyle ){
            defaultOpt = Object.merge( Object.clone(this.form.json.selectorStyle), defaultOpt );
            if( this.form.json.selectorStyle.style )defaultOpt.style = this.form.json.selectorStyle.style;
        }

        if( this.selectTypeList.length === 1 ){
            return Object.merge( defaultOpt, {
                "type": this.selectTypeList[0],
                "onLoad": function(){
                    //this 为 selector
                    _self.selectOnLoad(this, this.selector )
                }
                //"onComplete": function(items){
                //    this.selectOnComplete(items);
                //}.bind(this),
                //"onCancel": this.selectOnCancel.bind(this),
                //"onClose": this.selectOnClose.bind(this)
            }, identityOpt || unitOpt )
        }else if( this.selectTypeList.length > 1 ){
            var options = {
                "type" : "",
                "types" : this.selectTypeList,
                "onLoad": function(){
                    //this 为 selector
                    _self.selectOnLoad(this)
                }
                //"onComplete": function(items){
                //    this.selectOnComplete(items);
                //}.bind(this),
                //"onCancel": this.selectOnCancel.bind(this),
                //"onClose": this.selectOnClose.bind(this)
            };
            if( identityOpt ){
                options.identityOptions = Object.merge( defaultOpt, identityOpt);
            }
            if( unitOpt ){
                options.unitOptions = Object.merge( defaultOpt, unitOpt);
            }
            //if( groupOpt )options.groupOptions = groupOpt;
            return options;
        }
    },
    //selectOnComplete: function(items){
    //    var array = [];
    //    items.each(function(item){
    //        array.push(item.data);
    //    }.bind(this));
    //    this.checkEmpower( array, function( data ){
    //        var values = [];
    //        data.each(function(d){
    //            values.push(MWF.org.parseOrgData(d, true));
    //        }.bind(this));
    //
    //        this.setData(values);
    //        //this.validationMode();
    //        //this.validation();
    //        this.fireEvent("select");
    //    }.bind(this))
    //},
    //selectOnCancel: function(){
    //    //this.validation();
    //},
    selectOnLoad: function( selector ){
        //if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
        this.fireEvent("loadSelector", [selector])
    },
    //selectOnClose: function(){
    //    //v = this._getBusinessData();
    //    //if (!v || !v.length) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
    //},

    hasEmpowerIdentity : function(){
        var data = this.getData();
        if(!this.empowerChecker )this.empowerChecker = new MWF.xApplication.process.Work.Processor.EmpowerChecker(this.form, this.json, this.processor);
        return this.empowerChecker.hasEmpowerIdentity( data );
    },
    checkEmpower : function( data, callback, container, selectAllNode ){
        if( typeOf(data)==="array" && this.identityOptions && this.json.isCheckEmpower && this.json.identityResultType === "identity" ) {
            if(!this.empowerChecker )this.empowerChecker = new MWF.xApplication.process.Work.Processor.EmpowerChecker(this.form, this.json, this.processor);
            this.empowerChecker.selectAllNode = selectAllNode;
            this.empowerChecker.load(data, callback, container);
        }else{
            if( callback )callback( data );
        }
    },

    loadCheckEmpower : function( callback, container, selectAllNode ){
        this.checkEmpower( this.getData(), callback, container, selectAllNode)
    },
    saveCheckedEmpowerData:function( callback ){
        var data = this.getData();
        this.empowerChecker.replaceEmpowerIdentity(data, function( newData ){
            var values = [];
            newData.each(function(d){
                values.push(MWF.org.parseOrgData(d, true));
            }.bind(this));
            this.setData( values );
            if( callback )callback(values)
        }.bind(this))
    },

    //saveWithCheckEmpower: function( isValid, callback ){
    //    var checkEmpowerData = function(){
    //        var array = this.getData();
    //        this.checkEmpower( array, function( data ){
    //            var values = [];
    //            data.each(function(d){
    //                values.push(MWF.org.parseOrgData(d, true));
    //            }.bind(this));
    //            this.setData( values );
    //            if( callback )callback(values)
    //        }.bind(this), container, selectAllNode)
    //    }.bind(this)
    //    if( isValid ){
    //        if( this.validation() ){
    //            checkEmpowerData( function(){
    //                if(callback)callback();
    //            }.bind(this));
    //            return true;
    //        }else{
    //            return false;
    //        }
    //    }else{
    //        //this.setData( this.getData() );
    //        checkEmpowerData( function(){
    //            if(callback)callback();
    //        }.bind(this));
    //        return true;
    //    }
    //},

    save: function( isValid ){
        if( isValid ){
            if( this.validation() ){
                return true;
            }else{
                return false;
            }
        }else{
            this.setData( this.getData() );
            return true;
        }
    },

    resetSelectorData : function(){
        if( this.selector && this.selector.selector ){
            this.selector.selector.emptySelectedItems();
            this.selector.selector.options.values = this.getValue();
            this.selector.selector.setSelectedItem();
        }
    },
    resetData: function(){
        var v = this.getValue();
        //this.setData((v) ? v.join(", ") : "");
        this.setData(v);
    },
    getData: function(){
        if( this.selector ){
            return this.getSelectedData();
        }else{
            return this.getValue();
        }
    },
    getSelectedData : function(){
        var data = [];
        if( this.selector && this.selector.selector){
            this.selector.selector.selectedItems.each( function( item ){
                data.push( MWF.org.parseOrgData(item.data) );
            })
        }
        return data;
    },
    getValue: function(){
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
        return value || "";
    },
    _computeValue: function(){
        var values = [];
        if (this.json.identityValue) {
            this.json.identityValue.each(function(v){ if (v) values.push(v)});
        }
        if (this.json.unitValue) {
            this.json.unitValue.each(function(v){ if (v) values.push(v)});
        }
        if (this.json.dutyValue) {
            var dutys = JSON.decode(this.json.dutyValue);
            var par;
            if (dutys.length){
                dutys.each(function(duty){
                    if (duty.code) par = this.form.Macro.exec(duty.code, this);
                    var code = "return this.org.getDuty(\""+duty.name+"\", \""+par+"\")";

                    var d = this.form.Macro.exec(code, this);
                    if (typeOf(d)!=="array") d = (d) ? [d.toString()] : [];
                    d.each(function(dd){if (dd) values.push(dd);});

                }.bind(this));
            }
        }
        if (this.json.defaultValue && this.json.defaultValue.code){
            var fd = this.form.Macro.exec(this.json.defaultValue.code, this);
            if (typeOf(fd)!=="array") fd = (fd) ? [fd] : [];
            fd.each(function(fdd){
                if (fdd){
                    if (typeOf(fdd)==="string"){
                        var data;
                        this.getOrgAction()[this.getValueMethod(fdd)](function(json){ data = json.data }.bind(this), null, fdd, false);
                        values.push(data);
                    }else{
                        values.push(fdd);
                    }
                }
            }.bind(this));
        }
        if (this.json.count>0){
            return values.slice(0, this.json.count);
        }
        return values;
        //return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },
    getOrgAction: function(){
        if (!this.orgAction) this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        //if (!this.orgAction) this.orgAction = new MWF.xApplication.Selector.Actions.RestActions();
        return this.orgAction;
    },
    setData: function(value){

        if (!value) return false;
        var oldValues = this.getValue();
        var values = [];

        var type = typeOf(value);
        if (type==="array"){
            value.each(function(v){
                var vtype = typeOf(v);
                var data = null;
                if (vtype==="string"){
                    this.getOrgAction()[this.getValueMethod(v)](function(json){ data = MWF.org.parseOrgData(json.data); }.bind(this), error, v, false);
                }
                if (vtype==="object") data = v;
                if (data)values.push(data);
            }.bind(this));
        }
        if (type==="string"){
            var vData;
            this.getOrgAction()[this.getValueMethod(value)](function(json){ vData = MWF.org.parseOrgData(json.data); }.bind(this), error, value, false);
            if (vData)values.push(vData);
        }
        if (type==="object")values.push(value);

        var change = false;
        if (oldValues.length && values.length){
            if (oldValues.length === values.length){
                for (var i=0; i<oldValues.length; i++){
                    if ((oldValues[i].distinguishedName!==values[i].distinguishedName) || (oldValues[i].name!==values[i].name) || (oldValues[i].unique!==values[i].unique)){
                        change = true;
                        break;
                    }
                }
            }else{
                change = true;
            }
        }else if (values.length || oldValues.length) {
            change = true;
        }
        this._setBusinessData(values);
        if (change) this.fireEvent("change");
    },

    getValueMethod: function(value){
        if (value){
            var flag = value.substr(value.length-1, 1);
            switch (flag.toLowerCase()){
                case "i":
                    return "getIdentity";
                case "p":
                    return "getPerson";
                case "u":
                    return "getUnit";
                case "g":
                    return "getGroup";
                default:
                    return (this.json.selectType==="unit") ? "getUnit" : "getIdentity";
            }
        }
        return (this.json.selectType==="unit") ? "getUnit" : "getIdentity";
    },

    _getBusinessData: function(){
        if (this.json.section=="yes"){
            return this._getBusinessSectionData();
        }else {
            if (this.json.type==="Opinion"){
                return this._getBusinessSectionDataByPerson();
            }else{
                return this.form.businessData.data[this.json.name] || "";
            }
        }
    },
    _getBusinessSectionData: function(){
        switch (this.json.sectionBy){
            case "person":
                return this._getBusinessSectionDataByPerson();
                break;
            case "unit":
                return this._getBusinessSectionDataByUnit();
                break;
            case "activity":
                return this._getBusinessSectionDataByActivity();
                break;
            case "script":
                return this._getBusinessSectionDataByScript(this.json.sectionByScript.code);
                break;
            default:
                return this.form.businessData.data[this.json.name] || "";
        }
    },
    _getBusinessSectionDataByPerson: function(){
        var dataObj = this.form.businessData.data[this.json.name];
        return (dataObj) ? (dataObj[layout.desktop.session.user.id] || "") : "";
    },
    _getBusinessSectionDataByUnit: function(){
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        var key = (this.form.businessData.task) ? this.form.businessData.task.unit : "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByActivity: function(){
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByScript: function(code){
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        var key = this.form.Macro.exec(code, this);
        return (key) ? (dataObj[key] || "") : "";
    },

    _setBusinessData: function(v){
        if (this.json.section=="yes"){
            this._setBusinessSectionData(v);
        }else {
            if (this.json.type==="Opinion"){
                this._setBusinessSectionDataByPerson(v);
            }else{
                if (this.form.businessData.data[this.json.name]){
                    this.form.businessData.data[this.json.name] = v;
                }else{
                    this.form.businessData.data[this.json.name] = v;
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
                if (this.json.isTitle) this.form.businessData.work.title = v;
            }
        }
    },
    _setBusinessSectionData: function(v){
        switch (this.json.sectionBy){
            case "person":
                this._setBusinessSectionDataByPerson(v);
                break;
            case "unit":
                this._setBusinessSectionDataByUnit(v);
                break;
            case "activity":
                this._setBusinessSectionDataByActivity(v);
                break;
            case "script":
                this._setBusinessSectionDataByScript(this.json.sectionByScript.code, v);
                break;
            default:
                if (this.form.businessData.data[this.json.name]){
                    this.form.businessData.data[this.json.name] = v;
                }else{
                    this.form.businessData.data[this.json.name] = v;
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
        }
    },
    _setBusinessSectionDataByPerson: function(v){
        var resetData = false;
        var key = layout.desktop.session.user.id;

        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj){
            dataObj = {};
            this.form.businessData.data[this.json.name] = dataObj;
            resetData = true;
        }
        if (!dataObj[key]) resetData = true;
        dataObj[key] = v;

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByUnit: function(v){
        var resetData = false;
        var key = (this.form.businessData.task) ? this.form.businessData.task.unit : "";

        if (key){
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj){
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByActivity: function(v){
        var resetData = false;
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";

        if (key){
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj){
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByScript: function(code, v){
        var resetData = false;
        var key = this.form.Macro.exec(code, this);

        if (key){
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj){
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },

    createErrorNode: function(text){
        var node;
        if( this.processor.css.errorContentNode ){
            node = new Element("div",{
                "styles" : this.processor.css.errorContentNode,
                "text": text
            });
            if( this.processor.css.errorCloseNode ){
                var closeNode = new Element("div",{
                    "styles" : this.processor.css.errorCloseNode ,
                    "events": {
                        "click" : function(){
                            this.destroy();
                        }.bind(node)
                    }
                }).inject(node);
            }
        }else {
            node = new Element("div");
            var iconNode = new Element("div", {
                "styles": {
                    "width": "20px",
                    "height": "20px",
                    "float": "left",
                    "background": "url(" + "/x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "height": "20px",
                    "line-height": "20px",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "keep-all"
                },
                "text": text
            }).inject(node);
        }
        return node;
    },
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            //this.isNotValidationMode = true;
            //this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            //this.node.setStyle("border-color", "red");

            this.errNode = this.createErrorNode(text);
            if( this.errContainer ){
                this.errContainer.empty();
                this.errNode.inject(this.errContainer);
            }else{
                this.errNode.inject(this.container, "after");
            }
            //this.showNotValidationMode(this.node);
            //if (!this.node.isIntoView()) this.node.scrollIntoView();
        }
    },
    validation: function(){
        var data = this.getData();
        this.setData( data );
        var flag=true;
        if( this.json.validationCount && typeOf( this.json.validationCount.toInt() ) === "number" ){
            if( data.length < this.json.validationCount.toInt() ){
                //if( this.json.validationCount.toInt() === 1 ){
                //    flag = "请选择"
                //}else{
                //    flag = "请至少选择"+this.json.validationCount+"项"
                //}
                flag = "请至少选择"+this.json.validationCount+"项"
            }
        }

        if( flag === true ){
            if ( this.json.validation && this.json.validation.code){
                flag = this.form.Macro.exec(this.json.validation.code, this);
                if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
            }
        }

        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }else if(this.errNode){
            this.errNode.destroy()
        }
        return true;
    }
});

MWF.xApplication.process.Work.Processor.EmpowerChecker = new Class({
    Extends : MWF.APPOrg.EmpowerChecker,
    initialize: function (form, json, processor) {
        this.form = form;
        this.json = json;
        this.processor = processor;
        this.css = this.processor.css;
        this.checkedAllItems = true;
    },
    hasEmpowerIdentity: function( data ){
        var flag = false;
        if( typeOf(data)==="array" && this.json.isCheckEmpower && this.json.identityResultType === "identity" ) {
            var array = [];
            data.each(function (d) {
                if (d.distinguishedName) {
                    var flag = d.distinguishedName.substr(d.distinguishedName.length - 1, 1).toLowerCase();
                    if (flag === "i")array.push(d.distinguishedName)
                }
            }.bind(this));
            if (array.length > 0) {
                o2.Actions.get("x_organization_assemble_express").listEmpowerWithIdentity({
                    "application": (this.form.businessData.work || this.form.businessData.workCompleted).application,
                    "process": (this.form.businessData.work || this.form.businessData.workCompleted).process,
                    "identityList": array
                }, function (json) {
                    var arr = [];
                    json.data.each(function (d) {
                        if (d.fromIdentity !== d.toIdentity)
                            arr.push(d);
                    });
                    if (arr.length > 0) {
                        flag = true;
                    }
                }.bind(this), null, false)
            }
        }
        return flag;
    },
    openSelectEmpowerDlg : function( data, orgData, callback, container ){
        var node = new Element("div", {"styles": this.css.empowerAreaNode});
        //var html = "<div style=\"line-height: 30px; color: #333333; overflow: hidden\">"+MWF.xApplication.process.Xform.LP.empowerDlgText+"</div>";
        var html = "<div style=\"margin-bottom:10px; margin-top:10px; overflow-y:auto;\"></div>";
        node.set("html", html);
        var itemNode = node.getLast();
        this.getEmpowerItems(itemNode, data);
        node.inject( container || this.container );

        if( this.selectAllNode ){
            var selectNode = this.createSelectAllEmpowerNode();
            selectNode.inject( this.selectAllNode );
            if( this.checkedAllItems ){
                selectNode.store("isSelected", true);
                selectNode.setStyles( this.css.empowerSelectAllItemNode_selected );
            }
        }
    },
    getSelectedData : function( callback ){
        var json = {};
        this.empowerSelectNodes.each(function(node){
            if( node.retrieve("isSelected") ){
                var d = node.retrieve("data");
                json[ d.fromIdentity ] = d;
            }
        }.bind(this));
        if( callback )callback( json );
    }
});

MWF.xApplication.process.Work.Processor.UnitOptions = new Class({
    Extends : MWF.APPOrg.UnitOptions
});

MWF.xApplication.process.Work.Processor.IdentityOptions = new Class({
    Extends : MWF.APPOrg.IdentityOptions
});
