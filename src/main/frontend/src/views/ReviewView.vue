<template>
  <section class="review-page">
    <header class="review-head">
      <div>
        <h1>复习计划</h1>
        <p>按照艾宾浩斯遗忘曲线安排复习，帮助长期记忆</p>
      </div>
      <div class="head-actions">
        <button class="primary-button" type="button" @click="openCreator">
          <span aria-hidden="true">+</span>
          新建复习计划
        </button>
        <label class="search-box">
          <span class="sr-only">搜索复习计划</span>
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m21 21-4.3-4.3M10.8 18a7.2 7.2 0 1 1 0-14.4 7.2 7.2 0 0 1 0 14.4Z" /></svg>
          <input v-model.trim="keyword" placeholder="搜索复习计划" />
        </label>
      </div>
    </header>

    <div class="review-layout">
      <main class="review-main">
        <section class="curve-card">
          <div class="curve-title">
            <div>
              <h2>艾宾浩斯遗忘曲线</h2>
              <p>记忆保持率</p>
            </div>
            <span>系统自动生成复习节点</span>
          </div>
          <div class="curve-chart" aria-label="艾宾浩斯遗忘曲线">
            <svg viewBox="0 0 760 230" role="img" aria-hidden="true">
              <g class="grid-lines">
                <path d="M44 30H725M44 78H725M44 126H725M44 174H725" />
              </g>
              <path class="axis" d="M44 30V198H725" />
              <path class="forget-line" d="M44 30 C110 76 136 110 182 128 C230 80 247 82 292 118 C345 86 360 90 405 128 C470 96 520 106 588 142 C640 162 690 171 725 178" />
              <g class="curve-points">
                <circle cx="44" cy="30" r="5" />
                <circle cx="182" cy="78" r="5" />
                <circle cx="292" cy="90" r="5" />
                <circle cx="405" cy="108" r="5" />
                <circle cx="588" cy="142" r="5" />
              </g>
              <g class="review-jumps">
                <path d="M182 128V78M292 118V90M405 128V108" />
              </g>
            </svg>
            <div class="curve-labels">
              <span>学习</span>
              <span>20分钟</span>
              <span>1天</span>
              <span>2天</span>
              <span>4天</span>
              <span>7天</span>
              <span>15天</span>
            </div>
          </div>
          <div class="curve-legend">
            <span><i></i>遗忘曲线</span>
            <span><b></b>复习节点（记忆提升）</span>
          </div>
        </section>

        <section class="plans-panel">
          <div class="panel-title">
            <div>
              <h2>复习计划</h2>
              <p>创建后的复习计划会直接显示在这里</p>
            </div>
            <span>{{ filteredGroups.length }} 项</span>
          </div>
          <div class="review-filters" role="tablist" aria-label="复习计划状态筛选">
            <button
              v-for="item in reviewFilters"
              :key="item.value"
              :class="{ active: reviewFilter === item.value }"
              type="button"
              @click="reviewFilter = item.value"
            >
              {{ item.label }}
              <span>{{ filterCount(item.value) }}</span>
            </button>
          </div>
          <div class="review-list">
            <article v-for="group in filteredGroups" :key="group.id" class="review-card">
              <span :class="['subject-icon', subjectTone(group)]">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path :d="iconPath(group)" />
                </svg>
              </span>
              <div class="review-info">
                <h2>{{ group.title }}</h2>
                <p>{{ group.content || "暂无复习内容说明。" }}</p>
                <small>首次学习：{{ formatDateTime(group.firstTime) }}</small>
              </div>
              <div class="review-progress">
                <div class="steps">
                  <span v-for="(step, index) in REVIEW_STEPS" :key="step" :class="{ done: index < group.finishedCount, current: index === group.currentIndex }">
                    <i></i>
                    <em>{{ step }}</em>
                  </span>
                </div>
                <p>
                  下次复习：
                  <strong>{{ nextReviewText(group) }}</strong>
                </p>
              </div>
              <span class="stage-pill">第 {{ Math.min(group.finishedCount + 1, REVIEW_STEPS.length) }} 次复习</span>
              <div class="card-actions">
                <button class="primary-button compact" type="button" :disabled="!group.nextPlan || loading" @click="finishPlan(group.nextPlan)">
                  开始复习
                </button>
              </div>
            </article>
            <p v-if="!filteredGroups.length" class="empty-state">暂无匹配的复习计划。</p>
          </div>
        </section>
      </main>

      <aside class="review-side">
        <section class="stats-grid">
          <article v-for="stat in stats" :key="stat.label">
            <span :class="['stat-icon', stat.tone]">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path :d="stat.icon" /></svg>
            </span>
            <div>
              <small>{{ stat.label }}</small>
              <strong>{{ stat.value }}</strong>
            </div>
          </article>
        </section>

        <section class="side-card">
          <div class="side-title">
            <h2>今日待复习</h2>
            <button type="button" @click="keyword = ''">查看全部</button>
          </div>
          <div v-if="todayPlans.length" class="today-list">
            <button v-for="plan in todayPlans" :key="plan.id" class="today-row" type="button" @click="finishPlan(plan)">
              <i :class="subjectTone(plan)"></i>
              <time>{{ formatClock(plan.reviewTime) }}</time>
              <strong>{{ plan.title || `复习任务 ${plan.reviewTaskId}` }}</strong>
              <span>第 {{ planStage(plan) }} 次</span>
            </button>
          </div>
          <p v-else class="empty-state">今天暂时没有到期复习。</p>
        </section>

        <section class="side-card">
          <div class="side-title">
            <h2>记忆进度</h2>
            <button type="button">查看详情</button>
          </div>
          <div class="memory-bars">
            <div v-for="item in memoryProgress" :key="item.label">
              <span>{{ item.label }}</span>
              <b><i :class="item.tone" :style="{ width: `${item.value}%` }"></i></b>
              <strong>{{ item.value }}%</strong>
            </div>
          </div>
          <p class="side-note">基于近期复习完成情况综合计算</p>
        </section>
      </aside>
    </div>

    <div v-if="creatorOpen" class="modal-backdrop" @click.self="closeCreator">
      <form class="review-modal" @submit.prevent="create">
        <div class="modal-head">
          <h2>新建复习计划</h2>
          <button class="close-button" type="button" @click="closeCreator">×</button>
        </div>
        <label>
          复习标题
          <input v-model.trim="form.title" placeholder="例如：英语单词 Unit 1" />
        </label>
        <label>
          复习内容
          <textarea v-model.trim="form.content" placeholder="可选，例如：高频基础词汇 50 个"></textarea>
        </label>
        <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
        <div class="modal-actions">
          <button class="secondary-button" type="button" @click="closeCreator">取消</button>
          <button class="primary-button compact" :disabled="loading" type="submit">创建</button>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { reviewApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const REVIEW_STEPS = ["20分钟", "1天", "2天", "4天", "7天", "15天"];
const plans = ref([]);
const keyword = ref("");
const reviewFilter = ref("active");
const creatorOpen = ref(false);
const form = reactive({ title: "", content: "" });
const { loading, status, ok, run } = useRequest();

const reviewFilters = [
  { label: "未完成", value: "active" },
  { label: "已完成", value: "finished" },
  { label: "全部", value: "all" }
];

const groupedPlans = computed(() => {
  const map = new Map();
  plans.value.forEach((plan) => {
    const id = plan.reviewTaskId || plan.id;
    if (!map.has(id)) {
      map.set(id, {
        id,
        title: plan.title || `复习任务 ${id}`,
        content: plan.content || "",
        plans: []
      });
    }
    map.get(id).plans.push(plan);
  });
  return [...map.values()].map((group) => {
    const sorted = group.plans.sort((a, b) => parseDate(a.reviewTime) - parseDate(b.reviewTime));
    const finishedCount = sorted.filter(isFinished).length;
    const nextPlan = sorted.find((plan) => !isFinished(plan)) || null;
    return {
      ...group,
      plans: sorted,
      firstTime: sorted[0]?.createTime || sorted[0]?.reviewTime,
      finishedCount,
      nextPlan,
      currentIndex: nextPlan ? Math.max(0, sorted.findIndex((plan) => plan.id === nextPlan.id)) : REVIEW_STEPS.length - 1
    };
  }).sort((a, b) => parseDate(b.firstTime) - parseDate(a.firstTime));
});

const filteredGroups = computed(() => {
  const term = keyword.value.toLowerCase();
  return groupedPlans.value.filter((group) => {
    if (!matchesFilter(group, reviewFilter.value)) return false;
    if (!term) return true;
    return [group.title, group.content].some((value) => String(value || "").toLowerCase().includes(term));
  });
});

const todayPlans = computed(() => {
  const today = new Date();
  return plans.value
    .filter((plan) => !isFinished(plan) && isSameDay(parseDate(plan.reviewTime), today))
    .sort((a, b) => parseDate(a.reviewTime) - parseDate(b.reviewTime));
});

const thisWeekFinished = computed(() => {
  const start = startOfWeek(new Date());
  const end = addDays(start, 7);
  return groupedPlans.value.filter((group) => {
    if (!group.plans.length || group.finishedCount < group.plans.length) return false;
    const lastFinish = group.plans.reduce((latest, plan) => {
      const finishTime = parseDate(plan.finishTime);
      return finishTime > latest ? finishTime : latest;
    }, new Date(0));
    return lastFinish >= start && lastFinish < end;
  }).length;
});

const stats = computed(() => [
  { label: "今日待复习", value: todayPlans.value.length, tone: "blue", icon: "M8 2v4M16 2v4M3.5 9.5h17M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z" },
  { label: "本周完成", value: thisWeekFinished.value, tone: "green", icon: "m5 12 4 4L19 6" },
  { label: "记忆阶段", value: `第 ${currentStage.value} 阶段`, tone: "purple", icon: "M4 12h4l3-8 4 16 3-8h2" },
  { label: "总复习任务", value: groupedPlans.value.length, tone: "orange", icon: "M7 3h10v18H7zM10 7h4M10 11h4M10 15h2" }
]);

const currentStage = computed(() => {
  if (!groupedPlans.value.length) return 1;
  const latest = groupedPlans.value[0];
  return Math.min(REVIEW_STEPS.length, Math.max(1, latest.finishedCount + 1));
});

const memoryProgress = computed(() => {
  const tones = ["blue", "orange", "purple", "green"];
  const groups = groupedPlans.value.slice(0, 4);
  if (!groups.length) return [
    { label: "英语", value: 0, tone: "blue" },
    { label: "历史", value: 0, tone: "orange" },
    { label: "编程", value: 0, tone: "purple" },
    { label: "数学", value: 0, tone: "green" }
  ];
  return groups.map((group, index) => ({
    label: subjectLabel(group),
    value: Math.round(group.finishedCount / REVIEW_STEPS.length * 100),
    tone: tones[index % tones.length]
  }));
});

function parseDate(value) {
  if (!value) return new Date(0);
  const date = new Date(String(value).replace(" ", "T"));
  return Number.isNaN(date.getTime()) ? new Date(0) : date;
}

function isFinished(plan) {
  return Number(plan?.isFinish || 0) === 1;
}

function isGroupFinished(group) {
  return Boolean(group?.plans?.length) && group.finishedCount >= group.plans.length;
}

function matchesFilter(group, filter) {
  if (filter === "finished") return isGroupFinished(group);
  if (filter === "active") return !isGroupFinished(group);
  return true;
}

function filterCount(filter) {
  return groupedPlans.value.filter((group) => matchesFilter(group, filter)).length;
}

function isSameDay(a, b) {
  return a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();
}

function startOfWeek(date) {
  const base = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  const day = base.getDay() || 7;
  base.setDate(base.getDate() - day + 1);
  return base;
}

function addDays(date, count) {
  const next = new Date(date);
  next.setDate(next.getDate() + count);
  return next;
}

function pad(value) {
  return String(value).padStart(2, "0");
}

function formatDateTime(value) {
  const date = parseDate(value);
  if (date.getTime() === 0) return "未安排";
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function formatClock(value) {
  const date = parseDate(value);
  if (date.getTime() === 0) return "--:--";
  return `${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function nextReviewText(group) {
  if (!group.nextPlan) return "已全部完成";
  const date = parseDate(group.nextPlan.reviewTime);
  const now = new Date();
  if (isSameDay(date, now)) return `今天 ${formatClock(date)}`;
  if (isSameDay(date, addDays(now, 1))) return `明天 ${formatClock(date)}`;
  return `${date.getMonth() + 1}月${date.getDate()}日 ${formatClock(date)}`;
}

function planStage(plan) {
  const group = groupedPlans.value.find((item) => item.id === plan.reviewTaskId);
  if (!group) return 1;
  const index = group.plans.findIndex((item) => item.id === plan.id);
  return index + 1;
}

function subjectLabel(group) {
  const title = group?.title || "";
  const text = `${title} ${group?.content || ""}`;
  if (/英语|单词|阅读/.test(text)) return "英语";
  if (/历史|时间线|政治/.test(text)) return "历史";
  if (/Java|代码|编程|接口|Vue/.test(text)) return "编程";
  if (/数学|函数|公式/.test(text)) return "数学";
  return title ? title.slice(0, 4) : "复习";
}

function subjectTone(group) {
  const label = subjectLabel(group);
  if (label === "英语") return "blue";
  if (label === "历史") return "orange";
  if (label === "编程") return "purple";
  if (label === "数学") return "green";
  return "blue";
}

function iconPath(group) {
  const label = subjectLabel(group);
  if (label === "英语") return "M4 19.5V5.8A2.8 2.8 0 0 1 6.8 3H20v15.5H6.8A2.8 2.8 0 0 0 4 21.3Zm0 0A2.8 2.8 0 0 1 6.8 17H20M8 7h8M8 10h6";
  if (label === "历史") return "M12 8v5l3 2M21 12a9 9 0 1 1-3.1-6.8M21 4v5h-5";
  if (label === "编程") return "m8 9-4 3 4 3m8-6 4 3-4 3M14 5l-4 14";
  if (label === "数学") return "M6 5h12M6 19h12M8 5l7 7-7 7";
  return "M12 3l2.5 5.1 5.6.8-4 3.9.9 5.5-5-2.6-5 2.6.9-5.5-4-3.9 5.6-.8Z";
}

function openCreator() {
  form.title = "";
  form.content = "";
  status.value = "";
  creatorOpen.value = true;
}

function closeCreator() {
  creatorOpen.value = false;
}

async function loadList(options = {}) {
  const data = options.silent ? await reviewApi.list() : await run(() => reviewApi.list(), "列表已刷新");
  plans.value = Array.isArray(data?.data) ? data.data : [];
}

async function create() {
  if (!form.title) {
    status.value = "请填写复习标题";
    ok.value = false;
    return;
  }
  const data = await run(() => reviewApi.create({ title: form.title, content: form.content || form.title }));
  if (data?.code === 200) {
    await loadList({ silent: true });
    closeCreator();
    status.value = "创建复习任务成功";
    ok.value = true;
  }
}

async function finishPlan(plan) {
  if (!plan?.id) return;
  const data = await run(() => reviewApi.finish(plan.id));
  if (data?.code === 200) {
    await loadList({ silent: true });
    status.value = "复习完成成功";
    ok.value = true;
  }
}

onMounted(() => loadList({ silent: true }));
</script>

<style scoped>
.review-page {
  min-height: 100vh;
  padding: 28px;
  background: #f7faff;
}

.review-head {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-start;
}

.review-head h1 {
  color: #111a33;
  font-size: 30px;
}

.review-head p {
  margin-top: 6px;
  color: #64718a;
  font-weight: 800;
}

.head-actions {
  display: grid;
  grid-template-columns: 190px 440px;
  gap: 16px;
  align-items: center;
}

.primary-button,
.secondary-button,
.close-button {
  border-radius: 8px;
  font-weight: 900;
}

.primary-button {
  display: inline-flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
  border-color: #2563eb;
  background: #2563eb;
  box-shadow: 0 10px 22px rgba(37, 99, 235, .18);
}

.primary-button.compact {
  min-width: 116px;
}

.secondary-button,
.close-button {
  border-color: #d8e1ee;
  color: #314158;
  background: #fff;
}

.search-box {
  position: relative;
}

.search-box input {
  height: 48px;
  padding-left: 44px;
  border-color: #d8e1ee;
}

.search-box svg {
  position: absolute;
  left: 14px;
  top: 50%;
  width: 22px;
  height: 22px;
  fill: none;
  stroke: #71809a;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  transform: translateY(-50%);
}

.review-layout {
  display: grid;
  grid-template-columns: minmax(680px, 1fr) 360px;
  gap: 22px;
  margin-top: 26px;
}

.review-main,
.review-side {
  display: grid;
  gap: 16px;
  align-content: start;
}

.curve-card,
.plans-panel,
.review-card,
.side-card,
.stats-grid article,
.review-modal {
  border: 1px solid #dfe7f3;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 14px 30px rgba(58, 82, 126, .06);
}

.curve-card {
  padding: 20px 24px;
}

.plans-panel {
  display: grid;
  gap: 14px;
  padding: 18px;
}

.curve-title,
.panel-title,
.side-title {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: center;
}

.curve-title h2,
.panel-title h2,
.side-title h2 {
  color: #172033;
  font-size: 18px;
}

.curve-title p,
.panel-title p {
  margin-top: 8px;
  color: #64718a;
  font-weight: 800;
}

.panel-title span {
  border-radius: 999px;
  padding: 7px 12px;
  color: #2563eb;
  background: #eff6ff;
  font-weight: 900;
}

.review-filters {
  display: flex;
  gap: 8px;
  align-items: center;
  overflow-x: auto;
  padding-bottom: 2px;
}

.review-filters button {
  display: inline-flex;
  gap: 8px;
  align-items: center;
  min-height: 36px;
  border-color: #d8e1ee;
  border-radius: 999px;
  color: #52627a;
  background: #fff;
  box-shadow: none;
  font-weight: 900;
  white-space: nowrap;
}

.review-filters button:hover,
.review-filters button.active {
  border-color: #2563eb;
  color: #2563eb;
  background: #eff6ff;
}

.review-filters span {
  display: inline-grid;
  place-items: center;
  min-width: 22px;
  height: 22px;
  border-radius: 999px;
  padding: 0 7px;
  color: #fff;
  background: #94a3b8;
  font-size: 12px;
}

.review-filters button.active span {
  background: #2563eb;
}

.curve-title span {
  border-radius: 8px;
  padding: 8px 12px;
  color: #2563eb;
  background: #eff6ff;
  font-weight: 900;
}

.curve-chart {
  margin-top: 18px;
}

.curve-chart svg {
  display: block;
  width: 100%;
  min-height: 220px;
}

.grid-lines path {
  fill: none;
  stroke: #e2e8f0;
  stroke-dasharray: 3 5;
}

.axis {
  fill: none;
  stroke: #b8c3d4;
  stroke-width: 2;
}

.forget-line {
  fill: none;
  stroke: #2563eb;
  stroke-width: 3;
}

.curve-points circle {
  fill: #2563eb;
}

.review-jumps path {
  fill: none;
  stroke: #2563eb;
  stroke-width: 2;
}

.curve-labels {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  color: #64718a;
  font-weight: 800;
  text-align: center;
}

.curve-legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 10px;
  color: #52627a;
  font-weight: 800;
}

.curve-legend span {
  display: inline-flex;
  gap: 8px;
  align-items: center;
}

.curve-legend i,
.curve-legend b {
  width: 18px;
  height: 3px;
  border-radius: 999px;
  background: #2563eb;
}

.curve-legend b {
  width: 10px;
  height: 10px;
}

.review-list {
  display: grid;
  gap: 14px;
  max-height: 430px;
  min-height: 220px;
  overflow: auto;
  padding-right: 4px;
}

.review-card {
  display: grid;
  grid-template-columns: 72px minmax(180px, 1.2fr) minmax(260px, 1.8fr) auto 120px;
  gap: 16px;
  align-items: center;
  padding: 18px 20px;
}

.subject-icon {
  display: grid;
  place-items: center;
  width: 58px;
  height: 58px;
  border-radius: 12px;
  color: #fff;
}

.subject-icon svg {
  width: 30px;
  height: 30px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.subject-icon.blue { background: linear-gradient(135deg, #70a7ff, #2563eb); }
.subject-icon.orange { background: linear-gradient(135deg, #ffc16b, #f59e0b); }
.subject-icon.purple { background: linear-gradient(135deg, #b588ff, #7c3aed); }
.subject-icon.green { background: linear-gradient(135deg, #62d58d, #16a765); }

.review-info h2 {
  color: #172033;
  font-size: 17px;
}

.review-info p,
.review-info small {
  display: block;
  margin-top: 5px;
  color: #64718a;
  font-weight: 800;
}

.review-progress {
  display: grid;
  gap: 12px;
}

.steps {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 0;
}

.steps span {
  position: relative;
  display: grid;
  justify-items: center;
  gap: 8px;
  color: #64718a;
  font-size: 12px;
  font-weight: 900;
}

.steps span::before {
  content: "";
  position: absolute;
  top: 7px;
  left: 0;
  right: 0;
  height: 2px;
  background: #dbe3ef;
}

.steps span.done::before,
.steps span.current::before {
  background: #2563eb;
}

.steps i {
  position: relative;
  z-index: 1;
  width: 14px;
  height: 14px;
  border: 2px solid #aab8cc;
  border-radius: 50%;
  background: #fff;
}

.steps span.done i,
.steps span.current i {
  border-color: #2563eb;
  background: #2563eb;
}

.steps span.current i {
  box-shadow: 0 0 0 4px #dbeafe;
}

.review-progress p {
  color: #64718a;
  font-weight: 800;
  text-align: center;
}

.review-progress strong {
  color: #2563eb;
}

.stage-pill {
  border-radius: 10px;
  padding: 8px 12px;
  color: #2563eb;
  background: #eff6ff;
  font-weight: 900;
  white-space: nowrap;
}

.card-actions {
  display: grid;
  gap: 8px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.stats-grid article {
  display: flex;
  gap: 12px;
  align-items: center;
  min-height: 92px;
  padding: 18px;
}

.stat-icon {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  color: #fff;
}

.stat-icon svg {
  width: 23px;
  height: 23px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.stat-icon.blue { background: #2563eb; }
.stat-icon.green { background: #16a765; }
.stat-icon.purple { background: #8b5cf6; }
.stat-icon.orange { background: #f59e0b; }

.stats-grid small {
  color: #64718a;
  font-weight: 800;
}

.stats-grid strong {
  display: block;
  margin-top: 4px;
  color: #172033;
  font-size: 22px;
}

.side-card {
  display: grid;
  gap: 14px;
  padding: 18px;
}

.side-title button {
  border: 0;
  min-height: 0;
  padding: 0;
  color: #64718a;
  background: transparent;
  font-weight: 900;
}

.today-list {
  display: grid;
  gap: 10px;
  max-height: 260px;
  overflow: auto;
}

.today-row {
  display: grid;
  grid-template-columns: 10px 48px minmax(0, 1fr) auto;
  gap: 10px;
  align-items: center;
  border: 0;
  border-bottom: 1px solid #edf2f8;
  border-radius: 0;
  padding: 9px 0;
  color: #334155;
  background: transparent;
  text-align: left;
}

.today-row i {
  width: 9px;
  height: 9px;
  border-radius: 50%;
}

.today-row i.blue { background: #2563eb; }
.today-row i.orange { background: #f59e0b; }
.today-row i.purple { background: #8b5cf6; }
.today-row i.green { background: #16a765; }

.today-row time {
  font-weight: 900;
}

.today-row strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.today-row span {
  border-radius: 999px;
  padding: 5px 9px;
  color: #2563eb;
  background: #eff6ff;
  font-size: 12px;
  font-weight: 900;
}

.memory-bars {
  display: grid;
  gap: 13px;
}

.memory-bars div {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr) 44px;
  gap: 10px;
  align-items: center;
  color: #52627a;
  font-weight: 900;
}

.memory-bars b {
  height: 8px;
  overflow: hidden;
  border-radius: 999px;
  background: #e8edf5;
}

.memory-bars i {
  display: block;
  height: 100%;
  border-radius: inherit;
}

.memory-bars i.blue { background: #2563eb; }
.memory-bars i.orange { background: #f59e0b; }
.memory-bars i.purple { background: #8b5cf6; }
.memory-bars i.green { background: #16a765; }

.side-note,
.empty-state {
  color: #64718a;
  font-weight: 800;
}

.empty-state {
  border: 1px dashed #d8e1ee;
  border-radius: 10px;
  padding: 18px;
  background: #fbfdff;
  text-align: center;
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

.review-modal {
  display: grid;
  gap: 14px;
  width: min(560px, 100%);
  padding: 22px;
}

.modal-head p {
  margin-top: 6px;
  color: #64718a;
  font-weight: 800;
}

.modal-head,
.modal-actions {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.close-button {
  width: 38px;
  min-height: 38px;
  padding: 0;
  font-size: 22px;
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
  .review-layout,
  .review-card {
    grid-template-columns: 1fr;
  }

  .review-side {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .review-page {
    padding: 16px 12px;
  }

  .review-head,
  .head-actions,
  .review-side,
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .review-head {
    display: grid;
  }

  .curve-labels,
  .steps {
    font-size: 11px;
  }
}
</style>
