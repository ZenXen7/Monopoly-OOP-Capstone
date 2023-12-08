package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class Player extends BoardObject {

    private int playerNumber;
    private Board gameBoard;

    JLabel lblPlayerNumber;
    static int totalPlayers = 0; // we might need this number later on
    static HashMap<Integer, Integer> ledger= new HashMap<>();

    private int currentSquareNumber = 0; // where player is currently located on (0 - 19). initially zero
    private ArrayList<Integer> titleDeeds = new ArrayList<Integer>(); // squares that the player has
    private int wallet = 500; // initial money

    public ArrayList<Integer> getTitleDeeds() {
        return titleDeeds;
    }

    public int getWallet() {
        return wallet;
    }

    public void withdrawFromWallet(int amount) {
        wallet -= amount;

        if (wallet < 0) {

            handleBankruptcy();
        } else if (wallet == 0) {

            MonopolyMain.infoConsole.setText("Your balance is now $0. Ending your turn.");
            MonopolyMain.btnNextTurn.setEnabled(true);
            MonopolyMain.btnBuy.setEnabled(false);
            MonopolyMain.btnPayRent.setEnabled(false);
            MonopolyMain.btnRollDice.setEnabled(false);
        }
    }

    public boolean isBankrupt() {
        return wallet < 0;
    }


    private void handleBankruptcy() {
        // Existing code...

        // Show a dialog with options for surrendering or selling property
        int option = JOptionPane.showOptionDialog(
                this,
                "You are bankrupt! What do you want to do?",
                "Bankruptcy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Surrender", "Sell Property"},
                "Surrender");

        if (option == JOptionPane.YES_OPTION) {
            // Implement surrender logic
            surrender();
        } else {
            // Implement sell property logic
            sellProperties();
        }

        // Existing code...
    }

    private void surrender() {
        // Implement surrender logic here
        // For example, you can end the game or take other actions
        MonopolyMain.errorBox("Player " + playerNumber + " has surrendered. Game Over!", "Game Over");
        System.exit(0); // Terminate the game (you may want to handle this differently)
    }

    private void sellProperties() {
        // Implement property selling logic here
        // For example, display a dialog to choose properties to sell

        ArrayList<Integer> propertiesToSell = new ArrayList<>(titleDeeds);
        int choice = JOptionPane.showOptionDialog(
                null,
                "You need to sell properties to cover your debt. Select properties to sell:",
                "Sell Properties",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                propertiesToSell.toArray(),
                null);

        if (choice >= 0 && choice < propertiesToSell.size()) {
            int propertyIndex = propertiesToSell.get(choice);
            int propertyPrice = gameBoard.getAllSquares().get(propertyIndex).getPrice();

            // Sell the selected property
            wallet += propertyPrice;
            titleDeeds.remove(Integer.valueOf(propertyIndex));
            Player.ledger.remove(propertyIndex);
            MonopolyMain.infoConsole.setText("Player " + playerNumber + " sold " + gameBoard.getAllSquares().get(propertyIndex).getName() + " for $" + propertyPrice);

            // Check if the player is still bankrupt
            if (wallet < 0) {
                // Player is still bankrupt after selling a property
                handleBankruptcy();
            }
        }
    }


    public void depositToWallet(int depositAmount) {
        wallet += depositAmount;
        System.out.println("Payday for player "+getPlayerNumber()+". You earned $200!");
    }


    public int getCurrentSquareNumber() {
        return currentSquareNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public boolean hasTitleDeed(int squareNumber) {
        return titleDeeds.contains(squareNumber) ? true : false;
    }

    public void buyTitleDeed(int squareNumber) {
        if(ledger.containsKey(squareNumber)) {
            System.out.println("It's already bought by someone. You cannot by here.");
        } else {
            titleDeeds.add(this.getCurrentSquareNumber());
            ledger.put(squareNumber, this.getPlayerNumber()); // everytime a player buys a title deed, it is written in ledger, for example square 1 belongs to player 2

        }
    }


    public Player(int playerNumber, Color color, Board gameBoard) {
        super(playerNumber * 30, 33, 20, 28); // Adjust coordinates based on player number
        this.playerNumber = playerNumber;
        this.setBackground(color);
        this.gameBoard = gameBoard;
        lblPlayerNumber = new JLabel("" + playerNumber);
        lblPlayerNumber.setFont(new Font("Lucida Grande", Font.BOLD, 15));
        lblPlayerNumber.setForeground(Color.WHITE);
        this.add(lblPlayerNumber);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }


    int[] xLocationsOfPlayer1 = {31, 131, 231, 331, 431, 531,
            531, 531, 531, 531, 531,
            431, 331, 231, 131, 31,
            31, 31, 31, 31};

    int[] yLocationsOfPlayer1 = {33, 33, 33, 33, 33, 33,
            133, 233, 333, 433, 533,
            533, 533, 533, 533, 533,
            433, 333, 233, 133};

    int[] xLocationsOfPlayer2 = {61, 191, 291, 361, 461, 561,
            561, 561, 561, 561, 561,
            461, 361, 261, 161, 61,
            61, 61, 61, 61};

    int[] yLocationsOfPlayer2 = {33, 33, 33, 33, 33, 33,
            133, 233, 333, 433, 533,
            533, 533, 533, 533, 533,
            433, 333, 233, 133};


    public void move(int dicesTotal) {
        if(currentSquareNumber + dicesTotal > 19) {
            depositToWallet(200);
        }
        int targetSquare = (currentSquareNumber + dicesTotal) % 20;
        currentSquareNumber = targetSquare;

        if(MonopolyMain.nowPlaying == 0) {
            this.setLocation(xLocationsOfPlayer1[targetSquare], yLocationsOfPlayer1[targetSquare]);
        } else {
            this.setLocation(xLocationsOfPlayer2[targetSquare], yLocationsOfPlayer2[targetSquare]);
        }

        if(ledger.containsKey(this.getCurrentSquareNumber())) {
            MonopolyMain.infoConsole.setText("This property belongs to player "+ledger.get(this.getCurrentSquareNumber()));
        }
        //ledger.put(this.getCurrentSquareNumber(), this.getPlayerNumber());
    }



    // by comparing player's coordinates according to the board, we will get it's
    // current square number
    // currently unused, found a better way
    public int getCurrentSquareNumberByCoordinates() {

        int x = this.getX();
        int y = this.getY();

        if(x < 100) {
            if(y < 100) {
                return 0;
            } else if(y > 100 && y < 200) {
                return 19;
            } else if(y > 200 && y < 300) {
                return 18;
            } else if(y > 300 && y < 400) {
                return 17;
            } else if(y > 400 && y < 500) {
                return 16;
            } else {
                return 15;
            }
        } else if(x > 100 && x < 200) {
            if(y < 100) {
                return 1;
            } else {
                return 14;
            }
        } else if(x > 200 && x < 300) {
            if(y < 100) {
                return 2;
            } else {
                return 13;
            }
        } else if(x > 300 && x < 400) {
            if(y < 100) {
                return 3;
            } else {
                return 12;
            }
        }else if(x > 400 && x < 500) {
            if(y < 100) {
                return 4;
            } else {
                return 11;
            }
        } else {
            if(y < 100) {
                return 5;
            } else if(y > 100 && y < 200) {
                return 6;
            } else if(y > 200 && y < 300) {
                return 7;
            } else if(y > 300 && y < 400) {
                return 8;
            } else if(y > 300 && y < 500) {
                return 9;
            } else {
                return 10;
            }
        }
    }

}
