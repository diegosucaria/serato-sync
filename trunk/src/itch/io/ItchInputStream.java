package itch.io;

import itch.exception.ItchLibraryException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class ItchInputStream extends DataInputStream {

    public ItchInputStream(InputStream in) {
        super(in);
    }

    /**
     * Utility method for reading variable amount of bytes
     *
     * @param bytes the number of bytes to read
     * @return 32-bit integer value
     * @throws itch.exception.ItchLibraryException
     *          In case of I/O exception
     */
    public long readLongValue(int bytes) throws ItchLibraryException {
        long nameLength = 0;
        for (int i = 0; i < bytes; i++) {
            nameLength <<= 8;
            try {
                nameLength += readUnsignedByte();
            } catch (IOException e) {
                throw new ItchLibraryException(e);
            }
        }
        return nameLength;
    }

    /**
     * Utility method for reading 4 bytes
     *
     * @return 32-bit integer value
     * @throws itch.exception.ItchLibraryException
     *          In case of I/O exception
     */
    public int readIntegerValue() throws ItchLibraryException {
        int nameLength = 0;
        for (int i = 0; i < 4; i++) {
            nameLength <<= 8;
            try {
                nameLength += readUnsignedByte();
            } catch (IOException e) {
                throw new ItchLibraryException(e);
            }
        }
        return nameLength;
    }

    /**
     * Utility method for reading a string and making sure it's the right one
     *
     * @param expected Expected string
     * @return true if EOF reached, false otherwise
     * @throws ItchLibraryException In case of I/O exception or string mismatch
     */
    public boolean skipExactString(String expected) throws ItchLibraryException {
        byte[] data = new byte[expected.length()];
        try {
            int read = read(data);
            if (read < 0) {
                return true;
            }
            if (read != data.length) {
                throw new ItchLibraryException("Expected '" + expected + "', but read only " + read + " bytes");
            }
        } catch (IOException e) {
            throw new ItchLibraryException(e);
        }
        String dataAsString = new String(data);
        if (!expected.equals(dataAsString)) {
            throw new ItchLibraryException("Expected '" + expected + "' but found '" + dataAsString + "'");
        }
        return false;
    }

    /**
     * Utility method for reading a UTF-16 string and making sure it's the right one
     *
     * @param expected Expected string
     * @return true if EOF reached, false otherwise
     * @throws itch.exception.ItchLibraryException
     *          In case of I/O exception, string mismatch, or unsupported encoding
     */
    public boolean skipExactStringUTF16(String expected) throws ItchLibraryException {
        byte[] data = new byte[expected.length() << 1];
        try {
            int read = read(data);
            if (read < 0) {
                return true;
            }
            if (read != data.length) {
                throw new ItchLibraryException("Expected '" + expected + "', but read only " + read + " bytes");
            }
        } catch (IOException e) {
            throw new ItchLibraryException(e);
        }
        String dataAsString;
        try {
            dataAsString = new String(data, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            throw new ItchLibraryException(e);
        }
        if (!expected.equals(dataAsString)) {
            throw new ItchLibraryException("Expected '" + expected + "' but found '" + dataAsString + "'");
        }
        return false;
    }

    /**
     * Utility method for reading a byte and making sure it's the right one
     *
     * @param expected Expected byte
     * @return true if EOF reached, false otherwise
     * @throws ItchLibraryException In case of I/O exception or string mismatch
     */
    public boolean skipExactByte(byte expected) throws ItchLibraryException {
        byte[] data = new byte[1];
        try {
            int read = read(data);
            if (read < 0) {
                return true;
            }
            if (read != data.length) {
                throw new ItchLibraryException("Expected a single byte '" + expected + "', but was unable to read anything");
            }
        } catch (IOException e) {
            throw new ItchLibraryException(e);
        }
        if (data[0] != expected) {
            throw new ItchLibraryException("Expected a single byte " + expected + " but found '" + data[0] + "'");
        }
        return false;
    }

    /**
     * Utility method for reading UTF-16 string
     *
     * @param length Number of bytes to read
     * @return string, read in UTF-16 format
     * @throws ItchLibraryException In case of I/O exception or unsupported encoding
     */
    public String readStringUTF16(int length) throws ItchLibraryException {
        byte[] data = new byte[length];
        try {
            int read = read(data);
            if (read != length) {
                throw new ItchLibraryException("Expected to read " + length + " bytes, but read only " + read);
            }
        } catch (IOException e) {
            throw new ItchLibraryException(e);
        }
        try {
            return new String(data, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            throw new ItchLibraryException(e);
        }
    }

    /**
     * Utility method for reading string
     *
     * @param length Number of bytes to read
     * @return string that was read
     * @throws ItchLibraryException In case of I/O exception or unsupported encoding
     */
    public String readString(int length) throws ItchLibraryException {
        byte[] data = new byte[length];
        try {
            int read = read(data);
            if (read != length) {
                throw new ItchLibraryException("Expected to read " + length + " bytes, but read only " + read);
            }
        } catch (IOException e) {
            throw new ItchLibraryException(e);
        }
        return new String(data);
    }


}
