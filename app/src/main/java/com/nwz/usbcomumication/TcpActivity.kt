package com.nwz.usbcomumication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView

//private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
//private  const val TAG="aaaaaa usb"
abstract class TcpActivity : AppCompatActivity() {

//    private  var mDev: UsbDevice? = null
//    private var inEndpoint:UsbEndpoint?=null
//    private  var outEndpoint: UsbEndpoint?=null
//    private  var usbCon: UsbDeviceConnection?=null
//    private lateinit var mTv:TextView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcp)
//        mTv = findViewById(R.id.info_text);
//        val filter = IntentFilter(ACTION_USB_PERMISSION)
//        registerReceiver(usbReceiver, filter)

    }

//    fun listDeviceClick(view: View) {
//        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
//        val deviceList: HashMap<String, UsbDevice> = manager.deviceList
//        val permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
//        val tv = findViewById<TextView>(R.id.listdev_view);
//        tv.text = ""
//        deviceList.values.forEach {
//            tv.text = tv.text.toString()+ "pid:0x%x,vid:0x%x\n".format(it.productId , it.vendorId)+it.toString()
//            manager.requestPermission(it, permissionIntent)
//        }
//    }
//
//    fun startUsbTranferClick(view:View){
//        if (usbCon != null){
//            var bytes = "123456789".encodeToByteArray()
//            val inbytes = ByteArray(inEndpoint!!.maxPacketSize)
//            var ret = usbCon?.bulkTransfer( outEndpoint,bytes,bytes.size,100)
//            if (ret != null) {
//                if (ret >0) {
//                    ret = usbCon?.bulkTransfer(inEndpoint, inbytes, inbytes.size, 5000)
//                    if (ret!! > 0){
//                        mTv.text = inbytes.toString();
//                    }
//                }
//            }
//        }else{
//            mTv.text = ("click listdevice first");
//        }
//    }

//    private val usbReceiver = object : BroadcastReceiver() {
//        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
//        override fun onReceive(context: Context, intent: Intent) {
//            if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {
//                val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
//                device?.apply {
//                    // call your method that cleans up and closes communication with the device
//                }
//            }else if (ACTION_USB_PERMISSION == intent.action) {
//                synchronized(this) {
//                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
//
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                        if (device != null) {
//                            mDev = device
//                            for (i in 0..device.getInterface(0).endpointCount) {
//                                var tmpPoint = device.getInterface(0).getEndpoint(0)
//                                if (tmpPoint.direction == UsbConstants.USB_DIR_IN) {
//                                    inEndpoint = tmpPoint
//                                } else if (tmpPoint.direction == UsbConstants.USB_DIR_OUT) {
//                                    outEndpoint = tmpPoint
//                                }
//                            }
//                            usbCon = manager.openDevice(device)
//                            usbCon?.claimInterface(device?.getInterface(0), true)
//                        }else{
//                            Log.e(TAG, "no device found")
//                            mTv.text = "no device found";
//                        }
//                    } else {
//                        Log.e(TAG, "permission denied for device $device")
//                    }
//                }
//            }
//        }
//    }

}