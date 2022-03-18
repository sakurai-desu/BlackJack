import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BlackJack {
    private static Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);
    private static final String[] TRUMP_RANK = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
    private static final String[] TRUMP_EREVEN_MORE = { "J", "Q", "K" };

    private static ArrayList<String> playerTrumpList = new ArrayList<>();
    private static ArrayList<String> dealerTrumpList = new ArrayList<>();
    // #region 単語の定数
    private static final String PLAYER = "あなた";
    private static final String DEALER = "ディーラー";
    private static final String YES = "Y";
    private static final String WIN = "勝ち";
    private static final String LOSE = "負け";
    private static final String ACE = "A";
    // #endregion

    // #region文章の定数
    private static final String TRAMP_SUM_MESSAGE = "%sの合計は%dです。\n";
    private static final String DRAW_CONFIRMATION_MESSAGE = "もう一枚カードを引きますか(Y/N):";
    private static final String DISTRIBUTE_MESSAGE = "%sに%sが配られました\n";
    private static final String BURST_MESSAGE = "合計が21を超えました\n";
    private static final String GAME_END_MESSAGE = "あなたの%sです";
    // #endregion

    // #region数字の定数
    private static final int DEALER_STOP_NUMBER = 17;
    private static final int EREVEN_A = 11;
    private static final int ONE_A = 1;
    private static final int TEN_OR_MORE = 10;
    private static final int BLACK_JACK_RANK = 21;
    private static final int DETERMINE_THE_SIZE_OF_A = 10;
    private static int playerRankSum = 0;
    private static int dealerRankSum = 0;
    // #endregion

    public static void main(String[] args) {
        final int FIRST_DISTRIBUTE_TRUMP = 2;
        distributefirstTrump(FIRST_DISTRIBUTE_TRUMP);
        drawPlayerTrump();
        drawDealer();
        CompareRankSum();
    }

    private static void distributefirstTrump(final int DISTRIBUTE_NUM) {
        for (int i = 0; i < DISTRIBUTE_NUM; i++) {
            playerTrumpList.add(getTrumpRank());
            addPlayerRankSum(playerTrumpList.get(i));
            showDistributeMessage(PLAYER, playerTrumpList.get(i));
            dealerTrumpList.add(getTrumpRank());
            addDealerRankSum(dealerTrumpList.get(i));
            showDistributeMessage(DEALER, dealerTrumpList.get(i));
        }
        showTrumpTotal();
    }

    private static void CompareRankSum() {
        if (isWin()) {
            showGameEndMessage(WIN);
        }
        showGameEndMessage(LOSE);
    }

    private static boolean isWin() {
        return playerRankSum > dealerRankSum;
    }

    private static void drawDealer() {
        if (isDealerDarw()) {
            drawDealerTrump();
            drawDealer();
        }
    }

    private static boolean isDealerDarw() {
        return dealerRankSum <= DEALER_STOP_NUMBER;
    }

    private static void drawDealerTrump() {
        dealerTrumpList.add(getTrumpRank());
        addDealerRankSum(dealerTrumpList.get(dealerTrumpList.size() - 1));
        showDistributeMessage(DEALER, dealerTrumpList.get(dealerTrumpList.size() - 1));
        showRankSumMessage(DEALER, dealerRankSum);
        confirmationNum();
    }

    private static void drawPlayerTrump() {
        showDrawConfirmationMessage();
        String playerReply = scanner.next();
        if (isDraw(playerReply)) {
            drawPlayerCard();
            drawPlayerTrump();
        }
        return;
    }

    private static Boolean isDraw(String replay) {
        return replay.equals(YES);
    }

    private static void showDrawConfirmationMessage() {
        System.out.print(DRAW_CONFIRMATION_MESSAGE);
    }

    private static void drawPlayerCard() {
        playerTrumpList.add(getTrumpRank());
        addPlayerRankSum(playerTrumpList.get(playerTrumpList.size() - 1));
        showDistributeMessage(PLAYER, playerTrumpList.get(playerTrumpList.size() - 1));
        showRankSumMessage(PLAYER, playerRankSum);
        confirmationNum();
    }

    private static void showTrumpTotal() {
        showRankSumMessage(DEALER, dealerRankSum);
        showRankSumMessage(PLAYER, playerRankSum);
    }

    private static void addDealerRankSum(String trumpRank) {
        int trumpRankNumber = convertTrumpRank(trumpRank, DEALER);
        dealerRankSum += trumpRankNumber;
    }

    private static void addPlayerRankSum(String trumpRank) {
        int trumpRankNumber = convertTrumpRank(trumpRank, PLAYER);
        playerRankSum += trumpRankNumber;
    }

    private static void showRankSumMessage(String displayName, int rankSum) {
        System.out.printf(TRAMP_SUM_MESSAGE, displayName, rankSum);
    }

    private static int convertTrumpRank(String trumpRank, String turnPlayer) {
        int afterRank = 0;
        if (isTenOrMore(trumpRank)) {
            afterRank = TEN_OR_MORE;
            return afterRank;
        }
        if (!isAce(turnPlayer)) {
            afterRank = Integer.parseInt(trumpRank);
            return afterRank;
        }
        if (isPlayerDraw(turnPlayer)) {
            if (isConvertLargeA(playerRankSum)) {
                afterRank = EREVEN_A;
                return afterRank;
            }
        }
        if (!isPlayerDraw(turnPlayer)) {
            if (isConvertLargeA(dealerRankSum)) {
                afterRank = EREVEN_A;
                return afterRank;
            }
        }
        return afterRank = ONE_A;
    }

    private static boolean isPlayerDraw(String turnPlayer) {
        return turnPlayer.equals(PLAYER);
    }

    private static boolean isAce(String turnPlayer) {
        return turnPlayer.equals(ACE);
    }

    private static boolean isTenOrMore(String trumpRank) {
        return trumpRank == TRUMP_EREVEN_MORE[0] || trumpRank == TRUMP_EREVEN_MORE[1]
                || trumpRank == TRUMP_EREVEN_MORE[2];
    }

    private static boolean isConvertLargeA(int turnPlayerRankSum) {
        return turnPlayerRankSum <= DETERMINE_THE_SIZE_OF_A;
    }

    private static void showDistributeMessage(String Target, String trumpNumber) {
        System.out.printf(DISTRIBUTE_MESSAGE, Target, trumpNumber);
    }

    private static String getTrumpRank() {
        return TRUMP_RANK[random.nextInt(TRUMP_RANK.length)];
    }

    private static void confirmationNum() {
        if (isBurst(dealerRankSum)) {
            showBurstMessage();
            showGameEndMessage(WIN);
        } else if (isBurst(playerRankSum)) {
            showBurstMessage();
            showGameEndMessage(LOSE);
        }
    }

    private static void showBurstMessage() {
        System.out.print(BURST_MESSAGE);
    }

    private static boolean isBurst(int rankSum) {
        return rankSum > BLACK_JACK_RANK;
    }

    private static void showGameEndMessage(String victoryOrDefeat) {
        System.out.printf(GAME_END_MESSAGE, victoryOrDefeat);
        endGame();
    }

    private static void endGame() {
        System.exit(0);
    }
}
