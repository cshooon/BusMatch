package hoon.mobile.soundmatch

import SearchItem
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SearchFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchTypeRadioGroup: RadioGroup
    private lateinit var searchResultsListView: ListView
    private var searchResults: List<SearchItem> = emptyList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchTypeRadioGroup = view.findViewById(R.id.searchTypeRadioGroup)
        searchResultsListView = view.findViewById(R.id.searchResultsListView)

        val searchButton = view.findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener {
            performSearch()
        }

        searchResultsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = searchResults[position] // searchResults 리스트에서 SearchItem 객체 직접 참조
            openArrivalFragment(selectedItem)
        }

        return view

    }

    private fun performSearch() {
        val searchQuery = searchEditText.text.toString()
        val searchType = when (searchTypeRadioGroup.checkedRadioButtonId) {
            R.id.radioBusName -> "bus"
            R.id.radioStopName -> "stop"
            else -> ""
        }

        lifecycleScope.launch {
            val response = when (searchType) {
                "bus" -> makeHttpRequest("http://10.0.2.2:5000/getRouteId/$searchQuery")
                "stop" -> makeHttpRequest("http://10.0.2.2:5000/getBusStopInfo/$searchQuery")
                else -> ""
            }
            val searchItems = parseSearchResults(response, searchType)
            displaySearchResults(searchItems)
        }
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

    private fun parseSearchResults(json: String, searchType: String): List<SearchItem> {
        return when (searchType) {
            "bus" -> listOf(SearchItem(JSONObject(json).getString("routeId")))
            "stop" -> {
                val items = mutableListOf<SearchItem>()
                val jsonArray = JSONObject(json).getJSONArray("busStops")
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    items.add(SearchItem(
                        routeId = jsonObject.getString("route_id"),
                        nodeId = jsonObject.getString("node_id"),
                        sequence = jsonObject.getInt("sequence")
                    ))
                }
                items
            }
            else -> emptyList()
        }
    }

    private fun displaySearchResults(results: List<SearchItem>) {
        searchResults = results // 검색 결과 저장
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, results.map {
            // SearchItem 객체를 문자열로 변환
            "${it.routeId}, ${it.nodeId ?: "N/A"}, ${it.sequence ?: "N/A"}"
        })
        searchResultsListView.adapter = adapter
    }




    private fun openArrivalFragment(selectedItem: SearchItem) {
        val arrivalFragment = ArrivalFragment.newInstance(selectedItem)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, arrivalFragment)
            .addToBackStack(null)
            .commit()
    }
}
