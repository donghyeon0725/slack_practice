import { getInstanceWithAuth } from '@/api/index';

const instance = getInstanceWithAuth();

// 보드 리스트 요청
function getCards(boardId) {
  return instance.get(`card/${boardId}`);
}

// 카드 생성하기
function createCard(cardJsonData) {
  let form_data = new FormData();

  for (let key in cardJsonData) {
    form_data.append(key, cardJsonData[key]);
  }

  return instance.post('card', form_data);
}

// 카드 수정하기
function modifyCard(cardJsonData) {
  let form_data = new FormData();

  for (let key in cardJsonData) {
    form_data.append(key, cardJsonData[key]);
  }

  return instance.patch('card', form_data);
}

// 카드 삭제하기
function deleteCard(cardId) {
  return instance.delete(`card/${cardId}`);
}

// 댓글 생성하기
function createReply(replyData) {
  return instance.post('card/replies', replyData);
}

// 댓글 삭제
function deleteReply(replyId) {
  return instance.delete(`card/replies/${replyId}`);
}

// 카드 위치 이동하기
function updateCardPosition(cardDummy) {
  return instance.patch('card/position', cardDummy);
}

export {
  getCards,
  createCard,
  modifyCard,
  deleteCard,
  createReply,
  deleteReply,
  updateCardPosition,
};
