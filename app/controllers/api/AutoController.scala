package controllers.api

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{Auto, AutoForm}
import services.AutoService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class AutoController @Inject()(
  cc: ControllerComponents,
  autoService: AutoService) extends AbstractController(cc) {
  implicit val autoFormat = Json.format[Auto]

  def getAll() = Action.async { implicit request: Request[AnyContent] =>
    val header: Map[String, Seq[String]] = request.queryString
    if (header.contains("sort")) {
        autoService.listAllItemsWithSort(header.get("sort").toList.head.head) map { items =>
          Ok(Json.toJson(items))
        }}
    else if (header.contains("filter")) {
        autoService.listAllWithFilter(header.get("filter").toList.head.head) map { items =>
          Ok(Json.toJson(items))
        }}
    else {
      autoService.listAllItems map { items =>
        Ok(Json.toJson(items))
      }}
  }

  def add() = Action.async { implicit request: Request[AnyContent] =>
    AutoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {
        val time: Duration = 5.seconds
        val vin = data.vin
        val result = Await.result(autoService.getItemByVin(vin), time)
        result match {
          case 0 => {
            val newAutoItem = Auto(0, data.vin, data.number, data.make, data.color, data.year)
            autoService.addItem(newAutoItem).map { res =>
              Ok(Json.toJson(res))}
        }
          case 1 => Future.successful(BadRequest(s"Auto with vim $vin already exists in database"))
        }
      })
  }

  def delete(id: Long) = Action.async { implicit request: Request[AnyContent] =>
        val time: Duration = 5.seconds
        val result = Await.result(autoService.getItem(id), time)
        result.map { res =>
          autoService.deleteItem(id) .map { res =>
            Ok(Json.toJson(res))
          }
        }.getOrElse(Future.successful(NotFound("Auto was not found")))
  }

  def getStatCountRecords() = Action.async { implicit request: Request[AnyContent] =>
    autoService.getStatCountRecords map { items =>
      Ok(Json.toJson(items))

    }
  }

  def getNumbersForEachMake() = Action.async { implicit request: Request[AnyContent] =>
    autoService.getNumberForEachMake map { items =>
      Ok(Json.toJson(items))
    }
  }
}
