package info.hadooptutorial.service

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.client.Get
import org.json4s.NoTypeHints
import scala.collection.mutable.ListBuffer
import java.util.Calendar
import org.json4s.native.Serialization
import org.apache.hadoop.hbase.client.Put
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.Extraction
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap

object CustomerServiceFinal {
  def main(args: Array[String]): Unit = {
    /* var hotel_name: String,
        var details: List[Details])*/

    case class Details(
        var length_of_stay: String,
        var season: String,
        var reservation_status: String,
        var booking_date: String,
        var upcoming_reservation_date: String,
        var arrival_date: String,
        var departure_date: String) {
      def this() = this(null, null, null, null, null, null, null)
    }

    case class Upcoming_stays(
         var hotel_details: scala.collection.immutable.Map[String, List[Details]]) {
      def this() = this(null)
    }

    case class Previous_stays(
        var hotel_details: scala.collection.immutable.Map[String, List[Details]]) {
      def this() = this(null)
    }

    case class Hotelname(
        var last_purchase_date: String,
        var length_of_last_stay: Double,
        var number_of_past_bookings: Double,
        var upcoming_stays: Upcoming_stays,
        var previous_stays: Previous_stays) {
      def this() = this(null, 0, 0, null, null)
    }

    case class Customer(
        var _id: Long,
        var _rev: Long,
        var creation_date: String,
        var last_update: String,
        var hotelname: Hotelname) {
      def this() = this(0, 0, null, null, null)
    }

    val conf = new SparkConf().setAppName("GSVJob")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)
    val customer_df = sqlContext.sql("SELECT * from test.customer order by seq")
    val customer_arr = customer_df.collect()
    implicit val formats = Serialization.formats(NoTypeHints)
    val hConf = org.apache.hadoop.hbase.HBaseConfiguration.create()
    val myTable = new org.apache.hadoop.hbase.client.HTable(hConf, "customer")  
    for (c <- customer_arr) {

      val csId = c.getLong(0)
      val g = new org.apache.hadoop.hbase.client.Get(csId.toString().getBytes)
      val result = myTable.get(g)
      println("Result from Hbase::"+result)
      if (!result.isEmpty()) {
        var cust = parse(new String(result.value()),true).extract[Customer]
        println("Got extracted Cust::"+cust)
        val _id = c.getLong(0)
        val _rev = c.getLong(1)
        val reservation_status = c.getString(3)
        val hotel_name = c.getString(4)
        val length_of_stay = c.getString(6)
        val arrival_date = c.getString(7)
        val departure_date = c.getString(8)
        val booking_date = c.getString(2)

        var details = new Details
        details.length_of_stay = c.getString(6)
        details.arrival_date = c.getString(7)
        details.departure_date = c.getString(8)
        details.reservation_status = c.getString(3)
        details.season = "Winter"
        details.booking_date=booking_date
        println("Got details object::"+details)
        reservation_status match {
          case "UPCOMING" =>
             println("Inside UPCOMING")
            var hotel_details_upcoming_map = cust.hotelname.upcoming_stays.hotel_details
            if (hotel_details_upcoming_map.keySet.contains(hotel_name)) {
              var details_list = hotel_details_upcoming_map.get(hotel_name).get.to[ListBuffer]
              details_list += details
              hotel_details_upcoming_map += (hotel_name -> details_list.toList)
            } else {
              var list = new scala.collection.mutable.ListBuffer[Details]
              list += details
              hotel_details_upcoming_map += (hotel_name -> list.toList)
            }
             cust.hotelname.upcoming_stays.hotel_details = hotel_details_upcoming_map
          case "CHECKED IN" =>
            println("Inside CHECKED IN")
            var hotel_details_upcoming_map = cust.hotelname.upcoming_stays.hotel_details
            var details_list = hotel_details_upcoming_map.get(hotel_name).get.to[ListBuffer]
            println("details_list::"+details_list)
            for (old_details <- details_list) {
              if (old_details.booking_date.equals(booking_date)) {
                 println(old_details.booking_date+"::old_booking_date::"+booking_date)
                val index = details_list.indexOf(old_details)
                details_list.remove(index)
                details_list += details
                hotel_details_upcoming_map += (hotel_name -> details_list.toList)
              }
            }
            cust.hotelname.upcoming_stays.hotel_details = hotel_details_upcoming_map
          case "CHECKED OUT" =>
             println("Inside CHECKED OUT")
            var hotel_details_upcoming_map = cust.hotelname.upcoming_stays.hotel_details
            var details_list = hotel_details_upcoming_map.get(hotel_name).get.to[ListBuffer]
             println("Converted details_list::"+details_list)
            for (old_details <- details_list) {
              if (old_details.booking_date.equals(booking_date)) {
                println(old_details.booking_date+"::old_details::"+booking_date)
                val index = details_list.indexOf(old_details)
                 details_list.remove(index)
                hotel_details_upcoming_map += (hotel_name -> details_list.toList)
              }
            }
             println("Removed in upcoming")
            var hotel_details_previous_map = cust.hotelname.previous_stays.hotel_details
            if (hotel_details_previous_map.keySet.contains(hotel_name)) {
              var previous_details_list = hotel_details_upcoming_map.get(hotel_name).get.to[ListBuffer]
              previous_details_list += details
              hotel_details_previous_map += (hotel_name -> previous_details_list.toList)
            } else {
              var list = new scala.collection.mutable.ListBuffer[Details]
              list += details
              hotel_details_previous_map += (hotel_name -> list.toList)
            }
  cust.hotelname.upcoming_stays.hotel_details = hotel_details_upcoming_map
  cust.hotelname.previous_stays.hotel_details = hotel_details_previous_map
        }
        val ser = org.json4s.native.Serialization.write(cust)
        var put = new Put(cust._id.toString().getBytes)
        println("Got STR for:"+cust._id.toString()+"::::"+ser)
        put.add("cf".getBytes(), "cust_data".getBytes(), ser.getBytes());
           myTable.put(put);
      } else {
        println("came to else")
        var cust = new Customer
        var hotel = new Hotelname
        var details_list = new scala.collection.mutable.ListBuffer[Details]
        var upcoming_stays = new Upcoming_stays
        var perivous_stays = new Previous_stays
        var hotel_detail_perivous_map = new scala.collection.mutable.HashMap[String, List[Details]]
        perivous_stays.hotel_details = hotel_detail_perivous_map.toMap
        hotel.previous_stays = perivous_stays
        var hotel_detail_map = new scala.collection.mutable.HashMap[String, List[Details]]
        var details = new Details
        cust._id = c.getLong(0)
        cust._rev = c.getLong(1)
        details.booking_date = c.getString(2)
        cust.creation_date = java.util.Calendar.getInstance().getTime().toString()
        details.length_of_stay = c.getString(6)
        details.arrival_date = c.getString(7)
        details.departure_date = c.getString(8)
        details.reservation_status = c.getString(3)
        details.season = "Winter"
        cust.hotelname = hotel
        details_list += details
        hotel_detail_map += (c.getString(4) -> details_list.toList)
        upcoming_stays.hotel_details = hotel_detail_map.toMap
        hotel.upcoming_stays = upcoming_stays

        details_list += details

        val ser = org.json4s.native.Serialization.write(cust)
        var put = new Put(cust._id.toString().getBytes)
        println("Adding Json::"+ser)
        put.add("cf".getBytes(), "cust_data".getBytes(), ser.getBytes());
           myTable.put(put);
      }

    }

  }
}