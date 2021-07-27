


MWF.xDesktop.requireApp("Collect", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Collect", "Main", null, false);
MWF.xApplication.Collect.MainInContainer = new Class({
    Extends: MWF.xApplication.Collect.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "Collect",
        "sideBarEnable" : false
    },
    initialize: function(desktop, options, container, content){
      this.setOptions(options);
      this.desktop = desktop;
      this.container = container;
      this.content = content;
      this.path = "../x_component_"+this.options.name.replace(/\./g, "_")+"/$Main/";
      this.options.icon = this.path+this.options.style+"/"+this.options.icon;

      this.cssPath =this.path+this.options.style+"/css.wcss" ;
      this.inBrowser = true;
      this.inContainer = true;
      this._loadCss();
  },
  loadInBrowser: function(){
    this.window = {
        "isHide": false,
        "isMax": true,
        "maxSize": function(){},
        "restore": function(){},
        "setCurrent": function(){},
        "hide": function(){},
        "maxOrRestoreSize": function(){},
        "restoreSize": function(){},
        "close": function(){},
        "titleText" : {
            set : function(){}
        }
    };
    this.window.content = this.content;

    this.fireAppEvent("postLoadWindow");
    this.fireAppEvent("queryLoadApplication");
    this.setContentEvent();
    this.loadApplication(function(){
        this.fireAppEvent("postLoadApplication");
    }.bind(this));
    this.fireAppEvent("postLoad");
},
});