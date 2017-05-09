MWF.xDesktop.requireApp("process.Xform", "ImageClipper", null, false);
MWF.xApplication.cms.Xform.ImageClipper = MWF.CMSImageClipper =  new Class({
	Extends: MWF.APPImageClipper,
    selectImage: function(d, callback){
        var clipperType = this.json.clipperType || "unrestricted";
        var ratio = 1;
        var description = "";
        var maxSize = 500;
        if( clipperType == "unrestricted" ){
            ratio = 0;
        }else if( clipperType == "size" ){
            var width = this.json.imageWidth.toInt();
            var height = this.json.imageHeight.toInt();
            ratio = width / height;
            //maxSize = Math.max( width, height );
            if( !isNaN( width ) && !isNaN( height )  ){
                description = MWF.LP.widget.pictureSize.replace(/{width}/g, width).replace(/{height}/g, height);
            }
        }else if( clipperType == "ratio" ){
            ratio = this.json.imageRatio || 1;
            description = MWF.LP.widget.pictureRatio.replace(/{ratio}/g, ratio);
        }
        MWF.xDesktop.requireApp("process.Xform", "widget.ImageClipper", function(){
            this.imageClipper = new MWF.xApplication.process.Xform.widget.ImageClipper(this.form.app, {
                "style": "default",
                "aspectRatio" : ratio,
                "description" : description,
                "imageUrl" : d ? MWF.xDesktop.getImageSrc( d ) : "",
                "reference" : this.form.businessData.document.id,
                "referenceType": "cmsDocument",
                "resultMaxSize" : maxSize,
                "onChange" : function(){
                    callback( { src : this.imageClipper.imageSrc, id : this.imageClipper.imageId } );
                }.bind(this)
            });
            this.imageClipper.load();
        }.bind(this));
    },
    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == "publish");
        if (flag){
            var n = this.getData();
            var v = (data.valueType=="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
	
}); 