<template>
  <section class="task-list-panel">
    <div class="task-toolbar">
      <label class="task-search">
        <span class="sr-only">搜索任务</span>
        <input :value="keyword" placeholder="按任务标题搜索" @input="$emit('update:keyword', $event.target.value.trim())" />
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="m21 21-4.3-4.3M10.8 18a7.2 7.2 0 1 1 0-14.4 7.2 7.2 0 0 1 0 14.4Z" />
        </svg>
      </label>
      <div class="task-tabs" aria-label="任务筛选">
        <button
          v-for="filter in filters"
          :key="filter.value"
          :class="{ active: activeFilter === filter.value }"
          type="button"
          @click="$emit('update:activeFilter', filter.value)"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="task-empty">正在加载任务...</div>
    <div v-else-if="tasks.length" class="task-list">
      <button
        v-for="task in tasks"
        :key="task.id"
        :class="['task-row', {
          active: selectedTask?.id === task.id,
          done: helpers.isFinished(task),
          next: helpers.isNext(task),
          overdue: helpers.isOverdue(task)
        }]"
        type="button"
        @click="$emit('select', task)"
      >
        <div class="task-row-main">
          <span :class="['task-star', { done: helpers.isFinished(task) }]" aria-hidden="true">
            {{ helpers.isFinished(task) ? "✓" : "★" }}
          </span>
          <div>
            <h3 class="task-title-line">
              <span>{{ task.title || "未命名任务" }}</span>
              <em v-if="helpers.isFinished(task)" class="task-status-pill done">已完成</em>
              <em v-else-if="helpers.isNext(task)" class="task-status-pill next">下一个重点</em>
              <em v-else-if="helpers.isOverdue(task)" class="task-status-pill overdue">已超时</em>
            </h3>
            <p>{{ task.content || "暂无任务说明。" }}</p>
          </div>
        </div>
        <span :class="['task-type-pill', helpers.taskTone(task)]">{{ helpers.taskTypeText(task) }}</span>
        <div class="task-row-meta">
          <span>ID: {{ helpers.displayId(task.id) }}</span>
          <span>父任务: {{ helpers.parentText(task) }}</span>
          <span class="task-time-text">{{ helpers.formatTimeRange(task.startTime, task.finishTime) }}</span>
          <span class="task-progress-label">子任务 {{ helpers.progressText(task) }}</span>
          <span class="mini-progress" aria-hidden="true">
            <i :style="{ width: `${helpers.progressPercent(task)}%` }"></i>
          </span>
        </div>
      </button>
    </div>
    <div v-else class="task-empty">
      没有匹配的任务，换个筛选条件试试。
    </div>
  </section>
</template>

<script setup>
defineEmits(["select", "update:activeFilter", "update:keyword"]);

defineProps({
  activeFilter: {
    type: String,
    required: true
  },
  filters: {
    type: Array,
    default: () => []
  },
  helpers: {
    type: Object,
    required: true
  },
  keyword: {
    type: String,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  selectedTask: {
    type: Object,
    default: null
  },
  tasks: {
    type: Array,
    default: () => []
  }
});
</script>
