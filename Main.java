import javax.swing.*;

/**
 * APPLICATION ENTRY POINT:
 * Initializes MVC architecture and launches the Swing GUI.
 *
 * Class Group: A2
 * Team No: 7
 * Project: Employee Payroll System
 */
public class Main {
    public static void main(String[] args) {
        // Set System Look and Feel for native operating system appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to standard Java Metal / Nimbus look and feel
        }

        // Launch GUI on the Event Dispatch Thread (Thread-safe)
        SwingUtilities.invokeLater(() -> {
            PayrollModel model = new PayrollModel();
            PayrollView view = new PayrollView();
            new PayrollController(model, view);
            view.setVisible(true);
        });
    }
}
