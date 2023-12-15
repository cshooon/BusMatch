package hoon.mobile.soundmatch

import SearchItem
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL

class ArrivalFragment : Fragment() {
    private var searchItem: SearchItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchItem = it.getParcelable("searchItem")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.arrival_fragment, container, false)
        val arrivalInfoTextView = view.findViewById<TextView>(R.id.arrivalInfoTextView)

        searchItem?.let {
            fetchBusArrivalInfo(it, arrivalInfoTextView)
        }

        return view
    }

    private fun fetchBusArrivalInfo(searchItem: SearchItem, textView: TextView) {
        lifecycleScope.launch {
            val urlString = when {
                searchItem.nodeId != null && searchItem.sequence != null -> {
                    "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute?serviceKey=${BuildConfig.api_key}&stId=${searchItem.nodeId}&busRouteId=${searchItem.routeId}&ord=${searchItem.sequence}"
                }
                else -> {
                    "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?serviceKey=${BuildConfig.api_key}&busRouteId=${searchItem.routeId}"
                }
            }
            val response = withContext(Dispatchers.IO) { makeHttpRequest(urlString) }
            val parsedData = parseBusArrivalInfo(response)
            withContext(Dispatchers.Main) {
                textView.text = parsedData.joinToString("\n\n")
            }
        }
    }
    private fun parseBusArrivalInfo(xmlData: String): List<String> {
        val infoList = mutableListOf<String>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xmlData))
            var eventType = parser.eventType
            var currentTag = ""

            var stNm = ""
            var arrmsg1 = ""
            var arrmsg2 = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                    }
                    XmlPullParser.TEXT -> {
                        when (currentTag) {
                            "stNm" -> stNm = parser.text
                            "arrmsg1" -> arrmsg1 = parser.text
                            "arrmsg2" -> arrmsg2 = parser.text
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "itemList" && stNm.isNotEmpty()) {
                            val info = "정류소: $stNm\n첫 번째 버스 도착 예정: $arrmsg1\n두 번째 버스 도착 예정: $arrmsg2"
                            infoList.add(info)
                            stNm = ""
                            arrmsg1 = ""
                            arrmsg2 = ""
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return infoList
    }

    private suspend fun makeHttpRequest(urlString: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL(urlString)
            (url.openConnection() as HttpURLConnection).run {
                requestMethod = "GET"
                inputStream.bufferedReader().use { it.readText() }
            }
        }
    }


    companion object {
        fun newInstance(searchItem: SearchItem): ArrivalFragment {
            val args = Bundle().apply {
                putParcelable("searchItem", searchItem)
            }
            return ArrivalFragment().apply {
                arguments = args
            }
        }
    }
}

