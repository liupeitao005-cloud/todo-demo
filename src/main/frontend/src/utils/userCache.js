const CACHE_PREFIX = "todo-cache";
const USERNAME_KEY = "todo-username";

function currentUserKey() {
  const username = (localStorage.getItem(USERNAME_KEY) || sessionStorage.getItem(USERNAME_KEY) || "").trim();
  return encodeURIComponent(username || "anonymous");
}

export function userCacheKey(name) {
  return `${CACHE_PREFIX}:${currentUserKey()}:${name}`;
}

export function removeUserCache(name) {
  sessionStorage.removeItem(userCacheKey(name));
  localStorage.removeItem(userCacheKey(name));
}

export function clearTodoUserCaches() {
  removePrefixedKeys(sessionStorage);
  removePrefixedKeys(localStorage);

  // Legacy cache keys used before per-user isolation.
  sessionStorage.removeItem("todo-home-dashboard");
  sessionStorage.removeItem("todo-backlog-list");
  localStorage.removeItem("todo-habit-checks");
}

function removePrefixedKeys(storage) {
  const keys = [];
  for (let index = 0; index < storage.length; index++) {
    const key = storage.key(index);
    if (key?.startsWith(`${CACHE_PREFIX}:`)) keys.push(key);
  }
  keys.forEach((key) => storage.removeItem(key));
}
