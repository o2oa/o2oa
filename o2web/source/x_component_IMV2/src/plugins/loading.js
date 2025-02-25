import { createApp} from "vue";
import Loading from "../components/Loading.vue";

export default {

    install(app) {
        // 动态挂载 LoadingComponent 到 DOM
        const container = document.createElement("div");
        document.body.appendChild(container);

        const appInstance = createApp(Loading); // 创建 Loading 组件的独立实例
        appInstance.mount(container); // 挂载到 DOM
        console.log('注册插件成功！！！！！！！！')
    },
};