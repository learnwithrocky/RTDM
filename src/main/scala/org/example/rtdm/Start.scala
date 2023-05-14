package org.example.rtdm

object Start  {
  def main(args: Array[String]): Unit = {

   val topic_name = args(0)
   val index_name = args(1)
   val checkpoint_location = args(2)
    val isConsoleMode = args(3)
    if(isConsoleMode.toBoolean){
      ConsoleStreamer.startStream(topic_name,index_name,checkpoint_location)
    }else{
      ElasticStreamer.startStream(topic_name,index_name,checkpoint_location)
    }


  }




}
