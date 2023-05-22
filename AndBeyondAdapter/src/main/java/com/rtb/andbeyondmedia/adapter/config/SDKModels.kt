package com.rtb.andbeyondmedia.adapter.config

import com.google.gson.annotations.SerializedName


internal data class SDKConfig(
        val prebid: Prebid? = null,
        @SerializedName("global")
        val switch: Int? = null,
        val refetch: Long? = null,
        @SerializedName("config")
        val refreshConfig: List<RefreshConfig>? = null,
) {

    data class Prebid(
            @SerializedName("firstlook")
            val firstLook: Int? = null,
            val other: Int? = null,
            val host: String? = null,
            @SerializedName("accountid")
            val accountId: String? = null,
            val timeout: String? = null,
            val debug: Int = 0,
            val schain: String? = null
    )

    data class RefreshConfig(
            val type: String? = null,
            @SerializedName("name_type")
            val nameType: String? = null,
            val sizes: List<Size>? = null,
            val follow: Int? = null,
            @SerializedName("pos")
            val position: Int? = null,
            val placement: Placement? = null,
            val specific: String? = null
    )

    data class Size(
            val width: String? = null,
            val height: String? = null,
            val sizes: List<Size>? = null
    ) {
        fun toSizes(): String {
            return sizes?.joinToString(",") ?: ""
        }

        override fun toString(): String {
            return String.format("%s x %s", width, height)
        }
    }

    data class Placement(
            @SerializedName("firstlook")
            val firstLook: String? = null,
            val other: String? = null
    )
}