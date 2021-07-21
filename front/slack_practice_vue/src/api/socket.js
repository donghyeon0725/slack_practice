// import { getInstance } from '@/api/index';
import axios from 'axios';

const ins = axios.create({
  baseURL: process.env.VUE_APP_API_URL,
});

function getSocketUrl() {
  return ins.get('socket');
}

export { getSocketUrl };
