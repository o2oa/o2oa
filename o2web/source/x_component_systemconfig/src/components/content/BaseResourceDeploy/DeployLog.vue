<template>
  <div class="systemconfig_area">
    <div class="systemconfig_item_title">{{lp._resource.deployLog}}</div>
    <div class="systemconfig_item_info">{{lp._resource.deployLogInfo}}</div>

    <div class="item_info">
      <el-table :data="deployLogs"
      stripe style="width: 100%"
       @expand-change="handleExpandChange"
       row-key="id"
        :expand-row-keys="expandedRowKeys"
       >
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="expand-content">
              <div v-if="row.expandLoading" class="loading">
                <el-icon class="is-loading"></el-icon>
                {{ lp._resource.loading }}
              </div>
              <div v-else-if="row.expandError" class="error">
                {{ lp._resource.loadError }}
                <el-button size="small" @click.stop="retryLoad(row)">
                  {{ lp._resource.retry }}
                </el-button>
              </div>
              <div v-else-if="row.expandData">
                <p style="padding-left: 50px;">{{lp._resource.remark}}: {{ row.expandData.remark }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="sequence" :label="lp._resource.sequence" width="70"/>
        <el-table-column prop="typeCn" :label="lp._resource.type" width="100"/>
        <el-table-column prop="title" :label="lp._resource.title"/>
        <el-table-column prop="name" :label="lp._resource.fileName"/>
        <el-table-column prop="version" :label="lp._resource.version" width="200"/>
        <el-table-column prop="installPersonCn" :label="lp._resource.installPerson" width="80"/>
        <el-table-column prop="installTime" :label="lp._resource.installTime" width="180"/>
      </el-table>
      <el-pagination style="margin-top:20px;" layout="prev, pager, next" :default-page-size="size" :total="total" @current-change="handleCurrentChange"  hide-on-single-page/>
  </div>
  </div>

</template>

<script setup>
import {ref, nextTick} from 'vue';
import {lp} from '@o2oa/component';
import {listDeployLog, getDeployLogData} from '@/util/acrions';

const deployLogs = ref([]);
const size = 10;
const total = ref(0);
const currentPage = ref(1);
const expandCache = ref(new Map());
const expandedRowKeys = ref([]);

const loadPaging = async () => {
  const json = await listDeployLog(currentPage.value, size);

  expandedRowKeys.value = [];

  deployLogs.value = json.data.map((l, i)=>({
    ...l,
    sequence: (currentPage.value - 1) * 10 + i + 1,
    installPersonCn: l.installPerson.split('@')[0],
    typeCn: lp._resource[l.type],
    expandLoading: false,
    expandError: false,
    expandData: null
  }));
  total.value = json.count;
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  loadPaging();
};

const handleExpandChange = async (row, expandedRows) => {
  expandedRowKeys.value = expandedRows.map(r => r.id);

  if (expandedRows.includes(row)) {
    await nextTick();
    await loadExpandData(row);
  }
};

const loadExpandData = async (row) => {
  if (row.expandLoading || row.expandData) return;

  try {
    row.expandLoading = true;
    row.expandError = false;

    if (expandCache.value.has(row.id)) {
      row.expandData = expandCache.value.get(row.id);
    } else {
      const data = await getDeployLogData(row.id);
      if( !data.remark )data.remark = lp._resource.noRemark;
      row.expandData = data;
      expandCache.value.set(row.id, data);
    }
  } catch (error) {
    row.expandError = true;
    console.error('加载展开数据失败:', error);
  } finally {
    row.expandLoading = false;
  }
};

const retryLoad = (row) => {
  row.expandError = false;
  loadExpandData(row);
};

loadPaging();


</script>

<style scoped>
.loading {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  color: var(--el-text-color-secondary);
}

.error {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  color: var(--el-color-danger);
}

.expand-content {
  padding: 10px;
}
</style>
