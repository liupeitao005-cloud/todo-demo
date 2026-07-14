<template>
  <section class="two-column">
    <form class="form-card" @submit.prevent="move">
      <h2>四象限管理</h2>
      <div class="form-row three">
        <label>任务 ID<input v-model.number="form.id" type="number" placeholder="放入四象限时填写任务 ID" /></label>
        <label>重要性
          <select v-model.number="form.importance">
            <option :value="1">重要</option>
            <option :value="0">不重要</option>
          </select>
        </label>
        <label>紧急性
          <select v-model.number="form.urgency">
            <option :value="1">紧急</option>
            <option :value="0">不紧急</option>
          </select>
        </label>
      </div>
      <label>四象限记录 ID<input v-model.number="selectedFourId" type="number" placeholder="点击右侧列表自动填入" /></label>
      <div class="actions">
        <button :disabled="loading" type="submit">放入四象限</button>
        <button class="secondary" type="button" @click="loadList">查询象限</button>
        <button class="danger" type="button" @click="remove">移出四象限</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>

    <aside class="result-panel">
      <h2>{{ quadrantName }} 列表</h2>
      <div v-if="items.length" class="list">
        <button v-for="item in items" :key="item.id" class="list-item" type="button" @click="pick(item)">
          <strong>#{{ item.id }} {{ item.title }}</strong>
          <span>{{ item.content || "无内容" }}</span>
          <small>{{ formatTime(item.startTime) || "未安排开始时间" }}</small>
        </button>
      </div>
      <p v-else class="empty">当前象限暂无任务。</p>
    </aside>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { fourApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ id: null, importance: 1, urgency: 1 });
const selectedFourId = ref(null);
const items = ref([]);
const { loading, status, ok, run } = useRequest();

const quadrantName = computed(() => {
  if (form.importance === 1 && form.urgency === 1) return "重要且紧急";
  if (form.importance === 1 && form.urgency === 0) return "重要不紧急";
  if (form.importance === 0 && form.urgency === 1) return "不重要但紧急";
  return "不重要不紧急";
});

function formatTime(value) {
  return value ? String(value).replace("T", " ") : "";
}

function pick(item) {
  selectedFourId.value = item.id;
}

async function refreshList() {
  const data = await fourApi.list({ importance: form.importance, urgency: form.urgency });
  items.value = Array.isArray(data?.data) ? data.data : [];
}

async function loadList() {
  const data = await run(() => fourApi.list({ importance: form.importance, urgency: form.urgency }), "列表已刷新");
  items.value = Array.isArray(data?.data) ? data.data : [];
}

async function move() {
  if (!form.id) {
    status.value = "请填写任务 ID";
    ok.value = false;
    return;
  }
  const data = await run(() => fourApi.move({ ...form }));
  if (data?.code === 200) {
    await refreshList().catch(() => {});
    status.value = "放入四象限成功";
    ok.value = true;
  }
}

async function remove() {
  if (!selectedFourId.value) {
    status.value = "请先点击右侧列表选择要移出的四象限记录";
    ok.value = false;
    return;
  }
  const data = await run(() => fourApi.remove(selectedFourId.value));
  if (data?.code === 200) {
    selectedFourId.value = null;
    await refreshList().catch(() => {});
    status.value = "移出四象限成功";
    ok.value = true;
  }
}

onMounted(() => {
  refreshList().catch(() => {});
});
</script>
