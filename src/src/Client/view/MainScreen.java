package Client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainScreen {
    public MainScreen() {
        JFrame frame = new JFrame("Màn hình bắt đầu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(810, 540);
        frame.setLocationRelativeTo(null);

        // Load and scale background image
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("../assets/back.png")); // Update the path to your image
        Image scaledImage = originalIcon.getImage().getScaledInstance(810, 540, Image.SCALE_SMOOTH);
        ImageIcon backgroundIcon = new ImageIcon(scaledImage);

        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout()); // Use BorderLayout to position components

        // Create buttons with images
        JButton btnLogin = createImageButton("../assets/login.png", "Đăng nhập");
        JButton btnRegister = createImageButton("../assets/login.png", "Đăng ký");

        // Add action listeners
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                new LoginScreen();
            }
        });
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                new RegisterScreen();
            }
        });

        // Create a panel for buttons and position them at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make panel transparent to show background
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Center buttons with spacing
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        // Add button panel to the bottom of the background label
        backgroundLabel.add(buttonPanel, BorderLayout.SOUTH); // Add buttons to the bottom

        // Add background label to the frame
        frame.add(backgroundLabel);
        frame.setVisible(true);
    }

    private JButton createImageButton(String imagePath, String tooltipText) {
        // Load and scale the image to the desired button size
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = originalIcon.getImage().getScaledInstance(120, 40, Image.SCALE_SMOOTH); // Adjust width and height
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Create the button with the scaled icon
        JButton button = new JButton(scaledIcon);
        button.setContentAreaFilled(false); // Make button background transparent
        button.setBorderPainted(false); // Remove button border
        button.setFocusPainted(false);
        button.setToolTipText(tooltipText);

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover
            }
        });

        return button;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreen());
    }
}
