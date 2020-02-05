package protocols

import play.api.libs.json.{Json, OFormat}

object studyProtocol {

  case object GetList

  case class Delete(id: Int)

  case class Update(update: study)

  case class Create(data: study)

  case class study(id: Option[Int] = None,
                     computer: String,
                    )
  implicit val studentFormat: OFormat[study] = Json.format[study]
}

