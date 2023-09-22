# wise-utils-android

用于Android Pos机应用开发的基础服务工具

## 版本管理

|  版本号  | 更新内容  |
|  ----  | ----  
| v1.0.1  | 添加打印模板库 |

## API
###打印模板库
####WisePrinter
 打印模板库打印操作类

#####1. init
初始化方法，参数context上下文。
#####2.startPrinter
打印方法，参数：temple:打印模板，printerSettingData:打印设置，listener：打印监听

####IWisePrinterListener
打印监听回调
#####1.onPrinterPrepareComplete
打印准备完成回调
#####2.onPrinterCoupletComplete
打印完成一联回调
#####2.onPrinterFail
打印失败回调
#####2.onPrinterSuccess
打印成功回调