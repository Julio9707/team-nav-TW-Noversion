export default {
  name: 'category-edit-index',
  components: {
    'category-select': () => import('@/components/category-select/index.vue'),
    'role-select': () => import('@/components/role-select/index.vue'),
    'category-icon-select': () => import('@/components/category-icon-select/index.vue'),
  },
  data() {
    return {
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      show: false,
      // 表单参数
      form: {
        id: null,
        pid: null,
        name: '',
        icon: 'dashboard',
        roleIds: [],
      },
      // 表单校验
      rules: {
        name: [
          {required: true, message: "分類名稱不能為空", trigger: "blur"}
        ],
      }
    }
  },
  methods: {
    open(item) {
      this.resetForm();
      if (item) {
        this.title = '編輯分類';
        this.form = {...item, roleIds: Array.isArray(item.roles) ? item.roles.map(it => (it.id)) : []};
        if(!this.form.icon){
          this.form.icon = 'dashboard';
        }
      } else {
        this.title = '新增分類';
      }
      this.show = true;
      this.$nextTick(() => {
        this.$refs.refCategory.init(2);
      })
    },
    // 表单重置
    resetForm() {
      this.form = {
        id: null,
        pid: null,
        name: '',
        icon: 'dashboard',
        roleIds: [],
      };
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      })
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (!this.form.pid) {
            this.form.pid = '0';
          }
          this.$http.save('/api/v1/category', this.form)
            .then(() => {
              this.$modal.msgSuccess("儲存成功");
              this.show = false;
              this.$emit('refresh');
            });
        }
      });
    },
    // 取消按钮
    cancel() {
      this.show = false;
      this.resetForm();
    },
  }
}
