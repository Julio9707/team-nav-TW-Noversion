<template>
  <el-dialog :title="title" :visible.sync="show"
             :close-on-click-modal="false"
             width="600px" append-to-body>
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" @submit.native.prevent>
      <el-form-item label="使用者姓名" prop="nickname">
        <el-input v-model="form.nickname" placeholder="請輸入使用者姓名" maxlength="100" v-trim @blur="nickNameChange"/>
      </el-form-item>
      <el-form-item label="登入帳號" prop="nickname">
        <el-input v-model="form.username" placeholder="請輸入登入帳號" maxlength="100" v-trim/>
      </el-form-item>
      <el-form-item label="e-mail"
                    :rules="[{required: form.randomPassword, message:'e-mail不能為空', trigger:'blue' }]"
                    prop="email">
        <el-input v-model="form.email" placeholder="請輸入e-mail" maxlength="100" v-trim/>
      </el-form-item>
      <el-form-item label="所屬角色">
        <role-select v-model="form.roleIds"></role-select>
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio
                v-for="dict in statusOptions"
                :key="dict.id"
                :label="dict.id"
              >{{ dict.name }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="!form.id">
          <el-form-item label="密碼原則" prop="randomPassword">
            <span slot="label">
              密碼原則
              <el-tooltip content='勾選隨機密碼，會產生隨機密碼傳送到郵箱，未勾選會使用系統預設配置的密碼'>
              <i class="el-icon-question"></i>
              </el-tooltip>
            </span>
            <el-checkbox v-model="form.randomPassword">隨機密碼</el-checkbox>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm">確 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: "user-edit",
  components: {
    'role-select': () => import('@/components/role-select/index.vue'),
  },
  data() {
    return {
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      show: false,
      statusOptions: [
        {
          id: '1',
          name: '有效'
        },
        {
          id: '2',
          name: '無效'
        },
      ],
      form: {
        id: '',
        nickname: '',
        username: '',
        email: '',
        randomPassword: false,
        roleIds: [],
        status: '1',
      },
      rules: {
        nickname: [
          {required: true, message: "使用者姓名不能為空", trigger: "blur"}
        ],
        username: [
          {required: true, message: "登入帳號不能為空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "狀態不能為空", trigger: "blur"}
        ],
      }
    }
  },
  methods: {
    open(item) {
      this.resetForm();
      if (item) {
        this.title = '編輯使用者';
        this.form = {...item, roleIds: Array.isArray(item.roles) ? item.roles.map(it => (it.id)) : []};
      } else {
        this.form.randomPassword = true;
        this.title = '新增使用者';
      }
      this.show = true;
    },
    resetForm() {
      this.form = {
        id: '',
        nickname: '',
        username: '',
        email: '',
        randomPassword: false,
        roleIds: [],
        status: '1',
      };
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.$http.save('/api/v1/user', {...this.form})
            .then(() => {
              this.$modal.msgSuccess('儲存成功');
              this.show = false;
              this.$emit('refresh');
            })
        }
      });
    },
    nickNameChange() {
      if (!this.form.nickname) {
        return;
      }
      this.$http.get('/api/v1/hanyu-to-pinyin', {params: {hanyu: this.form.nickname}})
        .then(res => {
          this.form.username = res;
        })
    },
    // 取消按钮
    cancel() {
      this.show = false;
    },
  }
}
</script>

<style scoped>

</style>
