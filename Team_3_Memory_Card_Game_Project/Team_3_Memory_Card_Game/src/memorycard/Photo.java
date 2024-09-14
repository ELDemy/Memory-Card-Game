package memorycard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;

//Class for the Photos we use

public class Photo{
    //Variables
  private String Name;
  private boolean matched = false;
  //constructor
    public Photo(String Name) {
        this.Name = Name;
    }
    //settter&getter
    public String getName() {return Name;} 
    public void setName(String Name) { this.Name = Name; }
    public boolean isMatched() {return matched;}
    public void setMatched(boolean matched) {this.matched = matched;}
    
     
    //Comparing between photo we will use to check matches
    public boolean isSamePhoto(Photo other){
        return this.getName().equals(other.getName());
    }
    
    // Calling the Photos from the source file
    public Image getPhoto() throws FileNotFoundException{
        FileInputStream s = new FileInputStream("src/dataUsed/"+this.getName()+".jpg");
        return new Image(s);
    }
    //Calling the Back image from the source file
    public static Image getBackImage() throws FileNotFoundException{
        FileInputStream s = new FileInputStream("src/dataUsed/Back.jpeg");
        return new Image(s);
   }

}
