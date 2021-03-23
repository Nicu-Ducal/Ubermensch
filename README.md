# Ubermensch
#### Proiect pentru cursul de Programare Avansată pe Obiecte
Aplicatie bancara realizata in Java, utilizand concepte de Programare Orientată pe Obiecte (OOP), design patterns și baze de date. Momentan este o aplicatie pentru consola, eventual as putea face upgrade la un GUI simplu.  

## TODO
1. Să completez funcționalitățile claselor ```CreditService``` și ```DepositService```
2. Sa adaug funcționalitate pentru ```Currency``` si ```ExchangeCurrency```
3. Sa definesc tranzactiile
4. Sa completez funcționalitatea clasei ```BankApp``` la rularea aplicatiei.

### Descrierea aplicației
Utilizatorii aplicației pot fi clienți ai băncii sau angajații acesteia. Mai întâi, pentru a efectua
careva operații, clienții sau angajații trebuie să se logheze cu contul lor, iar apoi în dependența
de tipul utilizatorului pot face careva operații. De exemplu, un client poate să-și verifice
toate conturile pe care le are, toate depozitele și creditele, poate solicita afișarea
stării unui cont, emiterea unui card bancar pentru un cont, deschiderea unui depozit bancar etc.

### Cerințele pentru etapa I:
1.1. Cel puțin 8 clase (clasele principale ale aplicației):
```java
BankApp                     // Clasa care ruleaza aplicatia (Singleton)
Person                      // Clasa abstracta
Client + ClientService
Account + AccountService
Deposit + DepositService
Credit + CreditService
CreditCard
Currency + CurrencyService
```
2. Cel puțin 10 interogări (interogările principale ale aplicației):
```java
ClientService:
    LogIn()
    LogOut()
    Register()
AccountService:
    selectAccount()
    openAccount()
    printExtrasDeCont()
    addBalance()
    withdrawBalance()
DepositService:
    openDeposit()
    printExtras()
CreditService:
    openCredit()
    addMoneyToCredit()
```
2.1. Clase simple cu atribute private/protected și metode de acces
  2. Cel puțin doua colecții să gestioneze obiectele definite anterior, dintre care cel puțin una să fie sortată
```java
private List<Account> accounts;
private List<Deposit> deposits;
private List<Credit> credits;
```
  3. Utilizarea moștenirii pentru crearea unor clase adiționale și utilizarea în cadrul colecțiilor
```java
public class Credit extends Contract
public class Deposit extends Contract
public class Client extends Person
```
  4. Cel puțin o clasă de serviciu care să expună operațiile
```java
public class ClientService
public class AccountService
public class CreditService
public class CurrencyService
```
  5. O clasa Main din care sunt făcute apelurile către funcțiile de serviciu. Pentru aceastră cerință am făcut o clasă 
     Main din care apelez obiectul Singleton ```BankApp``` și din el apelez funcțiile claselor 
     de serviciu
```java
public class BankApp
```

