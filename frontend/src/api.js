import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080/api" });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// If the token expires or is invalid, send the user back to login
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 403 && localStorage.getItem("token")) {
      // 403 may also mean "not allowed"; only force logout on auth endpoints failing
    }
    return Promise.reject(err);
  }
);

export default api;
