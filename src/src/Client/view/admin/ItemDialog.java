package Client.view.admin;

import Client.model.Bin;
import Client.model.TrashItem;

import javax.swing.*;
import java.awt.*;

public class ItemDialog extends JDialog {
    private JTextField txtName;
    private JTextField txtType;
    private JTextField txtUrl;
    private boolean succeeded;
    private TrashItem trashItem;
    private Bin bin;
    private boolean isTrash;

    public ItemDialog(JFrame parent, Object item, boolean isTrash) {
        super(parent, "Item", true);
        this.isTrash = isTrash;

        if (isTrash) {
            this.trashItem = (TrashItem) item;
        } else {
            this.bin = (Bin) item;
        }

        setLayout(new GridLayout(4, 2));
        add(new JLabel("Name:"));
        txtName = new JTextField();
        add(txtName);

        add(new JLabel("Type:"));
        txtType = new JTextField();
        add(txtType);

        add(new JLabel("URL:"));
        txtUrl = new JTextField();
        add(txtUrl);

        if (isTrash) {
            if (trashItem != null) {
                txtName.setText(trashItem.getName());
                txtType.setText(trashItem.getType());
                txtUrl.setText(trashItem.getUrl());
            }
        } else {
            if (bin != null) {
                txtName.setText(bin.getName());
                txtType.setText(bin.getType());
                txtUrl.setText(bin.getUrl());
            }
        }

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> saveItem());
        add(btnSave);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);

        pack();
        setLocationRelativeTo(parent);
    }

    private void saveItem() {
        String name = txtName.getText();
        String type = txtType.getText();
        String url = txtUrl.getText();

        if (name.isEmpty() || type.isEmpty() || url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Type, and URL cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
