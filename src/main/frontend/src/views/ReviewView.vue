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
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
    </form>
    <ResultPanel :result="result" />
  </section>
</template>

<script setup>
import { reactive, ref } from "vue";
import ResultPanel from "@/components/ResultPanel.vue";
import { reviewApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const form = reactive({ title: "", content: "" });
const planId = ref(null);
const { loading, status, ok, result, run } = useRequest();

function create() {
  if (!form.title) return (status.value = "请填写复习标题"), (ok.value = false);
  run(() => reviewApi.create({ ...form }));
}
function finish() {
  if (!planId.value) return (status.value = "请填写复习计划 ID"), (ok.value = false);
  run(() => reviewApi.finish(planId.value));
}
</script>
