MWF.xApplication.ftsearch.ElModule = new Class({
    Implements: [Events, Options],
    options:{
        id: ""
    },
    initialize: function(node, options, app, data){
        this.setOptions(options);
        this.container = $(node);
        this.data = data;
        this.app = app;
        this.load();
    },
    load: function(){
        this.container.appendHTML(this._createElementHtml());
        this.node = this.container.getFirst();
        this.node.addClass("o2_vue");

        if (!this.vm) this._loadVue(
            this._mountVueApp.bind(this)
        );
    },

    _loadVue: function(callback){
        if (!window.Vue){
            var vue = (o2.session.isDebugger) ? "vue_develop" : "vue";
            o2.loadAll({"css": "../o2_lib/vue/element/index.css", "js": [vue, "elementui"]}, { "sequence": true }, callback);
        }else{
            if (callback) callback();
        }
    },
    _mountVueApp: function(){
        if (!this.vueApp) this.vueApp = this._createVueExtend();
        this.vm = new Vue(this.vueApp);
        this.vm.$mount(this.node);
    },

    _createVueExtend: function(){
        var _self = this;
        var app = {
            data: this._createVueData(),
            mounted: function(){
                _self._afterMounted(this.$el);
            }
        };
        return app;
    },
    _createElementHtml: function(){

    },
    _createVueData: function(){

    },

    _afterMounted: function(el){
        this.node = el;
        // this.node.set({
        //     "id": this.json.id
        // });
        // this._loadVueCss();
        this.fireEvent("postLoad");
        this.fireEvent("load");
    },
    // _loadVueCss: function(){
    //     if (this.styleNode){
    //         this.node.removeClass(this.styleNode.get("id"));
    //     }
    //     if (this.json.vueCss && this.json.vueCss.code){
    //         this.styleNode = this.node.getParent().loadCssText(this.json.vueCss.code, {"notInject": true});
    //         this.styleNode.inject(this.node.getParent(), "before");
    //     }
    // },
});

MWF.xApplication.ftsearch.Input = new Class({
    Extends: MWF.xApplication.ftsearch.ElModule,
    options: {
        value: "",
        id: ""
    },
    _createVueData: function(){
        this.json = {
            data: this.data || this.options.value,
            readonly: false,
            disabled: false,
            clearable: true,
            editable: true,
            maxlength: "",
            minlength: "",
            showWordLimit: false,
            showPassword: false,
            size: "small",
            prefixIcon: "",
            suffixIcon: "",
            rows: 2,
            autosize: false,
            resize: "none",
            inputType: "text",
            description: "",
            click: function(){
                this.fireEvent("change", [this.json.data]);
            }.bind(this),
            change: function () {
                // debugger;
                this.fireEvent("change", [this.json.data]);
            }.bind(this),
            focus: function () {

            },
            blur: function () {

            },
            input: function () {

            },
            clear: function () {
                // debugger;
                this.fireEvent("change", [this.json.data]);
            }.bind(this)
        };
        // this.json[this.options.id] = this.data;
        return this.json;
    },
    _createElementHtml: function() {
        var html = "<div>";
        html += "<el-input";
        html += " v-model=\"data\"";
        html += " :maxlength=\"maxlength\"";
        html += " :minlength=\"minlength\"";
        html += " :show-word-limit=\"showWordLimit\"";
        html += " :show-password=\"showPassword\"";
        html += " :disabled=\"disabled\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :suffix-icon=\"suffixIcon\"";
        html += " :rows=\"rows\"";
        html += " :autosize=\"autosize\"";
        html += " :readonly=\"readonly\"";
        html += " :resize=\"resize\"";
        html += " :clearable=\"clearable\"";
        html += " :type=\"inputType\"";
        html += " :placeholder=\"description\"";
        html += " @change=\"change\"";
        html += " @focus=\"focus\"";
        html += " @blur=\"blur\"";
        html += " @input=\"input\"";
        html += " @clear=\"clear\"";

        // this.options.elEvents.forEach(function(k){
        //     html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        // });

        // if (this.json.elProperties){
        //     Object.keys(this.json.elProperties).forEach(function(k){
        //         if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
        //     }, this);
        // }
        //
        // if (this.json.elStyles) html += " :style=\"elStyles\"";

        html += ">";

        // html += "<el-button slot=\"append\">"+this.app.lp.ok+"</el-button>"

        // if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-input>";
        html += "</div>";
        return html;
    }
});

