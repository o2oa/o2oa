import {component} from '@o2oa/oovm';
import {serverStatus, echoServer} from '../../common/action.js';

const template = `
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title">执行服务器初始化</div>
    
    <div class="infoArea">
        <div oo-if="$.status==='waiting' || $.status==='running' || $.status==='starting'" class="loading" oo-element="testLoading"></div>
        <div oo-if="$.status==='success' || $.status==='started'" class="icon ooicon-check"></div>
        <div oo-if="$.status==='unknown'" class="icon ooicon-error" style="color:#ffc42bde"></div>
        
        <div oo-if="$.status==='waiting'" class="info">等待服务器执行初始化 ... </div>
        <div oo-if="$.status==='running'" class="info">服务器初始化正在执行中 ... </div>
        <div oo-if="$.status==='starting'" class="info">正在启动O2OA(翱途)服务器 ... </div>
        
        <div oo-if="$.status==='success'" class="info">服务器初始化执行成功，即将启动O2OA(翱途)服务器！</div>
        <div oo-if="$.status==='started'" class="info">服务器初始化执行成功，O2OA(翱途)服务器已启动！</div>
        <div oo-if="$.status==='started'" class="info"><oo-button style="margin-left:0" @click="gotoIndex">进入系统登录页面</oo-button></div>
        
        <div oo-if="$.status==='failure' || $.status==='stop'" class="icon ooicon-cancel"></div>
        <div oo-if="$.status==='failure'" class="info">服务器初始化执行失败 </div>
        <div oo-if="$.status==='failure'" class="info" style="font-size: 0.875rem; color: red; text-align: center;">{{$.failureMessage || $.messages.join(', ')}}</div>
        <div oo-if="$.status==='failure'" class="info" style="font-size: 0.875rem; color: #666666">您可以重启服务器后重新进行初始化配置！</div>
        
        <div oo-if="$.status==='unknown'" class="info">无法获取服务器状态，请查看服务器控制台信息</div>
        
        <div oo-if="$.status==='stop'" class="info">初始化服务器已关闭，您可以再次手工启动服务器，以完成初始化配置。</div>
        
<!--        <div class="line">-->
<!--            <div oo-if="$.secret.passStr" class="info"><span class="icon ooicon-check"></span>管理员密码已设置</div>-->
<!--            <div oo-if="!$.secret.passStr" class="info"><span class="icon ooicon-cancel"></span>管理员密码未设置</div>-->
<!--        </div>-->
<!--        -->
<!--        <div class="line">-->
<!--            <div class="info"><span class="icon ooicon-check"></span>数据库：{{$.database.type}}</div>-->
<!--        </div>-->
<!--        -->
<!--        <div class="line">-->
<!--            <div oo-if="$.restore.name" class="info"><span class="icon ooicon-check"></span>导入数据文件：{{$.restore.name}}</div>-->
<!--            <div oo-if="!$.restore.name" class="info"><span class="icon ooicon-cancel"></span>不导入数据文件</div>-->
<!--        </div>-->
    </div>
  
</div>
`;
const style = `
.info {
    display: flex;
    align-items: center;
    margin: 0.5rem;
}
.line{
    padding: 1rem;
}
.icon{
    font-size: 4rem;
    margin: 1rem;
}
.ooicon-check{
    color: #66cc80;
}
.ooicon-cancel{
    color: red;
}
.infoArea{
    display: flex;
    flex-direction: column;
    height: 25rem;
    justify-content: center;
    padding-left: 2rem;
    align-items: center;
}
.loading {
  position: relative;
  width: 3rem;
  height: 3rem;
  border: 0.25rem solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;
  animation: circle infinite 0.75s linear;
  margin: 2rem;
}
`;
export default component({
    template,
    style,
    autoUpdate: true,

    bind(){
        return {
            status: 'success',
            messages: [],
            failureMessage: '',
            checkCount: 0
        }
    },

    async afterRender() {
       if (this.$p.bind.serverStop){
           this.bind.status = 'stop';
       }else{
           this.checkCount = 0;
           this.timeoutCheck();
       }
    },
    async timeoutCheck() {
        if (this.bind.status==='starting'){
            await this.checkServer();
        }else{
            await this.check();
        }

        if (this.checkCount>150){
            this.bind.status = 'unknown';
        }else{
            if (this.bind.status==='started'){

            }else if (this.bind.status==='failure'){

            }else{
                window.setTimeout(() => {
                    this.timeoutCheck();
                }, 2000);
            }
        }
    },
    async check() {
        const json = await serverStatus();
        this.bind.status = json.status;
        this.bind.failureMessage = json.failureMessage || '';
        this.bind.messages = json.messages || [];
        this.checkCount++;
    },
    async checkServer() {
        const json = await echoServer();
        this.bind.status = json.status;
        this.bind.failureMessage = json.failureMessage || '';
        this.bind.messages = json.messages || [];
        this.checkCount++;
    },
    gotoIndex(){
        window.location=`/?${(new Date()).getTime()}`;
    }
});
