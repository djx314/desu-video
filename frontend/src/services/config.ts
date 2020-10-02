import axios from "axios";

const baseUrl = "/back";
axios.defaults.withCredentials = true;
axios.defaults.headers = {
  "Content-Type": "application/json",
  "Accept": "application/json",
};

//axios 的实例及拦截器配置
const axiosInstance = axios.create({
  baseURL: baseUrl,
});

axiosInstance.interceptors.response.use(
  (res) => res,
  (err) => err.response,
);

export default axiosInstance;
