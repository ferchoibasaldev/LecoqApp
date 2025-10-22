import axios from "axios";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? "http://localhost:8080",
});

// Cargar token si existe (al arrancar)
const token = localStorage.getItem("token");
if (token) {
  api.defaults.headers.common.Authorization = `Bearer ${token}`;
}

// Helpers para auth
export function setAuthToken(token?: string | null) {
  if (token) {
    api.defaults.headers.common.Authorization = `Bearer ${token}`;
    localStorage.setItem("token", token);
  } else {
    delete api.defaults.headers.common.Authorization;
    localStorage.removeItem("token");
  }
}

export default api;
