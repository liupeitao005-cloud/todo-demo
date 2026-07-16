<template>
  <section class="quadrant-page">
    <header class="quadrant-head">
      <div>
        <h1>四象限</h1>
        <p>按重要性与紧急性整理任务，快速决定先做什么</p>
      </div>
      <div class="head-actions">
        <label class="search-box">
          <span class="sr-only">搜索任务</span>
          <input v-model.trim="keyword" placeholder="搜索任务..." />
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m21 21-4.3-4.3M10.8 18a7.2 7.2 0 1 1 0-14.4 7.2 7.2 0 0 1 0 14.4Z" /></svg>
        </label>
        <button class="primary-button" type="button" @click="openMove(quadrants[0])">
          <span aria-hidden="true">+</span>
          添加任务
        </button>
      </div>
    </header>

    <div class="quadrant-layout">
      <section class="matrix-panel">
        <div class="axis-x">
          <span>紧急</span>
          <i></i>
          <span>不紧急</span>
        </div>
        <div class="matrix-body">
          <div class="axis-y">
            <span>重要</span>
            <i></i>
            <span>不重要</span>
          </div>
          <div class="quadrant-grid">
            <article
              v-for="quadrant in quadrants"
              :key="quadrant.key"
              :class="['quadrant-card', quadrant.tone]"
            >
              <div class="card-head">
                <span class="card-icon">
                  <svg viewBox="0 0 24 24" aria-hidden="true"><path :d="quadrant.icon" /></svg>
                </span>
                <div>
                  <h2>{{ quadrant.title }}</h2>
                  <p>{{ quadrant.action }}</p>
                </div>
                <strong>{{ quadrantItems(quadrant).length }}项</strong>
              </div>

              <div v-if="quadrantItems(quadrant).length" class="task-stack">
                <button
                  v-for="item in quadrantItems(quadrant)"
                  :key="item.id"
                  :class="['task-chip', { active: selectedItem?.id === item.id }]"
                  type="button"
                  @click="selectItem(item, quadrant)"
                >
                  <span class="drag-dot" aria-hidden="true">⋮⋮</span>
                  <strong>{{ item.title || "未命名任务" }}</strong>
                  <em>{{ item.content || quadrant.tag }}</em>
                  <time>{{ formatTime(item.startTime) || "未安排" }}</time>
                  <span class="more-button" aria-hidden="true">•••</span>
                </button>
              </div>
              <p v-else class="empty-state">暂无任务</p>

              <button class="add-link" type="button" @click="openMove(quadrant)">+ 添加任务</button>
            </article>
          </div>
        </div>
        <p class="matrix-tip">提示：根据任务的重要性和紧急性放入对应象限，定期清理右下角任务。</p>
      </section>

      <aside class="insight-side">
        <section class="side-card">
          <div class="side-title">
            <span class="side-icon">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 4.5A2.5 2.5 0 0 1 6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15Z" /></svg>
            </span>
            <h2>象限说明</h2>
          </div>
          <div class="guide-list">
            <article v-for="quadrant in quadrants" :key="`guide-${quadrant.key}`">
              <i :class="quadrant.tone"></i>
              <div>
                <strong>{{ quadrant.title }}</strong>
                <p>{{ quadrant.description }}</p>
              </div>
            </article>
          </div>
        </section>

        <section class="side-card">
          <h2>任务分布</h2>
          <div class="distribution">
            <div class="donut" :style="{ background: donutGradient }">
              <span>{{ totalCount }}</span>
              <small>总任务</small>
            </div>
            <div class="distribution-list">
              <p v-for="quadrant in quadrants" :key="`count-${quadrant.key}`">
                <i :class="quadrant.tone"></i>
                <span>{{ quadrant.title }}</span>
                <strong>{{ quadrantItemsRaw(quadrant).length }} 项</strong>
              </p>
            </div>
          </div>
        </section>

        <section class="side-card detail-card">
          <h2>当前任务</h2>
          <template v-if="selectedItem">
            <strong>{{ selectedItem.title || "未命名任务" }}</strong>
            <p>{{ selectedItem.content || "暂无任务说明。" }}</p>
            <div class="detail-meta">
              <span>{{ selectedQuadrant?.title }}</span>
              <span>{{ formatTime(selectedItem.startTime) || "未安排开始" }}</span>
            </div>
            <button class="danger-button" type="button" @click="removeSelected">移出四象限</button>
          </template>
          <p v-else class="empty-state">选择一个任务查看详情。</p>
          <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
        </section>
      </aside>
    </div>

    <div v-if="moveOpen" class="modal-backdrop" @click.self="closeMove">
      <form class="move-modal" @submit.prevent="moveTask">
        <div class="modal-head">
          <div>
            <h2>添加任务到「{{ moveTarget?.title }}」</h2>
            <p>选择一个已有任务添加到当前象限</p>
          </div>
          <button class="close-button" type="button" title="关闭" @click="closeMove">×</button>
        </div>

        <label class="task-picker-search">
          <span class="sr-only">搜索可选任务</span>
          <input v-model.trim="taskKeyword" placeholder="搜索任务标题或内容..." />
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m21 21-4.3-4.3M10.8 18a7.2 7.2 0 1 1 0-14.4 7.2 7.2 0 0 1 0 14.4Z" /></svg>
        </label>

        <div class="task-picker">
          <button
            v-for="task in filteredAvailableTasks"
            :key="task.id"
            :class="['pick-task-card', { active: form.id === task.id }]"
            type="button"
            @click="form.id = task.id"
          >
            <span class="pick-radio" aria-hidden="true">
              <i v-if="form.id === task.id"></i>
            </span>
            <span class="pick-main">
              <strong>{{ task.title || "未命名任务" }}</strong>
              <small>
                <b>ID: {{ task.id }}</b>
                <i></i>
                <b>{{ formatTime(task.startTime) || "未安排时间" }}</b>
                <i></i>
                <em :class="taskTypeTone(task)">{{ taskTypeLabel(task) }}</em>
              </small>
            </span>
            <span class="subtask-pill">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 6h11M9 12h11M9 18h11M4 6h.01M4 12h.01M4 18h.01" /></svg>
              子任务 {{ subtaskProgress(task) }}
            </span>
          </button>
          <p v-if="!filteredAvailableTasks.length" class="empty-state">没有可选择的任务。</p>
        </div>

        <div class="modal-actions">
          <span>共 {{ filteredAvailableTasks.length }} 个任务</span>
          <button class="secondary-button" type="button" @click="closeMove">取消</button>
          <button class="primary-button compact" :disabled="loading || !form.id" type="submit">确认添加</button>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { fourApi, taskApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const quadrants = [
  {
    key: "important-urgent",
    title: "重要且紧急",
    action: "立即处理",
    description: "需要优先处理，立刻行动，避免问题扩大。",
    tag: "优先",
    tone: "red",
    importance: 1,
    urgency: 1,
    icon: "M12 8v5M12 17h.01M12 22a10 10 0 1 1 0-20 10 10 0 0 1 0 20Z"
  },
  {
    key: "important-not-urgent",
    title: "重要不紧急",
    action: "规划时间",
    description: "需要规划时间完成，投入精力，创造长期价值。",
    tag: "计划",
    tone: "green",
    importance: 1,
    urgency: 0,
    icon: "M8 2v4M16 2v4M3.5 9.5h17M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z"
  },
  {
    key: "not-important-urgent",
    title: "不重要但紧急",
    action: "快速处理 / 委托",
    description: "优先快速处理或委托，减少对核心目标的干扰。",
    tag: "委托",
    tone: "orange",
    importance: 0,
    urgency: 1,
    icon: "m13 2-9 12h7l-1 8 9-12h-7l1-8Z"
  },
  {
    key: "not-important-not-urgent",
    title: "不重要不紧急",
    action: "可延后或删除",
    description: "可延后处理或直接删除，避免浪费时间。",
    tag: "延后",
    tone: "gray",
    importance: 0,
    urgency: 0,
    icon: "M5 12h14"
  }
];

const lists = reactive({
  "important-urgent": [],
  "important-not-urgent": [],
  "not-important-urgent": [],
  "not-important-not-urgent": []
});
const availableTasks = ref([]);
const form = reactive({ id: null });
const keyword = ref("");
const taskKeyword = ref("");
const selectedId = ref(null);
const selectedQuadrantKey = ref("");
const moveOpen = ref(false);
const moveTarget = ref(quadrants[0]);
const { loading, status, ok, run } = useRequest();

const totalCount = computed(() => quadrants.reduce((sum, quadrant) => sum + quadrantItemsRaw(quadrant).length, 0));
const selectedQuadrant = computed(() => quadrants.find((quadrant) => quadrant.key === selectedQuadrantKey.value) || null);
const selectedItem = computed(() => {
  if (!selectedQuadrant.value) return null;
  return quadrantItemsRaw(selectedQuadrant.value).find((item) => item.id === selectedId.value) || null;
});
const placedTaskSignatures = computed(() => {
  return new Set(quadrants.flatMap((quadrant) => quadrantItemsRaw(quadrant).map((item) => taskSignature(item))));
});

const filteredAvailableTasks = computed(() => {
  const term = taskKeyword.value.toLowerCase();
  return availableTasks.value
    .filter((task) => !Number(task.parentId || 0))
    .filter((task) => !placedTaskSignatures.value.has(taskSignature(task)))
    .filter((task) => {
      if (!term) return true;
      return [task.title, task.content, task.id].some((value) => String(value || "").toLowerCase().includes(term));
    });
});

const donutGradient = computed(() => {
  if (!totalCount.value) return "conic-gradient(#e2e8f0 0 100%)";
  const colors = { red: "#ef4444", green: "#35b987", orange: "#f59e0b", gray: "#94a3b8" };
  let cursor = 0;
  const parts = quadrants.map((quadrant) => {
    const size = quadrantItemsRaw(quadrant).length / totalCount.value * 100;
    const start = cursor;
    cursor += size;
    return `${colors[quadrant.tone]} ${start}% ${cursor}%`;
  });
  return `conic-gradient(${parts.join(", ")})`;
});

function quadrantItemsRaw(quadrant) {
  return lists[quadrant.key] || [];
}

function quadrantItems(quadrant) {
  const term = keyword.value.toLowerCase();
  if (!term) return quadrantItemsRaw(quadrant);
  return quadrantItemsRaw(quadrant).filter((item) => {
    return [item.title, item.content].some((value) => String(value || "").toLowerCase().includes(term));
  });
}

function formatTime(value) {
  if (!value) return "";
  return String(value).replace("T", " ").slice(0, 16);
}

function normalizeDateTime(value) {
  return value ? String(value).replace(" ", "T").slice(0, 16) : "";
}

function taskSignature(item) {
  return [
    item?.title || "",
    item?.content || "",
    normalizeDateTime(item?.startTime),
    normalizeDateTime(item?.finishTime)
  ].join("|");
}

function normalizeTaskType(value) {
  const map = {
    once: "一次性任务",
    short: "短期任务",
    normal: "短期任务",
    next: "短期任务",
    long: "长期任务",
    repeat: "重复性任务",
    "一次性任务": "一次性任务",
    "短期任务": "短期任务",
    "长期任务": "长期任务",
    "重复性任务": "重复性任务"
  };
  return map[value] || "短期任务";
}

function taskTypeLabel(task) {
  if (Number(task?.isNext || 0) === 1) return "下一步";
  return normalizeTaskType(task?.taskType);
}

function taskTypeTone(task) {
  if (Number(task?.isNext || 0) === 1) return "next";
  const label = normalizeTaskType(task?.taskType);
  if (label === "一次性任务") return "once";
  if (label === "长期任务") return "long";
  if (label === "重复性任务") return "repeat";
  return "short";
}

function subtaskProgress(task) {
  const children = availableTasks.value.filter((item) => Number(item.parentId || 0) === Number(task?.id || 0));
  return `${children.filter((item) => Number(item.isFinish || 0) === 1).length}/${children.length}`;
}

function selectItem(item, quadrant) {
  selectedId.value = item.id;
  selectedQuadrantKey.value = quadrant.key;
}

function openMove(quadrant) {
  form.id = null;
  taskKeyword.value = "";
  moveTarget.value = quadrant;
  status.value = "";
  moveOpen.value = true;
  loadTasks().catch(() => {});
}

function closeMove() {
  moveOpen.value = false;
}

async function loadQuadrant(quadrant) {
  const data = await fourApi.list({ importance: quadrant.importance, urgency: quadrant.urgency });
  lists[quadrant.key] = Array.isArray(data?.data) ? data.data : [];
}

async function loadTasks() {
  const data = await taskApi.list();
  availableTasks.value = Array.isArray(data?.data) ? data.data : [];
}

async function loadAll(options = {}) {
  const action = async () => {
    const results = await Promise.allSettled(quadrants.map((quadrant) => loadQuadrant(quadrant)));
    const failed = results.some((result) => result.status === "rejected");
    if (failed) throw new Error("部分象限加载失败");
    return { code: 200 };
  };
  if (options.silent) return action();
  return run(action, "列表已刷新");
}

async function moveTask() {
  if (!form.id) {
    status.value = "请先选择任务";
    ok.value = false;
    return;
  }
  const target = moveTarget.value || quadrants[0];
  const data = await run(() => fourApi.move({ id: form.id, importance: target.importance, urgency: target.urgency }));
  if (data?.code === 200) {
    await loadAll({ silent: true }).catch(() => {});
    moveOpen.value = false;
    status.value = `已添加到${target.title}`;
    ok.value = true;
  }
}

async function removeSelected() {
  if (!selectedItem.value?.id) {
    status.value = "请先选择要移出的任务";
    ok.value = false;
    return;
  }
  const data = await run(() => fourApi.remove(selectedItem.value.id));
  if (data?.code === 200) {
    selectedId.value = null;
    selectedQuadrantKey.value = "";
    await loadAll({ silent: true }).catch(() => {});
    status.value = "已移出四象限";
    ok.value = true;
  }
}

onMounted(() => {
  loadAll({ silent: true }).catch(() => {});
});
</script>

<style scoped>
.quadrant-page {
  min-height: 100vh;
  padding: 28px;
  background: #f7faff;
}

.quadrant-head {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: flex-start;
}

.quadrant-head h1 {
  color: #111a33;
  font-size: 28px;
}

.quadrant-head p {
  margin-top: 6px;
  color: #64718a;
  font-weight: 800;
}

.head-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-box {
  position: relative;
  display: block;
  width: 280px;
}

.search-box input {
  height: 46px;
  padding-left: 42px;
  border-color: #dfe7f3;
}

.search-box svg {
  position: absolute;
  left: 14px;
  top: 50%;
  width: 20px;
  height: 20px;
  fill: none;
  stroke: #71809a;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  transform: translateY(-50%);
}

.primary-button,
.secondary-button,
.danger-button,
.close-button,
.add-link,
.target-button {
  border-radius: 7px;
  font-weight: 900;
}

.primary-button {
  display: inline-flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
  min-width: 136px;
  border-color: #2563eb;
  background: #2563eb;
  box-shadow: 0 12px 22px rgba(37, 99, 235, .18);
}

.primary-button.compact {
  min-width: 96px;
}

.secondary-button,
.close-button {
  border-color: #d8e1ee;
  color: #314158;
  background: #fff;
}

.danger-button {
  border-color: #fecaca;
  color: #dc2626;
  background: #fff5f5;
}

.quadrant-layout {
  display: grid;
  grid-template-columns: minmax(720px, 1fr) 360px;
  gap: 22px;
  align-items: start;
  margin-top: 28px;
}

.matrix-panel,
.side-card,
.move-modal {
  border: 1px solid #e0e7f2;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 16px 34px rgba(58, 82, 126, .06);
}

.matrix-panel {
  padding: 22px;
}

.axis-x {
  display: grid;
  grid-template-columns: 1fr minmax(220px, 1.8fr) 1fr;
  align-items: center;
  margin: 0 16px 12px 64px;
  color: #64718a;
  font-weight: 900;
  text-align: center;
}

.axis-x span:first-child { color: #ef4444; }
.axis-x span:last-child { color: #16a765; }

.axis-x i {
  position: relative;
  height: 2px;
  background: #b8c3d4;
}

.axis-x i::after {
  content: "";
  position: absolute;
  right: -1px;
  top: 50%;
  border: 6px solid transparent;
  border-left-color: #b8c3d4;
  transform: translateY(-50%);
}

.matrix-body {
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr);
  gap: 10px;
}

.axis-y {
  display: grid;
  grid-template-rows: 1fr minmax(180px, 1fr) 1fr;
  justify-items: center;
  align-items: center;
  color: #64718a;
  font-weight: 900;
}

.axis-y span {
  writing-mode: vertical-rl;
  letter-spacing: 2px;
}

.axis-y span:first-child { color: #ef4444; }
.axis-y span:last-child { color: #f97316; }

.axis-y i {
  position: relative;
  width: 2px;
  height: 100%;
  background: #b8c3d4;
}

.axis-y i::after {
  content: "";
  position: absolute;
  left: 50%;
  bottom: -1px;
  border: 6px solid transparent;
  border-top-color: #b8c3d4;
  transform: translateX(-50%);
}

.quadrant-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.quadrant-card {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr) auto;
  gap: 14px;
  min-height: 290px;
  max-height: 340px;
  overflow: hidden;
  border: 1px solid;
  border-radius: 10px;
  padding: 16px;
}

.quadrant-card.red { border-color: #fecaca; background: #fff7f7; }
.quadrant-card.green { border-color: #bfe9d7; background: #f6fffb; }
.quadrant-card.orange { border-color: #fed7aa; background: #fffaf3; }
.quadrant-card.gray { border-color: #dbe3ef; background: #fbfcff; }

.card-head {
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
}

.card-icon {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  color: #fff;
}

.red .card-icon { background: #ef4444; }
.green .card-icon { background: #35b987; }
.orange .card-icon { background: #f59e0b; }
.gray .card-icon { background: #94a3b8; }

.card-icon svg {
  width: 24px;
  height: 24px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.4;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.card-head h2 {
  font-size: 18px;
}

.red h2 { color: #dc2626; }
.green h2 { color: #059669; }
.orange h2 { color: #ea580c; }
.gray h2 { color: #52627a; }

.card-head p {
  margin-top: 2px;
  color: #64718a;
  font-weight: 800;
}

.card-head strong {
  border-radius: 999px;
  padding: 5px 10px;
  font-size: 12px;
}

.red .card-head strong { color: #dc2626; background: #fee2e2; }
.green .card-head strong { color: #059669; background: #dff7ed; }
.orange .card-head strong { color: #ea580c; background: #ffedd5; }
.gray .card-head strong { color: #52627a; background: #edf2f7; }

.task-stack {
  display: grid;
  align-content: start;
  gap: 10px;
  min-height: 0;
  overflow: auto;
  padding-right: 3px;
}

.task-chip {
  display: grid;
  grid-template-columns: 22px minmax(0, 1fr) auto auto;
  gap: 10px;
  align-items: center;
  min-height: 48px;
  border: 1px solid #e5ecf5;
  border-radius: 8px;
  padding: 9px 10px;
  color: #172033;
  background: rgba(255, 255, 255, .86);
  text-align: left;
  box-shadow: 0 8px 16px rgba(58, 82, 126, .06);
}

.task-chip:hover,
.task-chip.active {
  border-color: #9dbbff;
  background: #fff;
}

.drag-dot {
  color: #9aa8bd;
  font-weight: 900;
}

.task-chip strong,
.task-chip em {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-chip em {
  justify-self: start;
  border-radius: 6px;
  padding: 3px 8px;
  color: #64718a;
  background: #f1f5f9;
  font-size: 12px;
  font-style: normal;
  font-weight: 900;
}

.task-chip time {
  color: #64718a;
  font-size: 12px;
  font-weight: 800;
}

.more-button {
  color: #8b9ab0;
  letter-spacing: 1px;
}

.add-link {
  justify-self: center;
  border: 0;
  color: #2563eb;
  background: transparent;
}

.red .add-link { color: #ef4444; }
.green .add-link { color: #059669; }
.orange .add-link { color: #ea580c; }
.gray .add-link { color: #52627a; }

.matrix-tip {
  margin-top: 18px;
  color: #8b9ab0;
  font-weight: 800;
}

.insight-side {
  display: grid;
  gap: 16px;
}

.side-card {
  display: grid;
  gap: 16px;
  padding: 18px;
}

.side-title {
  display: flex;
  gap: 10px;
  align-items: center;
}

.side-icon {
  display: grid;
  place-items: center;
  width: 30px;
  height: 30px;
  color: #2563eb;
}

.side-icon svg {
  width: 24px;
  height: 24px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.guide-list {
  display: grid;
  gap: 16px;
}

.guide-list article {
  display: grid;
  grid-template-columns: 12px minmax(0, 1fr);
  gap: 12px;
}

.guide-list i,
.distribution-list i {
  width: 10px;
  height: 10px;
  margin-top: 6px;
  border-radius: 50%;
}

.guide-list i.red,
.distribution-list i.red { background: #ef4444; }
.guide-list i.green,
.distribution-list i.green { background: #35b987; }
.guide-list i.orange,
.distribution-list i.orange { background: #f59e0b; }
.guide-list i.gray,
.distribution-list i.gray { background: #94a3b8; }

.guide-list strong {
  color: #334155;
}

.guide-list p {
  margin-top: 3px;
  color: #64718a;
  font-weight: 700;
}

.distribution {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr);
  gap: 16px;
  align-items: center;
}

.donut {
  position: relative;
  display: grid;
  place-items: center;
  width: 112px;
  height: 112px;
  border-radius: 50%;
}

.donut::after {
  content: "";
  position: absolute;
  inset: 22px;
  border-radius: 50%;
  background: #fff;
}

.donut span,
.donut small {
  position: relative;
  z-index: 1;
}

.donut span {
  align-self: end;
  color: #172033;
  font-size: 26px;
  font-weight: 900;
}

.donut small {
  align-self: start;
  color: #64718a;
  font-weight: 800;
}

.distribution-list {
  display: grid;
  gap: 9px;
}

.distribution-list p {
  display: grid;
  grid-template-columns: 12px minmax(0, 1fr) auto;
  gap: 8px;
  align-items: center;
  color: #64718a;
  font-weight: 800;
}

.distribution-list strong {
  color: #334155;
}

.detail-card > strong {
  color: #172033;
  font-size: 16px;
}

.detail-card > p {
  color: #64718a;
  overflow-wrap: anywhere;
  font-weight: 700;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-meta span {
  border-radius: 999px;
  padding: 5px 10px;
  color: #52627a;
  background: #f1f5f9;
  font-weight: 800;
}

.empty-state {
  border: 1px dashed #d8e1ee;
  border-radius: 8px;
  padding: 16px;
  color: #64718a;
  background: rgba(255, 255, 255, .68);
  text-align: center;
  font-weight: 800;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(15, 23, 42, .28);
}

.move-modal {
  display: grid;
  grid-template-rows: auto auto minmax(0, 1fr) auto;
  gap: 18px;
  width: min(920px, 100%);
  max-height: min(88vh, 760px);
  padding: 28px;
}

.modal-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.modal-head h2 {
  color: #111a33;
  font-size: 24px;
}

.modal-head p {
  margin-top: 8px;
  color: #71809a;
  font-weight: 800;
}

.close-button {
  width: 42px;
  min-height: 42px;
  border: 0;
  padding: 0;
  color: #52627a;
  background: transparent;
  font-size: 32px;
}

.task-picker-search {
  position: relative;
  display: block;
}

.task-picker-search input {
  height: 58px;
  padding-left: 54px;
  border-color: #d6e3f5;
  border-radius: 10px;
  font-size: 17px;
  font-weight: 800;
}

.task-picker-search svg {
  position: absolute;
  left: 18px;
  top: 50%;
  width: 24px;
  height: 24px;
  fill: none;
  stroke: #64718a;
  stroke-width: 2.2;
  stroke-linecap: round;
  stroke-linejoin: round;
  transform: translateY(-50%);
}

.task-picker {
  display: grid;
  align-content: start;
  gap: 10px;
  min-height: 0;
  overflow: auto;
  padding-right: 8px;
}

.pick-task-card {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) auto;
  gap: 16px;
  align-items: center;
  min-height: 92px;
  border: 1px solid #dfe7f3;
  border-radius: 10px;
  padding: 16px 18px;
  color: #172033;
  background: #fff;
  text-align: left;
}

.pick-task-card:hover,
.pick-task-card.active {
  border-color: #2f6df6;
  box-shadow: 0 0 0 1px #2f6df6 inset;
}

.pick-radio {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  border: 2px solid #cbd8ea;
  border-radius: 50%;
}

.pick-task-card.active .pick-radio {
  border-color: #2f6df6;
  background: #2f6df6;
}

.pick-radio i {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #fff;
}

.pick-main {
  display: grid;
  gap: 8px;
  min-width: 0;
}

.pick-main strong {
  overflow: hidden;
  color: #172033;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 18px;
}

.pick-main small {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  color: #64718a;
  font-size: 15px;
  font-weight: 800;
}

.pick-main small i {
  width: 1px;
  height: 16px;
  background: #cbd5e1;
}

.pick-main em {
  font-style: normal;
  font-weight: 900;
}

.pick-main em.next,
.pick-main em.once { color: #2563eb; }
.pick-main em.short { color: #16a765; }
.pick-main em.long { color: #d97706; }
.pick-main em.repeat { color: #7c3aed; }

.subtask-pill {
  display: inline-flex;
  gap: 7px;
  align-items: center;
  border: 1px solid #dfe7f3;
  border-radius: 999px;
  padding: 8px 12px;
  color: #52627a;
  background: #f8fbff;
  font-weight: 900;
  white-space: nowrap;
}

.subtask-pill svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.modal-actions {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 140px 140px;
  gap: 16px;
  align-items: center;
  padding-top: 8px;
}

.modal-actions > span {
  color: #64718a;
  font-weight: 900;
}

.modal-actions .secondary-button,
.modal-actions .primary-button {
  min-height: 54px;
  justify-content: center;
  font-size: 16px;
}

.status {
  min-height: 20px;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
}

@media (max-width: 1280px) {
  .quadrant-layout {
    grid-template-columns: 1fr;
  }

  .insight-side {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .quadrant-page {
    padding: 16px 12px;
  }

  .quadrant-head,
  .head-actions {
    flex-direction: column;
  }

  .head-actions,
  .search-box,
  .primary-button {
    width: 100%;
  }

  .matrix-panel {
    overflow: auto;
  }

  .matrix-body {
    min-width: 740px;
  }

  .insight-side,
  .modal-actions {
    grid-template-columns: 1fr;
  }

  .pick-task-card {
    grid-template-columns: 34px minmax(0, 1fr);
  }

  .subtask-pill {
    grid-column: 2;
    justify-self: start;
  }
}
</style>
