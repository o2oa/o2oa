MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class WpsOffice Wps组件。
 * @o2cn WpsOffice
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var wpsOffice = this.form.get("fieldId"); //获取组件
 * //方法2
 * var wpsOffice = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.WpsOffice = MWF.APPWpsOffice =  new Class(
    /** @lends MWF.xApplication.process.Xform.WpsOffice# */
    {
        Extends: MWF.APP$Module,
        isActive: false,
        options:{
            "version": "wpsWebOffice",
            /**
             * 文档打开前事件。
             * @since V8.0
             * @event MWF.xApplication.process.Xform.WpsOffice#beforeOpen
             */
            /**
             * 文档打开后事件。
             * @since V8.0
             * @event MWF.xApplication.process.Xform.WpsOffice#afterOpen
             */
            /**
             * 保存后事件。
             * @since V8.0
             * @event MWF.xApplication.process.Xform.WpsOffice#afterSave
             */
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

            this.officeType = {
                "docx" : "Writer",
                "doc" : "Writer",
                "xlsx" : "Spreadsheet",
                "xls" : "Spreadsheet",
                "pptx" : "Presentation",
                "ppt" : "Presentation",
                "pdf" : "Pdf",
                "ofd" : "Pdf"
            };

            this.appToken = "x_processplatform_assemble_surface";
            this.version = this.options.version;
        },
        _loadUserInterface: function(){
            this.node.empty();
            this.node.setStyles({
                "min-height": "700px"
            });
        },
        _afterLoaded: function(){
            if(!layout.serviceAddressList["x_wpsfile_assemble_control"]){
                this.node.set("html","<h3><font color=red>please install wps application</font></h3>");
                return false;
            }
            if(this.mode !== "read" && this.json.allowUpload){
                this.createUpload();
            }
            this.action = o2.Actions.load("x_wpsfile_assemble_control");
            if (!this.json.isNotLoadNow){
                this.data = this.getData();
                if(this.data.documentId === ""){

                    if (this.json.officeType === "other" && this.json.templateType === "script"){
                        this.json.template = this.form.Macro.exec(this.json.templeteScript.code, this);
                    }

                    this[this.json.officeType === "other"&&this.json.template !== ""? "createDocumentByTemplate":"createDocument"](function (){
                        this.loadDocument();
                    }.bind(this));



                }else {
                    this.documentId = this.data.documentId;
                    this.loadDocument();
                }
            }
        },
        createUpload : function (){


            this.uploadNode = new Element("div",{"style":"margin:10px;"}).inject(this.node);
            var uploadBtn = new Element("button",{"text":MWF.xApplication.process.Xform.LP.ofdview.upload,"style":"margin-left: 15px; color: rgb(255, 255, 255); cursor: pointer; height: 26px; line-height: 26px; padding: 0px 10px; min-width: 40px; background-color: rgb(74, 144, 226); border: 1px solid rgb(82, 139, 204); border-radius: 15px;"}).inject(this.uploadNode);
            uploadBtn.addEvent("click",function (){
                o2.require("o2.widget.Upload", null, false);
                var upload = new o2.widget.Upload(this.content, {
                    "action": o2.Actions.get(this.appToken).action,
                    "method": "uploadAttachment",
                    "accept" : ".docx,.xlsx,.pptx,.pdf,.ofd",
                    "parameter": {
                        "id" : this.form.businessData.work.id
                    },
                    "data":{
                    },
                    "onCompleted": function(data){

                        o2.Actions.load(this.appToken).AttachmentAction.delete(this.documentId,function( json ){
                        }.bind(this));

                        this.documentId = data.id;
                        this.reload();
                    }.bind(this)
                });

                upload.load();
            }.bind(this));

        },
        reload : function (){
            this.setData();
            this.node.empty();
            this.createUpload();
            this.loadDocument();
        },
        createDocumentByTemplate : function (callback){

            this.action.CustomAction.getInfo(this.json.template).then(function(json) {
                var data = {
                    "fileName": MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + json.data.extension,
                    "fileType": json.data.extension,
                    "appToken" : this.appToken,
                    "workId" : this.form.businessData.work.id,
                    "site" : "filetext",
                    "tempId": this.json.template
                };

                this.action.CustomAction.createForO2(data,
                    function( json ){
                        this.documentId = json.data.fileId;
                        this.setData();
                        if (callback) callback();
                    }.bind(this),null, false
                );

            }.bind(this))
        },
        createDocument : function (callback){
            var data = {
                "fileName" : MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + this.json.officeType,
                "appToken" : this.appToken,
                "workId" : this.form.businessData.work.id,
                "site" : "filetext"
            };
            this.action.CustomAction.createForO2(data,
                function( json ){
                    this.documentId = json.data.fileId;
                    this.setData();
                    if (callback) callback();
                }.bind(this),null, false
            );
        },
        loadDocument: function () {

            o2.Actions.load(this.appToken).AttachmentAction.getOnlineInfo(this.documentId, function( json ){

                this.documentData = json.data;

                this.fileName = this.documentData.name;
                this.extension = this.documentData.extension;

                this.getEditor(function () {
                    this.loadApi(function (){
                        this.loadEditor();
                    }.bind(this));
                }.bind(this));

            }.bind(this),null,false);

        },
        loadApi : function (callback){

            if(this.version === "wpsWebOffice"){
                o2.load(["../x_component_WpsOfficeEditor/web-office-sdk-solution-v2.0.2.umd.min.js"], {"sequence": true}, function () {
                    if (callback) callback();
                }.bind(this));
            }else {
                o2.load(["../x_component_WpsOfficeEditor/web-office-sdk-v1.1.19.umd.js"], {"sequence": true}, function () {
                    if (callback) callback();
                }.bind(this));
            }

        },
        getEditor: function (callback) {
            if (this.isReadonly()){
                this.mode  = "read";
            }else{
                if (this.json.readScript && this.json.readScript.code){
                    var flag = this.form.Macro.exec(this.json.readScript.code, this);
                    if (flag){
                        this.mode = "read";
                    }
                }
            }

            if(this.action.ConfigAction.getBaseConfig){
                this.action.ConfigAction.getBaseConfig(function( json ){
                    this.appId = json.data.appId;
                    this.version = json.data.version;
                    if (callback) callback();
                }.bind(this),null,false);
            }else {
                this.action.ConfigAction.getAppId(function( json ){
                    this.appId = json.data.value;
                    if (callback) callback();
                }.bind(this),null,false);

            }

        },

        loadEditor : function (){

            this.fireEvent("beforeOpen");

            if(this.wpsOffice) this.wpsOffice.destroy();

            this.officeNode = new Element("div#_" + this.documentId,{"style":"height:100%;overflow:hidden;min-height:700px"}).inject(this.node);

            var config = {

                mount: this.officeNode,
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

            };

            if(this.version === "wpsWebOffice"){
                config.officeType = WebOfficeSDK.OfficeType[this.officeType[this.extension.toLowerCase()]];
                config.appId = this.appId;
                config.fileId = this.documentId.replace(/-/g, "_");
                config.token = layout.session.token;
                config.customArgs = {
                    "appToken" : this.appToken,
                    "mode" : this.mode
                };
                this.wpsOffice = WebOfficeSDK.init(config);
            }else {
                this.action.CustomAction.getWpsFileUrl(this.documentId,{
                    "mode" : this.mode,
                    "appToken" : this.appToken
                },function( json ){
                    this.wpsUrl = json.data.wpsUrl;
                    config.url = this.wpsUrl;

                }.bind(this),null,false);
                // console.log(this.wpsUrl)
                this.wpsOffice = WebOfficeSDK.config(config);
                this.wpsOffice.setToken({
                    token: layout.session.token,
                    timeout: 100 * 60 * 1000 // token超时时间, 可配合refreshToken配置函数使用，当超时前将调用refreshToken
                });

            }

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
        setData: function() {
            var data = {
                "documentId": this.documentId,
                "appToken": this.appToken
            }
            this.data = data;
            this._setBusinessData(data);

            var jsonData = {}
            jsonData[this.json.id] = data;

            o2.Actions.load(this.appToken).DataAction.updateWithJob(this.form.businessData.work.job, jsonData, function (json) {
                data = json.data;
            })
        },
        /**
         * @summary 保存wps
         * @example
         * this.form.get("fieldId").save(callback)
         */
        save: function(callback){
            var promise =  this.wpsOffice.save();
            promise.then(function(){
                console.log("save success");
                if(callback) callback();
            });
        },
        /**
         * @summary 发送全局广播
         * @example
         * this.form.get("fieldId").sendBroadcast("测试")
         */
        sendBroadcast : async function (text){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const Public = await app.Public;
            const result = await Public.SendBroadcast({
                Data: { message: text }
            });
        },
        /**
         * @summary 获取word页面总数，
         * @return 返回是个Promise对象
         * @example
         * this.form.get("fieldId").getTotalPage()
         */
        getTotalPage : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const totalPages = await app.ActiveDocument.Range.Information(app.Enum.WdInformation.wdNumberOfPagesInDocument);

            console.log(totalPages)
            return totalPages;
        },
        /**
         * @summary 获取当前页
         * @example
         * this.form.get("fieldId").getCurrentPage()
         */
        getCurrentPage: async function (){
            // 获取当前页数
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const currentPage = await app.ActiveDocument.ActiveWindow.Selection.Information(
                app.Enum.WdInformation.wdActiveEndPageNumber
            )
            console.log(currentPage)
            return currentPage;
        },
        /**
         * @summary 跳转到指定页
         * @example
         * this.form.get("fieldId").gotoPage(page)
         */
        gotoPage : async function (page){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.Selection.GoTo({
                What: app.Enum.WdGoToItem.wdGoToPage,
                Which: app.Enum.WdGoToDirection.wdGoToAbsolute,
                Count: page
            });
        },
        /**
         * @summary 获取所有书签
         * @return 返回是个Promise对象
         * @example
         * this.form.get("fieldId").getAllBookmark()
         */
        getAllBookmark : async function (callback){
            this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const bookMarks =  await app.ActiveDocument.Bookmarks.Json();
            return bookMarks;
        },
        /**
         * @summary 获取书签值
         * @return 返回是个Promise对象
         * @example
         * this.form.get("fieldId").getBookmarkText()
         */
        getBookmarkText : async function (name) {

            this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const text =  await app.ActiveDocument.GetBookmarkText(name);

            return text;
        },
        /**
         * @summary 跳转到书签
         * @example
         * this.form.get("fieldId").gotoBookmark()
         */
        gotoBookmark : async function (name){
            this.wpsOffice.ready();
            const app = this.wpsOffice.Application;

            await app.ActiveDocument.Selection.GoTo({
                What: app.Enum.WdGoToItem.wdGoToBookmark,
                Which: app.Enum.WdGoToDirection.wdGoToAbsolute,
                Name: name
            });
        },
        /**
         * @summary 给书签赋值
         * @return 返回是个Promise对象
         * @example
         * this.form.get("fieldId").setBookmarkText(name,value)
         */
        setBookmarkText : async function (name,value){
            await this.wpsOffice.ready();

            const app = this.wpsOffice.Application;

            // 书签对象
            const bookmarks = await app.ActiveDocument.Bookmarks;
            // 替换书签内容
            const isReplaceSuccess = await bookmarks.ReplaceBookmark([
                {
                    name: name,
                    type: 'text',
                    value: value
                }
            ])
            return isReplaceSuccess;
        },
        /**
         * @summary 盖章
         * @return 返回是个Promise对象
         * @example
         * this.form.get("fieldId").setSeal(bookmark,img,left,top,width,height)
         */
        setSeal : async function (bookmark,img,left,top,width,height){
            await this.wpsOffice.ready();

            const app = this.wpsOffice.Application;

            //获取当前选区
            const selection = await app.ActiveDocument.ActiveWindow.Selection;
            // 跳转到指定的书签
            await app.ActiveDocument.ActiveWindow.Selection.GoTo(
                app.Enum.WdGoToItem.wdGoToBookmark, // 类型：Bookmark
                app.Enum.WdGoToDirection.wdGoToAbsolute, // 定位
                1, // 数量
                bookmark, // 书签名
            );

            // 获取图形对象
            const shapes = await app.ActiveDocument.Shapes;
            // 光标插入非嵌入式图片
            const shape = await shapes.AddPicture({
                FileName: img, // 图片地址
                LinkToFile: true,
                SaveWithDocument: true,
                Left: left, // 图片距离左边位置
                Top: top, // 图片距离顶部位置
                Width: width, // 图片宽度
                Height: height, // 图片高度
            });

            // 设置文字环绕模式为【衬于文字下方】
            shape.ZOrder(app.Enum.ZOrderCmd.sendBehindText);
        },

        /**
         * @summary 查找替换
         * @return 返回是个Promise对象
         * @example
         * this.form.get("fieldId").replaceText(key,value)
         */
        replaceText : async function (key,value,options){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const isReplaceSuccess = app.ActiveDocument.ReplaceText([{key: key, value: value}])
            return isReplaceSuccess;
        },
        /**
         * @summary 开启修订模式
         * @return 返回是个Promise对象
         * @example
         * this.form.get("fieldId").startRevisions()
         */
        startRevisions : function (){

            this.wpsOffice.ready().then(function(){
                const app = this.wpsOffice.Application;
                // 将当前文档的编辑状态切换成修订模式
                app.ActiveDocument.TrackRevisions = true;
            }.bind(this));
        },
        /**
         * @summary 关闭修订模式
         * @return 返回是个Promise对象
         * @example
         * this.form.get("fieldId").stopRevisions()
         */
        stopRevisions : function (){
            //关闭修订模式
            this.wpsOffice.ready().then(function(){
                const app = this.wpsOffice.Application;

                // 将当前文档的编辑状态切换成修订模式
                app.ActiveDocument.TrackRevisions = false;
            }.bind(this));
        },
        /**
         * @summary 接受所有修订
         * @example
         * this.form.get("fieldId").acceptAllRevisions()
         */
        acceptAllRevisions : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 获取修订对象
            const revisions = await app.ActiveDocument.Revisions;
            // 接受对指定文档的所有修订
            await revisions.AcceptAll();
        },
        /**
         * @summary 拒绝所有修订
         * @example
         * this.form.get("fieldId").rejectAllRevisions()
         */
        rejectAllRevisions : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 获取修订对象
            const revisions = await app.ActiveDocument.Revisions;
            // 拒绝对指定文档的所有修订
            await revisions.RejectAll();
        },
        /**
         * @summary 显示痕迹
         * @example
         * this.form.get("fieldId").showRevisions()
         */
        showRevisions : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 获取节对象
            const View = await app.ActiveDocument.ActiveWindow.View;
            View.RevisionsView = 0;
            // 设置修订状态为 显示标记的最终状态
            View.ShowRevisionsAndComments = true;

        },
        /**
         * @summary 隐藏痕迹
         * @example
         * this.form.get("fieldId").hideRevisions()
         */
        hideRevisions : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 获取节对象
            const View = await app.ActiveDocument.ActiveWindow.View;
            View.RevisionsView = 0;
            // 设置修订状态为 显示标记的最终状态
            View.ShowRevisionsAndComments = false;
        },
        /**
         * @summary 打印
         * @example
         * this.form.get("fieldId").print()
         */
        print : function (){
            this.wpsOffice.ready().then(function(){
                const app = this.wpsOffice.Application;
                // 页面定制对象：更多菜单
                app.CommandBars('TabPrintPreview').then(function(printMenu){
                    printMenu.Execute();
                });
            }.bind(this));
        },
        /**
         * @summary 导出pdf
         * @example
         * this.form.get("fieldId").exportPDF()
         */
        exportPDF : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            var pdfUrl;
            switch (this.json.officeType){
                case "docx":
                    pdfUrl = await app.ActiveDocument.ExportAsFixedFormat();
                    break;
                case "xlsx":
                    pdfUrl = await app.ActiveWorkbook.ExportAsFixedFormat()
                    break;
                case "pptx":
                    pdfUrl = await app.ActivePresentation.ExportAsFixedFormat()
            }

            window.open(pdfUrl.url);
        },
        /**
         * @summary 获取文档权限信息
         * @example
         * this.form.get("fieldId").getOperatorsInfo()
         */
        getOperatorsInfo : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            var operatorsInfo ;
            switch (this.json.officeType){
                case "docx":
                    operatorsInfo =  await app.ActiveDocument.GetOperatorsInfo();
                    break;
                case "xlsx":
                    operatorsInfo =  await app.ActiveWorkbook.GetOperatorsInfo();
                    break;
                case "pptx":
                    operatorsInfo =  await app.ActivePresentation.GetOperatorsInfo();
            }
            return operatorsInfo;
        },
        /**
         * @summary 获取内容控件个数
         * @return 返回是个Promise对象
         * @example
         * this.form.get("fieldId").getContentControlsCount()
         */
        getContentControlsCount : async function (){
            //获取内容控件
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 内容控件对象
            const contentControls = await app.ActiveDocument.ContentControls;
            // 内容控件数量
            const count = await contentControls.Count;
            return count;
        },
        /**
         * @summary 获取内容控件文本
         * @example
         * this.form.get("fieldId").getContentControlText()
         */
        getContentControlText : async function (pos){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const contentControls = await app.ActiveDocument.ContentControls;
            const contentControl = await contentControls.Item(pos);
            const range = await contentControl.Range;
            const text = range.Text;

            return text
            // range.Text = 'WebOffice'
        },
        /**
         * @summary 设置内容控件文本
         * @example
         * this.form.get("fieldId").setContentControlText()
         */
        setContentControlText : async function (pos,text){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const contentControls = await app.ActiveDocument.ContentControls;
            const contentControl = await contentControls.Item(pos);
            const range = await contentControl.Range;
            range.Text = text;

        },
        /**
         * @summary 获取文档缩放
         * @example
         * this.form.get("fieldId").getZoom()
         */
        getZoom : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            var zoom;
            switch (this.json.officeType){
                case "docx":
                    zoom =  await app.ActiveDocument.ActiveWindow.View.Zoom.Percentage
                    break;
                case "xlsx":
                    zoom =  await app.ActiveWorkbook.ActiveSheetView.Zoom
                    break;
                case "pptx":
                    zoom =  await app.ActivePresentation.View.Zoom
            }
            return zoom;
        },
        /**
         * @summary 设置文档缩放
         * @example
         * this.form.get("fieldId").setZoom()
         */
        setZoom : async function (zoom){
            //缩放属性值在50%到 300%之间。
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;

            switch (this.json.officeType){
                case "docx":
                    app.ActiveDocument.ActiveWindow.View.Zoom.Percentage = zoom;
                    break;
                case "xlsx":
                    app.ActiveWorkbook.ActiveSheetView.Zoom = zoom;
                    break;
                case "pptx":
                    app.ActivePresentation.View.Zoom = zoom;
            }
        },
        /**
         * @summary 是否有评论
         * @example
         * this.form.get("fieldId").hasComments()
         */
        hasComments : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            var hasComments;
            switch (this.json.officeType){
                case "docx":
                    hasComments = await app.ActiveDocument.HasComments();
                    break;
                case "xlsx":
                    hasComments = await app.ActiveWorkbook.HasComments();
                    break;
                case "pptx":
                    hasComments = await app.ActivePresentation.HasComments();
            }
            return hasComments;
        },
        /**
         * @summary 显示评论
         * @example
         * this.form.get("fieldId").showComments()
         */
        showComments : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.View.ShowComments = true;
        },
        /**
         * @summary 隐藏评论
         * @example
         * this.form.get("fieldId").hideComments()
         */
        hideComments : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.View.ShowComments = false;
        },
        /**
         * @summary 获取所有评论
         * @example
         * this.form.get("fieldId").hideComments()
         */
        getComments : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const operatorsInfo = await app.ActiveDocument.GetComments({ Offset: 0, Limit: 2000 });
            return operatorsInfo;
        },
        /**
         * @summary 页面模式
         * @example
         * this.form.get("fieldId").showPageMode()
         */
        showPageMode : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            app.ActiveDocument.SwitchTypoMode(false);
        },
        /**
         * @summary 连页模式
         * @example
         * this.form.get("fieldId").showPageMode()
         */
        showUnionPageMode : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            app.ActiveDocument.SwitchTypoMode(true);
        },
        /**
         * @summary 显示连页模式下的文件名
         * @example
         * this.form.get("fieldId").showFileName()
         */
        showFileName : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.SwitchTypoMode(true);
            await app.ActiveDocument.SwitchFileName(true);
        },
        /**
         * @summary 隐藏连页模式下的文件名
         * @example
         * this.form.get("fieldId").hideFileName()
         */
        hideFileName : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.SwitchTypoMode(true);
            await app.ActiveDocument.SwitchFileName(false);
        },
        /**
         * @summary 显示导航目录
         * @example
         * this.form.get("fieldId").showDocumentMap()
         */
        showDocumentMap : async function (callback){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.SwitchTypoMode(true);
            app.ActiveDocument.ActiveWindow.DocumentMap = true;

        },
        /**
         * @summary 隐藏导航目录
         * @example
         * this.form.get("fieldId").hideDocumentMap()
         */
        hideDocumentMap : async function (callback){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.SwitchTypoMode(true);
            app.ActiveDocument.ActiveWindow.DocumentMap = false;
        },
    });
