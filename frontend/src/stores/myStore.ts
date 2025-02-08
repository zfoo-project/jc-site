import {defineStore} from "pinia";
import User from "@/protocol/user/User";
import _ from "lodash";

export const useMyStore = defineStore("myStore", {
  state: () => ({
    rewardTipDialog: false,
    lastForceShow: 0,
    announce: {
      version: "",
      name: "",
      board: ""
    },

    loginDialog: false,
    profileDialog: false,
    adminDialog: false,
    token: "",
    user: {
      id: 0,
      name: "",
      ctime: 0,
      phoneNumber: "",
      ask: 0,
      draw: 0,
      login: 0,
      cost: 0
    },
    propmpt: "",
    baidu: true,
    xunfei: true,
    tencent: true,
    alibaba: true,
    llama: true,
    deepseek: true,
    google: true,
    googleSearch: false,
    bingSearch: true,
    weixinSearch: false,
    bilibiliSearch: true
  }),

  persist: {
    enabled: true,
    strategies: [{storage: localStorage, paths: ["announce", "token", "user", "lastForceShow", "propmpt", "baidu", "xunfei"
        , "llama", "tencent", "deepseek", "alibaba", "google", "googleSearch", "bingSearch", "weixinSearch", "bilibiliSearch"]}],
  },

  getters: {},

  actions: {

    updateUser(user: User | null) {
      if (user == null) {
        return;
      }
      this.user.id = user.id;
      this.user.name = user.name;
      this.user.ctime = user.ctime;
      this.user.phoneNumber = user.phoneNumber == 0 ? "" : _.toString(user.phoneNumber);
      this.user.ask = user.ask;
      this.user.draw = user.draw;
      this.user.login = user.login;
      this.user.cost = user.cost;
    }

  }
});
