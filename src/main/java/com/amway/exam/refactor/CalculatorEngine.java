package com.amway.exam.refactor;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * CalculatorEngine (Refined)
 */
public class CalculatorEngine {

    private double currentResult = 0.0;

    // 历史状态栈（存放值和当时的LastCommand）
    private final Deque<StateSnapshot> undoStack = new ArrayDeque<>();

    // Redo栈（存放被撤销的命令）
    private final Deque<Command> redoStack = new ArrayDeque<>();

    // 记录最后一次执行的有效命令（用于UI显示或逻辑链）
    private Command lastExecutedCommand;

    private static class StateSnapshot {
        final double result;
        final Command command; // 到达此时态的前一个命令

        StateSnapshot(double result, Command command) {
            this.result = result;
            this.command = command;
        }
    }

    public static class Command {
        public final MathOperation operation;
        public final double operand;

        public Command(MathOperation operation, double operand) {
            this.operation = operation;
            this.operand = operand;
        }

        @Override
        public String toString() {
            return operation.getSymbol() + operand;
        }
    }

    public double getCurrentResult() {
        return currentResult;
    }

    /**
     * 初始化/重置当前值为特定值（通常用于首次输入 1+2 的情况）
     */
    public void initialize(double value) {
        // 这算是一次状态变更，但它比较特殊，它重置了起点
        recordState();
        this.currentResult = value;
        this.lastExecutedCommand = null; // 初始化不是一个运算符命令
        this.redoStack.clear();
    }

    public void reset() {
        this.currentResult = 0.0;
        this.undoStack.clear();
        this.redoStack.clear();
        this.lastExecutedCommand = null;
    }

    /**
     * 执行计算： result = result OP operand
     */
    public double apply(MathOperation op, double operand) {
        recordState();
        this.currentResult = op.apply(this.currentResult, operand);

        Command cmd = new Command(op, operand);
        this.lastExecutedCommand = cmd;
        this.redoStack.clear(); // 新操作打断 Redo 链
        return this.currentResult;
    }

    public double undo() {
        if (undoStack.isEmpty()) {
            throw new CalculatorException("无法撤销：已经是初始状态");
        }

        // 1. 保存当前即将被丢弃的操作到 Redo 栈
        // 注意：如果你 Undo 了，那么“刚才做的那个操作”就是你可以 Redo 的操作
        if (lastExecutedCommand != null) {
            redoStack.push(lastExecutedCommand);
        }

        // 2. 恢复旧状态
        StateSnapshot snapshot = undoStack.pop();
        this.currentResult = snapshot.result;
        this.lastExecutedCommand = snapshot.command;

        return this.currentResult;
    }

    public double redo() {
        if (redoStack.isEmpty()) {
            throw new CalculatorException("无法重做：没有最近撤销的操作");
        }

        Command cmd = redoStack.pop();

        // 执行命令，但不清空 Redo 栈（因为这是恢复操作）
        // 也不需要重新 recordState 到 undoStack 吗？需要！
        // 因为 Redo 本质上也是一次状态变更，做完 Redo 后，你又可以 Undo 了。
        recordState();
        this.currentResult = cmd.operation.apply(this.currentResult, cmd.operand);
        this.lastExecutedCommand = cmd;

        return this.currentResult;
    }

    private void recordState() {
        undoStack.push(new StateSnapshot(this.currentResult, this.lastExecutedCommand));
    }
}
