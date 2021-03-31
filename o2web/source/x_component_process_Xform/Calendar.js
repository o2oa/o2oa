MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
/** @class Calendar 日期组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 * @extends MWF.xApplication.process.Xform.$Input
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Calendar = MWF.APPCalendar =  new Class(
    /** @lends MWF.xApplication.process.Xform.Calendar# */
{
	Implements: [Events],
	Extends: MWF.APP$Input,
	iconStyle: "calendarIcon",
    options: {
        /**
         * 日期选择完成时触发.
         * @event MWF.xApplication.process.Xform.Calendar#complete
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 日期选择器上点清空时触发.
         * @event MWF.xApplication.process.Xform.Calendar#clear
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 值改变时触发.
         * @event MWF.xApplication.process.Xform.Calendar#change
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 显示日期选择器时触发.
         * @event MWF.xApplication.process.Xform.Calendar#show
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 隐藏日期选择器时触发.
         * @event MWF.xApplication.process.Xform.Calendar#hide
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["queryLoad","postLoad","load","complete", "clear", "change","show","hide"]
    },
    _loadNode: function(){
        if (this.readonly || this.json.isReadonly){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
            var input = this.node.getFirst();
            input.set("readonly", true);
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            this.descriptionNode.addEvents({
                "mousedown": function(){
                    this.descriptionNode.setStyle("display", "none");
                    //this.clickSelect();
                }.bind(this)
            });
        }
    },
    _getValueAg: function(value,isDate){
        if (value && value.isAG){
            return value.then(function(v){
                this._getValueAg(v, isDate);
            }.bind(this), function(){});
        }else{
            var d = (!!value) ? Date.parse(value) : "";
            if (isDate){
                return d || null;
            }else{
                return (d) ? d.format(this.json.format) : "";
            }
        }
    },
    getValue: function(isDate){
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if( value && !isDate)return value;
        if (!value) value = this._computeValue();
        if (value.then) return value;

        var d = (!!value) ? Date.parse(value) : "";
        if (isDate){
            return d || null;
        }else{
            //if (d) value = Date.parse(value).format(this.json.format);
            return (d) ? d.format(this.json.format) : "";
        }

        return value || "";
    },
    getValueStr : function(){
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
        return value;
    },

    __setValue: function(value){
        var v;
        if( typeOf( value ) === "date" ){
            v = (value) ? ( Date.parse(value)).format(this.json.format) : "";
        }else{
            v = value;
        }
        this._setBusinessData(value);
        if (this.node.getFirst()) this.node.getFirst().set("value", v || "");
        if (this.readonly || this.json.isReadonly) this.node.set("text", v);
        this.moduleValueAG = null;
        return value;
    },

	clickSelect: function(){

        var _self = this;
        if (!this.calendar){
            MWF.require("MWF.widget.Calendar", function(){
                var defaultView = "day";
                if( this.json.selectType === "month" )defaultView = "month";
                if( this.json.selectType === "year" )defaultView = "year";
                var options = {
                    "style": layout.mobile ? "xform_mobile" : "xform",
                    "secondEnable" : this.json.isSelectSecond,
                    "isTime": (this.json.selectType==="datetime" || this.json.selectType==="time"),
                    "timeOnly": (this.json.selectType === "time"),
                    "monthOnly" : (this.json.selectType === "month"),
                    "yearOnly" : (this.json.selectType === "year"),
                    "defaultView" : defaultView,
                    //"target": this.form.node,
                    "target": layout.mobile ? $(document.body) : this.form.app.content,
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
                        }else{
                            var parent = _self.node.getParent();
                            while( parent ){
                                var overflow = parent.getStyle("overflow");
                                var overflowY = parent.getStyle("overflow-y");
                                if(  overflow === "auto" || overflow === "scroll" || overflowY === "auto" || overflowY === "scroll" ){
                                    _self.scrollFun = function( e ){
                                        if (this.container.position ) {
                                            this.container.position({
                                                relativeTo: this.node,
                                                position: 'bottomLeft',
                                                edge: 'upperLeft',
                                                allowNegative : true
                                            });
                                        }
                                    }.bind(this);
                                    _self.scrollParentNode = parent;
                                    parent.addEvent( "scroll", _self.scrollFun );
                                    parent = null;
                                }else{
                                    parent = parent.getParent();
                                }
                            }
                        }
                        _self.fireEvent("show");
                    },
                    "onHide": function(){
                        if (!this.node.getFirst().get("value")) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
                        if( _self.scrollParentNode && _self.scrollFun ){
                            _self.scrollParentNode.removeEvent("scroll", _self.scrollFun);
                        }
                        _self.fireEvent("hide");
                    }.bind(this)
                };
                options.baseDate = this.getBaseDate();
                /**
                 * @summary 日期弹出选择界面，只读情况下无此成员.
                 * @member {MWF.widget.Calendar}
                 * @example
                 * var calendar = this.form.get("fieldId").calendar; //获取组件
                 * if(calendar)calendar.show(); //弹出选择组件
                 */
                this.calendar = new MWF.widget.Calendar(this.node.getFirst(), options);
                if( this.form.json && this.form.json.canlendarStyle && typeOf( this.form.json.canlendarStyle.zIndex ) !== "null" && typeOf( this.form.json.canlendarStyle.zIndex ) !== "undefined" ){
                    this.calendar.container.setStyle("z-index", this.form.json.canlendarStyle.zIndex );
                }
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
	    debugger;
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
