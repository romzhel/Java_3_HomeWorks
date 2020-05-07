package lesson_4;

public class SymbolsByThreadsPrinter {
    private char[] symbolsSequence;
    private volatile int symbolIndex = 0;
    private int repeatCount;

    public static void main(String[] args) {
        SymbolsByThreadsPrinter sbtp = new SymbolsByThreadsPrinter();
        sbtp.printSymbolsByIndependentThreads(5, 'A', 'B', 'C');
    }

    public void printSymbolsByIndependentThreads(int repeatCount, char... symbols) {
        this.repeatCount = repeatCount;
        this.symbolsSequence = symbols;

        for (char symbol : symbols) {
            new Thread(() -> printSymbol(symbol), "Thread_" + symbol).start();
        }
    }

    private synchronized void printSymbol(Character symbol) {
        for (int i = 0; i < repeatCount; i++) {
            while (symbol != symbolsSequence[symbolIndex]) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.print(symbol);
            symbolIndex = ++symbolIndex % symbolsSequence.length;
            notifyAll();
        }
    }
}
