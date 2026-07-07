import { ref } from "vue";

export function useRequest() {
  const loading = ref(false);
  const status = ref("");
  const ok = ref(false);
  const result = ref(null);

  async function run(action, successMessage = "操作完成") {
    loading.value = true;
    status.value = "请求中...";
    ok.value = true;
    try {
      const data = await action();
      result.value = data;
      ok.value = data?.code === 200;
      status.value = data?.message || successMessage;
      return data;
    } catch (error) {
      result.value = error;
      ok.value = false;
      status.value = error?.message || "请求失败，请确认后端服务已启动";
      return null;
    } finally {
      loading.value = false;
    }
  }

  return { loading, status, ok, result, run };
}
