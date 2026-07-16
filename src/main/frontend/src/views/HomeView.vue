<template>
  <section class="home-page">
    <header class="home-header">
      <div>
        <h1>{{ greetingText }}，{{ greetingName }} <span aria-hidden="true">👋</span></h1>
        <p>今天是 {{ todayText }}，开启高效的一天吧！</p>
      </div>
      <div class="home-actions">
        <button class="icon-button" type="button" title="搜索" aria-label="搜索">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m21 21-4.3-4.3M10.8 18a7.2 7.2 0 1 1 0-14.4 7.2 7.2 0 0 1 0 14.4Z" /></svg>
        </button>
        <button class="icon-button" type="button" title="通知" aria-label="通知">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M18 9a6 6 0 0 0-12 0c0 7-3 7-3 7h18s-3 0-3-7ZM13.7 21a2 2 0 0 1-3.4 0" /></svg>
        </button>
        <button class="primary-add" type="button" @click="openCreateModal()">
          <span aria-hidden="true">+</span>
          添加事项
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m6 9 6 6 6-6" /></svg>
        </button>
      </div>
    </header>

    <div v-if="loadError" class="home-alert">{{ loadError }}</div>

    <section v-if="initialLoading" class="loading-panel">
      <span></span>
      <p>正在整理你的今日事项...</p>
    </section>

    <template v-else-if="!hasAnyData">
      <section class="empty-hero">
        <div class="empty-copy">
          <h2>你还没有创建任何待办</h2>
          <p>创建你的第一个待办，开始规划你的时间，让每一天都更有条理，更有成就感！</p>
          <button class="starter-button" type="button" @click="openCreateModal()">+ 创建第一个事项</button>
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
          <button class="starter-card green" type="button" @click="openCreateModal('schedule')">
            <span class="starter-icon">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 2v4M16 2v4M3.5 9.5h17M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Zm3 9h3v3H8z" /></svg>
            </span>
            <strong>创建日程</strong>
            <small>安排你的会议、学习、约会等</small>
            <em>新建日程 →</em>
          </button>
          <button class="starter-card amber" type="button" @click="openCreateModal('task')">
            <span class="starter-icon">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 11l2 2 4-5M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" /></svg>
            </span>
            <strong>创建任务</strong>
            <small>把有明确时间的事情纳入计划</small>
            <em>创建任务 →</em>
          </button>
          <button class="starter-card teal" type="button" @click="openCreateModal('habit')">
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

    <template v-else>
      <section class="dashboard-grid">
        <article class="dashboard-panel todo-panel">
            <div class="panel-head">
              <div class="panel-title">
                <span class="panel-icon blue" aria-hidden="true">
                  <svg viewBox="0 0 24 24"><path d="M9 11l2 2 4-5M20 6v14H4V4h11" /></svg>
                </span>
                <div>
                  <h2>今天待办</h2>
                  <p>优先处理今天要推进的事情</p>
                </div>
              </div>
            </div>
            <div v-if="todayTodos.length" class="clean-list">
              <div v-for="item in todayTodos" :key="item.key" class="clean-item">
                <span :class="['kind-dot', item.kind]"></span>
                <div>
                  <strong>{{ item.title }}</strong>
                  <small>{{ item.meta }}</small>
                </div>
              </div>
            </div>
            <p v-else class="soft-empty">今天还没有待办，可以从右上角添加一个。</p>
        </article>

        <article class="dashboard-panel task-panel">
            <div class="panel-head">
              <div class="panel-title">
                <span class="panel-icon violet" aria-hidden="true">
                  <svg viewBox="0 0 24 24"><path d="M12 3v18M5 8h14M7 16h10" /></svg>
                </span>
                <div>
                  <h2>今日任务</h2>
                  <p>今天要推进的任务</p>
                </div>
              </div>
            </div>
            <div v-if="todayTasks.length" class="clean-list">
              <div v-for="item in todayTasks" :key="item.key" class="clean-item">
                <span class="kind-dot task"></span>
                <div>
                  <strong>{{ item.title }}</strong>
                  <small>{{ formatTimeRange(item.startTime, item.finishTime) || item.content || "今日任务" }}</small>
                </div>
              </div>
            </div>
            <p v-else class="soft-empty">今天还没有任务。</p>
        </article>

        <article class="dashboard-panel schedule-panel">
            <div class="panel-head">
              <div class="panel-title">
                <span class="panel-icon blue" aria-hidden="true">
                  <svg viewBox="0 0 24 24"><path d="M8 2v4M16 2v4M3.5 9.5h17M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z" /></svg>
                </span>
                <div>
                  <h2>即将到来的行程</h2>
                  <p>按开始时间排序</p>
                </div>
              </div>
            </div>
            <div v-if="upcomingSchedules.length" class="schedule-list">
              <div v-for="item in upcomingSchedules" :key="item.key" class="schedule-card">
                <time>{{ formatShortDate(item.startTime) }}</time>
                <div>
                  <strong>{{ item.title }}</strong>
                  <small>{{ item.location || item.content || "未填写地点" }}</small>
                </div>
              </div>
            </div>
            <p v-else class="soft-empty">暂无即将到来的行程。</p>
        </article>

        <article class="dashboard-panel habit-panel">
            <div class="panel-head">
              <div class="panel-title">
                <span class="panel-icon green" aria-hidden="true">
                  <svg viewBox="0 0 24 24"><path d="M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18ZM8.5 12l2.2 2.2 4.8-5" /></svg>
                </span>
                <div>
                  <h2>习惯打卡</h2>
                  <p>保持今天的节奏</p>
                </div>
              </div>
            </div>
            <div v-if="todayHabits.length" class="habit-list">
              <div v-for="item in todayHabits" :key="item.key" class="habit-item">
                <span class="habit-check" aria-hidden="true"></span>
                <div>
                  <strong>{{ item.title }}</strong>
                  <small>{{ item.content || `每日 ${item.dayMinutes || 0} 分钟` }}</small>
                </div>
              </div>
            </div>
            <p v-else class="soft-empty">还没有习惯，可以在左侧“习惯”里创建。</p>
        </article>
      </section>
    </template>

    <div v-if="modalOpen" class="modal-backdrop" @click.self="closeModal">
      <form class="create-modal" @submit.prevent="submitCreate">
        <div class="modal-head">
          <div>
            <h2>添加事项</h2>
            <p>选择一种类型，创建后会立即显示在首页。</p>
          </div>
          <button class="modal-close" type="button" aria-label="关闭" @click="closeModal">×</button>
        </div>

        <div class="type-picker" role="tablist" aria-label="事项类型">
          <button
            v-for="type in createTypes"
            :key="type.value"
            :class="['type-option', { active: selectedType === type.value }]"
            type="button"
            @click="selectedType = type.value"
          >
            <strong>{{ type.label }}</strong>
            <small>{{ type.description }}</small>
          </button>
        </div>

        <div class="modal-fields">
          <label>
            标题
            <input v-model.trim="createForm.title" placeholder="例如：整理项目资料" />
          </label>
          <label>
            内容
            <textarea v-model.trim="createForm.content" placeholder="补充一点说明，方便之后继续处理"></textarea>
          </label>
          <label v-if="selectedType === 'task'">
            任务类型
            <input v-model.trim="createForm.taskType" placeholder="normal / long / once" />
          </label>
          <label v-if="selectedType === 'schedule'">
            地点
            <input v-model.trim="createForm.location" placeholder="例如：会议室 A" />
          </label>
          <div v-if="selectedType === 'habit'" class="modal-row three">
            <label>
              每日分钟
              <input v-model.number="createForm.dayMinutes" min="0" placeholder="30" type="number" />
            </label>
            <label>
              最少分钟
              <input v-model.number="createForm.minMinutes" min="0" placeholder="10" type="number" />
            </label>
            <label>
              最多分钟
              <input v-model.number="createForm.maxMinutes" min="0" placeholder="60" type="number" />
            </label>
          </div>
          <div v-if="selectedType === 'task' || selectedType === 'schedule'" class="modal-row">
            <label>
              开始时间
              <input v-model="createForm.startTime" type="datetime-local" />
            </label>
            <label>
              结束时间
              <input v-model="createForm.finishTime" type="datetime-local" />
            </label>
          </div>
        </div>

        <p :class="['modal-status', createOk ? 'ok' : 'err']">{{ createStatus }}</p>
        <div class="modal-actions">
          <button class="secondary" type="button" @click="closeModal">取消</button>
          <button :disabled="submitting" type="submit">{{ submitting ? "创建中..." : currentCreateLabel }}</button>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { backlogApi, habitApi, scheduleApi, taskApi } from "@/api/todoApi";
