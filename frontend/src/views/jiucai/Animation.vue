<script setup lang="ts">
import {useSnackbarStore} from "@/stores/snackbarStore";
import 'md-editor-v3/lib/preview.css';
import JsFileDownloader from "js-file-downloader";

import AnimationAILlama from "@/animation/AnimationAILlama.vue";

import OssPolicyResponse from "@/protocol/auth/OssPolicyResponse";
import OssPolicyRequest from "@/protocol/auth/OssPolicyRequest";
import OssPolicyVO from "@/protocol/auth/OssPolicyVO";
import AnimationRequest from "@/protocol/animation/AnimationRequest";
import AnimationNotice from "@/protocol/animation/AnimationNotice";
import AnimationImage from "@/protocol/animation/AnimationImage";
import GroupChatRequest from "@/protocol/chat/GroupChatRequest";

import {registerPacketReceiver, isWebsocketReady, send, asyncAsk} from "@/utils/websocket";
import {useNewsStore} from "@/stores/newsStore";
import {useImageStore} from "@/stores/imageStore";
import {useImageSdStore} from "@/stores/imageSdStore";
import {useDisplay} from "vuetify";
import _ from "lodash";
import {useMyStore} from "@/stores/myStore";
import axios from "axios";


const myStore = useMyStore();
const snackbarStore = useSnackbarStore();
const route = useRoute();

const {mobile, width, height} = useDisplay();
const newsStore = useNewsStore();
const imageStore = useImageStore();
const imageSdStore = useImageSdStore();


const imageFileRef = ref(null);
const imageFileUploadingRef = ref<boolean>(false);
const imageFileUploadValueRef = ref<number>(0);


onMounted(() => {
  registerPacketReceiver(AnimationNotice, atAnimationNotice);
  messages.value = imageSdStore.animations;
});

async function download(url) {
  if (_.findIndex(imageStore.downloads, it => it === url) >= 0) {
    snackbarStore.showErrorMessage("已经下载过该图片");
    return;
  }
  new JsFileDownloader({
    url: url
  }).then(function () {
    // Called when download ended
    imageStore.downloads.push(url);
    imageStore.downloads = _.takeRight(imageStore.downloads, 100);
  }).catch(function (error) {
    // Called when an error occurred
    snackbarStore.showErrorMessage(error);
  });
}

async function share(url) {
  const imagePromptMd = `**Animation Freedom**`;
  const imageUrlMd = `<img src="${url}" alt="${imageUrlRef.value}" width="300">`;
  const request = new GroupChatRequest();
  request.message = imagePromptMd + "\n" + imageUrlMd;
  request.type = 1;
  send(request);
  snackbarStore.showSuccessMessage("成功分享图片到广场");
}


interface Message {
  nonce: string;
  progress: number;
  type: string;
  originImageUrl: string;
  originImageUrlCompression: string;
  images: AnimationImage[];
  prompts: number[];
}

// Message List
const messages = ref<Message[]>([]);
const dialogRef = ref<boolean>(false);
const imageUrlRef = ref<string>("");

function openImage(imageUrl) {
  dialogRef.value = true;
  imageUrlRef.value = imageUrl;
}


const MAX_IMAGE_LENGTH = 4;
// 下面的逻辑都是自己的
const atAnimationNotice = (packet: AnimationNotice) => {
  const nonce = packet.nonce;
  const originImageUrl = packet.originImageUrl;
  const originImageUrlCompression = packet.originImageUrlCompression;
  const images = packet.images;
  const type = packet.type;
  const message = _.find(messages.value, it => it.nonce == nonce);
  if (_.isNil(message)) {
    console.error("找不到消息", packet);
    return;
  }
  message.originImageUrl = originImageUrl;
  message.originImageUrlCompression = originImageUrlCompression;
  message.progress = images.length / MAX_IMAGE_LENGTH * 100;
  message.type = type;
  message.images = images;
};


// Send Messsage
const sendMessage = (imageUrl) => {
  const nonce = _.toString(_.random(0, 10_0000_0000));
  const request = new AnimationRequest();
  request.nonce = nonce;
  request.imageUrl = imageUrl;
  send(request);
  messages.value.push({
    nonce: nonce,
    progress: 10,
    images: [],
  });
}

