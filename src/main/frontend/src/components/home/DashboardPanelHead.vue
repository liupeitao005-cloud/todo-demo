<template>
  <div class="panel-head">
    <div class="panel-title">
      <span :class="['panel-icon', iconTone]" aria-hidden="true">
        <svg viewBox="0 0 24 24">
          <slot></slot>
        </svg>
      </span>
      <div>
        <h2>{{ title }}</h2>
        <p>{{ description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  description: {
    type: String,
    required: true
  },
  iconTone: {
    type: String,
    required: true
  },
  title: {
    type: String,
    required: true
  }
});
</script>
