package jiguang.chat.model;

import android.support.annotation.NonNull;


public class ParentLinkedHolder<T> {
    public T item;
    private ParentLinkedHolder<T> parentLinkedHolder;

    public ParentLinkedHolder(@NonNull T item) {
        this.item = item;
    }

    public T get() {
        return item;
    }

    public boolean hasParent() {
        return parentLinkedHolder != null;
    }

    public ParentLinkedHolder<T> addParent(ParentLinkedHolder<T> holder) {
        parentLinkedHolder = holder;
        return this;
    }

    public ParentLinkedHolder<T> putParent() {
        ParentLinkedHolder<T> holder = parentLinkedHolder;
        parentLinkedHolder = null;
        return holder;
    }

    @Override
    public String toString() {
        return "ParentLinkedHolder{" +
                "item=" + item +
                ", parentLinkedHolder=" + parentLinkedHolder +
                '}';
    }
}
