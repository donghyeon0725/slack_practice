let box = {
  selectById: (list, id) => {
    return list.filter(s => s.id == id)[0];
  },
};

export { box };
