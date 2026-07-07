import { reactive } from "vue";

const TOKEN_KEY = "todo-token";

export const authState = reactive({
  token: localStorage.getItem(TOKEN_KEY) || ""
});

export function isLoggedIn() {
  return Boolean(authState.token);
}

export function setToken(token) {
  authState.token = token || "";
  if (authState.token) localStorage.setItem(TOKEN_KEY, authState.token);
  else localStorage.removeItem(TOKEN_KEY);
}

export function logout() {
  setToken("");
}
