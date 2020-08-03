
package com.oc.exception;

/**
 * @author chuangyeifang
 */
public class CannotCalculateSizeException extends Exception {

    public static final long serialVersionUID = 1754096832201752388L;

    public CannotCalculateSizeException(Object obj) {
        super("不能计算当前对象大小：" + obj.getClass() + ".");
    }
}
