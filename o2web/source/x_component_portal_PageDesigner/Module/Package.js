MWF.xDesktop.requireApp('process.FormDesigner', 'Module.Package', null, false);

MWF.xApplication.portal.PageDesigner = MWF.xApplication.portal.PageDesigner || {};
MWF.xApplication.portal.PageDesigner.Module = MWF.xApplication.portal.PageDesigner.Module || {};
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Page', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Div', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Label', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Button', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Image', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Table', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.View', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.ViewSelector', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Stat', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Html', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Common', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Tab', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Tree', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Iframe', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Textfield', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Number', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Currency', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Personfield', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Org', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Calendar', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Textarea', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Select', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Radio', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Checkbox', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Address', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Combox', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Source', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.SourceText', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.SubSource', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Widget', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Widgetmodules', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Statement', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.StatementSelector', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Datagrid', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Datatable', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Datatemplate', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Importer', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Application', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.SmartBI', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.ElementUI', null, false);
MWF.xDesktop.requireApp('portal.PageDesigner', 'Module.Htmleditor', null, false);

MWF.xApplication.portal.PageDesigner.Module.OOButton = MWF.PCOOButton = new Class({
    Extends: MWF.FCOOButton,
});
MWF.xApplication.portal.PageDesigner.Module.OOInput = MWF.PCOOInput = new Class({
    Extends: MWF.FCOOInput,
});
MWF.xApplication.portal.PageDesigner.Module.OOOOCheckGroup = MWF.PCOOCheckGroup = new Class({
    Extends: MWF.FCOOCheckGroup,
});
MWF.xApplication.portal.PageDesigner.Module.OODatetime = MWF.PCOODatetime = new Class({
    Extends: MWF.FCOODatetime,
});
MWF.xApplication.portal.PageDesigner.Module.OOOrg = MWF.PCOOOrg = new Class({
    Extends: MWF.FCOOOrg,
});
MWF.xApplication.portal.PageDesigner.Module.OORadioGroup = MWF.PCOORadioGroup = new Class({
    Extends: MWF.FCOORadioGroup,
});
MWF.xApplication.portal.PageDesigner.Module.OOSelect = MWF.PCOOSelect = new Class({
    Extends: MWF.FCOOSelect,
});
MWF.xApplication.portal.PageDesigner.Module.OOAddress = MWF.PCOOAddress = new Class({
    Extends: MWF.FCOOAddress,
});
MWF.xApplication.portal.PageDesigner.Module.OOFiles = MWF.PCOOFiles = new Class({
    Extends: MWF.FCOOFiles,
});
MWF.xApplication.portal.PageDesigner.Module.OOTextarea = MWF.PCOOTextarea = new Class({
    Extends: MWF.FCOOTextarea,
});
MWF.xApplication.portal.PageDesigner.Module.OOCurrency = MWF.PCOOCurrency = new Class({
    Extends: MWF.FCOOCurrency,
});
MWF.xApplication.portal.PageDesigner.Module.OOPagination = MWF.PCOOPagination = new Class({
    Extends: MWF.FCOOPagination,
});
MWF.xApplication.portal.PageDesigner.Module.Codeeditor = MWF.PCCodeeditor = new Class({
    Extends: MWF.FCCodeeditor,
});

MWF.PCOOButton.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOOInput.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOOCheckGroup.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOODatetime.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOOOrg.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOORadioGroup.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOOSelect.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOOAddress.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOOFiles.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOOTextarea.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOOCurrency.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCOOPagination.templateJsonPath = '../x_component_process_FormDesigner/Module/';
MWF.PCCodeeditor.templateJsonPath = '../x_component_process_FormDesigner/Module/';
