<script setup lang="ts">
import { useRouter } from "vue-router";
import StatusMenuJiucai from "./StatusMenuJiucai.vue";
import {useNewsStore} from "@/stores/newsStore";
import {useMyStore} from "@/stores/myStore";


const myStore = useMyStore();
const newsStore = useNewsStore();
const router = useRouter();


function adminManager() {
  const name = myStore.user.name;
  if (name == "127.0.0.1" || name == "o9Sc5xEtAB0Mcjgd3O-Dfg1tCRJc") {
    myStore.adminDialog = true;
  }
}

</script>

<template>
  <v-menu
    :close-on-content-click="false"
    location="bottom right"
    transition="slide-y-transition"
  >
    <!-- ---------------------------------------------- -->
    <!-- Activator Btn -->
    <!-- ---------------------------------------------- -->
    <template v-slot:activator="{ props }">
      <v-btn class="mx-2" icon v-bind="props">
        <v-badge content="2" :color="newsStore.online ? 'success' : 'error'" dot bordered>
          <v-avatar size="40">
            <v-img
              :src="newsStore.myAvatar()"
            ></v-img>
          </v-avatar>
        </v-badge>
      </v-btn>
    </template>
    <v-card max-width="300">
      <v-list lines="three" density="compact">
        <!-- ---------------------------------------------- -->
        <!-- Profile Area -->
        <!-- ---------------------------------------------- -->
        <v-list-item @click="adminManager">
          <template v-slot:prepend>
            <v-avatar size="40">
              <v-img
                :src="newsStore.myAvatar()"
              ></v-img>
            </v-avatar>
          </template>

          <v-list-item-title class="font-weight-bold text-primary">
            {{ newsStore.activeUid }}
            <StatusMenuJiucai />
          </v-list-item-title>
          <v-list-item-subtitle>
            <!-- {{ $store.state.user.email  }} -->
            {{ newsStore.ip }}
          </v-list-item-subtitle>
          <v-list-item-subtitle style="font-size: 9px">
            {{ newsStore.region }}
          </v-list-item-subtitle>
          <v-list-item-subtitle style="font-size: 8px" v-if="myStore.user.name">
            {{ myStore.user.name }}
          </v-list-item-subtitle>
        </v-list-item>
      </v-list>
      <v-divider />
      <!-- ---------------------------------------------- -->
      <!-- Menu Area -->
      <!-- ---------------------------------------------- -->

      <v-list variant="flat" elevation="0" :lines="false" density="compact">
        <v-list-item color="primary" density="compact" @click="myStore.rewardTipDialog = true">
          <template v-slot:prepend>
            <v-avatar size="30">
              <v-icon>mdi-credit-card-outline</v-icon>
            </v-avatar>
          </template>
          <div>
            <v-list-item-subtitle class="text-body-2">{{ $t("jiucai.profileDetails") + " " + myStore.user.cost/100 }}</v-list-item-subtitle>
          </div>
        </v-list-item>
        <v-list-item color="primary" density="compact" @click="myStore.rewardTipDialog = true">
          <template v-slot:prepend>
            <v-avatar size="30">
              <v-icon>mdi-bitcoin</v-icon>
            </v-avatar>
          </template>
          <div>
            <v-list-item-subtitle class="text-body-2">{{ $t("jiucai.billing") }}</v-list-item-subtitle>
          </div>
        </v-list-item>
        <v-list-item color="primary" href="https://github.com/yangjiakai/lux-admin-vuetify3" link density="compact">
          <template v-slot:prepend>
            <v-avatar size="30">
              <v-icon>mdi-vuetify</v-icon>
            </v-avatar>
          </template>
          <div>
            <v-list-item-subtitle class="text-body-2">{{ $t("jiucai.frontendGithub") }}</v-list-item-subtitle>
          </div>
        </v-list-item>
        <v-list-item color="primary" href="https://github.com/zfoo-project/zfoo" link density="compact">
          <template v-slot:prepend>
            <v-avatar size="30">
              <v-icon>mdi-github</v-icon>
            </v-avatar>
          </template>
          <div>
            <v-list-item-subtitle class="text-body-2">{{ $t("jiucai.backendGithub") }}</v-list-item-subtitle>
          </div>
        </v-list-item>
        <v-list-item v-if="myStore.token" color="primary" density="compact" @click="myStore.profileDialog = true">
          <template v-slot:prepend>
            <v-avatar size="30">
              <v-icon>mdi-account-box-outline</v-icon>
            </v-avatar>
          </template>
          <div>
            <v-list-item-subtitle class="text-body-2">{{ $t("jiucai.profile") }}</v-list-item-subtitle>
          </div>
        </v-list-item>
        <v-list-item v-else color="primary" density="compact" @click="myStore.loginDialog = true">
          <template v-slot:prepend>
            <v-avatar size="30">
              <v-icon>mdi-qrcode-scan</v-icon>
            </v-avatar>
          </template>
          <div>
            <v-list-item-subtitle class="text-body-2">{{ $t("jiucai.login") }}</v-list-item-subtitle>
          </div>
        </v-list-item>
      </v-list>
      <v-divider />
    </v-card>
  </v-menu>
</template>
