package info.hadooptutorial.service

//import scala.collection.mutable.List
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.Extraction
import org.json4s.native.Serialization
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
object JsonForm {
  def main(args: Array[String]): Unit = {
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
        var hotel_name: String,
        var details: List[Details]) {
      def this() = this(null, null)
    }
    case class Previous_stays(
        var hotelName: String,
        var details: List[Details]) {
      def this() = this(null, null)
    }
    case class Hotelname(
        var last_purchase_date: String,
        var length_of_last_stay: Double,
        var number_of_past_bookings: Double,
        var upcoming_stays: List[Upcoming_stays],
        var previous_stays: List[Previous_stays]) {
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
    implicit val formats = Serialization.formats(NoTypeHints)
    val raw_json = """{
			"_id": 98,
			"_rev": 11299114,
			"creation_date": "Thu Dec 01 08:12:11 EST 2016",
			"last_update": null,
			"hotelname": {
			"last_purchase_date": null,
			"length_of_last_stay": 0,
			"number_of_past_bookings": 0,
			"upcoming_stays": [
			{
			"hotel_name": "POR_B_HOTEL",
			"details": [
			{
			"length_of_stay": "2",
			"season": "Winter",
			"reservation_status": "UPCOMING",
			"booking_date": null,
			"upcoming_reservation_date": null,
			"arrival_date": "2017-11-09T00:00:00.520Z",
			"departure_date": "2018-11-09T00:00:00.520Z"
			}
			]
			}
			],
			"previous_stays": null
			}
			}"""
    val m = parse(raw_json).extract[Customer]
    println(m)

  //  val customer_arr = customer_df.collect()
    val customer_arr = null
/*
    for (c <- customer_arr) {

      val csId = c.getLong(0)

      val hConf = HBaseConfiguration.create()
      val myTable = new HTable(hConf, "customer")
      val g = new Get(Bytes.toBytes(csId));
      val result=myTable.get(g)
      //if(result.i

      cust._id = c.getLong(0)
      cust._rev = c.getLong(1)
      cust.creation_date = Calendar.getInstance().getTime().toString()

      cust.hotelname = hotel
      upcoming_stays_list += upcoming_stays
      hotel.upcoming_stays = upcoming_stays_list

      upcoming_stays.hotel_name = c.getString(4)
      upcoming_stays.details = details_list

      details.length_of_stay = c.getString(6)
      details.arrival_date = c.getString(7)
      //details.booking_date = 
      details.departure_date = c.getString(8)
      details.reservation_status = c.getString(3)
      details.season = "Winter"
      //   details.upcoming_reservation_date = 
      details_list += details
      implicit val formats = Serialization.formats(NoTypeHints)
      val ser = org.json4s.native.Serialization.write(cust)
      println(ser)

      var put = new Put(cust._id.toString().getBytes)

      put.add("cf".getBytes(), "cust_data".getBytes(), ser.getBytes());
      myTable.put(put);
    }
*/
  }
}