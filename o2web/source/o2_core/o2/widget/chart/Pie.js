o2.widget = o2.widget || {};
o2.widget.chart = o2.widget.chart || {};
o2.require("o2.widget.Common", null, false);
o2.require("o2.widget.chart.d3", null, false);
o2.widget.chart.Pie = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "margin": 10,
        "showText": false,
        "textType": "value",
        "dataFormat": ""
    },
    initialize: function(node, data, options){
        this.setOptions(options);
        this.node = $(node);
        this.data = data;

        this.path = o2.session.path+"/widget/chart/$Pie/";
        this.cssPath = o2.session.path+"/widget/chart/$Pie/"+this.options.style+"/css.wcss";
        this._loadCss();
    },
    load: function(){
        this.svgNode = new Element("div", {"styles": this.css.svgNode}).inject(this.node);
        if (this.fireEvent("queryLoad")){
            this.size = this.node.getSize();
            o2.widget.chart.d3.load(function(){
                //this.data = d3.map(this.jsonData, function(d, i){return i;});

                this.svg = d3.select(this.svgNode).append("svg");
                this.setSvgSize();
//                this.group = this.svg.append("g");

                this.loadPieData();
                this.loadPieArc();

                //this.loadHint();

                //this.loadXaes();
                //this.loadBars();
                //
                //this.setStyles();
                //this.setEvents();

            }.bind(this));
            this.fireEvent("postLoad");
        }
    },
    setSvgSize: function(){
        this.svg.attr("width", this.size.x).attr("height", this.size.y);
    },
    loadPieData: function(){
        if (this.options.textType==="percent"){
            var sum = d3.sum(this.data, function(d){return d.value});
            this.data.map(function(d){
                var p = ((d.value/sum)*10000).toInt()/100;
                d.percent = ""+p+"%";
                return d;
            });
        }

        this.pie = d3.pie().value(function(d, i){ return d.value;}).sort(function(a, b) { return false; });
        this.pieData = this.pie(this.data);
    },
    loadPieArc: function(){
        var r = (this.size.x>this.size.y) ? this.size.y : this.size.x;
        r = r/2 - this.options.margin;
        this.arc = d3.arc().innerRadius(0).outerRadius(r);
        this.arcOver = d3.arc().innerRadius(0).outerRadius(r+5);
        this.colors = this.css.colors || d3.schemeCategory10;

        var groups = this.svg.selectAll("g").data(this.pieData)
            .enter().append("g").attr("transform", "translate("+(this.size.x/2)+", "+(this.size.y/2)+")");

        var arcs = groups.append("path").attr("d", function(d,i){ var tmp={"startAngle": d.startAngle, "endAngle": d.startAngle}; return this.arc(tmp);}.bind(this))
            .attr("fill", function(d, i){ return this.colors[0+i];}.bind(this));

        Object.each(this.css.arcs, function(v,k){arcs.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
        this.setDefs("arcs_defs", arcs);

        this.transition();

        var _self = this;
        groups.on("mouseover", function(d, i, nodeList){
            _self.highlight(this, d, i);
            _self.fireEvent("mouseover", [arcs, d, i]);
        }).on("mousemove", function(d, i, nodeList){
            if (!_self.options.showText) _self.moveHint(this, d, i);
            _self.fireEvent("mousemove", [arcs, d, i]);
        }).on("mouseout", function(d, i, nodeList){
            _self.normal(this, d, i);
            _self.fireEvent("mouseout", [arcs, d, i]);
        }).on("click", function(d, i, nodeList){
            _self.fireEvent("click", [arcs, d, i]);
        });
    },
    highlight: function(arc, d, i){
        var selArc = d3.select(arc).select("path");
        var selText = d3.select(arc).select("text");

        selArc.attr("d", this.arcOver(d));
        Object.each(this.css.arcs_over, function(v,k){selArc.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
        this.setDefs("arcs_over_defs", selArc);
        Object.each(this.css.text_over, function(v,k){selText.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
        this.setDefs("text_over_defs", selText);

        if (!this.options.showText) this.showHint(arc, d, i);
    },
    normal: function(arc, d, i){
        var selArc = d3.select(arc).select("path");
        var selText = d3.select(arc).select("text");

        selArc.attr("d", this.arc(d));
        Object.each(this.css.arcs, function(v,k){selArc.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
        this.setDefs("arcs_defs", selArc);
        selArc.attr("fill", this.colors[0+i]);

        Object.each(this.css.text, function(v,k){selText.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
        this.setDefs("text_defs", selText);

        if (!this.options.showText) this.hideHint(arc, d, i);
    },

    createTextNode: function(arc, d, i, p){
        var hintGroup = this.svg.append("g");
        if (!p) p = d3.mouse(this.svg.node());

        var hint = d.data.name + " ("+((this.options.dataFormat) ? d3.format(this.options.dataFormat)(d.data.value) : d.data.text)+")";

        if (!d.size) d.size = o2.getTextSize(hint);
        var size = d.size;
        var x = (p[0]-size.x/2);
        var y = (p[1]-size.y-5);
        if (x<0) x = 0;
        var rect = hintGroup.attr("transform", "translate("+x+", "+y+")")
            .append("rect")
            .attr("height", size.y)
            .attr("width", size.x);

        var text = hintGroup.append("text")
            .attr("transform", "translate("+size.x/2+", "+(size.y/2+5)+")").text(hint);

        Object.each(this.css.hint_rect, function(v,k){rect.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
        this.setDefs("hint_rect_defs", rect);
        Object.each(this.css.hint_text, function(v,k){text.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
        this.setDefs("hint_text_defs", text);

        return hintGroup;
    },
    showHint: function(arc, d, i){
        var hintGroup = arc.retrieve("hint");
        if (!hintGroup){
            hintGroup = this.createTextNode(arc, d, i);
            arc.store("hint", hintGroup);
        }
    },
    moveHint: function(arc, d, i){
        var hintGroup = arc.retrieve("hint");
        if (hintGroup){
            var p = d3.mouse(this.svg.node());
            if (!d.size) d.size = o2.getTextSize(d.data.name);
            var size = d.size;
            var x = (p[0]-size.x/2);
            var y = (p[1]-size.y-5);
            if (x<0) x = 0;
            hintGroup.attr("transform", "translate(0,0)")
                .attr("transform", "translate("+x+", "+y+")");
        }
    },
    hideHint: function(arc, d, i){
        var hintGroup = arc.retrieve("hint");
        if (hintGroup){
            hintGroup.remove();
            arc.eliminate("hint");
        }
    },

    transition: function(arcs, groups){
        var _self = this;
        if (!arcs) arcs = this.svg.selectAll("path");
        if (!groups) groups = this.svg.selectAll("g");
        texts = this.svg.selectAll("text");
        texts.remove();

        var sum = d3.sum(this.data, function(d,i){ return d.value;});

        arcs.attr("d", function(d,i){ var tmp={"startAngle": d.startAngle, "endAngle": d.startAngle}; return this.arc(tmp);}.bind(this));
        arcs.transition()
            .delay(function(d, i) {
                var v = d3.sum(this.data, function(dd, idx){ return (idx<i)? dd.value : 0});
                return (i>0) ? ((v/sum) * 300) : 0;
            }.bind(this))
            .duration(function(d, i){ return (d.value/sum)*300;}.bind(this))
            .ease(d3.easeLinear)
            .attrTween("d", function(d,i){
                return function(t){
                    var a = d.endAngle-d.startAngle;
                    var tmp={"startAngle": d.startAngle, "endAngle": d.startAngle+(a*t)};
                    return this.arc(tmp);
                }.bind(this)
            }.bind(this))
            .on("end", function(d,i){
                var r = (this.size.x>this.size.y) ? this.size.y : this.size.x;
                r = r/2 - this.options.margin;
                var tmpArc = d3.arc().innerRadius(0).outerRadius(r+20);
                var p = tmpArc.centroid(d);

                var text = d3.select(groups.nodes()[i]).append("text")
                    .attr("transform", "translate("+(p[0])+", "+(p[1])+")")
                    .text(d.data.percent || d3.format(this.options.dataFormat)(d.data.value));
                Object.each(this.css.text, function(v,k){text.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
                this.setDefs("text_defs", text);

                //p[0] = p[0]+this.size.x/2;
                //p[1] = p[1]+this.size.y/2-5;
                //this.createTextNode(arcs.nodes()[i], d, i, p);
//debugger;
//                text.node().store("arc", arcs.nodes()[i]);
//
//                text.on("mouseover", function(){
//                    _self.highlight(arcs.nodes()[i], d, i);
//                    _self.fireEvent("mouseover", [arcs, d, i]);
//                }).on("mouseout", function(){
//                    _self.normal(arcs.nodes()[i], d, i);
//                    _self.fireEvent("mouseout", [arcs, d, i]);
//                });

            }.bind(this));
    },

    setDefs: function(name, selection){
        if (this.css[name]){
            var defs = this.svg.append("defs");
            this.createDefs(defs, this.css[name]);
            defs.select(function(){ return this.getFirst(); }).attr("id", this.classId+"_"+name);
            selection.attr(this.css[name].urlAttr, "url(#"+this.classId+"_"+name+")");
        }
    },
    hide: function(){
        this.svgNode.setStyle("display", "none");
    },
    show: function(){
        this.svgNode.setStyle("display", "block");
        this.transition();
    },

    destroy: function(){
        this.svgNode.destroy();
        o2.release(this);
    }
});