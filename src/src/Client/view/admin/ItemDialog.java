package Client.view.admin;

import Client.controller.admin.ItemManagementController; // Import your controller
import Client.model.Bin;
import Client.model.TrashItem;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ItemDialog extends JDialog {
    private JTextField txtName;
    private JComboBox<String> cmbType; // Changed from JTextField to JComboBox
    private JTextField txtUrl;
    private boolean succeeded;
    private TrashItem trashItem;
    private Bin bin;
    private boolean isTrash;
    private ItemManagementController itemManagementController; // Controller to get types

    public ItemDialog(JFrame parent, Object item, boolean isTrash) {
        super(parent, "Vật phẩm", true);
        this.isTrash = isTrash;

        // Initialize the controller
        itemManagementController = new ItemManagementController();

        if (isTrash) {
            this.trashItem = (TrashItem) item;
        } else {
            this.bin = (Bin) item;
        }

        setLayout(new GridLayout(4, 2));
        add(new JLabel("Tên vật phẩm:")); // Label for item name
        txtName = new JTextField();
        add(txtName);

        add(new JLabel("Loại vật phẩm:")); // Label for item type
        cmbType = new JComboBox<>(); // Create JComboBox
        populateTypeComboBox(); // Populate the JComboBox with types
        add(cmbType);

        add(new JLabel("URL:")); // Label for URL
        txtUrl = new JTextField();
        add(txtUrl);

        // Fill in information if available
        if (isTrash) {
            if (trashItem != null) {
                txtName.setText(trashItem.getName());
                cmbType.setSelectedItem(trashItem.getType()); // Select the item type from JComboBox
                txtUrl.setText(trashItem.getUrl());
            }
        } else {
            if (bin != null) {
                txtName.setText(bin.getName());
                cmbType.setSelectedItem(bin.getType()); // Select the bin type from JComboBox
                txtUrl.setText(bin.getUrl());
            }
        }

        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> saveItem());
        add(btnSave);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);

        pack();
        setLocationRelativeTo(parent);
    }

    private void populateTypeComboBox() {
        List<String> types = itemManagementController.getTrashTypes(); // Get types from the controller
        for (String type : types) {
            cmbType.addItem(type); // Add each type to the JComboBox
        }
    }

    private void saveItem() {
        String name = txtName.getText();
        String type = (String) cmbType.getSelectedItem(); // Get selected item type from JComboBox
        String url = txtUrl.getText();

        // Validate input
        if (name.isEmpty() || type == null || url.isEmpty()) { // Check if type is null
            JOptionPane.showMessageDialog(this, "Tên vật phẩm, Loại và URL không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save item information
        if (isTrash) {
            String id = (trashItem != null) ? trashItem.getId() : java.util.UUID.randomUUID().toString();
            trashItem = new TrashItem(id, name, type, url);
        } else {
            String id = (bin != null) ? bin.getId() : java.util.UUID.randomUUID().toString();
            bin = new Bin(id, name, type, url);
        }

        succeeded = true;
        dispose();
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public TrashItem getTrashItem() {
        return trashItem;
    }

    public Bin getBin() {
        return bin;
    }
}
