<template>
  <section class="backlog-page">
    <div class="backlog-shell">
      <header class="backlog-header">
        <div>
          <h1>待办箱</h1>
          <p>记录想法和待处理的事项，轻松管理从这里开始</p>
        </div>
        <button class="primary-action" type="button" @click="openCreate">
          <span>+</span>
          创建待办
        </button>
      </header>

      <div class="backlog-tabs">
        <button class="tab-button active" type="button">全部待办</button>
      </div>

      <section class="backlog-list" aria-label="待办列表">
        <article v-for="item in items" :key="item.id" class="backlog-card">
          <div class="card-icon" aria-hidden="true">
          </div>
          <div class="card-content">
            <h2>{{ item.title }}</h2>
            <p>{{ item.content || "暂无内容" }}</p>
            <time>{{ formatTime(item.createTime || item.updateTime) }}</time>
          </div>
          <div class="card-menu-wrap">
            <button class="more-button" type="button" aria-label="更多操作" @click.stop="toggleMenu(item.id)">
              <span aria-hidden="true">...</span>
            </button>
            <div v-if="activeMenuId === item.id" class="card-menu">
              <button type="button" @click="openEdit(item)">
                编辑
              </button>
              <button type="button" @click="openMove(item)">
                转为任务
              </button>
              <button class="danger" type="button" @click="openDelete(item)">
                删除
              </button>
            </div>
          </div>
        </article>

        <div v-if="hasLoaded && !items.length && !loading" class="empty-state">
          <div class="empty-icon">
          </div>
          <strong>还没有待办事项</strong>
          <p>点击右上角“创建待办”，记录你的第一个想法吧！</p>
        </div>
      </section>
    </div>

    <div v-if="dialogType" class="modal-mask" @click.self="closeDialog">
      <section class="modal-card" role="dialog" aria-modal="true">
        <header class="modal-header">
          <h2>{{ modalTitle }}</h2>
          <button class="close-button" type="button" aria-label="关闭" @click="closeDialog">
            x
          </button>
        </header>

        <form v-if="dialogType === 'create' || dialogType === 'edit'" class="modal-form" @submit.prevent="submitBacklog">
          <label>
            标题
            <input v-model.trim="form.title" placeholder="请输入待办标题，如：整理项目资料" />
          </label>
          <label>
            内容（可选）
            <textarea v-model.trim="form.content" placeholder="请输入待办内容，简单描述需要完成的事项..."></textarea>
          </label>
          <p v-if="status" :class="['status-text', ok ? 'ok' : 'err']">{{ status }}</p>
          <footer class="modal-actions">
            <button class="ghost-button" type="button" @click="closeDialog">取消</button>
            <button class="submit-button" type="submit" :disabled="loading">{{ dialogType === "edit" ? "保存" : "创建" }}</button>
          </footer>
        </form>

        <form v-else-if="dialogType === 'move'" class="modal-form" @submit.prevent="submitMove">
          <div class="move-summary">
            <span>待办</span>
            <strong>{{ form.title }}</strong>
          </div>
          <div class="time-grid">
            <label>
              开始时间
              <input v-model="form.startTime" type="datetime-local" />
            </label>
            <label>
              结束时间
              <input v-model="form.finishTime" type="datetime-local" />
            </label>
          </div>
          <p v-if="status" :class="['status-text', ok ? 'ok' : 'err']">{{ status }}</p>
          <footer class="modal-actions">
            <button class="ghost-button" type="button" @click="closeDialog">取消</button>
            <button class="submit-button" type="submit" :disabled="loading">转为任务</button>
          </footer>
        </form>

        <div v-else-if="dialogType === 'delete'" class="confirm-body">
          <p>确定要删除“{{ form.title }}”吗？删除后无法恢复。</p>
          <p v-if="status" :class="['status-text', ok ? 'ok' : 'err']">{{ status }}</p>
          <footer class="modal-actions">
            <button class="ghost-button" type="button" @click="closeDialog">取消</button>
            <button class="danger-button" type="button" :disabled="loading" @click="submitDelete">删除</button>
          </footer>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { backlogApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const CACHE_KEY = "todo-backlog-list";
const cachedState = readCache();
const items = ref(cachedState.items);
const hasLoaded = ref(cachedState.loaded);
const activeMenuId = ref(null);
const dialogType = ref("");
const form = reactive({ id: null, title: "", content: "", startTime: "", finishTime: "" });
const { loading, status, ok, run } = useRequest();

function readCache() {
  try {
    const raw = sessionStorage.getItem(CACHE_KEY);
    if (!raw) return { loaded: false, items: [] };
    const data = JSON.parse(raw);
    return { loaded: true, items: Array.isArray(data) ? data : [] };
  } catch {
    return { loaded: false, items: [] };
  }
}

function writeCache(nextItems) {
  try {
    sessionStorage.setItem(CACHE_KEY, JSON.stringify(nextItems));
  } catch {
    // Cache is only for smoothing page switches.
  }
}

const modalTitle = computed(() => {
  if (dialogType.value === "edit") return "编辑待办";
  if (dialogType.value === "move") return "转为任务";
  if (dialogType.value === "delete") return "删除待办";
  return "创建待办";
});

function resetForm() {
  form.id = null;
  form.title = "";
  form.content = "";
  form.startTime = "";
  form.finishTime = "";
  status.value = "";
  ok.value = false;
}

function fillForm(item) {
  form.id = item.id;
  form.title = item.title || "";
  form.content = item.content || "";
  form.startTime = "";
  form.finishTime = "";
}

function openCreate() {
  resetForm();
  activeMenuId.value = null;
  dialogType.value = "create";
}

function openEdit(item) {
  resetForm();
  fillForm(item);
  activeMenuId.value = null;
  dialogType.value = "edit";
}

function openMove(item) {
  resetForm();
  fillForm(item);
  activeMenuId.value = null;
  dialogType.value = "move";
}

function openDelete(item) {
  resetForm();
  fillForm(item);
  activeMenuId.value = null;
  dialogType.value = "delete";
}

function closeDialog() {
  dialogType.value = "";
  resetForm();
}

function toggleMenu(id) {
  activeMenuId.value = activeMenuId.value === id ? null : id;
}

function formatTime(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hour = String(date.getHours()).padStart(2, "0");
  const minute = String(date.getMinutes()).padStart(2, "0");
  return `${month}-${day} ${hour}:${minute}`;
}

function validateTitle() {
  if (form.title) return true;
  status.value = "请填写待办标题";
  ok.value = false;
  return false;
}

async function loadList(options = {}) {
  const data = options.silent ? await backlogApi.list() : await run(() => backlogApi.list(), "列表已刷新");
  items.value = Array.isArray(data?.data) ? data.data : [];
  hasLoaded.value = true;
  writeCache(items.value);
}

async function submitBacklog() {
  if (!validateTitle()) return;
  const content = form.content || form.title;
  const action =
    dialogType.value === "edit"
      ? () => backlogApi.update({ id: form.id, title: form.title, content })
      : () => backlogApi.create({ title: form.title, content });
  const data = await run(action);
  if (data?.code === 200) {
    await loadList({ silent: true });
    closeDialog();
  }
}

async function submitMove() {
  if (!form.id) return;
  if (!form.startTime || !form.finishTime) {
    status.value = "请填写开始和结束时间";
    ok.value = false;
    return;
  }
  const data = await run(() =>
    backlogApi.move({
      id: form.id,
      title: form.title,
      content: form.content || form.title,
      startTime: form.startTime,
      finishTime: form.finishTime
    })
  );
  if (data?.code === 200) {
    await loadList({ silent: true });
    closeDialog();
  }
}

async function submitDelete() {
  if (!form.id) return;
  const data = await run(() => backlogApi.remove(form.id));
  if (data?.code === 200) {
    await loadList({ silent: true });
    closeDialog();
  }
}

onMounted(() => loadList({ silent: true }));
</script>

<style scoped>
.backlog-page {
  min-height: 100vh;
  padding: 32px clamp(20px, 4vw, 44px);
  background: linear-gradient(135deg, #f7fbff 0%, #eef5ff 48%, #fbfdff 100%);
}

.backlog-shell {
  width: min(1280px, 100%);
  height: calc(100vh - 64px);
  min-height: 0;
  display: grid;
  grid-template-rows: auto auto minmax(0, 1fr);
  margin: 0 auto;
  padding: 34px 36px;
  border: 1px solid #e3eaf5;
  border-radius: 16px;
  background: rgba(255, 255, 255, .94);
  box-shadow: 0 18px 46px rgba(27, 75, 148, .08);
}

.backlog-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 28px;
}

.backlog-header h1 {
  margin: 0;
  color: #08142d;
  font-size: 30px;
  line-height: 1.2;
}

.backlog-header p {
  margin: 10px 0 0;
  color: #63728d;
  font-size: 15px;
}

.primary-action {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-height: 48px;
  border: 0;
  border-radius: 8px;
  padding: 0 24px;
  color: #fff;
  background: #1f6fff;
  box-shadow: 0 12px 28px rgba(31, 111, 255, .24);
  font-size: 16px;
  font-weight: 800;
}

.primary-action span {
  font-size: 24px;
  line-height: 1;
}

.backlog-tabs {
  margin-bottom: 8px;
  border-bottom: 1px solid #e5ebf4;
}

.tab-button {
  position: relative;
  border: 0;
  padding: 0 8px 14px;
  color: #17233d;
  background: transparent;
  font-size: 15px;
  font-weight: 800;
}

.tab-button.active::after {
  position: absolute;
  left: 50%;
  bottom: -1px;
  width: 28px;
  height: 3px;
  border-radius: 999px;
  background: #2f73ff;
  content: "";
  transform: translateX(-50%);
}

.backlog-list {
  display: grid;
  align-content: start;
  gap: 14px;
  min-height: 0;
  overflow: auto;
  padding-top: 14px;
  padding-right: 4px;
}

.backlog-card {
  position: relative;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 16px;
  align-items: center;
  min-height: 94px;
  border: 1px solid #e1e8f2;
  border-radius: 10px;
  padding: 18px 18px;
  background: rgba(255, 255, 255, .98);
  box-shadow: 0 10px 24px rgba(30, 65, 120, .06);
}

.card-icon {
  position: relative;
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 8px;
  color: #2f73ff;
  background: #eef5ff;
}

.card-icon::before,
.empty-icon::before {
  width: 18px;
  height: 22px;
  border: 2px solid currentColor;
  border-radius: 4px;
  content: "";
}

.card-icon::after,
.empty-icon::after {
  position: absolute;
  width: 8px;
  height: 2px;
  border-radius: 999px;
  background: currentColor;
  box-shadow: 0 6px 0 currentColor;
  content: "";
}

.card-content {
  min-width: 0;
}

.card-content h2 {
  margin: 0;
  color: #0e1830;
  font-size: 17px;
  line-height: 1.35;
  overflow-wrap: anywhere;
}

.card-content p {
  margin: 5px 0;
  color: #64728d;
  font-size: 14px;
  line-height: 1.45;
  overflow-wrap: anywhere;
}

.card-content time {
  color: #73829a;
  font-size: 13px;
}

.card-menu-wrap {
  position: relative;
  align-self: flex-start;
  display: flex;
  justify-content: center;
  width: 38px;
}

.more-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border: 0;
  border-radius: 8px;
  color: #71809a;
  background: transparent;
  outline: none;
  font-size: 18px;
  font-weight: 900;
  line-height: 1;
}