MWF.xApplication.ftsearch.DatePicker = new Class({
    Extends: MWF.xApplication.ftsearch.ElModule,
    options: {
        start: "",
        end: "",
        id: ""
    },
    _createVueData: function(){
        if(this.options.start)this.startDate = new Date(this.options.start);
        if(this.options.end)this.endDate = new Date(this.options.end);
        this.json = {
            data: this.data || [this.options.start, this.options.end],
            isReadonly: false,
            selectType: "datetimerange",
            disabled: false,
            clearable: true,
            editable: true,
            size: "small",
            prefixIcon: "",
            rangeSeparator: this.app.lp.rangeSeparator,
            startPlaceholder: this.app.lp.startPlaceholder,
            endPlaceholder: this.app.lp.endPlaceholder,
            description: "",
            arrowControl: true,
            format: "yyyy-MM-dd HH:mm:ss",
            pickerOptions: {
                firstDayOfWeek: 1,
                disabledDate: function(date){
                    if( this.startDate && date < this.startDate )return true;
                    if( this.endDate && date > this.endDate )return true;
                    return false;
                }.bind(this)
            },
            change: function () {
                this.fireEvent("change", [this.json.data]);
            }.bind(this),
            focus: function () {

            },
            blur: function () {

            }
        };
        // this.json[this.options.id] = this.data;
        return this.json;
    },
    _createElementHtml: function() {
        var html = "<el-date-picker";
        html += " v-model=\"data\"";
        html += " :type=\"selectType\"";
        html += " :readonly=\"isReadonly\"";
        html += " :disabled=\"disabled\"";
        html += " :editable=\"editable\"";
        html += " :clearable=\"clearable\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :range-separator=\"rangeSeparator\"";
        html += " :start-placeholder=\"startPlaceholder\"";
        html += " :end-placeholder=\"endPlaceholder\"";
        html += " :value-format=\"format\"";
        html += " :format=\"format\"";
        html += " :picker-options=\"pickerOptions\"";
        html += " :arrow-control=\"arrowControl\"";
        html += " @change=\"change\"";
        html += " @focus=\"focus\"";
        html += " @blur=\"blur\"";
        html += ">";
        html += "</el-date-picker>";
        return html;
    }
});

MWF.xApplication.ftsearch.NumberRange = new Class({
    Extends: MWF.xApplication.ftsearch.ElModule,
    options: {
        max: "100",
        min: "1",
        id: ""
    },
    checkNumber: function(num, type){
        debugger;
        var str = num;
        var len1 = str.substr(0, 1);
        var len2 = str.substr(1, 1);
        //如果第一位是0，第二位不是点，就用数字把点替换掉
        if (str.length > 1 && len1 === 0 && len2 !== ".") str = str.substr(1, 1);
        //第一位不能是.
        if (len1 === ".") str = "";
        //限制只能输入一个小数点
        if (str.indexOf(".") !== -1) {
            var str_ = str.substr(str.indexOf(".") + 1);
            if (str_.indexOf(".") !== -1) {
                str = str.substr(0, str.indexOf(".") + str_.indexOf(".") + 1);
            }
        }
        //正则替换
        str = str.replace(/[^\d^\.]+/g, '') // 保留数字和小数点
        return str
    },
    _createVueData: function(){
        this.data = {};
        this.json = {
            minData: this.options.min,
            maxData: this.options.max,
            min: {
                clearable: false,
                // click: function(){
                //     this.fireEvent("change", [this.json.data]);
                // }.bind(this),
                change: function () {
                    if( this.json.minData ){
                        var minData = (this.json.minData || 0).toFloat();
                        if( this.json.maxData ){
                            var maxData = (this.json.maxData || 0).toFloat();
                            if( minData > maxData ){
                                this.app.notice( this.app.lp.minGreatThanMaxError, "info", this.container);
                                this.json.maxData = this.json.minData;
                            }
                        }
                        if( minData < this.options.min.toFloat() ){
                            this.app.notice( this.app.lp.tooMinError, "info", this.container);
                            this.json.minData = this.options.min.toString();
                            return;
                        }
                        if( minData > this.options.max.toFloat() ){
                            this.app.notice( this.app.lp.tooMaxError, "info", this.container);
                            this.json.minData = this.options.max.toString();
                            return;
                        }
                    }
                    this.change();
                }.bind(this),
                focus: function () {},
                blur: function () {},
                oninput: function ( num ) {
                    return this.checkNumber(num, "min")
                }.bind(this),
                clear: function () {
                    this.change();
                }.bind(this)
            },
            max: {
                clearable: false,
                // click: function(){
                //     this.fireEvent("change", [this.json.data]);
                // }.bind(this),
                change: function () {
                    if( this.json.maxData ){
                        var maxData = (this.json.maxData || 0).toFloat();
                        if( this.json.minData ){
                            var minData = (this.json.minData || 0).toFloat();
                            if( minData > maxData ){
                                this.app.notice( this.app.lp.minGreatThanMaxError, "info", this.container);
                                this.json.minData = this.json.maxData;
                            }
                        }
                        if( maxData < this.options.min.toFloat() ){
                            this.app.notice(this.app.lp.tooMinError, "info", this.container);
                            this.json.maxData = this.options.min.toString();
                            return;
                        }
                        if( maxData > this.options.max.toFloat() ){
                            this.app.notice(this.app.lp.tooMaxError, "info", this.container);
                            this.json.maxData = this.options.max.toString();
                            return;
                        }
                    }
                    this.change();
                }.bind(this),
                focus: function () {},
                blur: function () {},
                oninput: function ( num ) {
                    return this.checkNumber(num, "max")
                }.bind(this),
                clear: function () {
                    this.change();
                }.bind(this)
            }
        };
        // this.json[this.options.id] = this.data;
        return this.json;
    },
    change: function(){
        debugger;
        var minData = this.json.minData;
        if( !minData )minData = this.options.min;
        var maxData = this.json.maxData;
        if( !maxData )maxData = this.options.max;
        this.fireEvent("change", [[minData.toFloat(), maxData.toFloat()]]);
    },
    _createElementHtml: function() {
        var html = "<div>";
        html += "<span style='color:#999;padding-right: 5px;'>"+this.app.lp.min+":"+this.options.min+"</span>";
        html += "<el-input style=\"width:200px;\"";
        html += " v-model=\"minData\"";
        html += " size=small";
        html += " :clearable=\"min.clearable\"";
        html += " type=text";
        html += " placeholder="+this.app.lp.fromValue;
        html += " @change=\"min.change\"";
        html += " @focus=\"min.focus\"";
        html += " @blur=\"min.blur\"";
        html += " @input=\"min.input\"";
        html += " @clear=\"min.clear\"";
        html += "@keyup.native=\"minData = min.oninput(minData)\"";
        html += ">";
        html += "</el-input>";
        html += "<span style='padding: 0px 5px;'>"+this.app.lp.to+"</span>";
        html += "<el-input style=\"width:200px;\"";
        html += " v-model=\"maxData\"";
        html += " size=small";
        html += " :clearable=\"max.clearable\"";
        html += " type=text";
        html += " placeholder="+this.app.lp.toValue;
        html += " @change=\"max.change\"";
        html += " @focus=\"max.focus\"";
        html += " @blur=\"max.blur\"";
        html += " @input=\"max.input\"";
        html += " @clear=\"max.clear\"";
        html += "@keyup.native=\"maxData = max.oninput(maxData)\"";
        html += ">";
        html += "</el-input>";
        html += "<span style='color:#999;padding-left: 5px;'>"+this.app.lp.max+":"+this.options.max+"</span>";
        html += "</div>";
        return html;
    }
})

