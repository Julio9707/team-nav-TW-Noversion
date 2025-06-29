<template>
  <div class="app-container">
    <el-row type="flex" justify="space-between">
      <el-col :span="1"></el-col>
      <el-row :gutter="10" class="mb8 mt5">
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-plus"
            size="small"
            @click="openDialog()"
          >新增
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
          >修改
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
        <el-col :span="1.5">
          <el-button
            plain
            size="small"
            icon="el-icon-delete"
            :disabled="dataList.length<2"
            @click="openSortDialog()"
          >排序
          </el-button>
        </el-col>
      </el-row>
    </el-row>
    <el-table
      stripe
      border
      ref="dataTable"
      v-loading="loading"
      :data="dataList"
      @selection-change="selections = $refs.dataTable.selection">
      <el-table-column label="序號" type="index" width="55" align="center"/>
      <el-table-column type="selection" width="50" align="center"/>
      <el-table-column label="公告內容" prop="content">
        <template slot-scope="scope">
          <div v-html="scope.row.content"></div>
        </template>
      </el-table-column>
      <el-table-column label="結束時間" align="center" prop="endTime"/>
      <el-table-column label="'狀態" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.status || !Boolean(scope.row.endTime)">啟用中</el-tag>
          <el-tag v-else type="danger">已停止</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="openDialog(scope.row)"
          >修改
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
    <edit-dialog ref="editDialog" @refresh="getList"></edit-dialog>
    <sort-dialog ref="sortDialog" @change-sort="changeSort"></sort-dialog>
  </div>
</template>

<script>
export default {
  name: "category-index",
  components: {
    'edit-dialog': () => import('@/admin/notice/dialog/index.vue'),
    'sort-dialog': () => import('@/admin/components/sort-dialog'),
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      selections: [],
      // 数据
      dataList: [],
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      this.$http.get(`/api/v1/notice`)
        .then(res => {
          this.dataList = res;
        }).finally(() => {
        this.loading = false;
      });
    },
    changeSort(draggingData, dropData, type) {
      const data = {
        draggingId: draggingData.id,
        dropId: dropData.id,
        type: type
      };
      this.$http.patch('/api/v1/notice/actions/sort', data).then(() => {
        this.$modal.msgSuccess("排序成功");
        this.getList();
      })
    },
    /** 修改按钮操作 */
    openDialog(row) {
      this.$refs.editDialog.open(row);
    },
    openSortDialog() {
      this.$refs.sortDialog.open(this.dataList, {label: 'content'});
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row ? row.id : this.selections.map(item => item.id).join(',');
      this.$modal.confirm('是否確認刪除選取的資料？').then(() => {
        return this.$http.delete(`/api/v1/notice/${ids}`);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("刪除成功");
      });
    },
  }
}
</script>

<style scoped>

</style>
