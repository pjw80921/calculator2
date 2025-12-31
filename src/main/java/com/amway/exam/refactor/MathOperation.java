package com.amway.exam.refactor;

import java.util.function.DoubleBinaryOperator;
import java.util.stream.Stream;

/**
 * 数学运算符枚举
 * 策略模式：每个枚举实例自带计算逻辑
 */
public enum MathOperation {
    ADD("+", (a, b) -> a + b),
    SUBTRACT("-", (a, b) -> a - b),
    MULTIPLY("*", (a, b) -> a * b),
    DIVIDE("/", (a, b) -> a / b);

    private final String symbol;
    private final DoubleBinaryOperator operator;

    MathOperation(String symbol, DoubleBinaryOperator operator) {
        this.symbol = symbol;
        this.operator = operator;
    }

    public double apply(double a, double b) {
        return operator.applyAsDouble(a, b);
    }

    public String getSymbol() {
        return symbol;
    }

    public static MathOperation fromSymbol(String symbol) {
        return Stream.of(values())
                .filter(op -> op.symbol.equals(symbol))
                .findFirst()
                .orElse(null);
    }
}
