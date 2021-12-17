package main.java.hp_storage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiaojianhui
 */
public class MappedFile {
    /**
     * 文件大小: 50B
     */
    static final int FILE_SIZE = 50;
    /**
     * 提交最小阈值: 20B
     */
    static final int COMMIT_LEAST_SIZE = 20;
    String filename;
    File file;
    FileChannel fileChannel;
    MappedByteBuffer mappedByteBuffer;
    ByteBuffer writeBuffer = ByteBuffer.allocate(FILE_SIZE);
    AtomicInteger wrotePos = new AtomicInteger(0);
    AtomicInteger committedPos = new AtomicInteger(0);
    AtomicInteger flushedPos = new AtomicInteger(0);
    AtomicInteger fileFromOffset = new AtomicInteger(0);

    public static void main(String[] args) {
        MappedFile mappedFile = new MappedFile("00200");
        mappedFile.writeMessage("this is a good example".getBytes());
        mappedFile.writeMessage("1231232".getBytes());
        mappedFile.writeMessage("2423".getBytes());
        mappedFile.commit0();
        mappedFile.flush();
        mappedFile.release();
        //mappedFile.printFileContent();


    }

    public MappedFile(String filename) {
        this.filename = filename;
        loadData(filename);
    }

    private void loadData(String filename) {
        file = new File(CommitLog.ROOT_PATH + filename);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileChannel = new RandomAccessFile(file.getPath(), "rw").getChannel();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());
            fileChannel.position(fileChannel.size());
            fileFromOffset.getAndAdd((int)fileChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean writeMessage(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return false;
        }
        try {
            writeBuffer.position(wrotePos.get());
            writeBuffer.limit(writeBuffer.capacity());
            writeBuffer.put(bytes);
        }catch (BufferOverflowException e) {
            System.out.println("BufferOverflowException occur");
            return false;
        }
        wrotePos.getAndAdd(bytes.length);
        fileFromOffset.getAndAdd(bytes.length);
        return true;
    }

    public void commit() {
        if (wrotePos.get() - committedPos.get() >= COMMIT_LEAST_SIZE) {
            commit0();
        }else {
            System.out.println("Not reach COMMIT_LEAST_SIZE");
        }
    }

    private void commit0() {
        int lastCommitPos = committedPos.get(), writePos = wrotePos.get(), len = writePos - lastCommitPos;
        if (len > 0) {
            writeBuffer.position(lastCommitPos);
            writeBuffer.limit(writePos);
            ByteBuffer buffer = writeBuffer.slice();
            try {
                fileChannel.write(buffer);
                committedPos.getAndAdd(len);
                System.out.println(filename + " commit success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void flush() {
        int len = committedPos.get() - flushedPos.get();
        if (len > 0 && fileChannel.isOpen()) {
            try {
                fileChannel.force(false);
                flushedPos.getAndAdd(len);
                System.out.println(filename + " flush success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void release() {
        try {
            // save memory data
            this.commit0();
            this.flush();

            this.fileChannel.close();
            this.mappedByteBuffer = null;
            this.writeBuffer = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFileContent() {
        ByteBuffer buffer = ByteBuffer.allocate(FILE_SIZE);
        try {
            this.fileChannel.position(0);
            if (this.fileChannel.read(buffer) > 0) {
                buffer.flip();
                System.out.println(new String(buffer.array()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
