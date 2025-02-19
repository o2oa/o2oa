import {component} from '@o2oa/oovm';
import {setPassword} from '../../common/action.js';
import {notice} from '../../common/notice.js';

const template = `
<div class="pane_content">
    <div class="logo60"></div>
    <div class="input_title">设置密码</div>
    <oo-input oo-element="passwordFiled"
        oo-model="secret.passStr" 
        type="password" 
        required="true"
        pattern="^(?=.*[a-zA-Z])(?=.*\\d).{6,}$"
        validity-blur="true" 
        validity="密码长度至少6位，同时包含数字和字母。"
        left-icon="password" 
        placeholder="请设置管理员密码" 
        skin="icon-right:var(--oo-color-main)" 
        right-icon="{{($.secret.passStr && $m.checkPassword($.secret.passStr)) ? 'icon_ok_round' : ''}}">
    </oo-input>
    
    
    <div class="input_title">确认密码</div>
    <oo-input oo-element="confirmFiled"
        @validity="validityConfirmFiled"
        oo-model="secret.confirmPass" 
        type="password" 
        required="true"
        pattern="^(?=.*[a-zA-Z])(?=.*\\d).{6,}$"
        validity-blur="true" 
        validity="密码长度至少6位，同时包含数字和字母。"
        left-icon="password" 
        placeholder="请再次输入密码" 
        skin="icon-right:var(--oo-color-main)" 
        right-icon="{{($.secret.passStr && $.secret.confirmPass && $m.checkConfirm($.secret.passStr, $.secret.confirmPass)) ? 'icon_ok_round' : ''}}">
    </oo-input>
    
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev" style="flex: 2">上一步</oo-button>
    <oo-button @click="setPassword" style="flex: 3">下一步</oo-button>
</div>
`;
export default component({
    template,
    autoUpdate: true,

    checkConfirm(passStr, confirmPass) {
        return !passStr || !confirmPass || (this.checkPassword(confirmPass) && passStr === confirmPass);
    },
    checkPassword(str) {
        const regex = /^(?=.*[a-zA-Z])(?=.*\d).{6,}$/;
        return !str || regex.test(str);
    },
    validityConfirmFiled(e) {
        if (this.bind.secret.passStr !== this.bind.secret.confirmPass) {
            e.target.setCustomValidity('两次输入的密码不一致。');
        } else {
            e.target.setCustomValidity('');
        }
    },
    stepPrev() {
        const step = this.$p.bind.step - 1;
        this.$p.bind.step = step < 0 ? 0 : step;
    },
    async setPassword() {
        if (this.passwordFiled.checkValidity() && this.confirmFiled.checkValidity()) {
            $OOUI.mask(this.dom.parentElement);
            await setPassword(this.bind.secret.passStr);
            $OOUI.unmask(this.dom.parentElement);

            this.bind.secret.passStr = this.bind.secret.passStr;

            const step = this.$p.bind.step + 1;
            this.$p.bind.step = step < 5 ? step : 0;
        }
    },
});
