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
        var items = [
            "append",
            // "arrange", "|",
            "edit_remove",
            "hyperLink", "image", "priority", "progress"
        ];

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
                    html +=
                        "<div item='undo' styles='" + style + "'></div>" +
                        "<div item='redo' styles='" + style + "'></div>";
                    break;
                case "append" :
                    html +=
                        "<div item='appendChild' styles='" + style + "'></div>" +
                        "<div item='appendParent' styles='" + style + "'></div>" +
                        "<div item='appendSibling' styles='" + style + "'></div>";
                    break;
                case "arrange" :
                    html +=
                        "<div item='arrangeUp' styles='" + style + "'></div>" +
                        "<div item='arrangeDown' styles='" + style + "'></div>";
                    break;
                case "edit_remove" :
                    html +=
                        "   <div item='edit' styles='" + style + "'></div>" +
                        "   <div item='remove' styles='" + style + "'></div>";
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
                case "style":
                    html +=
                        "   <div item='clearstyle' styles='" + style + "'></div>" +
                        "   <div item='copystyle' styles='" + style + "'></div>" +
                        "   <div item='pastestyle' styles='" + style + "'></div>";
                    break;
                case "help" :
                    html += "<div item='help' styles='toolItem_help'></div>";
                    break;
            }
        });
        return html;
    },
    load: function () {
        var html = this.getHtml();

        this.container.set("html", html);

        var buttonStyles = {
            background: "#fff",
            "font-size": "14px",
            color: '#666',
            cursor: "pointer",
            width: "50px",
            height:"16px",
            'padding-right': '10px'
        };

        var zoomInAction = new Element("div", {
            styles: buttonStyles,
            text: '放大',
            events: {
                click: ()=>{
                    this.minder.execCommand('zoomIn');
                }
            }
        }).inject(this.container, 'top');
        new Element("i.ooicon-zoom_in", {style:'font-size:16px;padding-right:4px;'}).inject(zoomInAction, 'top');

        var zoomOutAction = new Element("div", {
            styles: buttonStyles,
            text: '缩小',
            events: {
                click: ()=>{
                    this.minder.execCommand('zoomOut');
                }
            }
        }).inject(this.container, 'top');
        new Element("i.ooicon-zoom_out", {style:'font-size:16px;padding-right:4px;'}).inject(zoomOutAction, 'top');

        var handFlag = false;
        var handAction = new Element("div", {
            styles: buttonStyles,
            text: '拖拽',
            events: {
                click: ()=>{
                    !handFlag ? handAction.addClass('mainColor_color') : handAction.removeClass('mainColor_color');
                    handFlag = !handFlag;
                    this.minder.execCommand('hand');
                }
            }
        }).inject(this.container, 'top');
        new Element("i.ooicon-finger2", {style:'font-size:16px;padding-right:4px;'}).inject(handAction, 'top');

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