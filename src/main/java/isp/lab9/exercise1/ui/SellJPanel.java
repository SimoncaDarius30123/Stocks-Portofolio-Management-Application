package isp.lab9.exercise1.ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SellJPanel extends JPanel {
    private StockMarketJFrame mainFrame;
    private JTextField availableFundsTextField;
    private JComboBox<String> availableStocksComboBox;

    public SellJPanel(StockMarketJFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }


    private void initComponents() {
        setLayout(new GridLayout(2, 2));

        JPanel sellPanel = new JPanel();
        sellPanel.setLayout(new GridLayout(10, 2));

        JLabel availableStocksLabel = new JLabel("Available stocks:");
        availableStocksComboBox = new JComboBox<>();
        updateAvailableStocks();
        sellPanel.add(availableStocksLabel);
        sellPanel.add(availableStocksComboBox);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityTextField = new JTextField();

        JLabel sellPriceLabel = new JLabel("Total sell price:");
        JTextField sellPriceTextField = new JTextField();
        sellPriceTextField.setEditable(false);


        JLabel availableFundsLabel = new JLabel("Available funds:");
        availableFundsTextField = new JTextField(mainFrame.getPortfolio().getCash().toPlainString() + " $");
        availableFundsTextField.setEditable(false);

        JButton sellButton = new JButton("Sell");
        sellButton.setEnabled(false);
        sellButton.addActionListener(e -> {
            try {
                String symbol = (String) availableStocksComboBox.getSelectedItem();
                int quantity = Integer.parseInt(quantityTextField.getText());
                BigDecimal stockPrice = mainFrame.getMarketService().getStockPrice(symbol);
                BigDecimal totalSellPrice = stockPrice.multiply(BigDecimal.valueOf(quantity));

                mainFrame.getPortfolio().getShares().computeIfPresent(symbol, (k, v) -> v - quantity);
                mainFrame.getPortfolio().setCash(mainFrame.getPortfolio().getCash().add(totalSellPrice));


                availableFundsTextField.setText(mainFrame.getPortfolio().getCash().toPlainString() + " $");


                int remainingQuantity = mainFrame.getPortfolio().getShares().getOrDefault(symbol, 0);
                if (remainingQuantity == 0) {
                    availableStocksComboBox.removeItem(symbol);
                }

                quantityTextField.setText("");
                sellPriceTextField.setText("");
                sellButton.setEnabled(false);
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

        JButton calculateSellPriceButton = new JButton("Calculate Sell Price");
        calculateSellPriceButton.addActionListener(e ->
                calculateSellPriceActionPerformed(availableStocksComboBox, quantityTextField, sellPriceTextField, sellButton));

        sellPanel.add(availableStocksLabel);
        sellPanel.add(availableStocksComboBox);
        sellPanel.add(new JPanel());
        sellPanel.add(new JPanel());
        sellPanel.add(quantityLabel);
        sellPanel.add(quantityTextField);
        sellPanel.add(new JPanel());
        sellPanel.add(new JPanel());
        sellPanel.add(sellPriceLabel);
        sellPanel.add(sellPriceTextField);
        sellPanel.add(new JPanel());
        sellPanel.add(new JPanel());
        sellPanel.add(availableFundsLabel);
        sellPanel.add(availableFundsTextField);
        sellPanel.add(new JPanel());
        sellPanel.add(new JPanel());
        sellPanel.add(calculateSellPriceButton);
        sellPanel.add(sellButton);

        add(sellPanel);
        add(new JPanel());
        add(new JPanel());
        add(new JPanel());
    }



    private void calculateSellPriceActionPerformed(JComboBox<String> availableStocksComboBox,
                                                   JTextField quantityTextField,
                                                   JTextField totalSellPriceTextField,
                                                   JButton sellButton) {
        String symbol = (String) availableStocksComboBox.getSelectedItem();
        int availableQuantity = mainFrame.getPortfolio().getShares().getOrDefault(symbol, 0);

        try {
            int requestedQuantity = Integer.parseInt(quantityTextField.getText());
            if (requestedQuantity > availableQuantity) {
                JOptionPane.showMessageDialog(this,
                        "You don't have enough shares to sell.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal stockPrice = mainFrame.getMarketService().getStockPrice(symbol);
            BigDecimal totalSellPrice = stockPrice.multiply(BigDecimal.valueOf(requestedQuantity));
            totalSellPriceTextField.setText(totalSellPrice.toString());
            sellButton.setEnabled(true);
        } catch (NumberFormatException e) {
            totalSellPriceTextField.setText("Invalid quantity value!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(StockMarketJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void updateAvailableStocks() {
        availableStocksComboBox.removeAllItems();
        mainFrame.getPortfolio().getShares().forEach((symbol, quantity) -> {
            availableStocksComboBox.addItem(symbol);
        });
    }
}