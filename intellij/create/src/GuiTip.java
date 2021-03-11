import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GuiTip extends JFrame {
    private static final int IMAGE_WIDTH = 400;
    public static final int IMAGE_HEIGHT = 203;
    private final ArrayList<Tip> tips = new ArrayList<>();
    private JLabel l_name;
    private JLabel l_desc;
    private static JLabel l_image;
    private JLabel l_tipIndex;

    public GuiTip() {
        this.setTitle(StaticStuff.PROJECT_NAME + " - Tips");
        this.setIconImage(new ImageIcon("res/img/iconblue.png").getImage());
        this.setSize(241 + IMAGE_WIDTH, 87 + IMAGE_HEIGHT);
        String[] response = StaticStuff.filterEmptyLines(FileManager.getResponseFromURL("http://yanwittmann.de/projects/rpgengine/database/get_tips.php"));
        Arrays.setAll(response, i -> response[i].replaceAll("(.+)<br>", "$1"));
        if (response.length > 0) {
            String[] responses = response[0].split("<br>");
            for (String resp : responses) {
                String[] splitted = resp.split(";;");
                try {
                    Tip tip = new Tip(splitted[1], splitted[2], splitted[3]);
                    if (tip.isValid())
                        tips.add(tip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (tips.size() == 0) return;

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(241 + IMAGE_WIDTH, 87 + IMAGE_HEIGHT));
        contentPane.setBackground(new Color(0, 0, 0));

        l_name = new JLabel();
        l_name.setBounds(24, 35, 450, 26);
        l_name.setBackground(new Color(255, 255, 255));
        l_name.setForeground(new Color(255, 255, 255));
        l_name.setEnabled(true);
        l_name.setFont(new Font("sansserif", Font.PLAIN, 12));
        l_name.setText("Tip Name");
        l_name.setVisible(true);

        l_desc = new JLabel();
        l_desc.setBounds(24, 65, 190, IMAGE_HEIGHT);
        l_desc.setBackground(new Color(255, 255, 255));
        l_desc.setForeground(new Color(255, 255, 255));
        l_desc.setEnabled(true);
        l_desc.setFont(new Font("sansserif", Font.PLAIN, 12));
        l_desc.setText("Tip Description");
        l_desc.setVerticalAlignment(SwingConstants.TOP);
        l_desc.setVisible(true);

        l_image = new JLabel();
        l_image.setBounds(230, 65, IMAGE_WIDTH, IMAGE_HEIGHT);
        l_image.setBackground(new Color(255, 255, 255));
        l_image.setForeground(new Color(255, 255, 255));
        l_image.setEnabled(true);
        l_image.setFont(new Font("sansserif", Font.PLAIN, 12));
        l_image.setText("");
        l_image.setVisible(true);
        l_image.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                openImage();
            }
        });

        l_tipIndex = new JLabel();
        l_tipIndex.setBounds(14, 10, 43, 20);
        l_tipIndex.setBackground(new Color(255, 255, 255));
        l_tipIndex.setForeground(new Color(255, 255, 255));
        l_tipIndex.setEnabled(true);
        l_tipIndex.setFont(new Font("sansserif", Font.PLAIN, 12));
        l_tipIndex.setText("<html>Tip <b>#1");
        l_tipIndex.setVisible(true);

        JLabel l_prev = new JLabel();
        l_prev.setBounds(63, 9, 20, 22);
        l_prev.setBackground(new Color(255, 255, 255));
        l_prev.setForeground(new Color(255, 255, 255));
        l_prev.setEnabled(true);
        l_prev.setFont(new Font("sansserif", Font.PLAIN, 12));
        l_prev.setText("<<");
        l_prev.setVisible(true);
        l_prev.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                previousTip();
            }
        });

        JLabel l_next = new JLabel();
        l_next.setBounds(83, 9, 20, 22);
        l_next.setBackground(new Color(255, 255, 255));
        l_next.setForeground(new Color(255, 255, 255));
        l_next.setEnabled(true);
        l_next.setFont(new Font("sansserif", Font.PLAIN, 12));
        l_next.setText(">>");
        l_next.setVisible(true);
        l_next.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                nextTip();
            }
        });

        //adding components to contentPane panel
        contentPane.add(l_name);
        contentPane.add(l_desc);
        contentPane.add(l_image);
        contentPane.add(l_tipIndex);
        contentPane.add(l_prev);
        contentPane.add(l_next);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();

        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        showRandomTip();
    }

    private int currentTipIndex = 0;

    private void showRandomTip() {
        if (tips.size() == 0) return;
        currentTipIndex = StaticStuff.randomNumber(0, tips.size() - 1);
        updateTipDisplay();
    }

    private void nextTip() {
        currentTipIndex = (currentTipIndex + 1) % tips.size();
        updateTipDisplay();
    }

    private void previousTip() {
        currentTipIndex = (currentTipIndex - 1);
        if (currentTipIndex < 0) currentTipIndex = tips.size() - 1;
        updateTipDisplay();
    }

    private void updateTipDisplay() {
        Tip selectedTip = tips.get(currentTipIndex);
        ImageIcon img = selectedTip.getImage();
        if (img == null) l_image.setIcon(null);
        else {
            l_image.setBounds(l_image.getX(), l_image.getY(), img.getIconWidth(), img.getIconHeight());
            l_image.setIcon(img);
        }
        l_name.setText("<html><u><b>" + selectedTip.getName());
        l_desc.setText("<html>" + selectedTip.getDesc());
        l_tipIndex.setText("<html>Tip <b>#" + selectedTip.getIndex());
        prepareNextAndPrevTip();
    }

    private void prepareNextAndPrevTip() {
        new Thread(() -> {
            currentTipIndex = (currentTipIndex + 1) % tips.size();
            tips.get(currentTipIndex).getImage();
            currentTipIndex = (currentTipIndex - 1);
            if (currentTipIndex < 0) currentTipIndex = tips.size() - 1;
            currentTipIndex = (currentTipIndex - 1);
            if (currentTipIndex < 0) currentTipIndex = tips.size() - 1;
            tips.get(currentTipIndex).getImage();
            currentTipIndex = (currentTipIndex + 1) % tips.size();
        }).start();
    }

    public void open() {
        this.setVisible(true);
    }

    private void openImage() {
        try {
            Image img = tips.get(currentTipIndex).getOriginalImage().getImage();
            BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bi.createGraphics();
            g2.drawImage(img, 0, 0, null);
            g2.dispose();
            ImageIO.write(bi, "png", new File("res/tmp/openimg.png"));
            FileManager.openFile("res/tmp/openimg.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Tip {
        private static int index_counter = 0;
        private final int index;
        private final String name, desc;
        private ImageIcon scaledImage = null;
        private ImageIcon originalImage = null;
        private final String imageURL;

        public Tip(String name, String desc, String image) {
            index_counter++;
            this.index = index_counter;
            this.name = name;
            this.desc = desc.replace("EOL", "<br>");
            this.imageURL = image;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public ImageIcon getImage() {
            if (scaledImage == null) {
                FileManager.saveUrl("res/tmp/tipimg.png", "http://yanwittmann.de/projects/rpgengine/database/tipimg/" + imageURL);
                BufferedImage inputImage;
                try {
                    inputImage = ImageIO.read(new File("res/tmp/tipimg.png"));
                } catch (IOException e) {
                    this.scaledImage = null;
                    return null;
                }
                FileManager.delete("res/tmp/tipimg.png");
                int[] imgSize = getMaxDimensionsForCanvas(IMAGE_WIDTH, IMAGE_HEIGHT, inputImage.getWidth(), inputImage.getHeight());
                originalImage = new ImageIcon(inputImage);
                this.scaledImage = getScaledImage(new ImageIcon(inputImage), imgSize[0], imgSize[1]);
            }
            return scaledImage;
        }

        public ImageIcon getOriginalImage() {
            if(originalImage == null) getImage();
            return originalImage;
        }

        public boolean isValid() {
            return name.length() > 0 && desc.length() > 0;
        }
    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    public static ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        Image image = srcImg.getImage();
        Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    public static int[] getMaxDimensionsForCanvas(int maxX, int maxY, int x, int y) {
        int[] dimensions = new int[2];

        double scaleX = Double.parseDouble(maxX + "") / Double.parseDouble(x + "");
        double scaleY = Double.parseDouble(maxY + "") / Double.parseDouble(y + "");

        if (scaleX < scaleY) {
            dimensions[0] = (int) (x * scaleX);
            dimensions[1] = (int) (y * scaleX);
        } else {
            dimensions[0] = (int) (x * scaleY);
            dimensions[1] = (int) (y * scaleY);
        }

        return dimensions;
    }
}
