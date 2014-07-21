package utils

import scala.util.Random

object TokenGenerator {
  private final val Length = 64
  private val random = new Random(System.currentTimeMillis)
  def generate: String = random.alphanumeric.take(Length).mkString
}
