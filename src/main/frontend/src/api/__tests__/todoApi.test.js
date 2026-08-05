import { beforeEach, describe, expect, it, vi } from "vitest";
import {
  backlogApi,
  calendarApi,
  reminderApi,
  taskApi,
  userApi
} from "../todoApi";
import { http } from "../http";

vi.mock("../http", () => ({
  http: {
    delete: vi.fn(),
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn()
  }
}));

describe("todoApi", () => {
  beforeEach(() => {
    vi.mocked(http.delete).mockResolvedValue({ code: 200 });
    vi.mocked(http.get).mockResolvedValue({ code: 200 });
    vi.mocked(http.post).mockResolvedValue({ code: 200 });
    vi.mocked(http.put).mockResolvedValue({ code: 200 });
  });

  it("calls user login endpoint", async () => {
    const payload = { username: "demo", password: "123456" };

    await userApi.login(payload);

    expect(http.post).toHaveBeenCalledWith("/user/login", payload);
  });

  it("sends delete payloads in request body for backlog deletion", async () => {
    await backlogApi.remove(12);

    expect(http.delete).toHaveBeenCalledWith("/backlog/delete", { data: { id: 12 } });
  });

  it("wraps task actions with the expected id payload", async () => {
    await taskApi.finish(8);

    expect(http.put).toHaveBeenCalledWith("/task/finish", { id: 8 });
  });

  it("passes calendar filters as query params", async () => {
    const params = { startTime: "2026-08-01", finishTime: "2026-08-31" };

    await calendarApi.list(params);

    expect(http.get).toHaveBeenCalledWith("/calendar/select", { params });
  });

  it("loads pending reminders from the reminder endpoint", async () => {
    await reminderApi.pending();

    expect(http.get).toHaveBeenCalledWith("/reminder/pending");
  });
});
