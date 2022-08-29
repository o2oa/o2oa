<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.cacheConfig}}</div>


    <div class="item_title">{{lp._cacheConfig.type}}</div>
    <div class="item_info" v-html="lp._cacheConfig.typeInfo"></div>
    <div v-if="cacheData">
      <BaseItem
          :config="cacheData.type"
          :allowEditor="true"
          type="select"
          :options="{guava:'guava', redis: 'redis'}"
          @changeConfig="(value)=>{cacheData.type=value; saveConfig('cache', 'type', value)}"
      ></BaseItem>

      <div v-if="cacheData.type==='redis'">
        <div class="item_title">{{lp._cacheConfig.redis}}</div>
        <div class="item_info" v-html="lp._cacheConfig.redisInfo"></div>

        <div class="item_info">
          <BaseInput :label="lp._cacheConfig.redis_host" v-model:value="cacheData.redis.host"></BaseInput>
          <BaseInput :label="lp._cacheConfig.redis_port" v-model:value="cacheData.redis.port" input-type="number"></BaseInput>
          <BaseInput :label="lp._cacheConfig.redis_user" v-model:value="cacheData.redis.user"></BaseInput>
          <BaseInput :label="lp._cacheConfig.redis_password" v-model:value="cacheData.redis.password" input-type="password" show-password></BaseInput>
          <BaseInput :label="lp._cacheConfig.redis_connectionTimeout" v-model:value="cacheData.redis.connectionTimeout"></BaseInput>
          <BaseInput :label="lp._cacheConfig.redis_socketTimeout" v-model:value="cacheData.redis.socketTimeout"></BaseInput>
          <BaseBoolean :label="lp._cacheConfig.redis_sslEnable" v-model:value="cacheData.redis.sslEnable"></BaseBoolean>
          <BaseInput :label="lp._cacheConfig.redis_index" v-model:value="cacheData.redis.index"></BaseInput>
        </div>

        <div class="item_info">
          <button style="margin-left:30px" class="mainColor_bg" @click="saveCache">{{lp._cacheConfig.saveRedis}}</button>
        </div>

      </div>

      <div v-else>
        <div class="item_title">{{lp._cacheConfig.guava_maximumSize}}</div>
        <div class="item_info" v-html="lp._cacheConfig.guava_maximumSizeInfo"></div>
        <BaseItem
            :config="cacheData.guava.maximumSize"
            :allowEditor="true"
            type="number"
            :options="{guava:'guava', redis: 'redis'}"
            @changeConfig="(value)=>{cacheData.guava.maximumSize=value.toInt(); saveConfig('cache', 'guava.maximumSize', value.toInt())}"
        ></BaseItem>

        <div class="item_title">{{lp._cacheConfig.guava_expireMinutes}}</div>
        <div class="item_info" v-html="lp._cacheConfig.guava_expireMinutesInfo"></div>
        <BaseItem
            :config="cacheData.guava.expireMinutes"
            :allowEditor="true"
            type="number"
            :options="{guava:'guava', redis: 'redis'}"
            @changeConfig="(value)=>{cacheData.guava.expireMinutes=value.toInt(); saveConfig('cache', 'guava.expireMinutes', value.toInt())}"
        ></BaseItem>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp, component} from '@o2oa/component';
import {getConfigData, saveConfig} from "@/util/acrions";
import BaseInput from '@/components/item/BaseInput.vue';
import BaseBoolean from '@/components/item/BaseBoolean.vue';
import BaseItem from '@/components/item/BaseItem.vue';


const cacheData = ref();

const saveCache = async () => {
  await saveConfig('cache', 'redis', cacheData.value.redis);
  component.notice(lp._cacheConfig.saveRedisSuccess, "success");
}

getConfigData('cache').then((data)=>{
  cacheData.value = data;
});

</script>

<style scoped>
</style>
