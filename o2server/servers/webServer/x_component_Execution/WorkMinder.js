MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xDesktop.requireApp("Execution", "Minder", null, false);
MWF.xDesktop.requireApp("Execution", "WorkDeploy", null,false);

MWF.xApplication.Execution.WorkMinder = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "template" : "default",
        "theme": "fresh-blue-compat", //"fresh-blue"
        "width": "100%",
        "height": "100%",
        "hasTop": true,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasIcon": false,
        "hasBottom": false,
        "hasScroll" : false,
        "title": "脑图展现",
        "draggable": false,
        "closeAction": true
    },
    initialize: function (explorer, data, options) {
        this.setOptions(options);
        this.app = explorer.app;
        this.lp = this.app.lp;
        this.actions = this.app.restActions;
        this.explorer = explorer;

        this.path = "/x_component_Execution/$WorkMinder/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.options.title = this.lp.minderTitle + "-" +  ( data.title || "")　;

        this.data = data;
    },
    load: function (callback) {
        this.open();
    },
    _open: function () {
        this.formMaskNode = new Element("div.formMaskNode", {
            "styles": this.css.formMaskNode,
            "events": {
                "mouseover": function (e) {
                    e.stopPropagation();
                },
                "mouseout": function (e) {
                    e.stopPropagation();
                }
            }
        }).inject(this.app.content);

        this.formAreaNode = new Element("div.formAreaNode", {
            "styles": this.css.formAreaNode
        });

        this.formAreaNode.inject(this.formMaskNode, "after");
        this.formAreaNode.fade("in");

        this.createFormNode();

        //this.setFormNodeSize();
        this.refreshFun = this.refresh.bind(this);
        this.app.addEvent("resize", this.refreshFun);

        if (this.options.draggable && this.formTopNode) {
            var size = this.app.content.getSize();
            var nodeSize = this.formAreaNode.getSize();
            this.formAreaNode.makeDraggable({
                "handle": this.formTopNode,
                "limit": {
                    "x": [0, size.x - nodeSize.x],
                    "y": [0, size.y - nodeSize.y]
                }
            });
        }

    },
    destroy : function(){
        if(this.minder)this.minder.destroy() ;
        this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        delete this;
    },
    refresh: function(){
        this.setFormNodeSize();
        if(this.minder)this.minder.refresh();
    },
    reload: function(){
        if(this.minder)this.minder.destroy() ;
        this.formTableArea.empty();
        this._createTableContent();
    },
    _createTableContent: function () {
        this.setFormNodeSize();
        this.formTableArea.setStyles({"width": "100%", "height": "100%","background-color":"#fbfbfb"})
        this.listNestWork( function(json){
            this.createMinder( json );
        }.bind(this))
    },
    createMinder: function( json ) {
        this.minder = new MWF.xApplication.Execution.Minder(this.formTableArea, this.app, json, {
            "hasNavi" : false,
            "onPostLoad" : function(){
                this.minder.km.execCommand('ExpandToLevel', 1); //折叠到第一层
                this.minder.loadNavi( this.formTableContainer );
            }.bind(this),
            "onPostLoadNode" : function( minderNode ){
                this.setMinderNode( minderNode );
            }.bind(this)
        });
        this.minder.load();
    },
    listNestWork: function( callback ){
        //var data = {
        //    "root": {
        //        "data": {"id": "9f92035021ac",  "text": "软装修"},
        //        "children": [{
        //            "data": {"id": "b45yogtullsg", "created": 1463069010918, "text": "包阳台"},
        //            "children": [{
        //                "data": {"id": "3jl3i3j43", "created": 1463069010923, "text": "凤铝"}
        //            }, {
        //                "data": {"id": "3jl3i3j44", "created": 1463069010923, "text": "断桥"}
        //            }]
        //        },
        //            {
        //                "data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "衣柜"}, "children": [
        //                {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "主卧"}},
        //                {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "次卧"}}
        //            ]
        //            },
        //            {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "床"}, "children": []},
        //            {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "餐桌"}, "children": []},
        //            {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "灯具"}, "children": []},
        //            {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "窗帘"}, "children": []}
        //        ]
        //    }
        //};
        //if(callback)callback( data );

        //this.actions.getUserNestBaseWork(this.data.id, function(json){
        //    var d = this.transportData( json );
        //    if(callback)callback( d );
        //}.bind(this))

        this.actions.getUserMind(this.data.id, function(json){
            var d = this.transportData( json );
            if(callback)callback( d );
        }.bind(this))
    },
    transportData : function( json ){
        var newData = [];
        this.getNestData( json.data.works , newData );
        var d = {
            root : {
                data : this.data,
                children : newData
            }
        };
        d.root.data.text = d.root.data.title.length > 30 ? d.root.data.title.substr(0, 30) + "..." : d.root.data.title;
        d.root.data.note = " ";
        d.root.data.isCenterWork = true;
        return d;
    },
    getNestData: function( oldData, newData ){
        oldData.each( function(d){
            var newd = {};
            if(d.subWorks ){
                newd.children = [];
                this.getNestData( d.subWorks , newd.children );
            }
            newd.data = d;
            if( d.subWorks )delete newd.data.subWorks;
            newd.data.text = d.title.length > 20 ? d.title.substr(0, 20) + "..." : d.title;
            //if( newd.data.workProcessStatus == this.lp.WorkDeploy.statusDraft ){
            //    newd.data.readOnly = true;
            //}else{
            newd.data.note= d.watch? " ":null;
            //newd.data.note = " "
            /*
             *     取值为 0 移除进度信息；
             *     取值为 1 表示未开始；
             *     取值为 2 表示完成 1/8；
             *     取值为 3 表示完成 2/8；
             *     取值为 4 表示完成 3/8；
             *     其余类推，取值为 9 表示全部完成
             */
            //newd.data.progress = 8;
            newd.data.resource = this.getActions( newd.data, newd.children );
            //}
            newData.push( newd );
        }.bind(this))
    },
    setMinderNode : function( minderNode ){
        var _self = this;
        //alert("title="+minderNode.getData().title)
        //alert("watch="+minderNode.getData().watch)
        if( !minderNode.getData().watch ){
            //km.select([minderNode],true);
            //km.execCommand('Background', "#dcdcdc");
            //km.execCommand('ForeColor', "#fff");
        }else{
            var icon = minderNode.getRenderer('NoteIconRenderer').getRenderShape();
            if( icon ){
                icon.addEventListener("mouseover", function ( ev ) {
                    _self.workInforTimer = setTimeout(function() {
                        var c = this.getRenderBox('screen');
                        _self.showWorkInfor( this.getData(), c );
                    }.bind(this), 300);
                }.bind(minderNode));

                icon.addEventListener("mouseout", function ( ev ) {
                    clearTimeout(_self.workInforTimer);
                    _self.destroyWorkInfor();
                }.bind(minderNode));

                icon.addEventListener("click", function ( ev ) {
                    _self.openWork( this, this.getData() );
                    ev.stopPropagation();
                }.bind(minderNode));
            }

            var actions = minderNode.getRenderer('ResourceRenderer');
            if( actions && actions.overlays && actions.overlays.length > 0){
                actions.overlays.forEach(function( atn ){
                    atn.addEventListener("click", function ( ev ) {
                        var e = {
                            event : {
                                x : ev.originEvent.x,
                                y : ev.originEvent.y
                            }
                        };
                        _self.activeAction( atn.lastResourceName, this.getData(), e );
                        ev.stopPropagation();
                    }.bind(minderNode));
                })
            }


            var cNode = minderNode.getRenderContainer().node;
            cNode.addEventListener("dblclick", function ( ev ) {
                _self.openWork( this, this.getData() );
            }.bind(minderNode));
        }
    },
    getActions: function( data, children ){
        var actions = [];
        //return actions;
        if( data.workProcessStatus == this.lp.WorkDeploy.statusDraft ){
            if( this.app.identity == data.deployerIdentity ){
                actions.push("删除");
            }
        }else{
            if( (!children || children.length == 0) && this.app.identity == data.deployerIdentity) {
                actions.push("删除");
            }
            if( data.responsibilityIdentity == this.app.identity ){
                actions.push("汇报");
                actions.push("拆分");
            }
        }
        //return ["删除", "拆分"];
        return []

    },
    activeAction : function( actionText, data, ev){
        if( actionText == "删除" ){
            this.removeWork(data, ev);
        }else if(actionText == "汇报"){
            this.reportWork( data );
        }else if(actionText == "拆分"){
            this.splitWork( data );
        }
    },
    openWork : function(node, data){
        if( data.isCenterWork ){
            this.openCenterWork(node, data)
        }else{
            this.openBaseWork(node, data);
        }
    },
    openCenterWork: function(node, data){
        var isEditedBool = ( data.processStatus == this.lp.WorkDeploy.statusDraft  &&  this.app.identity == data.deployerIdentity  )? true : false;
        this.workDeploy = new MWF.xApplication.Execution.WorkMinder.WorkDeploy(this, this.actions,{"id":data.id},{
            "isEdited":isEditedBool,
            "centerWorkId":data.id,
            "onQueryClose" : function(){
                if( this.workDeploy.contentChanged ){
                    this.reload();
                }
            }.bind(this)
        } );
        this.workDeploy.load();
    },
    openBaseWork : function( node, data ){
        if( data.workProcessStatus == this.lp.WorkDeploy.statusDraft ){
            MWF.xDesktop.requireApp("Execution", "WorkForm", null, false);
            var workform = new MWF.xApplication.Execution.WorkForm(this.explorer, this.app.restActions, data,{
                "isNew": false,
                "isEdited": this.app.identity == data.deployerIdentity,
                "onPostSave" : function(){
                    this.reload();
                }.bind(this)
            });
            workform.load();
        }else{
            MWF.xDesktop.requireApp("Execution", "WorkDetail", function(){
                var workform = new MWF.xApplication.Execution.WorkDetail(this, this.app.restActions,data,{
                    "isNew": false,
                    "isEdited": false
                });
                workform.load();
            }.bind(this));
        }
    },
    removeWork:function( data , ev ){
        var lp = this.app.lp;
        var text = lp.deleteDocument2.replace(/{title}/g, data.title);
        var _self = this;
        this.readyRemove = true;
        this.app.confirm("warn", ev, lp.deleteDocumentTitle, text, 350, 120, function () {
            _self._removeDocument( data );
            this.close();
        }, function () {
            _self.readyRemove = false;
            this.close();
        });
    },
    _removeDocument: function( data ){
        if( data.isCenterWork ){
            this.actions.deleteCenterWork(data.id, function(json){
                this.app.notice(this.app.lp.deleteDocumentOK, "success");
                this.reload();
            }.bind(this));
        }else{
            this.actions.deleteBaseWork(data.id, function(json){
                if(json.type && json.type == "success"){
                    this.app.notice(this.app.lp.deleteDocumentOK, "success");
                    this.reload();
                }else{
                    this.app.notice(json.data.message, "error")
                }
            }.bind(this));
        }
    },
    reportWork: function( data ){
        MWF.xDesktop.requireApp("Execution", "WorkReport", function(){
            var d = {
                title : data.title,
                workId : data.id,
                centerId : data.centerId,
                //centerTitle: this.data.centerTitle,
                parentWorkId : data.parentWorkId,
                //parentWorkTitle : this.data.title,
                workType : data.workType,
                workLevel : data.workLevel,
                completeDateLimitStr : data.completeDateLimitStr,
                completeDateLimit : data.completeDateLimit,
                reportCycle: data.reportCycle,
                responsibilityOrganizationName: data.responsibilityOrganizationName,
                responsibilityEmployeeName: data.responsibilityEmployeeName,
                responsibilityIdentity: data.responsibilityIdentity,
                cooperateOrganizationName: data.cooperateOrganizationName,
                cooperateEmployeeName: data.cooperateEmployeeName,
                cooperateIdentity: data.cooperateIdentity,
                readLeaderName: data.readLeaderName,
                readLeaderIdentity: data.readLeaderIdentity,
                reportDayInCycle: data.reportDayInCycle
            };
            var workReport = new MWF.xApplication.Execution.WorkReport(this, this.app.restActions, d, {
                "isNew": false,
                "isEdited": false,
                "from" : "drafter"
            });
            workReport.load();
        }.bind(this));
    },
    splitWork: function( data ){
        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            var d = {
                title : data.title,
                centerId : data.centerId,
                //centerTitle: data.centerTitle,
                parentWorkId : data.id,
                //parentWorkTitle : data.title,
                workType : data.workType,
                workLevel : data.workLevel,
                completeDateLimitStr : data.completeDateLimitStr,
                completeDateLimit : data.completeDateLimit,
                reportCycle: data.reportCycle,
                reportDayInCycle: data.reportDayInCycle
            };
            var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,d,{
                "isNew": false,
                "isEdited": true,
                "tabLocation": "myDo",
                "onPostSave" : function(){ this.reload(); }.bind(this),
                "onPostDeploy" : function(){ this.reload();}.bind(this)
            });
            workform.load();
        }.bind(this));
    },
    destroyWorkInfor:function(){
        if( this.inforNode ){
            this.inforNode.destroy();
            this.inforNode = null;
        }
        if( this.tooltip ){
            this.tooltip.destroy();
            this.tooltip = null;
        }
    },
    showWorkInfor: function( data, c ){
        this.destroyWorkInfor();
        this.createInforNode( data, c);
    },
    setInforNodeCoondinates: function( inforNode, targetCoondinates ){
        var containerScroll = this.formTableContainer.getScroll();
        var containerSize = this.formTableContainer.getSize();
        var nodeSize = inforNode.getSize();
        var left;
        var arrowX, arrowY;
        if( targetCoondinates.left + 10 + nodeSize.x - containerScroll.x > containerSize.x ){
            left = targetCoondinates.left + 50 - nodeSize.x;
            arrowX = "right";
        }else{
            left = targetCoondinates.left + 10;
            arrowX = "left";
        }
        var top;
        if( targetCoondinates.top + 57 + nodeSize.y - containerScroll.y > containerSize.y ){
            top = targetCoondinates.top - nodeSize.y - 10;
            arrowY = "bottom";
        }else{
            top = targetCoondinates.top + targetCoondinates.height + 10;
            arrowY = "top";
        }
        inforNode.setStyles({
            "left" : left,
            "top" : top
        });
        this.inforBoxArrow = new Element("div", {"styles":this.css.inforBoxArrow}).inject(this.inforNode);
        if( arrowY == "top" ){
            this.inforBoxArrow.setStyles({
                "top" : "-8px",
                "background-position" : "0px -18px"
            })
        }else{
            this.inforBoxArrow.setStyles({
                "bottom" : "-8px",
                "background-position" : "0px -28px"
            })
        }
        if( arrowX == "left" ){
            this.inforBoxArrow.setStyle( "left" , "52px")
        }else{
            this.inforBoxArrow.setStyle( "right" , "10px")
        }
    },
    createInforNode: function( data, c, callback){
        this.inforNode = new Element("div", {
            styles : {
                "font-size" : "12px",
                "position" : "absolute",
                "z-index" : "11",
                "background-color" : "#fff",
                "border" : "1px solid #aaa",
                "padding" : "10px",
                "border-radius" : "3px",
                "box-shadow": "0px 0px 5px #aaa"
            }
        }).inject( this.formTableArea );
        this.inforNode.set("html", data.isCenterWork ? this.getCenterWorkInforHtml( data ) : this.getSubWorkInforHtml( data ) );
        this.setInforNodeCoondinates( this.inforNode, c );
        if( callback )callback();
    },
    getSubWorkInforHtml : function(data){
        var titleStyle = "font-weight:bold;";
        var valueStyle = "";
        var lp = this.lp.workForm;
        return "<table width='300' bordr='0' cellpadding='3' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td style='"+titleStyle+"' colspan='2'>"+data.title+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"' width='60px'>"+"状态"+":</td>" +
            "   <td style='"+valueStyle+"' width='140px'>"+data.workProcessStatus+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"' width='60px'>"+lp.workType+":</td>" +
            "   <td style='"+valueStyle+"' width='140px'>"+data.workType+"</td>" +
            //"</tr><tr>" +
            //"   <td style='"+titleStyle+"'>"+lp.workLevel+":</td>" +
            //"   <td style='"+valueStyle+"'>"+data.workLevel+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.timeLimit+":</td>" +
            "   <td style='"+valueStyle+"'>"+data.completeDateLimitStr+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.dutyDepartment+":</td>" +
            "   <td style='"+valueStyle+"'>"+data.responsibilityOrganizationName+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.dutyPerson+":</td>" +
            "   <td style='"+valueStyle+"'>"+data.responsibilityEmployeeName+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.secondDepartment+":</td>" +
            "   <td style='"+valueStyle+"'>"+data.cooperateOrganizationName+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.secondPerson+":</td>" +
            "   <td style='"+valueStyle+"'>"+data.cooperateEmployeeName+"</td>" +
            "</tr>" +
            "</table>"
    },
    getCenterWorkInforHtml: function(data){
        var titleStyle = "font-weight:bold;";
        var valueStyle = "";
        var lp = this.lp;
        var description = data.description.length > 50 ? data.description.substring(0,50) + "..." : data.description;
        return "<table width='300' bordr='0' cellpadding='3' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td style='"+titleStyle+"' colspan='2'>"+data.title+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"' width='60px'>"+"状态"+":</td>" +
            "   <td style='"+valueStyle+"' width='140px'>"+data.processStatus+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.workForm.workType+":</td>" +
            "   <td style='"+valueStyle+"'>"+data.defaultWorkType+"</td>" +
            //"</tr><tr>" +
            //"   <td style='"+titleStyle+"'>"+lp.workForm.workLevel+":</td>" +
            //"   <td style='"+valueStyle+"'>"+data.defaultWorkLevel+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.workForm.timeLimit+":</td>" +
            "   <td style='"+valueStyle+"'>"+data.defaultCompleteDateLimitStr+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.baseWorkView.deployerName+":</td>" +
            "   <td style='"+valueStyle+"'>"+data.deployerIdentity+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.WorkDeploy.draftDate+":</td>" +
            "   <td style='"+valueStyle+"'>"+data.createTime+"</td>" +
            "</tr><tr>" +
            "   <td style='"+titleStyle+"'>"+lp.description+":</td>" +
            "   <td style='"+valueStyle+"'>"+ description+"</td>" +
            "</tr>" +
            "</table>";
    },
    setFormNodeSize: function (width, height, top, left) {
        if (!width)width = this.options.width ? this.options.width : "50%";
        if (!height)height = this.options.height ? this.options.height : "50%";
        if (!top) top = this.options.top ? this.options.top : 0;
        if (!left) left = this.options.left ? this.options.left : 0;

        //var appTitleSize = this.app.window.title.getSize();

        var allSize = this.app.content.getSize();

        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);

        top = top || parseInt((limitHeight - height) / 2, 10); //+appTitleSize.y);
        left = left || parseInt((limitWidth - width) / 2, 10);

        this.formAreaNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });

        this.formNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.formIconNode ? this.formIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y;
        //var formMargin = formHeight -iconSize.y;
        this.formContentNode.setStyles({
            "height": "" + contentHeight + "px"
        });
        this.formTableContainer.setStyles({
            "width": "" + (width) + "px",
            "height": "" + contentHeight + "px"
        });
    }
});

