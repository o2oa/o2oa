MWF.xApplication.Calculator.options.multitask = true;
MWF.xApplication.Calculator.Main = new Class({
	    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Calculator",
		"icon": "icon.png",
		"width": "320",
		"height": "440",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.Calculator.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Calculator.LP;
	},
    init: function(){
        this.resultData = 0;
        this.currentOperation = "";
        this.startNumber = true;
    },
    loadDecimal: function(callback){
        if (!window.Decimal){
            var url = "/x_component_Calculator/Decimal.js";
            COMMON.AjaxModule.loadDom(url, function(){
            //    COMMON.AjaxModule.loadDom(this.tools, function(){
                    if (callback) callback();
            //    }.bind(this))
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
	loadApplication: function(callback){
        this.loadDecimal(function(){
            this.init();
            this.node = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
            this.screenNode = new Element("div", {"styles": this.css.screenNode}).inject(this.node);
            this.buttonAreaNode = new Element("div", {"styles": this.css.buttonAreaNode}).inject(this.node);

            this.screenReferenceNode = new Element("div", {"styles": this.css.screenReferenceNode}).inject(this.screenNode);
            this.screenContentNode = new Element("div", {"styles": this.css.screenContentNode, "text": "0"}).inject(this.screenNode);

            var html = "<table border='0' cellSpacing='8' cellPadding='0' width='100%'>" +
                "<tr style='height: 30px'>" +
                "<td style='width: 42px'><div class='but1' id='calculator_mc'>MC</div></td>" +
                "<td style='width: 42px'><div class='but1' id='calculator_mr'>MR</div></td>" +
                "<td style='width: 42px'><div class='but1' id='calculator_ms'>MS</div></td>" +
                "<td style='width: 42px'><div class='but1' id='calculator_madd'>M+</div></td>" +
                "<td style='width: 42px'><div class='but1' id='calculator_mminus'>M-</div></td></tr>" +

                "<tr style='height: 30px'>" +
                "<td style='width: 42px'><div class='but2' id='calculator_back'></div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_ce'>CE</div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_c'>C</div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_pm'>+-</div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_sqrt'></div></td></tr>" +

                "<tr style='height: 30px'>" +
                "<td style='width: 42px'><div class='but3' id='calculator_7'>7</div></td>" +
                "<td style='width: 42px'><div class='but3' id='calculator_8'>8</div></td>" +
                "<td style='width: 42px'><div class='but3' id='calculator_9'>9</div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_div'>/</div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_mod'>%</div></td></tr>" +

                "<tr style='height: 30px'>" +
                "<td style='width: 42px'><div class='but3' id='calculator_4'>4</div></td>" +
                "<td style='width: 42px'><div class='but3' id='calculator_5'>5</div></td>" +
                "<td style='width: 42px'><div class='but3' id='calculator_6'>6</div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_mul'>*</div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_inverse'>1/x</div></td></tr>" +

                "<tr style='height: 30px'>" +
                "<td style='width: 42px'><div class='but3' id='calculator_1'>1</div></td>" +
                "<td style='width: 42px'><div class='but3' id='calculator_2'>2</div></td>" +
                "<td style='width: 42px'><div class='but3' id='calculator_3'>3</div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_sub'>-</div></td>" +
                "<td rowspan='2'><div id='calculator_equal'>=</div></td></tr>" +

                "<tr style='height: 30px'>" +
                "<td colspan='2'><div id='calculator_0'>0</div></td>" +
                "<td style='width: 42px'><div class='but3' id='calculator_dot'>.</div></td>" +
                "<td style='width: 42px'><div class='but2' id='calculator_add'>+</div></td></tr>" +

                "</table>";
            this.buttonAreaNode.set("html", html);
            var but1 = this.buttonAreaNode.getElements(".but1");
            but1.setStyles(this.css.buttonStyle1);
            var but2 = this.buttonAreaNode.getElements(".but2");
            but2.setStyles(this.css.buttonStyle2);
            var but3 = this.buttonAreaNode.getElements(".but3");
            but3.setStyles(this.css.buttonStyle3);
            var _self = this;
            but1.addEvents({
                "mouseover": function(){this.setStyles(_self.css.button_over);},
                "mouseout": function(){this.setStyles(_self.css.buttonStyle1);},
                "mousedown": function(){this.setStyles(_self.css.button_down);},
                "mouseup": function(){this.setStyles(_self.css.button_over);},
            });
            but2.addEvents({
                "mouseover": function(){this.setStyles(_self.css.button_over);},
                "mouseout": function(){this.setStyles(_self.css.buttonStyle2);},
                "mousedown": function(){this.setStyles(_self.css.button_down);},
                "mouseup": function(){this.setStyles(_self.css.button_over);},
            });
            but3.addEvents({
                "mouseover": function(){this.setStyles(_self.css.button_over);},
                "mouseout": function(){this.setStyles(_self.css.buttonStyle3);},
                "mousedown": function(){this.setStyles(_self.css.button_down);},
                "mouseup": function(){this.setStyles(_self.css.button_over);},
            });


            this.but_equal = this.buttonAreaNode.getElement("#calculator_equal");
            this.but_equal.setStyles(this.css.but_equal);
            this.but_equal.addEvents({
                "mouseover": function(){this.setStyles(_self.css.but_equal_over);},
                "mouseout": function(){this.setStyles(_self.css.but_equal);},
                "mousedown": function(){this.setStyles(_self.css.but_equal_down);},
                "mouseup": function(){this.setStyles(_self.css.but_equal_over);},
            });

            this.but_0 = this.buttonAreaNode.getElement("#calculator_0");
            this.but_0.setStyles(this.css.but_0);
            this.but_0.addEvents({
                "mouseover": function(){this.setStyles(_self.css.but_0_over);},
                "mouseout": function(){this.setStyles(_self.css.but_0);},
                "mousedown": function(){this.setStyles(_self.css.but_0_down);},
                "mouseup": function(){this.setStyles(_self.css.but_0_over);},
            });

            this.but_back = this.buttonAreaNode.getElement("#calculator_back");
            var icon = new Element("div", {"styles": this.css.but_back_icon}).inject(this.but_back);

            this.but_sqrt = this.buttonAreaNode.getElement("#calculator_sqrt");
            icon = new Element("div", {"styles": this.css.but_sqrt_icon}).inject(this.but_sqrt);

            this.getButtons();
            this.setEvent();
        }.bind(this));
	},
    getButtons: function(){
        if (!this.but_mc) this.but_mc = this.buttonAreaNode.getElement("#calculator_mc");
        if (!this.but_mr) this.but_mr = this.buttonAreaNode.getElement("#calculator_mr");
        if (!this.but_ms) this.but_ms = this.buttonAreaNode.getElement("#calculator_ms");
        if (!this.but_madd) this.but_madd = this.buttonAreaNode.getElement("#calculator_madd");
        if (!this.but_mminus) this.but_mminus = this.buttonAreaNode.getElement("#calculator_mminus");

        if (!this.but_back) this.but_mminus = this.buttonAreaNode.getElement("#calculator_back");
        if (!this.but_ce) this.but_ce = this.buttonAreaNode.getElement("#calculator_ce");
        if (!this.but_c) this.but_c = this.buttonAreaNode.getElement("#calculator_c");
        if (!this.but_pm) this.but_pm = this.buttonAreaNode.getElement("#calculator_pm");
        if (!this.but_sqrt) this.but_sqrt = this.buttonAreaNode.getElement("#calculator_sqrt");

        if (!this.but_7) this.but_7 = this.buttonAreaNode.getElement("#calculator_7");
        if (!this.but_8) this.but_8 = this.buttonAreaNode.getElement("#calculator_8");
        if (!this.but_9) this.but_9 = this.buttonAreaNode.getElement("#calculator_9");
        if (!this.but_div) this.but_div = this.buttonAreaNode.getElement("#calculator_div");
        if (!this.but_mod) this.but_mod = this.buttonAreaNode.getElement("#calculator_mod");

        if (!this.but_4) this.but_4 = this.buttonAreaNode.getElement("#calculator_4");
        if (!this.but_5) this.but_5 = this.buttonAreaNode.getElement("#calculator_5");
        if (!this.but_6) this.but_6 = this.buttonAreaNode.getElement("#calculator_6");
        if (!this.but_mul) this.but_mul = this.buttonAreaNode.getElement("#calculator_mul");
        if (!this.but_inverse) this.but_inverse = this.buttonAreaNode.getElement("#calculator_inverse");

        if (!this.but_1) this.but_1 = this.buttonAreaNode.getElement("#calculator_1");
        if (!this.but_2) this.but_2 = this.buttonAreaNode.getElement("#calculator_2");
        if (!this.but_3) this.but_3 = this.buttonAreaNode.getElement("#calculator_3");
        if (!this.but_sub) this.but_sub = this.buttonAreaNode.getElement("#calculator_sub");
        if (!this.but_equal) this.but_equal = this.buttonAreaNode.getElement("#calculator_equal");

        if (!this.but_0) this.but_0 = this.buttonAreaNode.getElement("#calculator_0");
        if (!this.but_dot) this.but_dot = this.buttonAreaNode.getElement("#calculator_dot");
        if (!this.but_add) this.but_add = this.buttonAreaNode.getElement("#calculator_add");
    },
    setEvent: function(){
        var but3 = this.buttonAreaNode.getElements(".but3");
        but3.setStyles(this.css.buttonStyle3);
        var _self = this;

        but3.addEvent("click", function(){
            if (_self.startNumber){
                _self.screenContentNode.set("text", "0");
                _self.startNumber = false;
                _self.screenContentNode.setStyles(_self.css.screenContentNode);
            }
            var v = _self.screenContentNode.get("text");
            if (v.length>=19) return false;

            var value = this.get("text");
            var str = v+value;
            if (!v || v=="0") str = value;
            if (str.length>13) _self.screenContentNode.setStyles({"font-size": "24px", "line-height": "70px"});

            _self.screenContentNode.set("text", str);
        });

        this.but_dot.removeEvents("click");
        this.but_dot.addEvent("click", function(){
            if (_self.startNumber){
                _self.screenContentNode.set("text", "0");
                _self.startNumber = false;
                _self.screenContentNode.setStyles(_self.css.screenContentNode);
            }

            var v = _self.screenContentNode.get("text");
            if (v.length>=19) return false;

            str = v+".";
            if (!v || v=="0") str = "0.";
            if (str.length>13) _self.screenContentNode.setStyles({"font-size": "24px", "line-height": "70px"});

            _self.screenContentNode.set("text", str);
        });

        this.but_0.addEvent("click", function(){
            if (_self.startNumber){
                _self.screenContentNode.set("text", "0");
                _self.startNumber = false;
                _self.screenContentNode.setStyles(_self.css.screenContentNode);
            }

            var v = _self.screenContentNode.get("text");
            if (v.length>=19) return false;

            str = v+"0";
            if (!v || v=="0") str = "0";
            if (str.length>13) _self.screenContentNode.setStyles({"font-size": "24px", "line-height": "70px"});

            _self.screenContentNode.set("text", str);
        });

        this.but_add.addEvent("click", function(){
            _self.baseCompute("+", "plus");
        });

        this.but_sub.addEvent("click", function(){
            _self.baseCompute("-", "minus");
        });
        this.but_mul.addEvent("click", function(){
            _self.baseCompute("*", "times");
        });
        this.but_div.addEvent("click", function(){
            _self.baseCompute("/", "div");
        });
        this.but_mod.addEvent("click", function(){
            _self.baseCompute("%", "mod");
        });

        this.but_equal.addEvent("click", function(){
            _self.baseCompute("", "");
            _self.resultData = 0;
            _self.currentOperation = "";
            _self.startNumber = true;
            _self.screenReferenceNode.set("text", "");
        });
    },

    baseCompute: function(symbol, method){
        var v = this.screenContentNode.get("text");
        var vr = this.screenReferenceNode.get("text");
        this.screenReferenceNode.set("text", vr+v+symbol);

        if (this.currentOperation){
            var x = new Decimal(this.resultData);
            this.resultData = x[this.currentOperation](new Decimal(v));

            this.resultData = (new Decimal(this.resultData)).toNumber();
            if (this.resultData.toString().length>19){
                this.screenContentNode.setStyles({"font-size": "18px", "line-height": "70px"});
            }else if (this.resultData.toString().length>13){
                this.screenContentNode.setStyles({"font-size": "24px", "line-height": "70px"});
            }

            this.screenContentNode.set("text", this.resultData);
        }else{
            this.resultData = v;
        }
        this.currentOperation = method;
        this.startNumber = true;
    }

});
