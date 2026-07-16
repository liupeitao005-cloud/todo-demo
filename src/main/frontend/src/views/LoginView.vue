<template>
  <main class="auth-page">
    <section class="auth-left">
      <div class="brand">
        <span class="brand-icon">✓</span>
        <strong>Todo</strong>
      </div>

      <div class="hero-copy">
        <h1>欢迎回来</h1>
        <h2>高效待办，轻松管理每一天</h2>
        <p>Todo 待办与日程管理系统，帮助你合理规划时间，提升效率，专注于重要的事情。</p>
      </div>

      <div class="feature-list">
        <article>
          <span>✓</span>
          <div>
            <strong>待办管理</strong>
            <p>轻松创建、分类和管理任务</p>
          </div>
        </article>
        <article>
          <span>▣</span>
          <div>
            <strong>日程安排</strong>
            <p>日程视图，清晰掌握每一天</p>
          </div>
        </article>
        <article>
          <span>●</span>
          <div>
            <strong>智能提醒</strong>
            <p>多种方式提醒，不错过重要事项</p>
          </div>
        </article>
      </div>

      <div class="decor decor-one"></div>
      <div class="decor decor-two"></div>
      <div class="decor decor-three"></div>
      <div class="dot-grid"></div>

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
      <form class="auth-card" @submit.prevent="handleLogin">
        <div class="form-heading">
          <h2>账号登录</h2>
          <p>欢迎回来！请登录您的账号</p>
        </div>

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
              autocomplete="current-password"
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

        <div class="login-options">
          <label class="remember"><input type="checkbox" /> 记住我</label>
        </div>

        <button class="primary-action" :disabled="loading" type="submit">登录</button>
        <p :class="['status', ok ? 'ok' : 'err']">{{ status }}</p>

        <div class="divider"><span>或</span></div>

        <button class="register-link" type="button" @click="goRegister">
          <span>♟</span>
          立即注册
        </button>

        <p class="signup-tip">
          还没有账号？<button type="button" @click="goRegister">立即注册</button>
        </p>
      </form>
    </section>
  </main>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { userApi } from "@/api/todoApi";
import { setToken, setUsername } from "@/stores/auth";
import { useRequest } from "@/composables/useRequest";

const route = useRoute();
const router = useRouter();
const form = reactive({ username: "", password: "" });
const showPassword = ref(false);
const { loading, status, ok, run } = useRequest();

onMounted(() => {
  if (typeof route.query.username === "string") {
    form.username = route.query.username;
    form.password = "";
  }
});

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
    setUsername(form.username);
    router.push("/");
  }
}

function goRegister() {
  router.push({ name: "register", query: form.username ? { username: form.username } : {} });
}
</script>

<style>
body {
  background: #f3f0ff;
}

.auth-page {
  width: min(1640px, calc(100vw - 88px));
  height: min(940px, calc(100vh - 28px));
  min-height: 760px;
  margin: 14px auto;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  overflow: hidden;
  border-radius: 28px;
  color: #111827;
  background: #fff;
  box-shadow: 0 24px 80px rgba(64, 55, 135, .20);
}

.auth-left {
  position: relative;
  overflow: hidden;
  display: grid;
  align-content: start;
  padding: 68px 72px 300px;
  color: #fff;
  background:
    radial-gradient(circle at 72% 59%, rgba(255, 255, 255, .12), transparent 0 5%, transparent 6%),
    linear-gradient(135deg, #5B5FEF 0%, #7C4DFF 100%);
}

.auth-page .brand {
  position: relative;
  z-index: 3;
  display: flex;
  align-items: center;
  gap: 18px;
  color: #fff;
  font-size: 34px;
  line-height: 1;
}

.auth-page .brand-icon {
  display: grid;
  place-items: center;
  width: 52px;
  height: 52px;
  border-radius: 12px;
  color: #5B5FEF;
  background: #fff;
  font-size: 30px;
  font-weight: 800;
  box-shadow: 0 16px 32px rgba(36, 31, 100, .18);
}

.auth-page .hero-copy {
  position: relative;
  z-index: 3;
  display: grid;
  gap: 18px;
  max-width: 620px;
  margin-top: 84px;
}

.auth-page .hero-copy h1 {
  margin: 0;
  color: #fff;
  font-size: clamp(52px, 4vw, 64px);
  line-height: 1.08;
  letter-spacing: 0;
}

.auth-page .hero-copy h2 {
  margin: 0;
  color: rgba(255, 255, 255, .92);
  font-size: 26px;
  font-weight: 500;
  letter-spacing: 0;
}

.auth-page .hero-copy p {
  max-width: 560px;
  color: rgba(255, 255, 255, .88);
  font-size: 20px;
  line-height: 1.9;
}

.auth-page .feature-list {
  position: relative;
  z-index: 3;
  display: grid;
  gap: 24px;
  max-width: 560px;
  margin-top: 34px;
}

.auth-page .feature-list article {
  display: grid;
  grid-template-columns: 58px 1fr;
  align-items: center;
  gap: 22px;
}

.auth-page .feature-list article > span {
  display: grid;
  place-items: center;
  width: 58px;
  height: 58px;
  border-radius: 12px;
  color: #fff;
  background: rgba(255, 255, 255, .18);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, .16);
  font-size: 24px;
}

