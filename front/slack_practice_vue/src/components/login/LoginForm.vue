<template>
  <div style="width: 300px">
    <b-form @submit="onSubmit" @reset="onReset" v-if="show">
      <b-form-group
        id="input-group-1"
        label="Email address:"
        label-for="input-1"
        :description="emailValidation"
      >
        <b-form-input
          id="input-1"
          v-model="form.email"
          type="email"
          placeholder="Enter email"
          required
          autocomplete="off"
        ></b-form-input>
      </b-form-group>

      <b-form-group
        id="input-group-2"
        label="Password:"
        label-for="input-2"
        class="mb-2"
      >
        <b-form-input
          id="input-2"
          v-model="form.password"
          type="password"
          placeholder="Enter password"
          required
          autocomplete="off"
        ></b-form-input>
      </b-form-group>

      <b-button type="submit" variant="primary">Submit</b-button>
    </b-form>
  </div>
</template>

<script>
import { validateEmail } from '@/util/validation';

export default {
  data() {
    return {
      form: {
        email: '',
        password: '',
      },
      description: '',
      show: true,
    };
  },
  methods: {
    async onSubmit(event) {
      event.preventDefault();
      try {
        const { status } = await this.$store.dispatch('login', this.form);

        if (status == 200) {
          alert(`${this.form.email}님 반갑습니다.`);
        }
        await this.$router.push('/main');
        return;
      } catch (e) {
        console.log(e);
        if (e.response.status == 415)
          console.log(
            '로그인을 위한 데이터가 잘못 되었습니다. 요청을 확인하세요',
          );
        alert('error');
      }
    },
    onReset(event) {
      event.preventDefault();
      this.form.email = '';
      this.show = false;
      this.$nextTick(() => {
        this.show = true;
      });
    },
  },
  computed: {
    emailValidation() {
      if (!validateEmail(this.form.email) && this.form.email)
        return 'this email does not validate';
      else if (!this.form.email) return 'input email that you want to join';
      else return 'email validate';
    },
  },
};
</script>
