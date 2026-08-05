import { flushPromises, mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import HomeView from "../HomeView.vue";
import {
  backlogApi,
  habitApi,
  reminderApi,
  reviewApi,
  scheduleApi,
  taskApi
} from "../../api/todoApi";

vi.mock("../../api/todoApi", () => ({
  backlogApi: {
    create: vi.fn(),
    list: vi.fn()
  },
  habitApi: {
    create: vi.fn(),
    list: vi.fn()
  },
  reminderApi: {
    pending: vi.fn(),
    read: vi.fn()
  },
  reviewApi: {
    list: vi.fn()
  },
  scheduleApi: {
    create: vi.fn(),
    list: vi.fn()
  },
  taskApi: {
    create: vi.fn(),
    list: vi.fn()
  }
}));

vi.mock("../../stores/auth", () => ({
  authState: {
    token: "token-123",
    username: "demo"
  }
}));

describe("HomeView", () => {
  beforeEach(() => {
    vi.mocked(backlogApi.list).mockResolvedValue({ code: 200, data: [] });
    vi.mocked(taskApi.list).mockResolvedValue({ code: 200, data: [] });
    vi.mocked(scheduleApi.list).mockResolvedValue({ code: 200, data: [] });
    vi.mocked(habitApi.list).mockResolvedValue({ code: 200, data: [] });
    vi.mocked(reviewApi.list).mockResolvedValue({ code: 200, data: [] });
    vi.mocked(reminderApi.pending).mockResolvedValue({ code: 200, data: [] });
    vi.mocked(reminderApi.read).mockResolvedValue({ code: 200 });
    vi.mocked(backlogApi.create).mockResolvedValue({ code: 200 });
  });

  it("loads dashboard lists and renders returned backlog data", async () => {
    vi.mocked(backlogApi.list).mockResolvedValue({
      code: 200,
      data: [{ id: 1, title: "Write tests", content: "frontend coverage" }]
    });
    const wrapper = mount(HomeView);

    await flushPromises();

    expect(backlogApi.list).toHaveBeenCalled();
    expect(taskApi.list).toHaveBeenCalled();
    expect(scheduleApi.list).toHaveBeenCalled();
    expect(habitApi.list).toHaveBeenCalled();
    expect(reviewApi.list).toHaveBeenCalled();
    expect(reminderApi.pending).toHaveBeenCalled();
    expect(wrapper.text()).toContain("Write tests");

    wrapper.unmount();
  });

  it("creates a backlog item from the quick-create modal", async () => {
    const wrapper = mount(HomeView);
    await flushPromises();

    await wrapper.get(".primary-add").trigger("click");
    await wrapper.get(".create-modal input").setValue("New backlog");
    await wrapper.get(".create-modal").trigger("submit");
    await flushPromises();

    expect(backlogApi.create).toHaveBeenCalledWith({
      title: "New backlog",
      content: "New backlog"
    });

    wrapper.unmount();
  });
});
