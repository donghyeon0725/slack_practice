<template>
  <div class="container" :id="id" :class="list.length > 0 ? '' : 'disable'">
    <div class="section">
      <ul class="total">
        <li
          class="item"
          :key="index"
          v-for="(item, index) in list"
          @click="click(item)"
        >
          {{ item[search] }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AutoComplete',
  data() {
    return {
      defualtOption: {
        width: 500,
        top: 250,
        left: 250,
      },
      init: false,
    };
  },
  props: {
    list: {
      type: Array,
      required: true,
    },
    search: {
      type: String,
      required: true,
    },
    option: {
      type: Object,
      required: false,
    },
    id: {
      type: String,
      required: true,
    },
  },
  methods: {
    click(item) {
      this.$emit('update', item);
      document.getElementById(this.id).classList.add('disable');
    },
  },
  mounted() {
    if (!this.init) {
      Object.assign(this.defualtOption, this.option);

      let content = document.getElementById(this.id);

      content.style.width = `${this.defualtOption.width}px`;
      content.style.top = `${this.defualtOption.top}px`;
      content.style.left = `${this.defualtOption.left}px`;

      this.init = !this.init;
    }
  },
};
</script>

<style scoped>
.disable {
  display: none;
}

.container {
  position: absolute;
  width: 400px;
  padding: 0;
  margin: 0;
  top: 50px;
  left: 0px;
  background-color: white;
  border-radius: 3px;
  box-shadow: 0 4px 6px 0 rgb(32 33 36 / 28%);
  overflow: hidden;
}
.section {
  padding: 5px 10px 5px 10px;
}
ul {
  padding: 0;
  margin: 0;
  list-style: none;
}

li {
  display: block;
  padding: 20px 10px 20px 10px;
  text-align: left;
  vertical-align: middle;
  font-weight: 600;
}

li:hover {
  background-color: #f6f6f6;
  cursor: pointer;
}
</style>
