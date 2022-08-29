import { createApp } from 'vue';
import App from "./App.vue";
import {loadComponent} from '@o2oa/component';
import ElementPlus from 'element-plus';
// import 'element-plus/dist/index.css';
import {o2, lp, component} from '@o2oa/component';

loadComponent('systemconfig', (d, cb)=>{
    const dd = new Date().getTime();
    component.content.loadCss('../x_component_systemconfig/$Main/default/element.css');
    console.log(new Date().getTime()-dd);
    const app = createApp(App).use(ElementPlus, { size: 'middle'}).mount(d);
    //app.component("SystemInfor")
    cb();
}).then((c)=>{
    c.render();
});
