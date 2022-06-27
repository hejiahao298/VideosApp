package com.hjh.excpetion;

/**
 * 自定义异常处理
 */
public class IllegalTokenException extends RuntimeException{
    public IllegalTokenException(String message) {
        super(message);
    }
}
