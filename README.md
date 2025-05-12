# SDK for Android
The ECRHubClient Android SDK makes it quick and easy to connect AddPayCashier APP to your ECR Android APP. We offer many ways to connect to the AddPayCashier APP to quickly realize the cashiering capabilities of your ECR system.With the ECRHub Android SDK you can use CodePay Register's payments, refunds, queries and more!
## Getting Started
Get started with our [SDK reference](https://github.com/paycloud-open/ecr-hub-client-sdk-android/tree/main/wisecashier_ecr_sdk) or [example projects](https://github.com/paycloud-open/ecr-hub-client-sdk-android) or [integration guides](./posDevGuidelines)


## Features

* **Multiple Connection Mode**，This SDK provides multiple ways to connect to Wise Cashier, including Cloud, Serial and LAN. You can choose the applicable connection method according to the ECR system environment situation。

* **Payment**， This SDK provides a variety of payment methods, including code scanning bank cards, and also provides a lot of medium payment capabilities, such as consumption, revocation, refund, pre-authorization, pre-authorization completion and so on.
* **Client Manager**，In LAN mode, WiseCashier acts as a Server, and the SDK is capable of connecting to multiple Servers, and the SDK provides methods to manage the Servers.



## Installation
**Requirements**

* Android 8.0 (API level 24) and above

**Configuration**
* Add it in your root `build.gradle`  at the end of repositories:
```java
allprojects {
                repositories {
                        ...
                        maven { url 'https://jitpack.io' }
                }
        }
}
```
* Add `ecrhub-android` to your app `build.gradle` dependencies.

```java

dependencies {
                implementation 'com.github.paycloud-open:ecrhub-client-sdk-android:1.0.3'
        }
```

## API List

### Start/Stop Device Discovery Service

The terminal can only discover your POS application when the device discovery service is started

- After completing a pairing operation, the terminal and POS application will record each other's network information
- When pairing and connecting to the network are required, the device discovery service needs to be enabled

```java
import com.wiseasy.ecr.sdk.EcrWifiDiscoveryService

val discoveryService = EcrWifiDiscoveryService(context)

discoveryService.start(object : EcrPairListener {
    override fun onDevicePair(data: EcrMessageData, ip: String) {
    }

    override fun onDeviceUnpair(data: EcrMessageData?) {
    }

})

discoveryService.stop()
```

### Get a list of paired terminals

- When using POS applications to push orders, you can select a device from the paired device list to push the order;
- When POS applications need to display paired POS terminals, this function can be used to obtain a list of paired devices for display.

```java
val mPairedList = discoveryService.pairedDeviceList
```

### Remove paired terminals

When the POS terminal is no longer in use, it can be manually removed from the paired list of the POS application.

```java
discoveryService.unPair(mPairedList[0])
```

### 

### Create Client

`EcrClient` is the Client connection object, Connection and Communicate with a single service node.

```java
import com.wiseasy.ecr.sdk.EcrClient

val mClient = EcrClient.getInstance()

mClient.init(context, object : EcrConnectListener{
    override fun onConnect() {
    }

    override fun onDisconnect() {
    }

    override fun onError(errorCode: String, errorMsg: String) {
    }

}) 
```
### Connect
  A global listener is registered when the client object is created, which is used to listen to the status of the connection
  ```java
mClient.connectWifi(address)
  ```
### DisConnect
A global listener is registered when the client object is created, which is used to listen to the status of the connection
```java
mClient.disConnect()
```
### Purchase
 `PaymentRequestParams` is a class that manages payment parameters
```java
val params = PaymentRequestParams()
params.app_id = "you payment app id"
params.topic = Constants.PAYMENT_TOPIC
params.timestamp = System.currentTimeMillis().toString()

params.biz_data = PaymentRequestParams.BizData()
params.biz_data.trans_type = Constants.TRANS_TYPE_SALE
params.biz_data.order_amount = "1.00"
params.biz_data.merchant_order_no = "1234567890"
params.biz_data.pay_scenario = "SWIPE_CARD"
params.biz_data.isConfirm_on_terminal = false

val json = JSON.toJSONString(params)

mClient.doTransaction(json, object : EcrResponseCallBack {
    override fun onError(errorCode: String, errorMsg: String) {
    }

    override fun onSuccess(data: String) {
    }
})
```
### Refund
Refund transactions can be done for successfully paid orders
```java
val params = PaymentRequestParams()
params.app_id = "you payment app id"
params.topic = Constants.PAYMENT_TOPIC
params.timestamp = System.currentTimeMillis().toString()

params.biz_data = PaymentRequestParams.BizData()
params.biz_data.trans_type = Constants.TRANS_TYPE_REFUND
params.biz_data.order_amount = "1.00"
params.biz_data.merchant_order_no = "1234567890"
params.biz_data.orig_merchant_order_no = "1234567891"
params.biz_data.pay_scenario = "SWIPE_CARD"
params.biz_data.isConfirm_on_terminal = false

val json = JSON.toJSONString(params)

mClient.doTransaction(json, object : EcrResponseCallBack {
    override fun onError(errorCode: String, errorMsg: String) {
    }

    override fun onSuccess(data: String) {
    }
})
```

### Query Order
For transactions that do not return a payment result and you want to get the result of the transaction, you can call this query method
```java
val params = PaymentRequestParams()
params.app_id = "you payment app id"
params.topic = Constants.QUERY_TOPIC
params.biz_data = PaymentRequestParams.BizData()
params.biz_data.merchant_order_no = "1234567890"

val json = JSON.toJSONString(params)

mClient.queryTransaction(json, object : EcrResponseCallBack {
    override fun onError(errorCode: String, errorMsg: String) {
    }

    override fun onSuccess(data: String) {
    }

  })
```
### Close Order
After initiating a transaction, this method can be called to cancel the payment until the transaction is successful
```java
val params = PaymentRequestParams()
params.app_id = "you payment app id"
params.topic = Constants.CLOSE_TOPIC
params.biz_data = PaymentRequestParams.BizData()
params.biz_data.merchant_order_no = "1234567890"

val json = JSON.toJSONString(params)

mClient.cancelTransaction(json)
```


## Version history

* `1.0.3` 
    Publish date：2025-05-12