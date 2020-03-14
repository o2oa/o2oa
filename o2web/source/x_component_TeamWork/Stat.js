MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};

MWF.xApplication.TeamWork.Stat = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "mvcStyle": "style.css"
    },
    initialize: function (container, app, data, options) {
        this.setOptions(options);
        this.container = container;

        this.app = app;
        this.lp = this.app.lp.Stat;
        //this.actions = this.app.restActions;
        this.actions = o2.Actions.load("x_teamwork_assemble_control");

        this.path = "/x_component_TeamWork/$Stat/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        if (this.options.mvcStyle) this.stylePath = this.path + this.options.style + "/" + this.options.mvcStyle;

        this.data = data;
    },
    load: function () {
        this.container.empty();
        if(this.options.mvcStyle) this.container.loadCss(this.stylePath);

        var url = this.path+this.options.style+"/view.html";
        //o2.Actions.load("x_processplatform_assemble_surface").TaskAction.listMyPaging(1,20, function(json){

            this.container.loadHtml(url, {"bind": {"lp": this.lp, "data": {}}, "module": this}, function(){

            }.bind(this));

            // this.content.loadHtml(url, {"bind": {"lp": this.lp, "data": json}, "module": this}, function(){
            // 	this.doSomething();
            // }.bind(this));
            //
            // o2.load(["js1", "js2"], {}, function(){});	//js
            //
            // o2.loadCss	//css
            // o2.loadHtml("", {"dom": this.content})
            // o2.loadAll	//js,css,html
            //
            // o2.loadAll({
            // 	"css": [],
            // 	"js":[],
            // 	"html": []
            // },
            // 	)
            //



        //}.bind(this));




    },
    loadTask:function(){
        alert("loadtask1")
    },


});
