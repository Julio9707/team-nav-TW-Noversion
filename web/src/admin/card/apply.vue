<template>
  <div class="app-container">
    <el-row type="flex" justify="space-between">
      <div></div>
      <el-row :gutter="10" class="mb8 mt5">
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            size="small"
            icon="el-icon-edit"
            v-btn-multiple="selections"
            @click="handlePass()"
          >通過
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            size="small"
            icon="el-icon-edit"
            v-btn-single="selections"
            @click="openDialog(selections[0])"
          >審核
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            size="small"
            icon="el-icon-delete"
            v-btn-multiple="selections"
            @click="handleDelete()"
          >刪除
          </el-button>
        </el-col>
      </el-row>
    </el-row>
    <el-table
      stripe
      border
      ref="dataTable"
      v-loading="loading"
      :data="table.dataList"
      @selection-change="selections = $refs.dataTable.selection"
      empty-text="暫無資料" >
      <el-table-column label="序號" type="index" width="55" align="center" :index="table.index"/>
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="分類" align="center" prop="categoryName" show-overflow-tooltip/>
      <el-table-column label="圖示" align="center" width="80" prop="icon" class-name="narrow-padding">
        <template slot-scope="scope">
          <ivu-avatar v-if="scope.row.icon.src"
                      :src="scope.row.icon.src"
                      style="vertical-align: middle"></ivu-avatar>
          <ivu-avatar v-else
                      style="vertical-align: middle"
                      :style="{background: scope.row.icon.color}">{{ scope.row.icon.text }}
          </ivu-avatar>
        </template>
      </el-table-column>
      <el-table-column label="標題" align="center" prop="title" show-overflow-tooltip/>
      <el-table-column label="內容" align="center" prop="content" show-overflow-tooltip/>
      <el-table-column label="連結" align="center" prop="url" show-overflow-tooltip/>
      <el-table-column label="申請人" align="center" prop="applyBy" show-overflow-tooltip/>
      <el-table-column label="申請時間" align="center" prop="applyTime" show-overflow-tooltip/>
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="openDialog(scope.row)"
          >審核
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >刪除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="table.total>0"
      :total="table.total"
      :page.sync="queryParam.pageIndex"
      :limit.sync="queryParam.pageSize"
      @pagination="getList"
    />
    <edit-dialog ref="editDialog" @refresh="getList" apply></edit-dialog>
  </div>
</template>

<script>
export default {
  name: "card-index",
  components: {
    'edit-dialog': () => import("@/admin/card/dialog/index.vue"),
    'category-select': () => import('@/components/category-select/index.vue'),
  },
  data() {
    return {
      // 遮罩層
      loading: true,
      // 選中數組
      selections: [],
      // 查詢參數
      queryParam: {
        pageIndex: 1,
        pageSize: 10,
        keywords: ''
      },
      table: {
        total: 0,
        index: 1,
        dataList: []
      }
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      this.$http.get('/api/v1/apply/cards', {params: this.queryParam}).then(res => {
        this.table.dataList = res.content;
        this.table.total = res.totalElements;
        this.table.index = res.pageable.offset + 1;
        this.loading = false;
        this.$store.dispatch('user/refreshApplyCount');
      });
    },
    openDialog(item) {
      this.$refs.editDialog.open(item);
    },
    /** 刪除按鈕操作 */
    handleDelete(row) {
      const ids = row ? row.id : this.selections.map(item => item.id).join(',');
      this.$modal.confirm('是否確認刪除選中的數據項？').then(() => {
        return this.$http.delete(`/api/v1/card/${ids}`);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("刪除成功");
      });
    },
    /** 通過按鈕操作 */
    handlePass(row) {
      const ids = row ? row.id : this.selections.map(item => item.id).join(',');
      this.$modal.confirm('是否確認審核通過選中的數據項？').then(() => {
        return this.$http.patch(`/api/v1/card/${ids}/actions/pass_apply`);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("審核成功");
      });
    },
  }
}
</script>

<style scoped lang="scss">

</style>
