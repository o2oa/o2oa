<template>
	<div>
	  <div class="item_info" v-html="lp._queryConfig.restartServerInfo"></div>

		<div class="item_title">{{lp._queryConfig.documentIndexAttachment}}</div>
		<div class="item_info">{{lp._queryConfig.documentIndexAttachmentInfo}}</div>
		<div class="item_info">
			<el-switch
					@change="saveConfig('query', 'index.documentIndexAttachment', index.documentIndexAttachment)"
					v-model="index.documentIndexAttachment"
					:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
			</el-switch>
		</div>

		<hr>
		<div class="item_title">{{lp._queryConfig.highFreqDocumentEnable}}</div>
		<div class="item_info">{{lp._queryConfig.highFreqDocumentEnableInfo}}</div>
		<div class="item_info">
			<el-switch
					@change="saveConfig('query', 'index.highFreqDocumentEnable', index.highFreqDocumentEnable)"
					v-model="index.highFreqDocumentEnable"
					:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
			</el-switch>
		</div>

		<div v-if="!!index.highFreqDocumentEnable">
			<div class="item_title">{{lp._queryConfig.highFreqDocumentCron}}</div>
			<div class="item_info">{{lp._queryConfig.highFreqDocumentCronInfo}}</div>
			<div class="item_info">
				<BaseCron v-model:value="index.highFreqDocumentCron" @change="(value)=>{index.highFreqDocumentCron=value; saveConfig('query', 'index.highFreqDocumentCron', value)}"></BaseCron>
			</div>

			<BaseItem :title="lp._queryConfig.highFreqDocumentMaxCount"
					  :config="index.highFreqDocumentMaxCount"
					  type="number"
					  :info="lp._queryConfig.highFreqDocumentMaxCount"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.highFreqDocumentMaxCount=value; saveConfig('query', 'index.highFreqDocumentMaxCount', value)}"/>

			<BaseItem :title="lp._queryConfig.highFreqDocumentMaxMinutes"
					  :config="index.highFreqDocumentMaxMinutes"
					  type="number"
					  :info="lp._queryConfig.highFreqDocumentMaxMinutes"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.highFreqDocumentMaxMinutes=value; saveConfig('query', 'index.highFreqDocumentMaxMinutes', value)}"/>
		</div>

		<hr>

		<div class="item_title">{{lp._queryConfig.lowFreqDocumentEnable}}</div>
		<div class="item_info">{{lp._queryConfig.lowFreqDocumentEnableInfo}}</div>
		<div class="item_info">
			<el-switch
					@change="saveConfig('query', 'index.lowFreqDocumentEnable', index.lowFreqDocumentEnable)"
					v-model="index.lowFreqDocumentEnable"
					:active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
			</el-switch>
		</div>

		<div v-if="!!index.lowFreqDocumentEnable">
			<div class="item_title">{{lp._queryConfig.lowFreqDocumentCron}}</div>
			<div class="item_info">{{lp._queryConfig.lowFreqDocumentCronInfo}}</div>
			<div class="item_info">
				<BaseCron v-model:value="index.lowFreqDocumentCron" @change="(value)=>{index.lowFreqDocumentCron=value; saveConfig('query', 'index.lowFreqDocumentCron', value)}"></BaseCron>
			</div>


			<BaseItem :title="lp._queryConfig.lowFreqDocumentMaxCount"
					  :config="index.lowFreqDocumentMaxCount"
					  type="number"
					  :info="lp._queryConfig.lowFreqDocumentMaxCountInfo"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.lowFreqDocumentMaxCount=value; saveConfig('query', 'index.lowFreqDocumentMaxCount', value)}"/>

			<BaseItem :title="lp._queryConfig.lowFreqDocumentMaxMinutes"
					  :config="index.lowFreqDocumentMaxMinutes"
					  type="number"
					  :info="lp._queryConfig.lowFreqDocumentMaxMinutesInfo"
					  :allowEditor="true"
					  @changeConfig="(value)=>{index.lowFreqDocumentMaxMinutes=value; saveConfig('query', 'index.lowFreqDocumentMaxMinutes', value)}"/>
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
