<script setup lang="ts">
import CommonGptComponent from "./component/CommonGptComponent.vue";
import {useDisplay} from "vuetify";

const {mobile, width, height} = useDisplay();
</script>

<template>
  <CommonGptComponent :ai="6" :size="mobile ? width * 0.8 : height * 0.6"/>
</template>
