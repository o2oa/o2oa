MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.DictionaryDesigner = MWF.xApplication.portal.DictionaryDesigner || {};

MWF.xDesktop.requireApp("process.DictionaryDesigner", "Dictionary", null, false);

MWF.xApplication.portal.DictionaryDesigner.Dictionary = new Class({
    Extends: MWF.xApplication.process.DictionaryDesigner.Dictionary,


    createRootItem: function() {
        this.items.push(new MWF.xApplication.portal.DictionaryDesigner.Dictionary.item("ROOT", this.data.data, null, 0, this, true));
    },
    saveSilence: function(){
        if (!this.isSave){

            if( this.scriptPage.isShow ){
                if( this.scriptEditor ){

                    var data = this.getEditorValidData( true );
                    if( data !== false ){
                        this.data.data = data;
                    }else{
                        return false;
                    }
                }
            }

            var name = this.designer.propertyNameNode.get("value");
            var alias = this.designer.propertyAliasNode.get("value");
            var description = this.designer.propertyDescriptionNode.get("value");
            if (!name){
                this.designer.notice(this.designer.lp.notice.inputName, "error");
                return false;
            }
            this.data.name = name;
            this.data.alias = alias;
            this.data.description = description;

            this.isSave = true;

            this.designer.actions.saveDictionary(this.data, function(json){
                this.isSave = false;
                this.data.id = json.data.id;
                if (callback) callback();
            }.bind(this), function(xhr, text, error){
                this.isSave = false;
                //
                //var errorText = error+":"+text;
                //if (xhr) errorText = xhr.responseText;
                //MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
            }.bind(this));
        }
    },
    save: function(callback){
        if (!this.isSave){
            if (this.designer.tab.showPage==this.page){

                if( this.scriptPage.isShow ){
                    if( this.scriptEditor ){
                        var data = this.getEditorValidData();
                        if( data !== false ){
                            this.data.data = data;
                        }else{
                            return false;
                        }
                    }
                }

                var name = this.designer.propertyNameNode.get("value");
                var alias = this.designer.propertyAliasNode.get("value");
                var description = this.designer.propertyDescriptionNode.get("value");

                if (!name || !alias){
                    this.designer.notice(this.designer.lp.notice.inputName, "error");
                    return false;
                }
                this.data.name = name;
                this.data.alias = alias;
                this.data.description = description;
            }

            this.isSave = true;
            this.designer.actions.saveDictionary(this.data, function(json){
                this.isSave = false;
                this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});
                this.data.isNewDictionary = false;
                this.isNewDictionary = false;

                this.data.id = json.data.id;
                this.page.textNode.set("text", this.data.name);
                if (this.lisNode) {
                    this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
                }
                if (callback) callback();
            }.bind(this), function(xhr, text, error){
                this.isSave = false;

                var errorText = error+":"+text;
                if (xhr) errorText = xhr.responseText;
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
            }.bind(this));
        }else{
            MWF.xDesktop.notice("info", {x: "right", y:"top"}, this.designer.lp.isSave);
        }

    },

});

MWF.xApplication.portal.DictionaryDesigner.Dictionary.item = new Class({
    Extends: MWF.xApplication.process.DictionaryDesigner.Dictionary.item,
    createNewItem: function(key, value, parent, level, dictionary, exp, nextSibling){
        return new MWF.xApplication.portal.DictionaryDesigner.Dictionary.item(key, value, parent, level, dictionary, exp, nextSibling);
    }
});
MWF.xApplication.portal.DictionaryDesigner.DictionaryReader = new Class({
    Extends: MWF.xApplication.process.DictionaryDesigner.DictionaryReader,

    autoSave: function(){},
    createRootItem: function() {
        this.items.push(new MWF.xApplication.portal.DictionaryDesigner.Dictionary.ItemReader("ROOT", this.data.data, null, 0, this, true));
    },
});
MWF.xApplication.portal.DictionaryDesigner.Dictionary.ItemReader= new Class({
    Extends: MWF.xApplication.process.DictionaryDesigner.Dictionary.ItemReader,

    createNewItem: function(key, value, parent, level, dictionary, exp, nextSibling){
        return new MWF.xApplication.portal.DictionaryDesigner.Dictionary.ItemReader(key, value, parent, level, dictionary, exp, nextSibling);
    }
});
