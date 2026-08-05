import { flushPromises, mount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import LoginView from "../LoginView.vue";
import { userApi } from "../../api/todoApi";
import { setToken, setUsername } from "../../stores/auth";

const routerPush = vi.hoisted(() => vi.fn());
const routeQuery = vi.hoisted(() => ({ username: "from-query" }));

vi.mock("vue-router", () => ({
  useRoute: () => ({ query: routeQuery }),
  useRouter: () => ({ push: routerPush })
}));

vi.mock("../../api/todoApi", () => ({
  userApi: {
    login: vi.fn()
  }
}));

vi.mock("../../stores/auth", () => ({
  setToken: vi.fn(),
  setUsername: vi.fn()
}));

describe("LoginView", () => {
  it("prefills username from route query", async () => {
    const wrapper = mount(LoginView);
    await flushPromises();

    expect(wrapper.get('input[autocomplete="username"]').element.value).toBe("from-query");
  });

  it("does not call login api when required fields are missing", async () => {
    const wrapper = mount(LoginView);

    await wrapper.get("form").trigger("submit");

    expect(userApi.login).not.toHaveBeenCalled();
    expect(wrapper.text()).toContain("请填写用户名和密码");
  });

  it("stores token and navigates home after successful login", async () => {
    vi.mocked(userApi.login).mockResolvedValue({ code: 200, data: "token-123" });
    const wrapper = mount(LoginView);

    await wrapper.get('input[autocomplete="username"]').setValue("demo");
    await wrapper.get('input[autocomplete="current-password"]').setValue("123456");
    await wrapper.get("form").trigger("submit");
    await flushPromises();

    expect(userApi.login).toHaveBeenCalledWith(expect.objectContaining({
      username: "demo",
      password: "123456"
    }));
    expect(setToken).toHaveBeenCalledWith("token-123");
    expect(setUsername).toHaveBeenCalledWith("demo");
    expect(routerPush).toHaveBeenCalledWith("/");
  });
});
