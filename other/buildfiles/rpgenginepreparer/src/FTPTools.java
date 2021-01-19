
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class FTPTools {
    private String user, pass, server;
    private int port;
    private final FTPClient ftpClient = new FTPClient();

    public FTPTools(String user, String pass, String server, int port) {
        this.user = user;
        this.pass = pass;
        this.server = server;
        this.port = port;

        login();
    }

    public boolean upload(String localSourceFile, String remoteResultFile) {
        FileInputStream fis = null;
        boolean resultOk = true;

        try {
            fis = new FileInputStream(localSourceFile);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            resultOk = ftpClient.storeFile(remoteResultFile, fis);
            println(ftpClient.getReplyString());
        } catch (Exception e) {
            Popup.error("Error", "Failed to upload file: " + e);
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {e.printStackTrace();}
        }

        return resultOk;
    }

    public boolean download(String localResultFile, String remoteSourceFile) {
        FileOutputStream fos = null;
        boolean resultOk = true;

        try {
            fos = new FileOutputStream(localResultFile);
            resultOk = ftpClient.retrieveFile(remoteSourceFile, fos);
            println(ftpClient.getReplyString());
        } catch (Exception e) {
            Popup.error("Error", "Failed to download file: " + e);
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {/* nothing to do */}
        }

        return resultOk;
    }

    public boolean fileExists(String remoteFile) {
        return listFiles(remoteFile).length > 0;
    }

    public boolean createDirectory(String path) {
        try {
            boolean result = ftpClient.makeDirectory(path);
            println(ftpClient.getReplyString());
            return result;
        } catch (IOException e) {
            Popup.error("Error", "Failed to list files: " + e);
            e.printStackTrace();
            return false;
        }

    }

    public String[] listFiles(String path) {
        String[] filenameList = null;

        try {
            filenameList = ftpClient.listNames(path);
            println(ftpClient.getReplyString());
        } catch (IOException e) {
            Popup.error("Error", "Failed to list files: " + e);
            e.printStackTrace();
        }
        if (filenameList == null) filenameList = new String[0];
        return filenameList;
    }

    public boolean uploadDirectory(String remoteDirPath, String localParentDir) {
        return uploadDirectory(remoteDirPath, localParentDir, "");
    }

    private boolean uploadDirectory(String remoteDirPath, String localParentDir, String remoteParentDir) {
        try {
            println("LISTING directory: " + localParentDir);

            File localDir = new File(localParentDir);
            File[] subFiles = localDir.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                for (File item : subFiles) {
                    String remoteFilePath = remoteDirPath + "/" + remoteParentDir
                            + "/" + item.getName();
                    if (remoteParentDir.equals("")) {
                        remoteFilePath = remoteDirPath + "/" + item.getName();
                    }


                    if (item.isFile()) {
                        // upload the file
                        String localFilePath = item.getAbsolutePath();
                        println("About to upload the file: " + localFilePath);
                        boolean uploaded = upload(localFilePath, remoteFilePath);
                        if (uploaded) {
                            println("UPLOADED a file to: "
                                    + remoteFilePath);
                        } else {
                            println("COULD NOT upload the file: "
                                    + localFilePath);
                        }
                    } else {
                        // create directory on the server
                        boolean created = ftpClient.makeDirectory(remoteFilePath);
                        if (created) {
                            println("CREATED the directory: "
                                    + remoteFilePath);
                        } else {
                            println("COULD NOT create the directory: "
                                    + remoteFilePath);
                        }

                        // upload the sub directory
                        String parent = remoteParentDir + "/" + item.getName();
                        if (remoteParentDir.equals("")) {
                            parent = item.getName();
                        }

                        localParentDir = item.getAbsolutePath();
                        uploadDirectory(remoteDirPath, localParentDir, parent);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeFile(String remoteFile) {
        try {
            return ftpClient.deleteFile(remoteFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeDirectory(String removeDir) {
        return removeDirectory(removeDir, "");
    }

    private boolean removeDirectory(String parentDir, String currentDir) {
        try {
            String dirToList = parentDir;
            if (!currentDir.equals("")) {
                dirToList += "/" + currentDir;
            }

            String[] subFiles = listFiles(dirToList);

            if (subFiles != null && subFiles.length > 0) {
                for (String aFile : subFiles) {
                    File file = new File(aFile);
                    String currentFileName = file.getName();
                    if (currentFileName.equals(".") || currentFileName.equals("..")) {
                        // skip parent directory and the directory itself
                        continue;
                    }
                    String filePath = parentDir + "/" + currentDir + "/"
                            + currentFileName;
                    if (currentDir.equals("")) {
                        filePath = parentDir + "/" + currentFileName;
                    }

                    if (!currentFileName.contains(".")) {
                        // remove the sub directory
                        removeDirectory(dirToList, currentFileName);
                    } else {
                        // delete the file
                        boolean deleted = ftpClient.deleteFile(filePath);
                        if (deleted) {
                            println("DELETED the file: " + filePath);
                        } else {
                            println("CANNOT delete the file: "
                                    + filePath);
                        }
                    }
                }

                // finally, remove the directory itself
                boolean removed = ftpClient.removeDirectory(dirToList);
                if (removed) {
                    println("REMOVED the directory: " + dirToList);
                } else {
                    println("CANNOT remove the directory: " + dirToList);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean login() {
        try {
            ftpClient.connect(server, port);
            println(ftpClient.getReplyString());
            ftpClient.login(user, pass);
            println(ftpClient.getReplyString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean logout() {
        try {
            ftpClient.logout();
            println(ftpClient.getReplyString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isReady() {
        return ftpClient.isConnected();
    }

    private boolean print = false;

    private void println(String text) {
        if (print) System.out.println(text);
    }
}
