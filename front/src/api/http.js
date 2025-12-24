import axios from "axios";

export const http = axios.create({
  baseURL: "/api",
  headers: { "Content-Type": "application/json" },
});


http.interceptors.response.use(
  (res) => res,
  (err) => {
    console.error("API Error:", err?.response?.status, err?.response?.data);
    return Promise.reject(err);
  }
);
