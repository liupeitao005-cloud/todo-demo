<template>
  <main class="login-page">
    <section class="login-shell">
      <div class="login-hero">
        <span class="brand-mark">T</span>
        <h1>Todo 待办与日程管理系统</h1>
        <p>使用 Vue 框架完成 Web 展示，仍然放在同一个 Spring Boot 项目中。</p>
      </div>

      <form class="form-card login-card" @submit.prevent="handleLogin">
        <h2>账号登录</h2>
        <label>用户名<input v-model.trim="form.username" autocomplete="username" placeholder="请输入用户名" /></label>
        <label>密码<input v-model="form.password" type="password" autocomplete="current-password" placeholder="请输入密码" /></label>
        <div class="actions">
          <button :disabled="loading" type="submit">登录</button>
          <button class="secondary" :disabled="loading" type="button" @click="handleRegister">注册</button>
        </div>
        <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>
      </form>
    </section>
  </main>
</template>

<script setup>
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { userApi } from "@/api/todoApi";
import { setToken } from "@/stores/auth";
import { useRequest } from "@/composables/useRequest";

const router = useRouter();
const form = reactive({ username: "", password: "" });
const { loading, status, ok, run } = useRequest();

function validate() {
  if (!form.username || !form.password) {
    status.value = "请填写用户名和密码";
    ok.value = false;
    return false;
  }
  return true;
}

async function handleLogin() {
  if (!validate()) return;
  const data = await run(() => userApi.login(form));
  if (data?.code === 200 && data.data) {
    setToken(data.data);
    router.push("/");
  }
}

async function handleRegister() {
  if (!validate()) return;
  await run(() => userApi.register(form), "注册完成，请登录");
}
</script>
