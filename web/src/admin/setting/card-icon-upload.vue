<template>
  <el-card>
    <div slot="header" class="card-title">
      <span>卡片圖示上傳
        <com-tip tip="設定卡片圖示選擇預設圖示，檔案名稱用於搜索，必須保持唯一"></com-tip>
        <el-tooltip content="圖片裁切">
          <i class="el-icon-picture-outline card-icon-add" @click="openIconCropper()"></i>
        </el-tooltip>
      </span>
    </div>
    <div style="max-height: 325px;overflow: auto">
      <com-icon-uploader :action="uploadUrl"
                         show-name
                         show-edit
                         v-model="iconPaths"
                         @remove="onRemoveCardIcon"
                         @edit-name="onEditIconName"
                         :max-count="500"></com-icon-uploader>
    </div>
    <card-icon-cropper ref="refIconCropper" @uploaded="init"></card-icon-cropper>
  </el-card>
</template>

<script>
export default {
  name: "card-icon-upload",
  components: {
    'com-icon-uploader': () => import('@/components/com-icon-uploader/index.vue'),
    'com-tip': () => import('@/components/com-tip/index.vue'),
    'card-icon-cropper': () => import('@/components/card-icon-cropper/index.vue'),
  },
  data() {
    return {
      uploadUrl: process.env.VUE_APP_BASE_API + '/api/v1/upload/default',
      iconPaths: []
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.$http.get('/api/v1/card/icons')
        .then(res => {
          this.iconPaths = res.map(filename => (`/ext-resources/images/default/${filename}`));
        });
    },
    onRemoveCardIcon(url) {
      this.$http.delete(`/api/v1/icon/${url.substring(url.lastIndexOf('/') + 1)}`)
        .then(() => {
          this.$modal.msgSuccess('刪除成功');
        });
    },
    onEditIconName(url, newName) {
      this.$http.patch(`/api/v1/icon/${url.substring(url.lastIndexOf('/') + 1)}/to/${newName}`)
        .then(() => {
          this.$modal.msgSuccess('修改成功');
          this.init();
        });
    },
    openIconCropper() {
      this.$refs.refIconCropper.open();
    },
  }
}
</script>

<style scoped>
.card-icon-add {
  color: green;
  cursor: pointer;
  margin-left: 15px;
  vertical-align: -0.1em;
  font-size: 20px;
}
</style>