// MWF.xApplication.ftsearch.Slider = new Class({
//     Extends: MWF.xApplication.ftsearch.ElModule,
//     options: {
//         max: "100",
//         min: "1",
//         id: ""
//     },
//     _createVueData: function(){
//         this.json = {
//             data: this.data || [this.options.min, this.options.max],
//             isReadonly: false,
//             clearable: true,
//             editable: true,
//             max: this.options.max.toFloat(),
//             min: this.options.min.toFloat(),
//             step: 1,
//             showStops: false,
//             range: true,
//             vertical: false,
//             height: "100px",
//             showInput: false,
//             showInputControls: false,
//             inputSize: "small",
//             showTooltip: true,
//             tooltipClass: "",
//             disabled: false,
//             marks: {
//             },
//             click: function(){
//                 this.fireEvent("change", [this.json.data]);
//             }.bind(this),
//             change: function () {
//                 this.fireEvent("change", [this.json.data]);
//             }.bind(this),
//             input: function () {
//             }
//         };
//         var half = (this.options.max.toFloat() - this.options.min.toFloat()) / 2;
//         this.json.marks[this.options.min] = this.options.min.toString();
//         this.json.marks[half] = half.toString();
//         this.json.marks[this.options.max] = this.options.max.toString();
//         return this.json;
//     },
//     _createElementHtml: function() {
//         var html = "<div style='height: 50px; width:calc( 100% - 20px )'>";
//         html += "<el-slider";
//         html += " v-model=\"data\"";
//         html += " :max=\"max\"";
//         html += " :min=\"min\"";
//         html += " :step=\"step\"";
//         html += " :show-stops=\"showStops\"";
//         html += " :range=\"range\"";
//         html += " :vertical=\"vertical\"";
//         html += " :height=\"height\"";
//         html += " :show-input=\"showInput\"";
//         html += " :show-input-controls=\"showInputControls\"";
//         html += " :input-size=\"inputSize\"";
//         html += " :show-tooltip=\"showTooltip\"";
//         html += " :tooltip-class=\"tooltipClass\"";
//         html += " :disabled=\"disabled\"";
//         html += " :marks=\"marks\"";
//         html += " @change=\"change\"";
//         html += " @focus=\"input\"";
//         html += ">";
//         html += "</el-slider>";
//         html += "</div>";
//         return html;
//     }
// })