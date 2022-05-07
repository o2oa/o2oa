import Vue from 'vue'
import App from './App.vue'
import {loadComponent} from '@o2oa/component';

Vue.config.productionTip = false

loadComponent('appstore', (d, cb)=>{
    let component = new Vue({
        render: h => h(App),
    }).$mount();
    d.appendChild(component.$el);
    cb();
}).then((c)=>{
    c.render();
});
