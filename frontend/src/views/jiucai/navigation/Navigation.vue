<script setup lang="ts">

import navigation from "./navigation";
import {registerPacketReceiver, isWebsocketReady, send, asyncAsk} from "@/utils/websocket";
import StatisticsRequest from "@/protocol/admin/StatisticsRequest";
import {useDisplay} from "vuetify";
import _ from "lodash";
const route = useRoute();

const {mobile, width, height} = useDisplay();

const indexRef = ref<number>(_.toNumber(route.params.id));
watch(
  () => route.params,
  (val) => {
    let id = _.toNumber(route.params.id);
    if (id >= 5) {
      id = id + 1;
    }
    indexRef.value = id;
  }
);

function openNavigation(href: string) {
  // 统计数据
  const request = new StatisticsRequest();
  request.navigation = 1;
  send(request);
  window.open(href, '_blank');
}

</script>

<template>
  <v-container>
    <v-card>
      <template v-for="nav in navigation.children[indexRef - 1].children">
        <v-card-subtitle v-if="nav.content.startsWith('-')">{{ nav.content }}</v-card-subtitle>
        <v-card-title v-else>{{ nav.content }}</v-card-title>
        <v-card-text>
          <v-chip v-for="n in nav.children" label color="primary" class="ma-1" @click="openNavigation(n.href)">
            {{ n.content }}
          </v-chip>
        </v-card-text>
      </template>
    </v-card>
  </v-container>
</template>
