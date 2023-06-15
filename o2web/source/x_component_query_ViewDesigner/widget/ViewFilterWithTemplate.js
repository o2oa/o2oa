MWF.xDesktop.requireApp("query.ViewDesigner", "widget.ViewFilter", null, false);
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.query.ViewDesigner.widget.ViewFilterWithTemplate = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.widget.ViewFilter,
    setHtml: function(){
        var htmlString =
            '<div class="inputAreaNode_vf">'+
            '<table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable restrictViewFilterTable_vf">'+
            '   <tr style="display:none">'+
            '       <td class="editTableTitle">{{$.lp.title}}:</td>'+
            '       <td class="editTableValue"><input type="text" class="editTableInput titleInput_vf"/></td>'+
            '    </tr>'+
            '    <tr>'+
            '       <td class="editTableTitle">{{$.lp.path}}:</td>'+
            '       <td class="editTableValue"><input type="text" class="editTableInput pathInput_vf"/></td>'+
            '    </tr>'+
            '    <tr>'+
            '       <td class="editTableTitle">{{$.lp.dataType}}:</td>'+
            '       <td class="editTableValue"><select class="datatypeInput_vf">'+
            '           <option value="textValue" selected>{{$.lp.text}}</option>'+
            '           <option value="numberValue">{{$.lp.number}}</option>'+
            '           <option value="dateValue">{{$.lp.date}}</option>'+
            '           <option value="dateTimeValue">{{$.lp.dateTime2}}</option>'+
            '           <option value="booleanValue">{{$.lp.boolean}}</option>'+
            '       </select></td>'+
            '   </tr>'+
            '   <tr style="display:none">'+
            '       <td class="editTableTitle"></td>'+
            '       <td class="editTableValue">'+
            '           <input type="radio" class="restrictFilterInput_vf" value="restrict" checked/>{{$.lp.asDefaultFilter}}<br>'+
            '           <input type="radio" class="customFilterInput_vf" value="custom"/>{{$.lp.asCustomFilter}}'+
            '       </td>'+
            '   </tr>'+
            '</table>'+
            '<table id="text{$.pid}viewFilterRestrict" width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">'+
            '    <tr>'+
            '       <td class="editTableTitle">{{$.lp.logic}}:</td>'+
            '       <td class="editTableValue"><select class="logicInput_vf">'+
            '           <option selected value="and">{{$.lp.and}}</option>'+
            '           <option value="or">{{$.lp.or}}</option>'+
            '       </select></td>'+
            '   </tr>'+
            '   <tr>'+
            '       <td class="editTableTitle">{{$.lp.compare}}:</td>'+
            '       <td class="editTableValue"><select class="comparisonInput_vf">'+
            '           <option value="equals" selected>{{$.lp.equals}}(==)</option>'+
            '           <option value="notEquals">{{$.lp.notEquals}}(!=)</option>'+
            '           <option value="greaterThan">{{$.lp.greaterThan}}(>)</option>'+
            '           <option value="greaterThanOrEqualTo">{{$.lp.greaterThanOrEqualTo}}(>=)</option>'+
            '           <option value="lessThan">{{$.lp.lessThan}}(<)</option>'+
            '           <option value="lessThanOrEqualTo">{{$.lp.lessThanOrEqualTo}}(<=)</option>'+
            '           <option value="like">{{$.lp.like}}(like)</option>'+
            '           <option value="notLike">{{$.lp.notLike}}(not-like)</option>'+
            '           <option value="in">{{$.lp.in}}(in)</option>'+
            '       </select></td>'+
            '   </tr>'+
            '   <tr style="display:none">'+
            '       <td class="editTableTitle">{{$.lp.value}}:</td>'+
            '       <td class="editTableValue">'+
            '           <input type="text" class="editTableInput valueTextInput_vf" style="display: block"/>'+
            '           <input type="number" class="editTableInput valueNumberInput_vf" style="display: none"/>'+
            '           <input type="text" class="editTableInput valueDatetimeInput_vf" style="display: none" readonly/>'+
            '           <input type="text" class="editTableInput valueDateInput_vf" style="display: none" readonly/>'+
            '           <input type="text" class="editTableInput valueTimeInput_vf" style="display: none" readonly/>'+
            '           <select class="valueBooleanInput_vf" style="display: none">'+
            '               <option value="true" selected>{{$.lp.true}}(True)</option>'+
            '               <option value="false">{{$.lp.false}}(False)</option>'+
            '           </select>'+
            '           <div class="multiValueNotice_vf" style="display: none;color: #999;line-height: 24px;">{{$.lp.filterValueInfo}}</div>'+
            '       </td>'+
            '   </tr>'+
            '</table>'+
            '<div title="{{$.lp.value}}" class="MWFFilterFormulaArea"></div>'+
            '</div>'+
            '<div class="actionAreaNode_vf"></div>'+
            '<div style="height: 20px; line-height: 20px; text-align:center; background-color: #eeeeee">{{$.lp.defaultFilter}}</div>'+
            '<div class="listAreaNode_vf" style="min-height: 56px; border-bottom:1px solid #CCCCCC; overflow: hidden;"></div>'+
            '<div style="display:none; height: 20px; line-height: 20px; text-align:center; background-color: #eeeeee">{{$.lp.customFilter}}</div>'+
            '<div class="fieldListAreaNode_vf" style="display:none; min-height: 56px; border-bottom:1px solid #CCCCCC; overflow: hidden;"></div>';

        htmlString = o2.bindJson(htmlString, {"lp": MWF.xApplication.process.FormDesigner.LP.propertyTemplate});

        // this.JsonTemplate = new MWF.widget.JsonTemplate(this.data, this.htmlString);
        // this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyNode);
        //this.propertyContent.set("html", this.JsonTemplate.load());

        this.node.set("html", htmlString);
        this.setEditNodeStyles(this.node);

    },
    setEditNodeStyles: function(node){
        var nodes = node.getChildren();
        if (nodes.length){
            nodes.each(function(el){
                var cName = el.get("class");
                if (cName){
                    if (this.css[cName]) el.setStyles(this.css[cName]);
                }
                this.setEditNodeStyles(el);
            }.bind(this));
        }
    },
});