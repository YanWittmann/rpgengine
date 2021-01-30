import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Launcher {
    private final String version = "7";
    private final Configuration mainCFG;
    private final GuiLauncher launcher;
    private boolean loadingScreenDone = false, isDownloadingNewVersion = false;

    public Launcher() {
        mainCFG = new Configuration("files/res/txt/main.cfg");
        new Thread(() -> {
            GuiLoading loading = new GuiLoading();
            loading.setVisible(true);
            if (!mainCFG.get("fastsetup").equals("true"))
                Sleep.milliseconds(4500);
            else Sleep.milliseconds(400);
            loading.setVisible(false);
            loadingScreenDone = true;
        }).start();
        launcher = new GuiLauncher(this);
        new Thread(() -> {
            updateLauncher();
            loadArguments();
            String[] playVersions = getAvailableVersions("play");
            String[] createVersions = getAvailableVersions("create");
            if (playVersions == null || createVersions == null) {
                StaticStuff.openPopup("You need to be connected to the internet\nat least once to download the available versions.");
                System.exit(0);
            }
            if (FileManager.connectedToInternet())
                notifyUser("Version list updated");
            launcher.setAvailableVersions(prepareVersionStringsForComboBox(playVersions, "play"), "play");
            launcher.setAvailableVersions(prepareVersionStringsForComboBox(createVersions, "create"), "create");
            if (!mainCFG.get("fastsetup").equals("true"))
                while (!loadingScreenDone) Sleep.milliseconds(300);
            launcher.showMe(mainCFG.get("fastsetup").equals("true"));
        }).start();
    }

    private void updateLauncher() {
        if (!getOnlineLauncherVersion().equals(getLauncherVersion())) {
            FileManager.openJar("files/res/exe/updater.jar", "files/res/exe", new String[]{});
            System.exit(0);
        }
    }

    private String getOnlineLauncherVersion() {
        if (!FileManager.connectedToInternet()) return getLauncherVersion();
        return FileManager.getTextFromURL("http://yanwittmann.de/projects/rpgengine/launcher/version.txt")[0];
    }

    private String getLauncherVersion() {
        return version;
    }

    private boolean isVersionInstalled(String version, String which) {
        return FileManager.pathExists("files/" + which + "/" + version);
    }

    private String[] getAvailableVersions(String which) {
        String[] results = null;
        if (FileManager.connectedToInternet()) { //get online versions
            results = FileManager.getTextFromURL("http://yanwittmann.de/projects/rpgengine/" + which + "/versions.txt");
            FileManager.makeDirectory("files/" + which);
            FileManager.writeToFile("files/" + which + "/versions.txt", results);
        } else if (FileManager.fileExists("files/" + which + "/versions.txt")) { //get offline versions
            results = FileManager.readFile("files/" + which + "/versions.txt");
        } else { //never even started engine online and is offline
            results = new String[]{};
        }
        return results;
    }

    private String[] prepareVersionStringsForComboBox(String[] versions, String which) {
        for (int i = 0; i < versions.length; i++) {
            if (isVersionInstalled(versions[i], which))
                versions[i] = "Version " + versions[i] + " (installed)";
            else
                versions[i] = "Version " + versions[i];
        }
        return versions;
    }

    public void startVersionWithParameters(String version, String which) {
        if (which.equals("play")) {
            new GuiArguments(this, arguments, version);
        }
    }

    private void loadArguments() {
        if (FileManager.fileExists("files/res/txt/args.advdata"))
            arguments = FileManager.readFile("files/res/txt/args.advdata");
        else {
            FileManager.makeDirectory("files");
            FileManager.makeDirectory("files/res");
            FileManager.makeDirectory("files/res/txt");
            arguments = new String[]{};
        }
        addArgumentIfNotContain("lang", "english");
        addArgumentIfNotContain("scale", "100");
        addArgumentIfNotContain("textSpeed", "100");
        addArgumentIfNotContain("forceDebug", "false");
        FileManager.writeToFile("files/res/txt/args.advdata", arguments);
    }

    private void addArgumentIfNotContain(String name, String value) {
        for (String argument : arguments) {
            if (argument.replaceAll("([^:]+):(.+)", "$1").equals(name)) return;
        }
        String[] newArguments = new String[arguments.length + 1];
        System.arraycopy(arguments, 0, newArguments, 0, arguments.length);
        newArguments[newArguments.length - 1] = name + ":" + value;
        arguments = newArguments;
    }

    public void setArguments(String[] args) {
        arguments = args;
        if (!FileManager.fileExists("files/res/txt/args.advdata")) {
            FileManager.makeDirectory("files");
            FileManager.makeDirectory("files/res");
            FileManager.makeDirectory("files/res/txt");
        }
        FileManager.writeToFile("files/res/txt/args.advdata", arguments);
    }

    private String[] arguments = new String[]{"lang:english", "scale:100"};

    public void startVersion(String version, String which) {
        if (isDownloadingNewVersion) return;
        isDownloadingNewVersion = true;
        launcher.setButtonsActive(false);
        version = version.replaceAll("(?:Version )?([^ ]+)(?: \\(installed\\))?", "$1");
        if (!isVersionInstalled(version, which)) { //need to install version
            if (!installVersion(version, which)) { //install version
                launcher.setButtonsActive(true); //version could not be installed
                isDownloadingNewVersion = false;
            }
        }
        if (FileManager.fileExists("files/" + which + "/" + version + "/" + which + ".jar")) {
            FileManager.openJar("files/" + which + "/" + version + "/" + which + ".jar", "files/" + which + "/" + version, arguments);
            closeAfterLaunching();
            isDownloadingNewVersion = false;
        } else {
            notifyUser("Something went wrong during the installation...");
            FileManager.deleteDirectoryRecursively("files/" + which + "/" + version);
            launcher.setButtonsActive(true);
            isDownloadingNewVersion = false;
        }
    }

    public boolean installVersion(String version, String which) {
        notifyUser("Installing " + which + " version " + version);
        isDownloadingNewVersion = true;

        FileManager.makeDirectory("files/" + which);
        FileManager.makeDirectory("files/res/tmp/");

        if (!FileManager.saveUrl("files/res/tmp/" + which + version + ".zip", "http://yanwittmann.de/projects/rpgengine/" + which + "/" + version + ".zip")) {
            isDownloadingNewVersion = false;
            notifyUser("Unable to install " + which + " version " + version);
            return false;
        }

        FileManager.makeDirectory("files/" + which + "/" + version);
        if (!FileManager.unzip("files/res/tmp/" + which + version + ".zip", "files/" + which + "/")) {
            isDownloadingNewVersion = false;
            notifyUser("Unable to install " + which + " version " + version);
            return false;
        }

        FileManager.delete("files/res/tmp/" + which + version + ".zip");
        isDownloadingNewVersion = false;
        notifyUser("Installed " + which + " version " + version);
        launcher.setAvailableVersions(prepareVersionStringsForComboBox(getAvailableVersions("play"), "play"), "play");
        launcher.setAvailableVersions(prepareVersionStringsForComboBox(getAvailableVersions("create"), "create"), "create");
        return true;
    }

    public void manageAdventures() {
        int operation = StaticStuff.openPopup("What do you want to do?", new String[]{"Install adventure from file", "Install adventure from URL", "Uninstall adventure", "Open adventure list", "Nothing"});
        switch (operation) {
//Install adventure from file
            case 0 -> {
                String[] files = FileManager.windowsFilePicker();
                for (String file : files) {
                    if (FileManager.copyFile(file, "files/adventures/" + FileManager.getFilename(file)))
                        notifyUser("Installed " + FileManager.getFilename(file));
                    else
                        notifyUser("Could not install " + FileManager.getFilename(file));
                }
            }
//Install adventure from URL
            case 1 -> {
                String url = StaticStuff.openPopup("<html>" + StaticStuff.prepareString("Enter a valid URL (with [[green:http://]] or [[green:https://]]):"), "");
                if (url.contains("http://") || url.contains("https://")) ;
                else if (url.equals("")) return;
                else {
                    notifyUser("Could not install adventure (malformed URL)");
                    return;
                }
                if (FileManager.saveUrl("files/adventures/" + FileManager.getFilename(url).replace("%20", " "), url))
                    notifyUser("Installed " + FileManager.getFilename(url).replace("%20", " "));
                else
                    notifyUser("Could not install " + FileManager.getFilename(url).replace("%20", " "));
            }
//Uninstall adventure
            case 2 -> {
                String[] adventures = StaticStuff.append(FileManager.getFilesWithEnding("files/adventures/", "adv"), "Cancel");
                int index = StaticStuff.openPopup("Which adventure do you want to uninstall?", adventures);
                if (index == 0) return;
                if (FileManager.delete("files/adventures/" + adventures[index]))
                    notifyUser("Uninstalled " + adventures[index]);
                else
                    notifyUser("Could not uninstall " + adventures[index]);
            }
//Open adventure list
            case 3 -> StaticStuff.openURL("http://yanwittmann.de/projects/rpgengine/site/Adventures.html");
        }
    }

    private void notifyUser(String text) {
        new GuiNotification(text);
    }

    public void toggleFastMode() {
        mainCFG.set("fastsetup", !mainCFG.get("fastsetup").equals("true") + "");
        if (mainCFG.get("fastsetup").equals("true"))
            notifyUser("The launcher will now start up in fast mode");
        else notifyUser("The launcher will now start up in slow mode");
    }

    public boolean getFastMode() {
        return mainCFG.get("fastsetup").equals("true");
    }

    public void toggleStayMode() {
        mainCFG.set("stayopen", !mainCFG.get("stayopen").equals("true") + "");
        if (mainCFG.get("stayopen").equals("true"))
            notifyUser("The launcher will now stay open after launching a version");
        else notifyUser("The launcher will now close open after launching a version");
    }

    public boolean getStayMode() {
        return mainCFG.get("stayopen").equals("true");
    }

    public void closeAfterLaunching() {
        if (!getStayMode()) System.exit(0);
        else launcher.setState(Frame.ICONIFIED);
    }

    private GuiCommunity community;
    private boolean communityIsOpen = false;

    public void openCommunityManager() {
        if(communityIsOpen) return;
        if(community == null) community = new GuiCommunity(this);
        JFrame communityManager = new JFrame("Community Manager");
        community = new GuiCommunity(this);
        communityManager.setContentPane(community.getMainPanel());
        communityManager.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        communityManager.setLocationRelativeTo(null);
        communityManager.setIconImage(new ImageIcon("files/res/img/iconyellow.png").getImage());
        communityManager.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                communityIsOpen = false;
                e.getWindow().dispose();
            }
        });
        communityManager.pack();
        communityManager.setVisible(true);
        communityIsOpen = true;
    }

    public static void main(String[] args) {
        new Launcher();
    }
}
