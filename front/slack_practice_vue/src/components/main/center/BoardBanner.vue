<template>
  <div
    class="flex w-full h-screen items-center justify-center text-center"
    style="
      cursor: pointer;
      transition: all 0.7s cubic-bezier(0.1, -0.6, 0.2, 0) 0s;
    "
    @dragover="dragover"
    @dragleave="dragleave"
    @drop="drop"
  >
    <div
      class="cover-wrapper"
      v-b-tooltip.hover
      title="place image file in here. if you wanna change this cover"
    >
      <b-img
        :src="
          this.$isEmpty(path)
            ? 'https://mblogthumb-phinf.pstatic.net/MjAxOTAzMDZfMTM3/MDAxNTUxODE0NTI0NDkz.RvXlYv6p5xPwXrOIPfABgFuiknuTn0iFZNuUguVYc_og.ii04J6D96C6FKQSXQzy9DABBNk7vNuU343nmAquhmZgg.JPEG.mong728/%25EC%25B4%2588%25EA%25B3%25A0%25ED%2599%2594%25EC%25A7%2588_%25EC%25BB%25B4%25ED%2593%25A8%25ED%2584%25B0_%25EB%25B0%25B0%25EA%25B2%25BD%25ED%2599%2594%25EB%25A9%25B4_(7).jpg?type=w800'
            : ref +
              'getImage/' +
              path.replaceAll('\\', '@').replaceAll('/', '@')
        "
        fluid
        alt="Fluid image"
      ></b-img>
    </div>
    <div class="p-12">
      <input
        type="file"
        name="fields[assetsFieldHandle][]"
        id="assetsFieldHandle"
        class="w-px h-px opacity-0 overflow-hidden absolute"
        @change="onChange"
        @click="preventEvent"
        ref="file"
        accept=".pdf,.jpg,.jpeg,.png"
        style="display: none"
      />

      <!--<label for="assetsFieldHandle" class="block cursor-pointer file-label">
      </label>-->
      <!--<div class="mt-4" v-cloak v-if="this.file != null" key="file1">
        <div
          class="info text-sm p-1"
          style="cursor: pointer"
          @click="remove()"
          title="Remove file"
        >
          파일 제거
        </div>
      </div>-->
    </div>
  </div>
</template>

<script>
import { updateBoardBanner } from '@/api/board';

export default {
  name: 'BoardBanner',
  props: {
    path: {
      type: String,
      required: false,
    },
    auth: {
      type: Boolean,
      required: false,
    },
  },
  delimiters: ['${', '}'], // Avoid Twig conflicts
  data: function () {
    return {
      ref: process.env.VUE_APP_API_URL,
      form: {
        id: '',
        file: null, // Store our uploaded file
      },
    };
  },
  methods: {
    preventEvent(event) {
      event.preventDefault();
    },
    onChange() {
      this.form.file = this.$refs.file.files[0];
    },
    remove() {
      if (!this.auth) return;
      this.form.file = null;
    },
    // drag over 이벤트
    dragover(event) {
      if (!this.auth) return;
      event.preventDefault();
      event.currentTarget.classList.add('bg-green');
    },
    // 개체가 바깥으로 빠졌을 때 발생하는 이벤트 => 애니메이션 초기화
    dragleave(event) {
      if (!this.auth) return;
      // Clean up
      event.currentTarget.classList.remove('bg-green');
    },
    // 떨어졌을 때 이벤트 => 업로드가 완료 되었다면, 다시 class 제거
    async drop(event) {
      if (!this.auth) return;
      event.preventDefault();
      this.$refs.file.files = event.dataTransfer.files;
      this.onChange(); // Trigger the onChange event manually

      if (event.dataTransfer.files.length <= 0) {
        event.currentTarget.classList.remove('bg-green');
        return;
      }

      let target = event.currentTarget;
      target.classList.add('bg-green');

      try {
        this.form.id = this.$route.params.boardId;
        let { data } = await updateBoardBanner(this.form);

        this.$defualtToast('배너 변경');

        let boardId = this.$route.params.boardId;
        let board = this.$store.state.page.boards.filter(
          s => s.id == boardId,
        )[0];
        board.bannerPath = data.bannerPath;
        console.log(data);
      } catch (e) {
        this.$defualtToast('배너 오류', { type: 'error' });
        console.log(e);
      }

      target.classList.remove('bg-green');
    },
  },
};
</script>

<style scoped>
[v-cloak] {
  display: none;
}
img {
  display: block;
  object-fit: cover;
  border-radius: 0px;
  width: 100%;
  object-position: center 40%;
  height: 20vh;
}
.file-label {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
}

.bg-green {
  background-color: #42b983;
  opacity: 0.2;
}
</style>
