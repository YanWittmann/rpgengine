import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class GuiCommunity {
    private JTextField textFieldUserName;
    private JPasswordField passwordField;
    private JButton logInButton;
    private JButton createAccountButton;
    private JButton setProfilePictureButton;
    private JButton browseAdventuresButton;
    private JButton submitAdventureButton;
    private JButton editSubmittedAdventureButton;
    private JButton setNameButton;
    private JButton setPasswordButton;
    private JPanel mainPanel;
    private JLabel labelProfilePicture;
    private Launcher launcher;

    public GuiCommunity(Launcher launcher) {
        this.launcher = launcher;

        logInButton.addActionListener(e -> new Thread(() -> logIn(textFieldUserName.getText(), String.valueOf(passwordField.getPassword()), false)).start());

        createAccountButton.addActionListener(e -> new Thread(() -> createAccount(textFieldUserName.getText(), String.valueOf(passwordField.getPassword()))).start());

        setNameButton.addActionListener(e -> new Thread(this::setUserName).start());

        setPasswordButton.addActionListener(e -> new Thread(this::setUserPassword).start());

        setProfilePictureButton.addActionListener(e -> new Thread(this::setProfilePicture).start());

        browseAdventuresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseAdventures();
            }
        });

        submitAdventureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewAdventure();
            }
        });

        editSubmittedAdventureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageAdventures();
            }
        });
        attemptToRelogin();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private static String loggedInUserName = "";
    private String loggedInUserPassword = "";
    private int loggedInUserID = -1;

    public void createAccount(String userName, String password) {
        String email = StaticStuff.openPopup("Enter a valid email.<br>Leave empty to cancel.", "");
        if (email.length() == 0) return;
        String[] isValidUser = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/create_user.php?user_name=" + userName + "&e_mail=" + email + "&password=" + password));
        if (isValidUser.length > 0) {
            if (isValidUser[0].contains("ERROR:")) {
                showResponse(isValidUser[0]);
            } else {
                logIn(userName, password, true);
                new Thread(() -> StaticStuff.openPopup("Created new user [[gold:" + userName + "]]")).start();
            }
        }
    }

    public void logIn(String userName, String password, boolean silent) {
        String[] isValidUser = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/is_valid_user.php?user_name=" + userName + "&password=" + password + "&response=true"));
        if (isValidUser.length > 0) {
            if (isValidUser[0].contains("ERROR:")) {
                if (!silent) showResponse(isValidUser[0]);
            } else {
                String[] userID = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/user_name_to_id.php?user_name=" + userName + "&response=true"));
                if (userID.length > 0) {
                    if (userID[0].contains("ERROR:")) {
                        if (!silent) showResponse(userID[0]);
                    } else {
                        loggedInUserName = userName;
                        loggedInUserPassword = password;
                        loggedInUserID = Integer.parseInt(userID[0]);
                        labelProfilePicture.setIcon(getUserImage(loggedInUserID, false, false));
                        launcher.setMainCFGentry("userName", loggedInUserName);
                        launcher.setMainCFGentry("userPassword", loggedInUserPassword);
                        textFieldUserName.setText(loggedInUserName);
                        passwordField.setText("");
                        if (!silent)
                            new Thread(() -> StaticStuff.openPopup("Logged in as [[gold:" + loggedInUserName + "]]")).start();
                    }
                }
            }
        } else if (!silent) new Thread(() -> StaticStuff.openPopup("[[red:Cannot connect to server]]")).start();
    }

    public void setProfilePicture() {
        String image = StaticStuff.openPopup("Enter a valid image url.<br>Leave empty to cancel.", "");
        if (image.length() == 0) return;
        String[] response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/set_profile_image.php?user_name=" + loggedInUserName + "&password=" + loggedInUserPassword + "&image=" + image));
        if (response.length > 0) {
            if (response[0].contains("ERROR:")) {
                showResponse(response[0]);
            } else {
                new Thread(() -> StaticStuff.openPopup("Set new profile picture.")).start();
                labelProfilePicture.setIcon(getUserImage(loggedInUserID, true, false));
            }
        }
    }

    public void setUserName() {
        String newUserName = StaticStuff.openPopup("Enter a valid new name.<br>Username may only contain the following characters:<br>A-Z a-z 0-9 _ + and length must be between 2 and 30<br>Leave empty to cancel.", "");
        if (newUserName.length() == 0) return;
        String[] response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/set_user_name.php?user_name=" + loggedInUserName + "&password=" + loggedInUserPassword + "&new_user_name=" + newUserName));
        if (response.length > 0) {
            if (response[0].contains("ERROR:")) {
                showResponse(response[0]);
            } else {
                logIn(newUserName, loggedInUserPassword, false);
            }
        }
    }

    public void setUserPassword() {
        String newUserPassword = StaticStuff.openPopup("Enter a valid new password.<br>Password can only contain digits and lower/uppercase characters.<br>Length must be between 8 and 32<br>Leave empty to cancel.", "");
        if (newUserPassword.length() == 0) return;
        String[] response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/set_user_password.php?user_name=" + loggedInUserName + "&password=" + loggedInUserPassword + "&new_user_password=" + newUserPassword));
        if (response.length > 0) {
            if (response[0].contains("ERROR:")) {
                showResponse(response[0]);
            } else {
                logIn(loggedInUserName, newUserPassword, false);
            }
        }
    }

    public void createNewAdventure() {
        JFrame createNewAdventure = new JFrame("Create new adventure");
        GuiCreateNewOnlineAdventure createNewOnlineAdventure = new GuiCreateNewOnlineAdventure(createNewAdventure, this);
        createNewAdventure.setContentPane(createNewOnlineAdventure.getMainPanel());
        createNewAdventure.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createNewAdventure.setLocationRelativeTo(null);
        createNewAdventure.setIconImage(new ImageIcon("files/res/img/iconyellow.png").getImage());
        createNewAdventure.pack();
        createNewAdventure.setVisible(true);
    }

    public void submitAdventure(String advName, String advDescription, String advDownload) {
        String[] response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/create_adventure.php?user_name=" + loggedInUserName + "&password=" + loggedInUserPassword + "&adventure_name=" + advName + "&adventure_description=" + advDescription + "&adventure_link=" + advDownload));
        if (response.length > 0) {
            if (response[0].contains("ERROR:")) {
                showResponse(response[0]);
            } else {
                browseAdventures();
                searchAdventureTerms(advName + " " + advDescription);
                showResponse("[[green:Created adventure entry]]!");
            }
        }
    }

    public void manageAdventures() {
        String[] response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/get_adventures_from_user.php?user_id=" + loggedInUserID));
        if (response.length > 0) {
            if (response[0].contains("ERROR:")) {
                showResponse(response[0]);
            } else {
                AdventureSearchResult[] results = new AdventureSearchResult[response.length];
                for (int i = 0; i < response.length; i++) {
                    String[] split = response[i].split(";;");
                    results[i] = new AdventureSearchResult(split[4], split[5], split[3], GuiCommunity.getUserImage(Integer.parseInt(split[1]), false, true), split[6], Integer.parseInt(split[0]));
                }
                AdventureSearchResult adventure = Popup.dropDown("Manage adventures", "What adventure do you want to edit?", results);
                if (adventure == null) return;
                manageAdventure(adventure);
            }
        }
    }

    private static final String[] MANAGE_ADVENTURE_ACTIONS = new String[]{"Edit name", "Edit description", "Edit download link", "Delete"};

    public void manageAdventure(AdventureSearchResult adventure) {
        int result = Popup.selectButton("Manage adventures", "What do you want to do?", MANAGE_ADVENTURE_ACTIONS);
        String[] response;
        String input;
        switch (result) {
            case 0:
                input = Popup.input("Enter a new name:", "");
                if(input == null)  return; if(input.length() == 0) return;
                response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/edit_adventure_name.php?user_name=" + loggedInUserName + "&password=" + loggedInUserPassword + "&adventure_id=" + adventure.getId() + "&new_attribute=" + input));
                if (response.length > 0) {
                    if (response[0].contains("ERROR:")) {
                        showResponse(response[0]);
                    } else {
                        showResponse("Edited adventure name.");
                    }
                }
                break;
            case 1:
                input = Popup.input("Enter a new description:", "");
                if(input == null)  return; if(input.length() == 0) return;
                response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/edit_adventure_description.php?user_name=" + loggedInUserName + "&password=" + loggedInUserPassword + "&adventure_id=" + adventure.getId() + "&new_attribute=" + input));
                if (response.length > 0) {
                    if (response[0].contains("ERROR:")) {
                        showResponse(response[0]);
                    } else {
                        showResponse("Edited adventure description.");
                    }
                }
                break;
            case 2:
                input = Popup.input("Enter a new download link:", "");
                if(input == null)  return; if(input.length() == 0) return;
                response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/edit_adventure_download_link.php?user_name=" + loggedInUserName + "&password=" + loggedInUserPassword + "&adventure_id=" + adventure.getId() + "&new_attribute=" + input));
                if (response.length > 0) {
                    if (response[0].contains("ERROR:")) {
                        showResponse(response[0]);
                    } else {
                        showResponse("Edited adventure download link.");
                    }
                }
                break;
            case 3:
                if (!Popup.input("Type in 'delete' to confirm:", "").equals("delete")) return;
                response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/remove_adventure.php?user_name=" + loggedInUserName + "&password=" + loggedInUserPassword + "&adventure_id=" + adventure.getId()));
                if (response.length > 0) {
                    if (response[0].contains("ERROR:")) {
                        showResponse(response[0]);
                    } else {
                        showResponse("Deleted adventure.");
                    }
                }
        }
    }

    public void searchAdventureTerms(String terms) {
        launcher.searchAdventureTerms(terms);
    }

    public void browseAdventures() {
        launcher.openBorwseAdventures();
    }

    private static final String DEFAULT_PROFILE_PICTURE = "files/res/img/profilepictures/default.png";
    private static final String PROFILE_PICTURES_LOCATION = "files/res/img/profilepictures/";

    private static final HashMap<Integer, ImageIcon> profilePicturesSmall = new HashMap<>();
    private static final HashMap<Integer, ImageIcon> profilePicturesLarge = new HashMap<>();

    public static ImageIcon getUserImage(int userID, boolean forceReload, boolean small) {
        if (!FileManager.fileExists(PROFILE_PICTURES_LOCATION))
            FileManager.makeDirectory(PROFILE_PICTURES_LOCATION);

        if (!forceReload)
            if (profilePicturesSmall.containsKey(userID) && small)
                return profilePicturesSmall.get(userID);
            else if (profilePicturesLarge.containsKey(userID) && !small)
                return profilePicturesLarge.get(userID);

        String[] getProfilePicture = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/get_profile_image.php?user_id=" + userID + "&response=true"));
        if (getProfilePicture.length > 0) {
            if (getProfilePicture[0].contains("ERROR:")) {
                showResponse(getProfilePicture[0]);
            } else {
                int profPicSize = FileManager.getFileSize(getProfilePicture[0]);
                if (profPicSize < 50000 && profPicSize != -1) {
                    FileManager.saveUrl(PROFILE_PICTURES_LOCATION + userID + ".png", getProfilePicture[0]);
                    profilePicturesSmall.remove(userID);
                    profilePicturesLarge.remove(userID);
                    profilePicturesSmall.put(userID, StaticStuff.getScaledImage(new ImageIcon(PROFILE_PICTURES_LOCATION + userID + ".png"), 32, 32));
                    profilePicturesLarge.put(userID, StaticStuff.getScaledImage(new ImageIcon(PROFILE_PICTURES_LOCATION + userID + ".png"), 64, 64));
                    if (small)
                        return profilePicturesSmall.get(userID);
                    else
                        return profilePicturesLarge.get(userID);
                } else if (profPicSize == -1) {
                    new Thread(() -> StaticStuff.openPopup("[[red:Invalid profile picture: " + loggedInUserName + "]]")).start();
                } else {
                    new Thread(() -> StaticStuff.openPopup("[[red:Profile picture too large: " + loggedInUserName + "]]")).start();
                }
            }
        }

        if (!FileManager.fileExists(DEFAULT_PROFILE_PICTURE))
            FileManager.saveUrl(DEFAULT_PROFILE_PICTURE, "http://yanwittmann.de/projects/rpgengine/database/missing_profile_picture.png");
        profilePicturesLarge.put(userID, StaticStuff.getScaledImage(new ImageIcon(DEFAULT_PROFILE_PICTURE), 64, 64));
        profilePicturesSmall.put(userID, StaticStuff.getScaledImage(new ImageIcon(DEFAULT_PROFILE_PICTURE), 32, 32));
        if (small)
            return profilePicturesSmall.get(userID);
        else
            return profilePicturesLarge.get(userID);
    }

    public void attemptToRelogin() {
        String userName = launcher.getMainCFGentry("userName");
        String userPassword = launcher.getMainCFGentry("userPassword");

        if (userName.length() == 0 || userPassword.length() == 0) return;

        logIn(userName, userPassword, true);
    }

    static void showResponse(String response) {
        if (response.contains("ERROR:")) {
            new Thread(() -> StaticStuff.openPopup("[[red:" + response + "]]")).start();
        } else if (response.length() > 0) {
            new Thread(() -> StaticStuff.openPopup(response)).start();
        }
    }
}
