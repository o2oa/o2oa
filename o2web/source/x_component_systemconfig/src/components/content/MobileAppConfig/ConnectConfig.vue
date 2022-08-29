<template>
  <div class="systemconfig_area">
    <div class="item_title">{{lp._appConfig.cloudConnect}}</div>

    <div v-if="connectedCollect" class="item_info" v-html="lp._appConfig.connectedInfo"></div>
    <div v-else class="item_info" v-html="lp._appConfig.notConnectedInfo"></div>

    <div v-if="proxyData">
      <BaseItem
          :title="lp._appConfig.httpProtocol"
          :info="lp._appConfig.httpProtocolInfo"
          :config="proxyData.httpProtocol"
          :allowEditor="true"
          type="select"
          :options="{http: 'http', https: 'https'}"
          @changeConfig="(value)=>{proxyData.httpProtocol = value; setProxy(proxyData)}"
      ></BaseItem>


      <div class="item_title">{{lp._appConfig.centerServer}}</div>
      <div class="item_info" v-html="lp._appConfig.centerServerInfo"></div>
      <div class="item_info" style="display: flex; justify-content: flex-start;">
        <div class="item_server_item" @click="editCenterServer">
          <div class="item_server_area">
            <div class="o2icon-center item_config_icon mainColor_bg"></div>
            <div class="item_server_item_slot item_bold" style="min-width: 340px;">{{proxyData.center.proxyHost}}:{{proxyData.center.proxyPort}}</div>
          </div>
        </div>
      </div>


      <div class="item_title">{{lp._appConfig.webServer}}</div>
      <div class="item_info" v-html="lp._appConfig.webServerInfo"></div>
      <div class="item_info" style="display: flex; justify-content: flex-start;">
        <div class="item_server_item" @click="editWebServer">
          <div class="item_server_area">
            <div class="o2icon-web item_config_icon mainColor_bg"></div>
            <div class="item_server_item_slot item_bold" style="min-width: 340px;">{{proxyData.web.proxyHost}}:{{proxyData.web.proxyPort}}</div>
          </div>
        </div>
      </div>


      <div class="item_title">{{lp._appConfig.applicationServer}}</div>
      <div class="item_info" v-html="lp._appConfig.applicationServerInfo"></div>
      <div class="item_info" v-for="(server, index) in proxyData.applicationList" style="display: flex; justify-content: flex-start;">
        <div class="item_server_item"  @click="editAppServer(server, index)">
          <div class="item_server_area">
            <div class="o2icon-servers item_config_icon mainColor_bg"></div>
            <div class="item_server_item_slot item_bold" style="min-width: 340px;">{{server.proxyHost}}:{{server.proxyPort}}</div>
          </div>
        </div>
      </div>


      <div class="item_title">{{lp._appConfig.connectTest}}</div>
      <div class="item_info" v-html="lp._appConfig.connectTestInfo"></div>

      <div class="item_info">
        <button class="mainColor_bg" @click="getCheckConnectQrcode">{{lp._appConfig.getQrcode}}</button>
      </div>

      <div class="item_info" v-if="qrcode">
        <img :src="'data:image/png;base64,'+qrcode" />
      </div>


    </div>


    <div class="item_server_editorArea" ref="serverEditorArea">
      <BaseInput :label="lp._appConfig.host" v-model:value="currentServerData.proxyHost" />
      <BaseInput :label="lp._appConfig.port" v-model:value="currentServerData.proxyPort" input-type="number"/>
    </div>


  </div>

</template>

<script setup>
import {ref} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {validateCollect, getProxy, setProxy, mobileCheckConnect} from "@/util/acrions";
import BaseItem from '@/components/item/BaseItem.vue';
import BaseInput from '@/components/item/BaseInput.vue';


const connectedCollect = ref(false);
const proxyData = ref();
const currentServerData = ref({});
const serverEditorArea = ref();

const qrcode = ref('');

const openEditDlg = (data, node, cb, width, height)=>{
  currentServerData.value = Object.clone(data);
  const container = component.content.getElement('.systemconfig');
  const content = node.value;
  content.show();

  o2.DL.open({
    title: lp._appConfig.editServer,
    container,
    maskNode: container,
    width,
    height,
    content,
    onQueryClose: () => {
      content.hide();
      content.inject(container);
    },
    buttonList: [{
      text: lp.operation.ok,
      type: 'ok',
      action: async (dlg) => {
        cb(dlg);
      }
    }, {
      text: lp.operation.cancel,
      type: 'cancel',
      action: dlg => dlg.close()
    }]
  })
}

const editCenterServer = ()=>{
  openEditDlg(proxyData.value.center, serverEditorArea, (dlg)=>{
    proxyData.value.center = currentServerData.value;
    setProxy(proxyData.value);
    dlg.close();
  }, 620, 250);
}
const editWebServer = ()=>{
  openEditDlg(proxyData.value.web, serverEditorArea, (dlg)=>{
    proxyData.value.web = currentServerData.value;
    setProxy(proxyData.value);
    dlg.close();
  }, 620, 250);
}
const editAppServer = (server, index)=>{
  openEditDlg(server, serverEditorArea, (dlg)=>{
    proxyData.value.applicationList[index] = currentServerData.value;
    setProxy(proxyData.value);
    dlg.close();
  }, 620, 250);
}

const getCheckConnectQrcode = ()=>{
  mobileCheckConnect().then((data)=>{
    qrcode.value = data.qrcode;
  });
}

const load = ()=>{
  validateCollect().then((data)=>{
    connectedCollect.value = data.value;
  });
  getProxy().then((data)=>{
    proxyData.value = data;
  });
}
load();




</script>

<style scoped>
.item_server_item{
  height: 60px;
  cursor: pointer;
  border-radius: 20px;
}
.item_server_item:hover{
  background-color: #f1f1f1;
}
.item_server_area{
  display: flex;
  align-items: center;
  padding: 10px;
}
.item_config_icon{
  width: 40px;
  height: 40px;
  text-align: center;
  line-height: 40px!important;
  border-radius: 40%;
  margin-right: 10px;
  font-size: 16px;
}
.item_bold{
  font-weight: bold;
  color: #333333;
}
.item_server_editorArea{
  display: none;
  padding: 20px;
}
</style>
