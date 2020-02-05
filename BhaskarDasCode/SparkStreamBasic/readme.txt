follow instructions given in the PPT to work, This folder is fully working one
you could also use this to work.
Set your spark path to make this work

To Compile

sbt package

To Run

Step 1:

Open one terminal and in that terminal type the following
nc -lk 9999

Open another terminal and in that copy and paste this

$SPARK_HOME/bin/spark-submit --master "local[*]" --class in.goai.SparkStreamBasic target/scala-2.10/sparkstreambasic_2.10-1.0.jar


On terminal 1 where you have put nc -lk 9999 start typing some words and press enter and keep entering
multiple lines of input like this

$ nc -lk 9999
Hi this is mani
What is this program all about
I am sure its doing some processing



on the second terminal you will see results like this

-------------------------------------------
Time: 1463411623000 ms
-------------------------------------------
(What,1)
(program,1)
(about,1)
(all,1)
(this,1)
(is,1)
