package com.oc.auth;

/**
 * @author chuangyeifang
 */
public class UserStore {
    private static ThreadLocal<WaiterInfo> threadLocal = new ThreadLocal<>();

    public static WaiterInfo get() {
        return threadLocal.get();
    }

    public static void set(WaiterInfo authInfo) {
        threadLocal.set(authInfo);
    }

    public static void remove() {
        threadLocal.remove();
    }
}
