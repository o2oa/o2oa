MWF.xApplication.MinderEditor = MWF.xApplication.MinderEditor || {};
//MWF.xDesktop.requireApp("Minder", "Actions.RestActions", null, false);

MWF.xDesktop.requireApp("MinderEditor", "Tools", null, false);
MWF.xDesktop.requireApp("MinderEditor", "RuntimeInCommon", null, false);
MWF.xDesktop.requireApp("MinderEditor", "WidgetInCommon", null, false);
MWF.xDesktop.requireApp("MinderEditor", "Commands", null, false);
MWF.xDesktop.requireApp("MinderEditor", "LeftToolbar", null, false);
//脑图，数据样式如下：
//{
//    "root": {
//    "data": {
//        "text": "level_1"
//    },
//    "children": [
//        {
//            "data": {
//                "id": "blc4k0w350cg",
//                "created": 1525849642649,
//                "text": "level_2.1"
//            },
//            "children": []
//        },
//        {
//            "data": {
//                "id": "blc4k10mh0cg",
//                "created": 1525849642923,
//                "text": "level_2.2"
//            },
//            "children": []
//        }
//    ]
//},
//    "template": "structure",
//    "theme": "fresh-blue",
//    "version": "1.4.33"
//}
MWF.xApplication.MinderEditor.options = {
    multitask: true,
    executable: true
};
MWF.xApplication.MinderEditor.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "isEdited" : false,
        "style": "default",
        "name": "MinderEditor",
        "icon": "icon.png",
        "width": "1200",
        "height": "700",
        "isResize": false,
        "isMax": true,
        "title": MWF.xApplication.MinderEditor.LP.title,
        "align": "center",

        "folderId" : "root",
        "minderName" : "",
        "menuAction" : "",
        "id" : "",
        "defaultTheme" : "fresh-blue",
        "defaultTemplate" : "default",
        "isSetDataWhenExpand" : false,

        "leftToolbarEnable" : true,
        "notePreviewerEnable" : true,
        "tools" : {
            "top" : [ "menu", "|",  "save", "|", "undoredo", "|", "append", "|", "arrange", "|", "edit_remove", "|", "hyperLink", "image", "priority", "progress", "|", "style", "help" ],
            "left" : [ "zoom", "camera", "resetlayout", "move", "expandLevel", "selectAll", "preview", "template" , "theme", "search" ],
            "right" : [ "font", "resource", "note" ]
        },
        "disableTools" : [],
        "template" : [],
        "theme" : [],

        //dataMode参数，restful  通过MWF.xApplication.Minder.Actions.RestActions获取数据，
        //outer 打开应用的时候直接设置数据
        "dataMode" : "restful"


    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.MinderEditor.LP;
    },
    loadApplication: function (callback) {
        this.autoSaveInter = 3 * 60 * 1000;
        this.userName = layout.desktop.session.user.distinguishedName || layout.desktop.session.user.name;
        this.restActions = MWF.Actions.get("x_mind_assemble_control"); //new MWF.xApplication.Minder.Actions.RestActions();
        if( this.status ){
            this.options.isEdited = this.status.isEdited || false;
            this.options.isNew = this.status.isNew || false;
            this.options.dataMode = this.status.dataMode;
        }
        if( this.options.isEdited ){
            MWF.xDesktop.requireApp("MinderEditor", "RuntimeInEditMode", null, false);
            MWF.xDesktop.requireApp("MinderEditor", "WidgetInEditMode", null, false);
            MWF.xDesktop.requireApp("MinderEditor", "ToolbarInEditMode", null, false);
            MWF.xDesktop.requireApp("MinderEditor", "PopMenu", null, false);
        }else{
            MWF.xDesktop.requireApp("MinderEditor", "RuntimeInReadMode", null, false);
        }
        this.createNode();
        this.getData( function(){
            var name = this.data.name || this.options.title || "新建脑图";
            name = name.length > 30 ? name.substr(0,30) : name;
            this.setTitle( name );
            this.loadApplicationContent();
        }.bind(this));
        if( this.options.noticeText )this.notice( this.options.noticeText , "info");
    },
    getData : function( callback ){
        var id;
        if( this.options.dataMode == "outer" || (this.status && this.status.dataMode == "outer") ){
            if( this.status && this.status.data ){
                this.data = this.status.data
            }
        }else{
            if( this.status && this.status.id ){
                id = this.status.id;
            }else if( this.options.id ){
                id = this.options.id
            }else if( this.data && this.data.id ){
                id = this.data.id;
            }
        }
        if( id ) {
            this.restActions.getMind(id, function (json) {
                this.data = json.data;
                this.data.content = JSON.parse(this.data.content);
                if (callback)callback();
            }.bind(this))
        }else if( this.data ){
            if( this.data.content ){
                if( typeOf( this.data.content ) == "string" ){
                    this.data.content = JSON.parse(this.data.content);
                }
            }else{
                this.data.content = { data : {} };
            }
            if(callback)callback();
        }else{
            this.data = { content : { data : {} } };
            if(callback)callback();
        }
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div.node", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);

        this._createNode()
    },
    _createNode : function(){
        if( this.options.isEdited ){
            this.topToolbarNode = new Element("div.topToolbar").inject(this.node);
            this.topToolbarNode.setStyles( this.css.topToolbar );
        }

        this.contentNode = new Element("div.contentNode").inject(this.node);
        this.contentNode.classList.add("km-editor");

        if( this.options.isEdited ){
            this.rightToolbarNode = new Element("div.rightToolbar").inject( this.node );
            this.rightToolbarNode.setStyles( this.css.rightToolbar );
        }

        this.Content_Offset_Top = this.contentNode.getCoordinates( this.node).top;

        this.resizeContentFun = this.resizeContent.bind(this);
        this.addEvent("resize", this.resizeContentFun);
        this.resizeContent();
    },
    loadApplicationContent: function () {
        this.loadResource(function () {
            this.loadKityMinder(this.data.content);

            this.debug = new MWF.xApplication.MinderEditor.Debug(true);
            this.key = new MWF.xApplication.MinderEditor.Key();
            this.fsm = new MWF.xApplication.MinderEditor.FSM('normal');
            this.receiver = new MWF.xApplication.MinderEditor.Receiver(this);

            if( this.options.isEdited ){
                this.popmenu = new MWF.xApplication.MinderEditor.PopMenu(this.content, this, this.minder, this);
                this.input = new MWF.xApplication.MinderEditor.Input(this);
                if (this.minder.supportClipboardEvent && !kity.Browser.gecko) {
                    this.MimeType = new MWF.xApplication.MinderEditor.ClipboardMimeType();
                    this.clipboard = new MWF.xApplication.MinderEditor.Clipboard(this);
                }
                this.history = new MWF.xApplication.MinderEditor.History(this.minder);
                this.commands = new MWF.xApplication.MinderEditor.Commands( this );
                this.commands.load();

                this.topToolbar = new MWF.xApplication.MinderEditor.TopToolbar( this, this.topToolbarNode );
                this.topToolbar.load();

                this.rightToolbar = new MWF.xApplication.MinderEditor.RightToolbar( this, this.rightToolbarNode );
                this.rightToolbar.load();

                this.drag = new MWF.xApplication.MinderEditor.Drag(this);
                MWF.xApplication.MinderEditor.JumpingInEditMode(this);
                if( this.status && this.status.autoSave ){
                    this.startAutoSave();
                }
            }else{
                this.commands = new MWF.xApplication.MinderEditor.Commands( this );
                this.commands.load();

                this.drag = new MWF.xApplication.MinderEditor.Drag(this);
                MWF.xApplication.MinderEditor.JumpingInReadMode(this);
            }
            //this.loadNavi();

            if( this.options.notePreviewerEnable )new MWF.xApplication.MinderEditor.NotePrviewer( this );
            //this.attachEvent();
        }.bind(this));
    },
    openMainMenu : function( actionName ){
        var menuNode = this.topToolbar.getCommandNode("menu");
        menuNode.click();
        this.commands.mainMenu.show( actionName );
    },
    loadResource: function (callback) {
        var kityminderPath = "/o2_lib/kityminder/";

        COMMON.AjaxModule.loadCss("/x_component_MinderEditor/$Main/default/kityminder.editor.css", function () {
            COMMON.AjaxModule.loadCss(kityminderPath + "core/src/kityminder.css", function () {
                COMMON.AjaxModule.load("kity", function () {
                    COMMON.AjaxModule.load("kityminder", function () {
                        if (callback)callback();
                    }.bind(this));
                }.bind(this))
            }.bind(this))
        }.bind(this))
    },
    loadExtentResource : function (callback) {
        var kityminderPath = "/o2_lib/kityminder/";
        COMMON.AjaxModule.load("/o2_lib/jquery/jquery-2.2.4.min.js", function () {
            COMMON.AjaxModule.load(kityminderPath + "core/dist/kityminder.core.extend.js", function () {
                var jquery = jQuery.noConflict();
                if (callback)callback();
            }.bind(this));
        }.bind(this))
    },
    loadKityMinder: function (data) {
        var _self = this;
        this.isMovingCenter = true;
        // 创建 km 实例
        /* global kityminder */
        var km = this.minder = new kityminder.Minder();
        //var target = document.querySelector('#minder-view');
        km.renderTo(this.contentNode);

        data.theme = data.theme || this.options.defaultTheme;
        data.template = data.template || this.options.defaultTemplate;

        this.deepestLevel = 0;
        km.on('contentchange', function(){
            this.updateTime = new Date();
        }.bind(this));
        km.on("import", function (e) {
            if (!_self.alreadyBind) {
                var nodes = km.getAllNode();
                nodes.forEach(function (node) {
                    _self._loadMinderNode(node);
                });
                _self.alreadyBind = true;
                if (_self.options.leftToolbarEnable)_self.loadLeftToolbar();
                _self.fireEvent("postLoadMinder", _self);
            }
        });
        km.on("execCommand", function (e) {
            if (e.commandName === "template") {
                _self.moveToCenter();
            }
        });
        km.on("layoutallfinish", function () {
            if (_self.templateChanged || _self.isMovingCenter) {
                _self.moveToCenter();
                _self.templateChanged = false;
                _self.isMovingCenter = false;
            }
        });
        km.importJson(data);
    },
    _loadMinderNode : function( node ){
        var level = node.getLevel();
        this.deepestLevel = level > this.deepestLevel ? level : this.deepestLevel;
        this.fireEvent("postLoadMinderNode", node);
    },
    addMinderNodeEvents : function(minderNode, events){
        var cNode = minderNode.getRenderContainer().node;
        for( var key in events ){
            cNode.addEventListener( key, function ( ev ) {
                var coordinate = minderNode.getRenderBox('screen');
                events[this]( ev, minderNode, coordinate );
            }.bind(key));
        }
    },
    addMinderNoteIconEvents : function( minderNode, events ){
        var iconRenderer = minderNode.getRenderer('NoteIconRenderer');
        if( iconRenderer && iconRenderer.getRenderShape() ){
            var icon = iconRenderer.getRenderShape();
            for( var key in events ){
                icon.addEventListener( key, function ( ev ) {
                    var coordinate = minderNode.getRenderBox('screen');
                    events[this]( ev, minderNode, coordinate );
                }.bind(key));
                //icon.addEventListener("mouseover", function ( ev ) {
                //    _self.tooltipTimer = setTimeout(function() {
                //        var c = this.getRenderBox('screen');
                //        _self.loadTooltip( this.getData(), c );
                //    }.bind(this), 300);
                //}.bind(minderNode));
            }
        }
    },
    onExpandMinderNode : function( minderNode, callback ){
        var expanderNode = minderNode.getRenderer('ExpanderRenderer').getRenderShape();
        if( expanderNode ){
            expanderNode.addEventListener("mousedown", function(ev){
                var coordinate = minderNode.getRenderBox('screen');
                if(callback)callback(ev, minderNode, coordinate);
            }.bind(minderNode));
        }
    },
    replaceMinderNodeWithData : function(minderNode, data){
        var km = this.minder;
        while (minderNode.getChildren().length){
            var node = minderNode.getChildren()[0];
            km.removeNode(node)
        }
        km.importNode(minderNode, data );
        km.refresh();
        setTimeout( function(){
            var nodes = minderNode.getChildren();
            if( nodes.length ){
                nodes.forEach(function (node) {
                    this._loadMinderNode(node);
                }.bind(this));
            }
        }.bind(this), 100)
    },
    resizeContent: function () {
        var size = this.content.getSize();
        this.contentNode.setStyles({
            "height": (size.y - this.Content_Offset_Top ) + "px"
        });
        if( this.rightToolbar ){
            this.rightToolbar.setTooltipsSize();
        }
        if( this.minder ){
            this.moveToCenter();
        }
    },
    loadLeftToolbar: function () {
        this.leftToolbar = new MWF.xApplication.MinderEditor.LeftToolbar(this.node, this, this.minder, this);
        this.leftToolbar.load();
        //this.navi.setMoveOpen(false);
    },

    moveToCenter: function () {
        //setTimeout( this._moveToCenter.bind(this) , 100 );
        this._moveToCenter();
    },
    _moveToCenter: function () {
        if (this.options.align != "center")return;
        //图形居中
        var minderView = this.minder.getRenderContainer().getRenderBox('screen'); //.getBoundaryBox();
        var containerView = this.contentNode.getCoordinates();

        var root = this.minder.getRoot();
        var rootView = root.getRenderContainer().getRenderBox('screen'); //getRenderBox('top');
        var rootClientTop = rootView.top - minderView.top;
        var rootClientLeft = rootView.left - minderView.left;
        var rootChildrenLength = root.getChildren().length;

        var template = this.minder.queryCommandValue("template");

        var left, top, isCamera = false;
        if (minderView.width > containerView.width) {  //如果图形宽度大于容器宽度
            if (template == "fish-bone" || rootChildrenLength < 2) {
                left = 50;
            } else {
                isCamera = true;
            }
        } else {
            left = parseInt(( containerView.width - minderView.width ) / 2 + rootClientLeft + 50);
        }
        if (minderView.height > containerView.height) {  //如果图形高度大于容器高度
            if (rootClientTop > containerView.height) {
                if (template == "fish-bone") {
                    top = containerView.height - rootView.height
                } else if (rootChildrenLength < 2) {
                    top = parseInt(containerView.width / 2);
                } else {
                    isCamera = true;
                }
            } else {
                top = rootClientTop + 50;
            }
        } else {
            top = parseInt(( containerView.height - minderView.height ) / 2) + rootClientTop;
        }
        if (isCamera) {
            this.minder.execCommand('camera', this.minder.getRoot(), 600);
        } else {
            var dragger = this.minder.getViewDragger();
            dragger.moveTo(new kity.Point(left, top), 300);
        }
    },
    recordStatus: function () {
        var status = {
            id :  this.data ? this.data.id : "",
            autoSave : this.autoSave,
            isEdited : this.options.isEdited,
            isNew : this.options.isNew,
            dataMode : this.options.dataMode
        };
        if( this.options.dataMode == "outer" ){
            status.data = this.data;
        }
        if( this.rightToolbar ){
            status.styleActive = this.rightToolbar.styleActive;
            status.noteActive = this.rightToolbar.noteActive;
            status.resourceActive = this.rightToolbar.resourceActive;
        }
        return status;
    },
    startAutoSave : function(){
        this.notice("开启自动保存");
        this.autoSave = true;
        this.autosaveInterval = setInterval( function(){
            if( this.updateTime ){
                if( !this.saveTime || this.saveTime < this.updateTime ){
                    this.save( "自动保存成功" );
                    this.saveTime = this.updateTime.clone();
                }
            }
        }.bind(this), this.autoSaveInter )
    },
    stopAutoSave : function(){
        this.notice("关闭自动保存");
        this.autoSave = false;
        if( this.autosaveInterval ){
            clearInterval( this.autosaveInterval );
        }
    },
    saveAs : function ( folder, newName ) {
        var content = this.minder.exportJson();
        var contentStr = JSON.stringify( content );
        var title = this.minder.getRoot().getText();
        var data = {
            content : contentStr,
            name : newName || title,
            folderId : folder || this.options.folderId,
            description : ""
        };
        this.restActions.saveMind( data, function(json){
            var id = json.data.id;
            this.restActions.getMind( id, function( json2 ){
                var converter = new MWF.xApplication.MinderEditor.Converter(this, this.minder, this);
                converter.toPng(180, 130, function( img ){

                    var formData = new FormData();
                    formData.append('file', img, "untitled.png");
                    formData.append('site', id);

                    //this.restActions.uploadMindIcon( id, 180, function(){
                    //    this.notice( "另存成功" );
                    //}.bind(this), null,formData, img, false )

                    MWF.xDesktop.uploadImage( id, "mindInfo", formData, img,
                        function(json3){
                            data.id = id;
                            data.icon = json3.data.id;
                            this.restActions.saveMind( data, function(json4){
                                this.notice( "另存成功" );
                            }.bind(this))
                        }.bind(this)
                    );

                }.bind(this), function(){
                    this.notice( "另存成功，但由于脑图中有外网图片，浏览器无法生成缩略图" );
                }.bind(this))
            }.bind(this))
        }.bind(this));
    },
    setNewName: function( newname ){
        this.save( "重命名成功", newname, null )
    },
    save: function ( noticetText, newName , folder) {
        var content = this.minder.exportJson();
        var contentStr = JSON.stringify( content );
        var title = this.minder.getRoot().getText();

        var callback_save = function(id, flag, toPngFail){
            this.data.content = contentStr;
            var text = toPngFail ? "另存成功，但由于脑图中有外网图片，浏览器无法生成缩略图" : "保存成功";
            this.restActions.saveMind( this.data, function(json4){
                if( flag ){
                    this.restActions.getMind( id, function( json5 ){
                        this.data = json5.data;
                        this.data.content = content;
                        if( newName )this.setTitle(newName);
                        this.notice( noticetText || text );
                    }.bind(this))
                }else{
                    this.data.content = content;
                    if( newName )this.setTitle(newName);
                    this.notice( noticetText || text );
                }
            }.bind(this))
        }.bind(this);

        var callback = function( id, flag ){
            var converter = new MWF.xApplication.MinderEditor.Converter(this, this.minder, this);
            converter.toPng(180, 130, function( img ){
                var formData = new FormData();
                formData.append('file', img, "untitled.png");
                formData.append('site', id);

                //this.restActions.uploadMindIcon( id, 180, function(){
                //    if( newName )this.setTitle(newName);
                //    this.notice( noticetText || "保存成功" );
                //}.bind(this), null,formData, img, false )

                MWF.xDesktop.uploadImage( id, "mindInfo", formData, img,
                    function(json3){
                        this.data.icon = json3.data.id;
                        callback_save(id, flag)
                    }.bind(this)
                );

            }.bind(this), function(){
                this.data.icon = "";
                callback_save(id, flag, true);
            }.bind(this))
        }.bind(this);

        if( this.data && this.data.id){
            this.data.content = contentStr;
            if( newName ){ this.data.name = newName; }
            if( folder ){ this.data.folderId = folder; }
            callback(this.data.id);
        }else{
            this.data = {
                content : contentStr,
                name : newName || title,
                folderId : folder || this.options.folderId,
                description : ""
            };
            this.restActions.saveMind( this.data, function(json){
                var id = this.options.id = json.data.id;
                callback(id, true);
            }.bind(this));
        }
    },
    openSaveAsDialog : function(){
        var form = new MWF.xApplication.MinderEditor.SaveAsForm(this, {
            newname : (this.data ? this.data.name : "")
        }, {}, {
            app: this
        });
        form.edit()
    },
    openRenameDialog : function(){
        var form = new MWF.xApplication.MinderEditor.NewNameForm(this, {
            newname : (this.data ? this.data.name : "")
        }, {}, {
            app: this
        });
        form.edit()
    },
    openShareDialog : function(){
        MWF.xDesktop.requireApp("Minder", "Common", null, false);
        var form = new MWF.xApplication.Minder.ShareForm({ app : this }, {}, {
        }, {
            app: this
        });
        form.checkedItemData = [this.data];
        form.edit();
    },
    openExportDialog : function(){
        var form = new MWF.xApplication.MinderEditor.ExportForm({ app : this }, this.data, {
        }, {
            app: this
        });
        form.edit();
    },
    openNewMinderDialog : function(){
        MWF.xDesktop.requireApp("Minder", "Common", null, false);
        var form = new MWF.xApplication.Minder.NewNameForm({ app : this }, {}, {
        }, {
            app: this
        });
        form.edit();
    },
    loadCodeMirror : function( callback ){
        if( window.CodeMirror ){
            if( callback )callback();
            return;
        }
        var codeMirrorPath = "/o2_lib/codemirror";
        var markedPath = "/o2_lib/marked";

        var jsModules = {
            codemirror : codeMirrorPath + "/lib/codemirror.js",
            codemirror_xml : codeMirrorPath + "/mode/xml/xml.js",
            codemirror_javascript : codeMirrorPath + "/mode/javascript/javascript.js",
            codemirror_css : codeMirrorPath + "/mode/css/css.js",
            codemirror_htmlmixed : codeMirrorPath + "/mode/htmlmixed/htmlmixed.js",
            codemirror_markdown: codeMirrorPath + "/mode/markdown/markdown.js",
            codemirror_overlay: codeMirrorPath + "/addon/mode/overlay.js",
            codemirror_gfm: codeMirrorPath + "/mode/gfm/gfm.js",
            codemirror_marked: markedPath + "/lib/marked.js"
        };

        //var modules = [];
        //for( var key in jsModules ){
        //    if( !COMMON.AjaxModule[ key ] ){
        //        COMMON.AjaxModule[ key ] = jsModules[key];
        //    }
        //    modules.push( key );
        //}

        COMMON.AjaxModule.loadCss(codeMirrorPath + "/lib/codemirror.css", function () {
            o2.load( Object.values(jsModules), function () {
                marked.setOptions({
                    gfm: true,
                    tables: true,
                    breaks: true,
                    pedantic: false,
                    sanitize: true,
                    smartLists: true,
                    smartypants: false
                });

                //this.codeMirrorLoaded = true;
                if(callback)callback();
            }.bind(this))
        }.bind(this))
    }
});

