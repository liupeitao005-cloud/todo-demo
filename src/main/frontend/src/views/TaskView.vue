<template>
  <section class="task-page">
    <header class="task-hero">
      <div>
        <h1>任务管理</h1>
        <p>聚焦今天最重要的事项</p>
      </div>
      <button class="task-primary-button" type="button" @click="openCreate()">
        <span class="button-icon">+</span>
        新建任务
      </button>
    </header>

    <div class="task-stats">
      <article v-for="stat in stats" :key="stat.label" class="task-stat-card">
        <span :class="['stat-icon', stat.tone]">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path :d="stat.icon" />
          </svg>
        </span>
        <div>
          <span class="stat-label">{{ stat.label }}</span>
          <strong>{{ stat.value }}</strong>
          <small>
            {{ stat.caption }}
            <i :class="['stat-dot', stat.dot]"></i>
          </small>
        </div>
      </article>
    </div>

    <div class="task-board">
      <section class="task-list-panel">
        <div class="task-toolbar">
          <label class="task-search">
            <span class="sr-only">搜索任务</span>
            <input v-model.trim="keyword" placeholder="按任务标题搜索" />
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="m21 21-4.3-4.3M10.8 18a7.2 7.2 0 1 1 0-14.4 7.2 7.2 0 0 1 0 14.4Z" />
            </svg>
          </label>
          <div class="task-tabs" aria-label="任务筛选">
            <button
              v-for="filter in filters"
              :key="filter.value"
              :class="{ active: activeFilter === filter.value }"
              type="button"
              @click="activeFilter = filter.value"
            >
              {{ filter.label }}
            </button>
          </div>
        </div>

        <div v-if="loading" class="task-empty">正在加载任务...</div>
        <div v-else-if="filteredTasks.length" class="task-list">
          <button
            v-for="task in filteredTasks"
            :key="task.id"
            :class="['task-row', { active: selectedTask?.id === task.id, done: isFinished(task), next: isNext(task), overdue: isOverdue(task) }]"
            type="button"
            @click="selectTask(task)"
          >
            <div class="task-row-main">
              <span :class="['task-star', { done: isFinished(task) }]" aria-hidden="true">{{ isFinished(task) ? "✓" : "★" }}</span>
              <div>
                <h3 class="task-title-line">
                  <span>{{ task.title || "未命名任务" }}</span>
                  <em v-if="isFinished(task)" class="task-status-pill done">已完成</em>
                  <em v-else-if="isNext(task)" class="task-status-pill next">下一个重点</em>
                  <em v-else-if="isOverdue(task)" class="task-status-pill overdue">已超时</em>
                </h3>
                <p>{{ task.content || "暂无任务说明。" }}</p>
              </div>
            </div>
            <span :class="['task-type-pill', taskTone(task)]">{{ taskTypeText(task) }}</span>
            <div class="task-row-meta">
              <span>ID: {{ displayId(task.id) }}</span>
              <span>父任务: {{ parentText(task) }}</span>
              <span class="task-time-text">{{ formatTimeRange(task.startTime, task.finishTime) }}</span>
              <span class="task-progress-label">子任务 {{ progressText(task) }}</span>
              <span class="mini-progress" aria-hidden="true">
                <i :style="{ width: `${progressPercent(task)}%` }"></i>
              </span>
            </div>
          </button>
        </div>
        <div v-else class="task-empty">
          没有匹配的任务，换个筛选条件试试。
        </div>
      </section>

      <aside class="task-detail-panel">
        <template v-if="selectedTask">
          <div class="detail-head">
            <div class="detail-title">
              <h2>任务详情</h2>
              <span v-if="isFinished(selectedTask)" class="task-status-pill done">已完成</span>
              <span v-else-if="isNext(selectedTask)" class="task-status-pill next">下一个重点</span>
              <span v-else-if="isOverdue(selectedTask)" class="task-status-pill overdue">已超时</span>
            </div>
            <button class="icon-button" type="button" title="刷新列表" @click="loadList">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M20 12a8 8 0 1 1-2.3-5.7M20 4v6h-6" />
              </svg>
            </button>
          </div>

          <div class="detail-section">
            <span class="detail-label">标题</span>
            <h3>
              <span :class="['task-star', { done: isFinished(selectedTask) }]" aria-hidden="true">{{ isFinished(selectedTask) ? "✓" : "★" }}</span>
              {{ selectedTask.title || "未命名任务" }}
            </h3>
          </div>

          <div class="detail-section">
            <span class="detail-label">任务说明</span>
            <p>{{ selectedTask.content || "暂无任务说明。" }}</p>
          </div>

          <div class="detail-grid">
            <div>
              <span class="detail-label">任务类型</span>
              <span :class="['task-type-pill', taskTone(selectedTask)]">{{ taskTypeText(selectedTask) }}</span>
            </div>
            <div>
              <span class="detail-label">任务ID</span>
              <strong>{{ displayId(selectedTask.id) }}</strong>
            </div>
            <div>
              <span class="detail-label">父任务ID</span>
              <strong>{{ parentText(selectedTask) }}</strong>
            </div>
            <div>
              <span class="detail-label">开始时间</span>
              <strong>{{ formatDateTime(selectedTask.startTime) }}</strong>
            </div>
            <div>
              <span class="detail-label">结束时间</span>
              <strong>{{ formatDateTime(selectedTask.finishTime) }}</strong>
            </div>
            <div>
              <span class="detail-label">子任务进度</span>
              <div class="detail-progress">
                <span>{{ progressText(selectedTask) }}</span>
                <span class="mini-progress">
                  <i :style="{ width: `${progressPercent(selectedTask)}%` }"></i>
                </span>
                <span>{{ progressPercent(selectedTask) }}%</span>
              </div>
            </div>
          </div>

          <div class="subtask-card">
            <div class="subtask-head">
              <h3>子任务列表</h3>
              <button class="subtask-add" type="button" @click="openCreate(selectedTask.id)">+ 添加子任务</button>
            </div>
            <div v-if="childTasks(selectedTask).length" class="subtask-list">
              <button
                v-for="child in childTasks(selectedTask)"
                :key="child.id"
                class="subtask-row"
                type="button"
                @click="selectTask(child)"
              >
                <span :class="['check-box', { checked: isFinished(child) }]">
                  <svg v-if="isFinished(child)" viewBox="0 0 24 24" aria-hidden="true"><path d="m5 12 4 4 10-10" /></svg>
                </span>
                <span>{{ child.title || "未命名子任务" }}</span>
                <em :class="['subtask-status', isFinished(child) ? 'done' : 'todo']">
                  {{ isFinished(child) ? "已完成" : "待开始" }}
                </em>
              </button>
            </div>
            <p v-else class="subtask-empty">还没有子任务，可以点“添加子任务”拆出下一步。</p>
          </div>

          <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
          <div class="detail-actions">
            <button class="secondary action-button" type="button" @click="openEdit(selectedTask)">编辑</button>
            <button class="success action-button" type="button" :disabled="isFinished(selectedTask)" @click="finish">
              {{ isFinished(selectedTask) ? "已完成" : "完成" }}
            </button>
            <button class="secondary action-button" type="button" @click="split">拆分</button>
            <button class="secondary warn action-button" type="button" :disabled="isFinished(selectedTask) || !isOverdue(selectedTask)" @click="delay">延期</button>
            <button class="secondary action-button" type="button" :disabled="isFinished(selectedTask) || isNext(selectedTask)" @click="next">
              {{ isNext(selectedTask) && !isFinished(selectedTask) ? "已是重点" : "设为下一步" }}
            </button>
            <button class="danger soft-danger action-button" type="button" @click="remove">删除</button>
          </div>
        </template>
        <div v-else class="task-empty">选择一个任务查看详情。</div>
      </aside>
    </div>

    <div v-if="editorOpen" class="modal-backdrop" @click.self="closeEditor">
      <form class="task-modal" @submit.prevent="saveTask">
        <div class="modal-head">
          <h2>{{ form.id ? "编辑任务" : "新建任务" }}</h2>
          <button class="icon-button" type="button" title="关闭" @click="closeEditor">×</button>
        </div>
        <label>
          标题
          <input v-model.trim="form.title" placeholder="例如：完成产品需求文档评审" />
        </label>
        <label>
          任务说明
          <textarea v-model.trim="form.content" placeholder="写清楚这件事的目标、交付物和下一步。"></textarea>
        </label>
        <div class="form-row">
          <label>
            任务类型
            <select v-model="form.taskType">
              <option v-for="type in taskTypeOptions" :key="type.value" :value="type.value">
                {{ type.label }}
              </option>
            </select>
          </label>
          <label>
            父任务ID
            <input v-model.number="form.parentId" type="number" min="0" placeholder="没有父任务则填 0" />
          </label>
        </div>
        <div class="form-row">
          <label>
            开始时间
            <input v-model="form.startTime" type="datetime-local" />
          </label>
          <label>
            结束时间
            <input v-model="form.finishTime" type="datetime-local" />
          </label>
        </div>
        <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
        <div class="modal-actions">
          <button class="secondary" type="button" @click="closeEditor">取消</button>
          <button :disabled="loading" type="submit">{{ form.id ? "保存修改" : "创建任务" }}</button>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { taskApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const tasks = ref([]);
const selectedId = ref(null);
const keyword = ref("");
const activeFilter = ref("all");
const editorOpen = ref(false);
const { loading, status, ok, run } = useRequest();

const form = reactive({
  id: null,
  title: "",
  content: "",
  taskType: "short",
  parentId: 0,
  startTime: "",
  finishTime: ""
});

const iconPaths = {
  document: "M7 3h7l4 4v14H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Zm7 0v5h5M9 13h6M9 17h4",
  star: "m12 3 2.7 5.5 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.8 1-6.1-4.4-4.3 6.1-.9L12 3Z",
  flag: "M6 21V4h10l1 3h-7v8H6",
  clock: "M12 22a10 10 0 1 1 0-20 10 10 0 0 1 0 20Zm0-14v5l3 2",
  repeat: "M17 2l4 4-4 4M3 11V9a3 3 0 0 1 3-3h15M7 22l-4-4 4-4M21 13v2a3 3 0 0 1-3 3H3"
};

const taskTypeOptions = [
  { label: "一次性任务", value: "once", tone: "once", caption: "只需完成一次", icon: iconPaths.document },
  { label: "短期任务", value: "short", tone: "short", caption: "短周期内完成", icon: iconPaths.clock },
  { label: "长期任务", value: "long", tone: "long", caption: "持续推进", icon: iconPaths.flag },
  { label: "重复性任务", value: "repeat", tone: "repeat", caption: "固定周期重复", icon: iconPaths.repeat }
];

const filters = [
  { label: "全部", value: "all" },
  { label: "进行中", value: "active" },
  { label: "已完成", value: "done" },
  { label: "下一个重点", value: "next" },
  ...taskTypeOptions.map((type) => ({ label: type.label, value: type.value }))
];

const selectedTask = computed(() => tasks.value.find((item) => item.id === selectedId.value) || tasks.value[0] || null);
const topLevelTasks = computed(() => tasks.value.filter((task) => !Number(task.parentId)));

const stats = computed(() => taskTypeOptions.map((type) => ({
  label: type.label,
  value: tasks.value.filter((task) => normalizeTaskType(task.taskType) === type.value).length,
  caption: type.caption,
  tone: type.tone,
  dot: type.tone,
  icon: type.icon
})));

const filteredTasks = computed(() => {
  const term = keyword.value.toLowerCase();
  return topLevelTasks.value.filter((task) => {
    const matchesKeyword = !term || String(task.title || "").toLowerCase().includes(term) || String(task.content || "").toLowerCase().includes(term);
    const matchesFilter = matchesTaskFilter(task, activeFilter.value);
    return matchesKeyword && matchesFilter;
  });
});

watch(filteredTasks, (list) => {
  if (!list.length) return;
  if (!list.some((task) => task.id === selectedId.value)) selectedId.value = list[0].id;
});

function isFinished(task) {
  return Number(task?.isFinish || 0) === 1;
}

function isNext(task) {
  return Number(task?.isNext || 0) === 1 || task?.taskType === "next";
}

function isOverdue(task) {
  if (!task?.finishTime || isFinished(task)) return false;
  return new Date(task.finishTime).getTime() < Date.now();
}

function matchesTaskFilter(task, filter) {
  if (filter === "all") return true;
  if (filter === "active") return !isFinished(task);
  if (filter === "done") return isFinished(task);
  if (filter === "next") return isNext(task) && !isFinished(task);
  return normalizeTaskType(task.taskType) === filter;
}

function childTasks(task) {
  if (!task?.id) return [];
  return tasks.value.filter((item) => Number(item.parentId || 0) === Number(task.id));
}

function progressText(task) {
  const children = childTasks(task);
  if (!children.length) return "0/0";
  return `${children.filter(isFinished).length}/${children.length}`;
}

function progressPercent(task) {
  const children = childTasks(task);
  if (!children.length) return 0;
  return Math.round((children.filter(isFinished).length / children.length) * 100);
}

function taskTypeText(task) {
  return taskTypeOptions.find((type) => type.value === normalizeTaskType(task?.taskType))?.label || "短期任务";
}

function taskTone(task) {
  return taskTypeOptions.find((type) => type.value === normalizeTaskType(task?.taskType))?.tone || "short";
}

function normalizeTaskType(value) {
  const map = {
    once: "once",
    short: "short",
    normal: "short",
    next: "short",
    long: "long",
    repeat: "repeat",
    "一次性任务": "once",
    "短期任务": "short",
    "长期任务": "long",
    "重复性任务": "repeat"
  };
  return map[value] || "short";
}

function displayId(id) {
  if (!id) return "未保存";
  return `T-${String(id).padStart(4, "0")}`;
}

function parentText(task) {
  const parentId = Number(task?.parentId || 0);
  return parentId ? displayId(parentId) : "-";
}

function formatDateTime(value) {
  if (!value) return "未安排";
  return String(value).replace("T", " ").slice(0, 16);
}

function formatTimeRange(start, finish) {
  if (!start && !finish) return "未安排时间";
  if (!finish) return `${formatDateTime(start)} 开始`;
  if (!start) return `${formatDateTime(finish)} 截止`;
  return `${formatDateTime(start)} - ${formatDateTime(finish).slice(11)}`;
}

function normalizeDateTime(value) {
  if (!value) return "";
  return String(value).slice(0, 16);
}

function toLocalInputValue(date) {
  const offsetDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return offsetDate.toISOString().slice(0, 16);
}

function defaultStartTime() {
  const date = new Date();
  date.setMinutes(0, 0, 0);
  return toLocalInputValue(date);
}

function defaultFinishTime() {
  const date = new Date();
  date.setHours(date.getHours() + 2, 0, 0, 0);
  return toLocalInputValue(date);
}

function resetForm(parentId = 0) {
  form.id = null;
  form.title = "";
  form.content = "";
  form.taskType = "short";
  form.parentId = parentId || 0;
  form.startTime = defaultStartTime();
  form.finishTime = defaultFinishTime();
}

function openCreate(parentId = 0) {
  resetForm(parentId);
  editorOpen.value = true;
}

function openEdit(task) {
  form.id = task.id;
  form.title = task.title || "";
  form.content = task.content || "";
  form.taskType = normalizeTaskType(task.taskType);
  form.parentId = Number(task.parentId || 0);
  form.startTime = normalizeDateTime(task.startTime);
  form.finishTime = normalizeDateTime(task.finishTime);
  editorOpen.value = true;
}

function closeEditor() {
  editorOpen.value = false;
}

function selectTask(task) {
  selectedId.value = task.id;
}

function payload() {
  return {
    id: form.id,
    title: form.title,
    content: form.content,
    taskType: form.taskType,
    parentId: Number(form.parentId || 0),
    startTime: form.startTime,
    finishTime: form.finishTime
  };
}

function applyTaskList(list) {
  sessionStorage.removeItem("todo-home-dashboard");
  tasks.value = [...list].sort((a, b) => {
    const aDone = isFinished(a) ? 1 : 0;
    const bDone = isFinished(b) ? 1 : 0;
    if (aDone !== bDone) return aDone - bDone;
    const aNext = !aDone && isNext(a) ? 1 : 0;
    const bNext = !bDone && isNext(b) ? 1 : 0;
    if (aNext !== bNext) return bNext - aNext;
    return Number(b.id || 0) - Number(a.id || 0);
  });
  if (!selectedId.value && tasks.value.length) selectedId.value = tasks.value[0].id;
  if (selectedId.value && !tasks.value.some((task) => task.id === selectedId.value)) {
    selectedId.value = tasks.value[0]?.id || null;
  }
}

async function refreshList() {
  const data = await taskApi.list();
  applyTaskList(Array.isArray(data?.data) ? data.data : []);
}

async function loadList() {
  const data = await run(() => taskApi.list(), "列表已刷新");
  applyTaskList(Array.isArray(data?.data) ? data.data : []);
}

async function runOperation(action, successMessage) {
  const data = await run(action);
  if (data?.code !== 200) return data;
  await refreshList().catch(() => {});
  status.value = successMessage || data.message || "操作成功";
  ok.value = true;
  return data;
}

function validateForm() {
  if (!form.title) {
    status.value = "请填写任务标题";
    ok.value = false;
    return false;
  }
  if (!form.content) {
    status.value = "请填写任务说明";
    ok.value = false;
    return false;
  }
  if (!form.startTime || !form.finishTime) {
    status.value = "请填写开始和结束时间";
    ok.value = false;
    return false;
  }
  return true;
}

async function saveTask() {
  if (!validateForm()) return;
  const isEdit = Boolean(form.id);
  await runOperation(() => (isEdit ? taskApi.update(payload()) : taskApi.create(payload())), isEdit ? "修改成功" : "创建成功");
  editorOpen.value = false;
}

function needSelectedId() {
  if (selectedTask.value?.id) return true;
  status.value = "请先选择任务";
  ok.value = false;
  return false;
}

async function finish() {
  if (!needSelectedId()) return;
  const data = await runOperation(() => taskApi.finish(selectedTask.value.id), "完成成功，已放入已完成");
  if (data?.code === 200) activeFilter.value = "done";
}

function split() {
  if (needSelectedId()) runOperation(() => taskApi.split(selectedTask.value.id), "拆分成功");
}

function delay() {
  if (needSelectedId()) runOperation(() => taskApi.delay(selectedTask.value.id), "延期成功");
}

async function next() {
  if (!needSelectedId()) return;
  const data = await runOperation(() => taskApi.next(selectedTask.value.id), "已设为下一个重点");
  if (data?.code === 200) activeFilter.value = "next";
}

function remove() {
  if (needSelectedId()) runOperation(() => taskApi.remove(selectedTask.value.id), "删除成功");
}

onMounted(() => {
  refreshList().catch(() => {
    tasks.value = [];
  });
});
</script>

<style scoped>
.task-page {
  min-height: 100vh;
  padding: 0 28px 24px;
  background: #f6f9fe;
}

.task-hero {
  position: sticky;
  top: 0;
  z-index: 4;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  min-height: 102px;
  margin: 0 -28px 20px;
  padding: 20px 28px;
  border-bottom: 1px solid #e6edf7;
  background: rgba(255, 255, 255, .92);
  backdrop-filter: blur(12px);
}

.task-hero h1 {
  color: #111a33;
  font-size: 25px;
  line-height: 1.2;
}

.task-hero p {
  margin-top: 4px;
  color: #64718a;
  font-size: 15px;
  font-weight: 700;
}

.task-primary-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 148px;
  justify-content: center;
  border: 0;
  border-radius: 8px;
  background: #2f6df6;
  box-shadow: 0 10px 22px rgba(47, 109, 246, .22);
  font-weight: 800;
}

