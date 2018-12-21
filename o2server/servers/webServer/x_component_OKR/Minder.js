MWF.xApplication.OKR = MWF.xApplication.OKR || {};

MWF.xApplication.OKR.Minder = new Class({
    Implements: [Options],
    options: {
        "style": "default",
        "template" : "default",
        "theme": "fresh-blue"//"fresh-blue-compat"
    },
    initialize: function (container, explorer, data, options) {
        this.setOptions(options);
        this.app = explorer.app;
        this.lp = this.app.lp;
        this.actions = this.app.actions;
        this.container = container;
        this.css = explorer.css;
        this.data = data;

        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%"}
            //"type" : "application/kityminder",
            //"minder-data-type" : "json"
        }).inject(this.container)

    },
    destory : function(){
        if(this.km)delete this.km;
        if(this.node)this.node.destory();
        delete this;
    },
    load: function (callback) {
        this.loadResource(function () {
            this.loadKityMinder();
            if (callback)callback();
        }.bind(this))
    },
    loadResource: function (callback) {
        var kityminderPath = "/x_desktop/res/framework/kityminder/";

        COMMON.AjaxModule.loadCss(kityminderPath + "core/src/kityminder.css", function () {
            COMMON.AjaxModule.load(kityminderPath + "kity/kity.js", function () {
                COMMON.AjaxModule.load(kityminderPath + "core/dist/kityminder.core.js", function () {
                    if (callback)callback();
                }.bind(this));
            }.bind(this))
        }.bind(this))
    },
    loadKityMinder: function () {
        var _self = this;
        // 创建 km 实例
        /* global kityminder */
        var km = this.km = new kityminder.Minder();
        //var target = document.querySelector('#minder-view');
        km.renderTo(this.node);

        this.data.theme = this.data.theme || this.options.theme;
        this.data.template = this.data.template || this.options.template;

        km.importJson(this.data);
        if( this.options.onClickKMNode ){
            km.on("execCommand", function (e) {
                if (e.commandName === "camera") {
                    var nodes = km.getAllNode();
                    nodes.forEach(function (node) {
                        var container = node.getRenderContainer();
                        container.node.addEventListener("click", function () {
                            //alert(JSON.stringify((this.getData())))
                            _self.options.onClickKMNode( this, this.getData() )
                        }.bind(node));
                    })
                }
            });
        }
        km.execCommand('camera');
    }
})