import { authState } from "@/stores/auth";

const cachedDashboard = readDashboardCache();
const lists = reactive({
  backlogs: cachedDashboard?.backlogs || [],
  tasks: cachedDashboard?.tasks || [],
  schedules: cachedDashboard?.schedules || [],
  habits: cachedDashboard?.habits || []
});

const loading = ref(false);
const initialLoading = ref(!cachedDashboard);
const submitting = ref(false);
const modalOpen = ref(false);
const selectedType = ref("backlog");
const loadError = ref("");
const createStatus = ref("");
const createOk = ref(false);

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
const hasAnyData = computed(() => lists.backlogs.length + lists.tasks.length + lists.schedules.length + lists.habits.length > 0);
const currentCreateLabel = computed(() => createTypes.find((item) => item.value === selectedType.value)?.label || "创建");

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

function parseDate(value) {
  if (!value) return new Date(0);
  return new Date(String(value).replace(" ", "T"));
}

function dateKey(value) {
  const date = parseDate(value);
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
}

function isToday(value) {
  return value && dateKey(value) === dateKey(new Date());
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
  const [backlogResult, taskResult, scheduleResult, habitResult] = await Promise.allSettled([
    backlogApi.list(),
    taskApi.list(),
    scheduleApi.list(),
    habitApi.list()
  ]);

  if (backlogResult.status === "fulfilled") lists.backlogs = Array.isArray(backlogResult.value?.data) ? backlogResult.value.data : [];
  if (taskResult.status === "fulfilled") lists.tasks = Array.isArray(taskResult.value?.data) ? taskResult.value.data : [];
  if (scheduleResult.status === "fulfilled") lists.schedules = Array.isArray(scheduleResult.value?.data) ? scheduleResult.value.data : [];
  if (habitResult.status === "fulfilled") lists.habits = Array.isArray(habitResult.value?.data) ? habitResult.value.data : [];
  cacheDashboard();

  const failed = [backlogResult, taskResult, scheduleResult, habitResult].some((item) => item.status === "rejected");
  if (failed && !options.silent) loadError.value = "部分数据暂时加载失败，请稍后刷新页面。";
  loading.value = false;
  initialLoading.value = false;
}

