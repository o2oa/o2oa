function formatTimeRange(startTime, endTime) {
    const formatDate = (date, includeTime = true) => {
        const [year, month, day, hours, minutes] = [
            date.getFullYear(),
            String(date.getMonth() + 1).padStart(2, '0'),
            String(date.getDate()).padStart(2, '0'),
            String(date.getHours()).padStart(2, '0'),
            String(date.getMinutes()).padStart(2, '0')
        ];
        return includeTime ? `${year}年${month}月${day}日 ${hours}:${minutes}` : `${year}年${month}月${day}日`;
    };

    const start = new Date(startTime);
    const end = new Date(endTime);

    return start.toDateString() === end.toDateString()
        ? `${formatDate(start)} - ${end.getHours()}:${String(end.getMinutes()).padStart(2, '0')}`
        : `${formatDate(start)} - ${formatDate(end)}`;
}



this.define('addWidget', (e, data) => {
    const content = new Element('div');
    content.loadHtmlText(addWidgetHtml, { "bind": data, "module": this });
    $OOUI.dialog('添加组件', content, this.page.node(), {width: '60em', offset: {y: -10}});

    const parContentNode = content.querySelector('.add-widget-par-content');
    let html = `
        <oo-input label-style="min-width: 4.4em; max-width: 4.4em" label="宽度比例" type="number" value="${data.size.flex || 1}" style="flex: 100%"></oo-input>
        <oo-input label-style="min-width: 4.4em; max-width: 4.4em" label="最小宽度" type="number" value="${data.size.x || 30}" style="flex: 40%">
            <div slot="after-inner-after" style="padding-right:0.5em">em</div>
        </oo-input>
        <oo-input label-style="min-width: 4.4em; max-width: 4.4em" label="最小高度" type="number" value="${data.size.y || 24}" style="flex: 40%">
            <div slot="after-inner-after" style="padding-right:0.5em">em</div>
        </oo-input>
    `;
    if (data.pars?.length){
        data.pars.forEach(async (par)=>{
            if (par.type==='application'){
                const field = await createApplicationSelectorHtml(par);
                html += field;
            }else{
                html += `<oo-input label="${par.title}" label-style="min-width: 4.4em; max-width: 4.4em" value="${par.value || ''}" style="flex: 100%"></oo-input>`;
            }
            
        });
    }
    parContentNode.set('html', html);
    this.closeCustomWidgetContent();
});