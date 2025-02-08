<script setup lang="ts">
import {useMyStore} from "@/stores/myStore";
import {useDisplay} from "vuetify";
import {useSnackbarStore} from "@/stores/snackbarStore";
import {useCustomizeThemeStore} from "@/stores/customizeTheme";

const {mobile, width, height} = useDisplay();
const myStore = useMyStore();
const customizeTheme = useCustomizeThemeStore();
const snackbarStore = useSnackbarStore();

</script>

<template>
  <v-btn v-if="!myStore.token" icon @click="myStore.loginDialog = true">
    <v-badge dot color="error">
      <v-icon>mdi-account-off</v-icon>
    </v-badge>
  </v-btn>
  <v-btn v-else icon @click="myStore.profileDialog = true">
    <v-icon>mdi-account-check-outline</v-icon>
  </v-btn>
</template>
