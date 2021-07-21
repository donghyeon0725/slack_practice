import { getInstanceWithAuth } from '@/api/index';

const instance = getInstanceWithAuth();

// 보드 리스트 요청
function getBoards(id) {
  return instance.get(`board/${id}`);
}

// 보드 생성
function createBoard(boardData) {
  return instance.post('board', boardData);
}

// 보드 수정
function modifyBoard(boardData) {
  return instance.patch('board', boardData);
}

// 보드 삭제
function deleteBoard(id) {
  return instance.delete(`board/${id}`);
}

// 배너 수정
function updateBoardBanner(boardJsonData) {
  let form_data = new FormData();

  for (let key in boardJsonData) {
    form_data.append(key, boardJsonData[key]);
  }

  return instance.patch('board/banner', form_data);
}

export { getBoards, createBoard, modifyBoard, deleteBoard, updateBoardBanner };
