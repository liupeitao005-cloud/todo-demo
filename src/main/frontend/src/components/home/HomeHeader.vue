<template>
  <header class="home-header">
    <div>
      <h1>{{ greetingText }}，{{ greetingName }} <span aria-hidden="true">👋</span></h1>
      <p>今天是 {{ todayText }}，开启高效的一天吧！</p>
    </div>
    <div class="home-actions">
      <button class="icon-button" type="button" title="搜索" aria-label="搜索">
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m21 21-4.3-4.3M10.8 18a7.2 7.2 0 1 1 0-14.4 7.2 7.2 0 0 1 0 14.4Z" /></svg>
      </button>
      <div class="notification-wrap">
        <button
          :class="['icon-button', 'bell-button', { active: reminderPanelOpen, hasReminder: pendingReminders.length }]"
          type="button"
          title="通知"
          aria-label="通知"
          @click="$emit('toggle-reminders')"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M18 9a6 6 0 0 0-12 0c0 7-3 7-3 7h18s-3 0-3-7ZM13.7 21a2 2 0 0 1-3.4 0" /></svg>
          <span v-if="pendingReminders.length" class="notification-badge">{{ reminderBadgeText }}</span>
        </button>
        <div v-if="reminderPanelOpen" class="notification-panel">
          <div class="notification-head">
            <strong>提醒</strong>
            <button v-if="pendingReminders.length" type="button" @click="$emit('mark-all-reminders-read')">全部已读</button>
          </div>
          <div v-if="pendingReminders.length" class="notification-list">
            <button
              v-for="reminder in pendingReminders"
              :key="reminder.id"
              class="notification-item"
              type="button"
              @click="$emit('mark-reminder-read', reminder)"
            >
              <strong>{{ reminder.title || "Todo 提醒" }}</strong>
              <span>{{ reminder.content || "你有一条到期提醒" }}</span>
              <small>{{ formatShortDate(reminder.remindTime) }} · {{ reminderTypeLabel(reminder.targetType) }}</small>
            </button>
          </div>
          <p v-else class="notification-empty">暂无到期提醒</p>
        </div>
      </div>
      <button class="primary-add" type="button" @click="$emit('open-create')">
        <span aria-hidden="true">+</span>
        添加事项
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m6 9 6 6 6-6" /></svg>
      </button>
    </div>
  </header>
</template>

<script setup>
defineEmits(["mark-all-reminders-read", "mark-reminder-read", "open-create", "toggle-reminders"]);

defineProps({
  formatShortDate: {
    type: Function,
    required: true
  },
  greetingName: {
    type: String,
    required: true
  },
  greetingText: {
    type: String,
    required: true
  },
  pendingReminders: {
    type: Array,
    default: () => []
  },
  reminderBadgeText: {
    type: String,
    required: true
  },
  reminderPanelOpen: {
    type: Boolean,
    default: false
  },
  reminderTypeLabel: {
    type: Function,
    required: true
  },
  todayText: {
    type: String,
    required: true
  }
});
</script>
