import {component} from '@o2oa/oovm';
import {getTitle, setTitle} from '../../common/action.js';

const template = `
<div class="pane_content">
    <div class="logo60"></div>
    <div class="input_title">系统名称</div>
    <oo-input oo-element="titleFiled"
        required="true" 
        validity-blur="true"
        validity="请输入系统名称"
        oo-model="title.text" 
        left-icon="computer">
    </oo-input>
</div>
<div class="actions">
    <oo-button @click="next">下一步</oo-button>
</div>
`;
export default component({
    template,
    autoUpdate: true,

    async beforeRender() {
        this.bind.title.text = (await getTitle()) || 'O2OA(翱途)企业应用平台';
    },

    async next() {
        if (this.titleFiled.checkValidity()) {
            debugger;
            $OOUI.mask(this.dom.parentElement);
            await setTitle(this.bind.title.text);
            $OOUI.unmask(this.dom.parentElement);
            this.bind.title.text = this.bind.title.text;
            const step = this.$p.bind.step + 1;
            this.$p.bind.step = step < 5 ? step : 0;
        }
    },
});
