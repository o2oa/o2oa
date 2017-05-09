MWF.xDesktop.requireApp("process.Xform", "Form", null, false);
MWF.xApplication.portal.XPage = MWF.xApplication.portal.XPage || {};

MWF.xApplication.portal.XPage.Label = MWF.PageLabel =  new Class({
    Extends: MWF.APPLabel
});
MWF.xApplication.portal.XPage.Textfield = MWF.PageTextfield =  new Class({
    Extends: MWF.APPTextfield
});
MWF.xApplication.portal.XPage.Number = MWF.PageNumber =  new Class({
    Extends: MWF.APPNumber
});
MWF.xApplication.portal.XPage.Personfield = MWF.PagePersonfield =  new Class({
    Extends: MWF.APPPersonfield
});
MWF.xApplication.portal.XPage.Calendar = MWF.PageCalendar =  new Class({
    Extends: MWF.APPCalendar
});
MWF.xApplication.portal.XPage.Textarea = MWF.PageTextarea =  new Class({
    Extends: MWF.APPTextarea
});
MWF.xApplication.portal.XPage.Select = MWF.PageSelect =  new Class({
    Extends: MWF.APPSelect
});
MWF.xApplication.portal.XPage.Radio = MWF.PageRadio =  new Class({
    Extends: MWF.APPRadio
});
MWF.xApplication.portal.XPage.Checkbox = MWF.PageCheckbox =  new Class({
    Extends: MWF.APPCheckbox
});
MWF.xApplication.portal.XPage.Button = MWF.PageButton =  new Class({
    Extends: MWF.APPButton
});

MWF.xApplication.portal.XPage.Div = MWF.PageDiv =  new Class({
	Extends: MWF.APPDiv
});
MWF.xApplication.portal.XPage.Image = MWF.PageImage =  new Class({
	Extends: MWF.APPImage
});
MWF.xApplication.portal.XPage.Table = MWF.PageTable =  new Class({
    Extends: MWF.APPTable
});
MWF.xApplication.portal.XPage.Html = MWF.PageHtml =  new Class({
	Extends: MWF.APPHtml
});

MWF.xApplication.portal.XPage.Tab = MWF.PageTab =  new Class({
    Extends: MWF.APPTab
});
MWF.xApplication.portal.XPage.tab$Page = MWF.PageTab$Page =  new Class({
    Extends: MWF.APPTab$Page
});
MWF.xApplication.portal.XPage.tab$Content = MWF.PageTab$Content =  new Class({
    Extends: MWF.APPTab$Content
});

MWF.xApplication.portal.XPage.Tree = MWF.PageTree =  new Class({
    Extends: MWF.APPTree
});
MWF.xApplication.portal.XPage.Iframe = MWF.PageIframe =  new Class({
    Extends: MWF.APPIframe
});
MWF.xApplication.portal.XPage.View = MWF.PageView =  new Class({
    Extends: MWF.APPView
});