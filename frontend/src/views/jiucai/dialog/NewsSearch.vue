<script setup lang="ts">
import 'md-editor-v3/lib/preview.css';
import {useNewsStore, levelMap} from "@/stores/newsStore";
import {useDisplay} from "vuetify";
import {useSnackbarStore} from "@/stores/snackbarStore";
import {registerPacketReceiver} from "@/utils/websocket";
import NewsSearchResponse from "@/protocol/news/NewsSearchResponse";
import News from "@/protocol/news/News";

const {mobile, width, height} = useDisplay();
const snackbarStore = useSnackbarStore();
const newsStore = useNewsStore();

const newsRef = ref<News[]>([]);

onMounted(() => {
  registerPacketReceiver(NewsSearchResponse, atNewsSearchResponse);
});

function atNewsSearchResponse(packet: NewsSearchResponse) {
  newsStore.newsSearchDialog = true;
  newsRef.value = packet.news;
  snackbarStore.showSuccessMessage(`搜索到[${packet.news.length}]消息`);
}

</script>
<template>
  <v-dialog transition="dialog-top-transition" max-width="1100px" v-model="newsStore.newsSearchDialog" scrollable>
      <v-card class="mt-3">
        <v-card-title class="text-h5">
          <v-icon icon="mdi-cloud-search-outline" color="primary"></v-icon>
          搜索结果 [{{ newsRef.length}}] 条
        </v-card-title>
        <template v-for="newsEle in newsRef">
          <v-card-title>
            <v-icon :color="levelMap[newsEle.level].color" :icon="levelMap[newsEle.level].icon" size="x-small"></v-icon>
            {{ newsEle.title }} {{ newsEle.ctime }}
          </v-card-title>
          <v-card-subtitle class="text-pre-wrap">
            {{ newsEle.content }}
          </v-card-subtitle>
          <hr/>
        </template>
      </v-card>
  </v-dialog>
</template>
