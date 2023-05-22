<template>
	<div class="systemconfig_area">
		<div class="systemconfig_title">{{lp.queryIndexConfig}}</div>
		<div class="systemconfig_tab_area">
			<el-tabs tab-position="right">
				<el-tab-pane :label="lp._queryConfig.queryIndexConfig" lazy>
					<QueryBaseConfig></QueryBaseConfig>
				</el-tab-pane>
		  <el-tab-pane :label="lp._queryConfig.workConfig" lazy>
			  <WorkConfig></WorkConfig>
		  </el-tab-pane>

		  <el-tab-pane :label="lp._queryConfig.workCompletedConfig" lazy>
			  <WorkCompletedConfig></WorkCompletedConfig>
		  </el-tab-pane>

		  <el-tab-pane :label="lp._queryConfig.documentConfig" lazy>
			  <DocumentConfig></DocumentConfig>
		  </el-tab-pane>
		  <el-tab-pane :label="lp._queryConfig.indexTools" lazy>
			  <IndexTools></IndexTools>
		  </el-tab-pane>

		  <!--				<el-tab-pane :label="lp._serversConfig.serverInfo" lazy>-->
<!--					<QueryWorkConfig></QueryWorkConfig>-->
<!--				</el-tab-pane>-->
<!--				<el-tab-pane :label="lp._serversConfig.serverInfo" lazy>-->
<!--					<QueryWorkCompletedConfig></QueryWorkCompletedConfig>-->
<!--				</el-tab-pane>-->

<!--				<el-tab-pane :label="lp._serversConfig.serverInfo" lazy>-->
<!--					<QueryDocumentConfig></QueryDocumentConfig>-->
<!--				</el-tab-pane>-->

			</el-tabs>
		</div>

  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import QueryBaseConfig from './ServerQueryConfig/QueryBaseConfig.vue';
import WorkConfig from './ServerQueryConfig/WorkConfig.vue';
import WorkCompletedConfig from './ServerQueryConfig/WorkCompletedConfig.vue';
import DocumentConfig from './ServerQueryConfig/DocumentConfig.vue';
import IndexTools from './ServerQueryConfig/IndexTools.vue';

</script>

<style scoped>
</style>
