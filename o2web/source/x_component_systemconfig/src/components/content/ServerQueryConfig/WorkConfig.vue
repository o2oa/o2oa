<template>
	<div>
	  <div class="item_info" v-html="lp._queryConfig.restartServerInfo"></div>
		<div class="item_title">{{lp._queryConfig.workIndexAttachment}}</div>
		<div class="item_info">{{lp._queryConfig.workIndexAttachmentInfo}}</div>
		<div class="item_info">
			<el-switch
					@change="saveConfig('query', 'index.workIndexAttachment', index.workIndexAttachment)"
					v-model="index.workIndexAttachment"
					:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
			</el-switch>
		</div>

		<hr>
		<div class="item_title">{{lp._queryConfig.highFreqWorkEnable}}</div>
		<div class="item_info">{{lp._queryConfig.highFreqWorkEnableInfo}}</div>
		<div class="item_info">
			<el-switch
					@change="saveConfig('query', 'index.highFreqWorkEnable', index.highFreqWorkEnable)"
					v-model="index.highFreqWorkEnable"
					:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
			</el-switch>
		</div>

		<div v-if="!!index.highFreqWorkEnable">
			<div class="item_title">{{lp._queryConfig.highFreqWorkCron}}</div>
			<div class="item_info">{{lp._queryConfig.highFreqWorkCronInfo}}</div>
			<div class="item_info">
				<BaseCron v-model:value="index.highFreqWorkCron" @change="(value)=>{index.highFreqWorkCron=value; saveConfig('query', 'index.highFreqWorkCron', value)}"></BaseCron>
			</div>

			<BaseItem :title="lp._queryConfig.highFreqWorkMaxCount"
					  :config="index.highFreqWorkMaxCount"
					  type="number"
					  :info="lp._queryConfig.highFreqWorkMaxCount"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.highFreqWorkMaxCount=value; saveConfig('query', 'index.highFreqWorkMaxCount', value)}"/>

			<BaseItem :title="lp._queryConfig.highFreqWorkMaxMinutes"
					  :config="index.highFreqWorkMaxMinutes"
					  type="number"
					  :info="lp._queryConfig.highFreqWorkMaxMinutes"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.highFreqWorkMaxMinutes=value; saveConfig('query', 'index.highFreqWorkMaxMinutes', value)}"/>
		</div>

		<hr>

		<div class="item_title">{{lp._queryConfig.lowFreqWorkEnable}}</div>
		<div class="item_info">{{lp._queryConfig.lowFreqWorkEnableInfo}}</div>
		<div class="item_info">
			<el-switch
					@change="saveConfig('query', 'index.lowFreqWorkEnable', index.lowFreqWorkEnable)"
					v-model="index.lowFreqWorkEnable"
					:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
			</el-switch>
		</div>

		<div v-if="!!index.lowFreqWorkEnable">
			<div class="item_title">{{lp._queryConfig.lowFreqWorkCron}}</div>
			<div class="item_info">{{lp._queryConfig.lowFreqWorkCronInfo}}</div>
			<div class="item_info">
				<BaseCron v-model:value="index.lowFreqWorkCron" @change="(value)=>{index.lowFreqWorkCron=value; saveConfig('query', 'index.lowFreqWorkCron', value)}"></BaseCron>
			</div>


			<BaseItem :title="lp._queryConfig.lowFreqWorkMaxCount"
					  :config="index.lowFreqWorkMaxCount"
					  type="number"
					  :info="lp._queryConfig.lowFreqWorkMaxCountInfo"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.lowFreqWorkMaxCount=value; saveConfig('query', 'index.lowFreqWorkMaxCount', value)}"/>

			<BaseItem :title="lp._queryConfig.lowFreqWorkMaxMinutes"
					  :config="index.lowFreqWorkMaxMinutes"
					  type="number"
					  :info="lp._queryConfig.lowFreqWorkMaxMinutesInfo"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.lowFreqWorkMaxMinutes=value; saveConfig('query', 'index.lowFreqWorkMaxMinutes', value)}"/>
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
