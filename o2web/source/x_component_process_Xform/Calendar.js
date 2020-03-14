MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.xApplication.process.Xform.Calendar = MWF.APPCalendar =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Input,
	iconStyle: "calendarIcon",
    options: {
        "moduleEvents": ["queryLoad","postLoad","load","complete", "clear", "change"]
    },
    _loadNode: function(){
        if (this.readonly || this.json.isReadonly){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            this.descriptionNode.addEvents({
                "mousedown": function(){
                    this.descriptionNode.setStyle("display", "none");
                    this.clickSelect();
                }.bind(this)
            });
        }
    },
    getValue: function(isDate){
        var value = this._getBusinessData();
        if( value && !isDate)return value;

        if (!value) value = this._computeValue();
        var d = (!!value) ? Date.parse(value) : "";
        if (isDate){
            return d || null;
        }else{
            //if (d) value = Date.parse(value).format(this.json.format);
            return (d) ? d.format(this.json.format) : "";
        }
    },
    getValueStr : function(){
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
        return value;
    },
	clickSelect: function(){
        var _self = this;
        if (!this.calendar){
            MWF.require("MWF.widget.Calendar", function(){
                var options = {
                    "style": layout.mobile ? "xform_mobile" : "xform",
                    "secondEnable" : this.json.isSelectSecond,
                    "isTime": (this.json.selectType==="datetime" || this.json.selectType==="time"),
                    "timeOnly": (this.json.selectType === "time"),
                    //"target": this.form.node,
                    "target": this.form.app.content,
                    "format": this.json.format,
                    "onComplate": function(formateDate, date){
                        this.validationMode();
                        if(this.validation())this._setBusinessData(this.getInputData("change"));
                        this.fireEvent("complete");
                    }.bind(this),
                    "onChange": function(){
                        this.fireEvent("change");
                    }.bind(this),
                    "onClear": function(){
                        this.validationMode();
                        if(this.validation())this._setBusinessData(this.getInputData("change"));
                        this.fireEvent("clear");
                        if (!this.node.getFirst().get("value")) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
                    }.bind(this),
                    "onShow": function(){
                        if (_self.descriptionNode) _self.descriptionNode.setStyle("display", "none");

                        if( layout.mobile ){
                            this.container.position({
                                relativeTo: $(document.body),
                                position: 'leftCenter',
                                edge: 'leftCenter'
                                //offset : { y : -25 }
                            });
                        }

                    },
                    "onHide": function(){
                        if (!this.node.getFirst().get("value")) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
                    }.bind(this)
                };
                options.baseDate = this.getBaseDate();
                this.calendar = new MWF.widget.Calendar(this.node.getFirst(), options);
                this.calendar.show();
            }.bind(this));
        }else{
            var options = {};
            options.baseDate = this.getBaseDate();
            this.calendar.setOptions(options);
            //this.calendar.show();
            this.node.getFirst().focus();
        }
	},
    getBaseDate : function(){
        var d;
        var value = this.getValue(true);
        if( value && value.getTime() > 10000 ){
            d = value;
        }else{
            var ud = Date.parse( this.unformatDate( this.getValueStr() ) );
            if( ud && ud.getTime() > 10000 ){
                d = ud;
            }else{
                d = new Date();
            }
        }
        return d;
    },
    unformatDate : function( dateStr ){
        var formatStr = this.json.format;
        var matchArr = [ "%Y", "%m", "%d", "%H", "%M", "%S", "%z", "%Z" ];
        var lengthArr = [ 4, 2, 2, 2, 2, 2, 5, 3];
        var indexArr = [ formatStr.indexOf("%Y"), formatStr.indexOf("%m"), formatStr.indexOf("%d"), formatStr.indexOf("%H"), formatStr.indexOf("%M"), formatStr.indexOf("%S"), formatStr.indexOf("%z"), formatStr.indexOf("%Z") ];
        var resultArr = [ null, null, null, null, null, null, null, null ];
        for( var i=0; i<matchArr.length; i++ ){
            if( indexArr[i] === -1 )continue;
            var leftLength = 0;
            var leftUnitLength = 0;
            Array.each( indexArr, function( n, k ){
                if( n === -1 )return;
                if( indexArr[i] > n ){
                    leftLength += lengthArr[k];
                    leftUnitLength += matchArr[k].length;
                }
            });
            resultArr[i] = dateStr.substr( indexArr[i] - leftUnitLength + leftLength, lengthArr[i] );
        }
        var now = new Date();
        for( var i=0; i < resultArr.length; i++ ){
            if( !resultArr[i] ){
                switch ( matchArr[i] ){
                    case "%Y":
                    case "%m":
                    case "%d":
                        resultArr[i] = now.format( matchArr[i] );
                        break;
                    case "%H":
                    case "%M":
                    case "%S":
                        resultArr[i] = "00";
                        break;
                    case "%z":
                    case "%Z":
                    default:
                        break;
                }
            }
        }
        return resultArr[0] + "-" + resultArr[1] + "-" + resultArr[2] + " " + resultArr[3]+":"+resultArr[4]+":"+resultArr[5];
    }
}); 