package com.nwz.usbcomumication

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.util.Log

class SerialCom {
    fun config(usbDev: UsbDevice, usbCon: UsbDeviceConnection?):String {
        var ret = ""
        if (usbCon!= null && usbDev != null){
            var vid = usbDev.vendorId
            var pid = usbDev.productId
            var bts = ByteArray(10)
            if (vid == 0x1a86 && pid == 0x7523){ // ch340
                usbCon.controlTransfer(0x40,0x9a,0x1312,0xb282,bts,0,100)
                usbCon.controlTransfer(0x40,0x9a,0x2518,0x00c3,bts,0,100)
                usbCon.controlTransfer(0x40,0xa4,0xffff,0x0000,bts,0,100)
                usbCon.controlTransfer(0xc0,0x95,0x0706,0x0000,bts,2,100)
                Log.d(TAG,"ch340 return %02x %02x".format(bts[0].toInt() and  0xff,bts[1].toInt() and  0xff))
                usbCon.controlTransfer(0x40,0xa4,0xff9f,0x0000,bts,0,100)
                usbCon.controlTransfer(0x40,0x9a,0x1312,0xcc83,bts,0,100)
                usbCon.controlTransfer(0x40,0x9a,0x2518,0x00c3,bts,0,100)
                usbCon.controlTransfer(0x40,0x9a,0xff9a,0x0000,bts,0,100)
            }else if (vid == 0x1fc9 && pid == 0x0094){ // standard serial
                usbCon.controlTransfer(0x21,0x22,0x0003,0x0000,bts,0,100)
                usbCon.controlTransfer(0x21,0x20,0x0000,0x0000,bts,7,100)
            }else{
                ret = "device 0x%04x 0x%04x unknow".format(vid,pid)
                Log.e(TAG,ret)
            }
        }
        return ret
    }

}