MWF.xApplication.BAM.options.multitask = false;
MWF.xDesktop.requireApp("BAM", "Actions.RestActions", null, false);
MWF.require("MWF.widget.Tab", null, false);
//MWF.require("MWF.widget.UUID", null, false);
MWF.require("MWF.widget.MaskNode", null, false);
MWF.xApplication.BAM.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "BAM",
		"icon": "icon.png",
		"width": "1240",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.BAM.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.BAM.LP;
        this.actions = MWF.Actions.get("x_processplatform_assemble_bam");
        //this.actions = new MWF.xApplication.BAM.Actions.RestActions();
	},
    loadApplication: function(callback){
        this.createNode();
        this.createLayout(callback);
    },
    createNode: function(){
        this.contentNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.content);
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.contentNode);
    },
    createLayout: function(callback){
        this.tabNode = new Element("div", {"styles": this.css.tabNode}).inject(this.node);
        this.tab = new MWF.widget.Tab(this.tabNode, {"style": "administrator"});
        this.tab.load();

        this.summaryNode = new Element("div", {"styles": this.css.summaryNode});
        this.monthlyNode = new Element("div", {"styles": this.css.monthlyNode});
        this.summaryPage = this.tab.addTab(this.summaryNode, this.lp.summaryTitle);

        // 临时隐藏按月统计，等待查询效率提升后再开放。
        // this.monthlyPage = this.tab.addTab(this.monthlyNode, this.lp.monthlyTitle);
        // this.monthlyPage.addEvent("show", function(){
        //     this.clearMonthly();
        //     this.loadMonthly();
        // }.bind(this));

        this.summaryPage.addEvent("show", function(){
            this.clearSummary();
            this.loadSummary();
            if (callback) callback();
        }.bind(this));

        this.summaryPage.showTabIm();
    },

    clearSummary: function(){
        if (this.summary){
            this.summary.destroy();
            this.summary = null;
        }
    },
    loadSummary: function(){
        //this.app.content.mask();
        this.mask();
        MWF.xDesktop.requireApp("BAM", "Summary", function(){
            this.summary = new MWF.xApplication.BAM.Summary(this, this.summaryNode, {
                "onLoaded": function(){
                    this.unmask();
                }.bind(this)
            });
        }.bind(this));
    },

    clearMonthly: function(){
        if (this.monthly){
            this.monthly.destroy();
            this.monthly = null;
        }
    },
    loadMonthly: function(){
        //this.app.content.mask();
        this.mask();
        MWF.xDesktop.requireApp("BAM", "Monthly", function(){
            this.monthly = new MWF.xApplication.BAM.Monthly(this, this.monthlyNode, {
                "onLoaded": function(){
                    this.unmask();
                }.bind(this)
            });
        }.bind(this));
    },
    mask: function(){
        if (!this.maskNode){
            this.maskNode = new MWF.widget.MaskNode(this.content, {"style": "bam"});
            this.maskNode.load();
        }
    },
    unmask: function(){
        if (this.maskNode) this.maskNode.hide(function(){
            MWF.release(this.maskNode);
            this.maskNode = null;
        }.bind(this));
    }

});
