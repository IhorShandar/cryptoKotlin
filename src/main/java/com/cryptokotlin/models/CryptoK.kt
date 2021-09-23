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
        var ID: Int,
        var lastPrice: Double,                                         // last price
        var crypto: String,                                            // crypto value
        var dollar: String,                                            //dollar value
        var createdAt: LocalDateTime
) {

    //create constructor without ID
    constructor(lastPrice: Double, crypto: String, dollar: String, createdAt: LocalDateTime) :
            this(ID= 0, lastPrice = lastPrice, crypto = crypto, dollar = dollar, createdAt= createdAt)

    //create empty constructor
    constructor() : this(ID= 0, lastPrice = 0.0, crypto = "", dollar = "", createdAt= LocalDateTime.now())

}
