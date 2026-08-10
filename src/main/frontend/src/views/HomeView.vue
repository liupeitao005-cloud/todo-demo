<template>
  <section class="home-page">
    <HomeHeader
      :format-short-date="formatShortDate"
      :greeting-name="greetingName"
      :greeting-text="greetingText"
      :pending-reminders="pendingReminders"
      :reminder-badge-text="reminderBadgeText"
      :reminder-panel-open="reminderPanelOpen"
      :reminder-type-label="reminderTypeLabel"
      :today-text="todayText"
      @mark-all-reminders-read="markAllRemindersRead"
      @mark-reminder-read="markReminderRead"
      @open-create="openCreateModal"
      @toggle-reminders="toggleReminderPanel"
    />

    <div v-if="loadError" class="home-alert">{{ loadError }}</div>

    <section v-if="initialLoading" class="loading-panel">
      <span></span>
      <p>正在整理你的今日事项...</p>
    </section>

    <HomeEmptyState
      v-else-if="!hasAnyData"
      :has-any-data="hasAnyData"
      @open-create="openCreateModal"
    />

    <DashboardGrid
      v-else
      :format-review-meta="formatReviewMeta"
      :format-short-date="formatShortDate"
      :format-time-range="formatTimeRange"
      :today-habits="todayHabits"
      :today-reviews="todayReviews"
      :today-tasks="todayTasks"
      :today-todos="todayTodos"
      :upcoming-schedules="upcomingSchedules"
    />

    <QuickCreateModal
      v-model:selected-type="selectedType"
      :create-form="createForm"
      :create-ok="createOk"
      :create-status="createStatus"
      :create-types="createTypes"
      :current-create-label="currentCreateLabel"
      :open="modalOpen"
      :submitting="submitting"
      @close="closeModal"
      @submit="submitCreate"
    />
  </section>
</template>
<script setup>
import DashboardGrid from "@/components/home/DashboardGrid.vue";
import HomeEmptyState from "@/components/home/HomeEmptyState.vue";
import HomeHeader from "@/components/home/HomeHeader.vue";
import QuickCreateModal from "@/components/home/QuickCreateModal.vue";
import { computed, onMounted, onUnmounted, reactive, ref } from "vue";
import { backlogApi, habitApi, reminderApi, reviewApi, scheduleApi, taskApi } from "@/api/todoApi";
import { authState } from "@/stores/auth";
import { userCacheKey } from "@/utils/userCache";
import "@/styles/home.css";

const DASHBOARD_CACHE_KEY = userCacheKey("home-dashboard");
const cachedDashboard = readDashboardCache();
const REMINDER_POLL_MS = 30000;
const lists = reactive({
  backlogs: cachedDashboard?.backlogs || [],
  tasks: cachedDashboard?.tasks || [],
  schedules: cachedDashboard?.schedules || [],
  habits: cachedDashboard?.habits || [],
  reviews: cachedDashboard?.reviews || []
});

const loading = ref(false);
const initialLoading = ref(!cachedDashboard);
const submitting = ref(false);
const modalOpen = ref(false);
const selectedType = ref("backlog");
const loadError = ref("");
const createStatus = ref("");
const createOk = ref(false);
const pendingReminders = ref([]);
const reminderPanelOpen = ref(false);
const notifiedReminderIds = new Set();
let reminderTimer = null;
let reminderLoading = false;

const createForm = reactive({
  title: "",
  content: "",
  location: "",
  taskType: "normal",
  startTime: "",
  finishTime: "",
  dayMinutes: null,
  minMinutes: null,
  maxMinutes: null
});

const createTypes = [
  { value: "backlog", label: "新建待办", description: "先记录，稍后再安排" },
  { value: "task", label: "创建任务", description: "有明确开始和结束时间" },
  { value: "schedule", label: "安排行程", description: "会议、约会或外出安排" },
  { value: "habit", label: "创建习惯", description: "建立每天坚持的小目标" }
];

