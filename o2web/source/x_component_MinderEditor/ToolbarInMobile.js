MWF.xApplication.MinderEditor.ToolbarInMobile = new Class({
    Extends: MWF.widget.Common,
    initialize: function (editor, container) {
        this.editor = editor;
        this.minder = editor.minder;
        this.container = container;
        this.commands = this.editor.commands;

        this.path = "../x_component_MinderEditor/$ToolbarInMobile/";
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
                "append", "|",
                "arrange", "|",
                "edit_remove", "|",
                "hyperLink", "image", "priority", "progress"
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