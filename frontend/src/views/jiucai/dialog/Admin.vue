<script setup lang="ts">
import 'md-editor-v3/lib/preview.css';
import {getFormatDate, getFormatMonth} from "@/utils/timeUtils";
import {useMyStore} from "@/stores/myStore";
import {useDisplay} from "vuetify";
import {useSnackbarStore} from "@/stores/snackbarStore";
import {useCustomizeThemeStore} from "@/stores/customizeTheme";
import {registerPacketReceiver, isWebsocketReady, send, asyncAsk} from "@/utils/websocket";
import AdminInfoRequest from "@/protocol/admin/AdminInfoRequest";
import AdminInfoResponse from "@/protocol/admin/AdminInfoResponse";
import Broadcast from "@/protocol/admin/Broadcast";
import Statistics from "@/protocol/admin/Statistics";
import DoBroadcastRequest from "@/protocol/admin/DoBroadcastRequest";
import DoBroadcastResponse from "@/protocol/admin/DoBroadcastResponse";
import DeleteBroadcastRequest from "@/protocol/admin/DeleteBroadcastRequest";
import DeleteBroadcastResponse from "@/protocol/admin/DeleteBroadcastResponse";
import Chart from 'chart.js/auto';

const {mobile, width, height} = useDisplay();
const myStore = useMyStore();
const customizeTheme = useCustomizeThemeStore();
const snackbarStore = useSnackbarStore();
import _ from "lodash";

const broadcastsRef = ref<Broadcast[]>([]);
const statisticsRef = ref<Statistics[]>([]);

watch(
  () => myStore.adminDialog,
  async (val) => {
    if (val) {
      const response: AdminInfoResponse = await asyncAsk(new AdminInfoRequest());
      broadcastsRef.value = response.broadcasts;
      statisticsRef.value = response.stats;

      const stats = response.stats.filter(it => it.active > 0).sort((a, b) => a.time - b.time);

      new Chart(document.getElementById('tcpActivateChart'), {
        data: {
          labels: stats.map(it => getFormatDate(it.time).toString()),
          datasets: [
            {
              type: 'line',
              label: 'tcp activate',
              data: stats.map(it => it.active)
            },
          ],
        },
      });
    }
  },
  {
    deep: true,
  }
);

async function doBroadcast(id: number, type: string) {
  const request = new DoBroadcastRequest();
  request.id = id;
  request.type = type;
  const answer: DoBroadcastResponse = await asyncAsk(request);
  broadcastsRef.value = answer.broadcasts;
  snackbarStore.showSuccessMessage("开始广播消息，请查收信息");
}

async function deleteBroadcast(id: number) {
  const request = new DeleteBroadcastRequest();
  request.id = id;
  const answer: DeleteBroadcastResponse = await asyncAsk(request);
  broadcastsRef.value = answer.broadcasts;
  snackbarStore.showSuccessMessage("删除了无用的广播");
}


</script>
<template>
  <v-dialog transition="dialog-top-transition" max-width="80%" v-model="myStore.adminDialog">
    <template v-slot:default="{ isActive }">
      <v-card prepend-icon="mdi-skull-crossbones-outline">
        <template v-slot:title>
          后台管理
        </template>

        <v-card-text>
          <v-list density="compact">
            <v-list-subheader>广播消息</v-list-subheader>
            <template v-for="broadcast in broadcastsRef" :key="broadcast.id">
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-bullhorn-variant-outline"></v-icon>
                </template>
                <v-list-item-title>
                  {{ broadcast.content }}
                </v-list-item-title>
                <v-list-item-subtitle>微信->{{ broadcast.weChatResult }}</v-list-item-subtitle>
                <v-list-item-subtitle>短信->{{ broadcast.smsResult }}</v-list-item-subtitle>
                <v-list-item-action>
                  <v-btn icon="mdi-delete-alert-outline" size="x-small" class="mx-1" color="error" @click="deleteBroadcast(broadcast.id)" />
                  <v-btn icon="mdi-wechat" color="primary" size="x-small" class="mx-1" @click="doBroadcast(broadcast.id, 'wechat')" />
                  <v-btn icon="mdi-cellphone-nfc" color="primary" size="x-small" class="mx-1" @click="doBroadcast(broadcast.id, 'sms')" />
                </v-list-item-action>
              </v-list-item>
              <hr>
            </template>
          </v-list>
        </v-card-text>

        <v-card-text>
          <canvas id="tcpActivateChart"></canvas>
        </v-card-text>

        <v-card-text>
          <v-table>
            <thead>
            <tr>
              <th>
                时间
              </th>
              <th>
                ips
              </th>
              <th>
                tcp active
              </th>
              <th>
                chatgpt
              </th>
              <th>
                google
              </th>
              <th>
                bing
              </th>
              <th>
                微信
              </th>
              <th>
                bilibili
              </th>
              <th>
                midjourney
              </th>
              <th>
                导航
              </th>
              <th>
                stable diffusion
              </th>
              <th>
                animation
              </th>
              <th>
                搜索
              </th>
              <th>
                新闻总数
              </th>
              <th>
                S级
              </th>
              <th>
                A级
              </th>
              <th>
                B级
              </th>
              <th>
                C级
              </th>
              <th>
                D级
              </th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="stat in statisticsRef" :key="stat.id">
              <td>{{ getFormatDate(stat.time) }}</td>
              <td>{{ stat.ips }}</td>
              <td>{{ stat.active }}</td>
              <td>{{ stat.chatgptRequest }}</td>
              <td>{{ stat.googleSearch }}</td>
              <td>{{ stat.bingSearch }}</td>
              <td>{{ stat.weixinSearch }}</td>
              <td>{{ stat.bilibiliSearch }}</td>
              <td>{{ stat.midImagineRequest }}</td>
              <td>{{ stat.navigation }}</td>
              <td>{{ stat.sdSimulateRequest }}</td>
              <td>{{ stat.animationRequest }}</td>
              <td>{{ stat.newsSearchRequest }}</td>
              <td>{{ stat.newsStat.newsS + stat.newsStat.newsA + stat.newsStat.newsB + stat.newsStat.newsC + stat.newsStat.newsD }}</td>
              <td>{{ stat.newsStat.newsS }}</td>
              <td>{{ stat.newsStat.newsA }}</td>
              <td>{{ stat.newsStat.newsB }}</td>
              <td>{{ stat.newsStat.newsC }}</td>
              <td>{{ stat.newsStat.newsD }}</td>
            </tr>
            </tbody>
          </v-table>
        </v-card-text>
      </v-card>
    </template>
  </v-dialog>
</template>
