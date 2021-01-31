import javax.swing.*;

public class GuiCreateNewOnlineAdventure {
    private JPanel panel1;
    private JPanel mainPanel;
    private JButton createButton;
    private JButton cancelButton;
    private JTextField textFieldName;
    private JTextField textFieldDescription;
    private JTextField textFieldDownload;

    public GuiCreateNewOnlineAdventure(JFrame frame, GuiCommunity community) {
        createButton.addActionListener(e -> {
            submitAdventure(community, textFieldName.getText(), textFieldDescription.getText(), textFieldDownload.getText());
            frame.dispose();
        });
        cancelButton.addActionListener(e -> frame.dispose());
    }

    private void submitAdventure(GuiCommunity community, String advName, String advDescription, String advDownload) {
        community.submitAdventure(advName, advDescription, advDownload);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
