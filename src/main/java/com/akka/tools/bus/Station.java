package com.akka.tools.bus;/* 
    create qiangzhiwei time 2022/2/22
 */

public interface Station<T> {
   void debarkation(Event<T> event);
}
