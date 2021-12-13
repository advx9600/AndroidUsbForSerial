package com.nwz.usbcomumication

import android.Manifest
import android.app.PendingIntent
import android.content.*
import android.hardware.usb.*
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat.requestPermissions

import android.content.pm.PackageManager
import android.os.*
import android.text.format.Formatter
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.DataInputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket
import java.net.SocketTimeoutException


private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
const val TAG="aaaaaa usb"
private  const val PREF_IP = "ip"
private  const val PREF_PORT = "port"
private  const val MESSAGE_TIPINFO = 1
class TcpMainActivity : AppCompatActivity() {
    private var tcpOutput: OutputStream?=null
    private lateinit var pref: SharedPreferences
    private var sk: Socket?=null
    private  var mDev: UsbDevice? = null
    private var inEndpoint: UsbEndpoint?=null
    private  var outEndpoint: UsbEndpoint?=null
    private  var usbCon: UsbDeviceConnection?=null
    private lateinit var mTv: TextView;
    private  var exitThd = false

    private  var tipMsg = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcp_main)

        mTv = findViewById(R.id.info_text);
        requestPermission()
        pref = getSharedPreferences("pref", MODE_PRIVATE)
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        exitThd = true
        if (sk!= null && sk!!.isConnected)
            sk!!.close()
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg?.what) {
                MESSAGE_TIPINFO->{
                    toast(tipMsg)
                }
            }
        }
    }
    fun loadData(){
        findViewById<EditText>(R.id.ip_text).setText(pref.getString(PREF_IP,""))
        findViewById<EditText>(R.id.port_text).setText(pref.getString(PREF_PORT,""))
    }

    fun startUsbTranferClick(view:View){
        if (usbCon != null){
            var bytes = "123456789".encodeToByteArray()
            val inbytes = ByteArray(inEndpoint!!.maxPacketSize)
            var ret = usbCon?.bulkTransfer( outEndpoint,bytes,bytes.size,100)
//            if (ret != null) {
//                if (ret >0) {
//                    ret = usbCon?.bulkTransfer(inEndpoint, inbytes, inbytes.size, 5000)
//                    if (ret!! > 0){
//                        mTv.text = inbytes.toString();
//                    }
//                }
//            }
        }else{
            mTv.text = ("get listdevice first");
        }
    }

    private var recvThdcreated = 0
    fun startRecvThd(){
        if (recvThdcreated ==0) {
            recvThdcreated = 1
            Thread {
                while (!exitThd) {
                    val inbytes = ByteArray(inEndpoint!!.maxPacketSize)
                    var ret = usbCon?.bulkTransfer(inEndpoint, inbytes, inbytes.size, 5000)
                    if (ret!! > 0){
//                        Log.d(TAG,"recv "+ret+" "+getHex(inbytes,ret))
                        if (connected){
                            tcpOutput?.write(inbytes,0,ret)
                        }
                    }
                }
            }.start()
        }
    }

    private fun getHex(bytes: ByteArray, num: Int): String {
        var total =""
        for (i in 0..num-1){
            total += "%02x ".format(bytes[i])
        }
        return total
    }


    fun listDeviceClick(view: View) {
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = manager.deviceList
        val permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        val tv = findViewById<TextView>(R.id.listdev_view);
        if (deviceList.values.count() <1)
            tv.text = "no devices"
        else
            tv.text = ""
        deviceList.values.forEach {
            tv.text = tv.text.toString()+ "pid:0x%x,vid:0x%x\n".format(it.productId , it.vendorId)+it.toString()
            if (!manager.hasPermission(it)) {
                manager.requestPermission(it, permissionIntent)
            }else{
                var intnum = 0
                for (i in 0..it.getInterface(0).endpointCount) {
                    if (it.getInterface(intnum).endpointCount > 1) {
                        intnum = 0
                    }else{
                        intnum = 1
                    }
                    for (i in 0..it.getInterface(intnum).endpointCount-1) {
                        val tmpPoint = it.getInterface(intnum).getEndpoint(i)
                        if (tmpPoint.attributes == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                            if (tmpPoint.direction == UsbConstants.USB_DIR_IN) {
                                inEndpoint = tmpPoint
                            } else if (tmpPoint.direction == UsbConstants.USB_DIR_OUT) {
                                outEndpoint = tmpPoint
                            }
                        }
                    }
                }
                if (inEndpoint != null && outEndpoint != null){
                    usbCon = manager.openDevice(it)
                    val seinfo = SerialCom().config(it,usbCon)
                    if (!seinfo.isEmpty()){
                        toast(seinfo)
                    }
                    usbCon?.claimInterface(it.getInterface(intnum), true)
                    startRecvThd()
                }
            }
        }
    }

    private var connected = false
    fun connectClick(view: View) {
        val ip = findViewById<EditText>(R.id.ip_text).text.toString()
        val port = findViewById<EditText>(R.id.port_text).text.toString()

        pref.edit().putString(PREF_IP,ip).commit()
        pref.edit().putString(PREF_PORT,port).commit()

        val tv = findViewById<TextView>(R.id.conn_text)
        tv.text = ""
        if (!connected){
            Thread{
                try {
                    sk = Socket(ip, port.toInt())
                    sk?.soTimeout=10*1000

                    val input = DataInputStream(sk!!.getInputStream())
                    val output = sk!!.getOutputStream()
                    tcpOutput = output
                    var bts = ByteArray(512)

                    sendToast("connect OK")
                    connected = true
                    while (!exitThd){
                        var len = 0
                        try {
                            len = input.read(bts)
                        }catch (e1:SocketTimeoutException){
                            len = 0
                        }
                        if (len > -1){
                            if (len> 0) {
                                if (usbCon != null) {
                                    if (usbCon?.bulkTransfer(outEndpoint, bts, len, 3000)!! < 0) {
                                        sendToast("usb connect is closed")
                                        sk?.close()
                                        break
                                    }
                                }else{
                                    sendToast("recv "+len+",but usb not connected")
                                }
                            }
                        }else{
                            sendToast("tcp connect closed")
                            break
                        }
                    }
                    sk?.close()
                }catch (e:Exception){
                    Log.e(TAG,e.toString())
                    sendToast(e.toString())
                }finally {
                    connected = false
                }
            }.start()
        }else{
            toast("already connected")
        }
    }

    fun requestPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return
        }

        if (ContextCompat.checkSelfPermission(this@TcpMainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@TcpMainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(this@TcpMainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                requestPermissions(this@TcpMainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    fun wifiBtnClick(view: View) {
        var tv = findViewById<TextView >(R.id.wifi_text)
        val wifi :WifiManager=
            getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager;
        if(! wifi.isWifiEnabled()){
            tv.text = "wifi not enabled"
        }else{
            tv.text = "ssid:%s,ip:%s".format(wifi.connectionInfo.ssid,Formatter.formatIpAddress( wifi.connectionInfo.ipAddress))
        }
    }

    private  fun  sendToast(msg:String){
        tipMsg = msg
        mHandler.sendEmptyMessage(MESSAGE_TIPINFO)
    }
    private fun toast(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

}