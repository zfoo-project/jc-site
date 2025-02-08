<script setup lang="ts">
import axios from "axios";
import {useSnackbarStore} from "@/stores/snackbarStore";
import AnimationRun1 from "@/animation/AnimationRun1.vue";
import AnimationRun2 from "@/animation/AnimationRun2.vue";
import AnimationRun3 from "@/animation/AnimationRun3.vue";
import AnimationRun4 from "@/animation/AnimationRun4.vue";
import AnimationRun5 from "@/animation/AnimationRun5.vue";
import AnimationRun6 from "@/animation/AnimationRun6.vue";
import {Icon} from "@iconify/vue";
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import JsFileDownloader from "js-file-downloader";

import AnimationMidjourney from "@/animation/AnimationMidjourney.vue";
import MidImagineRequest from "@/protocol/midjourney/MidImagineRequest";
import MidRerollRequest from "@/protocol/midjourney/MidRerollRequest";
import MidHistoryRequest from "@/protocol/midjourney/MidHistoryRequest";
import MidSelectRequest from "@/protocol/midjourney/MidSelectRequest";
import MidUpscaleRequest from "@/protocol/midjourney/MidUpscaleRequest";
import MidZoomRequest from "@/protocol/midjourney/MidZoomRequest";
import MidInpaintRequest from "@/protocol/midjourney/MidInpaintRequest";
import MidImagineNotice from "@/protocol/midjourney/MidImagineNotice";
import ImageDownloadRequest from "@/protocol/sdiffusion/ImageDownloadRequest";
import ImageDownloadResponse from "@/protocol/sdiffusion/ImageDownloadResponse";
import OssPolicyRequest from "@/protocol/auth/OssPolicyRequest";
import OssPolicyResponse from "@/protocol/auth/OssPolicyResponse";
import OssPolicyVO from "@/protocol/auth/OssPolicyVO";
import GroupChatRequest from "@/protocol/chat/GroupChatRequest";

import {registerPacketReceiver, isWebsocketReady, send, asyncAsk} from "@/utils/websocket";
import {useNewsStore} from "@/stores/newsStore";
import {useImageStore} from "@/stores/imageStore";
import {useDisplay} from "vuetify";
import _ from "lodash";
import {isBlank} from "@/utils/stringUtils";
import {useMyStore} from "@/stores/myStore";


const myStore = useMyStore();
const snackbarStore = useSnackbarStore();
const route = useRoute();

const {mobile, width, height} = useDisplay();
const newsStore = useNewsStore();
const imageStore = useImageStore();

const MAX_HISTORY = 10;
const maxAnimation = 6;
let animationRunIndex = 1;

onMounted(() => {
  registerPacketReceiver(MidImagineNotice, atMidImagineNotice);
  messages.value = imageStore.midPrompts;
  initHistory();
  setInterval(() => initHistory(), 10 * 1000);
  setTimeout(() => scrollToBottomDelay(), 100);
});

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
    if (message.type === 'complete') {
      continue;
    }
    const request = new MidHistoryRequest();
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
  const imagePromptMd = `${imagePromptRef.value}`;
  const imageUrlMd = `<img src="${url}" alt="${imagePromptRef.value}" width="300">`;
  const request = new GroupChatRequest();
  request.message = imagePromptMd + "\n" + imageUrlMd;
  request.type = 2;
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
  id: string;
  type: string;
  content: string;
  imageUrl: string;
  imageUrlLow: string;
  imageUrlMiddle: string;
  imageUrlHigh: string;
  progress: number;
  midjourneyId: number;
  reroll: boolean;
  upsample: boolean;
  upscale: boolean;
}

// Message List
const messages = ref<Message[]>([]);
const dialogRef = ref<boolean>(false);
const dialogImg2ImgRef = ref<boolean>(false);
const imagePromptRef = ref<string>("");
const imageUrlRef = ref<string>("");
const imageUrlLowRef = ref<string>("");
const imageUrlMiddleRef = ref<string>("");
const imageUrlHighRef = ref<string>("");
const imageFileRef = ref(null);
const imageFileUploadingRef = ref<boolean>(false);
const imageFileUploadValueRef = ref<number>(0);

// User Input Message
const userMessage = ref("");
const isLoadingRef = ref(false);

