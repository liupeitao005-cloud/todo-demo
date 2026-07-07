<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">T</span>
        <div>
          <strong>Todo App</strong>
          <small>待办与日程管理</small>
        </div>
      </div>

      <nav>
        <RouterLink v-for="item in navItems" :key="item.to" :to="item.to">
          <span>{{ item.icon }}</span>{{ item.label }}
        </RouterLink>
      </nav>
    </aside>

    <div class="workspace">
      <header class="topbar">
        <div>
          <h1>{{ route.meta.title || "Todo App" }}</h1>
        </div>
        <button class="secondary" type="button" @click="handleLogout">退出登录</button>
      </header>
      <main class="content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { logout } from "@/stores/auth";

const route = useRoute();
const router = useRouter();

const navItems = [
  { to: "/", label: "项目首页", icon: "🏠" },
  { to: "/backlogs", label: "待办箱", icon: "📥" },
  { to: "/tasks", label: "任务管理", icon: "✅" },
  { to: "/schedules", label: "行程管理", icon: "📍" },
  { to: "/calendar", label: "日程视图", icon: "📅" },
  { to: "/habits", label: "习惯管理", icon: "⏱" },
  { to: "/quadrants", label: "四象限", icon: "🎯" },
  { to: "/reviews", label: "复习计划", icon: "📚" },
  { to: "/reminders", label: "提醒通知", icon: "🔔" }
];

function handleLogout() {
  logout();
  router.push("/login");
}
</script>
