<template>
  <v-menu scroll-y>
    <template v-slot:activator="{ props }">
      <v-btn :icon="toNewsLevel(currentLevel).icon" :color="toNewsLevel(currentLevel).color" v-bind="props">
      </v-btn>
    </template>
    <v-list elevation="1" nav>
      <v-list-item
        v-for="ele in newsLevel"
        :key="ele.level"
        @click="setLevel(ele.level, ele.value)"
        density="compact"
      >
        <v-icon :icon="ele.icon" :color="ele.color"></v-icon>
      </v-list-item>
    </v-list>
  </v-menu>
</template>
<script setup lang="ts">
import _ from "lodash";
import {useNewsStore} from "@/stores/newsStore";
const newsStore = useNewsStore();

const newsLevel = [
  {
    level: "S",
    value: 1,
    color: "error",
    icon: "mdi-alpha-s-circle-outline",
  },
  {
    level: "A",
    value: 2,
    color: "warning",
    icon: "mdi-alpha-a-circle-outline",
  },
  {
    level: "B",
    value: 3,
    color: "success",
    icon: "mdi-alpha-b-circle-outline",
  },
  {
    level: "C",
    value: 4,
    color: "info",
    icon: "mdi-alpha-c-circle-outline",
  },
  {
    level: "D",
    value: 5,
    color: "blue-grey",
    icon: "mdi-alpha-d-circle-outline",
  }
];

const currentLevel = ref("D");
const toNewsLevel = (level) => {
  return newsLevel.find(it => it.level == level);
}

onMounted(() => {
  currentLevel.value = newsStore.newsLevelFilter;
});

const setLevel = (level, value) => {
  currentLevel.value = level;
  newsStore.updateNewsLevelFilter(level, value);
};
</script>
