MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.TaskAttachmentList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (explorer, container,  data, options) {
        this.setOptions(options);
        this.container = container;
        this.explorer = explorer;

        this.app = explorer.app;
        this.lp = this.app.lp.taskAttachmentList;
        this.actions = this.app.restActions;

        this.path = "/x_component_TeamWork/$TaskAttachmentList/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.data = data;
    },
    load: function () {
        this.container.empty();
        this.titleContent = new Element("div.titleContent",{styles:this.css.titleContent}).inject(this.container);
        this.titleIcon = new Element("div.titleIcon",{styles:this.css.titleIcon}).inject(this.titleContent);
        this.titleText = new Element("div.titleText",{styles:this.css.titleText,text:this.lp.attachment}).inject(this.titleContent);

        this.valueContent = new Element("div.valueContent",{styles:this.css.valueContent}).inject(this.container);

        this.attachmentListContainer = new Element("div.attachmentListContainer",{styles:this.css.attachmentListContainer}).inject(this.valueContent);
        this.listAttachment( function( json ){
            json.data.each(function (att) {
                this.loadAttachmentItem(att);
            }.bind(this));
        }.bind(this));


        this.addContainer = new Element("div.addContainer",{styles:this.css.addContainer}).inject(this.valueContent);
        this.addIcon = new Element("div.addIcon",{styles:this.css.addIcon}).inject(this.addContainer);
        this.addText = new Element("div.addText",{styles:this.css.addText,text:this.lp.attachmentAdd}).inject(this.addContainer);
        this.addContainer.addEvents({
            click:function(){
                this.uploadAttachment()
            }.bind(this)
        });

    },
    loadAttachmentItem:function(att){
        var attachmentItem = new Element("div.attachmentItem",{styles:this.css.attachmentItem}).inject(this.attachmentListContainer);
        attachmentItem.set("id",att.id);
        attachmentItem.addEvents({
            mouseover:function(){
                this.setStyles({"background-color":"#f5f5f5","cursor":"pointer"});
            },
            mouseout:function(){
                this.setStyles({"background-color":"","cursor":"default"});
            },
            click:function(){
                this.downloadAttachment(att)
            }.bind(this)
        });
        var attachmentIcon = new Element("div.attachmentIcon",{styles:this.css.attachmentIcon}).inject(attachmentItem);
        var bgicon = "url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_unknow.png)";
        var ex = att.extension.toLowerCase();
        if(ex == "zip" || ex == "rar" || ex == "7z"){
            bgicon = "url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_zip.png)"
        }else if(ex == "xls" || ex == "xlsx"){
            bgicon = "url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_excel.png)"
        }else if(ex == "doc" || ex == "docx"){
            bgicon = "url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_word.png)"
        }else if(ex == "ppt" || ex == "pptx"){
            bgicon = "url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_ppt.png)"
        }else if(ex == "pdf"){
            bgicon = "url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_pdf.png)"
        }else if(ex == "png" || ex == "jpg" || ex == "jpeg" || ex == "bmp" || ex == "gif"){
            bgicon = "url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_image.png)"
        }else if(ex == "mp3"){
            bgicon = "url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_mp3.png)"
        }else if(ex == "mp4"){
            bgicon = "url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_mp4.png)"
        }
        attachmentIcon.setStyles({"background-image":bgicon});
        var attachmentName = new Element("div.attachmentName",{styles:this.css.attachmentName,text:att.name}).inject(attachmentItem);
        var attachmentSize = new Element("div.attachmentSize",{styles:this.css.attachmentSize}).inject(attachmentItem);
        var size = att.length.toFloat();
        if(size<1024) size = "1k"
        else if(size<1024*1024) size = Math.floor(size/1024) + "K"
        else if(size<1024*1024*1024) size = Math.floor(size/1024/1024) + "MB"
        attachmentSize.set("text",size)
        var attachmentMore = new Element("div.attachmentMore",{styles:this.css.attachmentMore}).inject(attachmentItem);
        attachmentMore.addEvents({
            mouseover:function(){
                this.setStyles({"background-color":"#ecf6fe","background-image":"url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_down_click.png)"})
            },
            mouseout:function(){
                this.setStyles({"background-color":"","background-image":"url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_down.png)"})
            },
            click:function(e){
                var data = {
                    explorer:this
                };
                data.data = att;
                var tm = new MWF.xApplication.TeamWork.TaskAttachmentList.More(this.app.content, attachmentMore, this.app, data, {
                    css:this.css, lp:this.lp, axis : "y",
                    position : { //node 固定的位置
                        x : "auto",
                        y : "middle"
                    },
                    nodeStyles : {
                        "min-width":"150px",
                        "padding":"2px",
                        "border-radius":"5px",
                        "box-shadow":"0px 0px 4px 0px #999999",
                        "z-index" : "201"
                    },
                    onPostLoad:function(){
                        tm.node.setStyles({"opacity":"0","top":(tm.node.getStyle("top").toInt()+4)+"px"});
                        var fx = new Fx.Tween(tm.node,{duration:400});
                        fx.start(["opacity"] ,"0", "1");
                    },
                    onClose:function(rd){
                        if(!rd) return;
                        if(rd.act == "remove"){
                            this.close(rd);
                            if(this.data.projectObj){ //reload project
                                this.data.projectObj.createTaskGroup()
                            }
                        }
                    }.bind(this)
                },null,this);
                tm.load();
                e.stopPropagation()
            }.bind(this)
        })
    },
    createUploadFileNode: function () {
        this.uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" multiple/>";
        this.uploadFileAreaNode.set("html", html);

        this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        this.fileUploadNode.addEvent("change", function () {
            this.isQueryUploadSuccess = true;
            this.fireEvent( "queryUploadAttachment" );
            if( this.isQueryUploadSuccess ){
                var files = this.fileUploadNode.files;
                if (files.length) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);
                        var formData = new FormData();
                        formData.append('file', file);

                        formData.append('site', this.data.id);
                        this.actions.attachmentTaskUpload(this.data.id, function (json) {
                            if(json.type=="success"){
                                if(json.id){
                                    this.actions.attachmentGet(json.id,  function (json) {
                                        json = this.transportData(json);
                                        if (json.data) {
                                            //this.load();
                                            this.getAttachment(json.data.id,function(json){
                                                //alert(JSON.stringify(json))
                                                this.loadAttachmentItem(json)
                                            }.bind(this))
                                        }

                                        this.fireEvent("upload", [json.data]);
                                    }.bind(this))
                                }
                                if(json.data.dynamics){
                                    json.data.dynamics.each(function(dd){
                                        this.explorer.loadDynamicItem(dd,"bottom");
                                    }.bind(this));
                                    this.explorer.dynamicContent.scrollTo(0,this.explorer.dynamicContent.getScrollSize().y);
                                }
                            }
                            this.uploadFileAreaNode.destroy();
                            this.uploadFileAreaNode = false;
                            //this.attachmentController.checkActions();
                        }.bind(this), null, formData, file,this.data.id);

                    }
                }
            }else{
                this.uploadFileAreaNode.destroy();
                this.uploadFileAreaNode = false;
            }
        }.bind(this));
    },
    uploadAttachment: function (e, node) {
        if (!this.uploadFileAreaNode) {
            this.createUploadFileNode();
        }
        this.fileUploadNode.click();
    },
    transportData : function( json ){
        if( typeOf(json.data) == "array" ){
            json.data.each(function(d){
                d.person = d.creatorUid;
                d.lastUpdateTime = d.updateTime;
            })
        }else if( typeOf(json.data) == "object" ){
            var d = json.data;
            d.person = d.creatorUid;
            d.lastUpdateTime = d.updateTime;
        }else{
            json.each(function(d){
                d.person = d.creatorUid;
                d.lastUpdateTime = d.updateTime;
            })
        }
        return json;
    },
    listAttachment: function( callback ){
        this.actions.attachmentListByTaskId(this.data.id, function(json){
            if(callback)callback(this.transportData(json));
        }.bind(this))
    },
    replaceAttachmentFile: function (attachment) {
        if (!this.replaceFileAreaNode) {
            this.createReplaceFileNode(attachment);
        }
        this.fileReplaceNode.click();
    },
    downloadAttachments: function (e, node, attachments) {
        attachments.each(function (att) {
            this.actions.attachmentDownloadStream(att.data.id, this.data.id);
        }.bind(this));
    },
    downloadAttachment: function (attachment) {
        this.actions.attachmentDownloadStream(attachment.id, this.data.id);
    },
    openAttachment: function (e, node, attachments) {
        attachments.each(function (att) {
            this.actions.attachmentDownloadStream(att.data.id, this.data.id);
        }.bind(this));
    },
    getAttachmentUrl: function (attachment, callback) {
        this.actions.attachmentDownloadUrl(attachment.id, this.data.id, callback);
    },
    getAttachment:function(id,callback){
       this.actions.attachmentGet(id,function(json){
            if(json.type == "success"){
                if(callback)callback(json.data)
            }
        }.bind(this))
    },
    reload:function(){

    },
    test:function(){

    }

});

