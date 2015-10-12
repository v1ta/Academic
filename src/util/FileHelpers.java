package util;

import model.TorrentManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;


public class FileHelpers {

    public static byte[] readFile(int index, int offset, int length, TorrentInfo torrentInfo, File outputFile) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(outputFile, "r");
        byte[] data = new byte[length];

        raf.seek(torrentInfo.piece_length * index + offset);
        raf.read(data);
        raf.close();

        return data;
    }
}
