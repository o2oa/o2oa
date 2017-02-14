MWF.xDesktop.requireApp("cms.Xform", "$Input", null, false);
MWF.xApplication.cms.Xform.Calendar = MWF.CMSCalendar =  new Class({
	Implements: [Events],
	Extends: MWF.CMS$Input,
	iconStyle: "calendarIcon",
    options: {
        "moduleEvents": ["complete", "clear"]
    },
	
	clickSelect: function(){
        if (!this.calendar){
            MWF.require("MWF.widget.Calendar", function(){
                this.calendar = new MWF.widget.Calendar(this.node, {
                    "style": "xform",
                    "isTime": (this.json.selectType=="datetime") ? true : false,
                    //"target": this.form.node,
                    "target": this.form.app.content,
                    "format": this.json.format,
                    "onComplate": function(){
                        this.validationMode();
                        this.validation();
                        this.fireEvent("complete");
                    }.bind(this),
                    "onClear": function(){
                        this.validationMode();
                        this.validation();
                        this.fireEvent("clear");
                    }.bind(this),
                });
                this.calendar.show();
            }.bind(this));
        }else{
            this.node.focus();
        }
	}
}); 