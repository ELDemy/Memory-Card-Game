package memorycard;

import java.util.ArrayList;
import java.util.Collections;

// This Class is just a Viewer of the Photos
public class PhotoViewer {
    private ArrayList<Photo> viewer;

    /*constructor
    creates a an arrayList 'viewer' of Photo objects.*/
    public PhotoViewer() {
        this.viewer = new ArrayList<>();
        for(int i=0;i<=9;i++){
            viewer.add(new Photo(String.valueOf(i)));
        }
    }
    
    //shuffling the viewer
    public void shuffle(){
        Collections.shuffle(viewer);
    }
    
    //getting the first photo from the viewer
   public Photo getFirstPhoto(){
        return viewer.remove(0);
    }

}
