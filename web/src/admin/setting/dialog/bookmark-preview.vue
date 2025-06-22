<template>
  <el-dialog title="瀏覽器書籤導入預覽" :visible.sync="show"
             :close-on-click-modal="false"
             width="800px" append-to-body>
    <div>
      <el-alert
        title="連結必須掛在最末端葉子節點（連結和目錄不能同級），目錄最多不能超過三層，請透過拖曳及刪除進行調整"
        type="warning"
        :closable="false"
        show-icon>
      </el-alert>
      <el-tree ref="dataTree"
               :props="props"
               :data="datas"
               highlight-current
               draggable
               node-key="id">
        <div slot-scope="{ node, data }" style="display: flex;width: 100%;justify-content: space-between">
          <el-link v-if="data.icon"
                   :underline="false"
                   @click="clickNodeHandler(data)">
            <img class="custom-icon" :src="data.icon" alt=""/>
            <span class="custom-node" :title="data.name">{{ data.name }}</span>
          </el-link>
          <el-link v-else :icon="data.type==='folder' ? 'el-icon-folder' : 'el-icon-document'"
                   :underline="false"
                   @click="clickNodeHandler(data)">
            <span class="custom-node" :title="data.name">{{ data.name }}</span>
          </el-link>
          <div>
            <span v-text="data.createTime" style="padding-right: 10px"></span>
            <el-button type="text"
                       size="small"
                       @click="() => removeNode(node, data)">刪除
            </el-button>
          </div>
        </div>
      </el-tree>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm">確 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: "bookmark-preview",
  data() {
    return {
      // 是否显示弹出层
      show: false,
      datas: [],
      props: {
        label: 'name'
      },
    }
  },
  methods: {
    open(data) {
      this.datas = data;
      this.show = true;
    },
    clickNodeHandler(data) {
      if (data.url) {
        window.open(data.url)
      }
    },
    removeNode(node, data){
      const parent = node.parent;
      const children = parent.data.children || parent.data;
      const index = children.findIndex(d => d.id === data.id);
      children.splice(index, 1);
    },
    submitForm() {
      this.$http.post('/api/v1/category/bookmark/actions/import', this.datas)
      .then(()=>{
        this.$modal.msgSuccess('儲存成功');
        this.show = false;
      })
    },
    // 取消按钮
    cancel() {
      this.show = false;
    },
  }
}
</script>

<style scoped lang="scss">
.el-alert {
  margin-top: -10px;
  margin-bottom: 20px;
}
.el-tree {
  padding-right: 8px;
  height: 500px;
  overflow: auto
}

.custom-icon {
  vertical-align: middle;
}

.custom-node {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 400px;
  display: inline-block;
  vertical-align: middle;
  margin-left: 6px;
}

::v-deep .el-icon-folder {
  color: #e6a23c;
}

</style>
