import itch.ItchCrate;
import itch.exception.ItchLibraryException;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Tests {

    public static String md5(File inFile) {
        try {
            return md5(new FileInputStream(inFile));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String md5(InputStream in) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        try {
            in = new DigestInputStream(in, md);
            byte[] buf = new byte[1 << 16];
            while (in.read(buf) >= 0) {
                // do nothing
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // do nothing
            }
        }
        return new String(md.digest());
    }

    public static void main(String[] args) {
        try {

            // Crates from ITCH (Mac OS)
            testRW("test/mac/small.crate");
            testRW("test/mac/large.crate");

            testRW("test/mac/main%%sub1.crate");
            testRW("test/mac/main%%sub2.crate");
            testRW("test/mac/main%%sub1%%subsub1.crate");

            // Crates from ITCH (Windows XP)
            testR("test/win/disk-c.crate");
            testR("test/win/disk-z.crate");
        } catch (ItchLibraryException e) {
            System.err.println("TESTS FAILED");
            e.printStackTrace();
        }
    }

    private static void testRW(String fileName) throws ItchLibraryException {
        File file = new File(fileName);
        ItchCrate crate = ItchCrate.readFrom(file);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        crate.writeTo(output);

        String md5Original = md5(file);
        String md5Reassembled = md5(new ByteArrayInputStream(output.toByteArray()));
        if (md5Original.equals(md5Reassembled)) {
            System.out.println("TEST PASSED: " + fileName);
            System.out.flush();
        } else {
            System.err.println("TEST FAILED: " + fileName);
            System.err.flush();
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private static void testR(String fileName) throws ItchLibraryException {
        File file = new File(fileName);
        ItchCrate crate = ItchCrate.readFrom(file);
        System.out.println("TEST PASSED: " + fileName);
        System.out.flush();
    }

}
