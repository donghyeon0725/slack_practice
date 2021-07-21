<template>
  <div :id="id" class="markdown-section">
    <div class="mark-container">
      <!--The main App-->
      <span v-html="previewText"></span>
    </div>
  </div>
</template>

<script>
import marked from 'marked';

export default {
  name: 'Markdown',
  props: {
    option: {
      type: Object,
      required: false,
    },
    // 이 곳에 원하는 텍스트를 넣습니다.
    mdText: {
      type: String,
      required: true,
    },
    visible: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {
      id: 'markdown-area',
      defualtOption: {
        width: '0px',
        height: '0px',
        left: '0px',
        top: '0px',
        backgroundColor: '#ced4da',
        position: 'absolute',
        display: 'block',
        overflow: 'auto',
        borderRadius: '7px',
        borderColor: '#ced4da',
        opacity: 0.7,
      },
      container: {
        paddingLeft: '20px',
        paddingRight: '20px',
        paddingTop: '30px',
      },
    };
  },
  methods: {
    markdownRender() {
      if (this.visible) {
        // 사용자 설정과 기본설정 합
        Object.assign(this.defualtOption, this.option);

        let content = document.getElementById(this.id);
        let container = document.querySelector(`#${this.id} > .mark-container`);

        console.log(container);

        if (this.$isNotEmpty(content))
          // 스타일 설정과 합
          Object.assign(content.style, this.defualtOption);

        if (this.$isNotEmpty(container)) {
          console.log(container);
          Object.assign(container.style, this.defualtOption.container);
        }
      } else {
        let content = document.getElementById(this.id);
        if (this.$isNotEmpty(content)) content.style.display = 'none';
      }
    },
  },
  computed: {
    previewText() {
      marked.setOptions({
        renderer: new marked.Renderer(),
        gfm: true,
        tables: true,
        breaks: true,
        pedantic: false,
        sanitize: true,
        smartLists: true,
        smartypants: false,
      });
      return marked(this.mdText);
    },
  },
  watch: {
    // prop을 watch 하려면 deep 모드를 켜야한다.
    visible: {
      immediate: true,
      deep: true,
      handler() {
        // state의 값을 변화 시키기
        this.markdownRender();
      },
    },
  },
  mounted() {
    this.markdownRender();
  },
};
</script>

<style scoped>
.markdown-section {
  display: none;
}
</style>
