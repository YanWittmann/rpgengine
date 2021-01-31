import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

public class GuiBrowseAdventures {
    private JTextField textFieldSearchTerms;
    private JButton searchButton;
    private JButton installButton1;
    private JLabel labelUserProfilePicture1;
    private JButton nextPageButton;
    private JPanel mainPanel;
    private JLabel name1;
    private JLabel name2;
    private JLabel name3;
    private JLabel name4;
    private JLabel name5;
    private JLabel desc1;
    private JLabel desc3;
    private JLabel desc4;
    private JLabel desc5;
    private JLabel author1;
    private JLabel author2;
    private JLabel author3;
    private JLabel author4;
    private JLabel author5;
    private JLabel labelUserProfilePicture2;
    private JLabel labelUserProfilePicture3;
    private JLabel desc2;
    private JLabel labelUserProfilePicture4;
    private JLabel labelUserProfilePicture5;
    private JButton installButton2;
    private JButton installButton3;
    private JButton installButton4;
    private JButton installButton5;
    private JButton previousPageButton;

    private final Launcher launcher;

    public GuiBrowseAdventures(Launcher launcher) {
        this.launcher = launcher;

        searchButton.addActionListener(e -> searchForAdventures(textFieldSearchTerms.getText()));

        installButton1.addActionListener(e -> installAdventure(0));
        installButton2.addActionListener(e -> installAdventure(1));
        installButton3.addActionListener(e -> installAdventure(2));
        installButton4.addActionListener(e -> installAdventure(3));
        installButton5.addActionListener(e -> installAdventure(4));

        nextPageButton.addActionListener(e -> nextPage());

        previousPageButton.addActionListener(e -> previousPage());
    }

    private ArrayList<AdventureSearchResult> currentResults;
    private int currentResultsPage = 0;
    private static final int RESULTS_PER_PAGE = 5;

    public void searchForAdventures(String searchTerms) {
        currentResults = new ArrayList<>();

        String[] response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/get_all_adventures.php"));
        if (response.length > 0) {
            if (response[0].contains("ERROR:")) {
                GuiCommunity.showResponse(response[0]);
            } else {
                for (String adv : response) {
                    String[] split = adv.split(";;");
                    currentResults.add(new AdventureSearchResult(split[4], split[5], split[3], GuiCommunity.getUserImage(Integer.parseInt(split[1]), false, true), split[6], Integer.parseInt(split[0])));
                }
                Collections.reverse(currentResults);
            }
        }

        for (int i = currentResults.size() - 1; i >= 0; i--) {
            if (!currentResults.get(i).matchesSearchTerms(searchTerms))
                currentResults.remove(i);
        }

        setResults(currentResults);
    }

    public void setResults(ArrayList<AdventureSearchResult> results) {
        currentResults = results;
        currentResultsPage = 0;
        refreshResults();
    }

    public void nextPage() {
        if (currentResults == null) return;
        if ((currentResultsPage + 1) * RESULTS_PER_PAGE > currentResults.size() - 1) return;
        currentResultsPage++;
        refreshResults();
    }

    public void previousPage() {
        if (currentResults == null) return;
        if (currentResultsPage <= 0) return;
        currentResultsPage--;
        refreshResults();
    }

    private final String[] downloadLinks = new String[5];

    public void refreshResults() {
        resetResults();
        int maxIndex = Math.min((RESULTS_PER_PAGE * currentResultsPage) + 5, currentResults.size());
        int counter = 0;
        for (int i = RESULTS_PER_PAGE * currentResultsPage; i < maxIndex; i++) {
            switch (counter) {
                case 0 -> {
                    name1.setText(currentResults.get(i).getTitle());
                    desc1.setText(currentResults.get(i).getDescription());
                    author1.setText(currentResults.get(i).getAuthor());
                    labelUserProfilePicture1.setIcon(currentResults.get(i).getProfileIcon());
                }
                case 1 -> {
                    name2.setText(currentResults.get(i).getTitle());
                    desc2.setText(currentResults.get(i).getDescription());
                    author2.setText(currentResults.get(i).getAuthor());
                    labelUserProfilePicture2.setIcon(currentResults.get(i).getProfileIcon());
                }
                case 2 -> {
                    name3.setText(currentResults.get(i).getTitle());
                    desc3.setText(currentResults.get(i).getDescription());
                    author3.setText(currentResults.get(i).getAuthor());
                    labelUserProfilePicture3.setIcon(currentResults.get(i).getProfileIcon());
                }
                case 3 -> {
                    name4.setText(currentResults.get(i).getTitle());
                    desc4.setText(currentResults.get(i).getDescription());
                    author4.setText(currentResults.get(i).getAuthor());
                    labelUserProfilePicture4.setIcon(currentResults.get(i).getProfileIcon());
                }
                case 4 -> {
                    name5.setText(currentResults.get(i).getTitle());
                    desc5.setText(currentResults.get(i).getDescription());
                    author5.setText(currentResults.get(i).getAuthor());
                    labelUserProfilePicture5.setIcon(currentResults.get(i).getProfileIcon());
                }
            }
            downloadLinks[counter] = currentResults.get(i).getDownloadLink();
            counter++;
        }
    }

    private void resetResults() {
        name1.setText("Type in search terms above or leave them empty.");
        desc1.setText("Click search to find fitting adventures!");
        author1.setText("");
        labelUserProfilePicture1.setIcon(null);

        name2.setText("");
        desc2.setText("");
        author2.setText("");
        labelUserProfilePicture2.setIcon(null);

        name3.setText("");
        desc3.setText("");
        author3.setText("");
        labelUserProfilePicture3.setIcon(null);

        name4.setText("");
        desc4.setText("");
        author4.setText("");
        labelUserProfilePicture4.setIcon(null);

        name5.setText("");
        desc5.setText("");
        author5.setText("");
        labelUserProfilePicture5.setIcon(null);
    }

    private void installAdventure(int resultIndex) {
        launcher.installAdventure(downloadLinks[resultIndex].replace("<br>", ""));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
