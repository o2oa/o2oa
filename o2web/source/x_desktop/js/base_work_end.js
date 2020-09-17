o2.xDesktop.$all=true;
o2.xDesktop.Common=true;
o2.xApplication.Selector.package = MWF.O2Selector;

//layout.addReady(function(){
o2.Actions.actions["x_organization_assemble_authentication"] = new o2.xAction.RestActions.Action["x_organization_assemble_authentication"]("x_organization_assemble_authentication", orgActins);
o2.Actions.actions["x_processplatform_assemble_surface"] = new o2.xAction.RestActions.Action["x_processplatform_assemble_surface"]("x_processplatform_assemble_surface", processActins);
o2.Actions.actions["x_cms_assemble_control"] = new o2.xAction.RestActions.Action["x_cms_assemble_control"]("x_cms_assemble_control", cmsActins);
o2.Actions.actions["x_organization_assemble_control"] = new o2.xAction.RestActions.Action["x_organization_assemble_control"]("x_organization_assemble_control", orgControlActins);
o2.Actions.actions["x_query_assemble_surface"] = new o2.xAction.RestActions.Action["x_query_assemble_surface"]("x_query_assemble_surface", queryActins);

o2.xAction.RestActions.Action["x_program_center"] = new Class({Extends: o2.xAction.RestActions.Action});
o2.Actions.actions["x_program_center"] = new o2.xAction.RestActions.Action["x_program_center"]("x_program_center", centerActins);

o2.Actions.actions["x_organization_assemble_personal"] = new o2.xAction.RestActions.Action["x_organization_assemble_personal"]("x_organization_assemble_personal", personalActions);


//});
