<template>
  <el-dialog :title="title" :visible.sync="show" width="700px"
             :close-on-click-modal="false"
             append-to-body>
    <el-form ref="form" :model="form" :rules="rules" submit.native.prevent label-width="80px">
      <el-form-item label="公告內容" prop="content">
        <quill-editor v-model="form.content" @on-change="editorOnchange" placeholder="請輸入公告內容"/>
      </el-form-item>
      <el-form-item label="結束時間" prop="endTime">
        <div style="display: flex;align-items: center">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            clearable
            style="flex: auto"
            value-format="yyyy-MM-dd HH:mm"
            format="yyyy-MM-dd HH:mm"
            placeholder="請選擇結束時間">
          </el-date-picker>
          <com-tip tip="設定後首頁顯示的公告內容將出現倒數計時"></com-tip>
        </div>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm">確 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: "index",
  components: {
    'quill-editor': () => import('@/components/Editor/index.vue'),
    'com-tip': () => import('@/components/com-tip/index.vue'),
  },
  data() {
    return {
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      show: false,
      contentText: '',
      // 表单参数
      form: {
        endTime: '',
        content: '',
      },
      // 表单校验
      rules: {
        content: [
          {required: true, message: "公告內容不能為空", trigger: "blur"},
          {
            validator: (rule, value, callback) => {
              if (!this.contentText) {
                callback(new Error('公告內容不能為空'));
                return;
              }
              callback();
            },
            trigger: 'blur'
          }
        ],
      }
    }
  },
  methods: {
    open(item){
      this.reset();
      if(item){
        this.title = '編輯';
        this.form = {...item};
      }else {
        this.form.id = '';
        this.title = '新增'
      }
      this.show = true;
    },
    editorOnchange(text){
      this.contentText = text;
    },
    // 取消按钮
    cancel() {
      this.show = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: '',
        content: '',
        endTime: '',
      };
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.$http.save('/api/v1/notice', {...this.form})
            .then(() => {
              this.$modal.msgSuccess('儲存成功');
              this.show = false;
              this.$emit('refresh');
            })
        }
      });
    },
  }
}
</script>

<style scoped>

</style>
