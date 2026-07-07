<template>
  <section class="two-column">
    <form class="form-card" @submit.prevent="create">
      <h2>任务管理</h2>
      <label>任务 ID<input v-model.number="form.id" type="number" placeholder="修改、删除、完成等操作时填写" /></label>
      <div class="form-row">
        <label>标题<input v-model.trim="form.title" placeholder="完成 Vue 前端页面" /></label>
        <label>任务类型<input v-model.trim="form.taskType" placeholder="normal / long / once" /></label>
      </div>
      <label>内容<textarea v-model.trim="form.content" placeholder="任务说明"></textarea></label>
      <div class="form-row three">
        <label>父任务 ID<input v-model.number="form.parentId" type="number" /></label>
        <label>开始时间<input v-model="form.startTime" type="datetime-local" /></label>
        <label>结束时间<input v-model="form.finishTime" type="datetime-local" /></label>
      </div>
      <div class="actions">
        <button :disabled="loading" type="submit">创建</button>
        <button class="secondary" type="button" @click="update">修改</button>
        <button class="secondary" type="button" @click="finish">完成</button>
        <button class="secondary" type="button" @click="split">拆分</button>
        <button class="secondary" type="button" @click="delay">延期</button>
        <button class="secondary" type="button" @click="next">设为下一个</button>
        <button class="danger" type="button" @click="remove">删除</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>
    <ResultPanel :result="result" />
  </section>
</template>

<script setup>
import { reactive } from "vue";
import ResultPanel from "@/components/ResultPanel.vue";
import { taskApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ id: null, title: "", content: "", taskType: "", parentId: null, startTime: "", finishTime: "" });
const { loading, status, ok, result, run } = useRequest();
const payload = () => ({ ...form });

function needId() {
  if (form.id) return true;
  status.value = "请填写任务 ID";
  ok.value = false;
  return false;
}
function create() {
  if (!form.title) return (status.value = "请填写标题"), (ok.value = false);
  run(() => taskApi.create(payload()));
}
function update() { if (needId()) run(() => taskApi.update(payload())); }
function finish() { if (needId()) run(() => taskApi.finish(form.id)); }
function split() { if (needId()) run(() => taskApi.split(form.id)); }
function delay() { if (needId()) run(() => taskApi.delay(form.id)); }
function next() { if (needId()) run(() => taskApi.next(form.id)); }
function remove() { if (needId()) run(() => taskApi.remove(form.id)); }
</script>
