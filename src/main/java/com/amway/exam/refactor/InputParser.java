package com.amway.exam.refactor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析用户输入
 */
public class InputParser {

    // 匹配完整表达式： 1.2 + 3
    private static final Pattern FULL_EXPRESSION = Pattern.compile("^([\\d\\.]+)\\s*([\\+\\-\\*\\/])\\s*([\\d\\.]+)$");

    // 匹配后续表达式： + 3
    private static final Pattern NEXT_EXPRESSION = Pattern.compile("^([\\+\\-\\*\\/])\\s*([\\d\\.]+)$");

    public static ParsedInput parseFirst(String input) {
        Matcher m = FULL_EXPRESSION.matcher(input.replace(" ", ""));
        if (m.matches()) {
            double n1 = Double.parseDouble(m.group(1));
            String symbol = m.group(2);
            double n2 = Double.parseDouble(m.group(3));
            return new ParsedInput(n1, MathOperation.fromSymbol(symbol), n2);
        }
        throw new CalculatorException("输入格式错误，首个表达式应如: 1+2");
    }

    public static ParsedInput parseNext(String input) {
        Matcher m = NEXT_EXPRESSION.matcher(input.replace(" ", ""));
        if (m.matches()) {
            String symbol = m.group(1);
            double n2 = Double.parseDouble(m.group(2));
            return new ParsedInput(null, MathOperation.fromSymbol(symbol), n2);
        }
        throw new CalculatorException("输入格式错误，后续表达式应如: +2");
    }

    // DTO
    public static class ParsedInput {
        public final Double firstNum; // Optional
        public final MathOperation op;
        public final double secondNum;

        public ParsedInput(Double firstNum, MathOperation op, double secondNum) {
            this.firstNum = firstNum;
            this.op = op;
            this.secondNum = secondNum;
        }
    }
}
