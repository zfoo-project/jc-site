import {defineStore} from "pinia";


export const useImageSdStore = defineStore("imageSdStore", {
  state: () => ({
    sdPrompts: [],
    animations: [],
    sdParameters: {
      prompt: "",
      negativePrompt: "",
      style: 0,
      step: 40,
      batchSize: 6,
      dimension: 1
    },
    sds: []
  }),

  persist: {
    enabled: true,
    strategies: [{storage: localStorage, paths: ["sdPrompts", "sdParameters", "sds", "animations"]}],
  },

  getters: {},

  actions: {}
});
