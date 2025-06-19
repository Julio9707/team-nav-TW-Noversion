<template>
  <el-dialog :title="title" :visible.sync="show"
             width="630px"
             :close-on-click-modal="false"
             append-to-body>
    <el-form ref="form" :model="form" :rules="rules" label-width="110px" @submit.native.prevent>
      <el-form-item label="卡片類型" prop="type">
         <span slot="label">
          <el-tooltip placement="top">
            <div slot="content">
              普通卡片：內容型卡片，通過添加連結跳轉對應網址<br/>
              靜態網站：上傳靜態網站的zip壓縮包，自動生成可訪問連結<br/>
              動態卡片：添加http接口或sql查詢獲取動態的卡片內容（暫不可用，開發中...）
            </div>
            <i class="el-icon-question"></i>
          </el-tooltip>
          卡片類型
        </span>
        <el-radio-group v-model="form.type">
          <el-radio
            v-for="item in types"
            :key="item.id"
            :label="item.id"
            :disabled="Boolean(form.id) || item.disabled"
          >{{ item.name }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="所屬分類" prop="category">
        <category-select ref="refCategory"
                         style="width: 100%"
                         v-model="form.category"
                         placeholder="請選擇所屬分類"></category-select>
      </el-form-item>
      <el-form-item label="標題" prop="title">
        <el-input v-model="form.title" placeholder="標題" clearable v-trim />
      </el-form-item>
      <el-form-item label="內容" prop="content">
        <el-input v-model="form.content" type="textarea" v-trim
                  maxlength="200"
                  show-word-limit
                  clearable
                  :rows="3"
                  placeholder="請輸入內容"/>
      </el-form-item>
      <el-form-item label="私密內容" prop="privateContent">
        <span slot="label">
          <el-tooltip content='登錄後才能看到的信息' placement="top">
          <i class="el-icon-question"></i>
          </el-tooltip>
          私密信息
        </span>
        <el-input v-model="form.privateContent" type="textarea" v-trim
                  maxlength="200"
                  show-word-limit
                  clearable
                  :rows="2"
                  placeholder="請輸入內容"/>
      </el-form-item>
      <el-form-item label="連結"
                    v-if="form.type==='default'"
                    :rules="[{required: form.showQrcode, message:'連結不能為空', trigger:'blue' }]"
                    prop="url">
        <el-row>
          <el-col :span="18">
            <el-input v-model="form.url" placeholder="連結" @blur="getFavicons" v-trim/>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-checkbox v-model="form.showQrcode">顯示二維碼</el-checkbox>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="網站文件" prop="zip" v-else>
        <file-uploader type="modules"
                       accept="application/zip"
                       :file-list="form.zip?[form.zip]:[]"
                       @file-change="zipFileChange"
                       :limit="1">zip壓縮包上傳
        </file-uploader>
      </el-form-item>
      <el-form-item class="form-icon-loading" v-if="showFaviconLoading">
        <el-button loading type="text">正在嘗試獲取該地址的圖示</el-button>
      </el-form-item>
      <el-form-item label="圖示" prop="icon">
        <card-icon-select ref="refCardIcon" v-model="form.icon" />
      </el-form-item>
      <el-form-item label="附件" prop="attachment">
        <file-uploader type="attachments"
                       :file-list="form.attachments?form.attachments:[]"
                       multiple
                       @file-change="attachmentChange"
                       :limit="10">附件上傳
        </file-uploader>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-checkbox v-if="!Boolean(form.id)" v-model="saveOption.saveKeepAdd">保存後不關閉</el-checkbox>
      <el-checkbox v-if="!Boolean(form.id)" v-model="saveOption.saveNotClear" style="margin-right: 25px">保存後不清空</el-checkbox>
      <el-button v-if="apply" type="success" @click="submitForm">通 過</el-button>
      <el-button v-else type="primary" @click="submitForm">保 存</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script src="./index.js"></script>

<style scoped lang="scss">
.form-icon-loading {
  margin-top: -16px;
  margin-bottom: 0;
}
</style>
