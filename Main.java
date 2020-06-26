import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String imgFile = "C:\\Users\\rtoma\\Desktop\\faces-1.JPG";
        Mat src  = Imgcodecs.imread(imgFile);

        String xmlFile = "C:\\Users\\rtoma\\Downloads\\opencv\\build\\etc\\lbpcascades\\lbpcascade_frontalface.xml";
        CascadeClassifier cc = new CascadeClassifier(xmlFile); //Detector

        MatOfRect faceDetection = new MatOfRect();
        cc.detectMultiScale(src, faceDetection); //Detectar várias faces


        for(Rect rect: faceDetection.toArray()){
            Random random = new Random();
            int x = random.nextInt(5);
            Mat mask = src.submat(rect);
            if(x==0) {
                Imgproc.rectangle(src, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(255,0,0), 1);
                Imgproc.GaussianBlur(mask, mask, new Size(5, 5), 0);
            }else if(x==1) {
                //Imgproc.bilateralFilter(mask,9,75,75);
                //Imgproc.bilateralFilter(mask,mask,9,75,75);
                //Não consegui implementar
            }else if(x==2){
                Imgproc.rectangle(src, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(0,0,255), 1);
                Imgproc.medianBlur(mask, mask,5);
            }else if(x==3){
                Mat invert= new Mat(mask.rows(),mask.cols(), mask.type(), new Scalar(255,255,255));
                Core.subtract(invert, mask, mask);
            }else if(x==4){
                //https://www.tutorialspoint.com/java-example-demonstrating-canny-edge-detection-in-opencv

                Mat gray = new Mat(mask.rows(), mask.cols(), mask.type());
                Mat edges = new Mat(mask.rows(), mask.cols(), mask.type());
                Imgproc.cvtColor(mask, gray, Imgproc.COLOR_BGR2GRAY);
                Imgproc.Canny(gray, edges, 100, 100*3);
                Mat black= new Mat(mask.rows(),mask.cols(), mask.type(), new Scalar(255,255,255));
                Core.subtract(mask,black,mask);
                //Core.add(mask,edges,mask);
                edges.copyTo(mask);
                //Imgcodecs.imwrite("C:\\Users\\rtoma\\Desktop\\faces-2.JPG", mask);
                //Não consegui adicionar na imagem essa mask, mas ao criar outro documento, ela aparece lá

            }
        }

        Imgcodecs.imwrite("C:\\Users\\rtoma\\Desktop\\faces-2.JPG", src);//Cria imagem resultante

        //https://answers.opencv.org/question/31505/how-load-and-display-images-with-java-using-opencv-solved/
        BufferedImage img = bufferedImage(src);
        ImageIcon icon=new ImageIcon(img);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



    }
    public static BufferedImage bufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        m.get(0,0,((DataBufferByte)image.getRaster().getDataBuffer()).getData());
        return image;
    }
}