package com.cryptokotlin.controllers

import com.cryptokotlin.models.CryptoK
import com.cryptokotlin.services.CryptoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@EnableScheduling
@RestController
class Controller{

    @Autowired
    lateinit var cryptoService: CryptoService

    @Scheduled(cron = "0/20 * * * * ?")
    fun saveEvery10Seconds(){
        val cryptoK: CryptoK? = cryptoService.parseCurrency("BTC", "USD")
        if (cryptoK != null) cryptoService.save(cryptoK)
        val cryptoK1: CryptoK? = cryptoService.parseCurrency("ETH", "USD")
        if (cryptoK1 != null) cryptoService.save(cryptoK1)
        val cryptoK2: CryptoK? = cryptoService.parseCurrency("XRP", "USD")
        if (cryptoK2 != null) cryptoService.save(cryptoK2)
    }

    @GetMapping(value = ["/crypto/{s1}/{s2}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun lastPrice(@PathVariable s1: String, @PathVariable s2: String): ResponseEntity<*> {
        return if ((s1 == "BTC" || s1 == "ETH" || s1 == "XRP") && s2 == "USD") {
            val last = cryptoService.findLastByCurrencyName(s1, s2)
            if (last != null){
                ResponseEntity.ok(last)
            } else ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"success\":false,\"error\":\"Not found\"}")
        } else {
            ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("{\"success\":false,\"error\":\"Incorrect currency name. First value can be: BTC, ETH, XRP and second value - USD\"}")
        }
    }

    @GetMapping(value = ["/cryptocurrencies/minprice"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMinPrice(@RequestParam name: String): ResponseEntity<*> {
        return if (name == "BTC" || name == "ETH" || name == "XRP") {
            val findMin = cryptoService.findMinByCurrencyName(name)
            if (findMin != null){
                ResponseEntity.ok(findMin)
            } else ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"success\":false,\"error\":\"Not found\"}")
        } else ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("{\"success\":false,\"error\":\"Incorrect currency name. Can be: BTC, ETH, XRP\"}")
    }

    @GetMapping(value = ["/cryptocurrencies/maxprice"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMaxPrice(@RequestParam name: String): ResponseEntity<*> {
        return if (name == "BTC" || name == "ETH" || name == "XRP") {
            val findMax = cryptoService.findMaxByCurrencyName(name)
            if (findMax != null){
                ResponseEntity.ok(findMax)
            } else ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"success\":false,\"error\":\"Not found\"}")
        } else ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("{\"success\":false,\"error\":\"Incorrect currency name. Can be: BTC, ETH, XRP\"}")
    }

    @GetMapping(value = ["/cryptocurrencies"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPrices(@RequestParam params: HashMap<String, String>): ResponseEntity<*> {
        val name: String = params["name"] ?: ""
        val size: Int = params["size"]?.toInt() ?: 10
        val page: Int = params["page"]?.toInt() ?: 0
        return if (name != "") {
            val findAll = cryptoService.findAll(name, page, size)
            if (findAll.isNotEmpty()){
                ResponseEntity.ok(findAll)
            } else ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"success\":false,\"error\":\"Not found\"}")
        } else ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("{\"success\":false,\"error\":\"Incorrect currency name. Can be: BTC, ETH, XRP\"}")
    }

    @GetMapping("/cryptocurrencies/csv")
    fun createCSV() {
        val cryptos: MutableList<CryptoK?> = ArrayList()
        cryptos.add(cryptoService.findMaxByCurrencyName("BTC"))
        cryptos.add(cryptoService.findMinByCurrencyName("BTC"))
        cryptos.add(cryptoService.findMaxByCurrencyName("ETH"))
        cryptos.add(cryptoService.findMinByCurrencyName("ETH"))
        cryptos.add(cryptoService.findMaxByCurrencyName("XRP"))
        cryptos.add(cryptoService.findMinByCurrencyName("XRP"))
        cryptoService.createCSV(cryptos)
    }
}
