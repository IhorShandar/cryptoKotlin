package com.cryptokotlin.models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class CryptoK(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonIgnore
        var ID: Int = 0,
        var lastPrice: Double = 0.0,                                         // last price
        var crypto: String = "",                                            // crypto value
        var dollar: String = "",                                            //dollar value
        var createdAt: LocalDateTime = LocalDateTime.now()
) {

}
