package com.ange.ioc_api;

import android.app.Activity;
import android.view.View;

/**
 *
 * Created by zhy on 16/4/22.
 */
public class ViewInjector
{
    private static final String SUFFIX = "$$ViewInject";

    /**
     * 静态方法：先找出该activity的代理类名通过反射生成代理类实例
     * @param activity
     */
    public static void injectView(Activity activity)
    {
        ViewInject proxyActivity = findProxyActivity(activity);
        proxyActivity.inject(activity, activity);
    }

    public static void injectView(Object object, View view)
    {
        ViewInject proxyActivity = findProxyActivity(object);
        proxyActivity.inject(object, view);
    }

    private static ViewInject findProxyActivity(Object activity)
    {
        try
        {
            Class clazz = activity.getClass();
            Class injectorClazz = Class.forName(clazz.getName() + SUFFIX);
            return (ViewInject) injectorClazz.newInstance();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("can not find %s , something when compiler.", activity.getClass().getSimpleName() + SUFFIX));
    }
}
