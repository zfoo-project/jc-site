<script setup lang="ts">
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import axios from "axios";

const tutorialUrl = import.meta.env.VITE_BASE_HTTP_URL + "/config/tutorial.md";

onMounted(async () => {
  const response = await axios.get(tutorialUrl);
  console.log(response);
  const tutorialMd = response.data;
  tutorialRef.value = tutorialMd;
});


// User Input Message
const tutorialRef = ref("");

</script>

<template>
  <v-container >
    <md-preview v-model="tutorialRef" editor-id="preview-only"/>
  </v-container>

</template>
