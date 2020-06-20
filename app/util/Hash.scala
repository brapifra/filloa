package util

import com.github.t3hnar.bcrypt._

object Hash {
  private val salt = generateSalt

  def apply(input: String): String = input.bcrypt(salt)
}