/*
 * @author tuituidan
 * @date 2023/11/24
 */
export default {
  name: "card-edit-index",
  components: {
    'category-select': () => import('@/components/category-select/index.vue'),
    'card-icon-select': () => import('@/components/card-icon-select/index.vue'),
    'file-uploader': () => import('@/components/file-uploader/index.vue'),
  },
  props: {
    apply: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      // 彈出層標題
      title: "",
      // 是否顯示彈出層
      show: false,
      showFaviconLoading: false,
      // 菜單樹選項
      categoryOptions: [],
      // 表單參數
      form: {
        type: 'default',
        category: '',
        title: '',
        content: '',
        privateContent: '',
        url: '',
        showQrcode: false,
        icon: null,
        zip: null,
        attachmentIds: [],
        attachments: [],
      },
      types: [
        {
          id: 'default',
          name: '普通卡片',
          disabled: false,
        },
        {
          id: 'zip',
          name: '靜態網站',
          disabled: false,
        },
        {
          id: 'dynamic',
          name: '動態卡片',
          disabled: true,
        },
      ],
      // 表單校驗
      rules: {
        category: [
          {required: true, message: "所屬分類不能為空", trigger: "blur"}
        ],
        title: [
          {required: true, message: "標題不能為空", trigger: "blur"}
        ],
        zip: [
          {required: true, message: '請上傳網站zip文件'}
        ]
      },
      saveOption: {
        saveNotClear: false,
        saveKeepAdd: false,
      },
    }
  },
  methods: {
    open(item = {}) {
      this.resetForm();
      if (item.id) {
        this.title = this.apply ? '審核卡片' : '編輯卡片';
        this.form = {...item};
        this.form.attachmentIds = Array.isArray(this.form.attachments)
          ? this.form.attachments.map(item => item.id) : [];
      } else if (item.category) {
        this.form = {...item};
        this.title = '新增卡片';
      } else {
        this.title = '申請卡片';
      }
      this.show = true;
      this.$nextTick(() => {
        this.$refs.refCategory.init();
        this.$refs.refCardIcon.init(this.form.icon);
      })
    },
    resetForm() {
      this.form = {
        type: 'default',
        category: '',
        title: '',
        content: '',
        privateContent: '',
        url: '',
        showQrcode: false,
        icon: null,
        zip: null,
        attachmentIds: [],
        attachments: [],
      };
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      })
    },
    zipFileChange(fileList) {
      if (fileList.length <= 0) {
        this.form.zip = null;
      } else {
        this.form.zip = {...fileList[0], isNew: true};
        this.$refs.form.validateField('zip');
      }
    },
    attachmentChange(fileList) {
      this.form.attachmentIds = Array.isArray(fileList)
        ? fileList.map(item => item.path) : [];
    },
    /** 提交按鈕 */
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const newZip = this.form.type === 'zip' && this.form.zip && this.form.zip.isNew;
          if (newZip) {
            this.$modal.loading('壓縮包解壓時間較長，請耐心等待...');
          }
          if (!this.form.icon) {
            this.form.icon = {
              text: this.form.title.substr(0, 2),
              color: this.$refs.refCardIcon.getRandomColor(),
            };
          }
          if (!this.form.content) {
            this.form.content = this.form.url || this.form.title;
          }
          this.$http.save('/api/v1/card', {...this.form})
            .then(() => {
              this.$modal.msgSuccess('保存成功');
              if (this.form.id) {
                this.show = false;
              } else {
                this.show = this.saveOption.saveKeepAdd;
                if (!this.saveOption.saveNotClear) {
                  this.form.title = '';
                  this.form.content = '';
                  this.form.privateContent = '';
                  this.form.showQrcode = false;
                  this.form.zip = null;
                  this.form.attachmentIds = [];
                  this.form.attachments = [];
                }
              }
              this.$emit('refresh', this.form.category);
            })
            .finally(() => {
              if (newZip) {
                this.$modal.closeLoading();
              }
            })
        }
      });
    },
    // 取消按鈕
    cancel() {
      this.show = false;
    },
    getFavicons() {
      if (!(this.form.url && this.form.url.startsWith('http'))) {
        return;
      }
      this.showFaviconLoading = true;
      this.$http.get('/api/v1/card/icon', {params: {url: this.form.url}})
        .then(res => {
          if (!(Array.isArray(res) && res.length > 0)) {
            return;
          }
          for (const url of res) {
            this.$refs.refCardIcon.uploadSuccess(url);
          }
        })
        .finally(() => {
          this.showFaviconLoading = false;
        });
    },
  },
}
