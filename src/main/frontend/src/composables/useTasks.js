import { computed, onMounted, reactive, ref, watch } from "vue";
import { taskApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";
import { removeUserCache } from "@/utils/userCache";

const iconPaths = {
  document: "M7 3h7l4 4v14H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Zm7 0v5h5M9 13h6M9 17h4",
  flag: "M6 21V4h10l1 3h-7v8H6",
  clock: "M12 22a10 10 0 1 1 0-20 10 10 0 0 1 0 20Zm0-14v5l3 2",
  repeat: "M17 2l4 4-4 4M3 11V9a3 3 0 0 1 3-3h15M7 22l-4-4 4-4M21 13v2a3 3 0 0 1-3 3H3"
};

export const taskTypeOptions = [
  { label: "一次性任务", value: "once", tone: "once", caption: "只需完成一次", icon: iconPaths.document },
  { label: "短期任务", value: "short", tone: "short", caption: "短周期内完成", icon: iconPaths.clock },
  { label: "长期任务", value: "long", tone: "long", caption: "持续推进", icon: iconPaths.flag },
  { label: "重复性任务", value: "repeat", tone: "repeat", caption: "固定周期重复", icon: iconPaths.repeat }
];

export const taskFilters = [
  { label: "全部", value: "all" },
  { label: "进行中", value: "active" },
  { label: "已完成", value: "done" },
  { label: "下一个重点", value: "next" },
  ...taskTypeOptions.map((type) => ({ label: type.label, value: type.value }))
];

export function normalizeTaskType(value) {
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

export function isFinished(task) {
  return Number(task?.isFinish || 0) === 1;
}

export function isNext(task) {
  return Number(task?.isNext || 0) === 1 || task?.taskType === "next";
}

export function isOverdue(task) {
  if (!task?.finishTime || isFinished(task)) return false;
  return new Date(task.finishTime).getTime() < Date.now();
}

export function displayTaskId(id) {
  if (!id) return "未保存";
  return `T-${String(id).padStart(4, "0")}`;
}

export function formatTaskDateTime(value) {
  if (!value) return "未安排";
  return String(value).replace("T", " ").slice(0, 16);
}

export function formatTaskTimeRange(start, finish) {
  if (!start && !finish) return "未安排时间";
  if (!finish) return `${formatTaskDateTime(start)} 开始`;
  if (!start) return `${formatTaskDateTime(finish)} 截止`;
  return `${formatTaskDateTime(start)} - ${formatTaskDateTime(finish).slice(11)}`;
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

export function useTasks() {
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
      return matchesKeyword && matchesTaskFilter(task, activeFilter.value);
    });
  });

  watch(filteredTasks, (list) => {
    if (!list.length) return;
    if (!list.some((task) => task.id === selectedId.value)) selectedId.value = list[0].id;
  });

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

  function parentText(task) {
    const parentId = Number(task?.parentId || 0);
    return parentId ? displayTaskId(parentId) : "-";
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
    removeUserCache("home-dashboard");
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

  const helpers = {
    isFinished,
    isNext,
    isOverdue,
    displayId: displayTaskId,
    formatDateTime: formatTaskDateTime,
    formatTimeRange: formatTaskTimeRange,
    childTasks,
    progressText,
    progressPercent,
    taskTypeText,
    taskTone,
    parentText
  };

  return {
    activeFilter,
    closeEditor,
    editorOpen,
    filteredTasks,
    filters: taskFilters,
    finish,
    form,
    helpers,
    keyword,
    loadList,
    loading,
    next,
    ok,
    openCreate,
    openEdit,
    remove,
    saveTask,
    selectTask,
    selectedTask,
    split,
    delay,
    stats,
    status,
    taskTypeOptions
  };
}
