<template>
  <section class="page-grid">
    <div class="hero-card">
      <p class="eyebrow">Todo Web App</p>
      <h2>一个放在 Spring Boot 项目内部的 Vue 前端</h2>
      <p>不是纯 HTML 三件套，也不是独立部署的前后端分离项目；Vue 打包后由 Spring Boot 提供页面访问。</p>
    </div>
    <div class="stats-grid">
      <article v-for="feature in features" :key="feature.title" class="feature-card">
        <span>{{ feature.icon }}</span>
        <h3>{{ feature.title }}</h3>
        <p class="muted">{{ feature.desc }}</p>
      </article>
    </div>
  </section>
</template>

<script setup>
const features = [
  { icon: "📥", title: "待办收集", desc: "先记录不确定时间的事项，再安排为任务。" },
  { icon: "✅", title: "任务执行", desc: "支持创建、修改、完成、拆分、延期和删除任务。" },
  { icon: "📅", title: "日程展示", desc: "按日、周、月、年或自定义范围查看安排。" },
  { icon: "🎯", title: "四象限管理", desc: "按重要性和紧急性整理优先级。" },
  { icon: "📚", title: "复习计划", desc: "创建艾宾浩斯复习任务并完成复习计划。" },
  { icon: "🔔", title: "提醒通知", desc: "支持桌面、App、Telegram Bot 提醒渠道。" }
];
</script>
