package memorycard;
//Team 3
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MemoryCard extends Application {

    //the FlowPane that we will put the imagViews on it.
    FlowPane PhotoPane = new FlowPane(Orientation.VERTICAL);

    //labels for score
    Label movesLabel = new Label();
    Label MissedLabel = new Label();
    Label TimerLabel = new Label();
    Label TimerScore = new Label();

    //score
    int numOfMoves;
    int numOfMatches;
    int numOfMissed;
    int timer = 0;

    //list of the photos that is used in a single match,i.e. the match DECK.
    ArrayList<Photo> photosInGame;

    //two photos that we will use to define whether the two clicked cards are matched or not.
    Photo photo1, photo2;

    Font font = new Font("Times New Roman", 24);

    //defining 4 scenes and one stage.
    Scene entryScene;
    Scene playScene;
    Scene winScene;
    Scene loseScene;
    Stage stage;

    //fixed width and height for all the four scenes.
    final double width = 670;
    final double height = 750;

    //the background theme for each scene.
    Background backgroundPlay = new Background(new BackgroundFill(Color.CADETBLUE, null, null));

    //the background theme for each Button.
    Background backgroundExit = new Background(new BackgroundFill(Color.RED, null, null));
    Background backgroundAgain = new Background(new BackgroundFill(Color.CYAN, null, null));
    
    //Our media 
    String intro = "src/dataUsed/Main.mp3";
    Media mIntro= new Media(Paths.get(intro).toUri().toString());
    MediaPlayer MediaPlayerIntro =new MediaPlayer(mIntro);
    String win = "src/dataUsed/winning.mp3";
    Media mWin= new Media(Paths.get(win).toUri().toString());
    MediaPlayer MediaPlayerWin =new MediaPlayer(mWin);
    String lose="src/dataUsed/losing.mp3";
    Media mLose= new Media(Paths.get(lose).toUri().toString());
    MediaPlayer MediaPlayerLose =new MediaPlayer(mLose);
    String press = "src/dataUsed/flippingcard.mp3";
    Media mPress= new Media(Paths.get(press).toUri().toString());
    MediaPlayer MediaPlayerPress =new MediaPlayer(mPress);
    
    //time line used to make a timer
    Timeline tLTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
        timer++;
        TimerLabel.setText(String.valueOf(timer));
    }));
 
    //time line used to show the DECK for the player for 5 seconds
    Timeline remember;
    //counter used in 'remember' time line to count 5 seconds
    int count;
    //used in 'showPhoto()' 379 method to prevent the error of Repeated clicking. 
    int photo1Index;
    int photo2Index;
    
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        //we set a stage  to a have access for the primaryStage all over the program.
        stage = primaryStage;
                                
        //timer time line setting
        tLTimer.setCycleCount(Timeline.INDEFINITE);
        
        //time line used to repeat the music when it ends(after two minutes).
        Timeline tMusic = new Timeline(new KeyFrame(Duration.seconds(120),e->{MediaPlayerIntro.seek(Duration.seconds(0.0));})) ;
        tMusic.setCycleCount(Timeline.INDEFINITE);
        tMusic.play();
        //music start
        MediaPlayerIntro.play();
        //setting the lables font
        movesLabel.setFont(font);
        MissedLabel.setFont(font);
        TimerLabel.setFont(font);

                                        //layOut for ENTRY Scene
                                        
        //the buttons and their setting of the ENTRY Scene.                           
        Button bMainScene = new Button("Play");
        bMainScene.setBackground(backgroundAgain);
        bMainScene.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 40));
        bMainScene.setLayoutX(270);
        bMainScene.setLayoutY(500);
        bMainScene.setOnAction(e -> {
            try {
                LetsPlay();
                stage.setScene(playScene);
            } catch (FileNotFoundException exception) {System.out.println("Error While trying to restart the Game");}
        });
        Button bMainSceneExit = new Button("Exit");
        bMainSceneExit.setBackground(backgroundExit);
        bMainSceneExit.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 40));
        bMainSceneExit.setLayoutX(270);
        bMainSceneExit.setLayoutY(600);
        bMainSceneExit.setOnAction(e -> {stage.close();});

        //the Background of the ENTRY scene.
        FileInputStream s1 = new FileInputStream("src/dataUsed/Main.png");
        Image i1 = new Image(s1);
        ImageView v = new ImageView(i1);
        v.setFitWidth(width + 10);
        v.setFitHeight(height + 10);
        //finally the pane which contains all the nodes of the ENTRY scene.
        Pane pMainScene = new Pane(v, bMainScene, bMainSceneExit);
        entryScene = new Scene(pMainScene, width, height);

                                    //LayOut for Play Scene
        /**
         * first we add 12 ImageViews in the "PhotoPane" FlowPane using loop and
         * the method "addImageView()" in line 297
         *  so now there are 12 imageViews that exist in the flow pane
         */
        PhotoPane.setHgap(10);
        PhotoPane.setVgap(10);
        for (int i = 0; i < 12; i++) {PhotoPane.getChildren().add(addImageView());}

        //details of the MATCH.
        Label lMoves = new Label("Moves: ");
        lMoves.setFont(font);
        Label lTimer = new Label("Time: ");
        lTimer.setFont(font);
        Label lsec = new Label(" Sec");
        lsec.setFont(font);
        Label lMissed = new Label("Missed: ");
        lMissed.setFont(font);
        HBox hUP1 = new HBox();
        hUP1.getChildren().addAll(lMoves, movesLabel);
        HBox hUP2 = new HBox();
        hUP2.getChildren().addAll(lTimer, TimerLabel, lsec);
        HBox hUP3 = new HBox();
        hUP3.getChildren().addAll(lMissed, MissedLabel);
        
        HBox hUP = new HBox(hUP1, hUP2, hUP3);
        hUP.setSpacing(150);
        //the buttons and their setting of the Play Scene.
        Button bExit = new Button("Go to ENTRY Scene");
        bExit.setBackground(backgroundExit);
        bExit.setFont(font);
        bExit.setOnAction(e -> {timer=0;tLTimer.pause();stage.setScene(entryScene);MediaPlayerIntro.play();});
