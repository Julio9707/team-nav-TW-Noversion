<template>
  <el-dialog title="登入" :visible.sync="show"
             width="400px"
             append-to-body
             :before-close="beforeClose"
             :close-on-click-modal="false">
    <div slot="title">
      <i class="el-icon-coffee-cup" style="color: #67C23A"></i>
      歡迎登入
    </div>
    <el-form ref="form" :model="form" :rules="formRules"
             @keyup.enter.native="loginHandler()"
             @submit.native.prevent>
      <el-row>
        <el-col :span="24">
          <el-form-item label="" prop="username">
            <el-input clearable v-model="form.username" placeholder="請輸入帳號" v-trim>
              <template slot="prepend">
                <i class="el-icon-user"></i>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="" prop="password">
            <el-input clearable
                      show-password
                      v-model="form.password"
                      v-trim
                      placeholder="請輸入密碼">
              <template slot="prepend">
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="">
            <el-checkbox v-model="rememberMe">記住密碼</el-checkbox>
            <el-checkbox v-model="autoLogin" :disabled="!rememberMe">自動登入</el-checkbox>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="">
            <el-button type="primary"
                       :loading="loading"
                       class="login-button"
                       round @click="loginHandler">登 入
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-dialog>
</template>

<script>
// 是否显示登录弹窗

export let isRelogin = {show: false};

export default {
  name: "login-dialog-index",
  data() {
    return {
      show: false,
      loading: false,
      rememberMe: false,
      autoLogin: false,
      form: {
        username: '',
        password: '',
      },
      formRules: {
        username: [
          {required: true, message: '帳號不能為空', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '密碼不能為空', trigger: 'blur'}
        ]
      }
    }
  },
  watch: {
    rememberMe(val) {
      if (!val) {
        this.autoLogin = false
      }
    }
  },
  mounted() {
    this.show = true;
    this.init();
  },
  methods: {
    init() {
      if (localStorage.rememberMe === 'true') {
        this.form.username = localStorage.username;
        this.form.password = localStorage.password;
        this.rememberMe = true;
        if (localStorage.autoLogin === 'true') {
          this.autoLogin = true;
          if (!localStorage.userLogout) {
            this.$nextTick(() => {
              this.loginHandler();
            })
          }
        }
      }
    },
    loginHandler() {
      this.loading = true;
      this.$refs.form.validate(valid => {
        if (!valid) {
          this.loading = false;
          return;
        }
        if (this.rememberMe) {
          localStorage.username = this.form.username;
          localStorage.password = this.form.password;
          localStorage.rememberMe = this.rememberMe;
          if (this.autoLogin) {
            localStorage.autoLogin = this.autoLogin
          } else {
            delete localStorage.autoLogin;
          }
        } else {
          delete localStorage.username;
          delete localStorage.password;
          delete localStorage.rememberMe;
        }
        let cardIds = null;
        if (localStorage.hasOwnProperty('starCardIds')) {
          if (localStorage.starCardIds) {
            cardIds = localStorage.starCardIds.split(',');
          } else {
            cardIds = [];
          }
        }
        this.$http.post(`/api/v1/quick/login?returnUrl=${window.location.href}`, {...this.form, cardIds: cardIds}).then(() => {
          window.location.reload();
          isRelogin.show = false;
          this.show = false;
          delete localStorage.userLogout;
        }).finally(() => {
          this.loading = false;
        })
      });
    },
    beforeClose() {
      isRelogin.show = false
      this.show = false;
    }
  }
}
</script>

<style scoped lang="scss">
.login-button {
  width: 100%;
}

.el-input {
  ::v-deep .el-input-group__prepend {
    padding: 0 10px;
  }
}
</style>
