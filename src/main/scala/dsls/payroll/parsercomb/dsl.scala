package dsls.payroll.parsercomb

import scala.util.parsing.combinator._
import dsls.payroll.common._

object Payroll {
  import dsl.PayrollParser

  def main(args: Array[String]) = {
    val input =
      """biweekly {
        |federal tax        20.0  percent,
        |state tax          3.0   percent,
        |insurance premiums 250.0 dollars,
        |retirement savings 15.0  percent
      }""".stripMargin
    val parser = new PayrollParser
    val biweeklyDeductions = parser.parseAll(parser.biweekly, input).get

    println(biweeklyDeductions)
    val annualGross = 100000.0
    val gross = biweeklyDeductions.gross(annualGross)
    val net   = biweeklyDeductions.net(annualGross)
    print(f"Biweekly pay (annual: $$$annualGross%.2f): ")
    println(f"Gross: $$$gross%.2f, Net: $$$net%.2f")
  }
}

object dsl {
  class PayrollParser extends JavaTokenParsers {

    /** @return Parser[(Deductions)] */
    def biweekly = "biweekly" ~> "{" ~> deductions <~ "}" ^^ { ds => Deductions("Biweekly", 26.0, ds) }

    /** @return Parser[ Vector[Deduction] ] */
    def deductions = repsep(deduction, ",") ^^ { ds => ds.foldLeft(Vector.empty[Deduction]) (_ :+ _) }

    /** @return Parser[Deduction] */
    def deduction = federal_tax | state_tax | insurance | retirement

    /** @return Parser[Deduction] */
    def federal_tax = parseDeduction("federal", "tax")
    def state_tax   = parseDeduction("state", "tax")
    def insurance   = parseDeduction("insurance", "premiums")
    def retirement  = parseDeduction("retirement", "savings")

    private def parseDeduction(word1: String, word2: String) =
      word1 ~> word2 ~> amount ^^ { amount => Deduction(s"$word1 $word2", amount) }

    /** @return Parser[Amount] */
    def amount = dollars | percentage

    /** @return Parser[Dollars] */
    def dollars = doubleNumber <~ "dollars" ^^ { d => Dollars(d) }

    /** @return Parser[Percentage] */
    def percentage = doubleNumber <~ "percent" ^^ { d => Percentage(d) }

    def doubleNumber = floatingPointNumber ^^ (_.toDouble)
  }
}
