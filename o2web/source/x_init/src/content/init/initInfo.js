import {component} from '@o2oa/oovm';
import {dom} from '@o2oa/util';
import {uploadRestore, cancelRestore} from '../../common/action.js';
import {notice} from '../../common/notice.js';

const template = `
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title">初始化信息</div>
    
    <div class="infoArea">
        <div class="line">
            <div oo-if="$.secret.passStr" class="info"><span class="icon ooicon-check"></span>管理员密码已设置</div>
            <div oo-if="!$.secret.passStr" class="info"><span class="icon ooicon-cancel"></span>管理员密码未设置</div>
        </div>
        
        <div class="line">
            <div class="info"><span class="icon ooicon-check"></span>数据库：{{$.database.type}}</div>
        </div>
        
        <div class="line">
            <div oo-if="$.restore.name" class="info"><span class="icon ooicon-check"></span>导入数据文件：{{$.restore.name}}</div>
            <div oo-if="!$.restore.name" class="info"><span class="icon ooicon-cancel"></span>不导入数据文件</div>
        </div>
    </div>
  
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev">上一步</oo-button>
    <oo-button type="cancel" @click="stepPrev">取消</oo-button>
    <oo-button @click="nextStep">执行</oo-button>
</div>
<input type="file" @change="uploadFile" oo-element="uploadFileNode" style="display: none"/>
`;
const style = `
.info {
    display: flex;
    align-items: center;
}
.line{
    padding: 1rem;
}
.icon{
    font-size: 2rem;
    margin-right: 0.3rem;
}
.ooicon-check{
    color: green;
}
.ooicon-cancel{
    color: red;
}
.infoArea{
    display: flex;
    flex-direction: column;
    height: 16rem;
    justify-content: center;
    padding-left: 2rem;
}
`;
export default component({
    template,
    style,
    autoUpdate: true,

    stepPrev(){
        const step = this.$p.bind.step - 1;
        this.$p.bind.step = (step<0) ? 0 : step;
    },

    nextStep(){
        const step = this.$p.bind.step + 1;
        this.$p.bind.step = (step<5) ? step : 0;
    }

});