onMounted(() => {
  loadDashboard();
});

function readDashboardCache() {
  try {
    const cached = JSON.parse(sessionStorage.getItem("todo-home-dashboard") || "null");
    if (!cached) return false;
    return {
      backlogs: Array.isArray(cached.backlogs) ? cached.backlogs : [],
      tasks: Array.isArray(cached.tasks) ? cached.tasks : [],
      schedules: Array.isArray(cached.schedules) ? cached.schedules : [],
      habits: Array.isArray(cached.habits) ? cached.habits : []
    };
  } catch {
    return null;
  }
}

function cacheDashboard() {
  sessionStorage.setItem("todo-home-dashboard", JSON.stringify({
    backlogs: lists.backlogs,
    tasks: lists.tasks,
    schedules: lists.schedules,
    habits: lists.habits
  }));
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding: 44px clamp(22px, 4vw, 48px) 34px;
  background:
    radial-gradient(circle at 58% 17%, rgba(74, 120, 255, .08), transparent 0 86px),
    radial-gradient(circle at 93% 35%, rgba(74, 120, 255, .07), transparent 0 72px),
    linear-gradient(135deg, #f7fbff 0%, #eef5ff 46%, #fbfdff 100%);
}

.home-header {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-start;
  max-width: 1320px;
  margin: 0 auto 30px;
}

.home-header h1 {
  color: #111a33;
  font-size: clamp(28px, 3.1vw, 40px);
  line-height: 1.2;
  letter-spacing: 0;
}

.home-header p {
  margin-top: 20px;
  color: #64718a;
  font-size: 17px;
  font-weight: 700;
}

.home-actions {
  display: flex;
  gap: 18px;
  align-items: center;
}

.icon-button,
.primary-add,
.modal-close {
  display: inline-grid;
  place-items: center;
}

.icon-button {
  width: 42px;
  min-height: 42px;
  border: 0;
  border-radius: 12px;
  padding: 0;
  color: #111a33;
  background: transparent;
}

.icon-button:hover {
  background: rgba(37, 99, 235, .08);
}

.icon-button svg,
.primary-add svg,
.starter-icon svg {
  width: 23px;
  height: 23px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.primary-add {
  grid-auto-flow: column;
  gap: 10px;
  min-height: 52px;
  border: 0;
  border-radius: 9px;
  padding: 0 22px;
  color: #fff;
  background: #2f6df6;
  box-shadow: 0 16px 28px rgba(47, 109, 246, .22);
  font-size: 16px;
  font-weight: 800;
}

.primary-add:hover,
.starter-button:hover {
  background: #1f5bea;
}

.primary-add svg {
  width: 17px;
  height: 17px;
}

.home-alert {
  max-width: 1320px;
  margin: -24px auto 24px;
  border: 1px solid #fed7aa;
  border-radius: 10px;
  padding: 12px 14px;
  color: #9a3412;
  background: #fff7ed;
}

.empty-hero,
.starter-section,
.dashboard-grid,
.loading-panel {
  max-width: 1320px;
  margin: 0 auto;
}

.loading-panel {
  min-height: 320px;
  display: grid;
  place-items: center;
  align-content: center;
  gap: 16px;
  border: 1px solid rgba(226, 232, 240, .88);
  border-radius: 14px;
  background: rgba(255, 255, 255, .86);
  box-shadow: 0 18px 38px rgba(47, 109, 246, .08);
}

.loading-panel span {
  width: 36px;
  height: 36px;
  border: 4px solid #dbe7ff;
  border-top-color: #2f6df6;
  border-radius: 50%;
  animation: home-spin .8s linear infinite;
}

.loading-panel p {
  color: #71809a;
  font-weight: 800;
}

@keyframes home-spin {
  to { transform: rotate(360deg); }
}

.empty-hero {
  position: relative;
  min-height: 468px;
  display: grid;
  grid-template-columns: minmax(0, .95fr) minmax(360px, 1.05fr);
  gap: 16px;
  overflow: hidden;
  border: 1px solid rgba(226, 232, 240, .88);
  border-radius: 14px;
  padding: 76px 42px 48px;
  background: rgba(255, 255, 255, .92);
  box-shadow: 0 22px 48px rgba(47, 109, 246, .10);
}

.empty-copy {
  position: relative;
  z-index: 2;
  display: grid;
  align-content: start;
  gap: 20px;
  padding-left: 4px;
}

.empty-copy h2 {
  color: #121a2f;
  font-size: clamp(24px, 2.4vw, 32px);
  line-height: 1.22;
}

.empty-copy p {
  max-width: 430px;
  color: #68758f;
  font-size: 16px;
  font-weight: 700;
  line-height: 2;
}

.starter-button {
  justify-self: start;
  min-width: 214px;
  min-height: 56px;
  margin-top: 24px;
  border: 0;
  border-radius: 8px;
  padding: 0 24px;
  background: #2f6df6;
  box-shadow: 0 16px 28px rgba(47, 109, 246, .24);
  font-size: 16px;
  font-weight: 800;
}

.starter-hint {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  margin-top: 28px;
  color: #64718a;
  font-weight: 800;
}

.starter-hint i {
  width: 96px;
  height: 58px;
  border-bottom: 2px solid #9aa7bd;
  border-right: 2px solid #9aa7bd;
  border-radius: 0 0 70px 0;
  transform: rotate(20deg);
}

.empty-art {
  position: relative;
  min-height: 340px;
}

.art-glow {
  position: absolute;
  right: 54px;
  top: 14px;
  width: 352px;
  height: 302px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(47, 109, 246, .12), rgba(47, 109, 246, .02));
}

.art-clipboard {
  position: absolute;
  right: 112px;
  top: 52px;
  width: 236px;
  height: 272px;
  border: 10px solid #78a2ff;
  border-radius: 18px;
  background: #fff;
  box-shadow: 22px 30px 44px rgba(47, 109, 246, .18);
  transform: rotate(3deg);
}

.art-clipboard b {
  position: absolute;
  left: 58px;
  top: -38px;
  width: 120px;
  height: 52px;
  border-radius: 14px 14px 9px 9px;
  background: linear-gradient(180deg, #7aa2ff, #3f70ec);
  box-shadow: 0 14px 22px rgba(47, 109, 246, .22);
}

.art-clipboard b::after {
  content: "";
  position: absolute;
  left: 50%;
  top: -13px;
  width: 20px;
  height: 20px;
  border: 8px solid #7aa2ff;
  border-radius: 50%;
  background: #eef5ff;
  transform: translateX(-50%);
}

.art-clipboard span {
  position: relative;
  display: block;
  height: 23px;
  margin: 28px 38px;
  border-radius: 7px;
  background: #edf2ff;
}

.art-clipboard span::before {
  content: "";
  position: absolute;
  left: -36px;
  top: -3px;
  width: 26px;
  height: 26px;
  border-radius: 7px;
  background: #dfe8ff;
  box-shadow: inset 0 0 0 6px #edf3ff;
}

.art-plant {
  position: absolute;
  left: 86px;
  bottom: 12px;
  width: 58px;
  height: 78px;
  border-radius: 8px 8px 18px 18px;
  background: #fff;
  box-shadow: 10px 18px 34px rgba(82, 117, 177, .18);
}

.art-plant::before {
  content: "";
  position: absolute;
  left: 18px;
  bottom: 54px;
  width: 28px;
  height: 72px;
  border-radius: 28px 28px 6px 28px;
  background: #64cc7a;
  box-shadow: -32px 28px 0 #54c477, 30px 18px 0 #7edb8f;
}

.art-pen {
  position: absolute;
  right: 34px;
  bottom: 66px;
  width: 32px;
  height: 188px;
  border-radius: 18px;
  background: linear-gradient(180deg, #557df6, #8fa8ff 72%, #f6f8ff 72%);
  box-shadow: 13px 20px 28px rgba(47, 109, 246, .20);
  transform: rotate(39deg);
}

.art-pen::after {
  content: "";
  position: absolute;
  left: 6px;
  bottom: -28px;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-top: 34px solid #5d6f90;
}

.art-dot {
  position: absolute;
  z-index: 2;
  width: 14px;
  height: 14px;
}

.art-dot::before,
.art-dot::after {
  content: "";
  position: absolute;
  background: #a9c0ff;
  border-radius: 999px;
}

.art-dot::before {
  left: 5px;
  top: 0;
  width: 4px;
  height: 14px;
}

.art-dot::after {
  left: 0;
  top: 5px;
  width: 14px;
  height: 4px;
}

.dot-a { left: 32px; top: 92px; }
.dot-b { right: 84px; top: 116px; }
.dot-c { left: 12px; bottom: 96px; }

.starter-section {
  margin-top: 30px;
  padding: 26px 12px;
  border-radius: 14px;
  background: rgba(255, 255, 255, .72);
  box-shadow: 0 16px 38px rgba(47, 109, 246, .07);
}

.starter-section h2 {
  margin: 0 0 20px 12px;
  color: #111a33;
  font-size: 18px;
}

.starter-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
}

.starter-card {
  display: grid;
  grid-template-columns: 80px 1fr;
  gap: 8px 20px;
  align-items: center;
  min-height: 178px;
  border: 1px solid #e1e7f0;
  border-radius: 14px;
  padding: 24px 22px;
  color: #1d2638;
  background: #fff;
  text-align: left;
  box-shadow: none;
}

.starter-card:hover {
  border-color: #c7d7ff;
  background: #fff;
  box-shadow: 0 18px 30px rgba(47, 109, 246, .08);
}

.starter-icon {
  grid-row: span 3;
  display: grid;
  place-items: center;
  width: 72px;
  height: 72px;
  border-radius: 50%;
}

.starter-card strong {
  font-size: 17px;
}

.starter-card small {
  max-width: 190px;
  color: #66748e;
  line-height: 1.7;
}

.starter-card em {
  align-self: end;
  justify-self: stretch;
  display: grid;
  place-items: center;
  min-height: 44px;
  border-radius: 9px;
  font-style: normal;
  font-weight: 800;
}

.starter-card.green .starter-icon,
.starter-card.green em {
  color: #2dbb72;
  background: #e8f9ef;
}

.starter-card.amber .starter-icon,
.starter-card.amber em {
  color: #f59e0b;
  background: #fff3d9;
}

.starter-card.violet .starter-icon,
.starter-card.violet em {
  color: #7255df;
  background: #eee9ff;
}

.starter-card.teal .starter-icon,
.starter-card.teal em {
  color: #14a67b;
  background: #e7f8f2;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(420px, 1.15fr) minmax(360px, .85fr);
  grid-template-areas:
    "todo schedule"
    "task habit";
  gap: 22px;
  align-items: stretch;
}

.dashboard-panel {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  min-height: 0;
  border: 1px solid #e1e7f0;
  border-radius: 16px;
  padding: 24px 26px;
  background: rgba(255, 255, 255, .96);
  box-shadow: 0 18px 42px rgba(47, 109, 246, .09);
}

.todo-panel { grid-area: todo; }
.task-panel { grid-area: task; }
.schedule-panel { grid-area: schedule; }
.habit-panel { grid-area: habit; }

.todo-panel,
.schedule-panel {
  min-height: 354px;
}

.task-panel,
.habit-panel {
  min-height: 182px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-start;
  margin-bottom: 22px;
}

.panel-title {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 14px;
  align-items: start;
}

.panel-icon {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  border-radius: 8px;
}

.panel-icon svg {
  width: 20px;
  height: 20px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.panel-icon.blue {
  color: #1f6fff;
  background: #edf4ff;
}

.panel-icon.violet {
  color: #7b55e7;
  background: #f1edff;
}

.panel-icon.green {
  color: #25b86f;
  background: #eaf9f1;
}

.panel-head h2 {
  color: #111a33;
  font-size: 22px;
  line-height: 1.2;
}

.panel-head p,
.soft-empty {
  margin-top: 7px;
  color: #71809a;
}

.clean-list,
.schedule-list,
.habit-list {
  display: grid;
  align-content: start;
  gap: 12px;
  min-height: 0;
  max-height: 310px;
  overflow: auto;
  padding-right: 4px;
}

.task-panel .clean-list,
.habit-list {
  max-height: 190px;
}

.clean-item,
.schedule-card {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 14px;
  align-items: center;
  min-height: 68px;
  border: 1px solid #edf1f7;
  border-radius: 12px;
  padding: 14px;
  background: #fff;
}

.clean-item strong,
.schedule-card strong {
  display: block;
  color: #1d2638;
  overflow-wrap: anywhere;
}

.clean-item small,
.schedule-card small {
  display: block;
  margin-top: 4px;
  color: #71809a;
  overflow-wrap: anywhere;
}

.kind-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
}

.kind-dot.task { background: #2f6df6; }
.kind-dot.backlog { background: #35c47a; }

.schedule-card time {
  display: grid;
  place-items: center;
  width: 78px;
  min-height: 48px;
  border-radius: 10px;
  color: #2f6df6;
  background: #eef4ff;
  font-weight: 700;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.overview-item {
  min-height: 96px;
  display: grid;
  align-content: center;
  gap: 6px;
  border-radius: 12px;
  padding: 16px;
  background: #f7faff;
}

.overview-item strong {
  color: #111a33;
  font-size: 30px;
  line-height: 1;
}

.overview-item span {
  color: #71809a;
  font-weight: 700;
}

.habit-item {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 14px;
  align-items: center;
  min-height: 64px;
  border: 1px solid #edf1f7;
  border-radius: 12px;
  padding: 14px;
  background: #fff;
}

.habit-item strong {
  display: block;
  color: #1d2638;
  overflow-wrap: anywhere;
}

.habit-item small {
  display: block;
  margin-top: 4px;
  color: #71809a;
  overflow-wrap: anywhere;
}

.habit-check {
  width: 22px;
  height: 22px;
  border: 2px solid #35c47a;
  border-radius: 50%;
  background: #f0fdf6;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 20;
  display: grid;
  place-items: center;
  padding: 22px;
  background: rgba(15, 23, 42, .36);
  backdrop-filter: blur(7px);
}

.create-modal {
  width: min(680px, 100%);
  max-height: min(860px, calc(100vh - 44px));
  overflow: auto;
  display: grid;
  gap: 20px;
  border-radius: 18px;
  padding: 26px;
  background: #fff;
  box-shadow: 0 28px 70px rgba(15, 23, 42, .20);
}

.modal-head {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-start;
}

.modal-head h2 {
  color: #111a33;
  font-size: 24px;
}

.modal-head p {
  margin-top: 8px;
  color: #71809a;
}

.modal-close {
  width: 38px;
  min-height: 38px;
  border: 0;
  border-radius: 10px;
  padding: 0;
  color: #475569;
  background: #f1f5f9;
  font-size: 24px;
}

.modal-close:hover {
  background: #e2e8f0;
}

.type-picker {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.type-option {
  display: grid;
  gap: 5px;
  min-height: 86px;
  border: 1px solid #dbe4f0;
  border-radius: 12px;
  padding: 14px;
  color: #1d2638;
  background: #fff;
  text-align: left;
}

.type-option:hover,
.type-option.active {
  border-color: #2f6df6;
  color: #2f6df6;
  background: #f4f8ff;
}

.type-option small {
  color: #71809a;
  line-height: 1.45;
}

.modal-fields {
  display: grid;
  gap: 14px;
}

.modal-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.modal-row.three {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.modal-status {
  min-height: 22px;
  color: #b91c1c;
}

.modal-status.ok {
  color: #047857;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 960px) {
  .home-page {
    padding-top: 28px;
  }

  .home-header,
  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-grid {
    grid-template-areas:
      "todo"
      "task"
      "schedule"
      "habit";
  }

  .todo-panel,
  .schedule-panel,
  .task-panel,
  .habit-panel {
    min-height: 0;
  }

  .home-header {
    display: grid;
  }

  .empty-hero {
    grid-template-columns: 1fr;
    padding: 38px 24px;
  }

  .empty-art {
    min-height: 300px;
  }

  .starter-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .home-actions,
  .modal-actions {
    width: 100%;
  }

  .primary-add,
  .modal-actions button {
    flex: 1;
  }

  .type-picker,
  .modal-row,
  .overview-grid {
    grid-template-columns: 1fr;
  }

  .modal-row.three {
    grid-template-columns: 1fr;
  }

  .empty-hero {
    min-height: 0;
  }

  .empty-art {
    transform: scale(.82);
    transform-origin: top center;
    min-height: 260px;
  }

  .starter-card {
    grid-template-columns: 68px 1fr;
  }

}
</style>
