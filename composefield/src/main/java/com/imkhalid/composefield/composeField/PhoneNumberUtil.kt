package com.imkhalid.composefield.composeField

import android.content.Context
import android.telephony.TelephonyManager
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale

class PhoneNumberUtil {

    var prefix = "254"
    var currentCountryCode = "ke"
    var shouldShowPicker=true
    var currentCountryFlag = "\uD83C\uDDF0\uD83C\uDDEA +"
    var minLength = 9
    var maxLength = 15

    data class CountryModel(
        val code: String,
        val dialCode: String,
        val name: String,
        var emoji: String,
        var length: Int = -1,
        var maxLength: Int = -1
    )

    data class PhoneNumber(
        val dialCode: String,
        val number: String,
        val emoji: String
    ){
        fun getFullNumber(): String{
            return dialCode+number
        }
    }

    fun setDefaultCountry(userCountryCode:String){
        when(userCountryCode){
            "1"->{
                prefix="92"
                currentCountryCode="pk"
                currentCountryFlag="\uD83C\uDDF5\uD83C\uDDF0 +"
                minLength=10
                maxLength=10
            }
            "2"->{ //Kenya
                prefix="254"
                currentCountryCode="ke"
                currentCountryFlag="\uD83C\uDDF0\uD83C\uDDEA +"
                minLength=9
                maxLength=9
            }
            "3"->{ //Tanzania
                prefix="255"
                currentCountryCode="tz"
                currentCountryFlag="\uD83C\uDDF9\uD83C\uDDFF +"
                minLength=9
                maxLength=10
            }
            "4"->{ //Uganda
                prefix="256"
                currentCountryCode="ug"
                currentCountryFlag="\uD83C\uDDFA\uD83C\uDDEC +"
                minLength=9
                maxLength=9
            }
        }
    }

    /*fun validateNumbers(number: String): Boolean {
        var bool = false
        numbers
            .find { x -> x.dialCode == prefix }
            ?.let {
                val isSameMinMax = it.length == it.maxLength
                bool =
                    if (isSameMinMax) number.length == it.length
                    else number.length >= it.length && number.length <= it.maxLength
            }
        return bool
    }*/

    fun validateNumbers(input: String): Boolean {
        try {
            val phoneUtil = PhoneNumberUtil.getInstance()
            val number = phoneUtil.parse(
                input,
                currentCountryCode
            )
            return phoneUtil.isValidNumber(
                number
            )
        } catch (e: Exception){
            return false
        }
    }

    fun getCountryModel(countryCode: Int, phoneUtil: PhoneNumberUtil): CountryModel? {
        val countryCode = phoneUtil.getRegionCodeForCountryCode(countryCode)
        return getLibraryMasterCountriesEnglish().orEmpty().find { it.code == countryCode.toString() }
    }

    fun setDefaultCountryCode(context: Context) {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountryCode = tm.simCountryIso.uppercase()
        val networkCountryCode = tm.networkCountryIso.uppercase()
        val localeCountryCode = Locale.getDefault().country // e.g., "US", "IN"
        val country = simCountryCode.ifEmpty {
            networkCountryCode.ifEmpty {
                localeCountryCode.ifEmpty {
                    currentCountryCode = "pk"
                    currentCountryCode
                }
            }
        }
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val model = getCountryModel(
            phoneNumberUtil.getCountryCodeForRegion(country),
            phoneUtil = phoneNumberUtil
        )
        currentCountryFlag = model?.emoji ?: currentCountryFlag
        currentCountryCode = model?.code ?: currentCountryCode
        this.prefix = model?.dialCode ?: this.prefix
    }

