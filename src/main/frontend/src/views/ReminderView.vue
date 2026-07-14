<template>
  <section class="two-column">
    <form class="form-card" @submit.prevent="create">
      <h2>提醒通知</h2>
      <div class="form-row three">
        <label>提醒对象
          <select v-model="form.targetType">
            <option value="task">任务</option>
            <option value="schedule">行程</option>
            <option value="review">复习</option>
          </select>
        </label>
        <label>对象 ID<input v-model.number="form.targetId" type="number" placeholder="任务/行程/复习计划 ID" /></label>
        <label>渠道
          <select v-model="form.channel">
            <option value="desktop">桌面通知</option>
            <option value="telegramBot">Telegram Bot</option>
            <option value="app">App</option>
          </select>
        </label>
      </div>
      <div class="form-row">
        <label>标题<input v-model.trim="form.title" placeholder="任务提醒" /></label>
        <label>提醒时间<input v-model="form.remindTime" type="datetime-local" /></label>
      </div>
      <label>内容<textarea v-model.trim="form.content" placeholder="提醒内容"></textarea></label>
      <div class="actions">
        <button :disabled="loading" type="submit">创建提醒</button>
        <button class="secondary" type="button" @click="requestNotify">开启通知权限</button>
        <button class="secondary" type="button" @click="check">检查到期提醒</button>
        <button class="secondary" type="button" @click="loadList">刷新列表</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>

    <aside class="result-panel">
      <h2>提醒列表</h2>
      <div v-if="items.length" class="list">
        <button v-for="item in items" :key="item.id" class="list-item" type="button" @click="pick(item)">
          <strong>#{{ item.id }} {{ item.title }}</strong>
          <span>{{ item.targetType }} #{{ item.targetId }} · {{ item.channel }}</span>
          <small>{{ formatTime(item.remindTime) }}</small>
          <span class="tag">{{ item.isSent ? "已提醒" : "未提醒" }}</span>
        </button>
      </div>
      <p v-else class="empty">暂无提醒，创建后会自动显示在这里。</p>
    </aside>
  </section>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from "vue";
import { reminderApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const POLL_INTERVAL_MS = 60000;
const form = reactive({ targetType: "task", targetId: null, title: "", content: "", remindTime: "", channel: "desktop" });
const items = ref([]);
const notifiedIds = new Set();
const { loading, status, ok, run } = useRequest();
let pollingTimer = null;
let checking = false;

function formatTime(value) {
  return value ? String(value).replace("T", " ") : "";
}

function pick(item) {
  form.targetType = item.targetType || "task";
  form.targetId = item.targetId || null;
  form.title = item.title || "";
  form.content = item.content || "";
  form.remindTime = item.remindTime || "";
  form.channel = item.channel || "desktop";
}

async function loadList(options = {}) {
  const data = options.silent ? await reminderApi.list() : await run(() => reminderApi.list(), "列表已刷新");
  items.value = Array.isArray(data?.data) ? data.data : [];
}

async function create() {
  if (!form.targetId || !form.title || !form.remindTime) {
    status.value = "请填写对象 ID、标题和提醒时间";
    ok.value = false;
    return;
  }
  const data = await run(() => reminderApi.create({ ...form }));
  if (data?.code === 200) {
    await loadList({ silent: true });
    status.value = "创建提醒成功";
    ok.value = true;
  }
}

async function requestNotify() {
  if (!("Notification" in window)) {
    status.value = "当前浏览器不支持桌面通知";
    ok.value = false;
    return;
  }
  const permission = Notification.permission === "granted" ? "granted" : await Notification.requestPermission();
  ok.value = permission === "granted";
  status.value = ok.value ? "桌面通知已开启" : "未获得桌面通知权限";
  if (ok.value) check();
}

async function check(options = {}) {
  if (checking) return;
  checking = true;
  const silent = options?.silent === true;
  let data = null;
  try {
    data = silent ? await reminderApi.pending() : await run(() => reminderApi.pending());
  } catch {
    return;
  } finally {
    checking = false;
  }
  if (!data) return;
  const reminders = Array.isArray(data?.data) ? data.data : [];
  if (!reminders.length) {
    if (!silent) status.value = "暂无到期提醒";
    ok.value = true;
    return;
  }
  if (!("Notification" in window)) {
    status.value = `有 ${reminders.length} 条到期提醒，但当前浏览器不支持桌面通知`;
    ok.value = false;
    return;
  }
  if (Notification.permission !== "granted") {
    status.value = `有 ${reminders.length} 条到期提醒，请先开启通知权限`;
    ok.value = false;
    return;
  }
  let handledCount = 0;
  for (const reminder of reminders) {
    if (!reminder.id || notifiedIds.has(reminder.id)) continue;
    notifiedIds.add(reminder.id);
    new Notification(reminder.title || "Todo 提醒", { body: reminder.content || "你有一条到期提醒" });
    await reminderApi.read(reminder.id);
    handledCount++;
  }
  const operationMessage = handledCount ? `已处理 ${handledCount} 条提醒` : "暂无新的到期提醒";
  status.value = operationMessage;
  ok.value = true;
  await loadList({ silent: true });
  status.value = operationMessage;
  ok.value = true;
}

onMounted(() => {
  loadList({ silent: true });
  check({ silent: true });
  pollingTimer = window.setInterval(() => check({ silent: true }), POLL_INTERVAL_MS);
});

onUnmounted(() => {
  if (pollingTimer) window.clearInterval(pollingTimer);
});
</script>
