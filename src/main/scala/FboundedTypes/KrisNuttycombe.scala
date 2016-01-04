package FboundedTypes

// code examples from:
// http://logji.blogspot.co.uk/2012/11/f-bounded-type-polymorphism-give-up-now.html
object KrisNuttycombe {

  trait Account[T <: Account[T]] {
    def addFunds(amount: BigDecimal): T
  }

  class BrokerageAccount(total: BigDecimal) extends Account[BrokerageAccount] {
    def addFunds(amount: BigDecimal) = new BrokerageAccount(total + amount)
  }

  class SavingsAccount(total: BigDecimal) extends Account[SavingsAccount] {
    def addFunds(amount: BigDecimal) = new SavingsAccount(total + amount)
  }


  object Account {
    val feePercentage = BigDecimal("0.02")
    val feeThreshold = BigDecimal("10000.00")

    def deposit[T <: Account[T]](amount: BigDecimal, account: T): T = {
      if (amount < feeThreshold) account.addFunds(amount - (amount * feePercentage))
      else account.addFunds(amount)
    }

    def debitAll(amount: BigDecimal, accounts: List[T forSome { type T <: Account[T] }]): List[T forSome { type T <: Account[T] }] = {
      accounts map { _.addFunds(-amount) }
    }
    // contrast the above constraint on the list with:
    // List[T] forSome { type T <: Account[T] }

    def debitAll2[T <: Account[T]](amount: BigDecimal, accounts: List[T]): List[T] = {
      accounts map { _.addFunds(-amount) }
    }
  }

  class MalignantAccount extends Account[SavingsAccount] {
    def addFunds(amount: BigDecimal) = new SavingsAccount(-amount)
  }

  def main(argv: Array[String]): Unit = {
    // compiles, though requires type ascription of the list; this is where the inferencer breaks down.
    Account.debitAll(BigDecimal("10.00"), List[T forSome { type T <: Account[T] }](new SavingsAccount(BigDecimal("0")), new BrokerageAccount(BigDecimal("0"))))

    // doesn't compile
    // Account.debitAll2(BigDecimal("10.00"), new SavingsAccount(BigDecimal("0")) :: new BrokerageAccount(BigDecimal("0")) :: Nil)

    // doesn't compile
    // Account.debitAll2(BigDecimal("10.00"), List[T forSome { type T <: Account[T] }](new SavingsAccount(BigDecimal("0")), new BrokerageAccount(BigDecimal("0"))))
  }
}
