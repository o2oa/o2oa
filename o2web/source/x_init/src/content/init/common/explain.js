import {component} from '@o2oa/oovm';
const template = `
<div class="explain">
    <div class="explain_content">
        <div class="explain_title ooicon-{{$.explain.icon}}">{{$.explain.title}}</div>
        <div oo-each="$.explain.textList" oo-item="text" class="explain_text">
            <p oo-html="{{text.value}}"></p>
        </div>
    </div>
</div>
`;
export default component({
    template,
    autoUpdate: true

});