MWF.xApplication.Execution.WorkMinder.WorkDeploy = new Class({
    Extends: MWF.xApplication.Execution.WorkDeploy,
    deploy: function(){

        var ids = [];
        this.actions.getUserDeployBaseWork( this.centerWorkId, function(json){
            if(this.centerWorkInforData){
                if(this.centerWorkInforData.processStatus == this.lp.statusDraft){  //中心工作草稿环节，
                    json.data.each(function(d){
                        if(d.workProcessStatus == this.lp.statusDraft){
                            ids.push(d.id)
                        }
                    }.bind(this))
                }else{  //其他环节，其他环节也有可能拟稿人追加
                    json.data.each(function( d ){
                        //if( d.subWrapOutOkrWorkBaseInfos ){
                        //    d.subWrapOutOkrWorkBaseInfos.each(function( infor ){
                        if( d.subWorks ){
                            d.subWorks.each(function( infor ){
                                if( infor.workProcessStatus == this.lp.statusDraft ){
                                    ids.push( infor.id )
                                }
                            }.bind(this))
                        }
                        if(d.workProcessStatus == this.lp.statusDraft){
                            ids.push(d.id)
                        }
                    }.bind(this))
                }
            }

            if( ids.length > 0 ){
                var data = {  "workIds":ids };
                this.actions.deployBaseWork( data, function( j ){
                    if(j.type && j.type == "success"){
                        this.app.notice(this.lp.deployeSuccess, "ok");
                        //this.reloadContent();
                        this.close();
                        this.explorer.reload();
                    }else{
                        this.app.notice(j.data.message, "error")
                    }
                }.bind(this));
            }else{
                this.app.notice(this.lp.noWordNeedDeployed, "ok");
            }
        }.bind(this));
    }
});