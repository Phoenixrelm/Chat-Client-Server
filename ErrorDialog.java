import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ErrorDialog {

   /*Constructor
    *Shows an error dialog box
    *@param String errMsg - message to be shown
    */
   public ErrorDialog(String title, String errMsg){
    JOptionPane.showMessageDialog(new JFrame(), errMsg, title,
        JOptionPane.ERROR_MESSAGE);          
   }
   
   
   public static void main(String[] args){
    //  ErrorDialog ed = new ErrorDialog("Error", "Server connection lost");  
   }
   
}
