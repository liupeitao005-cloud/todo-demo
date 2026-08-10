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

    <TaskStats :stats="stats" />

    <div class="task-board">
      <TaskList
        v-model:active-filter="activeFilter"
        v-model:keyword="keyword"
        :filters="filters"
        :helpers="helpers"
        :loading="loading"
        :selected-task="selectedTask"
        :tasks="filteredTasks"
        @select="selectTask"
      />

      <TaskDetail
        :helpers="helpers"
        :ok="ok"
        :status="status"
        :task="selectedTask"
        @create-child="openCreate"
        @delay="delay"
        @edit="openEdit"
        @finish="finish"
        @next="next"
        @refresh="loadList"
        @remove="remove"
        @select="selectTask"
        @split="split"
      />
    </div>

    <TaskEditorModal
      :form="form"
      :loading="loading"
      :ok="ok"
      :open="editorOpen"
      :status="status"
      :task-type-options="taskTypeOptions"
      @close="closeEditor"
      @submit="saveTask"
      @update:form="updateForm"
    />
  </section>
</template>

<script setup>
import TaskDetail from "@/components/task/TaskDetail.vue";
import TaskEditorModal from "@/components/task/TaskEditorModal.vue";
import TaskList from "@/components/task/TaskList.vue";
import TaskStats from "@/components/task/TaskStats.vue";
import { useTasks } from "@/composables/useTasks";
import "@/styles/task.css";

const {
  activeFilter,
  closeEditor,
  delay,
  editorOpen,
  filteredTasks,
  filters,
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
  stats,
  status,
  taskTypeOptions
} = useTasks();

function updateForm(patch) {
  Object.assign(form, patch);
}
</script>
