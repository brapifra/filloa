package user

import javax.inject._
import controllers.CreateOrLoginUserDTO
import com.google.inject.ImplementedBy
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import util.Hash

@ImplementedBy(classOf[UserFacadeImpl])
trait UserFacade {
  def createUser(user: CreateOrLoginUserDTO): Future[User];
}

@Singleton
case class UserFacadeImpl @Inject() (userRepository: UserRepository)
    extends UserFacade {
  def createUser(userInfo: CreateOrLoginUserDTO) = Future {
    val user = User(userInfo.email, Hash(userInfo.password))
    userRepository.add(user)
    user
  }
}