.auth-page .feature-list strong {
  display: block;
  color: #fff;
  font-size: 24px;
  line-height: 1.2;
}

.auth-page .feature-list p {
  margin-top: 8px;
  color: rgba(255, 255, 255, .84);
  font-size: 18px;
}

.auth-page .decor {
  position: absolute;
  z-index: 1;
  border-radius: 999px;
  background: rgba(255, 255, 255, .10);
  box-shadow: inset 0 0 18px rgba(255, 255, 255, .08);
}

.auth-page .decor-one {
  left: 88px;
  bottom: 128px;
  width: 96px;
  height: 96px;
}

.auth-page .decor-two {
  right: 216px;
  bottom: 276px;
  width: 72px;
  height: 72px;
}

.auth-page .decor-three {
  right: 96px;
  bottom: 156px;
  width: 64px;
  height: 64px;
}

.auth-page .dot-grid {
  position: absolute;
  right: 72px;
  bottom: 168px;
  z-index: 1;
  width: 112px;
  height: 72px;
  opacity: .32;
  background-image: radial-gradient(circle, rgba(255, 255, 255, .7) 2px, transparent 2px);
  background-size: 16px 16px;
}

.auth-page .desk-illustration {
  position: absolute;
  left: 50%;
  bottom: 24px;
  z-index: 2;
  width: 560px;
  height: 310px;
  transform: translateX(-50%);
}

.auth-page .calendar-art {
  position: absolute;
  left: 154px;
  bottom: 26px;
  width: 286px;
  height: 196px;
  border-radius: 18px 18px 24px 24px;
  background: #fff;
  border-top: 34px solid #f2f4ff;
  box-shadow: 22px 34px 54px rgba(33, 30, 94, .24);
  transform: rotate(4deg);
}

