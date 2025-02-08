import ByteBuffer from '@/protocol/buffer/ByteBuffer';
import SignalAttachment from '@/protocol/attachment/SignalAttachment';
import ProtocolManager from '@/protocol/ProtocolManager';
import Error from '@/protocol/common/Error';
import Message from '@/protocol/common/Message';
import Ping from '@/protocol/common/Ping';
import Pong from '@/protocol/common/Pong';
import LoginRequest from '@/protocol/auth/LoginRequest';
import LoginResponse from '@/protocol/auth/LoginResponse';
import GroupChatNotice from '@/protocol/chat/GroupChatNotice';


import {useSnackbarStore} from "@/stores/snackbarStore";
import {useNewsStore} from "@/stores/newsStore";
import {useMyStore} from "@/stores/myStore";
import _ from "lodash";


const wsUrl: string = import.meta.env.VITE_API_BASE_URL;
let pingTime: number = 0;
let ws: WebSocket = connect("init websocket");
let uuid: number = 0;

const signalAttachmentMap: Map<number, EncodedPacketInfo> = new Map<number, EncodedPacketInfo>();

setInterval(() => reconnect(), 30 * 1000);

// 如果服务器长时间没有回应，则重新连接
function reconnect() {
  if (new Date().getTime() - pingTime < 3 * 60 * 1000) {
    // 每30秒发送一次心跳包
    send(new Ping())
    return;
  }
  const snackbarStore = useSnackbarStore();
  snackbarStore.showInfoMessage("正在连接服务器");
  ws.close(3999);
  ws = connect("timeout and reconnect");
}

// readyState的状态码定义
// 0 (CONNECTING)，正在链接中
// 1 (OPEN)，已经链接并且可以通讯
// 2 (CLOSING)，连接正在关闭
// 3 (CLOSED)，连接已关闭或者没有链接成功
function connect(desc): WebSocket {
  console.log(new Date(), 'start connect websocket: ' + desc);

  const webSocket = new WebSocket(wsUrl);

  webSocket.binaryType = 'arraybuffer';

  webSocket.onopen = async function () {
    const snackbarStore = useSnackbarStore();
    const newsStore = useNewsStore();
    const myStore = useMyStore();

    console.log(new Date(), 'websocket open success');

    // websocket连接成功过后，先发送ping同步服务器时间，再发送登录请求
    send(new Ping());

    pingTime = new Date().getTime();
    snackbarStore.showSuccessMessage("连接服务器成功");
    newsStore.online = true;

    // 登录
    const loginRequest = new LoginRequest();
    loginRequest.newsId = newsStore.getMaxNewsId();
    loginRequest.chatMessageId = newsStore.chatMessageId;
    loginRequest.token = myStore.token;

    const loginResponse: LoginResponse = await asyncAsk(loginRequest);
    newsStore.ip = loginResponse.ip;
    newsStore.ipLong = loginResponse.ipLong;
    newsStore.region = loginResponse.region;
    newsStore.sid = loginResponse.sid;
    newsStore.activeUid = loginResponse.activeUid;
    newsStore.chatMessageIdDiff = loginResponse.chatMessageIdDiff;
    myStore.updateUser(loginResponse.user)
  };


  webSocket.onmessage = function (event) {
    const snackbarStore = useSnackbarStore();
    const data = event.data;

    const buffer = new ByteBuffer();
    buffer.writeBytes(data as ArrayBuffer);
    buffer.setReadOffset(4);
    const packet = ProtocolManager.read(buffer);

    if (packet.constructor == Error) {
      snackbarStore.showErrorMessage(packet.message);
      return;
    }

    if (packet.constructor == Message) {
      if (packet.code == 0) {
        snackbarStore.showErrorMessage(packet.message);
      } else if (packet.code == 1) {
        snackbarStore.showSuccessMessage(packet.message);
      } else if (packet.code == 2) {
        snackbarStore.showInfoMessage(packet.message);
      } else if (packet.code == 3) {
        snackbarStore.showWarningMessage(packet.message);
      } else {
        snackbarStore.showInfoMessage(packet.message);
      }
      return;
    }

    let attachment: any = null;
    if (buffer.isReadable() && buffer.readBool()) {
      if (process.env.NODE_ENV === "development") {
        console.log(new Date(), "Websocket收到异步response <-- ", packet);
      }
      attachment = ProtocolManager.read(buffer);
      const encodedPacketInfo = signalAttachmentMap.get(attachment.signalId);
      if (encodedPacketInfo == undefined) {
        throw "可能消息超时找不到对应的SignalAttachment:" + attachment;
      }
      encodedPacketInfo.promiseResolve(packet);
      return;
    }
    if (process.env.NODE_ENV === "development") {
      console.log(new Date(), "Websocket收到同步response <-- ", packet);
    }
    if (packet.constructor == Pong) {
      if (Number.isInteger(packet.time)) {
        pingTime = packet.time;
      } else {
        pingTime = Number.parseInt(packet.time);
      }
      return;
    }

    route(packet);
  };

  webSocket.onerror = function (event) {
    console.log(new Date(), 'websocket error', event);
    const newsStore = useNewsStore();
    newsStore.online = false;
  };

  webSocket.onclose = function (event) {
    console.log(new Date(), 'websocket close', event);
    const newsStore = useNewsStore();
    newsStore.online = false;
  };
  return webSocket;
}

