MWF.xApplication.process.Xform.widget = MWF.xApplication.process.Xform.widget || {};
MWF.require("MWF.widget.ImageClipper", null, false);
MWF.xApplication.process.Xform.widget.ImageClipper = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
        "reference" : "",
        "referenceType" : "",
        "imageUrl" : "",
        "resultMaxSize" : 800,
        "description" : "",
        "title": "Select Image",
		"style": "default",
        "aspectRatio": 1
	},
	initialize: function(designer, options){
		this.setOptions(options);
        this.app = designer;
		this.path = "/x_component_process_Xform/widget/$ImageClipper/";
		this.cssPath = "/x_component_process_Xform/widget/$ImageClipper/"+this.options.style+"/css.wcss";
		this._loadCss();
	},

	load: function(data){
		this.data = data;

        var options = {};
        var width = "668";
        var height = "510";
        width = width.toInt();
        height = height.toInt();

        var size = this.app.content.getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;
        if (layout.mobile){
            x = 20;
            y = 0;
        }

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function() {
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.options.title || "Select Image",
                "style": options.style || "image",
                "top": y,
                "left": x - 20,
                "fromTop": y,
                "fromLeft": x - 20,
                "width": width,
                "height": height,
                "html": "<div></div>",
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": MWF.LP.process.button.ok,
                        "action": function () {
                            if( _self.image.getResizedImage() ){
                                _self.image.uploadImage( function( json ){
                                    _self.imageSrc = MWF.xDesktop.getImageSrc( json.id );
                                    _self.imageId = json.id;
                                    _self.fireEvent("change");
                                    this.close();
                                }.bind(this));
                            }else{
                                _self.imageSrc = "";
                                _self.imageId = "";
                                _self.fireEvent("change");
                                this.close();
                            }
                        }
                    },
                    {
                        "text": MWF.LP.process.button.cancel,
                        "action": function () {
                            this.close();
                        }
                    }
                ]
            });
            dlg.show();

            this.image = new MWF.widget.ImageClipper(dlg.content.getFirst(), {
                "aspectRatio": this.options.aspectRatio,
                "description" : this.options.description,
                "imageUrl" : this.options.imageUrl,
                "resultMaxSize" : this.options.resultMaxSize,
                "reference" : this.options.reference,
                "referenceType": this.options.referenceType,
                "resetEnable" : true
            });
            this.image.load(this.data);
        }.bind(this))
	}
	
});

