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
        super(parent, "Vật phẩm", true);
        this.isTrash = isTrash;

        if (isTrash) {
            this.trashItem = (TrashItem) item;
        } else {
            this.bin = (Bin) item;
        }

        setLayout(new GridLayout(4, 2));
        add(new JLabel("Tên vật phẩm:")); // Thay đổi nhãn
        txtName = new JTextField();
        add(txtName);

        add(new JLabel("Loại vật phẩm:")); // Thay đổi nhãn
        txtType = new JTextField();
        add(txtType);

        add(new JLabel("URL:"));
        txtUrl = new JTextField();
        add(txtUrl);

        // Điền thông tin nếu có
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

        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> saveItem());
        add(btnSave);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);

        pack();
        setLocationRelativeTo(parent);
    }

    private void saveItem() {
        String name = txtName.getText();
        String type = txtType.getText();
        String url = txtUrl.getText();

        // Kiểm tra hợp lệ
        if (name.isEmpty() || type.isEmpty() || url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên vật phẩm, Loại và URL không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lưu thông tin vật phẩm
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
