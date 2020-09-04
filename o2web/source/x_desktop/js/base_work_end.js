o2.xDesktop.$all=true;
o2.xDesktop.Common=true;
MWF.xApplication.Selector.package = MWF.O2Selector;

//layout.addReady(function(){
    MWF.Actions.actions["x_organization_assemble_authentication"] = new MWF.xAction.RestActions.Action["x_organization_assemble_authentication"]("x_organization_assemble_authentication", orgActins);
    MWF.Actions.actions["x_processplatform_assemble_surface"] = new MWF.xAction.RestActions.Action["x_processplatform_assemble_surface"]("x_processplatform_assemble_surface", processActins);
    MWF.Actions.actions["x_cms_assemble_control"] = new MWF.xAction.RestActions.Action["x_cms_assemble_control"]("x_cms_assemble_control", cmsActins);
MWF.Actions.actions["x_organization_assemble_control"] = new MWF.xAction.RestActions.Action["x_organization_assemble_control"]("x_organization_assemble_control", orgControlActins);
MWF.Actions.actions["x_query_assemble_surface"] = new MWF.xAction.RestActions.Action["x_query_assemble_surface"]("x_query_assemble_surface", queryActins);

MWF.xAction.RestActions.Action["x_program_center"] = new Class({Extends: MWF.xAction.RestActions.Action});
this.actions["x_program_center"] = new MWF.xAction.RestActions.Action["x_program_center"]("x_program_center", centerActins);


//});
