<template>
  <section class="two-column">
    <form class="form-card" @submit.prevent="create">
      <h2>行程管理</h2>
      <label>行程 ID<input v-model.number="form.id" type="number" placeholder="修改或删除时填写" /></label>
      <div class="form-row">
        <label>标题<input v-model.trim="form.title" placeholder="项目答辩会议" /></label>
        <label>地点<input v-model.trim="form.location" placeholder="会议室 A" /></label>
      </div>
      <label>内容<textarea v-model.trim="form.content" placeholder="行程说明"></textarea></label>
      <div class="form-row">
        <label>开始时间<input v-model="form.startTime" type="datetime-local" /></label>
        <label>结束时间<input v-model="form.finishTime" type="datetime-local" /></label>
      </div>
      <div class="actions">
        <button :disabled="loading" type="submit">创建行程</button>
        <button class="secondary" type="button" @click="update">修改行程</button>
        <button class="danger" type="button" @click="remove">删除行程</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>
    <ResultPanel :result="result" />
  </section>
</template>

<script setup>
import { reactive } from "vue";
import ResultPanel from "@/components/ResultPanel.vue";
import { scheduleApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ id: null, title: "", content: "", location: "", startTime: "", finishTime: "" });
const { loading, status, ok, result, run } = useRequest();
const payload = () => ({ ...form });

function needId() {
  if (form.id) return true;
  status.value = "请填写行程 ID";
  ok.value = false;
  return false;
}
function create() {
  if (!form.title) return (status.value = "请填写标题"), (ok.value = false);
  run(() => scheduleApi.create(payload()));
}
function update() { if (needId()) run(() => scheduleApi.update(payload())); }
function remove() { if (needId()) run(() => scheduleApi.remove(form.id)); }
</script>
