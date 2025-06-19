<template>
  <el-card>
    <div slot="header" class="card-title">
      <span>電子信箱設定</span>
      <el-button class="btn-save" type="text" @click="saveSetting">儲存</el-button>
      <el-button class="btn-save btn-test" type="text" @click="testSetting">測試</el-button>
      <el-button class="btn-save btn-test" type="text" @click="resetForm">清空</el-button>
    </div>
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" @submit.native.prevent>
      <el-form-item label="電子信箱服務地址" prop="host">
        <el-input v-model="form.host" placeholder="請輸電子信箱服務地址" maxlength="100" v-trim/>
      </el-form-item>
      <el-form-item label="電子信箱服務協議" prop="host">
        <el-input v-model="form.protocol" placeholder="請輸入電子信箱服務地址" maxlength="100" v-trim/>
      </el-form-item>
      <el-form-item label="電子信箱服務連接埠" prop="port">
        <el-input-number v-model="form.port"
                         :min="1"
                         step-strictly
                         :max="65535"
                         placeholder="請輸入電子信箱服務連接埠" maxlength="100" v-trim/>
      </el-form-item>
      <el-form-item label="電子信箱帳號" prop="username">
        <el-input v-model="form.username" placeholder="請輸入電子信箱帳號" maxlength="100" v-trim/>
      </el-form-item>
      <el-form-item label="電子信箱密碼" prop="password">
        <el-input v-model="form.password" show-password placeholder="請輸入電子信箱密碼" maxlength="100" v-trim/>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
export default {
  name: "email-setting",
  components: {
    'com-tip': () => import('@/components/com-tip/index.vue'),
  },
  data() {
    return {
      form: {
        host: '',
        protocol: '',
        port: '',
        username: '',
        password: '',
      },
      rules: {
        host: [
          {required: true, message: "電子信箱服務地址不能為空", trigger: "blur"}
        ],
        port: [
          {required: true, message: "電子信箱服務埠不能為空", trigger: "blur"}
        ],
        username: [
          {required: true, message: "電子信箱帳號不能為空", trigger: "blur"}
        ],
        password: [
          {required: true, message: "電子信箱密碼不能為空", trigger: "blur"}
        ],
      },
    }
  },
  mounted() {
    this.loadSetting();
  },
  methods: {
    resetForm() {
      this.form = {
        host: '',
        port: 0,
        username: '',
        password: '',
        protocol:''
      };
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      })

      this.$http.patch(`/api/v1/setting/email`, {...this.form})
        .then(() => {
        });
    },
    loadSetting() {
      this.$http.get(`/api/v1/setting/email`)
        .then(res => {
          this.form = res;
        });
    },
    saveSetting() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http.patch(`/api/v1/setting/email`, {...this.form})
            .then(() => {
              this.$modal.msgSuccess('儲存成功');
            });
        }
      });
    },
    testSetting() {
      if (!this.form.id) {
        this.$modal.msgError('請先儲存配置');
        return;
      }
      this.$modal.loading('正在嘗試發送電子郵件，請耐心等待...');
      this.$http.post(`/api/v1/setting/email/test`)
        .then(() => {
          this.$modal.msgSuccess('發送成功');
        })
        .finally(() => {
          this.$modal.closeLoading();
        });
    },
  }
}
</script>

<style scoped>
.btn-test {
  margin-right: 10px;
}

.el-input-number {
  width: 100%;
}

</style>
