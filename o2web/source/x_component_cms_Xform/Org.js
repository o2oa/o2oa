MWF.xDesktop.requireApp("process.Xform", "Org", null, false);
MWF.xApplication.cms.Xform.Org = MWF.CMSOrg =  new Class({
	Extends: MWF.APPOrg,
	_getOrgOptions: function(){
		this.selectTypeList = typeOf( this.json.selectType ) == "array" ? this.json.selectType : [this.json.selectType];
		if( this.selectTypeList.contains( "identity" ) ) {
			this.identityOptions = new MWF.CMSOrg.IdentityOptions(this.form, this.json);
		}
		if( this.selectTypeList.contains( "unit" ) ) {
			this.unitOptions = new MWF.CMSOrg.UnitOptions(this.form, this.json);
		}
		if( this.selectTypeList.contains( "group" ) ){
			this.groupOptions = new MWF.APPOrg.GroupOptions( this.form, this.json );
		}
	}
});

MWF.CMSOrg.IdentityOptions = new Class({
	Extends: MWF.APPOrg.IdentityOptions,
	_getSelectRange : function(){
		if (this.json.identityRange==="unit"){
			return this.getScriptSelectUnit();
		}
		if (this.json.identityRange==="draftUnit"){
			var dn = this.form.businessData.document.creatorIdentity;
			if (!dn){
				if ( layout.session.user.identityList && layout.session.user.identityList.length){
					var ids = [];
					layout.session.user.identityList.each(function(id){ ids.push(id.id); });
					return this.getNextSelectUnit(ids);
				}else{
					return [];
				}
			}else{
				return this.getNextSelectUnit(dn);
			}
		}
		if (this.json.identityRange==="currentUnit"){
			if ( layout.session.user.identityList && layout.session.user.identityList.length){
				var ids = [];
				layout.session.user.identityList.each(function(id){ ids.push(id.id); });
				return this.getNextSelectUnit(ids);
			}else{
				return [];
			}
		}
		return [];
	}
});

MWF.CMSOrg.UnitOptions = new Class({
	Extends: MWF.APPOrg.UnitOptions,
	_getSelectRange : function(){
		if (this.json.unitRange==="unit"){
			return this.getScriptSelectUnit();
		}
		if (this.json.unitRange==="draftUnit"){
			var dn = this.form.businessData.document.creatorIdentity;
			if (!dn){
				if ( layout.session.user.identityList && layout.session.user.identityList.length){
					var ids = [];
					layout.session.user.identityList.each(function(id){ ids.push(id.id); });
					return this.getNextSelectUnit(ids);
				}else{
					return [];
				}
			}else{
				return this.getNextSelectUnit(dn);
			}
		}
		if (this.json.unitRange==="currentUnit"){
			if ( layout.session.user.identityList && layout.session.user.identityList.length){
				var ids = [];
				layout.session.user.identityList.each(function(id){ ids.push(id.id); });
				return this.getNextSelectUnit(ids);
			}else{
				return [];
			}
		}
		return [];
	}
});