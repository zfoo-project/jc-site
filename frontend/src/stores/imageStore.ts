import {defineStore} from "pinia";


export const useImageStore = defineStore("imageStore", {
  state: () => ({
    midPrompts: [],
    downloads: []
  }),

  persist: {
    enabled: true,
    strategies: [{storage: localStorage, paths: ["midPrompts", "downloads"]}],
  },

  getters: {},

  actions: {}
});
