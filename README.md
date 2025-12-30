# calculator

题目：
写一个计算器类（Calculator），可以实现两个数的加、减、乘、除运算，并可以进行undo和redo操作

实现功能：
1. 实现模拟计算器的功能，可以实现两个数的加、减、乘、除运算
2. 可以进行redo操作，重新执行上一次输入并输出结果
3. 可以进行undo操作，回退到上上次操作的结束值，可多次undo，但undo不可撤销
4. 可以重新复位，并重新输入计数
5. 可以手动关闭程序

核心代码：
com.amway.exam.Calculator

测试方法：
com.amway.exam.CalculatorTest#testCalculator

亮点：
1. 工程化思想编写
2. 抽象数据模型
3. 代码结构良好，每个功能分支抽取成单独的方法
4. 基本没有重复的代码量