const greetingName = computed(() => {
  const username = authState.username?.trim();
  return username ? `${username}同学` : "同学";
});
const greetingText = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return "夜深了";
  if (hour < 12) return "早上好";
  if (hour < 18) return "下午好";
  return "晚上好";
});
const todayText = computed(() => {
  const date = new Date();
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
});
const hasAnyData = computed(() => lists.backlogs.length + lists.tasks.length + lists.schedules.length + lists.habits.length + lists.reviews.length > 0);
const currentCreateLabel = computed(() => createTypes.find((item) => item.value === selectedType.value)?.label || "创建");

const reminderBadgeText = computed(() => pendingReminders.value.length > 99 ? "99+" : String(pendingReminders.value.length));

const todayTodos = computed(() => {
  return lists.backlogs.map((item, index) => ({
    key: `backlog-${item.id || index}`,
    kind: "backlog",
    title: item.title || "未命名待办",
    meta: item.content || "待安排"
  }));
});

const todayTasks = computed(() => {
  return lists.tasks
    .filter((item) => !Number(item.isFinish) && taskOverlapsToday(item))
    .sort((a, b) => parseDate(a.startTime) - parseDate(b.startTime))
    .map((item, index) => ({
      key: `task-${item.id || index}`,
      kind: "task",
      title: item.title || "未命名任务",
      meta: formatTimeRange(item.startTime, item.finishTime) || item.content || "今日任务"
    }));
});

const upcomingSchedules = computed(() => {
  const startOfToday = new Date();
  startOfToday.setHours(0, 0, 0, 0);
  return lists.schedules
    .filter((item) => !item.finishTime || parseDate(item.finishTime) >= startOfToday)
    .sort((a, b) => parseDate(a.startTime) - parseDate(b.startTime))
    .slice(0, 5)
    .map((item, index) => ({ ...item, key: `schedule-${item.id || index}` }));
});

const todayHabits = computed(() => {
  return lists.habits.slice(0, 6).map((item, index) => ({
    ...item,
    key: `habit-${item.id || index}`
  }));
});

const todayReviews = computed(() => {
  return lists.reviews
    .filter((item) => !Number(item.isFinish) && isDueTodayOrOverdue(item.reviewTime))
    .sort((a, b) => parseDate(a.reviewTime) - parseDate(b.reviewTime))
    .map((item, index) => ({
      ...item,
      key: `review-${item.id || index}`,
      title: item.title || `复习任务 ${item.reviewTaskId || ""}`.trim() || "未命名复习"
    }));
});

function parseDate(value) {
  if (!value) return new Date(0);
  return new Date(String(value).replace(" ", "T"));
}

function startOfToday() {
  const date = new Date();
  date.setHours(0, 0, 0, 0);
  return date;
}

function startOfTomorrow() {
  const date = startOfToday();
  date.setDate(date.getDate() + 1);
  return date;
}

function taskOverlapsToday(task) {
  if (!task?.startTime && !task?.finishTime) return false;
  const start = task.startTime ? parseDate(task.startTime) : parseDate(task.finishTime);
  const finish = task.finishTime ? parseDate(task.finishTime) : parseDate(task.startTime);
  return start < startOfTomorrow() && finish >= startOfToday();
}

function isDueTodayOrOverdue(value) {
  if (!value) return false;
  return parseDate(value) < startOfTomorrow();
}

function formatShortDate(value) {
  if (!value) return "未定";
  const date = parseDate(value);
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hour = String(date.getHours()).padStart(2, "0");
  const minute = String(date.getMinutes()).padStart(2, "0");
  return `${month}/${day} ${hour}:${minute}`;
}

function formatTimeRange(start, finish) {
  if (!start && !finish) return "";
  if (!finish) return formatShortDate(start);
  return `${formatShortDate(start)} - ${formatShortDate(finish).slice(6)}`;
}

function formatReviewMeta(item) {
  const time = formatShortDate(item.reviewTime);
  const prefix = parseDate(item.reviewTime) < startOfToday() ? "已逾期" : time;
  return `${prefix} · ${item.content || "复习计划"}`;
}

function reminderTypeLabel(type) {
  const labels = {
    task: "任务",
    schedule: "行程",
    review: "复习"
  };
  return labels[type] || "提醒";
}

function toggleReminderPanel() {
  reminderPanelOpen.value = !reminderPanelOpen.value;
  if (reminderPanelOpen.value) {
    requestNotificationPermission();
    loadPendingReminders({ notify: false });
  }
}