MWF.xApplication.TeamWork.TaskAttachmentList.More = new Class({
    Extends: MWF.xApplication.TeamWork.Common.ToolTips,
    options : {
        // displayDelay : 300,
        hasArrow:false,
        event:"click"
    },
    _loadCustom : function( callback ){
        var _self = this;
        this.css = this.options.css;
        this.lp = this.options.lp;
        this.explorer = this.data.explorer;
        this.data = this.data.data;
        //this.data
        //this.contentNode

        var topMoreTitle = new Element("div.topMoreTitle",{styles:this.css.topMoreTitle,text:this.lp.attachmentMenu}).inject(this.contentNode);

        // var details = new Element("div.details",{styles:this.css.topMoreItem}).inject(this.contentNode);
        // details.addEvents({
        //     mouseenter:function(){this.setStyles({"background-color":"#F7F7F7"})},
        //     mouseleave:function(){this.setStyles({"background-color":""})}
        // });
        // var detailsIcon = new Element("div.detailsIcon",{styles:this.css.topMoreItemIcon}).inject(details);
        // detailsIcon.setStyles({"background":"url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_details.png) no-repeat center"});
        // var detailsText = new Element("div.detailsText",{styles:this.css.topMoreItemText,text:this.lp.attachmentDetails}).inject(details);

        var download = new Element("div.download",{styles:this.css.topMoreItem}).inject(this.contentNode);
        download.addEvents({
            mouseenter:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseleave:function(){this.setStyles({"background-color":""})},
            click:function(){
                this.downloadAttachment(this.data)
            }.bind(this)
        });
        var downloadIcon = new Element("div.downloadIcon",{styles:this.css.topMoreItemIcon}).inject(download);
        downloadIcon.setStyles({"background":"url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_download.png) no-repeat center"});
        var downloadText = new Element("div.downloadText",{styles:this.css.topMoreItemText,text:this.lp.attachmentDownload}).inject(download);

        var remove = new Element("div.remove",{styles:this.css.topMoreItem}).inject(this.contentNode);
        remove.addEvents({
            mouseenter:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseleave:function(){this.setStyles({"background-color":""})},
            click:function(e){
                this.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                    _self.deleteAttachment(_self.data);
                    this.close();
                },function(){
                    this.close();
                });
            }.bind(this)
        });
        var removeIcon = new Element("div.removeIcon",{styles:this.css.topMoreItemIcon}).inject(remove);
        removeIcon.setStyles({"background":"url(/x_component_TeamWork/$TaskAttachmentList/default/icon/icon_remove.png) no-repeat center"});
        var removeText = new Element("div.removeText",{styles:this.css.topMoreItemText,text:this.lp.attachmentRemove}).inject(remove);

        if(callback)callback();
    },
    downloadAttachment: function (attachment) {
        this.actions.attachmentDownloadStream(attachment.id, attachment.site);
    },
    deleteAttachment: function (attachment) {
        this.actions.attachmentRemove(attachment.id, function (json) {
            if(json.type == "success"){
                var dom = this.explorer.container.getElementById(json.data.id);
                if(dom) dom.destroy();
            }

            if(json.data.dynamics){
                json.data.dynamics.each(function(data){
                    this.explorer.explorer.loadDynamicItem(data,"bottom");
                    this.explorer.explorer.dynamicContent.scrollTo(0,this.explorer.explorer.dynamicContent.getScrollSize().y);
                }.bind(this))
            }

            this.close();
        }.bind(this));
    }
});