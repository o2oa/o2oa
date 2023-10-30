MWF.xDesktop.requireApp("process.FormDesigner", "Module.Currency", null, false);
MWF.xApplication.portal.PageDesigner.Module.Currency = MWF.PCCurrency = new Class({
    Extends: MWF.FCCurrency
});
MWF.APPPOD.Module.Currency.templateJsonPath = "../x_component_process_FormDesigner/Module/";