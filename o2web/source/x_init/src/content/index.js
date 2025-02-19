import {component} from '@o2oa/oovm';
import indexHeader from './header.js';
import indexMain from './main.js';
import indexFooter from './footer.js';
import template from './index.html?raw';
export default component({
    template,
    autoUpdate: true,
    components: {indexHeader, indexMain, indexFooter},

    // 初始化数据
    async bind() {
        return {
            secret: {
                passStr: '',
                confirmPass: '',
            },
            database: {
                type: 'h2',
                url: '',
                username: '',
                password: '',
            },
            restore: {
                name: '',
            },
            title: {
                text: '',
            },
        };
    },
});
