package AWT;

import java.awt.*;
import java.awt.event.*;

public class BillingSystem extends Frame implements ActionListener {
    private TextField tfCode, tfName, tfPrice, tfQty;
    private CheckboxGroup taxGroup;
    private Checkbox cbTax0, cbTax5, cbTax10;
    private Button btnAdd, btnClear;
    private TextArea tableArea;
    private Label headerLabel;
    private Label lblGrandTotal;
    private double grandTotal = 0.0;

    private static final String HEADER =
            pad("Code",     10) + pad("Name",        18) +
                    pad("Price",     9) + pad("Qty",          6) +
                    pad("Tax",       7) + pad("Total",       10);
    public BillingSystem() {
        super("Product Billing System");

        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(240, 242, 245));
        Panel northPanel = new Panel(new GridLayout(2, 1, 4, 4));
        northPanel.add(buildFormPanel());
        northPanel.add(buildControlPanel());
        add(northPanel, BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildSouthPanel(), BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setSize(660, 520);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private Panel buildFormPanel() {
        Panel p = new Panel(new GridLayout(2, 4, 6, 6));
        p.setBackground(new Color(225, 230, 240));

        p.add(makeLabel("Product Code:"));
        p.add(makeLabel("Product Name:"));
        p.add(makeLabel("Price ($):"));
        p.add(makeLabel("Quantity:"));

        tfCode  = new TextField(10);
        tfName  = new TextField(16);
        tfPrice = new TextField(8);
        tfQty   = new TextField(5);

        p.add(tfCode);
        p.add(tfName);
        p.add(tfPrice);
        p.add(tfQty);

        return p;
    }
    private Panel buildControlPanel() {
        Panel p = new Panel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        p.setBackground(new Color(215, 222, 235));

        p.add(makeLabel("Tax:"));

        taxGroup = new CheckboxGroup();
        cbTax0  = new Checkbox("0%",  taxGroup, true);
        cbTax5  = new Checkbox("5%",  taxGroup, false);
        cbTax10 = new Checkbox("10%", taxGroup, false);

        p.add(cbTax0);
        p.add(cbTax5);
        p.add(cbTax10);

        // Spacer
        p.add(new Label("     "));

        btnAdd = new Button("  Add Product  ");
        btnAdd.setBackground(new Color(60, 120, 200));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(this);
        p.add(btnAdd);

        return p;
    }
    private Panel buildTablePanel() {
        Panel p = new Panel(new BorderLayout(0, 2));
        headerLabel = new Label(HEADER);
        headerLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        headerLabel.setBackground(new Color(60, 80, 120));
        headerLabel.setForeground(Color.WHITE);
        p.add(headerLabel, BorderLayout.NORTH);
        tableArea = new TextArea("", 12, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
        tableArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tableArea.setEditable(false);
        tableArea.setBackground(new Color(250, 252, 255));

        ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scrollPane.add(tableArea);
        p.add(scrollPane, BorderLayout.CENTER);
        return p;
    }
    private Panel buildSouthPanel() {
        Panel p = new Panel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        p.setBackground(new Color(225, 230, 240));

        lblGrandTotal = new Label("Total:  $0.00");
        lblGrandTotal.setFont(new Font("SansSerif", Font.BOLD, 13));
        p.add(lblGrandTotal);

        btnClear = new Button("  Clear All  ");
        btnClear.setBackground(new Color(200, 60, 60));
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(this);
        p.add(btnClear);

        return p;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            handleAddProduct();
        } else if (e.getSource() == btnClear) {
            handleClear();
        }
    }
    private void handleAddProduct() {
        // Read and trim input
        String code  = tfCode.getText().trim();
        String name  = tfName.getText().trim();
        String priceStr = tfPrice.getText().trim();
        String qtyStr   = tfQty.getText().trim();
        if (code.isEmpty() || name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            showError("All fields are required.");
            return;
        }
        double price;
        int qty;
        try {
            price = Double.parseDouble(priceStr);
            qty   = Integer.parseInt(qtyStr);
        } catch (NumberFormatException ex) {
            showError("Price must be a decimal number and Quantity must be a whole number.");
            return;
        }
        if (price <= 0) { showError("Price must be greater than zero."); return; }
        if (qty   <= 0) { showError("Quantity must be greater than zero."); return; }
        Checkbox selected = taxGroup.getSelectedCheckbox();
        double taxRate = 0.0;
        if      (selected == cbTax5)  taxRate = 0.05;
        else if (selected == cbTax10) taxRate = 0.10;

        double subtotal = price * qty;
        double taxAmt   = subtotal * taxRate;
        double total    = subtotal + taxAmt;
        String taxLabel = (int)(taxRate * 100) + "%";
        String row =
                pad(code,              10) +
                        pad(name,              18) +
                        pad(String.format("%.2f", price),   9) +
                        pad(String.valueOf(qty),              6) +
                        pad(taxLabel,                          7) +
                        pad(String.format("$%.2f", total),   10);
        tableArea.append(row + "\n");
        grandTotal += total;
        lblGrandTotal.setText(String.format("Grand Total:  $%.2f", grandTotal));
        clearFields();
        tfCode.requestFocus();
    }
    private void handleClear() {
        tableArea.setText("");
        grandTotal = 0.0;
        lblGrandTotal.setText("Grand Total:  $0.00");
        clearFields();
        cbTax0.setState(true);
        tfCode.requestFocus();
    }
    private void clearFields() {
        tfCode.setText("");
        tfName.setText("");
        tfPrice.setText("");
        tfQty.setText("");
    }
    private void showError(String msg) {
        Dialog d = new Dialog(this, "Input Error", true);
        d.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 16));
        d.add(new Label(msg));
        Button ok = new Button("OK");
        ok.addActionListener(ev -> d.dispose());
        d.add(ok);
        d.setSize(360, 110);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
    private Label makeLabel(String text) {
        Label l = new Label(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }
    private static String pad(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(BillingSystem::new);
    }
}