const sendMessageRefresh = (imageUrl) => {
  const nonce = _.toString(_.random(0, 10_0000_0000));
  const request = new AnimationRequest();
  request.nonce = nonce;
  request.imageUrl = imageUrl;
  request.type = "refresh";
  send(request);
  messages.value.push({
    nonce: nonce,
    progress: 10,
    images: [],
  });
}

const img2Animation = async () => {
  if (imageFileRef.value == null) {
    snackbarStore.showErrorMessage("图片不能为空");
    return
  }

  if (imageFileUploadingRef.value) {
    snackbarStore.showSuccessMessage("正在上传图片请稍等");
    return
  }

  // 先获得oss policy
  const response: OssPolicyResponse = await asyncAsk(new OssPolicyRequest());
  const ossPolicy: OssPolicyVO | null = response.ossPolicy;
  if (ossPolicy == null) {
    snackbarStore.showErrorMessage("图片上传策略异常");
    return
  }

  // 上传图片
  const formData = new FormData();
  formData.append('key', ossPolicy.dir);
  formData.append('policy', ossPolicy.policy);
  formData.append('OSSAccessKeyId', ossPolicy.accessKeyId);
  formData.append('success_action_status', "200");
  formData.append('callback', '');
  formData.append('signature', ossPolicy.signature);
  formData.append('file', imageFileRef.value);

  imageFileUploadingRef.value = true;
  imageFileUploadValueRef.value = 0;
  snackbarStore.showSuccessMessage("开始上传");

  const uploadImageResponse = await axios.postForm("https://jiucai.fun", formData, {
    onUploadProgress: (progressEvent) => {
      const complete = progressEvent.loaded / progressEvent.total * 100 | 0;
      imageFileUploadValueRef.value = complete;
    }
  });

  imageFileUploadingRef.value = false;
  imageFileUploadValueRef.value = 0;
  imageFileRef.value = null;

  const imageUrl = "https://jiucai.fun/" + ossPolicy.dir;
  sendMessage(imageUrl);
};

</script>

