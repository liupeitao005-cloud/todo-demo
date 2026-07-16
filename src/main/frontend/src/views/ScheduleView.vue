<template>
  <section class="schedule-planner">
    <header class="planner-head">
      <div>
        <h1>行程</h1>
        <p>把每天的行程放进月历中，轻松查看与安排</p>
      </div>
      <button class="primary-action" type="button" @click="openCreate(selectedDate)">
        <span aria-hidden="true">+</span>
        新建行程
      </button>
    </header>

    <section class="planner-toolbar">
      <div class="period-controls">
        <button class="icon-button" type="button" title="上一段" @click="movePeriod(-1)">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m15 18-6-6 6-6" /></svg>
        </button>
        <strong>{{ rangeTitle }}</strong>
        <label class="month-picker">
          <span class="sr-only">选择月份</span>
          <input :value="monthInputValue" type="month" min="1970-01" max="2099-12" @change="jumpToMonth($event.target.value)" />
        </label>
        <button class="icon-button" type="button" title="下一段" @click="movePeriod(1)">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m9 18 6-6-6-6" /></svg>
        </button>
        <button class="today-button" type="button" @click="goToday">今天</button>
      </div>

      <label class="planner-search">
        <span class="sr-only">搜索行程</span>
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m21 21-4.3-4.3M10.8 18a7.2 7.2 0 1 1 0-14.4 7.2 7.2 0 0 1 0 14.4Z" /></svg>
        <input v-model.trim="keyword" placeholder="搜索行程、地点或内容" />
      </label>
    </section>

    <div class="planner-layout">
      <section class="month-card">
        <div class="weekday-row">
          <span v-for="day in weekdayNames" :key="day">{{ day }}</span>
        </div>

        <div v-if="loadingData" class="calendar-loading">正在整理行程...</div>
        <div v-else class="month-grid">
          <button
            v-for="day in visibleDays"
            :key="dateKey(day)"
            :class="['day-cell', { outside: !isCurrentPeriod(day), today: isSameDay(day, todayDate), selected: isSameDay(day, selectedDate) }]"
            type="button"
            @click="selectDay(day)"
          >
            <span class="date-line">
              <strong>{{ day.getDate() }}</strong>
              <small>{{ secondaryDateLabel(day) }}</small>
            </span>

            <span class="pill-stack">
              <span
                v-for="item in dayItems(day).slice(0, 3)"
                :key="`${dateKey(day)}-${item.key}`"
                :class="['calendar-pill', item.tone]"
                @click.stop="selectItem(item, day)"
              >
                {{ item.title || "未命名事项" }}
              </span>
              <span v-if="dayItems(day).length > 3" class="more-pill">
                +{{ dayItems(day).length - 3 }}
              </span>
            </span>
          </button>
        </div>

        <div class="legend-row">
          <span v-for="item in legendItems" :key="item.label">
            <i :class="item.tone"></i>
            {{ item.label }}
          </span>
        </div>
      </section>

      <aside class="planner-side">
        <section class="side-panel today-panel">
          <div class="side-title">
            <h2>今日行程</h2>
            <time>{{ sideDateLabel }}</time>
          </div>
          <div v-if="selectedDayItems.length" class="timeline-list">
            <button
              v-for="item in selectedDayItems"
              :key="`side-${item.key}`"
              :class="['timeline-item', { active: selectedItem?.key === item.key }]"
              type="button"
              @click="selectItem(item, selectedDate)"
            >
              <time>{{ timeText(item) }}</time>
              <i :class="item.tone"></i>
              <span>
                <strong>{{ item.title || "未命名事项" }}</strong>
                <small>{{ item.location || "行程" }}</small>
              </span>
            </button>
          </div>
          <p v-else class="empty-state">这一天还没有安排。</p>
          <button class="text-action" type="button" @click="openCreate(selectedDate)">+ 添加行程</button>
        </section>

        <section class="side-panel detail-panel">
          <div class="side-title">
            <h2>行程详情</h2>
            <time>{{ sideDateLabel }}</time>
          </div>

          <template v-if="selectedItem">
            <article class="detail-card">
              <span :class="['detail-dot', selectedItem.tone]"></span>
              <div>
                <h3>{{ selectedItem.title || "未命名事项" }}</h3>
                <p>{{ timeText(selectedItem) }} <template v-if="selectedItem.location"> · {{ selectedItem.location }}</template></p>
                <small>{{ selectedItem.content || "暂无行程说明。" }}</small>
              </div>
            </article>

            <div class="detail-meta">
              <span>行程</span>
              <span v-if="selectedItem.id">ID {{ selectedItem.id }}</span>
            </div>

            <div class="detail-actions">
              <button class="secondary-action" type="button" @click="openEdit(selectedItem)">编辑</button>
              <button class="danger-action" type="button" @click="deleteSchedule(selectedItem)">删除</button>
            </div>
          </template>
          <p v-else class="empty-state">选择一个日期或事项查看详情。</p>
        </section>
      </aside>
    </div>

    <div v-if="editorOpen" class="modal-backdrop" @click.self="closeEditor">
      <form class="schedule-modal" @submit.prevent="saveSchedule">
        <div class="modal-head">
          <h2>{{ form.id ? "编辑行程" : "新建行程" }}</h2>
          <button class="close-button" type="button" title="关闭" @click="closeEditor">×</button>
        </div>
        <label>
          标题
          <input v-model.trim="form.title" placeholder="例如：产品评审" />
        </label>
        <label>
          地点
          <input v-model.trim="form.location" placeholder="例如：会议室 A / 线上" />
        </label>
        <label>
          内容
          <textarea v-model.trim="form.content" placeholder="补充这次行程的目标、说明或准备事项。"></textarea>
        </label>
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
          <button class="secondary-action" type="button" @click="closeEditor">取消</button>
          <button class="primary-action compact" :disabled="loading" type="submit">{{ form.id ? "保存修改" : "创建行程" }}</button>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { scheduleApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const weekdayNames = ["一", "二", "三", "四", "五", "六", "日"];
const legendItems = [
  { label: "工作", tone: "work" },
  { label: "项目", tone: "project" },
  { label: "学习", tone: "study" },
  { label: "生活", tone: "life" },
  { label: "出差", tone: "travel" },
  { label: "其他", tone: "other" }
];

const schedules = ref([]);
const selectedDate = ref(new Date());
const cursorDate = ref(new Date());
const selectedKey = ref("");
const keyword = ref("");
const editorOpen = ref(false);
const loadingData = ref(false);
const todayDate = new Date();
const { loading, status, ok, run } = useRequest();

const form = reactive({
  id: null,
  title: "",
  content: "",
  location: "",
  startTime: "",
  finishTime: ""
});

const visibleDays = computed(() => {
  const start = startOfCalendarMonth(cursorDate.value);
  return Array.from({ length: 42 }, (_, index) => addDays(start, index));
});

const rangeTitle = computed(() => {
  return `${cursorDate.value.getFullYear()}年${cursorDate.value.getMonth() + 1}月`;
});

const monthInputValue = computed(() => {
  return `${cursorDate.value.getFullYear()}-${pad(cursorDate.value.getMonth() + 1)}`;
});

const allItems = computed(() => {
  return schedules.value.map((item, index) => normalizeItem(item, index)).sort((a, b) => a.startDate - b.startDate);
});

const filteredItems = computed(() => {
  const term = keyword.value.toLowerCase();
  if (!term) return allItems.value;
  return allItems.value.filter((item) => [item.title, item.content, item.location].some((value) => String(value || "").toLowerCase().includes(term)));
});

const selectedDayItems = computed(() => dayItems(selectedDate.value));

const selectedItem = computed(() => {
  return filteredItems.value.find((item) => item.key === selectedKey.value) || selectedDayItems.value[0] || null;
});

const sideDateLabel = computed(() => `${selectedDate.value.getMonth() + 1}月${selectedDate.value.getDate()}日 ${weekName(selectedDate.value)}`);

function pad(value) {
  return String(value).padStart(2, "0");
}

function parseDate(value) {
  if (!value) return null;
  const date = new Date(String(value).replace(" ", "T"));
  return Number.isNaN(date.getTime()) ? null : date;
}

function dateValue(date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

function dateTimeValue(date) {
  return `${dateValue(date)}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function startOfDay(date) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate());
}

function endOfDay(date) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate() + 1);
}

function startOfWeek(date) {
  const base = startOfDay(date);
  const day = base.getDay() || 7;
  base.setDate(base.getDate() - day + 1);
  return base;
}

function startOfCalendarMonth(date) {
  return startOfWeek(new Date(date.getFullYear(), date.getMonth(), 1));
}

function addDays(date, count) {
  const next = new Date(date);
  next.setDate(next.getDate() + count);
  return next;
}

function dateKey(date) {
  return dateValue(date);
}

function isSameDay(a, b) {
  return dateKey(a) === dateKey(b);
}

function weekName(date) {
  return ["周日", "周一", "周二", "周三", "周四", "周五", "周六"][date.getDay()];
}

function secondaryDateLabel(day) {
  if (isSameDay(day, todayDate)) return "今天";
  if (!isCurrentPeriod(day)) return `${day.getMonth() + 1}月`;
  return weekName(day).replace("周", "");
}

function isCurrentPeriod(day) {
  return day.getMonth() === cursorDate.value.getMonth();
}

function categoryTone(item) {
  const text = `${item.title || ""} ${item.content || ""} ${item.location || ""}`;
  if (/出差|外出|航班|车站|机场|酒店/.test(text)) return "travel";
  if (/复习|学习|课程|考试|读书/.test(text)) return "study";
  if (/项目|答辩|评审|会议|产品|汇报/.test(text)) return "project";
  if (/休息|家庭|健身|运动|吃饭|生活/.test(text)) return "life";
  if (/值班|工作|整理|接回|任务/.test(text)) return "work";
  return "other";
}

function normalizeItem(item, index) {
  const start = parseDate(item.startTime) || parseDate(item.finishTime) || new Date();
  const finish = parseDate(item.finishTime) || start;
  return {
    ...item,
    key: `schedule-${item.id || index}`,
    startDate: start,
    finishDate: finish,
    tone: categoryTone(item),
    title: item.title || "行程",
    location: item.location || ""
  };
}

function itemOverlapsDay(item, day) {
  const start = startOfDay(day);
  const end = endOfDay(day);
  return item.startDate < end && item.finishDate >= start;
}

function dayItems(day) {
  return filteredItems.value.filter((item) => itemOverlapsDay(item, day));
}

function selectDay(day) {
  selectedDate.value = startOfDay(day);
  const first = dayItems(day)[0];
  selectedKey.value = first?.key || "";
}

function selectItem(item, day) {
  selectedDate.value = startOfDay(day || item.startDate);
  selectedKey.value = item.key;
}

function timeText(item) {
  const start = item.startDate;
  const finish = item.finishDate;
  if (!start) return "未安排";
  if (!finish || start.getTime() === finish.getTime()) return `${pad(start.getHours())}:${pad(start.getMinutes())}`;
  return `${pad(start.getHours())}:${pad(start.getMinutes())} - ${pad(finish.getHours())}:${pad(finish.getMinutes())}`;
}

function movePeriod(direction) {
  const next = new Date(cursorDate.value);
  next.setMonth(next.getMonth() + direction);
  cursorDate.value = next;
  selectedDate.value = startOfDay(next);
}

function goToday() {
  const now = new Date();
  cursorDate.value = now;
  selectedDate.value = startOfDay(now);
}

function jumpToMonth(value) {
  const match = /^(\d{4})-(\d{2})$/.exec(value || "");
  if (!match) return;
  const next = new Date(Number(match[1]), Number(match[2]) - 1, 1);
  cursorDate.value = next;
  selectedDate.value = startOfDay(next);
  selectedKey.value = "";
}

function resetForm(date = selectedDate.value) {
  const start = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 9, 0);
  const finish = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 10, 0);
  Object.assign(form, {
    id: null,
    title: "",
    content: "",
    location: "",
    startTime: dateTimeValue(start),
    finishTime: dateTimeValue(finish)
  });
}

function openCreate(date) {
  resetForm(date);
  status.value = "";
  editorOpen.value = true;
}

function openEdit(item) {
  Object.assign(form, {
    id: item.id,
    title: item.title || "",
    content: item.content || "",
    location: item.location || "",
    startTime: dateTimeValue(item.startDate),
    finishTime: dateTimeValue(item.finishDate)
  });
  status.value = "";
  editorOpen.value = true;
}

function closeEditor() {
  editorOpen.value = false;
}

function validateForm() {
  if (!form.title) {
    status.value = "请填写行程标题";
    ok.value = false;
    return false;
  }
  if (!form.startTime || !form.finishTime) {
    status.value = "请填写开始和结束时间";
    ok.value = false;
    return false;
  }
  if (parseDate(form.finishTime) <= parseDate(form.startTime)) {
    status.value = "结束时间必须晚于开始时间";
    ok.value = false;
    return false;
  }
  return true;
}

async function saveSchedule() {
  if (!validateForm()) return;
  const action = form.id ? () => scheduleApi.update({ ...form }) : () => scheduleApi.create({ ...form });
  const data = await run(action);
  if (data?.code === 200) {
    await loadData();
    editorOpen.value = false;
    status.value = form.id ? "行程已更新" : "行程已创建";
    ok.value = true;
  }
}

async function deleteSchedule(item) {
  if (!item?.id) return;
  const data = await run(() => scheduleApi.remove(item.id));
  if (data?.code === 200) {
    selectedKey.value = "";
    await loadData();
    status.value = "行程已删除";
    ok.value = true;
  }
}

async function loadData() {
  loadingData.value = true;
  try {
    const data = await scheduleApi.list();
    schedules.value = Array.isArray(data?.data) ? data.data : [];
  } finally {
    loadingData.value = false;
  }
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.schedule-planner {
  min-height: 100vh;
  padding: 28px;
  background: #f7faff;
}

.planner-head {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-start;
  padding-bottom: 20px;
  border-bottom: 1px solid #dfe7f3;
}

.planner-head h1 {
  color: #121b33;
  font-size: 28px;
}

.planner-head p {
  margin-top: 6px;
  color: #64718a;
  font-weight: 700;
}

.primary-action,
.secondary-action,
.danger-action,
.today-button,
.icon-button,
.text-action,
.close-button {
  border-radius: 7px;
  font-weight: 900;
}

.primary-action {
  display: inline-flex;
  gap: 8px;
  align-items: center;
  border-color: #2563eb;
  background: #2563eb;
  box-shadow: 0 10px 22px rgba(37, 99, 235, .18);
}

.primary-action.compact {
  min-width: 104px;
  justify-content: center;
}

.planner-toolbar {
  display: grid;
  grid-template-columns: minmax(330px, auto) minmax(260px, 410px);
  gap: 16px;
  align-items: center;
  justify-content: space-between;
  margin-top: 22px;
}

.period-controls {
  display: flex;
  gap: 10px;
  align-items: center;
}

.period-controls strong {
  min-width: 150px;
  color: #172033;
  font-size: 18px;
}

.month-picker {
  width: 138px;
}

.month-picker input {
  min-height: 40px;
  border-color: #d8e1ee;
  color: #314158;
  background: #fff;
  font-weight: 900;
}

.icon-button,
.today-button,
.secondary-action,
.text-action,
.close-button {
  border-color: #d8e1ee;
  color: #314158;
  background: #fff;
}

.icon-button {
  display: grid;
  place-items: center;
  width: 42px;
  padding: 0;
}

.icon-button svg,
.planner-search svg {
  width: 20px;
  height: 20px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.planner-search {
  position: relative;
  display: block;
  justify-self: end;
  width: min(410px, 100%);
}

.planner-search input {
  height: 42px;
  padding-left: 42px;
  border-color: #dfe7f3;
}

.planner-search svg {
  position: absolute;
  left: 14px;
  top: 50%;
  color: #64718a;
  transform: translateY(-50%);
}

.planner-layout {
  display: grid;
  grid-template-columns: minmax(680px, 1fr) 360px;
  gap: 18px;
  align-items: start;
  margin-top: 18px;
}

.month-card,
.side-panel,
.schedule-modal {
  border: 1px solid #dfe7f3;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 14px 30px rgba(55, 80, 125, .05);
}

.month-card {
  min-width: 0;
  overflow: hidden;
}

.weekday-row,
.month-grid {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
}

.weekday-row {
  min-width: 720px;
  border-bottom: 1px solid #e7edf6;
  background: #fbfdff;
}

.weekday-row span {
  display: grid;
  place-items: center;
  min-height: 44px;
  color: #64718a;
  font-weight: 900;
}

.month-grid {
  min-width: 720px;
  max-height: calc(100vh - 306px);
  overflow: auto;
}

.day-cell {
  display: grid;
  align-content: start;
  gap: 10px;
  min-height: 124px;
  border: 0;
  border-right: 1px solid #e7edf6;
  border-bottom: 1px solid #e7edf6;
  border-radius: 0;
  padding: 14px;
  color: #172033;
  background: #fff;
  text-align: left;
}

.day-cell:hover {
  background: #f9fbff;
}

.day-cell.outside {
  color: #a6afbf;
  background: #fbfcfe;
}

.day-cell.selected {
  position: relative;
  box-shadow: inset 0 0 0 2px #2563eb;
}

.date-line {
  display: flex;
  gap: 8px;
  align-items: center;
}

.date-line strong {
  font-size: 17px;
}

.date-line small {
  color: #7a879b;
  font-weight: 800;
}

.day-cell.today .date-line strong {
  display: grid;
  place-items: center;
  width: 30px;
  height: 30px;
  border-radius: 999px;
  color: #fff;
  background: #2563eb;
}

.pill-stack {
  display: grid;
  gap: 7px;
  min-width: 0;
}

.calendar-pill,
.more-pill {
  display: block;
  min-width: 0;
  min-height: 24px;
  border-radius: 5px;
  padding: 3px 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  font-weight: 900;
}

.calendar-pill.work,
.detail-dot.work,
.timeline-item i.work,
.legend-row i.work { background: #dbeafe; color: #1d4ed8; }
.calendar-pill.project,
.detail-dot.project,
.timeline-item i.project,
.legend-row i.project { background: #cffafe; color: #0e7490; }
.calendar-pill.study,
.detail-dot.study,
.timeline-item i.study,
.legend-row i.study { background: #eadcff; color: #7e22ce; }
.calendar-pill.life,
.detail-dot.life,
.timeline-item i.life,
.legend-row i.life { background: #dcfce7; color: #15803d; }
.calendar-pill.travel,
.detail-dot.travel,
.timeline-item i.travel,
.legend-row i.travel { background: #ffedd5; color: #ea580c; }
.calendar-pill.other,
.detail-dot.other,
.timeline-item i.other,
.legend-row i.other { background: #f1f5f9; color: #64748b; }

.more-pill {
  color: #52627a;
  background: transparent;
}

.legend-row {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 26px;
  padding: 14px;
  color: #58677d;
  font-weight: 800;
}

.legend-row span {
  display: inline-flex;
  gap: 7px;
  align-items: center;
}

.legend-row i {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.calendar-loading {
  display: grid;
  place-items: center;
  min-height: 360px;
  color: #64718a;
  font-weight: 900;
}

.planner-side {
  display: grid;
  gap: 16px;
}

.side-panel {
  display: grid;
  gap: 16px;
  max-height: calc((100vh - 234px) / 2);
  min-height: 0;
  padding: 18px;
  overflow: hidden;
}

.side-title {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: center;
}

.side-title h2 {
  color: #172033;
}

.side-title time {
  color: #2563eb;
  font-weight: 900;
}

.timeline-list {
  display: grid;
  align-content: start;
  min-height: 0;
  overflow: auto;
  padding-right: 4px;
}

.timeline-item {
  display: grid;
  grid-template-columns: 54px 12px minmax(0, 1fr);
  gap: 10px;
  align-items: start;
  min-height: 56px;
  border: 0;
  border-bottom: 1px solid #edf2f8;
  border-radius: 0;
  padding: 9px 0;
  color: #314158;
  background: transparent;
  text-align: left;
}

.timeline-item:hover,
.timeline-item.active {
  color: #2563eb;
  background: transparent;
}

.timeline-item time {
  color: #314158;
  font-weight: 900;
}

.timeline-item i,
.detail-dot {
  width: 9px;
  height: 9px;
  margin-top: 7px;
  border-radius: 50%;
}

.timeline-item strong,
.timeline-item small {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.timeline-item small {
  color: #7a879b;
  font-weight: 800;
}

.text-action {
  justify-self: center;
  border: 0;
  color: #2563eb;
  background: transparent;
}

.detail-card {
  display: grid;
  grid-template-columns: 12px minmax(0, 1fr);
  gap: 12px;
  border-bottom: 1px solid #edf2f8;
  padding-bottom: 14px;
}

.detail-card h3,
.detail-card p,
.detail-card small {
  overflow-wrap: anywhere;
}

.detail-card h3 {
  color: #172033;
  font-size: 16px;
}

.detail-card p {
  margin-top: 5px;
  color: #52627a;
  font-weight: 800;
}

.detail-card small {
  display: block;
  margin-top: 7px;
  color: #64718a;
  font-weight: 700;
}

.detail-meta,
.detail-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.detail-meta span {
  border-radius: 999px;
  padding: 4px 10px;
  color: #52627a;
  background: #f1f5f9;
  font-weight: 800;
}

.danger-action {
  border-color: #fecaca;
  color: #dc2626;
  background: #fff5f5;
}

.readonly-tip,
.empty-state {
  border: 1px dashed #d8e1ee;
  border-radius: 8px;
  padding: 16px;
  color: #64718a;
  background: #fbfdff;
  text-align: center;
  font-weight: 800;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 40;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(15, 23, 42, .28);
}

.schedule-modal {
  display: grid;
  gap: 14px;
  width: min(560px, 100%);
  max-height: calc(100vh - 40px);
  overflow: auto;
  padding: 20px;
}

.modal-head {
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

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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
  .planner-toolbar,
  .planner-layout {
    grid-template-columns: 1fr;
  }

  .planner-search {
    justify-self: stretch;
    width: 100%;
  }

  .planner-side {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .side-panel {
    max-height: 360px;
  }
}

@media (max-width: 760px) {
  .schedule-planner {
    padding: 16px 12px;
  }

  .planner-head {
    flex-direction: column;
  }

  .primary-action {
    width: 100%;
    justify-content: center;
  }

  .period-controls {
    flex-wrap: wrap;
  }

  .period-controls strong {
    width: 100%;
  }

  .month-picker {
    width: calc(100% - 52px);
  }

  .planner-side {
    grid-template-columns: 1fr;
  }

  .month-card {
    overflow: auto;
  }

  .modal-actions {
    flex-direction: column-reverse;
  }
}
</style>
