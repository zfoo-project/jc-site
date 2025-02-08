
import {defineStore} from "pinia";


export const useImageSdReStore = defineStore("imageSdReStore", {
  state: () => ({
    sdPrompts: [],
    sdParameters: {
      prompt: ""
    },
    sds: []
  }),

  persist: {
    enabled: true,
    strategies: [{storage: localStorage, paths: ["sdPrompts", "sdParameters", "sds"]}],
  },

  getters: {},

  actions: {}
});
