package com.ange.ioc_api;

/**
 * Created by ange on 2017/9/19.
 */

public interface ViewInject<T> {
    /**
     *
     * @param t 宿主
     * @param source view/activity
     */
    void inject(T t,Object source);
}
