import {component} from '@o2oa/oovm';
import {dom} from '@o2oa/util';
import {uploadRestore, cancelRestore} from '../../common/action.js';

const template = `
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title">初始化数据</div>
    <div class="upload_area" @dragover="dragover" @drop="drop" @dragout="dragout">
        <div>将zip文件拖动到此处</div>
        <div>或</div>
        <oo-button @click="selectFile">选择zip文件</oo-button>
    </div>
    <div class="upload_name" oo-if="!!$.restore.name">
        <div><span>已上传存储文件：</span><span style="color:var(--oo-color-main)">{{$.restore.name}}</span></div>
        <oo-button class="delete_button" @click="deleteUpload">删除</oo-button>
    </div>
    <div class="upload_name">
        <div style="font-size: 0.875rem; color:#666666">如果需要导入数据，请上传数据zip文件，如果不需要，可直接点击“下一步”</div>
    </div>
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev">上一步</oo-button>
    <oo-button @click="nextStep">下一步</oo-button>
</div>
<input type="file" @change="uploadFile" oo-element="uploadFileNode" style="display: none"/>
<div class="maskNode" oo-if="$.status==='uploading'">
    <div class="loading"></div>
    <div>正在上传数据文件 ... </div>
</div>
`;
const style = `
.upload_area {
    border: 0.12rem dashed #cccccc;
    border-radius: 1rem;
    width: 90%;
    height: 12rem;
    margin-top: 3rem;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    font-size: 14px;
}
.upload_area.over{
    background-color: #f1f1f1;
}
.upload_area>div{
    line-height: 1.8em
}
.upload_name{
    display: flex;
    width: 90%;
    justify-content: space-between;
    align-items: center;
    margin: 2rem 0;
}
.delete_button{
    font-size: 0.725rem;
    width: unset;
    height:1.5rem;
}
.loading {
  position: relative;
  width: 1.4rem;
  height: 1.4rem;
  border: 0.2rem solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;
  animation: circle infinite 0.75s linear;
  margin-right: 0.5rem;
}
.maskNode {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: #eeeeee;
    opacity: 0.8;
    display: flex;
    justify-content: center;
    align-items: center;
    border-top-right-radius: 1rem;
    border-bottom-right-radius: 1rem;
}
`;
export default component({
    template,
    style,
    autoUpdate: true,

    bind(){
        return {
            status: ''
        }
    },

    dragover(e){
        e.preventDefault();
        dom.addClass(e.target, 'over');
    },
    dragout(e){
        e.preventDefault();
        dom.removeClass(e.target, 'over');
    },
    async drop(e) {
        e.preventDefault();
        this.bind.restore.name = '';
        dom.removeClass(e.target, 'over');
        if (e.dataTransfer.files && e.dataTransfer.files.length) {
            const file = e.dataTransfer.files[0];
            const formData = new FormData();
            formData.append('file', file);
            this.bind.status = 'uploading';
            await uploadRestore(formData);
            this.bind.restore.name = file.name;
            this.bind.status = '';
        }
    },

    async uploadFile(e) {
        e.preventDefault();
        this.bind.restore.name = '';
        if (e.target.files && e.target.files.length) {
            const file = e.target.files[0];
            const formData = new FormData();
            formData.append('file', file);

            this.bind.status = 'uploading';

            await uploadRestore(formData);
            this.bind.restore.name = file.name;
            this.bind.status = '';
        }
    },
    selectFile(){
        if (this.uploadFileNode){
            this.uploadFileNode.click();
        }
    },
    async deleteUpload() {
        await cancelRestore();
        this.bind.restore.name = '';
    },
    stepPrev(){
        const step = this.$p.bind.step - 1;
        this.$p.bind.step = (step<0) ? 0 : step;
    },

    nextStep(){
        const step = this.$p.bind.step + 1;
        this.$p.bind.step = (step<5) ? step : 0;
    }

});
