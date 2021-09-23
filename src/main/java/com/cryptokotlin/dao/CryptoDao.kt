package com.cryptokotlin.dao

import com.cryptokotlin.models.CryptoK
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CryptoDao: JpaRepository<CryptoK, Int> {
    @Query(value = "select * from crypto.cryptok where crypto = :crypto AND last_price = (select MIN(last_price) from cryptok where crypto = :crypto) limit 1", nativeQuery = true)
    fun findMinByCrypto(crypto: String): CryptoK?

    @Query(value = "select * from crypto.cryptok where crypto = :crypto AND last_price = (select MAX(last_price) from cryptok where crypto = :crypto) limit 1", nativeQuery = true)
    fun findMaxByCrypto(crypto: String): CryptoK?

    fun findAllByCrypto(crypto: String, pageable: Pageable?): List<CryptoK?>

    @Query(value = "select * from crypto.cryptok where id = (select MAX(id) from cryptok where crypto = :crypto AND dollar = :dollar) limit 1", nativeQuery = true)
    fun findLastByCrypto(crypto: String, dollar: String): CryptoK?
}
