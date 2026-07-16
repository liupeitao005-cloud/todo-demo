import { reactive } from "vue";

const TOKEN_KEY = "todo-token";
const USERNAME_KEY = "todo-username";

export const authState = reactive({
  token: localStorage.getItem(TOKEN_KEY) || "",
  username: localStorage.getItem(USERNAME_KEY) || ""
});

export function isLoggedIn() {
  return Boolean(authState.token);
}

export function setToken(token) {
  authState.token = token || "";
  if (authState.token) localStorage.setItem(TOKEN_KEY, authState.token);
  else localStorage.removeItem(TOKEN_KEY);
}

export function setUsername(username) {
  authState.username = username || "";
  if (authState.username) localStorage.setItem(USERNAME_KEY, authState.username);
  else localStorage.removeItem(USERNAME_KEY);
}

export function logout() {
  setToken("");
  setUsername("");
}
