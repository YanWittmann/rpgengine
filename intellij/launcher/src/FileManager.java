
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileManager {
    public static String[] readFile(String filename) {
        String[] result;
        File file = new File(filename);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

            int lines = 0, counter = 0;
            while (br2.readLine() != null) lines++;
            result = new String[lines];

            String st;
            while ((st = br.readLine()) != null) {
                result[counter] = st;
                counter++;
            }
            br.close();
            br2.close();
            return result;
        } catch (IOException e) {
            return new String[]{};
        }
    }

    public static void writeToFile(String filename, String[] text) {
        try {
            File file = new File(filename);
            makeDirectory(file.getAbsolutePath().replace(file.getName(), ""));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            for (int i = 0; i < text.length - 1; i++) {
                bw.write(text[i]);
                bw.newLine();
            }
            bw.write(text[text.length - 1]);
            bw.close();
        } catch (IOException ignored) {
        }
    }

    public static void writeToFile(String filename, String text) {
        try {
            File file = new File(filename);
            makeDirectory(file.getAbsolutePath().replace(file.getName(), ""));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            bw.write(text);
            bw.close();
        } catch (IOException ignored) {
        }
    }

    public static byte[] readFileToByteArray(String filename) {
        try {
            return Files.readAllBytes(new File(filename).toPath());
        } catch (IOException ignored) {
        }
        return null;
    }

    public static void writeFileFromByteArray(String filename, byte[] array) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(array);
            fos.close();
        } catch (Exception ignored) {
        }
    }

    public static boolean fileExists(String filename) {
        return new File(filename).isFile();
    }

    public static boolean pathExists(String path) {
        File f = new File(path);
        if (f.exists() && f.isDirectory())
            return true;
        return false;
    }

    public static long fileSize(String filename) {
        return new File(filename).length();
    }

    public static boolean delete(String filename) {
        if (fileExists(filename)) new File(filename).delete();
        else return false;
        return true;
    }

    public static boolean makeDirectory(String dir) {
        return new File(dir).mkdirs();
    }

    public static String[] getFiles(String path) {
        File directoryPath = new File(path);
        int counter = 0;
        for (File file : directoryPath.listFiles())
            counter++;
        String[] allFiles = new String[counter];
        counter = 0;
        for (File file : directoryPath.listFiles()) {
            allFiles[counter] = file.getName();
            counter++;
        }
        return allFiles;
    }

    public static String[] getFilesWithEnding(String path, String ending) {
        final String ending2 = ending.replace(".", "");
        File directoryPath = new File(path);

        File[] files = directoryPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("." + ending2);
            }
        });

        int counter = 0;
        for (File file : files)
            counter++;
        String[] allFiles = new String[counter];
        counter = 0;
        for (File file : files) {
            allFiles[counter] = file.getName();
            counter++;
        }
        return allFiles;
    }

    private static String lastPickLocation = "";

    public static String filePicker() {
        JFileChooser chooser = null;
        if (lastPickLocation.equals("")) chooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
        else chooser = new JFileChooser(lastPickLocation);
        chooser.showOpenDialog(null);
        lastPickLocation = chooser.getSelectedFile().getAbsolutePath();
        try {
            return chooser.getSelectedFile().getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }

    public static String[] windowsFilePicker() {
        FileDialog picker = new java.awt.FileDialog((java.awt.Frame) null);
        picker.setVisible(true);
        File[] f = picker.getFiles();
        String[] paths = new String[f.length];
        for (int i = 0; i < f.length; i++)
            paths[i] = f[i].getAbsolutePath();
        return paths;
    }

    public static boolean copyFile(String src, String dst) {
        try {
            Files.copy(new File(src).toPath(), new File(dst).toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getFilename(String path) {
        File f = new File(path);
        return f.getName();
    }

    public static void deleteFile(String filename) {
        File file = new File(filename);
        file.delete();
    }

    public static void deleteDirectory(String directory) {
        File dir = new File(directory);
        File[] listFiles = dir.listFiles();
        for (File file : listFiles) {
            file.delete();
        }
        dir.delete();
    }

    public static void deleteDirectoryRecursively(String directory) {
        try {
            Path dir = Paths.get(directory);
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ignored) {
        }
    }

    //https://www.codejava.net/java-se/file-io/programmatically-extract-a-zip-file-using-java
    public static boolean unzip(String zipFilePath, String destDirectory) {
        try {
            File destDir = new File(destDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final int BUFFER_SIZE = 4096;

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        makeDirectory(filePath.replace(new File(filePath).getName(), ""));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public static void zipDirectory(String directory, String zipFile) {
        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(directory);

            zipFile(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();
        } catch (Exception ignored) {
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) {
        try {
            if (fileToZip.isHidden()) {
                return;
            }
            if (fileToZip.isDirectory()) {
                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                }
                zipOut.closeEntry();
                File[] children = fileToZip.listFiles();
                assert children != null;
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
                return;
            }
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        } catch (Exception ignored) {
        }
    }

    public static String[] getResponseFromURL(String pUrl) {
        pUrl = pUrl.replace(" ", "%20");
        //System.out.println(pUrl);
        ArrayList<String> lines = new ArrayList<>();
        try {
            URL url = new URL(pUrl);
            BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
            String i;
            while ((i = read.readLine()) != null)
                lines.add(i);
            read.close();
        } catch (Exception ignored) {
        }
        String[] result = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++)
            result[i] = lines.get(i);
        return result;
    }

    public static boolean saveUrl(String filename, String urlString) {
        BufferedInputStream in;
        FileOutputStream fout;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte[] data = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
            in.close();
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean connectedToInternet() {
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String[] getTextFromURL(String url) {
        saveUrl("downloaded.fm", url);
        String[] ret = readFile("downloaded.fm");
        delete("downloaded.fm");
        return ret;
    }

    public static void openFile(String filename) {
        try {
            Desktop.getDesktop().open(new File(filename));
        } catch (Exception ignored) {
        }
    }

    public static Font getFont(String filename) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(filename)).deriveFont(30f);
        } catch (Exception e) {
            return new Font("TimesRoman", Font.PLAIN, 30);
        }
    }

    /* thank you SO much,   Maur�cio Linhares   https://stackoverflow.com/questions/6811522/changing-the-working-directory-of-command-from-java/6811578
     * and                  Aniket Thakur       https://stackoverflow.com/questions/17985036/run-a-jar-file-from-java-program
     * this literally took me 2.5 hours to make.
     */
    public static void openJar(String jar, String path, String[] args) {
        try {
            File pathToExecutable = new File(jar);
            String[] args2 = new String[args.length + 3];
            args2[0] = "java";
            args2[1] = "-jar";
            args2[2] = pathToExecutable.getAbsolutePath();
            System.arraycopy(args, 0, args2, 3, args2.length - 3);
            /*for (int i = 3; i < args2.length; i++)
                args2[i] = args[i - 3];*/
            ProcessBuilder builder = new ProcessBuilder(args2);
            builder.directory(new File(path).getAbsoluteFile()); // this is where you set the root folder for the executable to run with
            builder.redirectErrorStream(true);
            builder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getFileSize(String url) {
        try {
            URL url1 = new URL(url);
            URLConnection conn = null;
            try {
                conn = url1.openConnection();
                if (conn != null) {
                    ((HttpURLConnection) conn).setRequestMethod("HEAD");
                }
                assert conn != null;
                conn.getInputStream();
                return conn.getContentLength();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn instanceof HttpURLConnection) {
                    ((HttpURLConnection) conn).disconnect();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
