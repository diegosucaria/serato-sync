package itch.io;

import itch.exception.ItchLibraryException;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ItchOutputStream extends DataOutputStream {

    public ItchOutputStream(OutputStream out) {
        super(new BufferedOutputStream(out));
    }


    public void writeUTF16(String version) throws ItchLibraryException {
        try {
            writeChars(version);
        } catch (IOException e) {
            throw new ItchLibraryException(e);
        }
    }

    public void writeLong(long value, int bytes) throws ItchLibraryException {
        byte[] data = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            data[bytes - 1 - i] = (byte) (value & 0xFF);
            value >>>= 8;
        }
        for (byte v : data) {
            try {
                write(v);
            } catch (IOException e) {
                throw new ItchLibraryException(e);
            }
        }
    }

}
