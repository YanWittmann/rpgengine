
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GuiLogger extends JFrame {
    private JPanel middlePanel;
    private JTextArea display;
    private JTextField input;

    public GuiLogger(String name, ArrayList<String> text) {
        super(name);
        setResizable(true);
        this.text = text;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());

        input = new JTextField();
        input.setText("");
        input.setEditable(true);
        input.setPreferredSize(new Dimension(1000, 20));
        input.addActionListener(e -> {
            executeCommand(input.getText());
            input.setText("");
        });
        middlePanel.add(input, BorderLayout.PAGE_START);

        display = new JTextArea(16, 58);
        display.setPreferredSize(new Dimension(1000, 300));
        display.setText("");
        display.setEditable(false);
        display.addMouseWheelListener(this::scroll);
        middlePanel.add(display, BorderLayout.PAGE_END);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int height = e.getComponent().getHeight();
                display.setPreferredSize(new Dimension(1000, height - 60));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }
        });

        add(middlePanel);

        setIconImage(new ImageIcon("res/img/iconred.png").getImage());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void scroll(MouseWheelEvent evt) {
        if (!(evt.getUnitsToScroll() > 0)) {
            //if(offset>0) offset--;
            offset = Math.max(0, offset - 2);
        } else
            offset = offset + 2;
        updateTextWithoutAddScroll();
    }

    private int offset = 0;
    private ArrayList<String> text;

    public void updateText() {
        if (text.size() > 16) offset++;
        StringBuilder displayString = new StringBuilder();
        for (int i = offset; i < text.size(); i++) displayString.append(text.get(i)).append("\n");
        display.setText(StaticStuff.replaceLast(displayString.toString(), "\n", ""));
    }

    public void updateTextWithoutAddScroll() {
        StringBuilder displayString = new StringBuilder("");
        for (int i = offset; i < text.size(); i++) displayString.append(text.get(i)).append("\n");
        display.setText(StaticStuff.replaceLast(displayString.toString(), "\n", ""));
    }

    public void executeCommand(String text) {
        new Thread(() -> Interpreter.executePlayerCommandFromLogger(text)).start();

    }
}