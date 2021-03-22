package features;

import java.time.LocalDateTime;

public class CreditCard {
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private Account linkedAccount;
    private Integer cardNumber;
    private Integer CVV;

    public CreditCard(Account linkedAccount) {
        this.creationDate = LocalDateTime.now();
        this.expirationDate = this.creationDate.plusYears(4);
        this.linkedAccount = linkedAccount;
    }

    public Account getLinkedAccount() {
        return linkedAccount;
    }

    public Integer getCardNumber() {
        return cardNumber;
    }
}
