<template>
  <div v-if="open" class="modal-backdrop" @click.self="$emit('close')">
    <form class="create-modal" @submit.prevent="$emit('submit')">
      <div class="modal-head">
        <div>
          <h2>添加事项</h2>
          <p>选择一种类型，创建后会立即显示在首页。</p>
        </div>
        <button class="modal-close" type="button" aria-label="关闭" @click="$emit('close')">×</button>
      </div>

      <div class="type-picker" role="tablist" aria-label="事项类型">
        <button
          v-for="type in createTypes"
          :key="type.value"
          :class="['type-option', { active: selectedType === type.value }]"
          type="button"
          @click="$emit('update:selectedType', type.value)"
        >
          <strong>{{ type.label }}</strong>
          <small>{{ type.description }}</small>
        </button>
      </div>

      <div class="modal-fields">
        <label>
          标题
          <input v-model.trim="createForm.title" placeholder="例如：整理项目资料" />
        </label>
        <label>
          内容
          <textarea v-model.trim="createForm.content" placeholder="补充一点说明，方便之后继续处理"></textarea>
        </label>
        <label v-if="selectedType === 'task'">
          任务类型
          <input v-model.trim="createForm.taskType" placeholder="normal / long / once" />
        </label>
        <label v-if="selectedType === 'schedule'">
          地点
          <input v-model.trim="createForm.location" placeholder="例如：会议室 A" />
        </label>
        <div v-if="selectedType === 'habit'" class="modal-row three">
          <label>
            每日分钟
            <input v-model.number="createForm.dayMinutes" min="0" placeholder="30" type="number" />
          </label>
          <label>
            最少分钟
            <input v-model.number="createForm.minMinutes" min="0" placeholder="10" type="number" />
          </label>
          <label>
            最多分钟
            <input v-model.number="createForm.maxMinutes" min="0" placeholder="60" type="number" />
          </label>
        </div>
        <div v-if="selectedType === 'task' || selectedType === 'schedule'" class="modal-row">
          <label>
            开始时间
            <input v-model="createForm.startTime" type="datetime-local" />
          </label>
          <label>
            结束时间
            <input v-model="createForm.finishTime" type="datetime-local" />
          </label>
        </div>
      </div>

      <p :class="['modal-status', createOk ? 'ok' : 'err']">{{ createStatus }}</p>
      <div class="modal-actions">
        <button class="secondary" type="button" @click="$emit('close')">取消</button>
        <button :disabled="submitting" type="submit">{{ submitting ? "创建中..." : currentCreateLabel }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { computed } from "vue";

const emit = defineEmits(["close", "submit", "update:createForm", "update:selectedType"]);

const props = defineProps({
  createForm: {
    type: Object,
    required: true
  },
  createOk: {
    type: Boolean,
    default: false
  },
  createStatus: {
    type: String,
    default: ""
  },
  createTypes: {
    type: Array,
    default: () => []
  },
  currentCreateLabel: {
    type: String,
    required: true
  },
  open: {
    type: Boolean,
    default: false
  },
  selectedType: {
    type: String,
    required: true
  },
  submitting: {
    type: Boolean,
    default: false
  }
});

const createForm = computed(() => new Proxy(props.createForm, {
  get(target, key) {
    return target[key];
  },
  set(_target, key, value) {
    emit("update:createForm", { [key]: value });
    return true;
  }
}));
</script>
