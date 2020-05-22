MWF.xApplication.MinderEditor.TopToolbar = new Class({
    Extends: MWF.widget.Common,
    initialize: function (editor, container) {
        this.editor = editor;
        this.minder = editor.minder;
        this.container = container;
        this.commands = this.editor.commands;

        this.path = "../x_component_MinderEditor/$ToolbarInEditMode/";
        this.cssPath = this.path + this.editor.options.style + "/css.wcss";
        this._loadCss();
    },
    getHtml : function(){
        var items;
        var tools = this.editor.options.tools;
        if( tools && tools.top ){
            items = tools.top;
        }else{
            items = [
                "menu", "|",
                "save", "|",
                "undoredo", "|",
                "append", "|",
                "arrange", "|",
                "edit_remove", "|",
                "hyperLink", "image", "priority", "progress", "|",
                "style",
                "help"
            ];
        }
        var disableTools = this.editor.options.disableTools || [];
        disableTools.each( function( tool ){
            items.erase( tool )
        });

        var html = "";
        var style = "toolItem";
        items.each( function( item ){
            switch( item ){
                case "|":
                    html +=  "<div styles='" + "separator" + "'></div>";
                    break;
                case "menu":
                    html +=   "<div item='menu' itemevent='click' styles='" + style + "'></div>";
                    break;
                case "save" :
                    html +=  "<div item='save' styles='" + style + "'></div>";
                    break;
                case "undoredo" :
                    html += "<div styles='" + "toolItemGroup" + "' style='width:70px;'>" +
                    "   <div item='undo' styles='" + "toolItem_horizontal_top" + "'></div>" +
                    "   <div item='redo' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
                    "</div>";
                    break;
                case "append" :
                    html +=  "<div styles='" + "toolItemGroup" + "' style='width:250px;'>" +
                        "   <div item='appendChild' styles='" + "toolItem_horizontal_top" + "'></div>" +
                        "   <div item='appendParent' styles='" + "toolItem_horizontal_top" + "'></div>" +
                        "   <div item='appendSibling' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
                        "</div>";
                    break;
                case "arrange" :
                    html +=  "<div styles='" + "toolItemGroup" + "' style='width:70px;'>" +
                        "   <div item='arrangeUp' styles='" + "toolItem_horizontal_top" + "'></div>" +
                        "   <div item='arrangeDown' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
                        "</div>";
                    break;
                case "edit_remove" :
                    html +=  "<div styles='" + "toolItemGroup" + "' style='width:70px;'>" +
                        "   <div item='edit' styles='" + "toolItem_horizontal_top" + "'></div>" +
                        "   <div item='remove' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
                        "</div>";
                    break;
                case "hyperLink" :
                    html +=  "<div item='hyperLink' styles='" + style + "'></div>";
                    break;
                case "image" :
                    html +=  "<div item='image' styles='" + style + "'></div>";
                    break;
                case "priority" :
                    html +=  "<div item='priority' styles='" + style + "'></div>";
                    break;
                case "progress" :
                    html += "<div item='progress' styles='" + style + "'></div>";
                    break;
                case "style" :
                    html += "<div styles='" + "toolItemGroup" + "' style='width:170px;'>" +
                        "   <div item='clearstyle' styles='" + style + "'></div>" +
                        "   <div item='copystyle' styles='" + "toolItem_horizontal_top" + "'></div>" +
                        "   <div item='pastestyle' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
                        "</div>";
                    break;
                case "help" :
                    html += "<div item='help' styles='toolItem_help'></div>";
                    break;
            }
        });
        return html;
    },
    load: function () {
        /*

         var style = "toolItem";
        var html =
            //"<div item='template' styles='" + style + "'></div>" +
            //"<div item='theme' styles='" + style + "'></div>" +
            //"<div item='resetlayout' styles='" + style + "'></div>" +
            "<div item='menu' itemevent='click' styles='" + style + "'></div>" +
            "<div styles='" + "separator" + "'></div>" +
            "<div item='save' styles='" + style + "'></div>" +
            //"<div item='export' styles='" + style + "'></div>" +
            "<div styles='" + "separator" + "'></div>" +
            "<div styles='" + "toolItemGroup" + "' style='width:70px;'>" +
            "   <div item='undo' styles='" + "toolItem_horizontal_top" + "'></div>" +
            "   <div item='redo' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
            "</div>" +
            "<div styles='" + "separator" + "'></div>" +
            "<div styles='" + "toolItemGroup" + "' style='width:250px;'>" +
            "   <div item='appendChild' styles='" + "toolItem_horizontal_top" + "'></div>" +
            "   <div item='appendParent' styles='" + "toolItem_horizontal_top" + "'></div>" +
            "   <div item='appendSibling' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
            "</div>" +
            "<div styles='" + "separator" + "'></div>" +
            "<div styles='" + "toolItemGroup" + "' style='width:70px;'>" +
            "   <div item='arrangeUp' styles='" + "toolItem_horizontal_top" + "'></div>" +
            "   <div item='arrangeDown' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
            "</div>" +
            "<div styles='" + "separator" + "'></div>" +
            "<div styles='" + "toolItemGroup" + "' style='width:70px;'>" +
            "   <div item='edit' styles='" + "toolItem_horizontal_top" + "'></div>" +
            "   <div item='remove' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
            "</div>" +
            "<div styles='" + "separator" + "'></div>" +
            "<div item='hyperLink' styles='" + style + "'></div>" +
            "<div item='image' styles='" + style + "'></div>" +
            "<div item='priority' styles='" + style + "'></div>" +
            "<div item='progress' styles='" + style + "'></div>" +
                //"<div item='resource' styles='" + style + "'></div>" +
            "<div styles='" + "separator" + "'></div>" +
            "<div styles='" + "toolItemGroup" + "' style='width:170px;'>" +
            "   <div item='clearstyle' styles='" + style + "'></div>" +
            "   <div item='copystyle' styles='" + "toolItem_horizontal_top" + "'></div>" +
            "   <div item='pastestyle' styles='" + "toolItem_horizontal_bottom" + "'></div>" +
            "</div>" +
            //"<div styles='" + "separator" + "'></div>" +
                //"<div item='fontfamily' styles='" + style + "'></div>" +
                //"<div item='fontsize' styles='" + style + "'></div>" +
                //"<div item='bold' styles='" + style + "'></div>" +
                //"<div item='forecolor' styles='" + style + "'></div>" +
                //"<div item='background' styles='" + style + "'></div>" +
                //"<div styles='" + "separator" + "'></div>" +
            //"<div item='expandLevel' styles='" + style + "'></div>" +
            //"<div item='selectAll' styles='" + style + "'></div>"+
            "<div item='help' styles='toolItem_help'></div>";
    */
        var html = this.getHtml();

        this.container.set("html", html);
        this.container.getElements("[styles]").each(function (el) {
            if (!el.get("item")) {
                el.setStyles(this.css[el.get("styles")]);
            }
        }.bind(this));

        this.commands.addContainer( "toptoolbar", this.container, this.css );
    },
    getCommandNode : function( name ){
        return this.commands.getItemNode( name, "toptoolbar" );
    }
});

