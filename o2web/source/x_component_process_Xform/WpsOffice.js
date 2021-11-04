MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.WpsOffice = MWF.APPWpsOffice =  new Class({
    Extends: MWF.APP$Module,
    isActive: false,
    options:{
        "version": "v1.1.8",
        "moduleEvents": ["beforeOpen",
            "afterOpen",
            "afterSave"
        ]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.documentId = "";
        this.mode = "write";
    },
    _loadUserInterface: function(){
        this.node.empty();
        this.node.setStyles({
            "min-height": "700px"
        });
    },
    _afterLoaded: function(){
        if(!layout.serviceAddressList["x_wps_assemble_control"]){
            this.node.set("html","<h3><font color=red>请先安装wps应用</font></h3>");
            return false;
        }
        this.action = o2.Actions.load("x_wps_assemble_control");
        if (!this.json.isNotLoadNow){
            this.data = this.getData();
            if(this.data.documentId === ""){
                this.createDocument(function (){
                    this.loadDocument();
                }.bind(this));
            }else {
                this.documentId = this.data.documentId;
                this.loadDocument();
            }
        }
    },
    createDocument : function (callback){
        this.action.CustomAction.createFileBlank(this.json.officeType,{}, function( json ){
            this.documentId = json.data.docId;
            this.setData();
            if (callback) callback();
        }.bind(this),null, false);
    },
    loadDocument: function () {
        this.getEditor(function () {
            this.loadApi(function (){
                this.loadEditor();
            }.bind(this));
        }.bind(this));
    },
    loadApi : function (callback){
        o2.load(["../o2_lib/wps/polyfill.min.js","../o2_lib/wps/web-office-sdk.umd.js"], {"sequence": true}, function () {
            if (callback) callback();
        }.bind(this));
    },
    getEditor: function (callback) {
        if (this.readonly || this.json.isReadonly){
            this.mode  = "read";
        }else{
            if (this.json.readScript && this.json.readScript.code){
                var flag = this.form.Macro.exec(this.json.readScript.code, this);
                if (flag){
                    this.mode = "read";
                }
            }
        }
        this.action.CustomAction.getFileUrl(this.documentId,{"permission":this.mode} ,function( json ){
            this.wpsUrl = json.data.wpsUrl;
            this.wpsToken = json.data.token;
            if (callback) callback();
        }.bind(this),null,false);
    },
    loadEditor : function (){

        this.fireEvent("beforeOpen");

        this.wpsOffice = WebOfficeSDK.config({
            url : this.wpsUrl,
            mount: this.node,
            mode : this.json.showMode,
            cooperUserAttribute: {
                isCooperUsersAvatarVisible: this.json.isCooperUsersAvatarVisible //是否显示协作用户头像
            },
            // 通用选项，所有类型文档适用
            commonOptions: {
                isShowTopArea: this.json.isShowTopArea, // 隐藏顶部区域(头部和工具栏)
                isShowHeader: this.json.isShowHeader, // 隐藏头部区域
                isBrowserViewFullscreen : this.json.isBrowserViewFullscreen,//是否在浏览器区域全屏
                isIframeViewFullscreen : this.json.isIframeViewFullscreen//是否在iframe区域内全屏
            },
            wordOptions : {
                isShowDocMap : this.json.isShowDocMap,//是否开启目录功能，默认开启
                isBestScale : this.json.isBestScale,//打开文档时，默认以最佳比例显示(适用于pc)
                isShowBottomStatusBar : this.json.isShowBottomStatusBar,//pc-是否展示底部状态栏
                "mobile.isOpenIntoEdit" : this.json.isOpenIntoEdit//mobile-要有编辑权限，移动端打开时是否进入编辑
            },
            commandBars: [
                {
                    cmbId: "HeaderLeft",
                    attributes: {
                        visible: this.json.HeaderLeft,
                        enable: this.json.HeaderLeft
                    }
                },
                {
                    cmbId: "HeaderRight",
                    attributes: {
                        visible: this.json.HeaderRight,
                        enable: this.json.HeaderRight
                    }
                },
                {
                    cmbId: "FloatQuickHelp",
                    attributes: {
                        visible: this.json.FloatQuickHelp,
                        enable: this.json.FloatQuickHelp
                    }
                },
                {
                    cmbId: "HistoryVersion",
                    attributes: {
                        visible: this.json.HistoryVersion,
                        enable: this.json.HistoryVersion
                    }
                },
                {
                    cmbId: "HistoryRecord",
                    attributes: {
                        visible: this.json.HistoryRecord,
                        enable: this.json.HistoryRecord
                    }
                },
                {
                    cmbId: "HistoryVersionDivider",
                    attributes: {
                        visible: this.json.HistoryVersionDivider,
                        enable: this.json.HistoryVersionDivider
                    }
                },
                {
                    cmbId: "Logo",
                    attributes: {
                        visible: this.json.Logo,
                        enable: this.json.Logo
                    }
                },
                {
                    cmbId: "Cooperation",
                    attributes: {
                        visible: this.json.Cooperation,
                        enable: this.json.Cooperation
                    }
                },
                {
                    cmbId: "More",
                    attributes: {
                        visible: this.json.More,
                        enable: this.json.More
                    }
                },
                {
                    cmbId: "SendButton",
                    attributes: {
                        visible: this.json.SendButton,
                        enable: this.json.SendButton
                    }
                },
                {
                    cmbId: "CooperHistoryMenuItem",
                    attributes: {
                        visible: this.json.CooperHistoryMenuItem,
                        enable: this.json.CooperHistoryMenuItem
                    }
                },
                {
                    cmbId: "TabPrintPreview",
                    attributes: {
                        visible: this.json.TabPrintPreview,
                        enable: this.json.TabPrintPreview
                    }
                },
                {
                    cmbId: "MenuPrintPreview",
                    attributes: {
                        visible: this.json.MenuPrintPreview,
                        enable: this.json.MenuPrintPreview
                    }
                },
                {
                    cmbId: "ReviewTrackChanges",
                    attributes: {
                        visible: this.json.ReviewTrackChanges,
                        enable: this.json.ReviewTrackChanges
                    }
                },
                {
                    cmbId: "TrackChanges",
                    attributes: {
                        visible: this.json.TrackChanges,
                        enable: this.json.TrackChanges
                    }
                },
                {
                    cmbId: "ContextMenuConvene",
                    attributes: {
                        visible: this.json.ContextMenuConvene,
                        enable: this.json.ContextMenuConvene
                    }
                },
                {
                    cmbId: "WriterHoverToolbars",
                    attributes: {
                        visible: this.json.WriterHoverToolbars,
                        enable: this.json.WriterHoverToolbars
                    }
                },
                {
                    cmbId: "ReadSetting",
                    attributes: {
                        visible: this.json.ReadSetting,
                        enable: this.json.ReadSetting
                    }
                }
            ]

        });
        var token = this.wpsToken;
        // 设置token
        // this.wpsOffice.setToken({
        //     token: token,
        //     timeout: 10000 // token超时时间, 可配合refreshToken配置函数使用，当超时前将调用refreshToken
        // })
        // 打开文档结果
        this.wpsOffice.on('fileOpen', function(result) {
            this.fireEvent("afterOpen");
        }.bind(this));
        this.wpsOffice.on('fileStatus', function(result) {
            if(result.status === 7){
                this.fireEvent("afterSave");
            }
        }.bind(this));

        this.wpsOffice.on('fullscreenChange', function(result) {
            console.log(JSON.stringify(result))
        });
        this.wpsOffice.on('previewLimit', function(result) {
            console.log(JSON.stringify(result))
        });
        this.wpsOffice.on('tabSwitch', function(result) {
            console.log(JSON.stringify(result))
        });
        this.wpsOffice.on('error', function(result) {
            console.log(JSON.stringify(result))
        });
        //是否显示评论
        if(this.json.isShowComment){
            this.showComments();
        }else {
            this.hideComments();
        }
    },
    hide: function(){
        this.node.hide();
    },
    show: function(){
        this.node.show();
    },
    isEmpty : function(){
        var data = this.getData();
        if(data.documentId === ""){
            return true;
        }else {
            return false;
        }
    },
    getData: function(){
        var data = {
            "documentId" : ""
        };
        if(this.form.businessData.data[this.json.id]){
            data.documentId = this.form.businessData.data[this.json.id].documentId;
        }
        return data;
    },
    setData: function(){
        var data = {
            "documentId" : this.documentId
        };
        this._setBusinessData(data);
    },
    save: function(){
        var promise =  this.wpsOffice.save();
        promise.then(function(){
            console.log("save success");
        });
    },
    getTotalPage :  function (callback){
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.WordApplication()
            var p2 = app.ActiveDocument.Range.Information(app.Enum.WdInformation.wdNumberOfPagesInDocument);
            p2.then(function(totalPages){
                if(totalPages.End){
                    if(callback) callback(totalPages.PagesCount);
                }else {
                    promise();
                }
            }.bind(this));
        }.bind(this));
    },
    getCurrentPage:  function (callback){
        var p1 = this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.Selection.Information(app.Enum.WdInformation.wdActiveEndPageNumber);
            p2.then(function(currentPage){
                if(callback) callback(currentPage);
            });
        }.bind(this));
    },
    gotoPage : function (page){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.Selection.GoTo({
                What: app.Enum.WdGoToItem.wdGoToPage,
                Which: app.Enum.WdGoToDirection.wdGoToAbsolute,
                Count: page
            });
            p2.then(function(){

            })
        }.bind(this));
    },
    getAllBookmark : function (callback){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.Bookmarks.Json();
            p2.then(function(bookMarks){
                if(callback) callback(bookMarks);
            });
        }.bind(this));
    },
    getBookmarkText : function (name,callback) {
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.GetBookmarkText(name);
            p2.then(function(text){
                if(callback) callback(text);
            });
        }.bind(this));
    },
    gotoBookmark : function (name){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.Selection.GoTo({
                What: app.Enum.WdGoToItem.wdGoToBookmark,
                Which: app.Enum.WdGoToDirection.wdGoToAbsolute,
                Name: name
            })
            p2.then(function(){
            });
        }.bind(this));
    },
    setBookmarkText : function (name,value,callback){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.ReplaceBookmark([{name: name, type: 'text', value: value} ])
            p2.then(function(isReplaceSuccess){
                if(callback) callback(isReplaceSuccess);
            });
        }.bind(this));
    },
    replaceText : function (key,value,options){

        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.ReplaceText([{key: key, value: value}])
            p2.then(function(isReplaceSuccess){
                if(callback) callback(isReplaceSuccess);
            });
        }.bind(this));
    },
    startRevisions : function (){
        //开启修订模式
        var promise =  this.wpsOffice.save();
        promise.then(function(){
            const app = this.wpsOffice.WordApplication();
            app.ActiveDocument.TrackRevisions = true;
        }.bind(this));
    },
    stopRevisions : function (){
        //关闭修订模式
        var promise =  this.wpsOffice.ready();
        promise.then(function(){
            const app = this.wpsOffice.WordApplication();
            app.ActiveDocument.TrackRevisions = false;
        }.bind(this));
    },
    exportPDF : function (){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2;
            switch (this.json.officeType){
                case "word":
                    p2 = app.ActiveDocument.ExportAsFixedFormat();
                    break;
                case "excel":
                    p2 = app.ActiveWorkbook.ExportAsFixedFormat()
                    break;
                case "ppt":
                    p2 = app.ActivePresentation.ExportAsFixedFormat()
            }
            p2.then(function (pdfUrl){
                window.open(pdfUrl.url);
            });
        }.bind(this));
    },
    getContentControlsCount : function (callback){
        //获取内容控件
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.ContentControls.Count;
            p2.then(function (count){
                if(callback) callback(count);
            });
        }.bind(this));
    },
    getContentControl : function (pos,callback){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 =  app.ActiveDocument.ContentControls.Item(pos);
            p2.then(function (contentControl){
                var p3 = contentControl.Title;
                var p4 = contentControl.Tag;
                var p5 = contentControl.PlaceholderText;
                var content = {};
                p3.then(function (contentTitle){
                    content.contentTitle = contentTitle;
                    p4.then(function (contentTag){
                        content.contentTag = contentTag;
                        p5.then(function (contentPlaceholderText){
                            content.contentPlaceholderText = contentPlaceholderText;
                            if(callback) callback(content);
                        });
                    });
                });
            });
        }.bind(this));
    },
    setContentControl : function (pos,contentTitle,contentTag,contentPlaceholderText,callback){

        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 =  app.ActiveDocument.ContentControls.Item(pos);
            p2.then(function (contentControl){
                contentControl.Title = contentTitle
                contentControl.Tag = contentTag
                contentControl.SetPlaceholderText({Text: contentPlaceholderText})
                if(callback) callback();
            });
        }.bind(this));
    },
    getOperatorsInfo : function (callback){
        //获取文档权限信息
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 ;
            switch (this.json.officeType){
                case "word":
                    p2 =  app.ActiveDocument.GetOperatorsInfo();
                    break;
                case "excel":
                    p2 =  app.ActiveWorkbook.GetOperatorsInfo();
                    break;
                case "ppt":
                    p2 =  app.ActivePresentation.GetOperatorsInfo();
            }
            p2.then(function (operatorsInfo){
                if(callback) callback(operatorsInfo);
            });
        }.bind(this));
    },
    getZoom : function (callback){
        //获取文档缩放
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 ;
            switch (this.json.officeType){
                case "word":
                    p2 =  app.ActiveDocument.ActiveWindow.View.Zoom.Percentage
                    break;
                case "excel":
                    p2 =  app.ActiveWorkbook.ActiveSheetView.Zoom
                    break;
                case "ppt":
                    p2 =  app.ActivePresentation.View.Zoom
            }
            p2.then(function (zoom){
                if(callback) callback(zoom);
            });
        }.bind(this));
    },
    setZoom : function (zoom,callback){
        //缩放属性值在50%到 300%之间。
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.Application;
            switch (this.json.officeType){
                case "word":
                    app.ActiveDocument.ActiveWindow.View.Zoom.Percentage = zoom;

                    break;
                case "excel":
                    app.ActiveWorkbook.ActiveSheetView.Zoom = zoom;
                    break;
                case "ppt":
                    app.ActivePresentation.View.Zoom = zoom;
            }
            if(callback) callback();
        }.bind(this));
    },
    hasComments : function (callback){
        //是否有评论
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.Application;
            var p2;
            switch (this.json.officeType){
                case "word":
                    p2 =  app.ActiveDocument.HasComments();
                    break;
                case "excel":
                    p2 =  app.ActiveWorkbook.HasComments();
                    break;
                case "ppt":
                    p2 =  app.ActivePresentation.HasComments();
            }
            p2.then(function (hasComments){
                if(callback) callback(hasComments);
            })
        }.bind(this));
    },
    showComments : function (callback){

        var promise = this.wpsOffice.ready();
        promise.then(function (){
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.View.ShowComments = true;
            if(callback) callback();
        }.bind(this));

    },
    hideComments : function (callback){
        var promise = this.wpsOffice.ready();
        promise.then(function (){
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.View.ShowComments = false;
            if(callback) callback();
        }.bind(this));
    },
    getComments : function (callback){
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.Application;
            var p2 = app.ActiveDocument.GetComments({ Offset: 0, Limit: 2000 });
            p2.then(function (operatorsInfo){
                if(callback) callback(operatorsInfo);
            })
        }.bind(this));
    },
    showPageMode : function (callback){
        //页面模式
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.Application;
            var p2 = app.ActiveDocument.SwitchTypoMode(false);
            p2.then()
        }.bind(this));
    },
    showUnionPageMode : function (callback){
        //连页模式
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.Application;
            var p2 = app.ActiveDocument.SwitchTypoMode(true);
            p2.then()
        }.bind(this));
    },
    showFileName : function (callback){
        //显示连页模式下的文件名
        this.showUnionPageMode(function (){

            var p1 = this.wpsOffice.ready();
            p1.then(function (){
                const app = this.wpsOffice.Application;
                var p2 = app.ActiveDocument.SwitchFileName(true);
                p2.then()
            }.bind(this));

        }.bind(this))
    },
    hideFileName : function (callback){
        //显示连页模式下的文件名
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.Application;
            var p2 = app.ActiveDocument.SwitchFileName(false);
            p2.then()
        }.bind(this));
    },
    showDocumentMap : function (callback){
        // debugger
        //连页模式
        // var promise =  async () =>{
        //     await this.wpsOffice.ready();
        //     const app = this.wpsOffice.Application;
        //     app.ActiveDocument.ActiveWindow.DocumentMap = true
        //     if(callback) callback();
        // }
        // promise();

        var promise = new Promise(function(resolve, reject){
            this.wpsOffice.ready();
            resolve();
        }.bind(this));
        promise.then(function(){
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.DocumentMap = true;
            if(callback) callback();
        }.bind(this));
    },
    hideDocumentMap : function (callback){
        //连页模式
        var promise = new Promise(function(resolve, reject){
            this.wpsOffice.ready();
            resolve();
        }.bind(this));
        promise.then(function(){
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.DocumentMap = false;
            if(callback) callback();
        }.bind(this));
    },
});
