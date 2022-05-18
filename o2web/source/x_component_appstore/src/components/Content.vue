<template>
  <div v-if="initialized" class="appstore-content">
    <div v-if="isConnected && isAdmin" class="appstore-content content">
      <div class="recommend-content">
        <Recommend/>
      </div>
      <div class="category-content">
        <Category/>
      </div>
      <div class="applications-content">
        <Applications ref="applications" :isVip="isVip"/>
      </div>
    </div>
    <div v-else-if="isAdmin" class="appstore-content">
      <CloudConnectTip/>
    </div>
    <div v-else class="appstore-content">
      <NotAdminTip/>
    </div>
  </div>
</template>

<script>
import {o2} from '@o2oa/component';
import Recommend from './Recommend.vue';
import Applications from './Applications.vue';
import CloudConnectTip from './CloudConnectTip.vue';
import Category from './Category.vue';
import NotAdminTip from './NotAdminTip.vue';
export default {
  name: 'Content',
  data(){
    return {
      isVip: false,
      isConnected: false,
      isAdmin: o2.AC.isAdministrator(),
      initialized: false
    };
  },
  created(){
    o2.Actions.load('x_program_center').CollectAction.login().then((json)=>{
      this.isConnected = true;
      this.initialized = true;
      this.isVip = json.data.vipUnit;
    }, ()=>{
      this.initialized = true;
      return false;
    });
  },
  provide: function () {
    const refs = this.$refs;
    return {
      filterCategory(category){
        refs.applications.category = category;
        refs.applications.listApplications();
      },
      filterKeyword(key){
        refs.applications.searchKey = key;
        refs.applications.listApplications();
      }
    }
  },

  components: {
    Recommend, Applications, CloudConnectTip, Category, NotAdminTip
  }
}
</script>
<style scoped>
  .content{
    display: flex;
    flex-direction: column;
  }
  .recommend-content{
    min-width: 1000px;
    min-height: 320px;
    height: 45%;
    padding: 20px;
  }
  .applications-content{
    height: calc(55% - 80px);
    padding: 20px;
  }
  .category-content{
    height: 80px;
    padding: 20px;
  }

</style>
