<template>
  <section class="calendar-page">
    <header class="calendar-hero">
      <div>
        <h1>日历</h1>
        <p>统一查看任务和行程的时间安排</p>
      </div>
    </header>

    <section class="calendar-toolbar">
      <div class="date-switcher">
        <button class="icon-button" type="button" title="上一段" @click="moveRange(-1)">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m15 18-6-6 6-6" /></svg>
        </button>
        <button class="range-button" type="button" @click="goToday">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 2v4M16 2v4M3.5 9.5h17M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z" /></svg>
          {{ rangeLabel }}
        </button>
        <button class="icon-button" type="button" title="下一段" @click="moveRange(1)">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m9 18 6-6-6-6" /></svg>
        </button>
        <button class="today-button" type="button" @click="goToday">今天</button>
      </div>

      <label class="search-box">
        <span class="sr-only">搜索日历事项</span>
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m21 21-4.3-4.3M10.8 18a7.2 7.2 0 1 1 0-14.4 7.2 7.2 0 0 1 0 14.4Z" /></svg>
        <input v-model.trim="keyword" placeholder="搜索任务、行程、地点或内容" />
      </label>

      <div class="mode-tabs" aria-label="视图切换">
        <button v-for="item in modes" :key="item.value" :class="{ active: mode === item.value }" type="button" @click="mode = item.value">
          {{ item.label }}
        </button>
      </div>

      <div v-if="mode === 'custom'" class="custom-range">
        <label>
          开始
          <input v-model="customStart" type="date" />
        </label>
        <label>
          结束
          <input v-model="customEnd" type="date" />
        </label>
      </div>
    </section>

    <div class="calendar-layout">
      <section class="calendar-panel">
        <template v-if="mode === 'year'">
          <div class="year-grid">
            <article v-for="month in yearMonths" :key="month.key" class="year-month">
              <div class="month-head">
                <h2>{{ month.label }}</h2>
                <span>{{ month.count }} 项</span>
              </div>
              <div class="mini-weekdays">
                <span v-for="day in miniWeekdays" :key="`${month.key}-${day}`">{{ day }}</span>
              </div>
              <div class="mini-days">
                <button
                  v-for="day in month.days"
                  :key="day.key"
                  :class="['mini-day', day.status, { muted: !day.inMonth, today: isSameDay(day.date, todayDate), selected: isSameDay(day.date, selectedDate) }]"
                  type="button"
                  @click="selectYearDay(day)"
                >
                  {{ day.date.getDate() }}
                </button>
              </div>
            </article>
          </div>
        </template>
        <template v-else>
          <div class="day-head" :style="gridStyle">
            <span></span>
            <button
              v-for="day in visibleDays"
              :key="dateKey(day)"
              :class="['day-label', { today: isSameDay(day, todayDate), selected: isSameDay(day, selectedDate) }]"
              type="button"
              @click="selectedDate = new Date(day)"
            >
              <strong>{{ weekName(day) }}</strong>
              <span>{{ shortDate(day) }}</span>
            </button>
          </div>

          <div class="time-grid-wrap">
            <div class="time-grid" :style="gridStyle">
              <div class="time-column">
                <span v-for="hour in hours" :key="hour">{{ pad(hour) }}:00</span>
              </div>
              <div
                v-for="day in visibleDays"
                :key="`col-${dateKey(day)}`"
                :class="['day-column', { selected: isSameDay(day, selectedDate) }]"
              >
                <span v-for="hour in hours" :key="`${dateKey(day)}-${hour}`" class="hour-line"></span>
              </div>

              <button
                v-for="item in positionedItems"
                :key="item.key"
                :class="['calendar-block', item.itemType, item.tone, { active: selectedItem?.key === item.key }]"
                :style="blockStyle(item)"
                type="button"
                @click="selectItem(item)"
              >
                <strong>{{ formatTimeRange(item.startTime, item.finishTime) }}</strong>
                <span>{{ item.title || '未命名事项' }}</span>
                <small>{{ item.itemType === 'schedule' ? (item.location || '行程') : '任务' }}</small>
              </button>
            </div>
          </div>
        </template>
      </section>

      <aside class="calendar-side">
        <section class="side-card">
          <div class="side-head">
            <h2>今日事项</h2>
            <span>{{ todayItems.length }} 项</span>
          </div>
          <div v-if="todayItems.length" class="today-list">
            <button v-for="item in todayItems" :key="`today-${item.key}`" class="today-item" type="button" @click="selectItem(item)">
              <i :class="[item.itemType, item.tone]"></i>
              <time>{{ formatTimeRange(item.startTime, item.finishTime) }}</time>
              <strong>{{ item.title || "未命名事项" }}</strong>
              <small>{{ item.itemType === "schedule" ? (item.location || "行程") : "任务" }}</small>
            </button>
          </div>
          <p v-else class="empty-text">今天没有任务或行程。</p>
        </section>

        <section class="side-card detail-card">
          <div class="side-head">
            <h2>事项详情</h2>
            <span v-if="selectedItem">{{ selectedItem.itemType === "schedule" ? "行程" : "任务" }}</span>
          </div>
          <template v-if="selectedItem">
            <h3>{{ selectedItem.title || "未命名事项" }}</h3>
            <dl class="detail-list">
              <div>
                <dt>时间</dt>
                <dd>{{ detailDate(selectedItem) }} {{ formatTimeRange(selectedItem.startTime, selectedItem.finishTime) }}</dd>
              </div>
              <div v-if="selectedItem.location">
                <dt>地点</dt>
                <dd>{{ selectedItem.location }}</dd>
              </div>
              <div>
                <dt>内容</dt>
                <dd>{{ selectedItem.content || "暂无说明" }}</dd>
              </div>
            </dl>
          </template>
          <p v-else class="empty-text">选择一个事项查看详情。</p>
        </section>
      </aside>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { calendarApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const items = ref([]);
const selectedKey = ref("");
const selectedDate = ref(new Date());
const todayDate = new Date();
const keyword = ref("");
const mode = ref("week");
const customStart = ref(dateKey(startOfWeek(new Date())));
const customEnd = ref(dateKey(addDays(startOfWeek(new Date()), 6)));
const { run } = useRequest();

const modes = [
  { label: "日", value: "day" },
  { label: "周", value: "week" },
  { label: "月", value: "month" },
  { label: "年", value: "year" },
  { label: "自定义", value: "custom" }
];
const miniWeekdays = ["日", "一", "二", "三", "四", "五", "六"];
const hours = Array.from({ length: 13 }, (_, index) => index + 8);
const tones = ["blue", "green", "purple", "amber"];

const visibleDays = computed(() => {
  if (mode.value === "day") return [startOfDay(selectedDate.value)];
  if (mode.value === "month") return daysOfMonth(selectedDate.value);
  if (mode.value === "year") return daysOfYear(selectedDate.value);
  if (mode.value === "custom") return customDays.value;
  const start = startOfWeek(selectedDate.value);
  return Array.from({ length: 7 }, (_, index) => addDays(start, index));
});

const customDays = computed(() => {
  const start = parseDate(customStart.value);
  const end = parseDate(customEnd.value);
  const first = start <= end ? start : end;
  const last = start <= end ? end : start;
  return daysBetween(first, last);
});

const gridStyle = computed(() => ({
  "--day-count": visibleDays.value.length,
  "--day-width": mode.value === "month" ? "112px" : "minmax(112px, 1fr)",
  "--grid-min-width": `${72 + visibleDays.value.length * 112}px`
}));

const rangeLabel = computed(() => {
  const days = visibleDays.value;
  if (!days.length) return "";
  if (mode.value === "day") return fullDate(days[0]);
  if (mode.value === "year") return `${selectedDate.value.getFullYear()}年`;
  if (mode.value === "custom") return `${fullDate(days[0])} - ${fullDate(days[days.length - 1])}`;
  const first = days[0];
  const last = days[days.length - 1];
  if (mode.value === "month") return `${first.getFullYear()}年${first.getMonth() + 1}月`;
  return `${first.getFullYear()}年${first.getMonth() + 1}月${first.getDate()}日 - ${last.getMonth() + 1}月${last.getDate()}日`;
});

const yearMonths = computed(() => {
  const year = selectedDate.value.getFullYear();
  return Array.from({ length: 12 }, (_, month) => {
    const first = new Date(year, month, 1);
    const days = monthCalendarDays(first);
    return {
      key: `${year}-${month + 1}`,
      label: `${month + 1}月`,
      days,
      count: days.filter((day) => day.inMonth).reduce((sum, day) => sum + itemsOnDay(day.date).length, 0)
    };
  });
});

const normalizedItems = computed(() => {
  return items.value.map((item, index) => ({
    ...item,
    key: `${item.itemType || "item"}-${item.id || index}`,
    tone: tones[index % tones.length]
  }));
});

const filteredItems = computed(() => {
  const term = keyword.value.toLowerCase();
  return normalizedItems.value
    .filter((item) => {
      if (!term) return true;
      return [item.title, item.location, item.content, item.itemType].some((value) => String(value || "").toLowerCase().includes(term));
    })
    .sort((a, b) => parseDate(a.startTime) - parseDate(b.startTime));
});

const positionedItems = computed(() => (mode.value === "year" ? [] : layoutOverlappingItems(filteredItems.value)));

const todayItems = computed(() => {
  const start = startOfDay(todayDate);
  const end = addDays(start, 1);
  return normalizedItems.value
    .filter((item) => itemOverlaps(item, start, end))
    .sort((a, b) => parseDate(a.startTime) - parseDate(b.startTime));
});

const selectedItem = computed(() => positionedItems.value.find((item) => item.key === selectedKey.value) || filteredItems.value.find((item) => item.key === selectedKey.value) || todayItems.value.find((item) => item.key === selectedKey.value) || positionedItems.value[0] || todayItems.value[0] || null);

watch([selectedDate, mode, customStart, customEnd], () => loadCalendar());

function pad(value) {
  return String(value).padStart(2, "0");
}

function parseDate(value) {
  if (!value) return new Date(0);
  const text = String(value);
  if (/^\d{4}-\d{2}-\d{2}$/.test(text)) {
    const [year, month, day] = text.split("-").map(Number);
    return new Date(year, month - 1, day);
  }
  return new Date(text.replace(" ", "T"));
}

function startOfDay(date) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate());
}

