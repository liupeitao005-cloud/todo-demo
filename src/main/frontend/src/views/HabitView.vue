<template>
  <section class="two-column">
    <form class="form-card" @submit.prevent="create">
      <h2>习惯管理</h2>
      <div class="form-row">
        <label>习惯标题<input v-model.trim="form.title" placeholder="阅读" /></label>
        <label>习惯内容<input v-model.trim="form.content" placeholder="每天阅读专业书" /></label>
      </div>
      <div class="form-row three">
        <label>每日总分钟<input v-model.number="form.dayMinutes" type="number" placeholder="60" /></label>
        <label>单次最少分钟<input v-model.number="form.minMinutes" type="number" placeholder="20" /></label>
        <label>单次最多分钟<input v-model.number="form.maxMinutes" type="number" placeholder="30" /></label>
      </div>
      <div class="actions">
        <button :disabled="loading" type="submit">创建习惯</button>
        <button class="secondary" type="button" @click="loadList">刷新列表</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>

    <aside class="result-panel">
      <h2>习惯列表</h2>
      <div v-if="items.length" class="list">
        <button v-for="item in items" :key="item.id" class="list-item" type="button" @click="pick(item)">
          <strong>#{{ item.id }} {{ item.title }}</strong>
          <span>{{ item.content || "无内容" }}</span>
          <small>每日 {{ item.dayMinutes || 0 }} 分钟，单次 {{ item.minMinutes || 0 }}-{{ item.maxMinutes || 0 }} 分钟</small>
        </button>
      </div>
      <p v-else class="empty">暂无习惯，创建后会自动显示在这里。</p>
    </aside>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { habitApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ title: "", content: "", dayMinutes: null, minMinutes: null, maxMinutes: null });
const items = ref([]);
const { loading, status, ok, run } = useRequest();

function pick(item) {
  form.title = item.title || "";
  form.content = item.content || "";
  form.dayMinutes = item.dayMinutes || null;
  form.minMinutes = item.minMinutes || null;
  form.maxMinutes = item.maxMinutes || null;
}

async function loadList(options = {}) {
  const data = options.silent ? await habitApi.list() : await run(() => habitApi.list(), "列表已刷新");
  items.value = Array.isArray(data?.data) ? data.data : [];
}

async function create() {
  if (!form.title) {
    status.value = "请填写习惯标题";
    ok.value = false;
    return;
  }
  const data = await run(() => habitApi.create({ ...form }));
  if (data?.code === 200) {
    await loadList({ silent: true });
    status.value = "创建成功";
    ok.value = true;
  }
}

onMounted(() => loadList({ silent: true }));
</script>
