import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;

public class BlackJack {
    final static String winMessage = "YOU WIN!";
    final static String loseMessage = "YOU LOSE!";
    final static String tieMessage = "TIE!";

    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        @Override
        public String toString() {
            // form: 2-H = two of hearts
            return value + "-" + type;
        }

        public int getValue() {
            int toReturn;
            switch (value) {
                case "A":
                    toReturn = 11;
                    break;
                case "J":
                case "Q":
                case "K":
                    toReturn = 10;
                    break;
                default:
                    toReturn = Integer.parseInt(value);
                    break;
            }
            return toReturn;
            // return {"A", "J", "Q", "K"}. this.type this.getValue();
        }

        public boolean isAce() {
            return value == "A";
        }

        public String getImagePath() {
            return "./cards/" + toString() + ".png";
        }
    }

    ArrayList<Card> deck;
    Random random = new Random();

    // dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    // player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    // window
    int boardWidth = 600;
    int boardHeight = 600;
    int cardWidth = 110;
    int cardHeight = 154;
    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
                // draw hidden card
                Image hiddenCardImage = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (!stayButton.isEnabled()) {
                    hiddenCardImage = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImage, 20, 20, cardWidth, cardHeight, null);

                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }

                // player hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);
                }

                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("STAY: ");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if (playerSum > 21) {
                        message = loseMessage;
                    } else if (dealerSum > 21) {
                        message = winMessage;
                    } else if (playerSum == dealerSum) {
                        message = tieMessage;
                    } else if (playerSum > dealerSum) {
                        message = winMessage;
                    } else if (playerSum < dealerSum) {
                        message = loseMessage;
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");
    JButton resetButton = new JButton("Reset");

    BlackJack() {
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        resetButton.setFocusable(false);
        buttonPanel.add(resetButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // button functionality
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce() > 21) {
                    hitButton.setEnabled(false);
                }
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
                gamePanel.repaint();
            }
        });

        gamePanel.repaint();
    }

    public void startGame() {
        buildDeck();
        shuffleDeck();

        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        // player
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;
        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);
    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
        String[] types = { "C", "D", "H", "S" };

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        // for each card position in deck swap it with a random other card in deck
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(i);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
    }

    public int reducePlayerAce() {
        int toReturn = playerSum;
        int playerAceCountTemp = playerAceCount;
        while (toReturn > 21 && playerAceCountTemp > 0) {
            toReturn -= 10;
            playerAceCountTemp -= 1;
        }
        return toReturn;
    }

    public int reduceDealerAce() {
        int toReturn = dealerSum;
        int dealerAceCountTemp = dealerAceCount;
        while (toReturn > 21 && dealerAceCountTemp > 0) {
            toReturn -= 10;
            dealerAceCountTemp -= 1;
        }
        return toReturn;
    }

    public void resetGame() {
        startGame();
        hitButton.setEnabled(true);
        stayButton.setEnabled(true);
    }
}
