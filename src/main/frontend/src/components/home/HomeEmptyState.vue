<template>
  <template v-if="!hasAnyData">
    <section class="empty-hero">
      <div class="empty-copy">
        <h2>你还没有创建任何待办</h2>
        <p>创建你的第一个待办，开始规划你的时间，让每一天都更有条理，更有成就感！</p>
        <button class="starter-button" type="button" @click="$emit('open-create')">+ 创建第一个事项</button>
        <div class="starter-hint">
          <span>或者试试下面这些方式开始</span>
          <i aria-hidden="true"></i>
        </div>
      </div>
      <div class="empty-art" aria-hidden="true">
        <span class="art-dot dot-a"></span>
        <span class="art-dot dot-b"></span>
        <span class="art-dot dot-c"></span>
        <div class="art-glow"></div>
        <div class="art-clipboard">
          <b></b>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
        </div>
        <div class="art-plant"></div>
        <div class="art-pen"></div>
      </div>
    </section>

    <section class="starter-section">
      <h2>你还可以</h2>
      <div class="starter-grid">
        <button class="starter-card green" type="button" @click="$emit('open-create', 'schedule')">
          <span class="starter-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 2v4M16 2v4M3.5 9.5h17M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Zm3 9h3v3H8z" /></svg>
          </span>
          <strong>创建日程</strong>
          <small>安排你的会议、学习、约会等</small>
          <em>新建日程 →</em>
        </button>
        <button class="starter-card amber" type="button" @click="$emit('open-create', 'task')">
          <span class="starter-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 11l2 2 4-5M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" /></svg>
          </span>
          <strong>创建任务</strong>
          <small>把有明确时间的事情纳入计划</small>
          <em>创建任务 →</em>
        </button>
        <button class="starter-card teal" type="button" @click="$emit('open-create', 'habit')">
          <span class="starter-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18ZM8.5 12l2.2 2.2 4.8-5" /></svg>
          </span>
          <strong>创建习惯</strong>
          <small>建立每天坚持的小目标</small>
          <em>创建习惯 →</em>
        </button>
      </div>
    </section>
  </template>
</template>

<script setup>
defineEmits(["open-create"]);

defineProps({
  hasAnyData: {
    type: Boolean,
    default: false
  }
});
</script>