function startOfWeek(date) {
  const base = startOfDay(date);
  const day = base.getDay() || 7;
  base.setDate(base.getDate() - day + 1);
  return base;
}

function daysOfMonth(date) {
  const first = new Date(date.getFullYear(), date.getMonth(), 1);
  const total = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
  return Array.from({ length: total }, (_, index) => addDays(first, index));
}

function daysOfYear(date) {
  const first = new Date(date.getFullYear(), 0, 1);
  const last = new Date(date.getFullYear(), 11, 31);
  return daysBetween(first, last);
}

function daysBetween(start, end) {
  const total = Math.max(1, Math.round((startOfDay(end) - startOfDay(start)) / 86400000) + 1);
  return Array.from({ length: total }, (_, index) => addDays(startOfDay(start), index));
}

function monthCalendarDays(date) {
  const first = new Date(date.getFullYear(), date.getMonth(), 1);
  const gridStart = addDays(first, -first.getDay());
  return Array.from({ length: 42 }, (_, index) => {
    const day = addDays(gridStart, index);
    const dayItems = itemsOnDay(day);
    return {
      date: day,
      key: dateKey(day),
      inMonth: day.getMonth() === date.getMonth(),
      status: dayItems.length ? "has-items" : "",
      count: dayItems.length
    };
  });
}