MWF.xApplication.MinderEditor.Converter = new Class({
    initialize: function (editor, minder) {
        this.editor = editor;
        this.minder = minder;
    },
    toPng: function (width, height, callback, failure) {
        var img;
        this.toCanvas(width, height, function (canvas) {
            try{
                var src = canvas.toDataURL("image/png");

                var base64Code = src.split(',')[1];
                if (!base64Code) {
                    img = null;
                    return;
                }
                base64Code = window.atob(base64Code);

                var ia = new Uint8Array(base64Code.length);
                for (var i = 0; i < base64Code.length; i++) {
                    ia[i] = base64Code.charCodeAt(i);
                }
                img = new Blob([ia], {type: "image/png"});
                if(callback)callback( img );
            }catch(e){
                if(failure)failure();
                //debugger;
                //var pr = new MWF.xApplication.MinderEditor.PreviewConverter(this.editor, this.minder, width, height);
                //pr.toPng(width, height, callback, failure );
            }
        }.bind(this))
    },
    toCanvas: function (width, height, callback, svg) {
        this.loadCanvgResource(function () {
            if( !svg )svg = this.editor.contentNode.get("html");

            var coordinates = this.getSvgCoordinates();

            var offsetLeft = Math.abs(coordinates.left), offsetTop = Math.abs(coordinates.top);
            var contentWidth = coordinates.x, contentHeight = coordinates.y;
            var matrix;
            if (width && height) {
                if ((width > coordinates.x) && (height > coordinates.y)) {
                    //如果宽度比指定宽度小，设置偏移量
                    if (width > coordinates.x) {
                        offsetLeft += ( width - coordinates.x ) / 2;
                        contentWidth = width;
                    }
                    //如果高度比指定高度小，设置偏移量
                    if (height > coordinates.y) {
                        offsetTop += ( height - coordinates.y ) / 2;
                        contentHeight = height;
                    }
                }

                //如果宽度比指定宽度大，进行缩小
                var xRatio, yRatio, ox, oy, zoom;
                if (width < coordinates.x) {
                    xRatio = width / coordinates.x;
                }
                //如果高度比指定高度大，进行缩小
                if (height < coordinates.y) {
                    yRatio = height / coordinates.y;
                }
                if (xRatio || yRatio) {

                    contentWidth = width;
                    contentHeight = height;

                    xRatio = xRatio || 1;
                    yRatio = yRatio || 1;
                    if( xRatio >= yRatio ){
                        zoom = yRatio;
                        ox = (width - zoom * coordinates.x)/2;
                        oy = 0;
                    }else{
                        zoom = xRatio;
                        ox = 0;
                        oy = ( height - zoom * coordinates.y )/2;
                    }

                    matrix = zoom + " 0 0 " + zoom + " " + ox + " " + oy;
                }
            }

            var regex = /<svg.*?>(.*?)<\/svg>/ig;
            svg = "<svg width=\"" + contentWidth + "\" height=\"" + contentHeight + "\">" + regex.exec(svg)[1] + "</svg>";

            var arr1 = svg.split("</defs>");
            var arr2 = svg.split("<g id=\"minder_connect_group");

            svg = arr1[0] + "</defs>"
                + "<g transform=\"" + ( matrix ? "matrix(" + matrix + ")" : "translate(0.5 0.5)") + "\">"
                + "<g transform=\"translate(" + offsetLeft + " " + offsetTop + ")\" text-rendering=\"" + ( matrix ? "geometricPrecision" : "optimize-speed")  + "\">"
                + "<g id=\"minder_connect_group" + arr2[1];

            var canvas = new Element("canvas", {
                width: contentWidth, height: contentHeight,
                styles: {width: contentWidth + "px", height: contentHeight + "px"}
            }).inject(this.editor.node);
            canvg(canvas, svg, {
                useCORS : true, log: true, renderCallback: function (dom) {
                    if (callback)callback(canvas);
                }
            });
        }.bind(this))
    },
    loadCanvgResource: function (callback) {
        var canvgPath = "/o2_lib/canvg/";
        COMMON.AjaxModule.load(canvgPath + "canvg.js", function () {
            if (callback)callback();
        }.bind(this))
    },
    getSvgCoordinates: function () {

        var topBox = {top: 0, left: 0, right: 0, bottom: 0};
        var leftBox = {top: 0, left: 0, right: 0, bottom: 0};
        var rightBox = {top: 0, left: 0, right: 0, bottom: 0};
        var bottomBox = {top: 0, left: 0, right: 0, bottom: 0};

        this.minder.getRoot().traverse(function (node) {
            var renderBox = node.getLayoutBox();
            if (renderBox.top < topBox.top) {
                topBox = renderBox;
            }
            if (renderBox.left < leftBox.left) {
                leftBox = renderBox;
            }
            if (renderBox.right > rightBox.right) {
                rightBox = renderBox;
            }
            if (renderBox.bottom > bottomBox.bottom) {
                bottomBox = renderBox;
            }
        }.bind(this));

        return {
            top: topBox.top,
            right: rightBox.right,
            bottom: bottomBox.bottom,
            left: leftBox.left,
            width: rightBox.right - leftBox.left + 1,
            height: bottomBox.bottom - topBox.top + 1,
            x: rightBox.right - leftBox.left + 1,
            y: bottomBox.bottom - topBox.top + 1
        };
    }
});