function seed(): string {
  const a = _.random(10_0000_0000, 20_0000_0000);
  const b = _.random(1_0000_0000, 2_0000_0000);
  const seed = _.toString(a) + _.toString(b);
  return seed;
}

function openImage(message) {
  dialogRef.value = true;
  imagePromptRef.value = message.content;
  imageUrlRef.value = message.imageUrl;
  imageUrlLowRef.value = message.imageUrlLow;
  imageUrlMiddleRef.value = message.imageUrlMiddle;
  imageUrlHighRef.value = message.imageUrlHigh;
  console.log(height.value)
}

const text2Img = async () => {
  if (isBlank(userMessage.value)) {
    snackbarStore.showErrorMessage("prompt不能为空");
    return
  }
  // 自己的韭菜广场的发送
  const request = new MidImagineRequest();
  request.prompt = userMessage.value;
  request.nonce = seed();
  isLoadingRef.value = true;
  userMessage.value = "";
  animationRunIndex = _.random(1, maxAnimation);
  send(request);
  myStore.user.cost += 10;
};

const img2Img = async () => {
  if (isBlank(userMessage.value)) {
    snackbarStore.showErrorMessage("prompt不能为空");
    return
  }

  if (imageFileRef.value == null) {
    snackbarStore.showErrorMessage("图片不能为空");
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

  const uploadImageResponse = await axios.postForm("https://jiucai.fun", formData, {
    onUploadProgress: (progressEvent) => {
      const complete = progressEvent.loaded / progressEvent.total * 100 | 0;
      imageFileUploadValueRef.value = complete;
    }
  });

  // const uploadImageResponse = await axios.create({baseURL: "/upload", timeout: 100000,}).postForm("", formData, {
  //   onUploadProgress: (progressEvent) => {
  //     console.log(progressEvent)
  //     const complete = progressEvent.loaded / progressEvent.total * 100 | 0;
  //     imageFileUploadValueRef.value = complete;
  //   }
  // });

  dialogImg2ImgRef.value = false;
  imageFileUploadingRef.value = false;
  imageFileUploadValueRef.value = 0;
  imageFileRef.value = null;

  userMessage.value = "https://jiucai.fun/" + ossPolicy.dir + " " + userMessage.value;
  text2Img();
};

const reroll = async (midjourneyId) => {
  const request = new MidRerollRequest();
  request.midjourneyId = midjourneyId;
  request.nonce = seed();
  isLoadingRef.value = true;
  userMessage.value = "";
  animationRunIndex = _.random(1, 5);
  send(request);
  myStore.user.cost += 10;
};

const select = async (midjourneyId, index, category) => {
  const request = new MidSelectRequest();
  request.midjourneyId = midjourneyId;
  request.index = index;
  request.category = category;
  request.nonce = seed();
  isLoadingRef.value = true;
  userMessage.value = "";
  animationRunIndex++;
  send(request);
  myStore.user.cost += 10;
};

const upscale = async (midjourneyId, category) => {
  const request = new MidUpscaleRequest();
  request.midjourneyId = midjourneyId;
  request.category = category;
  request.nonce = seed();
  isLoadingRef.value = true;
  userMessage.value = "";
  animationRunIndex++;
  send(request);
  myStore.user.cost += 10;
};
const zoom = async (midjourneyId, zoom) => {
  const request = new MidZoomRequest();
  request.midjourneyId = midjourneyId;
  request.zoom = zoom;
  request.nonce = seed();
  isLoadingRef.value = true;
  userMessage.value = "";
  animationRunIndex++;
  send(request);
  myStore.user.cost += 10;
};

const inpaint = async (midjourneyId) => {
  const request = new MidInpaintRequest();
  request.midjourneyId = midjourneyId;
  request.nonce = seed();
  isLoadingRef.value = true;
  userMessage.value = "";
  animationRunIndex++;
  send(request);
  myStore.user.cost += 10;
};

// 下面的逻辑都是自己的
const atMidImagineNotice = (packet: MidImagineNotice) => {
  const id = packet.nonce;
  const type = packet.type;
  const imageUrl = packet.imageUrl;
  const content = packet.content;
  const progress = packet.progress;
  const midjourneyId = packet.midjourneyId;
  if (type === "provider") {
    const message = _.find(messages.value, it => it.id == id);
    if (message == null) {
      messages.value.push({
        id: packet.nonce,
        type: packet.type,
        imageUrl: packet.imageUrl,
        imageUrlLow: packet.imageUrlLow,
        imageUrlMiddle: packet.imageUrlMiddle,
        imageUrlHigh: packet.imageUrlHigh,
        content: packet.content,
        progress: packet.progress,
        midjourneyId: midjourneyId,
        reroll: false,
        upsample: false,
        upscale: false
      });
    }
    updateMessage(packet);
    scrollToBottomDelay();
  } else if (type === "consumer") {
    updateMessage(packet);
  } else if (type === "create") {
    updateMessage(packet);
  } else if (type === "update") {
    updateMessage(packet);
  } else if (type === "complete") {
    updateMessage(packet);
    scrollToBottomDelay();
    isLoadingRef.value = false;
  } else if (type === "stop") {
    updateMessage(packet);
    isLoadingRef.value = false;
  } else if (type === "expire") {
    // 过期给一个提示
    isLoadingRef.value = false;
    snackbarStore.showErrorMessage(content)
  }
};

function updateMessage(packet: MidImagineNotice) {
  const id = packet.nonce;
  const type = packet.type;
  const imageUrl = packet.imageUrl;
  const imageUrlLow = packet.imageUrlLow;
  const imageUrlMiddle = packet.imageUrlMiddle;
  const imageUrlHigh = packet.imageUrlHigh;
  const content = packet.content;
  const progress = packet.progress;
  const reroll = packet.reroll;
  const upsample = packet.upsample;
  const upscale = packet.upscale;
  const midjourneyId = packet.midjourneyId;
  const message = _.find(messages.value, it => it.id == id);
  if (message == null) {
    return;
  }
  message.type = type;
  message.imageUrl = imageUrl;
  message.imageUrlLow = imageUrlLow;
  message.imageUrlMiddle = imageUrlMiddle;
  message.imageUrlHigh = imageUrlHigh;
  message.content = content;
  message.progress = progress;
  message.midjourneyId = midjourneyId;
  message.reroll = reroll;
  message.upsample = upsample;
  message.upscale = upscale;
  // 保存到本地
  imageStore.midPrompts = _.takeRight(messages.value, MAX_HISTORY);
}

const handleKeydown = (e) => {
  if (e.key === "Enter" && (e.altKey || e.shiftKey)) {
    // 当同时按下 alt或者shift 和 enter 时，插入一个换行符
    e.preventDefault();
    userMessage.value += "\n";
  } else if (e.key === "Enter") {
    // 当只按下 enter 时，发送消息
    e.preventDefault();
    if (isLoadingRef.value) {
      snackbarStore.showWarningMessage("等待一会");
      return
    }
    if (dialogImg2ImgRef.value) {
      img2Img()
    } else {
      text2Img();
    }
  }
};

</script>

<template>
  <v-container v-if="messages.length <= 0">
    <v-row justify="center" align="center">
      <v-col cols="12">
        <AnimationMidjourney :size="mobile ? width * 0.8 : height * 0.6"/>
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
      <v-row v-if="!_.isEmpty(message.imageUrl)">
        <v-avatar v-if="!mobile" class="mt-3 ml-3 mb-1">
        </v-avatar>
        <v-col cols="12" md="11">
          <v-card max-width="500px">
            <v-img :src="message.imageUrlLow" @click="openImage(message)" alt="alt">
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
      <v-row v-if="message.reroll" class="my-0 py-0">
        <v-avatar v-if="!mobile" class="ml-3">
        </v-avatar>
        <v-col cols="12" md="11" class="my-0 py-0">
          <v-btn-toggle color="primary" variant="outlined" multiple rounded divided>
            <v-btn class="font-weight-bold" @click="select(message.midjourneyId, 1, 'upsample')">U1</v-btn>
            <v-btn class="font-weight-bold" @click="select(message.midjourneyId, 2, 'upsample')">U2</v-btn>
            <v-btn class="font-weight-bold" @click="select(message.midjourneyId, 3, 'upsample')">U3</v-btn>
            <v-btn class="font-weight-bold" @click="select(message.midjourneyId, 4, 'upsample')">U4</v-btn>
            <v-btn icon="mdi-reload" @click="reroll(message.midjourneyId)"></v-btn>
          </v-btn-toggle>
          <br/>
          <v-btn-toggle color="primary" variant="outlined" multiple rounded divided>
            <v-btn class="font-weight-bold" @click="select(message.midjourneyId, 1, 'variation')">V1</v-btn>
            <v-btn class="font-weight-bold" @click="select(message.midjourneyId, 2, 'variation')">V2</v-btn>
            <v-btn class="font-weight-bold" @click="select(message.midjourneyId, 3, 'variation')">V3</v-btn>
            <v-btn class="font-weight-bold" @click="select(message.midjourneyId, 4, 'variation')">V4</v-btn>
          </v-btn-toggle>
        </v-col>
      </v-row>
      <v-row v-else-if="message.upsample" class="my-0 py-0">
        <v-avatar v-if="!mobile" class="ml-3">
        </v-avatar>
        <v-col cols="12" md="11" class="my-0 py-0">
          <v-btn-toggle color="primary" variant="outlined" multiple rounded divided>
            <v-btn class="font-weight-bold" @click="upscale(message.midjourneyId, 'upsample_v6r1_2x_subtle')" prepend-icon="mdi-arrow-expand-all">Upscale(Subtle)</v-btn>
            <v-btn class="font-weight-bold" @click="upscale(message.midjourneyId, 'upsample_v6r1_2x_creative')" prepend-icon="mdi-arrow-expand-all">Upscale(Creative)</v-btn>
            <v-btn class="font-weight-bold" @click="upscale(message.midjourneyId, 'low_variation')" prepend-icon="mdi-magic-staff">Vary(Subtle)</v-btn>
            <v-btn class="font-weight-bold" @click="upscale(message.midjourneyId, 'high_variation')" prepend-icon="mdi-magic-staff">Vary(Strong)</v-btn>
<!--            <v-btn class="font-weight-bold" @click="inpaint(message.midjourneyId)" prepend-icon="mdi-fountain-pen">Vary(Region)</v-btn>-->
          </v-btn-toggle>
          <br/>
          <v-btn-toggle color="primary" variant="outlined" multiple rounded divided>
            <v-btn class="font-weight-bold" @click="zoom(message.midjourneyId, '50')" prepend-icon="mdi-magnify">Zoom Out 2x</v-btn>
            <v-btn class="font-weight-bold" @click="zoom(message.midjourneyId, '75')" prepend-icon="mdi-magnify">Zoom Out 1.5x</v-btn>
          </v-btn-toggle>
          <br/>
          <v-btn-toggle color="primary" variant="outlined" multiple rounded divided>
            <v-btn class="font-weight-bold" @click="upscale(message.midjourneyId, 'pan_left')" prepend-icon="mdi-arrow-left-bold"></v-btn>
            <v-btn class="font-weight-bold" @click="upscale(message.midjourneyId, 'pan_right')" prepend-icon="mdi-arrow-right-bold"></v-btn>
            <v-btn class="font-weight-bold" @click="upscale(message.midjourneyId, 'pan_up')" prepend-icon="mdi-arrow-up-bold"></v-btn>
            <v-btn class="font-weight-bold" @click="upscale(message.midjourneyId, 'pan_down')" prepend-icon="mdi-arrow-down-bold"></v-btn>
          </v-btn-toggle>
        </v-col>
      </v-row>
      <v-row v-if="message.type === 'provider' || message.type === 'consumer' || message.type === 'create' || message.type === 'update'">
        <v-col cols="12" md="11">
          如果超过2分钟无响应，则考虑刷新页面换一个提示词
          <v-progress-linear
            v-model="message.progress"
            height="8"
            color="primary"
            class="mb-2"
            buffer-value="0"
            rounded
            :indeterminate="message.type === 'provider' || message.type === 'consumer'"
            :stream="message.type === 'create'"
            :striped="message.type === 'update'"
          >
          </v-progress-linear>
        </v-col>
      </v-row>
    </template>

    <v-row v-if="isLoadingRef">
      <v-col cols="12">
        <AnimationRun1 v-if="animationRunIndex === 1" :size="300"/>
        <AnimationRun2 v-else-if="animationRunIndex === 2" :size="300"/>
        <AnimationRun3 v-else-if="animationRunIndex === 3" :size="300"/>
        <AnimationRun4 v-else-if="animationRunIndex === 4" :size="300"/>
        <AnimationRun5 v-else-if="animationRunIndex === 5" :size="300"/>
        <AnimationRun6 v-else :size="300"/>
      </v-col>
    </v-row>
  </v-container>

  <v-footer color="transparent" app>
    <template v-if="mobile">
      <v-textarea
        color="primary"
        type="text"
        variant="solo"
        ref="input"
        v-model="userMessage"
        placeholder="prompt"
        hide-details
        @keydown="handleKeydown"
        rows="1"
        max-rows="9"
        :autofocus="!mobile"
        auto-grow
      >
        <template #prepend-inner>
          <v-icon color="primary" @click="dialogImg2ImgRef = !dialogImg2ImgRef">mdi-image-plus-outline</v-icon>
        </template>
        <template v-slot:append-inner>
          <v-fade-transition leave-absolute>
            <Icon v-if="isLoadingRef" class="text-primary" width="30" icon="eos-icons:three-dots-loading"/>
            <v-icon color="primary" v-else @click="text2Img">mdi-send</v-icon>
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
            v-model="userMessage"
            placeholder="prompt"
            hide-details
            @keydown="handleKeydown"
            rows="1"
            max-rows="9"
            :autofocus="!mobile"
            auto-grow
          >
            <template #prepend-inner>
              <v-icon color="primary" @click="dialogImg2ImgRef = !dialogImg2ImgRef">mdi-image-plus-outline</v-icon>
            </template>
            <template v-slot:append-inner>
              <v-fade-transition leave-absolute>
                <Icon v-if="isLoadingRef" class="text-primary" width="30" icon="eos-icons:three-dots-loading"/>
                <v-icon color="primary" v-else @click="text2Img">mdi-send</v-icon>
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
        <v-img :src="imageUrlMiddleRef" :lazy-src="imageUrlLowRef" :max-width="width * 0.8" :max-height="height * 0.95">
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
        <v-btn color="primary" icon="mdi-share-outline" size="large" @click="share(imageUrlMiddleRef)"></v-btn>
      </v-col>
      <v-col cols="3">
        <v-btn color="primary" icon="mdi-cloud-download-outline" size="large" @click="download(imageUrlRef)"></v-btn>
      </v-col>
    </v-row>
    <v-row v-else>
      <v-col offset="1">
        <v-img :src="imageUrlMiddleRef" :lazy-src="imageUrlLowRef" :max-height="height * 0.95">
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
        <v-btn color="primary" icon="mdi-share-outline" size="x-large" @click="share(imageUrlMiddleRef)"></v-btn>
      </v-col>
      <v-col cols="1" align-self="end">
        <v-btn color="primary" icon="mdi-cloud-download-outline" size="x-large" @click="download(imageUrlRef)"></v-btn>
      </v-col>
    </v-row>
  </v-dialog>



  <v-dialog v-model="dialogImg2ImgRef" max-width="700">
    <v-card>
      <v-card-text>
        <v-file-input
          v-model="imageFileRef"
          label="image to image"
          variant="solo-filled"
          prepend-icon="mdi-image-outline"
          accept="image/*"
          color="primary"
          chips
          show-size
          counter
        ></v-file-input>
      </v-card-text>

      <v-card-text>
        <v-textarea
          color="primary"
          type="text"
          variant="solo"
          ref="input"
          v-model="userMessage"
          placeholder="prompt"
          hide-details
          @keydown="handleKeydown"
          rows="5"
          max-rows="9"
          auto-grow
        >
        </v-textarea>
      </v-card-text>

      <v-card-actions>
        <v-progress-circular
          v-if="imageFileUploadingRef"
          :rotate="-90"
          :size="100"
          :width="15"
          :model-value="imageFileUploadValueRef"
          color="primary"
        >
          {{ imageFileUploadValueRef }}
        </v-progress-circular>
        <v-spacer/>
        <v-btn :disabled="imageFileUploadingRef || isLoadingRef" prepend-icon="mdi-send" color="primary" variant="flat" @click="img2Img">
          图生图
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
