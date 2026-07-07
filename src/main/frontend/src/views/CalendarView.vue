<template>
  <section class="two-column">
    <div class="form-card">
      <h2>日程视图</h2>
      <div class="form-row three">
        <label>视图类型
          <select v-model="mode" @change="syncPoint">
            <option value="day">日视图</option>
            <option value="week">周视图</option>
            <option value="month">月视图</option>
            <option value="year">年视图</option>
            <option value="custom">自定义</option>
          </select>
        </label>
        <label v-if="mode !== 'custom'">{{ pointLabel }}
          <input v-model="point" :type="pointType" min="1970" max="2099" />
        </label>
        <label>数量<input :value="items.length + ' 条'" readonly /></label>
      </div>
      <div v-if="mode === 'custom'" class="form-row">
        <label>开始时间<input v-model="customStart" type="datetime-local" /></label>
        <label>结束时间<input v-model="customFinish" type="datetime-local" /></label>
      </div>
      <div class="actions">
        <button :disabled="loading" type="button" @click="load">查询日程</button>
        <button class="secondary" type="button" @click="today">回到今天</button>
      </div>
      <p class="muted">{{ rangeText }}</p>
      <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>

      <div class="list">
        <article v-for="item in items" :key="item.itemType + '-' + item.id" class="schedule-item">
          <strong>{{ item.title || "未命名" }}</strong>
          <span class="muted">{{ formatDate(item.startTime) }} - {{ formatDate(item.finishTime) }}</span>
          <span class="tag">{{ item.itemType === "task" ? "任务" : "行程" }}</span>
        </article>
        <p v-if="!items.length" class="empty">暂无日程数据。</p>
      </div>
    </div>
    <ResultPanel :result="result" />
  </section>
</template>

<script setup>
import { computed, ref } from "vue";
import ResultPanel from "@/components/ResultPanel.vue";
import { calendarApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const { loading, status, ok, result, run } = useRequest();
const mode = ref("day");
const point = ref("");
const customStart = ref("");
const customFinish = ref("");
const items = ref([]);
const rangeText = ref("请选择日期查询。");

const pointType = computed(() => ({ day: "date", week: "week", month: "month", year: "number" }[mode.value] || "date"));
const pointLabel = computed(() => ({ day: "日期", week: "周", month: "月份", year: "年份" }[mode.value] || "日期"));

function pad(num) { return String(num).padStart(2, "0"); }
function dateValue(date) { return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`; }
function monthValue(date) { return `${date.getFullYear()}-${pad(date.getMonth() + 1)}`; }
function dateTimeValue(date) { return `${dateValue(date)}T${pad(date.getHours())}:${pad(date.getMinutes())}`; }
function addDays(date, days) { const next = new Date(date); next.setDate(next.getDate() + days); return next; }
function startOfDay(date) { return new Date(date.getFullYear(), date.getMonth(), date.getDate()); }
function formatDate(text) { return text ? String(text).replace("T", " ").slice(0, 16) : ""; }

function isoWeek(date) {
  const target = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  const day = target.getUTCDay() || 7;
  target.setUTCDate(target.getUTCDate() + 4 - day);
  const yearStart = new Date(Date.UTC(target.getUTCFullYear(), 0, 1));
  const week = Math.ceil((((target - yearStart) / 86400000) + 1) / 7);
  return `${target.getUTCFullYear()}-W${pad(week)}`;
}

function startOfWeek(value) {
  const match = /^(\d{4})-W(\d{2})$/.exec(value || "");
  if (!match) return null;
  const jan4 = new Date(Number(match[1]), 0, 4);
  const jan4Day = jan4.getDay() || 7;
  const week1Monday = addDays(startOfDay(jan4), 1 - jan4Day);
  return addDays(week1Monday, (Number(match[2]) - 1) * 7);
}

function syncPoint() {
  const now = new Date();
  if (mode.value === "day") point.value = dateValue(now);
  if (mode.value === "week") point.value = isoWeek(now);
  if (mode.value === "month") point.value = monthValue(now);
  if (mode.value === "year") point.value = String(now.getFullYear());
}

function getRange() {
  if (mode.value === "custom") return { startTime: customStart.value, finishTime: customFinish.value };
  if (mode.value === "day") {
    const start = startOfDay(new Date(`${point.value || dateValue(new Date())}T00:00:00`));
    return { startTime: dateTimeValue(start), finishTime: dateTimeValue(addDays(start, 1)) };
  }
  if (mode.value === "week") {
    const start = startOfWeek(point.value || isoWeek(new Date()));
    return { startTime: dateTimeValue(start), finishTime: dateTimeValue(addDays(start, 7)) };
  }
  if (mode.value === "month") {
    const [year, month] = (point.value || monthValue(new Date())).split("-").map(Number);
    return { startTime: dateTimeValue(new Date(year, month - 1, 1)), finishTime: dateTimeValue(new Date(year, month, 1)) };
  }
  const year = Number(point.value || new Date().getFullYear());
  return { startTime: dateTimeValue(new Date(year, 0, 1)), finishTime: dateTimeValue(new Date(year + 1, 0, 1)) };
}

async function load() {
  const params = getRange();
  if (!params.startTime || !params.finishTime) return (status.value = "请选择开始和结束时间"), (ok.value = false);
  rangeText.value = `${params.startTime.replace("T", " ")} 至 ${params.finishTime.replace("T", " ")}`;
  const data = await run(() => calendarApi.list(params));
  items.value = Array.isArray(data?.data) ? data.data : [];
}

function today() {
  syncPoint();
  load();
}

syncPoint();
</script>
