<template>
  <aside class="task-detail-panel">
    <template v-if="task">
      <div class="detail-head">
        <div class="detail-title">
          <h2>任务详情</h2>
          <span v-if="helpers.isFinished(task)" class="task-status-pill done">已完成</span>
          <span v-else-if="helpers.isNext(task)" class="task-status-pill next">下一个重点</span>
          <span v-else-if="helpers.isOverdue(task)" class="task-status-pill overdue">已超时</span>
        </div>
        <button class="icon-button" type="button" title="刷新列表" @click="$emit('refresh')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M20 12a8 8 0 1 1-2.3-5.7M20 4v6h-6" />
          </svg>
        </button>
      </div>

      <div class="detail-section">
        <span class="detail-label">标题</span>
        <h3>
          <span :class="['task-star', { done: helpers.isFinished(task) }]" aria-hidden="true">
            {{ helpers.isFinished(task) ? "✓" : "★" }}
          </span>
          {{ task.title || "未命名任务" }}
        </h3>
      </div>

      <div class="detail-section">
        <span class="detail-label">任务说明</span>
        <p>{{ task.content || "暂无任务说明。" }}</p>
      </div>

      <div class="detail-grid">
        <div>
          <span class="detail-label">任务类型</span>
          <span :class="['task-type-pill', helpers.taskTone(task)]">{{ helpers.taskTypeText(task) }}</span>
        </div>
        <div>
          <span class="detail-label">任务ID</span>
          <strong>{{ helpers.displayId(task.id) }}</strong>
        </div>
        <div>
          <span class="detail-label">父任务ID</span>
          <strong>{{ helpers.parentText(task) }}</strong>
        </div>
        <div>
          <span class="detail-label">开始时间</span>
          <strong>{{ helpers.formatDateTime(task.startTime) }}</strong>
        </div>
        <div>
          <span class="detail-label">结束时间</span>
          <strong>{{ helpers.formatDateTime(task.finishTime) }}</strong>
        </div>
        <div>
          <span class="detail-label">子任务进度</span>
          <div class="detail-progress">
            <span>{{ helpers.progressText(task) }}</span>
            <span class="mini-progress">
              <i :style="{ width: `${helpers.progressPercent(task)}%` }"></i>
            </span>
            <span>{{ helpers.progressPercent(task) }}%</span>
          </div>
        </div>
      </div>

      <div class="subtask-card">
        <div class="subtask-head">
          <h3>子任务列表</h3>
          <button class="subtask-add" type="button" @click="$emit('create-child', task.id)">+ 添加子任务</button>
        </div>
        <div v-if="helpers.childTasks(task).length" class="subtask-list">
          <button
            v-for="child in helpers.childTasks(task)"
            :key="child.id"
            class="subtask-row"
            type="button"
            @click="$emit('select', child)"
          >
            <span :class="['check-box', { checked: helpers.isFinished(child) }]">
              <svg v-if="helpers.isFinished(child)" viewBox="0 0 24 24" aria-hidden="true"><path d="m5 12 4 4 10-10" /></svg>
            </span>
            <span>{{ child.title || "未命名子任务" }}</span>
            <em :class="['subtask-status', helpers.isFinished(child) ? 'done' : 'todo']">
              {{ helpers.isFinished(child) ? "已完成" : "待开始" }}
            </em>
          </button>
        </div>
        <p v-else class="subtask-empty">还没有子任务，可以点“添加子任务”拆出下一步。</p>
      </div>

      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
      <div class="detail-actions">
        <button class="secondary action-button" type="button" @click="$emit('edit', task)">编辑</button>
        <button class="success action-button" type="button" :disabled="helpers.isFinished(task)" @click="$emit('finish')">
          {{ helpers.isFinished(task) ? "已完成" : "完成" }}
        </button>
        <button class="secondary action-button" type="button" @click="$emit('split')">拆分</button>
        <button class="secondary warn action-button" type="button" :disabled="helpers.isFinished(task) || !helpers.isOverdue(task)" @click="$emit('delay')">延期</button>
        <button class="secondary action-button" type="button" :disabled="helpers.isFinished(task) || helpers.isNext(task)" @click="$emit('next')">
          {{ helpers.isNext(task) && !helpers.isFinished(task) ? "已是重点" : "设为下一步" }}
        </button>
        <button class="danger soft-danger action-button" type="button" @click="$emit('remove')">删除</button>
      </div>
    </template>
    <div v-else class="task-empty">选择一个任务查看详情。</div>
  </aside>
</template>

<script setup>
defineEmits(["create-child", "delay", "edit", "finish", "next", "refresh", "remove", "select", "split"]);

defineProps({
  helpers: {
    type: Object,
    required: true
  },
  ok: {
    type: Boolean,
    default: false
  },
  status: {
    type: String,
    default: ""
  },
  task: {
    type: Object,
    default: null
  }
});
</script>
