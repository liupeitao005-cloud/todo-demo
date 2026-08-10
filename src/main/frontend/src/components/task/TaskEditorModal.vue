<template>
  <div v-if="open" class="modal-backdrop" @click.self="$emit('close')">
    <form class="task-modal" @submit.prevent="$emit('submit')">
      <div class="modal-head">
        <h2>{{ form.id ? "编辑任务" : "新建任务" }}</h2>
        <button class="icon-button" type="button" title="关闭" @click="$emit('close')">×</button>
      </div>
      <label>
        标题
        <input v-model.trim="form.title" placeholder="例如：完成产品需求文档评审" />
      </label>
      <label>
        任务说明
        <textarea v-model.trim="form.content" placeholder="写清楚这件事的目标、交付物和下一步。"></textarea>
      </label>
      <div class="form-row">
        <label>
          任务类型
          <select v-model="form.taskType">
            <option v-for="type in taskTypeOptions" :key="type.value" :value="type.value">
              {{ type.label }}
            </option>
          </select>
        </label>
        <label>
          父任务ID
          <input v-model.number="form.parentId" type="number" min="0" placeholder="没有父任务则填 0" />
        </label>
      </div>
      <div class="form-row">
        <label>
          开始时间
          <input v-model="form.startTime" type="datetime-local" />
        </label>
        <label>
          结束时间
          <input v-model="form.finishTime" type="datetime-local" />
        </label>
      </div>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
      <div class="modal-actions">
        <button class="secondary" type="button" @click="$emit('close')">取消</button>
        <button :disabled="loading" type="submit">{{ form.id ? "保存修改" : "创建任务" }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { computed } from "vue";

const emit = defineEmits(["close", "submit", "update:form"]);

const props = defineProps({
  form: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  ok: {
    type: Boolean,
    default: false
  },
  open: {
    type: Boolean,
    default: false
  },
  status: {
    type: String,
    default: ""
  },
  taskTypeOptions: {
    type: Array,
    default: () => []
  }
});

const form = computed(() => new Proxy(props.form, {
  get(target, key) {
    return target[key];
  },
  set(_target, key, value) {
    emit("update:form", { [key]: value });
    return true;
  }
}));
</script>
