<template>
  <el-card>
    <div slot="header" class="card-title">
      <span>數據導入導出</span>
    </div>
    <div>
      <el-divider content-position="left">資料備份/還原</el-divider>
      <div class="backup-panel">
        <el-button type="primary"
                   class="backup-item"
                   icon="el-icon-download"
                   :loading="backupLoading"
                   size="small"
                   @click="backupHandler">資料備份
        </el-button>
        <file-uploader type="revert"
                       class="backup-item"
                       accept="application/zip"
                       :file-list="[]"
                       @file-change="revertUploadSuccess"
                       :limit="1">還原資料備份
        </file-uploader>
      </div>
      <el-divider content-position="left">瀏覽器書籤導入</el-divider>
      <div class="backup-panel">
        <file-uploader type="bookmark"
                       accept="text/html"
                       :file-list="[]"
                       @file-change="bookmarkUploadSuccess"
                       :limit="1">上傳瀏覽器書籤
        </file-uploader>
      </div>
      <bookmark-preview ref="refBookmark"></bookmark-preview>
    </div>
  </el-card>
</template>

<script>
export default {
  name: "data-backup",
  components: {
    'com-tip': () => import('@/components/com-tip/index.vue'),
    'file-uploader': () => import('@/components/file-uploader/index.vue'),
    'bookmark-preview': () => import('@/admin/setting/dialog/bookmark-preview.vue'),
  },
  data() {
    return {
      backupLoading: false,
      revertLoading: false,
      revertUrl: `${process.env.VUE_APP_BASE_API}/api/v1/data/revert`,
    }
  },
  methods: {
    backupHandler() {
      this.backupLoading = true
      this.$download.download(`/api/v1/data/backup`)
        .then(() => {
          this.$modal.msgSuccess('備份成功');
        })
        .finally(() => {
          this.backupLoading = false;
        });
    },
    revertUploadSuccess() {
      this.$modal.msgSuccess('還原成功');
      setTimeout(() => {
        location.reload();
      }, 1000);
    },
    bookmarkUploadSuccess(list){
      this.$refs.refBookmark.open(list[0].path);
    }
  }
}
</script>

<style scoped lang="scss">
.backup-panel {
  margin-bottom: 80px;
  overflow: hidden;

  .backup-item {
    float: right;
  }

  .backup-item.el-button {
    float: left;
  }
}
</style>
