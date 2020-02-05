package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.studyDao
import javax.inject.Inject
import play.api.Environment
import protocols.studyProtocol._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class StudyManager @Inject()(val environment: Environment,
                             studyDao: studyDao
                              )
                            (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)



  def receive = {
    case Create(data) =>
      log.warning(s"menagerga keldi: $data")
      create(data).pipeTo(sender())

        case Update(data) =>
          update(data).pipeTo(sender())

        case Delete(id) =>
          delete(id).pipeTo(sender())

        case GetList =>
          getList.pipeTo(sender())

    case _ => log.info(s"received unknown message")
  }


  private def create(data: study): Future[Int] = {
    log.warning(s"daoga yuborildi: $data")
    studyDao.create(data)
  }
  private def getList: Future[Seq[study]] = {
    studyDao.getAll
  }
  private def delete(id: Int): Future[Int] = {
    studyDao.delete(id)
  }

  private def update(data: study): Future[Int] = {
    studyDao.update(data: study)
  }
}
