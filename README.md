Webit Schedule
==============
<a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=7be9d8a59a8533b7c2837bdc22295b4b47c65384eda323971cf5f3b9943ad9db"><img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="QQ群: 302505483" title="QQ群: 302505483"></a>

Task schedule, used cron expression, all writen in Java.

## First glance

~~~~~java
//Create a Scheduler instance.
Scheduler scheduler = new Scheduler();

//Add my task
//
//   my task ----------- +
//   cron -------- +     |  
//                 |     |  
scheduler.addTask("*", myTask);

//Start scheduler
scheduler.start();
...
//Stop scheduler
scheduler.stop();
~~~~~

## About Cron

~~~~~
 + ---------------- minute (0 - 59)
 |  + ------------- hour (0 - 23)
 |  |  + ---------- day of month (1 - 31)
 |  |  |  + ------- month (1 - 12)
 |  |  |  |  + ---- day of week (1 - 7), Monday - Sunday
 |  |  |  |  |  + - year
 |  |  |  |  |  |  
 *  *  *  *  *  *  
~~~~~
### Star


### Examples

~~~~~

~~~~