async function loadPendingReminders(options = {}) {
  if (reminderLoading) return;
  reminderLoading = true;
  try {
    const data = await reminderApi.pending();
    const reminders = dedupeReminders(Array.isArray(data?.data) ? data.data : []);
    pendingReminders.value = reminders;
    if (options.notify !== false) notifyDueReminders(reminders);
  } catch {
    pendingReminders.value = [];
  } finally {
    reminderLoading = false;
  }
}

async function notifyDueReminders(reminders) {
  if (!("Notification" in window) || Notification.permission !== "granted") return;
  for (const reminder of reminders) {
    if (!reminder?.id || notifiedReminderIds.has(reminder.id)) continue;
    notifiedReminderIds.add(reminder.id);
    new Notification(reminder.title || "Todo 提醒", {
      body: reminder.content || "你有一条到期提醒"
    });
    try {
      await reminderApi.read(reminder.id);
      pendingReminders.value = pendingReminders.value.filter((item) => item.id !== reminder.id);
    } catch {
      // 通知已经弹出，本轮不再重复提醒。
    }
  }
}

function dedupeReminders(reminders) {
  const map = new Map();
  for (const reminder of reminders) {
    if (!reminder) continue;
    const key = reminder.targetType && reminder.targetId
      ? `${reminder.targetType}:${reminder.targetId}:${reminder.channel || "desktop"}`
      : `id:${reminder.id}`;
    if (!map.has(key)) map.set(key, reminder);
  }
  return [...map.values()];
}

async function requestNotificationPermission() {
  if (!("Notification" in window) || Notification.permission !== "default") return;
  try {
    await Notification.requestPermission();
  } catch {
    // Browser notification permission is optional; the in-app bell still works.
  }
}

async function markReminderRead(reminder) {
  if (!reminder?.id) return;
  await reminderApi.read(reminder.id);
  pendingReminders.value = pendingReminders.value.filter((item) => item.id !== reminder.id);
}

async function markAllRemindersRead() {
  const reminders = [...pendingReminders.value];
  await Promise.allSettled(reminders.filter((item) => item?.id).map((item) => reminderApi.read(item.id)));
  pendingReminders.value = [];
}

function toDateTimeLocal(date) {
  const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return localDate.toISOString().slice(0, 16);
}

function resetCreateForm() {
  const start = new Date();
  start.setMinutes(Math.ceil(start.getMinutes() / 15) * 15, 0, 0);
  const finish = new Date(start.getTime() + 60 * 60 * 1000);
  createForm.title = "";
  createForm.content = "";
  createForm.location = "";
  createForm.taskType = "normal";
  createForm.startTime = toDateTimeLocal(start);
  createForm.finishTime = toDateTimeLocal(finish);
  createForm.dayMinutes = null;
  createForm.minMinutes = null;
  createForm.maxMinutes = null;
  createStatus.value = "";
  createOk.value = false;
}

function openCreateModal(type = "backlog") {
  selectedType.value = type;
  resetCreateForm();
  modalOpen.value = true;
}

function closeModal() {
  if (submitting.value) return;
  modalOpen.value = false;
}

function optimisticCreate(type, payload) {
  const item = {
    ...payload,
    id: `draft-${Date.now()}`,
    createTime: new Date().toISOString(),
    isFinish: 0
  };
  if (type === "backlog") lists.backlogs = [item, ...lists.backlogs];
  if (type === "task") lists.tasks = [item, ...lists.tasks];
  if (type === "schedule") lists.schedules = [item, ...lists.schedules];
  if (type === "habit") lists.habits = [item, ...lists.habits];
}

function validateCreate() {
  if (!createForm.title) {
    createStatus.value = "请填写标题";
    createOk.value = false;
    return false;
  }
  if (selectedType.value === "task" || selectedType.value === "schedule") {
    if (!createForm.startTime || !createForm.finishTime) {
      createStatus.value = "请填写开始和结束时间";
      createOk.value = false;
      return false;
    }
    if (parseDate(createForm.startTime) >= parseDate(createForm.finishTime)) {
      createStatus.value = "结束时间需要晚于开始时间";
      createOk.value = false;
      return false;
    }
  }
  return true;
}

