package models
import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

case class Auto(id: Long, vin: String, number: String, make: String, color: String, year: Int)

case class AutoFormData(vin: String, number: String, make: String, color: String, year: Int)

object AutoForm {
  val form = Form(
    mapping(
      "vin" -> nonEmptyText,
      "number" -> nonEmptyText,
      "make" -> nonEmptyText,
      "color" -> nonEmptyText,
      "year" -> number
    ) (AutoFormData.apply)(AutoFormData.unapply)
  )
}

class AutoTableDef(tag: Tag) extends Table[Auto](tag, "auto") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def vin = column[String]("vin")
  def number = column[String]("number")
  def make = column[String]("make")
  def color = column[String]("color")
  def year = column[Int]("year")

  override def * = (id, vin, number, make, color, year) <> (Auto.tupled, Auto.unapply)
}

class AutoList @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) (
                         implicit executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {
  var autoList = TableQuery[AutoTableDef]

  def add(autoItem: Auto): Future[String] = {
    dbConfig.db
      .run(autoList += autoItem)
      .map(res => "Auto was successfully added")
      .recover {
        case ex: Exception => {
          printf(ex.getMessage())
          ex.getMessage
        }
      }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(autoList.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Auto]] = {
    dbConfig.db.run(autoList.filter(_.id === id).result.headOption)
  }

  def getByVin(vin: String): Future[Int] = {
    dbConfig.db.run(autoList.filter(_.vin === vin).length.result)
  }

  def listAll(): Future[Seq[Auto]] = {
    dbConfig.db.run(autoList.result)
  }

  def listAllWithSort(sort: String): Future[Seq[Auto]] = {
    sort match {
      case "vin" => dbConfig.db.run(autoList.sortBy(p => p.vin).result)
      case "color" => dbConfig.db.run(autoList.sortBy(p => p.color).result)
      case "make" => dbConfig.db.run(autoList.sortBy(p => p.make).result)
      case "number" => dbConfig.db.run(autoList.sortBy(p => p.number).result)
      case "year" => dbConfig.db.run(autoList.sortBy(p => p.year).result)
    }
  }

  def listAllWithFilter(filter: String): Future[Seq[Auto]] = {
    val result = filter.split(":")
    val key = result(0)
    val value = result(1)
    key match {
      case "vin" => dbConfig.db.run(autoList.filter(_.vin === value).result)
      case "color" => dbConfig.db.run(autoList.filter(_.color === value).result)
      case "make" => dbConfig.db.run(autoList.filter(_.make === value).result)
      case "number" => dbConfig.db.run(autoList.filter(_.number === value).result)
      case "year" => dbConfig.db.run(autoList.filter(_.year === value.toInt).result)
    }
  }

  def getStatCountRecords: Future[Int] = {
    dbConfig.db.run(autoList.length.result)
  }

  def getNumbersForEachMake: Future[Seq[(String,Int)]] = {
    dbConfig.db.run(autoList.sortBy(p => p.make)
      .groupBy(_.make)
      .map {case (make, group) => (make, group.map(_.make).length)}
      .result)
  }

}