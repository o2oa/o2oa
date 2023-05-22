<template>
	<div>
	  <div class="item_info" v-html="lp._queryConfig.restartServerInfo"></div>
	  <div class="item_title">{{lp._queryConfig.enable}}</div>
	  <div class="item_info">{{lp._queryConfig.enable}}</div>
	  <div class="item_info">
		<el-switch
				@change="saveConfig('query', 'index.enable', index.enable)"
				v-model="index.enable"
				:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
		</el-switch>
	  </div>

	  <BaseItem :title="lp._queryConfig.indexMode"
				:info="lp._queryConfig.modeConfigInfo"
				:config="index.mode"
				:allowEditor="true"
				type="select"
				:options="lp._queryConfig.modeOptions"
				@changeConfig="(value)=>{index.mode=value; saveConfig('query', 'index.mode', value)}"/>

	  <div v-if="index.mode==='hdfsDirectory'">
		  <BaseItem :title="lp._queryConfig.hdfsDirectoryDefaultFS"
					:config="index.hdfsDirectoryDefaultFS"
					:allowEditor="true"
					@changeConfig="(value)=>{index.hdfsDirectoryDefaultFS=value; saveConfig('query', 'index.hdfsDirectoryDefaultFS', value)}"/>

		  <BaseItem :title="lp._queryConfig.hdfsDirectoryPath"
					:config="index.hdfsDirectoryPath"
					:allowEditor="true"
					@changeConfig="(value)=>{index.hdfsDirectoryPath=value; saveConfig('query', 'index.hdfsDirectoryPath', value)}"/>
	  </div>

	  <div v-if="index.mode==='sharedDirectory'">
		  <BaseItem :title="lp._queryConfig.sharedDirectoryPath"
					:config="index.sharedDirectoryPath"
					:allowEditor="true"
					@changeConfig="(value)=>{index.sharedDirectoryPath=value; saveConfig('query', 'index.sharedDirectoryPath', value)}"/>
	  </div>


	  <div class="item_title">{{lp._queryConfig.optimizeIndexEnable}}</div>
	  <div class="item_info">{{lp._queryConfig.optimizeIndexEnableInfo}}</div>
	  <div class="item_info">
		  <BaseBoolean v-model:value="index.optimizeIndexEnable" :label="lp._queryConfig.optimizeIndexEnable" @change="(value)=>{index.optimizeIndexEnable=value; saveConfig('query', 'index.optimizeIndexEnable', value)}"></BaseBoolean>
		  <BaseCron v-model:value="index.optimizeIndexCron" :label="lp._queryConfig.cron" @change="(value)=>{index.optimizeIndexCron=value; saveConfig('query', 'index.optimizeIndexCron', value)}"></BaseCron>
	  </div>

	  <BaseItem :title="lp._queryConfig.dataStringThreshold"
				:config="index.dataStringThreshold"
              type="number"
        :info="lp._queryConfig.dataStringThresholdInfo"
				:allowEditor="true"
				@changeConfig="(value)=>{index.dataStringThreshold=value; saveConfig('query', 'index.dataStringThreshold', value)}"/>

	  <BaseItem :title="lp._queryConfig.summaryLength"
				:config="index.summaryLength"
			  type="number"
				:info="lp._queryConfig.summaryLength"
				:allowEditor="true"
				@changeConfig="(value)=>{index.summaryLength=value; saveConfig('query', 'index.summaryLength', value)}"/>

	  <BaseItem :title="lp._queryConfig.attachmentMaxSize"
				:config="index.attachmentMaxSize"
				type="number"
				:info="lp._queryConfig.attachmentMaxSizeInfo"
				:allowEditor="true"
				@changeConfig="(value)=>{index.attachmentMaxSize=value; saveConfig('query', 'index.attachmentMaxSize', value)}"/>

	  <BaseItem :title="lp._queryConfig.cleanupThresholdDays"
				:config="index.cleanupThresholdDays"
				type="number"
				:info="lp._queryConfig.cleanupThresholdDaysInfo"
				:allowEditor="true"
				@changeConfig="(value)=>{index.cleanupThresholdDays=value; saveConfig('query', 'index.cleanupThresholdDays', value)}"/>

	  <BaseItem :title="lp._queryConfig.searchMaxPageSize"
				:config="index.searchMaxPageSize"
				type="number"
				:info="lp._queryConfig.searchMaxPageSizeInfo"
				:allowEditor="true"
				@changeConfig="(value)=>{index.searchMaxPageSize=value; saveConfig('query', 'index.searchMaxPageSize', value)}"/>

	  <BaseItem :title="lp._queryConfig.moreLikeThisMaxSize"
				:config="index.moreLikeThisMaxSize"
				type="number"
				:info="lp._queryConfig.moreLikeThisMaxSizeInfo"
				:allowEditor="true"
				@changeConfig="(value)=>{index.moreLikeThisMaxSize=value; saveConfig('query', 'index.moreLikeThisMaxSize', value)}"/>

	</div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import BaseCron from '@/components/item/BaseCron.vue';
import BaseBoolean from "@/components/item/BaseBoolean";
import {getConfigData, saveConfig} from '@/util/acrions';
import BaseItem from '@/components/item/BaseItem.vue';
import defaultJson from './query.json';

const index = ref({});
const load = async () => {
    getConfigData('query').then((data)=>{
        index.value = Object.assign(defaultJson, data.index);
        debugger;
    });
}
load();
</script>

<style scoped>
.item{
    padding: 10px 30px 10px 0;
}
</style>
