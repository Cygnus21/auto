package services

import com.google.inject.Inject
import models.{Auto, AutoList}
import scala.concurrent.Future

class AutoService @Inject() (items: AutoList) {

  def addItem(item: Auto): Future[String] = {
      items.add(item)
    }

  def deleteItem(id: Long): Future[Int] = {
    items.delete(id)
  }

  def getItem(id: Long): Future[Option[Auto]] = {
    items.get(id)
  }

  def getItemByVin(vin: String): Future[Int] = {
    items.getByVin(vin)
  }

  def listAllItems(): Future[Seq[Auto]] = {
    items.listAll
  }

  def listAllItemsWithSort(sort: String): Future[Seq[Auto]] = {
    items.listAllWithSort(sort)
  }

  def listAllWithFilter(filter: String): Future[Seq[Auto]] = {
    items.listAllWithFilter(filter)
  }

  def getStatCountRecords: Future[Int] = {
    items.getStatCountRecords
  }

  def getNumberForEachMake: Future[Seq[(String, Int)]] = {
    items.getNumbersForEachMake
  }
}
