<template>
  <div class="app-container">
    <el-row type="flex" justify="space-between">
      <el-form ref="queryForm" :model="queryParam"
               @submit.native.prevent
               :inline="true" label-width="50px">
        <el-form-item>
          <el-input placeholder="請輸入分類名稱"
                    style="width: 340px"
                    v-model="queryParam.keywords"
                    v-trim
                    clearable
                    @clear="getList"
                    @keyup.enter.native="getList">
            <el-button slot="append" icon="el-icon-search" @click="getList"></el-button>
          </el-input>
        </el-form-item>
      </el-form>
      <el-row :gutter="10" class="mb8 mt5">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            size="small"
            icon="el-icon-circle-plus-outline"
            v-btn-multiple="selections"
            @click="handleRecover()"
          >還原
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
      @selection-change="selections = $refs.dataTable.selection"
      :data="table.dataList">
      <el-table-column label="序號" type="index" width="55" align="center" :index="table.index"/>
      <el-table-column type="selection" align="center" width="55"></el-table-column>
      <el-table-column label="分類" prop="name" show-overflow-tooltip/>
      <el-table-column prop="icon" label="圖示" align="center" width="100">
        <template slot-scope="scope">
          <svg-icon v-if="scope.row.icon" :icon-class="scope.row.icon"/>
        </template>
      </el-table-column>
      <el-table-column label="卡片數" align="center" prop="cardCount" show-overflow-tooltip/>
      <el-table-column label="可查看角色（不配置則任何人都可查看）" align="center" prop="roles">
        <template slot-scope="scope">
          <span>{{ scope.row.roles && scope.row.roles.map(item => (item.roleName)).join(',') }}</span>
        </template>
        <template slot="header">
          <span>
             <el-tooltip content='不設定則為公開分類，不登入也可以看到，設定後需要登入且擁有對應角色才能看到' placement="top">
                <i class="el-icon-question"></i>
             </el-tooltip>
             所屬角色
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-circle-plus-outline"
            @click="handleRecover(scope.row)"
          >還原
          </el-button>
          <el-button
            slot="reference"
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
  </div>
</template>

<script>
export default {
  name: "category-invalid",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      selections: [],
      // 查询参数
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
      this.$http.get(`/api/v1/category/page`, {params: this.queryParam})
        .then(res => {
          this.table.dataList = res.content;
          this.table.total = res.totalElements;
          this.table.index = res.pageable.offset + 1;
          this.loading = false;
        });
    },
    handleRecover(row) {
      const ids = row ? [row.id] : this.selections.map(item => item.id);
      this.$http.patch(`/api/v1/category/valid/true`, ids)
        .then(() => {
          this.$modal.msgSuccess('已還原');
          this.queryParam.pageIndex = 1;
          this.getList();
        });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row ? row.id : this.selections.map(item => item.id).join(',');
      this.$modal.confirm('將會永久刪除，是否確認刪除選取的資料？').then(() => {
        return this.$http.delete(`/api/v1/category/${ids}`);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("刪除成功");
      });
    },
  }
}
</script>

<style scoped lang="scss">

</style>
