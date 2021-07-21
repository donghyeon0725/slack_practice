<template>
  <div>
    <b-form @submit="onSubmit" v-if="show">
      <b-form-group
        id="input-group-1"
        label="Email address:"
        label-for="input-1"
        description="We'll never share your email with anyone else."
      >
        <b-form-input
          id="input-1"
          v-model="form.email"
          type="email"
          placeholder="Enter email"
          required
          disabled
        ></b-form-input>
      </b-form-group>

      <b-form-group id="input-group-2" label="Your Name:" label-for="input-2">
        <b-form-input
          id="input-2"
          v-model="form.name"
          placeholder="Enter name"
          required
        ></b-form-input>
      </b-form-group>

      <b-form-group id="input-group-3" label="Password:" label-for="input-3">
        <b-form-input
          type="password"
          id="text-password"
          aria-describedby="password-help-block1"
          v-model="form.password"
        ></b-form-input>
        <b-form-text id="password-help-block1">
          Your password must be 8-20 characters long, contain letters and
          numbers, and must not contain spaces, special characters, or emoji.
        </b-form-text>
      </b-form-group>

      <b-form-group
        id="input-group-4"
        label="Password Check:"
        label-for="input-4"
      >
        <b-form-input
          type="password"
          id="text-password"
          aria-describedby="password-help-block2"
          v-model="form.passwordCheck"
          class="mb-2"
        ></b-form-input>
        <b-form-text id="password-help-block2"> </b-form-text>
      </b-form-group>

      <b-form-group
        id="input-group-5"
        label-for="input-5"
        class="mb-2"
        style="display: none"
      >
        <b-form-input id="input-5" v-model="form.token"></b-form-input>
      </b-form-group>

      <b-button type="submit" variant="primary">Submit</b-button>
    </b-form>
  </div>
</template>

<script>
import { join } from '@/api/auth';

export default {
  data() {
    return {
      form: {
        email: '',
        name: '',
        password: '',
        passwordCheck: '',
        token: '',
      },
      show: true,
    };
  },
  methods: {
    async onSubmit(event) {
      event.preventDefault();

      if (this.form.passwordCheck !== this.form.password) {
        alert('비밀번호를 확인해주세요.');
        return;
      }
      try {
        const { status } = await join(this.form, this.form.token);

        if (status == 201) {
          alert('가입에 성공했습니다');
          await this.$router.push('/');
          return;
        }
      } catch (e) {
        alert(e.response.data.message);
      }
    },
  },
  created() {
    this.form.email = this.$route.params.email;
    this.form.token = this.$route.params.token;
  },
};
</script>
