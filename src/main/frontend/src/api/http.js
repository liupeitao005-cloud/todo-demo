import axios from "axios";
import { authState, logout } from "@/stores/auth";

export const http = axios.create({
  baseURL: import.meta.env.DEV ? "/api" : "",
  timeout: 10000
});

http.interceptors.request.use((config) => {
  if (authState.token) config.headers.token = authState.token;
  return config;
});

http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) logout();
    return Promise.reject(error.response?.data || { code: 500, message: error.message || "请求失败" });
  }
);
