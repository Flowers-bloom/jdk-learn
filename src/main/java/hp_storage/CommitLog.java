package main.java.hp_storage;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojianhui
 */
public class CommitLog {
    static final String ROOT_PATH = System.getProperty("user.dir") + "/commit-log/";
    static final int DIGIT = 5;
    static final ScheduledExecutorService SCHEDULE_FLUSH_SERVICE = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "CommitLogScheduleFlushThread");
        }
    });
    static final ScheduledExecutorService SCHEDULED_CLEAN_FILE_SERVICE = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "CommitLogScheduledCleanFileThread");
        }
    });
    static final int FLUSH_GAP_SECOND = 5;
    static final int CLEAN_FILE_GAP_SECOND = 6;
    static final int CLEAN_THRESHOLD = 3600 * 24;
    List<MappedFile> mappedFileQueue = new LinkedList<>();
    MappedFile mappedFile;
    int offset;

    public static void main(String[] args) {
        CommitLog commitLog = new CommitLog();
//        Scanner scanner = new Scanner(System.in);
//        String in = scanner.nextLine();
//        while (!"exit".equals(in)) {
//            commitLog.writeMessage(in);
//            in = scanner.nextLine();
//        }
//        System.out.println("exit input loop");

        tryWriteSomeMsg(commitLog);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        commitLog.release();
    }

    public static void tryWriteSomeMsg(CommitLog commitLog) {
        for (int i = 0; i < 5; i++) {
            commitLog.writeMessage("12345678901234");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public CommitLog() {
        mappedFile = findLastMappedFile();
        if (mappedFile == null) {
            mappedFile = new MappedFile(getNextMappedFileName(offset));
        }
        startScheduledFlushTask();
        startScheduledCleanFileTask();
    }

    public String getNextMappedFileName(int offset) {
        return String.format("%0" + DIGIT + "d", offset);
    }

    public MappedFile findLastMappedFile() {
        File file = new File(ROOT_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                MappedFile mappedFile = new MappedFile(f.getName());
                mappedFileQueue.add(mappedFile);
            }
            if (files.length > 0) {
                offset = (files.length - 1) * MappedFile.FILE_SIZE;
            }
        }
        return mappedFileQueue.isEmpty() ? null : mappedFileQueue.get(mappedFileQueue.size() - 1);
    }

    public void writeMessage(String message) {
        int fileOffset = this.mappedFile.fileFromOffset.get();
        if (fileOffset + message.length() > MappedFile.FILE_SIZE) {
            // close old mappedFile
            this.mappedFile.release();

            offset += this.mappedFile.fileFromOffset.get();
            int nextOffset = ((offset / MappedFile.FILE_SIZE) + 1) * MappedFile.FILE_SIZE;
            String newMappedFileName = getNextMappedFileName(nextOffset);
            this.mappedFile = new MappedFile(newMappedFileName);
            this.mappedFileQueue.add(this.mappedFile);
            System.out.println("create newMappedFile success: " + newMappedFileName);
        }
        this.mappedFile.writeMessage(message.getBytes());
        this.mappedFile.commit();
    }

    private void startScheduledFlushTask() {
        SCHEDULE_FLUSH_SERVICE.scheduleAtFixedRate(() -> {
            this.mappedFile.flush();
        }, FLUSH_GAP_SECOND, FLUSH_GAP_SECOND, TimeUnit.SECONDS);
        System.out.println("CommitLog schedule flush task start success");
    }

    private void startScheduledCleanFileTask() {
        SCHEDULED_CLEAN_FILE_SERVICE.scheduleAtFixedRate(() -> {
            int i = 0;
            MappedFile mf = this.mappedFileQueue.get(i);
            while (System.currentTimeMillis() - mf.file.lastModified() > CLEAN_THRESHOLD) {
                boolean delete = mf.file.delete();
                System.out.println("SCHEDULED_CLEAN_FILE_SERVICE: " + mf.filename + " delete " + (delete ? "success" : "failed"));
                this.mappedFileQueue.remove(mf);
                mf = this.mappedFileQueue.get(++i);
            }
        }, CLEAN_FILE_GAP_SECOND, CLEAN_FILE_GAP_SECOND, TimeUnit.SECONDS);
        System.out.println("CommitLog schedule clean file task start success");
    }

    public void release() {
        SCHEDULE_FLUSH_SERVICE.shutdownNow();
        SCHEDULED_CLEAN_FILE_SERVICE.shutdownNow();
    }
}
