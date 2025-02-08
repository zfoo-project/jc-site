<script setup lang="ts">
import {useSnackbarStore} from "@/stores/snackbarStore";
import {Icon} from "@iconify/vue";
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import JsFileDownloader from "js-file-downloader";

import AnimationStableDiffusion from "@/animation/AnimationStableDiffusion.vue";
import SdSimulateRequest from "@/protocol/sdiffusion/SdSimulateRequest";
import SdSimulateResponse from "@/protocol/sdiffusion/SdSimulateResponse";
import SdSimulateNotice from "@/protocol/sdiffusion/SdSimulateNotice";
import SdHistoryRequest from "@/protocol/sdiffusion/SdHistoryRequest";
import ImageDownloadRequest from "@/protocol/sdiffusion/ImageDownloadRequest";
import ImageDownloadResponse from "@/protocol/sdiffusion/ImageDownloadResponse";
import GroupChatRequest from "@/protocol/chat/GroupChatRequest";

import {registerPacketReceiver, isWebsocketReady, send, asyncAsk} from "@/utils/websocket";
import {useNewsStore} from "@/stores/newsStore";
import {useImageStore} from "@/stores/imageStore";
import {useImageSdReStore} from "@/stores/imageSdReStore";
import {useDisplay} from "vuetify";
import _ from "lodash";
import SdImage from "@/protocol/sdiffusion/SdImage";
import {isBlank} from "@/utils/stringUtils";
import {useMyStore} from "@/stores/myStore";


const myStore = useMyStore();
const snackbarStore = useSnackbarStore();
const route = useRoute();

const {mobile, width, height} = useDisplay();
const newsStore = useNewsStore();
const imageStore = useImageStore();
const imageSdReStore = useImageSdReStore();

const MAX_HISTORY = 20;
let animationRunIndex = 1;

onMounted(() => {
  registerPacketReceiver(SdSimulateNotice, atSdSimulateNotice);
  messages.value = imageSdReStore.sdPrompts;
  initHistory();
  setInterval(() => initHistory(), 10 * 1000);
  setTimeout(() => scrollToBottomDelay(), 100);
  loadConfigs();
});

function isFinished(message) {
  return _.size(message.sdImages) >= message.batchSize;
}

async function initHistory() {
  setTimeout(() => doInitHistory(), 1000);
}

async function doInitHistory() {
  if (!isWebsocketReady()) {
    initHistory();
    return;
  }
  if (_.isEmpty(messages.value)) {
    messages.value = [];
    return;
  }
  for (const message of messages.value) {
    if (isFinished(message)) {
      continue;
    }
    const request = new SdHistoryRequest();
    request.nonce = message.id;
    send(request);
  }
}

