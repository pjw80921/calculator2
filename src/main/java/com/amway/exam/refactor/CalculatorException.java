package com.amway.exam.refactor;

/**
 * 自定义业务异常
 */
public class CalculatorException extends RuntimeException {
    public CalculatorException(String message) {
        super(message);
    }
}
