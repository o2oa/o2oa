import {component} from '@o2oa/oovm';
// import {Dialog} from '@o2oa/ui';
import {executeServer, stopServer} from '../../common/action.js';

const template = `
<div class="pane_content" style="max-width: 60rem;">
    <div class="input_title">初始化信息</div>
    
    <div class="infoArea">
        <div class="line">
             <div class="info"><span>系统名称：{{$.title.text}}</span><span class="icon ooicon-icon_ok_round"></span></div>
        </div>
        <div class="line">
            <div oo-if="$.secret.passStr" class="info"><span>管理员密码已设置</span><span class="icon ooicon-icon_ok_round"></span></div>
            <div oo-if="!$.secret.passStr" class="info error"><span>管理员密码未设置</span><span class="icon ooicon-error1"></span></div>
        </div>
        
        <div class="line">
            <div class="info"><span>数据库：{{$.database.type==='$configured' ? '已初始化数据库' : $.database.type}}</span><span class="icon ooicon-icon_ok_round"></span></div>
        </div>
        
        <div class="line">
            <div oo-if="$.restore.name" class="info"><span>导入数据文件：{{$.restore.name}}</span><span class="icon ooicon-icon_ok_round"></span></div>
            <div oo-if="!$.restore.name" class="info error"><span>不导入数据文件</span><span class="icon ooicon-process-cancel2"></span></div>
        </div>
    </div>
    
    <div>
        <div oo-if="$.status==='unknown'" class="icon ooicon-error" style="color:#ffc42bde"></div>
        <div oo-if="$.status==='waiting'" class="info">等待服务器执行初始化 ... </div>
    </div>
  
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev">上一步</oo-button>
    <oo-button type="cancel" @click="stepCancel">取消</oo-button>
    <oo-button @click="nextStep">执行</oo-button>
</div>
<input type="file" @change="uploadFile" oo-element="uploadFileNode" style="display: none"/>
`;
const style = `
.info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #F2F7FC;
    padding: 1.25rem 1.875rem;
}
.info.error{
    background: #FFF0F0;
}
.line{
    padding: 1rem;
}
.icon{
    margin-right: 0.3rem;
    color: var(--oo-color-success);
    font-size: 1.2rem;
}
.info.error .icon{
    color: var(--oo-color-error);
}
.ooicon-check{
    color: green;
}
.ooicon-cancel{
    color: #999999;
}
.infoArea{
    display: flex;
    flex-direction: column;
    justify-content: center;
}
`;
export default component({
    template,
    style,
    autoUpdate: true,

    stepPrev() {
        const step = this.$p.bind.step - 1;
        this.$p.bind.step = step < 0 ? 0 : step;
    },
    async stepCancel(e) {
        debugger;
        this.dialog = new $OOUI.Dialog(this.$p.paneNode, {
            // modalArea: this.$p.dom.firstElementChild,
            title: '取消确认',
            content: `是否确定要关闭初始化服务器？`,
            width: '20em',
            position: 'center',
            events: {
                ok: async () => {
                    await stopServer();
                    this.$p.bind.step = 6;
                    this.$p.bind.serverStop = true;
                },
            },
        });
        this.dialog.show();
    },
    nextStep(e) {
        this.dialog = new $OOUI.Dialog(this.$p.paneNode, {
            content: `是否确定要执行初始化任务？`,
            width: '20em',
            position: 'center',
            events: {
                ok: async () => {
                    await executeServer();
                    const step = this.$p.bind.step + 1;
                    this.$p.bind.step = step < 6 ? step : 0;
                },
            },
        });
        this.dialog.show();
    },
});
