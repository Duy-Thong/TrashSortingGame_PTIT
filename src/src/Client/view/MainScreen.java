package Client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MainScreen {
    private Font customFont;

    public MainScreen() {
        // Load the custom font
        try {
            // Load the font from the resources folder
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("../assets/FVF.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Game phân loại rác");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(810, 540);
        frame.setLocationRelativeTo(null);

        // Load and set GIF background
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("../assets/new_back.gif")); // Update the path to your GIF
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout()); // Use BorderLayout to position components

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 169, 114, 0)); // Semi-transparent background
        titlePanel.setLayout(new GridBagLayout());

        // Create a panel for the slogan with a semi-transparent black background
        JPanel sloganBackgroundPanel = new JPanel();
        sloganBackgroundPanel.setBackground(new Color(0, 0, 0, 0)); // Black with 50% opacity
        sloganBackgroundPanel.setOpaque(true); // Ensure the panel is opaque
        sloganBackgroundPanel.setLayout(new GridBagLayout());

        // Slogan label with padding
        // Updated slogan label with HTML styling for background color
        JLabel sloganLabel = new JLabel("<html><span style='background-color:rgba(0, 169, 114, 0.4); padding: 5px;'>Phân loại rác, vì tương lai xanh ! </span></html>", JLabel.CENTER);
        sloganLabel.setFont(customFont.deriveFont(Font.ITALIC, 14)); // Set font style and size
        sloganLabel.setForeground(Color.WHITE);
        sloganLabel.setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 0)); // Set top padding of 50px
//        sloganLabel.setOpaque(true); // Set top padding of 50px

        // Add slogan label to slogan background panel
        sloganBackgroundPanel.add(sloganLabel); // Add sloganLabel to sloganBackgroundPanel

        // Add slogan background panel to title panel
        titlePanel.add(sloganBackgroundPanel); // Add the background panel to the titlePanel

        // Create buttons with different background colors
        JButton btnLogin = createCustomButton("Đăng nhập", new Color(0, 102, 204), new Color(0, 153, 255));
        JButton btnRegister = createCustomButton("Đăng ký", new Color(204, 102, 0), new Color(255, 153, 51));

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

        // Add title panel to the top of the background label
        backgroundLabel.add(titlePanel, BorderLayout.NORTH); // Add title to the top
        // Add button panel to the bottom of the background label
        backgroundLabel.add(buttonPanel, BorderLayout.SOUTH); // Add buttons to the bottom

        // Add background label to the frame
        frame.add(backgroundLabel);
        frame.setVisible(true);
    }

    private JButton createCustomButton(String text, Color defaultColor, Color hoverColor) {
        // Create a button with a custom size and style
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40)); // Keep the original button size
        button.setFont(customFont.deriveFont(Font.BOLD, 16f)); // Set the custom font with size
        button.setForeground(Color.WHITE); // Set text color
        button.setBackground(defaultColor); // Set the default background color
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Optional border
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor); // Change background color on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor); // Reset background color
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreen());
    }
}
