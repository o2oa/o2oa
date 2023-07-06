class EventBus {
  constructor() {
    this.listeners = {};
  }

  subscribe(eventName, callback) {
    this.listeners[eventName] = callback;
  }

  unsubscribe(eventName, callback) {
    this.listeners[eventName] = null;
  }

  publish(eventName, data) {
    if (this.listeners[eventName]) {
      this.listeners[eventName](data);
    }
  }
}


export {
  EventBus
}