export function isWebsocketReady(): boolean {
  return ws.readyState == 1;
}

export function send(packet: any, attachment: any = null) {
  const snackbarStore = useSnackbarStore();
  switch (ws.readyState) {
    case 0:
      console.log(new Date(), "0, ws connecting server");
      snackbarStore.showWarningMessage("正在连接服务器");
      break;
    case 1:
      const buffer = new ByteBuffer();
      buffer.setWriteOffset(4);
      ProtocolManager.write(buffer, packet);
      if (attachment == null) {
        buffer.writeBool(false);
        if (process.env.NODE_ENV === "development") {
          console.log(new Date(), "Websocket发送同步request --> ", packet)
        }
      } else {
        buffer.writeBool(true);
        ProtocolManager.write(buffer, attachment)
        if (process.env.NODE_ENV === "development") {
          console.log(new Date(), "Websocket发送异步request --> ", packet)
        }
      }
      const writeOffset = buffer.writeOffset;
      buffer.setWriteOffset(0);
      buffer.writeRawInt(writeOffset - 4);
      buffer.setWriteOffset(writeOffset);
      ws.send(buffer.buffer);
      break;
    case 2:
      pingTime = pingTime - 60 * 1000;
      console.log(new Date(), "2, ws is closing, trying to reconnect");
      break;
    case 3:
      pingTime = pingTime - 60 * 1000;
      console.log(new Date(), "3, ws is closing, trying to reconnect");
      break;
    default:
      console.log(new Date(), "4, server error");
      snackbarStore.showErrorMessage("server error");
  }
}

class EncodedPacketInfo {
  promiseResolve: any;
  promiseReject: any;
  attachment: SignalAttachment | null = null;
}

export async function asyncAsk(packet: any): Promise<any> {
  const currentTime = new Date().getTime();
  const attachment: SignalAttachment = new SignalAttachment();
  uuid++;
  const signalId = uuid;
  attachment.timestamp = currentTime;
  attachment.signalId = signalId;
  attachment.taskExecutorHash = -1;
  attachment.client = 12;
  const encodedPacketInfo = new EncodedPacketInfo();
  encodedPacketInfo.attachment = attachment;
  const promise = new Promise((resolve, reject) => {
    encodedPacketInfo.promiseResolve = resolve;
    encodedPacketInfo.promiseReject = reject;
  });
  // 遍历删除旧的attachment
  const deleteList = new Array<number>();
  signalAttachmentMap.forEach((value, key) => {
    if (value == null || value.attachment == null) {
      deleteList.push(key);
    }
    const att = value.attachment;
    if (att == null) {
      deleteList.push(key);
    }
    const time = att == null ? 0 : att.timestamp;
    if (currentTime - time > 60000) {
      deleteList.push(key);
    }
  });
  deleteList.forEach(it => signalAttachmentMap.delete(it));
  signalAttachmentMap.set(signalId, encodedPacketInfo);
  send(packet, attachment);
  return promise;
}

const receiverMap = new Map<any, any>();

export function registerPacketReceiver(protocol: any, fun: any) {
  receiverMap.set(protocol, fun);
}

function route(packet: any) {
  const receiver = receiverMap.get(packet.constructor);
  if (packet.constructor == GroupChatNotice) {
    const newsStore = useNewsStore();
    newsStore.chatMessageIdDiff = _.first(packet.messages).id - newsStore.chatMessageId;
    console.log(newsStore.chatMessageIdDiff);
  }
  if (receiver == null) {
    console.log("router not exist ", packet);
    return;
  }
  receiver(packet);
}
