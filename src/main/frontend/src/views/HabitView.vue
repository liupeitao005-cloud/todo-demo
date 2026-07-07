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
      <div class="actions"><button :disabled="loading" type="submit">创建习惯</button></div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>
    <ResultPanel :result="result" />
  </section>
</template>

<script setup>
import { reactive } from "vue";
import ResultPanel from "@/components/ResultPanel.vue";
import { habitApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ title: "", content: "", dayMinutes: null, minMinutes: null, maxMinutes: null });
const { loading, status, ok, result, run } = useRequest();

function create() {
  if (!form.title) return (status.value = "请填写习惯标题"), (ok.value = false);
  run(() => habitApi.create({ ...form }));
}
</script>
