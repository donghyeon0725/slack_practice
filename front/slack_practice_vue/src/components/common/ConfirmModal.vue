<template>
  <div>
    <b-button @click="$bvModal.show(this.id)">Open Modal</b-button>

    <b-modal :id="id">
      <template>
        <h5>{{ header }}</h5>
      </template>

      <template #modal-footer="{ ok, cancel }">
        <b-button size="sm" variant="success" @click="ok()"> 확인 </b-button>
        <b-button size="sm" variant="danger" @click="cancel()"> 닫기 </b-button>
      </template>
    </b-modal>
  </div>
</template>

<script>
export default {
  name: 'ConfirmModal',
  data() {
    return {
      id: 'confirm-modal-scoped',
    };
  },
  props: {
    header: {
      type: String,
      required: true,
    },
  },
  methods: {
    ok() {
      this.$emit('ok');
    },
    cancel() {
      this.$emit('cancel');
    },
  },
};
</script>

<style scoped></style>
