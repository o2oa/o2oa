/**
 * Created by CXY on 2017/6/13.
 */
o2.widget = o2.widget || {};
o2.widget.ImageViewer = o2.ImageViewer = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "path": o2.session.path + "/widget/$ImageViewer/",
        "imageUrl": ""
    },
    initialize: function (node, options) {
        this.node = node;
        this.setOptions(options);

        this.path = this.options.path || (o2.session.path + "/widget/$ImageViewer/");
        this.cssPath = this.path + this.options.style + "/css.wcss";

        this._loadCss();
        this.fireEvent("init");
    },
    load: function (imageBase64) {
        this.container = new Element("div.container", {styles: this.css.container}).inject(this.node);

        this.container.addEvent("selectstart", function (e) {
            e.preventDefault();
            e.stopPropagation();
        });

        if (!this.checkBroswer())return;

        this.lastPoint = null;

        this.loadToolBar();

        this.contentNode = new Element("div.contentNode", {styles: this.css.contentNode}).inject(this.container);
        this.loadEditorNode();
        this.loadResultNode();

        if (this.options.description) {
            this.loadDescriptionNode();
        }

        if (this.options.imageUrl) {
            this.loadImageAsUrl(this.options.imageUrl);
        }
        if (imageBase64) {
            this.loadImageAsFile(this.base64ToBlob(imageBase64));
        }
    }
});