import axios from 'axios';
import { setInterceptors } from '@/api/common/interceptor';

function getInstance() {
  return axios.create({
    baseURL: process.env.VUE_APP_API_URL,
  });
}

// 인스턴스 요청
function getInstanceWithAuth() {
  const instance = axios.create({
    baseURL: process.env.VUE_APP_API_URL,
  });
  return setInterceptors(instance);
}

export { getInstance, getInstanceWithAuth };
