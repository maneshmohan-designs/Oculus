package OpenCV3_1;

import opencv3_1.BrightnessManager;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class DetectFace {

    JFrame frame;
    JLabel lbl;
    ImageIcon icon, eyePos;
    double focalLength = 0.3;
    int DPI;
    int pixelsPercm;
    double distanceToCamera;
    int dist;
    BrightnessManager bright=new BrightnessManager();
    static DetectFace face;
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        face = new DetectFace();
        face.startOp();
                
    }

    public DetectFace() {
        /*try {
            bright.setBrightness((int)distanceToCamera);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }*/
        
        new Thread( new Runnable() {
    @Override
    public void run() {
       int brightness;
       while(true){ 
       if(distanceToCamera>100)
       {
           brightness = 100;
       }
        else if(distanceToCamera<10)
        {
            brightness = 10;
        }
        else 
            brightness = (int)distanceToCamera;
        try {
            bright.setBrightness(brightness);
            System.err.println("brightness:"+brightness);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
       }
       }
    
}).start();
       
    }

    
    public void startOp() {
        CascadeClassifier cascadeFaceClassifier = new CascadeClassifier(
                "F:\\Manesh\\OpenCV3\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml");
        CascadeClassifier cascadeEyeClassifier = new CascadeClassifier(
                "F:\\Manesh\\OpenCV3\\opencv\\sources\\data\\haarcascades\\haarcascade_eye.xml");

        VideoCapture videoDevice = new VideoCapture();
        videoDevice.open(0);
        if (videoDevice.isOpened()) {
            while (true) {
                Mat frameCapture = new Mat();
              //  frameCapture = cv3_1.flip();
                videoDevice.read(frameCapture);
                

                MatOfRect faces = new MatOfRect();
                cascadeFaceClassifier.detectMultiScale(frameCapture, faces);
                Rect rectCrop = null;
                for (Rect rect : faces.toArray()) {
                  //  Imgproc.putText(frameCapture, "Face " + rect.width + "x" + rect.height, new Point(rect.x, rect.y - 5), 1, 2, new Scalar(0, 0, 255));
                    Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                            new Scalar(0, 100, 0), 3);
                    rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
                    focalLength = 490;
                    distanceToCamera=(18*focalLength)/rect.width;
                    Imgproc.putText(frameCapture, "Distance:"+distanceToCamera, new Point(rect.x, rect.y +rect.width+20), 1, 2, new Scalar(0, 0, 255));
               }
                
             
//                MatOfRect eyes = new MatOfRect();
//                cascadeEyeClassifier.detectMultiScale(frameCapture, eyes);
//                for (Rect rect : eyes.toArray()) {
//                    Imgproc.putText(frameCapture, "Eye", new Point(rect.x, rect.y - 5), 1, 2, new Scalar(0, 0, 255));
//                    Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
//                            new Scalar(200, 200, 100), 2);
//               }
//                Rect eyeRect = null;
//                try {
//                    eyeRect = faces.toArray()[0];
//                } catch (Exception e) {
//                }
//                if (eyeRect != null) {
//                    Mat mat = frameCapture;
//                    PushImage(ConvertMat2Image(frameCapture), ConvertMat2Image(mat));
//                } else {
                    PushImage(ConvertMat2Image(frameCapture));
//                System.out.println(String.format("%s (FACES) %s (EYE) detected.", faces.toArray().length, eyes.toArray().length));
            }
        } else {
            System.out.println("Cant Open Video Camera.");
        }
        
    }

    private BufferedImage ConvertMat2Image(Mat camImage) {
        
        MatOfByte byteMatrix = new MatOfByte();
        Imgcodecs.imencode(".jpg", camImage, byteMatrix);
        byte[] byteArray = byteMatrix.toArray();
        BufferedImage goruntu = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            goruntu = ImageIO.read(in);
        } catch (IOException e) {
            System.err.println("Error Occured " + e.getLocalizedMessage());
            return null;
        }
        return goruntu;
    }

    public void BeginProcess() {
        frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(700, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(1, 2));

        lbl = new JLabel();

        panel.add(lbl);

        frame.add(panel);
    }

    public void PushImage(Image img2, Image eye) {
        if (frame == null) {
            BeginProcess();
        }
        icon = new ImageIcon(img2);
        lbl.setIcon(icon);
        frame.revalidate();
    }

    public void PushImage(Image img2) {
        if (frame == null) {
            BeginProcess();
        }
        icon = new ImageIcon(img2);
        lbl.setIcon(icon);
        frame.revalidate();
    }
}
