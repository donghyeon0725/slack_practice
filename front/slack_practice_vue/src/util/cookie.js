function saveAuthToCookie(value) {
  document.cookie = `til_auth=${value}; Path=/;`;
}

function saveUserEmailToCookie(value) {
  document.cookie = `til_user=${value}; Path=/;`;
}

/* json 방식으로 값을 저장합니다. */
function saveJsonToCookie(key, value) {
  let json = getJsonFromCookie();
  json[key] = value;

  document.cookie = `til_page=${JSON.stringify(json)}; Path=/;`;
}

/* 한번에 그 값을 저장합니다. */
function savewholeJsonToCookie(json) {
  document.cookie = `til_page=${JSON.stringify(json)}; Path=/;`;
}

/* json 데이터를 가져옵니다. */
function getJsonFromCookie() {
  return JSON.parse(
    document.cookie.replace(
      /(?:(?:^|.*;\s*)til_page\s*=\s*([^;]*).*$)|^.*$/,
      '$1',
    ) || '{}',
  );
}

/* json 데이터를 지웁니다. */
function deleteJsonValueFromCookie(key) {
  let json = getJsonFromCookie();

  if ({}.hasOwnProperty.call(json, key)) delete json[key];
}

/* json 의 key에 해당하는 데이터를 가져옵니다. */
function getJsonValueFromCookie(key) {
  return getJsonFromCookie()[key];
}

function getAuthFromCookie() {
  return document.cookie.replace(
    /(?:(?:^|.*;\s*)til_auth\s*=\s*([^;]*).*$)|^.*$/,
    '$1',
  );
}

function getUserEmailFromCookie() {
  return document.cookie.replace(
    /(?:(?:^|.*;\s*)til_user\s*=\s*([^;]*).*$)|^.*$/,
    '$1',
  );
}

// deleteCookie('til_auth'), deleteCookie('til_user')
function deleteCookie(value) {
  document.cookie = `${value}=; expires=Thu, 01 Jan 1970 00:00:01 GMT;`;
}

export {
  saveAuthToCookie,
  saveUserEmailToCookie,
  getAuthFromCookie,
  getUserEmailFromCookie,
  deleteCookie,
  saveJsonToCookie,
  deleteJsonValueFromCookie,
  getJsonValueFromCookie,
  getJsonFromCookie,
  savewholeJsonToCookie,
};
