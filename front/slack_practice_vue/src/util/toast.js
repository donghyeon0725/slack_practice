/**
 * 이 라이브러리는 아래와 같은 의존성을 필요로 합니다.
 * @see https://www.npmjs.com/package/vue-toast-notification
 * */
let toast = {
  defualt: function (message, options) {
    let config = {
      message: message,
      type: 'success',
      position: 'top-right',
      duration: 3000,
      dismissible: true,
      onClick: undefined,
      onDismiss: undefined,
      queue: false,
      pauseOnHover: true,
    };

    Object.assign(config, options);

    this.$toast.open(config);
  },
};

export default toast;
