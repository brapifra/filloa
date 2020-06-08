package user

import javax.inject._
import controllers.CreateOrLoginUserDTO
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserFacadeImpl])
trait UserFacade {
  def createUser(user: CreateOrLoginUserDTO): Future[Unit];
}

@Singleton
case class UserFacadeImpl @Inject() (userRepository: UserRepository)
    extends UserFacade {
  def createUser(userInfo: CreateOrLoginUserDTO) = {
    val user = User(userInfo.email, userInfo.password)
    userRepository.add(user)
  }
}
