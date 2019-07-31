package lbs.whatsaround

// Komplettes JSON von API
class HomeFeed(val query: Query)

// HomeFeed.Query
class Query(val geosearch: List<GeoSearch>)

// HomeFeed.Query.GeoSearch --> Einzelne Elemente (Articles)
class GeoSearch(val pageid: Int, val ns: Int, val title: String, val lat: Float,
                val lon: Float, val dist: Float, val primary: String)