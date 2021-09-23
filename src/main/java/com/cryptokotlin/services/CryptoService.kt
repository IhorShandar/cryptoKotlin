package com.cryptokotlin.services

import com.cryptokotlin.dao.CryptoDao
import com.cryptokotlin.models.CryptoK
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class CryptoService {

    @Autowired
    lateinit var cryptoDao: CryptoDao

    fun save(cryptoK: CryptoK): CryptoK? = cryptoDao.save(cryptoK)

    fun findLastByCurrencyName(curr1: String, curr2: String): CryptoK? = cryptoDao.findLastByCrypto(curr1, curr2)

    fun findMinByCurrencyName(name: String): CryptoK? = cryptoDao.findMinByCrypto(name)

    fun findMaxByCurrencyName(name: String): CryptoK? = cryptoDao.findMaxByCrypto(name)

    fun parseCurrency(s1: String, s2: String): CryptoK? {
        val response: StringBuilder = StringBuilder()
        try {
            val url = URL("https://cex.io/api/last_price/$s1/$s2")
            val request: HttpURLConnection = url.openConnection() as HttpURLConnection
            request.addRequestProperty("User-Agent", "Chrome")
            request.requestMethod = "GET"
            val inputStream: InputStream = request.inputStream
            var read: Int
            while (inputStream.read().also { read = it } != -1) {
                response.append(read.toChar())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var cryptoK = CryptoK()
        try {
            val jsonArray = JSONArray("[$response]")
            var dateFromNet = JSONObject()
            for (i in 0 until jsonArray.length()) {
                dateFromNet = jsonArray[i] as JSONObject
            }
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val currentTime: LocalDateTime = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter)
            println(dateFromNet)
            cryptoK = CryptoK(
                    lastPrice = dateFromNet.getDouble("lprice"),
                    crypto = dateFromNet.getString("curr1"),
                    dollar = dateFromNet.getString("curr2"),
                    createdAt = currentTime
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cryptoK
    }

    fun findAll(name: String, page: Int, size: Int): List<CryptoK?> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by("lprice"))
        return cryptoDao.findAllByCrypto(name, pageable)
    }

    fun createCSV(cryptos: MutableList<CryptoK?>){
        val nameColumns = arrayOf<String?>("Currency name", "minPrice", "maxPrice")
        val btc = arrayOf(cryptos[1]?.crypto, cryptos[1]?.lastPrice.toString(), cryptos[0]?.lastPrice.toString())
        val eth = arrayOf(cryptos[3]?.crypto, cryptos[3]?.lastPrice.toString(), cryptos[2]?.lastPrice.toString())
        val xpr = arrayOf(cryptos[5]?.crypto, cryptos[5]?.lastPrice.toString(), cryptos[4]?.lastPrice.toString())

        val list: MutableList<Array<String?>> = ArrayList()
        list.add(nameColumns)
        list.add(btc)
        list.add(eth)
        list.add(xpr)

        val csvOutputFile = File("Currency.csv")
        try {
            PrintWriter(csvOutputFile).use {
                pw -> list.map{ date -> java.lang.String.join("\t", *date) }
                          .forEach { x: String? -> pw.println(x) }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }


}
