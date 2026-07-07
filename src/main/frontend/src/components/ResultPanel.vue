<template>
  <aside class="result-panel">
    <div>
      <h2>接口返回</h2>
    </div>
    <pre>{{ formatted }}</pre>
  </aside>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  result: {
    type: [Object, String, null],
    default: null
  }
});

const formatted = computed(() => {
  if (!props.result) return "等待请求...";
  return typeof props.result === "string" ? props.result : JSON.stringify(props.result, null, 2);
});
</script>
