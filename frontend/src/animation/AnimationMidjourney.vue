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
  delay: {
    type: Number,
    default: 0,
  },
});
</script>

<template>
  <Vue3Lottie
    animationLink="/aa/midjourney-animation-1.json"
    :height="props.size"
    :width="props.size"
    :delay="props.delay"
  />
</template>

<style scoped lang="scss"></style>
