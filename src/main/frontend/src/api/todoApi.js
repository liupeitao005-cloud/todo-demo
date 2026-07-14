import { http } from "./http";

export const userApi = {
  login: (data) => http.post("/user/login", data),
  register: (data) => http.post("/user/register", data)
};

export const backlogApi = {
  create: (data) => http.post("/backlog/create", data),
  update: (data) => http.put("/backlog/update", data),
  move: (data) => http.post("/backlog/move", data),
  list: () => http.get("/backlog/select")
};

export const taskApi = {
  create: (data) => http.post("/task/create", data),
  update: (data) => http.put("/task/update", data),
  remove: (id) => http.delete("/task/delete", { data: { id } }),
  finish: (id) => http.put("/task/finish", { id }),
  split: (id) => http.put("/task/split", { id }),
  delay: (id) => http.put("/task/goout", { id }),
  next: (id) => http.put("/task/next", { id }),
  list: () => http.get("/task/select")
};

export const scheduleApi = {
  create: (data) => http.post("/schedule/create", data),
  update: (data) => http.put("/schedule/update", data),
  remove: (id) => http.delete("/schedule/delete", { data: { id } }),
  list: () => http.get("/schedule/select")
};

export const calendarApi = {
  list: (params) => http.get("/calendar/select", { params })
};

export const habitApi = {
  create: (data) => http.post("/habbit/create", data),
  list: () => http.get("/habbit/select")
};

export const fourApi = {
  move: (data) => http.post("/four/move", data),
  remove: (id) => http.delete("/four/delete", { data: { id } }),
  list: (params) => http.get("/four/select", { params })
};

export const reviewApi = {
  create: (data) => http.post("/review/create", data),
  finish: (id) => http.put("/review/finish", { id }),
  list: () => http.get("/review/plans")
};

export const reminderApi = {
  create: (data) => http.post("/reminder/create", data),
  list: () => http.get("/reminder/select"),
  pending: () => http.get("/reminder/pending"),
  read: (id) => http.put("/reminder/read", { id })
};
