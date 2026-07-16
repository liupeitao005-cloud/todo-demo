<template>
  <main class="auth-page register-page">
    <section class="auth-left">
      <div class="brand">
        <span class="brand-icon">✓</span>
        <strong>Todo</strong>
      </div>

      <div class="hero-copy">
        <h1>创建账号，<span>开始规划</span>你的每一天</h1>
        <p>注册后即可进入待办、任务、日程、提醒和复习管理模块，让项目 Demo 流程完整跑通。</p>
      </div>

      <div class="feature-list">
        <article>
          <span>✓</span>
          <div>
            <strong>账号独立</strong>
            <p>每个用户只查看自己的待办数据</p>
          </div>
        </article>
        <article>
          <span>▣</span>
          <div>
            <strong>密码确认</strong>
            <p>两次密码一致才允许注册成功</p>
          </div>
        </article>
        <article>
          <span>●</span>
          <div>
            <strong>登录跳转</strong>
            <p>注册成功后回到登录页继续登录</p>
          </div>
        </article>
      </div>

      <div class="desk-illustration" aria-hidden="true">
        <div class="plant"></div>
        <div class="calendar-art">
          <span></span><span></span><span></span><span></span>
          <b>✓</b>
          <i>★</i>
        </div>
        <div class="clock-art"></div>
      </div>
    </section>

    <section class="auth-right">
      <form class="auth-card" @submit.prevent="handleRegister">
        <h2>账号注册</h2>
        <p>请填写账号信息完成注册</p>

        <label>
          用户名
          <input v-model.trim="form.username" autocomplete="username" placeholder="请输入用户名或邮箱" />
        </label>

        <label>
          密码
          <span class="password-field">
            <input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              autocomplete="new-password"
              placeholder="请输入密码"
            />
            <button
              class="eye-button"
              type="button"
              :aria-label="showPassword ? '隐藏密码' : '显示密码'"
              :title="showPassword ? '隐藏密码' : '显示密码'"
              @click="showPassword = !showPassword"
            >
              <svg v-if="!showPassword" class="eye-icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                <path d="M2.1 12s3.6-6.5 9.9-6.5S21.9 12 21.9 12 18.3 18.5 12 18.5 2.1 12 2.1 12Z" />
                <circle cx="12" cy="12" r="3.2" />
              </svg>
              <svg v-else class="eye-icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                <path d="M3 3l18 18" />
                <path d="M10.6 10.6A3.1 3.1 0 0 0 12 15.1c.7 0 1.3-.2 1.8-.6" />
                <path d="M6.5 6.8C3.8 8.6 2.1 12 2.1 12s3.6 6.5 9.9 6.5c1.8 0 3.4-.5 4.8-1.3" />
                <path d="M19.3 15.1c1.6-1.5 2.6-3.1 2.6-3.1S18.3 5.5 12 5.5c-.9 0-1.7.1-2.5.3" />
              </svg>
            </button>
          </span>
        </label>

        <label>
          确认密码
          <span class="password-field">
            <input
              v-model="form.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              autocomplete="new-password"
              placeholder="请再次输入密码"
            />
            <button
              class="eye-button"
              type="button"
              :aria-label="showConfirmPassword ? '隐藏确认密码' : '显示确认密码'"
              :title="showConfirmPassword ? '隐藏确认密码' : '显示确认密码'"
              @click="showConfirmPassword = !showConfirmPassword"
            >
              <svg v-if="!showConfirmPassword" class="eye-icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                <path d="M2.1 12s3.6-6.5 9.9-6.5S21.9 12 21.9 12 18.3 18.5 12 18.5 2.1 12 2.1 12Z" />
                <circle cx="12" cy="12" r="3.2" />
              </svg>
              <svg v-else class="eye-icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                <path d="M3 3l18 18" />
                <path d="M10.6 10.6A3.1 3.1 0 0 0 12 15.1c.7 0 1.3-.2 1.8-.6" />
                <path d="M6.5 6.8C3.8 8.6 2.1 12 2.1 12s3.6 6.5 9.9 6.5c1.8 0 3.4-.5 4.8-1.3" />
                <path d="M19.3 15.1c1.6-1.5 2.6-3.1 2.6-3.1S18.3 5.5 12 5.5c-.9 0-1.7.1-2.5.3" />
              </svg>
            </button>
          </span>
        </label>

        <button class="primary-action" :disabled="loading" type="submit">注册</button>
        <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>

        <div class="divider"><span>或</span></div>

        <button class="register-link" type="button" @click="goLogin">返回登录</button>
      </form>
    </section>

    <footer class="auth-footer">
      <span>© 2025 Todo 待办与日程管理系统。保留所有权利。</span>
      <div>
        <span>帮助中心</span>
        <span>联系我们</span>
      </div>
    </footer>
  </main>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { userApi } from "@/api/todoApi";
import { useRequest } from "@/composables/useRequest";

const route = useRoute();
const router = useRouter();
const form = reactive({
  username: typeof route.query.username === "string" ? route.query.username : "",
  password: "",
  confirmPassword: ""
});
const showPassword = ref(false);
const showConfirmPassword = ref(false);
const { loading, status, ok, run } = useRequest();

function validate() {
  if (!form.username || !form.password || !form.confirmPassword) {
    status.value = "请填写用户名、密码和确认密码";
    ok.value = false;
    return false;
  }
  if (form.password !== form.confirmPassword) {
    status.value = "两次密码不一致";
    ok.value = false;
    return false;
  }
  return true;
}

async function handleRegister() {
  if (!validate()) return;
  const data = await run(() => userApi.register(form), "注册成功");
  if (data?.code === 200) {
    router.push({ name: "login", query: { username: form.username } });
  }
}

function goLogin() {
  router.push({ name: "login", query: form.username ? { username: form.username } : {} });
}
</script>
