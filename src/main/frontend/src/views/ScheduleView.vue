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
        <button class="secondary" type="button" @click="loadList">刷新列表</button>
        <button class="danger" type="button" @click="remove">删除行程</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>

    <aside class="result-panel">
      <h2>行程列表</h2>
      <div v-if="items.length" class="list">
        <button v-for="item in items" :key="item.id" class="list-item" type="button" @click="pick(item)">
          <strong>#{{ item.id }} {{ item.title }}</strong>
          <span>{{ item.location || "无地点" }}</span>
          <small>
            {{ formatTime(item.startTime) || "未安排开始时间" }}
            <template v-if="item.finishTime"> - {{ formatTime(item.finishTime) }}</template>
          </small>
        </button>
      </div>
      <p v-else class="empty">暂无行程，创建后会自动显示在这里。</p>
    </aside>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { scheduleApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ id: null, title: "", content: "", location: "", startTime: "", finishTime: "" });
const items = ref([]);
const { loading, status, ok, run } = useRequest();
const payload = () => ({ ...form });

function formatTime(value) {
  return value ? String(value).replace("T", " ") : "";
}

function pick(item) {
  form.id = item.id;
  form.title = item.title || "";
  form.content = item.content || "";
  form.location = item.location || "";
  form.startTime = item.startTime || "";
  form.finishTime = item.finishTime || "";
}

async function loadList(options = {}) {
  const data = options.silent ? await scheduleApi.list() : await run(() => scheduleApi.list(), "列表已刷新");
  items.value = Array.isArray(data?.data) ? data.data : [];
}

async function mutate(action, successMessage) {
  const data = await run(action);
  if (data?.code === 200) {
    const operationMessage = successMessage || data?.message || "操作成功";
    await loadList({ silent: true });
    status.value = operationMessage;
    ok.value = true;
  }
}

function needId() {
  if (form.id) return true;
  status.value = "请填写行程 ID";
  ok.value = false;
  return false;
}

function create() {
  if (!form.title) {
    status.value = "请填写标题";
    ok.value = false;
    return;
  }
  mutate(() => scheduleApi.create(payload()), "创建成功");
}

function update() { if (needId()) mutate(() => scheduleApi.update(payload()), "修改成功"); }
function remove() { if (needId()) mutate(() => scheduleApi.remove(form.id), "删除成功"); }

onMounted(() => loadList({ silent: true }));
</script>
