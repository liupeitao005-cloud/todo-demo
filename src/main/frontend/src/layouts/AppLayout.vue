<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m5 12 4 4 10-10" /></svg>
        </span>
        <div>
          <strong>Todo</strong>
          <small>专注当下 · 成就未来</small>
        </div>
      </div>

      <nav class="side-nav" aria-label="主导航">
        <RouterLink v-for="item in navItems" :key="item.to" :to="item.to">
          <span class="nav-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path v-for="path in item.paths" :key="path" :d="path" />
            </svg>
          </span>
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>

      <button class="logout-button" type="button" @click="handleLogout">
        <span class="nav-icon">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M10 17l5-5-5-5M15 12H3M21 19V5a2 2 0 0 0-2-2h-4M15 21h4a2 2 0 0 0 2-2" /></svg>
        </span>
        退出登录
      </button>
    </aside>

    <div class="workspace">
      <header v-if="route.name !== 'home' && !route.meta.hideShellTopbar" class="topbar">
        <div>
          <h1>{{ route.meta.title || "Todo" }}</h1>
          <p v-if="route.meta.subtitle">{{ route.meta.subtitle }}</p>
        </div>
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
  { to: "/home", label: "首页", paths: ["M3 10.5 12 3l9 7.5", "M5 10v10h14V10", "M9 20v-6h6v6"] },
  { to: "/backlogs", label: "待办", paths: ["M9 11l2 2 4-5", "M20 6v14H4V4h11"] },
  { to: "/tasks", label: "任务", paths: ["M8 6h13M8 12h13M8 18h13", "M3 6h.01M3 12h.01M3 18h.01"] },
  { to: "/schedules", label: "行程", paths: ["M8 2v4M16 2v4M3.5 9.5h17", "M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z"] },
  { to: "/habits", label: "习惯", paths: ["M12 3v18", "M5 8c5 0 7 3 7 7-5 0-7-3-7-7Z", "M19 8c-5 0-7 3-7 7 5 0 7-3 7-7Z"] },
  { to: "/quadrants", label: "四象限", paths: ["M4 4h7v7H4z", "M13 4h7v7h-7z", "M4 13h7v7H4z", "M13 13h7v7h-7z"] },
  { to: "/reviews", label: "复习计划", paths: ["M6 3h12v18H6z", "M9 7h6M9 11h6M9 15h3"] },
  { to: "/calendar", label: "日历", paths: ["M4 19V9", "M10 19V5", "M16 19v-7", "M22 19H2"] }
];

function handleLogout() {
  logout();
  router.push("/login");
}
</script>