.button-icon {
  font-size: 24px;
  line-height: 1;
}

.task-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
  margin-bottom: 18px;
}

.task-stat-card {
  display: grid;
  grid-template-columns: 64px minmax(0, 1fr);
  gap: 18px;
  align-items: center;
  min-height: 122px;
  border: 1px solid #e0e7f2;
  border-radius: 14px;
  padding: 22px 26px;
  background: #fff;
  box-shadow: 0 14px 34px rgba(58, 82, 126, .05);
}

.stat-icon {
  display: grid;
  place-items: center;
  width: 52px;
  height: 52px;
  border-radius: 13px;
  color: #fff;
}

.stat-icon svg {
  width: 27px;
  height: 27px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.stat-icon.blue { background: linear-gradient(135deg, #5d8dff, #2363ef); }
.stat-icon.amber { background: linear-gradient(135deg, #ffc43d, #f59e0b); }
.stat-icon.green { background: linear-gradient(135deg, #54d58a, #16a765); }
.stat-icon.purple { background: linear-gradient(135deg, #a76cff, #7c3aed); }
.stat-icon.once { background: linear-gradient(135deg, #5d8dff, #2363ef); }
.stat-icon.short { background: linear-gradient(135deg, #54d58a, #16a765); }
.stat-icon.long { background: linear-gradient(135deg, #ffc43d, #f59e0b); }
.stat-icon.repeat { background: linear-gradient(135deg, #a76cff, #7c3aed); }

.stat-label {
  display: block;
  color: #303b55;
  font-weight: 800;
}

.task-stat-card strong {
  display: inline-block;
  margin-top: 4px;
  color: #111a33;
  font-size: 28px;
  line-height: 1;
}

.task-stat-card small {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-top: 14px;
  color: #7b879d;
  font-weight: 700;
}

.stat-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
}

.stat-dot.green { background: #22c55e; }
.stat-dot.amber { background: #f59e0b; }
.stat-dot.red { background: #ef4444; }
.stat-dot.once { background: #2563eb; }
.stat-dot.short { background: #22c55e; }
.stat-dot.long { background: #f59e0b; }
.stat-dot.repeat { background: #7c3aed; }

.task-board {
  display: grid;
  grid-template-columns: minmax(520px, 1.25fr) minmax(420px, .95fr);
  gap: 24px;
  align-items: start;
}

.task-list-panel,
.task-detail-panel {
  border: 1px solid #e0e7f2;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 14px 34px rgba(58, 82, 126, .05);
}

.task-list-panel {
  padding: 12px;
}

.task-toolbar {
  display: grid;
  grid-template-columns: minmax(220px, .5fr) minmax(320px, .7fr);
  gap: 14px;
  margin-bottom: 12px;
}

.task-search {
  position: relative;
  display: flex;
  align-items: center;
}

.task-search input {
  height: 40px;
  padding: 0 42px 0 12px;
  border-color: #d9e3f0;
}

.task-search svg {
  position: absolute;
  top: 50%;
  right: 12px;
  width: 20px;
  height: 20px;
  pointer-events: none;
  transform: translateY(-50%);
  fill: none;
  stroke: #60708c;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.task-tabs {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 3px;
  border: 1px solid #e2e8f4;
  border-radius: 8px;
  padding: 3px;
  background: #f8fbff;
}

.task-tabs button {
  min-height: 34px;
  border: 0;
  border-radius: 7px;
  color: #52617c;
  background: transparent;
  font-size: 13px;
  font-weight: 800;
}

.task-tabs button:hover,
.task-tabs button.active {
  color: #2f6df6;
  background: #eef4ff;
}

.task-list {
  display: grid;
  gap: 10px;
  max-height: calc(100vh - 330px);
  overflow: auto;
  padding-right: 2px;
}

.task-row {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 106px;
  gap: 8px 16px;
  width: 100%;
  min-height: 112px;
  border: 1px solid #e0e7f2;
  border-radius: 10px;
  padding: 16px 18px;
  color: #172033;
  background: #fff;
  text-align: left;
}

.task-row:hover,
.task-row.active {
  border-color: #2f6df6;
  background: linear-gradient(180deg, #f7fbff, #fff);
  box-shadow: inset 0 0 0 1px rgba(47, 109, 246, .35);
}

.task-row.done {
  border-color: #bbf7d0;
  background: #f8fffb;
}

.task-row.next {
  border-color: #facc15;
  background: #fffdf2;
}

.task-row.overdue {
  border-color: #fed7aa;
}

.task-row-main {
  display: flex;
  gap: 12px;
  min-width: 0;
}

.task-star {
  color: #f6b51f;
  font-size: 18px;
  line-height: 1.4;
}

.task-star.done {
  color: #16a765;
}

.task-row h3 {
  color: #172033;
  font-size: 17px;
  line-height: 1.35;
  overflow-wrap: anywhere;
}

.task-title-line {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.task-status-pill {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  font-style: normal;
  font-weight: 900;
  line-height: 1;
  white-space: nowrap;
}

.task-status-pill.done {
  color: #16a765;
  background: #dcfce7;
}

.task-status-pill.next {
  color: #b45309;
  background: #fef3c7;
}

.task-status-pill.overdue {
  color: #dc2626;
  background: #fee2e2;
}

.task-row p {
  margin-top: 5px;
  color: #64718a;
  font-size: 13px;
  font-weight: 700;
  overflow-wrap: anywhere;
}

.task-row-meta {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: 124px 132px minmax(190px, 1fr) 76px minmax(92px, 132px);
  gap: 12px;
  align-items: center;
  color: #71809a;
  font-size: 13px;
  font-weight: 800;
}

.task-time-text {
  overflow-wrap: anywhere;
}

.task-progress-label {
  justify-self: end;
}

.task-type-pill {
  justify-self: end;
  align-self: start;
  border-radius: 999px;
  padding: 5px 12px;
  font-size: 12px;
  font-style: normal;
  font-weight: 900;
  white-space: nowrap;
}

.task-type-pill.once { color: #2563eb; background: #dbeafe; }
.task-type-pill.short { color: #16a765; background: #dcfce7; }
.task-type-pill.long { color: #d97706; background: #fef3c7; }
.task-type-pill.repeat { color: #7c3aed; background: #ede9fe; }

.mini-progress {
  display: inline-block;
  width: 100%;
  height: 8px;
  overflow: hidden;
  border-radius: 999px;
  background: #e8edf5;
}

.mini-progress i {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: #2f6df6;
}

.task-detail-panel {
  display: grid;
  gap: 0;
  min-height: calc(100vh - 300px);
  padding: 20px;
}

.detail-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #edf2f8;
}

.detail-head h2 {
  color: #111a33;
  font-size: 18px;
}

.detail-title {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.icon-button {
  display: grid;
  place-items: center;
  width: 34px;
  min-width: 34px;
  height: 34px;
  min-height: 34px;
  border: 1px solid #d9e3f0;
  border-radius: 7px;
  padding: 0;
  color: #52617c;
  background: #fff;
  font-size: 22px;
}

.icon-button:hover {
  color: #2f6df6;
  background: #f8fbff;
}

.icon-button svg {
  width: 19px;
  height: 19px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.detail-section {
  display: grid;
  gap: 8px;
  padding: 16px 0;
  border-bottom: 1px solid #edf2f8;
}

.detail-section h3 {
  display: flex;
  gap: 10px;
  color: #111a33;
  font-size: 17px;
  overflow-wrap: anywhere;
}

.detail-section p {
  color: #58677f;
  font-weight: 700;
  overflow-wrap: anywhere;
}

.detail-label {
  display: block;
  margin-bottom: 8px;
  color: #344054;
  font-size: 13px;
  font-weight: 900;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  border-bottom: 1px solid #edf2f8;
}

.detail-grid > div {
  min-height: 72px;
  padding: 14px 0;
}

.detail-grid > div:nth-child(odd) {
  padding-right: 16px;
  border-right: 1px solid #edf2f8;
}

.detail-grid > div:nth-child(even) {
  padding-left: 16px;
}

.detail-grid strong {
  color: #465670;
  font-weight: 800;
}

.detail-progress {
  display: grid;
  grid-template-columns: 36px 1fr 42px;
  gap: 10px;
  align-items: center;
  color: #58677f;
  font-weight: 800;
}

.subtask-card {
  display: grid;
  gap: 12px;
  margin-top: 18px;
  border: 1px solid #e0e7f2;
  border-radius: 10px;
  padding: 14px;
  background: #fbfdff;
}

.subtask-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.subtask-head h3 {
  color: #172033;
  font-size: 15px;
}

.subtask-add {
  min-height: 30px;
  border-color: #dbeafe;
  border-radius: 7px;
  padding: 4px 10px;
  color: #2f6df6;
  background: #fff;
  font-size: 12px;
  font-weight: 900;
}

.subtask-add:hover {
  background: #eef4ff;
}

.subtask-list {
  display: grid;
  align-content: start;
  gap: 8px;
  max-height: 180px;
  overflow: auto;
  padding-right: 4px;
}

.subtask-row {
  display: grid;
  grid-template-columns: 22px minmax(0, 1fr) 72px;
  gap: 10px;
  align-items: center;
  min-height: 32px;
  border: 0;
  padding: 0;
  color: #465670;
  background: transparent;
  text-align: left;
  font-weight: 800;
}

.subtask-row:hover {
  color: #2f6df6;
  background: transparent;
}

.subtask-row span:nth-child(2) {
  overflow-wrap: anywhere;
}

.check-box {
  display: grid;
  place-items: center;
  width: 18px;
  height: 18px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  background: #fff;
}

.check-box.checked {
  border-color: #2f6df6;
  background: #2f6df6;
}

.check-box svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: #fff;
  stroke-width: 3;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.subtask-status {
  justify-self: end;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  font-style: normal;
  font-weight: 900;
}

.subtask-status.done {
  color: #16a765;
  background: #dcfce7;
}

.subtask-status.todo {
  color: #64718a;
  background: #eef2f7;
}

.subtask-empty,
.task-empty {
  border: 1px dashed #d9e3f0;
  border-radius: 10px;
  padding: 24px;
  color: #71809a;
  background: #fbfdff;
  text-align: center;
  font-weight: 800;
}

.detail-actions {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 10px;
  margin: 18px -20px -20px;
  padding: 14px 18px;
  border-top: 1px solid #edf2f8;
  background: #fbfdff;
}

.action-button {
  min-height: 38px;
  padding: 7px 10px;
  font-weight: 900;
  white-space: nowrap;
}

.action-button:disabled {
  cursor: not-allowed;
  opacity: .55;
}

.success {
  border-color: #bbf7d0;
  color: #16a765;
  background: #f0fdf4;
}

.success:hover {
  color: #15803d;
  background: #dcfce7;
}

.warn {
  color: #f97316;
}

.soft-danger {
  color: #ef4444;
  background: #fff5f5;
}

.soft-danger:hover {
  background: #fee2e2;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 20;
  display: grid;
  place-items: center;
  padding: 24px;
  background: rgba(15, 23, 42, .32);
}

.task-modal {
  display: grid;
  gap: 14px;
  width: min(680px, 100%);
  max-height: calc(100vh - 48px);
  overflow: auto;
  border-radius: 14px;
  padding: 22px;
  background: #fff;
  box-shadow: 0 26px 60px rgba(15, 23, 42, .22);
}

.modal-head,
.modal-actions {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.modal-actions {
  justify-content: flex-end;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
}

@media (max-width: 1320px) {
  .task-stats { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .task-board { grid-template-columns: 1fr; }
  .task-list { max-height: 54vh; }
}

@media (max-width: 760px) {
  .task-page { padding: 0 12px 16px; }
  .task-hero {
    position: static;
    align-items: flex-start;
    flex-direction: column;
    margin: 0 -12px 14px;
    padding: 18px 12px;
  }
  .task-primary-button { width: 100%; }
  .task-stats,
  .task-toolbar,
  .detail-grid,
  .form-row { grid-template-columns: 1fr; }
  .task-board { display: block; }
  .task-detail-panel { margin-top: 16px; }
  .task-row,
  .task-row-meta,
  .detail-actions {
    grid-template-columns: 1fr;
  }
  .task-type-pill,
  .task-progress-label {
    justify-self: start;
  }
  .detail-grid > div:nth-child(odd),
  .detail-grid > div:nth-child(even) {
    padding-right: 0;
    padding-left: 0;
    border-right: 0;
  }
}
</style>
