function formatDate(value) {
  const date = new Date(value);
  const year = date.getFullYear();
  let month = date.getMonth() + 1;
  month = month > 9 ? month : `0${month}`;
  const day = date.getDate();
  let hours = date.getHours();
  hours = hours > 9 ? hours : `0${hours}`;
  const minutes = date.getMinutes();
  return `${year}-${month}-${day} ${hours}:${minutes}`;
}

function dayAgo(s) {
  let dayAgoTime = new Date() - new Date(s);
  return Math.floor(dayAgoTime / (3600 * 24 * 1000));
}

export { formatDate, dayAgo };