MWF.xApplication.MinderEditor.PreviewConverter = new Class({
    initialize: function (editor, minder, width, height) {
        this.editor = editor;
        this.minder = minder;

        this.previewer = new Element("div",{ "styles" : {
            width : width, height : height
        }}).inject( this.editor.content );
        this.initPreViewer();
        this.draw();
    },
    initPreViewer: function(){
        // 画布，渲染缩略图
        this.paper = new kity.Paper( this.previewer );

        // 用两个路径来挥之节点和连线的缩略图
        this.nodeThumb = this.paper.put(new kity.Path());
        this.connectionThumb = this.paper.put(new kity.Path());
        /**
         * 增加一个对天盘图情况缩略图的处理,
         * @Editor: Naixor line 104~129
         * @Date: 2015.11.3
         */
        this.pathHandler = this.getPathHandler(this.minder.getTheme());
    },
    getPathHandler: function (theme) {
        switch (theme) {
            case "tianpan":
            case "tianpan-compact":
                return function(nodePathData, x, y, width, height) {
                    var r = width >> 1;
                    nodePathData.push('M', x, y + r,
                        'a', r, r, 0, 1, 1, 0, 0.01,
                        'z');
                };
            default: {
                return function(nodePathData, x, y, width, height) {
                    nodePathData.push('M', x, y,
                        'h', width, 'v', height,
                        'h', -width, 'z');
                }
            }
        }
    },
    draw : function(){
        var view = this.minder.getRenderContainer().getBoundaryBox();
        var padding = 30;
        this.paper.setViewBox(
            view.x - padding - 0.5,
            view.y - padding - 0.5,
            view.width + padding * 2 + 1,
            view.height + padding * 2 + 1);

        var nodePathData = [];
        var connectionThumbData = [];
        this.minder.getRoot().traverse(function(node) {
            var box = node.getLayoutBox();
            this.pathHandler(nodePathData, box.x, box.y, box.width, box.height);
            if (node.getConnection() && node.parent && node.parent.isExpanded()) {
                connectionThumbData.push(node.getConnection().getPathData());
            }
        }.bind(this));
        this.paper.setStyle('background', this.minder.getStyle('background'));

        if (nodePathData.length) {
            this.nodeThumb
                .fill(this.minder.getStyle('root-background'))
                .setPathData(nodePathData);
        } else {
            this.nodeThumb.setPathData(null);
        }

        if (connectionThumbData.length) {
            this.connectionThumb
                .stroke(this.minder.getStyle('connect-color'), '0.5%')
                .setPathData(connectionThumbData);
        } else {
            this.connectionThumb.setPathData(null);
        }
    },
    toPng: function (width, height, callback, failure, svg) {
        var img;
        this.toCanvas(width, height, function (canvas) {
            try{
                var src = canvas.toDataURL("image/png");

                var base64Code = src.split(',')[1];
                if (!base64Code) {
                    img = null;
                    return;
                }
                base64Code = window.atob(base64Code);

                var ia = new Uint8Array(base64Code.length);
                for (var i = 0; i < base64Code.length; i++) {
                    ia[i] = base64Code.charCodeAt(i);
                }
                img = new Blob([ia], {type: "image/png"});
                if(callback)callback( img );
            }catch(e){
                if(failure)failure( );
            }
        }.bind(this), svg)
    },
    toCanvas: function (width, height, callback, svg ) {
        this.loadCanvgResource(function () {
            if(!svg)svg = this.previewer.get("html");

            var coordinates = this.getSvgCoordinates();

            var offsetLeft = Math.abs(coordinates.left), offsetTop = Math.abs(coordinates.top);
            var contentWidth = coordinates.x, contentHeight = coordinates.y;
            var matrix;
            if (width && height) {
                if ((width > coordinates.x) && (height > coordinates.y)) {
                    //如果宽度比指定宽度小，设置偏移量
                    if (width > coordinates.x) {
                        offsetLeft += ( width - coordinates.x ) / 2;
                        contentWidth = width;
                    }
                    //如果高度比指定高度小，设置偏移量
                    if (height > coordinates.y) {
                        offsetTop += ( height - coordinates.y ) / 2;
                        contentHeight = height;
                    }
                }

                //如果宽度比指定宽度大，进行缩小
                var xRatio, yRatio, ox, oy, zoom;
                if (width < coordinates.x) {
                    xRatio = width / coordinates.x;
                }
                //如果高度比指定高度大，进行缩小
                if (height < coordinates.y) {
                    yRatio = height / coordinates.y;
                }
                if (xRatio || yRatio) {

                    contentWidth = width;
                    contentHeight = height;

                    xRatio = xRatio || 1;
                    yRatio = yRatio || 1;
                    if( xRatio >= yRatio ){
                        zoom = yRatio;
                        ox = (width - zoom * coordinates.x)/2;
                        oy = 0;
                    }else{
                        zoom = xRatio;
                        ox = 0;
                        oy = ( height - zoom * coordinates.y )/2;
                    }

                    matrix = zoom + " 0 0 " + zoom + " " + ox + " " + oy;
                }
            }

            var regex = /<svg.*?>(.*?)<\/svg>/ig;
            svg = "<svg width=\"" + contentWidth + "\" height=\"" + contentHeight + "\">" + regex.exec(svg)[1] + "</svg>";

            var arr1 = svg.split("</defs>");
            var arr2 = svg.split("<g id=\"minder_connect_group");

            svg = arr1[0] + "</defs>"
                + "<g transform=\"" + ( matrix ? "matrix(" + matrix + ")" : "translate(0.5 0.5)") + "\">"
                + "<g transform=\"translate(" + offsetLeft + " " + offsetTop + ")\" text-rendering=\"" + ( matrix ? "geometricPrecision" : "optimize-speed")  + "\">"
                + "<g id=\"minder_connect_group" + arr2[1];

            var canvas = new Element("canvas", {
                width: contentWidth, height: contentHeight,
                styles: {width: contentWidth + "px", height: contentHeight + "px"}
            }).inject(this.editor.node);
            canvg(canvas, svg, {
                useCORS : true, log: true, renderCallback: function (dom) {
                    if (callback)callback(canvas);
                }
            });
        }.bind(this))
    },
    loadCanvgResource: function (callback) {
        var canvgPath = "/o2_lib/canvg/";
        COMMON.AjaxModule.load(canvgPath + "canvg.js", function () {
            if (callback)callback();
        }.bind(this))
    },
    getSvgCoordinates: function () {

        var topBox = {top: 0, left: 0, right: 0, bottom: 0};
        var leftBox = {top: 0, left: 0, right: 0, bottom: 0};
        var rightBox = {top: 0, left: 0, right: 0, bottom: 0};
        var bottomBox = {top: 0, left: 0, right: 0, bottom: 0};

        this.paper.getRoot().traverse(function (node) {
            var renderBox = node.getLayoutBox();
            if (renderBox.top < topBox.top) {
                topBox = renderBox;
            }
            if (renderBox.left < leftBox.left) {
                leftBox = renderBox;
            }
            if (renderBox.right > rightBox.right) {
                rightBox = renderBox;
            }
            if (renderBox.bottom > bottomBox.bottom) {
                bottomBox = renderBox;
            }
        }.bind(this));

        return {
            top: topBox.top,
            right: rightBox.right,
            bottom: bottomBox.bottom,
            left: leftBox.left,
            width: rightBox.right - leftBox.left + 1,
            height: bottomBox.bottom - topBox.top + 1,
            x: rightBox.right - leftBox.left + 1,
            y: bottomBox.bottom - topBox.top + 1
        };
    },
    destory : function(){
        this.paper.remove();
        this.previewer.destroy();
    }
});

