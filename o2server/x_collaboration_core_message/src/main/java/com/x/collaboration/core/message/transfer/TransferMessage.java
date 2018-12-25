//package com.x.collaboration.core.message.transfer;
//
//import java.util.Date;
//
//import com.x.base.core.gson.GsonPropertyObject;
//import com.x.base.core.gson.XGsonBuilder;
//import com.x.collaboration.core.message.BaseMessage;
//import com.x.collaboration.core.message.dialog.DialogMessage;
//import com.x.collaboration.core.message.notification.NotificationMessage;
//import com.x.collaboration.core.message.notification.NotificationType;
//import com.x.collaboration.core.message.operation.OperationMessage;
//
//public class TransferMessage extends GsonPropertyObject {
//
//	private String data;
//
//	public String getData() {
//		return data;
//	}
//
//	public void setData(String data) {
//		this.data = data;
//	}
//
//	public void setData(BaseMessage message) throws Exception {
//		this.data = XGsonBuilder.toJson(message);
//	}
//
//	public String getPerson() {
//		return XGsonBuilder.instance().fromJson(this.data, BaseMessage.class).getPerson();
//	}
//
//	public Date getDateTime() {
//		return XGsonBuilder.instance().fromJson(this.data, BaseMessage.class).getDateTime();
//	}
//
//	public String getType() {
//		return XGsonBuilder.instance().fromJson(this.data, BaseMessage.class).getType();
//	}
//
//	public NotificationType getMessageType() {
//		return XGsonBuilder.instance().fromJson(this.data, BaseMessage.class).getMessageType();
//	}
//
//	public BaseMessage unwrap() {
//		switch (this.getMessageType()) {
//		case notification:
//			return XGsonBuilder.instance().fromJson(this.data, NotificationMessage.class);
//		case operation:
//			return XGsonBuilder.instance().fromJson(this.data, OperationMessage.class);
//		case dialog:
//			return XGsonBuilder.instance().fromJson(this.data, DialogMessage.class);
//		default:
//			return null;
//		}
//	}
//
//	public NotificationMessage unwrapNotificationMessage() {
//		return XGsonBuilder.instance().fromJson(data, NotificationMessage.class);
//	}
//
//	public DialogMessage unwrapDialogMessage() {
//		return XGsonBuilder.instance().fromJson(data, DialogMessage.class);
//	}
//
//	public OperationMessage unwrapOperationMessage() {
//		return XGsonBuilder.instance().fromJson(data, OperationMessage.class);
//	}
//
//}
