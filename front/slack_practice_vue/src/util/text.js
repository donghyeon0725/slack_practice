function cutTextAtStart(str, len) {
  if (str.length > len) return str.substr(0, len) + '...';

  return str.substr(0, len);
}

export { cutTextAtStart };
