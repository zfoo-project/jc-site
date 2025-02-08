<!--
* @Component:
* @Maintainer: J.K. Yang
* @Description:
-->
<script setup lang="ts">
import { Vue3Lottie } from "vue3-lottie";
const props = defineProps({
  size: {
    type: Number,
    default: 400,
  },
});
</script>

<template>
  <Vue3Lottie
    animationLink="/aa/chat-animation-2.json"
    :height="props.size"
    :width="props.size"
  />
</template>

<style scoped lang="scss"></style>