MWF.xApplication.MinderEditor.RightToolbar = new Class({
    Extends: MWF.widget.Common,
    initialize: function (editor, container) {
        this.editor = editor;
        this.minder = editor.minder;
        this.container = container;
        this.commands = this.editor.commands;

        this.path = "../x_component_MinderEditor/$ToolbarInEditMode/";
        this.cssPath = this.path + this.editor.options.style + "/css.wcss";
        this._loadCss();
    },
    getItems : function(){
        var items;
        var tools = this.editor.options.tools;
        if( tools && tools.right ){
            items = tools.right;
        }else{
            items = [
                "font", "resource", "note"
            ];
        }
        var disableTools = this.editor.options.disableTools || [];
        disableTools.each( function( tool ){
            items.erase( tool )
        });
        return items;
    },
    load: function(){
        this.styleActive = true;
        this.resourceActive = false;
        this.noteActive = false;
        if( this.editor.status ){
            this.styleActive = this.editor.status.styleActive;
            this.resourceActive = this.editor.status.resourceActive;
            this.noteActive = this.editor.status.noteActive;
        }
        this.styleLoaded = this.styleActive;
        this.resourceLoaded = this.resourceActive;
        this.noteLoaded = this.noteActive;

        var items = this.getItems();

        var styleLazyLoding = "  lazyLoading='" + (this.styleActive ? "false" : "true") + "'";
        var html="";
        if( items.contains("font") ){
            html += "<div styles='" + (this.styleActive ? "rightToolbarItem_style_active" : "rightToolbarItem_style") +"' title='文本样式' action='switchStyle'></div>"+
                "<div styles='tooltipNode' id='styleTooltip' style='display:" + (this.styleActive ? "" : "none") +";'>"+
                "   <div styles='closeNode' action='closeStyle'></div>"+
                "   <div style='width: 100%; height: 100%;'>"+
                "       <div item='fontsize' styles='fontToolbarItemSelector'" + styleLazyLoding+"></div>" +
                "       <div item='bold' styles='fontToolbarItem'" + styleLazyLoding+"></div>" +
                "       <div item='italic' styles='fontToolbarItem'" + styleLazyLoding+"></div>" +
                "       <div item='forecolor' styles='fontToolbarItem'" + styleLazyLoding+"></div>" +
                "       <div item='background' styles='fontToolbarItem'" + styleLazyLoding+"></div>" +
                "       <div item='fontfamily' styles='fontToolbarItemSelector'" + styleLazyLoding+"></div>" +
                "   </div>"+
                    //"   <div styles='arrowNode'></div>"+
                "</div>";
        }
        if( items.contains("resource") ){
            html += "<div styles='" + (this.resourceActive ? "rightToolbarItem_resource_active" : "rightToolbarItem_resource") +"' title='标签' action='switchResource'></div>"+
                "<div styles='tooltipNode_resource' id='resourceTooltip' style='display:" + (this.resourceActive ? "" : "none") +";'>"+
                "   <div styles='closeNode' action='closeResource'></div>"+
                "   <div item='resource' styles='resourceToolbarItem' lazyLoading='" + (this.resourceActive ? "false" : "true") + "'></div>" +
                    //"   <div styles='arrowNode'></div>"+
                "</div>";
        }
        if( items.contains("note") ){
            html += "<div styles='" + (this.noteActive ? "rightToolbarItem_note_active" : "rightToolbarItem_note") +"' title='备注' action='switchNote'></div>"+
                "<div styles='tooltipNode_note' id='noteTooltip' style='display:" + (this.noteActive ? "" : "none") +";'>"+
                "   <div styles='closeNode' action='closeNote'></div>"+
                "   <div item='note' styles='noteToolbarItem' lazyLoading='" + (this.noteActive ? "false" : "true") + "'></div>" +
                    //"   <div styles='arrowNode'></div>"+
                "</div>"
        }
        /*
            "<div styles='" + (this.styleActive ? "rightToolbarItem_style_active" : "rightToolbarItem_style") +"' title='文本样式' action='switchStyle'></div>"+
            "<div styles='tooltipNode' id='styleTooltip' style='display:" + (this.styleActive ? "" : "none") +";'>"+
            "   <div styles='closeNode' action='closeStyle'></div>"+
            "   <div style='width: 100%; height: 100%;'>"+
            "       <div item='fontsize' styles='fontToolbarItemSelector'" + styleLazyLoding+"></div>" +
            "       <div item='bold' styles='fontToolbarItem'" + styleLazyLoding+"></div>" +
            "       <div item='italic' styles='fontToolbarItem'" + styleLazyLoding+"></div>" +
            "       <div item='forecolor' styles='fontToolbarItem'" + styleLazyLoding+"></div>" +
            "       <div item='background' styles='fontToolbarItem'" + styleLazyLoding+"></div>" +
            "       <div item='fontfamily' styles='fontToolbarItemSelector'" + styleLazyLoding+"></div>" +
            "   </div>"+
                //"   <div styles='arrowNode'></div>"+
            "</div>"+
            "<div styles='" + (this.resourceActive ? "rightToolbarItem_resource_active" : "rightToolbarItem_resource") +"' title='标签' action='switchResource'></div>"+
            "<div styles='tooltipNode_resource' id='resourceTooltip' style='display:" + (this.resourceActive ? "" : "none") +";'>"+
            "   <div styles='closeNode' action='closeResource'></div>"+
            "   <div item='resource' styles='resourceToolbarItem' lazyLoading='" + (this.resourceActive ? "false" : "true") + "'></div>" +
                //"   <div styles='arrowNode'></div>"+
            "</div>"+
            "<div styles='" + (this.noteActive ? "rightToolbarItem_note_active" : "rightToolbarItem_note") +"' title='备注' action='switchNote'></div>"+
            "<div styles='tooltipNode_note' id='noteTooltip' style='display:" + (this.noteActive ? "" : "none") +";'>"+
            "   <div styles='closeNode' action='closeNote'></div>"+
            "   <div item='note' styles='noteToolbarItem' lazyLoading='" + (this.noteActive ? "false" : "true") + "'></div>" +
                //"   <div styles='arrowNode'></div>"+
            "</div>";
        */

        this.container.set("html", html);
        this.container.getElements("[styles]").each( function( el ){
            if( !el.get("item") ){
                el.setStyles( this.css[ el.get("styles") ] );
            }
        }.bind(this));

        this.container.getElements("[action]").each( function( el ){
            var action = el.get("action");
            el.addEvents( {"click": function( e ){
                this[action]( e.target );
            }.bind(this)})
        }.bind(this));

        this.styleTooltip = this.container.getElement("#styleTooltip");
        this.resourceTooltip = this.container.getElement("#resourceTooltip");
        this.noteTooltip = this.container.getElement("#noteTooltip");

        this.setTooltipsSize();

        this.commands.addContainer( "righttoolbar", this.container, this.css );
    },
    closeStyle : function(el){
        this.switchStyle( this.container.getElement("[action='switchStyle']"), false );
    },
    switchStyle: function( el, forceFlag ){
        if( typeOf( forceFlag ) == "boolean" )this.styleActive = !forceFlag;
        var tooltip = this.styleTooltip;
        if( tooltip ){
            if( !this.styleActive  ){
                tooltip.setStyle("display","");
                el.setStyles( this.css.rightToolbarItem_style_active );
                if( !this.styleLoaded ){
                    this.commands.loadItemsByNameList( ["fontsize","bold","italic","forecolor","background","fontfamily"], "righttoolbar" );
                    this.styleLoaded = true;
                }
            }else{
                tooltip.setStyle("display","none");
                el.setStyles( this.css.rightToolbarItem_style )
            }
            this.styleActive = !this.styleActive;
        }
        this.setTooltipsSize();
    },
    closeResource : function(el){
        this.switchResource( this.container.getElement("[action='switchResource']"), false );
    },
    switchResource : function(el, forceFlag){
        if( typeOf( forceFlag ) == "boolean" )this.resourceActive = !forceFlag;
        var tooltip = this.resourceTooltip;
        if( tooltip ){
            if( !this.resourceActive ){
                tooltip.setStyle("display","");
                el.setStyles( this.css.rightToolbarItem_resource_active );
                if( !this.resourceLoaded ){
                    this.commands.loadItemByName("resource", "righttoolbar" );
                    this.resourceLoaded = true;
                }
            }else{
                tooltip.setStyle("display","none");
                el.setStyles( this.css.rightToolbarItem_resource )
            }
            this.resourceActive = !this.resourceActive;
        }
        this.setTooltipsSize();
    },
    closeNote: function(el){
        this.switchNote( this.container.getElement("[action='switchNote']"), false )
    },
    switchNote : function(el, forceFlag){
        if( typeOf( forceFlag ) == "boolean" )this.noteActive = !forceFlag;
        var tooltip = this.noteTooltip;
        if( tooltip ){
            if( !this.noteActive ){
                tooltip.setStyle("display","");
                el.setStyles( this.css.rightToolbarItem_note_active );
                if( !this.noteLoaded ){
                    this.commands.loadItemByName("note", "righttoolbar" );
                    this.noteLoaded = true;
                }
            }else{
                tooltip.setStyle("display","none");
                el.setStyles( this.css.rightToolbarItem_note )
            }
            this.noteActive = !this.noteActive;
        }
        this.setTooltipsSize();
    },
    setTooltipsSize : function(){
        var noneC = { bottom : 0, y : 0 };
        var containTop = this.editor.Content_Offset_Top + 5; //73横向菜单的高度， 5是间距
        var y = this.editor.content.getSize().y - containTop;
        var styleC = this.styleActive ? this.styleTooltip.getCoordinates() : noneC;
        if( this.resourceActive ){
            var top = styleC.height ? (styleC.height + 10 ) : 0;
            this.resourceTooltip.setStyle("top",top);
        }
        var resourceC = this.resourceActive ? this.resourceTooltip.getCoordinates() : noneC;
        if( this.noteActive ){
            var top = styleC.height ? (styleC.height + 10 ) : 0;
            top += resourceC.height ? (resourceC.height + 10 ) : 0;
            var height = y - top - 10;
            this.noteTooltip.setStyles({
                "top" : top,
                "height" : Math.min( height , 500 ) //500是 notetoooltip 的最大高度
            });
            //var noteC = this.noteActive ? this.noteTooltip.getCoordinates() : noneC;
        }
        if(this.commands)this.commands.setSizes();
    }
});

