package com.amway.exam.original_version;

import org.junit.Test;

import com.amway.exam.original_version.Calculator;

/**
 * 计算器测试类
 *
 * @author pengjunwei
 */
public class CalculatorTest {

    private Calculator calculator = Calculator.getInstance();

    /**
     * 如果用idea导入项目，run或debug以下方法时，无法在控制台输入，可参考如下文档解决：
     * https://blog.csdn.net/weixin_52938172/article/details/120794883
     */
    @Test
    public void testCalculator() {
        // String input = "1+2\nexit\n";
        calculator.setInput(new java.util.Scanner(System.in));
        calculator.calculator();
    }

}
