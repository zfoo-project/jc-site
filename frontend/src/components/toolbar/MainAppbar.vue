<!--
* @Component:
* @Maintainer: J.K. Yang
* @Description:
-->
<script setup lang="ts">
import { useDisplay } from "vuetify";
import { useCustomizeThemeStore } from "@/stores/customizeTheme";
import ToolbarLanguage from "@/components/toolbar/ToolbarLanguage.vue";
import ToolbarNewsLevel from "@/views/jiucai/common/ToolbarNewsLevel.vue";
import ToolbarUserJiucai from "@/views/jiucai/common/ToolbarUserJiucai.vue";
import ToolbarNewsNotifications from "@/views/jiucai/common/ToolbarNewsNotifications.vue";
import ToolbarAccountLogin from "@/views/jiucai/common/ToolbarAccountLogin.vue";
import ToolbarNotifications from "./ToolbarNotifications.vue";
import ToolbarUser from "./ToolbarUser.vue";
import { useTodoStore } from "@/views/app/todo/todoStore";
const { mdAndUp } = useDisplay();
const todoStore = useTodoStore();
const customizeTheme = useCustomizeThemeStore();
const showMobileSearch = ref(false);

import _ from "lodash";
import {send} from "@/utils/websocket";
import NewsSearchRequest from "@/protocol/news/NewsSearchRequest";
import {useSnackbarStore} from "@/stores/snackbarStore";
const snackbarStore = useSnackbarStore();
const searchRef = ref<string>('');
function search() {
  if (_.isEmpty(searchRef.value)) {
    snackbarStore.showErrorMessage("搜索内容不能为空");
    return;
  }
  const request = new NewsSearchRequest();
  request.query = searchRef.value;
  send(request);
}

</script>

<template>
  <!-- ---------------------------------------------- -->
  <!--App Bar -->
  <!-- ---------------------------------------------- -->
  <v-app-bar :density="mdAndUp ? 'default' : 'compact'">
    <!-- ---------------------------------------------- -->
    <!-- search input mobil -->
    <!-- ---------------------------------------------- -->
    <div class="d-flex flex-fill align-center" v-if="showMobileSearch">
      <v-text-field
        color="primary"
        variant="solo"
        prepend-inner-icon="mdi-magnify"
        append-inner-icon="mdi-close"
        @click:append-inner="showMobileSearch = false"
        hide-details
        placeholder="Search"
      ></v-text-field>
    </div>
    <div v-else class="px-2 d-flex align-center justify-space-between w-100">
      <!-- ---------------------------------------------- -->
      <!-- NavIcon -->
      <!-- ---------------------------------------------- -->
      <v-app-bar-nav-icon
        @click="customizeTheme.mainSidebar = !customizeTheme.mainSidebar"
      ></v-app-bar-nav-icon>
      <div>
        <v-text-field
          v-if="mdAndUp"
          v-model="searchRef"
          class="ml-2"
          style="width: 500px"
          color="primary"
          variant="solo"
          density="compact"
          append-inner-icon="mdi-magnify"
          hide-details
          placeholder="Search"
          @click:append-inner="search()"
          @keyup.enter="search()"
        ></v-text-field>
      </div>

      <v-spacer></v-spacer>

      <div class="d-flex">
        <ToolbarAccountLogin />
        <ToolbarNewsNotifications />
        <ToolbarNewsLevel />
        <ToolbarLanguage />
        <ToolbarUserJiucai />
      </div>
    </div>
  </v-app-bar>
</template>

<style scoped lang="scss"></style>
