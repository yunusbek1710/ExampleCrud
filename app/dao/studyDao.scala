package dao

import akka.actor.ActorSystem
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.studyProtocol.study
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import scala.concurrent.{ExecutionContext, Future}


trait studyComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class studyTable(tag: Tag) extends Table[study](tag, "Study") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def computer = column[String]("computer")

    def * = (id.?, computer) <> (study.tupled, study.unapply _)
  }

}

@ImplementedBy(classOf[studyDaoImpl])
trait studyDao {
  def create(data: study): Future[Int]

  def delete(id: Int): Future[Int]

  def update(data: study): Future[Int]

  def getAll: Future[Seq[study]]
}

@Singleton
class studyDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                               val actorSystem: ActorSystem)
                              (implicit val ec: ExecutionContext)
  extends studyDao
    with studyComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with Date2SqlDate
    with LazyLogging {

  import utils.PostgresDriver.api._

  val studiesTable = TableQuery[studyTable]

  override def create(data: study): Future[Int] = {
    db.run {
      logger.warn(s"daoga keldi: $data")
      (studiesTable returning studiesTable.map(_.id)) += data
    }
  }

  override def getAll: Future[Seq[study]] = {
    db.run {
      studiesTable.result
    }
  }
  override def delete(id: Int): Future[Int] = {
    db.run {
      studiesTable.filter(_.id === id).delete
    }
  }
  override def update(data: study): Future[Int] = {
    db.run {
      studiesTable.filter(_.id === data.id).update(data)
    }
  }
}

