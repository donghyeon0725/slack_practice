let modal = {
  confirmModal: function (title, message, options) {
    let config = {
      title: title,
      size: 'sm',
      buttonSize: 'sm',
      okVariant: 'primary',
      okTitle: '확인',
      cancelTitle: '취소',
      footerClass: 'p-2',
      hideHeaderClose: false,
      centered: true,
    };
    // 별도 사용할 설정이 있는 경우
    Object.assign(config, options);

    let result = this.$bvModal
      .msgBoxConfirm(message, config)
      .then(value => {
        // 다음 작업이 필요한 경우 사용
        if (value) return true;
        else return false;
      })
      .catch(err => {
        // An error occurred
        console.log(err);
        return false;
      });
    return result;
  },

  msgModal: function (title, message, callback, options) {
    let config = {
      title: title,
      size: 'sm',
      buttonSize: 'sm',
      okVariant: 'primary',
      okTitle: '확인',
      footerClass: 'p-2',
      hideHeaderClose: false,
      centered: true,
    };
    // 별도 사용할 설정이 있는 경우
    Object.assign(config, options);

    let result = this.$bvModal
      .msgBoxOk(message, config)
      .then(value => {
        // 다음 작업이 필요한 경우 사용
        if (value) return true;
        else return false;
      })
      .catch(err => {
        // An error occurred
        console.log(err);
        return false;
      });

    return result;
  },
};

export default modal;
