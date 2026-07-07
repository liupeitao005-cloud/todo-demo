import { createRouter, createWebHashHistory } from "vue-router";
import { isLoggedIn } from "@/stores/auth";
import AppLayout from "@/layouts/AppLayout.vue";
import LoginView from "@/views/LoginView.vue";
import BacklogView from "@/views/BacklogView.vue";
import TaskView from "@/views/TaskView.vue";
import ScheduleView from "@/views/ScheduleView.vue";
import CalendarView from "@/views/CalendarView.vue";
import HabitView from "@/views/HabitView.vue";
import FourQuadrantView from "@/views/FourQuadrantView.vue";
import ReviewView from "@/views/ReviewView.vue";
import ReminderView from "@/views/ReminderView.vue";

const routes = [
  { path: "/login", name: "login", component: LoginView },
  {
    path: "/",
    component: AppLayout,
    meta: { requiresAuth: true },
    redirect: "/backlogs",
    children: [
      { path: "backlogs", name: "backlogs", component: BacklogView, meta: { title: "待办箱" } },
      { path: "tasks", name: "tasks", component: TaskView, meta: { title: "任务管理" } },
      { path: "schedules", name: "schedules", component: ScheduleView, meta: { title: "行程管理" } },
      { path: "calendar", name: "calendar", component: CalendarView, meta: { title: "日程视图" } },
      { path: "habits", name: "habits", component: HabitView, meta: { title: "习惯管理" } },
      { path: "quadrants", name: "quadrants", component: FourQuadrantView, meta: { title: "四象限" } },
      { path: "reviews", name: "reviews", component: ReviewView, meta: { title: "复习计划" } },
      { path: "reminders", name: "reminders", component: ReminderView, meta: { title: "提醒通知" } }
    ]
  }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !isLoggedIn()) return { name: "login" };
  if (to.name === "login" && isLoggedIn()) return { name: "backlogs" };
  return true;
});

export default router;