.more-button span {
  display: block;
  width: 20px;
  text-align: center;
  transform: translateY(-4px);
}

.more-button:hover {
  color: #1f6fff;
  background: #eef4ff;
}

.more-button:focus-visible {
  color: #1f6fff;
  background: #eef4ff;
  box-shadow: 0 0 0 3px rgba(47, 115, 255, .16);
}

.card-menu {
  position: absolute;
  top: 38px;
  right: 0;
  z-index: 5;
  display: grid;
  gap: 2px;
  width: 132px;
  border: 1px solid #e2e8f2;
  border-radius: 8px;
  padding: 8px;
  background: #fff;
  box-shadow: 0 18px 36px rgba(31, 46, 84, .14);
}

.card-menu button {
  display: flex;
  align-items: center;
  min-height: 36px;
  border: 0;
  border-radius: 6px;
  padding: 0 10px;
  color: #17233d;
  background: transparent;
  font-weight: 700;
  text-align: left;
}

.card-menu button:hover {
  background: #f3f7ff;
}

.card-menu .danger {
  color: #ff4d4f;
}

.empty-state {
  display: grid;
  justify-items: center;
  gap: 8px;
  padding: 52px 20px 28px;
  color: #6f7f98;
  text-align: center;
}

.empty-state strong {
  color: #26344f;
  font-size: 16px;
}

