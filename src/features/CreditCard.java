package features;

import features.interfaces.Numeric;

import java.time.LocalDateTime;

public class CreditCard {
    private final Integer id;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private Account linkedAccount;
    private Long cardNumber;
    private Integer CVV;
    private final Long MAX_CARD_NUMBER = 9999999999999999L;
    private final Long MIN_CARD_NUMBER = 1000000000000000L;
    private final int MAX_CVV = 999;
    private final int MIN_CVV = 100;


    public CreditCard(Account linkedAccount) {
        this.id = Numeric.RandomNumber(0, 100);
        this.creationDate = LocalDateTime.now();
        this.expirationDate = this.creationDate.plusYears(4);
        this.linkedAccount = linkedAccount;
        this.cardNumber = Numeric.RandomNumber(MIN_CARD_NUMBER, MAX_CARD_NUMBER);
        this.CVV = Numeric.RandomNumber(MIN_CVV, MAX_CVV);
    }

    public Account getLinkedAccount() {
        return linkedAccount;
    }

    public Long getCardNumber() {
        return cardNumber;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }
}
