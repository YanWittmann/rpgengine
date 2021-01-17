
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class PopupText{
    private JWindow w;
    final private int x_size, y_size;
    public int selected = -1;
    private String text, buttons[];

    PopupText(String text){
        this.text = StaticStuff.prepareString(text); this.buttons = new String[]{"OK"};
        if(StaticStuff.getLongestLineLength(text.split("<br>"))*20 >= StaticStuff.getLongestLineLength(buttons)*20)
            if(StaticStuff.getLongestLineLength(text.split("<br>"))*20 > 170)
                this.x_size = StaticStuff.getLongestLineLength(text.split("<br>"))*20;
            else this.x_size = 170;
        else
            this.x_size = StaticStuff.getLongestLineLength(buttons)*20;
        this.y_size = (StaticStuff.countOccurrences(text,"<br>")*49) + (buttons.length*57) + 49 + 130;

    }

    public void createComponents(){
        w = new JWindow();
        w.setBackground(new Color(0, 0, 0, 0));
        w.setAlwaysOnTop(true);
        try{ UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}

        JPanel p = new JPanel(new BorderLayout()){
                public void paintComponent(Graphics g){
                    g.setColor(new Color(255,255,255));
                    g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
                    g.setColor(new Color(0,0,0));
                    g.fillRoundRect(3, 3, x_size-6, y_size-6, 20, 20);
                }
            };
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel l = new JLabel(("<html><center><font color=\"#000000\">-</font><font color=\"#FFFFFF\">"+text.replace("<br>","<font color=\"#000000\">-<br>-</font>")+
                    "</font><font color=\"#000000\">-</font>").replace("????","").replace("???",""));
        l.setFont(StaticStuff.getPixelatedFont());
        l.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4, true));
        p.add(l, gbc);
        JLabel l2 = new JLabel("<html><font color=\"#000000\">-</font>");
        p.add(l2, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        //gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton options;
        for(int i=0;i<buttons.length;i++){
            options = new JButton("<html>"+StaticStuff.prepareString(buttons[i]));
            options.setFont(StaticStuff.getPixelatedFont());
            final int ii = i;
            options.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        click(ii);
                    }
                });
            p.add(options, gbc);
        }

        w.add(p);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        w.setSize(x_size, y_size);
        w.setLocation((int)((width/2)-(x_size/2)), (int)((height/2)-(y_size/2)));

        w.setLocationRelativeTo(null);
        
        addListener(w);
        addListener(l);
        
        w.show();
    }
    
    private int pX,pY;
    private void addListener(Component c){
        c.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent me){
                    pX = me.getX();
                    pY = me.getY();
                    w.toFront();
                    w.repaint();
                }

                public void mouseDragged(MouseEvent me){
                    w.setLocation(w.getLocation().x + me.getX() - pX, w.getLocation().y + me.getY() - pY);
                }
            });
        c.addMouseMotionListener(new MouseMotionAdapter(){
                public void mouseDragged(MouseEvent me){
                    w.setLocation(w.getLocation().x + me.getX() - pX,w.getLocation().y + me.getY() - pY);
                }
            });
    }

    private void click(int index){
        selected = index;
        w.dispose();
    }
}
