import { getInstance, getInstanceWithAuth } from '@/api/index';

const ins = getInstance();
const insWithAuth = getInstanceWithAuth();

/**
 * 회원 가입 메일을 요청합니다.
 * @constructor
 * @param {string} email - 메일을 보낼 사용자의 이메일 주소
 */
function signupMail(email) {
  return ins.get(`users/join/${email}`);
}

/**
 * 회원 가입 진행
 * @constructor
 * @param {json} formData - email, name, password 가 필요 합니다.
 * @param {string} joinToken - 회원 가입 메일을 통해 받은 토큰을 보냅니다.
 */
function join(formData, joinToken) {
  return ins.post('users', formData, {
    headers: {
      Authorization: `bearer ${joinToken}`,
    },
  });
}

/**
 * 로그인 진행
 * @constructor
 * @param {json} formData - email, password 가 필요 합니다.
 */
function login(formData) {
  return ins.post('users/login', formData);
}

/**
 * 유저의 이메일 검색 (로그인 필요)
 * @constructor
 * @param {string} email 이 필요 합니다.
 * */
function getUserEmail(email) {
  return insWithAuth.get(`users/${email}`);
}

export { signupMail, join, login, getUserEmail };