async function download(url) {
  if (_.findIndex(imageStore.downloads, it => it === url) >= 0) {
    snackbarStore.showErrorMessage("已经下载过该图片");
    return;
  }
  const request: ImageDownloadRequest = new ImageDownloadRequest();
  request.url = url;
  const response: ImageDownloadResponse = await asyncAsk(request);
  const realUrl = response.realUrl;
  new JsFileDownloader({
    url: realUrl
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
  const imagePromptMd = `**${imagePromptRef.value}**`;
  const imageUrlMd = `<img src="${url}" alt="${imagePromptRef.value}" width="300">`;
  const request = new GroupChatRequest();
  request.message = imagePromptMd + "\n" + imageUrlMd;
  request.type = 1;
  send(request);
  snackbarStore.showSuccessMessage("成功分享图片到广场");
}

// Scroll to the bottom of the message container
const scrollToBottomDelay = () => {
  setTimeout(() => {
    window.scrollTo({top: 999999, behavior: "smooth"});
  }, 200);
};


interface Message {
  id: number;
  content: string;
  batchSize: number;
  progress: number;
  refreshTime: number;
  costTime: number;
  sdImages: Array<SdImage>;
}

const dimensionInfos = [
  {
    dimension: 0,
    description: "768 x 768"
  },
  {
    dimension: 1,
    description: "768 x 1024"
  },
];

// Message List
const isLoading = ref(false);
const messages = ref<Message[]>([]);
const dialogRef = ref<boolean>(false);
const imagePromptRef = ref<string>("");
const imageUrlRef = ref<string>("");
const imageUrlLowRef = ref<string>("");
const imageUrlMiddleRef = ref<string>("");
const imageUrlHighRef = ref<string>("");

const promptRef = ref<string>("");

function loadConfigs() {
  const sdParameters = imageSdReStore.sdParameters;
  if (_.isNil(sdParameters)) {
    return;
  }
  promptRef.value = sdParameters.prompt;
}

function saveConfigs() {
  imageSdReStore.sdParameters = {
    prompt: promptRef.value,
  };
}


function openImage(prompt, sdImage) {
  dialogRef.value = true;
  imagePromptRef.value = prompt;
  imageUrlRef.value = sdImage.imageUrl;
  imageUrlLowRef.value = sdImage.imageUrlLow;
  imageUrlMiddleRef.value = sdImage.imageUrlMiddle;
  imageUrlHighRef.value = sdImage.imageUrlHigh;
}

// Send Messsage
const sendMessage = async () => {
  if (isBlank(promptRef.value)) {
    snackbarStore.showErrorMessage("prompt不能为空");
    return
  }
  // Clear the input
  if (promptRef.value) {
    saveConfigs();
    isLoading.value = true;
    animationRunIndex = _.random(1, 5);

    const request = new SdSimulateRequest();
    request.nonce = _.random(0, 10_0000_0000);
    request.prompt = promptRef.value;
    request.negativePrompt = "";
    request.steps = 20;
    request.batchSize = 6;
    request.style = 1;
    request.dimension = 99;
    request.ignores = imageSdReStore.sds;
    const response: SdSimulateResponse = await asyncAsk(request);
    const message = {
      id: response.nonce,
      content: response.enPrompt,
      batchSize: request.batchSize,
      progress: 0,
      refreshTime: 0,
      costTime: response.costTime,
      sdImages: []
    };
    setTimeout(() => refreshMessage(response.nonce), 100);
    messages.value.push(message);
    imageSdReStore.sdPrompts = _.takeRight(messages.value, MAX_HISTORY);
    scrollToBottomDelay();
    myStore.user.cost += 5;
  }
};

function refreshMessage(id) {
  const message = _.find(messages.value, it => it.id == id);
  if (_.isNil(message)) {
    return
  }
  if (message.refreshTime > message.costTime) {
    return;
  }
  message.refreshTime += 100;
  message.progress = message.refreshTime / message.costTime * 100;
  setTimeout(() => refreshMessage(id), 100);
}


// 下面的逻辑都是自己的
const atSdSimulateNotice = (packet: SdSimulateNotice) => {

  const nonce = packet.nonce;
  const images = packet.images;
  const message = _.find(messages.value, it => it.id == nonce);
  if (_.isNil(message)) {
    console.error("找不到消息", packet);
    return;
  }
  message.sdImages = images;
  images.forEach(it => imageSdReStore.sds.push(it.id));
  imageSdReStore.sds = _.takeRight(imageSdReStore.sds, 2000);
  if (isFinished(message)) {
    message.refreshTime = message.costTime + 3000;
    isLoading.value = false;
  } else {
    const progressRatio = images.length / message.batchSize;
    const progress = progressRatio * 100;
    if (message.progress < progress) {
      message.progress = progress;
      message.refreshTime = message.costTime * progressRatio;
    }
  }
};


const handleKeydown = (e) => {
  if (e.key === "Enter" && (e.altKey || e.shiftKey)) {
    // 当同时按下 alt或者shift 和 enter 时，插入一个换行符
    e.preventDefault();
    promptRef.value += "\n";
  } else if (e.key === "Enter") {
    // 当只按下 enter 时，发送消息
    e.preventDefault();
    if (!isLoading.value) {
      sendMessage();
    }
  }
};

</script>

<template>
  <v-container v-if="messages.length <= 0">
    <v-row justify="center" align="center">
      <v-col cols="12">
        <AnimationStableDiffusion :size="mobile ? width * 0.8 : height * 0.6"/>
      </v-col>
    </v-row>
  </v-container>
  <v-container v-else>
    <template v-for="message in messages">
      <v-row>
        <v-avatar class="mt-3 ml-3 mb-1" rounded="sm" variant="elevated">
          <img :src="newsStore.myAvatar()" alt="alt"/>
        </v-avatar>
        <v-card class="mt-3 mx-3">
          <md-preview v-model="message.content" editor-id="preview-only"/>
        </v-card>
      </v-row>
      <v-row v-if="!_.isEmpty(message.sdImages)">
        <v-col md="3" lg="2" cols="6"  v-for="(sdImage, index) in message.sdImages" :key="index">
          <v-card max-width="500px">
            <v-img :src="sdImage.imageUrlMiddle" @click="openImage(message.content, sdImage)" alt="alt">
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
      <v-row v-if="!isFinished(message)">
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

  <v-footer color="transparent" app>
    <template v-if="mobile">
      <v-textarea
        color="primary"
        type="text"
        variant="solo"
        ref="input"
        v-model="promptRef"
        placeholder="prompt"
        hide-details
        @keydown="handleKeydown"
        rows="1"
        max-rows="9"
        :autofocus="!mobile"
        auto-grow
      >
        <template v-slot:append-inner>
          <v-fade-transition leave-absolute>
            <Icon
              v-if="isLoading"
              class="text-primary"
              width="30"
              icon="eos-icons:three-dots-loading"
            />
            <v-icon color="primary" v-else @click="sendMessage"
            >mdi-send
            </v-icon
            >
          </v-fade-transition>
        </template>
      </v-textarea>
    </template>
    <v-container v-else>
      <v-row>
        <v-col cols="8" offset="2">
          <v-textarea
            color="primary"
            type="text"
            variant="solo"
            ref="input"
            v-model="promptRef"
            placeholder="prompt"
            hide-details
            @keydown="handleKeydown"
            rows="1"
            max-rows="9"
            :autofocus="!mobile"
            auto-grow
          >
            <template v-slot:append-inner>
              <v-fade-transition leave-absolute>
                <Icon
                  v-if="isLoading"
                  class="text-primary"
                  width="30"
                  icon="eos-icons:three-dots-loading"
                />
                <v-icon color="primary" v-else @click="sendMessage"
                >mdi-send
                </v-icon
                >
              </v-fade-transition>
            </template>
          </v-textarea>
        </v-col>
      </v-row>
    </v-container>
  </v-footer>

  <v-dialog v-model="dialogRef" @click="dialogRef=!dialogRef">
    <v-row v-if="mobile">
      <v-col cols="12">
        <v-img :src="imageUrlMiddleRef" :lazy-src="imageUrlLowRef">
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
        <v-btn color="primary" icon="mdi-share-outline" size="large" @click="share(imageUrlHighRef)"></v-btn>
      </v-col>
      <v-col cols="3">
        <v-btn color="primary" icon="mdi-cloud-download-outline" size="large" @click="download(imageUrlRef)"></v-btn>
      </v-col>
    </v-row>
    <v-row v-else>
      <v-col offset="2">
        <v-img :src="imageUrlHighRef" :lazy-src="imageUrlMiddleRef" :max-height="height * 0.8">
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
        <v-btn color="primary" icon="mdi-share-outline" size="x-large" @click="share(imageUrlHighRef)"></v-btn>
      </v-col>
      <v-col cols="1" align-self="end">
        <v-btn color="primary" icon="mdi-cloud-download-outline" size="x-large" @click="download(imageUrlRef)"></v-btn>
      </v-col>
    </v-row>
  </v-dialog>
</template>
