import {component} from '@o2oa/oovm';
const template = `
<div class="explain">
    <div class="explain_content">
        <div class="explain_title"><span class="ooicon-{{$.explain.icon}}"></span>{{$.explain.title}}</div>
        <ul>
        <li oo-each="$.explain.textList" oo-item="text" class="explain_text">
            <span oo-html="{{text.value}}"></span>
        </li>
        </ul>
    </div>
</div>
`;
export default component({
    template,
    autoUpdate: true,
});
