<template>
  <el-dialog :title="title" :visible.sync="show" width="600px" append-to-body :close-on-click-modal="false">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px" @submit.native.prevent>
      <el-row>
        <el-col :span="24">
          <el-form-item label="上層分類" prop="pid">
            <category-select ref="refCategory"
                             style="width: 100%"
                             v-model="form.pid"
                             check-strictly
                             placeholder="請選擇上層分類"></category-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="分類圖示" prop="icon">
            <category-icon-select v-model="form.icon"></category-icon-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="分類名稱" prop="name">
            <el-input clearable v-model="form.name" placeholder="請選擇分類名稱" v-trim/>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item prop="roleIds">
              <span slot="label">
                <el-tooltip content='不設定則為公開分類，不登入也可以看到，設定後需要登入且擁有對應角色才能看到' placement="top">
                <i class="el-icon-question"></i>
                </el-tooltip>
                所屬角色
              </span>
            <role-select v-model="form.roleIds"></role-select>
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

<script src="./index.js"></script>

<style scoped>

</style>