<template>
  <v-container v-if="messages.length <= 0">
    <v-row justify="center" align="center">
      <v-col cols="12">
        <AnimationAILlama :size="mobile ? width * 0.8 : height * 0.6"/>
      </v-col>
    </v-row>
  </v-container>
  <v-container v-else>
    <template v-for="message in messages">
      <v-row v-if="!message.type && message.originImageUrl">
        <v-avatar class="mt-3 ml-3 mb-1" rounded="sm" variant="elevated">
          <img :src="newsStore.myAvatar()" alt="alt"/>
        </v-avatar>
        <v-col md="3" lg="2" cols="6">
          <v-card max-width="500px">
            <v-img :src="message.originImageUrlCompression" alt="alt">
              <template v-slot:placeholder>
                <div class="d-flex align-center justify-center fill-height">
                  <v-progress-circular
                    color="primary"
                    indeterminate
                  ></v-progress-circular>
                </div>
              </template>
            </v-img>
          </v-card>
        </v-col>
      </v-row>
      <v-row v-if="message.images">
        <v-avatar class="mt-3 ml-3 mb-1" rounded="sm" variant="elevated">
          <img :src="newsStore.aiAvatar()" alt="alt"/>
        </v-avatar>
        <v-col md="3" lg="2" cols="6" v-for="(image, index) in message.images" :key="index">
          <v-card max-width="500px">
            <v-img :src="image.imageUrlLow" @click="openImage(image.imageUrl)" alt="alt">
              <template v-slot:placeholder>
                <div class="d-flex align-center justify-center fill-height">
                  <v-progress-circular
                    color="primary"
                    indeterminate
                  ></v-progress-circular>
                </div>
              </template>
            </v-img>
          </v-card>
        </v-col>
      </v-row>
      <v-row v-if="message.images.length >= MAX_IMAGE_LENGTH" class="my-0 py-0">
        <v-avatar v-if="!mobile" class="ml-3">
        </v-avatar>
        <v-col cols="12" md="11" class="my-0 py-0">
          <v-btn-toggle color="primary" variant="outlined" multiple rounded divided>
            <v-btn class="font-weight-bold" @click="sendMessageRefresh(message.originImageUrl)">V1</v-btn>
            <v-btn class="font-weight-bold" @click="sendMessageRefresh(message.originImageUrl)">V2</v-btn>
            <v-btn class="font-weight-bold" @click="sendMessageRefresh(message.originImageUrl)">V3</v-btn>
            <v-btn class="font-weight-bold" @click="sendMessageRefresh(message.originImageUrl)">V4</v-btn>
            <v-btn icon="mdi-reload" @click="sendMessageRefresh(message.originImageUrl)"></v-btn>
          </v-btn-toggle>
        </v-col>
      </v-row>
      <v-row v-if="message.images.length < MAX_IMAGE_LENGTH">
        <v-col>
          <v-progress-linear
            v-model="message.progress"
            height="8"
            color="primary"
            class="mb-2"
            buffer-value="0"
            rounded
            striped
          >
          </v-progress-linear>
        </v-col>
      </v-row>
    </template>
  </v-container>

  <v-container>
    <v-row v-if="imageFileUploadingRef">
      <v-col>
        <v-progress-circular
          :rotate="-90"
          :size="100"
          :width="15"
          :model-value="imageFileUploadValueRef"
          color="primary"
        >
          {{ imageFileUploadValueRef }}
        </v-progress-circular>
      </v-col>
    </v-row>
  </v-container>

  <v-footer color="transparent" app>
    <template v-if="mobile">
      <v-file-input
        v-model="imageFileRef"
        label="image to animation"
        variant="solo-filled"
        append-icon="mdi-send"
        accept="image/*"
        color="primary"
        chips
        show-size
        counter
        @click:append="img2Animation"
        @keyup.enter="img2Animation"
      ></v-file-input>
    </template>
    <v-container v-else>
      <v-row>
        <v-col cols="8" offset="2">
          <v-file-input
            v-model="imageFileRef"
            label="image to animation"
            variant="solo-filled"
            append-icon="mdi-send"
            accept="image/*"
            color="primary"
            chips
            show-size
            counter
            @click:append="img2Animation"
            @keyup.enter="img2Animation"
          ></v-file-input>
        </v-col>
      </v-row>
    </v-container>
  </v-footer>

  <v-dialog v-model="dialogRef" @click="dialogRef=!dialogRef">
    <v-row v-if="mobile">
      <v-col cols="12">
        <v-img :src="imageUrlRef" :lazy-src="imageUrlRef">
          <template v-slot:placeholder>
            <div class="d-flex align-center justify-center fill-height">
              <v-progress-circular
                color="primary"
                indeterminate
              ></v-progress-circular>
            </div>
          </template>
        </v-img>
      </v-col>
      <v-col cols="3" offset="6">
        <v-btn color="primary" icon="mdi-share-outline" size="large" @click="share(imageUrlRef)"></v-btn>
      </v-col>
      <v-col cols="3">
        <v-btn color="primary" icon="mdi-cloud-download-outline" size="large" @click="download(imageUrlRef)"></v-btn>
      </v-col>
    </v-row>
    <v-row v-else>
      <v-col offset="2">
        <v-img :src="imageUrlRef" :lazy-src="imageUrlRef" :max-height="height * 0.8">
          <template v-slot:placeholder>
            <div class="d-flex align-center justify-center fill-height">
              <v-progress-circular
                color="primary"
                indeterminate
              ></v-progress-circular>
            </div>
          </template>
        </v-img>
      </v-col>
      <v-col cols="1" align-self="end">
        <v-btn color="primary" icon="mdi-share-outline" size="x-large" @click="share(imageUrlRef)"></v-btn>
      </v-col>
      <v-col cols="1" align-self="end">
        <v-btn color="primary" icon="mdi-cloud-download-outline" size="x-large" @click="download(imageUrlRef)"></v-btn>
      </v-col>
    </v-row>
  </v-dialog>
</template>
