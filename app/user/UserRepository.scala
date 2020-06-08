package user

import javax.inject._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import com.google.inject.ImplementedBy

@ImplementedBy(classOf[InMemoryUserRepository])
trait UserRepository {
  def add(user: User): Future[Unit]
  def findByEmail(email: String): Future[Option[User]]
}

@Singleton
case class InMemoryUserRepository() extends UserRepository {
  val users: mutable.Map[String, User] = mutable.Map()

  def add(user: User): Future[Unit] = Future {
    users.put(user.email, user)
  }

  def findByEmail(email: String): Future[Option[User]] = Future {
    users.get(email)
  }
}
