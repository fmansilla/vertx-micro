package ar.ferman.vertxmicro.ranking.domain

typealias UserId = String
typealias Score = Int

data class UserRanking(val userId: UserId, val score: Score)

