package Client.view.admin;

import Client.controller.admin.ItemManagementController;
import Client.model.Bin;
import Client.model.TrashItem;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManagementScreen extends JFrame {
    private JTable trashTable;
    private JTable binTable;
    private DefaultTableModel trashTableModel;
    private DefaultTableModel binTableModel;
    private ItemManagementController itemManagementController;
    private final int widthImage = 50;
    private final int heightImage = 50;
    private Map<String, ImageIcon> imageCache = new HashMap<>(); // Bộ nhớ cache cho hình ảnh

    public ItemManagementScreen() {
        itemManagementController = new ItemManagementController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Quản lý vật phẩm"); // Title in Vietnamese
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Trash items tab
        JPanel trashPanel = createTrashPanel();
        tabbedPane.addTab("Vật phẩm rác", trashPanel); // Trash Items in Vietnamese

        // Bin items tab
        JPanel binPanel = createBinPanel();
        tabbedPane.addTab("Thùng rác", binPanel); // Bin Items in Vietnamese

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTrashPanel() {
        JPanel trashPanel = new JPanel(new BorderLayout());
        trashTableModel = new DefaultTableModel(new Object[]{"ID", "Tên", "Loại", "Hình ảnh", "URL","Mô tả"}, 0) {
            public Class<?> getColumnClass(int column) {
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
        binTableModel = new DefaultTableModel(new Object[]{"ID", "Tên", "Loại", "Hình ảnh", "URL","Mô tả"}, 0) {
            public Class<?> getColumnClass(int column) {
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
        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setMaxWidth(0);
        table.getColumnModel().getColumn(4).setWidth(0);
    }

    private void loadTrashItems() {
        List<TrashItem> trashItems = itemManagementController.fetchTrashItemData();
        trashTableModel.setRowCount(0);
        for (TrashItem item : trashItems) {
            loadImageAsync(item.getUrl(), trashTableModel, new Object[]{
                    item.getId(), item.getName(), item.getType(), null, item.getUrl(), item.getDescription()
            });
        }
    }

    private void loadBinItems() {
        List<Bin> binItems = itemManagementController.fetchBinData();
        binTableModel.setRowCount(0);
        for (Bin bin : binItems) {
            loadImageAsync(bin.getUrl(), binTableModel, new Object[]{
                    bin.getId(), bin.getName(), bin.getType(), null, bin.getUrl(), bin.getDescription()
            });
        }
    }

    private JPanel createButtonPanel(boolean isTrash) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnBack = new JButton("Quay lại");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);

        btnAdd.addActionListener(e -> addItem(isTrash));
        btnEdit.addActionListener(e -> editItem(isTrash));
        btnDelete.addActionListener(e -> deleteItem(isTrash));
        btnBack.addActionListener(e -> dispose());

        return buttonPanel;
    }

    private void addItem(boolean isTrash) {
        ItemDialog dialog = new ItemDialog(this, null, isTrash);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            String url = isTrash ? dialog.getTrashItem().getUrl() : dialog.getBin().getUrl();
            if (!isValidImageURL(url)) {
                JOptionPane.showMessageDialog(this, "URL không hợp lệ hoặc không dẫn đến hình ảnh. Vui lòng nhập lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (isTrash) {
                itemManagementController.addTrashItem(dialog.getTrashItem());
                loadTrashItems();
            } else {
                itemManagementController.addBin(dialog.getBin());
                loadBinItems();
            }
        }
    }

    private void editItem(boolean isTrash) {
        int selectedRow = isTrash ? trashTable.getSelectedRow() : binTable.getSelectedRow();
        if (selectedRow >= 0) {
            if (isTrash) {
                TrashItem selectedItem = new TrashItem(
                        (String) trashTableModel.getValueAt(selectedRow, 0),
                        (String) trashTableModel.getValueAt(selectedRow, 1),
                        (String) trashTableModel.getValueAt(selectedRow, 2),
                        (String) trashTableModel.getValueAt(selectedRow, 4),
                        (String) trashTableModel.getValueAt(selectedRow, 5)
                );
                ItemDialog dialog = new ItemDialog(this, selectedItem, true);
                dialog.setVisible(true);
                if (dialog.isSucceeded()) {
                    String url = dialog.getTrashItem().getUrl();
                    if (!isValidImageURL(url)) {
                        JOptionPane.showMessageDialog(this, "URL không hợp lệ hoặc không dẫn đến hình ảnh. Vui lòng nhập lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    itemManagementController.updateTrashItem(dialog.getTrashItem());
                    loadTrashItems();
                }
            } else {
                Bin selectedBin = new Bin(
                        (String) binTableModel.getValueAt(selectedRow, 0),
                        (String) binTableModel.getValueAt(selectedRow, 1),
                        (String) binTableModel.getValueAt(selectedRow, 2),
                        (String) binTableModel.getValueAt(selectedRow, 4),
                        (String) binTableModel.getValueAt(selectedRow, 5)
                );
                ItemDialog dialog = new ItemDialog(this, selectedBin, false);
                dialog.setVisible(true);
                if (dialog.isSucceeded()) {
                    String url = dialog.getBin().getUrl();
                    if (!isValidImageURL(url)) {
                        JOptionPane.showMessageDialog(this, "URL không hợp lệ hoặc không dẫn đến hình ảnh. Vui lòng nhập lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    itemManagementController.updateBin(dialog.getBin());
                    loadBinItems();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vật phẩm để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deleteItem(boolean isTrash) {
        int selectedRow = isTrash ? trashTable.getSelectedRow() : binTable.getSelectedRow();
        if (selectedRow >= 0) {
            String id = isTrash ? (String) trashTableModel.getValueAt(selectedRow, 0) : (String) binTableModel.getValueAt(selectedRow, 0);
            if (isTrash) {
                itemManagementController.deleteTrashItem(id);
                loadTrashItems();
            } else {
                itemManagementController.deleteBin(id);
                loadBinItems();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vật phẩm để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadImageAsync(String urlString, DefaultTableModel tableModel, Object[] rowData) {
        if (imageCache.containsKey(urlString)) {
            rowData[3] = imageCache.get(urlString);
            tableModel.addRow(rowData);
        } else {
            SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() {
                    return urlToImage(urlString);
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon icon = get();
                        if (icon != null) {
                            imageCache.put(urlString, icon);
                            rowData[3] = icon;
                            tableModel.addRow(rowData);
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi tải hình ảnh: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }

    private ImageIcon urlToImage(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedImage originalImage = ImageIO.read(url);
            Image scaledImage = originalImage.getScaledInstance(widthImage, heightImage, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("Lỗi khi tải hình ảnh từ URL: " + e.getMessage());
            return null;
        }
    }
    private boolean isValidImageURL(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedImage image = ImageIO.read(url); // Try to read the image from the URL
            return image != null; // Returns true if the image loads successfully
        } catch (IOException e) {
            return false; // Returns false if there's an error (e.g., URL doesn't lead to an image)
        }
    }

}
