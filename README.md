# FBRCDemo_Droid
## 集成步骤

1. **请至https://github.com/FaceBeauty/FaceBeautySDK_Android下载最新的fbui以及资源文件**
2. 在`rtcapp/src/main/java/cn/rongcloud/demo/DemoApplication.java`添加您的融云的`APP_KEY`、`APP_SECRET`、`YOUR_APPID`(美颜的密钥)
3. 编译，运行，日志搜索`init-status`可以查看相关日志,如：美颜是否初始化成功
4. 在`calllib/src/main/java/cn/rongcloud/demo/calllib/activity/CalllibActivity.java`搜索`todo --FB`查看美颜集成到融云的步骤

### `CallPlus`

1. 在`rtcapp/src/main/java/cn/rongcloud/demo/DemoApplication.java`添加您的融云的`APP_KEY`、`APP_SECRET`、`YOUR_APPID`(美颜的密钥)
2. 编译，运行，日志搜索`init-status`可以查看相关日志,如：美颜是否初始化成功
3. 在`callplus/src/main/java/cn/rongcloud/demo/callplus/CallPlusActivity.java`搜索`todo --FB`查看美颜集成到融云的步骤
4. 添加Init类
