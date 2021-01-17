import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
public class Images extends JFrame{
    private static ArrayList<String> uids = new ArrayList<String>();
    private static ArrayList<String> filename = new ArrayList<String>();
    private ArrayList<Boolean> firstTimeOpen = new ArrayList<Boolean>();
    private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    String namespace;
    public Images(String namespace){
        this.namespace = namespace;
        uids.clear();filename.clear();firstTimeOpen.clear();images.clear();
    }

    public Images(String namespace, String files[]){
        this.namespace = namespace; int lastImage = 0, imagesAmount = 0;
        uids.clear();filename.clear();firstTimeOpen.clear();images.clear();
        try{
            for(int i=0;i<files.length;i++){
                if(files[i].contains("++image++")){
                    lastImage = i;
                    files[i] = files[i].replace("++image++","");
                    uids.add(files[i].split("---")[0]);
                    filename.add(files[i].split("---")[1]);
                    images.add(ImageIO.read(new File(Manager.pathExtension+"adventures/"+namespace+"/images/"+uids.get(imagesAmount)+".png")));
                    firstTimeOpen.add(false);
                    imagesAmount++;
                }
            }
        }catch(Exception e){Popup.error(StaticStuff.projectName, "Image '"+files[lastImage]+"' contains invalid data.\n"+e);}
    }

    public void setNamespace(String namespace){
        this.namespace = namespace;
    }

    public String generateSaveString(){
        for(int i=0;i<uids.size();i++){
            try{
                ImageIO.write(images.get(i), "png", new File(Manager.pathExtension+"adventures/"+namespace+"/images/"+uids.get(i)+".png"));
            }catch(Exception e){Popup.error(StaticStuff.projectName, "Image '"+filename.get(i)+"' contains invalid data.\n"+e);}
        }
        String str = "";
        for(int i=0;i<uids.size();i++)
            str = str + "\n++image++" + uids.get(i) + "---" + filename.get(i);
        return str;
    }

    public String generateMenuString(){
        String str = uids.size() + " image(s):";
        for(int i=0;i<uids.size();i++)
            str = str + "\n" + filename.get(i) + " --- " + uids.get(i);
        return str;
    }

    public boolean addImage(String name, String path){
        if(name == null || path == null) return false;
        if(name.length() == 0 || path.length() == 0) return false;
        if(!FileManager.fileExists(path)) return false;
        try{
            this.images.add(ImageIO.read(new File(path)));
        }catch(Exception e){e.printStackTrace();Popup.error(StaticStuff.projectName, "Unable to get image.");}
        filename.add(name);
        uids.add(UID.generateUID());
        firstTimeOpen.add(true);
        StaticStuff.copyString(uids.get(uids.size()-1));
        return true;
    }

    public boolean deleteImage(String uid){
        int index = uids.indexOf(uid);
        if(index == -1) return false;
        uids.remove(index);
        filename.remove(index);
        images.remove(index);
        firstTimeOpen.remove(index);
        return true;
    }

    public boolean openImage(String uid){
        int index = uids.indexOf(uid);
        if(index == -1) return false;
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JLabel(new ImageIcon(images.get(index))));
        frame.pack();
        frame.setVisible(true);
        if(firstTimeOpen.get(index))
            firstTimeOpen.set(index,false);
        else{
            int selected = Popup.selectButton(StaticStuff.projectName, "What do you want to do?", new String[]{"Nothing","Edit name","Edit image"});
            if(selected == -1 || selected == 0) return true;
            if(selected == 1){
                String rename = Popup.input("Enter new name:", filename.get(index));
                if(rename != null) if(!rename.equals("")) filename.set(index, rename);
            }else if(selected == 2){
                try{
                    String path = FileManager.filePicker();
                    if(path != null) if(!path.equals("")) images.set(index, ImageIO.read(new File(path)));
                }catch(Exception e){}
            }
        }
        return true;
    }

    public boolean imageExists(String uid){
        return uids.contains(uid);
    }

    public String getImageName(String uid){
        return filename.get(uids.indexOf(uid));
    }

    public static String getImageUID(String name){
        return uids.get(filename.indexOf(name));
    }

    public BufferedImage getBufferedImage(String uid){
        return images.get(uids.indexOf(uid));
    }

    public static BufferedImage readImageFromFile(String filename){
        try{return ImageIO.read(new File(filename));}catch(Exception e){
            try{return ImageIO.read(new File("res/img/null.png"));}catch(Exception e2){return null;}}
    }

    public static int getAmount(){
        return filename.size();
    }

    public static String getName(int index){
        return filename.get(index);
    }

    public static String getUID(int index){
        return uids.get(index);
    }

    public static String getTitleImageUID(){
        try{return getImageUID("projectIcon");}catch(Exception e){return "";}
    }

    public int refactor(String find, String replace){
        int occ = 0;
        occ += StaticStuff.refactorArrayList(find, replace, filename);
        occ += StaticStuff.refactorArrayList(find, replace, uids);
        return occ;
    }
}
