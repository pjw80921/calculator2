package com.amway.exam;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 题目：写一个计算器类（Calculator），可以实现两个数的加、减、乘、除运算，并可以进行undo和redo操作
 *
 * 功能：
 *  1. 实现模拟计算器的功能，可以实现两个数的加、减、乘、除运算
 *  2. 可以进行redo操作，重新执行上一次输入并输出结果
 *  3. 可以进行undo操作，回退到上上次操作的结束值，可多次undo，但是undo不可撤销
 *  4. 可以重新复位，并重新输入计数
 *  5. 可以手动关闭程序
 *
 * 核心方法 {@link com.amway.exam.Calculator#calculator()}
 *
 * 亮点：
 *  1. 工程化思想编写
 *  2. 抽象数据模型
 *  3. 代码结构良好，每个功能分支抽取成单独的方法
 *  4. 基本没有重复的代码量
 *
 *
 * 存储容器，存储每次输入的数据
 * 输入1+2，数据存储:
 * +------+
 * | 1+2  |
 * +------+
 * 输入*3:
 * +------+------+
 * | 1+2  | 3*3  |
 * +------+------+
 * 输入r(redo):
 * +------+------+------+
 * | 1+2  | 3*3  | 9*3  |
 * +------+------+------+
 * 输入u(undo):
 * +------+------+
 * | 1+2  | 3*3  |
 * +------+------+
 *
 *
 * @author pengjunwei
 */
public class Calculator {


    private final Scanner input = new Scanner(System.in);
    /**
     * 存每次输入的信息的结构体的容器
     */
    private List<CalStruct> cache = new ArrayList<>();
    /**
     * 当前输出的结果
     */
    private double result;
    /**
     * 撤销上次输入，当前只支持撤销一次
     */
    private static final String UNDO = "u";
    /**
     * 重新操作上一次输入
     */
    private static final String REDO = "r";
    /**
     * 复位
     */
    private static final String RESET = "c";
    /**
     * 结束程序
     */
    private static final String EXIT = "exit";

    private Calculator() {}

    private static Calculator calculator = new Calculator();

    /**
     * 单例
     */
    public static Calculator getInstance() {
        return calculator;
    }

    public void calculator() {

        // 这一层for是为了输入复位
        for (;;) {
            // 是否是首次输入
            boolean isFirstInput = true;

            // 第一次输入，这一层用for是为了在输入错误的时候可以重新输入
            for (; ; ) {
                println("请输出表达式，仅支持两个数的加减乘除，例如1.2+5，系统会计算并输出6.2。");
                try {
                    String input = input();
                    this.calculator(isFirstInput, input);
                } catch (BizException e) {
                    println(e.getMessage());
                    continue;
                }
                isFirstInput = false;
                break;
            }

            // 后续输入，可以循环输入
            for (; ; ) {
                println("请继续输入计算，例如:+2，会计算并输出'上个输出值'+2的值。输入u回退到上个操作，输入r再次重复上个操作，输入c清空历史操作，输入exit退出程序。");
                try {
                    String input = input();
                    // 输入复位，重置
                    if (RESET.equals(input)) {
                        this.result = 0;
                        this.cache.clear();
                        break;
                    }
                    this.calculator(isFirstInput, input);
                } catch (BizException e) {
                    println(e.getMessage());
                    continue;
                }
            }
        }
    }

    /**
     * 一次输入和输出
     */
    public void calculator(boolean isFirstInput, String input) throws BizException {
        // 解析
        // undo：回退到上一次输入，并输出上一次的结果
        if (UNDO.equals(input)) {
            if (this.cache.size() <= 1) {
                throw new BizException("第一次输入不支持undo,如果要撤销请输入：c, error_id=011");
            }
            final CalStruct lastStruct = this.cache.get(this.cache.size() - 1);
            final double undoResult = lastStruct.firstNum;
            this.result = undoResult;
            this.cache.remove(this.cache.size() - 1);
            println(result);
            return;
        }

        CalStruct struct = null;

        // redo：重新操作上次的输入
        if (REDO.equals(input)) {
            if (this.cache.size() == 0) {
                throw new BizException("第一次操作不支持redo, error_id=012");
            }
            final CalStruct lastStruct = this.cache.get(this.cache.size() - 1);
            struct = new CalStruct(this.result, lastStruct.calSymbol, lastStruct.secondNum);
            // 正常输入
        } else {
            struct = input2Struct(isFirstInput, input);
        }

        if (struct == null) {
            throw new BizException("输入错误，error_id:001");
        }

        // 计算
        final double result = calculate(struct);

        cache.add(struct);
        this.result = result;
        println(result);
    }

    public double calculate(CalStruct struct) {
        return struct.calculate();
    }

    private void println(String message) {
        System.out.println(message);
        System.out.println();
    }

    private void println(double message) {
        System.out.println(message);
        System.out.println();
    }

    /**
     * 解析输入，将字符转化为计算结构体
     */
    private CalStruct input2Struct(boolean isFirstInput, String input) throws BizException {
        int addIdx = input.indexOf(CalStruct.ADD);
        if (addIdx != -1) {
            return parseInput(isFirstInput, input, CalStruct.ADD);
        }

        int minusIdx = input.indexOf(CalStruct.MINUS);
        if (minusIdx != -1) {
            return parseInput(isFirstInput, input, CalStruct.MINUS);
        }

        int timesIdx = input.indexOf(CalStruct.TIMES);
        if (timesIdx != -1) {
            return parseInput(isFirstInput, input, CalStruct.TIMES);
        }

        int divideIdx = input.indexOf(CalStruct.DIVIDE);
        if (divideIdx != -1) {
            return parseInput(isFirstInput, input, CalStruct.DIVIDE);
        }

        return null;
    }

    private CalStruct parseInput(boolean isFirstInput, String input, String symbol) throws BizException {
        String[] split = split(isFirstInput, input, symbol);

        double n1 = 0.0;
        double n2 = 0.0;
        if (isFirstInput) {
            if (split.length != 2) {
                throw new BizException("输入的数据错误,error_id:002");
            }
            try {
                n1 = Double.parseDouble(split[0]);
                n2 = Double.parseDouble(split[1]);
            } catch (NumberFormatException nfe) {
                throw new BizException("数字输入错误, error_id:003");
            }
            return new CalStruct(n1, symbol, n2);
        }

        if (split.length != 1) {
            throw new BizException("输入的数据错误, error_id:004");
        }
        // 将上一次操作的输出作为本次第一个计算值
        n1 = this.result;
        try {
            n2 = Double.parseDouble(split[0]);
        } catch (NumberFormatException nfe) {
            throw new BizException("数字输入错误, error_id:005");
        }
        return new CalStruct(n1, symbol, n2);
    }

    /**
     * 从输入的字符拆分出前位数字和后位数字
     */
    private String[] split(boolean isFirstInput, String str, String symbol) throws BizException {
        final int i = str.indexOf(symbol);
        if (isFirstInput) {
            String[] strs = new String[2];
            strs[0] = str.substring(0, i);
            strs[1] = str.substring(i + 1);
            return strs;
        }
        if (!str.startsWith(symbol)) {
            throw new BizException("输入错误, error_id: 010");
        }
        String[] strs = new String[1];
        strs[0] = str.substring(1);
        return strs;
    }

    /**
     * 获取输入并判断是否为空
     */
    private String input() throws BizException {
        String input = this.input.nextLine();
        if (input == null) {
            throw new BizException("输入不能为空, error_id:006");
        }
        // 消除空格
        input = input.replace(" ", "");
        if ("".equals(input)) {
            throw new BizException("输入不能为空, error_id:007");
        }
        isExit(input);
        return input;
    }

    /**
     * 是否退出程序
     */
    private void isExit(String input) {
        if (EXIT.equals(input)) {
            println("bye");
            System.exit(0);
        }
    }

    /**
     * 存储每一次输入的计算结构体
     */
    class CalStruct {

        /**
         * 前位计算的数字，如：1+2中的1
         */
        private double firstNum;
        /**
         * 计算符，如：1+2中的+
         */
        private String calSymbol;
        /**
         * 后位计算的数字，如：1+2中的2
         */
        private double secondNum;

        static final String ADD = "+";
        static final String MINUS = "-";
        static final String TIMES = "*";
        static final String DIVIDE = "/";

        CalStruct(double firstNum, String calSymbol, double secondNum) {
            this.firstNum = firstNum;
            this.calSymbol = calSymbol;
            this.secondNum = secondNum;
        }

        /**
         * 计算
         */
        double calculate() {
            if (ADD.equals(this.calSymbol)) {
                return firstNum + this.secondNum;
            }
            if (MINUS.equals(this.calSymbol)) {
                return firstNum - this.secondNum;
            }
            if (TIMES.equals(this.calSymbol)) {
                return firstNum * this.secondNum;
            }
            if (DIVIDE.equals(this.calSymbol)) {
                return firstNum / this.secondNum;
            }
            return firstNum;
        }

    }

    /**
     * 抛出异常，用于提示错误
     */
    class BizException extends Exception {

        BizException(String message) {
            super(message);
        }

    }


}