async function submitCreate() {
  if (!validateCreate()) return;
  submitting.value = true;
  createStatus.value = "";
  const content = createForm.content || createForm.title;
  const payloads = {
    backlog: { title: createForm.title, content },
    task: {
      title: createForm.title,
      content,
      taskType: createForm.taskType || "normal",
      parentId: null,
      startTime: createForm.startTime,
      finishTime: createForm.finishTime
    },
    schedule: {
      title: createForm.title,
      content,
      location: createForm.location,
      startTime: createForm.startTime,
      finishTime: createForm.finishTime
    },
    habit: {
      title: createForm.title,
      content,
      dayMinutes: createForm.dayMinutes || null,
      minMinutes: createForm.minMinutes || null,
      maxMinutes: createForm.maxMinutes || null
    }
  };
  const actions = {
    backlog: () => backlogApi.create(payloads.backlog),
    task: () => taskApi.create(payloads.task),
    schedule: () => scheduleApi.create(payloads.schedule),
    habit: () => habitApi.create(payloads.habit)
  };

  try {
    const type = selectedType.value;
    const data = await actions[type]();
    if (data?.code !== 200) throw new Error(data?.message || "创建失败");
    optimisticCreate(type, payloads[type]);
    modalOpen.value = false;
    await loadDashboard({ silent: true });
    await loadPendingReminders({ notify: false });
  } catch (error) {
    createStatus.value = error?.message || "创建失败，请稍后重试";
    createOk.value = false;
  } finally {
    submitting.value = false;
  }
}

async function loadDashboard(options = {}) {
  loading.value = true;
  if (!options.silent) loadError.value = "";
  const [backlogResult, taskResult, scheduleResult, habitResult, reviewResult] = await Promise.allSettled([
    backlogApi.list(),
    taskApi.list(),
    scheduleApi.list(),
    habitApi.list(),
    reviewApi.list()
  ]);

  if (backlogResult.status === "fulfilled") lists.backlogs = Array.isArray(backlogResult.value?.data) ? backlogResult.value.data : [];
  if (taskResult.status === "fulfilled") lists.tasks = Array.isArray(taskResult.value?.data) ? taskResult.value.data : [];
  if (scheduleResult.status === "fulfilled") lists.schedules = Array.isArray(scheduleResult.value?.data) ? scheduleResult.value.data : [];
  if (habitResult.status === "fulfilled") lists.habits = Array.isArray(habitResult.value?.data) ? habitResult.value.data : [];
  if (reviewResult.status === "fulfilled") lists.reviews = Array.isArray(reviewResult.value?.data) ? reviewResult.value.data : [];
  cacheDashboard();

  const failed = [backlogResult, taskResult, scheduleResult, habitResult, reviewResult].some((item) => item.status === "rejected");
  if (failed && !options.silent) loadError.value = "部分数据暂时加载失败，请稍后刷新页面。";
  loading.value = false;
  initialLoading.value = false;
}

onMounted(() => {
  loadDashboard();
  loadPendingReminders({ notify: false });
  reminderTimer = window.setInterval(() => loadPendingReminders(), REMINDER_POLL_MS);
});

onUnmounted(() => {
  if (reminderTimer) {
    window.clearInterval(reminderTimer);
  }
});

function readDashboardCache() {
  try {
    const cached = JSON.parse(sessionStorage.getItem(DASHBOARD_CACHE_KEY) || "null");
    if (!cached) return false;
    return {
      backlogs: Array.isArray(cached.backlogs) ? cached.backlogs : [],
      tasks: Array.isArray(cached.tasks) ? cached.tasks : [],
      schedules: Array.isArray(cached.schedules) ? cached.schedules : [],
      habits: Array.isArray(cached.habits) ? cached.habits : [],
      reviews: Array.isArray(cached.reviews) ? cached.reviews : []
    };
  } catch {
    return null;
  }
}

function cacheDashboard() {
  sessionStorage.setItem(DASHBOARD_CACHE_KEY, JSON.stringify({
    backlogs: lists.backlogs,
    tasks: lists.tasks,
    schedules: lists.schedules,
    habits: lists.habits,
    reviews: lists.reviews
  }));
}
</script>
