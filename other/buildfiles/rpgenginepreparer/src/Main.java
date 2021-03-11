
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    private static FTPTools ftp = null;
    private final static String launcherVersion = "8", playVersion = "1.13", createVersion = "1.13";

    public static void main(String[] args) {

        System.out.println("\n\t!!! MAKE SURE TO HAVE THE CORRECT VERSION STRINGS EVERYWHERE !!!");
        System.out.println("\n\t!!! MAKE SURE TO HAVE BUILT ALL ARTIFACTS !!!");
        Sleep.milliseconds(2000);

        connectToFTP();

        createAndUploadCreateOrPlay("create", createVersion);
        createAndUploadCreateOrPlay("play", playVersion);
        createAndUploadDocumentation();
        //createAndUploadLauncher();

        cleanUp();

    }

    private static void createAndUploadCreateOrPlay(String which, String version) {
        System.out.println("\nCreate and upload " + which + " version " + version + "\n");
        System.out.println("\tLooking for files to delete...");
        if (ftp.fileExists("projects/rpgengine/" + which + "/" + version + ".zip")) {
            System.out.println("\tversion " + version + " of " + which + " already exists. Removing it from server");
            errorMessageIfFalse(ftp.removeFile("projects/rpgengine/" + which + "/" + version + ".zip"), "Unable to delete current uploaded version");
        }
        if (FileUtils.directoryExists("../" + which + "/" + version)) {
            System.out.println("\tDeleting old version directory '" + version + "'");
            errorMessageIfFalse(FileUtils.deleteDirectory("../" + which + "/" + version), "Unable to delete old version folder");
        }
        if (FileUtils.fileExists("../" + which + "/versions.txt")) {
            System.out.println("\tDeleting file 'versions.txt'");
            errorMessageIfFalse(FileUtils.deleteDirectory("../" + which + "/versions.txt"), "Unable to delete 'versions.txt'");
        }
        if (FileUtils.fileExists("../" + version + ".zip")) {
            System.out.println("\tDeleting old upload file ('" + version + ".zip')");
            errorMessageIfFalse(FileUtils.deleteDirectory("../" + version + ".zip"), "Unable to delete old upload file ('" + version + ".zip')");
        }

        System.out.println("\tCreating new version folder");
        FileUtils.makeDirectories(new File("../" + which + "/" + version + "/" + version));
        System.out.println("\tAdding '" + which + ".jar' to folder");
        errorMessageIfFalse(FileUtils.copyFile("../../../intellij/out/artifacts/" + which + "_jar/" + which + ".jar", "../" + which + "/" + version + "/" + version + "/" + which + ".jar"), "Unable to copy file '" + which + ".jar'");
        System.out.println("\tAdding new 'res' folder to folder");
        errorMessageIfFalse(FileUtils.copyDirectory("../../../intellij/" + which + "/res", "../" + which + "/" + version + "/" + version + "/res"), "Unable to copy 'res' folder");

        System.out.println("\tPacking " + which + " files into a .zip file ('" + version + ".zip')");
        errorMessageIfFalse(FileUtils.pack("../" + which + "/" + version, "../" + version + ".zip"), "Unable to pack " + which);
        System.out.println("\tUploading '" + version + ".zip'");
        errorMessageIfFalse(ftp.upload("../" + version + ".zip", "projects/rpgengine/" + which + "/" + version + ".zip"), "Unable to upload '" + version + ".zip'");

        System.out.println("\tChecking what versions are available online");
        String[] input = ftp.listFiles("projects/rpgengine/" + which + "/");
        ArrayList<String> versions = new ArrayList<>();
        for (String check : input) {
            check = new File(check).getName();
            if (!(check.equals(".") || check.equals("..") || check.equals("versions.txt")))
                versions.add(check.replace(".zip", ""));
        }
        System.out.println("\tFound versions: " + Arrays.toString(versions.toArray()));
        versions.sort(VersionNumberComparator.getInstance());
        Collections.reverse(versions);
        System.out.println("\tSorted & reversed versions: " + Arrays.toString(versions.toArray(new String[0])));
        System.out.println("\tCreating version list ('versions.txt')");
        errorMessageIfFalse(FileUtils.writeFile(new File("../" + which + "/versions.txt"), versions.toArray(new String[0])), "Unable to write file 'version.txt'");
        System.out.println("\tUploading new version list ('versions.txt')");
        errorMessageIfFalse(ftp.upload("../" + which + "/versions.txt", "projects/rpgengine/" + which + "/versions.txt"), "Unable to upload 'versions.txt'");

        System.out.println("\tDeleting local files");
        if (FileUtils.directoryExists("../" + which + "/" + version)) {
            System.out.println("\tDeleting version directory '" + version + "'");
            errorMessageIfFalse(FileUtils.deleteDirectory("../" + which + "/" + version), "Unable to delete old version folder");
        }
        if (FileUtils.fileExists("../" + which + "/versions.txt")) {
            System.out.println("\tDeleting file 'versions.txt'");
            errorMessageIfFalse(FileUtils.deleteDirectory("../" + which + "/versions.txt"), "Unable to delete 'versions.txt'");
        }
        if (FileUtils.fileExists("../" + version + ".zip")) {
            System.out.println("\tDeleting upload file ('" + version + ".zip')");
            errorMessageIfFalse(FileUtils.deleteDirectory("../" + version + ".zip"), "Unable to delete old upload file ('" + version + ".zip')");
        }

    }

    private static void createAndUploadLauncher() {
        System.out.println("\nCreate and upload launcher\n");
        System.out.println("\tChecking if 'launcher.jar' has been built");
        errorMessageIfFalse(FileUtils.fileExists("../../../intellij/out/artifacts/launcher_jar/launcher.jar"), "Make sure that the 'launcher.jar' artifact is built!");

        if (FileUtils.fileExists("../RPG Engine/launcher.jar")) {
            System.out.println("\tDeleting old launcher");
            FileUtils.deleteFile("../RPG Engine/launcher.jar");
        }
        System.out.println("\tCopying new built 'launcher.jar'");
        errorMessageIfFalse(FileUtils.copyFile("../../../intellij/out/artifacts/launcher_jar/launcher.jar", "../RPG Engine/launcher.jar"), "Unable to copy 'launcher.jar'");

        if (FileUtils.directoryExists("../RPG Engine/files")) {
            System.out.println("\tDeleting old 'files' directory");
            FileUtils.deleteDirectory("../RPG Engine/files");
        }
        System.out.println("\tCopying new 'files' directory");
        errorMessageIfFalse(FileUtils.copyDirectory("../../../intellij/launcher/files", "../RPG Engine/files"), "Unable to copy 'files' directory");
        System.out.println("\tDeleting unnecessary files from 'files' folder");
        FileUtils.deleteFile("../RPG Engine/files/create/versions.txt");
        FileUtils.deleteFile("../RPG Engine/files/play/versions.txt");

        System.out.println("\tPacking launcher files into a .zip file ('RPG Engine.zip')");
        errorMessageIfFalse(FileUtils.pack("../RPG Engine", "../RPG Engine.zip"), "Unable to pack RPG Engine launcher");

        System.out.println("\tUploading 'RPG Engine.zip'");
        errorMessageIfFalse(ftp.upload("../RPG Engine.zip", "projects/rpgengine/RPG Engine.zip"), "Unable to upload 'RPG Engine.zip'");

        System.out.println("\tUploading 'launcher.jar' to be the most recent version");
        errorMessageIfFalse(ftp.upload("../../../intellij/out/artifacts/launcher_jar/launcher.jar", "projects/rpgengine/launcher/launcher.jar"), "Unable to upload 'launcher.jar'");
        System.out.println("\tCreating version file and uploading it");
        errorMessageIfFalse(FileUtils.writeFile(new File("version.txt"), launcherVersion), "Unable to create 'version.txt' file");
        errorMessageIfFalse(ftp.upload("version.txt", "projects/rpgengine/launcher/version.txt"), "Unable to upload 'version.txt'");


        System.out.println("\tDeleting local files");
        if (FileUtils.fileExists("../RPG Engine.zip")) {
            System.out.println("\tDeleting file 'RPG Engine.zip'");
            FileUtils.deleteFile("../RPG Engine.zip");
        }
        if (FileUtils.fileExists("../RPG Engine/launcher.jar")) {
            System.out.println("\tDeleting file 'launcher.jar'");
            FileUtils.deleteFile("../RPG Engine/launcher.jar");
        }
        if (FileUtils.directoryExists("../RPG Engine/files")) {
            System.out.println("\tDeleting directory 'files'");
            FileUtils.deleteDirectory("../RPG Engine/files");
        }
        if (FileUtils.directoryExists("version.txt")) {
            System.out.println("\tDeleting file 'version.txt'");
            FileUtils.deleteDirectory("version.txt");
        }
    }

    private static void createAndUploadDocumentation() {
        System.out.println("\nCreate and upload documentation\n");
        if (FileUtils.directoryExists("../../rpg-engine-docs/site")) {
            System.out.println("\tDeleting local documentation");
            errorMessageIfFalse(FileUtils.deleteDirectory("../../rpg-engine-docs/site"), "Unable to delete current documentation");
        }

        System.out.println("\tCreating local documentation");
        errorMessageIfFalse(FileUtils.openFile("../../rpg-engine-docs/build.bat", "../../rpg-engine-docs"), "Cannot generate documentation by opening file\n../../rpg-engine-docs/build.bat");
        System.out.println("\tDeleting current online documentation");
        errorMessageIfFalse(ftp.removeDirectory("projects/rpgengine/documentation"), "Unable to delete current documentation");

        System.out.println("\tCreating online documentation directory");
        errorMessageIfFalse(ftp.createDirectory("projects/rpgengine/documentation"), "Unable to create new documentation folder");
        System.out.println("\tUploading local documentation");
        errorMessageIfFalse(ftp.uploadDirectory("projects/rpgengine/documentation", "../../rpg-engine-docs/site"), "Unable to upload new documentation");
    }

    private static void connectToFTP() {
        String[] loginData = FileUtils.readFile(new File("ftp.mydata"));
        errorMessageIfFalse(!(loginData == null || loginData.length < 4), "Missing or invalid file: ftp.mydata");

        assert loginData != null;
        ftp = new FTPTools(loginData[0], loginData[1], loginData[2], Integer.parseInt(loginData[3]));
        errorMessageIfFalse(ftp.isReady(), "Could not connect to server with login data");
    }

    private static void cleanUp() {
        System.out.println("\nCleaning up\n");
        if (FileUtils.directoryExists("../../rpg-engine-docs/site")) {
            System.out.println("\tDeleting local documentation");
            FileUtils.deleteDirectory("../../rpg-engine-docs/site");
        }
        if (FileUtils.fileExists("../RPG Engine/launcher.jar")) {
            System.out.println("\tDeleting 'launcher.jar'");
            FileUtils.deleteFile("../RPG Engine/launcher.jar");
        }
        if (FileUtils.directoryExists("../RPG Engine/files")) {
            System.out.println("\tDeleting 'files' directory");
            FileUtils.deleteDirectory("../RPG Engine/files");
        }
        if (ftp != null) {
            System.out.println("\tLogging out of ftp client");
            ftp.logout();
        }
    }

    private static void errorMessageIfFalse(boolean doNotShow, String message) {
        if (!doNotShow) {
            System.out.println("Error: " + message);
            Popup.error("Error", "An error occurred:\n" + message);
            cleanUp();
            System.exit(1);
        }
    }

}
