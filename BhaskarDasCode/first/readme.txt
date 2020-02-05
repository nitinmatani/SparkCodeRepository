This is the firsr spark scala application. Follow the instructions provided with the 
PPT.
Scala should have been installed by now
Spark should have been installed by now
Install sbt, its scala build tool, based on your development environment find the right way to install sbt
It helps to have an env variable called SPARK_HOME that points to stark installation
$SPARK_HOME/bin/spark-submit should be available
$SPARK_HOME/sbin/start-all  should be available
$SPARK_HOME/sbin/stop-all  should be available
$SPARK_HOME/bin/spark-shell  should be available
This example is done without any help of IDE using only command line tools.

To Execute in local mode (inputTextFile.txt is the actual input, this is a local file, use any text file)
$SPARK_HOME/bin/spark-submit --master "local[*]" --class in.goai.spark.SparkMeApp target/scala-2.11/first-spark_2.11-1.0.jar inputTextFile.txt

Your output would be 

There are 35 lines in inputTextFile.txt

