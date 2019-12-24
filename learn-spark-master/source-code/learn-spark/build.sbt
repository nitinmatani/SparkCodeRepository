name := "learn-spark"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark"  %%  "spark-core"    % "2.3.0"   % "provided",
  "org.apache.spark"  %%  "spark-sql"     % "2.3.0",
  "org.apache.spark"  %%  "spark-mllib"   % "2.3.0"
)

initialCommands in console := s"""
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
val sparkConf = new SparkConf().setAppName("Learn Spark").setMaster("local[*]").set("spark.cores.max", "2")
val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
"""