MWF.xApplication.Homepage.FileContent  = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent,
    Implements: [Options, Events],
    options: {
        "view": "fileContent.html"
    },
    load: function(){
        this.tabs = {};
        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp}, "module": this}, function(){
            this.initSize();
            this.loadMyFile(function(){
                this.fireEvent("load");
            }.bind(this));

            // //是否需要定时自动刷新 @todo
            // this.startProcessAction.addEvent("click", this.startProcess.bind(this));

            //this.moreInforAction.addEvent("click", this.moreInfor.bind(this));
        }.bind(this));
    },
    openFile: function(e){
        //应用市场中的云文件，门户id为95135369-ab35-47ba-a9ce-845f26ff9efb
        o2.Actions.load("x_portal_assemble_surface").PortalAction.get("95135369-ab35-47ba-a9ce-845f26ff9efb", function () {
            layout.openApplication(e, "portal.Portal", {
                portalId : "95135369-ab35-47ba-a9ce-845f26ff9efb"
            });
        }, function(){
            layout.openApplication(e, "File");
        })
    },
    setContentSize: function(){
        var total = this.container.getSize().y;
        var titleHeight = this.taskTitleNode.getSize().y+this.taskTitleNode.getEdgeHeight();
        var bottomHeight = this.pageAreaNode.getSize().y+this.pageAreaNode.getEdgeHeight();
        var thisHeight = this.itemContentNode.getEdgeHeight();
        var contentHeight = total-titleHeight-bottomHeight-thisHeight;
        this.itemContentNode.setStyle("height", ""+contentHeight+"px");
        this.contentHeight = contentHeight;
        //this.pageSize = (this.options.itemHeight/this.contentHeight).toInt();

        if (this.noItemNode){
            var m = (this.contentHeight- this.noItemNode.getSize().y)/2;
            this.noItemNode.setStyle("margin-top", ""+m+"px");
        }
    },

    loadMyFile: function(callback){
        this.loadFile(null, callback);
    },

    loadFile: function(e, callback){
        if (!this.isLoading) {
            if (!this.fileContentTab){
                this.fileContentTab = new MWF.xApplication.Homepage.FileContent.File(this, this.fileTab, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.fileContentTab.load();
            }
            this.currentTab = this.fileContentTab;
        }
    }

});

