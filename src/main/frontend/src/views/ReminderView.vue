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
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>
    <ResultPanel :result="result" />
  </section>
</template>

<script setup>
import { reactive } from "vue";
import ResultPanel from "@/components/ResultPanel.vue";
import { reminderApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ targetType: "task", targetId: null, title: "", content: "", remindTime: "", channel: "desktop" });
const notifiedIds = new Set();
const { loading, status, ok, result, run } = useRequest();

function create() {
  if (!form.targetId || !form.title || !form.remindTime) return (status.value = "请填写对象 ID、标题和提醒时间"), (ok.value = false);
  run(() => reminderApi.create({ ...form }));
}
async function requestNotify() {
  if (!("Notification" in window)) return (status.value = "当前浏览器不支持桌面通知"), (ok.value = false);
  const permission = Notification.permission === "granted" ? "granted" : await Notification.requestPermission();
  ok.value = permission === "granted";
  status.value = ok.value ? "桌面通知已开启" : "未获得桌面通知权限";
}
async function check() {
  const data = await run(() => reminderApi.pending());
  const reminders = Array.isArray(data?.data) ? data.data : [];
  if (!reminders.length) return (status.value = "暂无到期提醒"), (ok.value = true);
  for (const reminder of reminders) {
    if (!reminder.id || notifiedIds.has(reminder.id)) continue;
    notifiedIds.add(reminder.id);
    if ("Notification" in window && Notification.permission === "granted") {
      new Notification(reminder.title || "Todo 提醒", { body: reminder.content || "你有一条到期提醒" });
    }
    await reminderApi.read(reminder.id);
  }
  status.value = `已处理 ${reminders.length} 条提醒`;
}
</script>