.empty-state p {
  margin: 0;
}

.empty-icon {
  position: relative;
  display: grid;
  place-items: center;
  width: 64px;
  height: 64px;
  border-radius: 16px;
  color: #8fb7ff;
  background: #eef5ff;
}

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 30;
  display: grid;
  place-items: center;
  padding: 22px;
  background: rgba(8, 20, 45, .18);
  backdrop-filter: blur(6px);
}

.modal-card {
  width: min(460px, 100%);
  border: 1px solid #dfe7f2;
  border-radius: 12px;
  padding: 22px;
  background: #fff;
  box-shadow: 0 26px 70px rgba(17, 31, 62, .2);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.modal-header h2 {
  margin: 0;
  color: #101b34;
  font-size: 18px;
}

.close-button {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  border: 0;
  border-radius: 8px;
  color: #4d5d78;
  background: transparent;
}

.close-button:hover {
  background: #f2f5fa;
}

.modal-form {
  display: grid;
  gap: 16px;
}

.modal-form label {
  display: grid;
  gap: 8px;
  color: #1d2b46;
  font-size: 14px;
  font-weight: 800;
}

.modal-form input,
.modal-form textarea {
  width: 100%;
  border: 1px solid #d7e0ec;
  border-radius: 8px;
  padding: 12px 13px;
  color: #13213b;
  background: #fff;
  font: inherit;
  font-weight: 500;
  outline: none;
}

.modal-form input:focus,
.modal-form textarea:focus {
  border-color: #2f73ff;
  box-shadow: 0 0 0 3px rgba(47, 115, 255, .12);
}

.modal-form textarea {
  min-height: 92px;
  resize: vertical;
}

.time-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.move-summary {
  display: grid;
  gap: 4px;
  border-radius: 8px;
  padding: 12px;
  background: #f6f8fc;
}

.move-summary span {
  color: #77869d;
  font-size: 13px;
}

.move-summary strong {
  color: #13213b;
}

.status-text {
  margin: -4px 0 0;
  font-size: 14px;
}

.status-text.ok {
  color: #20a870;
}

.status-text.err {
  color: #ef4444;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 6px;
}

.ghost-button,
.submit-button,
.danger-button {
  min-width: 86px;
  min-height: 42px;
  border-radius: 8px;
  padding: 0 18px;
  font-weight: 800;
}

.ghost-button {
  border: 1px solid #d7e0ec;
  color: #3e4c64;
  background: #fff;
}

.submit-button {
  border: 0;
  color: #fff;
  background: #1f6fff;
}

.danger-button {
  border: 0;
  color: #fff;
  background: #ff4d4f;
}

.submit-button:disabled,
.danger-button:disabled {
  cursor: not-allowed;
  opacity: .65;
}

.confirm-body {
  display: grid;
  gap: 18px;
}

.confirm-body p {
  margin: 0;
  color: #344258;
  line-height: 1.7;
}

@media (max-width: 760px) {
  .backlog-page {
    padding: 16px;
  }

  .backlog-shell {
    height: calc(100vh - 32px);
    padding: 24px 18px;
  }

  .backlog-header {
    align-items: stretch;
    flex-direction: column;
  }

  .primary-action {
    justify-content: center;
  }

  .backlog-card {
    grid-template-columns: auto minmax(0, 1fr);
  }

  .card-menu-wrap {
    grid-column: 1 / -1;
    justify-self: end;
  }

  .time-grid {
    grid-template-columns: 1fr;
  }
}
</style>
