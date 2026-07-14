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
        <button class="secondary" type="button" @click="next">设为下一步</button>
        <button class="secondary" type="button" @click="loadList">刷新列表</button>
        <button class="danger" type="button" @click="remove">删除</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>

    <aside class="result-panel">
      <h2>任务列表</h2>
      <div v-if="items.length" class="list">
        <button v-for="item in items" :key="item.id" class="list-item" type="button" @click="pick(item)">
          <strong>#{{ item.id }} {{ item.title }}</strong>
          <span>{{ item.content || "无内容" }}</span>
          <small>
            {{ formatTime(item.startTime) || "未安排开始时间" }}
            <template v-if="item.finishTime"> - {{ formatTime(item.finishTime) }}</template>
          </small>
          <span class="tag">{{ item.isFinish ? "已完成" : "未完成" }}</span>
        </button>
      </div>
      <p v-else class="empty">暂无任务，创建后会自动显示在这里。</p>
    </aside>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { taskApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ id: null, title: "", content: "", taskType: "", parentId: null, startTime: "", finishTime: "" });
const items = ref([]);
const { loading, status, ok, run } = useRequest();
const payload = () => ({ ...form });

function formatTime(value) {
  return value ? String(value).replace("T", " ") : "";
}

function newestFirst(list) {
  return [...list].sort((a, b) => Number(b.id || 0) - Number(a.id || 0));
}

function pick(item) {
  if (item.id === "new") return;
  form.id = item.id;
  form.title = item.title || "";
  form.content = item.content || "";
  form.taskType = item.taskType || "";
  form.parentId = item.parentId || null;
  form.startTime = item.startTime || "";
  form.finishTime = item.finishTime || "";
}

function showDraftCreatedItem() {
  items.value = [{
    id: "new",
    title: form.title,
    content: form.content,
    taskType: form.taskType,
    parentId: form.parentId,
    startTime: form.startTime,
    finishTime: form.finishTime,
    isFinish: 0
  }, ...items.value.filter((item) => item.id !== "new")];
}

async function refreshList() {
  const data = await taskApi.list();
  items.value = Array.isArray(data?.data) ? newestFirst(data.data) : [];
  return data;
}

async function loadList() {
  const data = await run(() => taskApi.list(), "列表已刷新");
  items.value = Array.isArray(data?.data) ? newestFirst(data.data) : [];
}

async function runOperation(action, successMessage, options = {}) {
  const data = await run(action);
  if (data?.code !== 200) return;
  if (options.showDraftFirst) showDraftCreatedItem();
  try {
    await refreshList();
  } catch {
    // 操作已经成功，列表刷新失败时不要把成功状态覆盖掉。
  }
  status.value = successMessage || data.message || "操作成功";
  ok.value = true;
}

function needId() {
  if (form.id) return true;
  status.value = "请填写任务 ID";
  ok.value = false;
  return false;
}

function create() {
  if (!form.title) {
    status.value = "请填写标题";
    ok.value = false;
    return;
  }
  runOperation(() => taskApi.create(payload()), "创建成功", { showDraftFirst: true });
}

function update() { if (needId()) runOperation(() => taskApi.update(payload()), "修改成功"); }
function finish() { if (needId()) runOperation(() => taskApi.finish(form.id), "完成成功"); }
function split() { if (needId()) runOperation(() => taskApi.split(form.id), "拆分成功"); }
function delay() { if (needId()) runOperation(() => taskApi.delay(form.id), "延期成功"); }
function next() { if (needId()) runOperation(() => taskApi.next(form.id), "设置成功"); }
function remove() { if (needId()) runOperation(() => taskApi.remove(form.id), "删除成功"); }

onMounted(() => {
  refreshList().catch(() => {});
});
</script>
