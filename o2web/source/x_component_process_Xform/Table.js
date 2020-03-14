MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Table = MWF.APPTable =  new Class({
	Extends: MWF.APP$Module,
	_afterLoaded: function(){
        if (!this.table) this.table = this.node.getElement("table");
		//var tds = this.node.getElements("td");
		var rows = this.table.rows;
		for (var i=0; i<rows.length; i++){
		    var row = rows[i];
            for (var j=0; j<row.cells.length; j++){
                var td = row.cells[j];

                var json = this.form._getDomjson(td);
                if (json){
                    var table = this;
                    var module = this.form._loadModule(json, td, function(){
                        this.table = table;
                    });
                }


                this.form.modules.push(module);
            }
        }

        // this.table.rows.each(function(row){
        //     row.cells.each(function(td){
        //         var json = this.form._getDomjson(td);
        //         var table = this;
        //         var module = this.form._loadModule(json, td, function(){
        //             this.table = table;
        //         });
        //
        //         this.form.modules.push(module);
        //     }.bind(this));
        // }.bind(this));


		// tds.each(function(td){
		// 	var json = this.form._getDomjson(td);
         //    var table = this;
		// 	var module = this.form._loadModule(json, td, function(){
         //        this.table = table;
         //    });
        //
		// 	this.form.modules.push(module);
		// }.bind(this));
	},
    _loadBorderStyle: function(){
        if (this.json.styles.border){
            if (!this.table) this.table = this.node.getElement("table");
            if( this.json.styles["table-layout"] ){
                this.table.setStyle("table-layout",this.json.styles["table-layout"]);
            }
            this.table.set("cellspacing", "0");
            this.table.setStyles({
                "border-top": this.json.styles.border,
                "border-left": this.json.styles.border
            });
            var ths = this.table.getElements("th");
            ths.setStyles({
                "border-bottom": this.json.styles.border,
                "border-right": this.json.styles.border
            });
            var tds = this.table.getElements("td");
            tds.setStyles({
                "border-bottom": this.json.styles.border,
                "border-right": this.json.styles.border,
                "background": "transparent"
            });
        }
    },
    _loadStyles: function(){
        Object.each(this.json.styles, function(value, key){
            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                this.node.setStyle(key, value);
            }
        }.bind(this));
        this._loadBorderStyle();
    }
});
MWF.xApplication.process.Xform.Table$Td = MWF.APPTable$Td =  new Class({
	Extends: MWF.APP$Module,
    _queryLoaded: function(){

    },
    _afterLoaded: function(){
        //this.form._loadModules(this.node);
    },
    _loadStyles: function(){
        var addStyles = {};
        if (this.json.cellType=="title"){
            addStyles = this.table.json.titleTdStyles
        }
        if (this.json.cellType=="content"){
            addStyles = this.table.json.contentTdStyles
        }
        if (this.json.cellType=="layout"){
            addStyles = this.table.json.layoutTdStyles
        }
        this.node.setStyles(addStyles);
        this.node.setStyles(this.json.styles);

        if (this.json.cellType=="content"){
            this.form.addEvent("postLoad", function(){
                var inputs = this.node.getElements("input");
                inputs.each(function(input){
                    var inputType = input.get("type").toLowerCase();
                    if (inputType!="radio" && inputType!="checkbox" && inputType!="submit" && inputType!="buttom" && inputType!="image"){
                        input.setStyle("width", "100%");
                    }
                }.bind(this));
                var textareas = this.node.getElements("textarea");
                textareas.each(function(textarea){
                    textarea.setStyle("width", "100%");
                }.bind(this));

            }.bind(this))
        }
    }
});