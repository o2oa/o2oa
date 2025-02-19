import {component} from '@o2oa/oovm';
import {serverStatus, echoServer} from '../../common/action.js';

const template = `
<div class="pane_content" style="padding: 2rem 2rem;">
    <!--<div style="font-size: 1.2rem; padding-bottom: 1em;">服务器初始化已经取消</div>-->
    
    <div class="infoArea">
        <div class="infoText">初始化服务器已关闭，您可以再次手工启动服务器，重新执行初始化配置。</div>
    </div>
  
</div>
`;
const style = `
.infoText {
    display: flex;
    align-items: center;
}
.line{
    padding: 1rem;
}
.icon{
    font-size: 4rem;
    margin: 1rem;
}
`;
export default component({
    template,
    style,
    autoUpdate: true,
});
