<template>
  <div style="width: 300px">
    <b-form @submit="onSubmit" @reset="onReset" v-if="show">
      <b-form-group
        id="input-group-1"
        label="Email address:"
        label-for="input-1"
        :description="emailValidation"
        class="mb-2"
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

      <b-button type="submit" variant="primary">Submit</b-button>
    </b-form>
  </div>
</template>

<script>
import { signupMail } from '@/api/auth';
import { validateEmail } from '@/util/validation';

export default {
  data() {
    return {
      form: {
        email: '',
      },
      description: '',
      show: true,
    };
  },
  methods: {
    async onSubmit(event) {
      event.preventDefault();
      try {
        const { status } = await signupMail(this.form.email);

        if (status == 200)
          alert(
            `${this.form.email}으로 메일을 전송 했습니다. 확인 부탁드립니다.`,
          );
        await this.$router.push('/');
        return;
      } catch (e) {
        alert(e.response.data.message);
      }
    },
    onReset(event) {
      event.preventDefault();
      // Reset our form values
      this.form.email = '';
      this.form.checked = [];
      // Trick to reset/clear native browser form validation state
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
