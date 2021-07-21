import Vue from 'vue';
import SockJS from 'sockjs-client';
import globalBus from '/src/socket/event-bus';
import channel from '@/socket/channels';

/**
 * 웹 소켓 통신을 위해서
 * SocketJS를 사용하고 있습니다.
 *
 * 기본적으로 웹 소켓을 사용하기 위해
 *
 * 1. 연결 (초기화)
 * 2. 구독 (메세지를 받았을 때 동작을 수행할 클래스)
 * 3. 구독 해제
 * 위 세가지 동작을 제공합니다.
 *
 * init은 vue 어플리케이션이 생성 될 때 1번만 하면 되기 때문에 App.vue 의 created 훅에서 수행합니다.
 * */
class RealTimeClient {
  constructor() {
    this.serverUrl = null;
    this.channel = channel;
    this.token = null;
    this.socket = null;
    // If the client is authenticated through real time connection or not
    this.authenticated = false;
    this.loggedOut = false;
    this.$bus = new Vue();
    this.subscribeQueue = {
      /* channel: [handler1, handler2] */
    };
    this.unsubscribeQueue = {
      /* channel: [handler1, handler2] */
    };
  }
  /* 초기화 => 토큰, 서버 url 발급 받은 정보를 기반으로 connect 수행 */
  init(serverUrl, token) {
    console.log(serverUrl);
    console.log(token);
    if (this.authenticated) {
      console.warn('[RealTimeClient] WS connection already authenticated.');
      return;
    }
    console.log('[RealTimeClient] Initializing');
    this.serverUrl = serverUrl;
    this.token = token;
    this.connect();
  }
  /* 상태를 비웁니다. & 소켓 close */
  logout() {
    console.log('[RealTimeClient] Logging out');
    this.subscribeQueue = {};
    this.unsubscribeQueue = {};
    this.authenticated = false;
    this.loggedOut = true;
    this.socket && this.socket.close();
  }
  /* 연결 & 기본적인 4자기 이벤트 handling */
  connect() {
    console.log('[RealTimeClient] Connecting to ' + this.serverUrl);
    // 더블 슬래시를 사용하면, 스프링 시큐리티 정책 상 거부 당합니다.
    this.socket = new SockJS(
      process.env.VUE_APP_SOCKET_URL + this.serverUrl + '?token=' + this.token,
    );
    this.socket.onopen = () => {
      // Once the connection established, always set the client as authenticated
      this.authenticated = true;
      this._onConnected();
    };
    this.socket.onmessage = event => {
      this._onMessageReceived(event);
    };
    this.socket.onerror = error => {
      this._onSocketError(error);
    };
    this.socket.onclose = event => {
      this._onClosed(event);
    };
  }
  /* 구독 */
  subscribe(channel, handler) {
    /* 연결이 불안정하면, 연결 큐에 정보를 keep => 이후 다시 요청 */
    if (!this._isConnected()) {
      this._addToSubscribeQueue(channel, handler);
      return;
    }
    // 구독 액션을 보냅니다.
    const message = {
      action: 'subscribe',
      channel,
    };
    this._send(message);
    this.$bus.$on(this._channelEvent(channel), handler);
    console.log('[RealTimeClient] Subscribed to channel ' + channel);
  }
  /* 구독 해제 */
  unsubscribe(channel, handler) {
    // Already logged out, no need to unsubscribe
    if (this.loggedOut) {
      return;
    }

    if (!this._isConnected()) {
      this._addToUnsubscribeQueue(channel, handler);
      return;
    }
    const message = {
      action: 'unsubscribe',
      channel,
    };
    this._send(message);
    this.$bus.$off(this._channelEvent(channel), handler);
    console.log('[RealTimeClient] Unsubscribed from channel ' + channel);
  }
  /* 구독 전체 해제 */
  unsubscribeAll(channel, handler) {
    // Already logged out, no need to unsubscribe
    if (this.loggedOut) {
      return;
    }

    if (!this._isConnected()) {
      this._addToUnsubscribeQueue(channel, handler);
      return;
    }
    const message = {
      action: 'unsubscribeAll',
      channel,
    };
    this._send(message);
    this.$bus.$off(this._channelEvent(channel), handler);
    console.log('[RealTimeClient] Unsubscribed All from channel ' + channel);
  }
  _isConnected() {
    return this.socket && this.socket.readyState === SockJS.OPEN;
  }
  _onConnected() {
    globalBus.$emit('RealTimeClient.connected');
    console.log('[RealTimeClient] Connected');

    // Handle subscribe and unsubscribe queue
    this._processQueues();
  }
  _onMessageReceived(event) {
    const message = JSON.parse(event.data);
    console.log('[RealTimeClient] Received message', message);
    console.log(message);

    // 채널 메세지 인 경우 이벤트를 발생시킴
    console.log(message.channel);
    if (message.channel) {
      console.log('채널 메세지를 받음');
      this.$bus.$emit(
        this._channelEvent(message.channel),
        JSON.parse(message.payload),
      );
    }
  }
  _send(message) {
    this.socket.send(JSON.stringify(message));
  }
  _onSocketError(error) {
    console.error('[RealTimeClient] Socket error', error);
  }
  _onClosed(event) {
    console.log('[RealTimeClient] Received close event', event);
    if (this.loggedOut) {
      // Manually logged out, no need to reconnect
      console.log('[RealTimeClient] Logged out');
      globalBus.$emit('RealTimeClient.loggedOut');
    } else {
      // Temporarily disconnected, attempt reconnect
      console.log('[RealTimeClient] Disconnected');
      globalBus.$emit('RealTimeClient.disconnected');

      setTimeout(() => {
        console.log('[RealTimeClient] Reconnecting');
        globalBus.$emit('RealTimeClient.reconnecting');
        this.connect();
      }, 1000);
    }
  }
  _channelEvent(channel) {
    return 'channel:' + channel;
  }
  _processQueues() {
    console.log('[RealTimeClient] Processing subscribe/unsubscribe queues');

    // Process subscribe queue
    const subscribeChannels = Object.keys(this.subscribeQueue);
    subscribeChannels.forEach(channel => {
      const handlers = this.subscribeQueue[channel];
      handlers.forEach(handler => {
        this.subscribe(channel, handler);
        this._removeFromQueue(this.subscribeQueue, channel, handler);
      });
    });

    // Process unsubscribe queue
    const unsubscribeChannels = Object.keys(this.unsubscribeQueue);
    unsubscribeChannels.forEach(channel => {
      const handlers = this.unsubscribeQueue[channel];
      handlers.forEach(handler => {
        this.unsubscribe(channel, handler);
        this._removeFromQueue(this.unsubscribeQueue, channel, handler);
      });
    });
  }
  _addToSubscribeQueue(channel, handler) {
    console.log(
      '[RealTimeClient] Adding channel subscribe to queue. Channel: ' + channel,
    );
    // To make sure the unsubscribe won't be sent out to the server
    this._removeFromQueue(this.unsubscribeQueue, channel, handler);
    const handlers = this.subscribeQueue[channel];
    if (!handlers) {
      this.subscribeQueue[channel] = [handler];
    } else {
      handlers.push(handler);
    }
  }
  _addToUnsubscribeQueue(channel, handler) {
    console.log(
      '[RealTimeClient] Adding channel unsubscribe to queue. Channel: ' +
        channel,
    );
    // To make sure the subscribe won't be sent out to the server
    this._removeFromQueue(this.subscribeQueue, channel, handler);
    const handlers = this.unsubscribeQueue[channel];
    if (!handlers) {
      this.unsubscribeQueue[channel] = [handler];
    } else {
      handlers.push(handlers);
    }
  }
  _removeFromQueue(queue, channel, handler) {
    const handlers = queue[channel];
    if (handlers) {
      let index = handlers.indexOf(handler);
      if (index > -1) {
        handlers.splice(index, 1);
      }
    }
  }
}

export default new RealTimeClient();
