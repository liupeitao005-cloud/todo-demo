<template>
  <section class="two-column">
    <form class="form-card" @submit.prevent="create">
      <h2>复习计划</h2>
      <div class="form-row">
        <label>复习标题<input v-model.trim="form.title" placeholder="英语单词 Unit 1" /></label>
        <label>复习计划 ID<input v-model.number="planId" type="number" placeholder="完成复习时填写" /></label>
      </div>
      <label>复习内容<textarea v-model.trim="form.content" placeholder="需要复习的内容"></textarea></label>
      <div class="actions">
        <button :disabled="loading" type="submit">创建复习任务</button>
        <button class="secondary" type="button" @click="finish">完成复习计划</button>
        <button class="secondary" type="button" @click="loadList">刷新列表</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>

    <aside class="result-panel">
      <h2>复习计划列表</h2>
      <div v-if="items.length" class="list">
        <button v-for="item in items" :key="item.id" class="list-item" type="button" @click="pick(item)">
          <strong>#{{ item.id }} 复习任务 {{ item.reviewTaskId }}</strong>
          <span>{{ formatTime(item.reviewTime) }}</span>
          <small>{{ item.isFinish ? `已完成 ${formatTime(item.finishTime)}` : "未完成" }}</small>
        </button>
      </div>
      <p v-else class="empty">暂无复习计划，创建后会自动显示在这里。</p>
    </aside>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { reviewApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ title: "", content: "" });
const planId = ref(null);
const items = ref([]);
const { loading, status, ok, run } = useRequest();

function formatTime(value) {
  return value ? String(value).replace("T", " ") : "";
}

function pick(item) {
  planId.value = item.id;
}

async function loadList(options = {}) {
  const data = options.silent ? await reviewApi.list() : await run(() => reviewApi.list(), "列表已刷新");
  items.value = Array.isArray(data?.data) ? data.data : [];
}

async function create() {
  if (!form.title) {
    status.value = "请填写复习标题";
    ok.value = false;
    return;
  }
  const data = await run(() => reviewApi.create({ ...form }));
  if (data?.code === 200) {
    await loadList({ silent: true });
    status.value = "创建复习任务成功";
    ok.value = true;
  }
}

async function finish() {
  if (!planId.value) {
    status.value = "请填写复习计划 ID";
    ok.value = false;
    return;
  }
  const data = await run(() => reviewApi.finish(planId.value));
  if (data?.code === 200) {
    await loadList({ silent: true });
    status.value = "复习完成成功";
    ok.value = true;
  }
}

onMounted(() => loadList({ silent: true }));
</script>
