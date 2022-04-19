package net.jeebiz.boot.plugin.api.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.BeanUtils;

//JDK动态代理实现InvocationHandler接口
public class JdkProxy implements InvocationHandler {

    private Object target;//需要代理的目标对象

    public JdkProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("JDK动态代理，监听开始！");
        Object result = method.invoke(target, args);
        System.out.println("JDK动态代理，监听结束！");
        return result;
    }


    //定义获取代理对象方法
    public static Object getJDKProxy(Object targetObject) {
        //JDK动态代理只能针对实现了接口的类进行代理，newProxyInstance 函数所需参数就可看出
        return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass().getInterfaces(), new JdkProxy(targetObject));
    }

    // 定义获取代理对象方法
    public static <T> T getJDKProxy(Class<T> className) {
        JdkProxy target = new JdkProxy(BeanUtils.instantiateClass(className));
        //JDK动态代理只能针对实现了接口的类进行代理，newProxyInstance 函数所需参数就可看出
        return (T) Proxy.newProxyInstance(className.getClassLoader(), className.getInterfaces(), target);
    }

}