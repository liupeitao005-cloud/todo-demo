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
          {{ item.label }}
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
  { to: "/backlogs", label: "待办箱" },
  { to: "/tasks", label: "任务管理" },
  { to: "/schedules", label: "行程管理" },
  { to: "/calendar", label: "日程视图" },
  { to: "/habits", label: "习惯管理" },
  { to: "/quadrants", label: "四象限" },
  { to: "/reviews", label: "复习计划" },
  { to: "/reminders", label: "提醒通知" }
];

function handleLogout() {
  logout();
  router.push("/login");
}
</script>
