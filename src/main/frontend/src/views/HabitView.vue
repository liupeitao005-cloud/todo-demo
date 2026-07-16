<template>
  <section class="habit-page">
    <header class="habit-head">
      <div>
        <h1>今日习惯</h1>
        <p>养成好习惯，成就更好的自己</p>
      </div>
      <div class="head-actions">
        <div class="date-switch">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 2v4M16 2v4M3.5 9.5h17M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z" /></svg>
          <button type="button" title="前一天" @click="shiftDay(-1)">‹</button>
          <strong>{{ formatDate(selectedDate) }}</strong>
          <button type="button" title="后一天" @click="shiftDay(1)">›</button>
        </div>
        <button class="today-button" type="button" @click="selectedDate = todayKey">今天</button>
        <button class="primary-button" type="button" @click="openCreator">+ 创建习惯</button>
      </div>
    </header>

    <div class="summary-grid">
      <article>
        <span class="summary-ring">{{ completedCount }}/{{ items.length || 0 }}</span>
        <div>
          <small>今日完成</small>
          <strong>{{ completionRate }}%</strong>
          <p>完成率</p>
        </div>
      </article>
      <article>
        <span class="summary-icon green">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m5 12 4 4L19 6" /></svg>
        </span>
        <div>
          <small>连续坚持</small>
          <strong>{{ longestStreak }} 天</strong>
          <p>最长连续</p>
        </div>
      </article>
      <article>
        <span class="summary-icon amber">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m12 3 2.6 5.3 5.8.8-4.2 4.1 1 5.8-5.2-2.7L6.8 19l1-5.8-4.2-4.1 5.8-.8Z" /></svg>
        </span>
        <div>
          <small>累计打卡</small>
          <strong>{{ totalChecks }} 次</strong>
          <p>总打卡次数</p>
        </div>
      </article>
      <article>
        <span class="summary-icon purple">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 21h8M12 17v4M7 4h10v5a5 5 0 0 1-10 0ZM5 6H3v2a4 4 0 0 0 4 4M19 6h2v2a4 4 0 0 1-4 4" /></svg>
        </span>
        <div>
          <small>习惯总数</small>
          <strong>{{ items.length }} 个</strong>
          <p>进行中习惯</p>
        </div>
      </article>
    </div>

    <div class="habit-layout">
      <main class="today-panel">
        <div class="panel-title">
          <h2>今日习惯</h2>
          <span>{{ completedCount }} / {{ items.length || 0 }} 个习惯完成</span>
        </div>
        <div v-if="items.length" class="habit-list">
          <article v-for="habit in items" :key="habit.id" class="habit-row">
            <span :class="['habit-icon', iconTone(habit)]">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path :d="habitIcon(habit)" /></svg>
            </span>
            <div class="habit-info">
              <h3>{{ habit.title }}</h3>
              <p>{{ habit.content || "暂无习惯说明。" }}</p>
              <small>{{ habitFrequency(habit) }}</small>
            </div>
            <button :class="['check-button', { done: isChecked(habit.id) }]" type="button" @click="toggleCheck(habit.id)">
              <span></span>
              {{ isChecked(habit.id) ? "已完成" : "未完成" }}
            </button>
          </article>
        </div>
        <p v-else class="empty-state">暂无习惯，点击右上角创建第一个习惯。</p>
      </main>

      <aside class="side-stack">
        <section class="calendar-card">
          <div class="side-title">
            <h2>打卡日历</h2>
            <div>
              <button type="button" @click="shiftMonth(-1)">‹</button>
              <strong>{{ calendarTitle }}</strong>
              <button type="button" @click="shiftMonth(1)">›</button>
            </div>
          </div>
          <div class="weekday-row">
            <span v-for="day in weekdays" :key="day">{{ day }}</span>
          </div>
          <div class="calendar-grid">
            <button
              v-for="day in calendarDays"
              :key="day.key"
              :class="['calendar-day', day.status, { muted: !day.inMonth, selected: day.key === selectedDate }]"
              type="button"
              @click="selectCalendarDay(day)"
            >
              {{ day.date.getDate() }}
            </button>
          </div>
          <div class="calendar-legend">
            <span><i class="done"></i>全部完成</span>
            <span><i class="partial"></i>部分完成</span>
            <span><i></i>未打卡</span>
          </div>
        </section>

        <section class="week-card">
          <div class="side-title">
            <h2>本周完成情况</h2>
            <strong>{{ weekCompleted }}/{{ weekTotal }} 个习惯完成</strong>
          </div>
          <div class="week-bars">
            <div v-for="day in weekStats" :key="day.key">
              <span>{{ day.label }}</span>
              <b><i :style="{ width: `${day.percent}%` }"></i></b>
              <em>{{ day.done }}/{{ items.length || 0 }}</em>
            </div>
          </div>
        </section>
      </aside>
    </div>

    <div v-if="creatorOpen" class="modal-backdrop" @click.self="closeCreator">
      <form class="habit-modal" @submit.prevent="create">
        <div class="modal-head">
          <h2>创建习惯</h2>
          <button class="close-button" type="button" @click="closeCreator">×</button>
        </div>
        <label>
          习惯标题
          <input v-model.trim="form.title" placeholder="例如：阅读30分钟" />
        </label>
        <label>
          习惯内容
          <textarea v-model.trim="form.content" placeholder="例如：每天晚上读专业书"></textarea>
        </label>
        <div class="form-grid">
          <label>每日分钟<input v-model.number="form.dayMinutes" type="number" min="0" placeholder="30" /></label>
          <label>最少分钟<input v-model.number="form.minMinutes" type="number" min="0" placeholder="10" /></label>
          <label>最多分钟<input v-model.number="form.maxMinutes" type="number" min="0" placeholder="30" /></label>
        </div>
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
import { useRoute } from "vue-router";
import { habitApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const route = useRoute();
const weekdays = ["日", "一", "二", "三", "四", "五", "六"];
const todayKey = toDateKey(new Date());
const selectedDate = ref(todayKey);
const calendarMonth = ref(startOfMonth(new Date()));
const creatorOpen = ref(false);
const items = ref([]);
const checks = ref(loadChecks());
const form = reactive({ title: "", content: "", dayMinutes: null, minMinutes: null, maxMinutes: null });
const { loading, status, ok, run } = useRequest();

const selectedChecks = computed(() => checks.value[selectedDate.value] || []);
const completedCount = computed(() => items.value.filter((item) => selectedChecks.value.includes(Number(item.id))).length);
const completionRate = computed(() => items.value.length ? Math.round(completedCount.value / items.value.length * 100) : 0);
const totalChecks = computed(() => Object.values(checks.value).reduce((sum, ids) => sum + ids.length, 0));

const longestStreak = computed(() => {
  let best = 0;
  let current = 0;
  for (let offset = 90; offset >= 0; offset--) {
    const date = addDays(new Date(), -offset);
    if (dayStatus(toDateKey(date)) === "done") {
      current++;
      best = Math.max(best, current);
    } else {
      current = 0;
    }
  }
  return best;
});

const calendarTitle = computed(() => `${calendarMonth.value.getFullYear()}年${calendarMonth.value.getMonth() + 1}月`);

const calendarDays = computed(() => {
  const start = startOfMonth(calendarMonth.value);
  const first = new Date(start);
  first.setDate(first.getDate() - first.getDay());
  return Array.from({ length: 42 }, (_, index) => {
    const date = addDays(first, index);
    const key = toDateKey(date);
    return {
      date,
      key,
      inMonth: date.getMonth() === calendarMonth.value.getMonth(),
      status: dayStatus(key)
    };
  });
});

const weekStats = computed(() => {
  const start = startOfWeek(parseDate(selectedDate.value));
  return Array.from({ length: 7 }, (_, index) => {
    const date = addDays(start, index);
    const key = toDateKey(date);
    const done = (checks.value[key] || []).filter((id) => items.value.some((item) => Number(item.id) === Number(id))).length;
    return {
      key,
      label: `周${["一", "二", "三", "四", "五", "六", "日"][index]}`,
      done,
      percent: items.value.length ? Math.round(done / items.value.length * 100) : 0
    };
  });
});

const weekCompleted = computed(() => weekStats.value.reduce((sum, day) => sum + day.done, 0));
const weekTotal = computed(() => weekStats.value.length * items.value.length);

function loadChecks() {
  try {
    return JSON.parse(localStorage.getItem("todo-habit-checks") || "{}");
  } catch {
    return {};
  }
}

function saveChecks() {
  localStorage.setItem("todo-habit-checks", JSON.stringify(checks.value));
}

function toDateKey(date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

function parseDate(key) {
  return new Date(`${key}T00:00:00`);
}

function pad(value) {
  return String(value).padStart(2, "0");
}

function formatDate(key) {
  const date = parseDate(key);
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

function addDays(date, count) {
  const next = new Date(date);
  next.setDate(next.getDate() + count);
  return next;
}

function startOfMonth(date) {
  return new Date(date.getFullYear(), date.getMonth(), 1);
}

function startOfWeek(date) {
  const base = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  const day = base.getDay() || 7;
  base.setDate(base.getDate() - day + 1);
  return base;
}

function shiftDay(count) {
  selectedDate.value = toDateKey(addDays(parseDate(selectedDate.value), count));
  calendarMonth.value = startOfMonth(parseDate(selectedDate.value));
}

function shiftMonth(count) {
  calendarMonth.value = new Date(calendarMonth.value.getFullYear(), calendarMonth.value.getMonth() + count, 1);
}

function selectCalendarDay(day) {
  selectedDate.value = day.key;
  calendarMonth.value = startOfMonth(day.date);
}

function dayStatus(key) {
  if (!items.value.length) return "";
  const count = (checks.value[key] || []).filter((id) => items.value.some((item) => Number(item.id) === Number(id))).length;
  if (count === 0) return "";
  return count >= items.value.length ? "done" : "partial";
}

function isChecked(id) {
  return selectedChecks.value.includes(Number(id));
}

function toggleCheck(id) {
  const key = selectedDate.value;
  const numberId = Number(id);
  const set = new Set(checks.value[key] || []);
  if (set.has(numberId)) {
    set.delete(numberId);
  } else {
    set.add(numberId);
  }
  checks.value = { ...checks.value, [key]: [...set] };
  saveChecks();
}

function habitFrequency(habit) {
  const total = Number(habit.dayMinutes || 0);
  const min = Number(habit.minMinutes || 0);
  const max = Number(habit.maxMinutes || 0);
  if (total) return `每天 ${total} 分钟`;
  if (min || max) return `单次 ${min || 0}-${max || 0} 分钟`;
  return "每天";
}

function iconTone(habit) {
  const text = `${habit.title || ""}${habit.content || ""}`;
  if (/跑|走|运动|健身/.test(text)) return "green";
  if (/学习|写|技能|阅读/.test(text)) return "blue";
  if (/水|喝/.test(text)) return "cyan";
  if (/早|睡|起/.test(text)) return "purple";
  return "amber";
}

function habitIcon(habit) {
  const tone = iconTone(habit);
  if (tone === "green") return "M13 4a2 2 0 1 1-4 0 2 2 0 0 1 4 0ZM7 21l2-5-2-3 3-4 3 2 3-1 1 2-4 1-2-3-4-1-3 4-2 3 2 4-1 5";
  if (tone === "blue") return "M4 19.5V5.8A2.8 2.8 0 0 1 6.8 3H20v15.5H6.8A2.8 2.8 0 0 0 4 21.3Zm0 0A2.8 2.8 0 0 1 6.8 17H20M8 7h8M8 10h6";
  if (tone === "cyan") return "M12 3s6 6.2 6 11a6 6 0 0 1-12 0c0-4.8 6-11 6-11Z";
  if (tone === "purple") return "M21 12.8A8.5 8.5 0 1 1 11.2 3 6.5 6.5 0 0 0 21 12.8Z";
  return "M12 3l2.6 5.3 5.8.8-4.2 4.1 1 5.8-5.2-2.7L6.8 19l1-5.8-4.2-4.1 5.8-.8Z";
}

function openCreator() {
  form.title = "";
  form.content = "";
  form.dayMinutes = null;
  form.minMinutes = null;
  form.maxMinutes = null;
  status.value = "";
  creatorOpen.value = true;
}

function closeCreator() {
  creatorOpen.value = false;
}

async function loadList(options = {}) {
  const data = options.silent ? await habitApi.list() : await run(() => habitApi.list(), "列表已刷新");
  items.value = Array.isArray(data?.data) ? data.data : [];
}

async function create() {
  if (!form.title) {
    status.value = "请填写习惯标题";
    ok.value = false;
    return;
  }
  const payload = { ...form, content: form.content || form.title };
  const data = await run(() => habitApi.create(payload));
  if (data?.code === 200) {
    await loadList({ silent: true });
    closeCreator();
    status.value = "创建成功";
    ok.value = true;
  }
}

onMounted(async () => {
  await loadList({ silent: true });
  if (route.query.create === "1") openCreator();
});
</script>

<style scoped>
.habit-page {
  min-height: 100vh;
  padding: 26px 28px;
  background: #f6f9fe;
}

.habit-head,
.summary-grid,
.habit-layout,
.side-stack {
  display: grid;
  gap: 16px;
}

.habit-head {
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
}

.habit-head h1 {
  color: #111a33;
  font-size: 28px;
}

.habit-head p {
  margin-top: 6px;
  color: #64718a;
  font-weight: 800;
}

.head-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.date-switch {
  display: inline-flex;
  gap: 8px;
  align-items: center;
  min-height: 42px;
  border: 1px solid #d8e1ee;
  border-radius: 8px;
  padding: 0 8px 0 12px;
  background: #fff;
}

.date-switch svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: #64718a;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.date-switch button,
.today-button,
.side-title button,
.secondary-button,
.close-button {
  border-color: #d8e1ee;
  color: #314158;
  background: #fff;
}

.date-switch button,
.side-title button {
  min-height: 30px;
  padding: 4px 8px;
}

.primary-button {
  border-color: #2563eb;
  background: #2563eb;
  font-weight: 900;
  box-shadow: 0 10px 22px rgba(37, 99, 235, .16);
}

.summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-top: 22px;
}

.summary-grid article,
.today-panel,
.calendar-card,
.week-card,
.habit-modal {
  border: 1px solid #dfe7f3;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 14px 30px rgba(58, 82, 126, .06);
}

.summary-grid article {
  display: flex;
  gap: 16px;
  align-items: center;
  min-height: 100px;
  padding: 18px 22px;
}

.summary-ring,
.summary-icon {
  display: grid;
  place-items: center;
  width: 58px;
  height: 58px;
  border-radius: 18px;
  color: #2563eb;
  background: conic-gradient(#2563eb calc(var(--rate, 75) * 1%), #e8eef7 0);
  font-weight: 900;
}

.summary-icon {
  color: #fff;
}

.summary-icon svg {
  width: 26px;
  height: 26px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.4;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.summary-icon.green { background: #20b66c; }
.summary-icon.amber { background: #f59e0b; }
.summary-icon.purple { background: #9b5cf6; }

.summary-grid small,
.summary-grid p {
  color: #64718a;
  font-weight: 800;
}

.summary-grid strong {
  display: block;
  margin: 4px 0;
  color: #111a33;
  font-size: 26px;
}

.habit-layout {
  grid-template-columns: minmax(620px, 1fr) 420px;
  align-items: start;
  margin-top: 16px;
}

.today-panel,
.calendar-card,
.week-card {
  padding: 20px;
}

.panel-title,
.side-title {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.panel-title h2,
.side-title h2 {
  color: #111a33;
  font-size: 19px;
}

.panel-title span,
.side-title strong {
  color: #16a765;
  font-weight: 900;
}

.side-title div {
  display: inline-flex;
  gap: 8px;
  align-items: center;
}

.habit-list {
  display: grid;
  gap: 8px;
  max-height: 560px;
  overflow: auto;
  padding-right: 4px;
}

.habit-row {
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
  border-bottom: 1px solid #edf2f8;
  padding: 14px 0;
}

.habit-icon {
  display: grid;
  place-items: center;
  width: 48px;
  height: 48px;
  border-radius: 10px;
}

.habit-icon svg {
  width: 25px;
  height: 25px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.habit-icon.green { color: #16a765; background: #e9f9ef; }
.habit-icon.blue { color: #2563eb; background: #eaf2ff; }
.habit-icon.cyan { color: #0891b2; background: #e7f8fc; }
.habit-icon.purple { color: #8b5cf6; background: #f3ecff; }
.habit-icon.amber { color: #f59e0b; background: #fff6e5; }

.habit-info h3 {
  color: #172033;
  font-size: 16px;
}

.habit-info p,
.habit-info small {
  display: block;
  margin-top: 3px;
  color: #64718a;
  font-weight: 800;
}

.check-button {
  display: inline-flex;
  gap: 8px;
  align-items: center;
  border-color: #d8e1ee;
  color: #52627a;
  background: #fff;
  font-weight: 900;
}

.check-button span {
  width: 15px;
  height: 15px;
  border: 2px solid currentColor;
  border-radius: 50%;
}

.check-button.done {
  border-color: #b8e9cc;
  color: #16a765;
  background: #effbf3;
}

.check-button.done span {
  background: currentColor;
  box-shadow: inset 0 0 0 3px #effbf3;
}

.weekday-row,
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 8px;
  text-align: center;
}

.weekday-row {
  margin-bottom: 8px;
  color: #71809a;
  font-weight: 900;
}

.calendar-day {
  min-height: 38px;
  border: 0;
  border-radius: 999px;
  color: #172033;
  background: transparent;
  font-weight: 900;
}

.calendar-day.muted {
  color: #b0bac9;
}

.calendar-day.partial {
  background: #dff5e8;
}

.calendar-day.done {
  color: #fff;
  background: #16a765;
}

.calendar-day.selected {
  outline: 2px solid #2563eb;
}

.calendar-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 16px;
  color: #64718a;
  font-weight: 800;
}

.calendar-legend span {
  display: inline-flex;
  gap: 6px;
  align-items: center;
}

.calendar-legend i {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #d8e1ee;
}

.calendar-legend i.done { background: #16a765; }
.calendar-legend i.partial { background: #bdebd0; }

.week-bars {
  display: grid;
  gap: 10px;
}

.week-bars div {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) 42px;
  gap: 8px;
  align-items: center;
  color: #64718a;
  font-weight: 900;
}

.week-bars b {
  height: 10px;
  overflow: hidden;
  border-radius: 999px;
  background: #edf2f8;
}

.week-bars i {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #55c98b, #16a765);
}

.week-bars em {
  font-style: normal;
  text-align: right;
}

.empty-state {
  border: 1px dashed #d8e1ee;
  border-radius: 10px;
  padding: 28px;
  color: #64718a;
  background: #fbfdff;
  font-weight: 900;
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

.habit-modal {
  display: grid;
  gap: 14px;
  width: min(620px, 100%);
  padding: 22px;
}

.modal-head,
.modal-actions,
.form-grid {
  display: grid;
  gap: 12px;
}

.modal-head,
.modal-actions {
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
}

.form-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.close-button {
  width: 38px;
  min-height: 38px;
  padding: 0;
  font-size: 22px;
}

@media (max-width: 1180px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .habit-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .habit-page {
    padding: 18px 12px;
  }

  .habit-head,
  .head-actions,
  .summary-grid,
  .habit-row,
  .form-grid,
  .modal-head,
  .modal-actions {
    grid-template-columns: 1fr;
  }

  .head-actions {
    display: grid;
  }
}
</style>
