import {component} from '@o2oa/oovm';
import {setPassword} from '../../common/action.js';
import {notice} from '../../common/notice.js';

const template = `
<div class="pane_content">
    <div class="input_title">设置密码</div>
    <oo-input oo-model="secret.passStr" type="password" left-icon="password" placeholder="请设置管理员密码" skin="icon-right:var(--oo-color-main)" right-icon="{{($.secret.passStr && $m.checkPassword($.secret.passStr)) ? 'check' : ''}}"></oo-input>
    <div style="color: red; padding-left: 1em; font-size:0.875rem; height: 1rem;"><span oo-if="!$m.checkPassword($.secret.passStr)">密码必须6位以上，包含字母和数字</span></div>
    
    <div class="input_title">确认密码</div>
    <oo-input oo-model="secret.confirmPass" type="password" left-icon="password" placeholder="请再次输入密码" skin="icon-right:var(--oo-color-main)" right-icon="{{($.secret.passStr && $.secret.confirmPass && $m.checkConfirm($.secret.passStr, $.secret.confirmPass)) ? 'check' : ''}}"></oo-input>
    <div style="color: red; padding-left: 1em; font-size:0.875rem; height: 1rem;"><span oo-if="!$m.checkConfirm($.secret.passStr, $.secret.confirmPass)">确认密码和设置密码不一致</span></div>
</div>
<div class="actions">
    <oo-button @click="setPassword">下一步</oo-button>
</div>
`;
export default component({
    template,
    autoUpdate: true,

    checkConfirm(passStr, confirmPass){
        return !passStr || !confirmPass || passStr===confirmPass;
    },
    checkPassword(str){
        const regex = /^(?=.*[a-z])(?=.*\d).{6,30}$/;
        return !str || regex.test(str);
    },
    async setPassword() {
        if (!this.bind.secret.passStr || !this.bind.secret.confirmPass || !this.checkConfirm(this.bind.secret.passStr, this.bind.secret.confirmPass) || !this.checkPassword(this.bind.secret.passStr)) {
            notice.msg('设置管理员密码', '您必须为管理员（xadmin）设置密码！', 'error', {
                container: this.$p.paneNode,
                location: 'topRight'
            })
            return false;
        }
        await setPassword(this.bind.secret.passStr);
        this.bind.secret.passStr = this.bind.secret.passStr;

        const step = this.$p.bind.step + 1;
        this.$p.bind.step = (step < 5) ? step : 0;
    }

});
