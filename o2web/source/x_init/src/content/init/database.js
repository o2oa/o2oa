import {component} from '@o2oa/oovm';
import {dom, cloneObject} from '@o2oa/util';
import {listDatabase, testDatabase, h2Check, h2Upgrade, h2Cancel, setDatabase} from '../../common/action.js';
import {notice} from '../../common/notice.js';

const template = `
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title" style="padding: 0.5rem 0; margin-top: 0">选择数据库</div>
    <div style="color:#777777; font-size: 0.875rem">
        <div style="padding: 0.5em 0.3em; margin-top: 0.5rem; border: 1px solid #cccccc; border-radius: 0.5rem">
            <oo-radio-group name="database_type" col="4" oo-model="database.type" @change="changeDb" oo-element="databaseTypeNode">
                <oo-radio value="h2" name="database_type" style="margin: 0.3em 0em 0.3em 0">H2(内置数据库)</oo-radio>
                <oo-radio oo-each="$.databaseList" oo-item="db"  value="{{db.value.type}}" text="{{$.databaseName[db.value.type] || db.value.type}}" style="margin: 0.3em 0em 0.3em 0"></oo-radio>
            </oo-radio-group>
        </div>
    </div>
    <div oo-if="$.database.type!=='h2'">
        <div class="input_title" style="padding: 0.5rem 0; margin-top: 1rem">数据库配置</div>
        <div style="color:#777777; font-size: 0.875rem">
            <div style="padding: 0.5em 0.3em; margin-top: 0.5rem; border: 1px solid #cccccc; border-radius: 0.5rem">
                <oo-textarea label="数据库连接：" style="margin-top:0.5rem; width: 98%" oo-model="externalDataSources.url" spellcheck="false"></oo-textarea>
                <div style="margin-top:1rem; margin-bottom:1rem; width: 98%; display: flex; justify-content: space-between;">
                    <oo-input label="用户名：　　" style="width: 48%" oo-model="externalDataSources.username"></oo-input>
                    <oo-input label="密码：" style="width: 48%" oo-model="externalDataSources.password"></oo-input>
                </div>
                
                <div style="width: 98%; display: flex; justify-content: space-between;">
                    <div style="font-size: 0.725rem; width: calc(100% - 8.5rem); padding-left: 0.5rem; display: flex; align-items: center;">
                        <div class="loading" oo-element="testLoading"></div>
                        <div oo-if="$.testDbMessage==='success'" style="color: green">连接成功！</div>
                        <div oo-if="$.testDbMessage && $.testDbMessage!=='success'"  style="color: red">连接失败:{{$.testDbMessage}}</div>
                    </div>
                    <oo-button @click="test" style="width: 8.5rem;">测试数据库连接</oo-button>
                </div>
               
            </div>
        </div>
    </div>
<!--    <div oo-else>-->
<!--        <div class="input_title" style="padding: 0.5rem 0; margin-top: 1rem">H2数据库升级</div>-->
<!--        -->
<!--        <div style="padding: 0.5rem; text-align: center; font-size: 0.875rem; color: #666666">-->
<!--            本地h2版本：{{$.h2.localRepositoryDataH2Version}}-->
<!--        </div>-->
<!--        <div oo-if="$.h2.needUpgrade" style="padding: 0.5rem; text-align: center; font-size: 0.875rem; color: #666666">-->
<!--            可升级h2版本：{{$.h2.jarVersion}}-->
<!--        </div>-->
<!--        -->
<!--        <div oo-if="!$.h2.needUpgrade" style="padding: 0.5rem; text-align: center; font-size: 0.875rem; color: #666666">H2数据库版本已经是最新，不需要升级！</div>-->
<!--        <div oo-else style="padding: 0.5rem; text-align: center; font-size: 0.875rem; color: #666666; display: flex; justify-content: center;">-->
<!--            <oo-radio-group name="h2_upgrade" oo-model="h2_upgrade" label="是否升级H2:　" oo-element="h2UpgradeNode">-->
<!--                <oo-radio value="yes">是</oo-radio>-->
<!--                <oo-radio value="no">否</oo-radio>-->
<!--            </oo-radio-group>-->
<!--        </div>-->
<!--    </div>-->
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev">上一步</oo-button>
    <oo-button @click="nextStep">下一步</oo-button>
</div>
`;
const style = `
.loading {
  position: relative;
  width: 20px;
  height: 20px;
  border: 2px solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;
  display: none;
  animation: circle infinite 0.75s linear;
}
`;

export default component({
    template,
    style,
    autoUpdate: true,

    bind() {
        listDatabase().then((list)=>{
            this.bind.databaseList = list;
        });
        // h2Check().then((o)=>{
        //     this.bind.h2 = o;
        // });

        return {
            databaseName: {
                sqlserver: 'SQL Server',
                oracle: 'Oracle',
                postgresql: 'PostgreSQL',
                mysql: 'MySQL',
                dm: '达梦',
                kingbase: '人大金仓V7',
                kingbase8: '人大金仓V8',
                informix: 'Informix',
                gbase: '南大通用',
                gbasemysql: '南大通用(MySql)',
                db2: 'DB2'
            },
            externalDataSources: {},
            testDbMessage: '',

            h2:{},
            h2_upgrade: 'no'
        }
    },
    changeDb(e){
        this.bind.testDbMessage = '';
        if (this.bind.database.type!=='h2'){
            const o = this.bind.databaseList.find((db)=>{
                return db.type === this.bind.database.type;
            });
            if (o) this.bind.externalDataSources = cloneObject(o.externalDataSources[0]);
        }
        this.bind.testDbMessage = '';
    },
    async test(e) {
        this.bind.testDbMessage = '';
        dom.setStyle(this.testLoading, 'display', 'block');
        e.target.setAttribute('disabled', true)
        const json = await testDatabase(this.bind.externalDataSources);
        if (json[0].success){
            this.bind.testDbMessage = 'success';
        }else{
            this.bind.testDbMessage = json[0].failureMessage;
        }
        dom.setStyle(this.testLoading, 'display', 'none');
        e.target.setAttribute('disabled', false);
    },
    stepPrev(){
        const step = this.$p.bind.step - 1;
        this.$p.bind.step = (step<0) ? 0 : step;
    },
    async nextStep() {
        if (this.bind.database.type !== 'h2') {
            await setDatabase(this.bind.externalDataSources);
            this.bind.database.type = this.bind.database.type;
            this.bind.database.url = this.bind.externalDataSources.url;
        } else {
            // if (this.bind.h2_upgrade === 'yes') {
            //     await h2Upgrade();
            // } else {
            //     await h2Cancel();
            // }

            this.bind.database.type = this.bind.database.type;
        }
        const step = this.$p.bind.step + 1;
        this.$p.bind.step = (step < 5) ? step : 0;
    }

});
