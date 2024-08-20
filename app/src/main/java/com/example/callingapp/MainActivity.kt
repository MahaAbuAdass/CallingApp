package com.example.callingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.Manifest
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.callingapp.databinding.ActivityMainBinding
import com.nexmo.client.NexmoCall
import com.nexmo.client.NexmoCallEventListener
import com.nexmo.client.NexmoCallMemberStatus
import com.nexmo.client.NexmoClient
import com.nexmo.client.NexmoConversationListener
import com.nexmo.client.NexmoLegTransferEvent
import com.nexmo.client.NexmoMediaActionState
import com.nexmo.client.NexmoMember
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoConnectionListener
import com.nexmo.client.request_listener.NexmoRequestListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var client : NexmoClient

    var onGoingCall : NexmoCall ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val callsPermission = arrayOf(Manifest.permission.RECORD_AUDIO)
        ActivityCompat.requestPermissions(this,callsPermission,123)


        client = NexmoClient.Builder().build(this)
        client.setConnectionListener{connectionStatus,_ ->
            runOnUiThread{binding.tvConnectionStatus.text = connectionStatus.toString()}

            if (connectionStatus==NexmoConnectionListener.ConnectionStatus.CONNECTED){
                runOnUiThread { binding.btnStartCall.visibility= View.VISIBLE }
                return@setConnectionListener
            }
        }

        client.login("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1Dy+6OQPSv8psANL0tp8\n" +
                "lZOBIe7SukXmU3XoM9aRx+8HKzTLyGnpufHPzADUd69pKM6b35zu3ZNkEtsodtB9\n" +
                "k0k8tbsX6NGt2guo6R3KnzRt2JkB3hqDT1gGRs1+wj2OKy7YpOoA3JNtUGdJUpYE\n" +
                "ibxBwx4/BsBRi3c43kYk6i1FAN/u0BK/aoa3FNFFJUWogAe2UqjQ5IoRDQzY0yez\n" +
                "kwgwk5HJFCK3p6XAwgA+yBHPn3IubWa77o9oPOnfY+jx16/BwBsxlI4IKt3z8KvW\n" +
                "Jt4CHejPsbRTp/5smHfR1IBY8pHNbj9ropbGND7CwUlr+fdHArBFFYHDou2TSZHG\n" +
                "IwIDAQAB")

        binding.btnStartCall.setOnClickListener {
            startCall()
        }

        binding.btnEndCall.setOnClickListener {
            endCall()
        }


    }

    private fun endCall() {
        client.serverCall("00962797734726", null,
            object : NexmoRequestListener<NexmoCall> {
                override fun onError(error: NexmoApiError) {
                    // Handle error here
                }

                override fun onSuccess(result: NexmoCall?) {
                    runOnUiThread {
                        binding.btnEndCall.visibility = View.INVISIBLE
                        binding.btnStartCall.visibility = View.VISIBLE
                    }

                    onGoingCall = result
                    onGoingCall?.addCallEventListener(object : NexmoCallEventListener {
                        override fun onMemberStatusUpdated(
                            newState: NexmoCallMemberStatus?,
                            member: NexmoMember?
                        ) {
                            if (newState == NexmoCallMemberStatus.COMPLETED ||
                                newState == NexmoCallMemberStatus.CANCELLED
                            ) {
                                onGoingCall = null
                                runOnUiThread {
                                    binding.btnStartCall.visibility = View.VISIBLE
                                }
                            }
                        }

                        override fun onMuteChanged(
                            newState: NexmoMediaActionState?,
                            member: NexmoMember?
                        ) {
                            // Handle mute change
                        }

                        override fun onEarmuffChanged(
                            newState: NexmoMediaActionState?,
                            member: NexmoMember?
                        ) {
                            // Handle earmuff change
                        }

                        override fun onDTMF(dtmf: String?, member: NexmoMember?) {
                            // Handle DTMF
                        }

                        override fun onLegTransfer(
                            event: NexmoLegTransferEvent?,
                            member: NexmoMember?
                        ) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            })
    }

    private fun startCall() {
        onGoingCall?.hangup(object : NexmoRequestListener<NexmoCall> {
            override fun onError(error: NexmoApiError) {
                // Handle error here
            }

            override fun onSuccess(result: NexmoCall?) {
                onGoingCall = null
            }
        })
    }

}