//                MediaPlayerIntro.seek(Duration.seconds(0.0));
        Button bRestart = new Button("Restart");
        bRestart.setBackground(backgroundAgain);
        bRestart.setFont(font);
        bRestart.setOnAction(e -> {try {remember.stop();tLTimer.stop(); LetsPlay();} catch (FileNotFoundException exception) {System.out.println("Error While trying to restart the Game");}});
        
        HBox hDOWN = new HBox(bExit, bRestart);
        hDOWN.setSpacing(280);
                             
        //finally the borderpane which contains all the nodes of the play scene.
        BorderPane borderpane = new BorderPane();
        borderpane.setCenter(PhotoPane);
        borderpane.setTop(hUP);
        borderpane.setBottom(hDOWN);
        borderpane.setPadding(new Insets(20, 20, 20, 20));
        borderpane.setBackground(backgroundPlay);
        playScene = new Scene(borderpane, width, height);

                                        //layOut for Winning scene
                                        
        Text tWinScene = new Text(100, 260, "Winner Winner Chicken dinner");
        tWinScene.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 40));
        Text tTimer = new Text(180, 320, "Time Taken: ");
        tTimer.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 40));
        TimerScore.setLayoutX(415);
        TimerScore.setLayoutY(285);
        TimerScore.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 40));
        Text tSec = new Text(450, 320, " Sec");
        tSec.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 40));
        Text tagain = new Text(230, 380, "Play Again?");
        tagain.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 40));
        //the buttons and their setting of the Winning Scene.
        Button bWinScene1 = new Button("Play Again");
        bWinScene1.setBackground(backgroundAgain);
        bWinScene1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 30));
        bWinScene1.setLayoutX(100);
        bWinScene1.setLayoutY(400);
        bWinScene1.setOnAction(e -> {
            try {LetsPlay();stage.setScene(playScene);
                     MediaPlayerWin.seek(Duration.seconds(0.0));MediaPlayerWin.pause();
                    MediaPlayerIntro.seek(Duration.seconds(0.0)); MediaPlayerIntro.play();
            } catch (FileNotFoundException exception) {System.out.println("Error While trying to play Again");}});
        Button bWinScene2 = new Button("Go to ENTRY Scene");
        bWinScene2.setBackground(backgroundExit);
        bWinScene2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 30));
        bWinScene2.setLayoutX(340);
        bWinScene2.setLayoutY(400);
        bWinScene2.setOnAction(e -> {
                    stage.setScene(entryScene);
                    MediaPlayerWin.seek(Duration.seconds(0.0));MediaPlayerWin.pause();
                    MediaPlayerIntro.seek(Duration.seconds(0.0)); MediaPlayerIntro.play();});
        //setting BackGround
        FileInputStream swinning = new FileInputStream("src/dataUsed/winningBackGround.jpg");
        Image iwinning = new Image(swinning);
        ImageView vwinning = new ImageView(iwinning);
        vwinning.setFitWidth(width + 10);
        vwinning.setFitHeight(height + 10);
        //finally the pane which contains all the nodes of the Winning scene.
        Pane pWin = new Pane(vwinning,tWinScene, bWinScene1, bWinScene2, tTimer, TimerScore, tSec,tagain);
        winScene = new Scene(pWin, width, height);

                                            //layOut for Losing
                                            
        //the buttons and their setting of the Losing Scene.
        Button bLoseScene1 = new Button("Play Again");
        bLoseScene1.setBackground(backgroundAgain);
        bLoseScene1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 30));
        bLoseScene1.setLayoutX(100);
        bLoseScene1.setLayoutY(600);
        bLoseScene1.setOnAction(e -> {
            try {       LetsPlay();stage.setScene(playScene);
                        MediaPlayerLose.seek(Duration.seconds(0.0));MediaPlayerLose.pause();
                        MediaPlayerIntro.seek(Duration.seconds(0.0)); MediaPlayerIntro.play();
            } catch (FileNotFoundException exception) {System.out.println("Error While trying to Play Again");}});
        
        Button bLoseScene2 = new Button("Go to ENTRY Scene");
        bLoseScene2.setBackground(backgroundExit);
        bLoseScene2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 30));
        bLoseScene2.setLayoutX(340);
        bLoseScene2.setLayoutY(600);
        bLoseScene2.setOnAction(e -> {
                        stage.setScene(entryScene);
                        MediaPlayerLose.seek(Duration.seconds(0.0)); MediaPlayerLose.pause();
                        MediaPlayerIntro.seek(Duration.seconds(0.0)); MediaPlayerIntro.play();});
        //setting background
        FileInputStream slosing = new FileInputStream("src/dataUsed/losingBackGround.jpg");
        Image ilosing = new Image(slosing);
        ImageView vlosing = new ImageView(ilosing);
        vlosing.setFitWidth(width + 10);
        vlosing.setFitHeight(height + 10);
        //finally the pane which contains all the nodes of the Losing scene.
        Pane pLose = new Pane(vlosing,bLoseScene1, bLoseScene2);
        loseScene = new Scene(pLose, width, height);

        //stage setting and its Icon.
        stage.setTitle("Memory Card");
        stage.setScene(entryScene);
        FileInputStream s2 = new FileInputStream("src/dataUsed/Icon.png");
        Image i2 = new Image(s2);
        stage.getIcons().add(i2);
        stage.show();
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }

    //Adding ImageView to "PhotoPane" FlowPane
    public static ImageView addImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(200);
        return imageView;
    }

    //the method used to start the game.
    public void LetsPlay() throws FileNotFoundException {
        
        //set the timer and the counter to zero at the start of the game.
        timer = 0;
        count = 0;
        //play the music
        MediaPlayerIntro.play();
        //we set 'photo1' and 'photo2' to null
        photo1 = null;
        photo2 = null;
        
        /**
         * - creating an PhotoViewer object 'viewer' and shuffle it
         * - getting the first 6 photos from it and putting each one of them
         *      two times in 'photosInGame' and then shuffle them again
         */
        PhotoViewer viewer = new PhotoViewer();
        viewer.shuffle();
        photosInGame = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Photo photo = viewer.getFirstPhoto();
            photosInGame.add(photo);
            photosInGame.add(photo);
        }
        Collections.shuffle(photosInGame);

        /**
        * using for loop we.
         * - set and an action for each imageView on mouse Clicked(during the first 5 seconds do nothing)    
         * - show the photos that are in the 'photosInGame' on the imageViews   
         */
        for (int i = 0; i < 12; i++) {
            //gettin the imageView form the 'photoPane' by the index of it.
            ImageView V = (ImageView) PhotoPane.getChildren().get(i);             
            //disabling the imageViews 
            V.setOnMouseClicked(event -> {});
            //using the 'getPhoto()' method from our 'Photo' class to get the image
            V.setImage(photosInGame.get(i).getPhoto());
        }// end of the for loop

        //to initilize the score
        numOfMoves = 0;
        numOfMatches = 0;
        movesLabel.setText("0");
        MissedLabel.setText("0/3");
        TimerLabel.setText("0");
        
        //after the count reaches 5 we hide all photos and stop the timer
        remember = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            count++;
            if (count == 5) {
                try {hidePhotos();tLTimer.play();} catch (FileNotFoundException ex) {System.out.println("Error While trying to Hide the phots");}
                //creating a action for each imageView
                for (int i = 0; i < 12; i++) {
                    ImageView V = (ImageView) PhotoPane.getChildren().get(i);
                    //here we set value(just an index) for the imageView to be able to access it
                    V.setUserData(i); 
                    //What happens when we click on an imageView is handled in method "showPhoto()" in line 451
                    V.setOnMouseClicked(event -> {
                        try {
                            //we cast the value of the Data of the imageView to be integer
                            showPhoto((int) V.getUserData());
                            MediaPlayerPress.seek(Duration.seconds(0.0));	
                            MediaPlayerPress.play();   
                        } catch (FileNotFoundException ex) {System.out.println("Error While trying to restart the Game");}});
                }// end of the for loop  
            }// end of if statment
        })); // end of the time line
        remember.setCycleCount(5);
        remember.play();
    }
    

    // Here is the "showPhoto()" method that determine what happens when we click on an imageView
    public void showPhoto(int i) throws FileNotFoundException {

        /*first we make sure that if their is no matching all Photos that are not matched won't appear
        using the 'hidePhotos()' Method in line 451*/
        if (photo1 == null && photo2 == null) 
                hidePhotos();

        /**
         * You clicked on the imageView the program knows which imageView you
         * clicked on (Because of the data we set it to it)
         * so it takes the photo from the "photosInGame" list that has the same index and put
         * it in 'photo1' photo and then on the imageView that you clicked on
         */
        ImageView V = (ImageView) PhotoPane.getChildren().get(i);
        if (photo1 == null) {
            photo1 = photosInGame.get(i);
            V.setImage(photo1.getPhoto());
            //saving the index to send it to the 'checkMatch(int i,int j)' method in line 464
            photo1Index = i;
        } 
        
        /**
         * now if the 'photo1' was NOT null (second click) and you clicked on another
         * imageView so it takes the photo from the "photosInGame" list and put
         * it in 'photo2' photo and then on the imageView that you clicked on
         *
         * THIS IS A GUESS so we increase the number of guess by one
         *
         * and immediately we check if 'photo1' and 'photo2' are the same or not
         * using 'checkMatch(int i,int j)' method in line 464
         * if they are matched we increase the number of matches by one
         */
        else if (photo2 == null && photo1Index != i ) {
            numOfMoves++;
            photo2 = photosInGame.get(i);
            V.setImage(photo2.getPhoto());
            //'checkMatch(int i,int j)' method that shows whether the the photos are matched or not.
            photo2Index=i;
            checkMatch(photo1Index,photo2Index);
            //updating the score labels 
            movesLabel.setText(String.valueOf(numOfMoves));
            numOfMissed = numOfMoves - numOfMatches;
            MissedLabel.setText(String.valueOf(numOfMissed + "/3"));
            
            /*here if the number of matches is 6 this means that you finished 
            so switch to winScene*/
            if (numOfMatches == 6) {
                stage.setScene(winScene);
                tLTimer.stop();
                TimerScore.setText(TimerLabel.getText());
                MediaPlayerIntro.pause();
                MediaPlayerWin.seek(Duration.seconds(0.0));
                MediaPlayerWin.play();
            }
            
            /*here if the number of missed is 3 this means that you lost 
            so switch to loseScene*/
            if (numOfMissed == 3) {
                stage.setScene(loseScene);
                tLTimer.stop();
                MediaPlayerIntro.pause();
                MediaPlayerLose.seek(Duration.seconds(0.0));
                MediaPlayerLose.play();
            }
        }  
    }

    /*simply this method go through all the imageViews and if the photo that has the same a
        index as this imageView in 'PhotosInGame' are not matched 
        the method will hide it by putting the Back photo
        and if it is matched will show it
     */
    public void hidePhotos() throws FileNotFoundException {
        for (int i = 0; i < 12; i++) {
            ImageView V = (ImageView) PhotoPane.getChildren().get(i);
            Photo p = photosInGame.get(i);
            if (p.isMatched())
                V.setImage(p.getPhoto());
             else 
                V.setImage(Photo.getBackImage());
            }
    }

    /*Here is the method that checks if 'photo1' and 'photo2' are matched or not
      and in both cases we will make them null again*/
    public void checkMatch(int i,int j) {
        if (photo1.isSamePhoto(photo2)) {
            numOfMatches++;
            photo1.setMatched(true);
            photo2.setMatched(true);
            // disabling the imageViews that contain matched photos
            ImageView V1 = (ImageView) PhotoPane.getChildren().get(i);  
            V1.setOnMouseClicked(e->{});
            ImageView V2 = (ImageView) PhotoPane.getChildren().get(j);
            V2.setOnMouseClicked(e->{});
        }
        photo1 = null;
        photo2 = null;
    }
}
