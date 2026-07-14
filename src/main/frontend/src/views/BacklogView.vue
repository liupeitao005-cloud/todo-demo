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
        <button class="secondary" type="button" @click="loadList">刷新列表</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>

    <aside class="result-panel">
      <h2>待办列表</h2>
      <div v-if="items.length" class="list">
        <button v-for="item in items" :key="item.id" class="list-item" type="button" @click="pick(item)">
          <strong>#{{ item.id }} {{ item.title }}</strong>
          <span>{{ item.content || "无内容" }}</span>
          <small>{{ formatTime(item.createTime) }}</small>
        </button>
      </div>
      <p v-else class="empty">暂无待办，创建后会自动显示在这里。</p>
    </aside>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { backlogApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ id: null, title: "", content: "", startTime: "", finishTime: "" });
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
}

async function loadList(options = {}) {
  const data = options.silent ? await backlogApi.list() : await run(() => backlogApi.list(), "列表已刷新");
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
  status.value = "请填写待办 ID";
  ok.value = false;
  return false;
}

function create() {
  if (!form.title) {
    status.value = "请填写标题";
    ok.value = false;
    return;
  }
  mutate(() => backlogApi.create(payload()), "创建成功");
}

function update() {
  if (needId()) mutate(() => backlogApi.update(payload()), "修改成功");
}

function move() {
  if (needId()) mutate(() => backlogApi.move(payload()), "安排为任务成功");
}

onMounted(() => loadList({ silent: true }));
</script>