.auth-page .calendar-art::before {
  content: "";
  position: absolute;
  inset: 34px 34px 24px;
  background:
    linear-gradient(#ececf6 0 0) 0 0 / 46px 38px,
    linear-gradient(#ececf6 0 0) 70px 0 / 46px 38px,
    linear-gradient(#ececf6 0 0) 140px 0 / 46px 38px,
    linear-gradient(#ececf6 0 0) 0 60px / 46px 38px,
    linear-gradient(#ececf6 0 0) 70px 60px / 46px 38px,
    linear-gradient(#ececf6 0 0) 140px 60px / 46px 38px;
  background-repeat: no-repeat;
}

.auth-page .calendar-art span {
  position: relative;
  top: -62px;
  display: inline-block;
  width: 18px;
  height: 54px;
  margin-left: 38px;
  border: 7px solid rgba(124, 77, 255, .66);
  border-bottom: 0;
  border-radius: 16px 16px 0 0;
}

.auth-page .calendar-art b,
.auth-page .calendar-art i {
  position: absolute;
  z-index: 1;
  display: grid;
  place-items: center;
  width: 50px;
  height: 50px;
  border-radius: 10px;
  color: #fff;
  font-style: normal;
  font-size: 24px;
  box-shadow: 0 12px 24px rgba(37, 34, 109, .25);
}

.auth-page .calendar-art b {
  left: 112px;
  top: 92px;
  background: #6658f2;
}

.auth-page .calendar-art i {
  right: 22px;
  bottom: 20px;
  background: #ffbf32;
}

.auth-page .clock-art {
  position: absolute;
  right: 0;
  bottom: 18px;
  width: 168px;
  height: 168px;
  border: 16px solid #7865f6;
  border-radius: 50%;
  background: #f9f8ff;
  box-shadow: 18px 28px 48px rgba(33, 30, 94, .26);
}

.auth-page .clock-art::before,
.auth-page .clock-art::after {
  content: "";
  position: absolute;
  left: 50%;
  top: 50%;
  width: 46px;
  height: 4px;
  border-radius: 999px;
  background: #6b5df2;
  transform-origin: left center;
}

.auth-page .clock-art::before { transform: rotate(-36deg); }
.auth-page .clock-art::after { width: 32px; transform: rotate(90deg); }

.auth-page .plant {
  position: absolute;
  left: 78px;
  bottom: 24px;
  width: 72px;
  height: 82px;
  border-radius: 0 0 22px 22px;
  background: #fff;
  box-shadow: 12px 22px 34px rgba(33, 30, 94, .20);
}

.auth-page .plant::before {
  content: "";
  position: absolute;
  left: 24px;
  bottom: 60px;
  width: 24px;
  height: 84px;
  border-radius: 28px 28px 4px 28px;
  background: #53c95a;
  box-shadow: -34px 34px 0 #79d95b, 32px 20px 0 #3cab45;
}

.auth-right {
  display: grid;
  place-items: center;
  padding: 88px 72px;
  background: #fff;
}

.auth-card {
  width: 580px;
  display: grid;
  gap: 24px;
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
}

.auth-page .form-heading {
  display: grid;
  gap: 16px;
  margin-bottom: 28px;
  text-align: center;
}

.auth-card h2 {
  margin: 0;
  color: #12182f;
  font-size: 36px;
  line-height: 1.2;
}

.auth-card > p,
.auth-page .form-heading p {
  color: #697086;
  font-size: 18px;
  text-align: center;
}

.auth-card label {
  color: #111827;
  font-size: 18px;
  font-weight: 700;
}

.auth-card input {
  width: 100%;
  min-height: 64px;
  border: 1px solid #d8deeb;
  border-radius: 12px;
  padding: 14px 24px;
  color: #111827;
  font-size: 18px;
  background: #fff;
}

.auth-page .password-field {
  position: relative;
  display: block;
}

.auth-page .password-field input {
  padding-right: 64px;
}

.auth-page .eye-button {
  position: absolute;
  right: 12px;
  top: 50%;
  display: grid;
  place-items: center;
  width: 40px;
  min-height: 40px;
  border: 0;
  border-radius: 10px;
  padding: 0;
  color: #5B5FEF;
  background: transparent;
  transform: translateY(-50%);
}

.auth-page .eye-button:hover,
.auth-page .eye-button:focus-visible {
  background: #f3f0ff;
}

.auth-page .eye-icon {
  width: 22px;
  height: 22px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.9;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.login-options {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 12px;
  color: #4b5565;
  min-height: 32px;
}

.auth-page .remember {
  display: flex;
  grid-template-columns: none;
  align-items: center;
  gap: 12px;
  color: #31394b;
  font-size: 18px;
  font-weight: 400;
}

.auth-page .remember input {
  width: 22px;
  min-height: 22px;
  border-radius: 5px;
}

.auth-page .primary-action {
  min-height: 64px;
  border: 0;
  border-radius: 12px;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #6860f0, #4d45a8);
  box-shadow: 0 16px 32px rgba(91, 95, 239, .24);
}

.auth-page .divider {
  position: relative;
  display: grid;
  place-items: center;
  margin: 18px 0 8px;
  color: #7c8798;
  font-size: 18px;
}

.auth-page .divider::before {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  height: 1px;
  background: #e7ebf3;
}

.auth-page .divider span {
  position: relative;
  padding: 0 28px;
  background: #fff;
}

.auth-page .register-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  min-height: 64px;
  border: 1px solid #d8deeb;
  border-radius: 12px;
  color: #1f2937;
  font-size: 18px;
  font-weight: 700;
  background: #fff;
}

.auth-page .register-link span {
  color: #625af0;
}

.auth-page .register-link:hover {
  background: #fafbff;
}

.auth-page .signup-tip {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin-top: 18px;
  color: #687083;
  font-size: 18px;
}

.auth-page .signup-tip button {
  min-height: 0;
  border: 0;
  padding: 0;
  color: #5B5FEF;
  background: transparent;
  font-size: 18px;
  font-weight: 700;
}

.auth-page .status {
  min-height: 16px;
  margin-top: -12px;
  margin-bottom: -4px;
  text-align: center;
}

@media (max-width: 1100px) {
  .auth-page {
    width: calc(100vw - 32px);
    height: auto;
    min-height: calc(100vh - 32px);
    margin: 16px auto;
    grid-template-columns: 1fr;
  }

  .auth-left {
    min-height: 760px;
    padding: 56px 40px 300px;
  }

  .auth-right {
    padding: 64px 24px;
  }
}

@media (max-width: 680px) {
  .auth-page {
    width: calc(100vw - 24px);
    margin: 12px auto;
    border-radius: 24px;
  }

  .auth-left {
    min-height: 680px;
    padding: 40px 24px 260px;
  }

  .auth-page .brand {
    font-size: 28px;
  }

  .auth-page .hero-copy {
    margin-top: 64px;
  }

  .auth-page .hero-copy h1 {
    font-size: 44px;
  }

  .auth-page .hero-copy h2,
  .auth-page .hero-copy p,
  .auth-page .feature-list p {
    font-size: 16px;
  }

  .auth-page .feature-list strong {
    font-size: 18px;
  }

  .auth-page .desk-illustration {
    width: 440px;
    transform: translateX(-50%) scale(.78);
    transform-origin: center bottom;
  }

  .auth-card {
    width: 100%;
  }
}
</style>
