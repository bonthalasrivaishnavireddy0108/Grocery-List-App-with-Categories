
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GroceryGUI app = new GroceryGUI();
            app.setVisible(true);
        });
    }
}
