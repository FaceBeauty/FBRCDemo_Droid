# FBRCDemo_Droid
## 集成步骤

1. 在`rtcapp/src/main/java/cn/rongcloud/demo/DemoApplication.java`添加您的融云的`APP_KEY`、`APP_SECRET`、`YOUR_APPID`(美颜的密钥)
2. 编译，运行，日志搜索`init-status`可以查看相关日志,如：美颜是否初始化成功
3. 在`calllib/src/main/java/cn/rongcloud/demo/calllib/activity/CalllibActivity.java`搜索`todo --FB`查看美颜集成到融云的步骤
