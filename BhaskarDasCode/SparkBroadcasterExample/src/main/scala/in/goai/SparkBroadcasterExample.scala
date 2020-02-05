package in.goai
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object  SparkBroadcasterExample  {
  def main(args: Array[String]) {

     val conf = new SparkConf().setAppName("SparkAccumulatorBroadcastJoin")
     val sc = new SparkContext(conf)
     val purchaseInDelhi = sc.accumulator(0)

     val payRDD = sc.textFile("userInfo.csv")
     val payPair = payRDD.map(x => (x.split(",")(0),x))
     val usrRDD = sc.textFile("payments.csv")
     val usrPair = usrRDD.map(
       x => {
         if(x.split(",")(1)=="Delhi") {
           purchaseInDelhi += 1
         }
       (x.split(",")(0),x.split(",")(2))
       }
     )
     val usrMap = usrPair.collectAsMap()
     val broadcastUserMap = sc.broadcast(usrMap)
     val r = payPair.map(v => (v._1,(broadcastUserMap.value(v._1),v._2)))
     r.foreach(println)
     r.saveAsTextFile("results")
     println("No of Delhi purchase " + purchaseInDelhi.value)
   }
}
