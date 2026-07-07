<template>
  <section class="two-column">
    <form class="form-card" @submit.prevent="create">
      <h2>待办箱</h2>
      <label>待办 ID<input v-model.number="form.id" type="number" placeholder="修改或安排时填写" /></label>
      <div class="form-row">
        <label>标题<input v-model.trim="form.title" placeholder="整理项目资料" /></label>
        <label>内容<input v-model.trim="form.content" placeholder="暂时没有明确时间的事项" /></label>
      </div>
      <div class="form-row">
        <label>开始时间<input v-model="form.startTime" type="datetime-local" /></label>
        <label>结束时间<input v-model="form.finishTime" type="datetime-local" /></label>
      </div>
      <div class="actions">
        <button :disabled="loading" type="submit">创建待办</button>
        <button class="secondary" type="button" @click="update">修改待办</button>
        <button class="secondary" type="button" @click="move">安排为任务</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>
    <ResultPanel :result="result" />
  </section>
</template>

<script setup>
import { reactive } from "vue";
import ResultPanel from "@/components/ResultPanel.vue";
import { backlogApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ id: null, title: "", content: "", startTime: "", finishTime: "" });
const { loading, status, ok, result, run } = useRequest();
const payload = () => ({ ...form });

function needId() {
  if (form.id) return true;
  status.value = "请填写待办 ID";
  ok.value = false;
  return false;
}
function create() {
  if (!form.title) return (status.value = "请填写标题"), (ok.value = false);
  run(() => backlogApi.create(payload()));
}
function update() { if (needId()) run(() => backlogApi.update(payload())); }
function move() { if (needId()) run(() => backlogApi.move(payload())); }
</script>
