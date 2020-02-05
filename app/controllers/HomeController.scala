package controllers

import akka.actor.ActorRef
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.webjars.play.WebJarsUtil
import play.api.mvc._
import views.html._
import akka.pattern.ask
import play.api.libs.json.{JsValue, Json}
import protocols.studyProtocol.{Create, Delete, GetList, Update, study}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               implicit val webJarsUtil: WebJarsUtil,
                               @Named("study-manager") val studyManager: ActorRef,
                               indexTemplate: index
                              )
                              (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def index = Action {
    Ok(indexTemplate())
  }

  def create =Action.async(parse.json) { implicit request =>
    val computer = (request.body \ "computer").as[String]
    logger.warn(s"controllerga keldi")
    (studyManager ? Create(study(None, computer))).mapTo[Int].map { id =>
        Ok(Json.toJson(id))
    }
  }
  def getComputer: Action[AnyContent] = Action.async {
    (studyManager ? GetList).mapTo[Seq[study]].map{ computer =>
      Ok(Json.toJson(computer))
    }
  }
  def delete = Action.async(parse.json) { implicit request =>
    val id: Int = (request.body \ "id").as[Int]
    (studyManager ? Delete(id)).mapTo[Int].map{ i =>
      if (i != 0){
        Ok(Json.toJson(id + " raqamli ism o`chirildi"))
      }
      else {
        Ok("Bunday raqamli ism topilmadi")
      }
    }
  }
  def update = Action.async(parse.json) { implicit request =>
    val id: Int = (request.body \ "id").as[Int]
    val computer = (request.body \ "computer").as[String]
    (studyManager ? Update(study(Some(id), computer))).mapTo[Int].map{ i =>
      if (i != 0){
        Ok(Json.toJson(id + " raqamli ism yangilandi"))
      }
      else {
        Ok("Bunday raqamli ism topilmadi")
      }
    }
  }
}