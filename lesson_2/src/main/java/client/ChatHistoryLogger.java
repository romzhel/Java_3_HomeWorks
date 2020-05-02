package client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatHistoryLogger {
    private static final String PATH_TEMPLATE = "/history/history_%s.txt";
    private static ChatHistoryLogger instance;
    private File file;
    private BufferedWriter bufferedWriter;

    public static ChatHistoryLogger getInstance() {
        if (instance == null) {
            instance = new ChatHistoryLogger();
        }
        return instance;
    }

    public void init(String nickName) throws Exception {
        file = new File(String.format(PATH_TEMPLATE, nickName));

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        bufferedWriter = new BufferedWriter(new FileWriter(file, true));
    }

    public List<String> getHistory(int lastHistoryLinesCount) {
        BufferedReader reader = null;
        Stream<String> linesStream = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            linesStream = reader.lines();
            List<String> lines = linesStream.collect(Collectors.toList());
            int fromIndex = Math.max(lines.size() - lastHistoryLinesCount, 0);
            int toIndex = Math.min(lastHistoryLinesCount, lines.size());
            return lines.subList(fromIndex, toIndex);
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } finally {
            try {
                linesStream.close();
            } catch (Exception e) {
            }
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    public void saveMessage(String message) {
        new Thread(() -> {
            try {
                bufferedWriter.append(message);
                bufferedWriter.flush();
            } catch (IOException e) {
                System.out.println("сообщение не было сохранено в файл");
            }
        }, "fileWriteThread").start();
    }

    public void close() {
        try {
            bufferedWriter.flush();
        } catch (IOException e) {
        }

        try {
            bufferedWriter.close();
        } catch (IOException e) {
        }

        file = null;
    }
}
