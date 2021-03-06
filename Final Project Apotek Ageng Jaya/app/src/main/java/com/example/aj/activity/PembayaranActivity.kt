package com.example.aj.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aj.R
import com.example.aj.adapter.AdapterBank
import com.example.aj.app.ApiConfig
import com.example.aj.helper.Helper
import com.example.aj.model.Bank
import com.example.aj.model.Chekout
import com.example.aj.model.ResponModel
import com.example.aj.model.Transaksi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_pembayaran.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)
        Helper().setToolbar(this, toolbar, "Pembayaran")

        displayBank()
    }

    fun displayBank(){
        val arrBank = ArrayList<Bank>()
        arrBank.add(Bank("BCA", "123456789", "Ageng Jaya", R.drawable.logo_bca))
        arrBank.add(Bank("BRI", "9876543321", "Ageng Jaya", R.drawable.logo_bri))
        arrBank.add(Bank("Mandiri", "57539391", "Ageng Jaya", R.drawable.logo_madiri))

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_data.layoutManager = layoutManager
        rv_data.adapter = AdapterBank(arrBank, object :AdapterBank.Listeners{
            override fun onClicked(data: Bank, index: Int) {
                bayar(data)
            }

        })
    }

    fun bayar (bank: Bank){

        val json = intent.getStringExtra("extra")!!.toString()
        val chekout = Gson().fromJson(json, Chekout::class.java)
        chekout.bank = bank.nama

        ApiConfig.instanceRetrofit.chekout(chekout).enqueue(object : Callback<ResponModel>{
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if (!response.isSuccessful){
                    Log.d("Respon", "Errornya"+response.message())
                    return
                }

                val respon = response.body()!!
                if (respon.success == 1){

                    val jsBank = Gson().toJson(bank, Bank::class.java)
                    val jsTransaksi = Gson().toJson(respon.transaksi, Transaksi::class.java)
                    val jsChekout = Gson().toJson(chekout, Chekout::class.java)

                    val intent = Intent(this@PembayaranActivity, SuccessActivity::class.java)
                    intent.putExtra("bank", jsBank)
                    intent.putExtra("transaksi", jsTransaksi)
                    intent.putExtra("chekout", jsChekout)
                    startActivity(intent)

                } else {
                    Toast.makeText(this@PembayaranActivity, "Error" + respon.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}