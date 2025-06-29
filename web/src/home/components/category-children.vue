<template>
  <div class="category-children" :class="{'large-layout-container': cardSize==='large'}">
    <div class="card-sm card-md card-xl card-xxl" v-for="card in datas" :key="card.id">
      <el-popover
        trigger="hover"
        popper-class="my-popover"
        :close-delay="0"
      >
        <el-card
          class="nav-card"
          shadow="hover"
          slot="reference"
          @click.native="cardClickHandler(card)"
        >
          <div style="display: flex">
            <ivu-avatar
              v-if="card.icon.src"
              :src="card.icon.src"
              :shape="cardIconShape"
              class="nav-card-icon"
            ></ivu-avatar>
            <ivu-avatar
              v-else
              class="nav-card-icon"
              :shape="cardIconShape"
              :style="{background: card.icon.color}"
            >
              {{ card.icon.text }}
            </ivu-avatar>
            <div class="nav-card-content">
              <div v-text="card.title" class="card-title text-ellipsis"></div>
              <div v-text="card.content" class="card-content text-ellipsis"></div>
            </div>
          </div>
        </el-card>
        <i
          v-if="card.star"
          class="el-icon-star-on card-star"
          @click="starHandler(card)"
          title="移除常用"
        ></i>
        <i
          v-else
          class="el-icon-star-off card-star"
          @click="starHandler(card)"
          title="設為常用"
        ></i>
        <div v-html="card.tip"></div>
        <div v-if="card.showQrcode">
          <img :src="showQrcodeHandler(card.url)" alt="" />
        </div>
        <div v-if="Array.isArray(card.attachments) && card.attachments.length > 0">
          <el-row
            type="flex"
            justify="space-between"
            v-for="item in card.attachments"
            :key="item.id"
          >
            <el-link
              icon="el-icon-download"
              @click.native="downloadAttachment(item.id)"
              type="warning"
            >
              {{ item.name }}
            </el-link>
            <span style="text-align: right; min-width: 50px;" v-text="item.size"></span>
          </el-row>
        </div>
      </el-popover>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "category-children",
  props: {
    datas: {
      type: Array,
    },
  },
  computed: {
    ...mapGetters([
      'cardIconShape',
      'cardSize',
    ]),
  },
  methods: {
    cardClickHandler(card) {
      if (card.url) {
        window.open(card.url, '_blank', 'noopener=yes,noreferrer=yes');
      }
    },
    showQrcodeHandler(url) {
      return `${process.env.VUE_APP_BASE_API}/api/v1/qrcode?url=${encodeURIComponent(url)}`;
    },
    downloadAttachment(id) {
      window.open(`${process.env.VUE_APP_BASE_API}/api/v1/attachment/${id}/actions/download`);
    },
    starHandler(card) {
      this.$store.dispatch('home/setStarCard', card).then(star => {
        card.star = star;
        this.$modal.msgSuccess(star ? '已加入個人常用' : '已移出個人常用');
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.category-children {
  display: flex;
  flex-wrap: wrap;
  padding-left: 20px;
  padding-bottom: 20px;

  .nav-card {
    border: none;
    box-shadow: 0 2px 2px 0 rgba(16, 116, 231, 0.14), 0 1px 5px 0 rgba(16, 116, 231, 0.12), 0 3px 1px -2px rgba(16, 116, 231, 0.2);
    cursor: pointer;

    ::v-deep .el-card__body {
      padding: 13px;
    }

    .nav-card-icon {
      vertical-align: middle;
      width: 40px;
    }

    .nav-card-content {
      width: calc(100% - 46px);
      margin-left: 6px;
    }

    .card-title {
      font-size: 14px;
      font-weight: bold;
    }

    .card-content {
      font-size: 13px;
      color: #0366d6;
      display: block;
      margin-top: 4px;
    }

    .text-ellipsis {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  .nav-card:hover {
    box-shadow: 0 8px 17px 0 rgba(3, 102, 214, 0.2), 0 6px 20px 0 rgba(3, 102, 214, 0.19);
    border-color: #eee;
  }
}

.large-layout-container {
  .card-title {
    font-size: 16px !important;
  }
  .card-content {
    font-size: 14px !important;
  }
  .nav-card-icon {
    width: 45px !important;
    height: 45px !important;
    line-height: 45px !important;
  }
}

.card-sm {
  -ms-flex: 0 0 50%;
  flex: 0 0 50%;
  max-width: 50%;
  padding-right: 20px;
  margin-top: 20px;
}

@media (min-width: 768px) {
  .card-md {
    -ms-flex: 0 0 25%;
    flex: 0 0 25%;
    max-width: 25%;
  }
  .large-layout-container .card-md {
    -ms-flex: 0 0 33.333333%;
    flex: 0 0 33.333333%;
    max-width: 33.333333%;
  }
}

@media (min-width: 1200px) {
  .card-xl {
    -ms-flex: 0 0 20%;
    flex: 0 0 20%;
    max-width: 20%;
  }
  .large-layout-container .card-xl {
    -ms-flex: 0 0 25%;
    flex: 0 0 25%;
    max-width: 25%;
  }
}

@media (min-width: 1680px) {
  .card-xxl {
    -ms-flex: 0 0 16.666%;
    flex: 0 0 16.666%;
    max-width: 16.666%;
  }
  .large-layout-container .card-xxl {
    -ms-flex: 0 0 20%;
    flex: 0 0 20%;
    max-width: 20%;
  }
}
</style>

<style lang="scss">
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background-color: #c0c0c0;
  border-radius: 3px;
}

.my-popover {
  background-color: rgba(70, 76, 91, .9);
  color: white;
  max-width: 500px;

  .popper__arrow {
    border-bottom-color: rgba(70, 76, 91, .9) !important;
  }

  .popper__arrow::after {
    border-bottom-color: rgba(70, 76, 91, .9) !important;
  }

  .card-star {
    position: absolute;
    right: 5px;
    top: 5px;
    padding: 3px;
    cursor: pointer;
  }

  .card-star.el-icon-star-on {
    color: gold;
    font-weight: bolder;
  }
}
</style>