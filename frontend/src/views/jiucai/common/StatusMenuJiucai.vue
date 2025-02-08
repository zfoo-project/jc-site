<!--
* @Component:
* @Maintainer: J.K. Yang
* @Description:
-->
<script setup lang="ts">
import {useNewsStore} from "@/stores/newsStore";
const newsStore = useNewsStore();

interface UserStatus {
  code: string;
  name: string;
  label: string;
  color: string;
}

const userStatusList = [
  {
    code: "online",
    name: "us",
    label: "Online",
    color: "success",
  },
  {
    code: "offline",
    name: "kr",
    label: "Offline",
    color: "error",
  },
  {
    code: "away",
    name: "cn",
    label: "Away",
    color: "warning",
  },
  {
    code: "busy",
    name: "jp",
    label: "Busy",
    color: "error",
  },
];

const currentStatus = ref<UserStatus>({
  code: "online",
  name: "us",
  label: "Online",
  color: "success",
});

const setStatus = (status: string) => {
  currentStatus.value = userStatusList.find(
    (userStatus) => userStatus.code === status
  ) as UserStatus;
};

const onlineConvert = (online: boolean) => {
  return online ? userStatusList[0] : userStatusList[1];
}
</script>

<template>
  <v-btn
    width="60"
    variant="text"
    size="small"
    :color="onlineConvert(newsStore.online).color"
  >
    {{ onlineConvert(newsStore.online).label }}
  </v-btn>
</template>

<style scoped lang="scss"></style>
