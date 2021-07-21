<template>
  <b-dropdown
    size="sm"
    variant="link-dark"
    ref="toolBox"
    toggle-class="text-decoration-none"
    no-caret
    class="c-dropdown"
    style="max-width: inherit; word-break: break-all"
  >
    <template #button-content>
      <b-icon
        icon="exclamation"
        class="user-icon"
        v-if="isDeleted(chat)"
      ></b-icon
      >{{ chat.description }}</template
    >
    <b-dropdown-item @click="deleteObject(chat)" v-if="!isDeleted(chat)"
      >삭제</b-dropdown-item
    >
    <b-dropdown-item>가리기</b-dropdown-item>
  </b-dropdown>
</template>

<script>
export default {
  name: 'ToolBox.vue',
  props: {
    chat: {
      type: Object,
      required: true,
    },
  },
  methods: {
    deleteObject(chat) {
      this.$emit('delete', chat);
    },
    isDeleted(chat) {
      return chat.state == 'DELETED';
    },
  },
};
</script>

<style scoped>
.c-dropdown {
  position: relative;
  padding: 0;
}
</style>
