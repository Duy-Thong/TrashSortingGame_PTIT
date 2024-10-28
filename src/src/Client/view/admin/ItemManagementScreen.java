package Client.view.admin;

import Client.controller.HelpController;
import Client.model.Bin;
import Client.model.TrashItem;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ItemManagementScreen extends JFrame {
    private JTable trashTable;
    private JTable binTable;
    private DefaultTableModel trashTableModel;
    private DefaultTableModel binTableModel;
    private HelpController helpController;
    private final int widthImage = 50;
    private final int heightImage = 50;

    public ItemManagementScreen() {
        helpController = new HelpController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Item Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Trash items tab
        JPanel trashPanel = createTrashPanel();
        tabbedPane.addTab("Trash Items", trashPanel);

        // Bin items tab
        JPanel binPanel = createBinPanel();
        tabbedPane.addTab("Bin Items", binPanel);

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTrashPanel() {
        JPanel trashPanel = new JPanel(new BorderLayout());
        trashTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Type", "Image", "URL"}, 0) {
            public Class getColumnClass(int column) {
                return (column == 3) ? ImageIcon.class : Object.class; // Set ImageIcon class for image column
            }
        };
        trashTable = new JTable(trashTableModel);
        trashTable.setRowHeight(heightImage); // Set row height to fit image
        hideUrlColumn(trashTable);

        loadTrashItems();

        trashPanel.add(new JScrollPane(trashTable), BorderLayout.CENTER);
        trashPanel.add(createButtonPanel(true), BorderLayout.SOUTH);
        return trashPanel;
    }

    private JPanel createBinPanel() {
        JPanel binPanel = new JPanel(new BorderLayout());
        binTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Type", "Image", "URL"}, 0) {
            public Class getColumnClass(int column) {
                return (column == 3) ? ImageIcon.class : Object.class; // Set ImageIcon class for image column
            }
        };
        binTable = new JTable(binTableModel);
        binTable.setRowHeight(heightImage); // Set row height to fit image
        hideUrlColumn(binTable);

        loadBinItems();

        binPanel.add(new JScrollPane(binTable), BorderLayout.CENTER);
        binPanel.add(createButtonPanel(false), BorderLayout.SOUTH);
        return binPanel;
    }

    private void hideUrlColumn(JTable table) {
        table.getColumnModel().getColumn(4).setMinWidth(0); // Hide the URL column
        table.getColumnModel().getColumn(4).setMaxWidth(0);
        table.getColumnModel().getColumn(4).setWidth(0);
    }

    private void loadTrashItems() {
        List<TrashItem> trashItems = helpController.fetchTrashItemData();
        trashTableModel.setRowCount(0);
        for (TrashItem item : trashItems) {
            ImageIcon imageIcon = urlToImage(item.getUrl());
            trashTableModel.addRow(new Object[]{item.getId(), item.getName(), item.getType(), imageIcon, item.getUrl()});
        }
    }

    private void loadBinItems() {
        List<Bin> binItems = helpController.fetchBinData();
        binTableModel.setRowCount(0);
        for (Bin bin : binItems) {
            ImageIcon imageIcon = urlToImage(bin.getUrl());
            binTableModel.addRow(new Object[]{bin.getId(), bin.getName(), bin.getType(), imageIcon, bin.getUrl()});
        }
    }

    private JPanel createButtonPanel(boolean isTrash) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnBack = new JButton("Back"); // Add the Back button

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack); // Add Back button to panel

        btnAdd.addActionListener(e -> addItem(isTrash));
        btnEdit.addActionListener(e -> editItem(isTrash));
        btnDelete.addActionListener(e -> deleteItem(isTrash));

        // Add action listener for the Back button
        btnBack.addActionListener(e -> {
            dispose(); // Close the current screen
            // Optionally, you can navigate to another screen or main menu here
        });

        return buttonPanel;
    }

    private void addItem(boolean isTrash) {
        ItemDialog dialog = new ItemDialog(this, null, isTrash);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            if (isTrash) {
                TrashItem newItem = dialog.getTrashItem();
                helpController.addTrashItem(newItem);
            } else {
                Bin newBin = dialog.getBin();
                helpController.addBin(newBin);
            }
            refreshTables(); // Refresh tables after adding
        }
    }

    private void editItem(boolean isTrash) {
        int selectedRow = isTrash ? trashTable.getSelectedRow() : binTable.getSelectedRow();
        if (selectedRow >= 0) {
            if (isTrash) {
                TrashItem selectedItem = new TrashItem(
                        (String) trashTableModel.getValueAt(selectedRow, 0), // id
                        (String) trashTableModel.getValueAt(selectedRow, 1), // name
                        (String) trashTableModel.getValueAt(selectedRow, 2), // type
                        (String) trashTableModel.getValueAt(selectedRow, 4)  // url (stored in hidden column)
                );
                ItemDialog dialog = new ItemDialog(this, selectedItem, true);
                dialog.setVisible(true);
                if (dialog.isSucceeded()) {
                    TrashItem updatedItem = dialog.getTrashItem();
                    helpController.updateTrashItem(updatedItem);
                }
            } else {
                Bin selectedBin = new Bin(
                        (String) binTableModel.getValueAt(selectedRow, 0), // id
                        (String) binTableModel.getValueAt(selectedRow, 1), // name
                        (String) binTableModel.getValueAt(selectedRow, 2), // type
                        (String) binTableModel.getValueAt(selectedRow, 4)  // url (stored in hidden column)
                );
                ItemDialog dialog = new ItemDialog(this, selectedBin, false);
                dialog.setVisible(true);
                if (dialog.isSucceeded()) {
                    Bin updatedBin = dialog.getBin();
                    helpController.updateBin(updatedBin);
                }
            }
            refreshTables(); // Refresh tables after editing
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem(boolean isTrash) {
        int selectedRow = isTrash ? trashTable.getSelectedRow() : binTable.getSelectedRow();
        if (selectedRow >= 0) {
            if (isTrash) {
                String id = (String) trashTableModel.getValueAt(selectedRow, 0);
                helpController.deleteTrashItem(id);
            } else {
                String id = (String) binTableModel.getValueAt(selectedRow, 0);
                helpController.deleteBin(id);
            }
            refreshTables(); // Refresh tables after deletion
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTables() {
        loadTrashItems();
        loadBinItems();
    }

    // Method to load an image from a URL and return an ImageIcon
    private ImageIcon urlToImage(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedImage originalImage = ImageIO.read(url);
            Image scaledImage = originalImage.getScaledInstance(widthImage, heightImage, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("Error loading image from URL: " + e.getMessage());
            return null;
        }
    }
}
