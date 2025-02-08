<script setup lang="ts">
import {useSnackbarStore} from "@/stores/snackbarStore";
import {useChatStore} from "@/views/app/chat/chatStore";
import {Icon} from "@iconify/vue";
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import AnimationSquare1 from "@/animation/AnimationSquare1.vue";
import AnimationSquare2 from "@/animation/AnimationSquare2.vue";
import GroupChatRequest from "@/protocol/chat/GroupChatRequest";
import GroupHistoryMessageRequest from "@/protocol/chat/GroupHistoryMessageRequest";
import GroupHistoryMessageResponse from "@/protocol/chat/GroupHistoryMessageResponse";
import {asyncAsk, isWebsocketReady, registerPacketReceiver, send} from "@/utils/websocket";
import GroupChatNotice from "@/protocol/chat/GroupChatNotice";
import ChatMessage from "@/protocol/chat/ChatMessage";
import {avatarAutoUrl, useNewsStore} from "@/stores/newsStore";
import {parseTime} from "@/utils/timeUtils";
import {useDisplay} from "vuetify";
import _ from "lodash";
import {isBlank} from "@/utils/stringUtils";
import axios from "axios";

const snackbarStore = useSnackbarStore();
const chatStore = useChatStore();
const route = useRoute();

const {mobile, height, width} = useDisplay();
const newsStore = useNewsStore();
onMounted(() => {
  registerPacketReceiver(GroupChatNotice, atGroupChatNotice);
  initHistory();
});


interface Message {
  id: number;
  type: number;
  sendId: number;
  region: string;
  message: string;
  timestamp: number;

  avatar: string;
  content: string;
}

// Message List
const messages = ref<Message[]>([]);

// User Input Message
const userMessage = ref("");

const isLoading = ref(false);

const onlineUsersRef = ref(0);
const dialogRef = ref<boolean>(false);
// Send Messsage
const sendMessage = async () => {
  // Clear the input
  const message = userMessage.value;
  if (isBlank(message)) {
    snackbarStore.showErrorMessage("prompt不能为空");
    return
  }

  // 自己的韭菜广场的发送
  isLoading.value = true;

  const request = new GroupChatRequest();
  try {
    const mdMessage = await convertMessage2Markdown(message);
    request.message = mdMessage;
  } catch (error) {
    request.message = message;
  }
  send(request);

  userMessage.value = "";
};

// 下面的逻辑都是自己的
const atGroupChatNotice = (packet: GroupChatNotice) => {
  isLoading.value = false;
  updateMessage(packet.messages);
  scrollToBottom();
  if (_.isEqual(route.path, "/square")) {
    refreshMessageNotification();
    return;
  }
};

function refreshMessageNotification() {
  const maxMessage = _.maxBy(messages.value, it => it.id);
  if (!_.isNil(maxMessage)) {
    newsStore.chatMessageId = maxMessage.id;
  }
  newsStore.chatMessageIdDiff = 0;
}

function toMessage(chatMessage: ChatMessage): Message {
  return {
    id: chatMessage.id,
    type: chatMessage.type,
    sendId: chatMessage.sendId,
    region: chatMessage.region,
    message: chatMessage.message,
    timestamp: chatMessage.timestamp,
    avatar: avatarAutoUrl(chatMessage.sendId),
    content: chatMessage.message,
  };
}

const updateMessage = (chatMessages: Array<ChatMessage>) => {
  if (_.isEmpty(chatMessages)) {
    return;
  }
  for (const chatMessage of chatMessages) {
    if (_.findIndex(messages.value, it => it.id == chatMessage.id) >= 0) {
      continue;
    }
    messages.value.push(toMessage(chatMessage));
  }
}

async function initHistory() {
  setTimeout(() => doInitHistory(), 1000);
}

async function doInitHistory() {
  if (!isWebsocketReady()) {
    initHistory();
    return;
  }
  const firstMessage = _.first(messages.value);
  const firstMessageId = _.isNil(firstMessage) ? 0 : firstMessage.id;
  const request = new GroupHistoryMessageRequest();
  request.lastMessageId = firstMessageId;
  const response: GroupHistoryMessageResponse = await asyncAsk(request);
  updateMessage(response.messages);
  onlineUsersRef.value = response.onlineUsers;
  snackbarStore.showSuccessMessage("聊天记录加载成功");
  refreshMessageNotification();
  setTimeout(() => scrollToBottom(), 500);
}

