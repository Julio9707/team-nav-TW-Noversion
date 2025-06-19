<template>
  <el-card>
    <div slot="header" class="card-title">
      <span>基本設定</span>
      <el-button class="btn-save" type="text" @click="saveSetting">儲存</el-button>
    </div>
    <el-form ref="settingForm"
             :model="settingItem"
             label-width="120px"
             :rules="settingItemRules" @submit.native.prevent>
      <el-form-item label="網站標題" prop="navName">
        <el-input v-model="settingItem.navName" v-trim
                  clearable
                  maxlength="50"
                  placeholder="網站標題"></el-input>
      </el-form-item>
      <el-form-item label="網站LOGO" prop="logoPath">
        <div class="multi-form-item">
          <com-icon-uploader v-model="settingItem.logoPath" @remove="onRemoveLogo"></com-icon-uploader>
          <div>
            <el-checkbox v-model="settingItem.logoToFavicon">同步修改favicon</el-checkbox>
            <com-tip tip="勾選時網站的favicon也將使用此logo圖片"></com-tip>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="公告切換間隔" prop="countdown">
        <div class="multi-form-item">
          <el-input-number :max="200"
                           :min="10"
                           v-model="settingItem.cutOverSpeed"
                           class="multi-form-item-input"></el-input-number>
          <div style="margin-left: 5px">秒</div>
          <com-tip tip="設定首頁多個通知公告的自動切換間隔時間"></com-tip>
        </div>
      </el-form-item>
      <el-form-item label="Nginx訪問網站" prop="nginxOpen">
        <div class="multi-form-item">
          <div>
            <el-switch v-model="settingItem.nginxOpen">
            </el-switch>
          </div>
          <el-input type="text"
                    maxlength="100"
                    clearable
                    class="multi-form-item-input"
                    v-model="settingItem.nginxUrl"
                    v-trim
                    placeholder="Nginx地址"></el-input>
          <com-tip tip="可以設定透過Nginx來存取上傳的靜態網站，Nginx設定可參考README.md，開啟後造訪網站時將跳轉Nginx位址"></com-tip>
        </div>
      </el-form-item>
      <el-form-item label="顯示幫助文件" prop="showDoc">
        <div class="multi-form-item">
          <div>
            <el-switch v-model="settingItem.showDoc">
            </el-switch>
          </div>
          <com-tip tip="首頁頭部顯示幫助文件"></com-tip>
        </div>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
export default {
  name: "base-setting",
  components: {
    'com-icon-uploader': () => import('@/components/com-icon-uploader/index.vue'),
    'com-tip': () => import('@/components/com-tip/index.vue'),
  },
  data() {
    return {
      settingItem: {
        navName: '',
        logoPath: '',
        logoToFavicon: false,
        cutOverSpeed: 10,
        nginxOpen: false,
        nginxUrl: '',
        showDoc: false,
      },
      settingItemRules: {
        navName: [
          {required: true, message: '網站標題不能為空'},
        ]
      }
    }
  },
  mounted() {
    this.loadSetting();
  },
  methods: {
    loadSetting() {
      this.$http.get(`/api/v1/setting`)
        .then(res => {
          this.settingItem = res;
          this.settingItem.cutOverSpeed = res.cutOverSpeed || 10;
          this.settingItem.logoToFavicon = Boolean(res.logoToFavicon);
          this.settingItem.nginxOpen = Boolean(res.nginxOpen);
          this.settingItem.showDoc = Boolean(res.showDoc);
        });
    },
    saveSetting() {
      this.$refs.settingForm.validate(valid => {
        if (valid) {
          this.$http.patch(`/api/v1/setting`, {...this.settingItem})
            .then(() => {
              this.$modal.msgSuccess('儲存成功');
              setTimeout(() => {
                location.reload();
              }, 1000);
            });
        }
      });
    },
    onRemoveLogo(url) {
      // 只有一个，不用判断url，直接移除
      this.settingItem.logoPath = '';
    },
  }
}
</script>

<style scoped>
.multi-form-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.multi-form-item .multi-form-item-input {
  margin-left: 15px;
  flex: auto;
}
</style>
