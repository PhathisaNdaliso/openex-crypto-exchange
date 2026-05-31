import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

export async function getUsers() {
  const response = await apiClient.get("/api/users");
  return response.data;
}

export async function getWallets() {
  const response = await apiClient.get("/api/wallets");
  return response.data;
}

export async function getOrders() {
  const response = await apiClient.get("/api/orders");
  return response.data;
}

export async function getMarketOverview() {
  const response = await apiClient.get("/api/market/overview");
  return response.data;
}

export default apiClient;
