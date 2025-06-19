<template>
  <div class="app-container">
    <el-row type="flex" justify="space-between">
      <el-form :model="queryParam" ref="queryForm"
               @submit.native.prevent>
        <el-form-item>
          <el-input
            v-model="queryParam.keywords"
            v-trim
            placeholder="請輸入登入帳號或使用者姓名進行搜尋"
            clearable
            style="width: 300px"
            @keyup.enter.native="getList"
            suffix-icon="el-icon-search"
          />
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8 mt5">
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            size="small"
            icon="el-icon-plus"
            @click="openEditDialog()"
          >新稱
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            size="small"
            icon="el-icon-edit"
            v-btn-single="selections"
            @click="openEditDialog(selections[0])"
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
            @click="handleDelete"
          >刪除
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            size="small"
            icon="el-icon-key"
            v-btn-multiple="selections"
            @click="handleResetPwd"
          >重置密碼
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
      @selection-change="selections = $refs.dataTable.selection">
      <el-table-column label="序號" type="index" width="55" align="center" :index="table.index"/>
      <el-table-column type="selection" width="50" align="center"
                       :selectable="tableSelectable"/>
      <el-table-column label="登入帳號" align="center" prop="username"
                       :show-overflow-tooltip="true"/>
      <el-table-column label="使用者姓名" align="center" prop="nickname"
                       :show-overflow-tooltip="true"/>
      <el-table-column label="e-mail" align="center" prop="email"
                       :show-overflow-tooltip="true"/>
      <el-table-column label="角色" align="center" prop="roles"
                       :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.roles && scope.row.roles.map(item => (item.roleName)).join(',') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="狀態" align="center" prop="status">
        <template slot-scope="scope">
          <el-switch
            @change="statusChange(scope.row, $event)"
            v-if="scope.row.username!=='admin'"
            v-model="scope.row.status"
            active-value="1"
            inactive-value="2"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作"
                       align="center"
                       width="140"
                       class-name="small-padding fixed-width">
        <template slot-scope="scope" v-if="scope.row.username!=='admin'">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="openEditDialog(scope.row)"
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

    <pagination
      v-show="table.total>0"
      :total="table.total"
      :page.sync="queryParam.pageIndex"
      :limit.sync="queryParam.pageSize"
      @pagination="getList"
    />
    <user-edit ref="refUser" @refresh="getList"></user-edit>
  </div>
</template>

<script>
export default {
  name: "user-role",
  components: {
    'user-edit': () => import('./user-edit')
  },
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
      this.$http.get(`/api/v1/user/page`, {params: this.queryParam})
        .then(res => {
          this.table.dataList = res.content;
          this.table.total = res.totalElements;
          this.table.index = res.pageable.offset + 1;
          this.loading = false;
        });
    },
    /** 修改按钮操作 */
    openEditDialog(row) {
      this.$refs.refUser.open(row);
    },
    tableSelectable(row) {
      return row.username !== 'admin'
    },
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      const userIds = row.id || this.selections.map(item => item.id).join(',');
      this.$modal.confirm('是否確認重設選取使用者的密碼？').then(() => {
        return this.$http.patch(`/api/v1/user/${userIds}/actions/reset-password`);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("密碼重置成功");
      });
    },
    statusChange({id}, status){
      this.$http.patch(`/api/v1/user/${id}/status/${status}`).then(()=>{
        this.$modal.msgSuccess("狀態修改成功");
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const userIds = row.id || this.selections.map(item => item.id).join(',');
      this.$modal.confirm('是否確認刪除選取資料項？').then(() => {
        return this.$http.delete(`/api/v1/user/${userIds}`);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("刪除成功");
      });
    },
  }
};
</script>
