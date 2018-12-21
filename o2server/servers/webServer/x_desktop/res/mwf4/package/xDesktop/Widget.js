MWF.xDesktop.Widget = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default",
		"title": "widget",
		"width": "400",
		"height": "400",
		"position": {"right": 10, "bottom": 10},
        "html": "",
        "text": "",
        "url": "",
        "content": null
	},
    initialize: function(desktop, options){
        this.setOptions(options);
        this.desktop = desktop;
        this.css = this.desktop.css;
    },
    load: function(){
        this.fireEvent("queryLoad");
        this.node = new Element("div#widget", {"styles": this.css.widgetNode}).inject(this.desktop.desktopNode);
        this.node.setStyles({
            "width": ""+this.options.width+"px",
            "height": ""+this.options.height+"px"
        });
        this.titleNode = new Element("div", {"styles": this.css.widgetTitleNode}).inject(this.node);
        this.titleOpenNode = new Element("div", {"styles": this.css.widgetTitleOpenNode}).inject(this.titleNode);
        this.titleCloseNode = new Element("div", {"styles": this.css.widgetTitleCloseNode}).inject(this.titleNode);
        this.titleTextNode = new Element("div", {
            "styles": this.css.widgetTitleTextNode,
            "text": this.options.title
        }).inject(this.titleNode);

        this.contentScrollNode = new Element("div", {"styles": this.css.widgetContentScrollNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.widgetContentNode}).inject(this.contentScrollNode);

        this.setSize();
        this.setEvent();

        this.fireEvent("queryLoadContent");
        this.loadContent();
        this.fireEvent("postLoadContent");

        this.fireEvent("postLoad");
    },
    setSize: function(){
        this.node.setStyles({
            "height": ""+this.options.height+"px",
            "width": ""+this.options.width+"px",
            "z-index": 10
        });

        if (this.options.position.top || this.options.position.top==0) this.node.setStyle("top", ""+this.options.position.top+"px");
        if (this.options.position.left || this.options.position.left==0) this.node.setStyle("left", ""+this.options.position.left+"px");
        if (this.options.position.right || this.options.position.right==0) this.node.setStyle("right", ""+this.options.position.right+"px");
        if (this.options.position.bottom || this.options.position.bottom==0) this.node.setStyle("bottom", ""+this.options.position.bottom+"px");

        var nodeSize = this.node.getComputedSize();
        var titleSize = this.titleNode.getSize();
        var mt = this.titleNode.getStyle("margin-top").toFloat();
        var mb = this.titleNode.getStyle("margin-bottom").toFloat();
        var smt = this.contentScrollNode.getStyle("margin-top").toFloat();
        var smb = this.contentScrollNode.getStyle("margin-bottom").toFloat();

        var y = nodeSize.height-nodeSize["padding-bottom"]-nodeSize["padding-top"]-titleSize.y-mt-mb-smt-smb;

        this.contentScrollNode.setStyle("height", ""+y+"px");

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.contentScrollNode, {
                "style":"xDesktop_Widget", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
                "onScroll": function(y){
                    this.fireEvent("scroll", [y]);
                }.bind(this)
            });
        }.bind(this));
    },

    setEvent: function(){
        this.node.makeDraggable({
            "handle": this.titleTextNode,
            "container": this.desktop.desktopNode,
            "onComplete": function(el, e){
                this.fireEvent("dragComplete", [el, e]);
            }.bind(this)
        });

        this.titleCloseNode.addEvent("click", function(){this.close();}.bind(this));
        this.titleOpenNode.addEvent("click", function(){this.open();}.bind(this));
    },
    close: function(){
        this.fireEvent("queryClose");
        this.node.destroy();
        this.fireEvent("postClose");
    },
    open: function(){
        this.fireEvent("open");
    },
    loadContent: function(){


    }
});
