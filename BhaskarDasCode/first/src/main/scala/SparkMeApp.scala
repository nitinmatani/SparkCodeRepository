package in.goai.spark

import org.apache.spark.{SparkContext, SparkConf}

object SparkMeApp {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("First Spark")
    val sc = new SparkContext(conf)

    val fileName = args(0)
    val lines = sc.textFile(fileName)

    val c = lines.count
    println(s"There are $c lines in $fileName")
  }
}
