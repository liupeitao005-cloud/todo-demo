import { reactive } from "vue";
import { clearTodoUserCaches } from "@/utils/userCache";

const TOKEN_KEY = "todo-token";
const USERNAME_KEY = "todo-username";

function readStoredValue(key) {
  return localStorage.getItem(key) || sessionStorage.getItem(key) || "";
}

function writeStoredValue(key, value, remember = true) {
  localStorage.removeItem(key);
  sessionStorage.removeItem(key);
  if (!value) return;
  const storage = remember ? localStorage : sessionStorage;
  storage.setItem(key, value);
}

export const authState = reactive({
  token: readStoredValue(TOKEN_KEY),
  username: readStoredValue(USERNAME_KEY)
});

export function isLoggedIn() {
  return Boolean(authState.token);
}

export function setToken(token, remember = true) {
  authState.token = token || "";
  writeStoredValue(TOKEN_KEY, authState.token, remember);
}

export function setUsername(username, remember = true) {
  authState.username = username || "";
  writeStoredValue(USERNAME_KEY, authState.username, remember);
}

export function logout() {
  clearTodoUserCaches();
  setToken("");
  setUsername("");
}
