class EventBus {

    // // 外部调用此函数实例化
    // static getInstance() {
    //   if (!EventBus.instance) {
    //     EventBus.instance = new EventBus()
    //   }
    //   return EventBus.instance
    // }

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
        console.debug(' publish ', eventName, data, this.listeners)
        if (this.listeners[eventName]) {
            this.listeners[eventName](data);
        }
    }
}

export const eventBus = () => {
    return  new EventBus()
}


export class EventName {
    static addConversationToList = 'addConversationToList'
    static refreshMyConversation = 'refreshMyConversation'
    static initOpenedConversation = 'initOpenedConversation'
    static openConversation = 'openConversation'
    static updateOrDeleteConversation = 'updateOrDeleteConversation'
    static openChooseConversation = 'openChooseConversation'
    static openMsg = 'openMsg'
    static changeIMConfig = 'changeIMConfig'
    static wsAddMsg = 'wsAddMsg'
    static wsRevokeMsg = 'wsRevokeMsg'
}
