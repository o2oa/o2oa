import {component} from '@o2oa/oovm';
import {dom, cloneObject} from '@o2oa/util';
import {listDatabase, testDatabase, h2Check, h2Upgrade, h2Cancel, setDatabase, checkDatabase} from '../../common/action.js';
import template from './database.html?raw';

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
.icon{
    font-size: 3rem;
}
.ooicon-check{
    color: green;
}
.ooicon-cancel{
    color: red;
}
`;

const parseJdbcUrl = (url, type) => {
    const result = {
        type: '',
        host: '',
        port: '',
        database: '',
        params: {},
    };

    const urlPatterns = {
        sqlserver: /jdbc:([\w-]+):\/\/([^:]+):(\d+);databaseName=([^;]+);?(.*)?/i,
        gbase: /jdbc:([\w-]+):\/\/([^:\/]+)(?::(\d+))?(?:\/([^?^:]+))?(?::(.*))?/i,
        default: /jdbc:([\w-]+):(?:thin:@)?\/\/([^:\/]+)(?::(\d+))?(?:\/([^?]+))?(?:\?)?(.*)?/,
    };
    const urlPattern = urlPatterns[type] || urlPatterns.default;

    const match = url.match(urlPattern);

    if (match) {
        result.type = match[1];
        result.host = match[2];
        result.port = match[3] || '';
        result.database = match[4] || '';
        result.params = match[5] || '';
    }

    return result;
};

export default component({
    template,
    style,
    autoUpdate: true,

    bind() {
        listDatabase().then((list) => {
            this.bind.databaseList = list;
        });
        this.databaseCheck();
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
                // gbasemysql: '南大通用(MySql)',
                db2: 'DB2',
            },
            externalDataSources: {},
            testDbMessage: '',

            databaseConfigured: null,
            h2: {},
            h2_upgrade: 'no',
            databaseType: 'h2',
            databaseList: [],
        };
    },
    selectDatabaseType(type, e) {
        if (this.bind.databaseType !== type) {
            this.bind.databaseType = type;
            if (type === 'h2') this.bind.database.type = 'h2';
            e.currentTarget.querySelector('oo-radio').setAttribute('checked', true);
        }
    },
    async databaseCheck() {
        const flag = await checkDatabase();
        this.bind.databaseConfigured = flag;

        if (this.bind.database.type === 'h2') {
            this.bind.databaseType = 'h2';
        } else {
            this.bind.databaseType = 'other';
        }
    },
    getDatabaseUrl() {
        const {host, port, database, params, dbType} = this.bind.externalDataSources;
        let url = '';
        switch (this.bind.database.type) {
            case 'sqlserver':
                url = `jdbc:sqlserver://${host}:${port};DatabaseName=${database}${params ? ';' + params : ''}`;
                break;
            case 'gbase':
                url = `jdbc:gbasedbt-sqli://${host}:${port}/${database}${params ? ':' + params : ''}`;
                break;
            case 'oracle':
                url = `jdbc:oracle:thin:@//${host}:${port}/${database}${params ? '?' + params : ''}`;
                break;
            default:
                url = `jdbc:${dbType}://${host}:${port}/${database}${params ? '?' + params : ''}`;
        }
        this.bind.externalDataSources.url = url;
        return url;
    },
    changeDb(e) {
        this.bind.testDbMessage = '';
        if (this.bind.database.type !== 'h2') {
            const o = this.bind.databaseList.find((db) => {
                return db.type === this.bind.database.type;
            });
            if (o) {
                const dbInfo = parseJdbcUrl(o.externalDataSources[0].url, o.type);
                const db = cloneObject(o.externalDataSources[0]);
                db.host = dbInfo.host;
                db.port = dbInfo.port;
                db.database = dbInfo.database;
                db.params = dbInfo.params;
                db.dbType = dbInfo.type;
                this.bind.externalDataSources = db;
            }
        }
        this.bind.testDbMessage = '';
    },
    async test() {
        this.bind.testDbMessage = '';
        dom.setStyle(this.testLoading, 'display', 'block');
        this.testDatabaseButton.setAttribute('disabled', true);
        const json = await testDatabase(this.bind.externalDataSources);
        if (json[0].success) {
            this.bind.testDbMessage = 'success';
        } else {
            this.bind.testDbMessage = json[0].failureMessage;
        }
        dom.setStyle(this.testLoading, 'display', 'none');
        this.testDatabaseButton.setAttribute('disabled', false);

        return this.bind.testDbMessage === 'success';
    },
    stepPrev() {
        const step = this.$p.bind.step - 1;
        this.$p.bind.step = step < 0 ? 0 : step;
    },

    validityDatabaseType(e) {
        debugger;
        if (this.bind.databaseType !== 'h2' && this.bind.database.type === 'h2') {
            e.target.setCustomValidity('请选择外置数据库类型。');
        } else {
            e.target.setCustomValidity('');
        }
    },
    async nextStep() {
        if (this.databaseTypeNode && !this.databaseTypeNode.checkValidity()) {
            return false;
        }
        if (this.bind.databaseConfigured) {
            this.bind.database.type = '$configured';
        } else {
            if (this.bind.database.type !== 'h2') {
                this.nextButton.setAttribute('disabled', true);
                if (!(await this.test())) {
                    this.nextButton.setAttribute('disabled', false);
                    return false;
                }
                $OOUI.mask(this.dom.parentElement);
                await setDatabase(this.bind.externalDataSources);
                $OOUI.unmask(this.dom.parentElement);
                this.nextButton.setAttribute('disabled', false);
                this.bind.database.type = this.bind.database.type;
                this.bind.database.url = this.bind.externalDataSources.url;
            } else {
                this.bind.database.type = this.bind.database.type;
            }
        }

        const step = this.$p.bind.step + 1;
        this.$p.bind.step = step < 5 ? step : 0;
    },
});
