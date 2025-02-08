<script setup lang="ts">
import createLasso from 'lasso-canvas-image';

const imageRef = ref<HTMLImageElement>(null);
const canvasRef = ref<any>(null);
const initRef = ref<boolean>(false);

watch(
  () => imageRef.value,
  (val) => {
    loadLasso();
  }
);

watch(
  () => canvasRef.value,
  (val) => {
    loadLasso();
  }
);

const loadLasso = () => {
  if (initRef.value) {
    return;
  }
  const canvas = canvasRef.value;
  const ctx = canvas.getContext("2d");
  ctx.fillStyle = "black"; // 设置填充颜色为黑色
  ctx.fillRect(0, 0, canvas.width, canvas.height); // 填充整个 canvas


  // Init
  const lasso = createLasso({
    element: imageRef.value,
    onChange(polygon) {
      console.log('Selection area changed: ' + polygon);
    },
    onUpdate(polygon) {
      console.log('Selection area updated: ' + polygon);

      // 将polygon字符串转为坐标数组
      const coordinates = polygon
        .split(" ")
        .map((point) => point.split(",").map(Number));

      ctx.clearRect(0, 0, canvas.width, canvas.height);
      ctx.fillStyle = "black";
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      ctx.beginPath();
      for (let i = 0; i < coordinates.length; i++) {
        const [x, y] = coordinates[i];
        i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
      }
      ctx.closePath();
      ctx.fillStyle = "white";
      ctx.fill();
      const base64Image = canvas.toDataURL("image/webp"); // 把 canvas 转为 base64 格式的图片
      console.log(base64Image.split(","));
    }
  });

  lasso.reset();

  initRef.value = true;
  console.log("init canvas success!")
};


</script>
<template>
  <img ref="imageRef" src="https://jiucai.fun/am/pWKEv7FkO7mF5sg11QBGEYUN.jpg" />
  <canvas ref=canvasRef style="display: none"/>
</template>
