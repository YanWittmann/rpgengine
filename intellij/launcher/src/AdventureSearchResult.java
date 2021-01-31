import javax.swing.*;

public class AdventureSearchResult {
    private final String title, description, author, downloadLink;
    private final ImageIcon profileIcon;
    private final int id;

    public AdventureSearchResult(String title, String description, String author, ImageIcon profileIcon, String downloadLink, int id) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.downloadLink = downloadLink;
        this.profileIcon = profileIcon;
        this.id = id;
    }

    public ImageIcon getProfileIcon() {
        return profileIcon;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public boolean matchesSearchTerms(String search) {
        for (String s : search.split(" "))
            if (s.length() > 0)
                if (!(description.contains(s) || title.contains(s) || author.contains(s))) return false;
        return true;
    }

    @Override
    public String toString() {
        return title;
    }
}
