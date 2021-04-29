# Ubermensch
#### Proiect pentru cursul de Programare Avansată pe Obiecte
Aplicatie bancara realizata in Java, utilizand concepte de Programare Orientată pe Obiecte (OOP), design patterns și baze de date. Momentan este o aplicatie pentru consola, eventual as putea face upgrade la un GUI simplu.  

## TODO
1. Să adaug Exceptii custom
2. Să completez funcționalitatea clasei ```BankApp``` la rularea aplicatiei.
3. Să adaug GET Requests/Fetch catre un API care sa-mi permita sa colectez informatii despre exchange rates
ale valutelor din `Currency`
4. Partea III a proiectului + CRUD pe 4 clase (Client, Account, Deposit?, Credit?)
   
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
private HashMap<ExchangeCurrency, Double> exchangeRates;
private TreeSet<Transaction> transactions;
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

### Cerințele pentru etapa II:
1. Extindeți proiectul din prima etapă prin realizarea presistenței, utilizând fișiere.
   * Se vor realiza fișiere de tip csv pentru cel puțin 4 dintre clasele definite în 
   prima etapă:
   ```
   Client.csv
   Account.csv
   Deposit.csv
   Credit.csv
   Transaction.csv
   Currency.csv
   ```
   * Se vor realiza servicii singleton generice pentru scrierea și citirea din fișiere:
   ```java
   public class CSVReader;
   public class CSVWriter;
   ```
   * La pornirea programului, se vor încărca datele din fișiere utilizând servicile create:
   ```java
   // In aceasta clasa se contine aceasta functionalitate de load si store a datelor in fisiere
   public class Database;
   ```
2. Realizarea unui serviciu de audit care să scrie într-un fișier de tip csv de fiecare dată când
este executată una dintre acțiunile descrise în prima etapă. Structura fișierulu: nume_acțiune, timestamp
```java
public class AuditService;
```
Surse ajutătoare: 
* CSV Read and Write Operations - `https://stackabuse.com/reading-and-writing-csvs-in-java/`
