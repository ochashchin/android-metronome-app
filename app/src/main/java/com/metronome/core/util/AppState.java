package com.metronome.core.util;

import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.CopyOnWriteArrayList;

public class AppState<T> extends MutableLiveData<T> {
    public interface Observer<T> {
        void onChanged(T value);
    }

    private final CopyOnWriteArrayList<Observer<T>> observers = new CopyOnWriteArrayList<>();
    private volatile T value;

    public AppState(T initial) {
        this.value = initial;
    }

    public void setValue(T v) {
        this.value = v;
        for (Observer<T> o : observers) {
            o.onChanged(v);
        }
    }

    public T getValue() {
        return value;
    }

    public void observe(Observer<T> observer) {
        observers.add(observer);
        observer.onChanged(value);
    }
}