function addDays(date, count) {
  const next = new Date(date);
  next.setDate(next.getDate() + count);
  return next;
}

function dateKey(date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

function isSameDay(a, b) {
  return dateKey(a) === dateKey(b);
}

function weekName(date) {
  return ["周日", "周一", "周二", "周三", "周四", "周五", "周六"][date.getDay()];
}

function shortDate(date) {
  return `${date.getMonth() + 1}/${date.getDate()}`;
}

function fullDate(date) {
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
}

function dateTimeValue(date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function formatClock(value) {
  const date = parseDate(value);
  if (Number.isNaN(date.getTime())) return "";
  return `${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function formatTimeRange(start, finish) {
  if (!start && !finish) return "未安排时间";
  if (!finish) return `${formatClock(start)} 开始`;
  return `${formatClock(start)} - ${formatClock(finish)}`;
}

function detailDate(item) {
  return fullDate(parseDate(item.startTime || item.finishTime));
}

function itemOverlaps(item, start, end) {
  const itemStart = parseDate(item.startTime || item.finishTime);
  const itemEnd = parseDate(item.finishTime || item.startTime);
  return itemStart < end && itemEnd > start;
}

function itemsOnDay(day) {
  const start = startOfDay(day);
  const end = addDays(start, 1);
  return filteredItems.value.filter((item) => itemOverlaps(item, start, end));
}

function dayIndex(item) {
  const start = parseDate(item.startTime || item.finishTime);
  return visibleDays.value.findIndex((day) => isSameDay(day, start));
}

function layoutOverlappingItems(list) {
  const byDay = new Map();
  list.forEach((item) => {
    const indexOfDay = dayIndex(item);
    if (indexOfDay < 0) return;
    const normalized = {
      ...item,
      dayIndex: indexOfDay,
      laneIndex: 0,
      laneCount: 1,
      startDate: parseDate(item.startTime || item.finishTime),
      endDate: parseDate(item.finishTime || item.startTime)
    };
    if (!byDay.has(indexOfDay)) byDay.set(indexOfDay, []);
    byDay.get(indexOfDay).push(normalized);
  });

  const result = [];
  byDay.forEach((dayItems) => {
    const sorted = dayItems.sort((a, b) => a.startDate - b.startDate || a.endDate - b.endDate);
    let group = [];
    let groupEnd = null;

    const flushGroup = () => {
      if (!group.length) return;
      result.push(...assignOverlapLanes(group));
      group = [];
      groupEnd = null;
    };

    sorted.forEach((item) => {
      if (!group.length || item.startDate < groupEnd) {
        group.push(item);
        groupEnd = !groupEnd || item.endDate > groupEnd ? item.endDate : groupEnd;
        return;
      }
      flushGroup();
      group.push(item);
      groupEnd = item.endDate;
    });

    flushGroup();
  });

  return result;
}

function assignOverlapLanes(group) {
  const laneEnds = [];
  let maxLaneCount = 1;
  const assigned = group.map((item) => {
    const freeLane = laneEnds.findIndex((end) => end <= item.startDate);
    const laneIndex = freeLane >= 0 ? freeLane : laneEnds.length;
    laneEnds[laneIndex] = item.endDate;
    maxLaneCount = Math.max(maxLaneCount, laneEnds.length);
    return { ...item, laneIndex };
  });
  return assigned.map((item) => ({ ...item, laneCount: maxLaneCount }));
}

function blockStyle(item) {
  const start = parseDate(item.startTime || item.finishTime);
  const finish = parseDate(item.finishTime || item.startTime);
  const minHour = hours[0];
  const maxHour = hours[hours.length - 1] + 1;
  const startMinutes = Math.max(0, (start.getHours() + start.getMinutes() / 60 - minHour) * 64);
  const rawDuration = (finish - start) / 60000 / 60 * 64;
  const blockInset = 4;
  const durationMinutes = Math.max(34, Math.min((maxHour - minHour) * 64 - startMinutes, rawDuration) - blockInset * 2);
  const laneCount = Math.max(1, item.laneCount || 1);
  const laneIndex = Math.min(item.laneIndex || 0, laneCount - 1);
  const gap = 4;
  return {
    gridColumn: `${item.dayIndex + 2} / span 1`,
    top: `${startMinutes + blockInset}px`,
    height: `${durationMinutes}px`,
    left: `calc(8px + ${laneIndex} * ((100% - 16px - ${(laneCount - 1) * gap}px) / ${laneCount} + ${gap}px))`,
    width: `calc((100% - 16px - ${(laneCount - 1) * gap}px) / ${laneCount})`
  };
}

function selectItem(item) {
  selectedKey.value = item.key;
  selectedDate.value = parseDate(item.startTime || item.finishTime);
}

function selectYearDay(day) {
  selectedDate.value = new Date(day.date);
  const dayItems = itemsOnDay(day.date);
  selectedKey.value = dayItems[0]?.key || "";
}

function moveRange(direction) {
  const next = new Date(selectedDate.value);
  if (mode.value === "day") next.setDate(next.getDate() + direction);
  if (mode.value === "week") next.setDate(next.getDate() + direction * 7);
  if (mode.value === "month") next.setMonth(next.getMonth() + direction);
  if (mode.value === "year") next.setFullYear(next.getFullYear() + direction);
  if (mode.value === "custom") {
    const days = customDays.value.length || 1;
    const start = addDays(parseDate(customStart.value), direction * days);
    const end = addDays(parseDate(customEnd.value), direction * days);
    customStart.value = dateKey(start);
    customEnd.value = dateKey(end);
    selectedDate.value = start;
    return;
  }
  selectedDate.value = next;
}

function goToday() {
  selectedDate.value = new Date();
  if (mode.value === "custom") {
    const start = startOfWeek(new Date());
    customStart.value = dateKey(start);
    customEnd.value = dateKey(addDays(start, 6));
  }
}

async function loadCalendar() {
  const start = visibleDays.value[0] || startOfDay(selectedDate.value);
  const finish = addDays(visibleDays.value[visibleDays.value.length - 1] || start, 1);
  const data = await run(() => calendarApi.list({ startTime: dateTimeValue(start), finishTime: dateTimeValue(finish) }));
  items.value = Array.isArray(data?.data) ? data.data : [];
}

onMounted(() => loadCalendar());
</script>

<style scoped>
.calendar-page {
  min-height: 100vh;
  padding: 0 28px 24px;
  background: #f6f9fe;
}

.calendar-hero {
  position: sticky;
  top: 0;
  z-index: 5;
  min-height: 102px;
  margin: 0 -28px 20px;
  padding: 20px 28px;
  border-bottom: 1px solid #e6edf7;
  background: rgba(255, 255, 255, .92);
  backdrop-filter: blur(12px);
}

.calendar-hero h1 {
  color: #111a33;
  font-size: 26px;
}

.calendar-hero p {
  margin-top: 5px;
  color: #64718a;
  font-weight: 800;
}

.calendar-toolbar {
  display: grid;
  grid-template-columns: minmax(320px, auto) minmax(260px, 1fr) minmax(210px, auto);
  gap: 14px;
  align-items: center;
  border: 1px solid #e0e7f2;
  border-radius: 14px;
  padding: 16px;
  background: #fff;
  box-shadow: 0 12px 30px rgba(58, 82, 126, .05);
}

.date-switcher {
  display: flex;
  gap: 8px;
  align-items: center;
}

.icon-button,
.today-button,
.range-button,
.mode-tabs button {
  min-height: 40px;
  border: 1px solid #d9e3f0;
  border-radius: 7px;
  color: #465670;
  background: #fff;
  font-weight: 900;
}

.icon-button {
  display: grid;
  place-items: center;
  width: 38px;
  padding: 0;
}

.icon-button svg,
.range-button svg,
.search-box svg {
  width: 20px;
  height: 20px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.range-button {
  display: inline-flex;
  gap: 9px;
  align-items: center;
}

.search-box {
  position: relative;
  display: block;
}

.search-box input {
  height: 40px;
  padding-left: 42px;
  border-color: #d9e3f0;
}

.search-box svg {
  position: absolute;
  left: 13px;
  top: 50%;
  color: #64718a;
  transform: translateY(-50%);
}

.mode-tabs {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  border: 1px solid #e0e7f2;
  border-radius: 8px;
  padding: 3px;
  background: #f8fbff;
}

.mode-tabs button {
  min-width: 64px;
  border: 0;
  background: transparent;
}

.mode-tabs button.active {
  color: #2f6df6;
  background: #eaf1ff;
}

.custom-range {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: repeat(2, minmax(180px, 240px));
  gap: 12px;
  align-items: end;
  padding-top: 2px;
}

.custom-range label {
  color: #60708c;
  font-weight: 900;
}

.custom-range input {
  margin-top: 6px;
  height: 38px;
  border-color: #d9e3f0;
}

.calendar-layout {
  display: grid;
  grid-template-columns: minmax(620px, 1fr) 360px;
  gap: 18px;
  align-items: start;
  margin-top: 18px;
}

.calendar-panel,
.side-card {
  border: 1px solid #e0e7f2;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 14px 34px rgba(58, 82, 126, .05);
}

.calendar-panel {
  min-width: 0;
  max-height: calc(100vh - 300px);
  overflow: auto;
}

.year-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(220px, 1fr));
  gap: 14px;
  padding: 16px;
}

.year-month {
  border: 1px solid #e6edf7;
  border-radius: 12px;
  padding: 12px;
  background: #fbfdff;
}

.month-head {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}

.month-head h2 {
  color: #111a33;
  font-size: 16px;
}

.month-head span {
  border-radius: 999px;
  padding: 4px 9px;
  color: #2f6df6;
  background: #eaf1ff;
  font-size: 12px;
  font-weight: 900;
}

.mini-weekdays,
.mini-days {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 4px;
}

.mini-weekdays {
  margin-bottom: 6px;
  color: #71809a;
  font-size: 12px;
  font-weight: 900;
  text-align: center;
}

.mini-day {
  display: grid;
  place-items: center;
  min-height: 30px;
  border: 0;
  border-radius: 8px;
  padding: 0;
  color: #27344f;
  background: transparent;
  font-size: 12px;
  font-weight: 900;
}

.mini-day.muted {
  color: #b5bfce;
}

.mini-day.has-items {
  color: #1f5bea;
  background: #eaf1ff;
}

.mini-day.today {
  color: #fff;
  background: #2f6df6;
}

.mini-day.selected {
  box-shadow: 0 0 0 2px rgba(47, 109, 246, .25);
}

.day-head {
  position: sticky;
  top: 0;
  z-index: 4;
  display: grid;
  grid-template-columns: 72px repeat(var(--day-count), var(--day-width));
  min-width: max(100%, var(--grid-min-width));
  border-bottom: 1px solid #edf2f8;
  background: #fff;
}

.day-label {
  display: grid;
  justify-items: center;
  gap: 6px;
  min-height: 72px;
  border: 0;
  border-left: 1px solid #edf2f8;
  border-radius: 0;
  padding: 14px 4px;
  color: #27344f;
  background: #fff;
}

.day-label:hover,
.day-label.selected {
  color: #2f6df6;
  background: #f5f8ff;
}

.day-label.today span {
  display: grid;
  place-items: center;
  width: 38px;
  height: 32px;
  border-radius: 999px;
  color: #fff;
  background: #2f6df6;
}

.time-grid-wrap {
  overflow: visible;
}

.time-grid {
  position: relative;
  display: grid;
  grid-template-columns: 72px repeat(var(--day-count), var(--day-width));
  min-width: max(100%, var(--grid-min-width));
  min-height: 840px;
}

.time-column {
  display: grid;
  grid-template-rows: repeat(13, 64px);
  border-right: 1px solid #edf2f8;
  color: #60708c;
  background: #fff;
  font-weight: 800;
}

.time-column span {
  padding-top: 18px;
  text-align: center;
}

.day-column {
  display: grid;
  grid-template-rows: repeat(13, 64px);
  border-right: 1px solid #edf2f8;
  background: #fff;
}

.day-column.selected {
  background: #f7fbff;
}

.hour-line {
  border-bottom: 1px dashed #e2e8f0;
}

.calendar-block {
  position: absolute;
  z-index: 2;
  display: grid;
  align-content: start;
  gap: 2px;
  min-width: 0;
  overflow: hidden;
  border-radius: 8px;
  padding: 6px 8px;
  text-align: left;
  line-height: 1.18;
}

.calendar-block:hover,
.calendar-block.active {
  box-shadow: 0 0 0 2px rgba(47, 109, 246, .28);
}

.calendar-block strong,
.calendar-block span,
.calendar-block small {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.calendar-block strong {
  font-size: 11px;
}

.calendar-block span {
  font-size: 12px;
  font-weight: 900;
}

.calendar-block small {
  font-size: 11px;
}

.blue { border-color: #9dbbff; color: #1f5bea; background: #eaf1ff; }
.green { border-color: #a7e6c1; color: #138a55; background: #e9fbf1; }
.purple { border-color: #c7b8ff; color: #6847d9; background: #f0edff; }
.amber { border-color: #ffd98a; color: #d97706; background: #fff5db; }

.calendar-block.task {
  border-style: dashed;
}

.calendar-side {
  display: grid;
  gap: 16px;
}

.side-card {
  display: grid;
  gap: 14px;
  max-height: 318px;
  min-height: 0;
  padding: 18px;
}

.detail-card {
  max-height: calc(100vh - 454px);
}

.side-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.side-head h2 {
  color: #111a33;
  font-size: 18px;
}

.side-head span {
  color: #64718a;
  font-weight: 900;
}

.today-list {
  display: grid;
  align-content: start;
  min-height: 0;
  overflow: auto;
  padding-right: 4px;
}

.today-item {
  display: grid;
  grid-template-columns: 12px 54px minmax(0, 1fr);
  gap: 8px 12px;
  align-items: start;
  min-height: 58px;
  border: 0;
  border-bottom: 1px solid #edf2f8;
  border-radius: 0;
  padding: 9px 0;
  color: #465670;
  background: transparent;
  text-align: left;
}

.today-item:hover {
  color: #2f6df6;
  background: transparent;
}

.today-item i {
  width: 9px;
  height: 9px;
  margin-top: 6px;
  border-radius: 50%;
}

.today-item i.task {
  border-radius: 3px;
}

.today-item time {
  color: #60708c;
  font-weight: 800;
}

.today-item strong {
  color: #1f2937;
  overflow-wrap: anywhere;
}

.today-item small {
  grid-column: 3;
  color: #71809a;
  overflow-wrap: anywhere;
}

.detail-card h3 {
  color: #111a33;
  font-size: 17px;
  overflow-wrap: anywhere;
}

.detail-list {
  display: grid;
  gap: 12px;
  min-height: 0;
  overflow: auto;
  margin: 0;
}

.detail-list div {
  display: grid;
  grid-template-columns: 46px minmax(0, 1fr);
  gap: 12px;
}

.detail-list dt {
  color: #64718a;
  font-weight: 900;
}

.detail-list dd {
  margin: 0;
  color: #465670;
  overflow-wrap: anywhere;
  font-weight: 800;
}

.empty-text {
  border: 1px dashed #d9e3f0;
  border-radius: 10px;
  padding: 18px;
  color: #71809a;
  background: #fbfdff;
  text-align: center;
  font-weight: 800;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
}

@media (max-width: 1260px) {
  .calendar-toolbar,
  .calendar-layout {
    grid-template-columns: 1fr;
  }

  .year-grid {
    grid-template-columns: repeat(2, minmax(220px, 1fr));
  }

  .calendar-side {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .detail-card {
    max-height: 318px;
  }
}

@media (max-width: 760px) {
  .calendar-page {
    padding: 0 12px 16px;
  }

  .calendar-hero {
    position: static;
    margin: 0 -12px 14px;
    padding: 18px 12px;
  }

  .calendar-toolbar,
  .calendar-side,
  .custom-range,
  .year-grid {
    grid-template-columns: 1fr;
  }

  .date-switcher {
    flex-wrap: wrap;
  }

  .range-button {
    flex: 1;
  }

  .calendar-panel {
    max-height: 58vh;
  }
}
</style>