MWF.xApplication.Homepage.FileContent.File = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.Task,
    Implements: [Options, Events],
    options: {
        "itemHeight": 50,
        "type": "file"
    },
    getIconJson: function(callback){
        if (!this.iconJson){
            o2.JSON.get("../x_component_File/$Main/icon.json", function(iconJson){
                this.iconJson = iconJson;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    loadItemsRes: function(){
        var items = [];
        o2.Actions.load("x_file_assemble_control").Attachment2Action.listTop(function(json2){
            o2.Actions.load("x_file_assemble_control").AttachmentAction.listTop(function(json){
                if( json2.data && json2.data.length ){
                    json2.data.map(function (d) {
                        d.version = "v2"
                    });
                    items = items.concat( json2.data );
                }
                if( json.data && json.data.length ){
                    json.data.map(function (d) {
                        d.version = "v1"
                    });
                    items = items.concat( json.data );
                }
                if (items.length){
                    items.sort( function (a, b) {
                        return Date.parse( b.lastUpdateTime ) - Date.parse( a.lastUpdateTime )
                    });
                    this.getIconJson(function(){
                        this.loadItems(items);
                    }.bind(this));
                }else{
                    this.emptyLoadContent();
                }
                this.fireEvent("load");
            }.bind(this));
        }.bind(this));
        // o2.Actions.load("x_file_assemble_control").AttachmentAction.listTop(function(json){
        //     if (json.data && json.data.length){
        //         this.getIconJson(function(){
        //             this.loadItems(json.data);
        //         }.bind(this));
        //     }else{
        //          this.emptyLoadContent();
        //     }
        //     this.fireEvent("load");
        // }.bind(this));
    },
    emptyLoadContent: function(){
        this.container.empty();
        this.container.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        this.content.pageAreaNode.empty();
        //this.itemContentNode.addClass("o2_homepage_task_area_content_empty").addClass("icon_notask");
        this.content.noItemNode = new Element("div.o2_homepage_file_area_content_empty_node", {"text": this.app.lp.noFile}).inject(this.container);
        this.content.noItemNode.addEvent("click", function(e){
            // layout.openApplication(e, "File");
            this.content.openFile(e);
        }.bind(this));
        var m = (this.content.contentHeight- this.content.noItemNode.getSize().y)/2;
        this.content.noItemNode.setStyle("margin-top", ""+m+"px");
        this.content.isLoading = false;
    },

    loadItems: function(data){

        for (var i=0; i<Math.min(data.length, this.pageSize); i++){
            var d = data[i];
            this.loadItem(d, i);
        }
        this.endLoadContent();
    },
    loadItemRow: function(d){
        var row = new Element("div.o2_homepage_file_item_node").inject(this.container);

        var iconArea = new Element("div.o2_homepage_file_item_icon").inject(row);
        var actionArea = new Element("div.o2_homepage_file_item_action", {"title": this.app.lp.download}).inject(row);
        var titleArea = new Element("div.o2_homepage_file_item_title", {"text": d.name, "title": d.name}).inject(row);

        var imgName = this.iconJson[d.extension.toLowerCase()];
        if (!imgName) imgName = this.iconJson["unknow"];
        iconArea.setStyle("background-image", "url(../x_component_File/$Main/default/file/"+imgName+")");

        return row;
    },

    loadItem: function(d, i){
        var row = this.loadItemRow(d, i);

        var _self = this;
        row.store("data", d);
        row.addEvents({
            "mouseover": function(){
                this.addClass("mainColor_color").addClass("o2_homepage_task_item_row_over");
            },
            "mouseout": function(){
                this.removeClass("mainColor_color").removeClass("o2_homepage_task_item_row_over");
            }
        });
        row.getLast().addEvent("click", function(e){
            var d = this.getParent().retrieve("data");
            _self.openAttachment(d);
        });
        row.getLast().getPrevious().addEvent("click", function(e){
            var d = this.getParent().retrieve("data");
            _self.downloadAttachment(d);
        });
    },
    openAttachment: function(d){
        if( d.version === "v2" ){
            o2.Actions.get("x_file_assemble_control").getFileDownloadUrl2(d.id, function(url){
                window.open(o2.filterUrl(url));
            });
        }else{
            o2.Actions.get("x_file_assemble_control").getFileDownloadUrl(d.id, function(url){
                window.open(o2.filterUrl(url));
            });
        }
    },
    downloadAttachment: function(d){
        if( d.version === "v2" ) {
            o2.Actions.get("x_file_assemble_control").getFileUrl2(d.id, function (url) {
                window.open(o2.filterUrl(url));
            });
        }else{
            o2.Actions.get("x_file_assemble_control").getFileUrl(d.id, function (url) {
                window.open(o2.filterUrl(url));
            });
        }
    },
    open: function(e, d){
        layout.openApplication(e, "Meeting");
    }

});

// MWF.xApplication.Homepage.MeetingContent.Meeting = new Class({
//     Extends: MWF.xApplication.Homepage.MeetingContent.MeetingInvited,
//     Implements: [Options, Events],
//     options: {
//         "itemHeight": 80,
//         "type": "meetingInvited",
//         "month": 1
//     },
//     loadItemsRes: function(){
//         o2.Actions.load("x_meeting_assemble_control").MeetingAction.listComingMonth(this.options.month, function(json){
//             if (json.data && json.data.length){
//                 this.loadItems(json.data);
//             }else{
//                 this.emptyLoadContent();
//             }
//             this.fireEvent("load");
//         }.bind(this));
//     },
//     loadItemRow: function(d){
//         var row = new Element("div.o2_homepage_meeting_item_node").inject(this.container);
//
//         var actionArea = new Element("div.o2_homepage_meeting_item_action").inject(row);
//         var inforArea = new Element("div.o2_homepage_meeting_item_infor").inject(row);
//
//         var titleNode = new Element("div.o2_homepage_meeting_item_title", {"text": d.subject, "title": d.subject}).inject(inforArea);
//
//         var timeNode = new Element("div.o2_homepage_meeting_item_time").inject(inforArea);
//         var start = (new Date()).parse(d.startTime);
//         var completed = (new Date()).parse(d.completedTime);
//         var startStr = start.format("%Y-%m-%d %H:%M");
//         var completedStr = start.format("%H:%M");
//         timeNode.set("html", this.app.lp.meetingTime+": <span style='color: #999999'>"+startStr+" - "+completedStr+"<span>");
//
//         var locationNode = new Element("div.o2_homepage_meeting_item_location").inject(inforArea);
//         locationNode.set("html", this.app.lp.meetingLocation+": <span style='color: #999999'>"+d.woRoom.name+"<span>");
//
//
//         return row;
//     },
// });
