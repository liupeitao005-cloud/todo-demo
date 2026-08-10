<template>
  <section class="dashboard-grid">
    <article class="dashboard-panel todo-panel">
      <DashboardPanelHead icon-tone="blue" title="今天待办" description="优先处理今天要推进的事情">
        <path d="M9 11l2 2 4-5M20 6v14H4V4h11" />
      </DashboardPanelHead>
      <div v-if="todayTodos.length" class="clean-list">
        <div v-for="item in todayTodos" :key="item.key" class="clean-item">
          <span :class="['kind-dot', item.kind]"></span>
          <div>
            <strong>{{ item.title }}</strong>
            <small>{{ item.meta }}</small>
          </div>
        </div>
      </div>
      <p v-else class="soft-empty">今天还没有待办，可以从右上角添加一个。</p>
    </article>

    <article class="dashboard-panel task-panel">
      <DashboardPanelHead icon-tone="violet" title="今日任务" description="今天要推进的任务">
        <path d="M12 3v18M5 8h14M7 16h10" />
      </DashboardPanelHead>
      <div v-if="todayTasks.length" class="clean-list">
        <div v-for="item in todayTasks" :key="item.key" class="clean-item">
          <span class="kind-dot task"></span>
          <div>
            <strong>{{ item.title }}</strong>
            <small>{{ formatTimeRange(item.startTime, item.finishTime) || item.content || "今日任务" }}</small>
          </div>
        </div>
      </div>
      <p v-else class="soft-empty">今天还没有任务。</p>
    </article>

    <article class="dashboard-panel schedule-panel">
      <DashboardPanelHead icon-tone="blue" title="即将到来的行程" description="按开始时间排序">
        <path d="M8 2v4M16 2v4M3.5 9.5h17M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z" />
      </DashboardPanelHead>
      <div v-if="upcomingSchedules.length" class="schedule-list">
        <div v-for="item in upcomingSchedules" :key="item.key" class="schedule-card">
          <time>{{ formatShortDate(item.startTime) }}</time>
          <div>
            <strong>{{ item.title }}</strong>
            <small>{{ item.location || item.content || "未填写地点" }}</small>
          </div>
        </div>
      </div>
      <p v-else class="soft-empty">暂无即将到来的行程。</p>
    </article>

    <article class="dashboard-panel review-panel">
      <DashboardPanelHead icon-tone="orange" title="今日复习" description="今天应处理的复习任务">
        <path d="M4 19.5V5.8A2.8 2.8 0 0 1 6.8 3H20v15.5H6.8A2.8 2.8 0 0 0 4 21.3Zm0 0A2.8 2.8 0 0 1 6.8 17H20M8 7h8M8 10h6" />
      </DashboardPanelHead>
      <div v-if="todayReviews.length" class="clean-list">
        <div v-for="item in todayReviews" :key="item.key" class="clean-item">
          <span class="kind-dot review"></span>
          <div>
            <strong>{{ item.title }}</strong>
            <small>{{ formatReviewMeta(item) }}</small>
          </div>
        </div>
      </div>
      <p v-else class="soft-empty">今天暂时没有需要处理的复习。</p>
    </article>

    <article class="dashboard-panel habit-panel">
      <DashboardPanelHead icon-tone="green" title="习惯打卡" description="保持今天的节奏">
        <path d="M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18ZM8.5 12l2.2 2.2 4.8-5" />
      </DashboardPanelHead>
      <div v-if="todayHabits.length" class="habit-list">
        <div v-for="item in todayHabits" :key="item.key" class="habit-item">
          <span class="habit-check" aria-hidden="true"></span>
          <div>
            <strong>{{ item.title }}</strong>
            <small>{{ item.content || `每日 ${item.dayMinutes || 0} 分钟` }}</small>
          </div>
        </div>
      </div>
      <p v-else class="soft-empty">还没有习惯，可以在左侧“习惯”里创建。</p>
    </article>
  </section>
</template>

<script setup>
import DashboardPanelHead from "@/components/home/DashboardPanelHead.vue";

defineProps({
  formatReviewMeta: {
    type: Function,
    required: true
  },
  formatShortDate: {
    type: Function,
    required: true
  },
  formatTimeRange: {
    type: Function,
    required: true
  },
  todayHabits: {
    type: Array,
    default: () => []
  },
  todayReviews: {
    type: Array,
    default: () => []
  },
  todayTasks: {
    type: Array,
    default: () => []
  },
  todayTodos: {
    type: Array,
    default: () => []
  },
  upcomingSchedules: {
    type: Array,
    default: () => []
  }
});
</script>
