<template>
  <section class="two-column">
    <form class="form-card" @submit.prevent="move">
      <h2>四象限管理</h2>
      <div class="form-row three">
        <label>任务 ID<input v-model.number="form.id" type="number" placeholder="任务 ID" /></label>
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
      <div class="actions">
        <button :disabled="loading" type="submit">放入四象限</button>
        <button class="secondary" type="button" @click="list">查询象限</button>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>
    <ResultPanel :result="result" />
  </section>
</template>

<script setup>
import { reactive } from "vue";
import ResultPanel from "@/components/ResultPanel.vue";
import { fourApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ id: null, importance: 1, urgency: 1 });
const { loading, status, ok, result, run } = useRequest();

function move() {
  if (!form.id) return (status.value = "请填写任务 ID"), (ok.value = false);
  run(() => fourApi.move({ ...form }));
}
function list() {
  run(() => fourApi.list({ ...form }));
}
</script>
