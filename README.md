车辆追踪与现场仪器蓝牙通信,DBF文件解析
====================================

程序的功能有:
- 利用GPS追踪车辆
- 有GPS日志功能
- 解析DBF文件获取任务信息
- 用蓝牙与仪器通过spp协议建立通信
- 解析TXT配置文件

Author
--------
 **zhiwenluo** 
- If any question , email me : **zhiwenluo@hustunique.com**

Constructor
--------
要导入该工程，需要如下文件：

- android-async-http-1.4.4.jar 用于http协议 https://github.com/zhiwenluo/android-async-http
- javadbf.jar 用于解析DBF文件
- library_ABS 工程 https://github.com/zhiwenluo/library_ABS
- library_Sliding 工程https://github.com/zhiwenluo/library_Sliding

**jar包放到libs文件夹下，右键添加到工程即可**


**对于两个工程：**
- 右健library_ABS - Properties - Android - Librarie下勾选is Library 
- 右健library_Sliding同样勾选，并且点击Add添加library_ABS
- 最后右健你的工程 - Properties - Android 添加library_Sliding即可
- 如果报错，是suppor-V4包版本的问题，复制ABS的support包覆盖你的工程的support包

Other
--------





