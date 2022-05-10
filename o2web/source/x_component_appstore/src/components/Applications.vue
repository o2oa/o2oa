<template>
  <div class="applications-area">
    <div v-for="(item, index) in applications" :key="item.id+index" class="application-item">
      <div class="application-item-pic" :style="{backgroundImage: 'url('+item.indexPic+')'}" @click="openApplication(item)"></div>
      <div class="application-item-info">
        <div class="application-item-info-title" @click="openApplication(item)">{{item.name}}</div>
        <div class="application-item-info-recommend">
          <div v-if="item.vipApp" class="application-item-info-vip">VIP</div>
          <div class="application-item-info-version">{{item.version}}</div>
        </div>
        <div class="application-item-info-category">{{item.category}}</div>
        <div class="application-item-info-action"
             :class="{mainColor_bg: checkInstallStatus(item, ['notInstalled', 'update']), grayColor_bg: checkInstallStatus(item, ['installed']), vipColor_bg: checkInstallStatus(item, ['vip'])}"
             @click="install(item, $event)">{{getActionText(item)}}</div>
        <div class="application-item-info-price">{{parseInt(item.price) ? "￥"+item.price : "Free"}}</div>
      </div>
    </div>

  </div>
</template>

<script>
import {o2, lp, component} from '@o2oa/component';

export default {
  name: 'Applications',
  data(){
    return {
      applications: [],
      lp: lp,
      category:'',
      searchKey: ''
    }
  },
  props: {
    isVip: Boolean
  },
  created(){
    this.listApplications();
  },
  mounted(){
    component.addEvent('resize', ()=>{
      this.resizeArea();
    });
    this.resizeArea();
  },
  methods: {
    resizeArea(){
      const size = this.$el.getParent().getSize();
      const w = parseInt((size.x-40)/260)*260;
      this.$el.setStyle('width', w+'px');
    },
    listApplications(){
      let o;
      let method = "listByCategoryPaging";
      if (this.searchKey){
        o = {
          name: this.searchKey,
          category: this.category || ''
        };
        method = "listPaging";
      }else{
        o = this.category || '(0)';
      }
      o2.Actions.load('x_program_center').MarketAction[method](1, 100, o).then((json)=>{
        var arr = json.data;
        this.applications = json.data;
        //this.applications = arr.concat(arr,arr,arr,arr,arr,arr)
      });
    },
    install(item, event){
      const vm = this;
      const status = this.getInstalledStatus(item);
      switch (status) {
        case 'vip':
          this.contactUs();
          break;
        case 'notInstalled':
          this.installOrUpdate(item, event.currentTarget, lp.installInfoTitle, lp.installInfo);
          break;
        default:
          this.installOrUpdate(item, event.currentTarget, lp.updateInfoTitle, lp.updateInfo);
      }
    },
    contactUs(){
      const node = new Element("div", {
        html: `<div class="appstore-contactus-text">${lp.contactUs}</div>
        <div class="appstore-contactus"></div>
        <div class="appstore-contactus-phone">${lp.phoneNumber}</div>`
      });
      var dlg = o2.DL.open({
        title: '',
        height: 400,
        content: node,
        container: component.content,
        maskNode: component.content
      });
    },
    installOrUpdate(item, e, title, info){
      component.confirm('info', e, title, info, 380, 100, function(){
        e.set('text', lp.installing);
        o2.Actions.load('x_program_center').MarketAction.installOrUpdate(item.id).then(()=>{
          component.notice(lp.installSuccess, 'success');
          e.set('text', lp.installed);
        });
        this.close()
      }, function(){
        this.close();
      }, null, component.content);
    },
    openApplication(item){
      o2.api.page.openApplication('appstore.application', {
        appId: item.id,
        appName: item.name
      });
    },
    getInstalledStatus(item){
      if (item.installStatus) return item.installStatus;
      let installStatus = '';
      if (item.vipApp && !this.isVip){
        installStatus = 'vip';
      }else if (!item.installedVersion){
        installStatus = 'notInstalled';
      }else{
        installStatus = (item.installedVersion===item.version) ? 'installed' : 'update';
      }
      item.installStatus = installStatus;
      return installStatus;
    },
    getActionText(item){
      const o = {
        vip: lp.installVip,
        notInstalled: lp.install,
        installed: lp.installed,
        update: lp.update
      }
      return o[item.installStatus || this.getInstalledStatus(item)];
    },
    checkInstallStatus(item, types){
      const installStatus = item.installStatus || this.getInstalledStatus(item);
      return types.includes(installStatus);
    }
  }
}
</script>

<style scoped>
.applications-area{
  /*display: flex;*/
  /*flex-wrap: wrap;*/
  /*justify-content: space-evenly;*/
  margin: auto;
  overflow: hidden;
}
.application-item{
  width: 240px;
  height: 462px;
  background: rgba(255,255,255,1);
  box-shadow: 0px 0px 8px 0px rgb(0 0 0 / 25%);
  border-radius: 16px;
  margin-bottom: 30px;
  cursor: pointer;
  margin-right: 20px;
  text-align: left;
  float: left;
}
.application-item-pic{
  height: 300px;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  background-size: cover;
}
.application-item-info{
  height: 162px;
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 16px;
  padding: 15px 20px;
}
.application-item-info-title{
  height: 26px;
  font-size: 20px;
  color: rgba(51,51,51,1);
  line-height: 26px;
  overflow: hidden;
  text-align: left;
}
.application-item-info-recommend{
  margin-top: 10px;
  margin-bottom: 20px;
  height: 20px;
}
.application-item-info-version{
  font-size: 14px;
  color: #999999;
  float: left;
}
.application-item-info-vip{
  border: 2px solid #FFCB17;
  padding: 0 5px;
  border-radius: 20px;
  color: #FFCB17;
  font-size: 12px;
  display: inline;
  font-weight: bold;
  float: left;
  margin-right: 20px;
}
.application-item-info-category{
  height: 24px;
  line-height: 24px;
  font-size: 14px;
  color: #999999;
  float: left;
}
.application-item-info-action{
  float: right;
  font-size: 12px;
  color: #666666;
  line-height: 24px;
  margin: 4px auto 3px auto;
  width: 70px;
  height: 24px;
  border-radius: 20px;
  text-align: center;
  cursor: pointer;
}
.application-item-info-price{
  clear: both;
  height: 24px;
  font-size: 16px;
  color: #333333;
  line-height: 24px;
}

</style>
