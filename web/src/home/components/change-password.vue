<template>
  <el-dialog title="修改密碼" :visible.sync="show" width="500px"
             :close-on-click-modal="false"
             append-to-body>
    <el-form ref="form" :model="form" :rules="rules" label-width="80px" @submit.native.prevent>
      <el-form-item label="舊密碼" prop="oldPassword">
        <el-input v-model="form.oldPassword" placeholder="請輸入舊密碼" type="password" show-password v-trim/>
      </el-form-item>
      <el-form-item label="新密碼" prop="newPassword">
        <el-input v-model="form.newPassword" placeholder="請輸入新密碼" type="password" show-password v-trim/>
      </el-form-item>
      <el-form-item label="確認密碼" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" placeholder="請輸入新密碼" type="password" show-password v-trim/>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm">儲 存</el-button>
      <el-button @click="cancel">關 閉</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: "change-password",
  data() {
    return {
      // 是否显示弹出层
      show: false,
      // 表单参数
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      // 表单校验
      // 表单校验
      rules: {
        oldPassword: [
          {required: true, message: "舊密碼不能為空", trigger: "blur"}
        ],
        newPassword: [
          {required: true, message: "新密碼不能為空", trigger: "blur"},
          {min: 6, max: 20, message: "長度在 6 到 20 個字符", trigger: "blur"}
        ],
        confirmPassword: [
          {required: true, message: "確認密碼不能為空", trigger: "blur"},
          {
            required: true, validator: (rule, value, callback) => {
              if (this.form.newPassword !== value) {
                callback(new Error("兩次輸入的密碼不一致"));
              } else {
                callback();
              }
            }, trigger: "blur"
          }
        ]
      }
    }
  },
  methods: {
    open() {
      this.show = true;
      this.resetForm();
    },
    // 取消按钮
    cancel() {
      this.show = false;
      this.resetForm();
    },
    resetForm(){
      this.form = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      };
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.$http.patch(`/api/v1/user/password`, this.form)
            .then(() => {
              this.$modal.msgSuccess('密碼修改成功');
              this.show = false;
            })
        }
      });
    },
  }
}
</script>

<style scoped>

</style>
