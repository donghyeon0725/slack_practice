function validateEmail(email) {
  var re =
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(String(email).toLowerCase());
}

function isEmpty(value) {
  if (typeof value === 'undefined' || value === '' || value === null)
    return true;
  return false;
}

function isNotEmpty(value) {
  return !isEmpty(value);
}

function isLetter(str) {
  return str.length === 1 && str.match(/[a-z]/i);
}

export { validateEmail, isEmpty, isNotEmpty, isLetter };
