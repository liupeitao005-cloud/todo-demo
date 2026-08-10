<template>
  <div class="task-stats">
    <article v-for="stat in stats" :key="stat.label" class="task-stat-card">
      <span :class="['stat-icon', stat.tone]">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path :d="stat.icon" />
        </svg>
      </span>
      <div>
        <span class="stat-label">{{ stat.label }}</span>
        <strong>{{ stat.value }}</strong>
        <small>
          {{ stat.caption }}
          <i :class="['stat-dot', stat.dot]"></i>
        </small>
      </div>
    </article>
  </div>
</template>

<script setup>
defineProps({
  stats: {
    type: Array,
    default: () => []
  }
});
</script>
