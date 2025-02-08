<script setup lang="ts">
import 'md-editor-v3/lib/preview.css';
import {useMyStore} from "@/stores/myStore";
import {useDisplay} from "vuetify";
import {useSnackbarStore} from "@/stores/snackbarStore";
import {useCustomizeThemeStore} from "@/stores/customizeTheme";
import {registerPacketReceiver, isWebsocketReady, send, asyncAsk} from "@/utils/websocket";
import UpdateUserProfileRequest from "@/protocol/user/UpdateUserProfileRequest";
import UpdateUserProfileResponse from "@/protocol/user/UpdateUserProfileResponse";
import GetUserProfileRequest from "@/protocol/user/GetUserProfileRequest";
import GetUserProfileResponse from "@/protocol/user/GetUserProfileResponse";

const {mobile, width, height} = useDisplay();
const myStore = useMyStore();
const customizeTheme = useCustomizeThemeStore();
const snackbarStore = useSnackbarStore();
import _ from "lodash";

const phoneNumberRef = ref<string>("");

watch(
  () => myStore.profileDialog,
  async (val) => {
    if (val) {
      const answer: GetUserProfileResponse = await asyncAsk(new GetUserProfileRequest());
      myStore.updateUser(answer.user);
      phoneNumberRef.value = myStore.user.phoneNumber;
    }
  },
  {
    deep: true,
  }
);

const rules = ref({
  required: value => !!value || '请填写手机号码',
  phone: value => {
    const pattern = /^[0-9]{11}$/
    return pattern.test(value) || '手机号码不正确'
  }
});

async function updateProfile() {
  const request = new UpdateUserProfileRequest();
  request.phoneNumber = _.toNumber(phoneNumberRef.value);
  const response: UpdateUserProfileResponse = await asyncAsk(request);
  myStore.updateUser(response.user);
  snackbarStore.showSuccessMessage("手机号码更新成功");
}

</script>
<template>
  <v-dialog transition="dialog-top-transition" max-width="600px" v-model="myStore.profileDialog">
    <template v-slot:default="{ isActive }">
      <v-card prepend-icon="mdi-shield-account-outline">
        <template v-slot:title>
          我的主页
        </template>

        <v-card-text>
          <v-text-field v-model="myStore.user.name"
                        density="compact"
                        label="名称"
                        prepend-inner-icon="mdi-face-man-profile"
                        variant="outlined"
                        readonly
                        disabled
          />

          <v-list density="compact">
            <v-list-subheader>数据统计</v-list-subheader>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon icon="mdi-lightbulb-question-outline"></v-icon>
              </template>
              <v-list-item-title v-text="myStore.user.ask"></v-list-item-title>
              <v-list-item-subtitle>累计提问</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon icon="mdi-image-edit-outline"></v-icon>
              </template>
              <v-list-item-title v-text="myStore.user.draw"></v-list-item-title>
              <v-list-item-subtitle>累计绘图</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon icon="mdi-login-variant"></v-icon>
              </template>
              <v-list-item-title v-text="myStore.user.login"></v-list-item-title>
              <v-list-item-subtitle>累计登录</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon icon="mdi-currency-jpy"></v-icon>
              </template>
              <v-list-item-title v-text="myStore.user.cost / 100"></v-list-item-title>
              <v-list-item-subtitle>白嫖指数</v-list-item-subtitle>
            </v-list-item>
          </v-list>

          <v-text-field v-model="phoneNumberRef"
                        :rules="[rules.required, rules.phone]"
                        density="compact"
                        label="手机号码"
                        placeholder="请填写正确的手机号码"
                        prepend-inner-icon="mdi-cellphone-basic"
                        variant="outlined"
                        maxlength="11"
                        counter
          />
        </v-card-text>

        <v-card-subtitle>
          手机号码用来推送最新的S级情报和SSR新概念行情
        </v-card-subtitle>

        <v-card-actions>
          <v-btn color="primary" variant="flat" @click="updateProfile">
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </template>
  </v-dialog>
</template>
