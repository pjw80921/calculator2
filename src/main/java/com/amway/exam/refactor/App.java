package com.amway.exam.refactor;

import java.util.Scanner;

/**
 * 重构后的主程序入口
 */
public class App {

    private final CalculatorEngine engine = new CalculatorEngine();
    private Scanner scanner;
    private boolean isRunning = true;

    public App() {
        this.scanner = new Scanner(System.in);
    }

    // 允许注入 Scanner 用于测试
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        println("=== Refactored Calculator ===");

        while (isRunning) {
            runSession();
        }
    }

    /**
     * 一次完整的计算会话（从 reset 开始）
     */
    private void runSession() {
        boolean firstInput = true;

        while (isRunning) {
            if (firstInput) {
                println("请输入表达式 (如 1.2+5):");
            } else {
                println("请输入后续计算 (如 +2), u(undo), r(redo), c(clear), exit:");
            }

            String line = readLine();
            if (line == null)
                return; // End of stream

            try {
                handleInput(firstInput, line);

                // 如果是 clear 命令，会抛出一个特殊的控制流信号，或者我们简单地通过状态判断
                // 原版逻辑：输入 c 后，跳出并清空，重新开始 firstInput
                // 在这里由 handleInput 返回状态或者直接修改外部变量可能更简单
                if ("c".equalsIgnoreCase(line)) {
                    firstInput = true;
                    continue;
                }

                // 如果成功执行了计算，那么下一次就不是 firstInput 了
                // 注意：Undo/Redo 不改变 "非 firstInput" 的状态，除非 Undo 到了底？
                // 简化起见，只要初始化过，就一直保持非 first 状态，直到 clear
                if (firstInput && engine.getCurrentResult() != 0.0) { // 简单判断
                    firstInput = false;
                }
                // 修正：parseFirst 会调用 engine.initialize，此时 currentResult 可能为 0 (如 1-1)，
                // 但 firstInput 确实应该翻转。
                // 更好的逻辑：只要没有抛异常，就翻转。
                firstInput = false;

            } catch (CalculatorException e) {
                println("Error: " + e.getMessage());
            } catch (ResetSignal e) {
                engine.reset();
                firstInput = true;
                println("已重置");
            }
        }
    }

    // 使用异常做控制流虽然不推荐，但在这种简单的 REPL 此时是个便捷选择，
    // 或者我们让 handleInput 返回一个 enum Result { SUCCESS, RESET, EXIT }
    private static class ResetSignal extends RuntimeException {
    }

    private void handleInput(boolean isFirst, String line) {
        // 1. 处理控制命令
        if ("exit".equalsIgnoreCase(line)) {
            println("bye");
            this.isRunning = false;
            return;
        }
        if ("c".equalsIgnoreCase(line)) {
            throw new ResetSignal();
        }
        if ("u".equalsIgnoreCase(line)) {
            if (isFirst)
                throw new CalculatorException("初始状态无法撤销，请用 c 重置");
            double res = engine.undo();
            printlnResult(res);
            return;
        }
        if ("r".equalsIgnoreCase(line)) {
            if (isFirst)
                throw new CalculatorException("初始状态无法重做");
            double res = engine.redo();
            printlnResult(res);
            return;
        }

        // 2. 处理计算解析
        if (isFirst) {
            InputParser.ParsedInput data = InputParser.parseFirst(line);
            // 首次输入逻辑： 1+2 -> init(1) -> apply(+ 2)
            // 原版逻辑：1+2 作为一个原子操作，结果存入 result。
            // 我们的 engine 支持 apply。
            // 我们可以把 1 当作初始值。
            engine.initialize(data.firstNum);
            double res = engine.apply(data.op, data.secondNum);
            printlnResult(res);
        } else {
            InputParser.ParsedInput data = InputParser.parseNext(line);
            double res = engine.apply(data.op, data.secondNum);
            printlnResult(res);
        }
    }

    private String readLine() {
        // 处理 Ctrl+D 等情况
        if (!scanner.hasNextLine()) {
            isRunning = false;
            return null;
        }
        String line = scanner.nextLine().trim();
        if (line.isEmpty())
            return readLine(); // 忽略空行
        return line;
    }

    private void println(String msg) {
        System.out.println(msg);
    }

    private void printlnResult(double res) {
        System.out.println(res + "\n");
    }

    public static void main(String[] args) {
        new App().run();
    }
}
