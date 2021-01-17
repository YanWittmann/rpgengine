public class Launcher {
    private final String version = "2";
    private Configuration playCFG, createCFG;
    private final GuiLauncher launcher;
    private boolean loadingScreenDone = false;

    public Launcher() {
        new Thread(() -> {
            GuiLoading loading = new GuiLoading();
            loading.setVisible(true);
            Sleep.milliseconds(4500);
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
                notifyUser("Synced local version list with online version list");
            launcher.setAvailableVersions(prepareVersionStringsForComboBox(playVersions, "play"), "play");
            launcher.setAvailableVersions(prepareVersionStringsForComboBox(createVersions, "create"), "create");
            while (!loadingScreenDone) Sleep.milliseconds(300);
            launcher.showMe();
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
        for (int i = 0; i < arguments.length; i++) newArguments[i] = arguments[i];
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
        launcher.setButtonsActive(false);
        version = version.replaceAll("(?:Version )?([^ ]+)(?: \\(installed\\))?", "$1");
        if (!isVersionInstalled(version, which)) { //need to install version
            notifyUser("Installing " + which + " version " + version);
            if (installVersion(version, which)) { //install version
                notifyUser("Installed " + which + " version " + version);
            } else { //version could not be installed
                notifyUser("Unable to install " + which + " version " + version);
                launcher.setButtonsActive(true);
            }
        }
        if (FileManager.fileExists("files/" + which + "/" + version + "/" + which + ".jar")) {
            FileManager.openJar("files/" + which + "/" + version + "/" + which + ".jar", "files/" + which + "/" + version, arguments);
            System.exit(0);
        } else {
            notifyUser("Something went wrong during the installation...");
            FileManager.deleteDirectoryRecursively("files/" + which + "/" + version);
            launcher.setButtonsActive(true);
        }
    }

    private boolean installVersion(String version, String which) {
        if (!FileManager.saveUrl("files/res/tmp/" + which + version, "http://yanwittmann.de/projects/rpgengine/" + which + "/" + version + ".zip"))
            return false;
        FileManager.makeDirectory("files/" + which + "/" + version);
        if (!FileManager.unzip("files/res/tmp/" + which + version, "files/" + which + "/")) return false;
        FileManager.delete("files/res/tmp/" + which + version);
        return true;
    }

    public void manageAdventures() {
        int operation = StaticStuff.openPopup("What do you want to do?", new String[]{"Install adventure from file", "Install adventure from URL", "Uninstall adventure", "Open adventure list", "Nothing"});
        switch (operation) {
            case 0: //Install adventure from file
                String[] files = FileManager.windowsFilePicker();
                for (String file : files) {
                    if (FileManager.copyFile(file, "files/adventures/" + FileManager.getFilename(file)))
                        notifyUser("Installed " + FileManager.getFilename(file));
                    else
                        notifyUser("Could not install " + FileManager.getFilename(file));
                }
                break;

            case 1: //Install adventure from URL
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
                break;

            case 2: //Uninstall adventure
                String[] adventures = StaticStuff.append(FileManager.getFilesWithEnding("files/adventures/", "adv"), "Cancel");
                int index = StaticStuff.openPopup("Which adventure do you want to uninstall?", adventures);
                if (index == 0) return;
                if (FileManager.delete("files/adventures/" + adventures[index]))
                    notifyUser("Uninstalled " + adventures[index]);
                else
                    notifyUser("Could not uninstall " + adventures[index]);
                break;

            case 3: //Open adventure list
                StaticStuff.openURL("http://yanwittmann.de/projects/rpgengine/site/Adventures.html");
                break;
        }
    }

    private void notifyUser(String text) {
        new GuiNotification(text);
    }

    public static void main(String[] args) {
        new Launcher();
    }
}