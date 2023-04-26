<template>
	<div>
	  <div class="item_info" v-html="lp._queryConfig.restartServerInfo"></div>
		<div class="item_title">{{lp._queryConfig.workCompletedIndexAttachment}}</div>
		<div class="item_info">{{lp._queryConfig.workCompletedIndexAttachmentInfo}}</div>
		<div class="item_info">
			<el-switch
					@change="saveConfig('query', 'index.workCompletedIndexAttachment', index.workCompletedIndexAttachment)"
					v-model="index.workCompletedIndexAttachment"
					:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
			</el-switch>
		</div>

		<hr>
		<div class="item_title">{{lp._queryConfig.highFreqWorkCompletedEnable}}</div>
		<div class="item_info">{{lp._queryConfig.highFreqWorkCompletedEnableInfo}}</div>
		<div class="item_info">
			<el-switch
					@change="saveConfig('query', 'index.highFreqWorkCompletedEnable', index.highFreqWorkCompletedEnable)"
					v-model="index.highFreqWorkCompletedEnable"
					:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
			</el-switch>
		</div>

		<div v-if="!!index.highFreqWorkCompletedEnable">
			<div class="item_title">{{lp._queryConfig.highFreqWorkCompletedCron}}</div>
			<div class="item_info">{{lp._queryConfig.highFreqWorkCompletedCronInfo}}</div>
			<div class="item_info">
				<BaseCron v-model:value="index.highFreqWorkCompletedCron" @change="(value)=>{index.highFreqWorkCompletedCron=value; saveConfig('query', 'index.highFreqWorkCompletedCron', value)}"></BaseCron>
			</div>

			<BaseItem :title="lp._queryConfig.highFreqWorkCompletedMaxCount"
					  :config="index.highFreqWorkCompletedMaxCount"
					  type="number"
					  :info="lp._queryConfig.highFreqWorkCompletedMaxCount"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.highFreqWorkCompletedMaxCount=value; saveConfig('query', 'index.highFreqWorkCompletedMaxCount', value)}"/>

			<BaseItem :title="lp._queryConfig.highFreqWorkCompletedMaxMinutes"
					  :config="index.highFreqWorkCompletedMaxMinutes"
					  type="number"
					  :info="lp._queryConfig.highFreqWorkCompletedMaxMinutes"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.highFreqWorkCompletedMaxMinutes=value; saveConfig('query', 'index.highFreqWorkCompletedMaxMinutes', value)}"/>
		</div>

		<hr>
		<div class="item_title">{{lp._queryConfig.lowFreqWorkCompletedEnable}}</div>
		<div class="item_info">{{lp._queryConfig.lowFreqWorkCompletedEnableInfo}}</div>
		<div class="item_info">
			<el-switch
					@change="saveConfig('query', 'index.lowFreqWorkCompletedEnable', index.lowFreqWorkCompletedEnable)"
					v-model="index.lowFreqWorkCompletedEnable"
					:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
			</el-switch>
		</div>

		<div v-if="!!index.lowFreqWorkCompletedEnable">
			<div class="item_title">{{lp._queryConfig.lowFreqWorkCompletedCron}}</div>
			<div class="item_info">{{lp._queryConfig.lowFreqWorkCompletedCronInfo}}</div>
			<div class="item_info">
				<BaseCron v-model:value="index.lowFreqWorkCompletedCron" @change="(value)=>{index.lowFreqWorkCompletedCron=value; saveConfig('query', 'index.lowFreqWorkCompletedCron', value)}"></BaseCron>
			</div>


			<BaseItem :title="lp._queryConfig.lowFreqWorkCompletedMaxCount"
					  :config="index.lowFreqWorkCompletedMaxCount"
					  type="number"
					  :info="lp._queryConfig.lowFreqWorkCompletedMaxCountInfo"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.lowFreqWorkCompletedMaxCount=value; saveConfig('query', 'index.lowFreqWorkCompletedMaxCount', value)}"/>

			<BaseItem :title="lp._queryConfig.lowFreqWorkCompletedMaxMinutes"
					  :config="index.lowFreqWorkCompletedMaxMinutes"
					  type="number"
					  :info="lp._queryConfig.lowFreqWorkCompletedMaxMinutesInfo"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.lowFreqWorkCompletedMaxMinutes=value; saveConfig('query', 'index.lowFreqWorkCompletedMaxMinutes', value)}"/>
		</div>

	</div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import BaseCron from '@/components/item/BaseCron.vue';

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
hr {
    margin: 20px 0 10px 0;
}
</style>
