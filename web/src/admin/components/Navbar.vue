<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
               @toggleClick="toggleSideBar"/>

    <div class="right-menu" v-if="version.currentVersion">
      <div class="right-menu-item hover-effect btn-apply" @click="$router.push({path: '/admin/card/apply'})">
        <el-badge :hidden="applyCount === 0" :value="applyCount" :max="99">
          <i class="el-icon-message-solid" title="申請列表"></i>
        </el-badge>
      </div>
      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <header-avatar></header-avatar>
          <i class="el-icon-caret-bottom"/>
        </div>
        <el-dropdown-menu slot="dropdown">
          <a href="https://github.com/Julio9707/team-nav-noversion" target="_blank">
            <el-badge is-dot :hidden="!version.hasNewVersion"
                      :title="'最新版本：'+version.remoteVersion">
              <el-dropdown-item>版本：{{ version.currentVersion }}</el-dropdown-item>
            </el-badge>
          </a>
          <a target="_blank" href="https://github.com/Julio9707/team-nav-noversion/issues">
            <el-dropdown-item>問題反饋</el-dropdown-item>
          </a>
          <el-dropdown-item divided @click.native="logout">登出</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'

export default {
  components: {
    Hamburger: () => import('@/components/Hamburger'),
    'header-avatar': () => import('@/components/header-avatar'),
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'version',
      'applyCount',
    ]),
  },
  mounted() {
    this.$store.dispatch('settings/loadVersion');
    this.$store.dispatch('user/refreshApplyCount');
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      this.$confirm('確定要登出嗎？', '提示', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('user/LogOut').then(() => {
          this.$store.dispatch('settings/loadVersion');
        })
      }).catch(() => {
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, .08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .btn-apply {
      margin-right: 10px;
      padding: 0 12px !important;

      ::v-deep .el-badge__content {
        top: 7px;
      }

    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
