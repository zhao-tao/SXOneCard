package com.sxonecard.base;

/**
 * Created by HeQiang on 2016/11/6.
 */

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xialo on 2016/6/28.
 */
public class RxBus {
    private static RxBus instance;
    private ConcurrentHashMap<Object, List<Subject>> maps = new ConcurrentHashMap<>();
    //Observable<Navigation> navigationObservable;
    /*
    navigationObservable = RxBus.get().register("home", Navigation.class);
        navigationObservable.subscribe(new Action1<Navigation>() {
            @Override
            public void call(Navigation navigation) {
                Integer index = (Integer) navigation.get("index");
                setRadio(index);
            }
        });
     */

    private RxBus() {
    }

    public static RxBus get() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> clazz) {
        List<Subject> subjects = maps.get(tag);
        if (subjects == null) {
            subjects = new ArrayList<>();
            maps.put(tag, subjects);
        }
        Subject<T, T> subject = PublishSubject.create();
        subject.observeOn(AndroidSchedulers.mainThread());
        subjects.add(subject);
        return subject;
    }

    @SuppressWarnings("unchecked")
    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<Subject> subjects = maps.get(tag);
        if (subjects != null) {
            subjects.remove(observable);
            if (subjects.isEmpty()) {
                maps.remove(tag);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void post(@NonNull Object o) {
        post(o.getClass().getSimpleName(), o);
    }

    @SuppressWarnings("unchecked")
    public void post(@NonNull Object tag, @NonNull Object o) {
        List<Subject> subjects = maps.get(tag);
        if (subjects != null && !subjects.isEmpty()) {
            for (Subject s : subjects) {
                s.onNext(o);
            }
        }
    }
}
