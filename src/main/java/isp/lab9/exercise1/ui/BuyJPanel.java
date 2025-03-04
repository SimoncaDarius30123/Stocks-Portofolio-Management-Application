package isp.lab9.exercise1.ui;

import isp.lab9.exercise1.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuyJPanel extends JPanel {
    private StockMarketJFrame mainFrame;
    private JComboBox<String> availableStocksComboBoxSell;

    public BuyJPanel(StockMarketJFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(2, 2));

        JPanel buyPanel = new JPanel();
        buyPanel.setLayout(new GridLayout(10, 2));

        JLabel availableFundsLabel = new JLabel("Available funds:");
        JTextField availableFundsTextField = new JTextField(mainFrame.getPortfolio().getCash().toPlainString() + " $");
        availableFundsTextField.setEditable(false);

        JLabel symbolLabel = new JLabel("Symbol:");
        JComboBox<String> symbolComboBox = new JComboBox<>();
        symbolComboBox.setModel(new DefaultComboBoxModel(mainFrame.getMarketService().getSymbols()));

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityTextField = new JTextField();

        JLabel costLabel = new JLabel("Total cost:");
        JTextField costTextField = new JTextField();
        costTextField.setEditable(false);

        JButton buyButton = new JButton("Buy");
        buyButton.setEnabled(false);
        buyButton.addActionListener(e -> {
            try {
                String symbol = (String) symbolComboBox.getSelectedItem();
                BigDecimal stockPrice = mainFrame.getMarketService().getStockPrice(symbol);
                int quantity = Integer.parseInt(quantityTextField.getText());
                BigDecimal totalCost = stockPrice.multiply(BigDecimal.valueOf(quantity));
                if (mainFrame.getPortfolio().getCash().compareTo(totalCost) >= 0) {
                    mainFrame.getPortfolio().getShares().merge(symbol, quantity, Integer::sum);
                    mainFrame.getPortfolio().setCash(mainFrame.getPortfolio().getCash().subtract(totalCost));
                    availableFundsTextField.setText(Utils.formatBigDecimal(mainFrame.getPortfolio().getCash()) + " $");
                    quantityTextField.setText("");
                    costTextField.setText("");
                    buyButton.setEnabled(false);
                    DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) availableStocksComboBoxSell.getModel();
                    model.addElement(symbol);
                    mainFrame.updateAvailableStocks();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient funds!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid quantity value!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(StockMarketJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        JButton costButton = new JButton("Get cost");
        costButton.addActionListener(e ->
                calculateTotalCostActionPerformed(symbolComboBox, quantityTextField, costTextField, buyButton));

        buyPanel.add(availableFundsLabel);
        buyPanel.add(availableFundsTextField);
        buyPanel.add(new JPanel());
        buyPanel.add(new JPanel());
        buyPanel.add(symbolLabel);
        buyPanel.add(symbolComboBox);
        buyPanel.add(new JPanel());
        buyPanel.add(new JPanel());
        buyPanel.add(quantityLabel);
        buyPanel.add(quantityTextField);
        buyPanel.add(new JPanel());
        buyPanel.add(new JPanel());
        buyPanel.add(costLabel);
        buyPanel.add(costTextField);
        buyPanel.add(new JPanel());
        buyPanel.add(new JPanel());
        buyPanel.add(costButton);
        buyPanel.add(buyButton);
        add(buyPanel);
        add(new JPanel());
        add(new JPanel());
        add(new JPanel());

        availableStocksComboBoxSell = new JComboBox<>();
        availableStocksComboBoxSell.setModel(new DefaultComboBoxModel(mainFrame.getPortfolio().getShares().keySet().toArray()));
    }

    /**
     * Calculates the total transaction cost
     */
    private void calculateTotalCostActionPerformed(JComboBox<String> symbolComboBox,
                                                   JTextField quantityTextField,
                                                   JTextField totalCostTextField,
                                                   JButton buyButton) {
        try {
            String symbol = (String) symbolComboBox.getSelectedItem();
            BigDecimal stockPrice = mainFrame.getMarketService().getStockPrice(symbol);

            try {
                int quantity = Integer.parseInt(quantityTextField.getText());
                totalCostTextField.setText(
                        Utils.formatBigDecimal(stockPrice.multiply(new BigDecimal(quantity))));
                buyButton.setEnabled(true);
            } catch (NumberFormatException e) {
                totalCostTextField.setText("Invalid quantity value!");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(StockMarketJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