async function moreHistory() {
  const firstMessage = _.first(messages.value);
  const firstMessageId = _.isNil(firstMessage) ? 0 : firstMessage.id;
  const request = new GroupHistoryMessageRequest();
  request.lastMessageId = firstMessageId;
  const response: GroupHistoryMessageResponse = await asyncAsk(request);
  const chatMessages = response.messages;
  if (_.isEmpty(chatMessages)) {
    return;
  }
  messages.value = _.concat(chatMessages.map(it => toMessage(it)), messages.value);
  snackbarStore.showSuccessMessage("加载成功");
}

async function convertMessage2Markdown(message: string) {
  if (isBlank(message)) {
    return message;
  }
  message = _.trim(message);
  if (!(message.startsWith("http://") || message.startsWith("https://"))) {
    return message;
  }
  // 正则匹配常见媒体类型的文件扩展名
  const pattern = /\.(jpg|jpeg|png|gif|mp4|avi|mov)$/i;
  if (pattern.test(message)) {
    return toMediaMarkdown(message);
  }

  const response = await axios.get(message);
  if (_.isNil(response) || _.isNil(response.headers)) {
    return message;
  }
  const contentType = response.headers.get('content-type');
  if (_.isNil(contentType)) {
    return message;
  }
  if (contentType.includes('image/') || contentType.includes('video/')) {
    return toMediaMarkdown(message);
  }
  return message;
}

// 当发现是url链接，则直接转换为markdown格式文档
function toMediaMarkdown(message: string) {
  const imageUrlMd = `![${message}](${message})`;
  return imageUrlMd;
}

// Scroll to the bottom of the message container
const scrollToBottom = () => {
  setTimeout(() => {
    window.scrollTo({top: 999999, behavior: "smooth"});
  }, 100);
};

const handleKeydown = (e) => {
  if (e.key === "Enter" && (e.altKey || e.shiftKey)) {
    // 当同时按下 alt或者shift 和 enter 时，插入一个换行符
    e.preventDefault();
    userMessage.value += "\n";
  } else if (e.key === "Enter") {
    // 当只按下 enter 时，发送消息
    e.preventDefault();
    sendMessage();
  }
};
</script>

<template>
  <v-container v-if="messages.length <= 0">
    <v-progress-linear indeterminate color="primary"></v-progress-linear>
    <v-row justify="center" align="center">
      <v-col cols="12">
        <AnimationSquare1 :size="mobile ? width * 0.8 : height * 0.6"/>
      </v-col>
    </v-row>
  </v-container>
  <v-container v-else>
    <v-row>
      <v-col v-ripple @click="moreHistory()">
        <div>
          <div class="text-h4 text-md-h4 text-center text-blue-lighten-1 font-weight-bold">
            加载更多聊天记录
          </div>
          <AnimationSquare2 :size="200"/>
        </div>
      </v-col>
    </v-row>

    <template v-for="message in messages">
      <v-row>
        <v-avatar class="ml-3 mb-1" rounded="sm" variant="elevated">
          <img :src="message.avatar" alt="alt"/>
        </v-avatar>
        <v-card class="mx-3">
          <md-preview v-model="message.content" editor-id="preview-only"/>
        </v-card>
      </v-row>
      <v-row justify="center">
        <v-col class="ma-0 pa-0">
          <div class="ma-0 pa-0 text-center text-caption font-weight-thin">
            {{ parseTime(message.timestamp) }} {{ message.region }}
          </div>
        </v-col>
      </v-row>
    </template>
    <v-row justify="center">
      <v-col class="ma-0 pa-0">
        <div class="ma-0 pa-0 text-center text-caption font-weight-thin">
          online user {{ onlineUsersRef }}
        </div>
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
        placeholder="Send Message"
        hide-details
        @keydown="handleKeydown"
        rows="1"
        max-rows="9"
        :autofocus="!mobile"
        auto-grow
      >
<!--        <template #prepend-inner>-->
<!--          <v-icon color="primary">mdi-file-gif-box</v-icon>-->
<!--        </template>-->
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
            v-model="userMessage"
            placeholder="Send Message"
            hide-details
            @keydown="handleKeydown"
            rows="1"
            max-rows="9"
            :autofocus="!mobile"
            auto-grow
          >
<!--            <template #prepend-inner>-->
<!--              <v-icon color="primary">mdi-file-gif-box</v-icon>-->
<!--            </template>-->
            <template v-slot:append-inner>
              <v-fade-transition leave-absolute>
                <Icon
                  v-if="isLoading"
                  class="text-primary"
                  width="30"
                  icon="eos-icons:three-dots-loading"
                />
                <v-icon color="primary" v-else @click="sendMessage">mdi-send</v-icon>
              </v-fade-transition>
            </template>
          </v-textarea>
        </v-col>
      </v-row>
    </v-container>
  </v-footer>

</template>