    companion object {
        val DEFAULT_FLAG_RES = ""
        val numbers =
            arrayOf(
                CountryModel("ke", "07", "Kenya", "🇰🇪 ", 8, 8),
                CountryModel("ke", "254", "Kenya", "🇰🇪 +", 9, 9),
                CountryModel("tz", "255", "Tanzania", "🇹🇿 +", 9, 10),
                CountryModel("ug", "256", "Uganda", "🇺🇬 +", 9, 9),
                CountryModel("pk", "92", "Pakistan", "🇵🇰 +", 10, 10)
            )

        /**
         * Returns image res based on country name code
         *
         * @param CountryModel
         * @return
         */
        fun getFlagEmoji(name: String): String {
            return when (name) {
                "ad" -> "🇦🇩"
                "ae" -> "🇦🇪"
                "af" -> "🇦🇫"
                "ag" -> "🇦🇬"
                "ai" -> "🇦🇮"
                "al" -> "🇦🇱"
                "am" -> "🇦🇲"
                "ao" -> "🇦🇴"
                "aq" -> "🇦🇶"
                "ar" -> "🇦🇷"
                "as" -> "🇦🇸"
                "at" -> "🇦🇹"
                "au" -> "🇦🇺"
                "aw" -> "🇦🇼"
                "ax" -> "🇦🇽"
                "az" -> "🇦🇿"
                "ba" -> "🇧🇦"
                "bb" -> "🇧🇧"
                "bd" -> "🇧🇩"
                "be" -> "🇧🇪"
                "bf" -> "🇧🇫"
                "bg" -> "🇧🇬"
                "bh" -> "🇧🇭"
                "bi" -> "🇧🇮"
                "bj" -> "🇧🇯"
                "bl" -> "🇧🇱"
                "bm" -> "🇧🇲"
                "bn" -> "🇧🇳"
                "bo" -> "🇧🇴"
                "bq" -> "🇧🇶"
                "br" -> "🇧🇷"
                "bs" -> "🇧🇸"
                "bt" -> "🇧🇹"
                "bv" -> "🇧🇻"
                "bw" -> "🇧🇼"
                "by" -> "🇧🇾"
                "bz" -> "🇧🇿"
                "ca" -> "🇨🇦"
                "cc" -> "🇨🇨"
                "cd" -> "🇨🇩"
                "cf" -> "🇨🇫"
                "cg" -> "🇨🇬"
                "ch" -> "🇨🇭"
                "ci" -> "🇨🇮"
                "ck" -> "🇨🇰"
                "cl" -> "🇨🇱"
                "cm" -> "🇨🇲"
                "cn" -> "🇨🇳"
                "co" -> "🇨🇴"
                "cr" -> "🇨🇷"
                "cu" -> "🇨🇺"
                "cv" -> "🇨🇻"
                "cw" -> "🇨🇼"
                "cx" -> "🇨🇽"
                "cy" -> "🇨🇾"
                "cz" -> "🇨🇿"
                "de" -> "🇩🇪"
                "dj" -> "🇩🇯"
                "dk" -> "🇩🇰"
                "dm" -> "🇩🇲"
                "do" -> "🇩🇴"
                "dz" -> "🇩🇿"
                "ec" -> "🇪🇨"
                "ee" -> "🇪🇪"
                "eg" -> "🇪🇬"
                "eh" -> "🇪🇭"
                "er" -> "🇪🇷"
                "es" -> "🇪🇸"
                "et" -> "🇪🇹"
                "fi" -> "🇫🇮"
                "fj" -> "🇫🇯"
                "fk" -> "🇫🇰"
                "fm" -> "🇫🇲"
                "fo" -> "🇫🇴"
                "fr" -> "🇫🇷"
                "ga" -> "🇬🇦"
                "gb" -> "🇬🇧"
                "gd" -> "🇬🇩"
                "ge" -> "🇬🇪"
                "gf" -> "🇬🇫"
                "gg" -> "🇬🇬"
                "gh" -> "🇬🇭"
                "gi" -> "🇬🇮"
                "gl" -> "🇬🇱"
                "gm" -> "🇬🇲"
                "gn" -> "🇬🇳"
                "gp" -> "🇬🇵"
                "gq" -> "🇬🇶"
                "gr" -> "🇬🇷"
                "gs" -> "🇬🇸"
                "gt" -> "🇬🇹"
                "gu" -> "🇬🇺"
                "gw" -> "🇬🇼"
                "gy" -> "🇬🇾"
                "hk" -> "🇭🇰"
                "hm" -> "🇭🇲"
                "hn" -> "🇭🇳"
                "hr" -> "🇭🇷"
                "ht" -> "🇭🇹"
                "hu" -> "🇭🇺"
                "id" -> "🇮🇩"
                "ie" -> "🇮🇪"
                "il" -> "🇮🇱"
                "im" -> "🇮🇲"
                "in" -> "🇮🇳"
                "io" -> "🇮🇴"
                "iq" -> "🇮🇶"
                "ir" -> "🇮🇷"
                "is" -> "🇮🇸"
                "it" -> "🇮🇹"
                "je" -> "🇯🇪"
                "jm" -> "🇯🇲"
                "jo" -> "🇯🇴"
                "jp" -> "🇯🇵"
                "ke" -> "🇰🇪"
                "kg" -> "🇰🇬"
                "kh" -> "🇰🇭"
                "ki" -> "🇰🇮"
                "km" -> "🇰🇲"
                "kn" -> "🇰🇳"
                "kp" -> "🇰🇵"
                "kr" -> "🇰🇷"
                "kw" -> "🇰🇼"
                "ky" -> "🇰🇾"
                "kz" -> "🇰🇿"
                "la" -> "🇱🇦"
                "lb" -> "🇱🇧"
                "lc" -> "🇱🇨"
                "li" -> "🇱🇮"
                "lk" -> "🇱🇰"
                "lr" -> "🇱🇷"
                "ls" -> "🇱🇸"
                "lt" -> "🇱🇹"
                "lu" -> "🇱🇺"
                "lv" -> "🇱🇻"
                "ly" -> "🇱🇾"
                "ma" -> "🇲🇦"
                "mc" -> "🇲🇨"
                "md" -> "🇲🇩"
                "me" -> "🇲🇪"
                "mf" -> "🇲🇫"
                "mg" -> "🇲🇬"
                "mh" -> "🇲🇭"
                "mk" -> "🇲🇰"
                "ml" -> "🇲🇱"
                "mm" -> "🇲🇲"
                "mn" -> "🇲🇳"
                "mo" -> "🇲🇴"
                "mp" -> "🇲🇵"
                "mq" -> "🇲🇶"
                "mr" -> "🇲🇷"
                "ms" -> "🇲🇸"
                "mt" -> "🇲🇹"
                "mu" -> "🇲🇺"
                "mv" -> "🇲🇻"
                "mw" -> "🇲🇼"
                "mx" -> "🇲🇽"
                "my" -> "🇲🇾"
                "mz" -> "🇲🇿"
                "na" -> "🇳🇦"
                "nc" -> "🇳🇨"
                "ne" -> "🇳🇪"
                "nf" -> "🇳🇫"
                "ng" -> "🇳🇬"
                "ni" -> "🇳🇮"
                "nl" -> "🇳🇱"
                "no" -> "🇳🇴"
                "np" -> "🇳🇵"
                "nr" -> "🇳🇷"
                "nu" -> "🇳🇺"
                "nz" -> "🇳🇿"
                "om" -> "🇴🇲"
                "pa" -> "🇵🇦"
                "pe" -> "🇵🇪"
                "pf" -> "🇵🇫"
                "pg" -> "🇵🇬"
                "ph" -> "🇵🇭"
                "pk" -> "🇵🇰"
                "pl" -> "🇵🇱"
                "pm" -> "🇵🇲"
                "pn" -> "🇵🇳"
                "pr" -> "🇵🇷"
                "ps" -> "🇵🇸"
                "pt" -> "🇵🇹"
                "pw" -> "🇵🇼"
                "py" -> "🇵🇾"
                "qa" -> "🇶🇦"
                "re" -> "🇷🇪"
                "ro" -> "🇷🇴"
                "rs" -> "🇷🇸"
                "ru" -> "🇷🇺"
                "rw" -> "🇷🇼"
                "sa" -> "🇸🇦"
                "sb" -> "🇸🇧"
                "sc" -> "🇸🇨"
                "sd" -> "🇸🇩"
                "se" -> "🇸🇪"
                "sg" -> "🇸🇬"
                "sh" -> "🇸🇭"
                "si" -> "🇸🇮"
                "sj" -> "🇸🇯"
                "sk" -> "🇸🇰"
                "sl" -> "🇸🇱"
                "sm" -> "🇸🇲"
                "sn" -> "🇸🇳"
                "so" -> "🇸🇴"
                "sr" -> "🇸🇷"
                "ss" -> "🇸🇸"
                "st" -> "🇸🇹"
                "sv" -> "🇸🇻"
                "sx" -> "🇸🇽"
                "sy" -> "🇸🇾"
                "sz" -> "🇸🇿"
                "tc" -> "🇹🇨"
                "td" -> "🇹🇩"
                "tf" -> "🇹🇫"
                "tg" -> "🇹🇬"
                "th" -> "🇹🇭"
                "tj" -> "🇹🇯"
                "tk" -> "🇹🇰"
                "tl" -> "🇹🇱"
                "tm" -> "🇹🇲"
                "tn" -> "🇹🇳"
                "to" -> "🇹🇴"
                "tr" -> "🇹🇷"
                "tt" -> "🇹🇹"
                "tv" -> "🇹🇻"
                "tw" -> "🇹🇼"
                "tz" -> "🇹🇿"
                "ua" -> "🇺🇦"
                "ug" -> "🇺🇬"
                "um" -> "🇺🇲"
                "us" -> "🇺🇸"
                "uy" -> "🇺🇾"
                "uz" -> "🇺🇿"
                "va" -> "🇻🇦"
                "vc" -> "🇻🇨"
                "ve" -> "🇻🇪"
                "vg" -> "🇻🇬"
                "vi" -> "🇻🇮"
                "vn" -> "🇻🇳"
                "vu" -> "🇻🇺"
                "wf" -> "🇼🇫"
                "ws" -> "🇼🇸"
                "xk" -> "🇽🇰"
                "ye" -> "🇾🇪"
                "yt" -> "🇾🇹"
                "za" -> "🇿🇦"
                "zm" -> "🇿🇲"
                "zw" -> "🇿🇼"
                else -> " "
            }
        }

        fun getLibraryMasterCountriesEnglish(): List<CountryModel>? {
            val countries = listOf(
                CountryModel(code = "PS", dialCode = "+970", name = "Palestine", emoji = "🇵🇸"),
                CountryModel(code = "PT", dialCode = "+351", name = "Portugal", emoji = "🇵🇹"),
                CountryModel(code = "PW", dialCode = "+680", name = "Palau", emoji = "🇵🇼"),
                CountryModel(code = "PY", dialCode = "+595", name = "Paraguay", emoji = "🇵🇾"),
                CountryModel(code = "QA", dialCode = "+974", name = "Qatar", emoji = "🇶🇦"),
                CountryModel(
                    code = "AC",
                    dialCode = "+247",
                    name = "Ascension Island",
                    emoji = " ",
                ),
                CountryModel(code = "AD", dialCode = "+376", name = "Andorra", emoji = "🇦🇩"),
                CountryModel(
                    code = "AE",
                    dialCode = "+971",
                    name = "United Arab Emirates",
                    emoji = "🇦🇪",
                ),
                CountryModel(code = "AF", dialCode = "+93", name = "Afghanistan", emoji = "🇦🇫"),
                CountryModel(
                    code = "AG",
                    dialCode = "+1",
                    name = "Antigua & Barbuda",
                    emoji = "🇦🇬",
                ),
                CountryModel(code = "AI", dialCode = "+1", name = "Anguilla", emoji = "🇦🇮"),
                CountryModel(code = "AL", dialCode = "+355", name = "Albania", emoji = "🇦🇱"),
                CountryModel(code = "AM", dialCode = "+374", name = "Armenia", emoji = "🇦🇲"),
                CountryModel(code = "AO", dialCode = "+244", name = "Angola", emoji = "🇦🇴"),
                CountryModel(code = "AR", dialCode = "+54", name = "Argentina", emoji = "🇦🇷"),
                CountryModel(code = "AS", dialCode = "+1", name = "American Samoa", emoji = "🇦🇸"),
                CountryModel(code = "AT", dialCode = "+43", name = "Austria", emoji = "🇦🇹"),
                CountryModel(code = "RE", dialCode = "+262", name = "Réunion", emoji = "🇷🇪"),
                CountryModel(code = "AU", dialCode = "+61", name = "Australia", emoji = "🇦🇺"),
                CountryModel(code = "AW", dialCode = "+297", name = "Aruba", emoji = "🇦🇼"),
                CountryModel(code = "AX", dialCode = "+358", name = "Åland Islands", emoji = "🇦🇽"),
                CountryModel(code = "AZ", dialCode = "+994", name = "Azerbaijan", emoji = "🇦🇿"),
                CountryModel(code = "RO", dialCode = "+40", name = "Romania", emoji = "🇷🇴"),
                CountryModel(
                    code = "BA",
                    dialCode = "+387",
                    name = "Bosnia & Herzegovina",
                    emoji = "🇧🇦",
                ),
                CountryModel(code = "BB", dialCode = "+1", name = "Barbados", emoji = "🇧🇧"),
                CountryModel(code = "RS", dialCode = "+381", name = "Serbia", emoji = "🇷🇸"),
                CountryModel(code = "BD", dialCode = "+880", name = "Bangladesh", emoji = "🇧🇩"),
                CountryModel(code = "RU", dialCode = "+7", name = "Russia", emoji = "🇷🇺"),
                CountryModel(code = "BE", dialCode = "+32", name = "Belgium", emoji = "🇧🇪"),
                CountryModel(code = "BF", dialCode = "+226", name = "Burkina Faso", emoji = "🇧🇫"),
                CountryModel(code = "RW", dialCode = "+250", name = "Rwanda", emoji = "🇷🇼"),
                CountryModel(code = "BG", dialCode = "+359", name = "Bulgaria", emoji = "🇧🇬"),
                CountryModel(code = "BH", dialCode = "+973", name = "Bahrain", emoji = "🇧🇭"),
                CountryModel(code = "BI", dialCode = "+257", name = "Burundi", emoji = "🇧🇮"),
                CountryModel(code = "BJ", dialCode = "+229", name = "Benin", emoji = "🇧🇯"),
                CountryModel(
                    code = "BL",
                    dialCode = "+590",
                    name = "St. Barthélemy",
                    emoji = "🇧🇱",
                ),
                CountryModel(code = "BM", dialCode = "+1", name = "Bermuda", emoji = "🇧🇲"),
                CountryModel(code = "BN", dialCode = "+673", name = "Brunei", emoji = "🇧🇳"),
                CountryModel(code = "BO", dialCode = "+591", name = "Bolivia", emoji = "🇧🇴"),
                CountryModel(code = "SA", dialCode = "+966", name = "Saudi Arabia", emoji = "🇸🇦"),
                CountryModel(
                    code = "BQ",
                    dialCode = "+599",
                    name = "Caribbean Netherlands",
                    emoji = "🇧🇶",
                ),
                CountryModel(
                    code = "SB",
                    dialCode = "+677",
                    name = "Solomon Islands",
                    emoji = "🇸🇧",
                ),
                CountryModel(code = "BR", dialCode = "+55", name = "Brazil", emoji = "🇧🇷"),
                CountryModel(code = "SC", dialCode = "+248", name = "Seychelles", emoji = "🇸🇨"),
                CountryModel(code = "BS", dialCode = "+1", name = "Bahamas", emoji = "🇧🇸"),
                CountryModel(code = "SD", dialCode = "+249", name = "Sudan", emoji = "🇸🇩"),
                CountryModel(code = "SE", dialCode = "+46", name = "Sweden", emoji = "🇸🇪"),
                CountryModel(code = "BT", dialCode = "+975", name = "Bhutan", emoji = "🇧🇹"),
                CountryModel(code = "SG", dialCode = "+65", name = "Singapore", emoji = "🇸🇬"),
                CountryModel(code = "BW", dialCode = "+267", name = "Botswana", emoji = "🇧🇼"),
                CountryModel(code = "SH", dialCode = "+290", name = "St. Helena", emoji = "🇸🇭"),
                CountryModel(code = "SI", dialCode = "+386", name = "Slovenia", emoji = "🇸🇮"),
                CountryModel(
                    code = "SJ",
                    dialCode = "+47",
                    name = "Svalbard & Jan Mayen",
                    emoji = "🇸🇯",
                ),
                CountryModel(code = "BY", dialCode = "+375", name = "Belarus", emoji = "🇧🇾"),
                CountryModel(code = "SK", dialCode = "+421", name = "Slovakia", emoji = "🇸🇰"),
                CountryModel(code = "BZ", dialCode = "+501", name = "Belize", emoji = "🇧🇿"),
                CountryModel(code = "SL", dialCode = "+232", name = "Sierra Leone", emoji = "🇸🇱"),
                CountryModel(code = "SM", dialCode = "+378", name = "San Marino", emoji = "🇸🇲"),
                CountryModel(code = "SN", dialCode = "+221", name = "Senegal", emoji = "🇸🇳"),
                CountryModel(code = "SO", dialCode = "+252", name = "Somalia", emoji = "🇸🇴"),
                CountryModel(code = "CA", dialCode = "+1", name = "Canada", emoji = "🇨🇦"),
                CountryModel(code = "SR", dialCode = "+597", name = "Suriname", emoji = "🇸🇷"),
                CountryModel(
                    code = "CC",
                    dialCode = "+61",
                    name = "Cocos (Keeling) Islands",
                    emoji = "🇨🇨",
                ),
                CountryModel(code = "SS", dialCode = "+211", name = "South Sudan", emoji = "🇸🇸"),
                CountryModel(
                    code = "ST",
                    dialCode = "+239",
                    name = "São Tomé & Príncipe",
                    emoji = "🇸🇹",
                ),
                CountryModel(
                    code = "CD",
                    dialCode = "+243",
                    name = "Congo - Kinshasa",
                    emoji = "🇨🇩",
                ),
                CountryModel(
                    code = "CF",
                    dialCode = "+236",
                    name = "Central African Republic",
                    emoji = "🇨🇫",
                ),
                CountryModel(code = "SV", dialCode = "+503", name = "El Salvador", emoji = "🇸🇻"),
                CountryModel(
                    code = "CG",
                    dialCode = "+242",
                    name = "Congo - Brazzaville",
                    emoji = "🇨🇬",
                ),
                CountryModel(code = "SX", dialCode = "+1", name = "Sint Maarten", emoji = "🇸🇽"),
                CountryModel(code = "CH", dialCode = "+41", name = "Switzerland", emoji = "🇨🇭"),
                CountryModel(code = "CI", dialCode = "+225", name = "Côte d’Ivoire", emoji = "🇨🇮"),
                CountryModel(code = "SY", dialCode = "+963", name = "Syria", emoji = "🇸🇾"),
                CountryModel(code = "SZ", dialCode = "+268", name = "Eswatini", emoji = "🇸🇿"),
                CountryModel(code = "CK", dialCode = "+682", name = "Cook Islands", emoji = "🇨🇰"),
                CountryModel(code = "CL", dialCode = "+56", name = "Chile", emoji = "🇨🇱"),
                CountryModel(code = "CM", dialCode = "+237", name = "Cameroon", emoji = "🇨🇲"),
                CountryModel(code = "CN", dialCode = "+86", name = "China", emoji = "🇨🇳"),
                CountryModel(code = "CO", dialCode = "+57", name = "Colombia", emoji = "🇨🇴"),
                CountryModel(
                    code = "TA",
                    dialCode = "+290",
                    name = "Tristan da Cunha",
                    emoji = " ",
                ),
                CountryModel(
                    code = "TC",
                    dialCode = "+1",
                    name = "Turks & Caicos Islands",
                    emoji = "🇹🇨",
                ),
                CountryModel(code = "CR", dialCode = "+506", name = "Costa Rica", emoji = "🇨🇷"),
                CountryModel(code = "TD", dialCode = "+235", name = "Chad", emoji = "🇹🇩"),
                CountryModel(code = "CU", dialCode = "+53", name = "Cuba", emoji = "🇨🇺"),
                CountryModel(code = "TG", dialCode = "+228", name = "Togo", emoji = "🇹🇬"),
                CountryModel(code = "CV", dialCode = "+238", name = "Cape Verde", emoji = "🇨🇻"),
                CountryModel(code = "TH", dialCode = "+66", name = "Thailand", emoji = "🇹🇭"),
                CountryModel(code = "CW", dialCode = "+599", name = "Curaçao", emoji = "🇨🇼"),
                CountryModel(
                    code = "CX",
                    dialCode = "+61",
                    name = "Christmas Island",
                    emoji = "🇨🇽",
                ),
                CountryModel(code = "CY", dialCode = "+357", name = "Cyprus", emoji = "🇨🇾"),
                CountryModel(code = "TJ", dialCode = "+992", name = "Tajikistan", emoji = "🇹🇯"),
                CountryModel(code = "TK", dialCode = "+690", name = "Tokelau", emoji = "🇹🇰"),
                CountryModel(code = "CZ", dialCode = "+420", name = "Czechia", emoji = "🇨🇿"),
                CountryModel(code = "TL", dialCode = "+670", name = "Timor-Leste", emoji = "🇹🇱"),
                CountryModel(code = "TM", dialCode = "+993", name = "Turkmenistan", emoji = "🇹🇲"),
                CountryModel(code = "TN", dialCode = "+216", name = "Tunisia", emoji = "🇹🇳"),
                CountryModel(code = "TO", dialCode = "+676", name = "Tonga", emoji = "🇹🇴"),
                CountryModel(code = "TR", dialCode = "+90", name = "Türkiye", emoji = "🇹🇷"),
                CountryModel(
                    code = "TT",
                    dialCode = "+1",
                    name = "Trinidad & Tobago",
                    emoji = "🇹🇹",
                ),
                CountryModel(code = "DE", dialCode = "+49", name = "Germany", emoji = "🇩🇪"),
                CountryModel(code = "TV", dialCode = "+688", name = "Tuvalu", emoji = "🇹🇻"),
                CountryModel(code = "TW", dialCode = "+886", name = "Taiwan", emoji = "🇹🇼"),
                CountryModel(code = "DJ", dialCode = "+253", name = "Djibouti", emoji = "🇩🇯"),
                CountryModel(code = "TZ", dialCode = "+255", name = "Tanzania", emoji = "🇹🇿"),
                CountryModel(code = "DK", dialCode = "+45", name = "Denmark", emoji = "🇩🇰"),
                CountryModel(code = "DM", dialCode = "+1", name = "Dominica", emoji = "🇩🇲"),
                CountryModel(
                    code = "DO",
                    dialCode = "+1",
                    name = "Dominican Republic",
                    emoji = "🇩🇴",
                ),
                CountryModel(code = "UA", dialCode = "+380", name = "Ukraine", emoji = "🇺🇦"),
                CountryModel(code = "UG", dialCode = "+256", name = "Uganda", emoji = "🇺🇬"),
                CountryModel(code = "DZ", dialCode = "+213", name = "Algeria", emoji = "🇩🇿"),
                CountryModel(code = "US", dialCode = "+1", name = "United States", emoji = "🇺🇸"),
                CountryModel(code = "EC", dialCode = "+593", name = "Ecuador", emoji = "🇪🇨"),
                CountryModel(code = "EE", dialCode = "+372", name = "Estonia", emoji = "🇪🇪"),
                CountryModel(code = "EG", dialCode = "+20", name = "Egypt", emoji = "🇪🇬"),
                CountryModel(
                    code = "EH",
                    dialCode = "+212",
                    name = "Western Sahara",
                    emoji = "🇪🇭",
                ),
                CountryModel(code = "UY", dialCode = "+598", name = "Uruguay", emoji = "🇺🇾"),
                CountryModel(code = "UZ", dialCode = "+998", name = "Uzbekistan", emoji = "🇺🇿"),
                CountryModel(code = "VA", dialCode = "+39", name = "Vatican City", emoji = "🇻🇦"),
                CountryModel(
                    code = "VC",
                    dialCode = "+1",
                    name = "St. Vincent & Grenadines",
                    emoji = "🇻🇨",
                ),
                CountryModel(code = "ER", dialCode = "+291", name = "Eritrea", emoji = "🇪🇷"),
                CountryModel(code = "ES", dialCode = "+34", name = "Spain", emoji = "🇪🇸"),
                CountryModel(code = "VE", dialCode = "+58", name = "Venezuela", emoji = "🇻🇪"),
                CountryModel(code = "ET", dialCode = "+251", name = "Ethiopia", emoji = "🇪🇹"),
                CountryModel(
                    code = "VG",
                    dialCode = "+1",
                    name = "British Virgin Islands",
                    emoji = "🇻🇬",
                ),
                CountryModel(
                    code = "VI",
                    dialCode = "+1",
                    name = "U.S. Virgin Islands",
                    emoji = "🇻🇮",
                ),
                CountryModel(code = "VN", dialCode = "+84", name = "Vietnam", emoji = "🇻🇳"),
                CountryModel(code = "VU", dialCode = "+678", name = "Vanuatu", emoji = "🇻🇺"),
                CountryModel(code = "FI", dialCode = "+358", name = "Finland", emoji = "🇫🇮"),
                CountryModel(code = "FJ", dialCode = "+679", name = "Fiji", emoji = "🇫🇯"),
                CountryModel(
                    code = "FK",
                    dialCode = "+500",
                    name = "Falkland Islands (Islas Malvinas)",
                    emoji = "🇫🇰",
                ),
                CountryModel(code = "FM", dialCode = "+691", name = "Micronesia", emoji = "🇫🇲"),
                CountryModel(code = "FO", dialCode = "+298", name = "Faroe Islands", emoji = "🇫🇴"),
                CountryModel(code = "FR", dialCode = "+33", name = "France", emoji = "🇫🇷"),
                CountryModel(
                    code = "WF",
                    dialCode = "+681",
                    name = "Wallis & Futuna",
                    emoji = "🇼🇫",
                ),
                CountryModel(code = "GA", dialCode = "+241", name = "Gabon", emoji = "🇬🇦"),
                CountryModel(code = "GB", dialCode = "+44", name = "United Kingdom", emoji = "🇬🇧"),
                CountryModel(code = "WS", dialCode = "+685", name = "Samoa", emoji = "🇼🇸"),
                CountryModel(code = "GD", dialCode = "+1", name = "Grenada", emoji = "🇬🇩"),
                CountryModel(code = "GE", dialCode = "+995", name = "Georgia", emoji = "🇬🇪"),
                CountryModel(code = "GF", dialCode = "+594", name = "French Guiana", emoji = "🇬🇫"),
                CountryModel(code = "GG", dialCode = "+44", name = "Guernsey", emoji = "🇬🇬"),
                CountryModel(code = "GH", dialCode = "+233", name = "Ghana", emoji = "🇬🇭"),
                CountryModel(code = "GI", dialCode = "+350", name = "Gibraltar", emoji = "🇬🇮"),
                CountryModel(code = "GL", dialCode = "+299", name = "Greenland", emoji = "🇬🇱"),
                CountryModel(code = "GM", dialCode = "+220", name = "Gambia", emoji = "🇬🇲"),
                CountryModel(code = "GN", dialCode = "+224", name = "Guinea", emoji = "🇬🇳"),
                CountryModel(code = "GP", dialCode = "+590", name = "Guadeloupe", emoji = "🇬🇵"),
                CountryModel(
                    code = "GQ",
                    dialCode = "+240",
                    name = "Equatorial Guinea",
                    emoji = "🇬🇶",
                ),
                CountryModel(code = "GR", dialCode = "+30", name = "Greece", emoji = "🇬🇷"),
                CountryModel(code = "GT", dialCode = "+502", name = "Guatemala", emoji = "🇬🇹"),
                CountryModel(code = "GU", dialCode = "+1", name = "Guam", emoji = "🇬🇺"),
                CountryModel(code = "GW", dialCode = "+245", name = "Guinea-Bissau", emoji = "🇬🇼"),
                CountryModel(code = "GY", dialCode = "+592", name = "Guyana", emoji = "🇬🇾"),
                CountryModel(code = "XK", dialCode = "+383", name = "Kosovo", emoji = "🇽🇰"),
                CountryModel(code = "HK", dialCode = "+852", name = "Hong Kong", emoji = "🇭🇰"),
                CountryModel(code = "HN", dialCode = "+504", name = "Honduras", emoji = "🇭🇳"),
                CountryModel(code = "HR", dialCode = "+385", name = "Croatia", emoji = "🇭🇷"),
                CountryModel(code = "YE", dialCode = "+967", name = "Yemen", emoji = "🇾🇪"),
                CountryModel(code = "HT", dialCode = "+509", name = "Haiti", emoji = "🇭🇹"),
                CountryModel(code = "HU", dialCode = "+36", name = "Hungary", emoji = "🇭🇺"),
                CountryModel(code = "ID", dialCode = "+62", name = "Indonesia", emoji = "🇮🇩"),
                CountryModel(code = "YT", dialCode = "+262", name = "Mayotte", emoji = "🇾🇹"),
                CountryModel(code = "IE", dialCode = "+353", name = "Ireland", emoji = "🇮🇪"),
                CountryModel(code = "IL", dialCode = "+972", name = "Israel", emoji = "🇮🇱"),
                CountryModel(code = "IM", dialCode = "+44", name = "Isle of Man", emoji = "🇮🇲"),
                CountryModel(code = "IN", dialCode = "+91", name = "India", emoji = "🇮🇳"),
                CountryModel(
                    code = "IO",
                    dialCode = "+246",
                    name = "British Indian Ocean Territory",
                    emoji = "🇮🇴",
                ),
                CountryModel(code = "ZA", dialCode = "+27", name = "South Africa", emoji = "🇿🇦"),
                CountryModel(code = "IQ", dialCode = "+964", name = "Iraq", emoji = "🇮🇶"),
                CountryModel(code = "IR", dialCode = "+98", name = "Iran", emoji = "🇮🇷"),
                CountryModel(code = "IS", dialCode = "+354", name = "Iceland", emoji = "🇮🇸"),
                CountryModel(code = "IT", dialCode = "+39", name = "Italy", emoji = "🇮🇹"),
                CountryModel(code = "ZM", dialCode = "+260", name = "Zambia", emoji = "🇿🇲"),
                CountryModel(code = "JE", dialCode = "+44", name = "Jersey", emoji = "🇯🇪"),
                CountryModel(code = "ZW", dialCode = "+263", name = "Zimbabwe", emoji = "🇿🇼"),
                CountryModel(code = "JM", dialCode = "+1", name = "Jamaica", emoji = "🇯🇲"),
                CountryModel(code = "JO", dialCode = "+962", name = "Jordan", emoji = "🇯🇴"),
                CountryModel(code = "JP", dialCode = "+81", name = "Japan", emoji = "🇯🇵"),
                CountryModel(code = "KE", dialCode = "+254", name = "Kenya", emoji = "🇰🇪"),
                CountryModel(code = "KG", dialCode = "+996", name = "Kyrgyzstan", emoji = "🇰🇬"),
                CountryModel(code = "KH", dialCode = "+855", name = "Cambodia", emoji = "🇰🇭"),
                CountryModel(code = "KI", dialCode = "+686", name = "Kiribati", emoji = "🇰🇮"),
                CountryModel(code = "KM", dialCode = "+269", name = "Comoros", emoji = "🇰🇲"),
                CountryModel(
                    code = "KN",
                    dialCode = "+1",
                    name = "St. Kitts & Nevis",
                    emoji = "🇰🇳",
                ),
                CountryModel(code = "KP", dialCode = "+850", name = "North Korea", emoji = "🇰🇵"),
                CountryModel(code = "KR", dialCode = "+82", name = "South Korea", emoji = "🇰🇷"),
                CountryModel(code = "KW", dialCode = "+965", name = "Kuwait", emoji = "🇰🇼"),
                CountryModel(code = "KY", dialCode = "+1", name = "Cayman Islands", emoji = "🇰🇾"),
                CountryModel(code = "KZ", dialCode = "+7", name = "Kazakhstan", emoji = "🇰🇿"),
                CountryModel(code = "LA", dialCode = "+856", name = "Laos", emoji = "🇱🇦"),
                CountryModel(code = "LB", dialCode = "+961", name = "Lebanon", emoji = "🇱🇧"),
                CountryModel(code = "LC", dialCode = "+1", name = "St. Lucia", emoji = "🇱🇨"),
                CountryModel(code = "LI", dialCode = "+423", name = "Liechtenstein", emoji = "🇱🇮"),
                CountryModel(code = "LK", dialCode = "+94", name = "Sri Lanka", emoji = "🇱🇰"),
                CountryModel(code = "LR", dialCode = "+231", name = "Liberia", emoji = "🇱🇷"),
                CountryModel(code = "LS", dialCode = "+266", name = "Lesotho", emoji = "🇱🇸"),
                CountryModel(code = "LT", dialCode = "+370", name = "Lithuania", emoji = "🇱🇹"),
                CountryModel(code = "LU", dialCode = "+352", name = "Luxembourg", emoji = "🇱🇺"),
                CountryModel(code = "LV", dialCode = "+371", name = "Latvia", emoji = "🇱🇻"),
                CountryModel(code = "LY", dialCode = "+218", name = "Libya", emoji = "🇱🇾"),
                CountryModel(code = "MA", dialCode = "+212", name = "Morocco", emoji = "🇲🇦"),
                CountryModel(code = "MC", dialCode = "+377", name = "Monaco", emoji = "🇲🇨"),
                CountryModel(code = "MD", dialCode = "+373", name = "Moldova", emoji = "🇲🇩"),
                CountryModel(code = "ME", dialCode = "+382", name = "Montenegro", emoji = "🇲🇪"),
                CountryModel(code = "MF", dialCode = "+590", name = "St. Martin", emoji = "🇲🇫"),
                CountryModel(code = "MG", dialCode = "+261", name = "Madagascar", emoji = "🇲🇬"),
                CountryModel(
                    code = "MH",
                    dialCode = "+692",
                    name = "Marshall Islands",
                    emoji = "🇲🇭",
                ),
                CountryModel(
                    code = "MK",
                    dialCode = "+389",
                    name = "North Macedonia",
                    emoji = "🇲🇰",
                ),
                CountryModel(code = "ML", dialCode = "+223", name = "Mali", emoji = "🇲🇱"),
                CountryModel(
                    code = "MM",
                    dialCode = "+95",
                    name = "Myanmar (Burma)",
                    emoji = "🇲🇲",
                ),
                CountryModel(code = "MN", dialCode = "+976", name = "Mongolia", emoji = "🇲🇳"),
                CountryModel(code = "MO", dialCode = "+853", name = "Macao", emoji = "🇲🇴"),
                CountryModel(
                    code = "MP",
                    dialCode = "+1",
                    name = "Northern Mariana Islands",
                    emoji = "🇲🇵",
                ),
                CountryModel(code = "MQ", dialCode = "+596", name = "Martinique", emoji = "🇲🇶"),
                CountryModel(code = "MR", dialCode = "+222", name = "Mauritania", emoji = "🇲🇷"),
                CountryModel(code = "MS", dialCode = "+1", name = "Montserrat", emoji = "🇲🇸"),
                CountryModel(code = "MT", dialCode = "+356", name = "Malta", emoji = "🇲🇹"),
                CountryModel(code = "MU", dialCode = "+230", name = "Mauritius", emoji = "🇲🇺"),
                CountryModel(code = "MV", dialCode = "+960", name = "Maldives", emoji = "🇲🇻"),
                CountryModel(code = "MW", dialCode = "+265", name = "Malawi", emoji = "🇲🇼"),
                CountryModel(code = "MX", dialCode = "+52", name = "Mexico", emoji = "🇲🇽"),
                CountryModel(code = "MY", dialCode = "+60", name = "Malaysia", emoji = "🇲🇾"),
                CountryModel(code = "MZ", dialCode = "+258", name = "Mozambique", emoji = "🇲🇿"),
                CountryModel(code = "NA", dialCode = "+264", name = "Namibia", emoji = "🇳🇦"),
                CountryModel(code = "NC", dialCode = "+687", name = "New Caledonia", emoji = "🇳🇨"),
                CountryModel(code = "NE", dialCode = "+227", name = "Niger", emoji = "🇳🇪"),
                CountryModel(
                    code = "NF",
                    dialCode = "+672",
                    name = "Norfolk Island",
                    emoji = "🇳🇫",
                ),
                CountryModel(code = "NG", dialCode = "+234", name = "Nigeria", emoji = "🇳🇬"),
                CountryModel(code = "NI", dialCode = "+505", name = "Nicaragua", emoji = "🇳🇮"),
                CountryModel(code = "NL", dialCode = "+31", name = "Netherlands", emoji = "🇳🇱"),
                CountryModel(code = "NO", dialCode = "+47", name = "Norway", emoji = "🇳🇴"),
                CountryModel(code = "NP", dialCode = "+977", name = "Nepal", emoji = "🇳🇵"),
                CountryModel(code = "NR", dialCode = "+674", name = "Nauru", emoji = "🇳🇷"),
                CountryModel(code = "NU", dialCode = "+683", name = "Niue", emoji = "🇳🇺"),
                CountryModel(code = "NZ", dialCode = "+64", name = "New Zealand", emoji = "🇳🇿"),
                CountryModel(code = "OM", dialCode = "+968", name = "Oman", emoji = "🇴🇲"),
                CountryModel(code = "PA", dialCode = "+507", name = "Panama", emoji = "🇵🇦"),
                CountryModel(code = "PE", dialCode = "+51", name = "Peru", emoji = "🇵🇪"),
                CountryModel(
                    code = "PF",
                    dialCode = "+689",
                    name = "French Polynesia",
                    emoji = "🇵🇫",
                ),
                CountryModel(
                    code = "PG",
                    dialCode = "+675",
                    name = "Papua New Guinea",
                    emoji = "🇵🇬",
                ),
                CountryModel(code = "PH", dialCode = "+63", name = "Philippines", emoji = "🇵🇭"),
                CountryModel(code = "PK", dialCode = "+92", name = "Pakistan", emoji = "🇵🇰"),
                CountryModel(code = "PL", dialCode = "+48", name = "Poland", emoji = "🇵🇱"),
                CountryModel(
                    code = "PM",
                    dialCode = "+508",
                    name = "St. Pierre & Miquelon",
                    emoji = "🇵🇲",
                )
            )
            return countries
        }
    }
}
